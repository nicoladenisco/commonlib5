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
import java.io.Serializable;
import java.math.*;

public class Punto3D implements Cloneable, Serializable
{
  public float x, y, z;
  public static float epsilonX;
  public static float epsilonY;
  public static float epsilonZ;

  public Punto3D()
  {
    x = y = z = 0.0f;
  }

  public Punto3D(float xx, float yy, float zz)
  {
    x = xx;
    y = yy;
    z = zz;
  }

  public Punto3D(Punto3D p)
  {
    x = p.x;
    y = p.y;
    z = p.z;
  }

  public double GetDistance(Punto3D p)
  {
    double tmpX = p.x - x;
    double tmpY = p.y - y;
    double tmpZ = p.z - z;
    return Math.sqrt((tmpX * tmpX) + (tmpY * tmpY) + (tmpZ * tmpZ));
  }

  public double GetMagnitude(Punto3D p)
  {
    return ((double) x) * p.x + ((double) y) * p.y + ((double) z) * p.z;
  }

  public Punto3D AccumulaMin(Punto3D p)
  {
    x = Math.min(x, p.x);
    y = Math.min(y, p.y);
    z = Math.min(z, p.z);
    return this;
  }

  public Punto3D AccumulaMax(Punto3D p)
  {
    x = Math.max(x, p.x);
    y = Math.max(y, p.y);
    z = Math.max(z, p.z);
    return this;
  }

  public Punto3D AccumulaMin(float xx, float yy, float zz)
  {
    x = Math.min(x, xx);
    y = Math.min(y, yy);
    z = Math.min(z, zz);
    return this;
  }

  public Punto3D AccumulaMax(float xx, float yy, float zz)
  {
    x = Math.max(x, xx);
    y = Math.max(y, yy);
    z = Math.max(z, zz);
    return this;
  }

  public void MultMatrix(Matrix3 m)
  {
    double v[] = new double[4];
    v[0] = (double) x;
    v[1] = (double) y;
    v[2] = (double) z;
    v[3] = 1;
    m.MultPoint(v);
    x = (float) (v[0] / v[3]);
    y = (float) (v[1] / v[3]);
    z = (float) (v[2] / v[3]);
  }

  public static boolean equEpsX(float a, float b)
  {
    return Math.abs(a - b) < epsilonX;
  }

  public static boolean equEpsY(float a, float b)
  {
    return Math.abs(a - b) < epsilonY;
  }

  public static boolean equEpsZ(float a, float b)
  {
    return Math.abs(a - b) < epsilonZ;
  }

  public Punto3D copy(Punto3D p)
  {
    x = p.x;
    y = p.y;
    z = p.z;
    return this;
  }

  public Punto3D meno(Punto3D p2)
  {
    return new Punto3D(x - p2.x, y - p2.y, z - p2.z);
  }

  public Punto3D piu(Punto3D p2)
  {
    return new Punto3D(x + p2.x, y + p2.y, z + p2.z);
  }

  public Punto3D per(double scalare)
  {
    return new Punto3D((float) (x * scalare), (float) (y * scalare), (float) (z * scalare));
  }

  public Punto3D per(Punto3D p)
  {
    return new Punto3D(
       (float) (((double) y) * p.z - ((double) z) * p.y),
       (float) (((double) z) * p.x - ((double) x) * p.z),
       (float) (((double) x) * p.y - ((double) y) * p.x));
  }

  public Punto3D menoeq(Punto3D p2)
  {
    x -= p2.x;
    y -= p2.y;
    z -= p2.z;
    return this;
  }

  public Punto3D piueq(Punto3D p2)
  {
    x += p2.x;
    y += p2.y;
    z += p2.z;
    return this;
  }

  public boolean isEqual(Punto3D p2)
  {
    return equEpsX(x, p2.x) && equEpsY(y, p2.y) && equEpsZ(z, p2.z);
  }

  @Override
  public boolean equals(Object parm1)
  {
    return parm1 instanceof Punto3D ? isEqual((Punto3D) parm1) : super.equals(parm1);
  }

  @Override
  public Object clone() throws java.lang.CloneNotSupportedException
  {
    return new Punto3D(x, y, z);
  }

  @Override
  public String toString()
  {
    return "X=" + x + " Y=" + y + " Z=" + z;
  }

}

