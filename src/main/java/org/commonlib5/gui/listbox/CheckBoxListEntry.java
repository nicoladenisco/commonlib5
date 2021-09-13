/*
 * Copyright (C) 2014 Nicola De Nisco
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
package org.commonlib5.gui.listbox;

import javax.swing.JCheckBox;

/**
 * An entry in CheckBoxList inerithed from checkbox.
 *
 * @author Nicola De Nisco
 */
public class CheckBoxListEntry extends JCheckBox
{
  private Object value = null;
  private boolean red = false;

  public CheckBoxListEntry(Object itemValue, boolean selected)
  {
    this(itemValue, itemValue.toString(), selected);
  }

  public CheckBoxListEntry(Object itemValue, String description, boolean selected)
  {
    super(description, selected);
    setValue(itemValue);
  }

  public Object getValue()
  {
    return value;
  }

  public void setValue(Object value)
  {
    this.value = value;
  }

  public boolean isRed()
  {
    return red;
  }

  public void setRed(boolean red)
  {
    this.red = red;
  }
}
