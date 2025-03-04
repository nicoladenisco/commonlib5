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

import java.io.File;

/**
 * Classe per l'identificazione precisa di un sistema supportato.
 *
 * @author Nicola De Nisco
 */
public class OsIdent
{
  public static final int OS_UNDEFINED = 0;
  public static final int OS_LINUX = 1;
  public static final int OS_MACOSX = 2;
  public static final int OS_WINDOWS = 3;
  public static final int OS_SOLARIS = 4;
  public static final int OS_FREEBSD = 5;
  public static final int OS_UNKNOW = 6;
  //
  private static int osType = OS_UNDEFINED;
  private static boolean vm64bit = false;
  private static String javaVersion;
  private static File dirTmp;
  private static final Object semaforo = new Object();

  /**
   * Idendifica il sistema operativo ospite.
   * @return una delle costanti OS_...
   */
  public static int checkOStype()
  {
    if(osType == OS_UNDEFINED)
    {
      synchronized(semaforo)
      {
        if(osType == OS_UNDEFINED)
        {
          __checkOStypePrivate();
        }
      }
    }

    return osType;
  }

  private static int __checkOStypePrivate()
  {
    String os = (System.getProperty("os.name")).toLowerCase();

    if(os.contains("windows"))
      osType = OS_WINDOWS;
    else if(os.contains("linux"))
      osType = OS_LINUX;
    else if(os.contains("mac os x"))
      osType = OS_MACOSX;
    else if(os.contains("solaris"))
      osType = OS_SOLARIS;
    else if(os.contains("freebsd"))
      osType = OS_FREEBSD;
    else
      osType = OS_UNKNOW;

    javaVersion = System.getProperty("java.version");

    // verifica per architetture a 64 bit
    vm64bit = System.getProperty("sun.arch.data.model").equals("64");

    switch(osType)
    {
      case OS_WINDOWS:
        dirTmp = new File("c:\\windows\\temp");
        break;
      case OS_LINUX:
      case OS_MACOSX:
      case OS_SOLARIS:
      case OS_FREEBSD:
        dirTmp = new File("/tmp");
        break;
    }

    if(dirTmp != null)
      dirTmp.mkdirs();

    return osType;
  }

  /**
   * Verifica se il sistema operativo è Unix.
   * @return vero se verificato
   */
  public static boolean isUnix()
  {
    switch(checkOStype())
    {
      case OS_LINUX:
      case OS_MACOSX:
      case OS_SOLARIS:
      case OS_FREEBSD:
        return true;
    }
    return false;
  }

  /**
   * Verifica per un sistema Unix 'puro' (no MACOSX).
   * @return vero se verificato
   */
  public static boolean isPureUnix()
  {
    switch(checkOStype())
    {
      case OS_LINUX:
      case OS_SOLARIS:
      case OS_FREEBSD:
        return true;
    }
    return false;
  }

  public static boolean isLinux()
  {
    return checkOStype() == OS_LINUX;
  }

  public static boolean isSolaris()
  {
    return checkOStype() == OS_SOLARIS;
  }

  public static boolean isFreeBsd()
  {
    return checkOStype() == OS_FREEBSD;
  }

  public static boolean isMac()
  {
    return checkOStype() == OS_MACOSX;
  }

  public static boolean isWindows()
  {
    return checkOStype() == OS_WINDOWS;
  }

  /**
   * Verifica per sistema operativo a 64 bit.
   * @return vero se verificato
   */
  public static boolean is64bit()
  {
    if(osType == OS_UNDEFINED)
      checkOStype();

    return vm64bit;
  }

  /**
   * Ritorna directory di archiviazione locale.
   * Una applicazione può memorizzare dati locali relativi
   * all'utente in una cartella appositamente dedicata.
   * La cartella viene creata se non esiste.
   * L'ubicazione cambia a seconda del sistema operativo:
   *
   * <ul>
   * <li>/home/utente/.applicationName per Unix</li>
   * <li>/Users/utente/AppData/Roaming/applicationName per Windows</li>
   * <li>/Users/utente/Library/Application Support/applicationName per Mac OS X</li>
   * </ul>
   *
   * @param applicationName nome dell'applicazione
   * @return la directory per i dati locali
   */
  public static File getAppDirectory(String applicationName)
  {
    String userHome = System.getProperty("user.home", ".");
    File appDirectory;

    switch(checkOStype())
    {
      case OS_LINUX:
      case OS_SOLARIS:
      case OS_FREEBSD:
        appDirectory = new File(userHome, "." + applicationName);
        break;

      case OS_WINDOWS:
        String applicationData = System.getenv("APPDATA");
        if(applicationData != null)
          appDirectory = new File(applicationData, applicationName);
        else
          appDirectory = new File(userHome, applicationName);
        break;

      case OS_MACOSX:
        appDirectory = new File(userHome, "Library" + File.separator
           + "Application Support" + File.separator + applicationName);
        break;

      default:
        return new File(".");
    }

    if(!appDirectory.exists())
      if(!appDirectory.mkdirs())
        throw new RuntimeException("The working directory could not be created: " + appDirectory.getAbsolutePath());

    return appDirectory;
  }

