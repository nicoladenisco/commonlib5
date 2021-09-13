/*
 * Copyright (C) 2018 Nicola De Nisco
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

import org.commonlib5.parser.Calcolatore;

/**
 *
 * @author Nicola De Nisco
 */
public class testCalcolatore
{
  public static void main(String[] args)
  {
    try
    {
      Calcolatore calc = new Calcolatore();

      System.out.println("RES: " + calc.parse("10+25"));
      System.out.println("RES: " + calc.parse("10 + 25"));
      System.out.println("RES: " + calc.parse("10+(5*5)"));
      System.out.println("RES: " + calc.parse("10 + ( 5 * 5 )"));

      System.out.println("RES: " + calc.parse("pi()"));
      System.out.println("RES: " + calc.parse("2*pi()*50"));
      System.out.println("RES: " + calc.parse("1*pow10(4+1)"));

      System.out.println("RES: " + calc.parse("(pi()*0.0915)+2.15"));

    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
