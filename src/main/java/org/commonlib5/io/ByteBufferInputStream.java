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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Implementazione di un buffer di bytes utilizzabile
 * come InputStream. L'estrazione dei dati avviene attraverso
 * le consuete funzioni di InputStream, ma e' possibile
 * inserire dati attraverso addToBuffer.
 * <p>
 * L'implementazione e' thread safe.
 * <p>
 * Il comportamento puo' essere bloccante (default) ovvero
 * quando il buffer e' vuoto letture dallo stream provocano
 * il blocco del thread che legge, oppure non bloccante
 * (read ritorna 0 quando lo stream e' vuoto).
 * @author Nicola De Nisco
 */
public class ByteBufferInputStream extends PushbackInputStream
{
  // le dimensioni sono state prese dai default di InputStream in Java26 (DEFAULT_BUFFER_SIZE)
  public static final int BUFFER_SIZE = 16384;

  /**
   * Determina il comportamento bloccante/non bloccante.
   */
  private boolean blocking = true;

  /**
   * The currently marked position in the stream. This defaults to 0, so a
   * reset operation on the stream resets it to read from array index 0 in
   * the buffer - even if the stream was initially created with an offset
   * greater than 0
   */
  protected int mark;

  /**
   * Posizione di inserimento nel buffer.
   * This indicates the maximum number of bytes that can be read from this
   * stream. It is the array index of the position after the last valid
   * byte in the buffer <code>buf</code>.
   */
  protected int count;

  /**
   * Create a new ByteBufferInputStream.
   */
  public ByteBufferInputStream()
  {
    super(null);
    this.blocking = true;
    buf = null;
    count = 0;
    mark = 0;
    pos = 0;
  }

  /**
   * Crea un nuovo buffer.
   * @param blocking se vero il comportamento e' bloccante
   */
  public ByteBufferInputStream(boolean blocking)
  {
    super(null);
    this.blocking = blocking;
    buf = null;
    count = 0;
    mark = 0;
    pos = 0;
  }

  /**
   * Crea un nuovo buffer.
   * @param blocking se vero il comportamento e' bloccante
   * @param initialContent contenuto iniziale del buffer
   */
  public ByteBufferInputStream(boolean blocking, byte[] initialContent)
  {
    this(blocking);
    addToBuffer(initialContent);
  }

  /**
   * This method returns the number of bytes available to be read from this
   * stream. The value returned will be equal to <code>count - pos</code>.
   *
   * @return The number of bytes that can be read from this stream
   * before blocking, which is all of them
   */
  @Override
  public synchronized int available()
  {
    return count - pos;
  }

  /**
   * This method reads one byte from the stream. The <code>pos</code>
   * counter is advanced to the next byte to be read. The byte read is
   * returned as an int in the range of 0-255. If the stream position
   * is already at the end of the buffer, no byte is read and a -1 is
   * returned in order to indicate the end of the stream.
   *
   * @return The byte read, or -1 if end of stream
   */
  @Override
  public synchronized int read()
  {
    while(pos >= count)
    {
      if(blocking)
      {
        try
        {
          wait();
          continue;
        }
        catch(InterruptedException ex)
        {
          return -1;
        }
      }

      return -1;
    }

    return ((int) buf[pos++]) & 0xFF;
  }

  /**
   * This method reads bytes from the stream and stores them into a
   * caller supplied buffer. It starts storing the data at index
   * <code>offset</code> into the buffer and attempts to read
   * <code>len</code> bytes. This method can return before reading
   * the number of bytes requested if the end of the stream is
   * encountered first. The actual number of bytes read is returned.
   * If no bytes can be read because the stream is already at the end
   * of stream position, a -1 is returned.
   * <p>
   * This method does not block.
   *
   * @param buffer The array into which the bytes read should be stored.
   * @param offset The offset into the array to start storing bytes
   * @param length The requested number of bytes to read
   *
   * @return The actual number of bytes read, or -1 if end of stream.
   */
  @Override
  public synchronized int read(byte[] buffer, int offset, int length)
  {
    while(pos >= count)
    {
      if(blocking)
      {
        try
        {
          wait();
          continue;
        }
        catch(InterruptedException ex)
        {
          return -1;
        }
      }

      return -1;
    }

    int numBytes = Math.min(count - pos, length);
    System.arraycopy(buf, pos, buffer, offset, numBytes);
    pos += numBytes;
    return numBytes;
  }

  /**
   * This method attempts to skip the requested number of bytes in the
   * input stream. It does this by advancing the <code>pos</code>
   * value by the specified number of bytes. It this would exceed the
   * length of the buffer, then only enough bytes are skipped to
   * position the stream at the end of the buffer. The actual number
   * of bytes skipped is returned.
   *
   * @param num The requested number of bytes to skip
   *
   * @return The actual number of bytes skipped.
   */
  @Override
  public synchronized long skip(long num)
  {
    // Even though the var numBytes is a long, in reality it can never
    // be larger than an int since the result of subtracting 2 positive
    // ints will always fit in an int.  Since we have to return a long
    // anyway, numBytes might as well just be a long.
    long numBytes = Math.min((long) (count - pos), num < 0 ? 0L : num);
    pos += numBytes;
    return numBytes;
  }

  /**
   * Ricerca di pattern nel buffer.
   * Lo stream viene letto fino a trovare la pattern indicata.
   * Se la pattern viene trovata la posizione dello stream sarà al byte immediatamente successivo.
   * @param pattern sequenza di byte da cercare
   * @return vero se la pattern è stata trovata
   * @throws IOException
   */
  public synchronized boolean skipPattern(byte[] pattern)
     throws IOException
  {
    int i = 0, c = 0;

    do
    {
      for(i = 0; i < pattern.length; i++)
      {
        if((c = read()) == -1)
          return false;

        if(c != pattern[i])
          break;
      }
    }
    while(i < pattern.length);

    return true;
  }

  /**
   * Aggiunge byte al buffer in memoria.
   * @param toAdd byte da aggiungere
   */
  public synchronized void addToBuffer(int toAdd)
  {
    byte[] dummy = new byte[1];
    dummy[0] = (byte) toAdd;
    addToBuffer(dummy, 0, 1);
  }

  /**
   * Aggiunge byte al buffer in memoria.
   * E' equivalente a addToBuffer(toAdd, 0, toAdd.length).
   * @param toAdd array con dati da aggiungere
   */
  public synchronized void addToBuffer(byte[] toAdd)
  {
    addToBuffer(toAdd, 0, toAdd.length);
  }

  /**
   * Aggiunge byte al buffer in memoria.
   * Il file viene letto in memoria (BUFFER_SIZE) e aggiunto al buffer.
   * @param toAdd file con dati da aggiungere
   * @throws FileNotFoundException
   * @throws IOException
   */
  public synchronized void addToBuffer(File toAdd)
     throws FileNotFoundException, IOException
  {
    long lenFile = toAdd.length();
    if(lenFile == 0)
      return;

    // prealloca il buffer per contenere tutto il file
    ensureSize((int) lenFile);

    try(InputStream is = new FileInputStream(toAdd))
    {
      int n;
      byte[] buffer = new byte[BUFFER_SIZE];
      while((n = is.read(buffer)) > 0)
      {
        addToBuffer(buffer, 0, n);
      }
    }
  }

  /**
   * Aggiunge byte al buffer in memoria.
   * L'array di byte passato viene copiato nel buffer interno
   * dopo che questi e' stato allargato di conseguenza.
   * Non ci sono limiti alla dimensione interna del buffer.
   * @param toAdd array con dati da aggiungere
   * @param apos posizione iniziale da copiare
   * @param alength numero di byte da copiare
   */
  public synchronized void addToBuffer(byte[] toAdd, int apos, int alength)
  {
    if(alength == 0)
      return;

    ensureSize(alength);
    System.arraycopy(toAdd, apos, buf, pos, alength);
    pos = mark = 0;
    count += alength;
    notify();
  }

  /**
   * Alloca il buffer per la lughezza richiesta.
   * @param alength la dimensione richiesta per il buffer
   * @return la dimensione ancora disponibile nel buffer
   */
  public synchronized int ensureSize(int alength)
  {
    if(alength == 0)
      return buf.length - count;

    int avail = available();
    int newSize = avail + alength;

    if(buf == null)
    {
      // il buffer non è stato ancora creato
      buf = new byte[newSize + BUFFER_SIZE];
      pos = mark = 0;
      count = 0;
    }
    else if(buf.length > newSize)
    {
      // il buffer è sufficientemente capiente per la dimensione richiesta
    }
    else
    {
      // il buffer va riallocato e copiato
      // attenzione: qui vengono copiati solo i bytes ancora da leggere
      byte[] newBuf = new byte[newSize + BUFFER_SIZE];
      if(avail != 0)
        System.arraycopy(buf, pos, newBuf, 0, avail);
      buf = newBuf;
      count = avail;
      pos = mark = 0;
    }

    return buf.length - count;
  }

  /**
   * Chiude lo stream.
   * Essendo questo un buffer in memoria, resetta
   * lo stato del buffer ma in realtà è ancora possibile
   * scrivere sullo stream dopo la chiamata di close.
   * @throws IOException
   */
  @Override
  public synchronized void close()
     throws IOException
  {
    buf = null;
    count = 0;
    mark = 0;
    pos = 0;
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    mark = pos;
  }

  @Override
  public boolean markSupported()
  {
    return true;
  }

  @Override
  public synchronized void reset()
     throws IOException
  {
    pos = mark;
  }

  @Override
  public synchronized void unread(int b)
     throws IOException
  {
    ensureSize(1);
    if(pos == 0)
    {
      throw new IOException("Push back buffer is full");
    }
    buf[--pos] = (byte) b;
  }

  @Override
  public void unread(byte[] b, int off, int len)
     throws IOException
  {
    ensureSize(len);
    if(len > pos)
    {
      throw new IOException("Push back buffer is full");
    }
    pos -= len;
    System.arraycopy(b, off, buf, pos, len);
  }

  @Override
  public synchronized void unread(byte[] b)
     throws IOException
  {
    super.unread(b);
  }

  /**
   * Ritorna lo stato di bloccante/non bloccante del buffer.
   * @return vero se il buffer e' bloccante.
   */
  public boolean isBlocking()
  {
    return blocking;
  }

  /**
   * Imposta lo stato di bloccante/non bloccante del buffer.
   * @param blocking vero per funzione bloccante.
   */
  public void setBlocking(boolean blocking)
  {
    this.blocking = blocking;
  }

  @Override
  public synchronized String toString()
  {
    return buf == null ? "" : new String(buf, pos, count - pos);
  }

  public synchronized String toString(String encoding)
     throws UnsupportedEncodingException
  {
    return buf == null ? "" : new String(buf, pos, count - pos, encoding);
  }

  public synchronized String toString(Charset encoding)
  {
    return buf == null ? "" : new String(buf, pos, count - pos, encoding);
  }

  /**
   * Accesso diretto al buffer interno.
   * Consente di accedere direttamente al buffer interno
   * dei byte salvati. Il numero di byte validi all'interno
   * di questo buffer viene restituito da position().
   * @return buffer interno con i dati accumulati
   */
  synchronized public byte[] array()
  {
    return buf;
  }
}
