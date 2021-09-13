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
import java.util.Date;
import java.util.Hashtable;
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

  @Override
  public synchronized String put(String key, String value)
  {
    if((value = StringOper.okStrNull(value)) == null)
      return remove(key);

    return super.put(key, value);
  }

  public synchronized String put(String key, int value)
  {
    return put(key, Integer.toString(value));
  }

  public synchronized String put(String key, double value)
  {
    return put(key, Double.toString(value));
  }

  public synchronized String put(String key, boolean value)
  {
    return put(key, value ? "1" : "0");
  }

  public synchronized String put(String key, Date value)
     throws Exception
  {
    return put(key, DateTime.formatIsoFull(value));
  }

  public synchronized String put(String key, Timestamp value)
     throws Exception
  {
    return put(key, DateTime.formatIsoFull(value));
  }

  public synchronized String putDateOnly(String key, Date value)
     throws Exception
  {
    return put(key, DateTime.formatIso(value));
  }

  public synchronized String putTimeOnly(String key, Date value)
     throws Exception
  {
    return put(key, StringOper.right(DateTime.formatIsoFull(value), 8));
  }
}
