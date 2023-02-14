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

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.commonlib5.io.ByteBufferOutputStream;
import org.json.JSONObject;

/**
 * Helper per chiamate API REST (json).
 *
 * @author nicola
 */
public class JsonHelper implements Closeable
{
  protected final URI uri;
  protected final ArrayMap<String, String> headers = new ArrayMap<>();

  public JsonHelper(URI uri)
  {
    this.uri = uri;
  }

  @Override
  public void close()
     throws IOException
  {
  }

  public URI getUri()
  {
    return uri;
  }

  public void addToHeader(String key, String value)
  {
    headers.put(key, value);
  }

  public void addToHeader(Map<String, String> values)
  {
    headers.putAll(values);
  }

  public Pair<Integer, JSONObject> postAsJson(JSONObject request)
     throws Exception
  {
    Pair<Integer, String> rv = postRequest(uri, request);
    return new Pair<>(rv.first, new JSONObject(rv.second));
  }

  public Pair<Integer, JSONObject> getAsJson()
     throws Exception
  {
    Pair<Integer, String> rv = getRequest(uri);
    return new Pair<>(rv.first, new JSONObject(rv.second));
  }

  public Pair<Integer, String> getAsText()
     throws Exception
  {
    return getRequest(uri);
  }

  public Pair<Integer, String> postAsText(JSONObject request)
     throws Exception
  {
    return postRequest(uri, request);
  }

  public Pair<Integer, String> deleteAsText()
     throws Exception
  {
    return deleteRequest(uri);
  }

  public Pair<Integer, String> putAsText(JSONObject request)
     throws Exception
  {
    return putRequest(uri, request);
  }

  public Pair<Integer, JSONObject> putAsJson(JSONObject request)
     throws Exception
  {
    Pair<Integer, String> rv = putRequest(uri, request);
    return new Pair<>(rv.first, new JSONObject(rv.second));
  }

  public Pair<Integer, String> patchAsText(JSONObject request)
     throws Exception
  {
    return patchRequest(uri, request);
  }

  public Pair<Integer, JSONObject> patchAsJson(JSONObject request)
     throws Exception
  {
    Pair<Integer, String> rv = patchRequest(uri, request);
    return new Pair<>(rv.first, new JSONObject(rv.second));
  }

  public Pair<Integer, JSONObject> deleteAsJson()
     throws Exception
  {
    Pair<Integer, String> rv = deleteRequest(uri);
    return new Pair<>(rv.first, new JSONObject(rv.second));
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method)
     throws Exception
  {
    URL url = uri.toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try
    {
      conn.setDoOutput(true);
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type", "application/json");
      headers.forEach((k, v) -> conn.setRequestProperty(k, v));

      ByteBufferOutputStream bos = new ByteBufferOutputStream();
      CommonFileUtils.copyStream(conn.getInputStream(), bos);
      return new Pair<>(conn.getResponseCode(), bos.toString("UTF-8"));
    }
    finally
    {
      conn.disconnect();
    }
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONObject req)
     throws Exception
  {
    URL url = uri.toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try
    {
      conn.setDoOutput(true);
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      headers.forEach((k, v) -> conn.setRequestProperty(k, v));

      String input = req.toString();
      byte[] byteInput = input.getBytes("UTF-8");

      conn.setFixedLengthStreamingMode(byteInput.length);
      conn.getOutputStream().write(byteInput);

      ByteBufferOutputStream bos = new ByteBufferOutputStream();
      CommonFileUtils.copyStream(conn.getInputStream(), bos);
      return new Pair<>(conn.getResponseCode(), bos.toString("UTF-8"));
    }
    finally
    {
      conn.disconnect();
    }
  }

  protected Pair<Integer, String> getRequest(URI uri)
     throws Exception
  {
    return genericRequest(uri, "GET");
  }

  protected Pair<Integer, String> deleteRequest(URI uri)
     throws Exception
  {
    return genericRequest(uri, "DELETE");
  }

  protected Pair<Integer, String> postRequest(URI uri, JSONObject req)
     throws Exception
  {
    return genericRequest(uri, "POST", req);
  }

  protected Pair<Integer, String> putRequest(URI uri, JSONObject req)
     throws Exception
  {
    return genericRequest(uri, "PUT", req);
  }

  protected Pair<Integer, String> patchRequest(URI uri, JSONObject req)
     throws Exception
  {
    return genericRequest(uri, "PATCH", req);
  }
}
