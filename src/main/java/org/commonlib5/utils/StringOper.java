package org.commonlib5.utils;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commonlib5.lambda.FunctionTrowException;

/**
 * <p>
 * Title: Commonlib</p>
 * <p>
 * Description: Libreria di utilizzo comune.
 * StringOper
 * Una serie di funzioni statiche per la manipolazione delle stringhe.</p>
 * <p>
 * Copyright: Copyright (c) 2001</p>
 * <p>
 * Company: WinSOFT</p>
 * @author Nicola De Nisco
 * @version 1.0
 */
public class StringOper
{
  public static final String sZero = "00000000000000000";
  public static final String sBlank = "                 ";
  public static final String CRLF = "\r\n";
  public static final String CR = "\r";
  public static final String LF = "\n";
  public static final String[] EMPTY_STRING_ARRAY =
  {
  };
  public static final int ALIGN_LEFT = 1;
  public static final int ALIGN_CENTER = 2;
  public static final int ALIGN_RIGHT = 3;
  /** pattern per la rimozione dei tag HTML */
  public static final Pattern htmlRemovePattern = Pattern.compile("\\<[^\\>]*\\>",
     Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  /** pattern per la conversione di HTML o XML unicode (decimale) */
  public static final Pattern decHtmlPattern = Pattern.compile("&#([0-9]+);");
  /** pattern per la conversione di HTML o XML unicode (esadecimale) */
  public static final Pattern esaHtmlPattern = Pattern.compile("&#[x|X]([0-9]+);");
  /** pattern per la conversione di HTML o XML unicode (caratteri speciali) */
  public static final Pattern specialHtmlPattern = Pattern.compile("&(.+?);");
  //
  public static final Map<Character, String> unicode2HtmlMap = Collections.unmodifiableMap(createUnicode2HtmlMap());
  public static final Map<String, Character> html2UnicodeMap = Collections.unmodifiableMap(createHtml2UnicodeMap());

  public static String GetSpaces(int len)
  {
    StringBuilder sb = new StringBuilder(len + 50);
    while(sb.length() < len)
    {
      sb.append(sBlank);
    }
    return sb.substring(0, len);
  }

  public static String GetZeroes(int len)
  {
    StringBuilder sb = new StringBuilder(len + 50);
    while(sb.length() < len)
    {
      sb.append(sZero);
    }
    return sb.substring(0, len);
  }

  public static String GetFixedString(int ch, int sz)
  {
    return new String(GetFixedChars(ch, sz));
  }

  public static char[] GetFixedChars(int ch, int sz)
  {
    char chAr[] = new char[sz];
    for(int i = 0; i < sz; i++)
    {
      chAr[i] = (char) ch;
    }
    return chAr;
  }

  public static char[] GetSpacesChars(int len)
  {
    return GetFixedChars(' ', len);
  }

  public static char[] GetZeroesChars(int len)
  {
    return GetFixedChars('0', len);
  }

  /**
   * Ritorna una stringa a lunghezza fissa (specificata) inserendo spazi in coda
   * @param val
   * @param fixLen
   * @return
   */
  public static String GetFixedString(String val, int fixLen)
  {
    return GetFixedString(val, fixLen, ALIGN_LEFT);
  }

  /**
   * Ritorna una stringa riempita con degli spazi.
   * @param s stringa originale
   * @param sz dimensioni richiesta
   * @param al tipo allineamento (vedi costanti ALIGN_..)
   * @return la stringa della lunghezza richiesta
   */
  public static String GetFixedString(String s, int sz, int al)
  {
    int l = s.length();

    if(l >= sz)
    {
      switch(al)
      {
        case ALIGN_LEFT:
        case ALIGN_CENTER:
          return s.substring(0, sz);
        case ALIGN_RIGHT:
          return s.substring(l - sz);
      }
      return s.substring(0, sz);
    }
    else
    {
      String blank = GetSpaces(sz - l);

      switch(al)
      {
        case ALIGN_LEFT:
          return s + blank;
        case ALIGN_CENTER:
          int hl = (sz - l) >> 1;
          return blank.substring(0, hl)
             + s
             + blank.substring(0, sz - l - hl);
        case ALIGN_RIGHT:
          return blank + s;
      }
    }
    return s;
  }

  /**
   * Ritorna una stringa a lunghezza fissa (specificata) inserendo zero in testa
   * @return
   */
  public static String GetZeroFixedString(int val, int fixLen)
  {
    return GetZeroFixedString(Integer.toString(val), fixLen, ALIGN_RIGHT);
  }

  /**
   * Ritorna una stringa a lunghezza fissa (specificata) inserendo zero in testa
   * @return
   */
  public static String GetZeroFixedString(String val, int fixLen)
  {
    return GetZeroFixedString(val, fixLen, ALIGN_RIGHT);
  }

  /**
   * Ritorna una stringa riempita con degli 0.
   * @param s stringa originale
   * @param sz dimensioni richiesta
   * @param al tipo allineamento (vedi costanti ALIGN_..)
   * @return la stringa della lunghezza richiesta
   */
  public static String GetZeroFixedString(String s, int sz, int al)
  {
    int l = s.length();

    if(l >= sz)
    {
      switch(al)
      {
        case ALIGN_LEFT:
        case ALIGN_CENTER:
          return s.substring(0, sz);
        case ALIGN_RIGHT:
          return s.substring(l - sz);
      }
      return s.substring(0, sz);
    }
    else
    {
      String blank = GetZeroes(sz - l);

      switch(al)
      {
        case ALIGN_LEFT:
          return s + blank;
        case ALIGN_CENTER:
          int hl = (sz - l) >> 1;
          return blank.substring(0, hl)
             + s
             + blank.substring(0, sz - l - hl);
        case ALIGN_RIGHT:
          return blank + s;
      }
    }
    return s;
  }

  /**
   * Converte opportunamente il carattere affinchè sia
   * compatibile con la sintassi SQL.
   * Ad esempio l'apostrofo viene convertito in un doppio apostrofo.
   * @param sb riceve i caratteri convertiti
   * @param c carattere da convertire
   * @return vero se ha effettuato una conversione
   */
  public static boolean appendJavascriptChar(StringBuffer sb, int c)
  {
    switch(c)
    {
      case '\'':
        sb.append("\\\'");
        break;
      case '\"':
        sb.append("\\\"");
        break;
      default:
        return false;
    }
    return true; // carattere convertito e inserito
  }

  /**
   * Converte opportunamente il carattere affinchè sia
   * compatibile con la sintassi SQL.
   * Ad esempio l'apostrofo viene convertito in un doppio apostrofo.
   * @param sb riceve i caratteri convertiti
   * @param c carattere da convertire
   * @return vero se ha effettuato una conversione
   */
  public static boolean appendSQLchar(StringBuffer sb, int c)
  {
    switch(c)
    {
      case '\'':
        sb.append("\'\'");
        break;
      case '\"':
        sb.append("\\\"");
        break;
      default:
        return false;
    }
    return true; // carattere convertito e inserito
  }

  /**
   * Converte un carattere nel suo equivalente HTML.
   * Ad esempio 'è' viene convertito in &egrave;
   * @param sb riceve i caratteri convertiti
   * @param c carattere da convertire
   * @return vero se ha effettuato una conversione
   */
  public static boolean appendWEBstring(StringBuffer sb, int c)
  {
    String tmp = unicode2HtmlMap.get((char) c);
    if(tmp != null)
      sb.append(tmp);
    return tmp != null;
  }

  /**
   * Converte il carattere per XML.
   * @param sb riceve i caratteri convertiti
   * @param c carattere da convertire
   * @return vero se ha effettuato una conversione
   */
  public static boolean appendXMLstring(StringBuffer sb, int c)
  {
    switch(c)
    {
      case '&':
        sb.append("&amp;");
        break;
      default:
        return false;
    }
    return true; // carattere convertito e inserito
  }

  /**
   * Converte la stringa rimuovendo o convertendo i caratteri non inseribili in SQL.
   * @return
   */
  public static String CvtJavascriptString(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuffer sb = new StringBuffer();

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      if(!appendJavascriptChar(sb, c))
        sb.append(arChar[i]);
    }

    return sb.toString();
  }

  /**
   * Converte la stringa rimuovendo o convertendo i caratteri non inseribili in SQL.
   * @return
   */
  public static String CvtSQLstring(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuffer sb = new StringBuffer();

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      if(!appendSQLchar(sb, c))
        sb.append(arChar[i]);
    }

