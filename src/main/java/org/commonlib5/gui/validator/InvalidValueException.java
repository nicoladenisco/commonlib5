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

/**
 * Eccezione sollevata da SimpleValidator quando un valore non è corretto.
 *
 * @author Nicola De Nisco
 */
public class InvalidValueException extends Exception
{
  protected int errorType = 0;
  protected String fldName = null;
  protected double min, max;

  public InvalidValueException(String message, int errorType, String fldName)
  {
    super(message);
    this.errorType = errorType;
    this.fldName = fldName;
  }

  public InvalidValueException(String message, int errorType, String fldName, int min, int max)
  {
    super(message);
    this.errorType = errorType;
    this.fldName = fldName;
    this.min = min;
    this.max = max;
  }

  public InvalidValueException(String message, int errorType, String fldName, double min, double max)
  {
    super(message);
    this.errorType = errorType;
    this.fldName = fldName;
    this.min = min;
    this.max = max;
  }
}
