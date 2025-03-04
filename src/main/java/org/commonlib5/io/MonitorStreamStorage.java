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

import java.io.IOException;

/**
 * Interfaccia di un ricevitore astratto di messaggi di monitoraggio.
 *
 * @author Nicola De Nisco
 */
public interface MonitorStreamStorage
{
  /**
   * Riceve i byte da aggiungere alla coda.
   * I byte vengono copiati generando una nuova
   * entry nella coda.
   * @param type marcatore del messaggio
   * @param b array di byte da aggiungere
   * @throws java.io.IOException
   */
  void addToStorage(int type, byte[] b)
     throws IOException;

  /**
   * Riceve i byte da aggiungere alla coda.
   * I byte vengono copiati generando una nuova
   * entry nella coda.
   * @param type marcatore del messaggio
   * @param b array di byte da aggiungere
   * @param offset offset all'interno di b
   * @param len numero di byte da aggiungere
   * @throws java.io.IOException
   */
  void addToStorage(int type, byte[] b, int offset, int len)
     throws IOException;

  /**
   * Riceve un byte da aggiungere alla coda.
   * @param type marcatore del messaggio
   * @param byteValue valore del byte da aggiungere
   * @throws IOException
   */
  void addToStorage(int type, int byteValue)
     throws IOException;

  /**
   * Aggiunge un commento.
   * Durante le operazioni è utile sapere dei dettagli.
   * @param type marcatore del commento
   * @param comment commento da visualizzare
   * @throws IOException
   */
  void addComment(int type, String comment)
     throws IOException;

  /**
   * Invia tutti i dati accumulati verso l'output.
   * @throws IOException
   */
  void flush()
     throws IOException;
}
