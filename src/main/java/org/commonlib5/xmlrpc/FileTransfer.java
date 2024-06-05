/*
 * FileTransfer.java
 *
 * Created on 2-lug-2012, 18.55.18
 *
 * Copyright (C) 2012 Informatica Medica s.r.l.
 *
 * Questo software è proprietà di Informatica Medica s.r.l.
 * Tutti gli usi non esplicitimante autorizzati sono da
 * considerarsi tutelati ai sensi di legge.
 *
 * Informatica Medica s.r.l.
 * Viale dei Tigli, 19
 * Casalnuovo di Napoli (NA)
 *
 * Creato il 2-lug-2012, 18.55.18
 */
package org.commonlib5.xmlrpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import org.commonlib5.utils.LongOperListener;
import org.commonlib5.utils.StringOper;

/**
 * Definizione delle costanti utilizzate per il file transfer.
 *
 * @author Nicola De Nisco
 */
public interface FileTransfer
{
  public static final String TIPAR_ID = "id";
  public static final String TIPAR_CURR_BLOCK = "currBlock";
  public static final String TIPAR_FILE_NAME = "fileName";
  public static final String TIPAR_FILE_SIZE = "fileSize";
  public static final String TIPAR_NUM_BLOCK = "numBlock";
  public static final String TIPAR_SIZE_BLOCK = "sizeBlock";

  /**
   * Prepara il trasferimento di un file da caleido verso l'applicazione esterna.
   * Dopo la preparazione chimate successive a
   * getFileBlock consentono di scaricare il file.
   * @param clientID id del ticket rilasciato da initClient
   * @param dati dettagli sul file richiesto
   * @param suggestBlockSize suggerimento di dimensioni blocco
   * @return informazioni per il download (vedi FileTransfer.TIPAR_...)
   * @throws Exception
   */
  public Map preparaDownload(String clientID, Map dati, int suggestBlockSize)
     throws Exception;

  /**
   * Trasferisce un blocco di file da caleido verso l'applicazione esterna.
   * L'id del file è stato trasferito nei parametri di ritorno
   * di preparaDownload() con anche il numero di blocchi da
   * trasferire.
   * @param clientID id del ticket rilasciato da initClient
   * @param idFile identificatore del file
   * @param block numero del blocco richiesto
   * @return array di bytes del blocco
   * @throws Exception
   * @deprecated usa getFileBlockCRC32
   */
  public byte[] getFileBlock(String clientID, String idFile, int block)
     throws Exception;

  /**
   * Trasferisce un blocco di file da caleido verso l'applicazione esterna.
   * L'id del file è stato trasferito nei parametri di ritorno
   * di preparaDownload() con anche il numero di blocchi da
   * trasferire.
   * @param clientID id del ticket rilasciato da initClient
   * @param idFile identificatore del file
   * @param block numero del blocco richiesto
   * @return vettore di due elementi: 0=CRC 1=array di bytes del blocco
   * @throws Exception
   */
  public List getFileBlockCRC32(String clientID, String idFile, int block)
     throws Exception;

  /**
   * Prepara il trasferimento di un file dall'applicazione esterna verso caleido.
   * Dopo la preparazione chiamate successive a
   * putFileBlock consentono di inviare il file di refertazione.
   * @param clientID id del ticket rilasciato da initClient
   * @param dati dettagli sul file da inviare
   * @param suggestBlockSize suggerimento di dimensioni blocco
   * @return informazioni per l'upload (vedi TIPAR_...)
   * @throws Exception
   */
  public Map preparaUpload(String clientID, Map dati, int suggestBlockSize)
     throws Exception;

  /**
   * Trasferisce un blocco di file dall'applicazione esterna a caleido.
   * L'id del file è stato trasferito nei parametri di ritorno
   * di preparaUpload() con anche il numero di blocchi da
   * trasferire.
   * @param clientID id del ticket rilasciato da initClient
   * @param idFile identificatore del file
   * @param block numero del blocco richiesto
   * @param data dati binari del file
   * @return 0 tutto ok
   * @throws Exception
   * @deprecated usa putFileBlockCRC32
   */
  public int putFileBlock(String clientID, String idFile, int block, byte[] data)
     throws Exception;

