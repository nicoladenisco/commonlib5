/*
 *  Classificatore.java
 *  Creato il 23-nov-2015, 14.42.33
 *
 *  Copyright (C) 2015 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 */
package org.commonlib5.utils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;
import org.commonlib5.lambda.FunctionTrowException;
import org.commonlib5.lambda.LEU;

/**
 * Classificatore generico.
 *
 * @author Nicola De Nisco
 * @param <K> chiave della map prodotta
 * @param <V> oggetti presenti nella lista associata alla chiave
 */
public class Classificatore<K, V> extends HashMap<K, List<V>>
   implements Serializable
{
  protected transient FunctionTrowException<V, K> estrattore = null;

  public Classificatore()
  {
  }

  public Classificatore(FunctionTrowException<V, K> estrattore)
  {
    this.estrattore = estrattore;
  }

  public Classificatore(Collection<V> lsVal)
     throws Exception
  {
    aggiungiTutti(lsVal);
  }

  public Classificatore(Stream<V> lsVal)
     throws Exception
  {
    aggiungiTutti(lsVal);
  }

  public Classificatore(Collection<V> lsVal, FunctionTrowException<V, K> estrattore)
     throws Exception
  {
    this.estrattore = estrattore;
    aggiungiTutti(lsVal);
  }

  public Classificatore(Stream<V> lsVal, FunctionTrowException<V, K> estrattore)
     throws Exception
  {
    this.estrattore = estrattore;
    aggiungiTutti(lsVal);
  }

  public K aggiungi(V v)
     throws Exception
  {
    if(v == null)
      return null;

    K k = estraiChiave(v);

    return aggiungi(k, v);
  }

  public K aggiungi(K k, V v)
  {
    if(k != null)
    {
      List<V> ls = listaNuova(k);
      ls.add(v);
    }

    return k;
  }

  public K aggiungiTutti(K k, Collection<V> v)
  {
    if(k != null)
    {
      List<V> ls = listaNuova(k);
      ls.addAll(v);
    }

    return k;
  }

  public K aggiungiTutti(K k, Stream<V> v)
  {
    if(k != null)
    {
      List<V> ls = listaNuova(k);
      v.forEach((t) -> ls.add(t));
    }

    return k;
  }

  public K aggiungiTutti(K k, V[] v)
  {
    if(k != null)
    {
      List<V> ls = listaNuova(k);
      ls.addAll(Arrays.asList(v));
    }

    return k;
  }

  public List<V> listaNuova(K k)
  {
    List<V> ls = get(k);
    if(ls == null)
    {
      ls = new ArrayList<V>();
      put(k, ls);
    }
    return ls;
  }

  public K estraiChiave(V v)
     throws Exception
  {
    if(estrattore == null)
      throw new IllegalArgumentException("FunctionTrowException extractor is null: estraiChiave() must be redefined.");

    return estrattore.apply(v);
  }

  public void aggiungiTutti(Collection<V> lsVal)
     throws Exception
  {
    for(V v : lsVal)
      aggiungi(v);
  }

  public void aggiungiTutti(Stream<V> lsVal)
     throws Exception
  {
    lsVal.forEach(LEU.rethrowConsumer((t) -> aggiungi(t)));
  }

  public boolean contains(V v)
     throws Exception
  {
    return contains(estraiChiave(v), v);
  }

  public boolean contains(K k, V v)
  {
    List<V> ls = get(k);
    return ls == null ? false : ls.contains(v);
  }
}
