/*
 * Copyright (C) 2012 Nicola De Nisco
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.commonlib5.utils.FileScanner;
import org.commonlib5.utils.OsIdent;

/**
 * Lancio di applicazioni esterne in Java.
 * Usa la stessa JVM in esecuzione per lanciare un programma
 * esterno in Java.
 *
 * @author Nicola De Nisco
 */
public class JavaHelper
{
  /**
   * Return the executable of the current JVM.
   * This can be used to launch othe Java process.
   * @return the file of the java executable
   * @throws Exception
   */
  public static File getRunningJvm()
     throws Exception
  {
    String jh = System.getProperty("java.home");
    if(jh == null)
      throw new Exception("Internal error: unknow java.home property.");

    return OsIdent.checkOStype() == OsIdent.OS_WINDOWS
              ? new File(jh + File.separator + "bin" + File.separator + "javaw")
              : new File(jh + File.separator + "bin" + File.separator + "java");
  }

  /**
   * Find all jars in the directory and its subdir.
   * @param dirJars directory to scan
   * @return an array of absoulute path for the found jars
   * @throws Exception
   */
  public static String parseClassPath(File dirJars)
     throws Exception
  {
    if(dirJars == null || !dirJars.isDirectory())
      return "";

    List<File> lsJars = FileScanner.scan(dirJars, 9999, "*.jar");
    if(lsJars.isEmpty())
      return "";

    StringBuilder rv = new StringBuilder(1024);
    for(int i = 0; i < lsJars.size(); i++)
    {
      File fJar = lsJars.get(i);

      if(i > 0)
        rv.append(File.pathSeparator);

      rv.append(fJar.getAbsolutePath());
    }

    return rv.toString();
  }

  /**
   * Run external Java program.
   * @param classMain the main class of the application (or "-jar mainapp.jar")
   * @param dirJars a directory with the jars need by application (my be null for nothing)
   * @param cmdarray an array with the command line for the application (my be null for nothing)
   * @return the helper with exec results
   * @throws IOException
   */
  public static ExecHelper exec(String classMain, File dirJars, String[] cmdarray)
     throws Exception
  {
    return exec(classMain, dirJars, cmdarray, null, null);
  }

  /**
   * Run external Java program.
   * @param classMain the main class of the application (or "-jar mainapp.jar")
   * @param dirJars a directory with the jars need by application (my be null for nothing)
   * @param cmdarray an array with the command line for the application (my be null for nothing)
   * @param envp envirnment for the application (my be null for nothing)
   * @param charset the charset used for application (my be null for default)
   * @return the helper with exec results
   * @throws IOException
   */
  public static ExecHelper exec(String classMain, File dirJars, String[] cmdarray, String[] envp, String charset)
     throws Exception
  {
    return exec(getRunningJvm(), classMain, parseClassPath(dirJars), cmdarray, envp, charset);
  }

  /**
   * Run external Java program.
   * @param jvmExec the JVM to launch
   * @param classMain the main class of the application (or "-jar mainapp.jar")
   * @param classPath the classpath to use for the application (my be null for nothing)
   * @param cmdarray an array with the command line for the application (my be null for nothing)
   * @param envp envirnment for the application (my be null for nothing)
   * @param charset the charset used for application (my be null for default)
   * @return the helper with exec results
   * @throws IOException
   */
  public static ExecHelper exec(File jvmExec, String classMain, String classPath, String[] cmdarray, String[] envp, String charset)
     throws IOException
  {
    return new ExecHelper(exec(jvmExec, classMain, classPath, cmdarray, envp), charset);
  }

  /**
   * Run external Java program.
   * @param jvmExec the JVM to launch
   * @param classMain the main class of the application (or "-jar mainapp.jar")
   * @param classPath the classpath to use for the application (my be null for nothing)
   * @param cmdarray an array with the command line for the application (my be null for nothing)
   * @param envp envirnment for the application (my be null for nothing)
   * @return the helper with exec results
   * @throws IOException
   */
  public static Process exec(File jvmExec, String classMain, String classPath, String[] cmdarray, String[] envp)
     throws IOException
  {
    ArrayList<String> cmd = new ArrayList<String>();
    cmd.add(jvmExec.getAbsolutePath());
    if(classPath != null)
    {
      cmd.add("-cp");
      cmd.add(classPath);
    }
    cmd.add(classMain);
    if(cmdarray != null && cmdarray.length > 0)
      cmd.addAll(Arrays.asList(cmdarray));

    String[] cmdcommand = new String[cmd.size()];
    cmd.toArray(cmdcommand);

    return Runtime.getRuntime().exec(cmdcommand, envp);
  }
}
