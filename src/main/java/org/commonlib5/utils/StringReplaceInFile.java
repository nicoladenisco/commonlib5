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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import org.commonlib5.io.ByteBufferOutputStream;

/**
 * Utility per cerca e cambia di un pacchetto di stringhe in un file di testo.
 *
 * @author Nicola De Nisco
 */
public class StringReplaceInFile
{
  private final File inFile;
  private final InputStream inStream;
  private final Charset incharset;
  private final Map<String, String> substMap = new ArrayMap<>();

  /**
   * Costruttore.
   * @param txtFile file da leggere
   * @param incharset encoding del file da leggere
   */
  public StringReplaceInFile(File txtFile, Charset incharset)
  {
    this.inFile = txtFile;
    this.incharset = incharset;
    this.inStream = null;
  }

  /**
   * Costruttore.
   * @param inStream file da leggere
   * @param incharset encoding del file da leggere
   */
  public StringReplaceInFile(InputStream inStream, Charset incharset)
  {
    this.inFile = null;
    this.inStream = inStream;
    this.incharset = incharset;
  }

  /**
   * Aggiunge una sostituzione.
   * @param cerca stringa da cercare
   * @param cambia sostituzione per stringa
   */
  public void addSubstituion(String cerca, String cambia)
  {
    if(cerca == null || cerca.isEmpty() || cambia == null)
      throw new NullPointerException("Parametri non validi.");

    substMap.put(cerca, cambia);
  }

  /**
   * Pulisce elenco sostituzioni.
   */
  public void clear()
  {
    substMap.clear();
  }

  /**
   * Sostituzione di stringhe una linea per volta.
   * Basso impatto di memoria; consigliata per file lunghi.
   * @param nuovoFile il file da scrivere con le sostituzioni
   * @param outcharset encoding file da scrivere
   * @throws Exception
   */
  public void replaceByLine(File nuovoFile, Charset outcharset)
     throws Exception
  {
    if(inFile != null)
    {
      try(BufferedReader ir = Files.newBufferedReader(inFile.toPath(), incharset);
         BufferedWriter ow = Files.newBufferedWriter(nuovoFile.toPath(), outcharset))
      {
        String linea;
        while((linea = ir.readLine()) != null)
        {
          String str = StringOper.strReplace(linea, substMap);
          ow.write(str);
          ow.write('\n');
        }
      }
    }
    else if(inStream != null)
    {
      try(BufferedReader ir = new BufferedReader(new InputStreamReader(inStream, incharset));
         BufferedWriter ow = Files.newBufferedWriter(nuovoFile.toPath(), outcharset))
      {
        String linea;
        while((linea = ir.readLine()) != null)
        {
          String str = StringOper.strReplace(linea, substMap);
          ow.write(str);
          ow.write('\n');
        }
      }
    }
  }

  /**
   * Sostituzione di stringhe in memoria.
   * Il file viene letto in memoria e tutte le stringhe sono sostituite.
   * Piu veloce ma adatta a file di piccole dimensioni.
   * @param nuovoFile il file da scrivere con le sostituzioni
   * @param outcharset encoding file da scrivere
   * @throws Exception
   */
  public void replaceInMemory(File nuovoFile, Charset outcharset)
     throws Exception
  {
    byte[] contenuto = null;

    if(inFile != null)
    {
      contenuto = CommonFileUtils.readFile(inFile);
    }
    else if(inStream != null)
    {
      ByteBufferOutputStream os = new ByteBufferOutputStream();
      CommonFileUtils.copyStream(inStream, os);
      contenuto = os.getBytes();
    }

    String inmemoria = new String(contenuto, incharset);
    String convertita = StringOper.strReplace(inmemoria, substMap);
    byte[] dascrivere = convertita.getBytes(outcharset);
    CommonFileUtils.writeFile(nuovoFile, dascrivere);
  }
}
