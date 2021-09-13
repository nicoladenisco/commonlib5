/*
 * SignUtils.java
 *
 * Created on 22 ottobre 2008, 11.06
 *
 * Copyright (C) 2011 Nicola De Nisco
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.commonlib5.utils.CommonFileUtils;

/**
 * Utility per la crittografia e firma digitale.
 *
 * @author Nicola De Nisco
 */
public class SignUtils
{
  public static final String X509_CERTIFICATE_TYPE = "X.509";
  public static final String DIGITAL_SIGNATURE_ALGORITHM_NAME = "SHA1withRSA";
  public static final String CERTIFICATION_CHAIN_ENCODING = "PkiPath";

  public static String createCerChain(Certificate[] certChain)
     throws Exception
  {
    String certificato = "";
    List certList = Arrays.asList(certChain);
    CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
    CertPath certPath = certFactory.generateCertPath(certList);
    List lista = certPath.getCertificates();
    for(int i = 0; i < lista.size(); i++)
    {
      certificato += SignUtils.createCERbase64((X509Certificate) lista.get(i));
    }
    return certificato;
  }

  public static String encodeX509CertChainToBase64(Certificate[] certChain)
     throws CertificateException
  {
    List certList = Arrays.asList(certChain);
    CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
    CertPath certPath = certFactory.generateCertPath(certList);
    byte[] certPathEncoded = certPath.getEncoded(CERTIFICATION_CHAIN_ENCODING);
    String base64encodedCertChain = new String(Base64.encodeBase64(certPathEncoded, true));
    return base64encodedCertChain;
  }

  /**
   * Ritorna il certificato sotto forma di file CER.
   * @return
   */
  public static String createCERbase64(X509Certificate xCert)
     throws Exception
  {
    return "-----BEGIN CERTIFICATE-----\r\n"
       + (new String(Base64.encodeBase64(xCert.getEncoded(), true)))
       + "-----END CERTIFICATE-----\r\n";
  }

  public static X509Certificate readX509CertFile(File file)
     throws Exception
  {
    try (FileInputStream is = new FileInputStream(file))
    {
      return readX509CertStream(is);
    }
  }

  public static X509Certificate readX509CertBuffer(byte[] data)
     throws Exception
  {
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    return readX509CertStream(bais);
  }

  public static X509Certificate readX509CertStream(InputStream is)
     throws Exception
  {
    CertificateFactory cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
    X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
    return cert;
  }

  public static X509Certificate readX509CertString(String certString)
     throws Exception
  {
    return readX509CertBuffer(certString.getBytes());
  }

  public static byte[] readFileFirma(File fileFirma)
     throws Exception
  {
    String contenuto = CommonFileUtils.readFileTxt(fileFirma, null);
    return Base64.decodeBase64(contenuto);
  }

  /**
   * Effettua una verifica sui dati firmati rispetto alla chiave pubblica passata.
   * @param certFile file con il certificato/chiave pubblica dell'utente
   * @param signFile file con la firma
   * @param fileToVerify file con i dati del documento firmato
   * @return vero se la chiave pubblica è coerente con la firma
   * @throws Exception
   */
  public static boolean verify(File certFile, File signFile, File fileToVerify)
     throws Exception
  {
    boolean verified = false;

    // import encoded public key
    X509Certificate cert = readX509CertFile(certFile);
    PublicKey pubKey = cert.getPublicKey();

    byte[] signature = readFileFirma(signFile);

    try (FileInputStream datafis = new FileInputStream(fileToVerify))
    {
      verified = verify(pubKey, signature, datafis);
    }

    return verified;
  }

  /**
   * Effettua una verifica sui dati firmati rispetto alla chiave pubblica passata.
   * @param certString certificato X509 sotto forma di stringa
   * @param signString firma digitale encoded in base 64
   * @param dataToVerify array con i dati da verificare
   * @return vero se la chiave pubblica è coerente con la firma
   * @throws Exception
   */
  public static boolean verify(String certString, String signString, byte[] dataToVerify)
     throws Exception
  {
    // import encoded public key
    X509Certificate cert = readX509CertString(certString);
    PublicKey pubKey = cert.getPublicKey();

    // convert encoded signature
    byte[] signature = Base64.decodeBase64(signString.getBytes());

    return verify(pubKey, signature, new ByteArrayInputStream(dataToVerify));
  }

