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
package org.commonlib5.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.*;

/**
 * DummySSLSocketFactory.
 *
 * @author Nicola De Nisco
 */
public class DummySSLSocketFactory extends SSLSocketFactory
{
  private SSLSocketFactory factory;

  public DummySSLSocketFactory()
  {
    this(new DummyTrustManager());
  }

  public DummySSLSocketFactory(X509TrustManager tm)
  {
    try
    {
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(null, new TrustManager[]
      {
        tm
      }, null);
      factory = (SSLSocketFactory) sslcontext.getSocketFactory();
    }
    catch(Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static SSLSocketFactory getDefault()
  {
    return new DummySSLSocketFactory();
  }

  @Override
  public Socket createSocket()
     throws IOException
  {
    return factory.createSocket();
  }

  @Override
  public Socket createSocket(Socket socket, String s, int i, boolean flag)
     throws IOException
  {
    return factory.createSocket(socket, s, i, flag);
  }

  @Override
  public Socket createSocket(InetAddress inaddr, int i,
     InetAddress inaddr1, int j)
     throws IOException
  {
    return factory.createSocket(inaddr, i, inaddr1, j);
  }

  @Override
  public Socket createSocket(InetAddress inaddr, int i)
     throws IOException
  {
    return factory.createSocket(inaddr, i);
  }

  @Override
  public Socket createSocket(String s, int i, InetAddress inaddr, int j)
     throws IOException
  {
    return factory.createSocket(s, i, inaddr, j);
  }

  @Override
  public Socket createSocket(String s, int i)
     throws IOException
  {
    return factory.createSocket(s, i);
  }

  @Override
  public String[] getDefaultCipherSuites()
  {
    return factory.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites()
  {
    return factory.getSupportedCipherSuites();
  }
}
