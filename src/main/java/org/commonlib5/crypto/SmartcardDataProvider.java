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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.commonlib5.utils.StringOper;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.SigningCertChainException;
import xades4j.providers.SigningKeyException;
import xades4j.verification.UnexpectedJCAException;

/**
 * Interfaccia verso il contenuto della smartcard.
 * Questa classe contiene le informazioni estratte dalla smartcard
 * e viene utilizzata da i vari engine per le operazioni di firma/crittografia.
 *
 * @author Nicola De Nisco
 */
public class SmartcardDataProvider implements KeyingDataProvider
{
  // costanti
  public static final String PKCS11_KEYSTORE_TYPE = "PKCS11";
  public static final String X509_CERTIFICATE_TYPE = "X.509";
  public static final String CERTIFICATION_CHAIN_ENCODING = "PkiPath";
  public static final String SUN_PKCS11_PROVIDER_CLASS = "sun.security.pkcs11.SunPKCS11";
  public static final String SUN_PKCS11_PROVIDER_NAME = "SunPKCS11-SmartCard";
  //
  // libreria nativa con l'implementazione PKCS11
  protected File libraryFile = null;
  //
  // risultati dell'elaborazione
  protected Provider pkcs11Provider = null;
  protected KeyStore userKeyStore = null;
  protected PrivateKey privateKey = null;
  protected java.security.cert.Certificate[] certificationChain = null;
  protected CertPath certPath = null;
  protected X509Certificate userCertificate = null;
  protected String mCertificationChain = null;
  protected String mUserCertificate = null;
  protected String currentAlias = null;
  protected final List<X509Certificate> originalChain = new ArrayList<>();

  /**
   * Costruttore di default.
   * @param libraryFile file libreria nativo (.dll,.dylib,.so) con
   * l'implementazione del layer PKCS11
   */
  public SmartcardDataProvider(File libraryFile)
  {
    this.libraryFile = libraryFile;
  }

  /**
   * Inizializza la smartcard.
   * La libreria nativa contentente l'implementazione PKCS11
   * viene caricata e le comunicazioni con la smartcard aperte
   * prelevando la catena di certificati e la chiave privata
   * corrispondente.
   * @param aPinCode pin per l'accesso alla smartcard
   * @throws DocumentSignException
   */
  public void prepareSmartCardAndData(char[] aPinCode)
     throws DocumentSignException
  {
    if(libraryFile == null || !libraryFile.exists())
    {
      String errorMessage = "It is mandatory to choose "
         + "a PCKS#11 native implementation library for "
         + "smart card (.dll or .so file)!";
      throw new DocumentSignException(errorMessage);
    }

    // Load the keystore from the smart card using the specified
    // PIN code
    try
    {
      userKeyStore = loadKeyStoreFromSmartCard(aPinCode);
    }
    catch(Exception ex)
    {
      String errorMessage = "Cannot read the keystore from "
         + "the smart card.\n"
         + "Possible reasons:\n"
         + " - The smart card reader in not connected.\n"
         + " - The smart card is not inserted.\n"
         + " - The PKCS#11 implementation library is invalid.\n"
         + " - The PIN for the smart card is incorrect.\n"
         + "Problem details: " + ex.getMessage();
      throw new DocumentSignException(errorMessage, ex);
    }

    // Get the private key and its certification chain from the keystore
    try
    {
      getPrivateKeyAndCertChain();
    }
    catch(GeneralSecurityException gsex)
    {
      String errorMessage = "Cannot extract the private key "
         + "and certificate from the smart card. Reason: "
         + gsex.getMessage();
      throw new DocumentSignException(errorMessage, gsex);
    }
  }

