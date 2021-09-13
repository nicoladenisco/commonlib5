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

public class Linea2D implements Cloneable,Serializable
{
  public static double epsilonAng = 0.01;
  protected Punto2D p1, p2, b1, b2;
  protected double coeff;
  protected char Tipo;

  public int GetTipo()
  {
    return (int) Tipo;
  }

  public double GetCoeff()
  {
    return coeff;
  }

  public boolean IsNull()
  {
    return Tipo == 3;
  }

  public Linea2D()
  {
    p1 = p2 = b1 = b2 = new Punto2D(0, 0);
    coeff = 0;
  }

  public Linea2D(Punto2D pa, Punto2D pb)
  {
    p1 = pa;
    p2 = pb;
    InitLinea();
  }

  public Linea2D(Punto3D pa, Punto3D pb)
  {
    p1 = new Punto2D(pa.x, pa.z);
    p2 = new Punto2D(pb.x, pb.z);
    InitLinea();
  }

  // NOTA: il terzo parametro serve per differenziare il costruttore XZ
  public Linea2D(Punto3D pa, Punto3D pb, int nullo)
  {
    p1 = new Punto2D(pa.x, pa.y);
    p2 = new Punto2D(pb.x, pb.y);
    InitLinea();
  }

  public Linea2D(Linea2D l)
  {
    p1 = l.p1;
    p2 = l.p2;
    InitLinea();
  }

  public Linea2D(float x1, float y1, float x2, float y2)
  {
    p1 = new Punto2D(x1, y1);
    p2 = new Punto2D(x2, y2);
    InitLinea();
  }

  public Linea2D(double x1, double y1, double x2, double y2)
  {
    p1 = new Punto2D((float) x1, (float) y1);
    p2 = new Punto2D((float) x2, (float) y2);
    InitLinea();
  }

  // inizializza dati interni linea
  public void InitLinea()
  {
    coeff = 0;
    Tipo = (char) InfoLinea();
    if(Tipo == 0)
      coeff = Punto2D.CoeffAng(p1.x, p1.y, p2.x, p2.y);
    b1 = new Punto2D();
    b2 = new Punto2D();
    b1.x = Math.min(p1.x, p2.x) - Punto2D.epsilonX;
    b1.y = Math.min(p1.y, p2.y) - Punto2D.epsilonY;
    b2.x = Math.max(p1.x, p2.x) + Punto2D.epsilonX;
    b2.y = Math.max(p1.y, p2.y) + Punto2D.epsilonY;
  }

  // ritorna distanza di un punto dalla retta
  public double GetDistance(Punto2D p)
  {
    switch(Tipo)
    {
      case 0:
      // caso generico
      {
        double dlc = p.y - coeff * p.x;
        return Math.abs((p1.y - coeff * p1.x - dlc) / Math.sqrt(1.0 + coeff * coeff));
      }
      case 1:
        // caso linea verticale
        return Math.abs(p.x - p1.x);
      case 2:
        // caso linea orizontale
        return Math.abs(p.y - p1.y);
      case 3:
      // caso linea nulla ritorna distanza dal punto
      {
        double x = p.x - p1.x;
        double y = p.y - p1.y;
        return Punto2D.hypot(x, y);
      }
    }
    return 0;
  }

  // informazioni per la linea
  public int InfoLinea()
  {
    // caso linea nulla
    if(p1 == p2)
      return 3;
    // caso linea verticale
    if(Punto2D.equEpsX(p1.x, p2.x))
      return 1;
    // caso linea orizontale
    if(Punto2D.equEpsY(p1.y, p2.y))
      return 2;
    // caso generico
    return 0;
  }

