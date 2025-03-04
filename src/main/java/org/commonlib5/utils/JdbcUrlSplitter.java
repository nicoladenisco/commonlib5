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
package org.commonlib5.utils;

import java.util.Objects;

/**
 * Split di una url JDBC nei componenti.
 *
 * @author Nicola De Nisco
 */
public class JdbcUrlSplitter
{
  public String driverName, host, port, database, params;

  /**
   * Costruttore con detect automatico del driver.
   * @param jdbcUrl uri jdbc da splittare
   */
  public JdbcUrlSplitter(String jdbcUrl)
  {
    int pos1;

    jdbcUrl = StringOper.okStrNull(jdbcUrl);
    if(jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")
       || (pos1 = jdbcUrl.indexOf(':', 5)) == -1)
      throw new IllegalArgumentException("Invalid JDBC url.");

    driverName = jdbcUrl.substring(5, pos1);
    split(jdbcUrl);
  }

  public void split(String jdbcUrl)
  {
    switch(driverName.toLowerCase())
    {
      case "oracle":
        splitOracle(jdbcUrl);
        break;

      case "hsqldb":
        splitHSql(jdbcUrl);
        break;

      default:
        splitClassic(jdbcUrl);
        break;
    }
  }

  public String merge()
  {
    switch(driverName.toLowerCase())
    {
      case "oracle":
        return mergeOracle();

      case "hsqldb":
        return mergeHSql();

      default:
        return mergeClassic();
    }
  }

  /**
   * Split della forma classica.
   * Estrae i componenti di una uri JDBC del tipo: <br>
   * String url = "jdbc:derby://localhost:1527/netld;collation=TERRITORY_BASED:PRIMARY"; <br>
   * nelle rispettive variabili pubbliche.
   * @param jdbcUrl uri jdbc da splittare
   */
  public void splitClassic(String jdbcUrl)
  {
    int pos, pos1, pos2;
    String connUri;

    jdbcUrl = StringOper.okStrNull(jdbcUrl);
    if(jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")
       || (pos1 = jdbcUrl.indexOf(':', 5)) == -1)
      throw new IllegalArgumentException("Invalid JDBC url.");

    driverName = jdbcUrl.substring(5, pos1);
    if((pos2 = jdbcUrl.indexOf(';', pos1)) == -1)
    {
      connUri = jdbcUrl.substring(pos1 + 1);
    }
    else
    {
      connUri = jdbcUrl.substring(pos1 + 1, pos2);
      params = jdbcUrl.substring(pos2 + 1);
    }

    if(connUri.startsWith("//"))
    {
      if((pos = connUri.indexOf('/', 2)) != -1)
      {
        host = connUri.substring(2, pos);
        database = connUri.substring(pos + 1);

        if((pos = host.indexOf(':')) != -1)
        {
          port = host.substring(pos + 1);
          host = host.substring(0, pos);
        }
      }
    }
    else
    {
      database = connUri;
    }
  }

  /**
   * Merge della forma classica.
   * Fonde i componenti di una uri JDBC del tipo: <br>
   * String url = "jdbc:derby://localhost:1527/netld;collation=TERRITORY_BASED:PRIMARY"; <br>
   * nelle rispettive variabili pubbliche.
   * @return
   */
  public String mergeClassic()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:");
    sb.append(driverName);

    if(host != null)
    {
      sb.append("://");
      sb.append(host);
      if(port != null)
      {
        sb.append(":");
        sb.append(port);
      }
      sb.append("/");
      sb.append(database);
    }
    else
    {
      sb.append(":");
      sb.append(database);
    }

    if(params != null)
    {
      sb.append(";");
      sb.append(params);
    }

    return sb.toString();
  }

  /**
   * Split della forma oracle thin.
   * Estrae i componenti di una uri JDBC del tipo: <br>
   * String url = "jdbc:oracle:thin:@localhost:1521:caspian"; <br>
   * nelle rispettive variabili pubbliche.
   * @param jdbcUrl uri jdbc da splittare
   */
  public void splitOracle(String jdbcUrl)
  {
    int pos1;

    jdbcUrl = StringOper.okStrNull(jdbcUrl);
    if(jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")
       || (pos1 = jdbcUrl.indexOf(':', 5)) == -1)
      throw new IllegalArgumentException("Invalid JDBC url.");

    String[] parts = jdbcUrl.split("\\:");
    driverName = parts[1];
    if(parts.length > 5)
    {
      host = parts[3].substring(1);
      port = parts[4];
      database = parts[5];
    }
    else
    {
      host = parts[3];
      database = parts[4];
    }
  }

  /**
   * Merge della forma oracle thin.
   * Fonde i componenti di una uri JDBC del tipo: <br>
   * String url = "jdbc:oracle:thin:@localhost:1521:caspian"; <br>
   * nelle rispettive variabili pubbliche.
   * @return
   */
  public String mergeOracle()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:");
    sb.append(driverName);
    sb.append(":thin");

    if(host != null)
    {
      sb.append(":@");
      sb.append(host);
      if(port != null)
      {
        sb.append(":");
        sb.append(port);
      }
      sb.append(":");
      sb.append(database);
    }
    else
    {
      sb.append(":");
      sb.append(database);
    }

    if(params != null)
    {
      sb.append(";");
      sb.append(params);
    }

    return sb.toString();
  }

  /**
   * Split della forma hsql.
   * Estrae i componenti di una uri JDBC del tipo: <br>
   * String url = "jdbc:hsqldb:data/tutorial"; <br>
   * nelle rispettive variabili pubbliche.
   * @param jdbcUrl uri jdbc da splittare
   */
  public void splitHSql(String jdbcUrl)
  {
    int pos1;

    jdbcUrl = StringOper.okStrNull(jdbcUrl);
    if(jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")
       || (pos1 = jdbcUrl.indexOf(':', 5)) == -1)
      throw new IllegalArgumentException("Invalid JDBC url.");

    String[] parts = jdbcUrl.split("\\:");
    driverName = parts[1];
    database = parts[2];
  }

  /**
   * Merge della forma hsql.
   * Fonde i componenti di una uri JDBC del tipo: <br>
   * String url = "jdbc:hsqldb:data/tutorial"; <br>
   * nelle rispettive variabili pubbliche.
   * @return
   */
  public String mergeHSql()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:");
    sb.append(driverName);

    if(host != null)
    {
      sb.append("://");
      sb.append(host);
      if(port != null)
      {
        sb.append(":");
        sb.append(port);
      }
      sb.append("/");
      sb.append(database);
    }
    else
    {
      sb.append(":");
      sb.append(database);
    }

    if(params != null)
    {
      sb.append(";");
      sb.append(params);
    }

    return sb.toString();
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.driverName);
    hash = 79 * hash + Objects.hashCode(this.host);
    hash = 79 * hash + Objects.hashCode(this.port);
    hash = 79 * hash + Objects.hashCode(this.database);
    hash = 79 * hash + Objects.hashCode(this.params);
    return hash;
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
    final JdbcUrlSplitter other = (JdbcUrlSplitter) obj;
    if(!Objects.equals(this.driverName, other.driverName))
      return false;
    if(!Objects.equals(this.host, other.host))
      return false;
    if(!Objects.equals(this.port, other.port))
      return false;
    if(!Objects.equals(this.database, other.database))
      return false;
    if(!Objects.equals(this.params, other.params))
      return false;
    return true;
  }

  @Override
  public String toString()
  {
    return "JdbcUrlSplitter{"
       + "driverName=" + driverName
       + ", host=" + host
       + ", port=" + port
       + ", database=" + database
       + ", params=" + params
       + '}';
  }
}
