/*
 * ProcessStreamHelper.java
 *
 * Created on 27-gen-2009, 10.10.41
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */

package org.commonlib5.exec;

import org.commonlib5.io.ByteBufferInputStream;
import java.io.*;

/**
 * Classe di supporto per l'esecuzione sicura di processi.
 * Il processo lanciato scrive il suo output in stream bufferati
 * leggibili esternamente in modo asincrono.
 *
 * @author Nicola De Nisco
 */
public class ProcessStreamHelper
{
  private Process process = null;
  private int exitValue = 0;
  private boolean running = false;
  private boolean errors = false;
  private Thread thRun = null;
  private boolean killOnExit = true;
  private ByteBufferInputStream out = new ByteBufferInputStream();
  private ByteBufferInputStream err = new ByteBufferInputStream();

  public static ProcessStreamHelper exec(String cmd) throws IOException
  {
    return new ProcessStreamHelper(Runtime.getRuntime().exec(cmd));
  }

  public static ProcessStreamHelper exec(String[] cmdArray) throws IOException
  {
    return new ProcessStreamHelper(Runtime.getRuntime().exec(cmdArray));
  }

  public static ProcessStreamHelper exec(String[] cmdArray, String[] env) throws IOException
  {
    return new ProcessStreamHelper(Runtime.getRuntime().exec(cmdArray, env));
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessStreamHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @throws IOException
   */
  public ProcessStreamHelper(Process process) throws IOException
  {
    this.process = process;
    startThread();
  }

  private synchronized void startThread()
  {
    running = true;
    thRun = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          runExecHelper(process);
          process = null;
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
          errors = true;
          running = false;
        }
      }
    };

    thRun.setName("ProcessStreamHelper");
    thRun.setDaemon(true);
    thRun.start();
  }

  public int getExitValue()
  {
    return exitValue;
  }

  public boolean isRunning()
  {
    return running;
  }

  public boolean isErrors()
  {
    return errors;
  }

  public void destroy()
  {
    if(process != null)
      process.destroy();
  }

  public synchronized void waitFor()
  {
    try
    {
      if(running)
        wait();
    }
    catch(Exception ex)
    {
    }
  }

  protected synchronized void runExecHelper(Process process) throws IOException
  {
    ProcessWatch.watch(process, killOnExit, new ProcessWatchListner()
    {
      @Override
      public void notifyStdout(byte[] output, int offset, int length)
      {
        out.addToBuffer(output, offset, length);
      }

      @Override
      public void notifyStderr(byte[] output, int offset, int length)
      {
        err.addToBuffer(output, offset, length);
      }
    });

    running = false;
    notify();
  }

  public boolean isKillOnExit()
  {
    return killOnExit;
  }

  public void setKillOnExit(boolean killOnExit)
  {
    this.killOnExit = killOnExit;
  }

  public InputStream getStdoutStream()
  {
    return out;
  }

  public InputStream getStderrStream()
  {
    return err;
  }
}

