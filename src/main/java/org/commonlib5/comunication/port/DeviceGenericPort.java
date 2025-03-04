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
package org.commonlib5.comunication.port;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Porta generica per l'uso con file device (solo UNIX).
 * Questa porta apre gli stream di input e output direttamente
 * sul file di device che rappresenta la porta (/dev/ttyS0 ad esempio).
 * Per impostare i parametri occorre utilizzare il comando setserial e/o stty.
 * ES: /dev/ttyS0 (COM1 dos) 9600,N,8,1
 * stty -F /dev/ttyS0 raw ispeed 9600 ospeed 9600 cs8 -ignpar -cstopb -echo
 *
 * @author Nicola De Nisco
 */
public class DeviceGenericPort extends AbstractPort
{
  @Override
  public void initComm(String sData)
     throws Exception
  {
    super.initComm(sData);

    // imposta gli stream di input/output
    setStream(
       new FileInputStream(sPorta),
       new FileOutputStream(sPorta));
  }
}
