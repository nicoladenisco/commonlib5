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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonlib5.io.ByteBufferOutputStream;

/**
 * Funzioni di utilita' per la manipolazione delle classi.
 *
 * @author Nicola De Nisco
 */
public class ClassOper
{
  private static final Log log = LogFactory.getLog(ClassOper.class);

  /**
   * Caricatore dinamico di classe.
   * @param className nome completo della classe da caricare
   * @return classe o null se non trovata
   */
  public static Class loadClass(String className)
  {
    try
    {
      return Class.forName(className);
    }
    catch(ClassNotFoundException e)
    {
      return null;
    }
    catch(Throwable e)
    {
      log.error("Unexpected error loading class " + className, e);
      return null;
    }
  }

  /**
   * Caricatore dinamico di classe.
   * @param basePath path base della classe (può essere null)
   * @param nameOnly nome della classe senza path
   * @return classe o null se non trovata
   */
  public static Class loadClass(String basePath, String nameOnly)
  {
    if(basePath == null)
      return loadClass(nameOnly);

    if(basePath.endsWith("."))
      return loadClass(basePath + nameOnly);

    Class c = loadClass(basePath + nameOnly);

    if(c != null)
      return c;

    return loadClass(basePath + "." + nameOnly);
  }

  /**
   * Caricatore dinamico di classe.
   * @param className nome completo o parziale della classe da caricare
   * @param basePath array di percorsi da anteporre al nome classe per la ricerca
   * @return classe o null se non trovata
   */
  public static Class loadClass(String className, String[] basePath)
  {
    Class rv = null;

    if((rv = loadClass(className)) != null)
      return rv;

    if(basePath != null)
    {
      for(int j = 0; j < basePath.length; j++)
      {
        if((rv = loadClass(basePath[j], className)) != null)
          return rv;
      }
    }

    return null;
  }

  /**
   * Caricatore dinamico di classe.
   * @param className nome completo o parziale della classe da caricare
   * @param prefBasePath percorso preferenziale per la ricerca della classe (puo essere null)
   * @param basePath array di percorsi da anteporre al nome classe per la ricerca (puo essere null)
   * @return classe o null se non trovata
   */
  public static Class loadClass(String className, String prefBasePath, String[] basePath)
  {
    Class rv = null;

    if((rv = loadClass(className)) != null)
      return rv;

    if(prefBasePath != null)
    {
      if((rv = loadClass(prefBasePath, className)) != null)
        return rv;
    }

    if(basePath != null)
    {
      for(int j = 0; j < basePath.length; j++)
      {
        if((rv = loadClass(basePath[j], className)) != null)
          return rv;
      }
    }

    return null;
  }

  /**
   * Ritorna il nome del package della classe indicata.
   * Per java.lang.String ritorna 'java.lang'.
   * @param c classe di cui si vuole il package
   * @return stringa nome del package che contiene la classe
   */
  public static String getClassPackage(Class c)
  {
    String rv = c.getName();
    int ldotidx = rv.lastIndexOf('.');
    return ldotidx == -1 ? rv : rv.substring(0, ldotidx);
  }

  /**
   * Ritorna il nome della classe (ultima parte).
   * Per java.lang.String ritorna 'String'.
   * @param c classe di cui si vuole il nome
   * @return stringa nome della classe
   */
  public static String getClassName(Class c)
  {
    String rv = c.getName();
    int ldotidx = rv.lastIndexOf('.');
    return ldotidx == -1 ? rv : rv.substring(ldotidx + 1);
  }

  /**
   * Parameters of the method to add an URL to the System classes.
   */
  private static final Class<?>[] parameters = new Class[]
  {
    URL.class
  };

  /**
   * Adds a file to the classpath.
   * @param s a String pointing to the file
   * @throws IOException
   */
  public static void addFileToLoader(String s)
     throws IOException
  {
    File f = new File(s);
    addFileToLoader(f);
  }

  /**
   * Adds a file to the classpath
   * @param f the file to be added
   * @throws IOException
   */
  public static void addFileToLoader(File f)
     throws IOException
  {
    addURLToLoader(f.toURI().toURL());
  }

  /**
   * Adds the content pointed by the URL to the classpath.
   * @param u the URL pointing to the content to be added
   * @throws IOException
   */
  public static void addURLToLoader(URL u)
     throws IOException
  {
    URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    Class<?> sysclass = URLClassLoader.class;
    try
    {
      Method method = sysclass.getDeclaredMethod("addURL", parameters);
      method.setAccessible(true);
      method.invoke(sysloader, u);
    }
    catch(Throwable t)
    {
      throw new IOException("Error, could not add URL to system classloader.", t);
    }
  }

  /**
   * Imposta path per caricamento liberie native.
   * La vecchia definizione di java.library.path viene distrutta
   * e sostituita con quella indicata.
   *
   * @param path nuova path per ricerca librerie native
   * @throws Exception
   */
  public static void setLibraryPath(String path)
     throws Exception
  {
    System.setProperty("java.library.path", path);

    // set sys_paths to null so that java.library.path will be reevalueted next time it is needed
    final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
    sysPathsField.setAccessible(true);
    sysPathsField.set(null, null);
  }

  /**
   * Adds the specified path to the java library path.
   * A differenza di setLibraryPath() che sostituisce il valore precedente
   * qui la nuova path viene aggiunta al contenuto attuale di java.library.path.
   *
   * @param pathToAdd the path to add
   * @throws Exception
   */
  public static void addLibraryPath(String pathToAdd)
     throws Exception
  {
    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
    usrPathsField.setAccessible(true);

    // get array of paths
    final String[] paths = (String[]) usrPathsField.get(null);

    // check if the path to add is already present
    for(String path : paths)
    {
      if(path.equals(pathToAdd))
      {
        return;
      }
    }

    // aggiunge la nuova path in testa (sarà valutata per prima)
    String[] newPaths = (String[]) ArrayUtils.add(paths, 0, pathToAdd);
    usrPathsField.set(null, newPaths);
  }

