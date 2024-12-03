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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.commonlib5.lambda.FunctionTrowException;
import static org.commonlib5.lambda.LEU.*;

/**
 * Join di stringhe in varie salse.
 *
 * @author Nicola De Nisco
 */
public class StringJoin
{
  protected final List<String> stringhe = new ArrayList<>();
  protected String separatore, delimitatore;

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

  public static StringJoin buildSQL(int[] cs)
  {
    return new StringJoin(",").add(cs);
  }

  public static StringJoin buildSQL(long[] cs)
  {
    return new StringJoin(",").add(cs);
  }

  public static StringJoin buildSQL(String[] cs)
  {
    return new StringJoin(",", "'").add(cs);
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
    stringhe.addAll(cs);
    return this;
  }

  public StringJoin add(Collection<String> cs, Function<String, String> fun)
  {
    for(String c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public StringJoin add(Collection<String> cs, FunctionTrowException<String, String> fun)
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
        stringhe.add(cs.toString());
    return this;
  }

  public <T> StringJoin addObjects(Collection<T> cs, Function<T, String> fun)
  {
    for(T c : cs)
      if(c != null)
        stringhe.add(fun.apply(c));
    return this;
  }

  public <T> StringJoin addObjects(Collection<T> cs, FunctionTrowException<T, String> fun)
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

  public <T> StringJoin addObjects(Stream<T> cs, FunctionTrowException<T, String> fun)
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

  public String join()
  {
    if(stringhe.isEmpty())
      return "";

    if(stringhe.size() == 1 && delimitatore == null)
      return StringOper.okStr(stringhe.get(0));

    int i = 0;
    StringBuilder rv = new StringBuilder();
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
}
