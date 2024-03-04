/*
 * DateTime.java
 *
 * Created on 26-set-2009, 17.29.07
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Funzioni di utilita' per data/ora.
 *
 * @author Nicola De Nisco
 */
public class DateTime
{
  // numero di millisecondi di tolleranza per eguaglianza date
  public static final int TIME_MILLIS_EQUAL = 1000; // 1 sec.
  //
  public static final long MSEC_MINUTO = 60 * 1000L; // millisecondi in un minuto
  public static final long MSEC_ORA = MSEC_MINUTO * 60; // millisecondi in un'ora
  public static final long MSEC_GIORNO = MSEC_ORA * 24; // millisecondi in un giorno
  public static final long MSEC_SETTIMANA = MSEC_GIORNO * 7; // millisecondi in una settimana
  public static final long MSEC_MESE = MSEC_GIORNO * 30; // millisecondi in un mese standard
  public static final long MSEC_ANNO = MSEC_GIORNO * 365; // millisecondi in un anno standard
  //
  public static final DateFormat ISOformat = new SimpleDateFormat("yyyy-MM-dd");
  public static final DateFormat ISOformatFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final DateFormat dfData = new SimpleDateFormat("yyyyMMdd");
  public static final DateFormat dfDataOra = new SimpleDateFormat("yyyyMMdd HHmmss");
  public static final DateFormat dfOra = new SimpleDateFormat("HHmmss");
  public static final DateFormat dfOrans = new SimpleDateFormat("HHmm");
  public static final DateFormat dfDTMXDS = new SimpleDateFormat("yyyyMMddHHmmss");
  public static final Pattern patIso = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
  public static final Pattern patFullIso = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$");

  public static String formatIso(Date d)
     throws Exception
  {
    return ISOformat.format(d);
  }

  public static Date parseIso(String s)
     throws Exception
  {
    return ISOformat.parse(s);
  }

  public static String formatIso(Date d, String defVal)
  {
    try
    {
      return ISOformat.format(d);
    }
    catch(Exception e)
    {
      return defVal;
    }
  }

  public static Date parseIso(String s, Date defVal)
  {
    return parseIsoAuto(s, defVal);
  }

  public static String formatIsoFull(Date d)
     throws Exception
  {
    return ISOformatFull.format(d);
  }

  public static Date parseIsoFull(String s)
     throws Exception
  {
    return ISOformatFull.parse(s);
  }

  public static String formatIsoFull(Date d, String defVal)
  {
    try
    {
      return ISOformatFull.format(d);
    }
    catch(Throwable t)
    {
      return defVal;
    }
  }

  public static Date parseIsoFull(String s, Date defVal)
  {
    return parseIsoAuto(s, defVal);
  }

  public static Date parseIsoAuto(String s, Date defVal)
  {
    if((s = StringOper.okStrNull(s)) == null)
      return defVal;

    try
    {
      return ISOformatFull.parse(s);
    }
    catch(Throwable ex)
    {
    }

    try
    {
      return ISOformat.parse(s);
    }
    catch(Throwable ex)
    {
    }

    return defVal;
  }

  public static String formatDateDicom(Date data)
     throws Exception
  {
    return dfData.format(data);
  }

  public static String formatDateDicom(Date data, String defVal)
  {
    try
    {
      return dfData.format(data);
    }
    catch(Throwable t)
    {
      return defVal;
    }
  }

  public static Date parseDateDicom(String sData, Date defVal)
  {
    try
    {
      return dfData.parse(sData);
    }
    catch(Throwable t)
    {
      return defVal;
    }
  }

  public static String formatTimeDicom(Date data)
     throws Exception
  {
    return dfOra.format(data);
  }

  public static String formatTimeDicom(Date data, String defVal)
  {
    try
    {
      return dfOra.format(data);
    }
    catch(Throwable t)
    {
      return defVal;
    }
  }

  public static Date parseTimeDicom(String sOra, Date defVal)
  {
    Date dOra = defVal;

    try
    {
      // prima il formato hhmmss
      dOra = dfOra.parse(sOra);
    }
    catch(Throwable t1)
    {
      try
      {
        // quindi riprova con hhmm
        dOra = dfOrans.parse(sOra);
      }
      catch(Throwable t2)
      {
      }
    }

    return dOra;
  }

