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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * Utility per estrarre una directory intera da un jar.
 *
 * @author Nicola De Nisco
 */
public class JarDirectoryExtractor
{
  /**
   * Estrazione generica dal jar specificato di una directory con il relativo contenuto.
   * @param jarPath jar da cui estrarre la directory
   * @param dirInJar nome della directory all'interno del jar
   * @param outputDir directory di output per i file e le directory contenute
   * @throws IOException
   */
  public static void extractDirectoryFromJar(String jarPath, String dirInJar, Path outputDir)
     throws IOException
  {
    try(ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath)))
    {
      ZipEntry entry;
      while((entry = zip.getNextEntry()) != null)
      {
        if(entry.getName().startsWith(dirInJar) && !entry.isDirectory())
        {
          Path outPath = outputDir.resolve(entry.getName().substring(dirInJar.length()));
          Files.createDirectories(outPath.getParent());
          try(OutputStream out = Files.newOutputStream(outPath))
          {
            zip.transferTo(out);
          }
        }
      }
    }
  }

  /**
   * Estrazione dal jar in esecuzione di una directory con il relativo contenuto.
   * @param resourceDir nome della directory all'interno del jar
   * @param outputDir directory di output per i file e le directory contenute
   * @throws IOException
   * @throws URISyntaxException
   */
  public static void extractDirectory(String resourceDir, Path outputDir)
     throws IOException, URISyntaxException
  {
    URL jarUrl = JarDirectoryExtractor.class.getProtectionDomain().getCodeSource().getLocation();
    try(ZipFile jar = new ZipFile(new File(jarUrl.toURI())))
    {
      Enumeration<? extends ZipEntry> entries = jar.entries();
      while(entries.hasMoreElements())
      {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        if(name.startsWith(resourceDir) && !entry.isDirectory())
        {
          Path outPath = outputDir.resolve(name.substring(resourceDir.length()));
          Files.createDirectories(outPath.getParent());
          try(InputStream in = jar.getInputStream(entry);
             OutputStream out = Files.newOutputStream(outPath))
          {
            in.transferTo(out);
          }
        }
      }
    }
  }

}
