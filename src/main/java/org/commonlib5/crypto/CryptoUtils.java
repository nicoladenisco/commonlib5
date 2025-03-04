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
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import org.commonlib5.utils.LongOperListener;

/**
 * Utilità per la crittografia.
 *
 * Richiede l'uso di Bouncy Castle.
 *
 * @author Nicola De Nisco
 */
public class CryptoUtils
{
  public static final int BUFFER_SIZE = 4096;
  public static final String BLOWFISH = "Blowfish/CFB/NoPadding";
  public static final String PKCS11_RSA = "RSA/ECB/PKCS1Padding";
  public static final String SUN_PKCS11_PROVIDER_NAME = "SunPKCS11-SmartCard";

  /**
   * Encrypt the plain text with private key.
   * @return
   */
  public static byte[] privateEncrypt(byte[] text, PrivateKey priKey)
     throws Exception
  {
    Cipher cipher = Cipher.getInstance(PKCS11_RSA);
    cipher.init(Cipher.ENCRYPT_MODE, priKey);
    return cipher.doFinal(text);
  }

  /**
   * Encrypt the plain text with public key.
   * @return
   */
  public static byte[] publicEncrypt(byte[] text, PublicKey pubKey)
     throws Exception
  {
    Cipher cipher = Cipher.getInstance(PKCS11_RSA);
    cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    return cipher.doFinal(text);
  }

  /**
   * Decrypt the cipher text with private key.
   * @return
   */
  public static byte[] privateDecrypt(byte[] text, PrivateKey priKey)
     throws Exception
  {
    Cipher cipher = Cipher.getInstance(PKCS11_RSA);
    cipher.init(Cipher.DECRYPT_MODE, priKey);
    return cipher.doFinal(text);
  }

  /**
   * Decrypt the cipher text with public key.
   * @param text
   * @param pubKey
   * @return
   * @throws Exception
   */
  public static byte[] publicDecrypt(byte[] text, PublicKey pubKey)
     throws Exception
  {
    Cipher cipher = Cipher.getInstance(PKCS11_RSA);
    cipher.init(Cipher.DECRYPT_MODE, pubKey);
    return cipher.doFinal(text);
  }

  public static void encryptBlowfish(File sourceFile, File encryptedFile, Key secretKey, LongOperListener lol)
     throws Exception
  {
    try (InputStream is = new FileInputStream(sourceFile);
       OutputStream os = new FileOutputStream(encryptedFile))
    {
      encryptBlowfish(is, os, secretKey, lol, sourceFile.length());
    }
  }

  public static void encryptBlowfish(InputStream in, OutputStream out, Key secretKey, LongOperListener lol, long total)
     throws Exception
  {
    Cipher cipherOut = Cipher.getInstance(BLOWFISH);
    cipherOut.init(Cipher.ENCRYPT_MODE, secretKey);
    byte iv[] = cipherOut.getIV();
    for(int i = 0; i < iv.length; i++)
      iv[i] = 0;
    cipherOut.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

    // bkSize è un numero intero di blocchi vicino alle dimensioni di BUFFER_SIZE
    int bkSize = (BUFFER_SIZE / cipherOut.getBlockSize()) * cipherOut.getBlockSize();
    streamChiper(in, out, cipherOut, bkSize, lol, total);
  }

  public static void decryptBlowfish(File encryptedFile, File outputFile, Key secretKey, LongOperListener lol)
     throws Exception
  {
    try (InputStream is = new FileInputStream(encryptedFile);
       OutputStream os = new FileOutputStream(outputFile))
    {
      decryptBlowfish(is, os, secretKey, lol, encryptedFile.length());
    }
  }

  public static void decryptBlowfish(InputStream in, OutputStream out, Key secretKey, LongOperListener lol, long total)
     throws Exception
  {
    Cipher cipherOut = Cipher.getInstance(BLOWFISH);
    cipherOut.init(Cipher.ENCRYPT_MODE, secretKey);
    byte iv[] = cipherOut.getIV();
    for(int i = 0; i < iv.length; i++)
      iv[i] = 0;
    cipherOut.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

    // bkSize è un numero intero di blocchi vicino alle dimensioni di BUFFER_SIZE
    int bkSize = (BUFFER_SIZE / cipherOut.getBlockSize()) * cipherOut.getBlockSize();
    streamChiper(in, out, cipherOut, bkSize, lol, total);
  }

