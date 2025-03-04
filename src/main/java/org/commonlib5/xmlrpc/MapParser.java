/*
 *  Copyright (C) 2011 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 *
 *  Creato il 25 marzo 2015, 18:53:00
 */
package org.commonlib5.xmlrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.mutable.MutableObject;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Tabella di hashing con funzioni di parser.
 *
 * @author Nicola De Nisco
 */
public class MapParser extends HashMap
{
  public MapParser(Map t)
  {
    super(t);
  }

  public String getAsString(String key)
  {
    return StringOper.okStr(get(key));
  }

  public MutableObject getAsMutableString(String key)
  {
    return new MutableObject(StringOper.okStr(get(key)));
  }

  public void getAsMutableString(String key, MutableObject obj)
  {
    obj.setValue(StringOper.okStr(get(key)));
  }

  public String getAsStringNull(String key)
  {
    return StringOper.okStrNull(get(key));
  }

  public MutableObject getAsMutableStringNull(String key)
  {
    String s = StringOper.okStrNull(get(key));
    return s == null ? null : new MutableObject(s);
  }

  public void getAsMutableStringNull(String key, MutableObject obj)
  {
    String s = StringOper.okStrNull(get(key));
    if(s != null)
      obj.setValue(s);
  }

  public int getAsInt(String key)
  {
    return getAsInt(key, 0);
  }

  public double getAsDouble(String key)
  {
    return getAsDouble(key, 0.0);
  }

  public boolean getAsBoolean(String key)
  {
    return getAsBoolean(key, false);
  }

  public Date getAsDate(String key)
  {
    return getAsDate(key, null);
  }

  public String getAsString(String key, String defVal)
  {
    return StringOper.okStr(get(key), defVal);
  }

  public int getAsInt(String key, int defVal)
  {
    Object rv = super.get(key);

    if(rv == null)
      return defVal;

    if(rv instanceof Number)
      return ((Number) rv).intValue();

    return StringOper.parse(rv, defVal);
  }

  public double getAsDouble(String key, double defVal)
  {
    Object rv = super.get(key);

    if(rv == null)
      return defVal;

    if(rv instanceof Number)
      return ((Number) rv).doubleValue();

    return StringOper.parse(rv, defVal);
  }

  public boolean getAsBoolean(String key, boolean defVal)
  {
    Object rv = super.get(key);

    if(rv == null)
      return defVal;

    if(rv instanceof Boolean)
      return (Boolean) rv;

    return StringOper.checkTrueFalse(rv, defVal);
  }

  public Date getAsDate(String key, Date defVal)
  {
    Object rv = super.get(key);

    if(rv == null)
      return defVal;

    if(rv instanceof Date)
      return (Date) rv;

    return DateTime.parseIsoFull(rv.toString(), defVal);
  }

  public Number getAsNumber(String key, Number defVal)
  {
    Object rv = super.get(key);

    if(rv == null)
      return defVal;

    if(rv instanceof Number)
      return (Number) rv;

    return StringOper.parse(rv.toString(), defVal.doubleValue());
  }

  public List getAsList(String key)
  {
    return getAsList(key, Collections.EMPTY_LIST);
  }

  public List getAsList(String key, List defVal)
  {
    Object rv = super.get(key);

    if(rv == null)
      return defVal;

    if(rv instanceof List)
      return (List) rv;

    if(rv instanceof Object[])
      return Arrays.asList((Object[]) rv);

    if(rv instanceof Collection)
      return new ArrayList((Collection) rv);

    return defVal;
  }

  public String getAsStringByList(String key)
  {
    return getAsStringByList(key, ':');
  }

  public String getAsStringByList(String key, char separator)
  {
    try
    {
      List emailList = getAsList(key);
      return StringOper.join(emailList.iterator(), separator);
    }
    catch(Exception ex)
    {
      return getAsString(key);
    }
  }

  public MapParser getAsMap(String key)
  {
    return getAsMap(key, null);
  }

  public MapParser getAsMap(String key, Map defVal)
  {
    Object obj = get(key);

    if(obj != null && (obj instanceof Map))
      return new MapParser((Map) obj);

    if(defVal != null && (defVal instanceof MapParser))
      return (MapParser) obj;

    if(defVal != null)
      return new MapParser(defVal);

    return null;
  }
}
