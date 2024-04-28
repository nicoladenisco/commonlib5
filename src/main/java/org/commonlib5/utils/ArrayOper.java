/*
 * ArrayOper.java
 *
 * Created on 6-giu-2013, 14.58.24
 *
 *Copyright (C) 2013 nicola
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.commonlib5.utils;

import java.util.*;
import java.util.function.Predicate;
import org.commonlib5.lambda.LEU;

/**
 * Operazioni comuni su array.
 *
 * @author Nicola De Nisco
 */
public class ArrayOper
{
  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di stringhe.
   * @param ls lista di stringhe
   * @return array di stringhe
   */
  public static String[] toArrayString(Collection<String> ls)
  {
    String[] rv = new String[ls.size()];
    ls.toArray(rv);
    return rv;
  }

  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di oggetti qualiasiasi.
   * Se l'oggetto contenuto è diverso da null chiama toString()
   * per ottenerne la rappresentazione.
   * @param ls lista di oggetti qualsiasi.
   * @return array di stringhe
   */
  public static String[] toArrayStringGeneric(Collection ls)
  {
    int i = 0;
    String[] rv = new String[ls.size()];
    for(Object o : ls)
      rv[i++] = o == null ? null : o.toString();
    return rv;
  }

  public static int[] toArrayInt(Collection<Integer> ls)
  {
    return ls.stream().mapToInt((i) -> i).toArray();
  }

  public static long[] toArrayLong(Collection<Long> ls)
  {
    return ls.stream().mapToLong((l) -> l).toArray();
  }

  /**
   * Helper per creare velocemente array di interi a partire
   * da liste di oggetti qualiasiasi.
   * Se l'oggetto contenuto è diverso da null chiama toString()
   * per ottenerne la rappresentazione e cerca di convertirla in intero.
   * Valori non convertibili vengono sostituiti con 0.
   * @param ls lista di oggetti qualsiasi.
   * @return array di interi
   */
  public static int[] toArrayIntGeneric(Collection ls)
  {
    return ls.stream().mapToInt((o) -> StringOper.parse(o, 0)).toArray();
  }

  public static float[] toArrayFloat(Collection<Float> ls)
  {
    int i = 0;
    float[] rv = new float[ls.size()];
    for(Float val : ls)
      rv[i++] = val;
    return rv;
  }

  public static double[] toArrayDouble(Collection<Double> ls)
  {
    return ls.stream().mapToDouble((d) -> d).toArray();
  }

  public static char[] toArrayChar(Collection<Character> ls)
  {
    int i = 0;
    char[] rv = new char[ls.size()];
    for(Character val : ls)
      rv[i++] = val;
    return rv;
  }

  /**
   * Converte una collezione di Pair nella relativa matrice bidimensionale.
   * @param ls Collection contenente esclusivamente oggetti Pair.
   * @return matrice
   */
  public static Object[][] toMatrixPairs(Collection ls)
  {
    Object[][] rv = new Object[ls.size()][2];

    int count = 0;
    for(Iterator it = ls.iterator(); it.hasNext();)
    {
      Pair v = (Pair) it.next();
      rv[count][0] = v.first;
      rv[count][1] = v.second;
    }

    return rv;
  }

  /**
   * Converte una collezione di Pair nella relativa matrice bidimensionale.
   * @param ls Collection contenente esclusivamente oggetti Pair.
   * @return matrice
   */
  public static String[][] toMatrixString(Collection<Pair<String, String>> ls)
  {
    String[][] rv = new String[ls.size()][2];

    int count = 0;
    for(Pair<String, String> v : ls)
    {
      rv[count][0] = v.first;
      rv[count][1] = v.second;
      count++;
    }

    return rv;
  }

  /**
   * Converte un array di tipo primitivo nella lista equivalente.
   * @param is array di interi
   * @return lista di interi
   */
  public static List<Integer> asList(final int[] is)
  {
    return new AbstractList<Integer>()
    {
      @Override
      public Integer get(int i)
      {
        return is[i];
      }

      @Override
      public int size()
      {
        return is.length;
      }
    };
  }

  /**
   * Converte un array di tipo primitivo nella lista equivalente.
   * @param is array di long
   * @return lista di Long
   */
  public static List<Long> asList(final long[] is)
  {
    return new AbstractList<Long>()
    {
      @Override
      public Long get(int i)
      {
        return is[i];
      }

      @Override
      public int size()
      {
        return is.length;
      }
    };
  }

