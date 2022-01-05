/*
 * Copyright (C) 2022 Nicola De Nisco
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Produce vari formati standard per la data.
 *
 * @author Nicola De Nisco
 */
public class DateStandardFormats
{
  public DateStandardFormats()
  {
    this(null, null);
  }

  public DateStandardFormats(Locale loc, TimeZone tz)
  {
    if(loc != null)
      locale = loc;

    if(tz != null)
      timeZone = tz;

    rfc1123Format = new SimpleDateFormat(RFC1123_PATTERN, locale);
    oldCookieFormat = new SimpleDateFormat(OLD_COOKIE_PATTERN, locale);
    rfc1036Format = new SimpleDateFormat(rfc1036Pattern, locale);
    asctimeFormat = new SimpleDateFormat(asctimePattern, locale);

    rfc1123Format.setTimeZone(timeZone);
    oldCookieFormat.setTimeZone(timeZone);
    rfc1036Format.setTimeZone(timeZone);
    asctimeFormat.setTimeZone(timeZone);
  }

  /**
   * US locale - all HTTP dates are in english
   */
  public Locale locale = Locale.getDefault();

  /**
   * GMT timezone - all HTTP dates are on GMT
   */
  public TimeZone timeZone = TimeZone.getDefault();

  /**
   * format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
   */
  public static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

  /**
   * Format for http response header date field
   */
  public static final String HTTP_RESPONSE_DATE_HEADER = "EEE, dd MMM yyyy HH:mm:ss zzz";

  // format for RFC 1036 date string -- "Sunday, 06-Nov-94 08:49:37 GMT"
  private static final String rfc1036Pattern = "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

  // format for C asctime() date string -- "Sun Nov  6 08:49:37 1994"
  private static final String asctimePattern = "EEE MMM d HH:mm:ss yyyy";

  /**
   * Pattern used for old cookies
   */
  public static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

  /**
   * DateFormat to be used to format dates
   */
  public final DateFormat rfc1123Format;

  /**
   * DateFormat to be used to format old netscape cookies
   */
  public final DateFormat oldCookieFormat;

  /**
   * RFC1036
   */
  public final DateFormat rfc1036Format;

  /**
   * Compatible asctime in C - Apache logs
   */
  public final DateFormat asctimeFormat;
}
