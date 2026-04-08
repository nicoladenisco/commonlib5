/*
 * Copyright (C) 2026 Nicola De Nisco
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

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import org.commonlib5.io.ByteBufferOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Helper per chiamate API REST (json).
 *
 * @author nicola
 */
public class JsonHelper5 implements Closeable
{
  protected final URI uri;
  protected final ArrayMap<String, String> headers = new ArrayMap<>();
  protected boolean wrapException = false;

  private static final HttpClient httpClient = HttpClient.newBuilder()
     .connectTimeout(Duration.ofSeconds(60))
     .build();

  public JsonHelper5(URI uri)
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

  public boolean isWrapException()
  {
    return wrapException;
  }

  public void setWrapException(boolean wrapException)
  {
    this.wrapException = wrapException;
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
    return wrappedJsonResponse(rv);
  }

  public Pair<Integer, JSONObject> getAsJson()
     throws Exception
  {
    Pair<Integer, String> rv = getRequest(uri);
    return wrappedJsonResponse(rv);
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
    return wrappedJsonResponse(rv);
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
    return wrappedJsonResponse(rv);
  }

  public Pair<Integer, JSONObject> patchAsJson(JSONArray request)
     throws Exception
  {
    Pair<Integer, String> rv = patchRequest(uri, request);
    return wrappedJsonResponse(rv);
  }

  public Pair<Integer, JSONObject> deleteAsJson()
     throws Exception
  {
    Pair<Integer, String> rv = deleteRequest(uri);
    return wrappedJsonResponse(rv);
  }

  private Pair<Integer, JSONObject> wrappedJsonResponse(Pair<Integer, String> rv)
  {
    try
    {
      return new Pair<>(rv.first, new JSONObject(rv.second));
    }
    catch(Exception e)
    {
      // Gestiamo eventuale response corrotta che non permetterebbe la costruzione del JSONObject
      return new Pair<>(rv.first, null);
    }
  }

  protected Pair<Integer, String> processResponse(HttpResponse<byte[]> response)
     throws Exception
  {
    /* Con HttpClient il body è sempre disponibile sia per successi che per errori.
       Inoltre, wrapException è superfluo: non serve più il catch per recuperare l'ErrorStream. */
    ByteBufferOutputStream bos = new ByteBufferOutputStream();
    InputStream is = new ByteArrayInputStream(response.body());

    CommonFileUtils.copyStream(is, bos);

    return new Pair<>(response.statusCode(), bos.toString("UTF-8"));
  }

  public URL buildConnection(URI uri1)
     throws Exception
  {
    return uri1.toURL();
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONObject req)
     throws Exception
  {
    String input = req.toString();
    byte[] byteInput = input.getBytes("UTF-8");

    return genericRequest(uri, method, byteInput);
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONArray req)
     throws Exception
  {
    String input = req.toString();
    byte[] byteInput = input.getBytes("UTF-8");

    return genericRequest(uri, method, byteInput);
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, byte[] byteInput)
     throws Exception
  {
    try
    {
      // Se byteInput è null o vuoto, usa noBody(), altrimenti usa il publisher per i byte
      HttpRequest.BodyPublisher publisher = (byteInput == null || byteInput.length == 0)
                                               ? HttpRequest.BodyPublishers.noBody()
                                               : HttpRequest.BodyPublishers.ofByteArray(byteInput);

      HttpRequest.Builder builder = HttpRequest.newBuilder()
         .uri(uri)
         .header("Content-Type", "application/json")
         .header("Accept", "application/json")
         .method(method, publisher);

      headers.forEach((k, v) -> builder.setHeader(k, v));

      HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
      return processResponse(response);
    }
    catch(Exception e)
    {
      // Se wrapException è attivo vengono gestiti errori di rete, timeout o parsing (non coperti dagli status HTTP)
      if(wrapException)
      {
        return new Pair<>(-1, e.getMessage());
      }
      throw e;
    }
  }

  protected Pair<Integer, String> getRequest(URI uri)
     throws Exception
  {
    return genericRequest(uri, "GET", (byte[]) null);
  }

  protected Pair<Integer, String> deleteRequest(URI uri)
     throws Exception
  {
    return genericRequest(uri, "DELETE", (byte[]) null);
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

  protected Pair<Integer, String> patchRequest(URI uri, JSONArray req)
     throws Exception
  {
    return genericRequest(uri, "PATCH", req);
  }
}
