/*
 * LongOperListenerDouble.java
 *
 * Created on 23 luglio 2007, 11.24
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
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

