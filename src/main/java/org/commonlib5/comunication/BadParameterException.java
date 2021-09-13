/*
 * BadParameterException.java
 *
 * Created on 18-lug-2013, 14.49.42
 *
 *Copyright (C) 2013 Nicola De Nisco
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.commonlib5.comunication;

/**
 * Eccezione sollevata quando un parametro di setup della seriale non è valido.
 *
 * @author Nicola De Nisco
 */
public class BadParameterException extends Exception
{
  public BadParameterException(String string)
  {
    super(string);
  }

  public BadParameterException(String string, Throwable thrwbl)
  {
    super(string, thrwbl);
  }
}
