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

import java.io.PushbackReader;
import java.util.HashMap;

/**
 * Parsing di una keyword rtf.
 *
 * @author Nicola De Nisco
 */
public class RtfKeyword
{
  public enum keyword_type
  {
    rkw_unknown,
    rkw_b, rkw_bin, rkw_blue, rkw_brdrnone, rkw_bullet,
    rkw_cb, rkw_cell, rkw_cellx, rkw_cf, rkw_clbrdrb, rkw_clbrdrl,
    rkw_clbrdrr, rkw_clbrdrt, rkw_clvertalb, rkw_clvertalc,
    rkw_clvertalt, rkw_clvmgf, rkw_clvmrg, rkw_colortbl,
    rkw_emdash, rkw_emspace, rkw_endash, rkw_enspace,
    rkw_fi, rkw_field, rkw_filetbl,
    rkw_f, rkw_fprq, rkw_fcharset,
    rkw_fnil, rkw_froman, rkw_fswiss, rkw_fmodern,
    rkw_fscript, rkw_fdecor, rkw_ftech, rkw_fbidi,
    rkw_fldrslt, rkw_fonttbl, rkw_footer, rkw_footerf, rkw_fs,
    rkw_green,
    rkw_header, rkw_headerf, rkw_highlight,
    rkw_i, rkw_info, rkw_intbl,
    rkw_ldblquote, rkw_li, rkw_line, rkw_lquote,
    rkw_margl,
    rkw_object,
    rkw_paperw, rkw_par, rkw_pard, rkw_pict, rkw_plain,
    rkw_qc, rkw_qj, rkw_ql, rkw_qmspace, rkw_qr,
    rkw_rdblquote, rkw_red, rkw_ri, rkw_row, rkw_rquote,
    rkw_sa, rkw_sb, rkw_sect, rkw_softline, rkw_stylesheet,
    rkw_sub, rkw_super,
    rkw_tab, rkw_title, rkw_trleft, rkw_trowd, rkw_trrh,
    rkw_ul, rkw_ulnone
  };

