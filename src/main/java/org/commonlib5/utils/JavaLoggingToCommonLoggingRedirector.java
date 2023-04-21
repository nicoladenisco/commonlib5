/*
 *  JavaLoggingToCommonLoggingRedirector.java
 *  Creato il 20-mar-2012, 15.30.11
 *
 *  Copyright (C) 2012 RAD-IMAGE s.r.l.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni, 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 *
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Alternatively, the contents of this file may be used under the terms of
 *  either the GNU General Public License Version 2 or later (the "GPL"), or
 *  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 *  in which case the provisions of the GPL or the LGPL are applicable instead
 *  of those above. If you wish to allow use of your version of this file only
 *  under the terms of either the GPL or the LGPL, and not to allow others to
 *  use your version of this file under the terms of the MPL, indicate your
 *  decision by deleting the provisions above and replace them with the notice
 *  and other provisions required by the GPL or the LGPL. If you do not delete
 *  the provisions above, a recipient may use your version of this file under
 *  the terms of any one of the MPL, the GPL or the LGPL.
 *  ***** END LICENSE BLOCK *****
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
