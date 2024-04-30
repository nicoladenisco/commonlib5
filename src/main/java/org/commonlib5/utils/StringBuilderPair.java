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

/**
 * Tipo di string build ma puo lavorare a coppie.
 *
 * @author Nicola De Nisco
 */
public class StringBuilderPair
{
  private final int size;
  private StringBuilder sb;

  public StringBuilderPair()
  {
    size = 128;
    sb = new StringBuilder(size);
  }

  public StringBuilderPair(int size)
  {
    this.size = size;
    sb = new StringBuilder(size);
  }

  public void clear()
  {
    sb = new StringBuilder(size);
  }

  public StringBuilder getSb()
  {
    return sb;
  }

  public void setSb(StringBuilder sb)
  {
    this.sb = sb;
  }

  public int compareTo(StringBuilder another)
  {
    return sb.compareTo(another);
  }

  public StringBuilderPair append(Object obj)
  {
    return append(String.valueOf(obj));
  }

  public StringBuilderPair append(String str)
  {
    sb.append(str);
    return this;
  }

  public StringBuilderPair appendPair(String str, Object obj)
  {
    sb.append(str).append(obj);
    return this;
  }

  public StringBuilderPair appendPairNotNull(String str, Object obj)
  {
    if(obj != null)
      sb.append(str).append(obj);
    return this;
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }
}
