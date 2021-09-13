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
package org.commonlib5.test;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.commonlib5.crypto.XmlSignatureEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test firma Xades.
 *
 * @author Nicola De Nisco
 */
public class testXmlsign extends testFirmaBase
{
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
     throws Exception
  {
    prepare();
    testOggettoCertificato(args);
  }

  private static void testOggettoCertificato(String[] args)
     throws Exception
  {
    File input = new File("/tmp/xades-originale.xml");
    File output = new File("/tmp/xades-firmato.xml");

    File libSmartcard = new File("/opt/smartcard-aruba/libbit4xpki.dylib");
    XmlSignatureEngine engine = new XmlSignatureEngine(libSmartcard);

    // qui viene passato il PIN
    engine.prepareSmartCardAndData(pin);

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(input);

    // optional, but recommended
    // read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
    Element root = doc.getDocumentElement();
    root.normalize();

    engine.sign(output, doc, root);
  }
}
