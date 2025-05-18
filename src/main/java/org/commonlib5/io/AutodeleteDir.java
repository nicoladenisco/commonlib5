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
package org.commonlib5.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.OsIdent;

/**
 * Implementa un wrapper di directory con interfaccia closeable.
 * Può essere utilizzato all'interno di un try-whit-resources con l'effetto
 * di cancellare automaticamente la directory all'uscita dal contesto.
 *
 * @author Nicola De Nisco
 */
public class AutodeleteDir implements Closeable
{
  private final File dir;
  private final boolean deleteItself;

  /**
   * Costruttore con directory già creata.
   * La creazione della directory deve avvenire prima della creazione dell'instanza.
   * @param dir directory già creata
   * @param deleteItself se vero all'uscita viene cancellata anche la directory stessa
   */
  public AutodeleteDir(File dir, boolean deleteItself)
  {
    this.dir = dir;
    this.deleteItself = deleteItself;
  }

  /**
   * Costruttore con directory temporanea autocreata.
   * La directory temporanea viene creata automaticamente nella
   * temp di sistema (vedi OsIdent).
   * Viene cancellate all'uscita del contesto.
   */
  public AutodeleteDir()
  {
    try
    {
      this.dir = CommonFileUtils.createTempDir("autodelete", OsIdent.getSystemTemp());
      this.deleteItself = true;
    }
    catch(IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void close()
     throws IOException
  {
    CommonFileUtils.deleteDir(dir, deleteItself);
  }

  public File getDir()
  {
    return dir;
  }

  public boolean isDeleteItself()
  {
    return deleteItself;
  }
}
