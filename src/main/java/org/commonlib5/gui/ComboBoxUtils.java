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

import javax.swing.JComboBox;
import org.commonlib5.utils.PropertyManager;
import org.commonlib5.utils.StringOper;

/**
 * Funzioni di utilità per la manipolazione di listbox e combobox.
 *
 * @author Nicola De Nisco
 */
public class ComboBoxUtils
{
  public static void restoreComboString(PropertyManager pm, JComboBox cb, String radice)
  {
    cb.removeAllItems();

    int num = pm.getInt(radice + "_num");
    for(int i = 0; i < num; i++)
      cb.addItem(pm.getString(radice + "_cb_" + i));

    int sel = pm.getInt(radice + "_sel", -1);
    if(num > 0 && sel >= 0)
      cb.setSelectedIndex(sel);
  }

  public static void saveComboString(PropertyManager pm, JComboBox cb, String radice)
  {
    if(cb.getSelectedIndex() == -1)
      cb.addItem(cb.getSelectedItem());

    int num = cb.getItemCount();
    pm.addInt(radice + "_num", num);
    for(int i = 0; i < num; i++)
      pm.addString(radice + "_cb_" + i, StringOper.okStr(cb.getItemAt(i)));

    pm.addInt(radice + "_sel", cb.getSelectedIndex());
  }
}
