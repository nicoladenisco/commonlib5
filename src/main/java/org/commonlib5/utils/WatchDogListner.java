/*
 * WatchDogListner.java
 *
 * Created on 24-ott-2008, 13.56.05
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.utils;

/**
 * Notifica eventi watchdog.
 *
 * @author Nicola De Nisco
 */
@FunctionalInterface
public interface WatchDogListner
{
  /**
   * Funzione di notifica di un timeout di WatchDog.
   * @param watchDogName nome del watchdog
   * @param timeout il timeout del watchdog
   * @return true per ripetere il conteggio false per interromperlo definitivamente
   * @see WatchDog
   */
  public boolean elapsedTotal(String watchDogName, long timeout);
}
