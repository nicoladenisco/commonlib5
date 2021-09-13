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
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;
import org.commonlib5.utils.StringOper;
import sun.security.jca.Providers;

/**
 * Questo provider consente di creare socket SSL che accettano i
 * certificati dei server senza implementare il controllo della validità
 * degli stessi, come invece avviene nell'implementazione di default di java.
 * Questo consente di attivare connessioni SSL anche con server che hanno
 * dei certificati autofirmati o scaduti.
 * La rimozione del trust manager non è strettamente necessaria. Serve
 * solo se si vuole ritornare all'implementazione di default del trust manager.
 * Prima di aprire il sockest SSL inserire le seguenti linee di codice:
 * <pre>
 *
 * // Installo il trust manager
 * TrustAllSecurityProvider.register();
 *
 * ...
 * // apro un socket SSL verso il server
 * // ignorando il certificato del server
 * ...
 *
 * // Rimuovo il trust manager
 * TrustAllSecurityProvider.remove();
 * </pre>
 * @author Nicola De Nisco
 */
public class TrustAllSecurityProvider extends Provider
{
  /** The name of our algorithm * */
  public static final String TRUST_PROVIDER_ALG = "TrustAllCertificates";
  /** Need to refer to ourselves somehow to know if we're already registered * */
  public static final String TRUST_PROVIDER_ID = "TrustAllSecurityProvider";
  /** Ultima catena di certificati client */
  protected static X509Certificate[] lastClientChain = null;
  /** Ultima catena di certificati server */
  protected static X509Certificate[] lastServerChain = null;
  /** Logger */
  private static final Logger logger = Logger.getLogger(TrustAllSecurityProvider.class.getName());

  public TrustAllSecurityProvider()
  {
    super(TRUST_PROVIDER_ID, 1.0, "Trust all certificates");
    put("TrustManagerFactory." + TRUST_PROVIDER_ALG, MyTrustManagerFactory.class.getName());
  }

  /**
   * Implementazione privata dalla factory dei trust manager.
   * Ritorna una unica implementazione ovvero MyX509TrustManager.
   */
  public static class MyTrustManagerFactory
     extends TrustManagerFactorySpi
  {
    public MyTrustManagerFactory()
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
        new MyX509TrustManager()
      };
    }
  }

  /**
   * Implementazione privata del trust manager.
   * Accetta qualsiasi client e qualsiasi server senza controllare
   * l'integrità dei certificati presentati.
   */
  public static class MyX509TrustManager
     implements X509TrustManager
  {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
    {
      lastClientChain = chain;
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
    {
      lastServerChain = chain;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
      return null;
    }
  }

  /**
   * Ritorna l'ultima catena di certificati con cui si è autenticato un client.
   *
   * @return the value of lastClientChain
   */
  public static X509Certificate[] getLastClientChain()
  {
    return lastClientChain;
  }

  /**
   * Ritorna l'ultima catena di certificati con cui si è autenticato un server.
   *
   * @return the value of lastServerChain
   */
  public static X509Certificate[] getLastServerChain()
  {
    return lastServerChain;
  }

  private static String prevAlgo = null;

  /**
   * Installa provider.
   * L'installazione avviene solo la prima volta.
   */
  public static void register()
  {
    Provider registered = Security.getProvider(TRUST_PROVIDER_ID);
    if(registered == null)
    {
      String prevAlgoTest = Security.getProperty("ssl.TrustManagerFactory.algorithm");

      // constrolla che non sia già stato registrato ...
      if(!StringOper.isEqu(prevAlgoTest, TRUST_PROVIDER_ALG))
      {
        // ... quindi registra come provider di sicurezza preferenziale il nostro algoritmo
        Security.insertProviderAt(new TrustAllSecurityProvider(), 1);
        Security.setProperty("ssl.TrustManagerFactory.algorithm", TRUST_PROVIDER_ALG);
        prevAlgo = prevAlgoTest;
      }

      List<Provider> lsProv = Providers.getFullProviderList().providers();
      for(int i = 0; i < lsProv.size(); i++)
      {
        Provider p = lsProv.get(i);
        logger.info(String.format("Provider %d: %s %f", i, p.getName(), p.getVersion()));
      }
    }
  }

  /**
   * Rimuove il provider.
   * La rimozione avviene solo se una volta e solo se il provider è stato registrato.
   */
  public static void remove()
  {
    Provider registered = Security.getProvider(TRUST_PROVIDER_ID);
    if(registered != null)
    {
      Security.removeProvider(TRUST_PROVIDER_ID);
      Security.setProperty("ssl.TrustManagerFactory.algorithm", prevAlgo);
    }
  }

  /**
   * Registra socket factory e hostname verifier per connessioni aperte da HttpsURLConnection.
   */
  public static void registerHttpsURL()
  {
    try
    {
      TrustManager[] trustAllCerts = new TrustManager[]
      {
        new MyX509TrustManager()
      };

      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier((String hostName, SSLSession ssls) -> true);
    }
    catch(Exception ex)
    {
      logger.log(Level.SEVERE, null, ex);
    }
  }
}
