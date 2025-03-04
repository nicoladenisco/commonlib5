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

/**
 * Come LongOperListener ma aggiorna due scrollbar
 * uno per l'avanzamento totale e uno per la sub operazione;
 * gestisce anche un dettaglio di sub operazione in corso.
 * @author Nicola De Nisco
 */
public interface LongOperListenerDouble extends LongOperListener
{
  /**
   * Azzera l'interfaccia (inizio operazioni)
   */
  public void resetMainUI();
  
  /**
   * Completa l'interfaccia (fine operazioni)
   * @param total
   */
  public void completeMainUI(long total);
  
  /**
   * Funzione di notifica per aggiornamento interfaccia.
   * @param part indica a che punto siamo del totale
   * @param total totale da raggiungere
   * @return true per continuare, false interrompe operazione
   */
  public boolean updateMainUI(long part, long total);
  
  /**
   * Notifica l'inizio di una sub operazione con il
   * relativo titolo da visualizzare.
   * @param subOperation titolo da visualizzare
   */
  public void displaySubOperation(String subOperation);
}