  // intersezione delle rette
  public boolean IntersecaRette(Linea2D l, Punto2D intr)
  {
    int t = Tipo + l.Tipo * 10;

    switch(t)
    {
      case 0:
        if(Math.abs(Math.atan(coeff) - Math.atan(l.coeff)) <= Linea2D.epsilonAng)
        {
          //	if( EquEps( coeff, l.coeff, Punto2D.epsilonX )) {
          // parallele
          return false;
        }
        else
        {
          intr.x =
             (float) (((p1.y - coeff * p1.x) - (l.p1.y - l.coeff * l.p1.x)) / (l.coeff - coeff));
          intr.y = (float) (coeff * intr.x + (p1.y - coeff * p1.x));
          return true;
        }

      case 11:
      case 22:
        // entrambe verticali od orizzontali
        return false;

      case 1:
        // 1a verticale
        intr.x = p1.x;
        intr.y = (float) (l.coeff * (p1.x - l.p1.x) + l.p1.y);
        return true;

      case 2:
        // 1a orizzontale
        intr.x = (float) ((p1.y - l.p1.y) / l.coeff + l.p1.x);
        intr.y = p1.y;
        return true;

      case 10:
        // 2a verticale
        intr.x = l.p1.x;
        intr.y = (float) (coeff * (l.p1.x - p1.x) + p1.y);
        return true;

      case 20:
        // 2a orizzontale
        intr.x = (float) ((l.p1.y - p1.y) / coeff + p1.x);
        intr.y = l.p1.y;
        return true;

      case 21:
        // 1a vert. 2a orizz.
        intr.x = p1.x;
        intr.y = l.p1.y;
        return true;

      case 12:
        // 1a orizz. 2a vert.
        intr.x = l.p1.x;
        intr.y = p1.y;
        return true;

      default:
        // punti coincidenti
        return false;

    }
  }

  // test di contenimento sulle linee
  public boolean InLinea(Linea2D l)
  {
    /*
    return ((( l.b1.x >= b1.x && l.b1.x <= b2.x ) ||	// contenimento su x
    ( l.b2.x >= b1.x && l.b2.x <= b2.x )) &&
    (( l.b1.y >= b1.y && l.b1.y <= b2.y ) ||	// contenimento su y
    ( l.b2.y >= b1.y && l.b2.y <= b2.y ))) ? 1 : 0;
     */

    return ((l.b1.x <= b2.x && l.b2.x >= b1.x) &&
       (l.b1.y <= b2.y && l.b2.y >= b1.y)) ? true : false;
  }

  // test di contenimento sulle linee
  // NOTA: il test effettuato da InLinea in realt tiene conto
  // di 2*Epsilon, dato che sia i punti di una linea che dell'altra
  // sono costruiti con una tolleranza Epsilon; questa  pi precisa!
  public int InLineaFine(Linea2D l)
  {
    float xl1p1 = b1.x + Punto2D.epsilonX / 2;
    float yl1p1 = b1.y + Punto2D.epsilonY / 2;
    float xl1p2 = b2.x - Punto2D.epsilonX / 2;
    float yl1p2 = b2.y - Punto2D.epsilonY / 2;

    float xl2p1 = l.b1.x + Punto2D.epsilonX / 2;
    float yl2p1 = l.b1.y + Punto2D.epsilonY / 2;
    float xl2p2 = l.b2.x - Punto2D.epsilonX / 2;
    float yl2p2 = l.b2.y - Punto2D.epsilonY / 2;

    return ((xl2p1 <= xl1p2 && xl2p2 >= xl1p1) &&
       (yl2p1 <= yl1p2 && yl2p2 >= yl1p1)) ? 1 : 0;
  }

  // intersezione delle sole linee
  public boolean IntersecaLinee(Linea2D l, Punto2D intr)
  {
    if(InLinea(l))
    {
      if(IntersecaRette(l, intr))
      {
        return (InSide(intr) & l.InSide(intr));
      }
    }
    return false;
  }

  // verifica se un punto e' nel quadrato circoscritto alla linea
  public boolean InSide(Punto2D p)
  {
    return p.x >= b1.x && p.x <= b2.x && p.y >= b1.y && p.y <= b2.y;
  }

  // ritorna distanza minima dei vertici di una linea
  public double GetDistance(Linea2D l)
  {
    double dist1 = GetDistance(l.p1);
    double dist2 = GetDistance(l.p2);

    return (dist1 < dist2) ? dist1 : dist2;
  }

