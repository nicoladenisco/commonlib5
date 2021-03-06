/*
 * Copyright (C) 2016 Nicola De Nisco
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
package org.commonlib5.crypto;

import java.io.File;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.commonlib5.utils.CommonFileUtils;

/**
 * Funzioni di utilità per leggere chiavi RSA.
 *
 * @author Nicola De Nisco
 */
public class KeyUtils
{
  static
  {
    if(Security.getProvider("BC") == null)
      Security.addProvider(new BouncyCastleProvider());
  }

  public static RSAPrivateKey getRSAPrivateKeyFromDER(File derFile)
     throws Exception
  {
    byte[] keyBytes = CommonFileUtils.readFile(derFile);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPrivateKey) kf.generatePrivate(spec);
  }

  public static RSAPublicKey getRSAPublicKeyFromDER(File derFile)
     throws Exception
  {
    byte[] keyBytes = CommonFileUtils.readFile(derFile);
    X509EncodedKeySpec spec1 = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf1 = KeyFactory.getInstance("RSA");
    return (RSAPublicKey) kf1.generatePublic(spec1);
  }

  public static RSAPrivateKey getRSAPrivateKeyFromPEM(File pemFile)
     throws Exception
  {
    KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
    PemFile pf = new PemFile(pemFile);
    byte[] content = pf.getPemObject().getContent();
    PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
    return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
  }

  public static RSAPublicKey getRSAPublicKeyFromPEM(File pemFile)
     throws Exception
  {
    KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
    PemFile pf = new PemFile(pemFile);
    byte[] content = pf.getPemObject().getContent();
    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
    return (RSAPublicKey) factory.generatePublic(pubKeySpec);
  }
}
