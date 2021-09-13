/*
 * ProcessLogHelper.java
 *
 * Created on 23-set-2008, 18.13.06
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.exec;

import java.io.*;

/**
 * Lancia un processo inviando in un file di log il suo output.
 *
 * @author Nicola De Nisco
 */
public class ProcessLogHelper
{
  private Process process = null;
  private int exitValue = 0;
  private boolean running = false;
  private boolean errors = false;
  private Thread thRun = null;
  private OutputStream logStdout = null;
  private OutputStream logStderr = null;

  public static ProcessLogHelper exec(String cmd, File log) throws IOException
  {
    FileOutputStream fos = new FileOutputStream(log);
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmd), fos, fos);
  }

  public static ProcessLogHelper exec(String cmd,
     OutputStream logStdout, OutputStream logStderr) throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmd), logStdout, logStderr);
  }

  public static ProcessLogHelper exec(String[] cmdArray,
     OutputStream logStdout, OutputStream logStderr) throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmdArray), logStdout, logStderr);
  }

  public static ProcessLogHelper exec(String[] cmdArray, String[] env,
     OutputStream logStdout, OutputStream logStderr) throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmdArray, env), logStdout, logStderr);
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessLogHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @param logStdout stream a cui inviare lo stdout del processo creato
   * @param logStderr stream a cui inviare lo stderr del processo creato
   * @throws IOException
   */
  public ProcessLogHelper(Process process,
     OutputStream logStdout, OutputStream logStderr) throws IOException
  {
    this.process = process;
    this.logStdout = logStdout;
    this.logStderr = logStderr;
    startThread();
  }

  private void startThread()
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

          // chiude gli stream ignorando gli errori
          closeSilent(logStdout);
          closeSilent(logStderr);
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
          errors = true;
          running = false;
        }
      }
    };

    thRun.setName("ProcessLogHelper");
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
    ProcessWatch.watch(process, true, new ProcessWatchListner()
    {
      @Override
      public void notifyStdout(byte[] output, int offset, int length)
      {
        try
        {
          logStdout.write(output, offset, length);
        }
        catch(IOException ex)
        {
          ex.printStackTrace();
        }
      }

      @Override
      public void notifyStderr(byte[] output, int offset, int length)
      {
        try
        {
          logStderr.write(output, offset, length);
        }
        catch(IOException ex)
        {
          ex.printStackTrace();
        }
      }
    });

    running = false;
    notify();
  }

  private void closeSilent(OutputStream os)
  {
    try
    {
      os.close();
    }
    catch(Exception e)
    {
    }
  }
}