  // ritorna il 1 0 -1 a seconda se il punto cade
  // da un lato o dall'altro della linea
  public int HalfPlane(Punto2D p)
  {
    double a, b;
    a = ((double) (p.x - p1.x)) * ((double) (p2.y - p1.y));
    b = ((double) (p2.x - p1.x)) * ((double) (p.y - p1.y));
    if(a > b)
      return (1);
    if(a < b)
      return (-1);
    return (0);
  }

  // calcola l'intersezione con la retta perpendicolare passante per un punto
  // detta anche proiezione del punto sulla retta
  public boolean IntersecaPerpendicolare(Punto2D p, Punto2D intr)
  {
    switch(Tipo)
    {
      case 0:
      // retta in posizione qualsiasi
      {
        double cort = -(1 / coeff); // coefficiente angolare dell'ortogonale
        intr.x = (float) ((p.y - cort * p.x - p1.y + coeff * p1.x) / (coeff - cort));
        intr.y = (float) (coeff * intr.x + p1.y - coeff * p1.x);
      }
      break;
      case 1:
        // retta verticale
        intr.x = p1.x;
        intr.y = p.y;
        break;
      case 2:
        // retta orizontale
        intr.x = p.x;
        intr.y = p1.y;
        break;
      default:
        // linea nulla
        return false;
    }
    return InSide(intr);
  }

  // ritorna una parallela passante per il punto indicato
  public Linea2D ParallelaPerPunto(Punto2D pto)
  {
    switch(Tipo)
    {
      case 0:
      // retta in posizione qualsiasi
      {
        // calcola lunghezza linea
        double LunLinea = p1.GetDistance(p2);
        // calcola differenza della linea reale
        Punto2D Diff = p2.meno(p1);
        // calcola la proiezione di P2 anticipata di 90°
        Punto2D Pt2p = new Punto2D(p1.x + Diff.y, p1.y - Diff.x);
        // calcola differenza della proiezione
        Punto2D Difp = Pt2p.meno(p1);

        // distanza fra la linea e il punto indicato
        double dist = GetDistance(pto);
        if(HalfPlane(pto) != HalfPlane(Pt2p))
          dist = -dist;

        // calcola estremi nuova linea
        Punto2D NewP1 = p1.piu(Difp.per(dist / LunLinea));
        Punto2D NewP2 = NewP1.piu(Diff);

        return new Linea2D(NewP1, NewP2);
      }
      //break;
      case 1:
        // retta verticale
        return new Linea2D(new Punto2D(pto.x, p1.y), new Punto2D(pto.x, p2.y));
      case 2:
        // retta orizontale
        return new Linea2D(new Punto2D(p1.x, pto.y), new Punto2D(p2.x, pto.y));
      default:
        // linea nulla
        return this;
    }
  }

  // ritorna una perpendicolare passante per il punto indicato
  // con estremi nel punto dato e nel punto di intersezione
  public Linea2D PerpendicolarePerPunto(Punto2D pto)
  {
    Punto2D intr = new Punto2D();
    if(IntersecaPerpendicolare(pto, intr))
      return new Linea2D(intr, pto);
    return null;
  }

  // restituisce l'angolo assoluto della linea
  public double GetAngolo()
  {
    Punto2D d = p2.meno(p1);
    switch(Tipo)
    {
      case 0:	// linea generica
      {
        double dta = Math.atan(coeff);
        if(d.x < 0)
        {
          if(d.y < 0)
          {
            return dta + Math.PI;
          }
          else
          {
            return dta + Math.PI;
          }
        }
        else
        {
          if(d.y < 0)
          {
            return dta + 2 * Math.PI;
          }
          else
          {
            return dta;
          }
        }
      }
      case 1:	// linea verticale
        if(d.y > 0)
        {
          return Math.PI / 2;
        }
        else
        {
          return -Math.PI / 2;
        }
      case 2:	// linea orizontale
        if(d.x > 0)
        {
          return 0;
        }
        else
        {
          return -Math.PI;
        }
      default:
        return 0;
    }
  }

