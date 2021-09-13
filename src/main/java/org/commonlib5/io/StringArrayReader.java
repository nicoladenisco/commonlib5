/*
 *  StringArrayReader.java
 *  Creato il Nov 21, 2017, 6:43:36 PM
 *
 *  Copyright (C) 2017 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 */
package org.commonlib5.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Un read per leggere un array o una collezione di stringhe.
 * Il reader ritorna i caratteri nelle stringhe.
 *
 * @author Nicola De Nisco
 */
public class StringArrayReader extends Reader
{
  private long pos;
  private int spos;
  private Iterator itrStr = null;
  private String currLine = null;
  private String stringTerminator = null;
  private char[] currArray = null;

  /**
   * Costruttore da array di stringhe.
   * @param arStr array di stringhe sorgente
   * @param stringTerminator terminatore da aggiungere ad ogni stringa
   */
  public StringArrayReader(String[] arStr, String stringTerminator)
  {
    this(Arrays.asList(arStr).iterator(), stringTerminator);
  }

  /**
   * Costruttore da iteratore.
   * Per ogni oggetto restituito viene chiamato toString().
   * @param itrStr iteratore di oggetti
   * @param stringTerminator terminatore da aggiungere ad ogni stringa
   */
  public StringArrayReader(Iterator itrStr, String stringTerminator)
  {
    this.itrStr = itrStr;
    this.pos = 0;
    this.spos = 0;
    this.stringTerminator = stringTerminator;
  }

  @Override
  public int read(char[] cbuf, int off, int len)
     throws IOException
  {
    synchronized(lock)
    {
      if(currLine == null)
      {
        if(!itrStr.hasNext())
          return -1;

        currLine = itrStr.next().toString();
        spos = 0;

        if(stringTerminator != null)
          currLine += stringTerminator;

        currArray = currLine.toCharArray();
      }

      int nb = Math.min(len, currArray.length - spos);
      System.arraycopy(currArray, spos, cbuf, off, nb);
      spos += nb;
      pos += nb;

      if(spos >= currLine.length())
        currLine = null;

      return nb;
    }
  }

  /**
   * Ritorna la posizione corrente all'interno del reader.
   * @return numero di caratteri letti
   */
  public long getPos()
  {
    return pos;
  }

  @Override
  public void close()
     throws IOException
  {
  }
}
