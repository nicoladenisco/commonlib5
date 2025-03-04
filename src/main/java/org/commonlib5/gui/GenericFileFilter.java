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
package org.commonlib5.gui;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * Un filtro generico in base alle estensioni dei files.
 *
 * @author Nicola De Nisco
 */
public class GenericFileFilter extends FileFilter
{
  protected String descr = "";
  protected String[] exts = null;

  public GenericFileFilter(String descr, String[] exts)
  {
    this.descr = descr;
    this.exts = exts;
  }

  public GenericFileFilter(String descr, String ext)
  {
    this.descr = descr;
    this.exts = new String[] {ext};
  }

  @Override
  public boolean accept(File f)
  {
    if(f.isDirectory())
    {
      return true;
    }

    String name = f.getName().toLowerCase();
    for(String ext : exts)
    {
      if(name.endsWith(ext))
        return true;
    }

    return false;
  }

  @Override
  public String getDescription()
  {
    return descr;
  }
}
