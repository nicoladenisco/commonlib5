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

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Supporto al download di files da HTTP.
 *
 * @author Nicola De Nisco
 */
public class DownloadHelper
{
  /**
   * Scarica un file da un URL utilizzando il comando esterno curl.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il contenuto.
   * @return true se il download ha avuto successo (exit code 0), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante
   * l'esecuzione del processo.
   * @throws InterruptedException Se il processo viene interrotto.
   */
  public static boolean downloadWithCurl(String url, File outputFile)
     throws IOException, InterruptedException
  {
    return downloadWithCurl(url, outputFile, null, null, true, true, 0);
  }

  /**
   * Scarica un file da un URL utilizzando il comando esterno curl con timeout.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il
   * contenuto.
   * @param timeoutMillis Timeout in millisecondi per la connessione (0 per nessun timeout).
   * @return true se il download ha avuto successo (exit code 0), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante
   * l'esecuzione del processo.
   * @throws InterruptedException Se il processo viene interrotto.
   */
  public static boolean downloadWithCurl(String url, File outputFile, int timeoutMillis)
     throws IOException, InterruptedException
  {
    return downloadWithCurl(url, outputFile, null, null, true, true, timeoutMillis);
  }

  /**
   * Scarica un file da un URL con autenticazione utilizzando il comando esterno
   * curl.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null se
   * non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se non
   * richiesta).
   * @return true se il download ha avuto successo (exit code 0), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante
   * l'esecuzione del processo.
   * @throws InterruptedException Se il processo viene interrotto.
   */
  public static boolean downloadWithCurl(String url, File outputFile, String username, String password)
     throws IOException, InterruptedException
  {
    return downloadWithCurl(url, outputFile, username, password, true, true, 0);
  }

  /**
   * Scarica un file da un URL con autenticazione e timeout utilizzando il comando
   * esterno curl.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il
   * contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null
   * se non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se
   * non richiesta).
   * @param timeoutMillis Timeout in millisecondi per la connessione (0 per nessun timeout).
   * @return true se il download ha avuto successo (exit code 0), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante
   * l'esecuzione del processo.
   * @throws InterruptedException Se il processo viene interrotto.
   */
  public static boolean downloadWithCurl(String url, File outputFile, String username, String password, int timeoutMillis)
     throws IOException, InterruptedException
  {
    return downloadWithCurl(url, outputFile, username, password, true, true, timeoutMillis);
  }

  /**
   * Scarica un file da un URL utilizzando curl con autenticazione, opzioni per il
   * controllo dei redirect, errori HTTP e timeout.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il
   * contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null
   * se non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se
   * non richiesta).
   * @param followRedirects Se true, aggiunge l'opzione -L per seguire i
   * reindirizzamenti.
   * @param failOnError Se true, aggiunge l'opzione -f per far fallire curl in
   * caso di errori HTTP (es. 404, 500).
   * @param timeoutMillis Timeout in millisecondi per la connessione (0 per nessun timeout).
   * @return true se il download ha avuto successo (exit code 0), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante
   * l'esecuzione del processo.
   * @throws InterruptedException Se il processo viene interrotto.
   */
  public static boolean downloadWithCurl(String url, File outputFile, String username, String password,
     boolean followRedirects, boolean failOnError, int timeoutMillis)
     throws IOException, InterruptedException
  {
    return downloadWithCurl(url, outputFile, username, password, followRedirects, failOnError, timeoutMillis, 0, null, null);
  }

