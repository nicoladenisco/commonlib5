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
package org.commonlib5.gui.table;

import java.util.*;
import java.util.function.Function;
import javax.swing.JTable;
import javax.swing.table.*;
import org.commonlib5.utils.PropertyManager;
import org.commonlib5.utils.StringOper;

/**
 * Estensione di table model generico.
 *
 * @author Nicola De Nisco
 * @param <T>
 */
abstract public class GenericTableModelVisibility<T> extends AbstractTableModel
{
  public static class ColumnDescriptor extends TableColumn
  {
    protected String name;
    protected Class cls;
    protected Comparator cmp;
    protected boolean visible = true;
    protected Function valfunc;

    public ColumnDescriptor(String name, Class cls, Comparator cmp)
    {
      setHeaderValue(name);
      this.name = name;
      this.cls = cls;
      this.cmp = cmp;
    }

    public ColumnDescriptor(String name, Class cls)
    {
      setHeaderValue(name);
      this.name = name;
      this.cls = cls;
    }

    public ColumnDescriptor(String caption, String name, Class cls, Comparator cmp)
    {
      setHeaderValue(caption);
      this.name = name;
      this.cls = cls;
      this.cmp = cmp;
    }

    public ColumnDescriptor(String caption, String name, Class cls)
    {
      setHeaderValue(caption);
      this.name = name;
      this.cls = cls;
    }

    public ColumnDescriptor(String caption, String name, Class cls, Function valfunc)
    {
      setHeaderValue(caption);
      this.name = name;
      this.cls = cls;
      this.valfunc = valfunc;
    }

    public ColumnDescriptor(String caption, String name, Class cls, Function valfunc, boolean visible)
    {
      setHeaderValue(caption);
      this.name = name;
      this.cls = cls;
      this.valfunc = valfunc;
      this.visible = visible;
    }

    public ColumnDescriptor(String caption, String name, Class cls, Comparator cmp, Function valfunc)
    {
      setHeaderValue(caption);
      this.name = name;
      this.cls = cls;
      this.valfunc = valfunc;
      this.cmp = cmp;
    }

    public ColumnDescriptor(String caption, String name, Class cls, Comparator cmp, Function valfunc, boolean visible)
    {
      setHeaderValue(caption);
      this.name = name;
      this.cls = cls;
      this.valfunc = valfunc;
      this.cmp = cmp;
      this.visible = visible;
    }

    public Class getCls()
    {
      return cls;
    }

    public void setCls(Class cls)
    {
      this.cls = cls;
    }

    public Comparator getCmp()
    {
      return cmp;
    }

    public void setCmp(Comparator cmp)
    {
      this.cmp = cmp;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public boolean isVisible()
    {
      return visible;
    }

    public void setVisible(boolean visible)
    {
      this.visible = visible;
    }

    @Override
    public String toString()
    {
      return "ColumnDescriptor{" + "name=" + name + ", visible=" + visible + '}';
    }
  }

  protected ArrayList<T> arData = new ArrayList<T>();
  protected ArrayList<ColumnDescriptor> arColumns = new ArrayList<ColumnDescriptor>();
  protected TableColumnModel dtcmdl = null;
  protected JTable tbl = null;

  public void addColumn(ColumnDescriptor cd)
  {
    arColumns.add(cd);
  }

  public <R> ColumnDescriptor addColumn(String name, Class<R> cls, Comparator<R> cmp)
  {
    arColumns.add(new ColumnDescriptor(name, cls, cmp));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String name, Class<R> cls)
  {
    arColumns.add(new ColumnDescriptor(name, cls));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String caption, String name, Class<R> cls, Comparator<R> cmp)
  {
    arColumns.add(new ColumnDescriptor(caption, name, cls, cmp));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String caption, String name, Class<R> cls)
  {
    arColumns.add(new ColumnDescriptor(caption, name, cls));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String caption, String name, Class<R> cls, Function<T, R> valfunc)
  {
    arColumns.add(new ColumnDescriptor(caption, name, cls, valfunc));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String caption, String name, Class<R> cls, Function<T, R> valfunc, boolean visible)
  {
    arColumns.add(new ColumnDescriptor(caption, name, cls, valfunc, visible));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String caption, String name, Class<R> cls, Comparator<R> cmp, Function<T, R> valfunc)
  {
    arColumns.add(new ColumnDescriptor(caption, name, cls, cmp, valfunc));
    return arColumns.get(arColumns.size() - 1);
  }

