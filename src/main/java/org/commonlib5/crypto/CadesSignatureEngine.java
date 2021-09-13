/*
 * CadesSignatureEngine.java
 *
 * Created on 8-ott-2011, 19.40.36
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.x509.X509CollectionStoreParameters;

/**
 * Questo motore di firma produce files p7m conformi
 * alla direttiva CADES valida in Italia.
 *
 * Il 17 agosto 2010 viene pubblicata in Gazzetta Ufficiale la
 * Determinazione Commissariale n.69/2010 DigitaPA che fissava
 * il 30/06/2011 quale data ultima per l’adeguamento dei formati
 * di firma alle specifiche CAdES.
 *
 * CMS Advanced Electronic Signatures è un set di estensioni della
 * specifica Cryptographic Message Syntax per la realizzazione di
 * firme digitali avanzate particolarmente sicure e, soprattutto,
 * conformi con la Direttiva Europea 1999/93/EC.
 *
 * Questa classe richiede le librerie Bouncy Castle.
 *
 * @author Nicola De Nisco
 */
public class CadesSignatureEngine extends SignatureEngine
{
  /**
   * Inizializza l'engine con la librearia nativa
   * contenente il layer PKCS11 nativo.
   * @param libraryFile libreria nativa (può essere null se solo operazioni di verifica)
   */
  public CadesSignatureEngine(File libraryFile)
  {
    super(libraryFile);
    if(Security.getProvider("BC") == null)
      Security.addProvider(new BouncyCastleProvider());
  }