  /**
   * Ritorna directory per cache dei dati.
   * ATTENZIONE: in questa directory devono essere presenti
   * solo dati di cache, ovvero la loro distruzione non deve
   * compromettere il funzionamento dell'applicazione.
   * Per una directory di storage locale utilizzare getAppDirectory().
   * Se l'applicazione utilizza una cache su disco per le sue
   * elaborazioni, il suo punto di memorizzazione naturale è
   * questa directory appositamente creata secondo regole
   * diverse a seconda del sistema operativo ospite:
   *
   * <ul>
   * <li>/home/utente/.cache/applicationName per Unix</li>
   * <li>/Users/utente/AppData/Roaming/cache/applicationName per Windows</li>
   * <li>/Users/utente/Library/Caches/applicationName per Mac OS X</li>
   * </ul>
   *
   * @param applicationName nome dell'applicazione
   * @return la directory per una cache su disco dei dati elaborati
   */
  public static File getCacheDirectory(String applicationName)
  {
    String userHome = System.getProperty("user.home", ".");
    File cacheDirectory;

    switch(checkOStype())
    {
      case OS_LINUX:
      case OS_SOLARIS:
      case OS_FREEBSD:
        cacheDirectory = new File(userHome, ".cache/" + applicationName);
        break;

      case OS_WINDOWS:
        String applicationData = System.getenv("APPDATA");
        if(applicationData != null)
          cacheDirectory = new File(applicationData, "cache\\" + applicationName);
        else
          cacheDirectory = new File(userHome, "cache\\" + applicationName);
        break;

      case OS_MACOSX:
        cacheDirectory = new File(userHome, "Library" + File.separator
           + "Caches" + File.separator + applicationName);
        break;

      default:
        return new File(".");
    }

    if(!cacheDirectory.exists())
      if(!cacheDirectory.mkdirs())
        throw new RuntimeException("The cache directory could not be created: " + cacheDirectory.getAbsolutePath());

    return cacheDirectory;
  }

  public static File getSystemTemp()
  {
    checkOStype();
    return dirTmp;
  }

  /**
   * File filter to access dynamic library in JFileDialog.
   * Check the platform and accept only .dll or .so or .dylib files.
   */
  public static class LibraryFileFilterSwing extends javax.swing.filechooser.FileFilter
  {
    protected String description = null;
    protected int os = checkOStype();

    public LibraryFileFilterSwing(String description)
    {
      this.description = description;
    }

    @Override
    public boolean accept(File file)
    {
      String test = file.getName().trim().toLowerCase();

      switch(os)
      {
        case OS_WINDOWS:
          return test.endsWith(".dll");
        case OS_LINUX:
        case OS_SOLARIS:
        case OS_FREEBSD:
          return test.endsWith(".so");
        case OS_MACOSX:
          return test.endsWith(".dylib");
      }

      return false;
    }

    @Override
    public String getDescription()
    {
      switch(os)
      {
        case OS_WINDOWS:
          return description + " (*.dll)";
        case OS_LINUX:
        case OS_SOLARIS:
        case OS_FREEBSD:
          return description + " (*.so)";
        case OS_MACOSX:
          return description + " (*.dylib)";
      }

      return description;
    }
  }

  public static class LibraryFileFilterIo implements java.io.FileFilter
  {
    protected String description = null;
    protected int os = checkOStype();

    @Override
    public boolean accept(File file)
    {
      String test = file.getName().trim().toLowerCase();

      switch(os)
      {
        case OS_WINDOWS:
          return test.endsWith(".dll");
        case OS_LINUX:
        case OS_SOLARIS:
        case OS_FREEBSD:
          return test.endsWith(".so");
        case OS_MACOSX:
          return test.endsWith(".dylib");
      }

      return false;
    }
  }

  public static String getSystemDescr()
  {
    String s = "";

    switch(osType)
    {
      case OS_UNDEFINED:
        s += "OS_UNDEFINED";
        break;
      case OS_LINUX:
        s += "OS_LINUX";
        break;
      case OS_MACOSX:
        s += "OS_MACOSX";
        break;
      case OS_WINDOWS:
        s += "OS_WINDOWS";
        break;
      case OS_SOLARIS:
        s += "OS_SOLARIS";
        break;
      case OS_FREEBSD:
        s += "OS_FREEBSD";
        break;
    }

    s += vm64bit ? " 64bit" : " 32bit";
    return s;
  }

  public static String getJavaVersion()
  {
    checkOStype();
    return javaVersion;
  }

  public static float getJavaVersionNumber()
  {
    checkOStype();

    int pos;
    if(javaVersion.startsWith("1.") && (pos = javaVersion.indexOf('.', 2)) != -1)
      return Float.parseFloat(javaVersion.substring(0, pos));

    if((pos = javaVersion.indexOf('.')) != -1)
      return Float.parseFloat(javaVersion.substring(0, pos));

    return Float.parseFloat(javaVersion);
  }

  public static boolean isJava8()
  {
    checkOStype();
    return javaVersion.startsWith("1.8.");
  }

  public static boolean isJava11()
  {
    checkOStype();
    return javaVersion.startsWith("11.");
  }

  public static boolean isJava17()
  {
    checkOStype();
    return javaVersion.startsWith("17.");
  }

  public static boolean isJava21()
  {
    checkOStype();
    return javaVersion.startsWith("21.");
  }
}
