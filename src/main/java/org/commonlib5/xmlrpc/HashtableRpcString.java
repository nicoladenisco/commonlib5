/*
 * HashtableRpcString.java
 *
 * Created on 28-gen-2013, 12.13.19
 *
 * Copyright (C) 2013 Informatica Medica s.r.l.
 *
 * Questo software è proprietà di Informatica Medica s.r.l.
 * Tutti gli usi non esplicitimante autorizzati sono da
 * considerarsi tutelati ai sensi di legge.
 *
 * Informatica Medica s.r.l.
 * Viale dei Tigli, 19
 * Casalnuovo di Napoli (NA)
 *
 * Creato il 28-gen-2013, 12.13.19
 */
package org.commonlib5.xmlrpc;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Hashtable con controllo sui valori inseriti.
 * I valori null non vengono inseriti e provocano
 * una rimozione della chiave.
 * Tutti i tipi semplici vengono convertiti in stringa,
 * compresi i valori Date (formattati ISO).
 *
 * @author Nicola De Nisco
 */
public class HashtableRpcString extends Hashtable<String, String>
{
  public HashtableRpcString()
  {
    super(64);
  }

  public Object put(String key, Object value)
  {
    if(value == null)
      return remove(key);

    if(value instanceof Collection || value.getClass().isArray())
      throw new IllegalArgumentException("Collections or Arrays are proibited: use HashtableRpc instead.");

    // valori tipi base più pratici in forma di stringa
    if(value instanceof String)
      value = StringOper.okStrNull(value);
    else if(value instanceof java.sql.Date)
      value = DateTime.formatIso((java.sql.Date) value, "");
    else if(value instanceof java.sql.Timestamp)
      value = DateTime.formatIsoFull((java.sql.Timestamp) value, "");
    else if(value instanceof java.util.Date)
      value = DateTime.formatIsoFull((java.util.Date) value, "");

    if(value == null)
      return remove(key);

    return super.put(key, value.toString());
  }

  @Override
  public void putAll(Map t)
  {
    for(Object key : t.keySet())
    {
      Object val = t.get(key);
      this.put(key.toString(), val);
    }
  }

  public HashtableRpcString append(String key, Object value)
  {
    put(key, value);
    return this;
  }

  @Override
  public String put(String key, String value)
  {
    if((value = StringOper.okStrNull(value)) == null)
      return remove(key);

    return super.put(key, value);
  }

  public String put(String key, int value)
  {
    return put(key, Integer.toString(value));
  }

  public String put(String key, double value)
  {
    return put(key, Double.toString(value));
  }

  public String put(String key, boolean value)
  {
    return put(key, value ? "1" : "0");
  }

  public String put(String key, Date value)
     throws Exception
  {
    return put(key, DateTime.formatIsoFull(value));
  }

  public String put(String key, Timestamp value)
     throws Exception
  {
    return put(key, DateTime.formatIsoFull(value));
  }

  public String putDateOnly(String key, Date value)
     throws Exception
  {
    return put(key, DateTime.formatIso(value));
  }

  public String putTimeOnly(String key, Date value)
     throws Exception
  {
    return put(key, StringOper.right(DateTime.formatIsoFull(value), 8));
  }
}
