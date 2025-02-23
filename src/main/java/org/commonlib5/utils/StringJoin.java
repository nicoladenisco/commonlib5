/*
 * Copyright (C) 2024 Nicola De Nisco
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
public class StringJoin implements Serializable, Cloneable
{
  protected final List<String> stringhe = new ArrayList<>();
  protected String separatore, delimitatore;
  protected int sizeBuffer = 128;
  public static final Pattern respazi = Pattern.compile("\\s+");

  public StringJoin(String separatore)
  {
    this.separatore = separatore;
  }

  public StringJoin(String separatore, String delimitatore)
  {
    this.separatore = separatore;
    this.delimitatore = delimitatore;
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

  public String getDelimitatore()
  {
    return delimitatore;
  }

  public StringJoin setDelimitatore(String delimitatore)
  {
    this.delimitatore = delimitatore;
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
    for(Object c : cs)
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

    delimitatore = origin.delimitatore;
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
    hash = 73 * hash + Objects.hashCode(this.delimitatore);
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
    if(!Objects.equals(this.delimitatore, other.delimitatore))
      return false;
    return Objects.equals(this.stringhe, other.stringhe);
  }

  @Override
  public String toString()
  {
    return "StringJoin{"
       + "stringhe=" + stringhe
       + ", separatore=" + separatore
       + ", delimitatore=" + delimitatore + '}';
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

    if(stringhe.size() == 1 && delimitatore == null)
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

      if(delimitatore == null)
        rv.append(val);
      else
        rv.append(delimitatore).append(val).append(delimitatore);
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
    delimitatore = "\"";

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
        rv.append(delimitatore).append(val).append(delimitatore);
    }

    return rv.toString();
  }
}
