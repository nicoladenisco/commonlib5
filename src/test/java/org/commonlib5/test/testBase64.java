/*
 * Copyright (C) 2021 Nicola De Nisco
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

import java.io.UnsupportedEncodingException;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.HexString;

/**
 * Prova funzioni base64.
 *
 * @author Nicola De Nisco
 */
public class testBase64
{
  public static void main(String[] args)
  {
    for(String arg : args)
    {
      try
      {
        byte[] bi = arg.getBytes("UTF-16");
        String b64 = CommonFileUtils.binary_2_Base64(bi);
        System.out.println("arg='" + arg + "' b64='" + b64 + "'");

        String hex = convertUTF16hex(arg);
        System.out.println("arg='" + arg + "' hex='" + hex + "'");

        String par = convertHexUTF16(hex);
        System.out.println("arg='" + arg + "' par='" + par + "'");
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  public static String removeBUM(String s)
  {
    if(s.startsWith("FEFF00"))
      s = s.substring(6);

    while((s.length() % 4) != 0)
      s += "0";

    return s;
  }

  public static String convertUTF16hex(String arg)
     throws UnsupportedEncodingException
  {
    byte[] bi = arg.getBytes("UTF-16");
    String s = HexString.bufferToHex(bi);

    if(s.startsWith("FEFF00"))
      s = s.substring(6);

    while((s.length() % 4) != 0)
      s += "0";

    return s;
  }

  private static String convertHexUTF16(String hex)
     throws UnsupportedEncodingException
  {
    if(hex.endsWith("00"))
      hex = hex.substring(0, hex.length() - 2);

    byte[] bi = HexString.hexToBuffer("FEFF00" + hex);
    return new String(bi, "UTF-16");
  }
}
