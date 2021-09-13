/*
 * Copyright (C) 2020 Nicola De Nisco
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
package org.commonlib5.io;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Monitor per cambiamenti di un file.
 * Usa le nuove API di java 7 per il monitoraggio di
 * file systems; il file indicato viene monitorato per cambiamenti
 * che verranno notificati.
 *
 * Si puo ridefinire doOnChange() o passare un oggetto runnable nel costruttore.
 *
 * @author Nicola De Nisco
 */
public class FileWatcher extends Thread
{
  private final File file;
  private final Runnable changeHandler;
  private final AtomicBoolean stop = new AtomicBoolean(false);

  /**
   * Costruttore semplice.
   * Occorre ridefinire il metodo doOnChange().
   * @param file file da monitorare
   */
  public FileWatcher(File file)
  {
    this.file = file;
    this.changeHandler = null;
  }

  /**
   * Costruttore con handler.
   * @param file file da monitorare
   * @param changeHandler verrà chiamato ad ogni cambiamento
   */
  public FileWatcher(File file, Runnable changeHandler)
  {
    this.file = file;
    this.changeHandler = changeHandler;
  }

  public boolean isStopped()
  {
    return stop.get();
  }

  public void stopThread()
  {
    stop.set(true);
  }

  public void doOnChange()
  {
    if(changeHandler != null)
      changeHandler.run();
  }

  @Override
  final public void run()
  {
    try (WatchService watcher = FileSystems.getDefault().newWatchService())
    {
      Path path = file.toPath().getParent();
      path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

      while(!isStopped())
      {
        WatchKey key;

        try
        {
          key = watcher.poll(200, TimeUnit.MILLISECONDS);
        }
        catch(InterruptedException e)
        {
          return;
        }

        if(key == null)
        {
          Thread.yield();
          continue;
        }

        for(WatchEvent<?> event : key.pollEvents())
        {
          WatchEvent.Kind<?> kind = event.kind();

          @SuppressWarnings("unchecked")
          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path filename = ev.context();

          if(kind == StandardWatchEventKinds.OVERFLOW)
          {
            Thread.yield();
            continue;
          }
          else if(kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
             && filename.toString().equals(file.getName()))
          {
            doOnChange();
          }

          boolean valid = key.reset();
          if(!valid)
            break;
        }

        Thread.yield();
      }
    }
    catch(Throwable ex)
    {
      Logger.getLogger(FileWatcher.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
