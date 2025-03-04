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
package org.commonlib5.nmath;

/**
 * Title:        Commonlib
 * Description:  Libreria di utilizzo comune.
 * Contiene varie funzioni di utilita'
 * quali calcolo matriciale, ecc.
 * @author Nicola De Nisco
 * @version 1.0
 */
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.math.*;

public class Punto2D implements Cloneable, Serializable
{
  public float x, y;
  public static float epsilonX = 0.001f;
  public static float epsilonY = 0.001f;

  public Punto2D()
  {
    x = y = 0.0f;
  }

  public Punto2D(float xx, float yy)
  {
    x = xx;
    y = yy;
  }

  public Punto2D(Punto2D p)
  {
    x = p.x;
    y = p.y;
  }

  public Punto2D(Point2D.Float p)
  {
    x = p.x;
    y = p.y;
  }

  public Punto2D(Point2D.Double p)
  {
    x = (float) p.x;
    y = (float) p.y;
  }

  public double GetDistance(Punto2D p)
  {
    return hypot(p.x - x, p.y - y);
  }

  public double GetDistance(float px, float py)
  {
    return hypot(px - x, py - y);
  }

  public float GetDistanceQuadrato(Punto2D p)
  {
    return Math.min(p.x - x, p.y - y);
  }

  public Punto2D AccumulaMin(Punto2D p)
  {
    x = Math.min(x, p.x);
    y = Math.min(y, p.y);
    return this;
  }

  public Punto2D AccumulaMax(Punto2D p)
  {
    x = Math.max(x, p.x);
    y = Math.max(y, p.y);
    return this;
  }

  public Punto2D AccumulaMin(float xx, float yy)
  {
    x = Math.min(x, xx);
    y = Math.min(y, yy);
    return this;
  }

  public Punto2D AccumulaMax(float xx, float yy)
  {
    x = Math.max(x, xx);
    y = Math.max(y, yy);
    return this;
  }

  public void MultMatrixXY(Matrix3 m)
  {
    double v[] = new double[4];
    v[0] = (double) x;
    v[1] = (double) y;
    v[2] = 0;
    v[3] = 1;
    m.MultPoint(v);
    x = (float) (v[0] / v[3]);
    y = (float) (v[1] / v[3]);
  }

  public void MultMatrixXZ(Matrix3 m)
  {
    double v[] = new double[4];
    v[0] = (double) x;
    v[1] = 0;
    v[2] = (double) y;
    v[3] = 1;
    m.MultPoint(v);
    x = (float) (v[0] / v[3]);
    y = (float) (v[2] / v[3]);
  }

  public static double CoeffAng(double xa, double ya, double xb, double yb)
  {
    return (yb - ya) / (xb - xa);
  }

  public static double hypot(double x, double y)
  {
    return Math.sqrt((x * x) + (y * y));
  }

  public static boolean equEps(float a, float b, float c)
  {
    return Math.abs(a - b) < c;
  }

  public static boolean equEpsX(float a, float b)
  {
    return Math.abs(a - b) < epsilonX;
  }

  public static boolean equEpsY(float a, float b)
  {
    return Math.abs(a - b) < epsilonY;
  }

  public Punto2D copy(Punto2D p)
  {
    x = p.x;
    y = p.y;
    return this;
  }

  public Punto2D meno(Punto2D p2)
  {
    return new Punto2D(x - p2.x, y - p2.y);
  }

  public Punto2D piu(Punto2D p2)
  {
    return new Punto2D(x + p2.x, y + p2.y);
  }

  public Punto2D per(double scalare)
  {
    return new Punto2D((float) (x * scalare), (float) (y * scalare));
  }

  public Punto2D menoeq(Punto2D p2)
  {
    x -= p2.x;
    y -= p2.y;
    return this;
  }

  public Punto2D piueq(Punto2D p2)
  {
    x += p2.x;
    y += p2.y;
    return this;
  }

  public boolean isEqual(Punto2D p2)
  {
    return equEpsX(x, p2.x) && equEpsY(y, p2.y);
  }

  @Override
  public boolean equals(Object parm1)
  {
    return parm1 instanceof Punto2D ? isEqual((Punto2D) parm1) : super.equals(parm1);
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 53 * hash + Float.floatToIntBits(this.x);
    hash = 53 * hash + Float.floatToIntBits(this.y);
    return hash;
  }

  @Override
  public Punto2D clone()
  {
    return new Punto2D(x, y);
  }

  @Override
  public String toString()
  {
    return "X=" + x + " Y=" + y;
  }

  public String toStringInt()
  {
    return "(" + ((int) x) + "," + ((int) y) + ")";
  }

  public Point2D.Float toPointFloat()
  {
    return new Point2D.Float(x, y);
  }

  public Point2D.Double toPointDouble()
  {
    return new Point2D.Double(x, y);
  }

}

