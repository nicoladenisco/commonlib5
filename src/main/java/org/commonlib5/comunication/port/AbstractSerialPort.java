/*
 * Copyright (C) 2011 Nicola De Nisco
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

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import org.commonlib5.comunication.BadParameterException;

/**
 * Rappresentazione astratta di una porta e di una metodologia di comunicazione
 * (vedi RS232Port e RS485Port).
 *
 * @author Nicola De Nisco
 */
abstract public class AbstractSerialPort extends AbstractPort
{
  public int bdrate = 9600;
  public int databit = 8;
  public int stopbit = SerialPort.ONE_STOP_BIT;
  public int parita = SerialPort.NO_PARITY;
  public int flowctl = SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED;
  protected SerialPort serialPort;

  /**
   * Imposta i parametri della porta seriale.
   * Vengono impostati tutti i parametri principali per le comunicazioni.
   * @param pbdrate velocità della porta
   * @param pdatabit numero di bit (valori ammessi: 5,6,7,8)
   * @param pstopbit numero di bit di stop (valori ammessi: 1,2)
   * @param pparita tipo di parità (valori ammessi: 'N' 'P' 'E' 'D' 'O' 'M' 'S')
   * @param pflowctl controllo di flusso (valori ammessi: 'N' 'X' 'R')
   * @throws Exception
   */
  public void setParams(int pbdrate, int pdatabit, int pstopbit, int pparita, int pflowctl)
     throws Exception
  {
    switch(pdatabit)
    {
      case 5:
      case 6:
      case 7:
      case 8:
        databit = pdatabit;
        break;
      default:
        throw new BadParameterException("illegal value for databit=" + pdatabit);
    }

    switch(pstopbit)
    {
      case 1:
        stopbit = SerialPort.ONE_STOP_BIT;
        break;
      case 2:
        stopbit = SerialPort.TWO_STOP_BITS;
        break;
      default:
        throw new BadParameterException("illegal value for stopbit=" + pstopbit);
    }

    switch(pparita)
    {
      case 'N':
        parita = SerialPort.NO_PARITY;
        break;
      case 'P':
      case 'E':
        parita = SerialPort.EVEN_PARITY;
        break;
      case 'D':
      case 'O':
        parita = SerialPort.ODD_PARITY;
        break;
      case 'M':
        parita = SerialPort.MARK_PARITY;
        break;
      case 'S':
        parita = SerialPort.SPACE_PARITY;
        break;
      default:
        throw new BadParameterException("illegal value for parity=" + pparita);
    }

    switch(pflowctl)
    {
      case 'N':
        flowctl = SerialPort.FLOW_CONTROL_DISABLED;
        break;
      case 'X':
        flowctl = SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
        break;
      case 'R':
        flowctl = SerialPort.FLOW_CONTROL_CTS_ENABLED | SerialPort.FLOW_CONTROL_RTS_ENABLED;
        break;
      default:
        throw new BadParameterException("illegal value for flow control=" + pflowctl);
    }

    this.bdrate = pbdrate;
  }

  /**
   * Inizializzazione della porta seriale.
   * @param sApplicazione nome dell'applicazione che apre la porta
   * @throws java.lang.Exception
   */
  @Override
  public void initComm(String sApplicazione)
     throws Exception
  {
    super.initComm(sApplicazione);

    // apre porta seriale
    if((serialPort = (SerialPort) SerialPort.getCommPort(sPorta)) == null)
      throw new IOException("Port doesn't exist.");

    if(!serialPort.openPort())
      throw new IOException("Can't open the port.");

    // imposta gli stream di input/output
    setStream(serialPort.getInputStream(), serialPort.getOutputStream());

    // imposta i parametri della porta
    serialPort.setFlowControl(flowctl);
    serialPort.setComPortParameters(bdrate, databit, stopbit, parita);
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
  }

  @Override
  public void closeComm()
  {
    super.closeComm();

    // chiude comunicazioni seriali
    if(serialPort != null)
    {
      serialPort.closePort();
      serialPort = null;
    }
  }
}
