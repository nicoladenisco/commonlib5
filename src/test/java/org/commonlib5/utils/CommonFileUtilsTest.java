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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.commonlib5.io.ByteBufferInputStream;
import org.commonlib5.io.ByteBufferOutputStream;
import static org.commonlib5.utils.CommonFileUtils.copyStream;
import static org.commonlib5.utils.CommonFileUtils.writeObjectToBytes;
import static org.junit.Assert.*;
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
    System.out.println("CommonFileUtilsTest:test1");
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
    System.out.println("CommonFileUtilsTest:test2");
    File dirTmp = OsIdent.getSystemTemp();
    File test1 = new File(dirTmp, "primo.txt");
    File test2 = new File(dirTmp, "secondo.txt");

    assertTrue(CommonFileUtils.isFilesEquals(test1, test1));
    assertFalse(CommonFileUtils.isFilesEquals(test1, test2));

    assertFalse(CommonFileUtils.copyFileIfDifferent(test1, test1));
    assertTrue(CommonFileUtils.copyFileIfDifferent(test1, test2));
  }

  @Test
  public void test3()
     throws Exception
  {
    System.out.println("CommonFileUtilsTest:test3");
    String s = buildLargeString();

    byte[] array = CommonFileUtils.writeObjectToBytes(s);
    System.out.println("test3: array.length " + array.length);
    String o = (String) CommonFileUtils.readObjectFromBytes(array);
    assertEquals(s, o);
  }

  public String buildLargeString()
  {
    StringBuilder rv = new StringBuilder(1024);
    for(int i = 0; i < 100; i++)
      rv.append("tanto va la gatta al lardo che ci lascia lo zampino.");
    return rv.toString();
  }

  @Test
  public void test31()
     throws Exception
  {
    System.out.println("CommonFileUtilsTest:test31");
    String s = buildLargeString();

    byte[] nonCompressi = writeObjectToBytes(s);
    ByteBufferOutputStream fzs = new ByteBufferOutputStream();
    try (GZIPOutputStream zipStream = new GZIPOutputStream(fzs);)
    {
      zipStream.write(nonCompressi);
    }

    ByteBufferInputStream fis = new ByteBufferInputStream(false, fzs.getBytes());
    ByteBufferOutputStream fos = new ByteBufferOutputStream();
    try (GZIPInputStream zipStream = new GZIPInputStream(fis);)
    {
      copyStream(zipStream, fos);
    }

    assertArrayEquals(nonCompressi, fos.getBytes());
  }

  @Test
  public void test4()
     throws Exception
  {
    System.out.println("CommonFileUtilsTest:test4");
    String s = buildLargeString();

    byte[] array = CommonFileUtils.writeObjectToBytesZipped(s);
    System.out.println("test4: array.length " + array.length);
    String o = (String) CommonFileUtils.readObjectFromBytesZipped(array);
    assertEquals(s, o);
  }
}
