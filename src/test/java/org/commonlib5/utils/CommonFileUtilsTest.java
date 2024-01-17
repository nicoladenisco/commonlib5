/*
 * Copyright (C) 2021 Nicola De Nisco
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
package org.commonlib5.utils;

import java.io.File;

import static junit.framework.Assert.*;

import org.junit.Test;

/**
 * Test per file utility.
 * @author Nicola De Nisco
 */
public class CommonFileUtilsTest
{
  @Test
  public void test1()
     throws Exception
  {
    File dirTmp = OsIdent.getSystemTemp();
    File test1 = new File(dirTmp, "primo.txt");
    File test2 = new File(dirTmp, "secondo.txt");
    CommonFileUtils.writeFileTxt(test1, "primo file\n", "UTF-8");
    CommonFileUtils.writeFileTxt(test2, "secondo file\n", "UTF-8");
  }

  @Test
  public void test2()
     throws Exception
  {
    File dirTmp = OsIdent.getSystemTemp();
    File test1 = new File(dirTmp, "primo.txt");
    File test2 = new File(dirTmp, "secondo.txt");

    assertTrue(CommonFileUtils.isFilesEquals(test1, test1));
    assertFalse(CommonFileUtils.isFilesEquals(test1, test2));

    assertFalse(CommonFileUtils.copyFileIfDifferent(test1, test1));
    assertTrue(CommonFileUtils.copyFileIfDifferent(test1, test2));
  }
}
