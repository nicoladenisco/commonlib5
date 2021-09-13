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


import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import org.commonlib5.rtfconvert.Color;
import org.commonlib5.rtfconvert.Font;
import org.commonlib5.rtfconvert.FormattingOptions;
import org.commonlib5.rtfconvert.RtfKeyword;
import org.commonlib5.rtfconvert.TableCell;
import org.commonlib5.rtfconvert.TableCellDef;
import org.commonlib5.rtfconvert.TableCellDefs;
import org.commonlib5.rtfconvert.TableRow;

/**
 *
 * @author nicola
 */
public class LowagieConverter {
  public static final char EOF = '\uFFFF';

  public static class CvtData
  {
    public int iDocWidth = 0;
    public int iMarginLeft = 0;
    public String title = null;
    public ArrayList<Element> arElements = new ArrayList<Element>();
  }

  protected static int rint(double f)
  {
    return (int) Math.round(f);
  }

  protected void skip_group(PushbackReader inRtf) throws Exception
  {
    int c = 0, cnt = 1;
    while(cnt != 0)
    {
      c = inRtf.read();
      switch(c)
      {
        case '{':
          cnt++;
          break;
        case '}':
          cnt--;
          break;
        case '\\':
        {
          RtfKeyword kw = new RtfKeyword(inRtf);
          if(!kw.is_control_char() && kw.keyword() == RtfKeyword.keyword_type.rkw_bin
             && kw.parameter() > 0)
          {
            inRtf.skip(kw.parameter());
          }
          break;
        }
      }
    }
  }

