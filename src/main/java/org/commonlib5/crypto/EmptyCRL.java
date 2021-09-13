/*
 * Copyright (C) 2020 Nicola De Nisco
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either crlversion 2
 * of the License, or (at your option) any later crlversion.
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

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import org.commonlib5.utils.DateTime;

/**
 * Una CRL vuota.
 *
 * @author Nicola De Nisco
 */
public class EmptyCRL extends X509CRL
{
  private int crlversion = 1;
  private Principal issuerDN;

  public EmptyCRL(Principal issuerDN)
  {
    this.issuerDN = issuerDN;
  }

  public EmptyCRL(Principal issuerDN, int version)
  {
    this.issuerDN = issuerDN;
    this.crlversion = version;
  }

  @Override
  public byte[] getEncoded()
     throws CRLException
  {
    return null;
  }

  @Override
  public void verify(PublicKey key)
     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
  }

  @Override
  public void verify(PublicKey key, String sigProvider)
     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
  }

  @Override
  public int getVersion()
  {
    return crlversion;
  }

  @Override
  public Principal getIssuerDN()
  {
    return issuerDN;
  }

  @Override
  public Date getThisUpdate()
  {
    return new Date();
  }

  @Override
  public Date getNextUpdate()
  {
    return DateTime.dataSpiazzata(new Date(), 365);
  }

  @Override
  public X509CRLEntry getRevokedCertificate(BigInteger serialNumber)
  {
    return null;
  }

  @Override
  public Set<? extends X509CRLEntry> getRevokedCertificates()
  {
    return Collections.EMPTY_SET;
  }

  @Override
  public byte[] getTBSCertList()
     throws CRLException
  {
    return null;
  }

  @Override
  public byte[] getSignature()
  {
    return null;
  }

  @Override
  public String getSigAlgName()
  {
    return null;
  }

  @Override
  public String getSigAlgOID()
  {
    return null;
  }

  @Override
  public byte[] getSigAlgParams()
  {
    return null;
  }

  @Override
  public String toString()
  {
    return issuerDN == null ? "EmptyCRL" : "EmptyCRL for " + issuerDN.getName();
  }

  @Override
  public boolean isRevoked(Certificate cert)
  {
    return false;
  }

  @Override
  public boolean hasUnsupportedCriticalExtension()
  {
    return false;
  }

  @Override
  public Set<String> getCriticalExtensionOIDs()
  {
    return Collections.EMPTY_SET;
  }

  @Override
  public Set<String> getNonCriticalExtensionOIDs()
  {
    return Collections.EMPTY_SET;
  }

  @Override
  public byte[] getExtensionValue(String oid)
  {
    return null;
  }
}
