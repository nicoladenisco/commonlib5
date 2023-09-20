/*
 * ByteBufferInputStream.java
 *
 * Created on 21-gen-2009, 15.45.39
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.io;

import java.io.IOException;
import java.io.PushbackInputStream;

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
   * This indicates the maximum number of bytes that can be read from this
   * stream. It is the array index of the position after the last valid
   * byte in the buffer <code>buf</code>
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

    int avail = available();
    int newSize = avail + alength;
    byte[] newBuf = new byte[newSize];

    if(buf != null && avail != 0)
      System.arraycopy(buf, pos, newBuf, 0, avail);
    System.arraycopy(toAdd, apos, newBuf, avail, alength);

    buf = newBuf;
    pos = mark = 0;
    count = newBuf.length;

    notify();
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
    super.unread(b);
  }

  @Override
  public synchronized void unread(byte[] b, int off, int len)
     throws IOException
  {
    super.unread(b, off, len);
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
    return buf == null ? "" : new String(buf, pos, buf.length);
  }
}
