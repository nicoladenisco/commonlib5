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

import java.util.Date;

/*
 * Copyright (C) 2012 Nicola De Nisco
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

/**
 * Blocco di dati osservato sugli stream.
 * MonitorInputStream e MonitorOutputStream salvano i blocchi
 * di dati passati in oggetti MonitorStreamData in modo da
 * marcarli con data e ora.
 * Gli oggetti MonitorStreamData sono accumulati in un
 * MonitorStreamQueue per essere osservati.
 *
 * @author Nicola De Nisco
 */
public class MonitorStreamData
{
  protected int t;
  protected Date d;
  protected byte[] b;

  public MonitorStreamData(int t, Date d, byte[] b)
  {
    this.t = t;
    this.d = d;
    this.b = b;
  }

  public MonitorStreamData(Date d, byte[] b)
  {
    this.t = 0;
    this.d = d;
    this.b = b;
  }

  public void setType(int t)
  {
    this.t = t;
  }

  public int getType()
  {
    return t;
  }

  public byte[] getByte()
  {
    return b;
  }

  public Date getDate()
  {
    return d;
  }
}
