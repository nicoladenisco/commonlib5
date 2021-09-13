/*
 *  DateDerivedCellRender.java
 *  Creato il 2-ago-2012, 20.29.54
 *
 *  Copyright (C) 2012 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni, 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
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

