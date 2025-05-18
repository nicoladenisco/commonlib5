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
package org.commonlib5.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione di LongOperListener con output su writer.
 *
 * @author Nicola De Nisco
 */
public class LongOperListenerPrint implements LongOperListener
{
  public static final long BYTE = 1;
  public static final long KILO_BYTE = 1024;
  public static final long MEGA_BYTE = 1024 * 1024;

  protected long tipoSize;
  protected String format = "%s/%s", sp, st, suffisso = "", newline = "\n";
  protected Writer wr;
  protected String lastValue = "";
  protected long lastPart = -10, lastTotal = -10;

  public LongOperListenerPrint(long tipoSize, Writer wr)
  {
    this(tipoSize, null, wr);
  }

  public LongOperListenerPrint(long tipoSize, String format, Writer wr)
  {
    this.tipoSize = tipoSize;
    this.wr = wr;

    if(format != null)
      this.format = format;

    if(tipoSize == KILO_BYTE)
      suffisso = "K";
    if(tipoSize == MEGA_BYTE)
      suffisso = "M";
  }

  public String getNewline()
  {
    return newline;
  }

  public void setNewline(String newline)
  {
    this.newline = newline;
  }

  @Override
  public void resetUI()
  {
  }

  @Override
  public void completeUI(long total)
  {
  }

  @Override
  public boolean updateUI(long part, long total)
  {
    if(lastPart != part)
    {
      sp = Long.toString(part / tipoSize) + suffisso;
      lastPart = part;
    }

    if(lastTotal != total)
    {
      st = Long.toString(total / tipoSize) + suffisso;
      lastTotal = total;
    }

    String value = String.format(format, sp, st);
    if(!value.equals(lastValue))
    {
      try
      {
        wr.write(value);
        wr.write(newline);
        wr.flush();
        lastValue = value;
      }
      catch(IOException ex)
      {
        Logger.getLogger(LongOperListenerPrint.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    // continua operazione in corso
    return true;
  }

  @Override
  public String toString()
  {
    return "LongOperListenerPrint{" + lastValue + '}';
  }
}
