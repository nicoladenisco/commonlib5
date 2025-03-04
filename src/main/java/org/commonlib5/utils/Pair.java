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

import java.util.Map.Entry;

/**
 * Generico contenitore per due valori.
 *
 * @author Nicola De Nisco
 * @param <V>
 */
public class Pair<K, V> implements Entry<K, V>
{
  public K first;
  public V second;

  public Pair()
  {
  }

  public Pair(K k, V v)
  {
    this.first = k;
    this.second = v;
  }

  public K getFirst()
  {
    return first;
  }

  public void setFirst(K first)
  {
    this.first = first;
  }

  public V getSecond()
  {
    return second;
  }

  public void setSecond(V second)
  {
    this.second = second;
  }

  @Override
  public int hashCode()
  {
    return (first == null ? 0 : first.hashCode())
       ^ (second == null ? 0 : second.hashCode());
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    Pair<K, V> other = (Pair<K, V>) obj;
    if(this.first != other.first && (this.first == null || !this.first.equals(other.first)))
      return false;
    if(this.second != other.second && (this.second == null || !this.second.equals(other.second)))
      return false;
    return true;
  }

  @Override
  public String toString()
  {
    return first + "=" + second;
  }

  @Override
  public K getKey()
  {
    return first;
  }

  @Override
  public V getValue()
  {
    return second;
  }

  @Override
  public V setValue(V value)
  {
    return second = value;
  }
}
