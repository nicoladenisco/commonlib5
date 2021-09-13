/*
 * Copyright (C) 2021 Nicola De Nisco
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
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.commonlib5.utils.CommonFileUtils;

/**
 * Classe base dei test di firma.
 *
 * @author Nicola De Nisco
 */
public class testFirmaBase
{
  protected static char[] pin;

  public static void prepare()
     throws Exception
  {
    // configurazione per il logging a console (Log4j/apache commons)
    BasicConfigurator.configure(new ConsoleAppender(
       new PatternLayout("%d [%t] %-5p %c{1} - %m%n")));

    File pinFile = new File("/tmp/pin.txt");
    if(!pinFile.exists())
    {
      System.err.println("Manca il file " + pinFile.getAbsolutePath());
      System.exit(-1);
    }

    String[] linee = CommonFileUtils.grep(pinFile, "UTF-8", null);
    pin = linee[0].toCharArray();
  }
}
