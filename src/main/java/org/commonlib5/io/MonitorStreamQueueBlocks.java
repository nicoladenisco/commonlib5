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
import java.util.Date;

/**
 * Accumulutare di blocchi con ricerca di marcatori.
 * Questa classe estende MonitorStreamQueue con la funzionalità
 * di cercare dei marcatori nei dati inviati alla coda che delimitano
 * i blocchi da archiviare.
 * Un buffer per i dati viene utilizzato per accumulare i byte
 * mentre passano e un analizzatore verifica quando compare una
 * determinata pattern che marca la fine di un blocco e l'inizio
 * del successivo.
 * I blocchi vengono salvati nella coda per ispezione successiva.
 *
 * @author Nicola De Nisco
 */
public class MonitorStreamQueueBlocks extends MonitorStreamQueue
{
  /** La sequenza di byte che marca la fine di un blocco */
  protected byte[] blockBoundary = null;
  /** Buffer per l'accumulo temporaneo prima di inserire nella coda. */
  protected ByteBufferOutputStream bb = new ByteBufferOutputStream();
  /** Memorizza l'ultimo tipo di memorizzazione effettuata. */
  protected int lastType = 0;

  /**
   * Costruisce con il marcatore di default '\r\n'.
   */
  public MonitorStreamQueueBlocks()
  {
    blockBoundary = "\r\n".getBytes();
  }

  /**
   * Costruisce con la lunghezza e il marcatore specificato.
   * @param queueLength lunghezza della coda (numero blocchi max)
   * @param blockBoundary marcatore di fine blocco
   */
  public MonitorStreamQueueBlocks(int queueLength, byte[] blockBoundary)
  {
    super(queueLength);
    this.blockBoundary = blockBoundary;
  }

  /**
   * Riceve un byte da aggiungere alla coda.
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
    byte b[] = bb.array();
    int len = bb.position();
    int j = 0;

    for(int i = 0; i < len; i++)
    {
      if(b[i] == blockBoundary[j])
      {
        if(++j >= blockBoundary.length)
        {
          // ho trovato il blocco
          synchronized(bb)
          {
            byte[] bt = new byte[i + 1];
            System.arraycopy(b, 0, bt, 0, i + 1);
            qData.add(new MonitorStreamData(lastType, new Date(), bt));
            bb.deleteHead(i + 1);
          }

          // verifica se nel residuo ci sono altri marcatori
          if((len - i) > blockBoundary.length)
            checkBuffer();

          return;
        }
      }
      else
      {
        j = 0;
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

      qData.add(new MonitorStreamData(lastType, new Date(), bb.getBytes()));
      bb.clear();
    }
  }
}