  public static Date parseDateTimeDicom(String sData, String sOra)
  {
    Date dData = parseDateDicom(sData, null);
    Date dOra = parseTimeDicom(sOra, null);

    if(dData == null)
      return null;
    if(dOra == null)
      return dData;

    return mergeDataOra(dData, dOra);
  }

  public static Date mergeDataOra(Date data, Date ora)
  {
    if(ora == null)
      return data;

    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    Calendar calt = new GregorianCalendar(Locale.ITALIAN);
    cald.setTime(data);
    calt.setTime(ora);
    cald.set(Calendar.HOUR_OF_DAY, calt.get(Calendar.HOUR_OF_DAY));
    cald.set(Calendar.MINUTE, calt.get(Calendar.MINUTE));
    cald.set(Calendar.SECOND, calt.get(Calendar.SECOND));
    cald.set(Calendar.MILLISECOND, calt.get(Calendar.MILLISECOND));
    return cald.getTime();
  }

  public static Date mergeDataOra(Date data, int ore, int minuti, int secondi)
  {
    return mergeDataOra(data, ore, minuti, secondi, 0);
  }

  public static Date mergeDataOra(Date data, int ore, int minuti, int secondi, int millisecondi)
  {
    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    cald.setTime(data);
    cald.set(Calendar.HOUR_OF_DAY, ore);
    cald.set(Calendar.MINUTE, minuti);
    cald.set(Calendar.SECOND, secondi);
    cald.set(Calendar.MILLISECOND, millisecondi);
    return cald.getTime();
  }

  public static Date mergeDataOra(int giorno, int mese, int anno, int ore, int minuti, int secondi)
  {
    return mergeDataOra(giorno, mese, anno, ore, minuti, secondi, 0);
  }

  public static Date mergeDataOra(int giorno, int mese, int anno, int ore, int minuti, int secondi, int millisecondi)
  {
    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    cald.setTimeInMillis(System.currentTimeMillis());

    cald.set(Calendar.YEAR, anno);
    cald.set(Calendar.MONTH, mese);
    cald.set(Calendar.DAY_OF_MONTH, giorno);

    cald.set(Calendar.HOUR_OF_DAY, ore);
    cald.set(Calendar.MINUTE, minuti);
    cald.set(Calendar.SECOND, secondi);
    cald.set(Calendar.MILLISECOND, millisecondi);

    return cald.getTime();
  }

  public static Date creaData(int giorno, int mese, int anno)
  {
    return mergeDataOra(giorno, mese, anno, 0, 0, 0, 0);
  }

  public static Date creaDataFine(int giorno, int mese, int anno)
  {
    return mergeDataOra(giorno, mese, anno, 23, 59, 59, 999);
  }

  public static Date inizioGiorno(Date data)
  {
    return mergeDataOra(data, 0, 0, 0, 0);
  }

  public static Date fineGiorno(Date data)
  {
    return mergeDataOra(data, 23, 59, 59, 999);
  }

  public static Date inizioMese(Date data)
  {
    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    cald.setTime(data);
    cald.set(Calendar.DAY_OF_MONTH, 1);
    cald.set(Calendar.HOUR_OF_DAY, 0);
    cald.set(Calendar.MINUTE, 0);
    cald.set(Calendar.SECOND, 0);
    cald.set(Calendar.MILLISECOND, 0);
    return cald.getTime();
  }

  public static Date fineMese(Date data)
  {
    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    cald.setTime(data);
    cald.set(Calendar.DAY_OF_MONTH, cald.getActualMaximum(Calendar.DAY_OF_MONTH));
    cald.set(Calendar.HOUR_OF_DAY, 23);
    cald.set(Calendar.MINUTE, 59);
    cald.set(Calendar.SECOND, 59);
    cald.set(Calendar.MILLISECOND, 999);
    return cald.getTime();
  }

