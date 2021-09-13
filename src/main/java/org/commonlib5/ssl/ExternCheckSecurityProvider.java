/*
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

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;
import org.commonlib5.utils.StringOper;

/**
 * Questo provider consente di installare più semplicemente un controllore
 * dei certificati di connessioni SSL per decidere se accettare o meno i
 * certificati forniti dal server o dal client.
 * ATTENZIONE: si può usare un solo ExternCheckSecurityProvider per volta;
 * prima che l'implementazione di X509TrustManager venga distrutta occorre
 * necessariamente rimuovere il provider.
 * Prima di aprire il sockest SSL inserire le seguenti linee di codice:
 * <pre>
 *
 * // Installo la mia implementazione di trust manager
 * X509TrustManager trustManagerImplementation = ...
 * ExternCheckSecurityProvider.register(trustManagerImplementation);
 *
 * ...
 * // apro un socket SSL con il server
 * // trustManagerImplementation controllerà la validità
 * // del certificato del server e deciderà se consentire
 * // o meno la connessione
 * ...
 *
 * // dopo aver chiuso il socket SSL rimuovo il provider
 * ExternCheckSecurityProvider.remove();
 * </pre>
 * @author Nicola De Nisco
 */
public class ExternCheckSecurityProvider extends Provider
{
  private static X509TrustManager tm = null;

  /** The name of our algorithm * */
  private static final String TRUST_PROVIDER_ALG = "CustomCheckCertificates";
  /** Need to refer to ourselves somehow to know if we're already registered * */
  private static final String TRUST_PROVIDER_ID = "ExternCheckSecurityProvider";

  public ExternCheckSecurityProvider(X509TrustManager tm)
  {
    super(TRUST_PROVIDER_ID, 1.0, "Trust all certificates");
    put("TrustManagerFactory." + TRUST_PROVIDER_ALG, EcTrustManagerFactory.class.getName());
    ExternCheckSecurityProvider.tm = tm;
  }

  /**
   * Implementazione privata dalla factory dei trust manager.
   * Ritorna l'oggetto passato nel costruttore.
   */
  protected static class EcTrustManagerFactory
     extends TrustManagerFactorySpi
  {
    public EcTrustManagerFactory()
    {
    }

    @Override
    protected void engineInit(KeyStore keystore)
    {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters mgrparams)
    {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers()
    {
      return new TrustManager[]
      {
        tm
      };
    }
  }

  private static String prevAlgo = null;

  public static void register(X509TrustManager tm)
  {
    Provider registered = Security.getProvider(TRUST_PROVIDER_ID);
    if(registered == null)
    {
      prevAlgo = Security.getProperty("ssl.TrustManagerFactory.algorithm");

      // constrolla che non sia già stato registrato ...
      if(!StringOper.isEqu(prevAlgo, TRUST_PROVIDER_ALG))
      {
        // ... quindi registra come provider di sicurezza il nostro algoritmo
        Security.insertProviderAt(new ExternCheckSecurityProvider(tm), 1);
        Security.setProperty("ssl.TrustManagerFactory.algorithm", TRUST_PROVIDER_ALG);
      }
    }
  }

  public static void remove()
  {
    Provider registered = Security.getProvider(TRUST_PROVIDER_ID);
    if(registered != null)
    {
      Security.removeProvider(TRUST_PROVIDER_ID);
      Security.setProperty("ssl.TrustManagerFactory.algorithm", prevAlgo);
    }
  }
}
