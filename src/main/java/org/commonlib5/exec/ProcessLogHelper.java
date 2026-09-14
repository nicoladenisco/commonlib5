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

/**
 * Lancia un processo inviando in un file di log il suo output.
 *
 * @author Nicola De Nisco
 */
public class ProcessLogHelper extends ProcessHelper
{
  public static ProcessLogHelper exec(String cmd, File log)
     throws IOException
  {
    FileOutputStream fos = new FileOutputStream(log);
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmd), fos);
  }

  public static ProcessLogHelper exec(String cmd,
     OutputStream logStdout, OutputStream logStderr)
     throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmd), logStdout, logStderr);
  }

  public static ProcessLogHelper exec(String[] cmdArray,
     OutputStream logStdout, OutputStream logStderr)
     throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmdArray), logStdout, logStderr);
  }

  public static ProcessLogHelper exec(String[] cmdArray, String[] env, OutputStream logStream)
     throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmdArray, env), logStream);
  }

  public static ProcessLogHelper exec(String[] cmdArray, String[] env,
     OutputStream logStdout, OutputStream logStderr)
     throws IOException
  {
    return new ProcessLogHelper(Runtime.getRuntime().exec(cmdArray, env), logStdout, logStderr);
  }

  /**
   * Costruttore di servizio.
   * Attacca questo ProcessLogHelper ad un processo già creato.
   * Vedi in alternativa le funzioni exec(...).
   * @param process processo da monitorare
   * @param logStream stream a cui inviare l'output del processo (entrambi stdin e stderr)
   * @throws IOException
   */
  public ProcessLogHelper(Process process, OutputStream logStream)
     throws IOException
  {
    this.process = process;
    this.out = new PrintStream(logStream);
    this.err = this.out;
    startThread(defaultListner);
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
  public ProcessLogHelper(Process process, OutputStream logStdout, OutputStream logStderr)
     throws IOException
  {
    this.process = process;
    this.out = new PrintStream(logStdout);
    this.err = new PrintStream(logStderr);
    startThread(defaultListner);
  }

  @Override
  protected void runExecHelper(Process process, ProcessWatchListner listner)
     throws IOException
  {
    try
    {
      exitValue = ProcessWatch.watch(process, killOnExit, listner);
    }
    finally
    {
      synchronized(this)
      {
        // chiude gli stream ignorando gli errori
        closeSilent(out);
        closeSilent(err);

        running = false;
        notify();
      }
    }
  }

  protected void closeSilent(PrintStream os)
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