  /**
   * Inizializza i dati dell'engine dopo aver scelto
   * la chiave privata e la certificationChain.
   * Va chiamata quando si cambia la chiave privata
   * utilizzata per la firma.
   * @throws DocumentSignException
   */
  protected void prepareData()
     throws DocumentSignException
  {
    // Check if the private key is available
    if(privateKey == null)
    {
      String errorMessage = "Cannot find the private key on the smart card.";
      throw new DocumentSignException(errorMessage);
    }

    // Check if X.509 certification chain is available
    if(certificationChain == null)
    {
      String errorMessage = "Cannot find the certificate on the smart card.";
      throw new DocumentSignException(errorMessage);
    }

    // estrae la catena logica dei certificati
    try
    {
      List certList = Arrays.asList(certificationChain);
      CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
      certPath = certFactory.generateCertPath(certList);

      List lista = certPath.getCertificates();
      // --- il certificato dell'utente e' sempre il primo della catena.
      if(lista.size() > 0)
        userCertificate = (X509Certificate) lista.get(0);
    }
    catch(CertificateException ex)
    {
      String errorMessage = "Cannot reconstruct the certification path.";
      throw new DocumentSignException(errorMessage, ex);
    }

    // Save X.509 certification chain in the result encoded in Base64
    try
    {
      mCertificationChain = encodeX509CertChainToBase64();
      mUserCertificate = createCerChainBase64();
    }
    catch(Exception cee)
    {
      String errorMessage = "Invalid certificate on the smart card.";
      throw new DocumentSignException(errorMessage, cee);
    }
  }

  /**
   * Inizilizza la smartcard.
   * La libreria nativa contentente l'implementazione PKCS11
   * viene caricata e le comunicazioni con la smartcard aperte.
   * NON viene prelevato nessun certificato dalla smartcard ed
   * è compito del chiamante usare getUserKeyStore() per recuperarne
   * la lista e setUserCertificate() per impostare il certificato
   * da utilizzare per operazioni successive.
   * @param aPinCode pin per l'accesso alla smartcard
   * @throws DocumentSignException
   */
  public void prepareSmartCard(char[] aPinCode)
     throws DocumentSignException
  {
    if(libraryFile == null || !libraryFile.exists())
    {
      String errorMessage = "It is mandatory to choose "
         + "a PCKS#11 native implementation library for "
         + "smart card (.dll or .so file)!";
      throw new DocumentSignException(errorMessage);
    }

    // Load the keystore from the smart card using the specified
    // PIN code
    try
    {
      userKeyStore = loadKeyStoreFromSmartCard(aPinCode);
    }
    catch(Exception ex)
    {
      String errorMessage = "Cannot read the keystore from "
         + "the smart card.\n"
         + "Possible reasons:\n"
         + " - The smart card reader in not connected.\n"
         + " - The smart card is not inserted.\n"
         + " - The PKCS#11 implementation library is invalid.\n"
         + " - The PIN for the smart card is incorrect.\n"
         + "Problem details: " + ex.getMessage();
      throw new DocumentSignException(errorMessage, ex);
    }
  }

  /**
   * Ritorna una stringa con encoding base64 con tutta la catena
   * di certificati estratti dalla smartcard.
   * @return certificati in base64
   * @throws Exception
   */
  protected String createCerChainBase64()
     throws Exception
  {
    String certificato = "";
    List lista = certPath.getCertificates();
    for(int i = 0; i < lista.size(); i++)
      certificato += SignUtils.createCERbase64((X509Certificate) lista.get(i));

    return certificato;
  }

  /**
   * Ritorna una stringa con encoding base64 con tutta la catena
   * di certificati estratti dalla smartcard. La catena di certificati
   * viene ottenuta con l'encoding CryptoBaseEngine.CERTIFICATION_CHAIN_ENCODING.
   * @return certificati in base64
   * @throws CertificateException
   */
  protected String encodeX509CertChainToBase64()
     throws CertificateException
  {
    byte[] certPathEncoded = certPath.getEncoded(CERTIFICATION_CHAIN_ENCODING);
    String base64encodedCertChain = new String(Base64.encodeBase64(certPathEncoded, true));
    return base64encodedCertChain;
  }

