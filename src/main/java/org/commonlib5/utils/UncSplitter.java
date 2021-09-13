/*
 * Copyright (C) 2018 Nicola De Nisco
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

import java.util.Objects;

/**
 * Split di una path UNC nei suoi componenti.
 * Una path UNC ha sempre la forma //server/condivisione/directory/risorsa.
 * Lo splitter identifica le quattro componenti se esistono.
 * La path UNC può essere ambigua in quanto risorsa può essere una directory
 * o un file; se la path termina con '/' viene considerata parte della directory.
 * Lo stesso si ottiene se alwaysDirectory è vero.
 * <code><pre>
 *  //mioserver/condiviso/uno/due/tre
 *  server = mioserver
 *  condivisione = condiviso
 *  directory = uno/due
 *  risorsa = tre
 *
 *  //mioserver/condiviso/uno/due/tre/
 *  server = mioserver
 *  condivisione = condiviso
 *  directory = uno/due/tre
 *  risorsa = null
 * </pre></code>
 *
 * @author Nicola De Nisco
 */
public class UncSplitter
{
  private String pathUNC, server, condivisione, directory, risorsa;
  private boolean valid = false;

  /**
   * Costruttore con split automatico.
   * La path può essere specificata sia nello stile windows che unix (\\ o /).
   * Viene comunque convertita nello stile unix.
   * @param pathSamba path UNC da analizzare
   */
  public UncSplitter(String pathSamba)
  {
    this(pathSamba, false);
  }

  /**
   * Costruttore con split automatico.
   * La path può essere specificata sia nello stile windows che unix (\\ o /).
   * Viene comunque convertita nello stile unix.
   * @param pathSamba path UNC da analizzare
   * @param alwaysDirectory se vero considera la risorsa come parte della directory
   */
  public UncSplitter(String pathSamba, boolean alwaysDirectory)
  {
    split(pathSamba, alwaysDirectory);
  }

  private void split(String pathSamba, boolean alwaysDirectory)
  {
    pathSamba = pathSamba.replace('\\', '/');
    if(!pathSamba.startsWith("//"))
      return;

    String[] cmp = StringOper.split(pathSamba, '/');
    if(cmp.length < 4 || cmp[2].isEmpty())
      return;

    pathUNC = pathSamba;

    switch(cmp.length)
    {
      default:
        for(int i = 5; i < cmp.length - 1; i++)
          cmp[4] += "/" + cmp[i];
        cmp[5] = cmp[cmp.length - 1];
      case 6:
        risorsa = StringOper.okStrNull(cmp[5]);
      case 5:
        directory = StringOper.okStrNull(cmp[4]);
      case 4:
        condivisione = cmp[3];
        server = cmp[2];
        break;
    }

    // se la UNC termina con '/' vuol dire che non c'è risorsa
    if(risorsa != null && !risorsa.isEmpty() && (alwaysDirectory || pathSamba.endsWith("/")))
    {
      directory += "/" + risorsa;
      risorsa = null;
    }

    valid = true;
  }

  /**
   * Restiuisce la path UNC completa.
   * @return lo stesso valore passato al costruttore
   */
  public String getPathUNC()
  {
    return pathUNC;
  }

  /**
   * Restituisce la componente server.
   * @return server
   */
  public String getServer()
  {
    return server;
  }

  /**
   * Restituisce la componente condivisione.
   * @return condivisione
   */
  public String getCondivisione()
  {
    return condivisione;
  }

  /**
   * Restituisce la share UNC.
   * @return stringa contenente //server/condivisione
   */
  public String getShare()
  {
    return "//" + server + "/" + condivisione;
  }

  /**
   * Restituisce la share UNC.
   * Usa le barre inverse nello stile windows.
   * @return stringa contenente \\server\condivisione
   */
  public String getShareWindows()
  {
    return "\\\\" + server + "\\" + condivisione;
  }

  /**
   * Restituisce la componente directory.
   * @return directory
   */
  public String getDirectory()
  {
    return directory;
  }

  /**
   * Restituisce la componente risorsa.
   * @return risorsa
   */
  public String getRisorsa()
  {
    return risorsa;
  }

  /**
   * Ritorna vero se la path UNC è valida.
   * Se falso il contenuto delle componenti sarà null.
   * @return vero per UNC valida
   */
  public boolean isValid()
  {
    return valid;
  }

  @Override
  public String toString()
  {
    if(!valid)
      return "UncSplitter{invalid}";

    return "UncSplitter{" + "pathUNC=" + pathUNC + ", server=" + server + ", condivisione=" + condivisione + ", directory=" + directory + ", risorsa=" + risorsa + '}';
  }

  @Override
  public int hashCode()
  {
    return valid ? pathUNC.hashCode() : super.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    final UncSplitter other = (UncSplitter) obj;
    if(this.valid ^ other.valid)
      return false;
    if(!Objects.equals(this.pathUNC, other.pathUNC))
      return false;
    return true;
  }
}
