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
package org.commonlib5.exec;

import java.io.*;
import java.util.Collection;

/**
 * Classe di supporto per l'avvio di processi server esterni.
 *
 * @author Nicola De Nisco
 */
public class ProcessHelper
{
  private Process process = null;
  private int exitValue = 0;
  private boolean running = false;
  private boolean errors = false;
  private Thread thRun = null;
  private PrintStream out, err;
  private boolean killOnExit = true;

  private ProcessWatchListner defaultListner = new ProcessWatchListner()
  {
    @Override
    public void notifyStdout(byte[] output, int offset, int length)
    {
      out.print(new String(output, offset, length));
    }

    @Override
    public void notifyStderr(byte[] output, int offset, int length)
    {
      err.print(new String(output, offset, length));
    }
  };

  public static ProcessHelper exec(String cmd)
     throws IOException
  {
    return new ProcessHelper(Runtime.getRuntime().exec(cmd));
  }

  public static ProcessHelper exec(String cmd, String[] env)
     throws IOException
  {
    return new ProcessHelper(Runtime.getRuntime().exec(cmd, env));
  }

  public static ProcessHelper exec(String[] cmdArray)
     throws IOException
  {
    return new ProcessHelper(Runtime.getRuntime().exec(cmdArray));
  }

  public static ProcessHelper exec(Collection<String> cmdArray)
     throws IOException
  {
    String[] cmd = cmdArray.toArray(new String[0]);
    return new ProcessHelper(Runtime.getRuntime().exec(cmd));
  }

  public static ProcessHelper exec(String[] cmdArray, String[] env)
     throws IOException
  {
    return new ProcessHelper(Runtime.getRuntime().exec(cmdArray, env));
  }

  public static ProcessHelper exec(Collection<String> cmdArray, Collection<String> envArray)
     throws IOException
  {
    String[] cmd = cmdArray.toArray(new String[0]);
    String[] env = envArray.toArray(new String[0]);
    return new ProcessHelper(Runtime.getRuntime().exec(cmd, env));
  }

  public static ProcessHelper execUsingShell(String command)
     throws IOException
  {
    if(command == null)
      throw new NullPointerException();
    String[] cmdarray;
    String os = System.getProperty("os.name");
    if(os.equals("Windows 95") || os.equals("Windows 98") || os.equals("Windows ME"))
      cmdarray = new String[]
      {
        "command.exe", "/C", command
      };
    else if(os.startsWith("Windows"))
      cmdarray = new String[]
      {
        "cmd.exe", "/C", command
      };
    else
      cmdarray = new String[]
      {
        "/bin/sh", "-c", command
      };

    return new ProcessHelper(Runtime.getRuntime().exec(cmdarray));
  }

  public static ProcessHelper execUsingShell(String command, String[] env)
     throws IOException
  {
    if(command == null)
      throw new NullPointerException();
    String[] cmdarray;
    String os = System.getProperty("os.name");
    if(os.equals("Windows 95") || os.equals("Windows 98") || os.equals("Windows ME"))
      cmdarray = new String[]
      {
        "command.exe", "/C", command
      };
    else if(os.startsWith("Windows"))
      cmdarray = new String[]
      {
        "cmd.exe", "/C", command
      };
    else
      cmdarray = new String[]
      {
        "/bin/sh", "-c", command
      };

    return new ProcessHelper(Runtime.getRuntime().exec(cmdarray, env));
  }

  public static ProcessHelper execUsingShell(String command, Collection<String> envArray)
     throws IOException
  {
    String[] env = envArray.toArray(new String[0]);
    return execUsingShell(command, env);
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @throws IOException
   */
  public ProcessHelper(Process process)
     throws IOException
  {
    this(process, null);
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @param listner listner a cui notificare l'avanzamento (può essere null)
   * @throws IOException
   */
  public ProcessHelper(Process process, ProcessWatchListner listner)
     throws IOException
  {
    this(process, listner, null, null);
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @param listner listner a cui notificare l'avanzamento (può essere null)
   * @param out stream per raccogliere l'output
   * @param err stream per raccogliere l'output (errori)
   * @throws IOException
   */
  public ProcessHelper(Process process, ProcessWatchListner listner, PrintStream out, PrintStream err)
     throws IOException
  {
    this.process = process;
    this.out = out;
    this.err = err;
    if(listner == null)
      listner = defaultListner;
    startThread(listner);
  }

  private synchronized void startThread(final ProcessWatchListner listner)
  {
    running = true;
    thRun = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          runExecHelper(process, listner);
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

    thRun.setName("ProcessHelper");
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

  protected synchronized void runExecHelper(Process process, ProcessWatchListner listner)
     throws IOException
  {
    if(out == null)
      out = System.out;
    if(err == null)
      err = System.err;

    try
    {
      ProcessWatch.watch(process, killOnExit, listner);
    }
    finally
    {
      running = false;
      notify();
    }
  }

  public boolean isKillOnExit()
  {
    return killOnExit;
  }

  public void setKillOnExit(boolean killOnExit)
  {
    this.killOnExit = killOnExit;
  }
}
