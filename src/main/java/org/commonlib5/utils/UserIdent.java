/*
 * Copyright (C) 2016 Nicola De Nisco
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

import java.io.IOException;
import org.commonlib5.exec.ExecHelper;

/**
 * Informazioni utente acquisite dal sistema operativo.
 *
 * @author Nicola De Nisco
 */
public class UserIdent
{
  public static class ii
  {
    public int id;
    public String name;
  }

  public static class UserInfo
  {
    public ii uid, gid;
    public ii[] groups;
  }

  public static UserInfo getCurrentUserIdent()
  {
    try
    {
      if(!OsIdent.isUnix())
        return null;

      UserInfo ui = new UserInfo();

      ExecHelper eh = ExecHelper.exec("id");
      String out = eh.getOutput();
      String ss[] = StringOper.split(out, ' ');
      if(ss.length < 3)
        return null;

      ui.uid = parse(ss[0].substring(4));
      ui.gid = parse(ss[1].substring(4));
      ui.groups = parseGroups(ss[2]);

      return ui;
    }
    catch(IOException ex)
    {
      return null;
    }
  }

  private static ii parse(String s)
  {
    int pos1 = s.indexOf('(');
    int pos2 = s.indexOf(')', pos1);
    if(pos1 == -1 || pos2 == -1)
      return null;

    ii rv = new ii();
    rv.id = StringOper.parse(s.substring(0, pos1), -1);
    rv.name = s.substring(pos1 + 1, pos2);
    return rv;
  }

  private static ii[] parseGroups(String s)
  {
    int pos = s.indexOf('=');
    if(pos != -1)
      s = s.substring(pos + 1);

    String[] ss = StringOper.split(s, ',');
    ii[] rv = new ii[ss.length];
    for(int i = 0; i < ss.length; i++)
      rv[i] = parse(ss[i]);

    return rv;
  }
}
