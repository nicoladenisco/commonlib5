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

import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import xades4j.providers.CertificateValidationException;
import xades4j.providers.CertificateValidationProvider;
import xades4j.providers.ValidationData;
import xades4j.verification.UnexpectedJCAException;

/**
 * Validatore semplificato.
 *
 * Un validatore da usare quando si hanno già tutti i certificati
 * necessari della catena, ovvero sia il certificato del firmatario,
 * sia gli eventuali intermendi, il certificato principale della CA.
 *
 * @author Nicola De Nisco
 */
public class PKIXSimpleSelfValidationProvider extends BaseValidationProvider
   implements CertificateValidationProvider
{
  private final boolean revocationEnabled;
  private final ArrayList<X509Certificate> certPath = new ArrayList<X509Certificate>();

  public PKIXSimpleSelfValidationProvider(
     String signatureProvider,
     boolean revocationEnabled,
     X509Certificate signerCertificate,
     X509Certificate... chainTrust)
  {
    super(signatureProvider);
    this.revocationEnabled = revocationEnabled;

    certPath.add(signerCertificate);
    certPath.addAll(Arrays.asList(chainTrust));
  }

  @Override
  public ValidationData validate(X509CertSelector certSelector,
     Date validationDate, Collection<X509Certificate> otherCerts)
     throws CertificateValidationException, UnexpectedJCAException
  {
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
