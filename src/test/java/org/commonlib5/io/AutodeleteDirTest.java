/*
 * Copyright (C) 2024 Nicola De Nisco
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
import static junit.framework.Assert.*;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.OsIdent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class AutodeleteDirTest
{

  public AutodeleteDirTest()
  {
  }

  @BeforeClass
  public static void setUpClass()
  {
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp()
  {
  }

  @After
  public void tearDown()
  {
  }

  @Test
  public void test1()
     throws Exception
  {
    System.out.println("test1");

    File dir = null, testFile = null;
    String testo = "Questo è un testo di prova.";
    String encoding = "UTF-8";

    try(AutodeleteDir instance = new AutodeleteDir())
    {
      dir = instance.getDir();
      testFile = new File(dir, "prova.txt");
      CommonFileUtils.writeFileTxt(testFile, testo, encoding);
      assertEquals(testo, CommonFileUtils.readFileTxt(testFile, encoding));
    }

    if(dir.exists() || testFile.exists())
      fail("La directory e/o il suo contenuto non sono stati cancellati.");
  }

  @Test
  public void test2()
     throws Exception
  {
    System.out.println("test2");

    File testFile = null;
    String testo = "Questo è un testo di prova.";
    String encoding = "UTF-8";

    File dir = new File(OsIdent.getSystemTemp(), "dirprova");
    assertTrue(dir.mkdirs());

    try(AutodeleteDir instance = new AutodeleteDir(dir, true))
    {
      dir = instance.getDir();
      testFile = new File(dir, "prova.txt");
      CommonFileUtils.writeFileTxt(testFile, testo, encoding);
      assertEquals(testo, CommonFileUtils.readFileTxt(testFile, encoding));
    }

    if(dir.exists() || testFile.exists())
      fail("La directory e/o il suo contenuto non sono stati cancellati.");
  }
}
