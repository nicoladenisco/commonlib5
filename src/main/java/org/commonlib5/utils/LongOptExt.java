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

import gnu.getopt.LongOpt;

/**
 * Opzione da linea di comando con visualizzazione automatica dell'help.
 *
 * @author Nicola De Nisco
 */
public class LongOptExt extends LongOpt
{
  protected String helpMsg = null;

  public LongOptExt(String name, int has_arg, int val, String helpMsg)
  {
    this(name, has_arg, null, val, helpMsg);
  }

  public LongOptExt(String name, int has_arg, StringBuffer flag, int val, String helpMsg)
  {
    super(name, has_arg, flag, val);
    this.helpMsg = helpMsg;
  }

  public String getHelpMsg()
  {
    String sArg = "";
    switch(getHasArg())
    {
      case NO_ARGUMENT:
        sArg = "";
        break;
      case OPTIONAL_ARGUMENT:
        sArg = " [val]";
        break;
      case REQUIRED_ARGUMENT:
        sArg = " <val>";
        break;
    }

    return String.format("  -%c --%-25.25s %s",
       (char) getVal(), getName() + sArg, helpMsg);
  }

  public static String getOptstring(LongOpt[] opts)
  {
    StringBuilder rv = new StringBuilder();
    for(LongOpt l : opts)
    {
      char opt = (char) l.getVal();

      if(rv.indexOf("" + opt) != -1)
        throw new IllegalArgumentException(String.format("Duplicate option '%c' in arguments.", opt));

      rv.append(opt);
      switch(l.getHasArg())
      {
        case OPTIONAL_ARGUMENT:
          rv.append(';');
          break;
        case REQUIRED_ARGUMENT:
          rv.append(':');
          break;
      }
    }
    return rv.toString();
  }
}