  protected static void streamChiper(InputStream in, OutputStream out,
     Cipher cipherOut, int bkSize,
     LongOperListener lol, long total)
     throws IOException
  {
    try (CipherOutputStream cout = new CipherOutputStream(out, cipherOut))
    {
      if(lol != null)
        lol.resetUI();

      int n;
      long count = 0;
      byte[] buffer = new byte[bkSize];
      while((n = in.read(buffer)) > 0)
      {
        cout.write(buffer, 0, n);
        count += n;

        if(lol != null)
          lol.updateUI(count, total);
      }

      if(lol != null)
        lol.completeUI(total);

      cout.flush();
    }
  }

  /**
   * Applica una crittografia RSA ai dati utilizzando la chiave
   * pubblica specificata.
   * @param certFile file con il certificato/chiave pubblica
   * @param sourceFile file con i dati in chiaro
   * @param encryptedFile fila con i dati crittografati
   * @param lol
   * @throws Exception
   */
  public static void encryptWithPublicCert(File certFile,
     File sourceFile, File encryptedFile, LongOperListener lol)
     throws Exception
  {
    X509Certificate cert = SignUtils.readX509CertFile(certFile);
    encryptWithPublicKey(cert.getPublicKey(), sourceFile, encryptedFile, lol);
  }

  /**
   * Applica una crittografia RSA ai dati utilizzando la chiave
   * pubblica specificata.
   * @param pk chiave pubblica per la crittografia
   * @param sourceFile file con i dati in chiaro
   * @param encryptedFile fila con i dati crittografati
   * @param lol
   * @throws Exception
   */
  public static void encryptWithPublicKey(PublicKey pk,
     File sourceFile, File encryptedFile, LongOperListener lol)
     throws Exception
  {
    try (InputStream is = new FileInputStream(sourceFile);
       OutputStream os = new FileOutputStream(encryptedFile))
    {
      encryptWithPublicKey(pk, is, os, lol, sourceFile.length());
    }
  }

  /**
   * Applica una crittografia RSA ai dati utilizzando la chiave
   * pubblica specificata.
   * @param pk chiave pubblica per la crittografia
   * @param in dati da crittografare
   * @param out dati crittografati
   * @param lol
   * @param total
   * @throws Exception
   */
  public static void encryptWithPublicKey(PublicKey pk,
     InputStream in, OutputStream out, LongOperListener lol, long total)
     throws Exception
  {
    Cipher rsa = Cipher.getInstance(PKCS11_RSA, SUN_PKCS11_PROVIDER_NAME);
    rsa.init(Cipher.ENCRYPT_MODE, pk);
    int bkSize = rsa.getBlockSize();

    try (OutputStream os = new CipherOutputStream(out, rsa))
    {
      if(lol != null)
        lol.resetUI();

      int n;
      long count = 0;
      byte[] buffer = new byte[bkSize];
      while((n = in.read(buffer)) > 0)
      {
        os.write(buffer, 0, n);
        count += n;

        if(lol != null)
          lol.updateUI(count, total);
      }

      if(lol != null)
        lol.completeUI(total);

      os.flush();
    }
  }

  /**
   * Decripta i dati ottenuti da encryptWithPublicKey utilizzando
   * la chiave privata specificata. Essendo l'algoritmo utilizato RSA
   * la chiave deve essere comlementare a quella utilizzata per la crittografia.
   * @param pk chive privata per la decriptazione
   * @param encryptedFile file con i dati crittografati in ingresso
   * @param outputFile file con i dati in chiaro
   * @param lol
   * @throws Exception
   */
  public static void decryptWithPrivateKey(PrivateKey pk,
     File encryptedFile, File outputFile, LongOperListener lol)
     throws Exception
  {
    try (InputStream is = new FileInputStream(encryptedFile);
       OutputStream os = new FileOutputStream(outputFile))
    {
      decryptWithPrivateKey(pk, is, os, lol, encryptedFile.length());
    }
  }

  /**
   * Decripta i dati ottenuti da encryptWithPublicKey utilizzando
   * la chiave privata specificata. Essendo l'algoritmo utilizato RSA
   * la chiave deve essere comlementare a quella utilizzata per la crittografia.
   * @param pk chive privata per la decriptazione
   * @param in dati crittografati in ingresso
   * @param out dati in chiaro in uscita
   * @param lol
   * @param total
   * @throws Exception
   */
  public static void decryptWithPrivateKey(PrivateKey pk,
     InputStream in, OutputStream out, LongOperListener lol, long total)
     throws Exception
  {
    Cipher rsa = Cipher.getInstance(PKCS11_RSA, SUN_PKCS11_PROVIDER_NAME);
    rsa.init(Cipher.DECRYPT_MODE, pk);
    streamChiper(in, out, rsa, rsa.getBlockSize(), lol, total);
  }
}