  /**
   * Caricatore di risorsa.
   * Cerca la risorsa (file) nello stesso package della classe
   * data come campione. Usa il caricatore della classe per accedere alla risorsa.
   * @param clz classe di riferimento
   * @param name nome della risorsa (file)
   * @return url per l'accesso alla risorsa
   */
  public static URL getResourceFromClass(Class clz, String name)
  {
    String classPac = getClassPackage(clz).replace('.', '/');
    return clz.getResource(classPac + "/" + name);
  }

  /**
   * Caricatore di risorsa.
   * Cerca la risorsa (file) nello stesso package della classe
   * data come campione. Usa il caricatore della classe per accedere alla risorsa.
   * @param clz classe di riferimento
   * @param name nome della risorsa (file)
   * @return stream per l'accesso alla risorsa
   */
  public static InputStream getResourceFromClassAsStream(Class clz, String name)
  {
    String classPac = getClassPackage(clz).replace('.', '/');
    return clz.getResourceAsStream("/" + classPac + "/" + name);
  }

  /**
   * Caricatore di risorsa.
   * Cerca la risorsa (file) nello stesso package della classe
   * data come campione. Usa il caricatore della classe per accedere alla risorsa.
   * @param clz classe di riferimento
   * @param name nome della risorsa (file)
   * @return contenuto binario della risorsa
   * @throws Exception
   */
  public static ByteBufferOutputStream getBinaryResourceFromClass(Class clz, String name)
     throws Exception
  {
    ByteBufferOutputStream bb = new ByteBufferOutputStream(1024);
    try(InputStream is = getResourceFromClassAsStream(clz, name))
    {
      CommonFileUtils.copyStream(is, bb);
    }

    return bb;
  }

  /**
   * Caricatore di risorsa.
   * Cerca la risorsa (file) nello stesso package della classe
   * data come campione. Usa il caricatore della classe per accedere alla risorsa.
   * @param clz classe di riferimento
   * @param name nome della risorsa (file)
   * @param encoding encoding da applicare per la conversione in testo
   * @return contenuto della risorsa sotto forma di testo
   * @throws Exception
   */
  public static String getTextResourceFromClass(Class clz, String name, String encoding)
     throws Exception
  {
    ByteBufferOutputStream bb = getBinaryResourceFromClass(clz, name);

    if(encoding == null)
      return bb.toString();

    return bb.toString(encoding);
  }

  /**
   * Ritorna la data dichirata nel manifest di un jar.
   * Ovvero la data di creazione del jar.
   * @param path path al jar
   * @return data oppure null
   * @throws IOException
   */
  public static Date getDateOfJar(String path)
     throws IOException
  {
    try(JarFile jarFile = new JarFile(path))
    {
      Enumeration ent = jarFile.entries();
      while(ent.hasMoreElements())
      {
        JarEntry entry = (JarEntry) ent.nextElement();

        String name = entry.getName();
        if(name.equals("META-INF/MANIFEST.MF"))
        {
          return new Date(entry.getTime());
        }
      }
    }

    return null;
  }

  /**
   * Determina la data e ora di compilazione di una classe.
   * @param currentClass classe di cui si cerca la data ora
   * @return la data e ora oppure null
   */
  public static Date getClassBuildTime(Class<?> currentClass)
  {
    URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");

    if(resource != null)
    {
      switch(resource.getProtocol())
      {
        case "file":
        {
          try
          {
            return new Date(new File(resource.toURI()).lastModified());
          }
          catch(URISyntaxException ignored)
          {
          }
          break;
        }

        case "jar":
        {
          String path = resource.getPath();
          return new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
        }

        case "zip":
        {
          String path = resource.getPath();
          File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
          //long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
          //Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
          try(JarFile jf = new JarFile(jarFileOnDisk))
          {
            ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));//Skip the ! and the /
            long zeTimeLong = ze.getTime();
            return new Date(zeTimeLong);
          }
          catch(IOException | RuntimeException ignored)
          {
          }
          break;
        }
      }
    }

    return null;
  }

  public static File getFileFromURL(String resourcePath)
  {
    return getFileFromURL(ClassOper.class.getClassLoader(), resourcePath);
  }

  public static File getFileFromURL(ClassLoader loader, String resourcePath)
  {
    URL url = loader.getResource(resourcePath);
    try
    {
      return new File(url.toURI());
    }
    catch(URISyntaxException e)
    {
      return new File(url.getPath());
    }
    catch(Throwable t)
    {
      return null;
    }
  }

  /**
   * Caricatore dinamico di oggetti.
   * Usa loadClass per caricare la classe dell'oggetto.
   * L'oggetto da caricare deve avere un costruttore vuoto da poter utilizzare.
   * @param className nome completo o parziale della classe da caricare
   * @param prefBasePath percorso preferenziale per la ricerca della classe (puo essere null)
   * @param basePath array di percorsi da anteporre al nome classe per la ricerca (puo essere null)
   * @return oggetto o null se non trovata
   * @see loadClass
   */
  public static Object createObject(String className, String prefBasePath, String[] basePath)
  {
    Class objClass = loadClass(className, prefBasePath, basePath);

    if(objClass == null)
      return null;

    try
    {
      Constructor c = objClass.getConstructor();
      return c.newInstance();
    }
    catch(Exception ex)
    {
      log.error("Failed to create object " + objClass, ex);
      return null;
    }
  }
}
