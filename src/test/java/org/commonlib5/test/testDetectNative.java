/*
 * Copyright (C) 2013 Nicola De Nisco
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
package org.commonlib5.test;

import java.io.File;
import org.commonlib5.exec.NativeLibCheck;

/**
 * Controllo funzione detect native.
 *
 * @author Nicola De Nisco
 */
public class testDetectNative
{
  public static void main(String[] args)
  {
    try
    {
      File nativeLib = new File(args[0]);

      System.out.printf("Detect %X\n", NativeLibCheck.getWindowsExecutableDescriptor(nativeLib));

      if(NativeLibCheck.is32bit(nativeLib))
        System.out.println("Libreria a 32 bit");
      if(NativeLibCheck.is64bit(nativeLib))
        System.out.println("Libreria a 64 bit");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
