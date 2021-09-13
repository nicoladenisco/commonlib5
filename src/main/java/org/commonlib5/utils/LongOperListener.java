/*
 * LongOperListener.java
 *
 * Created on 22 maggio 2006, 16.28
 */
package org.commonlib5.utils;

/**
 * Ascoltatore per aggiornamento durante operazioni lunghe.
 * @author Nicola De Nisco
 */
public interface LongOperListener
{
  /**
   * Azzera l'interfaccia (inizio operazioni)
   */
  public void resetUI();

  /**
   * Completa l'interfaccia (fine operazioni)
   * @param total totale raggiunto
   */
  public void completeUI(long total);

  /**
   * Funzione di notifica per aggiornamento interfaccia.
   * @param part indica a che punto siamo del totale
   * @param total totale da raggiungere
   * @return true per continuare, false interrompe operazione
   */
  public boolean updateUI(long part, long total);
}
