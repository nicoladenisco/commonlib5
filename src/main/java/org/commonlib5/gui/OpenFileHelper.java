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
package org.commonlib5.gui;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import org.commonlib5.exec.ExecHelper;
import org.commonlib5.utils.OsIdent;
import org.commonlib5.utils.StringOper;

/**
 * Un aiuto per aprire un file in una applicazione swing.
 *
 * @author Nicola De Nisco
 */
public class OpenFileHelper
{
  public void open(File file)
     throws IOException
  {
    if(OsIdent.isWindows())
    {
      String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
      Runtime.getRuntime().exec(cmd);
    }
    else
    {
      if(!GraphicsEnvironment.isHeadless())
      {
        try
        {
          Desktop.getDesktop().open(file);
        }
        catch(Throwable t)
        {
          fallback(file);
        }
      }
      else
      {
        fallback(file);
      }
    }
  }

  public void edit(File file)
     throws IOException
  {
    if(OsIdent.isWindows())
    {
      String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
      Runtime.getRuntime().exec(cmd);
    }
    else
    {
      if(!GraphicsEnvironment.isHeadless())
      {
        try
        {
          Desktop.getDesktop().edit(file);
        }
        catch(Throwable t)
        {
          fallback(file);
        }
      }
      else
      {
        fallback(file);
      }
    }
  }

  protected void fallback(File file)
     throws IOException
  {
    switch(OsIdent.checkOStype())
    {
      case OsIdent.OS_LINUX:
        fallbackLinux(file);
        break;
      case OsIdent.OS_MACOSX:
        fallbackMacosx(file);
        break;
    }
  }

  protected void fallbackLinux(File file)
     throws IOException
  {
    String dn = StringOper.okStr(System.getenv("XDG_SESSION_DESKTOP"));

    switch(dn)
    {
      case "cinnamon":
      case "xfce":
      case "gnome":
        ExecHelper.execUsingShell("xed '" + file.getAbsolutePath() + "'");
        break;
      case "kde":
        ExecHelper.execUsingShell("kate '" + file.getAbsolutePath() + "'");
        break;

      case "":
      default:
        ExecHelper.execUsingShell("vi '" + file.getAbsolutePath() + "'");
        break;
    }
  }

  protected void fallbackMacosx(File file)
     throws IOException
  {
    ExecHelper.execUsingShell("open -t '" + file.getAbsolutePath() + "'");
  }
}
