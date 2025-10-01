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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Questo motore di firma produce files PDF conformi
 * alla direttiva PADES valida in Italia.
 *
 * Il 17 agosto 2010 viene pubblicata in Gazzetta Ufficiale la
 * Determinazione Commissariale n.69/2010 DigitaPA che fissava
 * il 30/06/2011 quale data ultima per l’adeguamento dei formati
 * di firma alle specifiche CAdES.
 *
 * CMS Advanced Electronic Signatures è un set di estensioni della
 * specifica Cryptographic Message Syntax per la realizzazione di
 * firme digitali avanzate particolarmente sicure e, soprattutto,
 * conformi con la Direttiva Europea 1999/93/EC.
 *
 * Questa classe richiede le librerie Bouncy Castle.
 *
 * @author Nicola De Nisco
 */
public class PadesSignatureEngine extends SignatureEngine
{
  /** Lista certificati di revoca scaricati da Internet. */
  protected ArrayList<CrlClient> crlList = null;
  /** Parametri opzionali per la visualizzazione firma nel PDF. */
  protected PdfSignatureAppearanceInfo appearanceInfo;
  /** Flag per abilitare l'uso di Internet durante la firma. */
  protected boolean useInternetCrl = false;
  /** Istanza del recuperatore di timestamp */
  protected TSAClient tsaClient = null;

  /**
   * Attributi per la visualizzazione informazioni del firmatario.
   * E' previsto che nel PDF possa essere visualizzato un riquadro
   * con le informazioni di firma. Queste vengono passate usando
   * una istanza di questa classe di servizio.
   * I campi reason e location sono obbligatori e diversi da null.
   * Se uno dei due è null, solo il flag certificationLevel viene
   * considerato.
   * Il campo fieldName è il nome di un eventuale campo predefinito
   * nella struttura del PDF che si sta firmando; questo campo
   * riceverà il riquadro di firma.
   * Se fieldName è null vengono presi in considerazione position
   * (ovvero posizione riquadro sulla pagina) e page (numero di
   * pagina su cui applicare il riquadro base 1).
   */
  public static class PdfSignatureAppearanceInfo
  {
    public String infoName;
    public String reason, location, fieldName;
    public Rectangle position;
    public int page, certificationLevel = PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED;
  }

  /**
   * Costruttore.
   * Inizializza il motore di firma.
   * @param libraryFile libreria nativa PKCS11
   */
  public PadesSignatureEngine(File libraryFile)
  {
    super(libraryFile);
    if(Security.getProvider("BC") == null)
      Security.addProvider(new BouncyCastleProvider());
  }

