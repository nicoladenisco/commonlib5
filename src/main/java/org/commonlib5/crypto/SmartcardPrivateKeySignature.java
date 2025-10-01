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

import com.itextpdf.text.pdf.security.ExternalSignature;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * Implementa servizio di firma compatibile con Java 11 e successive.
 * Attenzione l'algoritmo usato è sempre SHA256withRSA.
 *
 * @author Nicola De Nisco
 */
public class SmartcardPrivateKeySignature implements ExternalSignature
{
  /** The protected key object. */
  protected PrivateKey pk;
  /** The hash algorithm. */
  protected String hashAlgorithm;
  /** The encryption algorithm (obtained from the protected key) */
  protected String encryptionAlgorithm;
  /** The security provider */
  protected String provider;

  /**
   * Creates an ExternalSignature instance
   * @param pk	a PrivateKey object
   * @param hashAlgorithm	the hash algorithm (e.g. "SHA-1", "SHA-256",...)
   * @param provider	the security provider (e.g. "BC")
   */
  public SmartcardPrivateKeySignature(PrivateKey pk, String hashAlgorithm, String provider)
  {
    this.pk = pk;
    this.provider = provider;
    this.hashAlgorithm = hashAlgorithm;
    encryptionAlgorithm = pk.getAlgorithm();
    if(encryptionAlgorithm.startsWith("EC"))
      encryptionAlgorithm = "ECDSA";
  }

  /**
   * Returns the hash algorithm.
   * @return	the hash algorithm (e.g. "SHA-1", "SHA-256,...")
   * @see com.itextpdf.text.pdf.security.ExternalSignature#getHashAlgorithm()
   */
  @Override
  public String getHashAlgorithm()
  {
    return hashAlgorithm;
  }

  /**
   * Returns the encryption algorithm used for signing.
   * @return the encryption algorithm ("RSA" or "DSA")
   * @see com.itextpdf.text.pdf.security.ExternalSignature#getEncryptionAlgorithm()
   */
  @Override
  public String getEncryptionAlgorithm()
  {
    return encryptionAlgorithm;
  }

  /**
   * Calcola la firma per i byte indicati.
   * Attenzione l'algoritmo usato è sempre SHA256withRSA
   * @param b the message you want to be hashed and signed
   * @return a signed message digest
   * @throws GeneralSecurityException
   */
  @Override
  public byte[] sign(byte[] b)
     throws GeneralSecurityException
  {
    Signature javasign = Signature.getInstance("SHA256withRSA");
    javasign.initSign(pk);
    javasign.update(b);
    return javasign.sign();
  }
}
