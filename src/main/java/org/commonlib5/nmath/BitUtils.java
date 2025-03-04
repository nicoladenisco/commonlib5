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
package org.commonlib5.nmath;

/**
 * Utilità generiche per manipolazione a bit.
 * ATTENZIONE: solo per LittleEndian.
 *
 * @author Nicola De Nisco
 */
public class BitUtils
{
  /**
   * Produce un long (64bit) a partire dagli 8 byte componenti.
   * @param b0
   * @param b1
   * @param b2
   * @param b3
   * @param b4
   * @param b5
   * @param b6
   * @param b7
   * @return
   */
  static public long makeLong(
     byte b0, byte b1, byte b2, byte b3,
     byte b4, byte b5, byte b6, byte b7)
  {
    return ((((long) b7) << 56)
       | (((long) b6 & 0xff) << 48)
       | (((long) b5 & 0xff) << 40)
       | (((long) b4 & 0xff) << 32)
       | (((long) b3 & 0xff) << 24)
       | (((long) b2 & 0xff) << 16)
       | (((long) b1 & 0xff) << 8)
       | (((long) b0 & 0xff)));
  }

  /**
   * Produce un int (32bit) a partire dai 4 byte componenti.
   * @param b0
   * @param b1
   * @param b2
   * @param b3
   * @return
   */
  static public int makeInt(byte b0, byte b1, byte b2, byte b3)
  {
    return (((b3) << 24)
       | ((b2 & 0xff) << 16)
       | ((b1 & 0xff) << 8)
       | ((b0 & 0xff)));
  }

  /**
   * Produce uno short (16bit) a partire dai 2 byte componenti.
   * @param b0
   * @param b1
   * @return
   */
  static public short makeShort(byte b0, byte b1)
  {
    return (short) ((b1 << 8) | (b0 & 0xff));
  }

  /**
   * Legge i 2 byte dell'array a partire dall'offset indicato
   * e restituisce lo short relativo.
   * @param ba array con i bytes
   * @param off offset all'interno dell'array
   * @return valore richiesto
   */
  public static int getWordValue(byte[] ba, int off)
  {
    return makeShort(ba[off], ba[off + 1]) & 0xFFFF;
  }

  /**
   * Legge i 4 byte dell'array a partire dall'offset indicato
   * e restituisce l'int relativo.
   * @param ba array con i bytes
   * @param off offset all'interno dell'array
   * @return valore richiesto
   */
  public static int getDWordValue(byte[] ba, int off)
  {
    return makeInt(ba[off], ba[off + 1], ba[off + 2], ba[off + 3]);
  }

  /**
   * Legge gli 8 byte dell'array a partire dall'offset indicato
   * e restituisce il long relativo.
   * @param ba array con i bytes
   * @param off offset all'interno dell'array
   * @return valore richiesto
   */
  public static long getQWordValue(byte[] ba, int off)
  {
    return makeLong(
       ba[off], ba[off + 1], ba[off + 2], ba[off + 3],
       ba[off + 4], ba[off + 5], ba[off + 6], ba[off + 7]);
  }
}
