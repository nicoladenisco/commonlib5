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
