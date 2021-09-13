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
import org.commonlib5.crypto.PKIXSelfValidationProvider;
import org.commonlib5.crypto.SystemKeystore;
import org.commonlib5.crypto.XadesSignatureEngine;
import xades4j.utils.FileSystemDirectoryCertStore;

/**
 * Codice di test per firma XADES.
 *
 * @author Nicola De Nisco
 */
public class testXades extends testFirmaBase
{
  private static PKIXSelfValidationProvider aa;
  private static XadesSignatureEngine engine;

  public static void main(String[] args)
     throws Exception
  {
    prepare();

    // carica il keystore di java e aggiunge i certificati nella directory indicata
    SystemKeystore ks = new SystemKeystore();
    FileSystemDirectoryCertStore certStore = new FileSystemDirectoryCertStore("/Users/nicola/tdk/conf/rootca");
    ks.loadKeystore();
    ks.addFrom(certStore.getStore());

    aa = new PKIXSelfValidationProvider(ks.getKeystore(), true, certStore.getStore());
    File libSmartcard = new File("/opt/smartcard-aruba/libbit4xpki.dylib");
    engine = new XadesSignatureEngine(libSmartcard);

    try
    {
//      System.out.println("====== FIRMA BES =============================================================");
//      testFirmaBES(args);
//      System.out.println("====== VERIFICA BES ==========================================================");
//      testVerificaBES(args);
//
//      System.out.println("====== FIRMA C ===============================================================");
//      testFirmaC(args);
//      System.out.println("====== FIRMA CDA2 ============================================================");
//      testFirmaCda(args);

      System.out.println("====== FIRMA CDA2 SARDEGNA ===================================================");
      testFirmaCdaSardegna(args);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private static void testFirmaBES(String[] args)
     throws Exception
  {
    File input = new File("/tmp/xades-originale.xml");
    File output = new File("/tmp/xades-firmato-bes.xml");

    // qui viene passato il PIN
    engine.prepareSmartCardAndData(pin);

    engine.signBes(input, output, true);
  }

  private static void testVerificaBES(String[] args)
     throws Exception
  {
    File output = new File("/tmp/xades-firmato-bes.xml");

    // qui viene passato il PIN
    engine.prepareSmartCardAndData(pin);

    engine.verifyBes(output);
  }

  private static void testFirmaC(String[] args)
     throws Exception
  {
    File input = new File("/tmp/xades-originale.xml");
    File output = new File("/tmp/xades-firmato-c.xml");

    // qui viene passato il PIN
    engine.prepareSmartCardAndData(pin);

    engine.signC(input, output, aa);
  }

  private static void testFirmaCda(String[] args)
     throws Exception
  {
    File input = new File("/tmp/xades-originale.xml");
    File output = new File("/tmp/xades-firmato-cda2.xml");

    // qui viene passato il PIN
    engine.prepareSmartCardAndData(pin);

    engine.signCda(input, output, aa);
  }

  private static void testFirmaCdaSardegna(String[] args)
     throws Exception
  {
    File input = new File("/tmp/xades-originale.xml");
    File output = new File("/tmp/xades-firmato-cda2-sardegna.xml");

    // qui viene passato il PIN
    engine.prepareSmartCardAndData(pin);

    engine.signCdaSardegna(input, output);
  }
}
