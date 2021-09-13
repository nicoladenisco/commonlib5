/*
 * Zip.java
 *
 * Created on 27-set-2010, 11.53.46
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Crea un file fileZip dai files passati come argomento.
 *
 * @author Nicola De Nisco
 */
public class Zip
{
  public static final int BUFFER_SIZE = 8192;

  public static interface ZipListener
  {
    public boolean zipBegin(File fileZip, int numEntry);

    public boolean zipNotify(File entry, int part, int total);

    public boolean zipCompleted(File fileZip, int numEntry);
  }

  /**
   * Crea un file zip con l'array di files indicati.
   * Viene utilizzata la compressione di default;
   * un buon compromesso fra dimensione e velocità di compressione.
   * @param fileZip file da creare
   * @param src file da includere all'interno dello zip
   * @throws Exception
   */
  public static void zipFiles(File fileZip, File[] src)
     throws Exception
  {
    zipFiles(fileZip, src, Deflater.DEFAULT_COMPRESSION, null);
  }

  /**
   * Crea un file zip con l'array di files indicati.
   * @param fileZip file da creare
   * @param src file da includere all'interno dello zip
   * @param compLevel livello di compressione richiesta
   * @param ul listner per notifica avanzamento (può essere null)
   * @throws Exception
   */
  public static void zipFiles(File fileZip, File[] src, int compLevel, ZipListener ul)
     throws Exception
  {
    try (ZipOutputStream fos = new ZipOutputStream(new FileOutputStream(fileZip)))
    {
      fos.setLevel(compLevel);

      if(ul == null || ul.zipBegin(fileZip, src.length))
        zipEntries(fos, src, null, ul);

      if(ul != null)
        ul.zipCompleted(fileZip, src.length);

      // Complete the ZIP file
      fos.finish();
    }
  }

  /**
   * Invia allo stream il file compresso con la lista di files indicati.
   * @param os stream di salvataggio
   * @param compLevel livello di compressione richiesta
   * @param entryToZip coppie file da salvare/nome alternativo (può essere null)
   * @param ul listner per notifica avanzamento (può essere null)
   * @throws Exception
   */
  public static void zipEntries(ZipOutputStream os,
     List<Pair<File, String>> entryToZip, ZipListener ul)
     throws Exception
  {
    File[] files = new File[entryToZip.size()];
    String[] alternateNames = new String[entryToZip.size()];

    for(int i = 0; i < entryToZip.size(); i++)
    {
      Pair<File, String> entry = entryToZip.get(i);
      files[i] = entry.first;
      alternateNames[i] = entry.second;
    }

    zipEntries(os, files, alternateNames, ul);
  }

  /**
   * Invia allo stream il file compresso con la lista di files indicati.
   * @param out stream di salvataggio
   * @param files array dei files
   * @param ul listner per notifica avanzamento (può essere null)
   * @throws IOException
   */
  public static void zipEntries(ZipOutputStream out, Collection<File> files, ZipListener ul)
     throws IOException
  {
    File[] arToZip = files.toArray(new File[files.size()]);
    zipEntries(out, arToZip, null, ul);
  }

  /**
   * Invia allo stream il file compresso con la lista di files indicati.
   * @param out stream di salvataggio
   * @param files array dei files
   * @param alternateNames nomi alternativi (può essere null oppure può essere null la corrispondenza)
   * @param ul listner per notifica avanzamento (può essere null)
   * @throws IOException
   */
  public static void zipEntries(ZipOutputStream out,
     File[] files, String[] alternateNames, ZipListener ul)
     throws IOException
  {
    int numEntries = files.length;

    // Create the ZIP file
    CRC32 crc = new CRC32();
    byte[] buf = new byte[BUFFER_SIZE];

    // Compress the files
    for(int i = 0; i < numEntries; i++)
    {
      File f = files[i];
      if(ul != null && !ul.zipNotify(f, i, numEntries))
        break;

      if(f == null || !f.exists() || !f.isFile())
        continue;

      // apre file input per la lettura
      try (FileInputStream in = new FileInputStream(f))
      {
        // determina il nome della entry nel file zip
        String name = f.getName();
        if(alternateNames != null && alternateNames[i] != null)
          name = alternateNames[i];

        // Add ZIP entry to output stream.
        ZipEntry entry = new ZipEntry(name);
        out.putNextEntry(entry);
        crc.reset();

        // Transfer bytes from the file to the ZIP file
        int len;
        while((len = in.read(buf)) > 0)
        {
          out.write(buf, 0, len);
          crc.update(buf, 0, len);
        }

        // Complete the entry
        entry.setCrc(crc.getValue());
      }

      out.closeEntry();
    }
  }

