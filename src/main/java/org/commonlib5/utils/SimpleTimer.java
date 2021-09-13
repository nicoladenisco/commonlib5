/*
 * SimpleTimer.java
 *
 * Created on 22 novembre 2007, 12.52
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.utils;

import java.io.Serializable;

/**
 * Semplice timer di verifica tempo trascorso.
 * Tutti i tempi sono espressi in millisecondi.
 *
 * @author Nicola De Nisco
 */
public class SimpleTimer implements Serializable
{
  private long tstart = 0;
  private boolean explicitSignaled = false;

  /**
   * Crea un timer resettato.
   */
  public SimpleTimer()
  {
    reset();
  }

  /**
   * Crea un timer in stato già scaduto.
   * Se explicitSignaled è true la prima chiamata a
   * isElapsed() restituisce sempre true.
   * @param explicitSignaled stato di già scaduto del timer
   */
  public SimpleTimer(boolean explicitSignaled)
  {
    this.explicitSignaled = explicitSignaled;
    reset();
  }

  /**
   * Reset del timer.
   */
  public void reset()
  {
    tstart = System.currentTimeMillis();
  }

  /**
   * Vero se è trascorso il tempo specificato.
   * @param tstop tempo timer in millisecondi (se minore o uguale a 0 torna subito)
   * @return vero se dall'ultimo reset sono trascorti tstop millisecondi
   */
  public boolean isElapsed(long tstop)
  {
    if(explicitSignaled || tstop <= 0)
    {
      explicitSignaled = false;
      return true;
    }

    long elapsed = getElapsed();
    return elapsed >= tstop;
  }

  /**
   * Ritorna il tempo trascorso dall'ultimo reset.
   * @return tempo in millisecondi
   */
  public long getElapsed()
  {
    return System.currentTimeMillis() - tstart;
  }

  /**
   * Ritorna il tempo trascorso dopo la scadenza del timer.
   * @param tstop tempo timer in millisecondi
   * @return milliscondi trascorsi dalla scadenza o 0 se ancora non scaduto
   */
  public long getOverTime(long tstop)
  {
    long elapsed = getElapsed();
    if(elapsed < tstop)
      return 0;

    return elapsed - tstop;
  }

  @Override
  public String toString()
  {
    return "Elapsed " + getElapsed();
  }

  /**
   * Ritorna lo stato di segnalazione esplicita.
   * La segnalazione esplicita viene resettata alla prima chiamata a isElapsed().
   * @return vero se il flag di segnalazione esplicita è attivo
   */
  public boolean isExplicitSignaled()
  {
    return explicitSignaled;
  }

  /**
   * Imposta segnalazione esplicita timer.
   * Se explicitSignaled è true la prossima chiamata a
   * isElapsed() restituisce sempre true.
   * @param explicitSignaled stato segnalazione esplicita timer
   */
  public void setExplicitSignaled(boolean explicitSignaled)
  {
    this.explicitSignaled = explicitSignaled;
  }

  /**
   * Congela il thread chiamante per il tempo specificato.
   * @param tstop millisecondi da attendere (se minore o uguale a 0 torna subito)
   * @return vero se l'attesa è completa falso in caso di interruzione
   */
  public boolean waitElapsed(long tstop)
  {
    if(explicitSignaled || tstop <= 0)
    {
      explicitSignaled = false;
      return true;
    }

    try
    {
      long elapsed;
      while((elapsed = getElapsed()) < tstop)
      {
        long waitmillis = (tstop - elapsed) / 5;

        if(waitmillis < 10)
          waitmillis = 10;
        if(waitmillis > 500)
          waitmillis = 500;

        if(explicitSignaled)
        {
          explicitSignaled = false;
          return true;
        }

        Thread.sleep(waitmillis);
      }
      return true;
    }
    catch(Throwable e)
    {
      return false;
    }
  }
}