  public PadesSignatureEngine(SmartcardDataProvider kdp)
  {
    super(kdp);
    if(Security.getProvider("BC") == null)
      Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Firma un documento PDF.
   * Può applicare più firme sullo stesso documento.
   * Per poter applicare più firme è necessario non utilizzare
   * le appearanceInfo altrimenti il loro inserimento
   * viene considerato nelle firme successive una alterazione
   * del documento.
   * @param fileInput file originale
   * @param fileFirmato file di output con firma apposta
   * @throws Exception
   */
  public void signDocument(File fileInput, File fileFirmato)
     throws Exception
  {
    signDocument(fileInput, fileFirmato, kdp.certificationChain, kdp.privateKey,
       DigestAlgorithms.SHA256, CryptoStandard.CADES, appearanceInfo, tsaClient);
  }

  /**
   * Firma un documento PDF e applica un Timestamp alla firma.
   * Può applicare più firme sullo stesso documento.
   * Per poter applicare più firme è necessario non utilizzare
   * le appearanceInfo altrimenti il loro inserimento
   * viene considerato nelle firme successive una alterazione
   * del documento.
   * @param fileInput file originale
   * @param fileFirmato file di output con firma apposta
   * @param chain catena di certificati (del firmatario)
   * @param pk chiave privata del firmatario
   * @param digestAlgorithm algoritmo per il calcolo del digest
   * @param subfilter modello di output del file
   * @param ainfo informazioni per la visualizzazione
   * @param tsaClient client per accesso al server erogatore timestamp
   * @throws GeneralSecurityException
   * @throws IOException
   * @throws DocumentException
   */
  public void signDocument(File fileInput, File fileFirmato,
     Certificate[] chain, PrivateKey pk, String digestAlgorithm,
     CryptoStandard subfilter, PdfSignatureAppearanceInfo ainfo, TSAClient tsaClient)
     throws GeneralSecurityException, IOException, DocumentException
  {
    // Creating the reader and the stamper
    PdfReader reader = new PdfReader(fileInput.getAbsolutePath());
    PdfStamper stamper = PdfStamper.createSignature(reader, null, '\0', fileFirmato, true);

    // Creating the appearance
    PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
    if(ainfo != null)
    {
      if(ainfo.reason != null && ainfo.location != null)
      {
        appearance.setReason(ainfo.reason);
        appearance.setLocation(ainfo.location);

        if(ainfo.fieldName == null)
        {
          if(ainfo.infoName != null)
            appearance.setVisibleSignature(ainfo.position, ainfo.page, ainfo.infoName);
        }
        else
        {
          appearance.setVisibleSignature(ainfo.fieldName);
        }
      }
      appearance.setCertificationLevel(ainfo.certificationLevel);
    }

    // revocation list e relativo agent (non strettamente necessari) richiede Internet
    OcspClient ocspClient = null;
    if(useInternetCrl)
    {
      ocspClient = new OcspClientBouncyCastle();
      if(crlList == null)
      {
        crlList = new ArrayList<>();
        crlList.add(new CrlClientOnline(chain));
      }
    }

    // Creating the javasign
    ExternalDigest digest = new BouncyCastleDigest();
    ExternalSignature signature = new SmartcardPrivateKeySignature(pk, digestAlgorithm, null);
    MakeSignature.signDetached(appearance, digest, signature, chain, crlList,
       ocspClient, tsaClient, 0, subfilter);
  }

  /**
   * Ritorna informazioni su server timestamp
   * dai certificati contenuti nella smartcard.
   * Se il fornitore di smartcard lo prevede,
   * può fornire il servizio di generazione del
   * timestamp; in tal caso nella smarcard stessa
   * è indicato il server di timestamp e le
   * credenziali per l'accesso.
   * @return client per la generazione timestamp o null
   */
  public TSAClient getTSAfromSmartcard()
  {
    for(int i = 0; i < kdp.certificationChain.length; i++)
    {
      X509Certificate cert = (X509Certificate) kdp.certificationChain[i];
      String tsaUrl = CertificateUtil.getTSAURL(cert);
      if(tsaUrl != null)
        return new TSAClientBouncyCastle(tsaUrl);
    }
    return null;
  }

  /**
   * Ritorna la revocation list prelevata da internet.
   * @return lista di crl client
   */
  public List<CrlClient> getCrlList()
  {
    return crlList;
  }

  /**
   * Impostazioni di visualizzazione della firma.
   * Se diverso da null sul PDF apparirà un riquadro
   * con indicato i dati della firma.
   * @return opzioni di visualizzazione firma.
   */
  public PdfSignatureAppearanceInfo getAppearanceInfo()
  {
    return appearanceInfo;
  }

  /**
   * Impostazioni di visualizzazione della firma.
   * Se diverso da null sul PDF apparirà un riquadro
   * con indicato i dati della firma.
   * @param appearanceInfo opzioni di visualizzazione firma.
   */
  public void setAppearanceInfo(PdfSignatureAppearanceInfo appearanceInfo)
  {
    this.appearanceInfo = appearanceInfo;
  }

  /**
   * Istanza del recupertore di timestamp.
   * @return istanza corrente
   */
  public TSAClient getTsaClient()
  {
    return tsaClient;
  }

  /**
   * Imposta il recuperatore di timestamp.
   * Esempio: setTsaClient(new TSAClientBouncyCastle(tsaUrl, tsaUser, tsaPass));
   * Esempio: setTsaClient(getTSAfromSmartcard());
   * @param tsaClient istanza da utilizzare
   */
  public void setTsaClient(TSAClient tsaClient)
  {
    this.tsaClient = tsaClient;
  }

  /**
   * Abilitazione uso di Internet.
   * Se attivo vengono prelevati dal server del
   * fornitore della smartcard i certificati di revoca
   * da aggiungere al PDF firmato. Viene anche inserito
   * nel PDF l'indicazione di verificare i CRL direttamente
   * dal server al momento della lettura del PDF.
   * @return vero per abilitare accessso a internet durante la firma
   */
  public boolean isUseInternetCrl()
  {
    return useInternetCrl;
  }

  /**
   * Abilitazione uso di Internet.
   * Se attivo vengono prelevati dal server del
   * fornitore della smartcard i certificati di revoca
   * da aggiungere al PDF firmato. Viene anche inserito
   * nel PDF l'indicazione di verificare i CRL direttamente
   * dal server al momento della lettura del PDF.
   * @param useInternet vero per abilitare accessso a internet durante la firma
   */
  public void setUseInternetCrl(boolean useInternet)
  {
    this.useInternetCrl = useInternet;
  }
}
