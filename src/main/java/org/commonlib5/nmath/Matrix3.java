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

public class Matrix3 implements Cloneable, Serializable
{
  public double m[][] = new double[4][4];

  /**
   * costruttori assortiti
   */
  public Matrix3()
  {
    copymatrix(Identita);
  }

  public Matrix3(double ma[][])
  {
    copymatrix(ma);
  }

  public Matrix3(float ma[][])
  {
    copymatrix(m, ma);
  }

  public Matrix3(Matrix3 ma)
  {
    copymatrix(ma.m);
  }

  /**
   * Pulisce la matrice ponendo tutti gli elementi a 0
   */
  public void Clear0()
  {
    copymatrix(Zero);
  }

  /**
   * Pulisce la matrice ponendola uguale alla matrice identita
   */
  public void ClearI()
  {
    copymatrix(Identita);
  }

  /**
   * Moltiplica per la matrice
   * @param mat
   */
  public void MultMatrix(Matrix3 mat)
  {
    MultMatrix(mat.m);
  }

  /**
   * Moltiplica per la matrice
   * @param ma
   */
  public void MultMatrix(double ma[][])
  {
    int i, j, k;
    double tmp;
    double m3[][] = new double[4][4];

    for(k = 0; k < 4; k++)
    {
      for(j = 0; j < 4; j++)
      {
        tmp = 0;
        for(i = 0; i < 4; i++)
          tmp += m[j][i] * ma[i][k];
        m3[j][k] = tmp;
      }
    }
    copymatrix(m3);
  }

  /**
   * Moltiplica la matrice corrente per una matrice scalatura costruita con i valori passati
   * @param z
   */
  public void ScalaMatrix(double x, double y, double z)
  {
    double n[][] = new double[4][4];
    copymatrix(n, Identita);
    n[ 0][ 0] = x;
    n[ 1][ 1] = y;
    n[ 2][ 2] = z;
    MultMatrix(n);
  }