  public <R> ColumnDescriptor addColumn(String caption, String name, Class<R> cls, Comparator<R> cmp, Function<T, R> valfunc, boolean visible)
  {
    arColumns.add(new ColumnDescriptor(caption, name, cls, cmp, valfunc, visible));
    return arColumns.get(arColumns.size() - 1);
  }

  public void setRender(Class cls, TableCellRenderer render)
  {
    arColumns.forEach((cd) ->
    {
      if(cd.getCls().equals(cls))
        cd.setCellRenderer(render);
    });
  }

  public <R> void setComparator(Class<R> cls, Comparator<R> cmp)
  {
    arColumns.forEach((cd) ->
    {
      if(cd.getCls().equals(cls))
        cd.cmp = cmp;
    });
  }

  public void attach(JTable tbl)
  {
    dtcmdl = new DefaultTableColumnModel();

    for(int i = 0, j = 0; i < arColumns.size(); i++)
    {
      ColumnDescriptor cd = (ColumnDescriptor) arColumns.get(i);

      if(cd.isVisible())
      {
        cd.setModelIndex(j++);
        cd.setWidth(100);
        cd.setPreferredWidth(100);
        dtcmdl.addColumn(cd);
      }
    }

    this.tbl = tbl;
    tbl.setModel(this);
    tbl.setColumnModel(dtcmdl);

    TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tbl.getRowSorter();
    if(sorter != null)
    {
      for(int i = 0; i < dtcmdl.getColumnCount(); i++)
      {
        ColumnDescriptor cd = (ColumnDescriptor) dtcmdl.getColumn(i);
        if(sorter != null && cd.cmp != null)
          sorter.setComparator(i, cd.cmp);
      }
    }

    fireTableDataChanged();
  }

  public void setVisible(boolean visible, String... columnsName)
  {
    for(String colName : columnsName)
    {
      ColumnDescriptor cd = arColumns.stream()
         .filter((c) -> StringOper.isEqu(colName, c.getName()))
         .findFirst().orElse(null);

      if(cd != null)
        cd.visible = visible;
    }
  }

  public void setAllVisible(boolean visible)
  {
    arColumns.forEach((c) -> c.visible = visible);
  }

  /**
   * Imposta visibilità di default per le colonne.
   * Questa implentazione è vuota e deve essere ridefinita in classi derivate.
   */
  public void setDefaultVisible()
  {
  }

  protected void testData()
  {
    if(dtcmdl == null || tbl == null)
      throw new RuntimeException("Use attach() before using the table.");
  }

  public void setData(List<T> data)
  {
    arData.clear();
    arData.addAll(data);
    fireTableDataChanged();
  }

  public void setData(Iterator<T> itr)
  {
    arData.clear();
    while(itr.hasNext())
      arData.add(itr.next());
    fireTableDataChanged();
  }

  public void addData(T data)
  {
    arData.add(data);
    fireTableDataChanged();
  }

  public void addData(List<T> d)
  {
    arData.addAll(d);
    fireTableDataChanged();
  }

  public void addData(Iterator<T> itr)
  {
    while(itr.hasNext())
      arData.add(itr.next());
    fireTableDataChanged();
  }

  public void clear()
  {
    arData.clear();
    fireTableDataChanged();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    try
    {
      T b = getData(rowIndex);
      if(b == null || dtcmdl == null)
        return null;

      ColumnDescriptor cd = (ColumnDescriptor) dtcmdl.getColumn(tbl.convertColumnIndexToView(columnIndex));

      if(cd.valfunc != null)
        return cd.valfunc.apply(b);
//
//      if(cd.valfunc != null)
//      {
//        Object val = cd.valfunc.apply(b);
//        System.out.printf("row=%d, col=%d, val=%s\n", rowIndex, columnIndex, StringOper.okStr(val));
//        return val;
//      }
    }
    catch(Throwable t)
    {
      System.err.println("Error in getValueAt(): " + t.getMessage());
    }

    return "";
  }

  /**
   * Recupera una riga del modello.
   * @param row
   * @return
   */
  public T getData(int row)
  {
    return arData.get(row);
  }

  /**
   * Recupera un blocco di elementi.
   * @param beginIndex
   * @param numItems
   * @return lista di oggetti
   */
  public synchronized List<T> getData(int beginIndex, int numItems)
  {
    ArrayList<T> arSelected = new ArrayList<T>();
    while(numItems-- > 0)
      arSelected.add(getData(beginIndex++));
    return arSelected;
  }

