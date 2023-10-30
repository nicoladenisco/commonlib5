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
package org.commonlib5.gui.validator;

import java.util.Date;
import org.commonlib5.utils.DateTime;
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
public class ItalianParserTest
{

  public ItalianParserTest()
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
   * Test of fmtDate method, of class ItalianParser.
   */
  @Test
  public void testFmtDate()
  {
    System.out.println("fmtDate");
    Date d = new Date(66, 7, 27, 14, 30, 15);
    ItalianParser instance = new ItalianParser();
    String expResult = "27/08/1966";
    String result = instance.fmtDate(d);
    assertEquals(expResult, result);
  }

  /**
   * Test of fmtDateTime method, of class ItalianParser.
   */
  @Test
  public void testFmtDateTime()
  {
    System.out.println("fmtDateTime");
    Date d = new Date(66, 7, 27, 14, 30, 15);
    ItalianParser instance = new ItalianParser();
    String expResult = "27/08/1966 14:30:15";
    String result = instance.fmtDateTime(d);
    assertEquals(expResult, result);
  }

  /**
   * Test of fmtTime method, of class ItalianParser.
   */
  @Test
  public void testFmtTime()
  {
    System.out.println("fmtTime");
    Date d = new Date(66, 7, 27, 14, 30, 15);
    ItalianParser instance = new ItalianParser();
    String expResult = "14:30:15";
    String result = instance.fmtTime(d);
    assertEquals(expResult, result);
  }

  /**
   * Test of parseDate method, of class ItalianParser.
   */
  @Test
  public void testParseDate_String_Date()
  {
    System.out.println("parseDate");
    String s = "27/08/1966 14:30:15";
    Date defVal = null;
    ItalianParser instance = new ItalianParser();
    Date expResult = new Date(66, 7, 27, 14, 30, 15);
    Date result = instance.parseDate(s, defVal);
    assertEquals(expResult, result);
  }

  @Test
  public void testParseDate1()
  {
    System.out.println("parseDate1");
    String s = "27/08/1966 14:30:15";
    Date defVal = null;
    int flags = 0;
    ItalianParser instance = new ItalianParser();
    Date expResult = new Date(66, 7, 27, 14, 30, 15);
    Date result = instance.parseDate(s, defVal, flags);
    assertEquals(expResult, result);
  }

  @Test
  public void testParseDate2()
  {
    System.out.println("parseDate2");
    String s = "27/08/1966 14:30:15";
    Date expResult = new Date(66, 7, 27, 14, 30, 15);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate3()
  {
    System.out.println("parseDate3");
    String s = "27/08/1966";
    Date expResult = new Date(66, 7, 27, 14, 30, 15);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate4()
  {
    System.out.println("parseDate4");
    String s = "oggi";
    Date expResult = DateTime.mergeDataOra(new Date(), 14, 30, 15);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate5()
  {
    System.out.println("parseDate5");
    String s = "oggi 14:30:15";
    Date expResult = DateTime.mergeDataOra(new Date(), 14, 30, 15);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate6()
  {
    System.out.println("parseDate6");
    String s = "oggi 143015";
    Date expResult = DateTime.mergeDataOra(new Date(), 14, 30, 15);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate7()
  {
    System.out.println("parseDate7");
    String s = "oggi 1430";
    Date expResult = DateTime.mergeDataOra(new Date(), 14, 30, 0);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate8()
  {
    System.out.println("parseDate8");
    String s = "ieri 143015";
    Date expResult = DateTime.mergeDataOra(DateTime.dataSpiazzata(null, -1), 14, 30, 15);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }

  @Test
  public void testParseDate9()
  {
    System.out.println("parseDate9");
    String s = "ieri 1430";
    Date expResult = DateTime.mergeDataOra(DateTime.dataSpiazzata(null, -1), 14, 30, 0);
    Date defVal = null;
    Date bd = DateTime.mergeDataOra(expResult, 0, 0, 0, 0);
    Date ed = DateTime.mergeDataOra(expResult, 23, 59, 59, 999);
    ItalianParser instance = new ItalianParser();

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ROUND_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(expResult, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_BEGIN_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(bd, result);
    }

    {
      int flags = ValidatorParserInterface.FLAG_ALWAIS_END_DAY;
      Date result = instance.parseDate(s, defVal, flags);
      assertEquals(ed, result);
    }
  }
}
