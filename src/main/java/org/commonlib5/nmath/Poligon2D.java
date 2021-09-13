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

import java.io.Serializable;
import java.util.*;

/**
 * Title:        Commonlib
 * Description:  Libreria di utilizzo comune.
 * Contiene varie funzioni di utilita'
 * quali calcolo matriciale, ecc.
 * @author Nicola De Nisco
 * @version 1.0
 */

public class Poligon2D implements Cloneable,Serializable
{
  private Punto2D ptMin, ptMax, ptMed;
  private Vector punti = new Vector(10, 10);
  private Vector linee = new Vector(10, 10);

  public Poligon2D()
  {
  }

  public void clear()
  {
    punti.clear();
    linee.clear();
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    try
    {
      Poligon2D rv = new Poligon2D();
      rv.punti = (Vector)punti.clone();
      if(!linee.isEmpty())
        generaLinee();
      return rv;
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  public List getPunti()
  {
    return punti;
  }

  public List getLinee()
  {
    return linee;
  }

  public void addPunto(Punto2D p)
  {
    punti.add(p);
  }

  public void addPunto(float x, float y)
  {
    punti.add(new Punto2D(x, y));
  }

  /**
   * Genera i bordi (linee) del poligono.
   * @throws java.lang.Exception
   */
  public void generaLinee()
    throws Exception
  {
    linee.clear();
    if(punti.isEmpty())
      throw new Exception("Nessun punto nel poligono.");
    ptMin = (Punto2D)(((Punto2D)(punti.get(0))).clone());
    ptMax = (Punto2D)(((Punto2D)(punti.get(0))).clone());
    for(int i=1 ; i<punti.size() ; i++) {
      int j = (i+1) % (punti.size());
      ptMin.AccumulaMin( ((Punto2D)(punti.get(i))) );
      ptMax.AccumulaMax( ((Punto2D)(punti.get(i))) );
      linee.add(new Linea2D( ((Punto2D)(punti.get(i))), ((Punto2D)(punti.get(j))) ) );
    }
    ptMed = ptMin.piu(ptMax).per(0.5);
  }

  /**
   * Ritorna vero se il punto e' all'interno dei punti minimi
   * e massimi. E' un test preliminare per il contenimento del punto.
   * @return 
   */
  public boolean InBoundary(Punto2D p)
  {
    return p.x >= ptMin.x && p.y >= ptMin.y && p.x <= ptMax.x && p.y <= ptMax.y;
  }

  /**
   * Classico contenimento del punto: si costruisce una linea orizontale
   * passante per il punto e si calcolano le intersezioni con i bordi del
   * poligono; se il numero di intersezioni e' dispari il punto e' all'interno.
   * @return 
   */
  public boolean InSide(Punto2D p)
  {
    int cIntr = 0, cAmbiguous = 0;
    Punto2D intr = new Punto2D();
    Linea2D test = new Linea2D(new Punto2D(p.x - 10, p.y), p);

    if(!InBoundary(p))
      return false;

    Iterator iter = linee.iterator();
    while (iter.hasNext())
    {
      Linea2D l = (Linea2D)(iter.next());
      if(l.IntersecaRette(test, intr) && intr.x < p.x && l.InSide(intr)) {
        // se l'intersezione coincide con uno degli estremi della linea
        // e la linea non e' verticale, viene incrementato il conteggio degli ambigui
        if(l.GetTipo() == 0 &&
           (intr.isEqual(l.getPunto(0)) || intr.isEqual(l.getPunto(1))))
          cAmbiguous++;
        else
          cIntr++;
      }
    }

    // recupero dei punti ambigui
    cIntr += (cAmbiguous / 2);

    return (cIntr & 1) == 1;
  }

  /**
   * Intersezione fra poligono e linea: viene ritornata la serie
   * di punti di intesezione.
   * @param addPuntiLinea se true vengon aggiunti anche gli estemi della linea
   * @return 
   */
  public Punto2D[] getIntersezioni(Linea2D o, boolean addPuntiLinea)
    throws Exception
  {
    Vector v = new Vector(10, 10);
    Punto2D intr = new Punto2D();

    if(addPuntiLinea)
      v.add(o.getPunto(0).clone());

    Iterator iter = linee.iterator();
    while (iter.hasNext())
    {
      Linea2D l = (Linea2D)(iter.next());
      if(l.IntersecaLinee(o, intr)) {
        // se l'intersezione coincide con uno degli estremi delle linee
        // non viene considerata valida
        if(intr.isEqual(l.getPunto(0)) || intr.isEqual(l.getPunto(1)) ||
           intr.isEqual(o.getPunto(0)) || intr.isEqual(o.getPunto(1)) )
          ;
        else
          v.add(intr.clone());
      }
    }

    if(addPuntiLinea)
      v.add(o.getPunto(1).clone());

    return (Punto2D[])v.toArray(new Punto2D[v.size()]);
  }

  /**
   * Intersezione fra poligono e linea: viene ritornata la serie
   * di punti di intesezione. I punti sono ritornati secondo il
   * verso della linea originale ordinati dal primo estremo verso il secondo.
   * @return 
   */
  public Punto2D[] getIntersezioniSorted(Linea2D o, boolean addPuntiLinea)
    throws Exception
  {
    final Punto2D ptRif = o.getPunto(0);
    Punto2D[] ptis = getIntersezioni(o, addPuntiLinea);
    Comparator c = new Comparator() {
      @Override
      public int compare(Object o1, Object o2)
      {
        double diff = ptRif.GetDistance((Punto2D)(o1)) -
                      ptRif.GetDistance((Punto2D)(o2));

        if(diff < 0)  return -1;
        if(diff > 0)  return +1;
        return 0;
      }
    };
    Arrays.sort(ptis, c);
    return ptis;
  }

  /**
   * Data una linea che interseca il poligono ritorna i segmenti (Linea2D)
   * di linea all'interno del poligono.
   * @return 
   */
  public Vector getIntLineeIn(Linea2D o)
    throws Exception
  {
    Vector vLin = new Vector(10, 10);
    Punto2D[] ptis = getIntersezioniSorted(o, true);

    if(InSide(ptis[0]))
    {
      // il primo vertice della linea cade all'interno del
      // poligono: scarta tutti i segmenti dispari
      for(int i=0 ; i<ptis.length-1 ; i++) {
        if((i & 1) != 0)
          continue;
        vLin.add(new Linea2D(ptis[i], ptis[i+1]));
      }
    }
    else
    {
      // il primo vertice della linea e' al di fuori del
      // poligono: scarta tutti i segmenti pari
      for(int i=0 ; i<ptis.length-1 ; i++) {
        if((i & 1) == 0)
          continue;
        vLin.add(new Linea2D(ptis[i], ptis[i+1]));
      }
    }

    return vLin;
  }

  /**
   * Data una linea che interseca il poligono ritorna i segmenti (Linea2D)
   * di linea all'esterno del poligono.
   * @return 
   */
  public Vector getIntLineeOut(Linea2D o)
    throws Exception
  {
    Vector vLin = new Vector(10, 10);
    Punto2D[] ptis = getIntersezioniSorted(o, true);

    if(InSide(ptis[0]))
    {
      // il primo vertice della linea cade all'interno del
      // poligono: scarta tutti i segmenti pari
      for(int i=0 ; i<ptis.length-1 ; i++) {
        if((i & 1) == 0)
          continue;
        vLin.add(new Linea2D(ptis[i], ptis[i+1]));
      }
    }
    else
    {
      // il primo vertice della linea e' al di fuori del
      // poligono: scarta tutti i segmenti dispari
      for(int i=0 ; i<ptis.length-1 ; i++) {
        if((i & 1) != 0)
          continue;
        vLin.add(new Linea2D(ptis[i], ptis[i+1]));
      }
    }

    return vLin;
  }

  /**
   * Come getIntLineeIn() ma ritorna solo la prima linea in uscita
   * @return 
   */
  public Linea2D getIntLineaIn(Linea2D o)
    throws Exception
  {
    // test per contenimento della linea
    if(InSide(o.getPunto(0)) && InSide(o.getPunto(1)))
      return o;

    Vector v = getIntLineeIn(o);
    return v.isEmpty() ? null : (Linea2D)(v.get(0));
  }

  /**
   * Come getIntLineeOut() ma ritorna solo la prima linea in uscita
   * @return 
   */
  public Linea2D getIntLineaOut(Linea2D o)
    throws Exception
  {
    Vector v = getIntLineeOut(o);
    return v.isEmpty() ? null : (Linea2D)(v.get(0));
  }

  public Punto2D getPtMax()
  {
    return ptMax;
  }

  public Punto2D getPtMed()
  {
    return ptMed;
  }

  public Punto2D getPtMin()
  {
    return ptMin;
  }
}

