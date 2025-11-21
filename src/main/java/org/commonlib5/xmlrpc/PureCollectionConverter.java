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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.commonlib5.utils.StringOper;

/**
 * Converte collezioni per renderli compatibili con XML-RPC.
 * Rende le collezioni pure ovvero contengono solo map e list.
 * Strutture come Object[] e alre forme di map o list vengono convertite
 * tutte in HashMap, ArrayList e String.
 *
 * @author Nicola De Nisco
 */
public class PureCollectionConverter
{
  public boolean preparaKeyUppercase = false, convertDate = false;

  public PureCollectionConverter()
  {
  }

  public PureCollectionConverter(boolean preparaKeyUppercase)
  {
    this.preparaKeyUppercase = preparaKeyUppercase;
  }

  public PureCollectionConverter(boolean preparaKeyUppercase, boolean convertDate)
  {
    this.preparaKeyUppercase = preparaKeyUppercase;
    this.convertDate = convertDate;
  }

  public List preparaList(List params)
  {
    List rv = new ArrayList();
    for(Object value : (List) params)
    {
      if(value != null)
      {
        if(value instanceof Map)
          value = preparaMap((Map) value);
        else if(value instanceof List)
          value = preparaList((List) value);
        else if(value instanceof Object[])
          value = preparaList(Arrays.asList((Object[]) value));
        else if(value instanceof Date && !convertDate)
          ;
        else
          value = StringOper.okStr(value);
      }
      rv.add(value);
    }
    return rv;
  }

  public Map preparaMap(Map params)
  {
    Map rv = new HashMap();
    Set<Map.Entry<Object, Object>> entrySet = params.entrySet();
    for(Map.Entry<Object, Object> entry : entrySet)
    {
      Object key = entry.getKey();
      Object value = entry.getValue();
      if(key != null && value != null)
      {
        if(value instanceof Map)
          value = preparaMap((Map) value);
        else if(value instanceof List)
          value = preparaList((List) value);
        else if(value instanceof Object[])
          value = preparaList(Arrays.asList((Object[]) value));
        else if(value instanceof Date && !convertDate)
          ;
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