  /**
   * Effettua una verifica sui dati firmati rispetto alla chiave pubblica passata.
   * @param pubKey chiave pubblica dell'utente
   * @param signature dati della firma
   * @param dataToVerify flusso con i dati da controllare
   * @return vero se la chiave pubblica è coerente con la firma
   * @throws Exception
   */
  public static boolean verify(PublicKey pubKey, byte[] signature, InputStream dataToVerify)
     throws Exception
  {
    // create a Signature object and initialize it with the public key
    Signature sig = Signature.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
    sig.initVerify(pubKey);

    // aggiorna dati leggendo lo stream
    int n;
    byte[] buffer = new byte[4096];
    while((n = dataToVerify.read(buffer)) > 0)
      sig.update(buffer, 0, n);

    // verifica firma
    return sig.verify(signature);
  }

  /**
   * Restituisce il certificato dell'utente analizzando la certification chain
   * passata in ingresso. Dalla certification chain passata ricostruisce l'albero
   * dei certificati. Quello dell'utente è in testa alla lista, ovvero quello che
   * non firma nessun altro certificato.
   * @param aCertificationChain catena di certificati
   * @return il certificato dell'utente
   * @throws CertificateException
   */
  public static X509Certificate getCert(Certificate[] aCertificationChain)
     throws CertificateException
  {
    List certList = Arrays.asList(aCertificationChain);
    CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
    CertPath certPath = certFactory.generateCertPath(certList);
    List lista = certPath.getCertificates();
    X509Certificate cert = null;
    // --- il certificato dell'utente e' sempre il primo della catena.
    if(lista.size() > 0)
      cert = (X509Certificate) lista.get(0);
    return cert;
  }

  /**
   * Applica la firma digitale al documento rappresentato come array di bytes.
   * @param aDocument documento da firmare
   * @param aPrivateKey chiave privata da utilizzare per la firma
   * @return firma digitale del documento
   * @throws GeneralSecurityException
   */
  public static byte[] signDocument(byte[] aDocument, PrivateKey aPrivateKey)
     throws GeneralSecurityException
  {
    Signature sa = Signature.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
    sa.initSign(aPrivateKey);
    sa.update(aDocument);
    return sa.sign();
  }

  /**
   * Applica la firma digitale al documento rappresentato come stream di bytes.
   * @param streamDocument documento da firmare
   * @param aPrivateKey chiave privata da utilizzare per la firma
   * @return firma digitale del documento
   * @throws GeneralSecurityException
   * @throws java.io.IOException
   */
  public static byte[] signDocument(InputStream streamDocument, PrivateKey aPrivateKey)
     throws GeneralSecurityException, IOException
  {
    Signature sa = Signature.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
    sa.initSign(aPrivateKey);

    int n;
    byte[] buffer = new byte[4096];
    while((n = streamDocument.read(buffer)) > 0)
      sa.update(buffer, 0, n);

    return sa.sign();
  }

  /**
   * Estrae il contenuto di un file P7M.
   * @param p7m file da leggere
   * @param output contenuto del p7m
   * @throws Exception
   */
  public static void extractDocument(File p7m, File output)
     throws Exception
  {
    InputStream is = new FileInputStream(p7m);
    OutputStream os = new FileOutputStream(output);
    try
    {
      CMSSignedData sdp = new CMSSignedData(is);
      CMSProcessable cmsp = sdp.getSignedContent();
      os.write((byte[]) cmsp.getContent());
    }
    finally
    {
      is.close();
      os.close();
    }
  }

  /**
   * Estrae il contenuto di un file P7M.
   * @param p7m file da leggere
   * @param output contenuto del p7m
   * @return
   * @throws Exception
   */
  public static byte[] extractDocument(File p7m)
     throws Exception
  {
    try (InputStream is = new FileInputStream(p7m))
    {
      CMSSignedData sdp = new CMSSignedData(is);
      CMSProcessable cmsp = sdp.getSignedContent();
      return (byte[]) cmsp.getContent();
    }
  }

  public static boolean isSmartcardPlugged()
     throws Exception
  {
    TerminalFactory factory = TerminalFactory.getDefault();
    CardTerminals terminals = factory.terminals();
    //for(CardTerminal terminal : terminals.list(CardTerminals.State.CARD_INSERTION))
    for(CardTerminal terminal : terminals.list())
    {
      if(terminal.isCardPresent())
        return true;
    }
    return false;
  }

  public static boolean tryLoadCertificate(File f, Collection contentList, CertificateFactory cf)
  {
    try (FileInputStream fis = new FileInputStream(f))
    {
      contentList.add((X509Certificate) cf.generateCertificate(fis));
      return true;
    }
    catch(Exception ex)
    {
      // ignore
    }
    return false;
  }

  public static boolean tryLoadCrl(File f, Collection contentList, CertificateFactory cf)
  {
    try (FileInputStream fis = new FileInputStream(f))
    {
      contentList.add((X509CRL) cf.generateCRL(fis));
      return true;
    }
    catch(Exception ex)
    {
      // ignore
    }
    return false;
  }
}
