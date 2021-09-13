/*
 * Copyright (C) 2012 Nicola De Nisco
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
package org.commonlib5.comunication.port;

import java.io.*;

/**
 * Porta su file system.
 *
 * @author Nicola De Nisco
 */
public class FileSystemPort extends AbstractPort
{
  protected File inFile, outFile;
  protected byte[] inputData = null;

  @Override
  public void initComm(String sData)
     throws Exception
  {
    super.initComm(sData);

    // imposta gli stream di input/output
    if(inputData != null)
      setStream(
         new ByteArrayInputStream(inputData),
         new FileOutputStream(outFile));
    else
      setStream(
         new FileInputStream(inFile),
         new FileOutputStream(outFile));
  }

  public void setParams(File inFile, File outFile)
  {
    this.inFile = inFile;
    this.outFile = outFile;
  }

  public void setParams(byte[] inputData, File outFile)
  {
    this.inputData = inputData;
    this.outFile = outFile;
  }
}
