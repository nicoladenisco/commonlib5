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
package org.commonlib5.ssl;

import java.io.File;
import java.util.Objects;

/**
 * Informazioni per socket SSL.
 * <ul>
 * <li> keyStore file della chiave (pubblica+privata)</li>
 * <li> trustStore file con le CA riconosciute</li>
 * <li> passPhraseKs password per il file keyStore (default PASSWORD) </li>
 * <li> passPhraseTs password per il file trustStore (default PASSWORD) </li>
 * <li> passPhraseKey password per la chiave (default "") </li>
 * <li> keyStoreType tipo di key store (default JKS) </li>
 * <li> trustStoreType tipo di key store (default JKS) </li>
 * </ul>
 * @author Nicola De Nisco
 */
public class SSLSocketInfo
{
  protected File keyStore, trustStore;
  protected String keyStorePassword = "PASSWORD", trustStorePassword = "PASSWORD",
     keyPassword = "",
     keyStoreType = "JKS", trustStoreType = "JKS";

  public File getKeyStore()
  {
    return keyStore;
  }

  public void setKeyStore(File keyStore)
  {
    this.keyStore = keyStore;
  }

  public File getTrustStore()
  {
    return trustStore;
  }

  public void setTrustStore(File trustStore)
  {
    this.trustStore = trustStore;
  }

  public String getKeyStorePassword()
  {
    return keyStorePassword;
  }

  public void setKeyStorePassword(String keyStorePassword)
  {
    this.keyStorePassword = keyStorePassword;
  }

  public String getTrustStorePassword()
  {
    return trustStorePassword;
  }

  public void setTrustStorePassword(String trustStorePassword)
  {
    this.trustStorePassword = trustStorePassword;
  }

  public String getKeyPassword()
  {
    return keyPassword;
  }

  public void setKeyPassword(String keyPassword)
  {
    this.keyPassword = keyPassword;
  }

  public String getKeyStoreType()
  {
    return keyStoreType;
  }

  public void setKeyStoreType(String keyStoreType)
  {
    this.keyStoreType = keyStoreType;
  }

  public String getTrustStoreType()
  {
    return trustStoreType;
  }

  public void setTrustStoreType(String trustStoreType)
  {
    this.trustStoreType = trustStoreType;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 97 * hash + Objects.hashCode(this.keyStore);
    hash = 97 * hash + Objects.hashCode(this.trustStore);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;

    final SSLSocketInfo other = (SSLSocketInfo) obj;
    if(!Objects.equals(this.keyStorePassword, other.keyStorePassword))
      return false;
    if(!Objects.equals(this.trustStorePassword, other.trustStorePassword))
      return false;
    if(!Objects.equals(this.keyPassword, other.keyPassword))
      return false;
    if(!Objects.equals(this.keyStoreType, other.keyStoreType))
      return false;
    if(!Objects.equals(this.trustStoreType, other.trustStoreType))
      return false;
    if(!Objects.equals(this.keyStore, other.keyStore))
      return false;
    if(!Objects.equals(this.trustStore, other.trustStore))
      return false;

    return true;
  }

  @Override
  public String toString()
  {
    return "SSLSocketInfo{"
       + "keyStore=" + keyStore
       + ", trustStore=" + trustStore
       + ", keyStorePassword=" + keyStorePassword
       + ", trustStorePassword=" + trustStorePassword
       + ", keyPassword=" + keyPassword
       + ", keyStoreType=" + keyStoreType
       + ", trustStoreType=" + trustStoreType + '}';
  }
}
