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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Un Set che usa un array come storage.
 * E' più lento di una hashset, ma conserva l'ordine di inserimento.
 *
 * @author Nicola De Nisco
 * @param <T>
 */
public class ArraySet<T> extends ArrayList<T>
   implements Set<T>, Serializable
{
  public ArraySet()
  {
  }

  public ArraySet(int i)
  {
    super(i);
  }

  public ArraySet(Collection<? extends T> clctn)
  {
    super(clctn.size());
    clctn.forEach((t) -> add(t));
  }

  @Override
  public boolean add(T e)
  {
    if(contains(e))
      return false;

    return super.add(e);
  }

  @Override
  public void add(int index, T e)
  {
    if(contains(e))
      return;

    super.add(index, e);
  }

  @Override
  public boolean addAll(Collection<? extends T> c)
  {
    int size = size();
    for(T t : c)
      add(t);
    return size != size();
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c)
  {
    int size = size();
    for(T t : c)
      add(index++, t);
    return size != size();
  }

  @Override
  public T set(int index, T element)
  {
    if(contains(element))
      return null;

    return super.set(index, element);
  }
}
