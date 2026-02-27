/*
 * Copyright (C) 2026 Nicola De Nisco
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * Array circolare.
 * Consente di estrarre l'elemento precedente e quello successivo
 * in senso circolare, in base ad una funziona di verifica passata.
 * @author Nicola De Nisco
 */
public class CircularArray<T> extends ArrayList<T>
{
  public CircularArray()
  {
  }

  public CircularArray(int initialCapacity)
  {
    super(initialCapacity);
  }

  public CircularArray(Collection<? extends T> c)
  {
    super(c);
  }

  /**
   * Ritorna elemento precedente.
   * @param i indice da cui cominciare la ricerca
   * @param test funzione di verifica: vero è valido
   * @return l'elemento trovato oppure null
   */
  public T getPrevValid(int i, Function<T, Boolean> test)
  {
    T rv;
    int prev = i;
    do
    {
      prev = --prev % size();
      if(prev < 0)
        prev = size() + prev;
      if(prev == i)
        return null;

      rv = get(prev);
    }
    while(!test.apply(rv));
    return rv;
  }

  /**
   * Ritorna elemento successivo.
   * @param i indice da cui cominciare la ricerca
   * @param test funzione di verifica: vero è valido
   * @return l'elemento trovato oppure null
   */
  public T getSuccValid(int i, Function<T, Boolean> test)
  {
    T rv;
    int prev = i;
    do
    {
      prev = ++prev % size();
      if(prev == i)
        return null;

      rv = get(prev);
    }
    while(!test.apply(rv));
    return rv;
  }
}