  /**
   * Converte un array di tipo primitivo nella lista equivalente.
   * @param is array di float
   * @return lista di Float
   */
  public static List<Float> asList(final float[] is)
  {
    return new AbstractList<Float>()
    {
      @Override
      public Float get(int i)
      {
        return is[i];
      }

      @Override
      public int size()
      {
        return is.length;
      }
    };
  }

  /**
   * Converte un array di tipo primitivo nella lista equivalente.
   * @param is array di dobule
   * @return lista di Double
   */
  public static List<Double> asList(final double[] is)
  {
    return new AbstractList<Double>()
    {
      @Override
      public Double get(int i)
      {
        return is[i];
      }

      @Override
      public int size()
      {
        return is.length;
      }
    };
  }

  /**
   * Converte un array di tipo primitivo nella lista equivalente.
   * @param is array di boolean
   * @return lista di Boolean
   */
  public static List<Boolean> asList(final boolean[] is)
  {
    return new AbstractList<Boolean>()
    {
      @Override
      public Boolean get(int i)
      {
        return is[i];
      }

      @Override
      public int size()
      {
        return is.length;
      }
    };
  }

  /**
   * Converte un array di un tipo nella sua corrispondente lista.
   * @param <T>
   * @param is
   * @return
   */
  public static <T> List<T> asList(T[] is)
  {
    return new ListArray<T>(is);
  }

  public static class ListArray<T> extends AbstractList<T>
  {
    private T[] array;

    public ListArray(T[] array)
    {
      this.array = array;
    }

    @Override
    public T get(int index)
    {
      return array[index];
    }

    @Override
    public int size()
    {
      return array.length;
    }
  }

  public static void ensureSize(ArrayList ls, int reqSize)
  {
    if(reqSize > ls.size())
    {
      ls.ensureCapacity(reqSize);
      for(int i = ls.size(); i < reqSize; i++)
        ls.add(null);
    }
  }

  /**
   * Ritorna intersezione univoca dei valori dei due array.
   * @param s1 array da verificare
   * @param s2 array da verificare
   * @return i valori (univoci) che esistono in entrambi gli array
   */
  public static int[] arraysMatching(int[] s1, int[] s2)
  {
    if(s1.length > s2.length)
      return arraysMatching(s2, s1);

    int c = 0, q = 0;
    int[] result = new int[s1.length];

    for(int i = 0; i < s1.length; i++)
    {
      int test = s1[i];
      for(int j = 0; j < s2.length; j++)
      {
        if(s2[j] == test)
        {
          for(q = 0; q < c; q++)
            if(result[q] == test)
              break;

          if(q == c)
            result[c++] = test;
          break;
        }
      }
    }

    return Arrays.copyOf(result, c);
  }

  public static Set<Integer> asSet(final int[] is)
  {
    return new HashSet<>(asList(is));
  }

  public static Set<Long> asSet(final long[] is)
  {
    return new HashSet<>(asList(is));
  }

  public static <T extends Number> Set<T> asSet(T[] is, Comparator<? super T> comparator)
  {
    return new SetArray<T>(is, comparator);
  }

  public static class SetArray<T extends Number> extends AbstractSet<T>
  {
    private T[] array;

    public SetArray(T[] is, Comparator<? super T> comparator)
    {
      int c = 0, i, j;
      array = (T[]) new Object[is.length];

      for(i = 0; i < is.length; i++)
      {
        T i1 = is[i];

        for(j = 0; j < c; j++)
        {
          if(i1.equals(array[j]))
            break;
        }

        if(j == c)
          array[c++] = i1;
      }

      array = Arrays.copyOf(array, c);
      Arrays.sort(is, comparator);
    }

    @Override
    public int size()
    {
      return array.length;
    }

    @Override
    public Iterator<T> iterator()
    {
      return new Iterator<T>()
      {
        int c = 0;

        @Override
        public boolean hasNext()
        {
          return c < array.length;
        }

        @Override
        public T next()
        {
          return array[c++];
        }
      };
    }
  }

