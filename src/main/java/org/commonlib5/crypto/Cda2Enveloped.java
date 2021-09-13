/*
 * Copyright (C) 2019 Nicola De Nisco
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

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.commonlib5.utils.StringOper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.XAdES4jException;
import xades4j.properties.DataObjectDesc;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesSignatureResult;
import xades4j.production.XadesSigner;
import xades4j.providers.ValidationDataException;
import xades4j.utils.DOMHelper;

/**
 * Classe di utility per creare un CDA2 firmato.
 * Un cda2 usa una firma XADES-X-L che viene racchiusa
 * in un tag legalAuthenticator con altre informazioni sull'utente
 * che applica la firma.
 * <p>
 * Viene firmato l'intero documento rimuovendo preventivamente
 * i tag legalAuthenticator e authenticator per il calcolo del digest.
 * In questo modo si possono applicare piu firme.
 * <p>
 * La struttura è descritta in: <br>
 * http://www.hl7.org/documentcenter/public/wg/ca/Supplement -- DS section V2.docx
 * <p>
 * <i>Quando si firma digitalmente un documento CDA, il Digest dell'oggetto Signed Data
 * è l'intero documento che esclude tutte le occorrenze di (ed elementi contenuti) in
 * 'authenticator' e 'legalAuthenticator'. Escludendo dal calcolo del Digest le occorrenze
 * di legalAuthenticator e partecipanti all'autenticatore, le informazioni firmate da ciascun
 * firmatario autorizzato e firmatario delegato non saranno modificate dai successivi eventi di firma.
 * Ciò consente a più firmatari autorizzati e firmatari delegati su qualsiasi C-CDA. Va notato che
 * escludere le occorrenze di legalAuthenticator e partecipanti all'autenticatore dal calcolo del
 * Digest non le rimuove dal C-CDA.</i>
 * <p>
 * I documenti CDA2 possono essere validati con: <br>
 * https://www.lantanagroup.com/validator/Validator
 * <ul>
 * <li>NON FIRMATI: CDA_R2 (Original Normative CDA Edition - No Extensions)</li>
 * <li>CON FIRMA: CDA_SDTC (CDA XML Schema with SDTC Approved Extensions) </li>
 * </ul>
 * <p>
 * La classe non è rientrante e non è thread safe; va istanziata per firmare un singolo documento.
 * @author Nicola De Nisco
 */
public class Cda2Enveloped
{
  public static final String URNHL7ORGV3 = "urn:hl7-org:v3";
  //
  protected final XadesSigner signer;
  protected final Document doc;
  protected Collection<Element> auths, lauths;
  protected String thumbnailText;
  protected Element assignedEntity;
  protected Map<String, String> attrs;
  protected SimpleDateFormat dt1 = new SimpleDateFormat("yyyyMMddHHmmssZ");
  protected SimpleDateFormat dt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  protected Date signTimestamp;

  /**
   * Crea un'istanza per firmare un singolo documento.
   * @param signer il motore di firma
   * @param doc documento da firmare
   * @param attrs mappa di attributi (X500) estratti dal certificato del firmatario
   */
  public Cda2Enveloped(XadesSigner signer, Document doc, Map<String, String> attrs)
  {
    this.signer = signer;
    this.doc = doc;
    this.attrs = attrs;
  }

  /**
   * Firma il documento.
   *
   * @throws XAdES4jException see {@link XadesSigner#sign(xades4j.production.SignedDataObjects, org.w3c.dom.Node)}
   * @throws IllegalArgumentException if {@code elementToSign} doesn't have an Id and isn't the document root
   */
  public void sign()
     throws Exception
  {
    // estrae il root node del documento
    Element elementToSign = doc.getDocumentElement();

    Collection<Element> custodian = DOMHelper.getChildElementsByTagNameNS(elementToSign, URNHL7ORGV3, "custodian");
    if(custodian.isEmpty())
      throw new ValidationDataException("Nessun tag 'custodian' nel documento; non conforme a CDA2.");

    // rimuove eventuali firme precedenti
    purgeSignatures();

    // firma il documento
    DataObjectDesc dataObjRef = new DataObjectReference("").withTransform(new EnvelopedSignatureTransform());
    XadesSignatureResult sres = signer.sign(new SignedDataObjects(dataObjRef), elementToSign);

    signTimestamp = new Date();

    if(thumbnailText == null)
      thumbnailText = createThumbnailText();

    // aggiunge al documento originale la firma ottenuta
    Element legalAuthenticator = createLegalAutenticator(sres.getSignature().getElement());

    // se non disponibile crea il tag assigned entity
    if(assignedEntity == null)
      assignedEntity = createAssignedEntity();

    legalAuthenticator.appendChild(assignedEntity);

    Node successivo = custodian.iterator().next().getNextSibling();

    // aggiunge nuovamente le firme precedenti
    auths.forEach((e) -> elementToSign.insertBefore(e, successivo));
    lauths.forEach((e) -> elementToSign.insertBefore(e, successivo));

    // aggiunge la nuova firma in coda a quelle già presenti
    elementToSign.insertBefore(legalAuthenticator, successivo);
  }

  /**
   * Produce un clone del documento passato.
   * @param originalDocument documento originale
   * @return clone del documento
   * @throws Exception
   */
  public Document cloneDocument(Document originalDocument)
     throws Exception
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();

    Node originalRoot = originalDocument.getDocumentElement();

    Document copiedDocument = db.newDocument();
    Node copiedRoot = copiedDocument.importNode(originalRoot, true);
    copiedDocument.appendChild(copiedRoot);

