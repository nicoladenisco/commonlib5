/*
 * SignatureEngine.java
 *
 * Created on 22 ottobre 2008, 13.24
 *
 * Copyright (C) 2011 Nicola De Nisco
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.*;
import org.apache.commons.codec.binary.Base64;

/**
 * Motore di firma digitale.
 * Questa classa viene inizializzata con un file che punta alla DLL che
 * contiene l'implementazione del layer PKCS11 per la smartcard che si
 * sta utilizzando.
 * Il metodo singDocument() calcola l'hash e applica la firma utilizzando
 * la smartcard.
 *
 * @author Nicola De Nisco
 */
public class SignatureEngine extends CryptoBaseEngine
{
  protected byte[] digitalSignature = null;
  protected String mSignature = null;

  /** Creates a new instance of SignatureEngine
   * @param libraryFile */
  public SignatureEngine(File libraryFile)
  {
    super(libraryFile);
  }

  public SignatureEngine(SmartcardDataProvider kdp)
  {
    super(kdp);
  }

  /**
   * Firma il documento. Usa i dati estratti dalla smartcard
   * (con il metodo prepareSmartCardAndData) per firmare il documento
   * sottoposto.
   * @param aDocument documento sotto forma di byte
   * @throws DocumentSignException
   */
  public void signDocument(byte[] aDocument)
     throws DocumentSignException
  {
    signDocument(new ByteArrayInputStream(aDocument));
  }

  /**
   * Firma il documento. Usa i dati estratti dalla smartcard
   * (con il metodo prepareSmartCardAndData) per firmare il documento
   * sottoposto.
   * @param fileDocument file su disco da firmare
   * @throws DocumentSignException
   * @throws java.io.FileNotFoundException
   */
  public void signDocument(File fileDocument)
     throws DocumentSignException, FileNotFoundException
  {
    signDocument(new FileInputStream(fileDocument));
  }

  /**
   * Firma il documento. Usa i dati estratti dalla smartcard
   * (con il metodo prepareSmartCardAndData) per firmare il documento
   * sottoposto.
   * Lo stream dei dati viene chiuso all'uscita, anche in
   * caso di errore (eccezione sollevata).
   * @param is Stream di dati da firmare
   * @throws DocumentSignException
   */
  public void signDocument(InputStream is)
     throws DocumentSignException
  {
    // pulisce i dati
    digitalSignature = null;
    mSignature = null;

    // Calculate the digital signature of the file,
    // encode it in Base64 and save it in the result
    try
    {
      try
      {
        digitalSignature = SignUtils.signDocument(is, kdp.getPrivateKey());
      }
      finally
      {
        is.close();
      }

      mSignature = new String(Base64.encodeBase64(digitalSignature, true));
    }
    catch(GeneralSecurityException gsex)
    {
      String errorMessage = "File signing failed.\n"
         + "Problem details: " + gsex.getMessage();
      throw new DocumentSignException(errorMessage, gsex);
    }
    catch(Exception gsex)
    {
      String errorMessage = "File signing failed.\n"
         + "Problem details: " + gsex.getMessage();
      throw new DocumentSignException(errorMessage, gsex);
    }
  }

  /**
   * Verifica il documento con i dati attualmente
   * in memoria (chiave utente e firma calcolata).
   * @param aDocument i dati del documento
   * @return vero se la verifica è corretta
   * @throws Exception
   */
  public boolean verify(byte[] aDocument)
     throws Exception
  {
    return verify(new ByteArrayInputStream(aDocument));
  }

  /**
   * Verifica il documento con i dati attualmente
   * in memoria (chiave utente e firma calcolata).
   * @param fileDocument il file documento
   * @return vero se la verifica è corretta
   * @throws Exception
   */
  public boolean verify(File fileDocument)
     throws Exception
  {
    try (InputStream is = new FileInputStream(fileDocument))
    {
      return verify(is);
    }
  }

  /**
   * Verifica il documento con i dati attualmente
   * in memoria (chiave utente e firma calcolata).
   * @param is lo stream dei dati in input; lo stream
   * viene letto fino alla fine e chiuso dopo l'elaborazione.
   * @return vero se la verifica è corretta
   * @throws Exception
   */
  public boolean verify(InputStream is)
     throws Exception
  {
    // import encoded public key
    PublicKey pubKey = kdp.getUserCertificate().getPublicKey();
    return SignUtils.verify(pubKey, digitalSignature, is);
  }

  /**
   * Ritorna firma digitale sotto forma di byte.
   * @return array di bytes con la firma digitale
   */
  public byte[] getDigitalSignature()
  {
    return digitalSignature;
  }

  /**
   * Imposta firma digitale sotto forma di byte.
   * @param data array di bytes con la firma digitale
   */
  public void setDigitalSignature(byte[] data)
  {
    digitalSignature = new byte[data.length];
    System.arraycopy(data, 0, digitalSignature, 0, data.length);
    mSignature = new String(Base64.encodeBase64(digitalSignature, true));
  }

  /**
   * Ritorna firma digitale sotto forma di stringa base 64.
   * @return stringa base64 con la firma digitale
   */
  public String getSignatureAsString()
  {
    return mSignature;
  }

  /**
   * Imposta firma digitale sotto forma di stringa base 64.
   * @param data stringa base64 con la firma digitale
   */
  public void setSignatureAsString(String data)
  {
    mSignature = data;
    digitalSignature = Base64.decodeBase64(mSignature.getBytes());
  }
}
