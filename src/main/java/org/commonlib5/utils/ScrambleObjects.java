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
import java.util.List;

/**
 * Mescola in modo random gli oggetti di una collezione.
 *
 * @author Nicola De Nisco
 */
public class ScrambleObjects<T> extends ArrayList<T>
{
  public int[] scrambleArray;

  public ScrambleObjects(Collection<? extends T> c)
  {
    super(c);
    scrambleArray = new int[size()];
    for(int i = 0; i < size(); i++)
      scrambleArray[i] = i;
  }

  /**
   * Mescola gli indici in modo random.
   */
  public void scramble()
  {
    scramble(10 * size());
  }

  /**
   * Mescola gli indici in modo random.
   *
   * @param numScramble
   */
  public void scramble(int numScramble)
  {
    for(int i = 0; i < numScramble; i++)
    {
      int i1 = (int) (Math.random() * size());
      int i2 = (int) (Math.random() * size());
      if(i1 == i2)
        continue;

      swap(i1, i2);
    }
  }

  private void swap(int i1, int i2)
  {
    int v = scrambleArray[i1];
    scrambleArray[i1] = scrambleArray[i2];
    scrambleArray[i2] = v;
  }

  /**
   * Ritorna oggetti mescolati.
   * @return lista mescolata
   */
  public List<T> getScrambled()
  {
    return getScrambled(size());
  }

  /**
   * Ritorna oggetti mescolati.
   * @param num numero oggetti recuperati
   * @return lista mescolata
   */
  public List<T> getScrambled(int num)
  {
    ArrayList<T> rv = new ArrayList<>(size());
    for(int i = 0; i < num; i++)
    {
      T val = get(scrambleArray[i]);
      rv.add(val);
    }
    return rv;
  }

  /**
   * Copia gli oggetti mescolati.
   * @param num numero oggetti da recuperare
   * @param output collezione da popolare con gli oggetti
   */
  public void copyScrambled(int num, Collection<T> output)
  {
    for(int i = 0; i < num; i++)
    {
      T val = get(scrambleArray[i]);
      output.add(val);
    }
  }
}
