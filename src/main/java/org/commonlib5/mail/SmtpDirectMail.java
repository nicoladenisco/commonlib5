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
package org.commonlib5.mail;

import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import org.commonlib5.io.StringBufferInputStreamCorrected;
import org.commonlib5.utils.CommonFileUtils;
//import sun.misc.BASE64Encoder;

/**
 * Invio di posta elettronica direttamente via SMTP.
 * Questa classe consente di inviare in modo semplice
 * posta elettronica da una applicazione. Utilizza il protocollo SMTP
 * per collegarsi con un server per il relay (per default localhost).
 */
public class SmtpDirectMail
{
  protected InputStream is;
  protected OutputStream os;
  protected BufferedReader in;
  protected PrintWriter out;
  protected String response;
  protected Vector<File> attachList = new Vector<File>(16, 16);
  protected Vector<String> destList = new Vector<String>(16, 16);
  protected Vector<String> headers = new Vector<String>(16, 16);
  protected Vector<StreamSupplier> ssupList = new Vector<StreamSupplier>(16, 16);
  /**
   * Parametri per l'invio della posta
   */
  protected String heloHostName;
  protected String from;
  protected String subject;
  protected String message;
  protected String smtpServer;
  protected int smtpPort;
  protected String smtpEncodign = "UTF-8";

  /**
   * Costruttore semplice: il mailer sara' localhost
   * @throws java.lang.Exception
   */
  public SmtpDirectMail()
     throws Exception
  {
    smtpServer = "localhost";
    smtpPort = 25;
  }

  /**
   * Costruttore con indicazione del mailer
   * @param smtpPort
   */
  public SmtpDirectMail(String smtpServer, int smtpPort)
  {
    this.smtpServer = smtpServer;
    this.smtpPort = smtpPort;
  }

  /**
   * Aggiunge il file specificato alla lista degli attach
   * @return
   */
  public int addAttach(File fileAttach)
     throws Exception
  {
    if(!fileAttach.canRead())
      throw new Exception("File non esistente o illeggibile");

    attachList.add(fileAttach);
    return attachList.size() - 1;
  }

  /**
   * Aggiunge il file specificato alla lista degli attach
   * @return
   */
  public int addAttach(StreamSupplier ss)
     throws Exception
  {
    ssupList.add(ss);
    return ssupList.size() - 1;
  }

  public int addHeader(String headerString)
     throws Exception
  {
    headers.add(headerString);
    return headers.size() - 1;
  }

  /**
   * Aggiunge il destinatario alla lista dei destinatari per il messaggio
   * @return
   */
  public int addRecipient(String nome)
  {
    destList.add(nome);
    return destList.size() - 1;
  }

  /**
   * Resetta tutti i parametri di invio (per un nuovo messaggio)
   * NON resetta l'host smtp
   */
  public void reset()
  {
    from = null;
    subject = null;
    message = null;
    response = null;
    attachList.clear();
    destList.clear();
    ssupList.clear();
    headers.clear();
  }

  /**
   * Invia il messaggio di posta elettronica
   * @throws java.lang.Exception
   */
  public void sendMail()
     throws Exception
  {
    // verifica parametri per invio posta
    if(smtpServer == null)
      throw new IllegalArgumentException("Server smtp non specificato.");

    // apre socket e stream verso servizio SMTP sull'host indicato
    Socket s = new Socket(smtpServer, smtpPort);
    try
    {
      sendMail(s);
    }
    finally
    {
      s.close();
    }
  }

  /**
   * Invia il messaggio di posta elettronica
   * @throws java.lang.Exception
   */
  public void sendMail(Socket s)
     throws Exception
  {
    is = s.getInputStream();
    os = s.getOutputStream();

    try
    {
      sendMail(is, os);
    }
    finally
    {
      is.close();
      os.close();
      is = null;
      os = null;
    }
  }

