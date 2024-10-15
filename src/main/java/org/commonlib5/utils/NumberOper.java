/*
 * Copyright (C) 2024 Nicola De Nisco
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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Una serie di funzioni statiche per la manipolazione di numeri.
 *
 * @author Nicola De Nisco
 */
public class NumberOper
{
  private static final Map<Class<?>, Object> zeroes = new HashMap<>();

  static
  {
    zeroes.put(Integer.class, 0);
    zeroes.put(Short.class, 0);
    zeroes.put(Byte.class, 0);
    zeroes.put(Float.class, 0.0f);
    zeroes.put(Double.class, 0.0);
    zeroes.put(Boolean.class, false);
    zeroes.put(BigInteger.class, BigInteger.ZERO);
  }

  public static < T extends Number> T zeroNumberIfNull(T number, Class<T> clazz)
     throws IllegalArgumentException
  {
    if(number == null)
    {
      if(zeroes.containsKey(clazz))
      {
        return (clazz.cast(zeroes.get(clazz)));
      }
      throw new IllegalArgumentException("Unexpected Number Class " + clazz.getName() + " with undefined zero value.");
    }
    return number;
  }

  public static < T extends Number> T zeroNumberAlways(Class<T> clazz)
     throws IllegalArgumentException
  {
    if(zeroes.containsKey(clazz))
    {
      return (clazz.cast(zeroes.get(clazz)));
    }
    throw new IllegalArgumentException("Unexpected Number Class " + clazz.getName() + " with undefined zero value.");
  }

  public static <T> T zeroAlways(Class<T> clazz)
     throws IllegalArgumentException
  {
    if(clazz.isAssignableFrom(Integer.class))
      return clazz.cast(0);

    return (clazz.cast(zeroes.getOrDefault(clazz, null)));
  }
}
