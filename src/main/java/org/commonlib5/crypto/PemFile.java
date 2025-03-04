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

import java.io.*;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * Lettura di un file PEM.
 *
 * @author Nicola De Nisco
 */
public class PemFile
{
  private PemObject pemObject;

  public PemFile(String filename)
     throws FileNotFoundException, IOException
  {
    this(new File(filename));
  }

  public PemFile(File filename)
     throws FileNotFoundException, IOException
  {
    try (PemReader pemReader = new PemReader(
       new InputStreamReader(new FileInputStream(filename))))
    {
      this.pemObject = pemReader.readPemObject();
    }
  }

  public PemObject getPemObject()
  {
    return pemObject;
  }
}
