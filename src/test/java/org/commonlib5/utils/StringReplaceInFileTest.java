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
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static junit.framework.TestCase.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test per StringReplaceInFile.
 *
 * @author Nicola De Nisco
 */
public class StringReplaceInFileTest
{

  public StringReplaceInFileTest()
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

  private String preparaFiles(File vecchioFile)
     throws Exception
  {
    String testo
       = "     * Opens or creates a file for writing, returning a {@code BufferedWriter}\n"
       + "     * to write text to the file in an efficient manner. The text is encoded\n"
       + "     * into bytes for writing using the {@link StandardCharsets#UTF_8 UTF-8}\n"
       + "     * {@link Charset charset}.\n"
       + "     *\n";
    String aspettato
       = "     * Apri or creates a file for writxxg, returnxxg a {@code BufferedWriter}\n"
       + "     * to write text to the file xx an efficient manner. The text is encoded\n"
       + "     * xxto bytes for writxxg usxxg the {@lxxk StandardCharsets#UTF_8 UTF-8}\n"
       + "     * {@lxxk Charset charset}.\n"
       + "     *\n";

    CommonFileUtils.writeFileTxt(vecchioFile, testo, "UTF-8");
    return aspettato;
  }

  @Test
  public void testReplaceByLine()
     throws Exception
  {
    System.out.println("replaceByLine");

    File tmp = OsIdent.getSystemTemp();
    File vecchioFile = new File(tmp, "input.txt");
    File nuovoFile = new File(tmp, "output.txt");

    String aspettato = preparaFiles(vecchioFile);
    StringReplaceInFile instance = new StringReplaceInFile(vecchioFile, StandardCharsets.UTF_8);
    instance.addSubstituion("Opens", "Apri");
    instance.addSubstituion("in", "xx");
    instance.replaceByLine(nuovoFile, StandardCharsets.UTF_8);

    String contenuto = CommonFileUtils.readFileTxt(nuovoFile, "UTF-8");
    assertEquals(aspettato, contenuto);
  }

  @Test
  public void testReplaceInMemory()
     throws Exception
  {
    System.out.println("replaceInMemory");

    File tmp = OsIdent.getSystemTemp();
    File vecchioFile = new File(tmp, "input.txt");
    File nuovoFile = new File(tmp, "output.txt");

    String aspettato = preparaFiles(vecchioFile);
    StringReplaceInFile instance = new StringReplaceInFile(vecchioFile, StandardCharsets.UTF_8);
    instance.addSubstituion("Opens", "Apri");
    instance.addSubstituion("in", "xx");
    instance.replaceInMemory(nuovoFile, StandardCharsets.UTF_8);

    String contenuto = CommonFileUtils.readFileTxt(nuovoFile, "UTF-8");
    assertEquals(aspettato, contenuto);
  }

  @Test
  public void testReplaceByLineStream()
     throws Exception
  {
    System.out.println("replaceByLineStream");

    File tmp = OsIdent.getSystemTemp();
    File vecchioFile = new File(tmp, "input.txt");
    File nuovoFile = new File(tmp, "output.txt");

    String aspettato = preparaFiles(vecchioFile);
    try(InputStream is = new FileInputStream(vecchioFile))
    {
      StringReplaceInFile instance = new StringReplaceInFile(is, StandardCharsets.UTF_8);
      instance.addSubstituion("Opens", "Apri");
      instance.addSubstituion("in", "xx");
      instance.replaceByLine(nuovoFile, StandardCharsets.UTF_8);
    }

    String contenuto = CommonFileUtils.readFileTxt(nuovoFile, "UTF-8");
    assertEquals(aspettato, contenuto);
  }

  @Test
  public void testReplaceInMemoryStream()
     throws Exception
  {
    System.out.println("replaceInMemoryStream");

    File tmp = OsIdent.getSystemTemp();
    File vecchioFile = new File(tmp, "input.txt");
    File nuovoFile = new File(tmp, "output.txt");

    String aspettato = preparaFiles(vecchioFile);
    try(InputStream is = new FileInputStream(vecchioFile))
    {
      StringReplaceInFile instance = new StringReplaceInFile(is, StandardCharsets.UTF_8);
      instance.addSubstituion("Opens", "Apri");
      instance.addSubstituion("in", "xx");
      instance.replaceInMemory(nuovoFile, StandardCharsets.UTF_8);
    }

    String contenuto = CommonFileUtils.readFileTxt(nuovoFile, "UTF-8");
    assertEquals(aspettato, contenuto);
  }
}
