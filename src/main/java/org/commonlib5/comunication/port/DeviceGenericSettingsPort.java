/*
 * DeviceGenericSettingsPort.java
 *
 * Created on 18-lug-2013, 13.40.48
 *
 *Copyright (C) 2012 Nicola De Nisco
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.commonlib5.comunication.port;

import com.fazecast.jSerialComm.SerialPort;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.commonlib5.comunication.BadParameterException;
import org.commonlib5.exec.ExecHelper;
import org.commonlib5.utils.OsIdent;
import org.commonlib5.utils.StringOper;

/**
 * Porta generica per l'uso con file device (solo UNIX).
 * Questa porta apre gli stream di input e output direttamente
 * sul file di device che rappresenta la porta (/dev/ttyS0 ad esempio).
 * Utilizza un comndo della shell per impostare la porta.
 * ES: /dev/ttyS0 (COM1 dos) 9600,N,8,1
 * stty -F /dev/ttyS0 raw ispeed 9600 ospeed 9600 cs8 -ignpar -cstopb -echo
 *
 * @author Nicola De Nisco
 */
public class DeviceGenericSettingsPort extends DeviceGenericPort
{
  public int bdrate = 9600;
  public int databit = 8;
  public int stopbit = SerialPort.ONE_STOP_BIT;
  public int parita = SerialPort.NO_PARITY;
  public int flowctl = SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED;

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

  @Override
  public void initComm(String sData)
     throws Exception
  {
    super.initComm(sData);

    // imposta gli stream di input/output
    setStream(
       new FileInputStream(sPorta),
       new FileOutputStream(sPorta));

    setPortParams();
  }

  /**
   * Imposta i parametri della porta.
   * Usa un comando di shell per lanciare il programma
   * di settaggio della seriale lanciando qualcosa del tipo:
   * ES: /dev/ttyS0 (COM1 dos) 9600,N,8,1
   * stty -F /dev/ttyS0 raw ispeed 9600 ospeed 9600 cs8 -ignpar -cstopb -echo
   * @throws java.lang.Exception
   */
  protected void setPortParams()
     throws Exception
  {
    if(OsIdent.isWindows())
      setPortParamsWindows();
    else
      setPortParamsUnix();
  }

  protected void setPortParamsWindows()
     throws Exception
  {
  }

  protected void setPortParamsUnix()
     throws Exception
  {
    ArrayList<String> arCmd = new ArrayList<String>();
    arCmd.add("/bin/stty");
    arCmd.add("-F");
    arCmd.add(sPorta);
    arCmd.add("raw");
    arCmd.add("ispeed");
    arCmd.add(Integer.toString(bdrate));
    arCmd.add("ospeed");
    arCmd.add(Integer.toString(bdrate));

    switch(databit)
    {
      case 5:
        arCmd.add("cs5");
        break;
      case 6:
        arCmd.add("cs6");
        break;
      case 7:
        arCmd.add("cs7");
        break;
      case 8:
        arCmd.add("cs8");
        break;
      default:
        throw new BadParameterException("illegal value for databit=" + databit);
    }

    switch(stopbit)
    {
      case SerialPort.ONE_STOP_BIT:
        arCmd.add("-cstopb");
        break;
      case SerialPort.TWO_STOP_BITS:
        arCmd.add("cstopb");
        break;
      default:
        throw new BadParameterException("illegal value for stopbit=" + stopbit);
    }

    switch(parita)
    {
      case SerialPort.NO_PARITY:
        arCmd.add("ignpar");
        arCmd.add("-parenb");
        break;
      case SerialPort.EVEN_PARITY:
        arCmd.add("parenb");
        arCmd.add("-parodd");
        break;
      case SerialPort.ODD_PARITY:
        arCmd.add("parenb");
        arCmd.add("parodd");
        break;
      case SerialPort.MARK_PARITY:
        arCmd.add("parmrk");
        break;
      default:
        throw new BadParameterException("illegal value for parity=" + parita);
    }

    if((flowctl & SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) != 0
       || (flowctl & SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) != 0)
    {
      arCmd.add("ixoff");
      arCmd.add("ixon");
    }

    if((flowctl & SerialPort.FLOW_CONTROL_RTS_ENABLED) != 0
       || (flowctl & SerialPort.FLOW_CONTROL_CTS_ENABLED) != 0)
    {
      arCmd.add("crtscts");
    }

    arCmd.add("-echo");

    String[] cmd = StringOper.toArray(arCmd);
    System.out.println("Exec:" + StringOper.join(cmd, ' '));
    ExecHelper.exec(cmd);
  }
}
