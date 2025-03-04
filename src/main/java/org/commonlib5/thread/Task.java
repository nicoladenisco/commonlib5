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
package org.commonlib5.thread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.commonlib5.gui.ErrorDialog;
import org.commonlib5.utils.LongOperListener;

/**
 * Classe base di tutti i task in background.
 * Viene implementata con compatibilità per la sua omonima in jdesktop.
 *
 * @author Nicola De Nisco
 */
public abstract class Task extends Thread
{
  protected int terminateValue = 0;
  protected ActionListener terminateAction = null;
  protected Throwable runError = null;
  protected boolean interactive = false;
  protected LongOperListener lol = null;

  public Task()
  {
  }

  public Task(ActionListener terminateAction, LongOperListener lol)
  {
    this.lol = lol;
    this.terminateAction = terminateAction;
  }

  @Override
  final public void run()
  {
    try
    {
      doInBackground();
    }
    catch(Throwable ex)
    {
      runError = ex;
      Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
      if(interactive)
        ErrorDialog.showError("Errore durante l'esecuzione del task.", ex);
    }

    try
    {
      finished();
    }
    catch(Throwable ex)
    {
      Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public abstract void doInBackground()
     throws Exception;

  protected void finished()
     throws Exception
  {
    if(terminateAction != null)
      terminateAction.actionPerformed(new ActionEvent(this, terminateValue,
         runError == null ? null : runError.getMessage()));
  }

  protected void setProgress(long currVal, long minVal, long maxVal)
  {
    if(minVal == maxVal)
      return;

    if(lol != null)
      lol.updateUI(currVal, maxVal);
  }

  public boolean isError()
  {
    return runError != null;
  }

  public Throwable getError()
  {
    return runError;
  }

  public boolean isInteractive()
  {
    return interactive;
  }

  public void setInteractive(boolean interactive)
  {
    this.interactive = interactive;
  }
}