  // costruisce una linea perpendicolare passante per uno dei punti
  public Linea2D CostruisciPerpendicolare(boolean ptflag)
  {
    Linea2D temp = new Linea2D();
    switch(Tipo)
    {
      case 0:	// linea generica
      {
        double dist = p1.GetDistance(p2);
        double ang = GetAngolo() + (Math.PI / 2);
        if(ptflag)
        {
          temp.p1 = p2;
          temp.p2.x = (float) (p2.x + dist * Math.cos(ang));
          temp.p2.y = (float) (p2.y + dist * Math.sin(ang));
        }
        else
        {
          temp.p1 = p1;
          temp.p2.x = (float) (p1.x + dist * Math.cos(ang));
          temp.p2.y = (float) (p1.y + dist * Math.sin(ang));
        }
      }
      break;
      case 1:	// linea verticale
        if(ptflag)
        {
          temp.p1 = p2;
          temp.p2.x = p2.x + (p2.y - p1.y);
          temp.p2.y = p2.y;
        }
        else
        {
          temp.p1 = p1;
          temp.p2.x = p1.x + (p2.y - p1.y);
          temp.p2.y = p1.y;
        }
        break;
      case 2:	// linea orizontale
        if(ptflag)
        {
          temp.p1 = p2;
          temp.p2.x = p2.x;
          temp.p2.y = p2.y - (p2.x - p1.x);
        }
        else
        {
          temp.p1 = p1;
          temp.p2.x = p1.x;
          temp.p2.y = p1.y - (p2.x - p1.x);
        }
        break;
      default:
        temp.p1 = p1;
        temp.p2 = p2;
        break;

    }
    temp.InitLinea();
    return temp;
  }

  // operatore di copia
  public Linea2D copy(Linea2D l)
  {
    p1 = l.p1;
    p2 = l.p2;
    b1 = l.b1;
    b2 = l.b2;
    Tipo = l.Tipo;
    coeff = l.coeff;
    return this;
  }

  // operatore per l'estrazione dei vertici
  public Punto2D getPunto(int idx)
  {
    switch(idx)
    {
      case 0:
        return p1;	// primo vertice
      case 1:
        return p2;	// secondo vertice
      case 2:
        return b1;	// circoscritto in alto  a SX
      case 3:
        return b2;	// circoscritto in basso a DX
      case 4:
        return new Punto2D(b1.x, b2.y);	// circoscritto in basso a SX
      case 5:
        return new Punto2D(b2.x, b1.y);	// circoscritto in alto  a DX
    }
    return new Punto2D(0, 0);
  }

  /**
   * Ritorn vero se uno dei vertici coincide con il punto indicato.
   * @param pto
   * @return
   */
  public boolean isVertex(Punto2D pto)
  {
    return pto.isEqual(p1) || pto.isEqual(p2);
  }

  // confronto dei vertici
  public boolean isEqual(Linea2D l2)
  {
    return (p1.isEqual(l2.p1) && p2.isEqual(l2.p2));
  }

  // operatore di intersezione: ritorna la retta/quadrato circoscritta
  public Linea2D intersezione(Linea2D l2)
  {
    if(!InLinea(l2))
      return new Linea2D(new Punto2D(0, 0), new Punto2D(0, 0));

    // primo punto dell'intersezione: è il più grande dei b1
    Punto2D pp1 = new Punto2D(Math.max(b1.x, l2.b1.x), Math.max(b1.y, l2.b1.y));
    // secondo punto dell'intersezione: è il più piccolo dei b2
    Punto2D pp2 = new Punto2D(Math.min(b2.x, l2.b2.x), Math.min(b2.y, l2.b2.y));

    return new Linea2D(pp1, pp2);
  }

  // operatore parallela: ritorna vero se sono linee parallele
  boolean isParallelaLinea(Linea2D l2)
  {
    Punto2D intr = new Punto2D();
    return IntersecaLinee(l2, intr) ? false : true;
  }

  // operatore parallela: ritorna vero se sono rette parallele
  boolean isParallelaRetta(Linea2D l2)
  {
    Punto2D intr = new Punto2D();
    return IntersecaRette(l2, intr) ? false : true;
  }

  @Override
  public Linea2D clone()
  {
    return new Linea2D(p1.x, p1.y, p2.x, p2.y);
  }

  @Override
  public String toString()
  {
    return "x1=" + p1.x + " y1=" + p1.y + " x2=" + p2.x + " y2=" + p2.y;
  }

}

