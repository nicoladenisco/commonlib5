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

import java.io.StringWriter;
import static junit.framework.TestCase.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test per LongOperListenerPrint.
 *
 * @author Nicola De Nisco
 */
public class LongOperListenerPrintTest
{
  public LongOperListenerPrintTest()
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
  public void testUpdateUI_byte()
  {
    System.out.println("updateUI");
    long part = 0L;
    long total = 100L;
    StringWriter sr = new StringWriter();
    LongOperListenerPrint instance = new LongOperListenerPrint(LongOperListenerPrint.BYTE, sr);
    instance.updateUI(part, total);
    assertEquals(instance.toString(), "LongOperListenerPrint{0/100}");

    sr = new StringWriter();
    instance.updateUI(10, total);
    assertEquals(instance.toString(), "LongOperListenerPrint{10/100}");
  }

  @Test
  public void testUpdateUI_kilo()
  {
    System.out.println("updateUI");
    long part = 0L;
    long total = 100 * 1024L;
    StringWriter sr = new StringWriter();
    LongOperListenerPrint instance = new LongOperListenerPrint(LongOperListenerPrint.KILO_BYTE, sr);
    instance.updateUI(part, total);
    assertEquals(instance.toString(), "LongOperListenerPrint{0K/100K}");

    sr = new StringWriter();
    instance.updateUI(10 * 1024L, total);
    assertEquals(instance.toString(), "LongOperListenerPrint{10K/100K}");
  }

  @Test
  public void testUpdateUI_mega()
  {
    System.out.println("updateUI");
    long part = 0L;
    long total = 100 * 1024L * 1024L;
    StringWriter sr = new StringWriter();
    LongOperListenerPrint instance = new LongOperListenerPrint(LongOperListenerPrint.MEGA_BYTE, sr);
    instance.updateUI(part, total);
    assertEquals(instance.toString(), "LongOperListenerPrint{0M/100M}");

    sr = new StringWriter();
    instance.updateUI(10 * 1024L * 1024L, total);
    assertEquals(instance.toString(), "LongOperListenerPrint{10M/100M}");
  }
}
