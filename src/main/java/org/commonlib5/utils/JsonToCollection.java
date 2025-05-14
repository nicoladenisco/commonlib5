/*
 *  JsonToCollection.java
 *
 *  Copyright (C) 2025 Dedalus Italia S.p.A
 *
 *  Questo software è proprietà di Dedalus Italia S.p.A
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Dedalus Italia S.p.A
 *  Via di Collodi, 6/C
 *  50141 Firenze
 */
package org.commonlib5.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Convertitore da json a collections.
 * La conversione è ricorsiva:
 * JSONObject diventano map, JSONArray diventano List.
 *
 * @author Nicola De Nisco
 */
public class JsonToCollection
{
  public Object convert(Object value)
  {
    if(value instanceof JSONArray)
    {
      return convertJson2List((JSONArray) value);
    }

    if(value instanceof JSONObject)
    {
      return convertJson2Map((JSONObject) value);
    }

    return value;
  }

  public Object convertString(Object value)
  {
    if(value instanceof JSONArray)
    {
      return convertJson2ListString((JSONArray) value);
    }

    if(value instanceof JSONObject)
    {
      return convertJson2MapString((JSONObject) value);
    }

    return StringOper.okStr(value);
  }

  public Map<String, Object> convertJson2Map(JSONObject jobj)
  {
    Map<String, Object> rv = new java.util.HashMap<>();

    if(jobj != null)
    {
      jobj.toMap().forEach((key, value) ->
      {
        if(value != null)
        {
          if(value instanceof JSONArray)
          {
            rv.put(key, convertJson2List((JSONArray) value));
          }
          else
          {
            rv.put(key, value);
          }
        }
      });
    }

    return rv;
  }

  public List<Object> convertJson2List(JSONArray jarr)
  {
    List<Object> rv = new ArrayList<>();

    if(jarr != null)
    {
      for(int i = 0; i < jarr.length(); i++)
      {
        Object value = jarr.get(i);

        if(value instanceof JSONObject)
        {
          rv.add(convertJson2Map((JSONObject) value));
        }
        else
        {
          rv.add(value);
        }
      }
    }

    return rv;
  }

  public Map<String, String> convertJson2MapString(JSONObject jobj)
  {
    Map<String, String> rv = new java.util.HashMap<>();

    if(jobj != null)
    {
      jobj.toMap().forEach((key, value) ->
      {
        if(value != null)
        {
          rv.put(key, value.toString());
        }
      });
    }

    return rv;
  }

  public List<String> convertJson2ListString(JSONArray jarr)
  {
    List<String> rv = new ArrayList<>();

    if(jarr != null)
    {
      for(int i = 0; i < jarr.length(); i++)
      {
        Object value = jarr.get(i);
        rv.add(StringOper.okStr(value));
      }
    }

    return rv;
  }
}