  private class keyword_map extends HashMap<String, keyword_type>
  {
    public keyword_map()
    {
      put("b", keyword_type.rkw_b);
      put("bin", keyword_type.rkw_bin);
      put("blue", keyword_type.rkw_blue);
      put("brdrnone", keyword_type.rkw_brdrnone);
      put("bullet", keyword_type.rkw_bullet);
      put("cb", keyword_type.rkw_cb);
      put("cell", keyword_type.rkw_cell);
      put("cellx", keyword_type.rkw_cellx);
      put("cf", keyword_type.rkw_cf);
      put("clbrdrb", keyword_type.rkw_clbrdrb);
      put("clbrdrl", keyword_type.rkw_clbrdrl);
      put("clbrdrr", keyword_type.rkw_clbrdrr);
      put("clbrdrt", keyword_type.rkw_clbrdrt);
      put("clvertalb", keyword_type.rkw_clvertalb);
      put("clvertalc", keyword_type.rkw_clvertalc);
      put("clvertalt", keyword_type.rkw_clvertalt);
      put("clvmgf", keyword_type.rkw_clvmgf);
      put("clvmrg", keyword_type.rkw_clvmrg);
      put("colortbl", keyword_type.rkw_colortbl);
      put("emdash", keyword_type.rkw_emdash);
      put("emspace", keyword_type.rkw_emspace);
      put("endash", keyword_type.rkw_endash);
      put("enspace", keyword_type.rkw_enspace);
      put("f", keyword_type.rkw_f);
      put("fprq", keyword_type.rkw_fprq);
      put("fcharset", keyword_type.rkw_fcharset);
      put("fnil", keyword_type.rkw_fnil);
      put("froman", keyword_type.rkw_froman);
      put("fswiss", keyword_type.rkw_fswiss);
      put("fmodern", keyword_type.rkw_fmodern);
      put("fscript", keyword_type.rkw_fscript);
      put("fdecor", keyword_type.rkw_fdecor);
      put("ftech", keyword_type.rkw_ftech);
      put("fbidi", keyword_type.rkw_fbidi);
      put("field", keyword_type.rkw_field);
      put("filetbl", keyword_type.rkw_filetbl);
      put("fldrslt", keyword_type.rkw_fldrslt);
      put("fonttbl", keyword_type.rkw_fonttbl);
      put("footer", keyword_type.rkw_footer);
      put("footerf", keyword_type.rkw_footerf);
      put("fs", keyword_type.rkw_fs);
      put("green", keyword_type.rkw_green);
      put("header", keyword_type.rkw_header);
      put("headerf", keyword_type.rkw_headerf);
      put("highlight", keyword_type.rkw_highlight);
      put("i", keyword_type.rkw_i);
      put("info", keyword_type.rkw_info);
      put("intbl", keyword_type.rkw_intbl);
      put("ldblquote", keyword_type.rkw_ldblquote);
      put("li", keyword_type.rkw_li);
      put("line", keyword_type.rkw_line);
      put("lquote", keyword_type.rkw_lquote);
      put("margl", keyword_type.rkw_margl);
      put("object", keyword_type.rkw_object);
      put("paperw", keyword_type.rkw_paperw);
      put("par", keyword_type.rkw_par);
      put("pard", keyword_type.rkw_pard);
      put("pict", keyword_type.rkw_pict);
      put("plain", keyword_type.rkw_plain);
      put("qc", keyword_type.rkw_qc);
      put("qj", keyword_type.rkw_qj);
      put("ql", keyword_type.rkw_ql);
      put("qr", keyword_type.rkw_qr);
      put("rdblquote", keyword_type.rkw_rdblquote);
      put("red", keyword_type.rkw_red);
      put("ri", keyword_type.rkw_ri);
      put("row", keyword_type.rkw_row);
      put("rquote", keyword_type.rkw_rquote);
      put("sa", keyword_type.rkw_sa);
      put("sb", keyword_type.rkw_sb);
      put("sect", keyword_type.rkw_sect);
      put("softline", keyword_type.rkw_softline);
      put("stylesheet", keyword_type.rkw_stylesheet);
      put("sub", keyword_type.rkw_sub);
      put("super", keyword_type.rkw_super);
      put("tab", keyword_type.rkw_tab);
      put("title", keyword_type.rkw_title);
      put("trleft", keyword_type.rkw_trleft);
      put("trowd", keyword_type.rkw_trowd);
      put("trrh", keyword_type.rkw_trrh);
      put("ul", keyword_type.rkw_ul);
      put("ulnone", keyword_type.rkw_ulnone);
    }
  };
  private static keyword_map keymap = null;
  private String s_keyword = "";
  private keyword_type e_keyword = keyword_type.rkw_unknown;
  private int param = 0;
  private char ctrl_chr = 0;
  private boolean is_ctrl_chr = false;
  private char curchar = 0;

  // iter must point after the backslash starting the keyword. We don't check it.
  // after construction, iter points at the char following the keyword
  public RtfKeyword(PushbackReader rd)
     throws Exception
  {
    if(keymap == null)
      keymap = new keyword_map();

    curchar = (char) rd.read();
    is_ctrl_chr = !Character.isLetter(curchar);

    if(is_ctrl_chr)
    {
      ctrl_chr = curchar;
    }
    else
    {
      do
      {
        s_keyword += curchar;
      }
      while(Character.isLetter(curchar = (char) rd.read()));

      String param_str = "";
      while(Character.isDigit(curchar) || curchar == '-')
      {
        param_str += curchar;
        curchar = (char) rd.read();
      }

      // ritorna l'ultimo carattere allo stream
      // a meno che non sia spazio che viene volutamente scartato
      if(curchar != ' ')
        rd.unread(curchar);

      if(param_str.isEmpty())
        param = -1;
      else
        param = Integer.parseInt(param_str);

      Object kw_pos = keymap.get(s_keyword);

      if(kw_pos == null)
        e_keyword = keyword_type.rkw_unknown;
      else
        e_keyword = ((keyword_type) kw_pos);

//       System.out.println("Keyword: " + s_keyword + "\tparam: " + param);
    }
  }

  public boolean is_control_char()
  {
    return is_ctrl_chr;
  }

  public String keyword_str()
  {
    return s_keyword;
  }

  public keyword_type keyword()
  {
    return e_keyword;
  }

  public int parameter()
  {
    return param;
  }

  public char control_char()
  {
    return ctrl_chr;
  }

  public char getLastChar()
  {
    return curchar;
  }

  @Override
  public String toString()
  {
    return "RtfKeyword: " + e_keyword + " [" + s_keyword + "]";
  }
}