  /**
   * Verifica un certificato per il flag di 'non ripudio'.
   * Il flag di 'non ripudio' marca generalmente un certificato
   * valido da utilizzare per la firma digitale.
   * @param javaCert certificato da analizzare
   * @return vero se contiene il flag di 'non ripudio'
   */
  public boolean isKeyUsageNonRepudiationCritical(X509Certificate javaCert)
  {
    boolean isNonRepudiationPresent = false;
    boolean isKeyUsageCritical = false;

    Set<String> oids = javaCert.getCriticalExtensionOIDs();

    // check presence between critical extensions of oid:2.5.29.15 (KeyUsage)
    if(oids != null)
      isKeyUsageCritical = oids.contains("2.5.29.15");

    boolean[] keyUsages = javaCert.getKeyUsage();
    if(keyUsages != null)
      //check non repudiation (index 1)
      isNonRepudiationPresent = keyUsages[1];

    return (isKeyUsageCritical && isNonRepudiationPresent);
  }

  /**
   * Estrae chiave privata e catena di certificati dal KeyStore.
   * Viene prima verificata la presenza di un certificato con il
   * flag di non ripudio, quindi un alias di nome 'DS' spesso
   * associato alla firma e quindi semplicemente il primo
   * della lista.
   * @throws GeneralSecurityException
   * @throws org.commonlib5.crypto.DocumentSignException
   */
  protected void getPrivateKeyAndCertChain()
     throws GeneralSecurityException, DocumentSignException
  {
    Enumeration aliasesEnum;

    // salva la catena di certificati come prodotta dal keystore della smartcard
    originalChain.clear();
    aliasesEnum = userKeyStore.aliases();
    while(aliasesEnum.hasMoreElements())
    {
      String alias = (String) aliasesEnum.nextElement();
      X509Certificate cert = (X509Certificate) userKeyStore.getCertificate(alias);
      originalChain.add(cert);
    }

    // usa i flags (opzionali) per scegliere il certificato giusto ...
    aliasesEnum = userKeyStore.aliases();
    while(aliasesEnum.hasMoreElements())
    {
      String alias = (String) aliasesEnum.nextElement();
      X509Certificate cert = (X509Certificate) userKeyStore.getCertificate(alias);

      // verifica il flag di non ripudio
      if(isKeyUsageNonRepudiationCritical(cert))
      {
        setCurrentAlias(alias);
        return;
      }
    }

    // ... ritorna il primo certificato con alias DS ...
    aliasesEnum = userKeyStore.aliases();
    while(aliasesEnum.hasMoreElements())
    {
      String alias = (String) aliasesEnum.nextElement();
      if(alias.startsWith("DS"))
      {
        setCurrentAlias(alias);
        return;
      }
    }

    // ... ritorna il primo certificato sulla smartcard
    aliasesEnum = userKeyStore.aliases();
    if(aliasesEnum.hasMoreElements())
    {
      String alias = (String) aliasesEnum.nextElement();
      setCurrentAlias(alias);
      return;
    }

    throw new KeyStoreException("The keystore is empty or invalid certificate found!");
  }

