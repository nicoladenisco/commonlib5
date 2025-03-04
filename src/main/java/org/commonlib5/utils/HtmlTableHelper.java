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
package org.commonlib5.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Classe di utilità per formattare dati in una tabella html.
 *
 * @author Nicola De Nisco
 */
public class HtmlTableHelper
{
  protected final ArrayList<? extends Object> header = new ArrayList<>();
  protected final ArrayList<Collection<? extends Object>> rows = new ArrayList<>();

  /** header della tabella utilizzato da formatHtmlTable ('table') */
  public String tableHeader = "<table>";
  /** footer della tabella utilizzato da formatHtmlTable ('/table') */
  public String tableFooter = "</table>";

  /**
   * Imposta gli header.
   * @param header
   */
  public void setHeader(Collection<? extends Object> header)
  {
    this.header.clear();
    this.header.addAll((Collection) header);
  }

  /**
   * Imposta gli header.
   * @param header
   */
  public void setHeader(Object... header)
  {
    setHeader(Arrays.asList(header));
  }

  /**
   * Aggiunge una riga.
   * Ogni valore diventerà una cella della tabella.
   * @param row
   */
  public void addRow(Collection<? extends Object> row)
  {
    this.rows.add(row);
  }

  /**
   * Aggiunge una riga.
   * Ogni valore diventerà una cella della tabella.
   * @param row
   */
  public void addRow(Object... row)
  {
    this.rows.add(Arrays.asList(row));
  }

  /**
   * Pulisce tutti i dati accumulati.
   * Anche gli header vengono cancellati.
   */
  public void clear()
  {
    header.clear();
    rows.clear();
  }

  /**
   * Pulisce solo gli header.
   */
  public void clearHeader()
  {
    header.clear();
  }

  /**
   * Pulisce solo il contenuto.
   */
  public void clearRows()
  {
    rows.clear();
  }

  /**
   * Formattazione completa della tabella.
   * Usa tableHeader e tableFooter per i tag 'table'.
   * @param sb accumulatore dell'HTML
   */
  public void formatHtmlTable(StringBuilder sb)
  {
    sb.append(tableHeader).append("\n");
    formatHtmlContent(sb);
    sb.append(tableFooter).append("\n");
  }

  /**
   * Formatta contenuto della tabella.
   * Viene formatta sia l'header che le rige.
   * @param sb accumulatore dell'HTML
   */
  public void formatHtmlContent(StringBuilder sb)
  {
    formatHtmlHeader(sb);
    formatHtmlRows(sb);
  }

  /**
   * Formata header della tabella.
   * Viene formattato tutto il tag 'thead'.
   * @param sb accumulatore dell'HTML
   */
  public void formatHtmlHeader(StringBuilder sb)
  {
    if(header.isEmpty())
      return;

    sb.append("<thead><tr>\n");
    int col = 0;
    for(Object h : header)
    {
      sb.append("<th>").append(formatCaption(col, h, "&nbsp;")).append("</th>");
      col++;
    }
    sb.append("\n</tr></thead>\n");
  }

  /**
   * Formata righe della tabella.
   * Viene formattato tutto il tag 'tbody'.
   * @param sb accumulatore dell'HTML
   */
  public void formatHtmlRows(StringBuilder sb)
  {
    if(rows.isEmpty())
      return;

    sb.append("<tbody>\n");
    int row = 0;
    for(Collection<? extends Object> valuesrow : rows)
    {
      sb.append("<tr>");
      int col = 0;
      for(Object val : valuesrow)
      {
        sb.append("<td>").append(formatValue(row, col, val, "&nbsp;")).append("</td>");
        col++;
      }
      sb.append("</tr>\n");
      row++;
    }
    sb.append("</tbody>\n");
  }

  /**
   * Formatta il contenuto di una cella della testata.
   * @param col colonna della testata
   * @param valore valore da formattare
   * @param defVal default per vuoto (di solito &nbsp;)
   * @return la stringa valore della testata
   */
  public String formatCaption(int col, Object valore, String defVal)
  {
    return StringOper.okStr(valore, defVal);
  }

  /**
   * Formatta il contenuto di una cella del corpo.
   * @param row riga della cella
   * @param col colonna della cella
   * @param valore valore da formattare
   * @param defVal default per vuoto (di solito &nbsp;)
   * @return la stringa valore della cella
   */
  public String formatValue(int row, int col, Object valore, String defVal)
  {
    return StringOper.okStr(valore, defVal);
  }

  /**
   * Produce l'HTML completo della tabella.
   * @return HTML della tabella
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(1024);
    formatHtmlTable(sb);
    return sb.toString();
  }
}