  /**
   * Scarica un file da un URL utilizzando curl con autenticazione, opzioni per il
   * controllo dei redirect, errori HTTP e timeout.<br>
   * curl supporta una vasta gamma di protocolli ed estese funzioni di globbing.
   * <ul>
   * <li>https://fun.example/{one,two,three}.jpg</li>
   * <li>sftp://{one,two,three}.example/README</li>
   * <li>ftp://ftp.example.com/file[1-100].txt</li>
   * </ul>
   * usa 'man curl' per maggiori informazioni
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null se non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se non richiesta).
   * @param followRedirects Se true, aggiunge l'opzione -L per seguire i reindirizzamenti.
   * @param failOnError Se true, aggiunge l'opzione -f per far fallire curl in
   * caso di errori HTTP (es. 404, 500).
   * @param timeoutMillis Timeout in millisecondi per la connessione (0 per nessun timeout).
   * @param maxTimeMillis tempo massimo in millisecondi per il trasferimento (0 per nessun limite).
   * @param options eventuali opzioni per curl (può essere null se non richiesto).
   * @param logFileCurl eventuale file con output di curl (può essere null se non richiesto).
   * @return true se il download ha avuto successo (exit code 0), false altrimenti.
   * @throws IOException Se si verifica un errore I/O durante l'esecuzione del processo.
   * @throws InterruptedException Se il processo viene interrotto.
   */
  public static boolean downloadWithCurl(String url, File outputFile, String username, String password,
     boolean followRedirects, boolean failOnError, int timeoutMillis, int maxTimeMillis,
     Collection<String> options, File logFileCurl)
     throws IOException, InterruptedException
  {
    List<String> command = new ArrayList<>();
    command.add("curl");
    command.add("-sS"); // Silent mode, ma mostra eventuali errori

    if(options != null)
      command.addAll(options);

    if(failOnError)
    {
      command.add("-f"); // Fallisce silenziosamente su errore HTTP (es. 404)
    }

    if(followRedirects)
    {
      command.add("-L"); // Segue i reindirizzamenti HTTP
    }

    if(timeoutMillis > 0)
    {
      float timeoutSecs = timeoutMillis / 1000.0f;
      command.add("--connect-timeout");
      command.add(String.valueOf(timeoutSecs));
    }

    if(maxTimeMillis > 0)
    {
      float mantimeSecs = maxTimeMillis / 1000.0f;
      command.add("--max-time");
      command.add(String.valueOf(mantimeSecs));
    }

    if(username != null && !username.isEmpty())
    {
      command.add("-u");
      if(password != null)
      {
        command.add(username + ":" + password);
      }
      else
      {
        command.add(username);
      }
    }

    command.add("-o");
    command.add(outputFile.getAbsolutePath());
    command.add(url);

    ProcessBuilder pb = new ProcessBuilder(command);

    // se richiesto invia output di curl a file log dedicato
    if(logFileCurl != null)
    {
      // fonde stderr e stdout
      pb.redirectErrorStream(true);
      pb.redirectOutput(logFileCurl);
    }

    Process process = pb.start();
    int exitCode;

    if(maxTimeMillis > 0)
    {
      boolean finished = process.waitFor(maxTimeMillis + 500, TimeUnit.MILLISECONDS);
      if(!finished)
      {
        process.destroyForcibly();
        if(failOnError && outputFile.exists())
        {
          Files.deleteIfExists(outputFile.toPath());
        }
        return false;
      }
      exitCode = process.exitValue();
    }
    else
    {
      exitCode = process.waitFor();
    }

    if(exitCode != 0 && failOnError && outputFile.exists())
    {
      Files.deleteIfExists(outputFile.toPath());
    }

    return exitCode == 0;
  }

  /**
   * Scarica un file da un URL utilizzando l'HttpClient nativo di Java 11+.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il contenuto.
   * @return true se il download ha avuto successo (status code 200-299), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante il
   * download.
   * @throws InterruptedException Se la richiesta viene interrotta.
   */
  public static boolean downloadWithHttpClient(String url, File outputFile)
     throws IOException,
     InterruptedException
  {
    return downloadWithHttpClient(url, outputFile, null, null, true, true, 0);
  }

  /**
   * Scarica un file da un URL utilizzando l'HttpClient nativo di Java 11+ con
   * timeout.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il
   * contenuto.
   * @param timeoutMillis Timeout in millisecondi (0 per nessun timeout).
   * @return true se il download ha avuto successo (status code 200-299), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante il
   * download.
   * @throws InterruptedException Se la richiesta viene interrotta.
   */
  public static boolean downloadWithHttpClient(String url, File outputFile, int timeoutMillis)
     throws IOException,
     InterruptedException
  {
    return downloadWithHttpClient(url, outputFile, null, null, true, true, timeoutMillis);
  }

