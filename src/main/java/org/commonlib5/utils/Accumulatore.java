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
package org.commonlib5.utils;

import java.io.Flushable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.commonlib5.lambda.ConsumerThrowException;

/**
 * Accumulatore (buffer) di oggetti.
 * Estende ArrayList.
 * Usando add() gli oggetti vengono aggiunti.
 * Quando il numero di oggetti
 * contenuti supera flushSize viene chiamata il consumer
 * per processare gli oggetti.
 * Dopo aver processato gli oggetti l'array viene
 * svuotato per poter accumulare nuovi oggetti.
 * Alla fine dell'utilizzo va chiamato flush() per consumare il residuo.
 * @author Nicola De Nisco
 * @param <T>
 */
public class Accumulatore<T> extends ArrayList<T>
   implements Flushable
{
  private int flushSize;
  private final ConsumerThrowException<List<T>> consumer;

  public Accumulatore(int flushSize, ConsumerThrowException<List<T>> consumer)
  {
    this.flushSize = flushSize;
    this.consumer = consumer;
  }

  public Accumulatore(int flushSize, ConsumerThrowException<List<T>> consumer, int initialCapacity)
  {
    super(initialCapacity);
    this.flushSize = flushSize;
    this.consumer = consumer;
  }

  public Accumulatore(int flushSize, ConsumerThrowException<List<T>> consumer, Collection<? extends T> c)
  {
    super(c);
    this.flushSize = flushSize;
    this.consumer = consumer;
  }

  public int getFlushSize()
  {
    return flushSize;
  }

  public void setFlushSize(int flushSize)
  {
    this.flushSize = flushSize;
  }

  @Override
  public boolean add(T e)
  {
    boolean rv = super.add(e);
    checkForFlush();
    return rv;
  }

  @Override
  public void add(int index, T element)
  {
    super.add(index, element);
    checkForFlush();
  }

  @Override
  public boolean addAll(Collection<? extends T> c)
  {
    for(T t : c)
      add(t);
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c)
  {
    for(T t : c)
      add(index++, t);
    return true;
  }

  @Override
  public void flush()
  {
    try
    {
      if(!isEmpty())
      {
        consumer.accept(this);
        clear();
      }
    }
    catch(Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private void checkForFlush()
  {
    if(size() >= flushSize)
      flush();
  }
}
