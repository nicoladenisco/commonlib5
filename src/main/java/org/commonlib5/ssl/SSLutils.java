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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.commonlib5.gui.ErrorDialog;

/**
 * Utility per la connessione SSL.
 *
 * @author Nicola De Nisco
 */
public class SSLutils
{
  /**
   * Ritorna il keystore di sistema per la JVM in esecuzione.
   * @return il keystore
   * @throws java.lang.Exception
   */
  public static KeyStore getSystemKeystore()
     throws Exception
  {
    File file = new File("jssecacerts");
    if(file.isFile() == false)
    {
      char SEP = File.separatorChar;
      File dir = new File(System.getProperty("java.home") + SEP
         + "lib" + SEP + "security");
      file = new File(dir, "jssecacerts");
      if(file.isFile() == false)
      {
        file = new File(dir, "cacerts");
      }
    }

    if(!file.exists())
      throw new Exception("Can't find keystore file.");

    KeyStore ks;
    try (InputStream in = new FileInputStream(file))
    {
      ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(in, null);
    }

    return ks;
  }

  public enum testResults
  {
    ok, userAbort, ignoreCerts, dontIgnoreCerts, errors
  };

  /**
   * Verifica il certificato emesso dal server controllandone la validità
   * e se è compatibile con le certification autority definite nel keystore.
   * @param host host con cui aprire una connessione SSL
   * @param port porta della connessione
   * @param uks keystore con le certification autority riconosciute (null=system keystore)
   * @return una delle costanti testResults
   * ok=il certificato è corretto e autorizzato
   * userAbort=l'utente ha richiesto l'abort della connessione
   * ignoreCerts=l'utente ha richiesto di ignorare la non validità del certifiato
   * dontIgnoreCerts=l'utente ha richiesto di non ignorare il certificato
   * errors=si sono verificati errori durante il collegamento
   */
  public static testResults testCertificato(String host, int port, KeyStore uks)
  {
    SavingTrustManager tm = null;

    try
    {
      KeyStore sysks = uks == null ? SSLutils.getSystemKeystore() : uks;
      SSLContext context = SSLContext.getInstance("TLS");

      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(sysks);

      X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
      tm = new SavingTrustManager(defaultTrustManager);
      context.init(null, new TrustManager[]
      {
        tm
      }, null);

      SSLSocketFactory factory = context.getSocketFactory();

      System.out.println("Opening connection to " + host + ":" + port + "...");
      try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port))
      {
        socket.setSoTimeout(10000);
        System.out.println("Starting SSL handshake...");
        socket.startHandshake();
      }
      System.out.println();
      System.out.println("No errors, certificate is already trusted");
      return testResults.ok;
    }
    catch(SSLHandshakeException eh)
    {
      String srvCerts = "Server sent " + tm.chain.length + " certificate(s):\n";
      for(int i = 0; i < tm.chain.length; i++)
      {
        X509Certificate cert = tm.chain[i];
        srvCerts += (" " + (i + 1) + " Subject " + cert.getSubjectDN());
        srvCerts += ("   Issuer  " + cert.getIssuerDN());
        srvCerts += "\n";
      }

      int rv = JOptionPane.showConfirmDialog(null,
         "Il server indicato non ha fornito un certificato valido.\n"
         + "Vuoi comunque utilizzare questo server (PERICOLO DI SICUREZZA)?\n"
         + srvCerts,
         "Conferma", JOptionPane.YES_NO_CANCEL_OPTION);

      if(rv == JOptionPane.CANCEL_OPTION)
        return testResults.userAbort;

      if(rv == JOptionPane.YES_OPTION)
        return testResults.ignoreCerts;

      if(rv == JOptionPane.NO_OPTION)
        return testResults.dontIgnoreCerts;

      return testResults.ok;
    }
    catch(Exception ex)
    {
      ErrorDialog.showError("Errore imprevisto durante la connessione a " + host + ":" + port, ex);
    }

    return testResults.errors;
  }

  private static class SavingTrustManager implements X509TrustManager
  {
    private final X509TrustManager tm;
    private X509Certificate[] chain;

    SavingTrustManager(X509TrustManager tm)
    {
      this.tm = tm;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
      return tm.getAcceptedIssuers();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
       throws CertificateException
    {
      this.chain = chain;
      tm.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
       throws CertificateException
    {
      this.chain = chain;
      tm.checkServerTrusted(chain, authType);
    }
  }
}