  /**
   * Scarica un file da un URL con autenticazione utilizzando l'HttpClient nativo
   * di Java 11+.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null se
   * non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se non
   * richiesta).
   * @return true se il download ha avuto successo (status code 200-299), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante il
   * download.
   * @throws InterruptedException Se la richiesta viene interrotta.
   */
  public static boolean downloadWithHttpClient(String url, File outputFile, String username, String password)
     throws IOException, InterruptedException
  {
    return downloadWithHttpClient(url, outputFile, username, password, true, true, 0);
  }

  /**
   * Scarica un file da un URL con autenticazione e timeout utilizzando
   * l'HttpClient nativo di Java 11+.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il
   * contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null
   * se non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se
   * non richiesta).
   * @param timeoutMillis Timeout in millisecondi (0 per nessun timeout).
   * @return true se il download ha avuto successo (status code 200-299), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante il
   * download.
   * @throws InterruptedException Se la richiesta viene interrotta.
   */
  public static boolean downloadWithHttpClient(String url, File outputFile, String username, String password, int timeoutMillis)
     throws IOException, InterruptedException
  {
    return downloadWithHttpClient(url, outputFile, username, password, true, true, timeoutMillis);
  }

  /**
   * Scarica un file da un URL utilizzando l'HttpClient nativo di Java 11+ con
   * autenticazione, opzioni per il controllo dei redirect, errori HTTP e timeout.
   *
   * @param url L'URL della risorsa da scaricare.
   * @param outputFile Il file di destinazione locale dove salvare il
   * contenuto.
   * @param username Nome utente per l'autenticazione HTTP (può essere null
   * se non richiesta).
   * @param password Password per l'autenticazione HTTP (può essere null se
   * non richiesta).
   * @param followRedirects Se true, segue i reindirizzamenti HTTP.
   * @param failOnError Se true, fa fallire il download e cancella il file in
   * caso di codice di errore HTTP (>= 400).
   * @param timeoutMillis Timeout in millisecondi (0 per nessun timeout).
   * @return true se il download ha avuto successo (status code 200-299), false
   * altrimenti.
   * @throws IOException Se si verifica un errore I/O durante il
   * download.
   * @throws InterruptedException Se la richiesta viene interrotta.
   */
  public static boolean downloadWithHttpClient(String url, File outputFile, String username, String password,
     boolean followRedirects, boolean failOnError, int timeoutMillis)
     throws IOException, InterruptedException
  {
    File parent = outputFile.getParentFile();
    if(parent != null && !parent.exists())
    {
      parent.mkdirs();
    }

    HttpClient.Builder clientBuilder = HttpClient.newBuilder();

    if(followRedirects)
    {
      clientBuilder.followRedirects(HttpClient.Redirect.ALWAYS);
    }
    else
    {
      clientBuilder.followRedirects(HttpClient.Redirect.NEVER);
    }

    if(timeoutMillis > 0)
    {
      clientBuilder.connectTimeout(Duration.ofMillis(timeoutMillis));
    }

    if(username != null && !username.isEmpty())
    {
      final String pwd = (password != null) ? password : "";
      clientBuilder.authenticator(new Authenticator()
      {
        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
          return new PasswordAuthentication(username, pwd.toCharArray());
        }
      });
    }

    HttpClient client = clientBuilder.build();

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
       .uri(URI.create(url))
       .GET();

    if(timeoutMillis > 0)
    {
      requestBuilder.timeout(Duration.ofMillis(timeoutMillis));
    }

    HttpRequest request = requestBuilder.build();

    try
    {
      HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(
         outputFile.toPath(),
         StandardOpenOption.CREATE,
         StandardOpenOption.WRITE,
         StandardOpenOption.TRUNCATE_EXISTING));

      int statusCode = response.statusCode();

      if(failOnError && (statusCode < 200 || statusCode >= 400))
      {
        if(outputFile.exists())
        {
          Files.deleteIfExists(outputFile.toPath());
        }
        return false;
      }

      return statusCode >= 200 && statusCode < 300;
    }
    catch(IOException e)
    {
      if(failOnError)
      {
        if(outputFile.exists())
        {
          try
          {
            Files.deleteIfExists(outputFile.toPath());
          }
          catch(IOException ignored)
          {
          }
        }
        return false;
      }
      throw e;
    }
  }
}
