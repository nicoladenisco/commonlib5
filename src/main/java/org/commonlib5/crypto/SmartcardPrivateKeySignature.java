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
import java.security.Provider;
import java.security.Signature;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonlib5.utils.ClassificatoreOrdinato;

/**
 * Implementa servizio di firma compatibile con Java 11 e successive.
 *
 * @author Nicola De Nisco
 */
public class SmartcardPrivateKeySignature implements ExternalSignature
{
  private static final Log log = LogFactory.getLog(SmartcardPrivateKeySignature.class);

  /** The protected key object. */
  protected PrivateKey pk;
  /** The hash algorithm. */
  protected String hashAlgorithm;
  /** The encryption algorithm (obtained from the protected key) */
  protected String encryptionAlgorithm;
  /** The security provider */
  protected String provider;
  /** il tipo di firma */
  protected String tipoFirma;

  /**
   * Creates an ExternalSignature instance
   * @param pk	a PrivateKey object
   * @param hashAlgorithm	the hash algorithm (e.g. "SHA-1", "SHA-256",...)
   * @param provider	the security provider (e.g. "BC") if null try to load auto
   */
  public SmartcardPrivateKeySignature(PrivateKey pk, String hashAlgorithm, String provider)
  {
    this.pk = pk;
    this.provider = provider;
    this.hashAlgorithm = hashAlgorithm;
    this.encryptionAlgorithm = pk.getAlgorithm();

    if(encryptionAlgorithm.startsWith("EC"))
      encryptionAlgorithm = "ECDSA";

    this.tipoFirma = this.hashAlgorithm.replace("-", "") + "with" + encryptionAlgorithm;

    if(this.provider == null)
      loadProviderAuto();
  }

  /**
   * Determinazione automatica del provider per l'implementazione della firma.
   * Fra i vari provider installati nella JVM cerca quello più adatto
   * all'algoritmo di firma che si sta utilizzando.
   */
  protected void loadProviderAuto()
  {
    String search = "Signature." + tipoFirma;
    ClassificatoreOrdinato<String, Provider> clprov = SignUtils.getSignProviders();
    List<Provider> lsProv = clprov.get(search);
    if(lsProv == null)
    {
      log.info("Nessun provider di firma per " + tipoFirma);
      return;
    }

    this.provider = lsProv.get(0).getName();

    if(lsProv.size() > 1)
    {
      String keyDesk = pk.toString();
      for(Provider pt : lsProv)
      {
        String pname = pt.getName();

        // in genere nella descrizione della chiave viene inserito il nome del provider da cui dipende
        // la ricerca non è esaustiva ma rappresenta un buon default
        if(keyDesk.startsWith(pname))
        {
          this.provider = pname;
          log.info("Provider di firma ambiguo per " + tipoFirma + "; trovati " + lsProv + "; per default uso " + pname);
          return;
        }
      }

      log.info("Provider di firma ambiguo per " + tipoFirma + "; trovati " + lsProv + "; per default uso il primo.");
    }
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
   * Ritorna il tipo firma.
   * Il valore è una combinazione di algoritmo di hash e algoritmo di crittografia.
   * @return qualcosa del tipo SHA256withRSA
   */
  public String getTipoFirma()
  {
    return tipoFirma;
  }

  /**
   * Calcola la firma per i byte indicati.
   * @param b the message you want to be hashed and signed
   * @return a signed message digest
   * @throws GeneralSecurityException
   */
  @Override
  public byte[] sign(byte[] b)
     throws GeneralSecurityException
  {
    Signature javasign = Signature.getInstance(tipoFirma, provider);
    javasign.initSign(pk);
    javasign.update(b);

    return javasign.sign();
  }
}
