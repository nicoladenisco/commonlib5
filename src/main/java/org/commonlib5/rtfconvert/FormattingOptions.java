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
package org.commonlib5.rtfconvert;

/**
 * Opzioni di formattazione.
 *
 * @author Nicola De Nisco
 */
public class FormattingOptions implements Cloneable
{
  public enum halign
  {
    align_left, align_right, align_center, align_justify, align_error
  };

  public enum valign
  {
    va_normal, va_sub, va_sup
  };
  public boolean chpBold, chpItalic, chpUnderline;
  public valign chpVAlign;
  public int chpFontSize, chpHighlight;
  public Color chpFColor, chpBColor;
  public Font chpFont;
  public int papLeft, papRight, papFirst;
  public int papBefore, papAfter;
  public halign papAlign;
  public boolean papInTbl;

  public FormattingOptions()
  {
    chpBold = chpItalic = chpUnderline = false;
    chpVAlign = valign.va_normal;
    chpFontSize = chpHighlight = 0;
    papLeft = papRight = papFirst = papBefore = papAfter = 0;
    papAlign = halign.align_left;
    papInTbl = false;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof FormattingOptions)
    {
      FormattingOptions opt = (FormattingOptions) obj;
      return chpBold == opt.chpBold && chpItalic == opt.chpItalic
         && chpUnderline == opt.chpUnderline && chpVAlign == opt.chpVAlign
         && chpFontSize == opt.chpFontSize
         && chpFColor == opt.chpFColor && chpBColor == opt.chpBColor
         && chpHighlight == opt.chpHighlight && chpFont == opt.chpFont;
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 59 * hash + (this.chpBold ? 1 : 0);
    hash = 59 * hash + (this.chpItalic ? 1 : 0);
    hash = 59 * hash + (this.chpUnderline ? 1 : 0);
    hash = 59 * hash + (this.chpVAlign != null ? this.chpVAlign.hashCode() : 0);
    hash = 59 * hash + this.chpFontSize;
    hash = 59 * hash + this.chpHighlight;
    hash = 59 * hash + (this.chpFColor != null ? this.chpFColor.hashCode() : 0);
    hash = 59 * hash + (this.chpBColor != null ? this.chpBColor.hashCode() : 0);
    hash = 59 * hash + (this.chpFont != null ? this.chpFont.hashCode() : 0);
    hash = 59 * hash + this.papLeft;
    hash = 59 * hash + this.papRight;
    hash = 59 * hash + this.papFirst;
    hash = 59 * hash + this.papBefore;
    hash = 59 * hash + this.papAfter;
    hash = 59 * hash + (this.papAlign != null ? this.papAlign.hashCode() : 0);
    hash = 59 * hash + (this.papInTbl ? 1 : 0);
    return hash;
  }

  @Override
  public String toString()
  {
    return "FormattingOptions{" + "chpBold=" + chpBold + ", chpItalic=" + chpItalic + ", chpUnderline=" + chpUnderline + ", chpVAlign=" + chpVAlign + ", chpFontSize=" + chpFontSize + ", chpHighlight=" + chpHighlight + ", chpFColor=" + chpFColor + ", chpBColor=" + chpBColor + ", chpFont=" + chpFont + ", papLeft=" + papLeft + ", papRight=" + papRight + ", papFirst=" + papFirst + ", papBefore=" + papBefore + ", papAfter=" + papAfter + ", papAlign=" + papAlign + ", papInTbl=" + papInTbl + '}';
  }

  public FormattingOptions copyFrom(FormattingOptions opt)
  {
    chpBold = opt.chpBold;
    chpItalic = opt.chpItalic;
    chpUnderline = opt.chpUnderline;
    chpVAlign = opt.chpVAlign;
    chpFontSize = opt.chpFontSize;
    chpFColor = opt.chpFColor;
    chpBColor = opt.chpBColor;
    chpHighlight = opt.chpHighlight;
    chpFont = opt.chpFont;
    papLeft = opt.papLeft;
    papRight = opt.papRight;
    papFirst = opt.papFirst;
    papBefore = opt.papBefore;
    papAfter = opt.papAfter;
    papAlign = opt.papAlign;
    papInTbl = opt.papInTbl;
    return this;
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    FormattingOptions rv = new FormattingOptions();
    rv.copyFrom(this);
    return rv;
  }
}