  /**
   * Recupera tutte le righe del modello.
   * @return una lista immodificabile con i dati
   */
  public List<T> getAllData()
  {
    return Collections.unmodifiableList(arData);
  }

  /**
   * Cambia l'elemento alla riga indicata.
   * @param row
   * @param newData
   * @return
   */
  public T replaceData(int row, T newData)
  {
    T rv = arData.remove(row);
    arData.add(row, newData);
    fireTableRowsUpdated(row, row);
    return rv;
  }

  @Override
  public Class getColumnClass(int column)
  {
    testData();
    return ((ColumnDescriptor) dtcmdl.getColumn(column)).cls;
  }

  @Override
  public String getColumnName(int column)
  {
    testData();
    return ((ColumnDescriptor) dtcmdl.getColumn(column)).name;
  }

  @Override
  public boolean isCellEditable(int row, int col)
  {
    return false;
  }

  /**
   * Muove la riga selezionata verso l'alto.
   * Non utilizzabile con selezione multipla.
   */
  public void moveUp()
  {
    int sel = tbl.getSelectedRow();
    if(sel != -1)
    {
      moveUp(sel);
      tbl.setRowSelectionInterval(sel - 1, sel - 1);
    }
  }

  /**
   * Muove la riga verso l'alto.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param row riga da spostare
   */
  public void moveUp(int row)
  {
    row = tbl.convertRowIndexToModel(row);

    if(row > 0 && row < arData.size())
    {
      T obj = arData.remove(row);
      arData.add(row - 1, obj);
      fireTableDataChanged();
    }
  }

  /**
   * Muova la riga selezionata verso il basso.
   */
  public void moveDown()
  {
    int sel = tbl.getSelectedRow();
    if(sel != -1)
    {
      moveDown(sel);
      tbl.setRowSelectionInterval(sel + 1, sel + 1);
    }
  }

  /**
   * Muove la riga verso il basso.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param row riga da spostare
   */
  public void moveDown(int row)
  {
    row = tbl.convertRowIndexToModel(row);

    if(row >= 0 && row < arData.size() - 1)
    {
      T obj = arData.remove(row);
      arData.add(row + 1, obj);
      fireTableDataChanged();
    }
  }

  /**
   * Ritorna oggetti selezionati.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @return null se nessuna selezione è attiva
   */
  public List<T> getSelected()
  {
    int[] arSel = tbl.getSelectedRows();
    if(arSel == null || arSel.length == 0)
      return null;

    ArrayList<T> rv = new ArrayList<T>();
    for(int i = 0; i < arSel.length; i++)
    {
      int sel = arSel[i];
      if(sel != -1)
      {
        T ts = arData.get(tbl.convertRowIndexToModel(sel));
        rv.add(ts);
      }
    }

    return rv;
  }

  /**
   * Ritorna riga selezionata.
   * Molto utile in caso di selezione singola.
   * @return null se nessuna selezione è attiva
   */
  public T getSelectedOne()
  {
    int sel = tbl.getSelectedRow();
    if(sel == -1)
      return null;

    return arData.get(tbl.convertRowIndexToModel(sel));
  }

  /**
   * Segnala gli oggetti selezionati come aggiornati.
   */
  public void fireTableRowsUpdated()
  {
    int[] arSel = tbl.getSelectedRows();
    if(arSel == null || arSel.length == 0)
      return;

    fireTableRowsUpdated(arSel[0], arSel[arSel.length - 1]);
  }

  /**
   * Rimuove il dato indicato dal vettore.
   * Questa funzione è un segnaposto per le
   * classi derivate, al fine di implementare
   * qualche logica al momento della cancellazione
   * di un elemento.
   * @param idx
   * @return
   */
  public boolean removeData(int idx)
  {
    arData.remove(idx);
    return true;
  }

  /**
   * Rimuove il dato indicato dal vettore.
   * Questa funzione è un segnaposto per le
   * classi derivate, al fine di implementare
   * qualche logica al momento della cancellazione
   * di un elemento.
   * @param toDel oggetto da cancellare
   * @return vero se l'oggetto esiste e cancellato
   */
  public boolean removeData(T toDel)
  {
    return arData.remove(toDel);
  }

