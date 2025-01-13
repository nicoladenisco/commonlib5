/*
 * Copyright (C) 2015 Nicola De Nisco
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
import org.commonlib5.utils.OsIdent;

/**
 * Test di varie funzioni.
 *
 * @author Nicola De Nisco
 */
public class testMisc
{
  public static void main(String args[])
  {
    try
    {
      testOsIdent();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private static void testOsIdent()
  {
    String os = (System.getProperty("os.name")).toLowerCase();

    System.out.println("OS descr=" + os);
    System.out.println("OS arch=" + System.getProperty("sun.arch.data.model"));

    System.out.println("OS=" + OsIdent.checkOStype() + " " + OsIdent.getSystemDescr());

    File appDir = OsIdent.getAppDirectory("appnameTest");
    File cacDir = OsIdent.getCacheDirectory("appnameTest");

    System.out.println("APPDIR=" + appDir.getAbsolutePath());
    System.out.println("CACHEDIR=" + cacDir.getAbsolutePath());

    appDir.delete();
    cacDir.delete();
  }
}
