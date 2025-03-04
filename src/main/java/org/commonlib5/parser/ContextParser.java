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
 * Parser di espressioni complesse.
 *
 * @author Nicola De Nisco
 */
public class ContextParser extends Calcolatore
{
  private boolean fast = true;
  protected CalcContext context = null;
  protected Stack stkVarResolver = new Stack();

  /**
   * <pre>
   * Elabora una expressione complessa:
   * valore1 = 10*27;
   * valore2 = 14+(valore1*4);
   *
   * Le variabili vengono cercate e create
   * nel context.
   * </pre>
   * @param exp
   * @param context
   * @throws Exception
   */
  public synchronized void calc(String exp, CalcContext context)
     throws Exception
  {
    this.context = context;
    StringTokenizer stok = new StringTokenizer(exp, ";\n");
    while(stok.hasMoreTokens())
    {
      String el = stok.nextToken();
      if(el == null)
        continue;

      el = el.trim();
      if(el.length() == 0 || el.charAt(0) == '#')
        continue;

      int pos = el.indexOf('=');
      if(pos == -1)
        throw new ParserException("L'espressione [" + el + "] non ha un right-value.");

      String rval = el.substring(0, pos);
      String lval = el.substring(pos + 1);

      double val = parse(lval);
      context.put(rval.trim(), val);
    }
  }

  @Override
  protected double externValoreVariabile(StringBuilder valAlfa)
     throws Exception
  {
    synchronized(this)
    {
      String nomeVariabile = valAlfa.toString().trim();

      if(stkVarResolver.search(nomeVariabile) != -1)
        throw new ParserException("Errore di sintassi: l'uso della variabile "
           + nomeVariabile + " compare nella sua stessa risoluzione.");

      stkVarResolver.push(nomeVariabile);
      double val = contextVariabileResolver(nomeVariabile);
      stkVarResolver.pop();
      return val;
    }
  }

  /**
   * Risolve una variabile cercandola nel context.
   * Se la variabile e' un'espressione, verra' valutata e
   * il relativo risultato salvato anch'esso nel context se
   * e' attiva la modalita' fast (default).
   * @param nomeVariabile
   * @return
   * @throws Exception
   */
  protected synchronized double contextVariabileResolver(String nomeVariabile)
     throws Exception
  {
    Object oc = context.getValue(nomeVariabile);
    if(oc != null)
    {
      if(oc instanceof Double)
        return ((Double) oc);

      String vexp = oc.toString();
      double val = parse(vexp);
      if(isFast())
        context.put(nomeVariabile, val);
      return val;
    }

    reportError(CalcErrori_NoVar);
    return 0;
  }

  /**
   * Ritorna vero se la modalita' fast e' attiva.
   * @return the fast
   */
  public boolean isFast()
  {
    return fast;
  }

  /**
   * Attiva/disattiva modalita' fast.
   * Se attiva le variabili che contengono espressioni
   * vengono valutate solo la prima volta e il valore
   * salvato nel context.
   * La modalita' fast e' attiva per default.
   * @param fast the fast to set
   */
  public void setFast(boolean fast)
  {
    this.fast = fast;
  }
}