  public CadesSignatureEngine(SmartcardDataProvider sdp)
  {
    super(sdp);
    if(Security.getProvider("BC") == null)
      Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Firma il file specificato dalla path generando
   * nella stessa directory un file con lo stesso nome
   * ed estensione p7m (ES: file.txt -> file.txt.p7m)
   * @param fileDaFirmarePath path del file da firmare
   * @throws Exception
   */
  public void signDocument(String fileDaFirmarePath)
     throws Exception
  {
    String fileFirmatoPath = fileDaFirmarePath + ".p7m";
    File fileInput = new File(fileDaFirmarePath);
    File fileFirmato = new File(fileFirmatoPath);
    signDocument(fileInput, fileFirmato);
  }

  /**
   * Applica la firma digitale al documento in input
   * generando un file nel formato p7m in output.
   * @param fileInput file da firmare
   * @param fileFirmato file output in formato p7m
   * @throws Exception
   */
  public void signDocument(File fileInput, File fileFirmato)
     throws Exception
  {
    /**
     * Inizializzo il generatore impostando l'algoritmo di
     * hashing desiderato ed aggiungendo un firmatario
     */
    CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
    ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider(
       kdp.pkcs11Provider.getName()).build(kdp.privateKey);

    /**
     * -- CADES --
     */
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(kdp.userCertificate.getEncoded());
    byte[] certHash = md.digest();

    // The OID for SHA-256: http://www.oid-info.com/get/2.16.840.1.101.3.4.2.1
    ASN1ObjectIdentifier oid = new ASN1ObjectIdentifier(
       "2.16.840.1.101.3.4.2.1").intern();
    AlgorithmIdentifier algId = new AlgorithmIdentifier(oid);

    ESSCertIDv2 essCert1 = new ESSCertIDv2(algId, certHash);
    ESSCertIDv2[] essCert1Arr =
    {
      essCert1
    };

    SigningCertificateV2 scv2 = new SigningCertificateV2(essCert1Arr);
    Attribute certHAttribute = new Attribute(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new DERSet(scv2));
    ASN1EncodableVector v = new ASN1EncodableVector();
    v.add(certHAttribute);
    /**
     * -- END CADES --
     */
    AttributeTable at = new AttributeTable(v);
    CMSAttributeTableGenerator attrGen = new DefaultSignedAttributeTableGenerator(at);
    SignerInfoGeneratorBuilder genBuild = new SignerInfoGeneratorBuilder(new BcDigestCalculatorProvider());
    genBuild.setSignedAttributeGenerator(attrGen);
    SignerInfoGenerator sifGen = genBuild.build(sigGen, new X509CertificateHolder(kdp.userCertificate.getEncoded()));

    gen.addSignerInfoGenerator(sifGen);

    /**
     * Popolo la "catena di certificazione" (certificate chain)
     */
    ArrayList certList = new ArrayList();
    certList.add(kdp.userCertificate);

    X509CollectionStoreParameters x509CollectionStoreParameters = new X509CollectionStoreParameters(certList);
    JcaCertStore jcaCertStore = new JcaCertStore(certList);
    gen.addCertificates(jcaCertStore);

    /**
     * Genera il file p7m e lo salva nel path specificato.
     * Inserendo "false" come secondo parametro di generate() si
     * otterrebbe invece un file p7s, ovvero la sola firma "detached".
     */
    CMSProcessableFile content = new CMSProcessableFile(fileInput);
    CMSSignedData data = gen.generate(content, true);
    byte[] res = data.getEncoded();
    try (FileOutputStream fos = new FileOutputStream(fileFirmato))
    {
      fos.write(res);
    }
  }

  /**
   * Verifica il documento PKCS7 rappresentato nell'array di bytes.
   * @param signedBytes
   * @return vero se la firma è verificata
   * @throws Exception
   */
  @Override
  public boolean verify(byte[] signedBytes)
     throws Exception
  {
    CMSSignedData s = new CMSSignedData(signedBytes);
    return verify(s, null);
  }

  /**
   * Verifica il documento PKCS7 letto dallo stream indicato.
   * @param is
   * @return vero se la firma è verificata
   * @throws Exception
   */
  @Override
  public boolean verify(InputStream is)
     throws Exception
  {
    CMSSignedData s = new CMSSignedData(is);
    return verify(s, null);
  }

  /**
   * Verifica il file p7m indicato.
   * Opzionalmente oltre alla verifica produce il contenuto originale.
   * @param inputFirmato file p7m da verificare
   * @param outputOrginale file da scrivere con il contenuto originale (può essere null)
   * @return vero se la firma è verificata
   * @throws Exception
   */
  public boolean verify(File inputFirmato, File outputOrginale)
     throws Exception
  {
    boolean rv = false;
    InputStream is = new FileInputStream(inputFirmato);
    OutputStream os = outputOrginale == null ? null : new FileOutputStream(outputOrginale);

    try
    {
      CMSSignedData s = new CMSSignedData(is);
      rv = verify(s, os);
    }
    finally
    {
      is.close();
      if(os != null)
        os.close();
    }

    return rv;
  }

  /**
   * Verifica la firma digitale ed eventualmente produce l'output originale.
   * @param s dati firmati
   * @param originalData stream per l'uscita dei dati originali (può essere null).
   * @return vero se la firma è verificata
   * @throws Exception
   */
  public boolean verify(CMSSignedData s, OutputStream originalData)
     throws Exception
  {
    boolean verified = false;
    Store certStore = s.getCertificates();
    SignerInformationStore signers = s.getSignerInfos();
    Collection c = signers.getSigners();
    Iterator it = c.iterator();

    while(it.hasNext())
    {
      SignerInformation signer = (SignerInformation) it.next();
      Collection certCollection = certStore.getMatches(signer.getSID());

      Iterator certIt = certCollection.iterator();
      X509CertificateHolder cert = (X509CertificateHolder) certIt.next();

      if(signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert)))
      {
        verified = true;
      }

      // salva il certificato se non ha già fatto
      if(kdp.userCertificate == null)
        kdp.userCertificate = convert(cert);
    }

    if(originalData != null)
    {
      CMSProcessable signedContent = s.getSignedContent();
      signedContent.write(originalData);
    }

    return verified;
  }

  public static X509Certificate convert(X509CertificateHolder certificateHolder)
     throws Exception
  {
    return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);
  }
}
