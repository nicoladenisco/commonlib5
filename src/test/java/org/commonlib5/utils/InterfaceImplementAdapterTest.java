/*
 * Copyright (C) 2023 Nicola De Nisco
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

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class InterfaceImplementAdapterTest
{

  public InterfaceImplementAdapterTest()
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
  public void testAll()
  {
    InterfaceImplementAdapter<TestStringJoiner> ii = new InterfaceImplementAdapter<>(TestStringJoiner.class);
    ii.addMethod("joinString", (args) -> joinStringLocal(args));
    TestStringJoiner proxy = ii.createAdapter();

    String sjoin = proxy.joinString("prima", "seconda");
    Assert.assertEquals("", sjoin, "[prima, seconda]");
  }

  private Object joinStringLocal(Object[] args)
  {
    return Arrays.toString(args);
  }

  public interface TestStringJoiner
  {
    public String joinString(String s1, String s2);
  }
}
