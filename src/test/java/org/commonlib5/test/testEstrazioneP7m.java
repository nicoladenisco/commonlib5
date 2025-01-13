/*
 * testEstrazioneP7m.java
 *
 * Created on 14-mar-2013, 14.55.02
 *
 *Copyright (C) 2013 nicola
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
package org.commonlib5.test;

import java.io.File;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.commonlib5.crypto.SignUtils;

/**
 * Programma di test per l'estrazione del contenuto dei p7m.
 *
 * @author Nicola De Nisco
 */
public class testEstrazioneP7m
{
  public static void main(String[] args)
     throws Exception
  {
    if(args.length < 2)
      System.out.println("Inserire file p7m e file di uscita.");

    if(Security.getProvider("BC") == null)
      Security.addProvider(new BouncyCastleProvider());

    SignUtils.extractDocument(
       new File(args[0]),
       new File(args[1]));
  }
}
