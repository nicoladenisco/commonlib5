/*
 *  Task.java
 *
 *  Creato il 20-mar-2012, 16.29.14
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