  /**
   * Inizializzazione delle comunicazioni PKCS11 e caricamento
   * del KeyStore dalla smartcard. In questa funzione avviene
   * il primo accesso fisico alla smartcard.
   * @param aSmartCardPIN pin dell'utente associato alla smartcard
   * @return KeyStore memorizzato nella smartcard
   * @throws GeneralSecurityException
   * @throws IOException
   */
  protected KeyStore loadKeyStoreFromSmartCard(char[] aSmartCardPIN)
     throws GeneralSecurityException, IOException
  {
    // First configure the Sun PKCS#11 provider. It requires a
    // stream (or file) containing the configuration parameters -
    // "name" and "library".
    String pkcs11ConfigSettings
       = "name = SmartCard\n"
       + "library = " + libraryFile.getAbsolutePath() + "\n"
       + "slot = -1\n";
    byte[] pkcs11ConfigBytes = pkcs11ConfigSettings.getBytes();
    ByteArrayInputStream confStream = new ByteArrayInputStream(pkcs11ConfigBytes);

    // Instantiate the provider dynamically with Java reflection
    // nota qui carica la liberia nativa
    try
    {
      Class sunPkcs11Class = Class.forName(SUN_PKCS11_PROVIDER_CLASS);
      Constructor pkcs11Constr = sunPkcs11Class.getConstructor(InputStream.class);
      pkcs11Provider = (Provider) pkcs11Constr.newInstance(confStream);
      Security.addProvider(pkcs11Provider);
    }
    catch(Exception e)
    {
      throw new KeyStoreException("Cant initialize "
         + "Sun PKCS#11 security provider. Reason: " + e.getCause().getMessage(), e);
    }

    // estrae il keystore dalla smartcard
    KeyStore keyStore = KeyStore.getInstance(PKCS11_KEYSTORE_TYPE, pkcs11Provider);
    keyStore.load(null, aSmartCardPIN);
    return keyStore;
  }

  public void unloadSmartcard()
  {
    Security.removeProvider(SUN_PKCS11_PROVIDER_NAME);
  }

  /**
   * Ritorna il Common Name del certificato utilizzato per la firma.
   * @return Common Name in formato stringa
   * @throws CertificateEncodingException
   */
  public String getUserCertificateCN()
     throws CertificateEncodingException
  {
    X500Name x500name = new JcaX509CertificateHolder(userCertificate).getSubject();
    RDN[] rdNs = x500name.getRDNs(BCStyle.CN);
    if(rdNs.length == 0)
      return null;

    RDN cn = rdNs[0];
    return IETFUtils.valueToString(cn.getFirst().getValue());
  }

  /**
   * Ritorna l'organizzazione di appartenenza contenuta nel certificato utilizzato per la firma.
   * @return Nome dell'organizzazione in formato stringa
   * @throws CertificateEncodingException
   */
  public String getUserCertificateOrg()
     throws CertificateEncodingException
  {
    X500Name x500name = new JcaX509CertificateHolder(userCertificate).getSubject();
    RDN[] rdNs = x500name.getRDNs(BCStyle.O);
    if(rdNs.length == 0)
      return null;

    RDN org = rdNs[0];
    return IETFUtils.valueToString(org.getFirst().getValue());
  }

  /**
   * Ritorna campi X500 contenuti nel subject del certificato utilizzato per la firma.
   * @return mappa nome/valore dei campi del subject
   * @throws Exception
   */
  public Map<String, String> getUserCertificateSubjectFields()
     throws Exception
  {
    TreeMap<String, String> rv = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    X500Name x500name = new JcaX509CertificateHolder(userCertificate).getSubject();
    ASN1ObjectIdentifier[] attributeTypes = x500name.getAttributeTypes();
    X500NameStyle style = X500Name.getDefaultStyle();

    for(int i = 0; i < attributeTypes.length; i++)
    {
      ASN1ObjectIdentifier oi = attributeTypes[i];
      String dispName = StringOper.okStrNull(style.oidToDisplayName(oi));

      if(dispName != null)
      {
        RDN[] rdNs = x500name.getRDNs(oi);
        String value = StringOper.join(Arrays.asList(rdNs),
           (rdn) -> IETFUtils.valueToString(rdn.getFirst().getValue()), ", ", "");

        if(value != null)
          rv.put(dispName, value);
      }
    }

    return rv;
  }

  /**
   * Catena di certificati della smartcard.
   * @return array di certificati
   */
  public java.security.cert.Certificate[] getCertificationChain()
  {
    return certificationChain;
  }

