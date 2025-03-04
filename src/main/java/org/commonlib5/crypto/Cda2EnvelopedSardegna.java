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

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.apache.xml.security.c14n.Canonicalizer;
import org.commonlib5.utils.CommonFileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xades4j.algorithms.Algorithm;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesSignatureResult;
import xades4j.production.XadesSigner;
import xades4j.providers.ValidationDataException;
import xades4j.utils.DOMHelper;

/**
 * Versione specializzata per regione sardegna.
 *
 * @author Nicola De Nisco
 */
public class Cda2EnvelopedSardegna extends Cda2Enveloped
{
  public Cda2EnvelopedSardegna(XadesSigner signer, Document doc, Map<String, String> attrs)
  {
    super(signer, doc, attrs);
  }

  /**
   * Crea il tag 'legalAuthenticator' con le nuove informazioni di firma.
   * @return tag completo
   * @throws java.lang.Exception
   */
  private Element createLegalAuthenticator()
     throws Exception
  {
    /*
  <legalAuthenticator>
    <time value="20190312101904+0100"/>
    <signatureCode code="S"/>

    ... Element signature ...

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

    signTimestamp = new Date();

    if(thumbnailText == null)
      thumbnailText = createThumbnailText();

    Element time = doc.createElement("time");
    time.setAttribute("value", dt1.format(signTimestamp));
    legalAuthenticator.appendChild(time);
    Element signatureCode = doc.createElement("signatureCode");
    signatureCode.setAttribute("code", "S");
    legalAuthenticator.appendChild(signatureCode);

    // se non disponibile crea il tag assigned entity
    if(assignedEntity == null)
      assignedEntity = createAssignedEntity();

    legalAuthenticator.appendChild(assignedEntity);

    return legalAuthenticator;
  }

  public void signS(Element elementToSign, File signedpdf)
     throws Exception
  {
    Collection<Element> custodian = DOMHelper.getChildElementsByTagNameNS(elementToSign, URNHL7ORGV3, "custodian");
    if(custodian.isEmpty())
      throw new ValidationDataException("Nessun tag 'custodian' nel documento; non conforme a CDA2.");

    /*
    Per la firma XAdES Enveloped, la rimozione delle firme precedenti e
    la successiva reintroduzione comprometterebbe l'impronta del documento
    quindi, momentaneamente commento la sezione interessata
     */
    //    purgeSignatures();
    //va inserito il PADES
    if(signedpdf != null)
    {
      Collection<Element> Ccomponent
         = DOMHelper.getChildElementsByTagNameNS(elementToSign, URNHL7ORGV3, "component");
      Element component = Ccomponent.iterator().next();
      Element nonXMLBody = (Element) component.getElementsByTagName("nonXMLBody").item(0);

      Node text = nonXMLBody.getElementsByTagName("text").item(0);
      text.setTextContent(CommonFileUtils.binary_2_Base64(signedpdf));
    }

    DataObjectReference dataObjRef = new DataObjectReference("");
    dataObjRef.withTransform(new EnvelopedSignatureTransform());
    dataObjRef.withTransform(new CanonicalXMLWithoutComments11());

    /*
    l'elemento che conterrà la signature va creato prima della firma stessa
    perché deve essere esso stesso firmato
     */
    Element legalAuthenticator = createLegalAuthenticator();
    Node successivo = custodian.iterator().next().getNextSibling();
    elementToSign.insertBefore(legalAuthenticator, successivo);

    /* aggiunge nuovamente le firme precedenti
    auths.forEach((e) -> elementToSign.insertBefore(e, successivo));
    lauths.forEach((e) -> elementToSign.insertBefore(e, successivo));
     */
    // firma il documento
    XadesSignatureResult sres = signer.sign(new SignedDataObjects(dataObjRef), elementToSign);

    //inietta la  signature nel legalAuthenticator prima di assignedEntity
    Element signature = sres.getSignature().getElement();
    legalAuthenticator.insertBefore(signature, assignedEntity);
  }

  public static class CanonicalXMLWithoutComments11 extends Algorithm
  {
    public CanonicalXMLWithoutComments11()
    {
      super(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS);
    }
  }
}