  /**
   * Rimuove il dato indicato dal vettore.
   * Questa funzione è un segnaposto per le
   * classi derivate, al fine di implementare
   * qualche logica al momento della cancellazione
   * di un elemento.
   * @param toDel oggetto da cancellare
   * @return vero se l'oggetto esiste e cancellato
   */
  public boolean removeData(List<T> toDel)
  {
    return arData.removeAll(toDel);
  }

  /**
   * Cancella tutte le righe selezionate nella tabella.
   */
  public void deleteSelectedRows()
  {
    int uSel = tbl.getSelectedRow();
    List<T> arSel = getSelected();
    if(arSel == null || arSel.isEmpty())
      return;

    if(removeData(arSel))
      fireTableDataChanged();

    // riseleziona la riga corrente
    if(uSel != -1 && uSel < arData.size())
      tbl.setRowSelectionInterval(uSel, uSel);
  }

  /**
   * Cancella una riga.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param row indice riga di tabella
   * @return
   */
  public T deleteRow(int row)
  {
    int mrow = tbl.convertRowIndexToModel(row);

    T rv = arData.get(mrow);
    removeData(mrow);
    fireTableRowsDeleted(row, row);
    return rv;
  }

  /**
   * Cancella un blocco di righe.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param firstRow
   * @param lastRow
   * @return
   */
  public synchronized List<T> deleteRows(int firstRow, int lastRow)
  {
    List<T> arSelected = getRows(firstRow, lastRow);
    if(removeData(arSelected))
      fireTableDataChanged();
    return arSelected;
  }

  /**
   * Cancella un blocco di righe.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param rows
   * @return
   */
  public synchronized List<T> deleteRows(int[] rows)
  {
    List<T> arSelected = getRows(rows);
    if(removeData(arSelected))
      fireTableDataChanged();
    return arSelected;
  }

  /**
   * Cancella tutte le righe selezionate nella tabella.
   * @return lista delle righe cancellate
   */
  public synchronized List<T> deleteRows()
  {
    int sel = tbl.getSelectedRow();

    if(sel != -1)
    {
      List<T> arSelected = getSelected();
      if(removeData(arSelected))
        fireTableDataChanged();

      if(getRowCount() > sel)
        tbl.setRowSelectionInterval(sel, sel);

      return arSelected;
    }

    return Collections.EMPTY_LIST;
  }

  /**
   * Cancella l'oggetto dalla tabella.
   * @param toDel elemento da cancellare
   */
  public synchronized void deleteRowByObject(T toDel)
  {
    if(removeData(toDel))
      fireTableDataChanged();
  }

  /**
   * Cancella l'elenco di oggetti dalla tabella
   * @param toDel elemento da cancellare
   */
  public synchronized void deleteRowByObject(List<T> toDel)
  {
    if(removeData(toDel))
      fireTableDataChanged();
  }

  /**
   * Cancella tutti gli elementi dal table model.
   */
  public synchronized void deleteAll()
  {
    arData.clear();
    fireTableDataChanged();
  }

  /**
   * Recupera una riga.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * @param row
   * @return
   */
  public synchronized T getRow(int row)
  {
    return getData(tbl.convertRowIndexToModel(row));
  }

  /**
   * Recupera un blocco di righe.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * @param firstRow
   * @param lastRow
   * @return
   */
  public synchronized List<T> getRows(int firstRow, int lastRow)
  {
    ArrayList<T> arSelected = new ArrayList<T>();
    for(int i = firstRow; i <= lastRow; i++)
    {
      int row = tbl.convertRowIndexToModel(i);
      if(row >= 0 && row < arData.size())
        arSelected.add(arData.get(row));
    }
    return arSelected;
  }

  /**
   * Recupera un blocco di righe.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param rows
   * @return
   */
  public synchronized List<T> getRows(int[] rows)
  {
    ArrayList<T> arSelected = new ArrayList<T>();
    for(int i = 0; i < rows.length; i++)
    {
      int row = tbl.convertRowIndexToModel(rows[i]);
      if(row >= 0 && row < arData.size())
        arSelected.add(arData.get(row));
    }
    return arSelected;
  }

  @Override
  public int getRowCount()
  {
    return arData.size();
  }

  @Override
  public int getColumnCount()
  {
    testData();
    return dtcmdl.getColumnCount();
  }

  public ColumnDescriptor getColumnDescriptor(int col)
  {
    return arColumns.get(col);
  }

