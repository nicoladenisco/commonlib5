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

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import org.commonlib5.utils.StringOper;

/**
 * Semplice validatore dei campi di una dialog.
 *
 * @author Nicola De Nisco
 */
public class SimpleValidator
{
  public static final int ERROR_TEXT = 1;
  public static final int ERROR_DATE = 2;
  public static final int ERROR_DATETIME = 3;
  public static final int ERROR_INT = 4;
  public static final int ERROR_INT_RANGE = 5;
  public static final int ERROR_DOUBLE = 6;
  public static final int ERROR_DOUBLE_RANGE = 7;
  public static final int ERROR_FILE_NOT_EXIST = 8;
  public static final int ERROR_DIRECTORY_NOT_EXIST = 9;
  public static final int ERROR_REGEXP = 10;
  //
  protected ValidatorParserInterface parser = null;
  public static final String LABELED_BY_PROPERTY = "labeledBy";

  public SimpleValidator()
  {
    this(new ItalianParser());
  }

  public SimpleValidator(ValidatorParserInterface parser)
  {
    this.parser = parser;
  }

  public ValidatorParserInterface getParser()
  {
    return parser;
  }

  public void setParser(ValidatorParserInterface parser)
  {
    this.parser = parser;
  }

  public String verifyTextField(JTextComponent cmp)
     throws InvalidValueException
  {
    String rv;

    if((rv = StringOper.okStrNull(cmp.getText())) == null)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_TEXT, nome, 0, 0, 0, 0, rv),
         ERROR_TEXT, nome);
    }

    // reinserisce il valore senza spazi
    cmp.setText(rv);

    return rv;
  }

  public Date verifyDateField(JTextComponent cmp)
     throws InvalidValueException
  {
    Date parsed;
    String val = verifyTextField(cmp);
    if((parsed = parser.parseDate(val, null)) == null)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DATE, nome, 0, 0, 0, 0, val),
         ERROR_DATE, nome);
    }

    // la data è valida riformattiamola correttamente e inseriamola nel componente
    cmp.setText(parser.fmtDate(parsed));

    return parsed;
  }

  public Date verifyDateTimeField(JTextComponent cmp)
     throws InvalidValueException
  {
    Date parsed;
    String val = verifyTextField(cmp);
    if((parsed = parser.parseDate(val, null)) == null)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DATETIME, nome, 0, 0, 0, 0, val),
         ERROR_DATETIME, nome);
    }

    // la data è valida riformattiamola correttamente e inseriamola nel componente
    cmp.setText(parser.fmtDateTime(parsed));

    return parsed;
  }

  public int verifyIntField(JTextComponent cmp)
     throws InvalidValueException
  {
    int parsed;
    String val = verifyTextField(cmp);
    try
    {
      parsed = Integer.parseInt(val);
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_INT, nome, 0, 0, 0, 0, val),
         ERROR_INT, nome);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(Integer.toString(parsed));

    return parsed;
  }

  public int verifyIntField(JTextComponent cmp, int min, int max)
     throws InvalidValueException
  {
    int parsed;
    String val = verifyTextField(cmp);

    try
    {
      parsed = Integer.parseInt(val);
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_INT, nome, 0, 0, 0, 0, val), ERROR_INT, nome);
    }

    if(parsed < min || parsed > max)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_INT_RANGE, nome, min, max, 0, 0, val),
         ERROR_INT_RANGE, nome, min, max);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(Integer.toString(parsed));

    return parsed;
  }

  public double verifyDoubleField(JTextComponent cmp)
     throws InvalidValueException
  {
    double parsed;
    String val = verifyTextField(cmp);

    try
    {
      parsed = Double.parseDouble(val);
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DOUBLE, nome, 0, 0, 0, 0, val),
         ERROR_DOUBLE, nome);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(Double.toString(parsed));

    return parsed;
  }

  public double verifyDoubleField(JTextComponent cmp, double min, double max)
     throws InvalidValueException
  {
    double parsed;
    String val = verifyTextField(cmp);

    try
    {
      parsed = Double.parseDouble(val);
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DOUBLE, nome, 0, 0, 0, 0, val),
         ERROR_DOUBLE, nome);
    }

    if(parsed < min || parsed > max)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DOUBLE_RANGE, nome, 0, 0, min, max, val),
         ERROR_DOUBLE_RANGE, nome, min, max);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(Double.toString(parsed));

    return parsed;
  }

  public double verifyDoubleField(JTextComponent cmp, NumberFormat format)
     throws InvalidValueException
  {
    double parsed;
    String val = verifyTextField(cmp);

    try
    {
      parsed = format.parse(val).doubleValue();
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DOUBLE, nome, 0, 0, 0, 0, val),
         ERROR_DOUBLE, nome);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(format.format(parsed));

    return parsed;
  }

  public double verifyDoubleField(JTextComponent cmp, NumberFormat format, double min, double max)
     throws InvalidValueException
  {
    double parsed;
    String val = verifyTextField(cmp);

    try
    {
      parsed = format.parse(val).doubleValue();
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DOUBLE, nome, 0, 0, 0, 0, val),
         ERROR_DOUBLE, nome);
    }

    if(parsed < min || parsed > max)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DOUBLE_RANGE, nome, 0, 0, min, max, val),
         ERROR_DOUBLE_RANGE, nome, min, max);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(format.format(parsed));

    return parsed;
  }

  public File verifyFilenameField(JTextComponent cmp, boolean mustExist, boolean mustExistDir)
     throws InvalidValueException
  {
    String val = verifyTextField(cmp);

    File f = new File(val);
    if(mustExist && !f.exists())
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_FILE_NOT_EXIST, nome, 0, 0, 0, 0, val),
         ERROR_FILE_NOT_EXIST, nome);
    }

    if(mustExistDir && !f.isDirectory())
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_DIRECTORY_NOT_EXIST, nome, 0, 0, 0, 0, val),
         ERROR_DIRECTORY_NOT_EXIST, nome);
    }

    // reinserisce il valore correttamente formattato
    cmp.setText(f.getAbsolutePath());

    return f;
  }

  public String verifyRegExpFieldNotNull(JTextComponent cmp)
     throws InvalidValueException
  {
    String val = verifyTextField(cmp);

    try
    {
      if(val != null)
      {
        Pattern.compile(val);

        // reinserisce il valore correttamente formattato
        cmp.setText(val);
      }
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_REGEXP, nome, 0, 0, 0, 0, val),
         ERROR_REGEXP, nome);
    }

    return val;
  }

  public String verifyRegExpField(JTextComponent cmp)
     throws InvalidValueException
  {
    String val = null;

    try
    {
      if((val = StringOper.okStrNull(cmp.getText())) != null)
      {
        Pattern.compile(val);

        // reinserisce il valore correttamente formattato
        cmp.setText(val);
      }
    }
    catch(Exception e)
    {
      JLabel label = (JLabel) cmp.getClientProperty(LABELED_BY_PROPERTY);
      String nome = label == null ? "" : label.getText() + " ";

      cmp.requestFocus();
      throw new InvalidValueException(parser.getErrorMessage(ERROR_REGEXP, nome, 0, 0, 0, 0, val),
         ERROR_REGEXP, nome);
    }

    return val;
  }
}
