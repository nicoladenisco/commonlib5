/*
 * Calcolatore.java
 *
 * Created on 24-ago-2009, 11.19.01
 *
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
package org.commonlib5.parser;

import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Parser e calcolatore di espressioni matematiche.
 * Può eseguire il parsing di espressioni anche molto complesse
 * del tipo: (3*ln(45+23))*sin(g2r(75)).
 * Vengono riconosciute variabili e funzioni (le funzioni hanno
 * sempre un argomento fra parentesi), ma in questa implementazione
 * non sono gestite. Vedi Parser.java per una estensione
 * che gestisce variabili e cache.
 *
 * <pre><code>
 * OPERATORI
 * expTest ? exp1 : exp2         se expTest != 0 assegna exp1 altrimenti exp2
 * "^", "%", "/", "*", "+", "-"  classici operatori aritmentici
 * "&gt;", "&lt;", "==", "&gt;=", "&lt;=", "!=" operatori di confronto: ritornano 0 o 1 se è verificato
 * "&lt;|"                          assegna il minimo di due operandi
 * "|&gt;"                          assegna il massimo di due operandi
 *
 *
 * FUNZIONI BUILT IN
 * pi()         costante PI greco
 * pi2()        costante PI/2
 * pi4()        costante PI/4
 * p1i()        costante 1/PI
 * p2i()        costante 2/PI
 * p1s()        costante 1/radice quadrata di PI
 * p2s()        costante 2/radice quadrata di PI
 * pow10(n)     ritorna 10^n
 * sin(n)       funzione seno
 * sqr(n)       funzione radice quadrata
 * sqr2()       costante radice quadrata di 2
 * sqr22()      costante 1/radice quadrata di 2
 * cos(n)       funzione coseno
 * ceil(n)      arrotonda a intero superiore
 * floor(n)     arrotonda a intero inferiore
 * g2r(n)       converte gradi in radianti
 * r2g(n)       converte radianti in gradi
 * tan(n)       funzione tangente
 * asin(n)      funzione arco seno
 * acos(n)      funzione arco coseno
 * atan(n)      funzione arco tangente
 * abs(n)       ritorna il valore assoluto
 * log(n)       funzione logaritmo
 * ln(n)        funzione logaritmo naturale
 * ln2()        costante logaritmo naturale di 2
 * ln10()       costante logaritmo naturale di 10
 * ne()         numero di nepero (e)
 * nlog2e()     costante logaritmo base 2 di e
 * nlog10e()    costante logaritmo base 10 di e
 * exp(n)       ritorna e^n
 * truncate1(n) arrotonda troncando i decimali a 1 decimale dopo la virgola
 * truncate2(n) arrotonda troncando i decimali a 2 decimale dopo la virgola
 * truncate3(n) arrotonda troncando i decimali a 3 decimale dopo la virgola
 * round(n)     arrotonda all'intero più vicino
 * </code></pre>
 *
 * @author Nicola De Nisco
 */
public class Calcolatore
{
  public static final int CalcErrori_Ok = 0;
  public static final int CalcErrori_Overflow_Stack = 1;
  public static final int CalcErrori_Parentesi = 2;
  public static final int CalcErrori_Sintassi = 3;
  public static final int CalcErrori_NoChar = 4;
  public static final int CalcErrori_OpNotSup = 5;
  public static final int CalcErrori_NoVar = 6;
  public static final int CalcErrori_CacheRankNotExist = 7;
  public static final String chOpers = ":?^%/*-+~!|=<>";
  public static final String[] operazioni =
  {
    ":", "?", "^", "%", "/", "*", "+", "-",
    ">", "<", "==", ">=", "<=", "!=",
    "<|", // minimo
    "|>", // massimo
  };

  /**
   * Classe di supporto per implementare lo
   * stack operazioni.
   */
  public static class sim
  {
    public double val;
    public String op;
  }

  private enum Stati
  {
    idle,
    parsenum,
    parsealpha,
    waitopernum,
    waitopervar,
    waitoperfun,
    waitoperparen
  }

  /**
   * Funzione esterna di parsing.
   * L'espressione viene valutata e il valore restituito.
   * Se si verificano errori Error sara' diverso da Ok.
   * @param toParse stringa con l'espressione da valutare
   * @return valore dell'espressione
   * @throws java.lang.Exception
   */
  public double parse(String toParse)
     throws Exception
  {
    if(toParse == null)
      return 0;

    return parse(new StringReader(toParse));
  }

