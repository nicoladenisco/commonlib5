/*
 * Copyright (C) 2020 Nicola De Nisco
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commonlib5.lambda.FunctionTrowException;

/**
 * Risolutore generico di macro.
 * In una stringa possono apparire costrutti del tipo ${macro}
 * dove 'macro' viene risolto attraverso una map di parametri.<br>
 * Le macro possono essere caricate in forma chiave/valore o in forma chiave/funzione lambda.
 * La funzione lambda riceve come parametro opzionale l'intera stringa dove compare la macro,
 * oppure per le macro con parametri i parametri (il contenuto delle parentesi che racchiudono i parametri).
 * @author Nicola De Nisco
 */
public class MacroResolver
{
  protected final HashMap<String, String> mapValues = new HashMap<>();
  protected final HashMap<String, FunctionTrowException<String, String>> mapFunction = new HashMap<>();
  protected final Date today = new Date();
  public static final Pattern macroPattern = Pattern.compile("\\$\\{([a-z|A-Z|0-9|_]+)\\}");
  public static final Pattern macroPatternParams = Pattern.compile("\\$\\{([a-z|A-Z|0-9|_]+)\\((.+)\\)\\}");

  public void putAll(Map params)
  {
    Iterator it = params.keySet().iterator();
    while(it.hasNext())
    {
      Object key = it.next();
      Object val = params.get(key);
      putValue(StringOper.okStr(key), StringOper.okStr(val));
    }
  }

  public String putValue(String key, String val)
  {
    return mapValues.put(key, val);
  }

  public String getValue(String key)
  {
    return mapValues.get(key);
  }

  public String removeValue(String key)
  {
    return mapValues.remove(key);
  }

  public FunctionTrowException<String, String> putValueFun(String key, FunctionTrowException<String, String> val)
  {
    return mapFunction.put(key, val);
  }

  public FunctionTrowException<String, String> getValueFun(String key)
  {
    return mapFunction.get(key);
  }

  public FunctionTrowException<String, String> removeValueFun(String key)
  {
    return mapFunction.remove(key);
  }

  public void clear()
  {
    mapValues.clear();
    mapFunction.clear();
  }

  public String resolveMacro(String seg)
     throws Exception
  {
    return resolveMacro2(resolveMacro1(seg));
  }

  protected String resolveMacro1(String seg)
     throws Exception
  {
    Matcher m = macroPattern.matcher(seg);

    StringBuffer sb = new StringBuffer();
    while(m.find())
    {
      if(m.groupCount() != 1)
        throw new Exception("Errore di sintassi in " + seg);

      String keyMacro = m.group(1);
      String valMacro = resolveMacro(keyMacro, seg);

      m.appendReplacement(sb, valMacro);
    }
    m.appendTail(sb);

    return sb.toString();
  }

  protected String resolveMacro2(String seg)
     throws Exception
  {
    Matcher m = macroPatternParams.matcher(seg);

    StringBuffer sb = new StringBuffer();
    while(m.find())
    {
      if(m.groupCount() != 2)
        throw new Exception("Errore di sintassi in " + seg);

      String keyMacro = m.group(1);
      String parMacro = m.group(2);
      String valMacro = resolveMacro(keyMacro, parMacro);

      m.appendReplacement(sb, valMacro);
    }
    m.appendTail(sb);

    return sb.toString();
  }

  protected String resolveMacro(String keyMacro, String seg)
     throws Exception
  {
    String sFound = mapValues.get(keyMacro);
    if(sFound != null)
      return sFound;

    FunctionTrowException<String, String> fun = mapFunction.get(keyMacro);
    if(fun != null)
      return fun.apply(seg);

    return "{?" + keyMacro + "?}";
  }

  @Override
  public String toString()
  {
    try
    {
      StringBuilder sb = new StringBuilder(1024);
      Map<String, String> allMacros = getAllMacros();

      for(Map.Entry<String, String> entry : allMacros.entrySet())
      {
        String key = entry.getKey();
        String value = entry.getValue();
        sb.append("${").append(key).append("} = '").append(value).append("'\n");
      }

      return sb.toString();
    }
    catch(Exception ex)
    {
      return "MacroResolver error: " + ex.getMessage();
    }
  }

  public String[] getVarKeys()
  {
    String[] keys = ArrayOper.toArrayString(mapValues.keySet());
    Arrays.sort(keys);
    return keys;
  }

  public String[] getFixedKeys()
  {
    String[] keys = ArrayOper.toArrayString(mapFunction.keySet());
    Arrays.sort(keys);
    return keys;
  }

  public Map<String, String> getAllMacros()
     throws Exception
  {
    TreeMap<String, String> rv = new TreeMap<>();
    rv.putAll(mapValues);

    for(Map.Entry<String, FunctionTrowException<String, String>> entry : mapFunction.entrySet())
    {
      String key = entry.getKey();
      FunctionTrowException<String, String> value = entry.getValue();

      rv.put(key, value.apply(""));
    }

    return rv;
  }
}
