/*
 * Copyright (C) 2015 Nicola De Nisco
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Accumulatore di blocchi con scrittura diretta su disco.
 * I dati vengono salvati in binario su disco nel file specificato.
 * Il tipo di blocco viene ignorato.
 *
 * @author Nicola De Nisco
 */
public class MonitorStreamOnFile implements MonitorStreamStorage
{
  private OutputStream os;

  public MonitorStreamOnFile(OutputStream os)
  {
    this.os = os;
  }

  public MonitorStreamOnFile(File toWrite, boolean append)
     throws FileNotFoundException
  {
    os = new FileOutputStream(toWrite, append);
  }

  @Override
  public void addToStorage(int type, byte[] b)
     throws IOException
  {
    os.write(b);
    os.flush();
  }

  @Override
  public void addToStorage(int type, byte[] b, int offset, int len)
     throws IOException
  {
    os.write(b, offset, len);
    os.flush();
  }

  @Override
  public void addToStorage(int type, int byteValue)
     throws IOException
  {
    os.write(byteValue);
    os.flush();
  }

  @Override
  public void flush()
     throws IOException
  {
    os.flush();
  }

  /**
   * In questa versione i commenti sono ignorati.
   * @param type
   * @param comment
   * @throws IOException
   */
  @Override
  public void addComment(int type, String comment)
     throws IOException
  {
  }
}