  /**
   * Catena di certificati sotto forma di stringa.
   * E' il risultato di encodeX509CertChainToBase64 applicato
   * alla catena di certificati estratta.
   * @return stringa base64
   */
  public String getCertificationChainAsString()
  {
    return mCertificationChain;
  }

  /**
   * La chiave privata estratta dalla smartcard.
   * In realtà la chiave privata non può essere estratta
   * dalla smartcard; questo oggetto è solo una interfaccia
   * verso le funzionalità di una chiave privata.
   * Le operazioni effettive passano fisicamente per la
   * smartcard senza estrarre mai la chiave privata in senso
   * letterale.
   * @return chiave privata dell'utente
   */
  public PrivateKey getPrivateKey()
  {
    return privateKey;
  }

  /**
   * Ritorna la chiave pubblica memorizzata nella smartcard.
   * @return chiave pubblica dell'utente
   * @throws CertificateException
   */
  public PublicKey getPublicKey()
     throws CertificateException
  {
    return userCertificate.getPublicKey();
  }

  /**
   * Ritorna il certificato memorizzato nella smartcard.
   * @return certificato dell'utente
   */
  public X509Certificate getUserCertificate()
  {
    return userCertificate;
  }

  /**
   * Ritorna il certificato memorizzato nella smartcard.
   * In realtà ritorna la catena di certificati in formato CER,
   * con gli opportuni separatori (BEGIN..END).
   * @return stringa base64 del certificato
   */
  public String getUserCertificateAsString()
  {
    return mUserCertificate;
  }

  /**
   * Ritorna il KeyStore estratto dalla smartcard.
   * @return il KeyStore
   */
  public KeyStore getUserKeyStore()
  {
    return userKeyStore;
  }

  /**
   * Ritorna la catena ordinata dei certificati.
   * Il certificato dell'utente è il primo della catena
   * e a seguire tutte le autority che hanno firmato il
   * certificato precedente.
   * @return catena ordinata dei certificati
   */
  public CertPath getCertPath()
  {
    return certPath;
  }

  /**
   * File di libreria dinamica nativa (.so,.dll,.dylib)
   * con l'implementazione del layer PKCS11 per la macchina ospite.
   * @return puntatore al file nativo
   */
  public File getLibraryFile()
  {
    return libraryFile;
  }

  /**
   * Imposta il file libreria dinamica nativo (vedi getLibraryFile() e costruttore).
   * @param libraryFile file libreria nativo
   */
  public void setLibraryFile(File libraryFile)
  {
    this.libraryFile = libraryFile;
  }

  /**
   * Ritorna l'alias del certificato selezionato per la firma.
   * @return alias del certificato
   */
  public String getCurrentAlias()
  {
    return currentAlias;
  }

  /**
   * Imposta certificato da usare per la firma attraverso
   * il suo alias.
   * @param alias certificato selezionato
   * @throws org.commonlib5.crypto.DocumentSignException
   */
  public void setCurrentAlias(String alias)
     throws GeneralSecurityException, DocumentSignException
  {
    if(!StringOper.isEqu(this.currentAlias, alias))
    {
      this.currentAlias = alias;
      certificationChain = userKeyStore.getCertificateChain(alias);
      privateKey = (PrivateKey) userKeyStore.getKey(alias, null);
      prepareData();
    }
  }

  @Override
  public List<X509Certificate> getSigningCertificateChain()
     throws SigningCertChainException, UnexpectedJCAException
  {
    return (List<X509Certificate>) certPath.getCertificates();
  }

  @Override
  public PrivateKey getSigningKey(X509Certificate signingCert)
     throws SigningKeyException, UnexpectedJCAException
  {
    return privateKey;
  }

  /**
   * Ritorna la lista certificati originale del keystore della smartcard.
   * @return lista di certificati
   */
  public List<X509Certificate> getOriginalChain()
  {
    return Collections.unmodifiableList(originalChain);
  }
}
