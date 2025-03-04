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

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Come GZIPOutputStream ma con un metodo detach()
 * che consente di staccare lo stream originale senza
 * chiuderlo.
 *
 * @author Nicola De Nisco
 */
public class DtGZIPOutputStream extends GZIPOutputStream
{
  public DtGZIPOutputStream(OutputStream out) throws IOException
  {
    super(out);
  }

  public DtGZIPOutputStream(OutputStream out, int size) throws IOException
  {
    super(out, size);
  }

  /**
   * Flush dello stream sottostante e distacco dallo stream.
   * Questo consente di continuare ad utilizzare lo stream
   * dopo che questa classe ha finito il suo utilizzo.
   *
   * @throws IOException
   */
  public void detach() throws IOException
  {
    finish();
    out.flush();
    out = null;
  }
}
