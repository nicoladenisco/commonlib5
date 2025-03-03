/*
 * Copyright (C) 2018 Nicola De Nisco
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

import com.sun.mail.util.MailSSLSocketFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Semplifica l'invio delle e-mail.
 * Esempio d'uso:
 * <pre><code>
 * SimpleMultiMailSender sm = new SimpleMultiMailSender("smpt.domain.it", 587, SimpleMultiMailSender.SMTP_PROTOCOL_STARTTLS, true);
 * sm.setAuth("xxx", "xxx");
 * sm.setDebugMailSession(true);
 *
 * sm.addRecipients("dest1@domain.com");
 * sm.addBodyPart("Questa è una prova di testo semplice.");
 * sm.addBodyPart("&lt;html&gt;&lt;body&gt;Questa è una prova di testo html.&lt;/body&gt;&lt;/html&gt;", "text/html; charset=UTF-8");
 * sm.addBodyPart("&lt;html&gt;&lt;body&gt;Questa è una prova di altro testo html.&lt;/body&gt;&lt;/html&gt;", "text/html; charset=UTF-8");
 * sm.addBodyPart(new File("/tmp/error_log"));
 * sm.addBodyPart(new File("/tmp/logoCsGroup.png"));
 * sm.addMail("Prova SimpleMultiMailSender (primo messaggio)", "from1@domain.com");
 *
 * sm.addRecipients("dest2@domain.com");
 * sm.addBodyPart("Questa è una prova di testo semplice.");
 * sm.addMail("Prova SimpleMultiMailSender (secondo messaggio)", "from2@domain.com");
 *
 * sm.sendAllMessages();
 * </code></pre>
 *
 * @author Nicola De Nisco
 */
public class SimpleMultiMailSender extends AbstractMailSender
{
  protected ArrayList<Message> messaggi = new ArrayList<>();
  protected Session mailSession = null;
  protected int maxMessageSession = 15;

  /**
   * Costruttore sender e-mail.
   * @param host host SMTP a cui collegarsi
   * @param porta porta relativa
   * @param protocollo protocollo utilizzato (vedi SMTP_PROTOCOL_...)
   * @param ignoraCertificato ignora certificato del server (non controlla validita)
   */
  public SimpleMultiMailSender(String host, int porta, int protocollo, boolean ignoraCertificato)
  {
    super(host, porta, protocollo, ignoraCertificato);
  }

  public int getMaxMessageSession()
  {
    return maxMessageSession;
  }

  public void setMaxMessageSession(int maxMessageSession)
  {
    this.maxMessageSession = maxMessageSession;
  }

  public void init(String from)
     throws Exception
  {
    Properties mailProps = new Properties();

    if(ignoraCertificato)
    {
      MailSSLSocketFactory sf = new MailSSLSocketFactory();
      sf.setTrustAllHosts(true);
      mailProps.put("mail.smtp.ssl.socketFactory", sf);
    }
    else
    {
      mailProps.put("mail.smtp.ssl.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    mailProps.put("mail.smtp.from", from);
    mailProps.put("mail.smtp.host", host);
    mailProps.put("mail.smtp.port", Integer.toString(porta));
    mailProps.put("mail.smtp.auth", "true");

    mailProps.put("mail.smtp.socketFactory.port", Integer.toString(porta));
    mailProps.put("mail.smtp.socketFactory.fallback", "false");

    mailProps.put("mail.smtp.starttls.enable", "false");
    mailProps.put("mail.smtp.starttls.required", "false");
    mailProps.put("mail.smtp.ssl.enable", "false");

    switch(protocollo)
    {
      case SMTP_PROTOCOL_CLEAR: // in chiaro
        break;
      case SMTP_PROTOCOL_TLS: // TLS
      case SMTP_PROTOCOL_SSL: // SSL
        mailProps.put("mail.smtp.ssl.enable", "true");
        mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProps.put("mail.smtp.socketFactory.fallback", "false");
        break;
      case SMTP_PROTOCOL_STARTTLS: // STARTTSL
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.starttls.required", "true");
        break;
    }

    mailSession = Session.getDefaultInstance(mailProps, new SimpleAuthenticator());
    mailSession.setDebug(debugMailSession);
  }

  /**
   * Aggiunge messaggio alla lista.
   * Le componenti del corpo e i destinatari devono già essere impostati;
   * dopo la creazione del messaggio verranno distrutti.
   * Il messaggio rimane nella lista; per spedire tutti i messaggi usare sendAllMessages().
   * @param subject oggetto del messaggio
   * @param from mittente del messaggio
   * @throws Exception
   */
  public void addMail(String subject, String from)
     throws Exception
  {
    if(mailSession == null)
      init(from);

    if(destinatari.isEmpty())
      throw new Exception("Recipient list is empty.");

    if(parti.isEmpty())
      throw new Exception("Body parts list is empty.");

    MimeMessage message = new MimeMessage(mailSession);
    message.setFrom(new InternetAddress(from));
    InternetAddress[] arDest = destinatari.toArray(new InternetAddress[0]);
    message.setRecipients(Message.RecipientType.TO, arDest);
    message.setSubject(subject, "UTF-8");

    message.setContent(prepareMultipart());
    message.setSentDate(new Date());

    messaggi.add(message);
    clear();

    if(messaggi.size() >= maxMessageSession)
    {
      sendAllMessages();
      messaggi.clear();
    }
  }

  /**
   * Funzione di invio messaggi.
   * Viene aperto il canale con il mail server e inviati tutti i messaggi.
   * @throws MessagingException
   */
  public void sendAllMessages()
     throws MessagingException
  {
    if(messaggi.isEmpty())
      return;

    try(Transport transport
       = (protocollo == SMTP_PROTOCOL_TLS || protocollo == SMTP_PROTOCOL_SSL)
            ? mailSession.getTransport("smtps") : mailSession.getTransport("smtp"))
    {
      transport.connect(host, porta, utente, password);

      for(Message msg : messaggi)
      {
        transport.sendMessage(msg, msg.getAllRecipients());
      }
    }
  }
}
