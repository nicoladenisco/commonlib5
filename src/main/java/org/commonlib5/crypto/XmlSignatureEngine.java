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
import java.io.FileOutputStream;
import org.w3c.dom.Document;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * Questo motore di firma produce files xml conformi
 * alla direttiva xADES valida in Italia.
 *
 * @author Nicola De Nisco
 */
public class XmlSignatureEngine extends SignatureEngine
{
  static
  {
    org.apache.xml.security.Init.init();
  }

  public XmlSignatureEngine(File libraryFile)
  {
    super(libraryFile);
  }

  public void sign(File signatureFile, Document doc, Element root)
     throws Exception
  {
    // baseURI è la URI da anteporre alle altre URI
    String baseURI = signatureFile.toURI().toURL().toString();

    // oggetto XML Signature creato a partire dal documento
    XMLSignature sig = new XMLSignature(doc, baseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);

    root.appendChild(sig.getElement());

//    sig.getSignedInfo().addResourceResolver(
//       new org.apache.xml.security.samples.utils.resolver.OfflineResolver()
//    );
    {
      // Enveloped signature, Oggetto transforms
      Transforms transforms = new Transforms(doc);
      transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
      transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
      sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
    }

    {
      // URIs esterni (OfflineResolver): detached Reference. Firma mista
      sig.addDocument("http://www.w3.org/TR/xml-stylesheet");
      sig.addDocument("http://www.nue.et-inf.uni-siegen.de/index.html");
    }

    {
      // Aggiunta delle informazioni riguardanti il certificato (e la chiave) e creazione della firma
      sig.addKeyInfo(kdp.userCertificate);
      sig.addKeyInfo(kdp.userCertificate.getPublicKey());
      sig.sign(kdp.privateKey);
    }

    try (FileOutputStream f = new FileOutputStream(signatureFile))
    {
      XMLUtils.outputDOM(doc, f, true);
    }
  }
}
