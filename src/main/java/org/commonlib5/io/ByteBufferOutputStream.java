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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Output stream che scrive in un array di bytes.
 * Questa classe è un buffer in memoria con ridimensionamento automatico
 * accessibile in scrittura come uno stream.
 * Il contenuto può essere letto per copia o con
 * un accesso diretto al buffer interno.
 * La dimensione iniziale è BUFFER_SIZE e durante la scrittura
 * il buffer si ridimensiona a passi di BUFFER_STEP per contenere i dati.
 * Il buffer non ha limiti di espansione fino all'esaurimento della memoria.
 *
 * @author Nicola De Nisco
 */
public class ByteBufferOutputStream extends OutputStream
{
  public static final int BUFFER_SIZE = 4096;
  public static final int BUFFER_STEP = 4096;
  protected int count = 0;
  protected byte[] buffer = null;

  public ByteBufferOutputStream()
  {
    buffer = new byte[BUFFER_SIZE];
  }

  public ByteBufferOutputStream(int initialSize)
  {
    buffer = new byte[initialSize];
  }

  /**
   * Scrive dati nel buffer.
   * I dati vengono copiati all'interno del buffer
   * ridimensiondo opportunamente il buffer interno.
   * La copia avviene fino all'esaurimento della memoria.
   * @param b byte da aggiungere al buffer
   * @throws IOException
   */
  @Override
  synchronized public void write(int b)
     throws IOException
  {
    while(count >= buffer.length)
    {
      resize();
    }

    buffer[count++] = (byte) b;
  }

  /**
   * Scrive dati nel buffer.
   * I dati vengono copiati all'interno del buffer
   * ridimensiondo opportunamente il buffer interno.
   * La copia avviene fino all'esaurimento della memoria.
   * @param b array di byte da copiare
   * @throws IOException
   */
  @Override
  synchronized public void write(byte[] b)
     throws IOException
  {
    write(b, 0, b.length);
  }

  /**
   * Scrive dati nel buffer.
   * I dati vengono copiati all'interno del buffer
   * ridimensiondo opportunamente il buffer interno.
   * La copia avviene fino all'esaurimento della memoria.
   * @param b array di byte da copiare
   * @param off offset del primo byte all'iterno di b
   * @param len numero di byte da copiare
   * @throws IOException
   */
  @Override
  synchronized public void write(byte[] b, int off, int len)
     throws IOException
  {
    while((count + len) > buffer.length)
    {
      resize();
    }

    System.arraycopy(b, off, buffer, count, len);
    count += len;
  }

  /**
   * Ridimensiona il buffer.
   * Il buffer viene riallocato aumentando al dimensione
   * di BUFFER_STEP bytes; i dati sono copiati nel nuovo
   * buffer.
   * @throws IOException
   */
  protected void resize()
     throws IOException
  {
    byte[] bnew = new byte[buffer.length + BUFFER_STEP];
    System.arraycopy(buffer, 0, bnew, 0, count);
    buffer = bnew;
  }

  /**
   * Restituisce il contenuto del buffer.
   * Viene creato un array della lunghezza corretta ovvero
   * del numero di byte presenti e riempito con il contenuto
   * del buffer interno.
   * @return array di byte copia del contenuto corrente
   * @throws IOException
   */
  synchronized public byte[] getBytes()
     throws IOException
  {
    if(count == 0)
      return null;

    byte[] bnew = new byte[count];
    System.arraycopy(buffer, 0, bnew, 0, count);
    return bnew;
  }

  /**
   * Scrive il contenuto del buffer in un output stream.
   * Il buffer non viene pulito. Occorre chimare il metodo clear()
   * per azzerarne il contenuto.
   * @param os destinazione del contenuto
   * @throws IOException
   */
  synchronized public void writeTo(OutputStream os)
     throws IOException
  {
    os.write(buffer, 0, count);
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
    return buffer;
  }

  /**
   * Ritorna la posizione di inserimento all'interno del buffer.
   * La posizione di inserimento è anche implicitamente il numero
   * di byte attualmente presenti nel buffer.
   * @return posizione (o numero di byte presenti) del buffer
   */
  synchronized public int position()
  {
    return count;
  }

  /**
   * Reset del buffer.
   * Il buffer interno viene riallocato con un
   * nuovo blocco di BUFFER_SIZE dimensioni e
   * il puntatore di inserimento azzerato.
   * Tutti i dati precedenti sono persi.
   */
  synchronized public void reset()
  {
    buffer = new byte[BUFFER_SIZE];
    count = 0;
  }

