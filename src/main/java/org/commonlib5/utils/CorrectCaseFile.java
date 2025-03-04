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

/*
 * Copyright (C) 2021 Nicola De Nisco
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


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Corregge un oggetto file per file systems che sono case sensitive.
 * Esempio: esiste un file /home/nicola/pippo e il file in ingresso è /HOME/nicola/PIPPO
 * in questo caso correctCase è in grado di correggere il file al case corretto.
 * L'utilità di questa classe è per file system unix dove il case è sensibile.
 *
 * @author Nicola De Nisco
 */
public class CorrectCaseFile
{
  /**
   * Verifica ed eventualmente corregge case del file.
   * Corregge l'oggetto file verificando che il percorso
   * sia vero indipendentemente dal case.
   * Prima di applicare la correzione verifica se l'oggeto file
   * può essere raggiunto in ogni caso.
   * @param parseFile file da cercare
   * @param baseDir parte del percorso da considerarsi sempre valida
   * @return il file corretto
   * @throws IOException se il file non esiste
   */
  public File correctCaseTest(File parseFile, File baseDir)
     throws IOException
  {
    if(parseFile.exists())
      return parseFile;

    return correctCase(parseFile, baseDir);
  }

  /**
   * Corregge case del file.
   * Corregge l'oggetto file verificando che il percorso
   * sia vero indipendentemente dal case.
   * @param parseFile file da cercare
   * @param baseDir parte del percorso da considerarsi sempre valida
   * @return il file corretto
   * @throws IOException se il file non esiste
   */
  public File correctCase(File parseFile, File baseDir)
     throws IOException
  {
    List<String> pathElements1 = new ArrayList<>();
    Paths.get(parseFile.getCanonicalPath()).forEach(p -> pathElements1.add(p.toString()));
    List<String> pathElements2 = new ArrayList<>();
    Paths.get(baseDir.getCanonicalPath()).forEach(p -> pathElements2.add(p.toString()));

    List<String> pathVerified = new ArrayList<>();
    for(int i = pathElements2.size(); i < pathElements1.size(); i++)
    {
      String test = pathElements1.get(i);
      String okval = checkdir(baseDir, pathVerified, test);

      if(okval == null)
        throw new IOException();

      pathVerified.add(okval);
    }

    return buildDir(baseDir, pathVerified, 0);
  }

  private String checkdir(File baseDir, List<String> pathVerified, String test)
     throws IOException
  {
    File dir = buildDir(baseDir, pathVerified, 0);
    String[] lsFiles = dir.list();

    for(int i = 0; i < lsFiles.length; i++)
    {
      String ft = lsFiles[i];
      if(test.equalsIgnoreCase(ft))
        return ft;
    }

    throw new IOException();
  }

  private File buildDir(File base, List<String> pathVerified, int pos)
  {
    if(pos < pathVerified.size())
    {
      base = new File(base, pathVerified.get(pos++));
      return buildDir(base, pathVerified, pos);
    }

    return base;
  }
}