  public static boolean isEqu(Date data1, Date data2)
  {
    if(data1 == null && data2 == null)
      return true;
    if(data1 == null || data2 == null)
      return false;

    // controlla se si tratta dello stesso oggetto
    if(data1 == data2)
      return true;

    return Math.abs(data1.getTime() - data2.getTime()) < TIME_MILLIS_EQUAL;
  }

  public static boolean isEquDaysOnly(Date data1, Date data2)
  {
    if(data1 == null && data2 == null)
      return true;
    if(data1 == null || data2 == null)
      return false;

    // controlla se si tratta dello stesso oggetto
    if(data1 == data2)
      return true;

    GregorianCalendar g1 = new GregorianCalendar();
    GregorianCalendar g2 = new GregorianCalendar();
    g1.setTime(data1);
    g2.setTime(data2);

    return g1.get(Calendar.YEAR) == g2.get(Calendar.YEAR)
       && g1.get(Calendar.DAY_OF_YEAR) == g2.get(Calendar.DAY_OF_YEAR);
  }

  public static long elapsed(Date d1)
  {
    return System.currentTimeMillis() - d1.getTime();
  }

  public static long elapsed(Date d1, Date d2)
  {
    return Math.abs(d1.getTime() - d2.getTime());
  }

  public static boolean isElapsed(long test, long elapse)
  {
    return (System.currentTimeMillis() - test) > elapse;
  }

  public static boolean isElapsed(Date d1, long elapse)
  {
    return elapsed(d1) > elapse;
  }

  public static boolean isElapsed(Date d1, Date d2, long elapse)
  {
    return elapsed(d1, d2) > elapse;
  }

  /**
   * Ritorna vero se la data test è compresa nell'intervallo
   * fra inizio e fine. Il test viene effettuato anche con
   * l'ora e precisione del millisecondo.
   * Gli estremi sono compresi nell'intervallo.
   * @param inizio data iniziale
   * @param fine data finale
   * @param test data da provare
   * @return vero se test è nell'intervallo
   */
  public static boolean isBeetwen(Date inizio, Date fine, Date test)
  {
    long tTime = test.getTime();
    return tTime >= inizio.getTime() && tTime <= fine.getTime();
  }

  /**
   * Ritorna vero se la data test è compresa nell'intervallo
   * fra inizio e fine. Il test viene effettuato con troncamento
   * al giorno solare, ignorando l'ora, con precisione del millisecondo.
   * Gli estremi sono compresi nell'intervallo.
   * @param inizio data iniziale
   * @param fine data finale
   * @param test data da provare
   * @return vero se test è nell'intervallo
   */
  public static boolean isBeetwenDaysOnly(Date inizio, Date fine, Date test)
  {
    return isBeetwen(inizioGiorno(inizio), fineGiorno(fine), test);
  }

  public static Date mergeDataOra(int year, int month, int day, Date data)
  {
    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    cald.setTime(data);
    cald.set(Calendar.YEAR, year);
    cald.set(Calendar.MONTH, month);
    cald.set(Calendar.DAY_OF_MONTH, day);
    return cald.getTime();
  }

  public static Date mergeDataOra(Date data, Date ora,
     int dofs, int mofs, int yofs)
  {
    Calendar cald = new GregorianCalendar(Locale.ITALIAN);
    Calendar calt = new GregorianCalendar(Locale.ITALIAN);
    cald.setTime(data);
    cald.add(Calendar.YEAR, yofs);
    cald.add(Calendar.MONTH, mofs);
    cald.add(Calendar.DAY_OF_YEAR, dofs);
    cald.getTime();
    calt.setTime(ora);
    cald.set(Calendar.HOUR_OF_DAY, calt.get(Calendar.HOUR_OF_DAY));
    cald.set(Calendar.MINUTE, calt.get(Calendar.MINUTE));
    cald.set(Calendar.SECOND, calt.get(Calendar.SECOND));
    return cald.getTime();
  }