  /**
   * Azzeramento rapido del buffer.
   * Imposta a 0 il puntatore di inserimento,
   * azzerando dal punto di vista logico il
   * buffer. Il buffer interno non viene toccato,
   * di conseguenza dimensioni e dati precedenti
   * sono ancora gli stessi dopo la chiamata del
   * metodo.
   */
  synchronized public void clear()
  {
    count = 0;
  }

  /**
   * Cancella byte dalla testa del buffer.
   * @param numBytes numero di caratteri da cancellare
   */
  synchronized public void deleteHead(int numBytes)
  {
    if(numBytes >= buffer.length || numBytes >= count)
    {
      count = 0;
    }
    else
    {
      count -= numBytes;
      System.arraycopy(buffer, numBytes, buffer, 0, count);
    }
  }

  /**
   * Cancella byte dalla coda del buffer.
   * @param numBytes numero di caratteri da cancellare
   */
  synchronized public void deleteTail(int numBytes)
  {
    if(numBytes >= buffer.length || numBytes >= count)
    {
      count = 0;
    }
    else
    {
      count -= numBytes;
    }
  }

  /**
   * Cancella byte da un punto qualsiasi del buffer.
   * @param position posizione dove incominciare la cancellazione
   * @param numBytes numero di caratteri da cancellare
   */
  synchronized public void delete(int position, int numBytes)
  {
    if(position == 0)
    {
      deleteHead(numBytes);
      return;
    }

    if(position >= count)
      return;

    if(position + numBytes >= count)
    {
      count = position;
      return;
    }

    System.arraycopy(buffer, position + numBytes, buffer, position, count - (position + numBytes));
    count -= numBytes;
  }

  public boolean isEmpty()
  {
    return count == 0;
  }

  @Override
  public synchronized String toString()
  {
    return new String(buffer, 0, count);
  }

  public synchronized String toString(String encoding)
     throws UnsupportedEncodingException
  {
    return new String(buffer, 0, count, encoding);
  }

  public synchronized String toString(Charset encoding)
  {
    return new String(buffer, 0, count, encoding);
  }

  public synchronized String toString(int offset, int lenght, Charset encoding)
  {
    return new String(buffer, offset, lenght, encoding);
  }

  public synchronized int indexOf(byte test)
  {
    return indexOf(test, 0);
  }

  public synchronized int indexOf(byte test, int fromIndex)
  {
    if(fromIndex < count)
    {
      for(int i = fromIndex; i < count; i++)
      {
        if(buffer[i] == test)
          return i;
      }
    }

    return -1;
  }

  /**
   * Cerca la prima occorrenza dell'array indicato all'interno del buffer.
   * @param test array da cercare
   * @return posizione o -1 per non trovato
   */
  public synchronized int indexOf(byte[] test)
  {
    return indexOf(buffer, 0, count, test, 0, test.length, 0);
  }

  /**
   * Cerca la prima occorrenza dell'array indicato all'interno del buffer.
   * La ricerca avviene a cominciare dal byte indicato
   * @param test array da cercare
   * @param fromIndex indice del primo byte all'interno del buffer
   * @return posizione o -1 per non trovato
   */
  public synchronized int indexOf(byte[] test, int fromIndex)
  {
    return indexOf(buffer, 0, count, test, 0, test.length, fromIndex);
  }

  /**
   * Cerca un array di bytes all'interno di un'altro array di bytes.
   * @param source array dove cercare
   * @param sourceOffset
   * @param sourceCount
   * @param target array da cercare all'interno di source
   * @param targetOffset
   * @param targetCount
   * @param fromIndex indice iniziale da cui incominciare la ricerca
   * @return posizione di target all'interno di source o -1 se non trovato
   */
  static int indexOf(byte[] source, int sourceOffset, int sourceCount,
     byte[] target, int targetOffset, int targetCount,
     int fromIndex)
  {
    if(fromIndex >= sourceCount)
      return (targetCount == 0 ? sourceCount : -1);

    if(fromIndex < 0)
      fromIndex = 0;
    if(targetCount == 0)
      return fromIndex;

    byte first = target[targetOffset];
    int max = sourceOffset + (sourceCount - targetCount);

    for(int i = sourceOffset + fromIndex; i <= max; i++)
    {
      // cerca il primo byte
      if(source[i] != first)
      {
        while(++i <= max && source[i] != first);
      }

      // trovato il primo byte, cerca per gli altri
      if(i <= max)
      {
        int j = i + 1;
        int end = j + targetCount - 1;
        for(int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++);

        if(j == end)
        {
          // trovata la corrispondeza
          return i - sourceOffset;
        }
      }
    }

    return -1;
  }
}
