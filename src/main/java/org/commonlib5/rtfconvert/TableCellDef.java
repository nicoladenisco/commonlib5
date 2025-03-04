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
package org.commonlib5.rtfconvert;

/**
 * Definizioni per una cella.
 * 
 * @author Nicola De Nisco
 */
public class TableCellDef
{
  public enum valign
  {
    valign_top, valign_bottom, valign_center
  };
  public boolean BorderTop, BorderBottom, BorderLeft, BorderRight;
  public Boolean ActiveBorder;
  public int Right, Left;
  public boolean Merged, FirstMerged;
  public valign VAlign;

  public TableCellDef()
  {
    BorderTop = BorderBottom = BorderLeft = BorderRight = Merged = FirstMerged = false;
    ActiveBorder = false;
    Right = Left = 0;
    VAlign = valign.valign_top;
  }

  boolean right_equals(int x)
  {
    return x == Right;
  }

  boolean left_equals(int x)
  {
    return x == Left;
  }
}
