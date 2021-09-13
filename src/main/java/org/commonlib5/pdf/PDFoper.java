/*
 * Copyright (C) 2015 Nicola De Nisco
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
package org.commonlib5.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.commonlib5.utils.CommonFileUtils;

/**
 * Fusione di più PDF in un unico PDF.
 *
 * @author Nicola De Nisco
 */
public class PDFoper
{
  /**
   * Fonde la lista di files specificata in un unico file.
   * @param inputFiles lista di file da fondere
   * @param outputFile file da produrre
   * @param paginate vero per aggiungere un numero di pagina globale sul PDF in uscita
   * @throws Exception
   */
  public static void concatPDFs(List<File> inputFiles, File outputFile, boolean paginate)
     throws Exception
  {
    OutputStream output = new FileOutputStream(outputFile);
    try
    {
      List<InputStream> pdfs = new ArrayList<InputStream>();
      try
      {
        for(File f : inputFiles)
          pdfs.add(new FileInputStream(f));

        PDFoper.concatPDFs(pdfs, output, paginate);
      }
      finally
      {
        for(InputStream is : pdfs)
          CommonFileUtils.safeClose(is);
      }
    }
    finally
    {
      CommonFileUtils.safeClose(output);
    }
  }

  /**
   * Fonde la lista di files specificata in un unico file.
   * @param streamOfPDFFiles stream di input da cui leggere
   * @param outputStream stream di output su cui scrivere
   * @param paginate vero per aggiungere un numero di pagina globale sul PDF in uscita
   * @throws Exception
   */
  public static void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate)
     throws Exception
  {
    Document document = null;
    try
    {
      ArrayList<PdfReader> readers = new ArrayList<PdfReader>();
      int totalPages = 0;

      // Crea i Readers per leggere i documenti
      for(InputStream is : streamOfPDFFiles)
      {
        PdfReader pdfReader = new PdfReader(is);
        readers.add(pdfReader);
        totalPages += pdfReader.getNumberOfPages();

        if(document == null)
          document = new Document(pdfReader.getPageSizeWithRotation(1));
      }

      // Crea il writer per il documento di uscita
      PdfWriter writer = PdfWriter.getInstance(document, outputStream);

      document.open();
      BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      PdfContentByte cb = writer.getDirectContent(); // Holds the PDF data

      PdfImportedPage page;
      int currentPageNumber = 0;

      for(PdfReader pdfReader : readers)
      {
        // legge una pagina per volta tutte le pagine del file origine e le scrive nell'output
        for(int pageCurrPDF = 0; pageCurrPDF < pdfReader.getNumberOfPages(); pageCurrPDF++)
        {
          document.newPage();
          currentPageNumber++;
          page = writer.getImportedPage(pdfReader, pageCurrPDF + 1);
          cb.addTemplate(page, 0, 0);

          // Code for pagination.
          if(paginate)
          {
            cb.beginText();
            cb.setFontAndSize(bf, 9);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
               "" + currentPageNumber + " of " + totalPages, 520, 5, 0);
            cb.endText();
          }
        }
      }
    }
    finally
    {
      if(document != null && document.isOpen())
        document.close();
    }
  }
}
