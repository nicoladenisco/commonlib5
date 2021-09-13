/*
 *  DateCellRender.java
 *  Creato il 2-ago-2012, 20.10.51
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

/**
 * Renderizzatore programmabile di valore data.
 *
 * @author Nicola De Nisco
 */
public class DateCellRender extends DefaultTableCellRenderer
{
  protected DateFormat f = null;

  public DateCellRender(String format)
  {
    f = new SimpleDateFormat(format);
  }

  public DateCellRender(DateFormat df)
  {
    f = df;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table,
     Object value, boolean isSelected, boolean hasFocus,
     int row, int column)
  {
    if(value instanceof Date)
      value = f.format(value);

    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}
