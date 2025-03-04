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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Accumulatore per i blocchi.
 * MonitorInputStream e MonitorOutputStream salvano i blocchi
 * di dati passati in oggetti MonitorStreamData in modo da
 * marcarli con data e ora.
 * Gli oggetti MonitorStreamData sono accumulati in un
 * MonitorStreamQueue per essere osservati.
 * La coda utilizzata per i blocchi è a dimensione fissa
 * e una volta riempita il flusso viene bloccato.
 * Un thread di ascolto può utilizzare la funzione bloccante
 * della coda per rimanere in attesa.
 *
 * @author Nicola De Nisco
 */
public class MonitorStreamQueue implements MonitorStreamStorage
{
  /** La coda dove salvare i blocchi osservati */
  protected ArrayBlockingQueue<MonitorStreamData> qData = null;
  /** La dimensione di default della coda */
  public static final int DEFAULT_QUEUE_LENGTH = 128;

  /**
   * Costruttore di default: lunghezza coda DEFAULT_QUEUE_LENGTH.
   */
  public MonitorStreamQueue()
  {
    this(DEFAULT_QUEUE_LENGTH);
  }

  /**
   * Costruisce con la lunghezza della coda specificata.
   * @param queueLength lunghezza della coda (numero blocchi max)
   */
  public MonitorStreamQueue(int queueLength)
  {
    qData = new ArrayBlockingQueue<MonitorStreamData>(queueLength, true);
  }

  /**
   * Controlla coda.
   * @return vero se la coda blocchi è vuota.
   */
  public boolean isEmpty()
  {
    return qData.isEmpty();
  }

  /**
   * Recupera da coda.
   * La chiamata è bloccante.
   * @return il prossimo oggetto nella coda
   */
  public MonitorStreamData pool()
  {
    return qData.poll();
  }

  /**
   * Recupera da coda.
   * La chiamata è bloccante.
   * @param timeout quanto aspettare
   * @param unit unità di tempo (@see TimeUnit)
   * @return il prossimo oggetto nella coda o null se timeout
   * @throws InterruptedException
   */
  public MonitorStreamData pool(long timeout, TimeUnit unit)
     throws InterruptedException
  {
    return qData.poll(timeout, unit);
  }

  /**
   * Preleva oggetto senza rimuoverlo dalla coda.
   * @return il prossimo oggetto nella coda o null se vuota
   */
  public MonitorStreamData peek()
  {
    return qData.peek();
  }

  /**
   * Recupera dimensione coda.
   * @return numero di oggetti nella coda
   */
  public int size()
  {
    return qData.size();
  }

  /**
   * Riceve un byte da aggiungere alla coda.
   * @param type marcatore del messaggio
   * @param byteValue valore del byte da aggiungere
   * @throws IOException
   */
  @Override
  public synchronized void addToStorage(int type, int byteValue)
     throws IOException
  {
    byte[] b = new byte[1];
    b[0] = (byte) byteValue;
    qData.add(new MonitorStreamData(type, new Date(), b));
  }

  /**
   * Riceve i byte da aggiungere alla coda.
   * I byte vengono copiati generando una nuova
   * entry nella coda.
   * @param type marcatore del messaggio
   * @param b array di byte da aggiungere
   * @throws java.io.IOException
   */
  @Override
  public synchronized void addToStorage(int type, byte[] b)
     throws IOException
  {
    qData.add(new MonitorStreamData(type, new Date(), b));
  }

  /**
   * Riceve i byte da aggiungere alla coda.
   * I byte vengono copiati generando una nuova
   * entry nella coda.
   * @param type marcatore del messaggio
   * @param b array di byte da aggiungere
   * @param offset offset all'interno di b
   * @param len numero di byte da aggiungere
   * @throws java.io.IOException
   */
  @Override
  public synchronized void addToStorage(int type, byte[] b, int offset, int len)
     throws IOException
  {
    byte[] bb = new byte[len];
    System.arraycopy(b, offset, bb, 0, len);
    qData.add(new MonitorStreamData(type, new Date(), bb));
  }

  @Override
  public void flush()
     throws IOException
  {
  }

  @Override
  public void addComment(int type, String comment)
     throws IOException
  {
    qData.add(new MonitorStreamData(type, new Date(), comment.getBytes()));
  }
}