  /**
   * Fuzione data/ora.
   * @param data data (l'ora viene ignorata)
   * @param ora nel formato HH:mm:ss oppure HH:mm
   * @return fusione data ora
   */
  public static Date mergeDataOra(Date data, String ora)
  {
    String ss[] = ora.split(":");
    if(ss.length < 2 || ss.length > 3)
      return data;

    int h = Integer.parseInt(ss[0]);
    int m = Integer.parseInt(ss[1]);
    int s = ss.length == 3 ? Integer.parseInt(ss[2]) : 0;
    return mergeDataOra(data, h, m, s);
  }

  public static java.sql.Date Cvt(Date d)
  {
    return new java.sql.Date(d.getTime());
  }

  public static Date Cvt(java.sql.Date d)
  {
    return new Date(d.getTime());
  }

  public static java.sql.Timestamp CvtTs(Date d)
  {
    // converte da ora locale a UTC in modo implicito
    return new java.sql.Timestamp(d.getTime());
  }

  public static Date CvtTs(java.sql.Timestamp t)
  {
    return new Date(t.getTime());
    // converte da UTC a ora locale
    // int minOffset = t.getTimezoneOffset();
    // return new Date(t.getTime() - (minOffset * 60000));
  }

  public static Date getSoloOra(Date d)
  {
    return mergeDataOra(0, 0, 0, d);
  }

  public static Date getSoloOra(int ora, int minuto)
  {
    return mergeDataOra(0, 0, 0, ora, minuto, 0);
  }

  public static Date getSoloOraUTC(int ora, int minuto)
  {
    long tmsec = ora * MSEC_ORA + minuto * MSEC_MINUTO;
    return new Date(tmsec);
  }

  public static Date min(Date d1, Date d2)
  {
    if(d1 == null)
      return d2;

    if(d2 == null)
      return d1;

    return d1.before(d2) ? d1 : d2;
  }

  public static Date max(Date d1, Date d2)
  {
    if(d1 == null)
      return d2;

    if(d2 == null)
      return d1;

    return d1.after(d2) ? d1 : d2;
  }

  public static int compare(Date d1, Date d2)
  {
    if(d1 == null && d2 == null)
      return 0;

    if(d1 == null || d1.before(d2))
      return -1;

    if(d2 == null || d1.after(d2))
      return 1;

    return 0;
  }

  public static int compareInverse(Date d1, Date d2)
  {
    if(d1 == null && d2 == null)
      return 0;

    if(d1 == null || d1.before(d2))
      return 1;

    if(d2 == null || d1.after(d2))
      return -1;

    return 0;
  }

  /**
   * Ritorna la prima data valida dei parametri passati.
   * @param values valori da confrontare
   * @return la prima data valida oppure null se nessun valore valido
   */
  public static Date okDateAny(Date... values)
  {
    for(Date d : values)
    {
      if(d != null)
        return d;
    }
    return null;
  }

  /**
   * Formatta una data.
   * Simile alla Format() di VB6.
   * @param date data da formattare (null per data/ora corrente)
   * @param format formato java per la data (vedi SimpleDateFormat)
   * @return stringa formattata con la data
   */
  public static String Format(Date date, String format)
  {
    SimpleDateFormat sf = new SimpleDateFormat(format);
    return sf.format(date == null ? new Date() : date);
  }

  /**
   * Parsing di una data.
   * Inverso della Format() di VB6.
   * @param date data da convertire
   * @param format formato java per la data (vedi SimpleDateFormat)
   * @return stringa formattata con la data
   */
  public static Date Parse(String date, String format)
  {
    SimpleDateFormat sf = new SimpleDateFormat(format);
    try
    {
      return sf.parse(date);
    }
    catch(Exception e)
    {
      return null;
    }
  }

  /**
   * Ritorna una data spiazzata dei giorni indicati.
   * @param origin data di origine (null=data corrente)
   * @param giorniSpiazzamento giorni da considerare
   * @return la nuova data
   */
  public static Date dataSpiazzata(Date origin, int giorniSpiazzamento)
  {
    GregorianCalendar cal = new GregorianCalendar();
    if(origin != null)
      cal.setTime(origin);
    cal.add(Calendar.DAY_OF_YEAR, giorniSpiazzamento);
    return cal.getTime();
  }
}
