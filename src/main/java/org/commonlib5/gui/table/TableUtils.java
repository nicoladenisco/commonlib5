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

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.commonlib5.utils.PropertyManager;
import org.commonlib5.utils.StringOper;

/**
 * Funzioni di utilità nella manipolazione delle tabelle.
 *
 * @author Nicola De Nisco
 */
public class TableUtils
{
  /**
   * Salva il layout di una tabella, compreso l'ordine delle colonne
   * in un {@link PropertyManager} sotto forma di entry stringa.
   * @param pm gestore di proprietà
   * @param radix stringa radice (unica per ogni tabella)
   * @param table tabella da salvare
   */
  public static void saveTableLayout(PropertyManager pm, String radix, JTable table)
  {
    TableColumnModel tm = table.getColumnModel();

    // salva larghezza colonne
    int numCol = tm.getColumnCount();
    int[] indice = new int[numCol];
    pm.addInt(radix + ".numCol", numCol);
    for(int i = 0; i < numCol; i++)
    {
      TableColumn col = tm.getColumn(i);
      int width = col.getWidth();
      int ordid = col.getModelIndex();

      //System.out.printf("saveTableLayout[%s]: %5d|%5d|%5d %s\n", radix, i, ordid, width, col.getHeaderValue());
      String colName = StringOper.okStr(col.getHeaderValue()).replace(' ', '_').toLowerCase();
      if(colName.isEmpty())
        continue;

      pm.addInt(radix + ".col." + colName, width);
      pm.addInt(radix + ".view." + colName, i);
      pm.addInt(radix + ".ord." + colName, ordid);

      indice[i] = ordid;
    }

    pm.addArrayInt(radix + ".viewindex", indice, false, false);
  }

  /**
   * Imposta un ordine delle colonne in base all'indice caricato.
   * @param indices indice delle colonne con l'ordine da impostare
   * @param table tabella da modificare
   * @param columnModel modello di colonna
   */
  public static void setColumnOrder(int[] indices, JTable table, TableColumnModel columnModel)
  {
    for(int i = 0; i < indices.length; i++)
    {
      int j = table.convertColumnIndexToView(indices[i]);
      //int j = table.convertColumnIndexToModel(indices[i]);
      //int j = indices[i];
      if(i != j)
        columnModel.moveColumn(i, j);
    }
  }

  /**
   * Recupera il layout della tabella, compreso l'ordine delle colonne
   * da un {@link PropertyManager} in cui è stato precedentemente salvato
   * con saveTableLayout().
   * @param pm gestore di proprietà
   * @param radix stringa radice (unica per ogni tabella)
   * @param table tabella da caricare
   */
  public static void loadTableLayout(PropertyManager pm, String radix, JTable table)
  {
    TableColumnModel tm = table.getColumnModel();

    int numCol = tm.getColumnCount();
    if(pm.getInt(radix + ".numCol", 0) != numCol)
      return;

    int[] indice = new int[numCol];

    for(int i = 0; i < numCol; i++)
    {
      TableColumn col = tm.getColumn(i);

      String colName = StringOper.okStr(col.getHeaderValue()).replace(' ', '_').toLowerCase();
      if(!colName.isEmpty())
      {
        int width = pm.getInt(radix + ".col." + colName, col.getWidth());
        int ordid = pm.getInt(radix + ".ord." + colName, col.getModelIndex());

        //System.out.printf("loadTableLayout[%s]: %5d|%5d|%5d %s\n", radix, i, ordid, width, col.getHeaderValue());
        col.setWidth(width);
        col.setPreferredWidth(width);
        col.setModelIndex(ordid);
        indice[i] = ordid;
      }
    }

    indice = pm.getArrayInt(radix + ".viewindex", indice);
    setColumnOrder(indice, table, tm);
  }
}
