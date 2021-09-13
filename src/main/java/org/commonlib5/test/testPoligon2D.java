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

import org.commonlib5.nmath.*;

/**
 * Title: Commonlib
 * Description: Libreria di utilizzo comune.
 * Contiene varie funzioni di utilita'
 * quali calcolo matriciale, ecc.
 * @author Nicola De Nisco
 * @version 1.0
 */
public class testPoligon2D
{

  public testPoligon2D()
  {
  }

  public void test1()
     throws Exception
  {
    Poligon2D poly = new Poligon2D();
    poly.addPunto(-1, -1);
    poly.addPunto(-1, +1);
    poly.addPunto(+1, +1);
    poly.addPunto(+1, -1);

    poly.generaLinee();

    Linea2D l = poly.getIntLineaIn(new Linea2D(-2, -0.5, +2, +0.5));
    if(l != null)
    {
      System.out.println("Intersezione " + l);
    }
  }

  public void test2()
     throws Exception
  {
    Poligon2D poly = new Poligon2D();
    poly.addPunto(0, 0);
    poly.addPunto(900, 0);
    poly.addPunto(900, 1800);
    poly.addPunto(0, 1800);

    poly.generaLinee();

    Linea2D l = poly.getIntLineaIn(new Linea2D(3747.666, 0, 1343.503, 5091.169));
    if(l != null)
    {
      System.out.println("Intersezione " + l);
    }
  }

  public void test3()
     throws Exception
  {
    Poligon2D poly = new Poligon2D();
    poly.addPunto(0, 0);
    poly.addPunto(900, 0);
    poly.addPunto(900, 1800);
    poly.addPunto(0, 1800);

    poly.generaLinee();

    Linea2D l = poly.getIntLineaIn(new Linea2D(-100, 900, +100, 100));
    if(l != null)
    {
      System.out.println("Intersezione " + l);
    }
  }

  public static void main(String[] args)
  {
    try
    {
      testPoligon2D testPoligon2D1 = new testPoligon2D();
      testPoligon2D1.test3();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