    return copiedDocument;
  }

  /**
   * Rimuove informazioni di firma dal documento da firmare.
   */
  public void purgeSignatures()
  {
    Element root = doc.getDocumentElement();
    auths = DOMHelper.getChildElementsByTagNameNS(root, URNHL7ORGV3, "authenticator");
    lauths = DOMHelper.getChildElementsByTagNameNS(root, URNHL7ORGV3, "legalAuthenticator");

    auths.forEach((e) -> root.removeChild(e));
    lauths.forEach((e) -> root.removeChild(e));
  }

  /**
   * Crea il tag 'legalAuthenticator' con le nuove informazioni di firma.
   * @param signature firma XADES da aggiungere al tag
   * @return tag completo
   */
  public Element createLegalAutenticator(Element signature)
  {
    /*
  <legalAuthenticator>
    <time value="20190312101904+0100"/>
    <signatureCode code="S"/>

    <sdtc:signatureText mediaType="text/xml" representation="B64">
      <thumbnail mediaType="text/plain" representation="TXT">Firmato digitalmente da Luca Verdi il 2019-03-12 alle 19:04 come medico in qualità di autore.
      </thumbnail>
      <digitalSignature xmlns="http://uri.etsi.org/01903/v1.1.1#">
        <authorizedSigner>
        ... Element signature ...
        </authorizedSigner>
      </digitalSignature>
    </sdtc:signatureText>

    <assignedEntity>
      <id assigningAuthorityName="Ministero Economia e Finanze" extension="METEST00X00X000X" root="2.16.840.1.113883.2.9.4.3.2"/>
      <assignedPerson>
        <name>
          <prefix>Dott.</prefix>
          <given>Luca</given>
          <family>Verdi</family>
        </name>
      </assignedPerson>
    </assignedEntity>
  </legalAuthenticator>
     */

    Element legalAuthenticator = doc.createElement("legalAuthenticator");

    Element time = doc.createElement("time");
    time.setAttribute("value", dt1.format(signTimestamp));
    legalAuthenticator.appendChild(time);
    Element signatureCode = doc.createElement("signatureCode");
    signatureCode.setAttribute("code", "S");
    legalAuthenticator.appendChild(signatureCode);

    Element signatureText = doc.createElement("ns2:signatureText");
    signatureText.setAttribute("mediaType", "text/xml");
    signatureText.setAttribute("representation", "B64");
    legalAuthenticator.appendChild(signatureText);

    Element thumbnail = doc.createElement("thumbnail");
    thumbnail.setAttribute("mediaType", "text/plain");
    thumbnail.setAttribute("representation", "TXT");
    thumbnail.setTextContent(StringOper.okStr(thumbnailText));
    signatureText.appendChild(thumbnail);

    Element digitalSignature = doc.createElementNS("http://uri.etsi.org/01903/v1.1.1#", "digitalSignature");
    signatureText.appendChild(digitalSignature);

    Element authorizedSigner = doc.createElement("authorizedSigner");
    digitalSignature.appendChild(authorizedSigner);

    authorizedSigner.appendChild(signature);

    return legalAuthenticator;
  }

  /**
   * Crea il tag 'assignedEntity' da inserire in 'legalAuthenticator'.
   * Usa il certificato del firmatario per estrarre i tag significativi.
   * NOTA: questa implementazione va bene per Italia.
   * @return il tag estratto dai dati della smartcard
   * @throws DOMException
   */
  public Element createAssignedEntity()
     throws DOMException
  {
    String tmp;
    Element lae = doc.createElement("assignedEntity");

    Element id = doc.createElement("id");
    id.setAttribute("assigningAuthorityName", "Ministero Economia e Finanze");
    id.setAttribute("extension", StringOper.okStr(attrs.get("SERIALNUMBER"), "METEST00X00X000X"));
    id.setAttribute("root", "2.16.840.1.113883.2.9.4.3.2");
    lae.appendChild(id);

    Element assignedPerson = doc.createElement("assignedPerson");
    lae.appendChild(assignedPerson);

    Element name = doc.createElement("name");
    assignedPerson.appendChild(name);

    if((tmp = StringOper.okStrNull(attrs.get("PREFIX"))) != null)
    {
      Element prefix = doc.createElement("prefix");
      prefix.setTextContent(tmp);
      name.appendChild(prefix);
    }

    if((tmp = StringOper.okStrNull(attrs.get("GIVENNAME"))) != null)
    {
      Element given = doc.createElement("given");
      given.setTextContent(tmp);
      name.appendChild(given);
    }

    if((tmp = StringOper.okStrNull(attrs.get("SURNAME"))) != null)
    {
      Element family = doc.createElement("family");
      family.setTextContent(tmp);
      name.appendChild(family);
    }

    return lae;
  }

  public String createThumbnailText()
     throws Exception
  {
    String nome = attrs.get("GIVENNAME");
    String cogn = attrs.get("SURNAME");

    if(nome == null || cogn == null)
      return "";

    return String.format(
       "Firmato digitalmente da %s %s il %s come medico in qualità di autore.",
       nome, cogn, dt2.format(signTimestamp));
  }

  public String getThumbnailText()
  {
    return thumbnailText;
  }

  public void setThumbnailText(String thumbnailText)
  {
    this.thumbnailText = thumbnailText;
  }

  public Element getAssignedEntity()
  {
    return assignedEntity;
  }

  public void setAssignedEntity(Element assignedEntity)
  {
    this.assignedEntity = assignedEntity;
  }
}
