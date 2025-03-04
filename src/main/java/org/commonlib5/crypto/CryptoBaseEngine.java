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

import java.io.File;

/**
 * Classe base per l'uso della smartcard.
 * In questa classe vengono gestite le comunicazioni con la smartcard.
 * Viene caricata la libreria nativa che implementa il layer PKCS11
 * e in prepareSmartCardAndData() viene inizializzata la comunicazione
 * con la smartcard ed estratti i certificati e le chiavi.
 * Classi derivate possono usare questi dati per firmare o crittografare dati.
 * Vedi anche SignUtils.java.
 *
 * @author Nicola De Nisco
 */
public class CryptoBaseEngine
{
  protected SmartcardDataProvider kdp;

  /**
   * Costruttore di default.
   * @param libraryFile file libreria nativo (.dll,.dylib,.so) con
   * l'implementazione del layer PKCS11
   */
  public CryptoBaseEngine(File libraryFile)
  {
    kdp = new SmartcardDataProvider(libraryFile);
  }

  /**
   * Costruttore con data provider fornito.
   * @param kdp provider dati smartcard
   */
  public CryptoBaseEngine(SmartcardDataProvider kdp)
  {
    this.kdp = kdp;
  }

  public SmartcardDataProvider getKdp()
  {
    return kdp;
  }

  public void setKdp(SmartcardDataProvider kdp)
  {
    this.kdp = kdp;
  }

  public void prepareSmartCardAndData(char[] toCharArray)
     throws DocumentSignException
  {
    this.kdp.prepareSmartCardAndData(toCharArray);
  }
}
