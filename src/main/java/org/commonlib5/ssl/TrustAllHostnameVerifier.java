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

import java.util.HashMap;
import java.util.Iterator;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Verificatore di host name per HttpsURLConnection con passaggio totale.
 * @deprecated usare TrustAllSecurityProvider.registerHttpsURL
 * @author Nicola De Nisco
 */
public class TrustAllHostnameVerifier
{
  private static HostnameVerifier oldVerifier = null;
  private static HashMap<String, Boolean> verifyResult = new HashMap<String, Boolean>();

  // Create all-trusting host name verifier
  public static class AllTrustHostnameVerifier implements HostnameVerifier
  {
    @Override
    public boolean verify(String hostName, SSLSession ssls)
    {
      verifyResult.put(hostName, oldVerifier.verify(hostName, ssls));
      return true;
    }
  };

  public static void register()
  {
    if(oldVerifier != null)
      return;

    oldVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(new AllTrustHostnameVerifier());
  }

  public static void remove()
  {
    if(oldVerifier == null)
      return;

    HttpsURLConnection.setDefaultHostnameVerifier(oldVerifier);
    oldVerifier = null;
  }

  public static Boolean getResult(String hostName)
  {
    return verifyResult.get(hostName);
  }

  public static Iterator<String> hostNamesIterator()
  {
    return verifyResult.keySet().iterator();
  }

  public static void clear()
  {
    verifyResult.clear();
  }
}
