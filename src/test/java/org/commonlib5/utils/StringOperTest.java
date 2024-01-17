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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test per StringOper.
 *
 * @author Nicola De Nisco
 */
public class StringOperTest
{

  public StringOperTest()
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
  public void testGetSpaces()
  {
    System.out.println("GetSpaces");
    int len = 5;
    String expResult = "     ";
    String result = StringOper.GetSpaces(len);
    assertEquals(expResult, result);
  }

  @Test
  public void testGetZeroes()
  {
    System.out.println("GetZeroes");
    int len = 5;
    String expResult = "00000";
    String result = StringOper.GetZeroes(len);
    assertEquals(expResult, result);
  }

  @Test
  public void testGetFixedString_int_int()
  {
    System.out.println("GetFixedString");
    int ch = ' ';
    int sz = 5;
    String expResult = "     ";
    String result = StringOper.GetFixedString(ch, sz);
    assertEquals(expResult, result);
  }

  @Test
  public void testGetFixedChars()
  {
    System.out.println("GetFixedChars");
    int ch = '0';
    int sz = 3;
    char[] expResult =
    {
      '0', '0', '0'
    };
    char[] result = StringOper.GetFixedChars(ch, sz);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetSpacesChars()
  {
    System.out.println("GetSpacesChars");
    int len = 3;
    char[] expResult =
    {
      ' ', ' ', ' '
    };
    char[] result = StringOper.GetSpacesChars(len);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetZeroesChars()
  {
    System.out.println("GetZeroesChars");
    int len = 3;
    char[] expResult =
    {
      '0', '0', '0'
    };
    char[] result = StringOper.GetZeroesChars(len);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetFixedString_String_int()
  {
    System.out.println("GetFixedString");
    String val = "AA";
    int fixLen = 5;
    String expResult = "AA   ";
    String result = StringOper.GetFixedString(val, fixLen);
    assertEquals(expResult, result);
  }

  @Test
  public void testGetFixedString_3args()
  {
    System.out.println("GetFixedString");
    String s = "AA";
    int sz = 5;
    String resultL = StringOper.GetFixedString(s, sz, StringOper.ALIGN_LEFT);
    String resultC = StringOper.GetFixedString(s, sz, StringOper.ALIGN_CENTER);
    String resultR = StringOper.GetFixedString(s, sz, StringOper.ALIGN_RIGHT);
    assertEquals("AA   ", resultL);
    assertEquals(" AA  ", resultC);
    assertEquals("   AA", resultR);
  }

  @Test
  public void testGetZeroFixedString_int_int()
  {
    System.out.println("GetZeroFixedString");
    int val = 15;
    int fixLen = 5;
    String expResult = "00015";
    String result = StringOper.GetZeroFixedString(val, fixLen);
    assertEquals(expResult, result);
  }

  @Test
  public void testGetZeroFixedString_String_int()
  {
    System.out.println("GetZeroFixedString");
    String val = "AA";
    int fixLen = 5;
    String expResult = "000AA";
    String result = StringOper.GetZeroFixedString(val, fixLen);
    assertEquals(expResult, result);
  }

  @Test
  public void testGetZeroFixedString_3args()
  {
    System.out.println("GetZeroFixedString");
    String s = "AA";
    int sz = 5;

    String resultL = StringOper.GetZeroFixedString(s, sz, StringOper.ALIGN_LEFT);
    String resultC = StringOper.GetZeroFixedString(s, sz, StringOper.ALIGN_CENTER);
    String resultR = StringOper.GetZeroFixedString(s, sz, StringOper.ALIGN_RIGHT);
    assertEquals("AA000", resultL);
    assertEquals("0AA00", resultC);
    assertEquals("000AA", resultR);
  }

  @Test
  public void testCvtJavascriptString()
  {
    System.out.println("CvtJavascriptString");
    String s = "questa e' una stringa";
    String expResult = "questa e\\' una stringa";
    String result = StringOper.CvtJavascriptString(s);
    assertEquals(expResult, result);
  }

  @Test
  public void testCvtSQLstring()
  {
    System.out.println("CvtSQLstring");
    String s = "questa e' una \" stringa";
    String expResult = "questa e'' una \\\" stringa";
    String result = StringOper.CvtSQLstring(s);
    assertEquals(expResult, result);
  }

  @Test
  public void testCvtWEBstring()
  {
    System.out.println("CvtWEBstring");
    String s = "";
    String expResult = "";
    String result = StringOper.CvtWEBstring(s);
    assertEquals(expResult, result);
  }

  @Test
  public void testCvtSQLWEBstring()
  {
    System.out.println("CvtSQLWEBstring");
    String s = "quantità";
    String expResult = "quantit&agrave;";
    String result = StringOper.CvtSQLWEBstring(s);
    assertEquals(expResult, result);
  }

//  @Test
//  public void testCvtXMLstring()
//  {
//    System.out.println("CvtXMLstring");
//    String s = "fagioli & cavoli";
//    String expResult = "fagioli &amp; cavoli";
//    String result = StringOper.CvtXMLstring(s);
//    assertEquals(expResult, result);
//  }
  @Test
  public void testCvtWEB2Ascii()
  {
    System.out.println("CvtWEB2Ascii");
    String str = "quantit&agrave; quantit&aacute;";;
    String expResult = "quantita' quantita'";
    String result = StringOper.CvtWEB2Ascii(str);
    assertEquals(expResult, result);
  }

//  @Test
//  public void testCvtWEB2Unicode()
//  {
//    System.out.println("CvtWEB2Unicode");
//    String str = "";
//    String expResult = "";
//    String result = StringOper.CvtWEB2Unicode(str);
//    assertEquals(expResult, result);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
  @Test
  public void testCvtGETstring()
  {
    System.out.println("CvtGETstring");
    String s = "questa prova";
    String expResult = "questa+prova";
    String result = StringOper.CvtGETstring(s);
    assertEquals(expResult, result);
  }

  @Test
  public void testCvtFILEstring()
  {
    System.out.println("CvtFILEstring");
    String s = "nome\\'di\"file/strano.txt";
    String expResult = "nome-_di_file-strano.txt";
    String result = StringOper.CvtFILEstring(s);
    assertEquals(expResult, result);
  }

//  @Test
//  public void testCvtASCIIstring()
//  {
//    System.out.println("CvtASCIIstring");
//    String s = "";
//    String expResult = "";
//    String result = StringOper.CvtASCIIstring(s);
//    assertEquals(expResult, result);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
  @Test
  public void testTestTokens_List_String()
  {
    System.out.println("testTokens");
    List<String> vToken = Arrays.asList("si", "si", "no", "si", "no", "si", "no", "no", "si", "no", "si");
    String strTest = "si";
    int expResult = 6;
    int result = StringOper.testTokens(vToken, strTest);
    assertEquals(expResult, result);
  }

  @Test
  public void testTestTokens_StringArr_String()
  {
    System.out.println("testTokens");
    String[] vToken = ArrayOper.asArray("si", "si", "no", "si", "no", "si", "no", "no", "si", "no", "si");
    String strTest = "si";
    int expResult = 6;
    int result = StringOper.testTokens(vToken, strTest);
    assertEquals(expResult, result);
  }

  @Test
  public void testIsEqualTokens_List_String()
  {
    System.out.println("isEqualTokens");
    List<String> lToken = Arrays.asList("si", "si", "no", "si", "no", "si", "no", "no", "si", "no", "si");
    String strTest = "si";
    boolean expResult = true;
    boolean result = StringOper.isEqualTokens(lToken, strTest);
    assertEquals(expResult, result);
  }

  @Test
  public void testIsEqualTokens_StringArr_String()
  {
    System.out.println("isEqualTokens");
    String[] vToken = ArrayOper.asArray("si", "si", "no", "si", "no", "si", "no", "no", "si", "no", "si");
    String strTest = "si";
    boolean expResult = true;
    boolean result = StringOper.isEqualTokens(vToken, strTest);
    assertEquals(expResult, result);
  }

  @Test
  public void testFmtHex()
  {
    System.out.println("fmtHex");
    int Numero = 10;
    int Len = 4;
    String expResult = "000a";
    String result = StringOper.fmtHex(Numero, Len);
    assertEquals(expResult, result);
  }

  @Test
  public void testFmtOctal()
  {
    System.out.println("fmtOctal");
    int Numero = 7;
    int Len = 4;
    String expResult = "0007";
    String result = StringOper.fmtOctal(Numero, Len);
    assertEquals(expResult, result);
  }

  @Test
  public void testFmtZero_int_int()
  {
    System.out.println("fmtZero");
    int Numero = 10;
    int Len = 4;
    String expResult = "0010";
    String result = StringOper.fmtZero(Numero, Len);
    assertEquals(expResult, result);
  }

  @Test
  public void testFmtZero_float_int()
  {
    System.out.println("fmtZero");
    float Numero = 0.0F;
    int Len = 4;
    String expResult = "00.0";
    String result = StringOper.fmtZero(Numero, Len);
    assertEquals(expResult, result);
  }

  @Test
  public void testFmtZero_double_int()
  {
    System.out.println("fmtZero");
    double Numero = 0.0;
    int Len = 4;
    String expResult = "00.0";
    String result = StringOper.fmtZero(Numero, Len);
    assertEquals(expResult, result);
  }

  @Test
  public void testStrReplace_3args()
  {
    System.out.println("strReplace");
    String Origine = "quanto sono giovane";
    String Cerca = "sono";
    String Cambia = "ero";
    String expResult = "quanto ero giovane";
    String result = StringOper.strReplace(Origine, Cerca, Cambia);
    assertEquals(expResult, result);
  }

  @Test
  public void testStrReplace_String_Map()
  {
    System.out.println("strReplace");
    String Origine = "quanto sono giovane e bello";
    Map<String, String> sostituzioni = ArrayOper.asMapFromPairStrings("sono", "ero", "bello", "carino");
    String expResult = "quanto ero giovane e carino";
    String result = StringOper.strReplace(Origine, sostituzioni);
    assertEquals(expResult, result);
  }

  @Test
  public void testStrReplace_String_Collection()
  {
    System.out.println("strReplace");
    String Origine = "quanto sono giovane e bello";
    Collection<Pair<String, String>> sostituzioni = ArrayOper.asListFromPairStrings("sono", "ero", "bello", "carino");
    String expResult = "quanto ero giovane e carino";
    String result = StringOper.strReplace(Origine, sostituzioni);
    assertEquals(expResult, result);
  }

  @Test
  public void testStrReplace_String_StringArr()
  {
    System.out.println("strReplace");
    String Origine = "quanto sono giovane e bello";
    String[] sostituzioni = ArrayOper.asArray("sono", "ero", "bello", "carino");
    String expResult = "quanto ero giovane e carino";
    String result = StringOper.strReplace(Origine, sostituzioni);
    assertEquals(expResult, result);
  }

  @Test
  public void testStrReplaceIndex_3args_1()
  {
    System.out.println("strReplaceIndex");
    String Origine = "questa 1 prova";
    int pos = 7;
    char newChar = '2';
    String expResult = "questa 2 prova";
    String result = StringOper.strReplaceIndex(Origine, pos, newChar);
    assertEquals(expResult, result);
  }

  @Test
  public void testStrReplaceIndex_3args_2()
  {
    System.out.println("strReplaceIndex");
    String Origine = "questa 1 prova";
    int pos = 7;
    String newString = "22";
    String expResult = "questa 22prova";
    String result = StringOper.strReplaceIndex(Origine, pos, newString);
    assertEquals(expResult, result);
  }

  /*
  @Test
  public void testMid_String_int()
  {
    System.out.println("mid");
    String Origine = "";
    int Inizio = 0;
    String expResult = "";
    String result = StringOper.mid(Origine, Inizio);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testMid_3args()
  {
    System.out.println("mid");
    String Origine = "";
    int Inizio = 0;
    int Lunghezza = 0;
    String expResult = "";
    String result = StringOper.mid(Origine, Inizio, Lunghezza);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testRight_String_int()
  {
    System.out.println("right");
    String Origine = "";
    int Len = 0;
    String expResult = "";
    String result = StringOper.right(Origine, Len);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testLeft_String_int()
  {
    System.out.println("left");
    String Origine = "";
    int Len = 0;
    String expResult = "";
    String result = StringOper.left(Origine, Len);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testMid_4args()
  {
    System.out.println("mid");
    Object origine = null;
    String defVal = "";
    int Inizio = 0;
    int Lunghezza = 0;
    String expResult = "";
    String result = StringOper.mid(origine, defVal, Inizio, Lunghezza);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testRight_3args()
  {
    System.out.println("right");
    Object origine = null;
    String defVal = "";
    int Len = 0;
    String expResult = "";
    String result = StringOper.right(origine, defVal, Len);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testFindAndGetRight_Object_String()
  {
    System.out.println("findAndGetRight");
    Object origine = null;
    String toSearch = "";
    String expResult = "";
    String result = StringOper.findAndGetRight(origine, toSearch);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testFindAndGetRight_3args()
  {
    System.out.println("findAndGetRight");
    Object origine = null;
    String defVal = "";
    String toSearch = "";
    String expResult = "";
    String result = StringOper.findAndGetRight(origine, defVal, toSearch);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testLeft_3args()
  {
    System.out.println("left");
    Object origine = null;
    String defVal = "";
    int Len = 0;
    String expResult = "";
    String result = StringOper.left(origine, defVal, Len);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testSplit()
  {
    System.out.println("split");
    String s = "";
    char delim = ' ';
    String[] expResult = null;
    String[] result = StringOper.split(s, delim);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_StringArr_char()
  {
    System.out.println("join");
    String[] arStrings = null;
    char separator = ' ';
    String expResult = "";
    String result = StringOper.join(arStrings, separator);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_3args_1()
  {
    System.out.println("join");
    String[] arStrings = null;
    char separator = ' ';
    char delimiter = ' ';
    String expResult = "";
    String result = StringOper.join(arStrings, separator, delimiter);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_3args_2()
  {
    System.out.println("join");
    String[] arStrings = null;
    String separator = "";
    String delimiter = "";
    String expResult = "";
    String result = StringOper.join(arStrings, separator, delimiter);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_4args_1()
     throws Exception
  {
    System.out.println("join");
    String expResult = "";
    String result = StringOper.join(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin2_4args_1()
  {
    System.out.println("join2");
    String expResult = "";
    String result = StringOper.join2(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_4args_2()
     throws Exception
  {
    System.out.println("join");
    String expResult = "";
    String result = StringOper.join(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin2_4args_2()
  {
    System.out.println("join2");
    String expResult = "";
    String result = StringOper.join2(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin2int()
  {
    System.out.println("join2int");
    String expResult = "";
    String result = StringOper.join2int(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoinNotNull_4args_1()
     throws Exception
  {
    System.out.println("joinNotNull");
    String expResult = "";
    String result = StringOper.joinNotNull(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoinNotNull_4args_2()
     throws Exception
  {
    System.out.println("joinNotNull");
    String expResult = "";
    String result = StringOper.joinNotNull(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin2NotNull()
  {
    System.out.println("join2NotNull");
    String expResult = "";
    String result = StringOper.join2NotNull(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoinNotEmpty()
  {
    System.out.println("joinNotEmpty");
    String separator = "";
    String delimiter = "";
    String[] arStrings = null;
    String expResult = "";
    String result = StringOper.joinNotEmpty(separator, delimiter, arStrings);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_4args_3()
  {
    System.out.println("join");
    String[] arStrings = null;
    char separator = ' ';
    int min = 0;
    int max = 0;
    String expResult = "";
    String result = StringOper.join(arStrings, separator, min, max);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_5args_1()
  {
    System.out.println("join");
    String[] arStrings = null;
    char separator = ' ';
    char delimiter = ' ';
    int min = 0;
    int max = 0;
    String expResult = "";
    String result = StringOper.join(arStrings, separator, delimiter, min, max);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_5args_2()
  {
    System.out.println("join");
    String[] arStrings = null;
    String separator = "";
    String delimiter = "";
    int min = 0;
    int max = 0;
    String expResult = "";
    String result = StringOper.join(arStrings, separator, delimiter, min, max);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_Iterator_char()
  {
    System.out.println("join");
    Iterator itr = null;
    char separator = ' ';
    String expResult = "";
    String result = StringOper.join(itr, separator);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_3args_3()
  {
    System.out.println("join");
    Iterator itr = null;
    char separator = ' ';
    char delimiter = ' ';
    String expResult = "";
    String result = StringOper.join(itr, separator, delimiter);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_3args_4()
  {
    System.out.println("join");
    Iterator itr = null;
    String separator = "";
    String delimiter = "";
    String expResult = "";
    String result = StringOper.join(itr, separator, delimiter);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_intArr_char()
  {
    System.out.println("join");
    int[] arInt = null;
    char separator = ' ';
    String expResult = "";
    String result = StringOper.join(arInt, separator);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoin_4args_4()
  {
    System.out.println("join");
    int[] arInt = null;
    char separator = ' ';
    int min = 0;
    int max = 0;
    String expResult = "";
    String result = StringOper.join(arInt, separator, min, max);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testString2List_3args()
  {
    System.out.println("string2List");
    String s = "";
    String delim = "";
    boolean removeEmpty = false;
    List<String> expResult = null;
    List<String> result = StringOper.string2List(s, delim, removeEmpty);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testString2List_String_String()
  {
    System.out.println("string2List");
    String s = "";
    String delim = "";
    List<String> expResult = null;
    List<String> result = StringOper.string2List(s, delim);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testString2Array_String_String()
  {
    System.out.println("string2Array");
    String s = "";
    String delim = "";
    String[] expResult = null;
    String[] result = StringOper.string2Array(s, delim);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testString2Array_3args()
  {
    System.out.println("string2Array");
    String s = "";
    String delim = "";
    boolean removeEmpty = false;
    String[] expResult = null;
    String[] result = StringOper.string2Array(s, delim, removeEmpty);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testString2Map_3args()
  {
    System.out.println("string2Map");
    String s = "";
    String delim = "";
    boolean removeEmpty = false;
    Map<String, String> expResult = null;
    Map<String, String> result = StringOper.string2Map(s, delim, removeEmpty);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testString2Map_4args()
  {
    System.out.println("string2Map");
    String s = "";
    String delim = "";
    char split = ' ';
    boolean removeEmpty = false;
    Map<String, String> expResult = null;
    Map<String, String> result = StringOper.string2Map(s, delim, split, removeEmpty);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testMap2String()
  {
    System.out.println("map2String");
    Map map = null;
    String delim = "";
    boolean removeEmpty = false;
    String expResult = "";
    String result = StringOper.map2String(map, delim, removeEmpty);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
   */
  @Test
  public void testStrarr2intarr_StringArr_int()
  {
    System.out.println("strarr2intarr");
    int defVal = 0;
    String[] sArr = StringOper.split("10,20,30", ',');
    int[] expResult = ArrayOper.asArrayInt(10, 20, 30);
    int[] result = StringOper.strarr2intarr(sArr, defVal);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testStrarr2intarr_3args()
  {
    System.out.println("strarr2intarr");
    String[] sArr = StringOper.split("30,bb,10,20,aa,20,0", ',');
    int[] expResult1 = ArrayOper.asArrayInt(10, 20, 20, 30);
    int[] expResult2 = ArrayOper.asArrayInt(10, 20, 30);
    int[] result1 = StringOper.strarr2intarr(sArr, 0, (i) -> i != 0);
    int[] result2 = StringOper.strarr2intarrUnique(sArr, 0, (i) -> i != 0);
    assertArrayEquals(expResult1, result1);
    assertArrayEquals(expResult2, result2);
  }
  /*
  @Test
  public void testPurge_String()
  {
    System.out.println("purge");
    String s = "";
    String expResult = "";
    String result = StringOper.purge(s);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testPurge_String_char()
  {
    System.out.println("purge");
    String s = "";
    char toremove = ' ';
    String expResult = "";
    String result = StringOper.purge(s, toremove);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsOkStr()
  {
    System.out.println("isOkStr");
    Object o = null;
    boolean expResult = false;
    boolean result = StringOper.isOkStr(o);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsOkStrAll()
  {
    System.out.println("isOkStrAll");
    Object[] args = null;
    boolean expResult = false;
    boolean result = StringOper.isOkStrAll(args);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsOkStrAny()
  {
    System.out.println("isOkStrAny");
    Object[] args = null;
    boolean expResult = false;
    boolean result = StringOper.isOkStrAny(args);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEqu_Object_Object()
  {
    System.out.println("isEqu");
    Object o1 = null;
    Object o2 = null;
    boolean expResult = false;
    boolean result = StringOper.isEqu(o1, o2);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEquNocase_Object_Object()
  {
    System.out.println("isEquNocase");
    Object o1 = null;
    Object o2 = null;
    boolean expResult = false;
    boolean result = StringOper.isEquNocase(o1, o2);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEqu_char_Object()
  {
    System.out.println("isEqu");
    char c = ' ';
    Object o2 = null;
    boolean expResult = false;
    boolean result = StringOper.isEqu(c, o2);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEqu_Object_StringArr()
  {
    System.out.println("isEqu");
    Object o1 = null;
    String[] values = null;
    boolean expResult = false;
    boolean result = StringOper.isEqu(o1, values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEquAny()
  {
    System.out.println("isEquAny");
    Object o1 = null;
    String[] values = null;
    boolean expResult = false;
    boolean result = StringOper.isEquAny(o1, values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEquNocase_Object_StringArr()
  {
    System.out.println("isEquNocase");
    Object o1 = null;
    String[] values = null;
    boolean expResult = false;
    boolean result = StringOper.isEquNocase(o1, values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testIsEquNocaseAny()
  {
    System.out.println("isEquNocaseAny");
    Object o1 = null;
    String[] values = null;
    boolean expResult = false;
    boolean result = StringOper.isEquNocaseAny(o1, values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testContains()
  {
    System.out.println("contains");
    Object o1 = null;
    String[] values = null;
    boolean expResult = false;
    boolean result = StringOper.contains(o1, values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testContainsAny()
  {
    System.out.println("containsAny");
    Object o1 = null;
    String[] values = null;
    boolean expResult = false;
    boolean result = StringOper.containsAny(o1, values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCompare()
  {
    System.out.println("compare");
    Object o1 = null;
    Object o2 = null;
    int expResult = 0;
    int result = StringOper.compare(o1, o2);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCompareIgnoreCase()
  {
    System.out.println("compareIgnoreCase");
    Object o1 = null;
    Object o2 = null;
    int expResult = 0;
    int result = StringOper.compareIgnoreCase(o1, o2);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCompareVersion()
  {
    System.out.println("compareVersion");
    Object v1 = null;
    Object v2 = null;
    char delim = ' ';
    int expResult = 0;
    int result = StringOper.compareVersion(v1, v2, delim);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStr_Object()
  {
    System.out.println("okStr");
    Object o = null;
    String expResult = "";
    String result = StringOper.okStr(o);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStr_Object_String()
  {
    System.out.println("okStr");
    Object o = null;
    String defVal = "";
    String expResult = "";
    String result = StringOper.okStr(o, defVal);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStr_Object_int()
  {
    System.out.println("okStr");
    Object o = null;
    int maxlen = 0;
    String expResult = "";
    String result = StringOper.okStr(o, maxlen);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStrNull()
  {
    System.out.println("okStrNull");
    Object o = null;
    String expResult = "";
    String result = StringOper.okStrNull(o);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStrHtml()
  {
    System.out.println("okStrHtml");
    Object o = null;
    String expResult = "";
    String result = StringOper.okStrHtml(o);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStr_ObjectArr_String()
  {
    System.out.println("okStr");
    Object[] o = null;
    String defVal = "";
    String expResult = "";
    String result = StringOper.okStr(o, defVal);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testOkStrAny()
  {
    System.out.println("okStrAny");
    Object[] values = null;
    String expResult = "";
    String result = StringOper.okStrAny(values);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testParse_Object_int()
  {
    System.out.println("parse");
    Object val = null;
    int defVal = 0;
    int expResult = 0;
    int result = StringOper.parse(val, defVal);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testParse_Object_double()
  {
    System.out.println("parse");
    Object val = null;
    double defVal = 0.0;
    double expResult = 0.0;
    double result = StringOper.parse(val, defVal);
    assertEquals(expResult, result, 0);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCheckTrue()
  {
    System.out.println("checkTrue");
    String sBool = "";
    boolean expResult = false;
    boolean result = StringOper.checkTrue(sBool);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCheckFalse()
  {
    System.out.println("checkFalse");
    String sBool = "";
    boolean expResult = false;
    boolean result = StringOper.checkFalse(sBool);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCheckTrueFalse()
  {
    System.out.println("checkTrueFalse");
    Object val = null;
    boolean defVal = false;
    boolean expResult = false;
    boolean result = StringOper.checkTrueFalse(val, defVal);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCvtHtml2Text()
  {
    System.out.println("cvtHtml2Text");
    String html = "";
    String expResult = "";
    String result = StringOper.cvtHtml2Text(html);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCreateUnicode2HtmlMap()
  {
    System.out.println("createUnicode2HtmlMap");
    Map<Character, String> expResult = null;
    Map<Character, String> result = StringOper.createUnicode2HtmlMap();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCreateHtml2UnicodeMap()
  {
    System.out.println("createHtml2UnicodeMap");
    Map<String, Character> expResult = null;
    Map<String, Character> result = StringOper.createHtml2UnicodeMap();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testReverseMap()
  {
    System.out.println("reverseMap");
    Map expResult = null;
    Map result = StringOper.reverseMap(null);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCountLineesInString()
  {
    System.out.println("countLineesInString");
    String input = "";
    int expResult = 0;
    int result = StringOper.countLineesInString(input);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testCountCharacterInString()
  {
    System.out.println("countCharacterInString");
    String input = "";
    int car = 0;
    int expResult = 0;
    int result = StringOper.countCharacterInString(input, car);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testStartRightString()
  {
    System.out.println("startRightString");
    String origine = "";
    String test = "";
    String expResult = "";
    String result = StringOper.startRightString(origine, test);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testToArray_StringArr()
  {
    System.out.println("toArray");
    String[] arr = null;
    String[] expResult = null;
    String[] result = StringOper.toArray(arr);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testToArrayList()
  {
    System.out.println("toArrayList");
    String[] arr = null;
    List<String> expResult = null;
    List<String> result = StringOper.toArrayList(arr);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testToArray_Collection()
  {
    System.out.println("toArray");
    Collection<String> ls = null;
    String[] expResult = null;
    String[] result = StringOper.toArray(ls);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testToArrayGeneric_List()
  {
    System.out.println("toArrayGeneric");
    List ls = null;
    String[] expResult = null;
    String[] result = StringOper.toArrayGeneric(ls);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testToArrayGeneric_3args()
  {
    System.out.println("toArrayGeneric");
    List ls = null;
    int begin = 0;
    int end = 0;
    String[] expResult = null;
    String[] result = StringOper.toArrayGeneric(ls, begin, end);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testMergePath_String_String()
  {
    System.out.println("mergePath");
    String path = "";
    String s = "";
    String expResult = "";
    String result = StringOper.mergePath(path, s);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testMergePath_String_StringArr()
  {
    System.out.println("mergePath");
    String path = "";
    String[] lsFiles = null;
    String expResult = "";
    String result = StringOper.mergePath(path, lsFiles);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testMergePath_String_List()
  {
    System.out.println("mergePath");
    String path = "";
    List<String> lsFiles = null;
    String expResult = "";
    String result = StringOper.mergePath(path, lsFiles);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testNormalizePath()
  {
    System.out.println("normalizePath");
    String path = "";
    String expResult = "";
    String result = StringOper.normalizePath(path);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testJoinCommand()
  {
    System.out.println("joinCommand");
    String[] arStrings = null;
    String expResult = "";
    String result = StringOper.joinCommand(arStrings);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  @Test
  public void testSplitCampiCSV()
     throws Exception
  {
    System.out.println("splitCampiCSV");
    String linea = "";
    String[] expResult = null;
    String[] result = StringOper.splitCampiCSV(linea);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
   */
}
