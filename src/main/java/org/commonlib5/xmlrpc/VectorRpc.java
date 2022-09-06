/*
 *  Copyright (C) 2014 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 *
 *  Creato il 10-ott-2014, 10.35.01
 */
package org.commonlib5.xmlrpc;

import java.util.*;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Vector con controllo sui valori inseriti.
 * I valori null non vengono inseriti.
 * Tutti i tipi semplici vengono convertiti in stringa,
 * compresi i valori Date (formattati ISO).
 * Map sono inserite sempre come hashtable.
 * List sono inserite sempre come vector.
 * Set sono inseriti sempre come vector.
 *
 * @author Nicola De Nisco
 */
public class VectorRpc extends Vector
{
  public VectorRpc()
  {
  }

  public VectorRpc(int initialCapacity)
  {
    super(initialCapacity);
  }

  public VectorRpc(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  public VectorRpc(Collection c)
  {
    super(c.size());
    for(Object o : c)
      add(o);
  }

  @Override
  public synchronized boolean add(Object value)
  {
    if(value == null)
      return false;

    if(value instanceof String)
      value = StringOper.okStrNull(value.toString());

    if(value == null)
      return false;

    return super.add(value);
  }

  public synchronized boolean add(int value)
  {
    return super.add(Integer.toString(value));
  }

  public synchronized boolean add(long value)
  {
    return super.add(Long.toString(value));
  }

  public synchronized boolean add(double value)
  {
    return super.add(Double.toString(value));
  }

  public synchronized boolean add(boolean value)
  {
    return super.add(value ? "1" : "0");
  }

  public synchronized boolean add(Date value)
     throws Exception
  {
    if(value == null)
      return false;

    return super.add(DateTime.formatIsoFull(value));
  }

  public synchronized boolean add(Map value)
     throws Exception
  {
    if(value == null)
      return false;

    if(value instanceof Hashtable)
      return super.add(value);

    return super.add(new Hashtable(value));
  }

  public synchronized boolean add(List value)
     throws Exception
  {
    if(value == null)
      return false;

    if(value instanceof Vector)
      return super.add(value);

    return super.add(new Vector(value));
  }

  public synchronized boolean add(Set value)
     throws Exception
  {
    if(value == null)
      return false;

    return super.add(new Vector(value));
  }
}
