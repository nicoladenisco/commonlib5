/*
 *  Copyright (C) 2011 WinSOFT di Nicola De Nisco
 *
 *  Questo software è proprietà di Nicola De Nisco.
 *  I termini di ridistribuzione possono variare in base
 *  al tipo di contratto in essere fra Nicola De Nisco e
 *  il fruitore dello stesso.
 *
 *  Fare riferimento alla documentazione associata al contratto
 *  di committenza per ulteriori dettagli.
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
