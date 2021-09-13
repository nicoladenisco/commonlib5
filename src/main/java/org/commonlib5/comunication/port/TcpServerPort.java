/*
 * Copyright (C) 2017 Nicola De Nisco
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

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.commonlib5.comunication.WaitCharException;
import org.commonlib5.utils.CommonFileUtils;

/**
 * Porta di comunicazione su protocollo TCP/IP.
 *
 * @author Nicola De Nisco
 */
public class TcpServerPort extends AbstractPort
{
  protected ServerSocket serverSocket = null;
  protected Socket clientSocket = null;
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

    if(port == 0)
      throw new IllegalArgumentException("Port must be initalized. Use setParams() before open connection.");

    if(serverSocket == null)
    {
      setPorta("SRV:" + port);
      serverSocket = new ServerSocket(port, 1);
    }
  }

  public void setParams(int port)
  {
    this.port = port;
  }

  public boolean accept(int waitMillis)
     throws Exception
  {
    try
    {
      serverSocket.setSoTimeout(waitMillis);
      clientSocket = serverSocket.accept();
      clientSocket.setSoTimeout(timeout);
      setStream(clientSocket.getInputStream(), clientSocket.getOutputStream());
      return true;
    }
    catch(SocketTimeoutException e)
    {
      return false;
    }
  }

  @Override
  public void closeComm()
  {
    super.closeComm();

    if(clientSocket != null)
    {
      CommonFileUtils.safeClose(clientSocket);
      clientSocket = null;
    }
  }

  @Override
  public boolean isOpen()
  {
    if(clientSocket == null)
    {
      super.closeComm();
      return false;
    }

    if(!clientSocket.isConnected() || clientSocket.isClosed())
      closeComm();

    return super.isOpen();
  }

  public void closeListen()
  {
    if(serverSocket != null)
    {
      CommonFileUtils.safeClose(serverSocket);
      serverSocket = null;
    }
  }

  public boolean isListen()
  {
    return serverSocket != null;
  }

  @Override
  public void setTimeoutMillis(int millis)
  {
    try
    {
      super.setTimeoutMillis(millis);

      if(clientSocket != null)
        clientSocket.setSoTimeout(millis);
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
    return clientSocket == null ? "non connesso" : clientSocket.toString();
  }
}