  /**
   * Spezza una lista in varie parti che non eccedano la massima lunghezza specificata.
   * Le liste prodotte sono bilanciate, ovvero hanno la stessa dimensione a parte il residuo.
   * @param <T> tipo generico
   * @param coll lista di oggetti
   * @param maxSize dimensione massima richiesta
   * @return lista di liste della dimensione richiesta
   */
  public static <T> List<List<T>> splitList(List<T> coll, int maxSize)
  {
    ArrayList<List<T>> rv = new ArrayList<>();

    // numero di elementi inferiore: non serve splittare
    if(coll.size() < maxSize)
    {
      rv.add(coll);
      return rv;
    }

    // calcola numero di slice richieste
    int num = coll.size() / maxSize;
    if((coll.size() % maxSize) != 0)
      num++;

    // calcola numero di elementi in ogni slice
    int itm = coll.size() / num;

    int prev = 0;
    for(int i = 0; i < num; i++)
    {
      // popola le slice; l'ultima prende il residuo
      if((i + 1) < num)
        rv.add(coll.subList(prev, prev + itm));
      else
        rv.add(coll.subList(prev, coll.size()));

      prev += itm;
    }

    return rv;
  }

  /**
   * Spezza una lista in pezzi.
   * @param <T> tipo generico
   * @param coll lista di oggetti
   * @param numSlice numero di pezzi da produrre
   * @return lista di liste
   */
  public static <T> List<List<T>> sliceList(List<T> coll, int numSlice)
  {
    ArrayList<List<T>> rv = new ArrayList<>();

    // calcola numero di elementi in ogni slice
    int itm = coll.size() / numSlice;

    int prev = 0;
    for(int i = 0; i < numSlice; i++)
    {
      // popola le slice; l'ultima prende il residuo
      if((i + 1) < numSlice)
        rv.add(coll.subList(prev, prev + itm));
      else
        rv.add(coll.subList(prev, coll.size()));

      prev += itm;
    }

    return rv;
  }

  /**
   * Fonde più collection in una unica lista.
   * @param <T>
   * @param args
   * @return
   */
  public static <T> List<T> mergeCollection(Collection<T>... args)
  {
    ArrayList<T> rv = new ArrayList<>();

    for(Collection<T> c : args)
    {
      if(c != null && !c.isEmpty())
        rv.addAll(c);
    }

    return rv;
  }