  /**
   * Trasferisce un blocco di file dall'applicazione esterna a caleido.
   * L'id del file è stato trasferito nei parametri di ritorno
   * di preparaUpload() con anche il numero di blocchi da
   * trasferire.
   * @param clientID id del ticket rilasciato da initClient
   * @param idFile identificatore del file
   * @param block numero del blocco richiesto
   * @param data dati binari del file
   * @return CRC a 32 bit del blocco trasferito (double perchè XML-RPC non supporta long)
   * @throws Exception
   */
  public double putFileBlockCRC32(String clientID, String idFile, int block, byte[] data)
     throws Exception;

  /**
   * Termina un trasferimento.
   * @param clientID id del ticket rilasciato da initClient
   * @param idFile id del file da terminare
   * @return 0 se tutto ok
   * @throws Exception
   */
  public int trasferimentoCompletato(String clientID, String idFile)
     throws Exception;

  /**
   * Implementazione standard del loop di upload files.
   * @param clientID id del ticket rilasciato da initClient
   * @param pup informazioni per l'upload rilasciate da preparaUpload
   * @param toSend file da inviare
   * @param lol listener avanzamento (può essere null)
   * @return id del file inviato rilasciato dal server per elaborazioni successive
   * @throws Exception
   */
  default public String uploadFileStandardLoop(String clientID, Map pup, File toSend, LongOperListener lol)
     throws Exception
  {
    int numBlock = StringOper.parse(pup.get(FileTransfer.TIPAR_NUM_BLOCK), 0);
    int sizeBlock = StringOper.parse(pup.get(FileTransfer.TIPAR_SIZE_BLOCK), 0);
    String idFile = StringOper.okStr(pup.get(FileTransfer.TIPAR_ID));

    if(lol != null)
      lol.resetUI();

    int nb = 0, count = 0, ct;
    try(FileInputStream fis = new FileInputStream(toSend))
    {
      long vc, cc;
      CRC32 checksum = new CRC32();
      byte[] buffer = new byte[sizeBlock];

      do
      {
        if(lol != null)
          if(!lol.updateUI(count, numBlock))
            throw new InterruptedException();

        if((nb = fis.read(buffer)) > 0)
        {
          ct = 0;

          do
          {
            checksum.reset();

            if(nb == buffer.length)
            {
              vc = (long) putFileBlockCRC32(clientID, idFile, count, buffer);
              checksum.update(buffer);
            }
            else
            {
              byte[] tmp = Arrays.copyOf(buffer, nb);
              vc = (long) putFileBlockCRC32(clientID, idFile, count, tmp);
              checksum.update(tmp);
            }

            cc = checksum.getValue();

            if(cc != vc && ++ct > 5)
              throw new IOException("Troppi tentativi: trasferimento abortito.");
          }
          while(cc != vc);

          count++;
        }
      }
      while(nb > 0);
    }

    if(lol != null)
      lol.completeUI(count);

    // chiude l'upload
    trasferimentoCompletato(clientID, idFile);
    return idFile;
  }

  default public String downloadFileStandardLoop(String clientID,
     Map pdown, File toSave, LongOperListener lol)
     throws Exception
  {
    int numBlock = StringOper.parse(pdown.get(FileTransfer.TIPAR_NUM_BLOCK), 0);
    int sizeBlock = StringOper.parse(pdown.get(FileTransfer.TIPAR_SIZE_BLOCK), 0);
    int sizeFile = StringOper.parse(pdown.get(FileTransfer.TIPAR_FILE_SIZE), 0);
    String idFile = StringOper.okStr(pdown.get(FileTransfer.TIPAR_ID));

    if(lol != null)
      lol.resetUI();

    int count = 0, ct = 0;
    byte[] block;
    long vc, cc;
    CRC32 checksum = new CRC32();

    try(FileOutputStream fos = new FileOutputStream(toSave))
    {
      for(count = 0; count < numBlock; count++)
      {
        ct = 0;
        if(lol != null)
          if(!lol.updateUI(count, numBlock))
            throw new InterruptedException();

        do
        {
          // richiede il blocco al server
          List vget = getFileBlockCRC32(clientID, idFile, count);
          vc = ((Double) vget.get(0)).longValue();
          block = (byte[]) vget.get(1);

          // calcola checksum del blocco ricevuto
          checksum.reset();
          checksum.update(block);
          cc = checksum.getValue();

          // confronta valore del checksum ricevuto con quello calcolato
          if(cc != vc && ++ct > 5)
            throw new IOException("Troppi tentativi: trasferimento abortito.");
        }
        while(vc != cc);

        fos.write(block);
      }
    }

    if(lol != null)
      lol.completeUI(count);

    // chiude download
    trasferimentoCompletato(clientID, idFile);
    return idFile;
  }
}
