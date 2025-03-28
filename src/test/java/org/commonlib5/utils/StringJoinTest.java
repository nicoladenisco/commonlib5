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
package org.commonlib5.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static junit.framework.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test per la classe StringJoin.
 *
 * @author Nicola De Nisco
 */
public class StringJoinTest
{

  public StringJoinTest()
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

  /**
   * Test of build method, of class StringJoin.
   */
  @Test
  public void testBuild_0args()
  {
    System.out.println("build");
    StringJoin expResult = null;
    StringJoin result = StringJoin.build();
    assertEquals(",", result.getSeparatore());
    assertEquals(null, result.getDelimitatoreInizio());
  }

  /**
   * Test of buildForSQL method, of class StringJoin.
   */
  @Test
  public void testBuildForSQL_intArr()
  {
    System.out.println("buildForSQL");
    int[] cs =
    {
      1, 2, 3, 4, 5
    };
    String expResult = "1,2,3,4,5";
    String result = StringJoin.buildForSQL(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of buildForSQL method, of class StringJoin.
   */
  @Test
  public void testBuildForSQL_longArr()
  {
    System.out.println("buildForSQL");
    long[] cs =
    {
      1, 2, 3, 4, 5
    };
    String expResult = "1,2,3,4,5";
    String result = StringJoin.buildForSQL(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of buildForSQL method, of class StringJoin.
   */
  @Test
  public void testBuildForSQL_StringArr()
  {
    System.out.println("buildForSQL");
    String[] cs =
    {
      "uno", "due", "tre"
    };
    String expResult = "'uno','due','tre'";
    String result = StringJoin.buildForSQL(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_StringArr()
  {
    System.out.println("add");
    String[] cs =
    {
      "uno", "due", "tre"
    };
    String expResult = "'uno','due','tre'";
    String result = StringJoin.buildForSQL(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_intArr()
  {
    System.out.println("add");
    int[] cs =
    {
      1, 2, 3, 4, 5
    };
    String expResult = "1,2,3,4,5";
    String result = StringJoin.buildForSQL(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_longArr()
  {
    System.out.println("add");
    long[] cs =
    {
      1, 2, 3, 4, 5
    };
    String expResult = "1,2,3,4,5";
    String result = StringJoin.buildForSQL(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_ObjectArr()
  {
    System.out.println("add");
    Object[] cs =
    {
      1, 2, 3, 4, 5
    };
    String expResult = "1,2,3,4,5";
    String result = StringJoin.build().add(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_Collection()
  {
    System.out.println("add");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "1,2,3,4,5";
    String result = StringJoin.build().add(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_Collection_Function()
  {
    System.out.println("add");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "01,02,03,04,05";
    String result = StringJoin.build().add(cs, (s) -> "0" + s).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of add method, of class StringJoin.
   */
  @Test
  public void testAdd_Collection_FunctionTrowException()
     throws Exception
  {
    System.out.println("addEx");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "01,02,03,04,05";
    String result = StringJoin.build().addEx(cs, (s) -> "0" + s).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of addObjects method, of class StringJoin.
   */
  @Test
  public void testAddObjects_Collection()
  {
    System.out.println("addObjects");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "1,2,3,4,5";
    String result = StringJoin.build().addObjects(cs).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of addObjects method, of class StringJoin.
   */
  @Test
  public void testAddObjects_Collection_Function()
  {
    System.out.println("addObjects");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "01,02,03,04,05";
    String result = StringJoin.build().addObjects(cs, (s) -> "0" + s).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of addObjects method, of class StringJoin.
   */
  @Test
  public void testAddObjects_Collection_FunctionTrowException()
     throws Exception
  {
    System.out.println("addObjectsEx");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "01,02,03,04,05";
    String result = StringJoin.build().addObjectsEx(cs, (s) -> "0" + s).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of addObjects method, of class StringJoin.
   */
  @Test
  public void testAddObjects_Stream()
  {
    System.out.println("addObjects");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "1,2,3,4,5";
    String result = StringJoin.build().addObjects(cs.stream()).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of addObjects method, of class StringJoin.
   */
  @Test
  public void testAddObjects_Stream_Function()
  {
    System.out.println("addObjects");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "01,02,03,04,05";
    String result = StringJoin.build().addObjects(cs.stream(), (s) -> "0" + s).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of addObjects method, of class StringJoin.
   */
  @Test
  public void testAddObjects_Stream_FunctionTrowException()
     throws Exception
  {
    System.out.println("addObjectsEx");
    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    String expResult = "01,02,03,04,05";
    String result = StringJoin.build().addObjectsEx(cs.stream(), (s) -> "0" + s).join();
    assertEquals(expResult, result);
  }

  /**
   * Test of distinct method, of class StringJoin.
   */
  @Test
  public void testDistinct()
  {
    System.out.println("distinct");
    Collection<String> cs = Arrays.asList("3", "1", "1", "2", "2");
    String expResult = "1,2,3";
    String result = StringJoin.build().addObjects(cs).distinct().join();
    assertEquals(expResult, result);
  }

  /**
   * Test of copyFrom method, of class StringJoin.
   */
  @Test
  public void testCopyFrom()
  {
    System.out.println("copyFrom");
    StringJoin origin = StringJoin.build().add(1, 2, 3, 4, 5);
    StringJoin instance = StringJoin.build();
    StringJoin result = instance.copyFrom(origin);
    assertEquals(origin, instance);
  }

  /**
   * Test of clone method, of class StringJoin.
   */
  @Test
  public void testClone()
     throws Exception
  {
    System.out.println("clone");
    StringJoin instance = StringJoin.build().add(1, 2, 3, 4, 5);
    StringJoin result = (StringJoin) instance.clone();
    assertEquals(instance, result);
  }

  /**
   * Test of clone method, of class StringJoin.
   */
  @Test
  public void testJoinCommand()
     throws Exception
  {
    System.out.println("joinCommand");
    String result = StringJoin.build().add("aa", "b b", "c  c", "dd").joinCommand();
    String expected = "aa \"b b\" \"c  c\" dd";
    assertEquals(expected, result);
  }

  @Test
  public void testOverall()
     throws Exception
  {
    System.out.println("overall");
    StringJoin sj = new StringJoin(",");
    assertEquals("", sj.join());

    sj.add(1, 2, 3, 4, 5);
    assertEquals("1,2,3,4,5", sj.join());

    Collection<String> cs = Arrays.asList("1", "2", "3", "4", "5");
    sj.add(cs);
    assertEquals("1,2,3,4,5,1,2,3,4,5", sj.join());

    List<Pair<Integer, Long>> lsObj = new ArrayList<>();
    lsObj.add(new Pair<>(0, 10L));
    lsObj.add(new Pair<>(1, 11L));
    lsObj.add(new Pair<>(2, 12L));
    lsObj.add(new Pair<>(3, 13L));
    lsObj.add(new Pair<>(4, 14L));

    sj.clear();
    sj.setSeparatore("/");
    sj.addObjects(lsObj);
    assertEquals("0=10/1=11/2=12/3=13/4=14", sj.join());

    sj.clear();
    sj.setSeparatore("-");
    sj.addObjects(lsObj, (p) -> Integer.toString(p.first));
    assertEquals("0-1-2-3-4", sj.join());

    sj.clear();
    sj.setSeparatore("-");
    sj.addObjects(lsObj.stream(), (p) -> Long.toString(p.second));
    assertEquals("10-11-12-13-14", sj.join());

    sj.clear();
    sj.setSeparatore("-");
    sj.addObjects(lsObj.stream().filter((p) -> p.second > 10), (p) -> Long.toString(p.second));
    assertEquals("11-12-13-14", sj.join());

    sj.clear();
    sj.setSeparatore("-");
    sj.setDelimitatoreInizio("(");
    sj.setDelimitatoreFine(")");
    sj.addObjects(lsObj.stream().filter((p) -> p.second > 10), (p) -> Long.toString(p.second));
    assertEquals("(11)-(12)-(13)-(14)", sj.join());
  }
}
