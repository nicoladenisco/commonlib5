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
package org.commonlib5.parser;

import java.util.*;

/**
 * Contesto per l'elaborazione di espressioni
 * complesse attraverso la classe ContextParser.
 *
 * @author Nicola De Nisco
 */
public class CalcContext
{
  protected Hashtable htContext = new Hashtable();

  public void clear()
  {
    htContext.clear();
  }

  public void put(String key, double val)
  {
    htContext.put(key, val);
  }

  public void put(String key, String exp)
  {
    htContext.put(key, exp);
  }

  public double get(String key)
     throws Exception
  {
    Object o = htContext.get(key);
    if(o == null)
      throw new Exception("Valore inesistente.");

    if(o instanceof Double)
      return ((Double) o);

    throw new Exception("Il valore indicato non è stato elaborato.");
  }

  /**
   * NON USARE: questa funzione è solo per
   * uso interno di ContextParser.
   * Utilizzare piuttosto get(key).
   * @param key
   * @return
   */
  public Object getValue(String key)
  {
    return htContext.get(key);
  }

  public Enumeration keys()
  {
    return htContext.keys();
  }
}
