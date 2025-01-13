/*
 * Copyright (C) 2014 nicola
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

import com.itextpdf.text.Rectangle;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.commonlib5.crypto.PadesSignatureEngine;

/**
 *
 * @author nicola
 */
public class testPades extends testFirmaBase
{
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
     throws Exception
  {
    prepare();
    File libSmartcard = new File("/opt/smartcard-aruba/libbit4xpki.dylib");
    PadesSignatureEngine engine = new PadesSignatureEngine(libSmartcard);

    engine.prepareSmartCardAndData(pin);

    testVisualizzaCertificato(engine);
    testOggettoCertificato(engine);
    testFirmaSingola(engine);
  }

  public static void testFirmaSingola(PadesSignatureEngine engine)
     throws Exception
  {
    File input = new File("/Users/nicola/Documents/rad-image/argo-installation-report.pdf");
    File output = new File("/tmp/firmato.pdf");

//      CardReaders readers = new CardReaders();
//      for(CardTerminal terminal : readers.getReaders())
//      {
//        System.out.println(terminal.getName());
//      }
//      for(CardTerminal terminal : readers.getReadersWithCard())
//      {
//        System.out.println(terminal.getName());
//        SmartCard card = new SmartCard(terminal);
//        IdentityPojo id = BeIDFileFactory.getIdentity(card);
//        System.out.println(id.toString());
//        AddressPojo address = BeIDFileFactory.getAddress(card);
//        System.out.println(address);
//        PhotoPojo photo = BeIDFileFactory.getPhoto(card);
//        FileOutputStream fos = new FileOutputStream(PHOTO);
//        fos.write(photo.getPhoto());
//        fos.flush();
//        fos.close();
//      }
    PadesSignatureEngine.PdfSignatureAppearanceInfo info = new PadesSignatureEngine.PdfSignatureAppearanceInfo();
    info.location = "Luogo della firma.";
    info.reason = "Motivo della firma.";
    info.position = new Rectangle(36, 748, 144, 780);
    info.page = 1;
    engine.setAppearanceInfo(info);

//      TSAClient tsa = engine.getTSAfromSmartcard();
    // Usa Digistamp per ottenere un account di test per il rilascio di timestamp
//    TSAClient tsa = new TSAClientBouncyCastle("http://tsatest1.digistamp.com/tsa", "12345678", "12345678");
//    engine.setTsaClient(tsa);
//    engine.setUseInternetCrl(true);
    engine.signDocument(input, output);
  }

  public static void testFirmaMultipla(PadesSignatureEngine engine)
     throws Exception
  {
    File input = new File("/Users/nicola/Documents/rad-image/argo-installation-report.pdf");
    File output1 = new File("/tmp/firmato1.pdf");
    File output2 = new File("/tmp/firmato2.pdf");

    // applica la prima firma
    PadesSignatureEngine.PdfSignatureAppearanceInfo info1 = new PadesSignatureEngine.PdfSignatureAppearanceInfo();
//    info1.infoName = "Firma 1";
//    info1.location = "Luogo della prima firma.";
//    info1.reason = "Motivo della prima firma.";
//    info1.position = new Rectangle(300, 750, 400, 780);
//    info1.page = 1;
//    engine.setAppearanceInfo(info1);
    engine.signDocument(input, output1);

    // applica la seconda firma
    PadesSignatureEngine.PdfSignatureAppearanceInfo info2 = new PadesSignatureEngine.PdfSignatureAppearanceInfo();
//    info2.infoName = "Firma 2";
//    info2.location = "Luogo della seconda firma.";
//    info2.reason = "Motivo della seconda firma.";
//    info2.position = new Rectangle(400, 750, 500, 780);
//    info2.page = 1;
//    engine.setAppearanceInfo(info2);
    engine.signDocument(output1, output2);
  }

  public static void testVisualizzaCertificato(PadesSignatureEngine engine)
     throws Exception
  {
    X509Certificate userCertificate = engine.getKdp().getUserCertificate();

    X500Name x500name = new JcaX509CertificateHolder(userCertificate).getSubject();
    ASN1ObjectIdentifier[] attributeTypes = x500name.getAttributeTypes();
    X500NameStyle style = X500Name.getDefaultStyle();
    System.out.println("Attributi certificato:");

    for(int i = 0; i < attributeTypes.length; i++)
    {
      ASN1ObjectIdentifier oi = attributeTypes[i];
      String dispName = style.oidToDisplayName(oi);
      System.out.println("  DISP: " + dispName);

      RDN[] rdNs = x500name.getRDNs(oi);
      for(int j = 0; j < rdNs.length; j++)
      {
        RDN rdN = rdNs[j];
        String value = IETFUtils.valueToString(rdN.getFirst().getValue());
        System.out.printf("  %4d: %s\n", j, value);
      }
    }
  }

  public static void testOggettoCertificato(PadesSignatureEngine engine)
     throws Exception
  {
    Map<String, String> subValues = engine.getKdp().getUserCertificateSubjectFields();

    for(Map.Entry<String, String> entry : subValues.entrySet())
    {
      String key = entry.getKey();
      String value = entry.getValue();

      System.out.printf("%15s: %s\n", key, value);
    }
  }
}
