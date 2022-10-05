/*
 *  JsonHelper.java
 *  Creato il 5 ott 2022, 13:06:58
 *
 *  Copyright (C) 2022 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
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
 * Helper per chiamate API REST (json).
 *
 * @author nicola
 */
public class JsonHelper implements Closeable
{
  private final URL url;
  private final HttpURLConnection httpConnection;

  public JsonHelper(URL url)
     throws IOException
  {
    this.url = url;
    this.httpConnection = (HttpURLConnection) url.openConnection();
  }

  @Override
  public void close()
     throws IOException
  {
    httpConnection.disconnect();
  }

  public URL getUrl()
  {
    return url;
  }

  public HttpURLConnection getHttpConnection()
  {
    return httpConnection;
  }

  public void addHeader(String key, String value)
     throws Exception
  {
    httpConnection.setRequestProperty(key, value);
  }

  public Pair<Integer, JSONObject> post()
     throws Exception
  {
    return post(null);
  }

  public Pair<Integer, JSONObject> post(JSONObject request)
     throws Exception
  {
    httpConnection.setRequestMethod("POST");
    httpConnection.setRequestProperty("Accept", "application/json");
    httpConnection.setRequestProperty("Content-Type", "application/json");

    if(request != null)
      return getJsonResponse(request, httpConnection);

    return getJsonResponse(httpConnection);
  }

  public Pair<Integer, JSONObject> get()
     throws Exception
  {
    return get(null);
  }

  public Pair<Integer, JSONObject> get(JSONObject request)
     throws Exception
  {
    httpConnection.setRequestMethod("GET");
    httpConnection.setRequestProperty("Accept", "application/json");
    httpConnection.setRequestProperty("Content-Type", "application/json");

    if(request != null)
      return getJsonResponse(request, httpConnection);

    return getJsonResponse(httpConnection);
  }

  public Pair<Integer, JSONObject> getJsonResponse(HttpURLConnection httpConnection)
     throws Exception
  {
    int responseCode = httpConnection.getResponseCode();
    JSONObject response = null;

    try ( BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream())))
    {
      StringBuilder sb = new StringBuilder();

      String s;
      while((s = br.readLine()) != null)
        sb.append(s);

      response = new JSONObject(sb.toString());
    }
    catch(Throwable t)
    {
      // eccezione ignorata
    }

    return new Pair<>(responseCode, response);
  }

  public Pair<Integer, JSONObject> getJsonResponse(JSONObject jsonRequest, HttpURLConnection httpConnection)
     throws Exception
  {
    try ( OutputStreamWriter wr = new OutputStreamWriter(httpConnection.getOutputStream()))
    {
      wr.write(jsonRequest.toString());
      wr.flush();
    }
    return getJsonResponse(httpConnection);
  }
}