  /**
   * Funzione esterna di parsing.
   * L'espressione viene valutata e il valore restituito.
   * Se si verificano errori Error sara' diverso da Ok.
   * @param r reader da cui leggere l'espressione
   * @return valore dell'espressione
   * @throws Exception
   */
  public double parse(Reader r)
     throws Exception
  {
    MutableInt pCount = new MutableInt(0);
    double val = pa(new PushbackReader(r), new StringBuilder(), pCount);

    if(pCount.intValue() != 0)
      reportError(CalcErrori_Parentesi);

    return val;
  }

  /**
   * Funzione interna di parsing.
   * Puo' essere chiamata ricorsivamente per risolvere
   * i risultati parziali contenuti all'interno delle parentesi.
   * @param r reader per leggere l'espressione
   * @param parsed un accumulatore per riportare il punto di un errore di sintassi
   * @param pCount contatore delle parentesi
   * @return valore dell'espressione
   * @throws Exception
   */
  protected double pa(PushbackReader r, StringBuilder parsed, MutableInt pCount)
     throws Exception
  {
    StringBuilder bufAlfa = new StringBuilder();
    StringBuilder bufOper = new StringBuilder();

    int sign = 1;	// default segno positivo
    double vPar = 0;

    // alloca stack operazioni
    ArrayList<sim> is = new ArrayList<sim>();

    // parsing della stringa e compilazione stack operazioni
    Stati stato = Stati.idle;
    int cc;
    while((cc = r.read()) != -1)
    {
      char c = (char) cc;
      parsed.append(c);

      if(c == ')')
      {
        pCount.decrement();
        break;
      }

      switch(stato)
      {
        case idle: // inizio parsing espressione
        {
          if(Character.isWhitespace(c))
            continue;

          if(c == '+' || c == '-')
          {
            sign = (c == '-') ? -1 : 1;
            continue;
          }

          if(c == '(')
          {
            // inizio parsing espressione fra parentesi
            pCount.increment();
            vPar = pa(r, parsed, pCount);
            stato = Stati.waitoperparen;
          }
          else if(Character.isDigit(c))
          {
            bufAlfa.delete(0, bufAlfa.length());
            bufAlfa.append(c);
            stato = Stati.parsenum;
          }
          else if(Character.isLetter(c))
          {
            bufAlfa.delete(0, bufAlfa.length());
            bufAlfa.append(c);
            stato = Stati.parsealpha;
          }
          else
            reportSyntaxError(parsed);
          break;
        }

        case parsenum: // parsing item numerico (valore)
        {
          if(isOper(c))
          {
            bufOper.delete(0, bufOper.length());
            bufOper.append(c);
            stato = Stati.waitopernum;
          }
          else if(Character.isDigit(c) || Character.isWhitespace(c) || c == '.')
          {
            bufAlfa.append(c);
          }
          else
            reportSyntaxError(parsed);

          break;
        }

        case parsealpha: // parsing item alfanumerico (variabile o funzione)
        {
          if(isOper(c))
          {
            bufOper.delete(0, bufOper.length());
            bufOper.append(c);
            stato = Stati.waitopervar;
          }
          else if(Character.isLetterOrDigit(c) || Character.isWhitespace(c))
          {
            bufAlfa.append(c);
          }
          else if(c == '(')
          {
            // parsing argomento funzione
            pCount.increment();
            vPar = pa(r, parsed, pCount);
            stato = Stati.waitoperfun;
          }
          else
            reportSyntaxError(parsed);

          break;
        }

        case waitopernum: // parsing operazioni dopo valore numerico
        {
          if(Character.isWhitespace(c))
            continue;

          if(!isOper(c))
          {
            // fine operatore: pushback del carattere
            // e salvataggio su stack operazioni
            r.unread(c);
            parsed.deleteCharAt(parsed.length() - 1);
            double val = parseValue(bufAlfa);
            pushStack(is, sign, val, bufOper);

            sign = 1; // riporta il segno al default
            bufAlfa.delete(0, bufAlfa.length());
            bufOper.delete(0, bufOper.length());
            stato = Stati.idle;
            break;
          }

          bufOper.append(c);
          break;
        }

        case waitopervar: // parsing operazioni dopo variabile
        {
          if(Character.isWhitespace(c))
            continue;

          if(!isOper(c))
          {
            // fine operatore: pushback del carattere
            // e salvataggio su stack operazioni
            r.unread(c);
            parsed.deleteCharAt(parsed.length() - 1);
            pushStack(is, sign, bufAlfa, bufOper);

            sign = 1; // riporta il segno al default
            bufAlfa.delete(0, bufAlfa.length());
            bufOper.delete(0, bufOper.length());
            stato = Stati.idle;
            break;
          }

          bufOper.append(c);
          break;
        }

        case waitoperfun: // parsing operazioni dopo funzione
        {
          if(Character.isWhitespace(c))
            continue;

          if(!isOper(c))
          {
            // fine operatore: pushback del carattere
            // e salvataggio su stack operazioni
            r.unread(c);
            parsed.deleteCharAt(parsed.length() - 1);
            pushStack(is, sign, bufAlfa, vPar, bufOper);

            sign = 1; // riporta il segno al default
            bufAlfa.delete(0, bufAlfa.length());
            bufOper.delete(0, bufOper.length());
            stato = Stati.idle;
            break;
          }

          bufOper.append(c);
          break;
        }

        case waitoperparen: // parsing operazioni dopo espressione fra parentesi
        {
          if(Character.isWhitespace(c))
            continue;

          if(!isOper(c))
          {
            // fine operatore: pushback del carattere
            // e salvataggio su stack operazioni
            r.unread(c);
            parsed.deleteCharAt(parsed.length() - 1);
            pushStack(is, sign, vPar, bufOper);

            sign = 1; // riporta il segno al default
            bufAlfa.delete(0, bufAlfa.length());
            bufOper.delete(0, bufOper.length());
            stato = Stati.idle;
            break;
          }

          bufOper.append(c);
          break;
        }
      }
    }

    // fine del ciclo: completa operazioni in base allo stato del parser
    switch(stato)
    {
      case idle:
        return 0;
      case parsealpha: // parsing item alfanumerico (variabile o funzione)
        pushStack(is, sign, bufAlfa, bufOper);
        break;
      case parsenum: // parsing item numerico (valore)
      case waitopernum: // parsing operazioni dopo valore numerico
        double val3 = parseValue(bufAlfa);
        pushStack(is, sign, val3, bufOper);
        break;
      case waitopervar: // parsing operazioni dopo variabile
        pushStack(is, sign, bufAlfa, bufOper);
        break;
      case waitoperfun: // parsing operazioni dopo funzione
        pushStack(is, sign, bufAlfa, vPar, bufOper);
        break;
      case waitoperparen: // parsing operazioni dopo espressione fra parentesi
        pushStack(is, sign, vPar, bufOper);
        break;
    }

    // calcolo dello stack operazioni
    return calcStack(is);
  }

