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

/**
 * Implementazione di una porta RS485.
 * Le porte RS585 utilizzano un unico cavo per i dati, quindi
 * ogni carattere trasmesso rientra nella porta come se lo avesse
 * spedito una periferica.
 * Questa classe scarta automaticamente i caratteri letti quando
 * sono gli stessi di quelli trasmessi.
 *
 * @author Nicola De Nisco
 * @version 1.0
 */
public class RS485Port extends AbstractSerialPort
{
  public static boolean checkTX = false;
  private static byte[] bCh1 = new byte[1];

  public RS485Port()
  {
  }

  /**
   * Invia un singolo byte alla porta seriale
   * @throws java.lang.Exception
   */
  @Override
  public synchronized void putTxChar(int c) throws Exception
  {
    bCh1[0] = (byte) c;
    putTxBuffer(bCh1, 0, 1);
  }

  /**
   * invia la stringa alla porta seriale
   * @throws java.lang.Exception
   */
  @Override
  public void putTxString(String messageString) throws Exception
  {
    putTxBuffer(messageString.getBytes());
  }

  /**
   * invia l'array di bytes in uscita
   * @throws java.lang.Exception
   */
  @Override
  public void putTxBuffer(byte[] arByte) throws Exception
  {
    putTxBuffer(arByte, 0, arByte.length);
  }

  /**
   * invia l'array di bytes in uscita
   * NOTA: nella RS485 con cablaggio su filo singolo, l'interfaccia
   * rilegge i byte che ha appena inviato in uscita, quindi noi li
   * rileggiamo e li scartiamo (eventualmente confrontando con quelli trasmessi)
   * @throws java.lang.Exception
   */
  @Override
  public synchronized void putTxBuffer(byte[] b, int off, int len) throws Exception
  {
    outputStream.write(b, off, len);
    waitRxCount(len);

    if(checkTX)
    {
      byte[] bR = new byte[len];
      inputStream.read(bR);

      for(int i = 0; i < len; i++)
      {
        if(b[off + i] != bR[i])
          throw new Exception("I bytes inviati non sono gli stessi!!");
      }
    }
    else
    {
      inputStream.read(b, off, len);
    }
  }
}
