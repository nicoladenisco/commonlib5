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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import javax.swing.JTable;
import javax.swing.table.*;

/**
 * Estensione di table model generico.
 *
 * @author Nicola De Nisco
 * @param <T>
 */
abstract public class GenericTableModel<T> extends AbstractTableModel
{
  public static class ColumnDescriptor
  {
    protected String name;
    protected Class cls;
    protected Comparator cmp;
    protected Function valfunc;

    public ColumnDescriptor(String name, Class cls, Comparator cmp)
    {
      this.name = name;
      this.cls = cls;
      this.cmp = cmp;
    }

    public ColumnDescriptor(String name, Class cls)
    {
      this.name = name;
      this.cls = cls;
    }

    public ColumnDescriptor(String name, Class cls, Function valfunc)
    {
      this.name = name;
      this.cls = cls;
      this.valfunc = valfunc;
    }

    public ColumnDescriptor(String name, Class cls, Comparator cmp, Function valfunc)
    {
      this.name = name;
      this.cls = cls;
      this.valfunc = valfunc;
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
  }
  protected ArrayList<T> arData = new ArrayList<>();
  protected ArrayList<ColumnDescriptor> arColumns = new ArrayList<>();

  public void addColumn(ColumnDescriptor cd)
  {
    arColumns.add(cd);
  }

  public <R> void addColumn(String name, Class<R> cls, Comparator<R> cmp)
  {
    arColumns.add(new ColumnDescriptor(name, cls, cmp));
  }

  public <R> void addColumn(String name, Class<R> cls)
  {
    arColumns.add(new ColumnDescriptor(name, cls));
  }

  public <R> void addColumn(String name, Class<R> cls, Function<T, R> valfunc)
  {
    arColumns.add(new ColumnDescriptor(name, cls, valfunc));
  }

  public <R> void addColumn(String name, Class<R> cls, Comparator<R> cmp, Function<T, R> valfunc)
  {
    arColumns.add(new ColumnDescriptor(name, cls, cmp, valfunc));
  }

  public void setData(Collection<T> data)
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

  public void addData(Collection<T> d)
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
    T b = getData(rowIndex);
    if(b == null)
      return null;

    ColumnDescriptor cd = arColumns.get(columnIndex);

    if(cd.valfunc != null)
      return cd.valfunc.apply(b);

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
  public Class getColumnClass(int col)
  {
    return arColumns.get(col).cls;
  }

  @Override
  public String getColumnName(int column)
  {
    return arColumns.get(column).name;
  }

  @Override
  public boolean isCellEditable(int row, int col)
  {
    return false;
  }

  /**
   * Muove la riga selezionata verso l'alto.
   * Non utilizzabile con selezione multipla.
   * @param tbl
   */
  public void moveUp(JTable tbl)
  {
    int sel = tbl.getSelectedRow();
    if(sel != -1)
    {
      moveUp(sel, tbl);
      tbl.setRowSelectionInterval(sel - 1, sel - 1);
    }
  }

  /**
   * Muove la riga verso l'alto.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param row riga da spostare
   * @param tbl
   */
  public void moveUp(int row, JTable tbl)
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
   * @param tbl
   */
  public void moveDown(JTable tbl)
  {
    int sel = tbl.getSelectedRow();
    if(sel != -1)
    {
      moveDown(sel, tbl);
      tbl.setRowSelectionInterval(sel + 1, sel + 1);
    }
  }

  /**
   * Muove la riga verso il basso.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param row riga da spostare
   * @param tbl
   */
  public void moveDown(int row, JTable tbl)
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
   * @param tbl tabella collegata
   * @return null se nessuna selezione è attiva
   */
  public List<T> getSelected(JTable tbl)
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
   * @param tbl tabella collegata
   * @return null se nessuna selezione è attiva
   */
  public T getSelectedOne(JTable tbl)
  {
    int sel = tbl.getSelectedRow();
    if(sel == -1)
      return null;

    return arData.get(tbl.convertRowIndexToModel(sel));
  }

  /**
   * Segnala gli oggetti selezionati come aggiornati.
   * @param tbl tabella collegata
   */
  public void fireTableRowsUpdated(JTable tbl)
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
   * Cancella una riga.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param row indice riga di tabella
   * @param tbl tabella di riferimento
   * @return
   */
  public T deleteRow(int row, JTable tbl)
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
   * @param tbl
   * @return
   */
  public synchronized List<T> deleteRows(int firstRow, int lastRow, JTable tbl)
  {
    List<T> arSelected = getRows(firstRow, lastRow, tbl);
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
   * @param tbl
   * @return
   */
  public synchronized List<T> deleteRows(int[] rows, JTable tbl)
  {
    List<T> arSelected = getRows(rows, tbl);
    if(removeData(arSelected))
      fireTableDataChanged();
    return arSelected;
  }

  /**
   * Cancella tutte le righe selezionate nella tabella.
   * @param tbl tabella da cui estrarre la selezione
   * @return lista delle righe cancellate
   */
  public synchronized List<T> deleteRows(JTable tbl)
  {
    int sel = tbl.getSelectedRow();

    if(sel != -1)
    {
      List<T> arSelected = getSelected(tbl);
      if(removeData(arSelected))
        fireTableDataChanged();

      if(getRowCount() > sel)
        tbl.setRowSelectionInterval(sel, sel);

      return arSelected;
    }

    return Collections.EMPTY_LIST;
  }

  /**
   * Cancella tutte le righe selezionate nella tabella.
   * E' un doppione di deleteRows(). Per compatibilità
   * con implementazioni precedenti.
   * @param tbl tabella da cui estrarre la selezione
   * @return lista delle righe cancellate
   * @deprecated usa deleteRows(JTable tbl)
   */
  public synchronized List<T> deleteSelectedRows(JTable tbl)
  {
    return deleteRows(tbl);
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
   * Recupera un blocco di righe.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param firstRow
   * @param lastRow
   * @param tbl
   * @return
   */
  public synchronized List<T> getRows(int firstRow, int lastRow, JTable tbl)
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
   * @param tbl
   * @return
   */
  public synchronized List<T> getRows(int[] rows, JTable tbl)
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
    return arColumns.size();
  }
}
