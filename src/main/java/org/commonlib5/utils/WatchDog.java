/*
 * WatchDog.java
 *
 * Created on 24-ott-2008, 13.48.21
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
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
  }

  /**
   * Avvia il watchdog.
   * Crea il thread per il conteggio asincrono del timeout.
   */
  public void start()
  {
    if(!thRun.isAlive())
    {
      thRun.setName("WatchDog "+name);
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
}
