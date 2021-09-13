/*
 *  XmlRpcUtils.java
 *  Creato il 21-dic-2011, 16.44.38
 *
 *  Copyright (C) 2011 WinSOFT di Nicola De Nisco
 *
 *  Questo software è proprietà di Nicola De Nisco.
 *  I termini di ridistribuzione possono variare in base
 *  al tipo di contratto in essere fra Nicola De Nisco e
 *  il fruitore dello stesso.
 *
 *  Fare riferimento alla documentazione associata al contratto
 *  di committenza per ulteriori dettagli.
 */
package org.commonlib5.xmlrpc;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Utility per XML-RPC.
 *
 * @author Nicola De Nisco
 */
public class XmlRpcUtils
{
  public static Date parseDate(Object val, Date defVal)
  {
    if(val == null)
      return defVal;

    if(val instanceof Date)
      return (Date) val;

    return DateTime.parseIso(val.toString(), defVal);
  }

  public static List preparaList(List params, boolean preparaKeyUppercase)
  {
    Vector rv = new Vector();
    for(Object value : (List) params)
    {
      if(value != null)
      {
        if(value instanceof Map)
          value = preparaMap((Map) value, preparaKeyUppercase);
        else if(value instanceof List)
          value = preparaList((List) value, preparaKeyUppercase);
        else
          value = StringOper.okStr(value);
      }
      rv.add(value);
    }
    return rv;
  }

  public static Map preparaMap(Map params, boolean preparaKeyUppercase)
  {
    Hashtable rv = new Hashtable();
    Set<Map.Entry<Object, Object>> entrySet = params.entrySet();
    for(Map.Entry<Object, Object> entry : entrySet)
    {
      Object key = entry.getKey();
      Object value = entry.getValue();
      if(key != null && value != null)
      {
        if(value instanceof Map)
          value = preparaMap((Map) value, preparaKeyUppercase);
        else if(value instanceof List)
          value = preparaList((List) value, preparaKeyUppercase);
        else
          value = StringOper.okStr(value);

        if(preparaKeyUppercase)
          rv.put(key.toString().toUpperCase(), value);
        else
          rv.put(key.toString(), value);
      }
    }
    return rv;
  }
}
