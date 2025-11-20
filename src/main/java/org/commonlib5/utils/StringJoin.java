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
package org.commonlib5.utils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.commonlib5.lambda.FunctionTrowException;
import static org.commonlib5.lambda.LEU.*;

/**
 * Join di stringhe in varie salse.
 * Il valore di default di sizeBuffer è 128 caratteri.
 * <br>
 * ES: String result = StringJoin.build().add("1","2","3").join();
 * <br>
 * Vedi StringJoinTest per ulteriori esempi.
 *
 * @author Nicola De Nisco
 */
public class StringJoin implements Serializable, Cloneable, Iterable<String>
{
  protected final List<String> stringhe = new ArrayList<>();
  protected String separatore, delimitatoreInizio, delimitatoreFine;
  protected int sizeBuffer = 128;
  public static final Pattern respazi = Pattern.compile("\\s+");

  public StringJoin(String separatore)
  {
    this.separatore = separatore;
  }

  public StringJoin(String separatore, String delimitatore)
  {
    this.separatore = separatore;
    this.delimitatoreInizio = this.delimitatoreFine = delimitatore;
  }

  public StringJoin(String separatore, String delimitatoreInizio, String delimitatoreFine)
  {
    this.separatore = separatore;
    this.delimitatoreInizio = delimitatoreInizio;
    this.delimitatoreFine = delimitatoreFine;
  }

  public static StringJoin build()
  {
    return new StringJoin(",");
  }

  public static StringJoin build(String separatore)
  {
    return new StringJoin(separatore);
  }

  public static StringJoin build(String separatore, String delimitatore)
  {
    return new StringJoin(separatore, delimitatore);
  }

  public static StringJoin build(String separatore, String delimitatoreInizio, String delimitatoreFine)
  {
    return new StringJoin(separatore, delimitatoreInizio, delimitatoreFine);
  }

  public static StringJoin buildForSQL(int[] cs)
  {
    return new StringJoin(",").add(cs);
  }

  public static StringJoin buildForSQL(long[] cs)
  {
    return new StringJoin(",").add(cs);
  }

  public static StringJoin buildForSQL(String[] cs)
  {
    return new StringJoin(",", "'").add(cs);
  }

  public static String joinForSQL(int[] cs)
  {
    return new StringJoin(",").add(cs).join();
  }

  public static String joinForSQL(long[] cs)
  {
    return new StringJoin(",").add(cs).join();
  }

  public static String joinForSQL(Collection<? extends Number> cs)
  {
    return new StringJoin(",").addNum(cs).join();
  }

  public static String joinForSQL(String[] cs)
  {
    return new StringJoin(",", "'").add(cs).join();
  }

  public String getSeparatore()
  {
    return separatore;
  }

  public StringJoin setSeparatore(String separatore)
  {
    this.separatore = separatore;
    return this;
  }

  public String getDelimitatoreInizio()
  {
    return delimitatoreInizio;
  }

  public String getDelimitatoreFine()
  {
    return delimitatoreFine;
  }

  public StringJoin setDelimitatore(String delimitatore)
  {
    this.delimitatoreInizio = this.delimitatoreFine = delimitatore;
    return this;
  }

  public StringJoin setDelimitatoreInizio(String delimitatoreInizio)
  {
    this.delimitatoreInizio = delimitatoreInizio;
    return this;
  }

  public StringJoin setDelimitatoreFine(String delimitatoreFine)
  {
    this.delimitatoreFine = delimitatoreFine;
    return this;
  }

  public int getSizeBuffer()
  {
    return sizeBuffer;
  }

  public StringJoin setSizeBuffer(int sizeBuffer)
  {
    this.sizeBuffer = sizeBuffer;
    return this;
  }

  public StringJoin add(String[] cs)
  {
    for(String c : cs)
      if(c != null)
        stringhe.add(c);
    return this;
  }

  public StringJoin addFromIntexToEnd(int fromIndex, String[] cs)
  {
    for(int i = fromIndex; i < cs.length; i++)
    {
      String c = cs[i];
      if(c != null)
        stringhe.add(c);
    }
    return this;
  }

