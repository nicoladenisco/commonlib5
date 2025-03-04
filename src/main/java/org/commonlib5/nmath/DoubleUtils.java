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
 * Classe di utilita per i confronti in virgola doppia.
 *
 * @author Nicola De Nisco
 */
public class DoubleUtils
{
  public double EPSI_VALUTA = 0.001;
  public double EPSI_QTA = 0.00001;
  public double EPSI_DIM = 0.0001;
  public double EPSI_GENERIC = 0.000001;

  public double EPSI_2D = 0.01;
  public double EPSI_3D = 0.001;
  public double EPSI_4D = 0.0001;

  public double ROUND_2D = 100;
  public double ROUND_3D = 1000;
  public double ROUND_4D = 10000;

  public int compareEpsi(double a, double b, double epsilon)
  {
    if(Math.abs(a - b) < epsilon)
      return 0;

    if(a < b)
      return -1;

    if(a > b)
      return +1;

    return 0;
  }

  public int compareEpsiValuta(double a, double b)
  {
    return compareEpsi(a, b, EPSI_VALUTA);
  }

  public int compareEpsiQta(double a, double b)
  {
    return compareEpsi(a, b, EPSI_QTA);
  }

  public int compareEpsiDim(double a, double b)
  {
    return compareEpsi(a, b, EPSI_DIM);
  }

  public int compareEpsiGeneric(double a, double b)
  {
    return compareEpsi(a, b, EPSI_GENERIC);
  }

  public int compareEpsi2D(double a, double b)
  {
    return compareEpsi(a, b, EPSI_2D);
  }

  public int compareEpsi3D(double a, double b)
  {
    return compareEpsi(a, b, EPSI_3D);
  }

  public int compareEpsi4D(double a, double b)
  {
    return compareEpsi(a, b, EPSI_4D);
  }

  public boolean equEpsi(double a, double b, double epsilon)
  {
    return compareEpsi(a, b, epsilon) == 0;
  }

  public boolean zeroEpsi(double num, double epsilon)
  {
    return compareEpsi(num, 0, epsilon) == 0;
  }

  public boolean equEpsiValuta(double a, double b)
  {
    return compareEpsiValuta(a, b) == 0;
  }

  public boolean zeroEpsiValuta(double val)
  {
    return compareEpsiValuta(val, 0) == 0;
  }

  public boolean zeroOrLessEpsiValuta(double val)
  {
    return compareEpsiValuta(val, 0) <= 0;
  }

  public boolean equEpsiQta(double a, double b)
  {
    return compareEpsiQta(a, b) == 0;
  }

  public boolean zeroEpsiQta(double val)
  {
    return compareEpsiQta(val, 0) == 0;
  }

  public boolean equEpsiDim(double a, double b)
  {
    return compareEpsiDim(a, b) == 0;
  }

  public boolean zeroEpsiDim(double val)
  {
    return compareEpsiDim(val, 0) == 0;
  }

  public boolean equEpsiGeneric(double a, double b)
  {
    return compareEpsiGeneric(a, b) == 0;
  }

  public boolean zeroEpsiGeneric(double val)
  {
    return compareEpsiGeneric(val, 0) == 0;
  }

  public boolean equEpsi2D(double a, double b)
  {
    return compareEpsi2D(a, b) == 0;
  }

  public boolean zeroEpsi2D(double val)
  {
    return compareEpsi2D(val, 0) == 0;
  }

  public boolean equEpsi3D(double a, double b)
  {
    return compareEpsi3D(a, b) == 0;
  }

  public boolean zeroEpsi3D(double val)
  {
    return compareEpsi3D(val, 0) == 0;
  }

  public boolean equEpsi4D(double a, double b)
  {
    return compareEpsi4D(a, b) == 0;
  }

  public boolean zeroEpsi4D(double val)
  {
    return compareEpsi4D(val, 0) == 0;
  }

  public double roundto(double value, int numCifre)
  {
    double exp = Math.pow(10, numCifre);
    return Math.round(value * exp) / exp;
  }

  public double round2D(double value)
  {
    return Math.round(value * ROUND_2D) / ROUND_2D;
  }

  public double round3D(double value)
  {
    return Math.round(value * ROUND_3D) / ROUND_3D;
  }

  public double round4D(double value)
  {
    return Math.round(value * ROUND_4D) / ROUND_4D;
  }
}
