/*
 * Copyright (C) 2020 Nicola De Nisco
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

import java.io.*;
import java.security.KeyStore;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;

/**
 * Caricatore del keystore standard java.
 *
 * @author Nicola De Nisco
 */
public class SystemKeystore
{
  public static final String SYSTEM_KEYSTORE_PASSWORD = "changeit";

  protected KeyStore keystore = null;

  public void loadKeystore()
     throws Exception
  {
    final char sep = File.separatorChar;
    File dir = new File(System.getProperty("java.home") + sep + "lib" + sep + "security");
    File file = new File(dir, "cacerts");
    try (InputStream localCertIn = new FileInputStream(file))
    {
      keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      keystore.load(localCertIn, SYSTEM_KEYSTORE_PASSWORD.toCharArray());
    }
  }

  public void addCertificate(InputStream certIn, String alias)
     throws Exception
  {
    try (BufferedInputStream bis = new BufferedInputStream(certIn))
    {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");

      for(int i = 0; bis.available() > 0; i++)
      {
        Certificate cert = cf.generateCertificate(bis);

        if(i == 0)
          keystore.setCertificateEntry(alias, cert);
        else
          keystore.setCertificateEntry(alias + i, cert);
      }
    }
  }

  public void addFrom(CertStore store)
     throws Exception
  {
    addFrom(store, "local");
  }

  public void addFrom(CertStore store, String baseAlias)
     throws Exception
  {
    Collection<? extends Certificate> certs = store.getCertificates(null);

    int count = 0;
    for(Certificate c : certs)
    {
      keystore.setCertificateEntry(baseAlias + count, c);
      count++;
    }
  }

  public void save(File file, String passphrase)
     throws Exception
  {
    try (OutputStream out = new FileOutputStream(file))
    {
      keystore.store(out, passphrase.toCharArray());
    }
  }

  public KeyStore getKeystore()
  {
    return keystore;
  }
}
