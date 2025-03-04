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
