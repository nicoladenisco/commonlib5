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
package org.commonlib5.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * ComboBox con popup a dimensione differente.
 * La proprietà widestLengh stabilisce la dimensione del popup del combo box.
 * Se widestLengh == 0 (default) viene calcolata in automatico alla massima
 * stringa chiamando setWide(true).
 * La funzonalità wide è comunque attiva per default; basta impostare widestLengh
 * con il valore desiderato.
 *
 * @author Nicola De Nisco
 */
public class WideComboBox extends JComboBox
{
  private boolean layingOut = false;
  private int widestLengh = 0;
  private boolean wide = true;

  public WideComboBox(ComboBoxModel aModel)
  {
    super(aModel);
  }

  public WideComboBox(Object[] items)
  {
    super(items);
  }

  public WideComboBox(Vector items)
  {
    super(items);
  }

  public WideComboBox()
  {
  }

  /**
   * Stato della funzionalità wide.
   * @return vero se attiva
   */
  public boolean isWide()
  {
    return wide;
  }

  /**
   * Imposta la funzionalità wide.
   * Se il combo è già popolato e widestLengh è 0, chiamando setWide(true)
   * widestLengh viene calcolata in automatico sulla stringa più lunga
   * utilizzando il font attivo nell'interfaccia UI.
   * @param wide
   */
  public void setWide(boolean wide)
  {
    this.wide = wide;
    if(wide && widestLengh == 0)
      widestLengh = getWidestItemWidth();
  }

  /**
   * Ritoran dimensione del combobox aperto.
   * @return in pixel
   */
  public int getWidestLengh()
  {
    return widestLengh;
  }

  /**
   * Imposta dimensione del combobox aperto.
   * @param widestLengh in pixel (0=auto)
   */
  public void setWidestLengh(int widestLengh)
  {
    this.widestLengh = widestLengh;
  }

  @Override
  public Dimension getSize()
  {
    Dimension dim = super.getSize();
    if(!layingOut && isWide())
      dim.width = Math.max(widestLengh, dim.width);
    return dim;
  }

  /**
   * Calcolo della massima ampiezza degli item del combobox.
   * Utilizza il font impostato per calcolare la massima dimensione
   * @return ampiezza in pixel
   */
  public int getWidestItemWidth()
  {
    int numOfItems = getItemCount();
    Font font = getFont();
    FontMetrics metrics = getFontMetrics(font);
    int widest = 0;
    for(int i = 0; i < numOfItems; i++)
    {
      Object item = getItemAt(i);
      int lineWidth = metrics.stringWidth(item.toString());
      widest = Math.max(widest, lineWidth);
    }

    return widest + 5;
  }

  @Override
  public void doLayout()
  {
    try
    {
      layingOut = true;
      super.doLayout();
    }
    finally
    {
      layingOut = false;
    }
  }
}
