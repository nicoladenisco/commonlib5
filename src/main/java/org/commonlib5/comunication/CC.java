/*
 *  CC.java
 *  Creato il 24-ott-2011, 12.27.03
 *
 *  Copyright (C) 2011 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 */
package org.commonlib5.comunication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Costanti per le comunicazioni.
 *
 * @author Nicola De Nisco
 */
public class CC
{
  public static final byte Nul = 0;
  public static final byte Soh = 1;
  public static final byte Stx = 2;
  public static final byte Etx = 3;
  public static final byte Eot = 4;
  public static final byte Enq = 5;
  public static final byte Ack = 6;
  public static final byte Bel = 7;
  public static final byte Bs = 8;
  public static final byte Tabc = 9;
  public static final byte Lf = 10;
  public static final byte Vt = 11;
  public static final byte Np = 12;
  public static final byte Cr = 13;
  public static final byte So = 14;
  public static final byte Si = 15;
  public static final byte Dle = 16;
  public static final byte Dc1 = 17;
  public static final byte Dc2 = 18;
  public static final byte Dc3 = 19;
  public static final byte Dc4 = 20;
  public static final byte Nack = 21;
  public static final byte Syn = 22;
  public static final byte Etb = 23;
  public static final byte Can = 24;
  public static final byte Em = 25;
  public static final byte Eofc = 26;
  public static final byte Esc = 27;
  public static final byte Fs = 28;
  public static final byte Gs = 29;
  public static final byte Rs = 30;
  public static final byte Us = 31;
  //
  public static final String[] SerialChar =
  {
    "Nul", "Soh", "Stx", "Etx", "Eot", "Enq", "Ack", "Bel", "Bs", "Tabc",
    "Lf", "Vt", "Np", "Cr", "So", "Si", "Dle", "Dc1", "Dc2", "Dc3", "Dc4",
    "Nack", "Syn", "Etb", "Can", "Em", "Eofc", "Esc", "Fs", "Gs", "Rs", "Us"
  };
  //
  public static final byte[] CRLF =
  {
    13, 10
  };
  //
  public static final Pattern p = Pattern.compile("\\[(\\w+)\\]");

  /**
   * Formatta una stringa sostituendo ai caratteri di comunicazione
   * i rispettivi mnemonici. La conversione è comunque compatibile UTF-16.
   * @param mes stringa da stampare
   * @return stringa formattata
   */
  public static String fmtCommString(String mes)
  {
    StringBuilder sb = new StringBuilder(128);

    for(int i = 0; i < mes.length(); i++)
    {
      char c = mes.charAt(i);

      if(c > 31)
        sb.append(c);
      else
        sb.append('[').append(SerialChar[(int) c]).append(']');
    }

    return sb.toString();
  }

  /**
   * Formatta una buffer di byte sostituendo ai caratteri di comunicazione
   * i rispettivi mnemonici. La codifica è di tipo ASCII.
   * @param buffer byte da stampare
   * @param offset primo byte da stampare nel buffer
   * @param len numero di byte da stampare
   * @return stringa formattata
   */
  public static String fmtCommBuffer(byte[] buffer, int offset, int len)
  {
    StringBuilder sb = new StringBuilder(128);

    while(len-- > 0 && offset < buffer.length)
    {
      int c = buffer[offset++];

      if(c > 31)
        sb.append((char) c);
      else if(c >= 0 && c <= SerialChar.length)
        sb.append('[').append(SerialChar[c]).append(']');
      else
        sb.append("[???]");
    }

    return sb.toString();
  }

  public static String fmtByte(int c)
  {
    StringBuilder sb = new StringBuilder();
    if(c > 31)
      sb.append((char) c);
    else if(c >= 0 && c <= SerialChar.length)
      sb.append('[').append(SerialChar[c]).append(']');
    else
      sb.append("[???]");
    return sb.toString();
  }

  public static byte parseLexicalChar(String s)
     throws Exception
  {
    for(int i = 0; i < SerialChar.length; i++)
    {
      if(s.equalsIgnoreCase(SerialChar[i]))
        return (byte) i;
    }
    return 0;
  }

  public static String parseLexicalCharAsString(String s)
     throws Exception
  {
    byte[] b = new byte[1];
    b[0] = parseLexicalChar(s);
    return new String(b, "UTF-8");
  }

  public static byte[] parseLexical(String lex, String charsetName)
     throws Exception
  {
    return parseLexicalString(lex).getBytes(charsetName);
  }

  public static String parseLexicalString(String lex)
     throws Exception
  {
    Matcher m = p.matcher(lex);

    StringBuffer sb = new StringBuffer();
    while(m.find())
    {
      if(m.groupCount() != 1)
        throw new Exception("Errore di sintassi in " + lex);

      String chMatch = m.group(1);
      String toReplace = parseLexicalCharAsString(chMatch);

      m.appendReplacement(sb, toReplace);
    }
    m.appendTail(sb);

    return sb.toString();
  }
}
