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
package org.commonlib5.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;
import javax.net.ssl.*;

/**
 * Costruttore di socket con chiavi specificate.
 *
 * @author Nicola De Nisco
 */
public class SSLSocketBuilder
{
  protected SSLSocketFactory clientFactory = null;
  protected SSLServerSocketFactory serverFactory = null;

  /**
   * Costruttore vuoto: necessario usare il metodo initFactory().
   */
  public SSLSocketBuilder()
  {
  }

  /**
   * Costruisce una factory per socket utilizzando i files indicati.
   * @param info informazioni per l'apertura del socket
   * @throws KeyStoreException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws CertificateException
   * @throws UnrecoverableKeyException
   * @throws KeyManagementException
   */
  public SSLSocketBuilder(SSLSocketInfo info)
     throws KeyStoreException, FileNotFoundException, IOException,
     NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException
  {
    initFactory(info);
  }

  /**
   * Costruisce una factory per socket utilizzando i files indicati.
   * @param info informazioni per l'apertura del socket
   * @throws KeyStoreException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws CertificateException
   * @throws UnrecoverableKeyException
   * @throws KeyManagementException
   */
  public void initFactory(SSLSocketInfo info)
     throws KeyStoreException, FileNotFoundException, IOException,
     NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException
  {
    // First initialize the key and trust material
    KeyStore ksKeys = KeyStore.getInstance(info.keyStoreType);
    try (FileInputStream is = new FileInputStream(info.keyStore))
    {
      ksKeys.load(is, info.keyStorePassword.toCharArray());
    }
    KeyStore ksTrust = KeyStore.getInstance(info.trustStoreType);
    try (FileInputStream is = new FileInputStream(info.trustStore))
    {
      ksTrust.load(is, info.trustStorePassword.toCharArray());
    }

    // KeyManagers decide which key material to use
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ksKeys, info.keyPassword.toCharArray());

    // TrustManagers decide whether to allow connections
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(ksTrust);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

    // factory con i parametri passati
    clientFactory = sslContext.getSocketFactory();
    serverFactory = sslContext.getServerSocketFactory();
  }

  public SSLSocketFactory getClientFactory()
  {
    return clientFactory;
  }

  public SSLServerSocketFactory getServerFactory()
  {
    return serverFactory;
  }

  public SSLServerSocket createServerSocket(int port)
     throws IOException
  {
    return (SSLServerSocket) serverFactory.createServerSocket(port);
  }

  public SSLSocket createClientSocket(InetAddress address, int port)
     throws IOException
  {
    return (SSLSocket) clientFactory.createSocket(address, port);
  }

  public SSLSocket createClientSocket(String ipOrAddress, int port)
     throws IOException
  {
    return createClientSocket(InetAddress.getByName(ipOrAddress), port);
  }
}
