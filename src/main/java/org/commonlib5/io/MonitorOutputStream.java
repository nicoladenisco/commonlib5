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
import java.io.OutputStream;

/**
 * OutputStream di monitoraggio.
 * Questa classe è un output stream passante che consente il
 * salvataggio di tutti i blocchi di dati scritti sullo
 * stream vero e proprio per ottenere informazioni di logging.
 * Tutte le funzioni sono thread safe.
 *
 * @author Nicola De Nisco
 */
public class MonitorOutputStream extends OutputStream
{
  /** Lo stream da monitorare. */
  protected OutputStream os = null;
  /** La coda di accumulo. */
  protected MonitorStreamStorage queue = null;
  /** Marcatore dei messaggi nella coda (per default 'O'). */
  protected int type = 'O';

  /**
   * Costruisce una coda di default.
   * @param os output stream da monitorare
   */
  public MonitorOutputStream(OutputStream os)
  {
    this.os = os;
    this.queue = new MonitorStreamQueue();
  }

  /**
   * Costruisce con specifica coda.
   * @param os output stream da monitorare
   * @param queue coda messaggi
   */
  public MonitorOutputStream(OutputStream os, MonitorStreamStorage queue)
  {
    this.os = os;
    this.queue = queue;
  }

  /**
   * Accesso alla coda messaggi.
   * @return coda messaggi.
   */
  public MonitorStreamStorage getQueue()
  {
    return queue;
  }

  @Override
  public void close() throws IOException
  {
    os.close();
  }

  @Override
  public void flush() throws IOException
  {
    os.flush();
  }

  @Override
  public void write(byte[] b) throws IOException
  {
    os.write(b);
    queue.addToStorage(type, b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException
  {
    os.write(b, off, len);
    queue.addToStorage(type, b, off, len);
  }

  @Override
  public void write(int b) throws IOException
  {
    os.write(b);
    queue.addToStorage(type, b);
  }

  /**
   * Ritorna lo stream di riferimento.
   * @return stream aperto
   */
  public OutputStream getStream()
  {
    return os;
  }

  /**
   * Ritorna il valore del marcatore dei messaggi.
   * @return intero memorizzato in MonitorStreamData.type.
   */
  public int getType()
  {
    return type;
  }

  /**
   * Imposta il valore del marcatore dei messaggi.
   * @param type  intero memorizzato in MonitorStreamData.type.
   */
  public void setType(int type)
  {
    this.type = type;
  }
}
