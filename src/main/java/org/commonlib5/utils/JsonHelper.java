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
package org.commonlib5.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.commonlib5.io.ByteBufferOutputStream;
import org.json.JSONArray;
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
  protected boolean wrapException = false;
  protected static boolean javaPatchApplied = false;

  public JsonHelper(URI uri)
  {
    this.uri = uri;
  }

  public JsonHelper(URI uri, boolean applayJavaPatchWorkaround)
  {
    this.uri = uri;

    if(applayJavaPatchWorkaround && !javaPatchApplied)
      javaPatchWorkaround();
  }

  /**
   * Aggiunta del motodo PATCH alla JVM ove fosse richiesto.
   * Per default non è previsto l'http method PATCH. L'errore che si presenta è:
   * java.net.ProtocolException: Invalid HTTP method: PATCH
   * La seguente soluzione, precedentemente adottata, non veniva accettata dall'endpoint invocato:
   * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
   * conn.setRequestMethod("POST");
   * conn.setRequestProperty ("X-HTTP-Method-Override", "PATCH");
   * (vedi porzione commentata nei metodi genericRequest)
   * Per ovviare al problema forziamo tramite reflection i metodi http consentiti
   */
  public static void javaPatchWorkaround()
  {
    try
    {
      Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
      methodsField.setAccessible(true);

      // get the methods field modifiers
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      // bypass the "private" modifier
      modifiersField.setAccessible(true);

      // remove the "final" modifier
      modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

      // valid HTTP methods
      String[] methods =
      {
        "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE", "PATCH"
      };

      // set the new methods - including patch
      methodsField.set(null, methods);

      javaPatchApplied = true;
    }
    catch(IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static boolean isJavaPatchApplied()
  {
    return javaPatchApplied;
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

  protected Pair<Integer, String> processResponse(HttpURLConnection conn)
     throws Exception
  {
    ByteBufferOutputStream bos = new ByteBufferOutputStream();

    try(InputStream is = conn.getInputStream())
    {
      CommonFileUtils.copyStream(is, bos);
    }
    catch(Exception ex)
    {
      if(wrapException)
      {
        try(InputStream is = conn.getErrorStream())
        {
          if(is != null)
            CommonFileUtils.copyStream(is, bos);
        }
      }
      else
        throw ex;
    }

    return new Pair<>(conn.getResponseCode(), bos.toString("UTF-8"));
  }

  public URL buildConnection(URI uri1)
     throws Exception
  {
    return uri1.toURL();
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method)
     throws Exception
  {
    URL url = buildConnection(uri);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try
    {
      conn.setDoOutput(true);
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type", "application/json");
      headers.forEach((k, v) -> conn.setRequestProperty(k, v));

      return processResponse(conn);
    }
    finally
    {
      conn.disconnect();
    }
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONObject req)
     throws Exception
  {
    String input = req.toString();
    byte[] byteInput = input.getBytes("UTF-8");

    if(byteInput.length == 0)
      return genericRequest(uri, method);

    return genericRequest(uri, method, byteInput);
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONArray req)
     throws Exception
  {
    String input = req.toString();
    byte[] byteInput = input.getBytes("UTF-8");

    if(byteInput.length == 0)
      return genericRequest(uri, method);

    return genericRequest(uri, method, byteInput);
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, byte[] byteInput)
     throws Exception
  {
    URL url = buildConnection(uri);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try
    {
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");

//      if("PATCH".equals(method))
//      {
//        // siamo obbligati a fare questo per il method PATCH.
//        // Leggere il link per i dettagli: https://medium.com/javarevisited/invalid-http-method-patch-e12ba62ddd9f
//        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
//        conn.setRequestMethod("POST");
//      }
//      else
      conn.setRequestMethod(method);

      headers.forEach((k, v) -> conn.setRequestProperty(k, v));

      conn.setFixedLengthStreamingMode(byteInput.length);
      conn.getOutputStream().write(byteInput);

      return processResponse(conn);
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

  protected Pair<Integer, String> patchRequest(URI uri, JSONArray req)
     throws Exception
  {
    return genericRequest(uri, "PATCH", req);
  }
}
