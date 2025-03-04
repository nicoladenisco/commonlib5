/* 
 * Copyright (C) 2025 Nicola De Nisco
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.commonlib5.xmlrpc;

import java.util.List;
import java.util.Map;

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
}
