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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class JsonToCollectionTest
{

  public JsonToCollectionTest()
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

  public static void assertEqualsMap(Map expected, Map actual)
  {
    expected.forEach((k, v) -> assertEquals("key " + k, v, actual.get(k)));
  }

  public static void assertEqualsList(List expected, List actual)
  {
    expected.forEach((v) -> assertTrue(actual.contains(v)));
  }

  @Test
  public void testConvert()
  {
    System.out.println("convert");
    JSONObject value = new JSONObject();
    value.put("num", 1);
    value.put("double", 1.0);
    value.put("String", "stringa");
    JsonToCollection instance = new JsonToCollection();
    Map expResult = ArrayOper.asMapFromPair(
       "num", 1,
       "double", 1.0,
       "String", "stringa"
    );
    Map result = (Map) instance.convert(value);
    assertEqualsMap(expResult, result);
  }

  @Test
  public void testConvertString()
  {
    System.out.println("convertString");
    JSONObject value = new JSONObject();
    value.put("num", 1);
    value.put("double", 1.0);
    value.put("String", "stringa");
    JsonToCollection instance = new JsonToCollection();
    Map expResult = ArrayOper.asMapFromPair(
       "num", "1",
       "double", "1.0",
       "String", "stringa"
    );
    Map result = (Map) instance.convertString(value);
    assertEqualsMap(expResult, result);
  }

  @Test
  public void testConvertJson2Map()
  {
    System.out.println("convertJson2Map");
    JSONObject value = new JSONObject();
    value.put("num", 1);
    value.put("double", 1.0);
    value.put("String", "stringa");
    JsonToCollection instance = new JsonToCollection();
    Map expResult = ArrayOper.asMapFromPair(
       "num", 1,
       "double", 1.0,
       "String", "stringa"
    );
    Map result = (Map) instance.convertJson2Map(value);
    assertEqualsMap(expResult, result);
  }

  @Test
  public void testConvertJson2List()
  {
    System.out.println("convertJson2List");
    JSONArray jarr = new JSONArray();
    jarr.put(1);
    jarr.put(2);
    jarr.put(3);
    JsonToCollection instance = new JsonToCollection();
    List<Object> expResult = Arrays.asList(1, 2, 3);
    List<Object> result = instance.convertJson2List(jarr);
    assertEqualsList(expResult, result);
  }

  @Test
  public void testConvertJson2MapString()
  {
    System.out.println("convertJson2MapString");
    JSONObject value = new JSONObject();
    value.put("num", 1);
    value.put("double", 1.0);
    value.put("String", "stringa");
    JsonToCollection instance = new JsonToCollection();
    Map expResult = ArrayOper.asMapFromPair(
       "num", "1",
       "double", "1.0",
       "String", "stringa"
    );
    Map result = (Map) instance.convertJson2MapString(value);
    assertEqualsMap(expResult, result);
  }

  @Test
  public void testConvertJson2ListString()
  {
    JSONArray jarr = new JSONArray();
    jarr.put(1);
    jarr.put(2);
    jarr.put(3);
    JsonToCollection instance = new JsonToCollection();
    List<String> expResult = Arrays.asList("1", "2", "3");
    List<String> result = instance.convertJson2ListString(jarr);
    assertEqualsList(expResult, result);
  }
}
