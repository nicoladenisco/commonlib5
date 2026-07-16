/*
 * Copyright (C) 2026 Nicola De Nisco
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test per ByteSizeParser.
 *
 * @author Nicola De Nisco
 */
public class ByteSizeParserTest
{
  @Test
  public void testParseBytes_NoSuffix()
  {
    assertEquals(512L, ByteSizeParser.parseBytes("512"));
  }

  @Test
  public void testParseBytes_ShortSuffix()
  {
    assertEquals(4L * 1024L, ByteSizeParser.parseBytes("4k"));
    assertEquals(2L * 1024L * 1024L, ByteSizeParser.parseBytes("2m"));
  }

  @Test
  public void testParseBytes_LongSuffix()
  {
    assertEquals(10L * 1024L * 1024L * 1024L, ByteSizeParser.parseBytes("10GB"));
    assertEquals(1L * 1024L * 1024L * 1024L, ByteSizeParser.parseBytes("1GiB"));
  }

  @Test
  public void testParseBytes_WithSpacesAndSign()
  {
    assertEquals(8L * 1024L, ByteSizeParser.parseBytes(" 8 kb "));
    assertEquals(-3L * 1024L, ByteSizeParser.parseBytes("-3K"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBytes_InvalidFormat()
  {
    ByteSizeParser.parseBytes("12xb");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBytes_Empty()
  {
    ByteSizeParser.parseBytes("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBytes_Null()
  {
    ByteSizeParser.parseBytes(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBytes_Overflow()
  {
    ByteSizeParser.parseBytes("9000000p");
  }
}