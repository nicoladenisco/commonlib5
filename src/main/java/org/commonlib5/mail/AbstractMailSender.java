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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.commonlib5.utils.StringOper;

/**
 * Classe base di SimpleMailSender e SimpleMultiMailSender.
 *
 * @author Nicola De Nisco
 */
public abstract class AbstractMailSender
{
  public static final int SMTP_PROTOCOL_CLEAR = 0;
  public static final int SMTP_PROTOCOL_TLS = 1;
  public static final int SMTP_PROTOCOL_SSL = 2;
  public static final int SMTP_PROTOCOL_STARTTLS = 3;
  //
  protected String host;
  protected int porta;
  protected int protocollo;
  protected boolean ignoraCertificato = false;
  protected boolean debugMailSession = false;
  protected String utente;
  protected String password;
  protected ArrayList<InternetAddress> destinatari = new ArrayList<>();
  protected ArrayList<MimeBodyPart> parti = new ArrayList<>();

  public AbstractMailSender(String host, int porta, int protocollo, boolean ignoraCertificato)
  {
    this.host = host;
    this.porta = porta;
    this.protocollo = protocollo;
    this.ignoraCertificato = ignoraCertificato;
  }

  /**
   * Imposta credenziali se richiesto.
   * @param utente utente di autenticazione
   * @param password passoword di autenticazione
   */
  public void setAuth(String utente, String password)
  {
    this.utente = utente;
    this.password = password;
  }

  public boolean isDebugMailSession()
  {
    return debugMailSession;
  }

  public void setDebugMailSession(boolean debugMailSession)
  {
    this.debugMailSession = debugMailSession;
  }

  /**
   * Aggiunge un destinatario.
   * @param rcpt info destinatario
   */
  public void addRecipient(InternetAddress rcpt)
  {
    destinatari.add(rcpt);
  }

  /**
   * Aggiunge un destinatario.
   * @param rcpt info destinatario
   * @throws javax.mail.internet.AddressException
   */
  public void addRecipient(String rcpt)
     throws AddressException
  {
    addRecipient(new InternetAddress(rcpt.trim().toLowerCase()));
  }

  /**
   * Aggiunge destinatari.
   * @param recipients uno o più indirizzi di e-mail separati da virgola
   * @throws AddressException
   */
  public void addRecipients(String recipients)
     throws AddressException
  {
    String[] emails = StringOper.string2Array(recipients, ",", true);
    addRecipients(emails);
  }

  /**
   * Aggiunge destinatari.
   * @param recipients array di indirizzi email
   * @throws AddressException
   */
  public void addRecipients(String[] recipients)
     throws AddressException
  {
    for(int i = 0; i < recipients.length; i++)
    {
      addRecipient(recipients[i]);
    }
  }

  /**
   * Aggiunge una parte del corpo messaggio.
   * @param mbp parte del corpo messaggio
   */
  public void addBodyPart(MimeBodyPart mbp)
  {
    parti.add(mbp);
  }

  /**
   * Aggiunge una parte del corpo messaggio (testo semplice ovvero 'text/plain').
   * @param testoSemplice testo da aggiungere
   * @throws MessagingException
   */
  public void addBodyPart(String testoSemplice)
     throws MessagingException
  {
    MimeBodyPart mbp = new MimeBodyPart();
    mbp.setContent(testoSemplice, "text/plain; charset=UTF-8");
    addBodyPart(mbp);
  }

  /**
   * Aggiunge una parte del corpo messaggio.
   * @param body testo da aggiungere
   * @param tipoMime tipo mime corrispondente
   * @throws MessagingException
   */
  public void addBodyPart(String body, String tipoMime)
     throws MessagingException
  {
    MimeBodyPart mbp = new MimeBodyPart();
    mbp.setContent(body, tipoMime);
    addBodyPart(mbp);
  }

  /**
   * Aggiunge una parte del corpo messaggio (attachment).
   * @param file file da aggiungere
   * @throws MessagingException
   * @throws IOException
   */
  public void addBodyPart(File file)
     throws MessagingException, IOException
  {
    MimeBodyPart mbp = new MimeBodyPart();
    mbp.attachFile(file);
    addBodyPart(mbp);
  }

  /**
   * Pulisce destinatari e parti del messaggio.
   */
  public void clear()
  {
    destinatari.clear();
    parti.clear();
  }

  /**
   * Pulisce destinatari.
   */
  public void clearRecipients()
  {
    destinatari.clear();
  }

  /**
   * Pulisce parti del messaggio.
   */
  public void clearBodyParts()
  {
    parti.clear();
  }

  /**
   * Prepara il multipart a partire dai componenti del messaggio.
   * @return multipart fusione componenti
   * @throws MessagingException
   */
  protected Multipart prepareMultipart()
     throws MessagingException
  {
    Multipart mp = new MimeMultipart();
    for(MimeBodyPart mbp : parti)
      mp.addBodyPart(mbp);
    return mp;
  }

  /**
   * Autenticatore di sessione per utente e password forniti.
   */
  protected class SimpleAuthenticator extends Authenticator
  {
    @Override
    protected PasswordAuthentication getPasswordAuthentication()
    {
      return new PasswordAuthentication(utente, password);
    }
  }
}
