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
package org.commonlib5.utils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

/**
 * Scompatta il file ZIP passato come argomento.
 *
 * @author Nicola De Nisco
 */
public class Unzip
{
  public static interface UnzipListener
  {
    /**
     * Inizio dell'operazione di scompattamento
     * @param fileZip file da scompattare
     * @param numEntry numero delle entità del file zip
     * @return true per continuare l'operazione
     */
    public boolean unzipBegin(File fileZip, int numEntry);

    /**
     * Notifica dell'estrazione di una entità nel file ZIP.
     * @param entry entità da scompattare
     * @param part conteggio parziale entità
     * @param total totale delle entità
     * @return vero per continuare, false interrompe scompattazione
     */
    public boolean unzipNotify(ZipEntry entry, int part, int total);

    /**
     * Notifica fine operazioni di scompattazione.
     * @param fileZip file da scompattare
     * @param numEntry numero delle entità del file zip
     * @return true per continuare l'operazione (ignorato)
     */
    public boolean unzipCompleted(File fileZip, int numEntry);

    /**
     * Mappa il file di input ad un'altro file.
     * Consente di spostare o rinominare il file durante l'estrazione.
     * @param input file come andrebbe scompattato
     * @return file alternativo
     */
    public File mapUnzippingFile(File input);
  }

  /**
   * Scompatta un file ZIP nella directory specificata.
   * @param fileZip file da scompattare
   * @param dirDest directory destinazione
   * @throws Exception
   */
  public static void run(File fileZip, File dirDest)
     throws Exception
  {
    run(fileZip, dirDest, null);
  }

  /**
   * Scompatta un file ZIP nella directory specificata.
   * Vengono prima create tutte le directory contenute nello zip
   * e quindi scompattati tutti i files.
   * Alcuni ZIP non sono compilati correttamente quindi l'entry
   * della directory può essere successiva a quelle dei files che contiene.
   * @param fileZip file da scompattare
   * @param dirDest directory destinazione
   * @param ul listner per la notifica dello stato di avanzamento
   * @throws Exception
   */
  public static void run(File fileZip, File dirDest, UnzipListener ul)
     throws Exception
  {
    try (ZipFile zipFile = new ZipFile(fileZip))
    {
      int count = 0, numEntries = zipFile.size();

      if(ul == null || ul.unzipBegin(fileZip, numEntries))
      {
        // prima passata: creazione directory
        Enumeration entries = zipFile.entries();
        while(entries.hasMoreElements())
        {
          ZipEntry entry = (ZipEntry) entries.nextElement();
          if(ul != null && !ul.unzipNotify(entry, count++, numEntries))
            break;

          File fileEntry = new File(dirDest, entry.getName());
          if(entry.isDirectory())
          {
            Logger.getLogger(Unzip.class.getName()).log(Level.INFO, "Extracting directory: " + entry.getName());

            if(!fileEntry.mkdirs())
              throw new IOException("Fail make directory " + fileEntry.getAbsolutePath());
          }
        }

        // seconda passata: estrazione files nelle directory
        entries = zipFile.entries();
        while(entries.hasMoreElements())
        {
          ZipEntry entry = (ZipEntry) entries.nextElement();
          if(ul != null && !ul.unzipNotify(entry, count++, numEntries))
            break;

          File fileEntry = new File(dirDest, entry.getName());
          if(entry.isDirectory())
            continue;

          // questo è per sicurezza: alcuni zip non contengono tutte le directory
          File dirParent = fileEntry.getParentFile();
          dirParent.mkdirs();

          if(ul != null)
            fileEntry = ul.mapUnzippingFile(fileEntry);

          Logger.getLogger(Unzip.class.getName()).log(Level.INFO, "Extracting file: " + entry.getName());

          try (InputStream is = zipFile.getInputStream(entry);
             OutputStream os = new FileOutputStream(fileEntry))
          {
            CommonFileUtils.copyStream(is, os);
          }
        }
      }

      if(ul != null)
        ul.unzipCompleted(fileZip, numEntries);
    }
  }

  /**
   * Scompatta singolo file.
   * Scompatta il primo file contenuto uno zip alla destinazione indicata.
   * Vengono scartate tutte le directory alla ricerca del primo file. Il nome
   * del file all'interno dello zip viene ignorato.
   * @param fileZip file zip da scompattare
   * @param toWrite file destinazione da scrivere
   * @return vero se un file è stato scompattato
   * @throws Exception
   */
  public static boolean unzipSingleFile(File fileZip, File toWrite)
     throws Exception
  {
    try (ZipFile zipFile = new ZipFile(fileZip))
    {
      Enumeration entries = zipFile.entries();
      while(entries.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        if(entry.isDirectory())
          continue;

        Logger.getLogger(Unzip.class.getName()).log(Level.INFO, "Extracting file: " + entry.getName());

        try (InputStream is = zipFile.getInputStream(entry);
           OutputStream os = new FileOutputStream(toWrite))
        {
          CommonFileUtils.copyStream(is, os);
        }

        return true;
      }
    }

    return false;
  }
}
