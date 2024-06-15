/*
 * Copyright (C) 2024 Nicola De Nisco
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
 * Implementazione standard dei loop di upload e download.
 *
 * @author Nicola De Nisco
 */
public class FileTransferLoop
{
  protected FileTransfer ft;

  public FileTransferLoop(FileTransfer ft)
  {
    this.ft = ft;
  }

  /**
   * Implementazione standard del loop di upload files.
   * @param clientID id del ticket rilasciato da initClient
   * @param pup informazioni per l'upload rilasciate da preparaUpload
   * @param toSend file da inviare
   * @param lol listener avanzamento (può essere null)
   * @return id del file inviato rilasciato dal server per elaborazioni successive
   * @throws Exception
   */
  public String uploadFileStandardLoop(String clientID, Map pup, File toSend, LongOperListener lol)
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
              vc = (long) ft.putFileBlockCRC32(clientID, idFile, count, buffer);
              checksum.update(buffer);
            }
            else
            {
              byte[] tmp = Arrays.copyOf(buffer, nb);
              vc = (long) ft.putFileBlockCRC32(clientID, idFile, count, tmp);
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
    ft.trasferimentoCompletato(clientID, idFile);
    return idFile;
  }

  /**
   * Implementazione standard del loop di download files.
   * @param clientID id del ticket rilasciato da initClient
   * @param pdown informazioni per il download rilasciate da preparaDownload
   * @param toSave file da inviare
   * @param lol listener avanzamento (può essere null)
   * @return id del file inviato rilasciato dal server per elaborazioni successive
   * @throws Exception
   */
  public String downloadFileStandardLoop(String clientID,
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
          List vget = ft.getFileBlockCRC32(clientID, idFile, count);
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
    ft.trasferimentoCompletato(clientID, idFile);
    return idFile;
  }
}