  /**
   * Ritorna vero se il carattere indicato
   * può comparire in un operatore.
   * @param c
   * @return
   */
  public boolean isOper(char c)
  {
    return chOpers.indexOf(c) != -1;
  }

  /**
   * Confronto di stringhe per le funzioni.
   * @param t
   * @param cmp
   * @param len
   * @return
   */
  protected boolean checkFuncName(String t, String cmp, int len)
  {
    return t.equals(cmp);
  }

  /**
   * Effettua il parsing di un valore numerico.
   * @param val
   * @return
   * @throws Exception
   */
  protected double parseValue(StringBuilder val)
     throws Exception
  {
    return Double.parseDouble(val.toString().trim());
  }

  /**
   * Cerca ed elabora il valore per una funzione.
   * @param valAlfa nome della funzione
   * @param vPar parametro della funzione
   * @return valore elaborato
   * @throws Exception
   */
  protected double testInternalFunction(StringBuilder valAlfa, double vPar)
     throws Exception
  {
    double v = 0;
    String t = valAlfa.toString().trim().toLowerCase();

    switch(t)
    {
      case "pi":
      {
        v = MathConsts.M_PI;
        break;
      }

      case "pi2":
      {
        v = MathConsts.M_PI_2;
        break;
      }

      case "pi4":
      {
        v = MathConsts.M_PI_4;
        break;
      }

      case "p1i":
      {
        v = MathConsts.M_1_PI;
        break;
      }

      case "p2i":
      {
        v = MathConsts.M_2_PI;
        break;
      }

      case "p1s":
      {
        v = MathConsts.M_1_SQRTPI;
        break;
      }

      case "p2s":
      {
        v = MathConsts.M_2_SQRTPI;
        break;
      }

      case "pow10":
      {
        v = Math.pow(10.0, vPar);
        break;
      }

      case "sin":
      {
        v = Math.sin(vPar);
        break;
      }

      case "sqr":
      {
        v = Math.sqrt(vPar);
        break;
      }

      case "sqr2":
      {
        v = MathConsts.M_SQRT2;
        break;
      }

      case "sqr22":
      {
        v = MathConsts.M_SQRT_2;
        break;
      }

      case "cos":
      {
        v = Math.cos(vPar);
        break;
      }

      case "ceil":
      {
        v = Math.ceil(vPar);
        break;
      }

      case "floor":
      {
        v = Math.floor(vPar);
        break;
      }

      case "g2r":
      {
        v = (vPar * MathConsts.M_PI) / 180.0;
        break;
      }

      case "r2g":
      {
        v = (vPar * 180.0) / MathConsts.M_PI;
        break;
      }

      case "tan":
      {
        v = Math.tan(vPar);
        break;
      }

      case "asin":
      {
        v = Math.asin(vPar);
        break;
      }

      case "acos":
      {
        v = Math.acos(vPar);
        break;
      }

      case "atan":
      {
        v = Math.atan(vPar);
        break;
      }

      case "abs":
      {
        v = Math.abs(vPar);
        break;
      }

      case "log":
      {
        v = Math.log10(vPar);
        break;
      }

      case "ln":
      {
        v = Math.log(vPar);
        break;
      }

      case "ln2":
      {
        v = MathConsts.M_LN2;
        break;
      }

      case "ln10":
      {
        v = MathConsts.M_LN10;
        break;
      }

      case "ne":
      {
        v = MathConsts.M_E;
        break;
      }

      case "nlog2e":
      {
        v = MathConsts.M_LOG2E;
        break;
      }

      case "nlog10e":
      {
        v = MathConsts.M_LOG10E;
        break;
      }

      case "exp":
      {
        v = Math.exp(vPar);
        break;
      }

      case "truncate1":
      {
        v = Math.round(vPar * 10) / 10.0;
        break;
      }

      case "truncate2":
      {
        v = Math.round(vPar * 100) / 100.0;
        break;
      }

      case "truncate3":
      {
        v = Math.round(vPar * 1000) / 1000.0;
        break;
      }

      case "round":
      {
        v = Math.round(vPar);
        break;
      }

      default:

        // aggancio con eventuali parsing esterni
        return externValoreFunzione(t, vPar);
    }

    return v;
  }

