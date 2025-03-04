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
package org.commonlib5.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convertitore da tabella html a json.
 *
 * @author Nicola De Nisco
 */
public class HtmlTableJsonConverter
{
  public static final Pattern TD_PATTERN = Pattern.compile("<td.*?>(.*?)</td>");
  public static final Pattern TR_PATTERN = Pattern.compile("<tr.*?>(.*?)</tr>");

  /**
   * Riceve in input HTML del corpo tabella e produce l'equivalente Json.
   * @param html corpo tabella (tr/td)
   * @return json equivalente
   * @throws Exception
   */
  public String convertHtml2Json(String html)
     throws Exception
  {
    String rv = StringOper.okStr(html).replaceAll("\n", "").replaceAll("\r", "");
    if(rv.isEmpty())
      return rv;

    rv = resolveMacroTD(TD_PATTERN, rv, "\"", "\",");
    rv = resolveMacroTR(TR_PATTERN, rv, "[", "],");

    rv = rv.replaceAll(",\\]", "]");
    if(rv.endsWith(","))
      rv = rv.substring(0, rv.length() - 1);

    return rv;
  }

  protected String resolveMacroTD(Pattern macroPattern, String seg, String before, String afther)
     throws Exception
  {
    Matcher m = macroPattern.matcher(seg);

    StringBuffer sb = new StringBuffer();
    while(m.find())
    {
      if(m.groupCount() != 1)
        throw new Exception("Errore di sintassi in " + seg);

      String keyMacro = m.group(1);
      String valMacro = resolveMacroTD(keyMacro, seg);

      m.appendReplacement(sb, before + valMacro + afther);
    }
    m.appendTail(sb);

    return StringOper.okStr(sb);
  }

  protected String resolveMacroTR(Pattern macroPattern, String seg, String before, String afther)
     throws Exception
  {
    Matcher m = macroPattern.matcher(seg);

    StringBuffer sb = new StringBuffer();
    while(m.find())
    {
      if(m.groupCount() != 1)
        throw new Exception("Errore di sintassi in " + seg);

      String keyMacro = m.group(1);
      String valMacro = resolveMacroTR(keyMacro, seg);

      m.appendReplacement(sb, before + valMacro + afther);
    }
    m.appendTail(sb);

    return StringOper.okStr(sb);
  }

  protected String resolveMacroTD(String keyMacro, String seg)
     throws Exception
  {
    return keyMacro.replace('\"', '\'');
  }

  protected String resolveMacroTR(String keyMacro, String seg)
     throws Exception
  {
    return keyMacro;
  }
}
