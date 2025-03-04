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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import xades4j.providers.CannotBuildCertificationPathException;
import xades4j.providers.CannotSelectCertificateException;
import xades4j.providers.CertificateValidationException;
import xades4j.providers.CertificateValidationProvider;
import xades4j.providers.ValidationData;
import xades4j.verification.UnexpectedJCAException;

/**
 * Implementation of {@code CertificateValidationProvider} using a PKIX {@code CertPathBuilder}.
 * <p>
 * Since the Java's PKIX API doesn't allow to access the CRLs used in the certification
 * path validation, this is manually done. There has to be a CRL for each issuer
 * in the path which is valid at the moment of validation (signature and date).
 * <p>
 * Questo validatore estre la CRL dal tag 2.5.29.31 del certificato ovvero CRLDistributionPoints.
 *
 * @author Luís
 * @author Nicola De Nisco
 */
public class PKIXSelfValidationProvider extends BaseValidationProvider
   implements CertificateValidationProvider
{
  private static final int DEFAULT_MAX_PATH_LENGTH = 6;

  private final KeyStore trustAnchors;
  private final boolean revocationEnabled;
  private final int maxPathLength;
  private final CertStore[] intermCertsAndCrls;
  private final CertPathBuilder certPathBuilder;

  /**
   * Initializes a new instance that uses the specified JCE providers for CertPathBuilder
   * and Signature.
   * @param trustAnchors the keystore with the trust-anchors ({@code TrustedCertificateEntry})
   * @param revocationEnabled whether revocation is enabled
   * @param maxPathLength the maximum length of the certification paths
   * @param certPathBuilderProvider the CertPathBuilder provider
   * @param signatureProvider the Signature provider
   * @param intermCertsAndCrls a set of {@code CertStore}s that contain certificates to be
   * used in the construction of the certification path. May contain CRLs to be used
   * if revocation is enabled
   * @see xades4j.utils.FileSystemDirectoryCertStore
   * @throws NoSuchAlgorithmException if there is no provider for PKIX CertPathBuilder
   */
  public PKIXSelfValidationProvider(
     KeyStore trustAnchors,
     boolean revocationEnabled,
     int maxPathLength,
     String certPathBuilderProvider,
     String signatureProvider,
     CertStore... intermCertsAndCrls)
     throws NoSuchAlgorithmException, NoSuchProviderException
  {
    super(signatureProvider);

    if(null == trustAnchors)
    {
      throw new NullPointerException("Trust anchors cannot be null");
    }

    this.trustAnchors = trustAnchors;
    this.revocationEnabled = revocationEnabled;
    this.maxPathLength = maxPathLength;
    this.certPathBuilder = certPathBuilderProvider == null ? CertPathBuilder.getInstance("PKIX")
                              : CertPathBuilder.getInstance("PKIX", certPathBuilderProvider);
    this.intermCertsAndCrls = intermCertsAndCrls;
  }

  /**
   * Initializes a new instance that uses the specified JCE providers for CertPathBuilder
   * and Signature.
   * @param trustAnchors the keystore with the trust-anchors ({@code TrustedCertificateEntry})
   * @param revocationEnabled whether revocation is enabled
   * @param certPathBuilderProvider the CertPathBuilder provider
   * @param signatureProvider the Signature provider
   * @param intermCertsAndCrls a set of {@code CertStore}s that contain certificates to be
   * used in the construction of the certification path. May contain CRLs to be used
   * if revocation is enabled
   * @see xades4j.utils.FileSystemDirectoryCertStore
   * @throws NoSuchAlgorithmException if there is no provider for PKIX CertPathBuilder
   */
  public PKIXSelfValidationProvider(
     KeyStore trustAnchors,
     boolean revocationEnabled,
     String certPathBuilderProvider,
     String signatureProvider,
     CertStore... intermCertsAndCrls)
     throws NoSuchAlgorithmException, NoSuchProviderException
  {
    this(trustAnchors, revocationEnabled, DEFAULT_MAX_PATH_LENGTH, certPathBuilderProvider, signatureProvider, intermCertsAndCrls);
  }

  /**
   * Initializes a new instance that uses the specified JCE provider for both
   * CertPathBuilder and Signature.
   * @param trustAnchors the keystore with the trust-anchors ({@code TrustedCertificateEntry})
   * @param revocationEnabled whether revocation is enabled
   * @param maxPathLength the maximum length of the certification paths
   * @param jceProvider the CertPathBuilder and Signature provider
   * @param intermCertsAndCrls a set of {@code CertStore}s that contain certificates to be
   * used in the construction of the certification path. May contain CRLs to be used
   * if revocation is enabled
   * @see xades4j.utils.FileSystemDirectoryCertStore
   * @throws NoSuchAlgorithmException if there is no provider for PKIX CertPathBuilder
   */
  public PKIXSelfValidationProvider(
     KeyStore trustAnchors,
     boolean revocationEnabled,
     int maxPathLength,
     String jceProvider,
     CertStore... intermCertsAndCrls)
     throws NoSuchAlgorithmException, NoSuchProviderException
  {
    this(trustAnchors, revocationEnabled, maxPathLength, jceProvider, jceProvider, intermCertsAndCrls);
  }

  /**
   * Initializes a new instance that uses the specified JCE provider for both
   * CertPathBuilder and Signature.
   * @param trustAnchors the keystore with the trust-anchors ({@code TrustedCertificateEntry})
   * @param revocationEnabled whether revocation is enabled
   * @param jceProvider the CertPathBuilder and Signature provider
   * @param intermCertsAndCrls a set of {@code CertStore}s that contain certificates to be
   * used in the construction of the certification path. May contain CRLs to be used
   * if revocation is enabled
   * @see xades4j.utils.FileSystemDirectoryCertStore
   * @throws NoSuchAlgorithmException if there is no provider for PKIX CertPathBuilder
   */
  public PKIXSelfValidationProvider(
     KeyStore trustAnchors,
     boolean revocationEnabled,
     String jceProvider,
     CertStore... intermCertsAndCrls)
     throws NoSuchAlgorithmException, NoSuchProviderException
  {
    this(trustAnchors, revocationEnabled, DEFAULT_MAX_PATH_LENGTH, jceProvider, intermCertsAndCrls);
  }

  /**
   * Initializes a new instance without specifying the JCE providers for CertPathBuilder
   * and Signature.
   * @param trustAnchors the keystore with the trust-anchors ({@code TrustedCertificateEntry})
   * @param revocationEnabled whether revocation is enabled
   * @param maxPathLength the maximum length of the certification paths
   * @param intermCertsAndCrls a set of {@code CertStore}s that contain certificates to be
   * used in the construction of the certification path. May contain CRLs to be used
   * if revocation is enabled
   * @see xades4j.utils.FileSystemDirectoryCertStore
   * @throws NoSuchAlgorithmException if there is no provider for PKIX CertPathBuilder
   */
  public PKIXSelfValidationProvider(
     KeyStore trustAnchors,
     boolean revocationEnabled,
     int maxPathLength,
     CertStore... intermCertsAndCrls)
     throws NoSuchAlgorithmException, NoSuchProviderException
  {
    this(trustAnchors, revocationEnabled, maxPathLength, null, null, intermCertsAndCrls);
  }

  /**
   * Initializes a new instance without specifying the JCE providers for CertPathBuilder
   * and Signature.
   * @param trustAnchors the keystore with the trust-anchors ({@code TrustedCertificateEntry})
   * @param revocationEnabled whether revocation is enabled
   * @param intermCertsAndCrls a set of {@code CertStore}s that contain certificates to be
   * used in the construction of the certification path. May contain CRLs to be used
   * if revocation is enabled
   * @see xades4j.utils.FileSystemDirectoryCertStore
   * @throws NoSuchAlgorithmException if there is no provider for PKIX CertPathBuilder
   */
  public PKIXSelfValidationProvider(
     KeyStore trustAnchors,
     boolean revocationEnabled,
     CertStore... intermCertsAndCrls)
     throws NoSuchAlgorithmException, NoSuchProviderException
  {
    this(trustAnchors, revocationEnabled, DEFAULT_MAX_PATH_LENGTH, null, null, intermCertsAndCrls);
  }

  @Override
  public ValidationData validate(
     X509CertSelector certSelector,
     Date validationDate,
     Collection<X509Certificate> otherCerts)
     throws CertificateValidationException, UnexpectedJCAException
  {
    PKIXBuilderParameters builderParams;
    try
    {
      builderParams = new PKIXBuilderParameters(trustAnchors, certSelector);
    }
    catch(KeyStoreException ex)
    {
      throw new CannotBuildCertificationPathException(certSelector, "Trust anchors KeyStore is not initialized", ex);
    }
    catch(InvalidAlgorithmParameterException ex)
    {
      throw new CannotBuildCertificationPathException(certSelector, "Trust anchors KeyStore has no trusted certificate entries", ex);
    }

    PKIXCertPathBuilderResult builderRes;
    try
    {
      // Certificates to be used to build the certification path.
      // - The other certificates from the signature (e.g. from KeyInfo).
      if(otherCerts != null)
      {
        CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters(otherCerts);
        CertStore othersCertStore = CertStore.getInstance("Collection", ccsp);
        builderParams.addCertStore(othersCertStore);
      }

      // - The external certificates/CRLs.
      for(int i = 0; i < intermCertsAndCrls.length; i++)
      {
        builderParams.addCertStore(intermCertsAndCrls[i]);
      }

      builderParams.setRevocationEnabled(false);
      builderParams.setMaxPathLength(maxPathLength);
      builderParams.setDate(validationDate);
      builderParams.setSigProvider(this.signatureProvider);

      builderRes = (PKIXCertPathBuilderResult) certPathBuilder.build(builderParams);
    }
    catch(CertPathBuilderException ex)
    {
      throw new CannotBuildCertificationPathException(certSelector, ex.getMessage(), ex);
    }
    catch(InvalidAlgorithmParameterException ex)
    {
      // SHOULD NOT be thrown due to wrong type of parameters.
      // Seems to be thrown when the CertSelector (in builderParams) criteria
      // cannot be applied.
      throw new CannotSelectCertificateException(certSelector, ex);
    }
    catch(NoSuchAlgorithmException ex)
    {
      // SHOULD NOT be thrown.
      throw new UnexpectedJCAException("No provider for Collection CertStore", ex);
    }

    // The cert path returned by the builder ends in a certificate issued by
    // the trust anchor. However, the complete path may be needed for property
    // verification.
    ArrayList<X509Certificate> certPath = new ArrayList<X509Certificate>();
    // aggiunge tutti gli intermedi
    certPath.addAll((List<X509Certificate>) builderRes.getCertPath().getCertificates());
    // aggiunge il certificato finale (quello della certification authority)
    certPath.add(builderRes.getTrustAnchor().getTrustedCert());

    if(revocationEnabled)
    {
      try
      {
        return new ValidationData(certPath, getCRLsForCertPath(certPath, validationDate));
      }
      catch(Exception ex)
      {
        throw new CertificateValidationException(null, "Cannot get CRLs", ex);
      }
    }

    return new ValidationData(certPath);
  }
}
