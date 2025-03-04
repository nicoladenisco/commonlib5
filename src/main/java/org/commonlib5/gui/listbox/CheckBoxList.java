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
package org.commonlib5.gui.listbox;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * JList with checkbox.
 *
 * @author Nicola De Nisco
 */
public class CheckBoxList extends JList
{
  private DefaultListModel<CheckBoxListEntry> model = new DefaultListModel<CheckBoxListEntry>();

  public CheckBoxList()
  {
    super();

    setModel(model);
    setCellRenderer(new CheckboxCellRenderer());

    addMouseListener(new MouseAdapter()
    {
      @Override
      public void mousePressed(MouseEvent e)
      {
        if(e.getPoint().x > 20)
          return;

        int index = locationToIndex(e.getPoint());

        if(index != -1)
        {
          Object obj = getModel().getElementAt(index);
          if(obj instanceof JCheckBox)
          {
            JCheckBox checkbox = (JCheckBox) obj;

            checkbox.setSelected(!checkbox.isSelected());
            repaint();
          }
        }
      }
    });

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  @SuppressWarnings("unchecked")
  public int[] getCheckedIdexes()
  {
    int j = 0;
    int[] selected = new int[model.getSize()];

    for(int i = 0; i < model.getSize(); ++i)
    {
      CheckBoxListEntry ce = model.getElementAt(i);
      if(ce.isSelected())
        selected[j++] = i;
    }

    return Arrays.copyOf(selected, j);
  }

  @SuppressWarnings("unchecked")
  public List getCheckedItems()
  {
    List list = new ArrayList();

    for(int i = 0; i < model.getSize(); ++i)
    {
      CheckBoxListEntry ce = model.getElementAt(i);
      if(ce.isSelected())
        list.add(ce.getValue());
    }

    return list;
  }
}
