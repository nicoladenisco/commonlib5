/*
 * Copyright (C) 2017 Nicola De Nisco
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
import java.util.*;
import org.commonlib5.lambda.ConsumerThrowException;

/**
 * Una Map che usa un array come storage.
 * E' più lenta di una hashmap, ma conserva l'ordine di inserimento.
 *
 * @author Nicola De Nisco
 * @param <K>
 * @param <V>
 */
public class ArrayMap<K, V> extends AbstractMap<K, V>
   implements Serializable
{
  protected final ArraySet<Entry<K, V>> theSet;

  public ArrayMap()
  {
    theSet = new ArraySet<>();
  }

  public ArrayMap(int size)
  {
    theSet = new ArraySet<>(size);
  }

  public ArrayMap(Map<K, V> othermap)
  {
    theSet = new ArraySet<>(othermap.size());
    putAll(othermap);
  }

  public ArrayMap(Collection<Pair<K, V>> pcoll)
  {
    theSet = new ArraySet<>(pcoll.size());
    addAll(pcoll);
  }

  /**
   * Costruttore da un numero pari di oggetti.
   * Per ogni coppia il primo è la chiave il secondo il valore.
   * @param pairObjects oggetti da inserire a coppie
   */
  public ArrayMap(Objects[] pairObjects)
  {
    if((pairObjects.length & 1) != 0)
      throw new IllegalArgumentException("The array must have a pair length.");

    theSet = new ArraySet<>(pairObjects.length / 2);

    for(int i = 0; i < pairObjects.length; i += 2)
    {
      Objects o1 = pairObjects[i];
      Objects o2 = pairObjects[i + 1];

      put((K) o1, (V) o2);
    }
  }

  @Override
  public Set<Entry<K, V>> entrySet()
  {
    return theSet;
  }

  @Override
  public V put(K key, V value)
  {
    if(key != null)
    {
      remove(key);
      add(new Pair<>(key, value));
    }

    return value;
  }

  public void add(Pair<K, V> p)
  {
    theSet.add(p);
  }

  public void addAll(Collection<Pair<K, V>> pcoll)
  {
    theSet.addAll(pcoll);
  }

  public List<Pair<K, V>> getAsList()
  {
    ArrayList<Pair<K, V>> rv = new ArrayList<>();
    for(Entry<K, V> entry : this.entrySet())
      rv.add((Pair<K, V>) entry);
    return rv;
  }

  public K getKeyByIndex(int idx)
  {
    return theSet.get(idx).getKey();
  }

  public V getValueByIndex(int idx)
  {
    return theSet.get(idx).getValue();
  }

  public Pair<K, V> getPairByIndex(int i)
  {
    return (Pair<K, V>) theSet.get(i);
  }

  public void forEach(ConsumerThrowException<Pair<K, V>> fun)
     throws Exception
  {
    for(Entry<K, V> entry : theSet)
      fun.accept((Pair<K, V>) entry);
  }

  public void forEachKey(ConsumerThrowException<K> fun)
     throws Exception
  {
    for(Entry<K, V> entry : theSet)
      fun.accept(entry.getKey());
  }

  public void forEachValue(ConsumerThrowException<V> fun)
     throws Exception
  {
    for(Entry<K, V> entry : theSet)
      fun.accept(entry.getValue());
  }

  public V getIgnoreCase(String key)
  {
    Iterator<Entry<K, V>> i = entrySet().iterator();
    if(key == null)
    {
      while(i.hasNext())
      {
        Entry<K, V> e = i.next();
        if(e.getKey() == null)
          return e.getValue();
      }
    }
    else
    {
      while(i.hasNext())
      {
        Entry<K, V> e = i.next();
        if(key.equalsIgnoreCase(e.getKey().toString()))
          return e.getValue();
      }
    }
    return null;
  }

  public void sortByKey(Comparator<K> cmp)
  {
    List<Pair<K, V>> rv = getAsList();
    rv.sort((a, b) -> cmp.compare(a.first, b.first));
    clear();
    addAll(rv);
  }

  public void sortByValue(Comparator<V> cmp)
  {
    List<Pair<K, V>> rv = getAsList();
    rv.sort((a, b) -> cmp.compare(a.second, b.second));
    clear();
    addAll(rv);
  }

  public List<Pair<K, V>> getSortedByKey(Comparator<K> cmp)
  {
    List<Pair<K, V>> rv = getAsList();
    rv.sort((a, b) -> cmp.compare(a.first, b.first));
    return rv;
  }

  public List<Pair<K, V>> getSortedByValue(Comparator<V> cmp)
  {
    List<Pair<K, V>> rv = getAsList();
    rv.sort((a, b) -> cmp.compare(a.second, b.second));
    return rv;
  }
}
