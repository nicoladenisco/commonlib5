/*
 * Copyright (C) 2012 Nicola De Nisco
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

import java.util.Date;

/**
 * Interfaccia di un parser delle date
 * da utlizzare per la validazione dei forms.
 *
 * @author Nicola De Nisco
 */
public interface ValidatorParserInterface
{
  public static final int FLAG_ROUND_DEAULT = 0;
  public static final int FLAG_ROUND_BEGIN_DAY = 1;
  public static final int FLAG_ROUND_END_DAY = 2;
  public static final int FLAG_ALWAIS_BEGIN_DAY = 3;
  public static final int FLAG_ALWAIS_END_DAY = 4;

  /**
   * Formatta la data.
   * @param d data da formattare
   * @return stringa rappresentazione della data
   */
  public String fmtDate(Date d);

  /**
   * Formatta l'ora.
   * @param d data da formattare
   * @return stringa rappresentazione dell'orario
   */
  public String fmtTime(Date d);

  /**
   * Formatta data e ora.
   * @param d data da formattare
   * @return stringa rappresentazione della data
   */
  public String fmtDateTime(Date d);

  /**
   * Interpreta la stringa e ritorna la data corrsipondente.
   * Equivalente a parseDate(s, defval, FLAG_ROUND_DEAULT).
   * @param s stringa da interpretare
   * @param defVal valore di default se il parsing non è possibile
   * @return il valore interpretato oppure defVal
   */
  public Date parseDate(String s, Date defVal);

  /**
   * Interpreta la stringa e ritorna la data corrsipondente.
   * @param s stringa da interpretare
   * @param defVal valore di default se il parsing non è possibile
   * @param flags una delle costanti FLAG_ROUND
   * @return il valore interpretato oppure defVal
   */
  public Date parseDate(String s, Date defVal, int flags);

  /**
   * Interpreta la string e ritorna l'ora corrispondente.
   * @param s stringa da interpretare
   * @param defVal valore di default se il parsing non è possibile
   * @return il valore interpretato oppure defVal
   */
  public Date parseTime(String s, Date defVal);

  /**
   * Prepara un apposito messaggio di errore.
   * @param errorType
   * @param fldName
   * @param min
   * @param max
   * @param dmin
   * @param dmax
   * @param invalidValue
   * @return messaggio d'errore
   */
  public String getErrorMessage(int errorType, String fldName,
     int min, int max, double dmin, double dmax, String invalidValue);
}
