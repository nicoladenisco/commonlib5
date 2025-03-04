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
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.jce.provider.*;

/**
 * Utility class that helps encrypt and decrypt strings using RSA algorithm.
 *
 * @author Aviran Mordo http://aviran.mordos.com
 * @version 1.0
 */
public class RSAEncryptUtils
{
  public static final String RSA_CHIPER = "RSA/ECB/PKCS1Padding";
  public static final String ALGORITHM = "RSA";
  public static final String BOUNCY_CASTLE = "BC";

  static
  {
    if(Security.getProvider(BOUNCY_CASTLE) == null)
      Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Generate key which contains a pair of private and public key using 1024 bytes
   * @return key pair
   * @throws NoSuchAlgorithmException
   */
  public static KeyPair generateKey()
     throws NoSuchAlgorithmException
  {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
    keyGen.initialize(1024);
    return keyGen.generateKeyPair();
  }

  /**
   * Encrypt a text using public key.
   * @param text The original unencrypted text
   * @param key The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
  public static byte[] encrypt(byte[] text, Key key)
     throws Exception
  {
    // get an RSA cipher object and print the provider
    Cipher cipher = Cipher.getInstance(RSA_CHIPER, BOUNCY_CASTLE);

    // encrypt the plaintext using the public key
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(text);
  }

  /**
   * Encrypt a text using public key.
   * The result is enctypted BASE64 encoded text.
   * Use UTF8 encoding.
   * @param text The original unencrypted text
   * @param key The public key
   * @return Encrypted text encoded as BASE64
   * @throws java.lang.Exception
   */
  public static String encrypt(String text, Key key)
     throws Exception
  {
    byte[] cipherText = encrypt(text.getBytes("UTF8"), key);
    return encodeBASE64(cipherText);
  }

  /**
   * Encrypt a text using public key.
   * The result is enctypted BASE64 encoded text.
   * Use UTF8 encoding.
   * @param text The original unencrypted text
   * @param key The public key
   * @param isChunked
   * @return Encrypted text encoded as BASE64
   * @throws java.lang.Exception
   */
  public static String encrypt(String text, Key key, boolean isChunked)
     throws Exception
  {
    byte[] cipherText = encrypt(text.getBytes("UTF8"), key);
    return encodeBASE64(cipherText, isChunked);
  }

  /**
   * Decrypt text using private key
   * @param text The encrypted text
   * @param key The private key
   * @return The unencrypted text
   * @throws java.lang.Exception
   */
  public static byte[] decrypt(byte[] text, Key key)
     throws Exception
  {
    // decrypt the text using the private key
    Cipher cipher = Cipher.getInstance(RSA_CHIPER, BOUNCY_CASTLE);
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(text);
  }

  /**
   * Decrypt BASE64 encoded text using private key
   * @param text The encrypted text, encoded as BASE64
   * @param key The private key
   * @return The unencrypted text encoded as UTF8
   * @throws java.lang.Exception
   */
  public static String decrypt(String text, Key key)
     throws Exception
  {
    // decrypt the text using the private key
    byte[] dectyptedText = decrypt(decodeBASE64(text), key);
    return new String(dectyptedText, "UTF8");
  }

  /**
   * Convert a Key to string encoded as BASE64
   * @param key The key (private or public)
   * @return A string representation of the key
   */
  public static String getKeyAsString(Key key)
  {
    // Get the bytes of the key
    byte[] keyBytes = key.getEncoded();
    return encodeBASE64(keyBytes);
  }

  /**
   * Generates Private Key from BASE64 encoded string
   * @param key BASE64 encoded string which represents the key
   * @return The PrivateKey
   * @throws java.lang.Exception
   */
  public static PrivateKey getPrivateKeyFromString(String key)
     throws Exception
  {
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeBASE64(key));
    PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
    return privateKey;
  }

  /**
   * Generates Public Key from BASE64 encoded string
   * @param key BASE64 encoded string which represents the key
   * @return The PublicKey
   * @throws java.lang.Exception
   */
  public static PublicKey getPublicKeyFromString(String key)
     throws Exception
  {
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodeBASE64(key));
    PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
    return publicKey;
  }

  /**
   * Encode bytes array to BASE64 string
   * @param bytes
   * @return Encoded string
   */
  public static String encodeBASE64(byte[] bytes)
  {
    return Base64.encodeBase64String(bytes);
  }

  /**
   * Encode bytes array to BASE64 string
   * @param bytes
   * @param isChunked
   * @return Encoded string
   */
  public static String encodeBASE64(byte[] bytes, boolean isChunked)
  {
    return StringUtils.newStringUtf8(Base64.encodeBase64(bytes, isChunked));
  }

  /**
   * Decode BASE64 encoded string to bytes array
   * @param text The string
   * @return Bytes array
   * @throws IOException
   */
  public static byte[] decodeBASE64(String text)
     throws IOException
  {
    return Base64.decodeBase64(text);
  }

  /**
   * Encrypt file using RSA encryption.
   *
   * @param src Source file
   * @param dest Destination file
   * @param key The key. For encryption this is the Private Key and for decryption this is the public key
   * @throws Exception
   */
  public static void encryptFile(File src, File dest, Key key)
     throws Exception
  {
    try (InputStream is = new FileInputStream(src); OutputStream os = new FileOutputStream(dest))
    {
      encryptDecryptFile(is, os, key, Cipher.ENCRYPT_MODE);
    }
  }

  /**
   * Decrypt file using RSA encryption.
   *
   * @param src Source file
   * @param dest Destination file
   * @param key The key. For encryption this is the Private Key and for decryption this is the public key
   * @throws Exception
   */
  public static void decryptFile(File src, File dest, Key key)
     throws Exception
  {
    try (InputStream is = new FileInputStream(src); OutputStream os = new FileOutputStream(dest))
    {
      encryptDecryptFile(is, os, key, Cipher.DECRYPT_MODE);
    }
  }

  /**
   * Encrypt and Decrypt stream using RSA encryption.
   * Vengono letti blocchi dallo stream compatibili con la chiave
   * ed effettuata l'operazione su ogni blocco letto.
   *
   * @param is Source stream
   * @param os Destination stream
   * @param key The key. For encryption this is the Private Key and for decryption this is the public key
   * @param cipherMode Cipher Mode (Cipher.ENCRYPT_MODE/Cipher.DECRYPT_MODE)
   * @return numero di blocchi generati
   * @throws Exception
   */
  public static int encryptDecryptFile(InputStream is, OutputStream os, Key key, int cipherMode)
     throws Exception
  {
    int numBlocks = 0;
    Cipher cipher = Cipher.getInstance(RSA_CHIPER, BOUNCY_CASTLE);
    cipher.init(cipherMode, key);
    byte[] buf = new byte[cipher.getBlockSize()];

    int nb;
    while((nb = is.read(buf)) > 0)
    {
      byte[] encText = cipher.doFinal(buf, 0, nb);
      os.write(encText);
      numBlocks++;
    }

    return numBlocks;
  }
}
