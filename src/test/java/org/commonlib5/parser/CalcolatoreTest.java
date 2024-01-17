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

import java.util.Enumeration;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class CalcolatoreTest
{
  public static final double epsilon = 0.00001;

  @Test
  public void test1()
     throws Exception
  {
    Calcolatore calc = new Calcolatore();

    assertEquals("RES: 35", 35.0, calc.parse("10+25"), epsilon);
    assertEquals("RES: 35", 35.0, calc.parse("10 + 25"), epsilon);
    assertEquals("RES: 35", 35.0, calc.parse("10+(5*5)"), epsilon);
    assertEquals("RES: 35", 35.0, calc.parse("10 + ( 5 * 5 )"), epsilon);

    assertEquals("RES: PI", Math.PI, calc.parse("pi()"), epsilon);
    assertEquals("RES: 2*PI*50", 2 * Math.PI * 50, calc.parse("2*pi()*50"), epsilon);
    assertEquals("RES: 1*pow10(4+1)", 1 * Math.pow(10.0, 4 + 1), calc.parse("1*pow10(4+1)"), epsilon);

    assertEquals("RES: (pi()*0.0915)+2.15", (Math.PI * 0.0915) + 2.15, calc.parse("(pi()*0.0915)+2.15"), epsilon);

    assertEquals("RES: PI", 3.1, calc.parse("truncate1(pi())"), epsilon);
    assertEquals("RES: PI", 3.14, calc.parse("truncate2(pi())"), epsilon);
    assertEquals("RES: PI", 3.142, calc.parse("truncate3(pi())"), epsilon);

    assertEquals("RES: PI", 3.0, calc.parse("round(pi())"), epsilon);
    assertEquals("RES: PI", 4.0, calc.parse("ceil(pi())"), epsilon);
    assertEquals("RES: PI", 3.0, calc.parse("floor(pi())"), epsilon);
  }

  @Test
  public void test2()
     throws Exception
  {
    ContextParser parser = new ContextParser();
    CalcContext context = new CalcContext();

    context.put("raggio", 100);
    context.put("diametro", "raggio*2");
    context.put("arco", 25);
    context.put("altezza", 700);
    context.put("VAL", 3.24);

    String toParse
       = "circonferenza= 2 * pi()*raggio\n"
       + "areacerchio=pi()*(raggio^2)\n"
       + "lunarco=circonferenza*arco/360\n"
       + "areaarco=areacerchio*arco/360\n"
       + "volumecilindro=areacerchio*altezza\n"
       + "suplatcilindro=2*pi()*raggio*altezza\n"
       + "suptotcilindro=suplatcilindro+areacerchio*2\n"
       + "volumecono=areacerchio*altezza/3\n"
       + "risultato=(VAL*0.0915)+2.15\n\n";

    parser.calc(toParse, context);

    double raggio = 100, arco = 25, altezza = 700, val = 3.24;

    double circonferenza = 2 * Math.PI * raggio;
    double areacerchio = Math.PI * (raggio * raggio);
    double lunarco = circonferenza * arco / 360;
    double areaarco = areacerchio * arco / 360;
    double volumecilindro = areacerchio * altezza;
    double suplatcilindro = 2 * Math.PI * raggio * altezza;
    double suptotcilindro = suplatcilindro + areacerchio * 2;
    double volumecono = areacerchio * altezza / 3;
    double risultato = (val * 0.0915) + 2.15;

    assertEquals("circonferenza= 2 * pi()*raggio", 2 * Math.PI * raggio, context.get("circonferenza"), epsilon);
    assertEquals("areacerchio=pi()*(raggio^2)", Math.PI * (raggio * raggio), context.get("areacerchio"), epsilon);
    assertEquals("lunarco=circonferenza*arco/360", circonferenza * arco / 360, context.get("lunarco"), epsilon);
    assertEquals("areaarco=areacerchio*arco/360", areacerchio * arco / 360, context.get("areaarco"), epsilon);
    assertEquals("volumecilindro=areacerchio*altezza", areacerchio * altezza, context.get("volumecilindro"), epsilon);
    assertEquals("suplatcilindro=2*pi()*raggio*altezza", 2 * Math.PI * raggio * altezza, context.get("suplatcilindro"), epsilon);
    assertEquals("suptotcilindro=suplatcilindro+areacerchio*2", suplatcilindro + areacerchio * 2, context.get("suptotcilindro"), epsilon);
    assertEquals("volumecono=areacerchio*altezza/3", areacerchio * altezza / 3, context.get("volumecono"), epsilon);
    assertEquals("risultato=(VAL*0.0915)+2.15", (val * 0.0915) + 2.15, context.get("risultato"), epsilon);

    System.out.println(toParse);
    dumpContext(context);
  }

  public static void dumpContext(CalcContext context)
  {
    Enumeration eKeys = context.keys();
    while(eKeys.hasMoreElements())
    {
      String key = eKeys.nextElement().toString();
      System.out.println(key + "=" + context.getValue(key));
    }
  }
}