  /**
   * calcola il valore dello stack operazioni
   * @param stack
   * @return
   * @throws java.lang.Exception
   */
  protected double calcStack(ArrayList<sim> stack)
     throws Exception
  {
    if(stack.size() < 1)
      return 0;
    if(stack.size() == 1)
      return stack.get(0).val;

    for(int k = 0; k < operazioni.length; k++)
    {
      String oper = operazioni[k];

      int j = stack.size() - 1;
      if(j == 0)
        break;

      for(int i = 0; i < j; i++)
      {
        sim s1 = (sim) stack.get(i);
        sim s2 = (sim) stack.get(i + 1);

        if(!s1.op.equals(oper))
          continue;

        double v1 = s2.val;
        double v2 = s1.val;

        switch(s1.op)
        {
          // ATTENZIONE
          // l'operatore ternario per poter funzionare
          // DEVE avere la massima priorita' ed in particolare
          // ':' deve avere priorita' piu' alta di '?'
          case ":":
            if(j < 3 || stack.get(i - 1).op.charAt(0) != '?')
              reportError(CalcErrori_Sintassi);

            if(stack.get(i - 1).val != 0)
            {
              // se vero il valore corretto e' gia' al suo posto
              v1 = v2;
            }
            else
            {
              // se falso il valore corretto viene spostato
              v2 = v1;
            }

            break;

          // l'operazione e' gia'stata effettuata va solo spostato
          case "?":
            v2 = v1;
            break;

          case "+":
            v1 = v2 + v1;
            break;

          case "-":
            v1 = v2 - v1;
            break;

          case "*":
            v1 = v2 * v1;
            break;

          case "/":
            v1 = v2 / v1;
            break;

          case "^":
            v1 = Math.pow(v2, v1);
            break;

          case "%":
            v1 = v2 % v1;
            break;

          case ">":
            v1 = (v2 > v1) ? 1 : 0;
            break;

          case "<":
            v1 = (v2 < v1) ? 1 : 0;
            break;

          case "=":
            v1 = (v1 == v2) ? 1 : 0;
            break;

          case ">=":

            v1 = (v2 >= v1) ? 1 : 0;
            break;

          case "<=":

            v1 = (v2 <= v1) ? 1 : 0;
            break;

          case "!=":

            v1 = (v1 != v2) ? 1 : 0;
            break;

          case "<!":

            v1 = (v1 < v2) ? v1 : v2;
            break; // funzione _min

          case "!>":

            v1 = (v1 > v2) ? v1 : v2;
            break; // funzione _max

          case "==":

            v1 = (v1 == v2) ? v1 : 0;
            break; // funzione _equ

          default:
            // verifica per eventuali operazioni
            // supportate da classi figlie di questa
            v1 = externOperazioni(v1, v2, oper);
            break;
        }

        s2.val = v1;
        stack.remove(i);
        j--;
        i--;
      }
    }

    // il valore finale e' nell'ultimo elemento rimasto sullo stack
    sim sv = (sim) stack.get(0);
    return sv.val;
  }