  /**
   * Fonde due array in un unico risultato.
   * @param <T> tipo dell'array
   * @param first primo array
   * @param second secondo array
   * @return un array con tutti gli elementi
   */
  public static <T> T[] concat(T[] first, T[] second)
  {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Fonde array multipli in un unico risultato.
   * @param <T> tipo dell'array
   * @param first primo array
   * @param rest altri array dello stesso tipo
   * @return un array con tutti gli elementi
   */
  public static <T> T[] concatAll(T[] first, T[]... rest)
  {
    int totalLength = first.length;
    for(T[] array : rest)
      totalLength += array.length;

    T[] result = Arrays.copyOf(first, totalLength);
    int offset = first.length;

    for(T[] array : rest)
    {
      System.arraycopy(array, 0, result, offset, array.length);
      offset += array.length;
    }

    return result;
  }

  /**
   * Fonde array multipli in un unico risultato.
   * @param first primo array
   * @param rest altri array dello stesso tipo
   * @return un array con tutti gli elementi
   */
  public static int[] concatAllInt(int[] first, int[]... rest)
  {
    int totalLength = first.length;
    for(int[] array : rest)
      totalLength += array.length;

    int[] result = Arrays.copyOf(first, totalLength);
    int offset = first.length;

    for(int[] array : rest)
    {
      System.arraycopy(array, 0, result, offset, array.length);
      offset += array.length;
    }

    return result;
  }

  public static Map asMapFromPair(Object... pairObjects)
  {
    ArrayMap rv = new ArrayMap();

    if((pairObjects.length & 1) != 0)
      throw new IllegalArgumentException("The array must have a pair length.");

    for(int i = 0; i < pairObjects.length; i += 2)
    {
      Object o1 = pairObjects[i];
      Object o2 = pairObjects[i + 1];

      rv.put(o1, o2);
    }

    return rv;
  }

  public static Map<String, String> asMapFromPairStrings(String... pairObjects)
  {
    if((pairObjects.length & 1) != 0)
      throw new IllegalArgumentException("The array must have a pair length.");

    ArrayMap<String, String> rv = new ArrayMap<>();
    for(int i = 0; i < pairObjects.length; i += 2)
    {
      String o1 = pairObjects[i];
      String o2 = pairObjects[i + 1];

      rv.put(o1, o2);
    }

    return rv;
  }

  public static List<Pair<String, String>> asListFromPairStrings(String... pairObjects)
  {
    if((pairObjects.length & 1) != 0)
      throw new IllegalArgumentException("The array must have a pair length.");

    List<Pair<String, String>> rv = new ArrayList<>(pairObjects.length / 2);
    for(int i = 0; i < pairObjects.length; i += 2)
    {
      String o1 = pairObjects[i];
      String o2 = pairObjects[i + 1];

      rv.add(new Pair<>(o1, o2));
    }

    return rv;
  }

  public static <T> T[] asArray(T... rest)
  {
    return rest;
  }

  public static int[] asArrayInt(int... rest)
  {
    return rest;
  }

  public static long[] asArrayLong(long... rest)
  {
    return rest;
  }

  public static List<Integer> convertToIntList(Object value, int defVal)
  {
    if(value == null)
      return Collections.EMPTY_LIST;

    if(value instanceof int[])
      return asList((int[]) value);

    if(value instanceof String[])
    {
      String[] ar = (String[]) value;
      ArrayList<Integer> rv = new ArrayList<>(ar.length);
      for(int i = 0; i < ar.length; i++)
        rv.add(StringOper.parse(ar[i], defVal));
      return rv;
    }

    if(value instanceof Object[])
    {
      Object[] ar = (Object[]) value;
      ArrayList<Integer> rv = new ArrayList<>(ar.length);
      for(int i = 0; i < ar.length; i++)
        rv.add(StringOper.parse(value, defVal));
      return rv;
    }

    if(value instanceof String)
    {
      String[] ss = StringOper.split(value.toString(), ',');
      return convertToIntList(ss, defVal);
    }

    if(value instanceof Collection)
    {
      Collection ar = (Collection) value;
      ArrayList<Integer> rv = new ArrayList<>();
      for(Object o : ar)
        rv.add(StringOper.parse(o, defVal));
      return rv;
    }

    return Arrays.asList(StringOper.parse(value, defVal));
  }

  public static int[] filter(int[] source, Predicate<Integer> fun)
  {
    int k = 0;
    int[] rv = new int[source.length];

    for(int i = 0; i < source.length; i++)
    {
      int j = source[i];
      if(fun.test(j))
        rv[k++] = j;
    }

    return Arrays.copyOf(rv, k);
  }

  /**
   * Estrazione di interi da una collezioni di oggetti.
   * @param <T>
   * @param objs collezione di oggetti
   * @param fun espressione lambda per l'estrazione interi
   * @return array di interi senza duplicazioni
   * @throws java.lang.Exception
   */
  public static <T> int[] extractIntArray(Collection<T> objs,
     LEU.ToIntFunction_WithExceptions<T, Exception> fun)
     throws Exception
  {
    return objs.stream()
       .mapToInt(LEU.rethrowFunctionInt(fun))
       .distinct()
       .sorted()
       .toArray();
  }

  /**
   * Estrazione di Stringhe da una collezioni di oggetti.
   * @param <T>
   * @param objs collezione di oggetti
   * @param fun espressione lambda per l'estrazione della stringa
   * @return array di stringhe senza duplicazioni scartando null e stringhe vuote
   * @throws java.lang.Exception
   */
  public static <T> String[] extractStringArray(Collection<T> objs,
     LEU.Function_WithExceptions<T, String, Exception> fun)
     throws Exception
  {
    return objs.stream()
       .map(LEU.rethrowFunction(fun))
       .filter((s) -> s != null && !s.isEmpty())
       .distinct()
       .sorted()
       .toArray(String[]::new);
  }

  /**
   * Estrazione di interi (Integer) da una collezioni di oggetti.
   * @param <T>
   * @param objs collezione di oggetti
   * @param fun espressione lambda per l'estrazione interi
   * @return array di interi senza duplicazioni
   * @throws java.lang.Exception
   */
  public static <T> Integer[] extractIntegerArray(Collection<T> objs,
     LEU.ToIntFunction_WithExceptions<T, Exception> fun)
     throws Exception
  {
    return objs.stream()
       .mapToInt(LEU.rethrowFunctionInt(fun))
       .distinct()
       .sorted()
       .boxed()
       .toArray(Integer[]::new);
  }
}
