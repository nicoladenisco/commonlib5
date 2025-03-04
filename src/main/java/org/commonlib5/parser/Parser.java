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
 * Title: Commonlib
 * Description: Libreria di utilizzo comune.
 * Contiene varie funzioni di utilita'
 * quali calcolo matriciale, ecc.
 * @author Nicola De Nisco
 * @version 1.0
 */
public class Parser extends Calcolatore
{
  protected int numCache = 1;
  protected HashMap<String, Double> cache[] = null;
  protected HashMap<String, String> htVars = new HashMap<>();
  protected int cacheRank = 0;

  public Parser()
  {
  }

  public void init()
     throws ParserException
  {
    setCacheLevels(numCache);
  }

  public void destroy()
     throws ParserException
  {
    cache = null;
    htVars.clear();
  }

  public void FlushAllCache()
     throws ParserException
  {
    setCacheLevels(numCache);
  }

  public void FlushCache(int level)
     throws ParserException
  {
    cache[level].clear();
  }

  public void FlushVariabili()
     throws ParserException
  {
    htVars.clear();
  }

  public void AddVariabile(String nome, String espressione)
     throws ParserException
  {
    htVars.put(nome, espressione);
  }

  public void AddCacheEntry(int Level, String nome, double val)
     throws ParserException
  {
    cache[Level].put(nome, val);
  }

  public void setCacheLevels(int NumLevels)
     throws ParserException
  {
    numCache = NumLevels;
    cache = new HashMap[numCache];
    for(int i = 0; i < cache.length; i++)
      cache[i] = new HashMap<>();
  }

  public int getCacheLevels()
     throws ParserException
  {
    return numCache;
  }

  @Override
  protected synchronized double externValoreVariabile(StringBuilder valAlfa)
     throws Exception
  {
    String nomeVariabile = valAlfa.toString().trim();

    // tenta il prelievo dalla cache
    Double val = getFromCache(nomeVariabile);
    if(val != null)
      return val;

    // estrae l'espressione variabile e la interpreta
    // il risultato verra' inserito nella cache con
    // il rank piu' elevato fra quello di tutte le
    // varibili interessate nella computazione
    String expVar = getFromVariables(nomeVariabile);
    if(expVar != null)
    {
      cacheRank = 0;
      double v = super.parse(expVar);
      cache[cacheRank].put(nomeVariabile, v);
      return v;
    }

    reportError(CalcErrori_NoVar);
    return 0;
  }

  protected Double getFromCache(String nomeVariabile)
  {
    int i = 0;
    Double val = null;
    for(i = 0; i < cache.length; i++)
    {
      HashMap<String, Double> ht = cache[i];
      if((val = ht.get(nomeVariabile)) != null)
        break;
    }

    if(val != null)
    {
      cacheRank = Math.max(cacheRank, i);
      return val;
    }

    return null;
  }

  protected String getFromVariables(String nomeVariabile)
  {
    return (String) htVars.get(nomeVariabile);
  }
}
