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
package org.commonlib5.rtfconvert.lowagie;

import java.util.*;
import org.commonlib5.rtfconvert.RtfconvertException;
import org.commonlib5.rtfconvert.TableCell;
import org.commonlib5.rtfconvert.TableCellDef;
import org.commonlib5.rtfconvert.TableRow;
import org.commonlib5.utils.ArraySet;

/**
 * Definizione di una tabella.
 *
 * @author Nicola De Nisco
 */
public class LowagieTable extends ArrayList<TableRow>
{
  public String makeHtml()
     throws Exception
  {
    String result;
    ArraySet<Integer> pts = new ArraySet<Integer>();
    int row, span_row, row2;
    int left, right, colspan;
    boolean btop, bbottom, bleft, bright;
    String style = "";

    for(row = 0; row < size(); row++)
    {
      TableRow tr = get(row);

      if(tr.Cells.isEmpty())
      {
        remove(row);
        row--;
      }
      else
      {
        pts.add(tr.Left);
        for(Iterator<TableCellDef> cell_def = tr.CellDefs.iterator(); cell_def.hasNext();)
        {
          TableCellDef tcd = cell_def.next();
          pts.add(tcd.Right);
        }
      }
    }

    if(pts.isEmpty())
      throw new RtfconvertException("No CellDefs!");

    Integer[] ptsArr = pts.toArray(new Integer[pts.size()]);

    result = "<table border=0 width=";
    result += ((int) rint((ptsArr[ptsArr.length - 1] - ptsArr[0]) / 15));
    result += " style=\"margin-left:";
    result += rint(ptsArr[0] / 15);
    result += ";border-collapse: collapse;\">";
    result += "<tr height=0>";

    for(int i = 1; i < ptsArr.length; i++)
    {
      int j = i - 1;
      result += "<td width=";
      result += ((int) rint((ptsArr[i] - ptsArr[j]) / 15));
      result += "></td>";
      //coefficient may be different
    }

    result += "</tr>\n";

    // first, we'll determine all the rowspans and leftsides
    for(row = 0; row < size(); row++)
    {
      TableRow tr = get(row);

      if(tr.CellDefs.size() != tr.Cells.size())
        throw new RtfconvertException("Number of Cells and number of CellDefs are unequal!");

      TableCellDef tcdPrev = null;
      for(int i = 0; i < tr.Cells.size(); i++)
      {
        TableCell tc = tr.Cells.get(i);
        TableCellDef tcd = tr.CellDefs.get(i);

        if(tcdPrev == null)
          tcd.Left = tr.Left;
        else
          tcd.Left = tcdPrev.Right;

        if(tcd.FirstMerged)
        {
          for(span_row = row, ++span_row; span_row < size(); span_row++)
          {
            TableRow sr = get(span_row);
            TableCellDef tcdsr = sr.findMergedRight(tcd.Right);
            if(tcdsr == null)
              break;

            if(!tcdsr.Merged)
              break;
          }
          tc.Rowspan = span_row - row;
        }

        tcdPrev = tcd;
      }
    }

    for(row = 0; row < size(); row++)
    {
      TableRow tr = get(row);

      result += "<tr>";
      int idxl = pts.indexOf(tr.Left);
      if(idxl == -1)
        throw new RtfconvertException("No row.left point!");

      if(idxl != 0)
      {
        result += "<td colspan=";
        result += idxl;
        result += "></td>";
      }

      for(int i = 0; i < tr.Cells.size(); i++)
      {
        TableCell tc = tr.Cells.get(i);
        TableCellDef tcd = tr.CellDefs.get(i);

        int idxr = pts.indexOf(tcd.Right);
        if(idxr == -1)
          throw new RtfconvertException("No celldef.right point!");

        colspan = idxr - idxl;
        idxl = idxr;
        if(!tcd.Merged)
        {
          result += "<td";
          // analyzing borders
          left = tcd.Left;
          right = tcd.Right;
          bbottom = tcd.BorderBottom;
          btop = tcd.BorderTop;
          bleft = tcd.BorderLeft;
          bright = tcd.BorderRight;
          span_row = row;
          if(tcd.FirstMerged)
            span_row += tc.Rowspan - 1;

          for(row2 = row; row2 != span_row; ++row2)
          {
            TableRow tr2 = get(row2);

            TableCellDef td2 = tr2.findMergedRight(left);
            if(td2 != null)
            {
              bleft = bleft && td2.BorderRight;
            }

            td2 = tr2.findMergedLeft(right);
            if(td2 != null)
            {
              bleft = bleft && td2.BorderRight;
            }

            if(td2 != null)
            {
              bright = bright && td2.BorderLeft;
            }
          }

          if(bbottom && btop && bleft && bright)
          {
            style = "border:1px solid black;";
          }
          else
          {
            style = "";
            if(bbottom)
              style += "border-bottom:1px solid black;";
            if(btop)
              style += "border-top:1px solid black;";
            if(bleft)
              style += "border-left:1px solid black;";
            if(bright)
              style += "border-right:1px solid black;";
          }
          if(!style.isEmpty())
          {
            result += " style=\"";
            result += style;
            result += "\"";
          }
          if(colspan > 1)
          {
            result += " colspan=";
            result += (int) (colspan);
          }
          if(tcd.FirstMerged)
          {
            result += " rowspan=";
            result += (int) (tc.Rowspan);
          }

          switch(tcd.VAlign)
          {
            case valign_top:
              result += " valign=top";
              break;
            case valign_bottom:
              result += " valign=bottom";
              break;
          }

          result += ">";
          if(!tc.Text.isEmpty())
            result += tc.Text;
          else
            result += "&nbsp;";
          result += "</td>";
        }
      }
      result += "</tr>";
    }
    result += "</table>";
    return result;
  }

  public static int rint(double f)
  {
    return (int) Math.round(f);
  }
}
