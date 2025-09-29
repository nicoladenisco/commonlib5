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
package org.commonlib5.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.List;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test per FileScanner.
 *
 * @author Nicola De Nisco
 */
public class FileScannerTest
{
  public static final String NOME_FILE_TEST = "FileScannerTest.testo";
  public static final String ESTENSIONE_TEST = "*.testo";

  public FileScannerTest()
  {
  }

  @BeforeClass
  public static void setUpClass()
  {
    File fDir = new File("/tmp/aaa");
    fDir.mkdirs();

    File testo = new File(fDir, NOME_FILE_TEST);
    try(FileOutputStream fos = new FileOutputStream(testo))
    {
      String msg = "questo è un testo di prova.\n";
      fos.write(msg.getBytes("UTF-8"));
    }
    catch(Exception ex)
    {
      throw new RuntimeException(ex);
    }
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

  public static boolean checkResult(File result)
  {
    String path = "/tmp/aaa/" + NOME_FILE_TEST;
    return path.equals(result.getAbsolutePath());
  }

  public static boolean checkList(List<File> result)
  {
    String path = "/tmp/aaa/" + NOME_FILE_TEST;
    return path.equals(result.get(0).getAbsolutePath());
  }

  @Test
  public void testScan_File_int()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    List<File> result = FileScanner.scan(fDir, maxLev);
    assertTrue(!result.isEmpty());
  }

  @Test
  public void testScan_3args_1()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    FileFilter ff = (f) -> NOME_FILE_TEST.equals(f.getName());
    List<File> result = FileScanner.scan(fDir, maxLev, ff);
    assertTrue(result.size() == 1);
    assertTrue(checkList(result));
  }

  @Test
  public void testScan_3args_2()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    FilenameFilter fn = (dir, name) -> NOME_FILE_TEST.equals(name);
    List<File> result = FileScanner.scan(fDir, maxLev, fn);
    assertTrue(result.size() == 1);
    assertTrue(checkList(result));
  }

  @Test
  public void testScan_3args_3()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    String wildCard = ESTENSIONE_TEST;
    List<File> result = FileScanner.scan(fDir, maxLev, wildCard);
    assertTrue(result.size() == 1);
    assertTrue(checkList(result));
  }

  @Test
  public void testScan_3args_4()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    WildcardFileFilter filter = new WildcardFileFilter(ESTENSIONE_TEST);
    List<File> result = FileScanner.scan(fDir, maxLev, filter);
    assertTrue(result.size() == 1);
    assertTrue(checkList(result));
  }

  @Test
  public void testScan_4args_1()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    List<File> result = FileScanner.scan(fDir, maxLev, ESTENSIONE_TEST, true);
    assertTrue(result.size() == 1);
    assertTrue(checkList(result));
  }

  @Test
  public void testScan_4args_2()
  {
    System.out.println("scan");
    File fDir = new File("/tmp");
    int maxLev = 9;
    WildcardFileFilter filter = new WildcardFileFilter(ESTENSIONE_TEST);
    List<File> result = FileScanner.scan(fDir, maxLev, filter, true);
    assertTrue(result.size() == 1);
    assertTrue(checkList(result));
  }

  @Test
  public void testFileExist_File_String()
  {
    System.out.println("fileExist");
    File fDir = new File("/tmp");
    String wildCard = ESTENSIONE_TEST;
    boolean expResult = true;
    boolean result = FileScanner.fileExist(fDir, wildCard);
    assertEquals(expResult, result);
  }

  @Test
  public void testFileExist_3args()
  {
    System.out.println("fileExist");
    File fDir = new File("/tmp");
    String wildCard = ESTENSIONE_TEST;
    boolean expResult = true;
    int maxLevel = 9;
    boolean result = FileScanner.fileExist(fDir, maxLevel, wildCard);
    assertEquals(expResult, result);
  }

  @Test
  public void testFindFirst()
  {
    System.out.println("findFirst");
    File fDir = new File("/tmp");
    int maxLev = 9;
    String wildCard = ESTENSIONE_TEST;
    File result = FileScanner.findFirst(fDir, maxLev, wildCard);
    assertTrue(checkResult(result));
  }

  @Test
  public void testFindDir()
  {
    System.out.println("findDir");
    File dirOrigin = new File("/tmp");
    int maxLev = 9;
    String nomeDirToFind = "aaa";
    File result = FileScanner.findDir(dirOrigin, maxLev, nomeDirToFind);
    assertTrue("/tmp/aaa".equals(result.getAbsolutePath()));
  }

  @Test
  public void testScanDirectory()
  {
    System.out.println("scanDirectory");
    File fDir = new File("/tmp");
    int maxLev = 9;
    FilenameFilter fn = (dir, name) -> "aaa".equals(name);
    List<File> result = FileScanner.scanDirectory(fDir, maxLev, fn);
    assertTrue(result.size() == 1);
    assertTrue("/tmp/aaa".equals(result.get(0).getAbsolutePath()));
  }
}
