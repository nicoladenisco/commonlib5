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
package org.commonlib5.exec;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test per DownloadHelper.
 *
 * @author Nicola De Nisco
 */
public class DownloadHelperTest
{
  private static HttpServer server;
  private static String baseUrl;
  private static String authUrl;
  private static String redirectUrl;
  private static String delayUrl;
  private static String notFoundUrl;
  private static File tempDir;

  @BeforeClass
  public static void setUpClass()
     throws Exception
  {
    tempDir = Files.createTempDirectory("download_helper_test").toFile();
    tempDir.deleteOnExit();

    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    int port = server.getAddress().getPort();
    baseUrl = "http://127.0.0.1:" + port + "/test.txt";
    authUrl = "http://127.0.0.1:" + port + "/auth/test.txt";
    redirectUrl = "http://127.0.0.1:" + port + "/redirect";
    delayUrl = "http://127.0.0.1:" + port + "/delay";
    notFoundUrl = "http://127.0.0.1:" + port + "/404";

    // Standard endpoint
    server.createContext("/test.txt", new HttpHandler()
    {
      @Override
      public void handle(HttpExchange exchange)
         throws IOException
      {
        byte[] response = "Hello World".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try(OutputStream os = exchange.getResponseBody())
        {
          os.write(response);
        }
      }
    });

    // Auth endpoint
    HttpContext authContext = server.createContext("/auth/test.txt", new HttpHandler()
    {
      @Override
      public void handle(HttpExchange exchange)
         throws IOException
      {
        byte[] response = "Authenticated Content".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try(OutputStream os = exchange.getResponseBody())
        {
          os.write(response);
        }
      }
    });
    authContext.setAuthenticator(new BasicAuthenticator("testrealm")
    {
      @Override
      public boolean checkCredentials(String username, String password)
      {
        return "admin".equals(username) && "secret".equals(password);
      }
    });

    // Redirect endpoint
    server.createContext("/redirect", new HttpHandler()
    {
      @Override
      public void handle(HttpExchange exchange)
         throws IOException
      {
        exchange.getResponseHeaders().set("Location", "/test.txt");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
      }
    });

    // Delay endpoint (per test dei timeout)
    server.createContext("/delay", new HttpHandler()
    {
      @Override
      public void handle(HttpExchange exchange)
         throws IOException
      {
        try
        {
          Thread.sleep(3000);
        }
        catch(InterruptedException ignored)
        {
        }
        byte[] response = "Delayed Content".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try(OutputStream os = exchange.getResponseBody())
        {
          os.write(response);
        }
      }
    });

    // 404 endpoint
    server.createContext("/404", new HttpHandler()
    {
      @Override
      public void handle(HttpExchange exchange)
         throws IOException
      {
        byte[] response = "Not Found".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(404, response.length);
        try(OutputStream os = exchange.getResponseBody())
        {
          os.write(response);
        }
      }
    });

    server.start();
  }

  @AfterClass
  public static void tearDownClass()
  {
    if(server != null)
    {
      server.stop(0);
    }
  }

  @Test
  public void testDownloadWithHttpClient_Success()
     throws Exception
  {
    File outFile = new File(tempDir, "http_success.txt");
    boolean ok = DownloadHelper.downloadWithHttpClient(baseUrl, outFile);
    assertTrue("Il download con HttpClient dovrebbe avere successo", ok);
    assertTrue("Il file scaricato deve esistere", outFile.exists());
    assertEquals("Hello World", Files.readString(outFile.toPath()));
  }

  @Test
  public void testDownloadWithCurl_Success()
     throws Exception
  {
    File outFile = new File(tempDir, "curl_success.txt");
    boolean ok = DownloadHelper.downloadWithCurl(baseUrl, outFile);
    assertTrue("Il download con Curl dovrebbe avere successo", ok);
    assertTrue("Il file scaricato deve esistere", outFile.exists());
    assertEquals("Hello World", Files.readString(outFile.toPath()));
  }

