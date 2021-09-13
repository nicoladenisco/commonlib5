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
package org.commonlib5.test;

import java.io.File;
import org.commonlib5.mail.SimpleMailSender;
import org.commonlib5.mail.SimpleMultiMailSender;

/**
 * Prova invio mail.
 *
 * @author Nicola De Nisco
 */
public class testSendMail
{
  public static void main1(String[] args)
  {
    try
    {
      SimpleMailSender sm = new SimpleMailSender("smtps.aruba.it", 465,
         SimpleMailSender.SMTP_PROTOCOL_SSL, true);
      sm.setAuth("xxx", "xxx");
      sm.setDebugMailSession(true);
      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      //sm.addBodyPart(new File("/tmp/error_log"));
      //sm.addBodyPart(new File("/tmp/logoCsGroup.png"));
      sm.sendMail("Prova SimpleMailSender", "caleido@infomedica.it");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main2(String[] args)
  {
    try
    {
      SimpleMailSender sm = new SimpleMailSender("mail.infomedica.it", 587,
         SimpleMailSender.SMTP_PROTOCOL_STARTTLS, true);
      sm.setAuth("xxx", "xxx");
      sm.setDebugMailSession(true);
      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      //sm.addBodyPart(new File("/tmp/error_log"));
      //sm.addBodyPart(new File("/tmp/logoCsGroup.png"));
      sm.sendMail("Prova SimpleMailSender", "caleido@infomedica.it");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main3(String[] args)
  {
    try
    {
      SimpleMultiMailSender sm = new SimpleMultiMailSender("mail.infomedica.it", 587,
         SimpleMailSender.SMTP_PROTOCOL_STARTTLS, true);
      sm.setAuth("xxx", "xxx");
      sm.setDebugMailSession(true);

      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart(new File("/tmp/Ecografia.jpg"));
      sm.addMail("Prova SimpleMultiMailSender (primo messaggio)", "caleido@infomedica.it");

      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart(new File("/tmp/Ecografia.jpg"));
      sm.addMail("Prova SimpleMultiMailSender (secondo messaggio)", "caleido@infomedica.it");

      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart(new File("/tmp/Ecografia.jpg"));
      sm.addMail("Prova SimpleMultiMailSender (terzo messaggio)", "caleido@infomedica.it");

      sm.sendAllMessages();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    try
    {
      SimpleMultiMailSender sm = new SimpleMultiMailSender("smtps.aruba.it", 465,
         SimpleMailSender.SMTP_PROTOCOL_SSL, true);
      sm.setAuth("xxx", "xxx");
      sm.setDebugMailSession(true);

      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart(new File("/tmp/Ecografia.jpg"));
      sm.addMail("Prova SimpleMultiMailSender (primo messaggio)", "caleido@infomedica.it");

      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart(new File("/tmp/Ecografia.jpg"));
      sm.addMail("Prova SimpleMultiMailSender (secondo messaggio)", "caleido@infomedica.it");

      sm.addRecipients("ndenisco@infomedica.it");
      sm.addBodyPart("Questa è una prova di testo semplice.");
      sm.addBodyPart("<html><body>Questa è una prova di testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart("<html><body>Questa è una prova di altro testo html.</body></html>", "text/html; charset=UTF-8");
      sm.addBodyPart(new File("/tmp/Ecografia.jpg"));
      sm.addMail("Prova SimpleMultiMailSender (terzo messaggio)", "caleido@infomedica.it");

      sm.sendAllMessages();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
