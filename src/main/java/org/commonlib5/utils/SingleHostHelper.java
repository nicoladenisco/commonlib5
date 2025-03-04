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

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper per creare una singola istanza dell'applicazione.
 * Apre un socket di controllo per rendere univoca l'applicazione sull'host.
 * Il socket di controllo accetta connessioni che possono inviare metacomandi.
 * Il socket è in ascolto solo su localhost.
 * Una istanza di applicazione dopo aver costruito l'istanza, prova a chiamare
 * tryConnect e se questo ritorna vero esce dall'applicazione.
 * Altrimenti chiama la funzione openListener.
 * Il comando minimale per tryConnect è quit ovvero tryConnect("quit") che provoca
 * una immediata chiusura del socket lato server.
 * Volendo si possono inviare altri comandi che verranno implementati nelle classi
 * derivate implementando metodi del tipo:
 * 'void cmd_nome_minuscolo(String[] params, PrintWriter pw)'.
 * @author Nicola De Nisco
 */
public class SingleHostHelper
{
  private static Log log = LogFactory.getLog(SingleHostHelper.class);

  protected String appname;
  protected ServerSocket server;
  protected Thread th;
  protected int port;
  protected transient InetAddress listenAddress = InetAddress.getLoopbackAddress();
  protected transient UUID marker = UUID.randomUUID();

  public SingleHostHelper(String appname, int port)
  {
    this.appname = appname;
    this.port = port;
  }

  public InetAddress getListenAddress()
  {
    return listenAddress;
  }

  public void setListenAddress(InetAddress listenAddress)
  {
    this.listenAddress = listenAddress;
  }

  /**
   * Verifica per attivita.
   * @return vero se il server è attivo
   */
  public boolean isAlive()
  {
    return th != null && th.isAlive();
  }

  /**
   * Tenta di connettersi ad una istanza già presente.
   * @param command comando da inviare all'istanza se esiste.
   * @return vero se già esiste una istanza
   */
  public boolean tryConnect(String command)
  {
    try (Socket sock = new Socket("127.0.0.1", port))
    {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
         PrintWriter pr = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8")))
      {
        boolean rv = handShake(br);
        if(rv)
          pr.println(command);
        pr.println("quit");
        return rv;
      }
    }
    catch(Throwable ex)
    {
      // connessione non riuscita: possiamo assumere nessuna istanza in esecuzione
      return false;
    }
  }

  /**
   * Controlla che l'istanza in esecuzione non siamo noi stessi.
   * @param br
   * @return vero se istanza diversa da noi
   * @throws IOException
   */
  protected boolean handShake(final BufferedReader br)
     throws IOException
  {
    String presentation = br.readLine();
    log.debug("<= " + presentation);

    if(presentation.startsWith("220 "))
    {
      int pos = presentation.indexOf("UUID=");
      if(pos != -1)
      {
        String remoteAppName = presentation.substring(4, pos - 8);
        if(!remoteAppName.equals(appname))
          return true;

        // se lo UUID siamo noi, allora stiamo parlando con noi stessi, non un'altra istanza
        UUID remote = UUID.fromString(presentation.substring(pos + 5));
        if(marker.equals(remote))
          return false;
      }
    }

    return true;
  }

  /**
   * Attiva listener per connessioni.
   * @throws Exception
   */
  public void openListener()
     throws Exception
  {
    server = new ServerSocket(port, 5, listenAddress);
    th = new Thread(() -> waitConnection());
    th.setName("SingleHostHelper");
    th.setDaemon(true);
    th.start();
  }

  /**
   * Funzione di servizio per attesa connessioni.
   * Viene eseguita all'interno di un thread dedicato.
   * Attende connessioni TCP/IP e invoca handleConnection quando un client si connette.
   */
  protected void waitConnection()
  {
    while(true)
    {
      try
      {
        try (Socket sock = server.accept())
        {
          handleConnection(sock);
        }
      }
      catch(Throwable ex)
      {
        log.error("", ex);
      }
    }
  }

  /**
   * Gestione della connessione con un client.
   * Quando un client si connette viene invocata questa funzione.
   * Implementa un dispatcher minimale di comandi; encoding UTF 8.
   * Ogni comando è una linea terminata da INVIO con una serie di parole separate da spazio.
   * La linea viene segmentata in un array e passata a executeCommand.
   * @param sock socket del client
   * @return vero se uscita richiesta dal comando quit.
   * @throws IOException
   */
  protected boolean handleConnection(final Socket sock)
     throws IOException
  {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
       PrintWriter pr = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8")))
    {
      pr.println("220 " + appname + " READY; UUID=" + marker.toString());
      pr.flush();

      try
      {
        String s;
        while((s = br.readLine()) != null)
        {
          String[] cmd = StringOper.split(s, ' ');
          if(cmd.length != 0)
          {
            switch(cmd[0].toLowerCase())
            {
              case "quit":
                pr.println("221 Goodbye.");
                pr.flush();
                return true;

              default:
                executeCommand(cmd, pr);
                pr.flush();
                break;
            }
          }
        }
      }
      catch(Exception ex)
      {
        log.error("FATAL ERROR", ex);
        pr.println("550 FATAL ERROR: " + ex.getMessage());
        pr.flush();
      }
    }

    return false;
  }

  /**
   * Esegue un comando generico.
   * Il comando è rappresentato da un array di stringhe;
   * il primo elemento è il nome del comando gli altri elementi sono parametri.
   * Cerca nella classe (o sua derivata) un metodo 'void cmd_nome_minuscolo(String[] params, PrintWriter pw)'.
   * Il metodo viene quindi invocato.
   * In caso di successo su pw verrà inviato qualcosa del tipo:
   * '200 ACTIVATE command successful.'
   * in caso di errore qualcosa del tipo
   * '500 Unknown command'
   * @param cmd array con comando e parametri
   * @param pr print writer per ritorno risualtato
   * @throws Exception
   */
  protected void executeCommand(String[] cmd, PrintWriter pr)
     throws Exception
  {
    String command = "cmd_" + cmd[0].toLowerCase();
    Method m = null;

    try
    {
      if((m = this.getClass().getMethod(command, String[].class, PrintWriter.class)) == null)
      {
        pr.println("500 Unknown command");
        return;
      }
    }
    catch(NoSuchMethodException noSuchMethodException)
    {
      pr.println("500 Unknown command");
      return;
    }
    catch(SecurityException securityException)
    {
      pr.println("500 Unknown command (SecurityException)");
      return;
    }

    m.invoke(this, cmd, pr);
  }
}
