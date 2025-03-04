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
package org.commonlib5.gui.table;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Renderizzatore di colonna tipo Date per JTable.
 * Consente di visualizzare correttamente un campo Date in una JTable.
 * <code>
 *  DateDerivedCellRender crDate =
 *     new DateDerivedCellRender("dd/MM/yyyy", mytable.getDefaultRenderer(String.class));
 *  mytable.getColumnModel().getColumn(2).setCellRenderer(crDate);
 * </code>
 *
 * @author Nicola De Nisco
 */
public class DateDerivedCellRender extends DefaultTableCellRenderer
{
  protected DateFormat f = null;
  protected TableCellRenderer r = null;

  public DateDerivedCellRender(String format, TableCellRenderer render)
  {
    f = new SimpleDateFormat(format);
    r = render;
  }

  public DateDerivedCellRender(DateFormat df, TableCellRenderer render)
  {
    f = df;
    r = render;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table,
     Object value, boolean isSelected, boolean hasFocus,
     int row, int column)
  {
    if(value == null)
      value = "";
    else if(value instanceof Date)
      value = f.format(value);

    return r.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}

