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
package org.commonlib5.crypto;

import java.io.*;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.utils.Constants;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.StringOper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xades4j.algorithms.Algorithm;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.*;
import xades4j.properties.*;
import xades4j.providers.*;
import xades4j.providers.impl.*;
import xades4j.utils.DOMHelper;
import xades4j.verification.*;

/**
 * Questo motore di firma produce files xml conformi
 * alla direttiva xADES valida in Italia.
 *
 * @author Nicola De Nisco
 */
public class XadesSignatureEngine extends SignatureEngine
{
  public static class TsaInfo
  {
    public String tsaUrl, tsaUser, tsaPassword;
  }

  @SuppressWarnings("Convert2Lambda")
  private CertificateValidationProvider cvp = new CertificateValidationProvider()
  {
    @Override
    public ValidationData validate(X509CertSelector certSelector, Date validationDate, Collection<X509Certificate> otherCerts)
       throws CertificateValidationException, UnexpectedJCAException
    {
      try
      {
        return new ValidationData(kdp.getSigningCertificateChain());
      }
      catch(SigningCertChainException ex)
      {
        throw new UnexpectedJCAException("Validate certificate failure.", ex);
      }
    }
  };

  public XadesSignatureEngine(File libraryFile)
  {
    super(libraryFile);
  }

  public XadesSignatureEngine(SmartcardDataProvider kdp)
  {
    super(kdp);
  }

  public void signBes(File toSign, File signed, boolean removeIdtag)
     throws Exception
  {
    signBes(toSign, signed, "", null, removeIdtag);
  }

  public void signBes(File toSign, File signed, TsaInfo ti, boolean removeIdtag)
     throws Exception
  {
    signBes(toSign, signed, "", ti, removeIdtag);
  }

  public void signBes(File toSign, File signed, String tagToSign, TsaInfo ti, boolean removeIdtag)
     throws Exception
  {
    // per scongiurare problemi con i validatori .NET, eliminiamo lineBreak (&#13;)
    System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    org.apache.xml.security.Init.init();

    Document doc = getDocument(toSign);
    Element elem = doc.getDocumentElement();
    DOMHelper.useIdAsXmlId(elem);

    /* il documento non va manomesso, altrimenti fallisce la verifica della firma
//    if(!StringOper.isOkStr(tagToSign))
//    {
//      // prova a leggere il tag 'Id' del root node
//      tagToSign = elem.getAttribute("Id");
//      if(!StringOper.isOkStr(tagToSign))
//      {
//        // se ancora vuoto da il nome del root node
//        tagToSign = elem.getTagName();
//      }
//    }
    // reimposta il tag Id del root node per identificare la sezione sottoposta a firma
    DOMHelper.setIdAsXmlId(elem, tagToSign);
     */
    DataObjectDesc obj = new DataObjectReference("")
       .withTransform(new EnvelopedSignatureTransform());
    SignedDataObjects dataObjs = new SignedDataObjects().withSignedDataObject(obj);

    XadesSigner signer;
    if(ti == null)
      signer = new XadesBesSigningProfile(kdp).newSigner();
    else
      signer = new XadesTSigningProfile(kdp)
         .withTimeStampTokenProvider(
            new AuthenticatedTimeStampTokenProvider(
               new DefaultMessageDigestProvider(),
               new TSAHttpAuthenticationData(ti.tsaUrl, ti.tsaUser, ti.tsaPassword)))
         .newSigner();

    signer.sign(dataObjs, elem, SignatureAppendingStrategies.AsLastChild);

//    if(removeIdtag && elem.hasAttributeNS(null, Constants._ATT_ID))
//    {
//      elem.removeAttributeNS(null, Constants._ATT_ID);
//    }
    writeDocument(doc, signed);
  }

  public void verifyBes(File toVerifiy)
     throws Exception
  {
    verifyBes(toVerifiy, cvp, new PrintWriter(System.out));
  }

  public void verifyBes(File toVerifiy, CertificateValidationProvider lcvp, PrintWriter out)
     throws Exception
  {
    Document doc = getDocument(toVerifiy);
    DOMHelper.useIdAsXmlId(doc.getDocumentElement());

    XadesVerificationProfile profile = new XadesVerificationProfile(lcvp);
    Element sigElem = getSigElement(doc);
    XAdESVerificationResult r = profile.newVerifier().verify(sigElem, null);

    out.println("Signature form: " + r.getSignatureForm());
    out.println("Algorithm URI: " + r.getSignatureAlgorithmUri());
    out.println("Signed objects: " + r.getSignedDataObjects().size());
    out.println("Qualifying properties: " + r.getQualifyingProperties().all().size());

    out.println("Signer certificate: " + r.getValidationCertificate());

    for(QualifyingProperty qp : r.getQualifyingProperties().all())
    {
      switch(StringOper.okStr(qp.getName()))
      {
        case "SigningCertificate":
          Collection<X509Certificate> certs = ((SigningCertificateProperty) qp).getsigningCertificateChain();
          certs.forEach((cert) -> out.println("Issuer DN: " + cert.getIssuerDN()));
          break;
        case "SigningTime":
          out.println("Time: " + ((SigningTimeProperty) qp).getSigningTime().getTime().toString());
          break;
        case "SignatureTimeStamp":
          out.println("Time stamp: " + ((SignatureTimeStampProperty) qp).getTime().toString());
          break;
        default:
          out.println("QP: " + qp.getName());
          break;
      }
    }
  }