  /**
   * Salva il valore di una variabile sullo stack operazioni.
   * @param stack
   * @param sign
   * @param alpha
   * @param oper
   * @throws java.lang.Exception
   */
  protected void pushStack(ArrayList<sim> stack, int sign, StringBuilder alpha, StringBuilder oper)
     throws Exception
  {
    double v = externValoreVariabile(alpha);
    pushStack(stack, sign, v, oper);
  }

  /**
   * Salva il valore di ritorno di una funzione sullo stack operazioni.
   * @param stack
   * @param sign
   * @param alpha
   * @param vPar argomento da passare alla funzione
   * @param oper
   * @throws java.lang.Exception
   */
  protected void pushStack(ArrayList<sim> stack, int sign, StringBuilder alpha, double vPar, StringBuilder oper)
     throws Exception
  {
    double v = testInternalFunction(alpha, vPar);
    pushStack(stack, sign, v, oper);
  }

  /**
   * salva il valore v sullo stack operazioni
   * @param stack
   * @param sign
   * @param v
   * @param oper
   * @throws Exception
   */
  protected void pushStack(ArrayList<sim> stack, int sign, double v, StringBuilder oper)
     throws Exception
  {
    sim sm = new sim();
    sm.val = v * sign;
    sm.op = oper.toString().trim();
    stack.add(sm);
  }
  //
  private static String errMsg[] =
  {
    "Risultato corretto, nessun errore",
    "Overflow stack operazioni: usare parentesi",
    "Parentesi non bilanciate",
    "Errore di sintassi",
    "Caratteri non validi",
    "Operatore non supportato",
    "Variabile non trovata",
    "Rank cache non valido",
    null
  };

  /**
   * ritorna una stringa descrittiva dell'errore
   * @param Error
   * @return
   */
  public static String getErrDescr(int Error)
  {
    if(Error < 0 || Error >= errMsg.length)
      return "Errore non definito";

    return errMsg[Error];
  }

  /**
   * Solleva errore generico.
   * @param err
   * @throws java.lang.Exception
   */
  protected void reportError(int err)
     throws Exception
  {
    throw new ParserException(getErrDescr(err));
  }

  /**
   * Solleva errore specificamente per errore di sintassi.
   * Cerca di indicare il punto preciso dell'errore.
   * @param parsed
   * @throws Exception
   */
  protected void reportSyntaxError(StringBuilder parsed)
     throws Exception
  {
    String s = parsed.toString();
    String msg = getErrDescr(CalcErrori_Sintassi) + " posizione " + s.length() + ": " + s + "<==QUI";
    throw new ParserException(msg);
  }

  /**
   * Ridefinibile in classi derivate.
   * Estende la capacità di riconoscere operatori.
   * Questi operatori devono comunque essere una
   * combinazione dei caratteri indicati in chOpers.
   * @param v1 primo operando
   * @param v2 secondo operando
   * @param oper operatore parsato
   * @return risultato dell'operazione
   * @throws Exception in caso di operazione non supportata
   */
  protected double externOperazioni(double v1, double v2, String oper)
     throws Exception
  {
    reportError(CalcErrori_OpNotSup);
    return 0;
  }

  /**
   * Ridefinibile in classi derivate.
   * Estende la capacità di creare funzioni.
   * @param nomeFunzione nome della funzione invocata
   * @param vPar valore del parametro fra parentesi
   * @return valore corrispondente
   * @throws Exception in caso di funzione non supportata
   */
  protected double externValoreFunzione(String nomeFunzione, double vPar)
     throws Exception
  {
    reportError(CalcErrori_OpNotSup);
    return 0;
  }

  /**
   * Ridefinibile in classi derivate.
   * Estende la capacità di creare variabili.
   * @param val nome della variabile
   * @return valore corrispondente alla variabile
   * @throws Exception in caso di variabile non trovata
   */
  protected double externValoreVariabile(StringBuilder val)
     throws Exception
  {
    reportError(CalcErrori_NoVar);
    return 0;
  }
}
