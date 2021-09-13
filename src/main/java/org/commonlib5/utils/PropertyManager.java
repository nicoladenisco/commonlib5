package org.commonlib5.utils;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Gestore di coppie chiave-valore generico.
 * Molto simile all'oggetto Properties di Java,
 * ma con il supporto per la tipizzazione dei valori
 * sia in ingresso che in uscita.
 *
 * @author Nicola De Nisco
 */
public class PropertyManager implements Serializable, Cloneable
{
  protected Properties prop = new Properties();

  public PropertyManager()
  {
  }

  public PropertyManager(File f)
     throws Exception
  {
    load(f);
  }

  public PropertyManager(File f, String encoding)
     throws Exception
  {
    load(f, encoding);
  }

  @Override
  public Object clone()
     throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void addAll(Map m)
  {
    Iterator itrKey = m.keySet().iterator();
    while(itrKey.hasNext())
    {
      Object key = itrKey.next();
      Object val = m.get(key);

      addObject(key.toString(), val);
    }
  }

  public Map getMap()
  {
    return (Map) prop.clone();
  }

  public void remove(String propName)
  {
    prop.remove(propName);
  }

  public void removeAll()
  {
    prop.clear();
  }

  public boolean isProperty(String propName)
  {
    return prop.containsKey(propName);
  }

  public boolean isValue(Object value)
  {
    return prop.containsValue(value);
  }

  public Set<String> getAllKeys()
  {
    return prop.stringPropertyNames();
  }

  public Collection getAllValues()
  {
    return prop.values();
  }

  public Enumeration keys()
  {
    return prop.keys();
  }

  public String getProperty(String key)
  {
    return prop.getProperty(key);
  }

  public void addString(String propName, String val)
  {
    if(val != null)
      prop.setProperty(propName, val);
  }

  public void addInt(String propName, int val)
  {
    prop.setProperty(propName, Integer.toString(val));
  }

  public void addLong(String propName, long val)
  {
    prop.setProperty(propName, Long.toString(val));
  }

  public void addFloat(String propName, float val)
  {
    prop.setProperty(propName, Float.toString(val));
  }

  public void addDouble(String propName, double val)
  {
    prop.setProperty(propName, Double.toString(val));
  }

  public void addBoolean(String propName, boolean val)
  {
    prop.setProperty(propName, val ? "true" : "false");
  }

  public void addDate(String propName, Date val)
  {
    prop.setProperty(propName, DateTime.formatIsoFull(val, ""));
  }

  public void addObject(String propName, Object val)
  {
    if(val == null)
      return;

    if(val instanceof String)
    {
      addString(propName, (String) val);
    }
    if(val instanceof Integer)
    {
      addInt(propName, (Integer) val);
    }
    if(val instanceof Long)
    {
      addLong(propName, (Long) val);
    }
    if(val instanceof Float)
    {
      addFloat(propName, (Float) val);
    }
    if(val instanceof Double)
    {
      addDouble(propName, (Double) val);
    }
    if(val instanceof Boolean)
    {
      addBoolean(propName, (Boolean) val);
    }
    if(val instanceof Date)
    {
      addDate(propName, (Date) val);
    }
    else
    {
      addString(propName, val.toString());
    }
  }

  public String getString(String propName)
  {
    return getString(propName, null);
  }

  public int getInt(String propName)
  {
    return getInt(propName, 0);
  }

  public long getLong(String propName)
  {
    return getLong(propName, 0);
  }

  public float getFloat(String propName)
  {
    return getFloat(propName, 0.0f);
  }

  public double getDouble(String propName)
  {
    return getDouble(propName, 0.0);
  }

  public boolean getBoolean(String propName)
  {
    return getBoolean(propName, false);
  }

  public Date getDate(String propName)
  {
    return getDate(propName, null);
  }

  public String getString(String propName, String defVal)
  {
    return prop.getProperty(propName, defVal);
  }

  public int getInt(String propName, int defval)
  {
    return StringOper.parse(prop.getProperty(propName), defval);
  }

  public long getLong(String propName, long defval)
  {
    try
    {
      return Long.parseLong(prop.getProperty(propName).trim());
    }
    catch(Exception ex)
    {
      return defval;
    }
  }

  public float getFloat(String propName, float defval)
  {
    return (float) getDouble(propName, defval);
  }

  public double getDouble(String propName, double defval)
  {
    return StringOper.parse(prop.getProperty(propName), defval);
  }

  public boolean getBoolean(String propName, boolean defval)
  {
    return StringOper.checkTrueFalse(prop.getProperty(propName), defval);
  }

  public Date getDate(String propName, Date defval)
  {
    return DateTime.parseIsoFull(prop.getProperty(propName), defval);
  }

  public int[] getArrayInt(String propName, int[] defVal)
  {
    try
    {
      String tmpBuf = prop.getProperty(propName);
      if(tmpBuf == null)
        return defVal;

      String[] sArr = StringOper.split(tmpBuf, '|');
      return StringOper.strarr2intarr(sArr, 0);
    }
    catch(Exception ex)
    {
      return defVal;
    }
  }

  public void addArrayInt(String propName, int[] values,
     boolean sort, boolean unique)
  {
    if(unique)
    {
      HashSet<Integer> hs = new HashSet<Integer>(ArrayOper.asList(values));
      values = ArrayOper.toArrayInt(hs);
    }

    if(sort || unique)
      Arrays.sort(values);

    prop.setProperty(propName, StringOper.join(values, '|'));
  }

  public String[] getArrayString(String propName, String[] defVal)
  {
    try
    {
      String tmpBuf = prop.getProperty(propName);
      if(tmpBuf == null)
        return defVal;

      return StringOper.split(tmpBuf, '|');
    }
    catch(Exception ex)
    {
      return defVal;
    }
  }

  public void addArrayString(String propName, String[] values,
     boolean sort, boolean unique)
  {
    if(unique)
    {
      HashSet<String> hs = new HashSet<String>(ArrayOper.asList(values));
      values = ArrayOper.toArrayString(hs);
    }

    if(sort || unique)
      Arrays.sort(values);

    prop.setProperty(propName, StringOper.join(values, '|'));
  }

  public void load(File f, String encoding)
     throws Exception
  {
    try (FileInputStream is = new FileInputStream(f))
    {
      prop.load(new InputStreamReader(is, encoding));
    }
  }

  public void load(File f)
     throws Exception
  {
    try (FileInputStream is = new FileInputStream(f))
    {
      prop.load(is);
    }
  }

  public void load(InputStream is)
     throws Exception
  {
    prop.load(is);
  }

  public void load(Reader r)
     throws Exception
  {
    prop.load(r);
  }

  public void load(URL url)
     throws Exception
  {
    try(InputStream in = url.openStream())
    {
      load(in);
    }
  }

  public void save(File f)
     throws Exception
  {
    try (FileOutputStream os = new FileOutputStream(f))
    {
      prop.store(os, "Properties");
    }
  }

  public void save(File f, String encoding)
     throws Exception
  {
    try (FileOutputStream os = new FileOutputStream(f))
    {
      prop.store(new OutputStreamWriter(os, encoding), "Properties");
    }
  }

  public void save(OutputStream os)
     throws Exception
  {
    prop.store(os, "Properties");
  }

  public void save(Writer wr)
     throws Exception
  {
    prop.store(wr, "Properties");
  }
}
