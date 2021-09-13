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

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 * Special render for CheckBoxList.
 *
 * @author Nicola De Nisco
 */
public class CheckboxCellRenderer extends DefaultListCellRenderer
{
//  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index,
     boolean isSelected, boolean cellHasFocus)
  {
    if(value instanceof CheckBoxListEntry)
    {
      CheckBoxListEntry checkbox = (CheckBoxListEntry) value;
      checkbox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

      if(checkbox.isRed())
      {
        checkbox.setForeground(Color.red);
      }
      else
      {
        checkbox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
      }

      checkbox.setEnabled(isEnabled());
      checkbox.setFont(getFont());
      checkbox.setFocusPainted(false);
      checkbox.setOpaque(true);
//      checkbox.setBorderPainted(true);
//      checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

      return checkbox;
    }
    else
    {
      return super.getListCellRendererComponent(list, value.getClass().getName(), index,
         isSelected, cellHasFocus);
    }
  }
}
