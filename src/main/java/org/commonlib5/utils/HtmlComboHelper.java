/*
 * Copyright (C) 2021 Nicola De Nisco
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
package org.commonlib5.utils;

import java.util.ArrayList;

/**
 * Classe di utilità per formattare un combobox html.
 *
 * @author Nicola De Nisco
 * @param <T> tipo della chiave (di solito Integer o String)
 */
public class HtmlComboHelper<T>
{
  protected final ArrayList<Pair<? extends T, ? extends Object>> rows = new ArrayList<>();

  /** header del combo ('select') */
  public String comboHeader = "<select>";
  /** footer del combo ('/select') */
  public String comboFooter = "</select>";

  /**
   * Aggiunge un riga nel combo
   * @param value valore da ritornare
   * @param description descrizione del valore
   */
  public void addRow(T value, Object description)
  {
    this.rows.add(new Pair<>(value, description));
  }

  //public void addOptGroup(String name, Collection<Pair<? extends T, ? extends Object>> itemGroup)
  //{
  //}
  /**
   * Pulisce tutti i dati accumulati.
   */
  public void clear()
  {
    rows.clear();
  }

  /**
   * Formata completa del combo.
   * Usa comboHeader e comboFooter per i tag 'select'.
   * @param sb accumulatore dell'HTML
   * @param defVal valore da selezionare nel combo (può essere null)
   */
  public void formatHtmlCombo(StringBuilder sb, T defVal)
  {
    sb.append(comboHeader).append("\n");
    formatHtmlOptions(sb, defVal);
    sb.append(comboFooter).append("\n");
  }

  /**
   * Formatta solo i tag 'option'.
   * @param sb accumulatore dell'HTML
   * @param defVal valore da selezionare nel combo (può essere null)
   */
  public void formatHtmlOptions(StringBuilder sb, T defVal)
  {
    int row = 0;
    String defValStr = StringOper.okStr(defVal);
    for(Pair<? extends T, ? extends Object> rowValues : rows)
    {
      sb.append(formatHtmlOption(row, rowValues.first, rowValues.second, defValStr));
      row++;
    }
  }

  /**
   * Formatta la singola opzione del combo.
   * @param row indice di riga
   * @param codice valore da ritornare
   * @param descrizione descrizione del valore
   * @param defVal valore da selezionare nel combo (può essere null)
   * @return
   */
  public String formatHtmlOption(int row, T codice, Object descrizione, String defVal)
  {
    String codStr = StringOper.okStr(codice);

    if(defVal.equals(codStr))
      return "<option value=\"" + codStr + "\" selected>" + StringOper.okStr(descrizione, "&nbsp;") + "</option>";
    else
      return "<option value=\"" + codStr + "\">" + StringOper.okStr(descrizione, "&nbsp;") + "</option>";
  }

  public String builCombo(T defVal)
  {
    StringBuilder sb = new StringBuilder(1024);
    formatHtmlCombo(sb, defVal);
    return sb.toString();
  }

  /**
   * Produce l'HTML completo della tabella.
   * @return HTML della tabella
   */
  @Override
  public String toString()
  {
    return builCombo(null);
  }
}
