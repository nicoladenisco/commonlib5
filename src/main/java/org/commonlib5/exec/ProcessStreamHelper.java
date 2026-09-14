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
package org.commonlib5.exec;

import java.io.*;
import org.commonlib5.io.ByteBufferInputStream;

/**
 * Classe di supporto per l'esecuzione sicura di processi.
 * Il processo lanciato scrive il suo output in stream bufferati
 * leggibili esternamente in modo asincrono.
 *
 * @author Nicola De Nisco
 */
public class ProcessStreamHelper extends ProcessHelper
{
  protected final ByteBufferInputStream bbout = new ByteBufferInputStream();
  protected final ByteBufferInputStream bberr = new ByteBufferInputStream();

  public static ProcessStreamHelper exec(String cmd)
     throws IOException
  {
    return new ProcessStreamHelper(Runtime.getRuntime().exec(cmd));
  }

  public static ProcessStreamHelper exec(String[] cmdArray)
     throws IOException
  {
    return new ProcessStreamHelper(Runtime.getRuntime().exec(cmdArray));
  }

  public static ProcessStreamHelper exec(String[] cmdArray, String[] env)
     throws IOException
  {
    return new ProcessStreamHelper(Runtime.getRuntime().exec(cmdArray, env));
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessStreamHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @throws IOException
   */
  public ProcessStreamHelper(Process process)
     throws IOException
  {
    this.process = process;
    startThread(new ProcessWatchListner()
    {
      @Override
      public void notifyStdout(byte[] output, int offset, int length)
      {
        bbout.addToBuffer(output, offset, length);
      }

      @Override
      public void notifyStderr(byte[] output, int offset, int length)
      {
        bberr.addToBuffer(output, offset, length);
      }
    });
  }

  public ByteBufferInputStream getBbout()
  {
    return bbout;
  }

  public ByteBufferInputStream getBberr()
  {
    return bberr;
  }
}
