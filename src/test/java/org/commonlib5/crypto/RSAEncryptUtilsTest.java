/*
 * Copyright (C) 2023 Nicola De Nisco
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
import java.nio.file.Files;
import java.security.KeyPair;
import javax.crypto.Cipher;
import org.commonlib5.utils.CommonFileUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test
 * @author Nicola De Nisco
 */
public class RSAEncryptUtilsTest
{

  public RSAEncryptUtilsTest()
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

  @Test
  public void testEncryptDecriptByteArray()
     throws Exception
  {
    System.out.println("testEncryptDecriptByteArray");

    String s = "testo per il test";
    KeyPair kp = RSAEncryptUtils.generateKey();
    byte[] origin = s.getBytes();
    byte[] encrypted = RSAEncryptUtils.encrypt(origin, kp.getPublic());
    byte[] decrypted = RSAEncryptUtils.decrypt(encrypted, kp.getPrivate());

    assertArrayEquals(origin, decrypted);
  }

  @Test
  public void testEncryptDecriptString()
     throws Exception
  {
    System.out.println("testEncryptDecriptString");

    String s = "testo per il test";
    KeyPair kp = RSAEncryptUtils.generateKey();
    String encrypted = RSAEncryptUtils.encrypt(s, kp.getPublic());
    String decrypted = RSAEncryptUtils.decrypt(encrypted, kp.getPrivate());

    assertEquals(s, decrypted);
  }

  @Test
  public void testEncryptDecryptFile()
     throws Exception
  {
    System.out.println("testEncryptDecryptFile");
    KeyPair kp = RSAEncryptUtils.generateKey();
    File __inputFile = File.createTempFile("__inputFile", ".bin");
    File encryptFile = File.createTempFile("encryptFile", ".bin");
    File decryptFile = File.createTempFile("decryptFile", ".bin");

    writeRandom(__inputFile, 2345);

    try (FileInputStream is = new FileInputStream(__inputFile); FileOutputStream os = new FileOutputStream(encryptFile))
    {
      int result = RSAEncryptUtils.encryptDecryptFile(is, os, kp.getPublic(), Cipher.ENCRYPT_MODE);
      System.out.println("Generati " + result + " blocchi.");
    }

    try (FileInputStream is = new FileInputStream(encryptFile); FileOutputStream os = new FileOutputStream(decryptFile))
    {
      int result = RSAEncryptUtils.encryptDecryptFile(is, os, kp.getPrivate(), Cipher.DECRYPT_MODE);
      System.out.println("Generati " + result + " blocchi.");
    }

    byte[] inp = Files.readAllBytes(__inputFile.toPath());
    byte[] out = Files.readAllBytes(decryptFile.toPath());
    assertArrayEquals(inp, out);

    __inputFile.delete();
    encryptFile.delete();
    decryptFile.delete();
  }

  private void writeRandom(File f, int numBytes)
     throws Exception
  {
    try (FileInputStream is = new FileInputStream("/dev/random"); FileOutputStream os = new FileOutputStream(f))
    {
      CommonFileUtils.copyStream(is, os, numBytes);
    }
  }
}
