/*
 * Copyright (C) 2026 Nicola De Nisco
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

import java.security.Provider;
import java.util.List;
import java.util.Map;
import org.commonlib5.utils.ClassificatoreOrdinato;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nicola De Nisco
 */
public class SignUtilsTest
{

  public SignUtilsTest()
  {
  }

  @BeforeClass
  public static void setUpClass()
  {
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp()
  {
  }

  @After
  public void tearDown()
  {
  }

//  @Test
//  public void testCreateCerChain()
//     throws Exception
//  {
//    System.out.println("createCerChain");
//    Certificate[] certChain = null;
//    String expResult = "";
//    String result = SignUtils.createCerChain(certChain);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testEncodeX509CertChainToBase64()
//     throws Exception
//  {
//    System.out.println("encodeX509CertChainToBase64");
//    Certificate[] certChain = null;
//    String expResult = "";
//    String result = SignUtils.encodeX509CertChainToBase64(certChain);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testCreateCERbase64()
//     throws Exception
//  {
//    System.out.println("createCERbase64");
//    X509Certificate xCert = null;
//    String expResult = "";
//    String result = SignUtils.createCERbase64(xCert);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReadX509CertFile()
//     throws Exception
//  {
//    System.out.println("readX509CertFile");
//    File file = null;
//    X509Certificate expResult = null;
//    X509Certificate result = SignUtils.readX509CertFile(file);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReadX509CertBuffer()
//     throws Exception
//  {
//    System.out.println("readX509CertBuffer");
//    byte[] data = null;
//    X509Certificate expResult = null;
//    X509Certificate result = SignUtils.readX509CertBuffer(data);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReadX509CertStream()
//     throws Exception
//  {
//    System.out.println("readX509CertStream");
//    InputStream is = null;
//    X509Certificate expResult = null;
//    X509Certificate result = SignUtils.readX509CertStream(is);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReadX509CertString()
//     throws Exception
//  {
//    System.out.println("readX509CertString");
//    String certString = "";
//    X509Certificate expResult = null;
//    X509Certificate result = SignUtils.readX509CertString(certString);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReadFileFirma()
//     throws Exception
//  {
//    System.out.println("readFileFirma");
//    File fileFirma = null;
//    byte[] expResult = null;
//    byte[] result = SignUtils.readFileFirma(fileFirma);
//    assertArrayEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testVerify_3args_1()
//     throws Exception
//  {
//    System.out.println("verify");
//    File certFile = null;
//    File signFile = null;
//    File fileToVerify = null;
//    boolean expResult = false;
//    boolean result = SignUtils.verify(certFile, signFile, fileToVerify);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testVerify_3args_2()
//     throws Exception
//  {
//    System.out.println("verify");
//    String certString = "";
//    String signString = "";
//    byte[] dataToVerify = null;
//    boolean expResult = false;
//    boolean result = SignUtils.verify(certString, signString, dataToVerify);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testVerify_3args_3()
//     throws Exception
//  {
//    System.out.println("verify");
//    PublicKey pubKey = null;
//    byte[] signature = null;
//    InputStream dataToVerify = null;
//    boolean expResult = false;
//    boolean result = SignUtils.verify(pubKey, signature, dataToVerify);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testGetCert()
//     throws Exception
//  {
//    System.out.println("getCert");
//    Certificate[] aCertificationChain = null;
//    X509Certificate expResult = null;
//    X509Certificate result = SignUtils.getCert(aCertificationChain);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testSignDocument_byteArr_PrivateKey()
//     throws Exception
//  {
//    System.out.println("signDocument");
//    byte[] aDocument = null;
//    PrivateKey aPrivateKey = null;
//    byte[] expResult = null;
//    byte[] result = SignUtils.signDocument(aDocument, aPrivateKey);
//    assertArrayEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testSignDocument_InputStream_PrivateKey()
//     throws Exception
//  {
//    System.out.println("signDocument");
//    InputStream streamDocument = null;
//    PrivateKey aPrivateKey = null;
//    byte[] expResult = null;
//    byte[] result = SignUtils.signDocument(streamDocument, aPrivateKey);
//    assertArrayEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testExtractDocument_File_File()
//     throws Exception
//  {
//    System.out.println("extractDocument");
//    File p7m = null;
//    File output = null;
//    SignUtils.extractDocument(p7m, output);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testExtractDocument_File()
//     throws Exception
//  {
//    System.out.println("extractDocument");
//    File p7m = null;
//    byte[] expResult = null;
//    byte[] result = SignUtils.extractDocument(p7m);
//    assertArrayEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testIsSmartcardPlugged()
//     throws Exception
//  {
//    System.out.println("isSmartcardPlugged");
//    boolean expResult = false;
//    boolean result = SignUtils.isSmartcardPlugged();
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testTryLoadCertificate()
//  {
//    System.out.println("tryLoadCertificate");
//    File f = null;
//    Collection contentList = null;
//    CertificateFactory cf = null;
//    boolean expResult = false;
//    boolean result = SignUtils.tryLoadCertificate(f, contentList, cf);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testTryLoadCrl()
//  {
//    System.out.println("tryLoadCrl");
//    File f = null;
//    Collection contentList = null;
//    CertificateFactory cf = null;
//    boolean expResult = false;
//    boolean result = SignUtils.tryLoadCrl(f, contentList, cf);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testGetUserCertificateSubjectFields()
//     throws Exception
//  {
//    System.out.println("getUserCertificateSubjectFields");
//    X509Certificate cert = null;
//    Map<String, String> expResult = null;
//    Map<String, String> result = SignUtils.getUserCertificateSubjectFields(cert);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
  @Test
  public void testGetSignProviders()
  {
    System.out.println("getSignProviders");
    ClassificatoreOrdinato<String, Provider> result = SignUtils.getSignProviders();

    for(Map.Entry<String, List<Provider>> entry : result.entrySet())
    {
      String key = entry.getKey();
      List<Provider> val = entry.getValue();

      System.out.println("=== " + key);
      System.out.println(" " + val);
    }
  }
}
