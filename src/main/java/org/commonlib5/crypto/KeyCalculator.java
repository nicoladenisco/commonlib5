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
package org.commonlib5.crypto;

import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Calcolo della chiave a partire dai parametri indicati.
 *
 * @author Nicola De Nisco
 */
public class KeyCalculator
{
  private static final Log log = LogFactory.getLog(KeyCalculator.class);

  private final long sed1, sed2;

  public KeyCalculator()
  {
    this.sed1 = 1103515245;
    this.sed2 = 12345;
  }

  public KeyCalculator(long sed1, long sed2)
  {
    this.sed1 = sed1;
    this.sed2 = sed2;
  }

  public long calc(String user_id, String system_password, long time, Object... params)
  {
    long sum = 0;
    sum = sumAdd(sum, user_id);
    sum = sumAdd(sum, Long.toString(time));

    for(Object p : params)
    {
      if(p != null)
        sum = sumAdd(sum, p.toString());
    }

    sum = sumAdd(sum, system_password);
    long rv = scramble(sum);

    if(log.isDebugEnabled())
      log.debug(String.format("user=%s password=%s time=%d params=%s rv=%d sed1=%d sed2=%d",
         user_id, system_password, time, Arrays.toString(params), rv, sed1, sed2
      ));

//    System.out.println(String.format("user=%s password=%s time=%d params=%s rv=%d sed1=%d sed2=%d",
//       user_id, system_password, time, Arrays.toString(params), rv, sed1, sed2
//    ));
    return rv;
  }

  public long sumAdd(long sum, String str)
  {
    long c, k, len = str.length();
    char[] data = str.toCharArray();

    for(int i = 0; i < len; i++)
    {
      c = (long) data[i];
      k = (i + 1) << 4;
      sum = (sum + (c * k)) % 0x10000L;
    }

    return sum;
  }

  public long scramble(long value)
  {
    long next = value;
    long result;

    next *= sed1;
    next += sed2;
    result = (long) (next / 65536) % 2048;
    next *= sed1;
    next += sed2;
    result <<= 11;
    result ^= (long) (next / 65536) % 1024;
    next *= sed1;
    next += sed2;
    result <<= 10;
    result ^= (long) (next / 65536) % 1024;

    // evita risultati negativi
    result = result & 0x8FFFFFFFL;

    return result;
  }
}
