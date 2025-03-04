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
package org.commonlib5.utils;

/**
 * Generatore di sequenze incrementali.
 *
 * @author Nicola De Nisco
 */
public class Sequencer
{
  protected int sequenza[];
  protected int minimi[];
  protected int massimi[];
  protected boolean overrun = false;

  /**
   * Costruisce un sequenziatore con il numero di gradi di
   * liberta' indicato nel costruttore
   * @param numItems
   */
  public Sequencer(int numItems)
  {
    sequenza = new int[numItems];
    minimi = new int[numItems];
    massimi = new int[numItems];
  }

  /**
   * Imposta valore iniziale, minimi e massimi del grado di liberta'
   * @param massimo
   */
  public void setData(int indice, int iniziale, int minimo, int massimo)
  {
    sequenza[indice] = iniziale;
    minimi[indice] = minimo;
    massimi[indice] = massimo;
  }

  /**
   * Incremento unitario del sequenziatore
   */
  public void incrementa()
  {
    incrementa(1);
  }

  /**
   * Incremento generico del sequenziatore
   * ATTENZIONE: l'incremento deve essere minore di massimo[0] * 2
   * @param quanto
   */
  public void incrementa(int quanto)
  {
    for(int i = 0; i < sequenza.length; i++)
    {
      sequenza[i] += quanto;
      quanto = 0;
      if(sequenza[i] >= massimi[i])
      {
        quanto = sequenza[i] - massimi[i];
        sequenza[i] = minimi[i] + quanto;
        quanto = 1;
      }
      else
      {
        break;
      }
    }

    overrun = quanto > 0;
  }

  /**
   * Resetta il sequenziatore
   */
  public void reset()
  {
    System.arraycopy(minimi, 0, sequenza, 0, sequenza.length);
  }

  /**
   * Impostazione arbitraria del sequenziatore
   * @param valori
   */
  public void preset(int[] valori)
  {
    System.arraycopy(valori, 0, sequenza, 0, sequenza.length);
  }

  /**
   * Verfica la condizione di overrun
   * @return 
   */
  public boolean isOverrun()
  {
    return overrun;
  }

  /**
   * Restituisce la sequenza corrente
   * @return 
   */
  public int[] getSequenza()
  {
    int[] arSeq = new int[sequenza.length];
    System.arraycopy(sequenza, 0, arSeq, 0, arSeq.length);
    return arSeq;
  }
}
