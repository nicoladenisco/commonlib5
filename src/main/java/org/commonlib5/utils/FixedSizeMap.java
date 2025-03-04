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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mappa con un numero massimo di entry.
 * Quando aggiungo più elementi di maxSize vengono cancellati i più vecchi.
 *
 * @author Nicola De Nisco
 * @param <K>
 * @param <V>
 */
public class FixedSizeMap<K, V> extends ArrayMap<K, V>
{
  protected int maxSize;

  public FixedSizeMap(int maxSize)
  {
    this.maxSize = maxSize;
  }

  public FixedSizeMap(int initialSize, int maxSize)
  {
    super(initialSize);
    this.maxSize = maxSize;
  }

  public int getMaxSize()
  {
    return maxSize;
  }

  public void setMaxSize(int maxSize)
  {
    this.maxSize = maxSize;
  }

  @Override
  public void add(Pair<K, V> p)
  {
    ensureSize(1);
    theSet.add(p);
  }

  @Override
  public void addAll(Collection<Pair<K, V>> pcoll)
  {
    final int sz = pcoll.size();
    ensureSize(sz);

    if(sz < maxSize)
    {
      theSet.addAll(pcoll);
      return;
    }

    List<Pair<K, V>> tmpList = new ArrayList<>(sz);
    theSet.addAll(tmpList.subList(sz - maxSize, sz));
  }

  private void ensureSize(int num)
  {
    if(num >= maxSize)
    {
      clear();
      return;
    }

    while((num + size()) >= maxSize)
      theSet.remove(0);
  }
}
