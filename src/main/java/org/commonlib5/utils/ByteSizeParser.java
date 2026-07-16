/*
 * Copyright (C) 2026 Nicola De Nisco
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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser di dimensioni espresse in byte con suffissi umani.
 *
 * Esempi validi: 512, 4k, 2M, 10gb, 1GiB, 8 kb.
 * Il moltiplicatore usato e' binario (base 1024).
 *
 * @author Nicola De Nisco
 */
public class ByteSizeParser
{
  public static final Pattern BYTE_SIZE_PATTERN = Pattern.compile("^([+-]?\\d+)\\s*([kmgtpe]?)(i?)(b?)$",
     Pattern.CASE_INSENSITIVE);

  private ByteSizeParser()
  {
  }

  /**
   * Converte una dimensione testuale in numero di byte.
   *
   * @param value valore testuale (es. 4k, 2M, 1GiB, 512)
   * @return numero di byte
   * @throws IllegalArgumentException se il formato non e' valido
   */
  public static long parseBytes(String value)
  {
    if(value == null)
      throw new IllegalArgumentException("Byte size is null.");

    String normalized = value.trim();

    if(normalized.isEmpty())
      throw new IllegalArgumentException("Byte size is empty.");

    Matcher matcher = BYTE_SIZE_PATTERN.matcher(normalized);
    if(!matcher.matches())
      throw new IllegalArgumentException("Invalid byte size: '" + value + "'.");

    long amount;
    try
    {
      amount = Long.parseLong(matcher.group(1));
    }
    catch(NumberFormatException ex)
    {
      throw new IllegalArgumentException("Invalid numeric value in byte size: '" + value + "'.", ex);
    }

    long multiplier = getMultiplier(matcher.group(2));

    try
    {
      return Math.multiplyExact(amount, multiplier);
    }
    catch(ArithmeticException ex)
    {
      throw new IllegalArgumentException("Byte size overflow: '" + value + "'.", ex);
    }
  }

  public static long getMultiplier(String unitPrefix)
  {
    if(unitPrefix == null || unitPrefix.isEmpty())
      return 1L;

    switch(unitPrefix.toLowerCase(Locale.ROOT))
    {
      case "k":
        return 1024L;
      case "m":
        return 1024L * 1024L;
      case "g":
        return 1024L * 1024L * 1024L;
      case "t":
        return 1024L * 1024L * 1024L * 1024L;
      case "p":
        return 1024L * 1024L * 1024L * 1024L * 1024L;
      case "e":
        return 1024L * 1024L * 1024L * 1024L * 1024L * 1024L;
      default:
        throw new IllegalArgumentException("Unsupported unit prefix: '" + unitPrefix + "'.");
    }
  }
}