  public StringJoin addFromIntexToIndex(int fromIndex, int endIndex, String[] cs)
  {
    for(int i = fromIndex; i < cs.length && i < endIndex; i++)
    {
      String c = cs[i];
      if(c != null)
        stringhe.add(c);
    }
    return this;
  }

  public StringJoin addFromIntexToLen(int fromIndex, int numElements, String[] cs)
  {
    for(int i = fromIndex; i < cs.length && numElements-- > 0; i++)
    {
      String c = cs[i];
      if(c != null)
        stringhe.add(c);
    }
    return this;
  }

  public StringJoin add(int[] cs)
  {
    for(int c : cs)
      stringhe.add(Integer.toString(c));
    return this;
  }

  public StringJoin add(long[] cs)
  {
    for(long c : cs)
      stringhe.add(Long.toString(c));
    return this;
  }

  public StringJoin add(Object... cs)
  {
    for(Object c : cs)
      if(c != null)
        stringhe.add(c.toString());
    return this;
  }

  public StringJoin add(Collection<String> cs)
  {
    for(String c : cs)
      if(c != null)
        stringhe.add(c);
    return this;
  }

  public StringJoin addNum(Collection<? extends Number> cs)
  {
    for(Number c : cs)
      if(c != null)
        stringhe.add(c.toString());
    return this;
  }

