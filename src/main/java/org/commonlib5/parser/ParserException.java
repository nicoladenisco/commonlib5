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
package org.commonlib5.parser;

/**
 * Errore sollevato da Calcolatore.
 *
 * @author Nicola De Nisco
 */
public class ParserException extends Exception
{
  private String missing;

  public ParserException()
  {
    super();
  }

  public ParserException(String message)
  {
    super(message);
  }

  public ParserException(String message, String missing)
  {
    super(message);
    this.missing = missing;
  }

  public String getMissing()
  {
    return missing;
  }

  public void setMissing(String missing)
  {
    this.missing = missing;
  }
}
