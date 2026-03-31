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
package org.commonlib5.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test per JsonHelper.
 *
 * @author Nicola De Nisco
 */
public class JsonHelperTest
{
  public JsonHelperTest()
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
  public void testJavaPatchWorkaround()
     throws Exception
  {
    System.out.println("JsonHelperTest/javaPatchWorkaround");
    JsonHelper.javaPatchWorkaround();
    assertTrue(JsonHelper.javaPatchApplied);

    Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
    methodsField.setAccessible(true);
    // get the methods field modifiers
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    // bypass the "private" modifier
    modifiersField.setAccessible(true);
    // remove the "final" modifier
    modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

    // recupera elenco metodi che deve apparire modificato
    String[] methods = (String[]) methodsField.get(null);
    assertTrue(ArrayUtils.contains(methods, "PATCH"));
  }
}
