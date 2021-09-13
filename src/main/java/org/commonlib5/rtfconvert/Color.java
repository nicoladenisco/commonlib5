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
package org.commonlib5.rtfconvert;

/**
 * Descrittore di un colore.
 * 
 * @author Nicola De Nisco
 */
public class Color
{
  public int r, g, b;

  public Color()
  {
    r = g = b = -1;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj.getClass().equals(Color.class))
    {
      Color clr = (Color) obj;
      return r == clr.r && g == clr.g && b == clr.b;
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 47 * hash + this.r;
    hash = 47 * hash + this.g;
    hash = 47 * hash + this.b;
    return hash;
  }

  @Override
  public String toString()
  {
    return "Color " + r + " " + g + " " + b;
  }

  public Color copy(Color clr)
  {
    r = clr.r;
    g = clr.g;
    b = clr.b;
    return this;
  }
}
