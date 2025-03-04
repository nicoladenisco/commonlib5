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

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.security.auth.x500.X500Principal;
import xades4j.providers.CertificateValidationException;

/**
 *
 * @author Nicola De Nisco
 */
public class BaseValidationProvider
{
  protected final String signatureProvider;
  protected static final Map<String, X509CRL> cacheCRL = new HashMap<>();

  public BaseValidationProvider(String signatureProvider)
  {
    this.signatureProvider = signatureProvider;
  }

  protected Collection<X509CRL> getCRLsForCertPath(List<X509Certificate> certPath, Date validationDate)
     throws Exception
  {
    // Map the issuers certificates in the chain. This is used to know the issuers
    // and later to verify the signatures in the CRLs.
    Map<X500Principal, X509Certificate> issuersCerts = new HashMap<X500Principal, X509Certificate>();
    for(int i = 0; i < certPath.size() - 1; i++)
    {
      // The issuer of one certificate is the subject of the following one.
      issuersCerts.put(certPath.get(i).getIssuerX500Principal(), certPath.get(i + 1));
    }

    Set<String> crlUrls = new HashSet<>();
    for(X509Certificate cert : certPath)
    {
      List<String> lsPoints = CRLVerifier.getCrlDistributionPoints(cert);
      crlUrls.addAll(lsPoints);
    }

    Set<X509CRL> crls = new HashSet<X509CRL>();
    for(String cUrl : crlUrls)
    {
      X509CRL crl;

      if((crl = cacheCRL.get(cUrl)) == null)
      {
        crl = CRLVerifier.downloadCRL(cUrl);
        cacheCRL.put(cUrl, crl);
      }

      crls.add(crl);
    }

    // Verify the CRLs' signatures. The issuers' certificates were validated
    // as part of the cert path creation.
    for(X509CRL crl : crls)
    {
      try
      {
        X509Certificate crlIssuerCert = issuersCerts.get(crl.getIssuerX500Principal());
        if(null == this.signatureProvider)
        {
          crl.verify(crlIssuerCert.getPublicKey());
        }
        else
        {
          crl.verify(crlIssuerCert.getPublicKey(), this.signatureProvider);
        }
      }
      catch(Exception ex)
      {
        throw new CertificateValidationException(null,
           "Invalid CRL signature from " + crl.getIssuerX500Principal().getName(), ex);
      }
    }

    return crls;
  }
}
