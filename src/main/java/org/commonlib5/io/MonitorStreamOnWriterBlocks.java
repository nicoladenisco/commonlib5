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

/**
 * Accumulutare di blocchi con ricerca di marcatori.
 * Questa classe estende MonitorStreamOnWriter con la funzionalità
 * di cercare dei marcatori nei dati inviati alla coda che delimitano
 * i blocchi da archiviare.
 * Un buffer per i dati viene utilizzato per accumulare i byte
 * mentre passano e un analizzatore verifica quando compare una
 * determinata pattern che marca la fine di un blocco e l'inizio
 * del successivo.
 * I blocchi vengono inviati al file di log.
 *
 * @author Nicola De Nisco
 */
public class MonitorStreamOnWriterBlocks extends MonitorStreamOnWriter
{
  /** La sequenza di byte che marca la fine di un blocco */
  protected byte[] blockBoundary = null;
  /** Buffer per l'accumulo temporaneo prima di inserire nella coda. */
  protected ByteBufferOutputStream bb = new ByteBufferOutputStream();
  /** Memorizza l'ultimo tipo di memorizzazione effettuata. */
  protected int lastType = 0;

  public MonitorStreamOnWriterBlocks(PrintWriter wr)
  {
    super(wr);
    blockBoundary = "\r\n".getBytes();
  }

  public MonitorStreamOnWriterBlocks(PrintWriter wr, byte[] blockBoundary)
  {
    super(wr);
    this.blockBoundary = blockBoundary;
  }

  public byte[] getBlockBoundary()
  {
    return blockBoundary;
  }

  public void setBlockBoundary(byte[] blockBoundary)
  {
    this.blockBoundary = blockBoundary;
  }

  /**
   * Riceve un byte da aggiungere alla coda.
   * @param type tipo di dato memorizzato ('I' 'O' ...)
   * @param byteValue valore del byte da aggiungere
   * @throws IOException
   */
  @Override
  public synchronized void addToStorage(int type, int byteValue)
     throws IOException
  {
    if(type != lastType)
    {
      saveTheBuffer();
      lastType = type;
    }

    bb.write(byteValue);
    if(bb.position() > blockBoundary.length)
      checkBuffer();
  }

  /**
   * Riceve i byte da aggiungere alla coda.
   * I byte vengono copiati in un buffer e confrontati con blockBoundary
   * per determinare la fine di un blocco, generando una nuova
   * entry nella coda.
   * @param type tipo di dato memorizzato ('I' 'O' ...)
   * @param b array di byte da aggiungere
   * @throws java.io.IOException
   */
  @Override
  public synchronized void addToStorage(int type, byte[] b)
     throws IOException
  {
    addToStorage(type, b, 0, b.length);
  }

  /**
   * Riceve i byte da aggiungere alla coda.
   * I byte vengono copiati in un buffer e confrontati con blockBoundary
   * per determinare la fine di un blocco, generando una nuova
   * entry nella coda.
   * @param type tipo di dato memorizzato ('I' 'O' ...)
   * @param b array di byte da aggiungere
   * @param offset offset all'interno di b
   * @param len numero di byte da aggiungere
   * @throws java.io.IOException
   */
  @Override
  public synchronized void addToStorage(int type, byte[] b, int offset, int len)
     throws IOException
  {
    if(type != lastType)
    {
      saveTheBuffer();
      lastType = type;
    }

    bb.write(b, offset, len);
    if(bb.position() > blockBoundary.length)
      checkBuffer();
  }

  /**
   * Verifica buffer e genera coda.
   * Controlla il contenuto del buffer a caccia di blockBoundary
   * e se necessario genera una nuova entry nella coda.
   * @throws java.io.IOException
   */
  protected void checkBuffer()
     throws IOException
  {
    synchronized(bb)
    {
      int pos = bb.indexOf(blockBoundary);

      if(pos != -1)
      {
        int last = pos + blockBoundary.length;

        // ho trovato il blocco
        printBuffer(lastType, bb.array(), 0, last);
        bb.deleteHead(last);

        // verifica se nel residuo ci sono altri marcatori
        if(bb.position() > blockBoundary.length)
          checkBuffer();
      }
    }
  }

  /**
   * Salva il contenuto attuale del buffer e lo pulisce.
   * @throws IOException
   */
  protected void saveTheBuffer()
     throws IOException
  {
    synchronized(bb)
    {
      if(bb.isEmpty())
        return;

      printBuffer(lastType, bb.buffer, 0, bb.position());
      bb.clear();
    }
  }

  @Override
  public void flush()
     throws IOException
  {
    saveTheBuffer();
  }
}
