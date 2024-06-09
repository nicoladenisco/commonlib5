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

  // Fino alla JDK 8 non era previsto l'http method PATCH. L'errore che si presenta è:
  // java.net.ProtocolException: Invalid HTTP method: PATCH
  // La seguente soluzione, precedentemente adottata, non veniva accettata dall'endpoint invocato:
  // HttpURLConnection conn = (HttpURLConnection) url.openConnection();
  // conn.setRequestMethod("POST");
  // conn.setRequestProperty ("X-HTTP-Method-Override", "PATCH");
  // (vedi porzione commentata nei metodi genericRequest)
  // Per ovviare al problema forziamo tramite reflection i metodi http consentiti
  static
  {
    try
    {
      if(OsIdent.getJavaVersionNumber() <= 1.8f)
      {
        Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
        methodsField.setAccessible(true);
        // get the methods field modifiers
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        // bypass the "private" modifier
        modifiersField.setAccessible(true);

        // remove the "final" modifier
        modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

        /* valid HTTP methods */
        String[] methods =
        {
          "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE", "PATCH"
        };
        // set the new methods - including patch
        methodsField.set(null, methods);
      }
    }
    catch(IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex)
    {
      throw new RuntimeException(ex);
    }
  }

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

  protected Pair<Integer, String> processResponse(HttpURLConnection conn)
     throws IOException, Exception
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

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONObject req)
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
//        // Per l'http method PATCH siamo obbligati a fare questo.
//        // Andare qui per i dettagli: https://medium.com/javarevisited/invalid-http-method-patch-e12ba62ddd9f
//        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
//        conn.setRequestMethod("POST");
//      }
//      else
      conn.setRequestMethod(method);

      headers.forEach((k, v) -> conn.setRequestProperty(k, v));

      String input = req.toString();
      byte[] byteInput = input.getBytes("UTF-8");

      conn.setFixedLengthStreamingMode(byteInput.length);
      conn.getOutputStream().write(byteInput);

      return processResponse(conn);
    }
    finally
    {
      conn.disconnect();
    }
  }

  protected Pair<Integer, String> genericRequest(URI uri, String method, JSONArray req)
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

      String input = req.toString();
      byte[] byteInput = input.getBytes("UTF-8");

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
