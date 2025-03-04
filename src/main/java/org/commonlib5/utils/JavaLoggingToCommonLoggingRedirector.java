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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Redirezione log java standard all'interno di Log4j.
 * Questa classe installa un ricevitore delle log nella
 * JVM reindirizzando tutto il log standard verso il
 * layer Log4j.
 * La mappatura dei livelli da Java a Log4j è la seguente:
 * <ul>
 * <li>SEVERE error</li>
 * <li>WARNING warning</li>
 * <li>INFO info</li>
 * <li>CONFIG debug</li>
 * <li>FINE trace</li>
 * <li>FINER trace</li>
 * <li>FINEST trace</li>
 * </ul>
 */
public class JavaLoggingToCommonLoggingRedirector
{
  static JDKLogHandler activeHandler;

  /**
   * Installa handler di redirezione.
   * Il livello massimo viene impostato a INFO,
   * ovvero i messaggi con livello CONFIG, FINE, FINER, FINEST
   * sono ignorati.
   * Per visualizzare tutti i messaggi usare activate(Level.ALL)
   * oppure activate(null).
   */
  public static void activate()
  {
    activate(Level.INFO);
  }

  /**
   * Installa handler di redirezione.
   * Vengono intercettati tutti i messaggi di livello
   * uguale o superiore a minLevel. ES: Level.INFO
   * intercetta SEVERE, WARNING, INFO e ignora CONFIG, FINE, FINER, FINEST
   * Per visualizzare tutti i messaggi usare activate(Level.ALL)
   * oppure activate(null).
   * @param minLivel livello minimo da visualizzare (null = tutti i livelli)
   */
  public static void activate(Level minLivel)
  {
    try
    {
      Logger rootLogger = LogManager.getLogManager().getLogger("");

      // remove old handlers
      for(Handler handler : rootLogger.getHandlers())
        rootLogger.removeHandler(handler);

      // add our own
      activeHandler = new JDKLogHandler(minLivel);
      activeHandler.setLevel(Level.ALL);
      rootLogger.addHandler(activeHandler);
      rootLogger.setLevel(Level.ALL);

      // done, let's check it right away!!!
      Logger.getLogger(JavaLoggingToCommonLoggingRedirector.class.getName()).
         fine("activated: sending JDK log messages to Commons Logging");
    }
    catch(Exception exc)
    {
      LogFactory.getLog(JavaLoggingToCommonLoggingRedirector.class).error("activation failed", exc);
    }
  }

  public static void deactivate()
  {
    Logger rootLogger = LogManager.getLogManager().getLogger("");
    rootLogger.removeHandler(activeHandler);

    Logger.getLogger(JavaLoggingToCommonLoggingRedirector.class.getName()).info("dactivated");
  }

  protected static class JDKLogHandler extends Handler
  {
    private Map<String, Log> cachedLogs = new ConcurrentHashMap<String, Log>();
    private Level maxLevelVerbose = null;

    public JDKLogHandler()
    {
    }

    public JDKLogHandler(Level maxLevelVerbose)
    {
      this.maxLevelVerbose = maxLevelVerbose;
    }

    private synchronized Log getLog(String logName)
    {
      Log log = cachedLogs.get(logName);
      if(log == null)
      {
        log = LogFactory.getLog(logName);
        cachedLogs.put(logName, log);
      }
      return log;
    }

    @Override
    public void publish(LogRecord record)
    {
      Level level = record.getLevel();
      if(maxLevelVerbose != null && level.intValue() < maxLevelVerbose.intValue())
        return;

      Log log = getLog(record.getLoggerName());
      String message = record.getMessage();
      Throwable exception = record.getThrown();

      if(level == Level.SEVERE)
        log.error(message, exception);
      else if(level == Level.WARNING)
        log.warn(message, exception);
      else if(level == Level.INFO)
        log.info(message, exception);
      else if(level == Level.CONFIG)
        log.debug(message, exception);
      else
        log.trace(message, exception);
    }

    @Override
    public void flush()
    {
      // nothing to do
    }

    @Override
    public void close()
    {
      // nothing to do
    }
  }
}
