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
 * Osservazione di un processo esterno.
 * Quando il processo produce output le relative
 * funzioni del listner sono chiamate.
 *
 * @author Nicola De Nisco
 */
public class ProcessWatch
{
  public static int watch(Process process, boolean killOnExit, ProcessWatchListner pwl)
     throws IOException
  {
    InputStream stdout = process.getInputStream();
    InputStream stderr = process.getErrorStream();

    byte[] buffer = new byte[1024];

    boolean done = false;
    boolean stdoutclosed = false;
    boolean stderrclosed = false;

    while(!done)
    {
      boolean readSomething = false;

      // read from the process's standard output
      if(!stdoutclosed && stdout.available() > 0)
      {
        readSomething = true;
        int read = stdout.read(buffer, 0, buffer.length);
        if(read < 0)
          stdoutclosed = true;
        else if(read > 0)
          pwl.notifyStdout(buffer, 0, read);
      }

      // read from the process's standard error
      if(!stderrclosed && stderr.available() > 0)
      {
        readSomething = true;
        int read = stderr.read(buffer, 0, buffer.length);
        if(read < 0)
          stderrclosed = true;
        else if(read > 0)
          pwl.notifyStderr(buffer, 0, read);
      }

      // Check the exit status only we haven't read anything,
      // if something has been read, the process is obviously not dead yet.
      if(readSomething)
        continue;

      try
      {
        return process.exitValue();
      }
      catch(IllegalThreadStateException itx)
      {
        // Exit status not ready yet.
        // Give the process a little breathing room.
        try
        {
          Thread.sleep(200);
        }
        catch(InterruptedException ix)
        {
          if(killOnExit)
          {
            process.destroy();
            throw new IOException("Interrupted - processes killed");
          }
        }
      }
    }

    return -1;
  }
}
