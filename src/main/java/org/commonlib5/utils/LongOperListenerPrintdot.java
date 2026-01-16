/*
 * Copyright (C) 2026 Nicola De Nisco
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Implementazione di LongOperListener con output su writer.
 *
 * @author Nicola De Nisco
 */
public class LongOperListenerPrintdot implements LongOperListener
{
  protected long oldperc = 0, numdot = 100;
  protected Writer wr;

  public LongOperListenerPrintdot(int numdot, Writer w)
  {
    this.numdot = numdot;
    this.wr = w;
  }

  public LongOperListenerPrintdot(int numdot, PrintStream ps)
  {
    this.numdot = numdot;
    this.wr = new PrintWriter(ps);
  }

  @Override
  public void resetUI()
  {
    oldperc = 0;
    write("Downloading ");
  }

  @Override
  public void completeUI(long total)
  {
    write(" Completed!\n");
  }

  @Override
  public boolean updateUI(long part, long total)
  {
    if(total != 0)
    {
      long p = (part * numdot) / total;
      if(p != oldperc)
      {
        write(".");
        oldperc = p;
      }
    }

    return true;
  }

  public void write(String s)
  {
    try
    {
      wr.write(s);
    }
    catch(Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString()
  {
    return "LongOperListenerPrintdot{" + "oldperc=" + oldperc + ", numdot=" + numdot + '}';
  }
}