  public CvtData convert(Reader inRtf) throws Exception
  {
    PushbackReader pbRtf = new PushbackReader(inRtf, 1024);
    ArrayList<Color> colortbl = new ArrayList<Color>();
    HashMap<Integer, Font> fonttbl = new HashMap<Integer, Font>();
    String title = "";

    boolean bAsterisk = false;
    Stack<FormattingOptions> foStack = new Stack<FormattingOptions>();
    FormattingOptions curOptions = new FormattingOptions();
    ArrayList<Element> arElements = new ArrayList<Element>();
    LowagieText parText = new LowagieText(curOptions);

    /* CellDefs in rtf are really queer. We'll keep a list of them in main()
    and will give an iterator into this list to a row */
    ArrayList<TableCellDefs> CellDefsList = new ArrayList<TableCellDefs>();
    TableCellDefs CurCellDefs = new TableCellDefs();
    TableCellDef tcdCurCellDef = new TableCellDef();
    TableCell tcCurCell = new TableCell();
    TableRow trCurRow = new TableRow();
    LowagieTable tblCurTable = new LowagieTable();
    int iLastRowLeft = 0, iLastRowHeight = 0;
    String tempStr;

    boolean bInTable = false;
    int iDocWidth = 12240;
    int iMarginLeft = 1800;
    char carattere;

    while((carattere = (char) pbRtf.read()) != EOF)
    {
      switch(carattere)
      {
        case '\\':
        {
          RtfKeyword kw = new RtfKeyword(pbRtf);
          if(kw.is_control_char())
          {
            switch(kw.control_char())
            {
              case '\\':
              case '{':
              case '}':
                parText.write(kw.control_char());
                break;
              case '\'':
              {
                String stmp = "";
                stmp += (char) pbRtf.read();
                stmp += (char) pbRtf.read();
                int code = Integer.parseInt(stmp, 16);
                switch(code)
                {
                  case 167:
                    parText.write("&bull;");
                    break;
                  case 188:
                    parText.write("&hellip;");
                    break;
                  default:
                    parText.write((char) code);
                }
                break;
              }
              case '*':
                bAsterisk = true;
                break;
              case '~':
                parText.write("&nbsp;");
                break;
            }
          }
          else //kw3.is_control_char
          {
            if(bAsterisk)
            {
              bAsterisk = false;
              skip_group(pbRtf);
            }
            else
            {
              switch(kw.keyword())
              {
                case rkw_filetbl:
                case rkw_stylesheet:
                case rkw_header:
                case rkw_footer:
                case rkw_headerf:
                case rkw_footerf:
                case rkw_pict:
                case rkw_object:
                  // we'll skip such groups
                  skip_group(pbRtf);
                  break;
                // document title
                case rkw_info:
                {
                  int depth = 1;
                  boolean in_title = false;
                  while(depth > 0)
                  {
//                     std::cout<<String(buf_in).substr(0,20)<<"\t"<<depth<<std::endl;
                    if((carattere = (char) pbRtf.read()) == EOF)
                      break;

                    switch(carattere)
                    {
                      case '\\':
                      {
                        RtfKeyword kw2 = new RtfKeyword(pbRtf);
                        if(kw2.keyword() == RtfKeyword.keyword_type.rkw_title)
                          in_title = true;
                        break;
                      }
                      case '{':
                        ++depth;
                        break;
                      case '}':
                        --depth;
                        in_title = false;
                        break;
                      default:
                        if(in_title)
                          title += carattere;
                        break;
                    }
                  }
                  break;
                }
                // color table
                case rkw_colortbl:
                {
                  Color clr = new Color();
                  while((carattere = (char) pbRtf.read()) != EOF && carattere != '}')
                  {
                    switch(carattere)
                    {
                      case '\\':
                      {
                        RtfKeyword kw3 = new RtfKeyword(pbRtf);
                        switch(kw3.keyword())
                        {
                          case rkw_red:
                            clr.r = kw3.parameter();
                            break;
                          case rkw_green:
                            clr.g = kw3.parameter();
                            break;
                          case rkw_blue:
                            clr.b = kw3.parameter();
                            break;
                        }
                        break;
                      }
                      case ';':
                        colortbl.add(clr);
                        break;
                      default:
                        break;
                    }
                  }
                  break;
                }
                // font table
                case rkw_fonttbl:
                {
                  Font fnt = new Font();
                  int font_num = 0;
                  boolean full_name = false;
                  boolean in_font = false;
                  while(!((carattere = (char) pbRtf.read()) == '}' && !in_font))
                  {
                    switch(carattere)
                    {
                      case '\\':
                      {
                        RtfKeyword kw4 = new RtfKeyword(pbRtf);
                        if(kw4.is_control_char() && kw4.control_char() == '*')
                          skip_group(pbRtf);
                        else
                          switch(kw4.keyword())
                          {
                            case rkw_f:
                              font_num = kw4.parameter();
                              break;
                            case rkw_fprq:
                              fnt.pitch = kw4.parameter();
                              break;
                            case rkw_fcharset:
                              fnt.charset = kw4.parameter();
                              break;
                            case rkw_fnil:
                              fnt.family = Font.font_family.ff_none;
                              break;
                            case rkw_froman:
                              fnt.family = Font.font_family.ff_serif;
                              break;
                            case rkw_fswiss:
                              fnt.family = Font.font_family.ff_sans_serif;
                              break;
                            case rkw_fmodern:
                              fnt.family = Font.font_family.ff_monospace;
                              break;
                            case rkw_fscript:
                              fnt.family = Font.font_family.ff_cursive;
                              break;
                            case rkw_fdecor:
                              fnt.family = Font.font_family.ff_fantasy;
                              break;
                          }
                        break;
                      }
                      case '{':
                        in_font = true;
                        break;
                      case '}':
                        in_font = false;
                        fonttbl.put(font_num, fnt);
                        fnt = new Font();
                        full_name = false;
                        break;
                      case ';':
                        full_name = true;
                        break;
                      default:
                        if(!full_name && in_font)
                          fnt.name += carattere;
                        break;
                    }
                  }
                  break;
                }
                // special characters
                case rkw_line:
                case rkw_softline:
                  parText.write("<br>");
                  break;
                case rkw_tab:
                  parText.write("&nbsp;&nbsp;");  // maybe, this can be done better
                  break;
                case rkw_enspace:
                case rkw_emspace:
                  parText.write("&nbsp;");
                  break;
                case rkw_qmspace:
                  parText.write("&thinsp;");
                  break;
                case rkw_endash:
                  parText.write("&ndash;");
                  break;
                case rkw_emdash:
                  parText.write("&mdash;");
                  break;
                case rkw_bullet:
                  parText.write("&bull;");
                  break;
                case rkw_lquote:
                  parText.write("&lsquo;");
                  break;
                case rkw_rquote:
                  parText.write("&rsquo;");
                  break;
                case rkw_ldblquote:
                  parText.write("&ldquo;");
                  break;
                case rkw_rdblquote:
                  parText.write("&rdquo;");
                  break;
                // paragraph formatting
                case rkw_ql:
                  curOptions.papAlign = FormattingOptions.halign.align_left;
                  break;
                case rkw_qr:
                  curOptions.papAlign = FormattingOptions.halign.align_right;
                  break;
                case rkw_qc:
                  curOptions.papAlign = FormattingOptions.halign.align_center;
                  break;
                case rkw_qj:
                  curOptions.papAlign = FormattingOptions.halign.align_justify;
                  break;
                case rkw_fi:
                  curOptions.papFirst = (int) rint(kw.parameter() / 20);
                  break;
                case rkw_li:
                  curOptions.papLeft = (int) rint(kw.parameter() / 20);
                  break;
                case rkw_ri:
                  curOptions.papRight = (int) rint(kw.parameter() / 20);
                  break;
                case rkw_sb:
                  curOptions.papBefore = (int) rint(kw.parameter() / 20);
                  break;
                case rkw_sa:
                  curOptions.papAfter = (int) rint(kw.parameter() / 20);
                  break;
                case rkw_pard:
                  curOptions.papBefore = curOptions.papAfter = 0;
                  curOptions.papLeft = curOptions.papRight = 0;
                  curOptions.papFirst = 0;
                  curOptions.papAlign = FormattingOptions.halign.align_left;
                  curOptions.papInTbl = false;
                  break;
                case rkw_par:
                case rkw_sect:
                {
                  Paragraph p = getParagraph(curOptions, parText);

                  if(!bInTable)
                  {
                    arElements.add(p);
                  }
                  else
                  {
                    if(curOptions.papInTbl)
                    {
                      // TODO: aggiunge testo nella cella
                      // tcCurCell.Text += tempStr;
                    }
                    else
                    {
                      // TODO: aggiunge tabella
                      // html += tblCurTable.makeHtml() + tempStr;
                      bInTable = false;
                      tblCurTable = new LowagieTable();
                    }
                  }

                  parText.clear();
                  break;
                }
                // character formatting
                case rkw_super:
                  curOptions.chpVAlign =
                     kw.parameter() == 0 ? FormattingOptions.valign.va_normal
                     : FormattingOptions.valign.va_sup;
                  break;
                case rkw_sub:
                  curOptions.chpVAlign =
                     kw.parameter() == 0 ? FormattingOptions.valign.va_normal
                     : FormattingOptions.valign.va_sub;
                  break;
                case rkw_b:
                  curOptions.chpBold = !(kw.parameter() == 0);
                  break;
                case rkw_i:
                  curOptions.chpItalic = !(kw.parameter() == 0);
                  break;
                case rkw_ul:
                  curOptions.chpUnderline = !(kw.parameter() == 0);
                  break;
                case rkw_ulnone:
                  curOptions.chpUnderline = false;
                  break;
                case rkw_fs:
                  curOptions.chpFontSize = kw.parameter();
                  break;
                case rkw_cf:
                  curOptions.chpFColor = colortbl.get(kw.parameter());
                  break;
                case rkw_cb:
                  curOptions.chpBColor = colortbl.get(kw.parameter());
                  break;
                case rkw_highlight:
                  curOptions.chpHighlight = kw.parameter();
                  break;
                case rkw_f:
                  curOptions.chpFont = fonttbl.get(kw.parameter());
                  break;
                case rkw_plain:
                  curOptions.chpBold = curOptions.chpItalic = curOptions.chpUnderline = false;
                  curOptions.chpVAlign = FormattingOptions.valign.va_normal;
                  curOptions.chpFontSize = curOptions.chpHighlight = 0;
                  curOptions.chpFColor = curOptions.chpBColor = new Color();
                  curOptions.chpFont = new Font();
                  break;
                // table formatting
                case rkw_intbl:
                  curOptions.papInTbl = true;
                  break;
                case rkw_trowd:
                  CurCellDefs = new TableCellDefs();
                  CellDefsList.add(CurCellDefs);

                case rkw_row:
                  if(!trCurRow.Cells.isEmpty())
                  {
                    trCurRow.CellDefs = CurCellDefs;

                    if(trCurRow.Left == -1000)
                      trCurRow.Left = iLastRowLeft;
                    if(trCurRow.Height == -1000)
                      trCurRow.Height = iLastRowHeight;
                    tblCurTable.add(trCurRow);
                    trCurRow = new TableRow();
                  }
                  bInTable = true;
                  break;
                case rkw_cell:
                  // TODO: aggiunge cella
                  //tempStr = getHtmlStyleString(curOptions) + parText.str()
                  //   + "&nbsp;" + parText.close() + "</p>\n";
                  //tcCurCell.Text += tempStr;
                  parText.clear();
                  trCurRow.Cells.add(tcCurCell);
                  tcCurCell = new TableCell();
                  break;
                case rkw_cellx:
                  tcdCurCellDef.Right = kw.parameter();
                  CurCellDefs.add(tcdCurCellDef);
                  tcdCurCellDef = new TableCellDef();
                  break;
                case rkw_trleft:
                  trCurRow.Left = kw.parameter();
                  iLastRowLeft = kw.parameter();
                  break;
                case rkw_trrh:
                  trCurRow.Height = kw.parameter();
                  iLastRowHeight = kw.parameter();
                  break;
                case rkw_clvmgf:
                  tcdCurCellDef.FirstMerged = true;
                  break;
                case rkw_clvmrg:
                  tcdCurCellDef.Merged = true;
                  break;
                case rkw_clbrdrb:
                  tcdCurCellDef.BorderBottom = true;
                  tcdCurCellDef.ActiveBorder = tcdCurCellDef.BorderBottom;
                  break;
                case rkw_clbrdrt:
                  tcdCurCellDef.BorderTop = true;
                  tcdCurCellDef.ActiveBorder = tcdCurCellDef.BorderTop;
                  break;
                case rkw_clbrdrl:
                  tcdCurCellDef.BorderLeft = true;
                  tcdCurCellDef.ActiveBorder = tcdCurCellDef.BorderLeft;
                  break;
                case rkw_clbrdrr:
                  tcdCurCellDef.BorderRight = true;
                  tcdCurCellDef.ActiveBorder = tcdCurCellDef.BorderRight;
                  break;
                case rkw_brdrnone:
                  if(tcdCurCellDef.ActiveBorder != null)
                  {
                    tcdCurCellDef.ActiveBorder = null;
                  }
                  break;
                case rkw_clvertalt:
                  tcdCurCellDef.VAlign = TableCellDef.valign.valign_top;
                  break;
                case rkw_clvertalc:
                  tcdCurCellDef.VAlign = TableCellDef.valign.valign_center;
                  break;
                case rkw_clvertalb:
                  tcdCurCellDef.VAlign = TableCellDef.valign.valign_bottom;
                  break;
                // page formatting
                case rkw_paperw:
                  iDocWidth = kw.parameter();
                  break;
                case rkw_margl:
                  iMarginLeft = kw.parameter();
                  break;
              }
            }
          }
          break;
        }
        case '{':
          // perform group opening actions here
          foStack.push(curOptions);
          break;
        case '}':
          // perform group closing actions here
          curOptions = foStack.pop();
          break;
        case 13:
        case 10:
          break;
        case '<':
          parText.write("&lt;");
          break;
        case '>':
          parText.write("&gt;");
          break;
        /*      case ' ':
        par_html.write("&ensp;");
        break;*/
        default:
          parText.write((char) carattere);
      }
    }

    CvtData cd = new CvtData();
    cd.iMarginLeft = iMarginLeft;
    cd.iDocWidth = iDocWidth;
    cd.title = title;
    cd.arElements = arElements;

    return cd;
  }

  public Paragraph getParagraph(FormattingOptions opt, LowagieText text)
  {
    Paragraph p = new Paragraph(text.str());
    text.close();

    return p;
  }
}
