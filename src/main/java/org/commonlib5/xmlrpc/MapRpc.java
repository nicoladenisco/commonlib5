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

import java.util.Map;

/**
 * Map con controllo sui valori inseriti.
 * I valori null non vengono inseriti e provocano
 * una rimozione della chiave.
 * Tutti i tipi semplici vengono convertiti in stringa,
 * compresi i valori Date (formattati ISO).
 * Map sono inserite sempre come hashtable.
 * List sono inserite sempre come vector.
 * Set sono inseriti sempre come vector.
 *
 * @author Nicola De Nisco
 */
public class MapRpc extends HashtableRpc
{
  public MapRpc()
  {
  }

  public MapRpc(Map<? extends String, ? extends Object> map)
  {
    super(map);
  }

  public MapRpc(Object... params)
  {
    putPair(params);
  }

  public void putPair(Object... params)
  {
    if((params.length & 1) != 0)
      throw new IllegalArgumentException("Params list must be pair.");

    for(int i = 0; i < params.length; i++, i++)
    {
      Object key = params[i];
      Object val = params[i + 1];

      put(key.toString(), val);
    }
  }
}
