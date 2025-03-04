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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Riga di una tabella.
 * 
 * @author Nicola De Nisco
 */
public class TableRow
{
  public ArrayList<TableCell> Cells = new ArrayList<TableCell>();
  public ArrayList<TableCellDef> CellDefs = new ArrayList<TableCellDef>();
  public int Height = -1000;
  public int Left = -1000;

  public TableCellDef findMergedRight(int rightValue)
  {
    Iterator<TableCellDef> itr = CellDefs.iterator();
    while(itr.hasNext())
    {
      TableCellDef tcdsr = itr.next();
      if(tcdsr.Right == rightValue)
        return tcdsr;
    }
    return null;
  }

  public TableCellDef findMergedLeft(int leftValue)
  {
    Iterator<TableCellDef> itr = CellDefs.iterator();
    while(itr.hasNext())
    {
      TableCellDef tcdsr = itr.next();
      if(tcdsr.Left == leftValue)
        return tcdsr;
    }
    return null;
  }
}
