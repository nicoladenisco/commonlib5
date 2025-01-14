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

import java.awt.event.ActionEvent;
import org.junit.*;

/**
 * Test per OSXProxy.java.
 *
 * @author Nicola De Nisco
 */
public class OSXProxyTest
{

  public OSXProxyTest()
  {
  }

  @BeforeClass
  public static void setUpClass()
  {
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp()
  {

  }

  @After
  public void tearDown()
  {
  }

  /**
   * Test of init method, of class OSXProxy.
   */
  @Test
  public void testInit()
  {
    System.out.println("init");

    if(!OsIdent.isMac())
    {
      System.out.println("Questo test può funzionare solo su Mac OSX");
      return;
    }

    OSXProxy instance = new OSXProxy();
    instance.init("prova");

    instance.setAboutHandler((e) -> dumpEvent(e));
  }

  private boolean dumpEvent(ActionEvent e)
  {
    System.out.println("** " + e);
    return true;
  }

}
