/*
 * Copyright (C) 2016 Nicola De Nisco
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;

/**
 * Generic ComboBox model.
 *
 * @author Nicola De Nisco
 * @param <T>
 */
public class GenericComboBoxModel<T> extends DefaultComboBoxModel<T>
{
  protected ArrayList<T> arData = new ArrayList<T>();

  @Override
  public int getSize()
  {
    return arData.size();
  }

  @Override
  public T getElementAt(int index)
  {
    return arData.get(index);
  }

  public void fireContentsChanged()
  {
    super.fireContentsChanged(this, 0, getSize());
  }

  public void setData(List<T> data)
  {
    arData.clear();
    arData.addAll(data);
    fireContentsChanged();
  }

  public void setData(Iterator<T> itr)
  {
    arData.clear();
    while(itr.hasNext())
      arData.add(itr.next());
    fireContentsChanged();
  }

  public void addData(T data)
  {
    arData.add(data);
    fireContentsChanged();
  }

  public void addData(List<T> d)
  {
    arData.addAll(d);
    fireContentsChanged();
  }

  public void addData(Iterator<T> itr)
  {
    while(itr.hasNext())
      arData.add(itr.next());
    fireContentsChanged();
  }

  public void clear()
  {
    arData.clear();
    fireContentsChanged();
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
    fireContentsChanged(this, row, row);
    return rv;
  }

  /**
   * Ritorna oggetti selezionati.
   * L'indice di riga è quello di tabella che viene convertito
   * opportunamente in quello del modello in base al sorting attivo.
   * L'oggetto tbl serve appunto per operare la conversione.
   * @param tbl tabella collegata
   * @return null se nessuna selezione è attiva
   */
  public List<T> getSelected(JList tbl)
  {
    int[] arSel = tbl.getSelectedIndices();
    if(arSel == null || arSel.length == 0)
      return null;

    ArrayList<T> rv = new ArrayList<T>();
    for(int i = 0; i < arSel.length; i++)
    {
      int sel = arSel[i];
      if(sel != -1)
      {
        T ts = arData.get(sel);
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
  public T getSelectedOne(JList tbl)
  {
    int sel = tbl.getSelectedIndex();
    if(sel == -1)
      return null;

    return arData.get(sel);
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
   * @param idx
   * @return
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
   * @param idx
   * @return
   */
  public boolean removeData(List<T> toDel)
  {
    return arData.removeAll(toDel);
  }

  /**
   * Cancella tutte le righe selezionate nella tabella.
   * @param tbl
   */
  public void deleteSelectedRows(JList tbl)
  {
    int uSel = tbl.getSelectedIndex();
    List<T> arSel = getSelected(tbl);
    if(arSel == null || arSel.isEmpty())
      return;

    if(removeData(arSel))
      fireContentsChanged();

    // riseleziona la riga corrente
    if(uSel != -1 && uSel < arData.size())
      tbl.setSelectedIndex(uSel);
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
  public T deleteRow(int mrow, JList tbl)
  {
    T rv = arData.get(mrow);
    removeData(mrow);
    fireContentsChanged();
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
  public synchronized List<T> deleteRows(int firstRow, int lastRow)
  {
    List<T> arSelected = getRows(firstRow, lastRow);
    if(removeData(arSelected))
      fireContentsChanged();
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
  public synchronized List<T> deleteRows(int[] rows)
  {
    List<T> arSelected = getRows(rows);
    if(removeData(arSelected))
      fireContentsChanged();
    return arSelected;
  }

  /**
   * Cancella tutte le righe selezionate nella tabella.
   * @param tbl tabella da cui estrarre la selezione
   * @return lista delle righe cancellate
   */
  public synchronized List<T> deleteRows(JList tbl)
  {
    int sel = tbl.getSelectedIndex();

    if(sel != -1)
    {
      List<T> arSelected = getSelected(tbl);
      if(removeData(arSelected))
        fireContentsChanged();

      if(getSize() > sel)
        tbl.setSelectedIndex(sel);

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
      fireContentsChanged();
  }

  /**
   * Cancella l'elenco di oggetti dalla tabella
   * @param toDel elemento da cancellare
   */
  public synchronized void deleteRowByObject(List<T> toDel)
  {
    if(removeData(toDel))
      fireContentsChanged();
  }

  /**
   * Cancella tutti gli elementi dal table model.
   */
  public synchronized void deleteAll()
  {
    arData.clear();
    fireContentsChanged();
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
  public synchronized List<T> getRows(int firstRow, int lastRow)
  {
    ArrayList<T> arSelected = new ArrayList<T>();
    for(int i = firstRow; i <= lastRow; i++)
    {
      if(i >= 0 && i < arData.size())
        arSelected.add(arData.get(i));
    }
    return arSelected;
  }

  /**
   * Recupera un blocco di righe.
   * @param rows
   * @return
   */
  public synchronized List<T> getRows(int[] rows)
  {
    ArrayList<T> arSelected = new ArrayList<T>();
    for(int i = 0; i < rows.length; i++)
    {
      int row = rows[i];
      if(row >= 0 && row < arData.size())
        arSelected.add(arData.get(row));
    }
    return arSelected;
  }
}
