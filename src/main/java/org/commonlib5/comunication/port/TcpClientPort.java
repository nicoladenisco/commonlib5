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

import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.commonlib5.comunication.WaitCharException;
import org.commonlib5.utils.CommonFileUtils;

/**
 * Porta di comunicazione su protocollo TCP/IP.
 *
 * @author Nicola De Nisco
 */
public class TcpClientPort extends AbstractPort
{
  protected Socket sock = null;
  protected String host = null;
  protected int port = 0;

  /**
   * Apre il socket a collega gli stream.
   * @param sData
   * @throws Exception
   */
  @Override
  public void initComm(String sData)
     throws Exception
  {
    super.initComm(sData);

    if(host == null || port == 0)
      throw new IllegalArgumentException("Host and/or port must be initalized. Use setParams() before open connection.");

    // imposta porta corretta
    setPorta(host + ":" + port);

    sock = new Socket(host, port);
    sock.setSoTimeout(timeout);

    // imposta gli stream di input/output
    setStream(sock.getInputStream(), sock.getOutputStream());
  }

  public void setParams(String host, int port)
  {
    this.host = host;
    this.port = port;
  }

  @Override
  public void closeComm()
  {
    super.closeComm();

    if(sock != null)
    {
      CommonFileUtils.safeClose(sock);
      sock = null;
    }
  }

  @Override
  public boolean isOpen()
  {
    if(sock == null)
    {
      super.closeComm();
      return false;
    }

    if(!sock.isConnected() || sock.isClosed())
      closeComm();

    return super.isOpen();
  }

  @Override
  public void setTimeoutMillis(int millis)
  {
    try
    {
      super.setTimeoutMillis(millis);

      if(sock != null)
        sock.setSoTimeout(millis);
    }
    catch(SocketException ex)
    {
      Logger.getLogger(TcpClientPort.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public int getRxChar()
     throws Exception
  {
    int ch = super.getRxCharBlocking();
    if(ch == -1)
      throw new WaitCharException("WaitRxCount(1): timeout attesa risposta.");
    return ch;
  }

  public String getClientInfo()
  {
    return sock == null ? "non connesso" : sock.toString();
  }
}