  /**
   * Invia il messaggio di posta elettronica
   * @throws java.lang.Exception
   */
  public void sendMail(InputStream is, OutputStream os)
     throws Exception
  {
    if(from == null)
      throw new IllegalArgumentException("Mittente del messaggio non specificato.");
    if(destList.isEmpty())
      throw new IllegalArgumentException("Nessun destinatario per il messaggio.");
    if(subject == null && message == null)
      throw new IllegalArgumentException("Messaggio vuoto. Occorre specificare il soggetto o il messaggio.");

    // determina nome del computer locale
    if(heloHostName == null)
      heloHostName = InetAddress.getLocalHost().getHostName();

    out = new PrintWriter(new OutputStreamWriter(os, smtpEncodign));
    in = new BufferedReader(new InputStreamReader(is, smtpEncodign));

    try
    {
      // invia intestazione SMTP
      response = "";
      send(null);
      send("HELO " + heloHostName);
      send("MAIL FROM: " + from);

      for(String sDest : destList)
        send("RCPT TO: " + sDest);

      send("DATA");

      for(String hs : headers)
        out.println(hs);

      // invia soggetto del messaggio
      if(subject != null)
        out.println("SUBJECT: " + subject);

      // invia corpo del messaggio
      if(attachList.size() > 0 || ssupList.size() > 0)
        sendEmailMultipartMime();
      else
        sendEmailSimple();

      // chiude le comunicazioni
      send(".");
      send("quit");
    }
    finally
    {
      out.flush();
      out.close();
      in.close();
    }
  }

  /**
   * Funzione di servizio per invio e handshake con server SMTP
   * @throws java.io.IOException
   */
  protected void send(String s)
     throws IOException
  {
    if(s != null)
    {
      response += s + "\n";
      out.println(s);
      out.flush();
      System.out.println(">:" + s);
    }
    String line;
    if((line = in.readLine()) != null)
    {
      response += line + "\n";
      System.out.println("<:" + response);
    }
  }

  /**
   * Funzione di servizio per invio messaggio semplice (no attach)
   * @throws java.lang.Exception
   */
  protected void sendEmailSimple()
     throws Exception
  {
    out.println("");

    // invia corpo del messaggio
    if(message != null)
      out.println(message);
  }

  /**
   * Funzione di servizio per invio messaggio multipart (con attach)
   * @throws java.lang.Exception
   */
  protected void sendEmailMultipartMime()
     throws Exception
  {
    String boundaryID = ("thisismymsgboundary" + Math.random()).replace('.', 'A');

    out.println("MIME-Version: 1.0");
    out.println("Content-Type: multipart/related;");
    out.println(" boundary=\"" + boundaryID + "\"");
    out.println("");
    out.println("");

    // invia corpo del messaggio
    if(message != null)
    {
      out.println("--" + boundaryID);
      out.println("Content-Type: text/plain");
      out.println("");
      out.println(message);
    }

    for(int i = 0; i < attachList.size(); i++)
    {
      File f = (File) attachList.get(i);

      out.println("--" + boundaryID);
      out.println("Content-Type: application/unknow");
      out.println("Content-Transfer-Encoding: base64");
      out.println("Content-Disposition: attachment; filename=\"" + f.getName() + "\"");
      out.println("");
      out.flush();

      // invia il file codificato come uuencode
      Base64.Encoder encoder = Base64.getEncoder();
      out.write(encoder.encodeToString(Files.readAllBytes(f.toPath())));

      out.println("");
    }

    for(int i = 0; i < ssupList.size(); i++)
    {
      StreamSupplier ss = (StreamSupplier) (ssupList.get(i));

      out.println("--" + boundaryID);
      out.println("Content-Type: " + ss.getMimeType(i));
      out.println("Content-Transfer-Encoding: base64");
      if(ss.isMimeInLine(i))
        out.println("Content-Disposition: in-line; filename=\"" + ss.getNomeFile(i) + "\"");
      else
        out.println("Content-Disposition: attachment; filename=\"" + ss.getNomeFile(i) + "\"");
      out.println("");
      out.flush();

//      try (InputStream istr = ss.getInput(i))
//      {
//        new BASE64Encoder().encodeBuffer(istr, os);
//        ss.notifyDone(istr, i);
//        out.println("");
//      }
    }

    out.println("--" + boundaryID + "--");
  }

//  /**
//   * Funzione di servizio per l'invio degli attach
//   */
//  protected void sendAttach()
//     throws Exception
//  {
//    // invia eventuali caratteri in attesa
//    out.flush();
//
//    for(int i = 0; i < attachList.size(); i++)
//    {
//      File f = (File) attachList.get(i);
//
//      // invia il file codificato come uuencode
//      FileInputStream fis = new FileInputStream(f);
//      new UUEncoder(f.getName()).encodeBuffer(fis, os);
//      fis.close();
//    }
//
//    for(int i = 0; i < ssupList.size(); i++)
//    {
//      StreamSupplier ss = (StreamSupplier) (ssupList.get(i));
//      InputStream istr = ss.getInput(i);
//      new UUEncoder(ss.getNomeFile(i)).encodeBuffer(istr, os);
//      ss.notifyDone(istr, i);
//    }
//  }
  /**
   * Ritorna lo stato dell'operazione
   * @return
   */
  public String getResponse()
  {
    return response;
  }

