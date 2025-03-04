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