  public void signC(File toSign, File signed, CertificateValidationProvider lcvp)
     throws Exception
  {
    // per scongiurare problemi con i validatori .NET, eliminiamo lineBreak (&#13;)
    System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    org.apache.xml.security.Init.init();

    Document doc = getDocument(toSign);
    Element elemToSign = doc.getDocumentElement();

    ValidationDataProvider vdp = new ValidationDataFromCertValidationProvider(lcvp);
    XadesSigner signer = new XadesCSigningProfile(kdp, vdp).newSigner();
    new Enveloped(signer).sign(elemToSign);

    writeDocument(doc, signed);
  }

  public void signCda(File toSign, File signed, CertificateValidationProvider lcvp)
     throws Exception
  {
    // per scongiurare problemi con i validatori .NET, eliminiamo lineBreak (&#13;)
    System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    org.apache.xml.security.Init.init();

    Document doc = getDocument(toSign);

    ValidationDataProvider vdp = new ValidationDataFromCertValidationProvider(lcvp);
    XadesSigner signer = new XadesCSigningProfile(kdp, vdp).newSigner();
    new Cda2Enveloped(signer, doc, kdp.getUserCertificateSubjectFields()).sign();

    writeDocument(doc, signed);
  }

  public static class AlgorithmsProviderSardegna extends DefaultAlgorithmsProviderEx
  {
    @Override
    public Algorithm getCanonicalizationAlgorithmForSignature()
    {
      return new CanonicalXMLWithComments11();
    }
  }

  public static class CanonicalXMLWithComments11 extends Algorithm
  {
    public CanonicalXMLWithComments11()
    {
      super(Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS);
    }
  }

  public void signCdaSardegna(File toSign, File signed, File signedpdf)
     throws Exception
  {
    // lineBreak (&#13;) nella signature mandano in crisi il validatore .NET
    System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    org.apache.xml.security.Init.init();

    Document doc = getDocument(toSign);
    Element elem = doc.getDocumentElement();

    BasicSignatureOptions opts = new BasicSignatureOptions();
    opts.includeIssuerSerial(false);
    opts.signKeyInfo(true);

    XadesBesSigningProfile profile = new XadesBesSigningProfile(kdp);
    profile.withBinding(BasicSignatureOptions.class, opts);
    profile.withBinding(AlgorithmsProviderEx.class, new AlgorithmsProviderSardegna());

    XadesSigner signer = profile.newSigner();
    Cda2EnvelopedSardegna envelope = new Cda2EnvelopedSardegna(signer, doc, kdp.getUserCertificateSubjectFields());
    envelope.signS(elem, signedpdf);

    writeDocument(doc, signed);
  }

  public static Document getDocument(File fileXML)
     throws Exception
  {
    try (FileInputStream fis = new FileInputStream(fileXML))
    {
      return parseDocument(fis);
    }
  }

  public static Document parseDocument(InputStream is)
     throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();

    Document doc = builder.parse(is);

//    // Apache Santuario now uses Document.getElementById; use this convention for tests.
//    Element elem = doc.getDocumentElement();
//    DOMHelper.useIdAsXmlId(elem);
    return doc;
  }

  protected XAdESForm verifySignature(File fileXML)
     throws Exception
  {
    return verifySignature(fileXML, new XadesVerificationProfile(cvp), null);
  }

  protected XAdESForm verifySignature(
     File fileXML,
     XadesVerificationProfile p)
     throws Exception
  {
    return verifySignature(fileXML, p, null);
  }

  protected XAdESForm verifySignature(
     File fileXML,
     SignatureSpecificVerificationOptions options)
     throws Exception
  {
    return verifySignature(fileXML, new XadesVerificationProfile(cvp), options);
  }

  protected XAdESForm verifySignature(
     File fileXML,
     XadesVerificationProfile p,
     SignatureSpecificVerificationOptions options)
     throws Exception
  {
    Element signatureNode = getSigElement(getDocument(fileXML));
    XAdESVerificationResult res = p.newVerifier().verify(signatureNode, options);
    return res.getSignatureForm();
  }

  static public Element getSigElement(Document doc)
     throws Exception
  {
    return (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS, Constants._TAG_SIGNATURE).item(0);
  }

  public static void removeIdTag(File inXml, File outXml)
     throws Exception
  {
    Document doc = getDocument(inXml);
    Element root = doc.getDocumentElement();

    if(root.hasAttributeNS(null, Constants._ATT_ID))
      root.removeAttributeNS(null, Constants._ATT_ID);

    writeDocument(doc, outXml);
  }

  public static void removeIdTag(File inPlaceXml)
     throws Exception
  {
    Document doc = getDocument(inPlaceXml);
    Element root = doc.getDocumentElement();

    if(!root.hasAttributeNS(null, Constants._ATT_ID))
      return;

    root.removeAttributeNS(null, Constants._ATT_ID);

    File tmpXml = File.createTempFile("tmp", ".xml", inPlaceXml.getParentFile());
    try
    {
      writeDocument(doc, tmpXml);
      inPlaceXml.delete();
      tmpXml.renameTo(inPlaceXml);
    }
    finally
    {
      tmpXml.delete();
    }
  }

  public static void writeDocument(Document doc, File outXml)
     throws Exception
  {
    // il file dopo la firma non può subire trasformazioni
    // altrimenti viene modificata l'impronta delle sezioni firmate del documento
    TransformerFactory tFactory = TransformerFactory.newInstance();
//    tFactory.setAttribute("indent-number", 2);

    Transformer transformer = tFactory.newTransformer();
//    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

    StringWriter sw = new StringWriter(8192);
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);

//    String out = sw.toString().replace("&#13;", "");
    String out = sw.toString();
    CommonFileUtils.writeFileTxt(outXml, out, "UTF-8");
  }
}
