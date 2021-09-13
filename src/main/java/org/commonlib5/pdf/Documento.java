/*
 * Documento.java
 *
 * Created on 7-dic-2010, 17.33.49
 *
 * Copyright (C) RAD-IMAGE s.r.l.
 */
package org.commonlib5.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.*;
import java.awt.Color;
import java.io.*;
import java.util.Iterator;
import org.commonlib5.rtfconvert.html.HtmlConverter;

/**
 * Scheletro di documento per il rendering.
 *
 * @author Nicola De Nisco
 */
public class Documento
{
  //
  // costanti formati carta
  public static final int PAPER_A3 = 1;
  public static final int PAPER_A3PLUS = 2;
  public static final int PAPER_A3WIDE = 3;
  public static final int PAPER_A3NOBI = 4;
  public static final int PAPER_A4 = 5;
  //
  // fonts
  protected Font fontC06N_N = new Font(FontFamily.COURIER, 6, Font.NORMAL, BaseColor.BLACK);
  protected Font fontC06B_I = new Font(FontFamily.COURIER, 6, Font.BOLD | Font.ITALIC, BaseColor.BLACK);
  protected Font fontH06N_N = new Font(FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
  protected Font fontH08N_N = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
  protected Font fontH08N_I = new Font(FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.BLACK);
  protected Font fontH09N_N = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
  protected Font fontH06B_N = new Font(FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.BLACK);
  protected Font fontH08B_N = new Font(FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
  protected Font fontH09B_N = new Font(FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
  protected Font fontH10B_N = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
  protected Font fontH10N_N = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
  protected Font fontH12B_N = new Font(FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
  protected Font fontH12N_N = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
  protected Font fontH16B_N = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
  protected Font fontH16N_N = new Font(FontFamily.HELVETICA, 16, Font.NORMAL, BaseColor.BLACK);
  protected Font fontH14B_N = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
  protected Font fontH14N_N = new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK);
  protected Font fontT18B_N = new Font(FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);
  protected Font fontT18N_N = new Font(FontFamily.TIMES_ROMAN, 18, Font.NORMAL, BaseColor.BLACK);
  //
  // various fonts
  protected BaseFont bf_helv = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
  protected BaseFont bf_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", false);
  protected BaseFont bf_courier = BaseFont.createFont(BaseFont.COURIER, "Cp1252", false);
  protected BaseFont bf_symbol = BaseFont.createFont(BaseFont.SYMBOL, "Cp1252", false);
  //
  // costanti per l'uso dei font predefiniti
  public static final int FONT_TYPE_HELV = 0;
  public static final int FONT_TYPE_TIMES = 1;
  public static final int FONT_TYPE_COURIER = 2;
  public static final int FONT_TYPE_SYMBOL = 3;
  //
  // variabili globali
  protected Document document;
  protected PdfWriter writer;
  protected PdfContentByte cb;
  protected Rectangle pageSize, pageRect;
  protected int paperType = PAPER_A3;
  //
  // bordi carta
  public float bordoSX = 10;
  public float bordoDX = 10;
  public float bordoTop = 10;
  public float bordoBottom = 10;

  public static class DocSize
  {
    public float w, h;

    public DocSize()
    {
    }

    public DocSize(float w, float h)
    {
      this.w = w;
      this.h = h;
    }

    public Rectangle rect()
    {
      return new Rectangle(w, h);
    }

    public Rectangle rotate()
    {
      return new Rectangle(h, w);
    }
  }

  public Documento()
     throws Exception
  {
  }

  public final void render(File toWrite)
     throws Exception
  {
    try (FileOutputStream fos = new FileOutputStream(toWrite))
    {
      render(fos);
    }
  }

  public void render(OutputStream os)
     throws Exception
  {
    // http://it.wikipedia.org/wiki/ISO_216
    // http://en.wikipedia.org/wiki/Paper_size
    // A3 plus (329 x 423 mm), A3 (297 × 420 mm) A2 (420 x 594 mm), A4 (210 x 297 mm)

//    pageSize = getPaperSizePoints(paperType).rotate();
    paperType = PAPER_A4;
    pageSize = getPaperSizePoints(paperType);
    document = new Document(pageSize, bordoSX, bordoDX, bordoTop, bordoBottom);
    writer = PdfWriter.getInstance(document, os);

    // imposta proprieta' del documento
    document.addAuthor("DicomPrint (C) RAD-IMAGE s.r.l.");
    document.addSubject("Rendering combinato di immagini e referto.");

    document.open();
    pageRect = new Rectangle(bordoSX, bordoBottom, pageSize.getRight() - bordoDX,
       pageSize.getTop() - bordoTop);
    cb = writer.getDirectContent();

    try
    {
      renderDoc();
    }
    finally
    {
      try
      {
        document.close();
      }
      catch(Exception e)
      {
      }
    }
  }

  protected void renderDoc()
     throws Exception
  {
  }

  protected void insertImageAbsolute(File imgFile, float posx, float posy)
     throws Exception
  {
    Image img = Image.getInstance(imgFile.getAbsolutePath());
    insertImageAbsolute(img, posx, posy);
  }

  protected void insertImageAbsolute(Image img, float posx, float posy)
     throws Exception
  {
    img.setAbsolutePosition(posx, posy);
    cb.addImage(img);
  }

  protected void insertImageRelative(File imgFile, float posx, float posy)
     throws Exception
  {
    Image img = Image.getInstance(imgFile.getAbsolutePath());
    insertImageRelative(img, posx, posy);
  }

  protected void insertImageRelative(Image img, float posx, float posy)
     throws Exception
  {
    Rectangle rPos = getRelativePosition(posx, posy);
    img.setAbsolutePosition(rPos.getLeft(), rPos.getBottom());
    cb.addImage(img);
  }

  protected void insertImageRelative(Image img, float posx, float posy, int halign, int valign)
     throws Exception
  {
    Rectangle rPos = getRelativePosition(posx, posy);
    float x = rPos.getLeft();
    float y = rPos.getBottom();
    float w = img.getPlainWidth();
    float h = img.getPlainHeight();

    switch(halign)
    {
      case Element.ALIGN_LEFT:
        break;
      case Element.ALIGN_CENTER:
        x -= w / 2;
        break;
      case Element.ALIGN_RIGHT:
        x -= w;
        break;
    }

    switch(valign)
    {
      case Element.ALIGN_TOP:
        y -= h;
        break;
      case Element.ALIGN_MIDDLE:
        y -= h / 2;
        break;
      case Element.ALIGN_BOTTOM:
        break;
    }

    img.setAbsolutePosition(x, y);
    cb.addImage(img);
  }

  protected void insertTextRelative(BaseFont font, int sizeFont, int align, float posx, float posy, String text)
     throws Exception
  {
    if(text == null)
      return;

    Rectangle rPos = getRelativePosition(posx, posy);

    cb.beginText();
    cb.setFontAndSize(font, sizeFont);
    cb.showTextAligned(align, text, rPos.getLeft(), rPos.getBottom(), 0);
    cb.endText();
  }

  protected void insertTextRelative(BaseFont font, int sizeFont, int align, Rectangle relPos, String text, boolean border)
     throws Exception
  {
    if(text == null)
      return;

    Rectangle rPos = getRelativeRect(relPos);

    ColumnText ct = new ColumnText(cb);
    Phrase myText = new Phrase(text, new Font(font, sizeFont, Font.NORMAL, BaseColor.BLACK));
    ct.setSimpleColumn(myText, rPos.getLeft(), rPos.getBottom(), rPos.getRight(), rPos.getTop(), sizeFont, align);
    ct.go();

    if(border)
    {
      cb.setLineWidth(0.1f);
      cb.setColorFill(BaseColor.BLACK);
      cb.setColorStroke(BaseColor.BLACK);
      cb.moveTo(rPos.getLeft(), rPos.getBottom());
      cb.lineTo(rPos.getRight(), rPos.getBottom());
      cb.lineTo(rPos.getRight(), rPos.getTop());
      cb.lineTo(rPos.getLeft(), rPos.getTop());
      cb.lineTo(rPos.getLeft(), rPos.getBottom());
      cb.stroke();
    }
  }

  protected void insertTextRelative(BaseFont font, int sizeFont, int align,
     float px, float py, float width, float height, String text, boolean border)
     throws Exception
  {
    if(text == null)
      return;

    Rectangle r = new Rectangle(px, py, px + width, py + height);
    insertTextRelative(font, sizeFont, align, r, text, border);
  }

  protected Rectangle getRelativePosition(float x, float y)
  {
    float px = pageRect.getLeft() + (x * pageRect.getWidth());
    float py = pageRect.getBottom() + (y * pageRect.getHeight());

    return new Rectangle(px, py, px, py);
  }

  protected Rectangle getRelativeRect(Rectangle r)
  {
    float llx = pageRect.getLeft() + (r.getLeft() * pageRect.getWidth());
    float lly = pageRect.getBottom() + (r.getBottom() * pageRect.getHeight());
    float urx = pageRect.getLeft() + (r.getRight() * pageRect.getWidth());
    float ury = pageRect.getBottom() + (r.getTop() * pageRect.getHeight());

    return new Rectangle(llx, lly, urx, ury);
  }

  protected float getRelativeWidth(float w)
  {
    return w * pageRect.getWidth();
  }

  protected float getRelativeHeight(float h)
  {
    return h * pageRect.getHeight();
  }

  protected void drawVerticalMiddleLine()
  {
    Rectangle rLinea = getRelativeRect(new Rectangle(0.5f, 0f, 0.5f, 1f));
    cb.setLineWidth(0.1f);
    cb.setColorFill(BaseColor.BLACK);
    cb.setColorStroke(BaseColor.BLACK);
    cb.moveTo(rLinea.getLeft(), rLinea.getBottom());
    cb.lineTo(rLinea.getRight(), rLinea.getTop());
    cb.stroke();
  }

  /**
   * Restituisce le dimensini in millimetri del tipo di carta
   * secondo le costanti PAPER...
   * Per maggiori informazioni consultare:
   * http://it.wikipedia.org/wiki/ISO_216
   * http://en.wikipedia.org/wiki/Paper_size
   * A3 plus (329 x 423 mm), A3 (297 × 420 mm) A2 (420 x 594 mm), A4 (210 x 297 mm)
   *
   * @param paperType tipo di carta (vedi PAPER_...)
   * @return dimensioni in millimetri
   */
  public static DocSize getPaperSizeMillimeters(int paperType)
     throws Exception
  {
    switch(paperType)
    {
      case PAPER_A3:
        return new DocSize(297.0f, 420.0f);
      case PAPER_A3PLUS:
        return new DocSize(329.0f, 423.0f);
      case PAPER_A3NOBI:
        return new DocSize(328.0f, 453.0f);
      case PAPER_A3WIDE:
        return new DocSize(320.0f, 450.0f);
      case PAPER_A4:
        return new DocSize(210.0f, 297.0f);
    }
    throw new Exception("Formato carta non supportato.");
  }

  public static Rectangle getPaperSizePoints(int paperType)
     throws Exception
  {
    switch(paperType)
    {
      case PAPER_A3:
        return PageSize.A3;
      case PAPER_A3PLUS:
      case PAPER_A3NOBI:
      case PAPER_A3WIDE:
        Rectangle rp = PageSize.A3;
        Rectangle rt = getPaperSizeMillimeters(PAPER_A3).rect();
        Rectangle rm = getPaperSizeMillimeters(paperType).rect();
        return new Rectangle(
           (rp.getWidth() * rm.getWidth()) / rt.getWidth(),
           (rp.getHeight() * rm.getHeight()) / rt.getHeight());
      case PAPER_A4:
        return PageSize.A4;
    }
    throw new Exception("Formato carta non supportato.");
  }

  public static String getPaperName(int paperType)
  {
    switch(paperType)
    {
      case PAPER_A3:
        return "A3";
      case PAPER_A3PLUS:
        return "A3PLUS";
      case PAPER_A3WIDE:
        return "A3WIDE";
      case PAPER_A3NOBI:
        return "A3NOBI";
      case PAPER_A4:
        return "A4";
    }

    return null;
  }

  public BaseFont getFontByType(int type)
  {
    switch(type)
    {
      case FONT_TYPE_HELV:
        return bf_helv;
      case FONT_TYPE_TIMES:
        return bf_times;
      case FONT_TYPE_COURIER:
        return bf_courier;
      case FONT_TYPE_SYMBOL:
        return bf_symbol;
    }
    return null;
  }

  public BaseColor cvtColor(Color c)
  {
    return new BaseColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
  }

  /**
   * Estrae una pagina da un pdf per poterla inserire in quello corrente.
   * @param filePdf
   * @param pageNumber
   * @return
   * @throws Exception
   */
  protected PdfTemplate convertPdf(File filePdf, int pageNumber)
     throws Exception
  {
    PdfImportedPage pagePdf;
    try (FileInputStream fis = new FileInputStream(filePdf))
    {
      PdfReader pdfReader = new PdfReader(fis);
      pagePdf = writer.getImportedPage(pdfReader, pageNumber);
    }
    return pagePdf;
  }

  /**
   * Converte rtf nell'equivalente html.
   * @param in
   * @return
   * @throws Exception
   */
  protected String convertRtf(Reader in)
     throws Exception
  {
    HtmlConverter cvt = new HtmlConverter();
    HtmlConverter.CvtData cd = cvt.convert(in);
    in.close();

    return cd.body;
  }

  /**
   * Aggiunge html alla cella indicata.
   * @param in
   * @param cell
   * @param fref
   * @throws Exception
   */
  protected void addHtmlContents(Reader in, PdfPCell cell, Font fref)
     throws Exception
  {
    java.util.List<Element> elementlist = HTMLWorker.parseToList(in, null);
    in.close();

    for(Element e : elementlist)
    {
      if(e instanceof Paragraph)
      {
        ((Paragraph) e).setFont(fref);
        Iterator itr = ((Paragraph) e).iterator();
        while(itr.hasNext())
        {
          Object o = itr.next();
          if(o instanceof Chunk)
            ((Chunk) o).setFont(fref);
        }
      }
      else if(e instanceof Chunk)
        ((Chunk) e).setFont(fref);

      cell.addElement(e);
    }
  }

  /**
   * Aggiunge un testo alla cella indicata.
   * @param in
   * @param cell
   * @param fref
   * @throws Exception
   */
  protected void addTxtContents(Reader in, PdfPCell cell, Font fref)
     throws Exception
  {
    String s;
    try (BufferedReader br = new BufferedReader(in, 4096))
    {
      while((s = br.readLine()) != null)
      {
        if(s.isEmpty())
          s = "  ";
        cell.addElement(new Paragraph(s, fref));
      }
    }
  }

  /**
   * Aggiunge html alla colonna di testo indicata.
   * @param in
   * @param col
   * @param fref
   * @throws Exception
   */
  protected void addHtmlContents(Reader in, ColumnText col, Font fref)
     throws Exception
  {
    java.util.List<Element> elementlist = HTMLWorker.parseToList(in, null);
    in.close();

    for(Element e : elementlist)
    {
      if(e instanceof Paragraph)
      {
        ((Paragraph) e).setFont(fref);
        Iterator itr = ((Paragraph) e).iterator();
        while(itr.hasNext())
        {
          Object o = itr.next();
          if(o instanceof Chunk)
            ((Chunk) o).setFont(fref);
        }
      }
      else if(e instanceof Chunk)
        ((Chunk) e).setFont(fref);

      col.addElement(e);
    }
  }

  /**
   * Aggiunge testo alla colonna di testo indicata.
   * @param in
   * @param col
   * @param fref
   * @throws Exception
   */
  protected void addTxtContents(Reader in, ColumnText col, Font fref)
     throws Exception
  {
    String s;
    try (BufferedReader br = new BufferedReader(in, 4096))
    {
      while((s = br.readLine()) != null)
      {
        if(s.isEmpty())
          s = "  ";
        col.addElement(new Paragraph(s, fref));
      }
    }
  }
}
