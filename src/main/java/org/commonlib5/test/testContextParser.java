/*
 * Copyright (C) 2011 Nicola De Nisco
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
package org.commonlib5.test;

import java.util.Enumeration;
import org.commonlib5.parser.CalcContext;
import org.commonlib5.parser.ContextParser;

/**
 * Test del ContextParser.
 *
 * @author Nicola De Nisco
 */
public class testContextParser
{
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    try
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
         + "(VAL*0.0915)+2.15";

      parser.calc(toParse, context);

      System.out.println(toParse);
      dumpContext(context);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
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
