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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Sostituto della classe originaria in java.io,
 * ma con una corretta gestione dei caratteri e dell'enconding.
 *
 * @author Nicola De Nisco
 */
public class StringBufferInputStreamCorrected extends InputStream
{
  /**
   * The string from which bytes are read.
   */
  protected byte[] buffer = null;
  /**
   * The index of the next character to read from the input stream buffer.
   */
  protected int pos = 0;
  /**
   * The number of valid characters in the input stream buffer.
   */
  protected int count = 0;

  /**
   * Creates a string input stream to read data from the specified string.
   *
   * @param s the underlying input buffer.
   */
  public StringBufferInputStreamCorrected(String s)
  {
    this.buffer = s.getBytes();
    count = buffer.length;
  }

  /**
   * Creates a string input stream to read data from the specified string.
   *
   * @param s the underlying input buffer.
   * @param charsetName
   * @throws java.io.UnsupportedEncodingException
   */
  public StringBufferInputStreamCorrected(String s, String charsetName)
     throws UnsupportedEncodingException
  {
    this.buffer = s.getBytes(charsetName);
    count = buffer.length;
  }

  /**
   * Reads the next byte of data from this input stream. The value
   * byte is returned as an <code>int</code> in the range
   * <code>0</code> to <code>255</code>. If no byte is available
   * because the end of the stream has been reached, the value
   * <code>-1</code> is returned.
   * <p>
   * The <code>read</code> method of
   * <code>StringBufferInputStreamCorrected</code> cannot block. It returns the
   * low eight bits of the next character in this input stream's buffer.
   *
   * @return the next byte of data, or <code>-1</code> if the end of the
   * stream is reached.
   */
  @Override
  public synchronized int read()
  {
    return (pos < count) ? (buffer[pos++] & 0xFF) : -1;
  }

  /**
   * Reads up to <code>len</code> bytes of data from this input stream
   * into an array of bytes.
   * <p>
   * The <code>read</code> method of
   * <code>StringBufferInputStreamCorrected</code> cannot block. It copies the
   * low eight bits from the characters in this input stream's buffer into
   * the byte array argument.
   *
   * @param b the buffer into which the data is read.
   * @param off the start offset of the data.
   * @param len the maximum number of bytes read.
   * @return the total number of bytes read into the buffer, or
   * <code>-1</code> if there is no more data because the end of
   * the stream has been reached.
   */
  @Override
  public synchronized int read(byte b[], int off, int len)
  {
    if(b == null)
    {
      throw new NullPointerException();
    }
    else if((off < 0) || (off > b.length) || (len < 0)
       || ((off + len) > b.length) || ((off + len) < 0))
    {
      throw new IndexOutOfBoundsException();
    }
    if(pos >= count)
    {
      return -1;
    }
    if(pos + len > count)
    {
      len = count - pos;
    }
    if(len <= 0)
    {
      return 0;
    }

    System.arraycopy(buffer, pos, b, off, len);
    pos += len;
    return len;
  }

  /**
   * Skips <code>n</code> bytes of input from this input stream. Fewer
   * bytes might be skipped if the end of the input stream is reached.
   *
   * @param n the number of bytes to be skipped.
   * @return the actual number of bytes skipped.
   */
  @Override
  public synchronized long skip(long n)
  {
    if(n < 0)
    {
      return 0;
    }
    if(n > count - pos)
    {
      n = count - pos;
    }
    pos += n;
    return n;
  }

  /**
   * Returns the number of bytes that can be read from the input
   * stream without blocking.
   *
   * @return the value of <code>count&nbsp;-&nbsp;pos</code>, which is the
   * number of bytes remaining to be read from the input buffer.
   */
  @Override
  public synchronized int available()
  {
    return count - pos;
  }

  /**
   * Resets the input stream to begin reading from the first character
   * of this input stream's underlying buffer.
   */
  @Override
  public synchronized void reset()
  {
    pos = 0;
  }
}
