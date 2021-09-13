/* 
 * Copyright (C) 2017 Nicola De Nisco
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
package org.commonlib5.rtfconvert.lowagie;

import java.util.ArrayDeque;
import java.util.Iterator;
import org.commonlib5.rtfconvert.FormattingOptions;

/**
 * Classe di supporto.
 * @author Nicola De Nisco
 */
public class LowagieFormatter
{
  private ArrayDeque<FormattingOptions> opt_stack = new ArrayDeque<FormattingOptions>();

  public String format(FormattingOptions _opt)
  {
    FormattingOptions last_opt = new FormattingOptions(), opt = _opt.copyFrom(new FormattingOptions());

    String result = "";
    if(!opt_stack.isEmpty())
    {
      int cnt = 0;
      Iterator<FormattingOptions> itr = opt_stack.descendingIterator();
      while(itr.hasNext())
      {
        FormattingOptions fo = itr.next();
        if(fo.equals(opt))
          break;

        cnt++;
      }

      if(cnt == 0)
        return "";

      if(itr.hasNext())
      {
        while(cnt-- > 0)
        {
          result += "</span>";
          opt_stack.pop();
        }
        return result;
      }

      last_opt = opt_stack.peekLast();
    }

    if(last_opt.chpVAlign != FormattingOptions.valign.va_normal
       && last_opt.chpVAlign != opt.chpVAlign)
    {
      int cnt = 0;
      Iterator<FormattingOptions> itr = opt_stack.descendingIterator();
      while(itr.hasNext())
      {
        FormattingOptions fo = itr.next();
        if(fo.chpVAlign == FormattingOptions.valign.va_normal)
          break;
        cnt++;
      }

      while(cnt-- > 0)
      {
        result += "</span>";
        opt_stack.pop();
      }

      last_opt = opt_stack.isEmpty() ? new FormattingOptions() : opt_stack.peekLast();
    }

    String style = "";
    if(opt.chpBold != last_opt.chpBold)
    {
      style += "font-weight:";
      style += opt.chpBold ? "bold" : "normal";
      style += ";";
    }
    if(opt.chpItalic != last_opt.chpItalic)
    {
      style += "font-style:";
      style += opt.chpItalic ? "italic" : "normal";
      style += ";";
    }
    if(opt.chpUnderline != last_opt.chpUnderline)
    {
      style += "text-decoration:";
      style += opt.chpUnderline ? "underline" : "none";
      style += ";";
    }
    if(opt.chpVAlign != FormattingOptions.valign.va_normal)
      opt.chpFontSize = (int) (0.7 * (opt.chpFontSize != 0 ? opt.chpFontSize : 24));
    if(opt.chpFontSize != last_opt.chpFontSize)
    {
      style += "font-size:";
      style += (int) (opt.chpFontSize / 2);
      style += "pt;";
    }
    if(opt.chpVAlign != last_opt.chpVAlign)
    {
      style += "vertical-align:";
      style += opt.chpVAlign == FormattingOptions.valign.va_sub ? "sub" : "super";
      style += ";";
    }
    if(opt.chpFColor != last_opt.chpFColor)
    {
      style += "color:";
      style += opt.chpFColor.r > 0 ? "#" + hex(opt.chpFColor.r & 0xFF)
         + hex(opt.chpFColor.g & 0xFF)
         + hex(opt.chpFColor.b & 0xFF)
               : "WindowText";
      style += ";";
    }
    if(opt.chpBColor != last_opt.chpBColor)
    {
      style += "background-color:";
      style += opt.chpBColor.r > 0 ? "#" + hex(opt.chpBColor.r & 0xFF)
         + hex(opt.chpBColor.g & 0xFF)
         + hex(opt.chpBColor.b & 0xFF)
               : "Window";
      style += ";";
    }
    if(opt.chpHighlight != last_opt.chpHighlight)
    {
      style += "background-color:";
      switch(opt.chpHighlight)
      {
        case 0:
          style += "Window";
          break;
        case 1:
          style += "black";
          break;
        case 2:
          style += "blue";
          break;
        case 3:
          style += "aqua";
          break;
        case 4:
          style += "lime";
          break;
        case 5:
          style += "fuchsia";
          break;
        case 6:
          style += "red";
          break;
        case 7:
          style += "yellow";
          break;
        case 9:
          style += "navy";
          break;
        case 10:
          style += "teal";
          break;
        case 11:
          style += "green";
          break;
        case 12:
          style += "purple";
          break;
        case 13:
          style += "maroon";
          break;
        case 14:
          style += "olive";
          break;
        case 15:
          style += "gray";
          break;
        case 16:
          style += "silver";
          break;
      }
      style += ";";
    }
    if(opt.chpFont != last_opt.chpFont)
    {
      style += "font-family:'";
      style += opt.chpFont.name.isEmpty() ? "serif" : opt.chpFont.name;
      style += "'";
      switch(opt.chpFont.family)
      {
        case ff_serif:
          style += ", serif";
          break;
        case ff_sans_serif:
          style += ", sans-serif";
          break;
        case ff_cursive:
          style += ", cursive";
          break;
        case ff_fantasy:
          style += ", fantasy";
          break;
        case ff_monospace:
          style += ", monospace";
          break;
      }
      style += ";";
    }

    opt_stack.push(opt);
    return result + "<span style=\"" + style + "\">";
  }

  public String close()
  {
    String result = "";
    Iterator<FormattingOptions> itr = opt_stack.iterator();
    while(itr.hasNext())
    {
      itr.next();
      result += "</span>";
    }
    return result;
  }

  public void clear()
  {
    opt_stack.clear();
  }

  private String hex(int i)
  {
    return Integer.toHexString(i);
  }
}