  @Test
  public void testDownloadWithHttpClient_Auth()
     throws Exception
  {
    File outFile = new File(tempDir, "http_auth.txt");
    boolean ok = DownloadHelper.downloadWithHttpClient(authUrl, outFile, "admin", "secret");
    assertTrue("Il download autenticato con HttpClient dovrebbe avere successo", ok);
    assertTrue("Il file scaricato deve esistere", outFile.exists());
    assertEquals("Authenticated Content", Files.readString(outFile.toPath()));

    File outFileFail = new File(tempDir, "http_auth_fail.txt");
    boolean okFail = DownloadHelper.downloadWithHttpClient(authUrl, outFileFail, "admin", "wrongpassword");
    assertFalse("Il download con credenziali errate deve fallire", okFail);
    assertFalse("Il file scaricato non deve esistere in caso di errore", outFileFail.exists());
  }

  @Test
  public void testDownloadWithCurl_Auth()
     throws Exception
  {
    File outFile = new File(tempDir, "curl_auth.txt");
    boolean ok = DownloadHelper.downloadWithCurl(authUrl, outFile, "admin", "secret");
    assertTrue("Il download autenticato con Curl dovrebbe avere successo", ok);
    assertTrue("Il file scaricato deve esistere", outFile.exists());
    assertEquals("Authenticated Content", Files.readString(outFile.toPath()));

    File outFileFail = new File(tempDir, "curl_auth_fail.txt");
    boolean okFail = DownloadHelper.downloadWithCurl(authUrl, outFileFail, "admin", "wrongpassword");
    assertFalse("Il download con credenziali errate deve fallire", okFail);
    assertFalse("Il file scaricato non deve esistere in caso di errore", outFileFail.exists());
  }

  @Test
  public void testDownloadWithHttpClient_Redirect()
     throws Exception
  {
    File outFile = new File(tempDir, "http_redirect.txt");
    boolean ok = DownloadHelper.downloadWithHttpClient(redirectUrl, outFile, true, true);
    assertTrue("Il download con redirect su HttpClient dovrebbe avere successo", ok);
    assertEquals("Hello World", Files.readString(outFile.toPath()));
  }

  @Test
  public void testDownloadWithCurl_Redirect()
     throws Exception
  {
    File outFile = new File(tempDir, "curl_redirect.txt");
    boolean ok = DownloadHelper.downloadWithCurl(redirectUrl, outFile, true, true);
    assertTrue("Il download con redirect su Curl dovrebbe avere successo", ok);
    assertEquals("Hello World", Files.readString(outFile.toPath()));
  }

  @Test
  public void testDownloadWithHttpClient_NotFound()
     throws Exception
  {
    File outFile = new File(tempDir, "http_404.txt");
    boolean ok = DownloadHelper.downloadWithHttpClient(notFoundUrl, outFile, true, true);
    assertFalse("Il download su 404 deve ritornare false", ok);
    assertFalse("Il file non deve essere salvato se failOnError è true", outFile.exists());
  }

  @Test
  public void testDownloadWithCurl_NotFound()
     throws Exception
  {
    File outFile = new File(tempDir, "curl_404.txt");
    boolean ok = DownloadHelper.downloadWithCurl(notFoundUrl, outFile, true, true);
    assertFalse("Il download su 404 deve ritornare false", ok);
    assertFalse("Il file non deve essere salvato se failOnError è true", outFile.exists());
  }

  @Test
  public void testDownloadWithHttpClient_Timeout()
     throws Exception
  {
    File outFile = new File(tempDir, "http_timeout.txt");
    boolean ok = false;
    try
    {
      ok = DownloadHelper.downloadWithHttpClient(delayUrl, outFile, 1000);
    }
    catch(Exception ignored)
    {
    }
    assertFalse("Il download che va in timeout con HttpClient deve ritornare false o lanciare eccezione", ok);
    assertFalse("Il file non deve esistere in caso di timeout", outFile.exists());
  }

  @Test
  public void testDownloadWithCurl_Timeout()
     throws Exception
  {
    File outFile = new File(tempDir, "curl_timeout.txt");
    boolean ok = DownloadHelper.downloadWithCurl(delayUrl, outFile, 1000);
    assertFalse("Il download che va in timeout con Curl deve ritornare false", ok);
    assertFalse("Il file non deve esistere in caso di timeout", outFile.exists());
  }
}
