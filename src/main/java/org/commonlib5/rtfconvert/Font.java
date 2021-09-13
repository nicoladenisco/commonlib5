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
 * Descrittore di un font.
 * 
 * @author Nicola De Nisco
 */
public class Font
{
  public enum font_family
  {
    ff_none, ff_serif, ff_sans_serif, ff_cursive,
    ff_fantasy, ff_monospace
  };
  public font_family family = font_family.ff_none;
  public String name = "";
  public int pitch = 0;
  public int charset = 0;

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof Font)
    {
      Font f = (Font) obj;
      return family == f.family && (name == null ? f.name == null : name.equals(f.name));
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 83 * hash + (this.family != null ? this.family.hashCode() : 0);
    hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 83 * hash + this.pitch;
    hash = 83 * hash + this.charset;
    return hash;
  }

  @Override
  public String toString()
  {
    return "Font " + name;
  }

  public Font copy(Font f)
  {
    family = f.family;
    name = f.name;
    pitch = f.pitch;
    charset = f.charset;
    return this;
  }
}