  public String getFrom()
  {
    return from;
  }

  public void setFrom(String from)
  {
    this.from = from;
  }

  public String getHeloHostName()
  {
    return heloHostName;
  }

  public void setHeloHostName(String heloHostName)
  {
    this.heloHostName = heloHostName;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getSmtpEncodign()
  {
    return smtpEncodign;
  }

  public void setSmtpEncodign(String smtpEncodign)
  {
    this.smtpEncodign = smtpEncodign;
  }

  public int getSmtpPort()
  {
    return smtpPort;
  }

  public void setSmtpPort(int smtpPort)
  {
    this.smtpPort = smtpPort;
  }

  public String getSmtpServer()
  {
    return smtpServer;
  }

  public void setSmtpServer(String smtpServer)
  {
    this.smtpServer = smtpServer;
  }

  public String getSubject()
  {
    return subject;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public void setSmtpserver(String newSmtpserver)
  {
    smtpServer = newSmtpserver;
  }

  public String getSmtpserver()
  {
    return smtpServer;
  }

  /**
   * Questa interfaccia consente di inviare files attach
   * senza dover materialmente scrivere un files su disco.
   * Oggetti che implementano questa interfaccia possono essere
   * aggiunti con il metodo addAttach(streamSupplier)
   */
  public interface StreamSupplier
  {
    /**
     * Restituisce uno stream con i dati da iniviare.
     * @param id indice dello stream durante la spedizione
     * @return lo stream da cui leggere i dati
     */
    public InputStream getInput(int id);

    /**
     * Restituisce un nome file da indicare nella mail:
     * @param id indice dello stream durante la spedizione
     * @return il nome del file
     */
    public String getNomeFile(int id);

    /**
     * Segnala fine delle operazioni sullo stream.
     * Utile ad esempio per chiudere lo stream se necessario.
     * @param st stream retstituito da getInput()
     * @param id indice dello stream durante la spedizione
     */
    public void notifyDone(InputStream st, int id);

    /**
     * Ritorna il tipo mime da specificare nell'attachment.
     * @param id indice dello stream durante la spedizione
     * @return tipo mime
     */
    public String getMimeType(int id);

    /**
     * Ritorna vero se l'indicazione di attache deve essere in-line.
     * @param id indice dello stream durante la spedizione
     * @return se vero sarà in-line piuttosto che attachment
     */
    public boolean isMimeInLine(int id);
  }

  /**
   * La piu' semplice implementazione possibile per streamSupplier.
   * Questo oggetto viene costruito con due stringhe una che rappresenta
   * il nome del file e un'altra con il contenuto.
   */
  public static class DatiFiles implements SmtpDirectMail.StreamSupplier
  {
    public String nomeFile, mimeType = "application/unknow";
    public InputStream isbuf;
    public boolean inLine = false;

    public DatiFiles(String nfile, InputStream is)
    {
      nomeFile = nfile;
      isbuf = is;
    }

    public DatiFiles(String nfile, String dati)
    {
      nomeFile = nfile;
      isbuf = new StringBufferInputStreamCorrected(dati);
    }

    // implementazione interfaccia
    @Override
    public InputStream getInput(int parm1)
    {
      return isbuf;
    }

    @Override
    public String getNomeFile(int parm1)
    {
      return nomeFile;
    }

    @Override
    public void notifyDone(InputStream parm1, int parm2)
    {
      CommonFileUtils.safeClose(isbuf);
      isbuf = null;
      nomeFile = null;
    }

    @Override
    public String getMimeType(int id)
    {
      return mimeType;
    }

    @Override
    public boolean isMimeInLine(int id)
    {
      return inLine;
    }
  }
}
