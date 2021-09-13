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
package org.commonlib5.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simile alla grep di Unix.
 * Cerca linee di testo e applica la regular expression ad ogni linea.
 * Se la regexp è null tutte le linee vengono ritornate nel risultato,
 * altrimenti solo quelle che contengono la regexp (metodo find).
 * Se la regexp contiene dei raggruppamenti, nel ritorno sono ritornati
 * i raggruppamenti e non la linea; i raggruppamenti sono inseriti in una
 * unica linea separta da groupSeparator (default = ',') e delimitati
 * da groupDelimiter (default = null).
 *
 * @author Nicola De Nisco
 */
public class Grep
{
  protected final Pattern toSearch;
  protected String groupSeparator = ",", groupDelimiter = null;

  /**
   * Costruttore.
   * @param toSearch pattern compilata con la regex da trovare (null=tutte le linee)
   */
  public Grep(Pattern toSearch)
  {
    this.toSearch = toSearch;
  }

  /**
   * Costruttore.
   * @param toSearch pattern compilata con la regex da trovare (null=tutte le linee)
   * @param groupSeparator separatore usato quando la regexp presenta delle sostituzioni
   * @param groupDelimiter delimitatore usato quando la regexp presenta delle sostituzioni
   */
  public Grep(Pattern toSearch, String groupSeparator, String groupDelimiter)
  {
    this.toSearch = toSearch;
    this.groupSeparator = groupSeparator;
    this.groupDelimiter = groupDelimiter;
  }

  public Pattern getPattern()
  {
    return toSearch;
  }

  public String getGroupSeparator()
  {
    return groupSeparator;
  }

  public void setGroupSeparator(String groupSeparator)
  {
    this.groupSeparator = groupSeparator;
  }

  public String getGroupDelimiter()
  {
    return groupDelimiter;
  }

  public void setGroupDelimiter(String groupDelimiter)
  {
    this.groupDelimiter = groupDelimiter;
  }

  /**
   * Simile all'utility grep di Unix.
   * Una versione semplificata adatta a semplici ricerche di contenimento è 'findStringInFile'.
   * @param asciiFileName file da interrogare
   * @param encoding encoding del file da leggere
   * @return le linee che onorano la regular expression
   * @throws java.lang.Exception
   */
  public String[] grep(File asciiFileName, String encoding)
     throws Exception
  {
    ArrayList<String> arRv = new ArrayList<String>();

    try (FileInputStream fis = new FileInputStream(asciiFileName);
       BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding)))
    {
      grep(br, arRv);
    }

    return StringOper.toArray(arRv);
  }

  /**
   * Simile all'utility grep di Unix.
   * @param br Reader da cui leggere le linee di testo
   * @param arRv popolato con le linee che onorano la regular expression
   * @return numero di linee lette dal reader
   * @throws Exception
   */
  public int grep(BufferedReader br, List<String> arRv)
     throws Exception
  {
    int count = 0;
    String linea;
    while((linea = br.readLine()) != null)
    {
      if(toSearch == null)
      {
        arRv.add(linea);
      }
      else
      {
        Matcher m = toSearch.matcher(linea);
        if(m.find())
          processPatternMatch(count, linea, m, arRv);
      }

      count++;
    }

    return count;
  }

  /**
   * Processa linea individuata dalla pattern.
   * @param lineNumber the value of lineNumber
   * @param linea linea completa di origine
   * @param m risultato della ricerca della pattern nella linea
   * @param arRv array di stringhe di ritorno
   */
  protected void processPatternMatch(int lineNumber, String linea, Matcher m, List<String> arRv)
  {
    if(m.groupCount() > 0)
    {
      mergePatternGroups(lineNumber, linea, m, arRv);
    }
    else
    {
      arRv.add(linea);
    }
  }

  /**
   * Fonde i gruppi individuati dalla pattern per inserirli nella lista di ritorno.
   * @param lineNumber the value of lineNumber
   * @param linea linea completa di origine
   * @param m risultato della ricerca della pattern nella linea
   * @param arRv array di stringhe di ritorno
   */
  protected void mergePatternGroups(int lineNumber, String linea, Matcher m, List<String> arRv)
  {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < m.groupCount(); i++)
    {
      if(i > 0 && groupSeparator != null)
        sb.append(groupSeparator);

      if(groupDelimiter != null)
        sb.append(groupDelimiter);

      sb.append(m.group(i + 1));

      if(groupDelimiter != null)
        sb.append(groupDelimiter);
    }
    arRv.add(sb.toString());
  }

  /**
   * Simile all'utility grep di Unix.
   * @param url oggetto URL da leggere
   * @return le linee che onorano la regular expression
   * @throws java.lang.Exception
   */
  public String[] grep(URL url)
     throws Exception
  {
    return grep(url, "UTF-8");
  }

  /**
   * Simile all'utility grep di Unix.
   * @param url oggetto URL da leggere
   * @param defaultEncoding encoding di default se non specificato nella risposta
   * @return le linee che onorano la regular expression
   * @throws java.lang.Exception
   */
  public String[] grep(URL url, String defaultEncoding)
     throws Exception
  {
    URLConnection connection = url.openConnection();
    connection.connect();

    String enc = connection.getContentEncoding();
    if(enc == null)
      enc = defaultEncoding;

    ArrayList<String> arRv = new ArrayList<String>();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), enc)))
    {
      grep(br, arRv);
    }

    return StringOper.toArray(arRv);
  }
}
