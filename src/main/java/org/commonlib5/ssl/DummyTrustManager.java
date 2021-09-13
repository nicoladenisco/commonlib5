/*
 * DummyTrustManager.java
 *
 * Created on 19-giu-2012, 17.54.26
 *
 *  Copyright (C) 2011 Nicola De Nisco
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.commonlib5.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonlib5.utils.StringOper;

/**
 * DummyTrustManager - NOT SECURE
 * @author Nicola De Nisco
 */
public class DummyTrustManager implements X509TrustManager
{
  private static Log log = LogFactory.getLog(DummyTrustManager.class);

  @Override
  public void checkClientTrusted(X509Certificate[] cert, String authType)
  {
    // everything is trusted
    if(log.isDebugEnabled())
    {
      log.debug("checkClientTrusted(" + authType + "):\n "
         + StringOper.join2(cert, (c) -> c.toString(), "----------------------\n", null));
    }
  }

  @Override
  public void checkServerTrusted(X509Certificate[] cert, String authType)
  {
    // everything is trusted
    if(log.isDebugEnabled())
    {
      log.debug("checkServerTrusted(" + authType + "):\n "
         + StringOper.join2(cert, (c) -> c.toString(), "----------------------\n", null));
    }
  }

  @Override
  public X509Certificate[] getAcceptedIssuers()
  {
    return new X509Certificate[0];
  }
}
