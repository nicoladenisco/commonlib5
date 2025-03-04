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
 * Generico watchdog.
 * Un watchdog attiva una funzione fornita attraverso il listner
 * allo scadere di un timeout. Il watchdog può essere periodicamente
 * resettato azzerando il conteggio del timeout dal momento del reset.
 * La sua funzione è di attivare un evento se non ci sono dei reset
 * intermedi. Ogni watchdog usa un thread dedicato. La risoluzione
 * del watchdog è per default 1 secondo (può essere variata con la
 * proprietà tsleep).
 *
 * <pre>
 *  WatchDog wdAbort = new WatchDog("Abort", 5000, new WatchDogListner()
 *  {
 *    public boolean elapsedTotal(String wathDogName, long timeout)
 *    {
 *      abortOperation();
 *      return false; // interrompe definitivamente il conteggio
 *    }
 *  });
 *  ..
 *  ..
 *  // probabilmente in un'altro thread eseguiamo una operazione asincrona
 *  wdAbort.start();
 *  for(...)
 *  {
 *    wdAbort.reset();
 *  }
 *  ..
 *  ..
 *  // funzione di notifica asincrona di timeout
 *  public void abortOperation()
 *  {
 *    System.out.println("L'operazione non è andata a buon fine causa timeout.");
 *  }
 * </pre>
 *
 * @author Nicola De Nisco
 */
public class WatchDog implements Runnable
{
  protected String name = null;
  protected long timeout = 0, tsleep = 1000;
  protected WatchDogListner listner = null;
  protected SimpleTimer st = new SimpleTimer();
  protected Thread thRun = new Thread(this);

  /**
   * Crea un watchdog.
   * @param name nome simbolico del watchdog
   * @param timeout tempo di attesa in millisecondi
   * @param listner il listner per la segnalazione del timeout
   * @see WatchDogListner
   */
  public WatchDog(String name, long timeout, WatchDogListner listner)
  {
    this.name = name;
    this.timeout = timeout;
    this.listner = listner;

    // abbassa il tempo di ciclo da 1 secondo alla metà del timeout (solo se inferiore a un secondo)
    long t = timeout / 10;
    if(t < 10)
      t = 10;
    if(t < tsleep)
      tsleep = t;

    if(listner == null)
      throw new NullPointerException("Listner cannot be null.");
  }

  /**
   * Avvia il watchdog.
   * Crea il thread per il conteggio asincrono del timeout.
   */
  public void start()
  {
    if(!thRun.isAlive())
    {
      thRun.setName("WatchDog_" + name);
      thRun.setDaemon(true);
      thRun.start();
    }
    reset();
  }

  /**
   * Reset del watchdog.
   */
  public void reset()
  {
    st.reset();
  }

  @Override
  public void run()
  {
    do
    {
      st.reset();
      while(!st.isElapsed(timeout))
        sleep(tsleep);
    }
    while(listner.elapsedTotal(name, timeout));
  }

  /**
   * Congela il processo per il ritardo indicato.
   * @param delay ritardo in millisecondi
   */
  protected void sleep(long delay)
  {
    try
    {
      Thread.sleep(delay);
    }
    catch(Exception e)
    {
    }
  }

  /**
   * Ritorna il listner associato.
   * @return
   */
  public WatchDogListner getListner()
  {
    return listner;
  }

  /**
   * Imposta il listner associato.
   * @param listner
   */
  public void setListner(WatchDogListner listner)
  {
    this.listner = listner;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  /**
   * Ritorna la risoluzione del timer (per default 1 secondo).
   * @return risoluzione in millisecondi
   */
  public long getTsleep()
  {
    return tsleep;
  }

  /**
   * Imposta la risoluzione del timer.
   * @param tsleep millisecondi di risoluzione
   */
  public void setTsleep(long tsleep)
  {
    this.tsleep = tsleep;
  }

  public void setExplicitSignaled(boolean explicitSignaled)
  {
    st.setExplicitSignaled(explicitSignaled);
  }
}
