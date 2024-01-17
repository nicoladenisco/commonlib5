/*
 * Copyright (C) 2020 Nicola De Nisco
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

import static junit.framework.Assert.*;
import org.junit.Test;

/**
 * Test per JdbcUrlSplitter.
 *
 * @author Nicola De Nisco
 */
public class JdbcUrlSplitterTest
{
  @Test
  public void testPostgres()
  {
    {
      String url1 = "jdbc:postgresql:caleido";
      JdbcUrlSplitter js1 = new JdbcUrlSplitter(url1);
      //host, port, database, params
      assertEquals("driverName must be postgresql", js1.driverName, "postgresql");
      assertEquals("database must be caleido", js1.database, "caleido");
      assertNull("host must be null", js1.host);
      assertNull("port must be null", js1.port);
      assertNull("params must be null", js1.params);
    }
    {
      String url2 = "jdbc:postgresql://localhost/caleido";
      JdbcUrlSplitter js2 = new JdbcUrlSplitter(url2);
      //host, port, database, params
      assertEquals("driverName must be postgresql", js2.driverName, "postgresql");
      assertEquals("database must be caleido", js2.database, "caleido");
      assertEquals("host must be localhost", js2.host, "localhost");
      assertNull("port must be null", js2.port);
      assertNull("params must be null", js2.params);
    }
    {
      String url3 = "jdbc:postgresql://localhost:5432/caleido";
      JdbcUrlSplitter js3 = new JdbcUrlSplitter(url3);
      //host, port, database, params
      assertEquals("driverName must be postgresql", js3.driverName, "postgresql");
      assertEquals("database must be caleido", js3.database, "caleido");
      assertEquals("host must be localhost", js3.host, "localhost");
      assertEquals("port must be 5432", js3.port, "5432");
      assertNull("params must be null", js3.params);
    }
  }

  @Test
  public void testDerby()
  {
    String url3 = "jdbc:derby://localhost:1527/netld;collation=TERRITORY_BASED:PRIMARY";
    JdbcUrlSplitter js3 = new JdbcUrlSplitter(url3);
    //host, port, database, params
    assertEquals("driverName must be derby", js3.driverName, "derby");
    assertEquals("database must be netld", js3.database, "netld");
    assertEquals("host must be localhost", js3.host, "localhost");
    assertEquals("port must be 1527", js3.port, "1527");
    assertEquals("params must be ...", js3.params, "collation=TERRITORY_BASED:PRIMARY");
  }

  @Test
  public void testOracle()
  {
    String url3 = "jdbc:oracle:thin:@localhost:1521:caspian";
    JdbcUrlSplitter js3 = new JdbcUrlSplitter(url3);
    //host, port, database, params
    assertEquals("driverName must be oracle", js3.driverName, "oracle");
    assertEquals("database must be caspian", js3.database, "caspian");
    assertEquals("host must be localhost", js3.host, "@localhost");
    assertEquals("port must be 1521", js3.port, "1521");
    assertNull("params must be null", js3.params);
  }
}
