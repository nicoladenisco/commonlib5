/*
 * Copyright (C) 2022 Nicola De Nisco
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
package org.commonlib5.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Helper per richieste JSON.
 *
 * @author Nicola De Nisco
 */
public class JsonSimple implements Closeable
{
  protected boolean autoclose;
  protected HttpURLConnection con;

  public JsonSimple(HttpURLConnection con)
  {
    this.con = con;
  }

  public JsonSimple(URL url, String method)
     throws IOException
  {
    con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod(method);
    con.setRequestProperty("Accept", "application/json");
    autoclose = true;
  }

  public JSONObject getJsonResponse()
     throws Exception
  {
    if(con.getResponseCode() != 200)
      throw new Exception("request failure: code " + con.getResponseCode() + " (" + con.getResponseMessage() + ")");

    try ( BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())))
    {
      StringBuilder sb = new StringBuilder();
      String s;
      while((s = br.readLine()) != null)
        sb.append(s);

      return new JSONObject(sb.toString());
    }
  }

  public JSONObject getJsonResponse(JSONObject jsonRequest)
     throws Exception
  {
    if(jsonRequest != null)
    {
      try ( OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream()))
      {
        wr.write(jsonRequest.toString());
        wr.flush();
      }
    }

    return getJsonResponse();
  }

  @Override
  public void close()
     throws IOException
  {
    if(autoclose && con != null)
    {
      con.disconnect();
      con = null;
    }
  }
}
