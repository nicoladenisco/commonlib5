/*
 * Copyright (C) 2017 Nicola De Nisco
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
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Semplifica l'invio delle e-mail.
 * Esempio d'uso:
 * <pre><code>
 * SimpleMailSender sm = new SimpleMailSender("smpt.domain.it", 587, SimpleMailSender.SMTP_PROTOCOL_STARTTLS, true);
 * sm.setAuth("xxx", "xxx");
 * sm.setDebugMailSession(true);
 * sm.addRecipients("dest@domain.com");
 * sm.addBodyPart("Questa è una prova di testo semplice.");
 * sm.addBodyPart("&lt;html&gt;&lt;body&gt;Questa è una prova di testo html.&lt;/body&gt;&lt;/html&gt;", "text/html; charset=UTF-8");
 * sm.addBodyPart("&lt;html&gt;&lt;body&gt;Questa è una prova di altro testo html.&lt;/body&gt;&lt;/html&gt;", "text/html; charset=UTF-8");
 * sm.addBodyPart(new File("/tmp/error_log"));
 * sm.addBodyPart(new File("/tmp/logoCsGroup.png"));
 * sm.sendMail("Prova SimpleMailSender", "from@domain.com");
 * </code></pre>
 *
 * @author Nicola De Nisco
 */
public class SimpleMailSender extends AbstractMailSender
{
  /**
   * Costruttore sender e-mail.
   * @param host host SMTP a cui collegarsi
   * @param porta porta relativa
   * @param protocollo protocollo utilizzato (vedi SMTP_PROTOCOL_...)
   * @param ignoraCertificato ignora certificato del server (non controlla validita)
   */
  public SimpleMailSender(String host, int porta, int protocollo, boolean ignoraCertificato)
  {
    super(host, porta, protocollo, ignoraCertificato);
  }

  /**
   * Invia e-mail.
   * @param subject oggetto del messaggio
   * @param from mittente del messaggio
   * @throws Exception
   */
  public void sendMail(String subject, String from)
     throws Exception
  {
    if(destinatari.isEmpty())
      throw new Exception("Recipient list is empty.");

    if(parti.isEmpty())
      throw new Exception("Body parts list is empty.");

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

    Session mailSession = Session.getDefaultInstance(mailProps, new SimpleAuthenticator());
    mailSession.setDebug(debugMailSession);

    // costruisce ed invia il messaggio
    sendWorker(mailSession, from, subject);
  }

  /**
   * Funzione di serivizio per l'invio.
   * Dopo che la sessione è stata creata con la modalità e le credenziali indicate
   * viene chiamata questa funzione per inviare effettivamente il messaggio.
   * @param mailSession sessione di invio
   * @param from mittente del messaggio
   * @param subject oggetto del messaggio
   * @throws MessagingException
   */
  protected void sendWorker(Session mailSession, String from, String subject)
     throws MessagingException
  {
    MimeMessage message = new MimeMessage(mailSession);
    message.setFrom(new InternetAddress(from));
    InternetAddress[] arDest = destinatari.toArray(new InternetAddress[destinatari.size()]);
    message.setRecipients(Message.RecipientType.TO, arDest);
    message.setSubject(subject, "UTF-8");

    message.setContent(prepareMultipart());
    message.setSentDate(new Date());

    Transport.send(message);
  }
}
