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
 * Test di UncSplitter.
 *
 * @author Nicola De Nisco
 */
public class UncSplitterTest
{
  public static final String UNC = "//server/condivisione/uno/due/tre/stella";

  @Test
  public void test1()
  {
    UncSplitter u1 = new UncSplitter(UNC);
    assertEquals("getServer() must be server", u1.getServer(), "server");
    assertEquals("getCondivisione() must be condivisione", u1.getCondivisione(), "condivisione");
    assertEquals("getDirectory() must be uno/due/tre", u1.getDirectory(), "uno/due/tre");
    assertEquals("getRisorsa() must be stella", u1.getRisorsa(), "stella");
    assertEquals("getShare() must be //server/condivisione", u1.getShare(), "//server/condivisione");
    assertEquals("getShareWindows() must be \\\\server\\condivisione", u1.getShareWindows(), "\\\\server\\condivisione");
  }

  @Test
  public void test2()
  {
    UncSplitter u1 = new UncSplitter(UNC, true);
    assertEquals("getServer() must be server", u1.getServer(), "server");
    assertEquals("getCondivisione() must be condivisione", u1.getCondivisione(), "condivisione");
    assertEquals("getDirectory() must be uno/due/tre/stella", u1.getDirectory(), "uno/due/tre/stella");
    assertNull("getRisorsa() must be null", u1.getRisorsa());
    assertEquals("getShare() must be //server/condivisione", u1.getShare(), "//server/condivisione");
    assertEquals("getShareWindows() must be \\\\server\\condivisione", u1.getShareWindows(), "\\\\server\\condivisione");
  }
}