  public StringJoin add(Collection<String> cs, Function<String, String> fun)
  {
    for(String c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public StringJoin addEx(Collection<String> cs, FunctionTrowException<String, String> fun)
     throws Exception
  {
    for(String c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public <T> StringJoin addObjects(Collection<T> cs)
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(c.toString());
    return this;
  }

  public <T> StringJoin addObjects(Collection<T> cs, Function<T, String> fun)
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public <T> StringJoin addObjectsEx(Collection<T> cs, FunctionTrowException<T, String> fun)
     throws Exception
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public <T> StringJoin addObjects(T[] cs)
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(c.toString());
    return this;
  }

  public <T> StringJoin addObjects(T[] cs, Function<T, String> fun)
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public <T> StringJoin addObjectsEx(T[] cs, FunctionTrowException<T, String> fun)
     throws Exception
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public <T> StringJoin addObjects(Stream<T> cs)
  {
    cs.forEach((o) ->
    {
      if(o != null)
        stringhe.add(o.toString());
    });
    return this;
  }

  public <T> StringJoin addObjects(Stream<T> cs, Function<T, String> fun)
  {
    cs.forEach((o) ->
    {
      if(o != null)
        stringhe.add(fun.apply(o));
    });
    return this;
  }

  public <T> StringJoin addObjectsEx(Stream<T> cs, FunctionTrowException<T, String> fun)
     throws Exception
  {
    cs.forEach(c((o) ->
    {
      if(o != null)
        stringhe.add(fun.apply(o));
    }));
    return this;
  }

  public StringJoin sort()
  {
    stringhe.sort((s1, s2) -> StringOper.compare(s1, s2));
    return this;
  }

  public StringJoin sortIgnoreCase()
  {
    stringhe.sort((s1, s2) -> StringOper.compareIgnoreCase(s1, s2));
    return this;
  }

  public StringJoin sort(Comparator<String> c)
  {
    stringhe.sort(c);
    return this;
  }

  public StringJoin distinct()
  {
    List<String> tmp = stringhe.stream()
       .filter((s) -> s != null)
       .sorted().distinct().collect(Collectors.toList());

    stringhe.clear();
    stringhe.addAll(tmp);
    return this;
  }

  public StringJoin clear()
  {
    stringhe.clear();
    return this;
  }

  public StringJoin copyFrom(StringJoin origin)
  {
    stringhe.clear();
    stringhe.addAll(origin.stringhe);

    delimitatoreInizio = origin.delimitatoreInizio;
    delimitatoreFine = origin.delimitatoreFine;
    separatore = origin.separatore;
    return this;
  }

  @Override
  public Object clone()
     throws CloneNotSupportedException
  {
    StringJoin oc = (StringJoin) super.clone();
    return oc.copyFrom(this);
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 73 * hash + Objects.hashCode(this.stringhe);
    hash = 73 * hash + Objects.hashCode(this.separatore);
    hash = 73 * hash + Objects.hashCode(this.delimitatoreInizio);
    hash = 73 * hash + Objects.hashCode(this.delimitatoreFine);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    final StringJoin other = (StringJoin) obj;
    if(!Objects.equals(this.separatore, other.separatore))
      return false;
    if(!Objects.equals(this.delimitatoreInizio, other.delimitatoreInizio))
      return false;
    if(!Objects.equals(this.delimitatoreFine, other.delimitatoreFine))
      return false;
    return Objects.equals(this.stringhe, other.stringhe);
  }

  @Override
  public String toString()
  {
    return "StringJoin{"
       + "stringhe=" + stringhe
       + ", separatore=" + separatore
       + ", delimitatoreInizio=" + delimitatoreInizio
       + ", delimitatoreFine=" + delimitatoreFine
       + '}';
  }

  /**
   * Fonde le sringhe.
   * Stringhe nulle o vuote vengono ignorate.
   * @return la stringa concatenata
   */
  public String join()
  {
    if(stringhe.isEmpty())
      return "";

    if(stringhe.size() == 1 && delimitatoreInizio == null && delimitatoreFine == null)
      return StringOper.okStr(stringhe.get(0));

    int i = 0;
    StringBuilder rv = new StringBuilder(sizeBuffer);
    for(String s : stringhe)
    {
      if(s == null)
        continue;

      String val = s.trim();
      if(val.isEmpty())
        continue;

      if(i++ > 0)
        rv.append(separatore);

      if(delimitatoreInizio != null)
        rv.append(delimitatoreInizio);

      rv.append(val);

      if(delimitatoreFine != null)
        rv.append(delimitatoreFine);
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * da utilizzare come stringa comando per l'attivazione di un eseguibile.
   * Ovvero gli elementi che contengono stringhe sono racchiusi in "" e il separatore è lo spazio.<br>
   * ES: joinCommand() restituisce "c:\program files\my app\pippo.exe" c:\tmp\mio.txt "c:\tmp\mio file.txt"<br>
   * Stringhe nulle o vuote vengono ignorate.
   * I precedenti valori di separatore e delimitatore vengono ignorati.
   * @return la stringa concatenata
   */
  public String joinCommand()
  {
    if(stringhe.isEmpty())
      return "";

    separatore = " ";
    delimitatoreInizio = delimitatoreFine = "\"";

    int i = 0;
    StringBuilder rv = new StringBuilder(sizeBuffer);
    for(String s : stringhe)
    {
      if(s == null)
        continue;

      String val = s.trim();
      if(val.isEmpty())
        continue;

      if(i++ > 0)
        rv.append(separatore);

      if(!respazi.matcher(val).find())
        rv.append(val);
      else
        rv.append(delimitatoreInizio).append(val).append(delimitatoreFine);
    }

    return rv.toString();
  }

  @Override
  public Iterator<String> iterator()
  {
    return stringhe.iterator();
  }

  /**
   * Fonde le stringhe e le riframmenta usando il delimitatore.
   * Il delimitatore ha la sintassi di StringTokenizer.
   * @param delimiter delimitatore per spezzare la stringa fusione
   * @return lista di stringhe
   */
  public List<String> joinAndSplit(String delimiter)
  {
    String tmp = join();
    return StringOper.string2List(tmp, delimiter, true);
  }

  /**
   * Fonde le stringhe e le riframmenta usando il delimitatore.
   * Il delimitatore ha la sintassi di StringTokenizer.
   * I duplicati vengono rimossi.
   * @param delimiter delimitatore per spezzare la stringa fusione
   * @return set di stringhe
   */
  public Set<String> joinAndSplitUnique(String delimiter)
  {
    return new ArraySet<>(joinAndSplit(delimiter));
  }

  public boolean isEmpty()
  {
    return stringhe.isEmpty();
  }

  public int size()
  {
    return stringhe.size();
  }

  public List<String> getStringhe()
  {
    return stringhe;
  }
}
