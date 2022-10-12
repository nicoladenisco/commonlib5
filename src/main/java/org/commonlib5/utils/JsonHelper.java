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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
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

  public Pair<Integer, JSONObject> postAsJson()
     throws Exception
  {
    return postAsJson(null);
  }

  public Pair<Integer, JSONObject> postAsJson(JSONObject request)
     throws Exception
  {
    httpConnection.setRequestMethod("POST");
    httpConnection.setRequestProperty("Accept", "application/json");
    httpConnection.setRequestProperty("Content-Type", "application/json");
    httpConnection.setDoOutput(true);

    if(request != null)
      return getJsonResponse(request, httpConnection);

    return getJsonResponse(httpConnection);
  }

  public Pair<Integer, JSONObject> getAsJson()
     throws Exception
  {
    return getAsJson(null);
  }

  public Pair<Integer, JSONObject> getAsJson(JSONObject request)
     throws Exception
  {
    httpConnection.setRequestMethod("GET");
    httpConnection.setRequestProperty("Accept", "application/json");
    httpConnection.setRequestProperty("Content-Type", "application/json");

    if(request != null)
      return getJsonResponse(request, httpConnection);

    return getJsonResponse(httpConnection);
  }

  public Pair<Integer, String> postAsText()
     throws Exception
  {
    return postAsText(null);
  }

  public Pair<Integer, String> postAsText(JSONObject request)
     throws Exception
  {
    httpConnection.setRequestMethod("POST");
    httpConnection.setRequestProperty("Accept", "application/text");
    httpConnection.setRequestProperty("Content-Type", "application/json");
    httpConnection.setDoOutput(true);

    if(request != null)
      return getTextResponse(request, httpConnection);

    return getTextResponse(httpConnection);
  }

  public Pair<Integer, String> getAsText()
     throws Exception
  {
    return getAsText(null);
  }

  public Pair<Integer, String> getAsText(JSONObject request)
     throws Exception
  {
    httpConnection.setRequestMethod("GET");
    httpConnection.setRequestProperty("Accept", "application/text");
    httpConnection.setRequestProperty("Content-Type", "application/json");

    if(request != null)
      return getTextResponse(request, httpConnection);

    return getTextResponse(httpConnection);
  }

  public Pair<Integer, JSONObject> getJsonResponse(HttpURLConnection httpConnection)
     throws Exception
  {
    int responseCode = httpConnection.getResponseCode();
    JSONObject response = null;

    try ( BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF-8")))
    {
      StringBuilder sb = new StringBuilder();

      String s;
      while((s = br.readLine()) != null)
        sb.append(s);

      response = new JSONObject(sb.toString());
    }
    catch(JSONException e)
    {
      throw e;
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
    try ( OutputStream os = httpConnection.getOutputStream())
    {
      byte[] input = jsonRequest.toString().getBytes("utf-8");
      os.write(input, 0, input.length);
    }
    return getJsonResponse(httpConnection);
  }

  public Pair<Integer, String> getTextResponse(HttpURLConnection httpConnection)
     throws Exception
  {
    int responseCode = httpConnection.getResponseCode();
    String response = null;

    try ( BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF-8")))
    {
      StringBuilder sb = new StringBuilder();

      String s;
      while((s = br.readLine()) != null)
        sb.append(s);

      response = sb.toString();
    }
    catch(Throwable t)
    {
      // eccezione ignorata
    }

    return new Pair<>(responseCode, response);
  }

  public Pair<Integer, String> getTextResponse(JSONObject jsonRequest, HttpURLConnection httpConnection)
     throws Exception
  {
    try ( OutputStream os = httpConnection.getOutputStream())
    {
      byte[] input = jsonRequest.toString().getBytes("utf-8");
      os.write(input, 0, input.length);
    }
    return getTextResponse(httpConnection);
  }
}
