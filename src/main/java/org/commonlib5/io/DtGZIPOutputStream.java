/*
 * DtGZIPOutputStream.java
 *
 * Created on 2-feb-2010, 16.51.08
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Come GZIPOutputStream ma con un metodo detach()
 * che consente di staccare lo stream originale senza
 * chiuderlo.
 *
 * @author Nicola De Nisco
 */
public class DtGZIPOutputStream extends GZIPOutputStream
{
  public DtGZIPOutputStream(OutputStream out) throws IOException
  {
    super(out);
  }

  public DtGZIPOutputStream(OutputStream out, int size) throws IOException
  {
    super(out, size);
  }

  /**
   * Flush dello stream sottostante e distacco dallo stream.
   * Questo consente di continuare ad utilizzare lo stream
   * dopo che questa classe ha finito il suo utilizzo.
   *
   * @throws IOException
   */
  public void detach() throws IOException
  {
    finish();
    out.flush();
    out = null;
  }
}
