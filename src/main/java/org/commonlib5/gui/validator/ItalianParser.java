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
package org.commonlib5.gui.validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Implementazione per i formati italiani di ValidatorParserInterface.
 *
 * @author Nicola De Nisco
 */
public class ItalianParser implements ValidatorParserInterface
{
  //
  // formati data ora
  public final DateFormat dfItalian = new SimpleDateFormat("dd/MM/yyyy");
  public final DateFormat dfCompatItalian = new SimpleDateFormat("ddMMyyyy");
  public final DateFormat dfFullItalian = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  public final DateFormat dfTimeItalian = new SimpleDateFormat("HH:mm:ss");
  public final DateFormat dfCompatFullItalian = new SimpleDateFormat("ddMMyyyy HHmmss");
  public final Pattern patItalian = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{3,4}$");
  public final Pattern patItalian1 = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{2}$");
  public final Pattern patFullItalian = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}$");
  public final Pattern patFullItalian1 = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{6}$");
  public final Pattern patFullItalian2 = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{4}$");
  public final Pattern patFullItalian3 = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}$");
  public final Pattern patCompatItalian = Pattern.compile("^[0-9]{8}$");
  public final Pattern patMoreCompatItalian = Pattern.compile("^[0-9]{6}$");
  public final Pattern patCompatFullItalian1 = Pattern.compile("^[0-9]{8} [0-9]{6}$");
  public final Pattern patCompatFullItalian2 = Pattern.compile("^[0-9]{8} [0-9]{4}$");
  public final Pattern patMoreCompatFullItalian1 = Pattern.compile("^[0-9]{6} [0-9]{6}$");
  public final Pattern patMoreCompatFullItalian2 = Pattern.compile("^[0-9]{6} [0-9]{4}$");

  public final Pattern patStringFullItalian1 = Pattern.compile("^([a-z|A-Z]+) ([0-9]{2}:[0-9]{2}:[0-9]{2})$");
  public final Pattern patStringFullItalian2 = Pattern.compile("^([a-z|A-Z]+) ([0-9]{2}:[0-9]{2})$");
  public final Pattern patStringCompatFullItalian1 = Pattern.compile("^([a-z|A-Z]+) ([0-9]{6})$");
  public final Pattern patStringCompatFullItalian2 = Pattern.compile("^([a-z|A-Z]+) ([0-9]{4})$");

  public final Pattern patGiorni = Pattern.compile("^[+|-][0-9]+$");
  public final Pattern patGiorni1 = Pattern.compile("^([+|-][0-9]+) ([0-9]{6})$");
  public final Pattern patGiorni2 = Pattern.compile("^([+|-][0-9]+) ([0-9]{4})$");

  public final Pattern patSettimane = Pattern.compile("^[+|-][0-9]+s$");
  public final Pattern patMesi = Pattern.compile("^[+|-][0-9]+m$");
  public final Pattern patAnni = Pattern.compile("^[+|-][0-9]+a$");
  //
  // formati solo ora
  public final DateFormat tfItalian = new SimpleDateFormat("HH:mm:ss");
  public final DateFormat tfItalianShort = new SimpleDateFormat("HH:mm");
  public final DateFormat tfCompactItalian = new SimpleDateFormat("HHmmss");
  public final DateFormat tfCompactItalianShort = new SimpleDateFormat("HHmm");
  public final Pattern patTimeItalian = Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}$");
  public final Pattern patTimeItalianShort = Pattern.compile("^[0-9]{2}:[0-9]{2}$");
  public final Pattern patCompactTimeItalian = Pattern.compile("^[0-9]{6}$");
  public final Pattern patCompactTimeItalianShort = Pattern.compile("^[0-9]{4}$");

  @Override
  public String fmtDate(Date d)
  {
    try
    {
      return dfItalian.format(d);
    }
    catch(Exception e)
    {
      return "";
    }
  }

  @Override
  public String fmtDateTime(Date d)
  {
    try
    {
      return dfFullItalian.format(d);
    }
    catch(Exception e)
    {
      return "";
    }
  }

  @Override
  public String fmtTime(Date d)
  {
    try
    {
      return dfTimeItalian.format(d);
    }
    catch(Exception e)
    {
      return "";
    }
  }

  @Override
  public Date parseDate(String s, Date defVal)
  {
    return parseDate(s, defVal, 0);
  }

  @Override
  public Date parseDate(String s, Date defVal, int flags)
  {
    Date rv = parseDateInternal(s, defVal, flags);

    if(flags == FLAG_ALWAIS_BEGIN_DAY)
      return DateTime.mergeDataOra(rv, 0, 0, 0, 0);

    if(flags == FLAG_ALWAIS_END_DAY)
      return DateTime.mergeDataOra(rv, 23, 59, 59, 999);

    return rv;
  }

  private Date parseDateInternal(String s, Date defVal, int flags)
  {
    try
    {
      if((s = StringOper.okStrNull(s)) == null)
        return defVal;

      if(StringOper.isEquNocaseAny(s, "oggi", "o", "today", "t"))
        return adjust(flags, new Date());

      if(StringOper.isEquNocaseAny(s, "ieri", "i", "yesterday", "y"))
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return adjust(flags, cal.getTime());
      }

      if(s.equalsIgnoreCase("domani") || s.equalsIgnoreCase("tomorrow")
         || s.equalsIgnoreCase("d") || s.equalsIgnoreCase("t"))
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return adjust(flags, cal.getTime());
      }

      if(patGiorni.matcher(s).matches())
      {
        int spiazzamento = StringOper.parse(s, 0);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, spiazzamento);
        return adjust(flags, cal.getTime());
      }
      if(patSettimane.matcher(s).matches())
      {
        s = s.substring(0, s.length() - 1);
        int spiazzamento = StringOper.parse(s, 0);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.WEEK_OF_YEAR, spiazzamento);
        return adjust(flags, cal.getTime());
      }
      if(patMesi.matcher(s).matches())
      {
        s = s.substring(0, s.length() - 1);
        int spiazzamento = StringOper.parse(s, 0);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, spiazzamento);
        return adjust(flags, cal.getTime());
      }
      if(patAnni.matcher(s).matches())
      {
        s = s.substring(0, s.length() - 1);
        int spiazzamento = StringOper.parse(s, 0);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, spiazzamento);
        return adjust(flags, cal.getTime());
      }

      if(patItalian.matcher(s).matches())
        return adjust(flags, dfItalian.parse(s));

      if(patItalian1.matcher(s).matches())
      {
        String anno = StringOper.right(s, 2);
        if(StringOper.parse(anno, 0) < 50)
          return adjust(flags, dfItalian.parse(s.substring(0, 6) + "20" + anno));
        else
          return adjust(flags, dfItalian.parse(s.substring(0, 6) + "19" + anno));
      }

      if(patFullItalian.matcher(s).matches())
        return dfFullItalian.parse(s);

      if(patFullItalian1.matcher(s).matches())
      {
        String ora = StringOper.right(s, 6);
        String data = StringOper.left(s, 10);
        return dfFullItalian.parse(data + " "
           + ora.substring(0, 2) + ":" + ora.substring(2, 4) + ":" + ora.substring(4, 6));
      }

      if(patFullItalian2.matcher(s).matches())
      {
        String ora = StringOper.right(s, 4);
        String data = StringOper.left(s, 10);
        return dfFullItalian.parse(data + " "
           + ora.substring(0, 2) + ":" + ora.substring(2, 4) + ":00");
      }

      if(patFullItalian3.matcher(s).matches())
      {
        return dfFullItalian.parse(s + ":00");
      }

      if(patCompatItalian.matcher(s).matches())
        return adjust(flags, dfCompatItalian.parse(s));

      if(patMoreCompatItalian.matcher(s).matches())
      {
        String anno = StringOper.right(s, 2);
        if(StringOper.parse(anno, 0) < 50)
          return adjust(flags, dfCompatItalian.parse(s.substring(0, 4) + "20" + anno));
        else
          return adjust(flags, dfCompatItalian.parse(s.substring(0, 4) + "19" + anno));
      }

      if(patCompatFullItalian1.matcher(s).matches())
        return dfCompatFullItalian.parse(s);

      if(patCompatFullItalian2.matcher(s).matches())
        return dfCompatFullItalian.parse(s + "00");

      if(patMoreCompatFullItalian1.matcher(s).matches())
      {
        s = StringOper.left(s, 4) + "19" + StringOper.right(s, 9);
        return dfCompatFullItalian.parse(s);
      }

      if(patMoreCompatFullItalian2.matcher(s).matches())
      {
        s = StringOper.left(s, 4) + "19" + StringOper.right(s, 7) + "00";
        return dfCompatFullItalian.parse(s);
      }

      {
        Matcher m = patStringFullItalian1.matcher(s);
        if(m.matches())
        {
          String giorno = m.group(1);
          String ora = m.group(2);

          return subparse1(giorno, ora, defVal);
        }
      }

      {
        Matcher m = patStringFullItalian2.matcher(s);
        if(m.matches())
        {
          String giorno = m.group(1);
          String ora = m.group(2) + ":00";

          return subparse1(giorno, ora, defVal);
        }
      }

      {
        Matcher m = patStringCompatFullItalian1.matcher(s);
        if(m.matches())
        {
          String giorno = m.group(1);
          String ora = m.group(2);

          return subparse2(giorno, ora, defVal);
        }
      }

      {
        Matcher m = patStringCompatFullItalian2.matcher(s);
        if(m.matches())
        {
          String giorno = m.group(1);
          String ora = m.group(2) + "00";

          return subparse2(giorno, ora, defVal);
        }
      }

      {
        Matcher m = patGiorni1.matcher(s);
        if(m.matches())
        {
          int spiazzamento = StringOper.parse(m.group(1), 0);
          String ora = m.group(2);
          GregorianCalendar cal = new GregorianCalendar();
          cal.setTime(new Date());
          cal.add(Calendar.DAY_OF_YEAR, spiazzamento);
          String tmp = dfCompatItalian.format(cal.getTime()) + " " + ora;
          return dfCompatFullItalian.parse(tmp);
        }
      }

      {
        Matcher m = patGiorni2.matcher(s);
        if(m.matches())
        {
          int spiazzamento = StringOper.parse(m.group(1), 0);
          String ora = m.group(2) + "00";
          GregorianCalendar cal = new GregorianCalendar();
          cal.setTime(new Date());
          cal.add(Calendar.DAY_OF_YEAR, spiazzamento);
          String tmp = dfCompatItalian.format(cal.getTime()) + " " + ora;
          return dfCompatFullItalian.parse(tmp);
        }
      }

      return defVal;
    }
    catch(Exception e)
    {
      return defVal;
    }
  }

  @Override
  public Date parseTime(String s, Date defVal)
  {
    try
    {
      if(patTimeItalian.matcher(s).matches())
        return tfItalian.parse(s);

      if(patTimeItalianShort.matcher(s).matches())
        return tfItalianShort.parse(s);

      if(patCompactTimeItalian.matcher(s).matches())
        return tfCompactItalian.parse(s);

      if(patCompactTimeItalianShort.matcher(s).matches())
        return tfCompactItalianShort.parse(s);

      return defVal;
    }
    catch(Exception e)
    {
      return defVal;
    }
  }

  @Override
  public String getErrorMessage(int errorType, String fldName,
     int min, int max, double dmin, double dmax, String invalidValue)
  {
    fldName = fldName.trim();

    switch(errorType)
    {
      case SimpleValidator.ERROR_TEXT:
        return String.format("Il campo '%s' non può essere vuoto.", fldName);
      case SimpleValidator.ERROR_DATE:
        return String.format("Il campo '%s' non contiene una data valida.", fldName);
      case SimpleValidator.ERROR_DATETIME:
        return String.format("Il campo '%s' non contiene una data/ora valida.", fldName);
      case SimpleValidator.ERROR_INT:
        return String.format("Il campo '%s' non contiene un valore numerico valido.", fldName);
      case SimpleValidator.ERROR_INT_RANGE:
        return String.format("Il valore del campo '%s' deve essere compreso fra i valori %d e %d.", fldName, min, max);
      case SimpleValidator.ERROR_DOUBLE:
        return String.format("Il campo '%s' non contiene un valore numerico valido.", fldName);
      case SimpleValidator.ERROR_DOUBLE_RANGE:
        return String.format("Il valore del campo '%s' deve essere compreso fra i valori %f e %f.", fldName, dmin, dmax);
      case SimpleValidator.ERROR_FILE_NOT_EXIST:
        return String.format("Il campo '%s' non contiene un file esistente.", fldName);
      case SimpleValidator.ERROR_DIRECTORY_NOT_EXIST:
        return String.format("Il campo '%s' non contiene una directory esistente.", fldName);
      case SimpleValidator.ERROR_REGEXP:
        return String.format("Il campo '%s' non contiene una espressione regolare valida.", fldName);

      default:
      case SimpleValidator.ERROR_REGEXP_TEST:
      case SimpleValidator.ERROR_CUSTOM:
        return String.format("Il campo '%s' non contiene un valore valido.", fldName);
    }
  }

  private Date adjust(int flags, Date date)
  {
    if(flags == FLAG_ROUND_BEGIN_DAY)
      return DateTime.mergeDataOra(date, 0, 0, 0, 0);
    if(flags == FLAG_ROUND_END_DAY)
      return DateTime.mergeDataOra(date, 23, 59, 59, 999);

    return date;
  }

  private Date subparse1(String s, String ora, Date defVal)
     throws ParseException
  {
    String base;

    if(StringOper.isEquNocaseAny(s, "oggi", "o", "today", "t"))
    {
      base = dfItalian.format(new Date());
      return dfFullItalian.parse(base + " " + ora);
    }

    if(StringOper.isEquNocaseAny(s, "ieri", "i", "yesterday", "y"))
    {
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(new Date());
      cal.add(Calendar.DAY_OF_YEAR, -1);
      base = dfItalian.format(cal.getTime());
      return dfFullItalian.parse(base + " " + ora);
    }

    return defVal;
  }

  private Date subparse2(String s, String ora, Date defVal)
     throws ParseException
  {
    String base;

    if(StringOper.isEquNocaseAny(s, "oggi", "o", "today", "t"))
    {
      base = dfCompatItalian.format(new Date());
      return dfCompatFullItalian.parse(base + " " + ora);
    }

    if(StringOper.isEquNocaseAny(s, "ieri", "i", "yesterday", "y"))
    {
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(new Date());
      cal.add(Calendar.DAY_OF_YEAR, -1);
      base = dfCompatItalian.format(cal.getTime());
      return dfCompatFullItalian.parse(base + " " + ora);
    }

    return defVal;
  }
}