  public ColumnDescriptor getColumnDescriptorByName(String name)
  {
    for(ColumnDescriptor c : arColumns)
    {
      if(StringOper.isEqu(name, c.getName()))
        return c;
    }
    return null;
  }

  public ColumnDescriptor getColumnDescriptorByCaption(String caption)
  {
    for(ColumnDescriptor c : arColumns)
    {
      if(StringOper.isEqu(caption, c.getHeaderValue()))
        return c;
    }
    return null;
  }

  public int getColumnDescriptorCount()
  {
    return arColumns.size();
  }

  public void saveTableLayout(PropertyManager pm, String radix, JTable table)
  {
    pm.addInt(radix + ".cdtnum", arColumns.size());
    for(int i = 0; i < arColumns.size(); i++)
    {
      ColumnDescriptor cd = arColumns.get(i);
      pm.addBoolean(radix + ".cdt" + i, cd.visible);
    }

    TableUtils.saveTableLayout(pm, radix, table);
  }

  public void loadTableLayoutAndAttach(PropertyManager pm, String radix, JTable table)
  {
    this.tbl = table;

    if(pm.getInt(radix + ".cdtnum") != arColumns.size())
    {
      attach(table);
      return;
    }

    for(int i = 0; i < arColumns.size(); i++)
    {
      ColumnDescriptor cd = arColumns.get(i);
      cd.visible = pm.getBoolean(radix + ".cdt" + i, cd.visible);
    }

    attach(tbl);
    TableUtils.loadTableLayout(pm, radix, table);
  }

//  public void loadTableLayoutAndAttach(PropertyManager pm, String radix, JTable table)
//  {
//    this.tbl = table;
//
//    if(pm.getInt(radix + ".cdtnum") != arColumns.size())
//    {
//      attach(table);
//      return;
//    }
//
//    ArrayList<ColumnDescriptor> colonneVisibili = new ArrayList<>();
//
//    for(int i = 0; i < arColumns.size(); i++)
//    {
//      ColumnDescriptor cd = arColumns.get(i);
//      cd.visible = pm.getBoolean(radix + ".cdt" + i, cd.visible);
//
//      if(cd.visible)
//        colonneVisibili.add(cd);
//    }
//
//    int numCol = colonneVisibili.size();
//    if(pm.getInt(radix + ".numCol", 0) != numCol)
//    {
//      attach(table);
//      return;
//    }
//
//    int[] indice = new int[numCol];
//
//    for(int i = 0; i < numCol; i++)
//    {
//      TableColumn col = colonneVisibili.get(i);
//
//      String colName = StringOper.okStr(col.getHeaderValue()).replace(' ', '_').toLowerCase();
//      if(!colName.isEmpty())
//      {
//        int width = pm.getInt(radix + ".col." + colName, col.getWidth());
//        int ordid = pm.getInt(radix + ".ord." + colName, col.getModelIndex());
//        int viewid = pm.getInt(radix + ".view." + colName, i);
//
//        //System.out.printf("loadTableLayout[%s]: %5d|%5d|%5d %s\n", radix, i, ordid, width, col.getHeaderValue());
//        col.setWidth(width);
//        col.setPreferredWidth(width);
//        col.setModelIndex(ordid);
//        indice[i] = viewid;
//      }
//    }
//
//    indice = pm.getArrayInt(radix + ".viewindex", indice);
//
//    // costruisce modello con i dati salvati
//    dtcmdl = new DefaultTableColumnModel();
//    for(int i = 0; i < numCol; i++)
//    {
//      ColumnDescriptor cd = colonneVisibili.get(indice[i]);
//      dtcmdl.addColumn(cd);
//    }
//
//    tbl.setModel(this);
//    tbl.setColumnModel(dtcmdl);
//
//    fireTableDataChanged();
//  }
//  public void attach(JTable tbl)
//  {
//    dtcmdl = new DefaultTableColumnModel();
//    for(int i = 0, j = 0; i < arColumns.size(); i++)
//    {
//      ColumnDescriptor cd = (ColumnDescriptor) arColumns.get(i);
//
//      if(cd.isVisible())
//      {
//        cd.setModelIndex(j++);
//        cd.setWidth(100);
//        cd.setPreferredWidth(100);
//        dtcmdl.addColumn(cd);
//      }
//    }
//
//    this.tbl = tbl;
//    tbl.setModel(this);
//    tbl.setColumnModel(dtcmdl);
//    fireTableDataChanged();
//  }
}
