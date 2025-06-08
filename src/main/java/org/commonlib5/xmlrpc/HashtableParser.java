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
package org.commonlib5.xmlrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.mutable.MutableObject;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Tabella di hashing con funzioni di parser.
 *
 * @author Nicola De Nisco
 * @deprecate usa MapParser
 */
public class HashtableParser extends Hashtable
{
  public HashtableParser(Map t)
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
}
