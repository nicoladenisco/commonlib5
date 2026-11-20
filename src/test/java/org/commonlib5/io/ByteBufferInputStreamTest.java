/*
 * Copyright (C) 2026 Nicola De Nisco
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.commonlib5.utils.CommonFileUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class ByteBufferInputStreamTest
{

  public ByteBufferInputStreamTest()
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
  public void testEnsureSize()
  {
    System.out.println("ensureSize");
    int alength = 100;
    ByteBufferInputStream instance = new ByteBufferInputStream();
    int expResult = ByteBufferInputStream.BUFFER_SIZE + 100;
    instance.ensureSize(alength);
    assertEquals(expResult, instance.array().length);

    expResult = (ByteBufferInputStream.BUFFER_SIZE * 3) + 1000;
    instance.ensureSize(expResult);
    assertEquals(ByteBufferInputStream.BUFFER_SIZE + expResult, instance.array().length);
  }

  @Test
  public void testAvailable()
  {
    System.out.println("available");
    ByteBufferInputStream instance = new ByteBufferInputStream();
    int expResult = 0;
    int result = instance.available();
    assertEquals(0, result);
  }

  @Test
  public void testAddToBuffer_byteArr()
  {
    System.out.println("addToBuffer");
    String str = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);
    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(toAdd);
    assertEquals(str, instance.toString(StandardCharsets.UTF_8));
    assertEquals(10, instance.available());
  }

  @Test
  public void testAddToBuffer_File()
     throws Exception
  {
    System.out.println("addToBuffer");
    File fAdd = File.createTempFile("data", ".bin");
    String str = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);
    CommonFileUtils.writeFile(fAdd, toAdd);
    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(fAdd);
    assertEquals(str, instance.toString(StandardCharsets.UTF_8));
    assertEquals(10, instance.available());
    fAdd.delete();
  }

  @Test
  public void testAddToBuffer_3args()
  {
    System.out.println("addToBuffer");
    String str = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);
    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(toAdd, 0, 4);
    assertEquals(str.substring(0, 4), instance.toString(StandardCharsets.UTF_8));
    assertEquals(4, instance.available());
  }

  @Test
  public void testRead_3args()
  {
    System.out.println("read");
    String str = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);
    byte[] buffer = new byte[4];
    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(toAdd);
    instance.read(buffer, 0, 4);
    assertEquals(str.substring(0, 4), new String(buffer, StandardCharsets.UTF_8));
    assertEquals(6, instance.available());
  }

  @Test
  public void testSkipPattern()
     throws IOException
  {
    System.out.println("skip");
    String str = "wjkekkqt123985cn012nv012390123456789z3pììllx95,.x-,nfwqeuroi1 23mr321tmo234ony33ngA.MQMELRTxxx13JHN";
    String residuo = "z3pììllx95,.x-,nfwqeuroi1 23mr321tmo234ony33ngA.MQMELRTxxx13JHN";
    String strt = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);

    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(toAdd);
    assertTrue(instance.skipPattern(strt.getBytes()));
    String res = instance.toString(StandardCharsets.UTF_8);
    assertEquals(residuo, res);
  }

  @Test
  public void testUnread_3args()
     throws Exception
  {
    System.out.println("unread");
    String str = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);
    byte[] buffer = new byte[4];
    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(toAdd);
    instance.read(buffer, 0, 4);
    instance.unread(buffer, 0, 4);
    assertEquals(str, instance.toString(StandardCharsets.UTF_8));
  }

  @Test
  public void testUnread_byteArr()
     throws Exception
  {
    System.out.println("unread");
    String str = "0123456789";
    byte[] toAdd = str.getBytes(StandardCharsets.UTF_8);
    byte[] buffer = new byte[4];
    ByteBufferInputStream instance = new ByteBufferInputStream();
    instance.addToBuffer(toAdd);
    instance.read(buffer, 0, 4);
    instance.unread(buffer);
    assertEquals(str, instance.toString(StandardCharsets.UTF_8));
  }
}
