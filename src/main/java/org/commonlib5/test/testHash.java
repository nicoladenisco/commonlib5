/*
 * Copyright (C) 2018 Nicola De Nisco
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
package org.commonlib5.test;

import org.commonlib5.utils.CommonFileUtils;

/**
 * Prova funzioni hash.
 *
 * @author Nicola De Nisco
 */
public class testHash
{
  public static final String algo = "SHA1";

  public static void main(String[] args)
  {
    for(String arg : args)
    {
      try
      {
        String hash = CommonFileUtils.calcolaHashStringa(arg, "SHA1");
        System.out.println("arg=" + arg + " hash=" + hash);
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