  /**
   * Moltiplica la matrice corrente per una matrice traslazione costruita con i valori passati
   * @param z
   */
  public void TraslaMatrix(double x, double y, double z)
  {
    double n[][] = new double[4][4];
    copymatrix(n, Identita);
    n[ 3][ 0] = x;
    n[ 3][ 1] = y;
    n[ 3][ 2] = z;
    MultMatrix(n);
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano ZY introno all'asse X.
   * L'angolo e' espresso in radianti.
   * @param angolo
   */
  public void RotateXMatrix(double angolo)
  {
    RotateXMatrix(Math.sin(angolo), Math.cos(angolo));
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano XZ introno all'asse Y.
   * Il sistema di coordinate a cui ci si riferisce non e' il sistema cartesiano
   * classico: il piano XZ ha lo zero in alto a sinistra se visto dall'alto.
   * L'angolo e' espresso in radianti.
   * @param angolo
   */
  public void RotateYMatrix(double angolo)
  {
    RotateYMatrix(Math.sin(angolo), Math.cos(angolo));
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano XZ introno all'asse Y.
   * Il sistema di coordinate a cui ci si riferisce e' il sistema cartesiano
   * classico: il piano XZ ha lo zero in basso a sinistra se visto dall'alto.
   * L'angolo e' espresso in radianti.
   * @param angolo
   */
  public void RotateYMatrixCart(double angolo)
  {
    RotateYMatrixCart(Math.sin(angolo), Math.cos(angolo));
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano XY introno all'asse Z.
   * L'angolo e' espresso in radianti.
   * @param angolo
   */
  public void RotateZMatrix(double angolo)
  {
    RotateZMatrix(Math.sin(angolo), Math.cos(angolo));
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano ZY introno all'asse X.
   * s e c rappresentano il seno e il coseno dell'angolo di rotazione desiderato.
   * @param c
   */
  public void RotateXMatrix(double s, double c)
  {
    double n[][] = new double[4][4];
    copymatrix(n, Identita);
    n[ 1][ 1] = n[ 2][ 2] = c;
    n[ 1][ 2] = s;
    n[ 2][ 1] = -n[ 1][ 2];
    MultMatrix(n);
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano XZ introno all'asse Y.
   * Il sistema di coordinate a cui ci si riferisce non e' il sistema cartesiano
   * classico: il piano XZ ha lo zero in alto a sinistra se visto dall'alto.
   * s e c rappresentano il seno e il coseno dell'angolo di rotazione desiderato.
   * @param c
   */
  public void RotateYMatrix(double s, double c)
  {
    double n[][] = new double[4][4];
    copymatrix(n, Identita);
    n[ 0][ 0] = n[ 2][ 2] = c;
    n[ 0][ 2] = s;
    n[ 2][ 0] = -n[ 0][ 2];
    MultMatrix(n);
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano XZ introno all'asse Y.
   * Il sistema di coordinate a cui ci si riferisce e' il sistema cartesiano
   * classico: il piano XZ ha lo zero in basso a sinistra se visto dall'alto.
   * s e c rappresentano il seno e il coseno dell'angolo di rotazione desiderato.
   * @param c
   */
  public void RotateYMatrixCart(double s, double c)
  {
    double n[][] = new double[4][4];
    copymatrix(n, Identita);
    n[ 0][ 0] = n[ 2][ 2] = c;
    n[ 0][ 2] = -s;
    n[ 2][ 0] = -n[ 0][ 2];
    MultMatrix(n);
  }

  /**
   * Moltiplica la matrice corrente per una matrice rotazione costruita con il valore passato.
   * La trasformazione provoca una rotazione del piano XY introno all'asse Z.
   * s e c rappresentano il seno e il coseno dell'angolo di rotazione desiderato.
   * @param c
   */
  public void RotateZMatrix(double s, double c)
  {
    double n[][] = new double[4][4];
    copymatrix(n, Identita);
    n[ 0][ 0] = n[ 1][ 1] = c;
    n[ 0][ 1] = s;
    n[ 1][ 0] = -n[ 0][ 1];
    MultMatrix(n);
  }

  /**
   * Moltiplica il punto indicato da p[0], p[1], p[2] (x,y,z) per la matrice
   * ATTENZIONE: occore dividere il risultato per p[3]:
   * double p[] = new double[4];
   * p[0] = x;
   * p[1] = y;
   * p[2] = z;
   * p[3] = 1;
   * mtrasf.MultPoint(p);
   * x = p[0] / p[3];
   * y = p[1] / p[3];
   * z = p[2] / p[3];
   * @param p
   */
  public void MultPoint(double p[])
  {
    int i, j;
    double tmp, o[] = new double[4];

    for(j = 0; j < 4; j++)
    {
      tmp = 0;
      for(i = 0; i < 4; i++)
        tmp += p[i] * m[i][j];
      o[j] = tmp;
    }

    p[0] = o[0];
    p[1] = o[1];
    p[2] = o[2];
    p[3] = o[3];
  }

  /**
   * Copia dei valori della matrice m2 in m1
   * @param m2
   */
  public static void copymatrix(double m1[][], double m2[][])
  {
    for(int k = 0; k < 4; k++)
    {
      for(int j = 0; j < 4; j++)
      {
        m1[k][j] = m2[k][j];
      }
    }
  }

  /**
   * Copia dei valori della matrice m2 in m1
   * @param m2
   */
  public static void copymatrix(double m1[][], float m2[][])
  {
    for(int k = 0; k < 4; k++)
    {
      for(int j = 0; j < 4; j++)
      {
        m1[k][j] = m2[k][j];
      }
    }
  }

  /**
   * Copia dei valori della matrice m1 nella matrice interna di Matrix3
   * @param m1
   */
  public void copymatrix(double m1[][])
  {
    copymatrix(m, m1);
  }

  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    return new Matrix3(this);
  }

  @Override
  public String toString()
  {
    String rv = "Matrix3 {";
    for(int k = 0; k < 4; k++)
    {
      rv += "[";
      for(int j = 0; j < 4; j++)
      {
        rv += m[k][j];
      }
      rv += "]";
    }
    rv += "}";

    return rv;
  }

  public final static double Identita[][] =
  {
    {
      1, 0, 0, 0
    },
    {
      0, 1, 0, 0
    },
    {
      0, 0, 1, 0
    },
    {
      0, 0, 0, 1
    }
  };
  public final static double Zero[][] =
  {
    {
      0, 0, 0, 0
    },
    {
      0, 0, 0, 0
    },
    {
      0, 0, 0, 0
    },
    {
      0, 0, 0, 0
    }
  };
}

