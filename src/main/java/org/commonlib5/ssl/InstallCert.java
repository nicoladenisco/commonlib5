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

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.*;

import java.security.*;
import java.security.cert.*;

import javax.net.ssl.*;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.LongOptExt;
import org.commonlib5.utils.StringOper;

/**
 * Questa classe estrae il certificato da un server SSL remoto e lo salva
 * in un keystore locale. Può quindi creare un keystore con il corretto
 * certificato semplicemente connettendosi al server remoto che lo emette.
 * Molto utile per creare un keystore locale quando ci si collega ad un
 * server che possiede un certificato autofirmato e quindi non riconosciuto
 * come valido in una normale connessione SSL.
 *
 * @author Nicola De Nisco
 */
public class InstallCert
{
  public static void main(String[] args)
     throws Exception
  {
    String host = "localhost";
    int port = 443;
    char[] passphrase = "changeit".toCharArray();
    boolean overwrite = false;
    String pathKeystore = null, alias = null;

    File fileKs = new File("jssecacerts");
    if(fileKs.isFile() == false)
    {
      File dir = new File(System.getProperty("java.home") + File.separatorChar
         + "lib" + File.separatorChar + "security");
      fileKs = new File(dir, "jssecacerts");
      if(fileKs.isFile() == false)
      {
        fileKs = new File(dir, "cacerts");
      }
    }

    LongOptExt longopts[] = new LongOptExt[]
    {
      new LongOptExt("help", LongOpt.NO_ARGUMENT, null, 'h', "help usage"),
      new LongOptExt("port", LongOpt.REQUIRED_ARGUMENT, null, 'p', "port to connect"),
      new LongOptExt("keystore", LongOpt.REQUIRED_ARGUMENT, null, 'k', "explicit keystore"),
      new LongOptExt("passphrase", LongOpt.REQUIRED_ARGUMENT, null, 'r', "passphrase for keystore"),
      new LongOptExt("alias", LongOpt.REQUIRED_ARGUMENT, null, 'a', "alias for new certificate"),
      new LongOptExt("overwrite", LongOpt.NO_ARGUMENT, null, 'w', "write result on existent keystore"),
    };

    String optString = LongOptExt.getOptstring(longopts);
    Getopt g = new Getopt("InstallCert", args, optString, longopts);
    g.setOpterr(false); // We'll do our own error handling

    int c;
    while((c = g.getopt()) != -1)
    {
      switch(c)
      {
        case 'h':
          help(longopts);
          return;

        case 'p':
          port = StringOper.parse(g.getOptarg(), port);
          break;

        case 'k':
          pathKeystore = g.getOptarg();
          fileKs = new File(pathKeystore);
          break;

        case 'r':
          passphrase = g.getOptarg().toCharArray();
          break;

        case 'a':
          alias = g.getOptarg();
          break;

        case 'w':
          overwrite = true;
          break;
      }
    }

    String tmp;
    if(args.length == g.getOptind() || (tmp = StringOper.okStrNull(args[g.getOptind()])) == null)
    {
      help(longopts);
      return;
    }

    String[] ss = tmp.split(":");
    host = ss[0];
    if(ss.length > 1)
      port = StringOper.parse(ss[1], port);

    if(!fileKs.exists())
    {
      System.out.println("Can't find keystore file.");
      System.exit(-1);
    }

    System.out.println("Loading KeyStore " + fileKs + "...");

    KeyStore ks;
    try (InputStream in = new FileInputStream(fileKs))
    {
      ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(in, passphrase);
    }

    SSLContext context = SSLContext.getInstance("TLS");
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(ks);
    X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
    SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
    context.init(null, new TrustManager[] {tm}, null);
    SSLSocketFactory factory = context.getSocketFactory();

    System.out.println("Opening connection to " + host + ":" + port + "...");
    try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port))
    {
      socket.setSoTimeout(10000);
      System.out.println("Starting SSL handshake...");
      socket.startHandshake();
      System.out.println();
      System.out.println("No errors, certificate is already trusted");
    }
    catch(SSLException e)
    {
      System.out.println();
      e.printStackTrace(System.out);
    }

    X509Certificate[] chain = tm.chain;
    if(chain == null)
    {
      System.out.println("Could not obtain server certificate chain");
      return;
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.println();
    System.out.println("Server sent " + chain.length + " certificate(s):");
    System.out.println();
    MessageDigest sha1 = MessageDigest.getInstance("SHA1");
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    for(int i = 0; i < chain.length; i++)
    {
      X509Certificate cert = chain[i];
      System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
      System.out.println("   Issuer  " + cert.getIssuerDN());
      sha1.update(cert.getEncoded());
      System.out.println("   sha1    " + toHexString(sha1.digest()));
      md5.update(cert.getEncoded());
      System.out.println("   md5     " + toHexString(md5.digest()));
      System.out.println();
    }

    System.out.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
    String line = reader.readLine().trim();
    int k;
    try
    {
      k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
    }
    catch(NumberFormatException e)
    {
      System.out.println("KeyStore not changed");
      return;
    }

    X509Certificate cert = chain[k];
    System.out.println();
    System.out.println(cert);
    System.out.println();

    if(alias == null)
      alias = host + "-" + (k + 1);
    ks.setCertificateEntry(alias, cert);

    File outks = new File("jssecacerts");
    try (OutputStream out = new FileOutputStream(outks))
    {
      ks.store(out, passphrase);
    }

    if(overwrite)
    {
      CommonFileUtils.moveFile(outks, fileKs);
      System.out.println("Added certificate to keystore '" + fileKs.getCanonicalPath() + "' using alias '" + alias + "'");
    }
    else
    {
      System.out.println("Added certificate to keystore 'jssecacerts' using alias '" + alias + "'");
      System.out.println("Copy this file in '" + fileKs.getCanonicalPath() + "' to save permanently.");
    }
  }

  private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

  private static String toHexString(byte[] bytes)
  {
    StringBuilder sb = new StringBuilder(bytes.length * 3);
    for(int b : bytes)
    {
      b &= 0xff;
      sb.append(HEXDIGITS[b >> 4]);
      sb.append(HEXDIGITS[b & 15]);
      sb.append(' ');
    }
    return sb.toString();
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

  public static void help(LongOptExt longopts[])
  {
    System.out.printf(
       "InstallCert\n"
       + "Import certificate in keystore from live SSL connection.\n"
       + "Usage: InstallCert.sh [options] <host>[:port]\n");

    for(LongOptExt l : longopts)
    {
      System.out.println(l.getHelpMsg());
    }

    System.out.println();
    System.exit(0);
  }
}
