/*
 * DtGZIPInputStream.java
 *
 * Created on 2-feb-2010, 16.56.36
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */

package org.commonlib5.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Come GZIPIutputStream ma con un metodo detach()
 * che consente di staccare lo stream originale senza
 * chiuderlo.
 *
 * @author Nicola De Nisco
 */
public class DtGZIPInputStream extends GZIPInputStream
{
  public DtGZIPInputStream(InputStream in) throws IOException
  {
    super(in);
  }

  public DtGZIPInputStream(InputStream in, int size) throws IOException
  {
    super(in, size);
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
    in = null;
  }
}
