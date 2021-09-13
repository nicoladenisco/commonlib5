/*
 * Copyright (C) 2021 Nicola De Nisco
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

import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xades4j.production.XadesSigner;

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
   * @param signature firma XADES da aggiungere al tag
   * @return tag completo
   */
  @Override
  public Element createLegalAutenticator(Element signature)
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

    Element time = doc.createElement("time");
    time.setAttribute("value", dt1.format(signTimestamp));
    legalAuthenticator.appendChild(time);
    Element signatureCode = doc.createElement("signatureCode");
    signatureCode.setAttribute("code", "S");
    legalAuthenticator.appendChild(signatureCode);

    legalAuthenticator.appendChild(signature);

    return legalAuthenticator;
  }
}