    return sb.toString();
  }

  /**
   * Converte la stringa rimuovendo o convertendo i caratteri non inseribili in HTML.
   * @return
   */
  public static String CvtWEBstring(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuffer sb = new StringBuffer();

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      if(!appendWEBstring(sb, c))
        if(c == 10 || c == 13 || (c >= 32 && c <= 128))
          sb.append(arChar[i]);
        else if(c > 128)
          sb.append("&#").append(Integer.toString(arChar[i])).append(";");
    }

    return sb.toString();
  }

  /**
   * Converte la stringa rimuovendo o convertendo i caratteri non inseribili in SQL.
   * @return
   */
  public static String CvtSQLWEBstring(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuffer sb = new StringBuffer();

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      if(!appendSQLchar(sb, c))
        if(!appendWEBstring(sb, c))
          if(c == 10 || c == 13 || (c >= 32 && c <= 128))
            sb.append(arChar[i]);
    }

    return sb.toString();
  }

  /**
   * Converte la stringa rimuovendo o convertendo i caratteri non inseribili in XML.
   * @return
   */
  public static String CvtXMLstring(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuffer sb = new StringBuffer();

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      if(!appendXMLstring(sb, c))
        sb.append("&#x").append(Integer.toString(c, 16)).append(";");
    }

    return sb.toString();
  }

  /**
   * Converte i tag di HTML in Ascii.
   * Le lettere accentate e simili vengono convertite
   * nel loro equivalente ASCII.
   * @param str
   * @return
   */
  public static String CvtWEB2Ascii(String str)
  {
    str = strReplace(str, "&agrave;", "a'");
    str = strReplace(str, "&egrave;", "e'");
    str = strReplace(str, "&igrave;", "i'");
    str = strReplace(str, "&ograve;", "o'");
    str = strReplace(str, "&ugrave;", "u'");
    str = strReplace(str, "&aacute;", "a'");
    str = strReplace(str, "&eacute;", "e'");
    str = strReplace(str, "&iacute;", "i'");
    str = strReplace(str, "&oacute;", "o'");
    str = strReplace(str, "&uacute;", "u'");
    str = strReplace(str, "&acirc;", "a");
    str = strReplace(str, "&atilde;", "a");
    str = strReplace(str, "&aring;", "a");
    str = strReplace(str, "&aelig;", "a");
    str = strReplace(str, "&auml;", "a");
    str = strReplace(str, "&ecirc;", "e");
    str = strReplace(str, "&euml;", "e");
    str = strReplace(str, "&iuml;", "i");
    str = strReplace(str, "&ocirc;", "o");
    str = strReplace(str, "&otilde;", "o");
    str = strReplace(str, "&ouml;", "o");
    str = strReplace(str, "&oslash;", "o");
    str = strReplace(str, "&ucirc;", "u");
    str = strReplace(str, "&uuml;", "u");
    str = strReplace(str, "&ccedil;", "c");
    str = strReplace(str, "&ntilde;", "n");
    str = strReplace(str, "&yacute;", "y");
    str = strReplace(str, "&yuml;", "y");
    str = strReplace(str, "&iexcl;", "i");
    str = strReplace(str, "&iquest;", "i");
    str = strReplace(str, "&Agrave;", "A'");
    str = strReplace(str, "&Aacute;", "A'");
    str = strReplace(str, "&Acirc;", "A");
    str = strReplace(str, "&Atilde;", "A");
    str = strReplace(str, "&AElig;", "AE");
    str = strReplace(str, "&Auml;", "A");
    str = strReplace(str, "&Aring;", "A");
    str = strReplace(str, "&Ccedil;", "C");
    str = strReplace(str, "&Egrave;", "E'");
    str = strReplace(str, "&Eacute;", "E'");
    str = strReplace(str, "&Ecirc;", "E");
    str = strReplace(str, "&Euml;", "E");
    str = strReplace(str, "&Igrave;", "I'");
    str = strReplace(str, "&Iacute;", "I'");
    str = strReplace(str, "&Icirc;", "I");
    str = strReplace(str, "&Iuml;", "I");
    str = strReplace(str, "&Ntilde;", "N");
    str = strReplace(str, "&Ograve;", "O'");
    str = strReplace(str, "&Oacute;", "O'");
    str = strReplace(str, "&Ocirc;", "O");
    str = strReplace(str, "&Otilde;", "O");
    str = strReplace(str, "&Ouml;", "O");
    str = strReplace(str, "&Oslash;", "O");
    str = strReplace(str, "&Ugrave;", "U'");
    str = strReplace(str, "&Uacute;", "U'");
    str = strReplace(str, "&Ucirc;", "U");
    str = strReplace(str, "&Uuml;", "U");
    str = strReplace(str, "&Yacute;", "Y'");

    return str;
  }

  /**
   * Converte i tag di HTML in Unicode.
   * Le lettere accentate e simili vengono convertite
   * nel loro equivalente ASCII.
   * @param str
   * @return
   */
  public static String CvtWEB2Unicode(String str)
  {
    // applico l'espressione regolare,
    // sostituendo i caratteri speciali &#x...; con il loro valore unicode
    StringBuffer sb = new StringBuffer();
    Matcher m = esaHtmlPattern.matcher(str);
    while(m.find())
    {
      String s = m.group(1);
      int c = Integer.parseInt(s, 16);
      m.appendReplacement(sb, Character.toString((char) c));
    }
    m.appendTail(sb);
    str = sb.toString();

    // applico l'espressione regolare,
    // sostituendo i caratteri speciali &#...; con il loro valore unicode
    sb = new StringBuffer();
    m = decHtmlPattern.matcher(str);
    while(m.find())
    {
      String s = m.group(1);
      int c = Integer.parseInt(s, 10);
      m.appendReplacement(sb, Character.toString((char) c));
    }
    m.appendTail(sb);
    str = sb.toString();

    // applico l'espressione regolare,
    // sostituendo i caratteri speciali &...; con il loro valore unicode
    sb = new StringBuffer();
    m = specialHtmlPattern.matcher(str);
    while(m.find())
    {
      String s = m.group(1);
      Character c = html2UnicodeMap.get("&" + s + ";");
      if(c != null)
        m.appendReplacement(sb, Character.toString(c));
      else
        m.appendReplacement(sb, s);
    }
    m.appendTail(sb);

    return strReplace(sb.toString(), "&nbsp;", " ");
  }

  /**
   * Converte la stringa inserendo + al posto degli spazi,
   * come richiesto dal metodo GET. Vengono scartati tutti
   * i caratteri non compresi fra 32 e 128.
   * @param s input
   * @return output
   */
  public static String CvtGETstring(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuilder sb = new StringBuilder(len + 5);

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      switch(c)
      {
        case ' ':
          sb.append("+");
          break;
        default:
          if(c >= 32 && c <= 128)
          {
            sb.append(arChar[i]);
            break;
          }

      }
    }

    return sb.toString();
  }

  /**
   * Rende una stringa compatibile con le regole di naming dei file.
   * I caratteri non ammessi come nomi di files vengono sostituiti
   * o soppressi.
   * @param s input
   * @return output
   */
  public static String CvtFILEstring(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuilder sb = new StringBuilder(len + 5);

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];
      switch(c)
      {
        case '\'':
          sb.append("_");
          break;
        case '\"':
          sb.append("_");
          break;
        case '/':
          sb.append("-");
          break;
        case '\\':
          sb.append("-");
          break;
        default:
          if(c >= 32 && c <= 128)
          {
            sb.append(arChar[i]);
            break;
          }
      }
    }

    return sb.toString();
  }

  /**
   * Ritorna la stringa con i soli caratteri compresi fra 32 e 128.
   * @param s input
   * @return output
   */
  public static String CvtASCIIstring(String s)
  {
    char[] arChar = s.toCharArray();
    StringBuilder sb = new StringBuilder(arChar.length);

    for(int i = 0; i < arChar.length; i++)
    {
      int c = arChar[i];

      if(c == 10 || c == 13 || (c >= 32 && c <= 128))
        sb.append(arChar[i]);
    }

    return sb.toString();
  }

  /**
   * Verifica se la stringa contiene i tokens.
   * @param vToken array di token
   * @param strTest stringa per il test di contenimento
   * @return numero di token contenuti in strTest
   */
  public static int testTokens(List<String> vToken, String strTest)
  {
    int Count = 0;
    for(int i = 0; i < vToken.size(); i++)
    {
      String s = (String) vToken.get(i);
      if(strTest.contains(s))
        Count++;
    }

    return Count;
  }

  /**
   * Verifica se la stringa contiene i tokens.
   * @param vToken array di token
   * @param strTest stringa per il test di contenimento
   * @return numero di token contenuti in strTest
   */
  public static int testTokens(String[] vToken, String strTest)
  {
    int Count = 0;
    for(int i = 0; i < vToken.length; i++)
    {
      if(strTest.contains(vToken[i]))
        Count++;
    }

    return Count;
  }

  /**
   * Verifica se la stringa è contenuta nell'array di tokens.
   * @param lToken lista di stringhe token
   * @param strTest string di test
   * @return vero se almeno una stringa è uguale a strTest
   */
  public static boolean isEqualTokens(List<String> lToken, String strTest)
  {
    for(String s : lToken)
    {
      if(strTest.equals(s))
        return true;
    }

    return false;
  }

  /**
   * Verifica se la stringa è contenuta nell'array di tokens.
   * @param vToken lista di stringhe token
   * @param strTest string di test
   * @return vero se almeno una stringa è uguale a strTest
   */
  public static boolean isEqualTokens(String[] vToken, String strTest)
  {
    for(int i = 0; i < vToken.length; i++)
    {
      if(strTest.equals(vToken[i]))
        return true;
    }

    return false;
  }

  /**
   * Converte un numero in string con zero alla sua sinistra per la lunghezza specificata
   * @return
   */
  public static String fmtHex(int Numero, int Len)
  {
    return GetZeroFixedString(Integer.toHexString(Numero), Len, ALIGN_RIGHT);
  }

  /**
   * Converte un numero in string con zero alla sua sinistra per la lunghezza specificata
   * @return
   */
  public static String fmtOctal(int Numero, int Len)
  {
    return GetZeroFixedString(Integer.toOctalString(Numero), Len, ALIGN_RIGHT);
  }

  /**
   * Converte un numero in string con zero alla sua sinistra per la lunghezza specificata
   * @return
   */
  public static String fmtZero(int Numero, int Len)
  {
    return GetZeroFixedString(Integer.toString(Numero), Len, ALIGN_RIGHT);
  }

  /**
   * Converte un numero in string con zero alla sua sinistra per la lunghezza specificata
   * @return
   */
  public static String fmtZero(float Numero, int Len)
  {
    return GetZeroFixedString(Float.toString(Numero), Len, ALIGN_RIGHT);
  }

  /**
   * Converte un numero in string con zero alla sua sinistra per la lunghezza specificata
   * @return
   */
  public static String fmtZero(double Numero, int Len)
  {
    return GetZeroFixedString(Double.toString(Numero), Len, ALIGN_RIGHT);
  }

  /**
   * Cerca e cambia.
   * Cerca in Origine tutte le occorrenze di Cerca e le
   * sostituisce con Cambia.
   * @param Origine Stringa in input
   * @param Cerca Striga da cercare in input
   * @param Cambia Stringa con cui sostituire Cerca
   * @return la nuova stringa ottenuta da Origine
   */
  public static String strReplace(String Origine, String Cerca, String Cambia)
  {
    int Pos = Origine.indexOf(Cerca);
    if(Pos == -1)
      return Origine;

    return Origine.substring(0, Pos) + Cambia
       + strReplace(Origine.substring(Pos + Cerca.length()), Cerca, Cambia);
  }

  /**
   * Cerca e cambia.
   * Cerca in origine le coppie di sostituzioni.
   * @param Origine
   * @param sostituzioni
   * @return la nuova stringa ottenuta da Origine
   */
  public static String strReplace(String Origine, Map<String, String> sostituzioni)
  {
    for(Map.Entry<String, String> entry : sostituzioni.entrySet())
    {
      String cerca = entry.getKey();
      String cambia = entry.getValue();

      Origine = strReplace(Origine, cerca, cambia);
    }

    return Origine;
  }

  /**
   * Cerca e cambia.
   * Cerca in origine le coppie di sostituzioni.
   * @param Origine
   * @param sostituzioni
   * @return la nuova stringa ottenuta da Origine
   */
  public static String strReplace(String Origine, Collection<Pair<String, String>> sostituzioni)
  {
    for(Pair<String, String> subs : sostituzioni)
      Origine = strReplace(Origine, subs.first, subs.second);

    return Origine;
  }

  /**
   * Cerca e cambia.
   * Cerca in origine le coppie di sostituzioni.
   * @param Origine
   * @param sostituzioni
   * @return la nuova stringa ottenuta da Origine
   * @throws IllegalArgumentException se sostuzioni è di lunghezza dispari
   */
  public static String strReplace(String Origine, String... sostituzioni)
  {
    if((sostituzioni.length & 1) != 0)
      throw new IllegalArgumentException("Sostituzioni di lunghezza dispari.");

    for(int i = 0; i < sostituzioni.length; i += 2)
    {
      String cerca = sostituzioni[i];
      String cambia = sostituzioni[i + 1];

      Origine = strReplace(Origine, cerca, cambia);
    }

    return Origine;
  }

  /**
   * Sostituisce un carattere della stringa.
   * @param Origine Stringa in input
   * @param pos posizione in cui operare la sostituzione
   * @param newChar nuovo carattere da sostituire
   * @return la nuova stringa ottenuta da Origine
   */
  public static String strReplaceIndex(String Origine, int pos, char newChar)
  {
    return Origine.substring(0, pos) + newChar + Origine.substring(pos + 1);
  }

  /**
   * Sostituisce una string all'interno di un'altra stringa.
   * @param Origine Stringa in input
   * @param pos posizione in cui operare la sostituzione
   * @param newString nuova stringa da sostituire
   * @return la nuova stringa ottenuta da Origine
   */
  public static String strReplaceIndex(String Origine, int pos, String newString)
  {
    return Origine.substring(0, pos) + newString + Origine.substring(pos + newString.length());
  }

  /**
   * Come la mid del basic.
   * Estrae una parte della stringa in ingresso.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * @param Origine stringa originale
   * @param Inizio primo carattere da estrarre (0=primo)
   * @return parte della stringa origine
   */
  public static String mid(String Origine, int Inizio)
  {
    Origine = okStr(Origine);

    if(Inizio > Origine.length())
      return "";

    return Origine.substring(Inizio);
  }

  /**
   * Come la mid del basic.
   * Estrae una parte della stringa in ingresso.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * Se inizio+lunghezza vanno otre la dimensione di origine
   * vengono ritornati tutti i caratteri dopo inizio.
   * @param Origine stringa originale
   * @param Inizio primo carattere da estrarre (0=primo)
   * @param Lunghezza numero di caratteri da estrarre
   * @return parte della stringa origine
   */
  public static String mid(String Origine, int Inizio, int Lunghezza)
  {
    Origine = okStr(Origine);

    if(Inizio > Origine.length())
      return "";

    if((Inizio + Lunghezza) >= Origine.length())
      return Origine.substring(Inizio);

    return Origine.substring(Inizio, Inizio + Lunghezza);
  }

  /**
   * Come la right del basic.
   * Estrae la parte finale di una stringa.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * Se len è maggiore dalla lunghezza di origine
   * tutta la stringa viene ritornata.
   * Se len è minore di 0 si intende la lunghezza della stringa meno len.
   * @param Origine stringa originale
   * @param Len numero di caratteri richiesti
   * @return parte finale della stringa origine
   */
  public static String right(String Origine, int Len)
  {
    Origine = okStr(Origine);

    if(Len < 0)
      Len = Origine.length() + Len;

    if(Len >= Origine.length())
      return Origine;

    return Origine.substring(Origine.length() - Len);
  }

  /**
   * Come la left del basic.
   * Estrae la parte iniziale di una stringa.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * Se len è maggiore dalla lunghezza di origine
   * tutta la stringa viene ritornata.
   * Se len è minore di 0 si intende la lunghezza della stringa meno len.
   * @param Origine stringa originale
   * @param Len numero di caratteri richiesti
   * @return la parte iniziale di origine
   */
  public static String left(String Origine, int Len)
  {
    Origine = okStr(Origine);

    if(Len >= Origine.length())
      return Origine;

    if(Len < 0)
      Len = Origine.length() + Len;

    return Origine.substring(0, Len);
  }

  /**
   * Come la mid del basic.
   * Estrae una parte della stringa in ingresso.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * Se inizio+lunghezza vanno otre la dimensione di origine
   * vengono ritornati tutti i caratteri dopo inizio.
   * @param origine stringa originale (toString())
   * @param defVal valore di default per stringa vuota/nulla
   * @param Inizio primo carattere da estrarre (0=primo)
   * @param Lunghezza numero di caratteri da estrarre
   * @return parte della stringa origine
   */
  public static String mid(Object origine, String defVal, int Inizio, int Lunghezza)
  {
    String Origine = okStr(origine, defVal);

    if(Origine == null)
      return null;

    if((Inizio + Lunghezza) >= Origine.length())
      return Origine.substring(Inizio);

    return Origine.substring(Inizio, Inizio + Lunghezza);
  }

  /**
   * Come la right del basic.
   * Estrae la parte finale di una stringa.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * Se len è maggiore dalla lunghezza di origine
   * tutta la stringa viene ritornata.
   * @param origine stringa originale (toString())
   * @param defVal valore di default per stringa vuota/nulla
   * @param Len numero di caratteri richiesti
   * @return parte finale della stringa origine
   */
  public static String right(Object origine, String defVal, int Len)
  {
    String Origine = okStr(origine, defVal);

    if(Origine == null)
      return null;

    if(Len < 0)
      Len = Origine.length() + Len;

    if(Len >= Origine.length())
      return Origine;

    return Origine.substring(Origine.length() - Len);
  }

  /**
   * Cerca ed estrae parte di stringa.
   * Cerca toSearch all'interno di Origine; se la trova ritorna la parte rimanente
   * della stringa, altrimenti l'intera stringa.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * @param origine stringa originale (toString())
   * @param toSearch stringa da cercare
   * @return stringa risultato
   */
  public static String findAndGetRight(Object origine, String toSearch)
  {
    String Origine = okStr(origine);
    int pos = Origine.indexOf(toSearch);
    return pos == -1 ? Origine : Origine.substring(pos + toSearch.length());
  }

  /**
   * Cerca ed estrae parte di stringa.
   * Cerca toSearch all'interno di Origine; se la trova ritorna la parte rimanente
   * della stringa, altrimenti l'intera stringa.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * @param origine stringa originale (toString())
   * @param defVal valore di default per stringa vuota/nulla
   * @param toSearch stringa da cercare
   * @return stringa risultato
   */
  public static String findAndGetRight(Object origine, String defVal, String toSearch)
  {
    String Origine = okStr(origine, defVal);

    if(Origine == null)
      return null;

    int pos = Origine.indexOf(toSearch);
    return pos == -1 ? Origine : Origine.substring(pos + toSearch.length());
  }

  /**
   * Come la left del basic.
   * Estrae la parte iniziale di una stringa.
   * La stringa in ingresso viene passata attraverso
   * okStr() che ne rimuove gli spazi in testa e in coda
   * e controlla che non sia null.
   * Se len è maggiore dalla lunghezza di origine
   * tutta la stringa viene ritornata.
   * @param origine stringa originale (toString())
   * @param defVal valore di default per stringa vuota/nulla
   * @param Len numero di caratteri richiesti
   * @return la parte iniziale di origine
   */
  public static String left(Object origine, String defVal, int Len)
  {
    String Origine = okStr(origine, defVal);

    if(Origine == null)
      return null;

    if(Len >= Origine.length())
      return Origine;

    return Origine.substring(0, Len);
  }

  /**
   * Restituisce un array di stringhe spezzando
   * la stringa in input con il delimitatore indicato.
   * @param s stringa da spezzare
   * @param delim delimitatore
   * @return array di stringhe
   */
  @SuppressWarnings("empty-statement")
  public static String[] split(String s, char delim)
  {
    if(s == null || s.length() == 0)
      return EMPTY_STRING_ARRAY;

    final int r0 = s.indexOf(delim);
    if(r0 == -1)
      return new String[]
      {
        s
      };

    int i = 2;
    int l, r = r0;
    for(; (r = s.indexOf(delim, l = r + 1)) != -1; ++i);
    String[] retval = new String[i];
    i = l = 0;
    r = r0;
    do
    {
      retval[i++] = s.substring(l, r);
    }
    while((r = s.indexOf(delim, l = r + 1)) != -1);
    retval[i] = s.substring(l);
    return retval;
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore specificato.
   * ES: join(stringhe, ',') restituisce una,due,tre
   * @param arStrings array delle stringhe da unire
   * @param separator carattere separatore fra le stringhe
   * @return
   */
  public static String join(String[] arStrings, char separator)
  {
    if(arStrings == null || arStrings.length < 1)
      return "";
    if(arStrings.length == 1)
      return arStrings[0];

    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arStrings.length; i++)
    {
      if(arStrings[i] == null)
        continue;

      if(i > 0)
        rv.append(separator);

      rv.append(arStrings[i]);
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * ES: join(stringhe, ',' , '\'') restituisce 'una','due','tre'
   * @param arStrings array delle stringhe da unire
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa
   * @return
   */
  public static String join(String[] arStrings, char separator, char delimiter)
  {
    if(arStrings == null || arStrings.length < 1)
      return "";
    if(arStrings.length == 1)
      return String.format("%c%s%c", delimiter, arStrings[0], delimiter);

    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arStrings.length; i++)
    {
      if(arStrings[i] == null)
        continue;

      if(i > 0)
        rv.append(separator);

      rv.append(delimiter).append(arStrings[i]).append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * ES: join(stringhe, ',' , '\'') restituisce 'una','due','tre'
   * @param arStrings array delle stringhe da unire
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   */
  public static String join(String[] arStrings, String separator, String delimiter)
  {
    if(arStrings == null || arStrings.length < 1)
      return "";

    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arStrings.length; i++)
    {
      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(arStrings[i]);

      if(delimiter != null)
        rv.append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   * @throws java.lang.Exception
   */
  public static <T> String join(T[] arStrings, FunctionTrowException<T, String> fn, String separator, String delimiter)
     throws Exception
  {
    if(arStrings == null || arStrings.length < 1)
      return "";

    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arStrings.length; i++)
    {
      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(fn.apply(arStrings[i]));

      if(delimiter != null)
        rv.append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   */
  public static <T> String join2(T[] arStrings, Function<T, String> fn, String separator, String delimiter)
  {
    if(arStrings == null || arStrings.length < 1)
      return "";

    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arStrings.length; i++)
    {
      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(fn.apply(arStrings[i]));

      if(delimiter != null)
        rv.append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   * @throws java.lang.Exception
   */
  public static <T> String join(Collection<T> arStrings, FunctionTrowException<T, String> fn, String separator, String delimiter)
     throws Exception
  {
    if(arStrings == null || arStrings.isEmpty())
      return "";

    int i = 0;
    StringBuilder rv = new StringBuilder(512);

    for(T obj : arStrings)
    {
      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(fn.apply(obj));

      if(delimiter != null)
        rv.append(delimiter);

      i++;
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   */
  public static <T> String join2(Collection<T> arStrings, Function<T, String> fn, String separator, String delimiter)
  {
    if(arStrings == null || arStrings.isEmpty())
      return "";

    int i = 0;
    StringBuilder rv = new StringBuilder(512);

    for(T obj : arStrings)
    {
      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(fn.apply(obj));

      if(delimiter != null)
        rv.append(delimiter);

      i++;
    }

    return rv.toString();
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @return
   */
  public static <T> String join2int(Collection<T> arStrings, Function<T, Integer> fn, String separator)
  {
    if(arStrings == null || arStrings.isEmpty())
      return "";

    int i = 0;
    StringBuilder rv = new StringBuilder(512);

    for(T obj : arStrings)
    {
      if(i > 0)
        rv.append(separator);

      rv.append(fn.apply(obj));

      i++;
    }

    return rv.toString();
  }

  /**
   * Fonde un array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * Se la funzione ritorna null o stringa vuota il valore viene ignorato.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   * @throws java.lang.Exception
   */
  public static <T> String joinNotNull(Collection<T> arStrings, FunctionTrowException<T, String> fn, String separator, String delimiter)
     throws Exception
  {
    if(arStrings == null || arStrings.isEmpty())
      return "";

    int i = 0;
    StringBuilder rv = new StringBuilder(512);

    for(T obj : arStrings)
    {
      String val = fn.apply(obj);

      if(val == null)
        continue;

      val = val.trim();
      if(val.isEmpty())
        continue;

      if(i++ > 0)
        rv.append(separator);

      if(delimiter == null)
        rv.append(val);
      else
        rv.append(delimiter).append(val).append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde un array di stringhe in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Specifico per funzione Lambda di estrazione del valore.
   * Se la funzione ritorna null o stringa vuota il valore viene ignorato.
   * @param <T> tipo di oggetto generico
   * @param arStrings array oggetti da iterare
   * @param fn funzione che ritorna il valore per ogni oggetto
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa (può essere null)
   * @return
   */
  public static <T> String join2NotNull(Collection<T> arStrings, Function<T, String> fn, String separator, String delimiter)
  {
    if(arStrings == null || arStrings.isEmpty())
      return "";

    int i = 0;
    StringBuilder rv = new StringBuilder(512);

    for(T obj : arStrings)
    {
      String val = fn.apply(obj);

      if(val == null)
        continue;

      val = val.trim();
      if(val.isEmpty())
        continue;

      if(i++ > 0)
        rv.append(separator);

      if(delimiter == null)
        rv.append(val);
      else
        rv.append(delimiter).append(val).append(delimiter);
    }

    return rv.toString();
  }

  public static String joinNotEmpty(String separator, String delimiter, String... arStrings)
  {
    if(arStrings == null || arStrings.length < 1)
      return "";

    int i = 0;
    StringBuilder rv = new StringBuilder();
    for(String s : arStrings)
    {
      if(s == null)
        continue;

      String val = s.trim();
      if(val.isEmpty())
        continue;

      if(i++ > 0)
        rv.append(separator);

      if(delimiter == null)
        rv.append(val);
      else
        rv.append(delimiter).append(val).append(delimiter);
    }

    return rv.toString();
  }

  public static String join(String[] arStrings, char separator, int min, int max)
  {
    return join(Arrays.copyOfRange(arStrings, min, max), separator);
  }

  public static String join(String[] arStrings, char separator, char delimiter, int min, int max)
  {
    return join(Arrays.copyOfRange(arStrings, min, max), separator, delimiter);
  }

  public static String join(String[] arStrings, String separator, String delimiter, int min, int max)
  {
    return join(Arrays.copyOfRange(arStrings, min, max), separator, delimiter);
  }

  /**
   * Fonde tutti gli oggetti restituiti dall'iterator in una unica stringa
   * utilizzando il separatore specificato.
   * Di ogni oggetto restituito viene chiamato il metodo toString().
   * ES: join(itrStringhe, ',') restituisce una,due,tre
   * @param itr iteratore generico
   * @param separator carattere separatore fra le stringhe
   * @return
   */
  public static String join(Iterator itr, char separator)
  {
    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; itr.hasNext(); i++)
    {
      Object obj = itr.next();

      if(i > 0)
        rv.append(separator);

      rv.append(obj.toString());
    }

    return rv.toString();
  }

  /**
   * Fonde tutti gli oggetti restituiti dall'iterator in una unica stringa
   * utilizzando il separatore e' il delimitatore specificato.
   * Di ogni oggetto restituito viene chiamato il metodo toString().
   * ES: join(itrStringhe, ',' , '\'') restituisce 'una','due','tre'
   * @param itr iteratore generico
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di ogni stringa
   * @return
   */
  public static String join(Iterator itr, char separator, char delimiter)
  {
    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; itr.hasNext(); i++)
    {
      Object obj = itr.next();

      if(i > 0)
        rv.append(separator);

      rv.append(delimiter).append(obj.toString()).append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde tutti gli oggetti restituiti dall'iterator in una unica stringa
   * utilizzando il separatore specificato.
   * Di ogni oggetto restituito viene chiamato il metodo toString().
   * ES: join(itrStringhe, ',') restituisce una,due,tre
   * @param itr iteratore generico
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di stringa (può essere null)
   * @return
   */
  public static String join(Iterator itr, String separator, String delimiter)
  {
    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; itr.hasNext(); i++)
    {
      Object obj = itr.next();

      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(obj.toString());

      if(delimiter != null)
        rv.append(delimiter);
    }

    return rv.toString();
  }

  /**
   * Fonde insieme l'array di interi indicato utilizzando il relativo separatore.
   * ES: join(valori, ',') restituisce 1,2,3
   * @param arInt array di interi da unire
   * @param separator carattere separatore fra le stringhe
   * @return
   */
  public static String join(int[] arInt, char separator)
  {
    if(arInt == null || arInt.length < 1)
      return "";
    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arInt.length; i++)
      rv.append(separator).append(arInt[i]);
    return rv.substring(1);
  }

  public static String join(int[] arInt, char separator, int min, int max)
  {
    return join(Arrays.copyOfRange(arInt, min, max), separator);
  }

  /**
   * Spezza una stringa in base al delimitatore
   * generando una lista di stringhe.
   * Viene usata la StringTokenizer per estrarre i componenti.
   * @param s stringa in ingresso
   * @param delim stringa delimitatore
   * @param removeEmpty se vero non inserisce stringhe vuote in uscita
   * @return lista di stringhe componenti (null se vuoto)
   */
  public static List<String> string2List(String s, String delim, boolean removeEmpty)
  {
    ArrayList<String> l = new ArrayList<String>();
    if(s == null || delim == null)
      return l;

    StringTokenizer st = new StringTokenizer(s, delim);
    while(st.hasMoreTokens())
    {
      String t = st.nextToken().trim();
      if(removeEmpty && t.isEmpty())
        continue;
      l.add(t);
    }

    return l;
  }

  /**
   * Spezza una stringa in base al delimitatore
   * generando una lista di stringhe.
   * Viene usata la StringTokenizer per estrarre i componenti.
   * @param s stringa in ingresso
   * @param delim stringa delimitatore
   * @return lista di stringhe componenti (null se vuoto)
   */
  public static List<String> string2List(String s, String delim)
  {
    return string2List(s, delim, false);
  }

  /**
   * Spezza una stringa in base al delimitatore
   * generando una lista di stringhe.
   * Viene usata la StringTokenizer per estrarre i componenti.
   * @param s stringa in ingresso
   * @param delim stringa delimitatore
   * @return array di stringhe componenti (null se vuoto)
   */
  public static String[] string2Array(String s, String delim)
  {
    return string2Array(s, delim, false);
  }

  /**
   * Spezza una stringa in base al delimitatore
   * generando una lista di stringhe.
   * Viene usata la StringTokenizer per estrarre i componenti.
   * @param s stringa in ingresso
   * @param delim stringa delimitatore
   * @param removeEmpty se vero non inserisce stringhe vuote in uscita
   * @return array di stringhe componenti (null se vuoto)
   */
  public static String[] string2Array(String s, String delim, boolean removeEmpty)
  {
    List l;
    if(s == null || (l = string2List(s, delim, removeEmpty)) == null || l.isEmpty())
      return EMPTY_STRING_ARRAY;

    return (String[]) l.toArray(new String[l.size()]);
  }

  /**
   * Parsing di una stringa di proprietà per delimitatore.
   * Effettua il parsing di una stringa del tipo
   * "CHIAVE1=VALORE1, CHIAVE2=VALORE2, CHIAVE3=VALORE3"
   * nella corrispondente mappa chiave/valore.
   * Chiave e valore sono trimmati.
   * @param s stringa da convertire
   * @param delim delimitatore delle coppie chiave/valore
   * @param removeEmpty vero per rimuovere i valori nulli
   * @return mappa chiave/valore
   */
  public static Map<String, String> string2Map(String s, String delim, boolean removeEmpty)
  {
    return string2Map(s, delim, '=', removeEmpty);
  }

  /**
   * Parsing di una stringa di proprietà per delimitatore.
   * Effettua il parsing di una stringa del tipo
   * "CHIAVE1=VALORE1, CHIAVE2=VALORE2, CHIAVE3=VALORE3"
   * nella corrispondente mappa chiave/valore.
   * Chiave e valore sono trimmati.
   * @param s stringa da convertire
   * @param delim delimitatore delle coppie chiave/valore
   * @param split il carattere che separa CHIAVE da VALORE (nell'esempio '=')
   * @param removeEmpty vero per rimuovere i valori nulli
   * @return mappa chiave/valore
   */
  public static Map<String, String> string2Map(String s, String delim, char split, boolean removeEmpty)
  {
    ArrayMap<String, String> rv = new ArrayMap<String, String>();
    List<String> lsStr = string2List(s, delim, removeEmpty);
    if(lsStr == null || lsStr.isEmpty())
      return rv;

    int pos;
    for(String ss : lsStr)
    {
      if((pos = ss.indexOf(split)) != -1)
      {
        String key = okStr(ss.substring(0, pos));
        String val = okStr(ss.substring(pos + 1));

        if(key.isEmpty() || (removeEmpty && val.isEmpty()))
          continue;

        rv.put(key, val);
      }
    }

    return rv;
  }

  /**
   * Formattazione di una mappa di proprietà.
   * Produce una stringa concatenzione di coppie CHIAVE=VALORE separta da delimitatore indicato.
   * Esegue l'inverso di string2Map().
   * @param map coppie chiave/valore
   * @param delim delimitatore delle coppie chiave/valore
   * @param removeEmpty vero per rimuovere i valori nulli
   * @return stringa concatenazione
   */
  public static String map2String(Map map, String delim, boolean removeEmpty)
  {
    StringBuilder sb = new StringBuilder(64 * map.size());
    for(Object entryObj : map.entrySet())
    {
      Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) entryObj;
      String key = okStrNull(entry.getKey());
      String value = okStr(entry.getValue());
      if(key == null || (removeEmpty && value.isEmpty()))
        continue;
      sb.append(key).append('=').append(value).append(delim);
    }
    return sb.toString();
  }

  /**
   * Converte un array di stringhe nel corrispettivo
   * array di interi. Le stringhe non convertibili
   * diventano defVal nell'indice corrispondente dell'array
   * ritornato.
   * @param sArr array da convertire
   * @param defVal valore di default per stringhe non convertibili
   * @return array di interi corrispondente
   */
  public static int[] strarr2intarr(String[] sArr, int defVal)
  {
    int[] rv = new int[sArr.length];
    for(int i = 0; i < rv.length; i++)
      rv[i] = parse(sArr[i], defVal);
    return rv;
  }

  /**
   * Sostituisce con underscore tutti i caratteri che non sono lettere [a-z][A-Z] o digit [0-9].
   * @param s stringa input
   * @return stringa sostituita
   */
  public static String purge(String s)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuilder sb = new StringBuilder(len + 5);

    for(int i = 0; i < len; i++)
    {
      int c = arChar[i];

      if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
        sb.append(arChar[i]);
      else
        sb.append('_');
    }

    return sb.toString();
  }

  /**
   * Rimuove uno scecifico carattere dalla stringa.
   * ES: purge("AA-BB-CC", '-') ritorna "AABBCC"
   * @param s stringa originale
   * @param toremove carattere da rimuovere
   * @return la stringa senza il carattere specificato
   */
  public static String purge(String s, char toremove)
  {
    int len = s.length();
    char[] arChar = s.toCharArray();
    StringBuilder sb = new StringBuilder(len + 5);

    for(int i = 0; i < len; i++)
    {
      if(arChar[i] != toremove)
        sb.append(arChar[i]);
    }

    return sb.toString();
  }

  /**
   * Verifica stringa valida.
   * Ritorna vero se il parametro in input
   * è diverso da null e la sua rappresentazione stringa
   * contiene caratteri diversi da spazio.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param o un qualiasia oggetto java
   * @return vero se toString() restituisce una stringa valida
   */
  public static boolean isOkStr(Object o)
  {
    return okStr(o, null) != null;
  }

  /**
   * Verifica gruppo di stringhe valide.
   * @param args stringhe da verificare
   * @return vero se sono tutte valide
   */
  public static boolean isOkStrAll(Object... args)
  {
    for(Object o : args)
    {
      if(!isOkStr(o))
        return false;
    }
    return true;
  }

  /**
   * Verifica gruppo di stringhe valide.
   * @param args stringhe da verificare
   * @return vero se almeno una stringa è valida
   */
  public static boolean isOkStrAny(Object... args)
  {
    for(Object o : args)
    {
      if(isOkStr(o))
        return true;
    }
    return false;
  }

  /**
   * Confronto fra stringhe.
   * Ritorna vero se i due oggetti sono entrambi null
   * oppure le due rappresentazione stringa sono uguali
   * ignorando eventuali spazi bianchi.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param o1 un qualsiasi oggetto java
   * @param o2 un qualsiasi oggetto java
   * @return vero se le stringhe sono uguali
   */
  public static boolean isEqu(Object o1, Object o2)
  {
    String sVal1 = okStr(o1, null);
    String sVal2 = okStr(o2, null);

    // entrambi null sono sicuramente uguali
    if(sVal1 == null && sVal2 == null)
      return true;

    // solo uno dei due null sono diversi
    if(sVal1 == null || sVal2 == null)
      return false;

    return sVal1.equals(sVal2);
  }

  /**
   * Confronto fra stringhe.
   * E' la versione case insensitive di isEqu
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param o1 un qualsiasi oggetto java
   * @param o2 un qualsiasi oggetto java
   * @return vero se le stringhe sono uguali (case insensitive)
   */
  public static boolean isEquNocase(Object o1, Object o2)
  {
    String sVal1 = okStr(o1, null);
    String sVal2 = okStr(o2, null);

    // entrambi null sono sicuramente uguali
    if(sVal1 == null && sVal2 == null)
      return true;

    // solo uno dei due null sono diversi
    if(sVal1 == null || sVal2 == null)
      return false;

    return sVal1.equalsIgnoreCase(sVal2);
  }

  /**
   * Confronto fra caratteri.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param c carattere da confrontare
   * @param o2 un qualsiasi oggetto java
   * @return
   */
  public static boolean isEqu(char c, Object o2)
  {
    return isEqu("" + c, o2);
  }

  /**
   * Confronto fra stringhe.
   * @param o1 un qualsiasi oggetto java
   * @param values un array di stringhe da confrontare
   * @return vero se almeno un confronto va a buon fine
   */
  public static boolean isEqu(Object o1, String[] values)
  {
    if(values == null || values.length == 0)
      return false;

    String sVal1 = okStr(o1, null);
    if(sVal1 == null)
      return false;

    for(String sVal2 : values)
    {
      if(sVal2 == null)
        continue;

      if(sVal1.equals(sVal2.trim()))
        return true;
    }

    return false;
  }

  /**
   * Confronto fra stringhe.
   * @param o1 un qualsiasi oggetto java
   * @param values una lista variabile di stringhe da confrontare
   * @return vero se almeno un confronto va a buon fine
   */
  public static boolean isEquAny(Object o1, String... values)
  {
    return isEqu(o1, values);
  }

  /**
   * Confronto fra stringhe.
   * E' la versione case insensitive di isEqu.
   * @param o1 un qualsiasi oggetto java
   * @param values un array di stringhe da confrontare
   * @return vero se almeno un confronto va a buon fine
   */
  public static boolean isEquNocase(Object o1, String[] values)
  {
    String sVal1 = okStr(o1, null);
    if(o1 == null)
      return false;

    for(String sTest : values)
    {
      String sVal2 = okStr(sTest, null);
      if(sVal2 == null)
        continue;

      if(sVal1.equalsIgnoreCase(sVal2))
        return true;
    }

    return false;
  }

  /**
   * Confronto fra stringhe.
   * E' la versione case insensitive di isEquAny.
   * @param o1 un qualsiasi oggetto java
   * @param values una lista variabile di stringhe da confrontare
   * @return vero se almeno un confronto va a buon fine
   */
  public static boolean isEquNocaseAny(Object o1, String... values)
  {
    return isEquNocase(o1, values);
  }

  /**
   * Confronto contenimento fra stringhe.
   * @param o1 un qualsiasi oggetto java
   * @param values un array di stringhe da confrontare
   * @return vero se almeno una delle stringhe è contenuta all'interno di o1.toString()
   */
  public static boolean contains(Object o1, String[] values)
  {
    String sVal1 = okStr(o1, null);
    if(sVal1 == null)
      return false;

    for(String sTest : values)
    {
      String sVal2 = okStr(sTest, null);
      if(sVal2 == null)
        continue;

      if(sVal1.contains(sVal2))
        return true;
    }

    return false;
  }

  /**
   * Confronto contenimento fra stringhe.
   * @param o1 un qualsiasi oggetto java
   * @param values una lista variabile di stringhe da confrontare
   * @return vero se almeno una delle stringhe è contenuta all'interno di o1.toString()
   */
  public static boolean containsAny(Object o1, String... values)
  {
    return contains(o1, values);
  }

  /**
   * Confronta due stringhe.
   * Equivalente a String.compare ma applica okStr ai due parametri.
   * @param o1 primo parametro da convertire in stringa
   * @param o2 secondo parametro da convertire in stringa
   * @return confronto fra le stringhe equivalenti
   */
  public static int compare(Object o1, Object o2)
  {
    return okStr(o1).compareTo(okStr(o2));
  }

  /**
   * Confronta due stringhe case insensitive.
   * Equivalente a String.compare ma applica okStr ai due parametri.
   * @param o1 primo parametro da convertire in stringa
   * @param o2 secondo parametro da convertire in stringa
   * @return confronto fra le stringhe equivalenti
   */
  public static int compareIgnoreCase(Object o1, Object o2)
  {
    return okStr(o1).compareToIgnoreCase(okStr(o2));
  }

  /**
   * Confronta due stringhe di versione.
   * Le stringhe possono avere più punti di separazione per le sotto versioni
   * (v1=2.1 v2=2.2; v1=2.0.1 v2=2.0).
   * @param v1 primo parametro da convertire in stringa
   * @param v2 secondo parametro da convertire in stringa
   * @param delim il carattere delimitatore (di solito '.')
   * @return il confronto (0, 1, -1) fra le due versioni
   */
  public static int compareVersion(Object v1, Object v2, char delim)
  {
    String[] s1 = split(okStr(v1), delim);
    String[] s2 = split(okStr(v2), delim);

    int num = Math.max(s1.length, s2.length);
    for(int i = 0; i < num; i++)
    {
      int k1 = i < s1.length ? parse(s1[i], 0) : 0;
      int k2 = i < s2.length ? parse(s2[i], 0) : 0;

      if(k1 > k2)
        return 1;
      if(k1 < k2)
        return -1;
    }

    return 0;
  }

  /**
   * Ritorna la stringa valida dell'oggetto specifiato.
   * Se l'oggetto è null oppure o.toString().trim() da stringa vuota
   * ritorna stringa vuota, altrimenti
   * il valore stringa senza gli spazi bianchi non significativi.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * Equivalente ad okStr(o, "").
   * @param o un qualsiasi oggetto java
   * @return stringa vuota oppure la stringa valida
   */
  public static String okStr(Object o)
  {
    return okStr(o, "");
  }

  /**
   * Ritorna la stringa valida dell'oggetto specifiato.
   * Se l'oggetto è null oppure o.toString().trim() da stringa vuota
   * ritorna defVal, altrimenti
   * il valore stringa senza gli spazi bianchi non significativi.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param o un qualsiasi oggetto java
   * @param defVal valore di default in caso di stringa non valida
   * @return defVal oppure la stringa valida
   */
  public static String okStr(Object o, String defVal)
  {
    if(o == null)
      return defVal;

    String rv = o.toString();
    if(rv == null || rv.isEmpty())
      return defVal;

    rv = rv.trim();
    if(rv.isEmpty())
      return defVal;

    return rv;
  }

  /**
   * Ritorna la stringa valida dell'oggetto specifiato.
   * Come okStr(o) ma la stringa ritornata è lunga max maxlen caratteri.
   * @param o un qualsiasi oggetto java
   * @param maxlen numero massimo caratteri in uscita
   * @return stringa vuota oppure la stringa valida
   */
  public static String okStr(Object o, int maxlen)
  {
    String rv = okStr(o);
    return rv.length() <= maxlen ? rv : rv.substring(0, maxlen);
  }

  /**
   * Ritorna la stringa valida dell'oggetto specifiato.
   * Se l'oggetto è null oppure o.toString().trim() da stringa vuota
   * ritorna null, altrimenti
   * il valore stringa senza gli spazi bianchi non significativi.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * Equivalente ad okStr(o, null).
   * @param o un qualsiasi oggetto java
   * @return null oppure la stringa valida
   */
  public static String okStrNull(Object o)
  {
    return okStr(o, null);
  }

  /**
   * Ritorna la stringa valida dell'oggetto specifiato.
   * Se l'oggetto è null oppure o.toString().trim() da stringa vuota
   * ritorna 'non breaking space' <!-- &nbsp; -->, altrimenti
   * il valore stringa senza gli spazi bianchi non significativi.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * Equivalente ad <code>okStr(o, "&nbsp;").</code>
   * @param o un qualsiasi oggetto java
   * @return stringa vuota oppure la stringa valida
   */
  public static String okStrHtml(Object o)
  {
    return okStr(o, "&nbsp;");
  }

  /**
   * Ritorna la prima stringa valida dell'array.
   * L'array viene analizzato in sequenza scartando le stringhe nulle o vuote.
   * @param o array di valori da verificare
   * @param defVal valore di default se nessun valore valido
   * @return la prima occorrenza valida trimmata oppure defVal se nessun valore valido
   */
  public static String okStr(Object[] o, String defVal)
  {
    if(o == null)
      return defVal;

    for(int i = 0; i < o.length; i++)
    {
      Object test = o[i];

      if(test == null)
        continue;

      String sTest = test.toString().trim();
      if(sTest.length() == 0)
        continue;

      return sTest;
    }

    return defVal;
  }

  /**
   * Ritorna la prima stringa valida dei parametri passati.
   * @param values valori da confrontare
   * @return la prima occorrenza valida trimmata oppure null se nessun valore valido
   */
  public static String okStrAny(Object... values)
  {
    return okStr(values, null);
  }

  /**
   * Converte stringa in intero.
   * Ritorna il valore intero della stringa in base 10 senza
   * sollevare alcuna eccezione. In caso di errore di qualsiasi
   * tipo il valore defVal viene ritornato.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param val un qualsiasi oggetto java
   * @param defVal valore di default se conversione non possibile
   * @return valore convertito oppure defVal
   */
  public static int parse(Object val, int defVal)
  {
    try
    {
      if(val == null)
        return defVal;

      if(val instanceof Integer)
        return (Integer) val;

      if(val instanceof Number)
        return ((Number) val).intValue();

      String s;
      if(val == null || (s = okStrNull(val)) == null)
        return defVal;

      // una stringa del tipo +90 viene interpretata come errata: rimuoviamo il +
      if(s.charAt(0) == '+')
        s = s.substring(1);

      return Integer.parseInt(s);
    }
    catch(Exception e)
    {
      return defVal;
    }
  }

  /**
   * Converte stringa in doppia precisione.
   * Ritorna il valore doppia precisione della stringa in base 10 senza
   * sollevare alcuna eccezione. In caso di errore di qualsiasi
   * tipo il valore defVal viene ritornato.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param val un qualsiasi oggetto java
   * @param defVal valore di default se conversione non possibile
   * @return valore convertito oppure defVal
   */
  public static double parse(Object val, double defVal)
  {
    try
    {
      if(val == null)
        return defVal;

      if(val instanceof Double)
        return (Double) val;

      if(val instanceof Number)
        return ((Number) val).doubleValue();

      String s;
      if(val == null || (s = okStrNull(val)) == null)
        return defVal;

      return Double.parseDouble(s);
    }
    catch(Exception e)
    {
      return defVal;
    }
  }

  /**
   * Converte in boolean.
   * La stringa in ingresso viene confrontata con
   * s,y,v,t,si,yes,vero,true,on,1 e se uno di questi
   * coincide ritorna true.
   * In tutti gli altri casi ritorna false.
   * @param sBool stringa da convertire (può essere null)
   * @return vero se uno dei pattern corrisponde
   */
  public static boolean checkTrue(String sBool)
  {
    // non rimuovere okstr() perchè spesso è usata direttamente
    sBool = okStr(sBool).toLowerCase();
    return (sBool != null && (sBool.equals("s") || sBool.equals("y")
       || sBool.equals("v") || sBool.equals("t")
       || sBool.equals("si") || sBool.equals("yes")
       || sBool.equals("vero") || sBool.equals("true") || sBool.equals("on")
       || sBool.equals("1")));
  }

  /**
   * Converte in boolean.
   * La stringa in ingresso viene confrontata con
   * n,f,no,not,falso,false,off,0 e se uno di questi
   * coincide ritorna true.
   * In tutti gli altri casi ritorna false.
   * @param sBool stringa da convertire (può essere null)
   * @return vero se uno dei pattern corrisponde
   */
  public static boolean checkFalse(String sBool)
  {
    // non rimuovere okstr() perchè spesso è usata direttamente
    sBool = okStr(sBool).toLowerCase();
    return (sBool != null && (sBool.equals("n")
       || sBool.equals("f")
       || sBool.equals("no") || sBool.equals("not")
       || sBool.equals("falso") || sBool.equals("false") || sBool.equals("off")
       || sBool.equals("0")));
  }

  /**
   * Converte in boolean.
   * La stringa in ingresso viene confrontata per un valido
   * valore di true o di false (checkTrue() checkFalse())
   * e se una pattern corrisponde ritorna il valore booleano
   * corrispondente. Gli spazi non significativi vengono ignorati.
   * Diversamente solleva una eccezione per segnalare errore.
   * Viene usato toString() per estrarre la stringa dell'oggetto.
   * @param val stringa da convertire (può essere null)
   * @param defVal valore di default se il parsing non è possibile
   * @return il valore booleano corrispondente
   */
  public static boolean checkTrueFalse(Object val, boolean defVal)
  {
    String s;
    if(val == null || (s = okStrNull(val)) == null)
      return defVal;

    if(checkTrue(s))
      return true;
    if(checkFalse(s))
      return false;

    return defVal;
  }

  public static String cvtHtml2Text(String html)
  {
    // converte le sequenze html
    html = CvtWEB2Unicode(html);

    // cambia i tag significativi
    html = strReplace(html, "<br />", "\r\n");
    html = strReplace(html, "<br/>", "\r\n");
    html = strReplace(html, "<br>", "\r\n");
    html = strReplace(html, "</tr>", "\r\n");
    html = strReplace(html, "</li>", "\r\n");

    // applico l'espressione regolare,
    // sostituendo i caratteri speciali con la stringa vuota
    Matcher m = htmlRemovePattern.matcher(html);
    StringBuffer sb = new StringBuffer();
    while(m.find())
    {
      m.appendReplacement(sb, "");
    }
    m.appendTail(sb);

    return sb.toString();
  }

  public static Map<Character, String> createUnicode2HtmlMap()
  {
    HashMap<Character, String> m = new HashMap<Character, String>(128);

    // accenti gravi
    m.put('à', "&agrave;");
    m.put('è', "&egrave;");
    m.put('ì', "&igrave;");
    m.put('ò', "&ograve;");
    m.put('ù', "&ugrave;");

    // accenti acuti
    m.put('á', "&aacute;");
    m.put('é', "&eacute;");
    m.put('í', "&iacute;");
    m.put('ó', "&oacute;");
    m.put('ú', "&uacute;");

    // speciali di 'a'
    m.put('â', "&acirc;");
    m.put('ã', "&atilde;");
    m.put('å', "&aring;");
    m.put('æ', "&aelig;");
    m.put('ä', "&auml;");

    // speciali di 'e'
    m.put('ê', "&ecirc;");
    m.put('ë', "&euml;");

    // speciali di 'i'
    m.put('ï', "&iuml;");

    // speciali di 'o'
    m.put('ô', "&ocirc;");
    m.put('õ', "&otilde;");
    m.put('ö', "&ouml;");
    m.put('ø', "&oslash;");

    // speciali di 'u'
    m.put('û', "&ucirc;");
    m.put('ü', "&uuml;");

    // speciali di 'c'
    m.put('ç', "&ccedil;");

    // speciali di 'n'
    m.put('ñ', "&ntilde;");

    // speciali di 'y'
    m.put('ý', "&yacute;");
    m.put('ÿ', "&yuml;");

    // altri speciali
    m.put('¡', "&iexcl;");
    m.put('¿', "&iquest;");

    // speciali di 'A'
    m.put('À', "&Agrave;");
    m.put('Á', "&Aacute;");
    m.put('Â', "&Acirc;");
    m.put('Ã', "&Atilde;");
    m.put('Æ', "&AElig;");
    m.put('Ä', "&Auml;");
    m.put('Å', "&Aring;");
    m.put('Ç', "&Ccedil;");

    // speciali di 'E'
    m.put('È', "&Egrave;");
    m.put('É', "&Eacute;");
    m.put('Ê', "&Ecirc;");
    m.put('Ë', "&Euml;");

    // speciali di 'I'
    m.put('Ì', "&Igrave;");
    m.put('Í', "&Iacute;");
    m.put('Î', "&Icirc;");
    m.put('Ï', "&Iuml;");

    // speciali di 'N'
    m.put('Ñ', "&Ntilde;");

    // speciali di 'O'
    m.put('Ò', "&Ograve;");
    m.put('Ó', "&Oacute;");
    m.put('Ô', "&Ocirc;");
    m.put('Õ', "&Otilde;");
    m.put('Ö', "&Ouml;");
    m.put('Ø', "&Oslash;");

    // speciali di 'U'
    m.put('Ù', "&Ugrave;");
    m.put('Ú', "&Uacute;");
    m.put('Û', "&Ucirc;");
    m.put('Ü', "&Uuml;");

    // speciali di 'Y'
    m.put('Ý', "&Yacute;");

    // miscellanea
    m.put('\'', "&rsquo;");
    m.put('&', "&amp;");

    //assolutamente da non fare: blocca il parsing dei form
    //m.put(' ', "&nbsp;");
    return m;
  }

  public static Map<String, Character> createHtml2UnicodeMap()
  {
    Map<Character, String> m = createUnicode2HtmlMap();
    return reverseMap(m, new HashMap<String, Character>());
  }

  /**
   * Produce la map inversa di una map in input.
   * Per poter invertire tutti i valori, questi devono essere
   * univoci, altrimenti la mappa inversa non potrà contenerli tutti.
   * @param <K> tipo della chiave mappa in ingresso
   * @param <V> tipo del valore mappa in ingresso
   * @param map mappa da invertire
   * @param rmap
   * @return mappa inversa
   */
  public static <K, V> Map<V, K> reverseMap(Map<K, V> map, Map<V, K> rmap)
  {
    Iterator<Map.Entry<K, V>> itrPat = map.entrySet().iterator();
    while(itrPat.hasNext())
    {
      Map.Entry<K, V> entry = itrPat.next();
      rmap.put(entry.getValue(), entry.getKey());
    }
    return rmap;
  }

  /**
   * Conta il numero di linee contenuto in una stringa.
   * @param input stringa in ingresso
   * @return numero di linee contenute
   */
  public static int countLineesInString(String input)
  {
    return countCharacterInString(input, '\n');
  }

  /**
   * Conta le occorrenze di un carattere nella stringa indicata.
   * @param input stringa in ingresso
   * @param car carattere da contare
   * @return numero di occorrenze
   */
  public static int countCharacterInString(String input, int car)
  {
    int pos = 0, count = 0;
    while((pos = input.indexOf(car, pos)) != -1)
    {
      pos++;
      count++;
    }
    return count;
  }

  /**
   * Ritorna la parte a destra di una stringa se inizia
   * con una determinata sequenza di test.
   * @param origine stringa originale
   * @param test stringa di test (in testa all'originale)
   * @return la parte rimanente di orgine se inizia per test
   */
  public static String startRightString(String origine, String test)
  {
    if(origine.startsWith(test))
      return origine.substring(test.length());
    return null;
  }

  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di stringhe.
   * @param arr lista di stringhe
   * @return array di stringhe
   */
  public static String[] toArray(String... arr)
  {
    return arr;
  }

  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di stringhe.
   * @param arr lista di stringhe
   * @return lista di stringhe
   */
  public static List<String> toArrayList(String... arr)
  {
    return Arrays.asList(arr);
  }

  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di stringhe.
   * @param ls lista di stringhe
   * @return array di stringhe
   */
  public static String[] toArray(Collection<String> ls)
  {
    if(ls.isEmpty())
      return EMPTY_STRING_ARRAY;

    return ls.toArray(new String[ls.size()]);
  }

  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di oggetti qualiasiasi.
   * Se l'oggetto contenuto è diverso da null chiama toString()
   * per ottenerne la rappresentazione.
   * @param ls lista di oggetti qualsiasi.
   * @return array di stringhe
   */
  public static String[] toArrayGeneric(List ls)
  {
    String[] rv = new String[ls.size()];
    for(int i = 0; i < ls.size(); i++)
    {
      Object o = ls.get(i);
      rv[i] = o == null ? null : o.toString();
    }
    return rv;
  }

  /**
   * Helper per creare velocemente array di stringhe a partire
   * da liste di oggetti qualiasiasi.
   * Se l'oggetto contenuto è diverso da null chiama toString()
   * per ottenerne la rappresentazione.
   * @param ls lista di oggetti qualsiasi.
   * @param begin indice iniziale della lista
   * @param end indice finale della lista
   * @return array di stringhe
   */
  public static String[] toArrayGeneric(List ls, int begin, int end)
  {
    if(begin == end)
      return EMPTY_STRING_ARRAY;

    if(begin > end)
      throw new RuntimeException("Invalid range: end must be greater than begin.");

    if(end > ls.size())
      end = ls.size();

    int j = 0;
    String[] rv = new String[end - begin];
    for(int i = begin; i < end; i++)
    {
      Object o = ls.get(i);
      rv[j++] = o == null ? null : o.toString();
    }
    return rv;
  }

  /**
   * Fonde stringhe path insieme evitando duplicati di separatore.
   * @param path path origine
   * @param s nome di file o directory da aggiungere
   * @return la path con i separatori corretti
   */
  public static String mergePath(String path, String s)
  {
    boolean ep = path.endsWith(File.separator);
    boolean ss = s.startsWith(File.separator);

    if(ep && ss)
      return path + s.substring(1);
    else if(ep && !ss)
      return path + s;
    else if(!ep && ss)
      return path + s;
    else
      return path + File.separator + s;
  }

  /**
   * Fonde stringhe path insieme evitando duplicati di separatore.
   * @param path path origine
   * @param lsFiles array di nomi file da fondere
   * @return la path con i separatori corretti
   */
  public static String mergePath(String path, String[] lsFiles)
  {
    StringBuilder sb = new StringBuilder(512);
    if(path.endsWith(File.separator))
      sb.append(path.substring(0, path.length() - 1));
    else
      sb.append(path);

    for(String sp : lsFiles)
    {
      sb.append(File.separator).append(sp);
    }

    return sb.toString();
  }

  /**
   * Fonde stringhe path insieme evitando duplicati di separatore.
   * @param path path origine
   * @param lsFiles lista di nomi file da fondere
   * @return la path con i separatori corretti
   */
  public static String mergePath(String path, List<String> lsFiles)
  {
    return mergePath(path, toArray(lsFiles));
  }

  /**
   * Normalizza path con separatore corretto per la piattaforma.
   * La path in ingresso può contentere come separatore entrambi
   * i caratteri '/' e '\\'. In uscita verrà restituita la path
   * con il serparatori corretti compatibili con la piattaforma.
   * @param path path da correggere
   * @return path corretta
   */
  public static String normalizePath(String path)
  {
    String rv = path.replace('\\', '/');

    if(File.separatorChar == '/')
    {
      return rv;
    }
    else
    {
      return rv.replace('/', File.separatorChar);
    }
  }

  /**
   * Fonde una array di stringhe in una unica stringa
   * da utilizzare come stringa comando per l'attivazione di un eseguibile.
   * Ovvero gli elementi che contengono stringhe sono racchiusi in "" e il separatore è lo spazio.
   * ES: joinCommand(stringhe) restituisce "c:\program files\my app\pippo.exe" c:\tmp\mio.txt "c:\tmp\mio file.txt"
   * @param arStrings array delle stringhe da unire
   * @return
   */
  public static String joinCommand(String[] arStrings)
  {
    if(arStrings == null || arStrings.length <= 0)
      return "";

    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; i < arStrings.length; i++)
    {
      if(arStrings[i] == null)
        continue;

      if(i > 0)
        rv.append(' ');

      if(arStrings[i].contains(" "))
        rv.append("\"").append(arStrings[i]).append("\"");
      else
        rv.append(arStrings[i]);
    }

    return rv.toString();
  }
}