  /**
   * Crea un file zip con tutti i files contenuti nella directory indicata.
   * Nel file zip verranno salvate le path relative alla directory indicata.
   * Viene utilizzata la compressione di default;
   * un buon compromesso fra dimensione e velocità di compressione.
   * Ricorsivamente vengono incluse tutte le sottodirectory.
   * @param fileZip file da creare
   * @param directory contenuto dei files da zippare
   * @throws IOException
   */
  public static void zipDirectory(File fileZip, File directory)
     throws IOException
  {
    byte[] buffer = new byte[BUFFER_SIZE];
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileZip)))
    {
      zip(zos, directory, directory, buffer, true);
      zos.finish();
    }
  }

  /**
   * Invia allo stream zip il contenuto della directory.
   * I files salvati nello zip verranno spiazzati a partire
   * dalla path base indicata in base.
   * Ricorsivamente vengono incluse tutte le sottodirectory
   * se recurseSubdir è vero.
   * @param zos stream zip di output
   * @param compLevel livello di compressione richiesta
   * @param directory contenuto dei files da zippare
   * @param base path di base da rimuovere nel file zip
   * @param buffer buffer di servizio per la compressione
   * @param recurseSubdir se vero effettua ricorsione nelle sottodirectory
   * @throws IOException
   */
  public static void zip(ZipOutputStream zos,
     File directory, File base, byte[] buffer, boolean recurseSubdir)
     throws IOException
  {
    CRC32 crc = new CRC32();
    File[] files = directory.listFiles();
    int read = 0;
    int lenBase = base.getPath().length() + 1;

    for(int i = 0, n = files.length; i < n; i++)
    {
      if(files[i].isDirectory())
      {
        // ricorsione per comprimere la directory
        if(recurseSubdir)
          zip(zos, files[i], base, buffer, recurseSubdir);
      }
      else
      {
        String path = files[i].getPath().substring(lenBase).replace('\\', '/');

        try (FileInputStream in = new FileInputStream(files[i]))
        {
          ZipEntry entry = new ZipEntry(path);
          zos.putNextEntry(entry);
          crc.reset();

          while((read = in.read(buffer)) > 0)
          {
            zos.write(buffer, 0, read);
            crc.update(buffer, 0, read);
          }

          // Complete the entry
          entry.setCrc(crc.getValue());
        }

        zos.closeEntry();
      }
    }
  }

  /**
   * Crea un file zip contenente un singolo file.
   * @param fileZip file da creare
   * @param tozip file originale
   * @param alternateName nome alternativo nello zip (può essere null)
   * @param compLevel livello di compressione richiesto
   * @throws IOException
   */
  public static void zipSingleFile(File fileZip, File tozip, String alternateName, int compLevel)
     throws IOException
  {
    if(!tozip.isFile())
      throw new IOException("This function need a single file to zip.");

    if(alternateName == null)
      alternateName = tozip.getName();

    File[] files = new File[1];
    files[0] = tozip;
    String[] alternateNames = new String[1];
    alternateNames[0] = alternateName;

    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileZip)))
    {
      zos.setLevel(compLevel);
      zipEntries(zos, files, alternateNames, null);
      zos.finish();
    }
  }

  /**
   * Crea un file zip contenente un singolo file.
   * @param fileZip file da creare
   * @param tozip file originale
   * @throws IOException
   */
  public static void zipSingleFile(File fileZip, File tozip)
     throws IOException
  {
    zipSingleFile(fileZip, tozip, null, Deflater.DEFAULT_COMPRESSION);
  }
}
