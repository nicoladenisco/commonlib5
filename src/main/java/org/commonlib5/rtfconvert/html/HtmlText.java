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
package org.commonlib5.rtfconvert.html;

import org.commonlib5.rtfconvert.FormattingOptions;

/**
 * Porzione di testo html.
 * 
 * @author Nicola De Nisco
 */
public class HtmlText
{
  private FormattingOptions opt = null;
  private HtmlFormatter fmt = new HtmlFormatter();
  private String text = "";

  public HtmlText(FormattingOptions opt)
  {
    this.opt = opt;
  }

  public String str()
  {
    return text;
  }

  public void write(String s) throws Exception
  {
    text += fmt.format(opt) + s;
  }

  public String close()
  {
    return fmt.close();
  }

  public void write(char c) throws Exception
  {
    text += fmt.format(opt) + c;
  }

  public void clear()
  {
    text = "";
    fmt.clear();
  }

  @Override
  public String toString()
  {
    return text;
  }

  public boolean isEmpty()
  {
    return text.isEmpty();
  }
}
