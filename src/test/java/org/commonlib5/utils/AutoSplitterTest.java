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

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

/**
 * Test auto splitter.
 *
 * @author Nicola De Nisco
 */
public class AutoSplitterTest
{
  @Test
  public void test1()
  {
    ArrayList<Integer> arInt = new ArrayList<>();
    for(int i = 0; i < 10; i++)
      arInt.add(i);

    MutableInt vt = new MutableInt();
    AutoSplitter.split(arInt, 3, (List<Integer> ls) ->
    {
      for(Integer i : ls)
      {
        assertEquals("Distribution failure", i, vt.toInteger());
        vt.increment();
      }
    });

    assertEquals("Final value must be 10", 10, (long) vt.toInteger());
  }

  @Test
  public void test2()
  {
    ArrayList<Integer> arInt = new ArrayList<>();
    for(int i = 0; i < 100; i++)
      arInt.add(i);

    MutableInt vt = new MutableInt();
    AutoSplitter.split(arInt, 17, (List<Integer> ls) ->
    {
      for(Integer i : ls)
      {
        assertEquals("Distribution failure", i, vt.toInteger());
        vt.increment();
      }
    });

    assertEquals("Final value must be 100", 100, (long) vt.toInteger());
  }
}
