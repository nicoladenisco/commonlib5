/*
 * Copyright (C) 2020 Nicola De Nisco
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
package org.commonlib5.parser;

import java.util.Date;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class ParserTest
{
  public static final double epsilon = 0.00001;

  @Test
  public void test()
     throws Exception
  {
    Parser pa = new Parser();

    pa.init();
    pa.setCacheLevels(2);

    // test semplice
    String expr = " 10 + 25 * 3";
    double val = pa.parse(expr);
    System.out.println("semplice " + expr + "=" + val);
    assertEquals("semplice " + expr, 10 + 25 * 3, val, epsilon);

    // test con cache
    double mia = 125;
    pa.AddCacheEntry(0, "mia", 125);
    expr = "( 14 + mia ) / 2";
    val = pa.parse(expr);
    System.out.println("cache (mia=125) " + expr + "=" + val);
    assertEquals("cache (mia=125) " + expr, (14.0 + mia) / 2, val, epsilon);

    // test con cache
    double mia100 = 1250;
    pa.AddCacheEntry(0, "mia100", 1250);
    expr = "(140+mia100)/2";
    val = pa.parse(expr);
    System.out.println("cache (mia100=1250) " + expr + "=" + val);
    assertEquals("cache (mia100=1250) " + expr, (140 + mia100) / 2, val, epsilon);

    // test variabili
    double tua = mia * 2;
    pa.AddVariabile("tua", "mia*2");
    expr = "tua*10";
    val = pa.parse(expr);
    System.out.println("variabili (mia=125, tua=mia*2) " + expr + "=" + val);
    assertEquals("variabili (mia=125, tua=mia*2) " + expr, tua * 10, val, epsilon);

    // test variabili
    double tua100 = mia100 * 2;
    pa.AddVariabile("tua100", "mia100*2");
    expr = "tua100*10";
    val = pa.parse(expr);
    System.out.println("variabili (mia100=1250, tua100=mia100*2) " + expr + "=" + val);
    assertEquals("variabili (mia100=1250, tua100=mia100*2) " + expr, tua100 * 10, val, epsilon);

    // test cache multilivello
    double lamb = 1000, hamb = 1000, lelem = 222, helem = 333;
    pa.AddCacheEntry(0, "lamb", 1000);
    pa.AddCacheEntry(0, "hamb", 1000);
    pa.AddCacheEntry(1, "lelem", 222);
    pa.AddCacheEntry(1, "helem", 333);
    double uno = lamb + lelem, due = hamb + helem;
    pa.AddVariabile("uno", "lamb+lelem");
    pa.AddVariabile("due", "hamb+helem");
    expr = "uno+due";
    val = pa.parse(expr);
    System.out.println("multilivello " + expr + "=" + val);
    assertEquals("multilivello " + expr, uno + due, val, epsilon);

    lelem = 444;
    helem = 555;
    uno = lamb + lelem;
    due = hamb + helem;
    pa.FlushCache(1);
    pa.AddCacheEntry(1, "lelem", 444);
    pa.AddCacheEntry(1, "helem", 555);
    val = pa.parse(expr);
    System.out.println("multilivello " + expr + "=" + val);
    assertEquals("multilivello " + expr, uno + due, val, epsilon);

    // test di velocita' senza cache
    int numIter = 30000;
    Date t1Start = new Date();
    expr = "(1000+444)+(1000+555)";
    for(int i = 0; i < numIter; i++)
    {
      val = pa.parse(expr);
    }
    Date t1Stop = new Date();
    System.out.println("velocita' no cache iterazioni=" + numIter
       + " " + expr + "=" + val + " tempo=" + (t1Stop.getTime() - t1Start.getTime()) + " millisecondi");

    // test di velocita' con cache
    Date t2Start = new Date();
    expr = "uno+due";
    for(int i = 0; i < numIter; i++)
    {
      val = pa.parse(expr);
    }
    Date t2Stop = new Date();
    System.out.println("velocita' con cache iterazioni=" + numIter
       + " " + expr + "=" + val + " tempo=" + (t2Stop.getTime() - t2Start.getTime()) + " millisecondi");

    pa.destroy();
  }

}
