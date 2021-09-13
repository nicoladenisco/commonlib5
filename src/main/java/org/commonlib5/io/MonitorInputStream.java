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
import java.io.InputStream;

/**
 * Input stream di monitoraggio.
 * Questa classe è un input stream passante che consente il
 * salvataggio di tutti i blocchi di dati in arrivo dallo
 * stream vero e proprio per ottenere informazioni di logging.
 * Tutte le funzioni sono thread safe.
 *
 * @author Nicola De Nisco
 */
public class MonitorInputStream extends InputStream
{
  /** Lo stream da monitorare. */
  protected InputStream is = null;
  /** La coda di accumulo. */
  protected MonitorStreamStorage queue = null;
  /** Marcatore dei messaggi nella coda (per default 'I'). */
  protected int type = 'I';

  /**
   * Costruisce una coda di default.
   * @param is input stream da monitorare
   */
  public MonitorInputStream(InputStream is)
  {
    this.is = is;
    this.queue = new MonitorStreamQueue();
  }

  /**
   * Costruisce con specifica coda.
   * @param is input stream da monitorare
   * @param queue coda messaggi
   */
  public MonitorInputStream(InputStream is, MonitorStreamStorage queue)
  {
    this.is = is;
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
  public int available() throws IOException
  {
    return is.available();
  }

  @Override
  public void close() throws IOException
  {
    is.close();
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    is.mark(readlimit);
  }

  @Override
  public boolean markSupported()
  {
    return is.markSupported();
  }

  @Override
  public int read() throws IOException
  {
    int rv = is.read();
    if(rv != -1)
      queue.addToStorage(type, rv);
    return rv;
  }

  @Override
  public int read(byte[] b) throws IOException
  {
    return read(b, 0, b.length);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException
  {
    int rv = is.read(b, off, len);
    if(rv > 0)
      queue.addToStorage(type, b, off, rv);

    return rv;
  }

  @Override
  public synchronized void reset() throws IOException
  {
    is.reset();
  }

  @Override
  public long skip(long n) throws IOException
  {
    return is.skip(n);
  }

  /**
   * Ritorna lo stream di riferimento.
   * @return stream aperto
   */
  public InputStream getStream()
  {
    return is;
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
