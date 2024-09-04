/*
 * KeyCalculator.java
 *
 * Created on 5 gennaio 2007, 16.11
 *
 * Copyright (C) Informatica Medica s.r.l.
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
