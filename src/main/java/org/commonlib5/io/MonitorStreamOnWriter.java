/*
 * Copyright (C) 2012 Nicola De Nisco
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
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.commonlib5.comunication.CC;

/**
 * Implementazione di un monitor per la scrittura su disco.
 * Il writer passato al costruttore riceverà i messaggi di log.
 *
 * @author Nicola De Nisco
 */
public class MonitorStreamOnWriter implements MonitorStreamStorage
{
  /** Il writer per i messaggi di log. */
  protected PrintWriter wr;
  /** Il formato della data utilizzata per i messaggi. */
  protected DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  /** buffer di un carattere per la stampa. */
  protected byte[] b1 = new byte[1];

  public MonitorStreamOnWriter(PrintWriter wr)
  {
    this.wr = wr;
  }

  public synchronized void changeWriter(PrintWriter wr)
  {
    this.wr.flush();
    this.wr.close();
    this.wr = wr;
  }

  @Override
  public synchronized void addToStorage(int type, int byteValue)
     throws IOException
  {
    b1[0] = (byte) byteValue;
    printBuffer(type, b1, 0, 1);
  }

  @Override
  public void addToStorage(int type, byte[] b)
     throws IOException
  {
    printBuffer(type, b, 0, b.length);
  }

  @Override
  public void addToStorage(int type, byte[] b, int offset, int len)
     throws IOException
  {
    printBuffer(type, b, offset, len);
  }

  protected synchronized void printBuffer(int type, byte[] b, int offset, int len)
     throws IOException
  {
    wr.println("[" + ((char) (type)) + "] " + df.format(new Date()) + " " + CC.fmtCommBuffer(b, offset, len));
    wr.flush();
  }

  @Override
  public void addComment(int type, String comment)
     throws IOException
  {
    wr.println("[" + ((char) (type)) + "] " + df.format(new Date()) + " " + comment);
    wr.flush();
  }

  @Override
  public void flush()
     throws IOException
  {
    wr.flush();
  }
}
