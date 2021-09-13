/*
 * ProcessWatchListner.java
 *
 * Created on 21-gen-2009, 16.51.47
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */

package org.commonlib5.exec;

/**
 * Interfaccia per ProcessWatch.
 *
 * @author Nicola De Nisco
 */
public interface ProcessWatchListner
{
  public void notifyStdout(byte[] output, int offset, int length);
  public void notifyStderr(byte[] output, int offset, int length);
}
