/*
 * FileScanner.java
 *
 * Created on 24 marzo 2006, 16.14
 */
package org.commonlib5.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Supporto per lo scan di una directory alla ricerca
 * di files al suo interno percorrendo ricorsivamente
 * la struttura delle directory.
 * Tutti i files sono accumulati in un vettore.
 * @author Nicola De Nisco
 */
public class FileScanner
{
  protected int maxLivello = 999;
  protected List<File> vFile = new ArrayList<File>();
  protected FileFilter ff = null;
  protected FilenameFilter fn = null;
  protected ActionListener al = null;
  protected boolean onlyOne = false;

  /** Creates a new instance of FileScanner */
  public FileScanner()
  {
  }

  public FileScanner(FileFilter ff)
  {
    this.ff = ff;
  }

  public FileScanner(FilenameFilter fn)
  {
    this.fn = fn;
  }

  public FileScanner(String wildCard)
  {
    this.fn = new WildcardFileFilter(wildCard);
  }

  public int scanDir(File fDir)
  {
    if(ff != null)
      return scanInternalFileFilter(0, fDir);
    else if(fn != null)
      return scanInternalFilenameFilter(0, fDir);
    else
      return scanInternal(0, fDir);
  }

  public ActionListener getAl()
  {
    return al;
  }

  public void setAl(ActionListener al)
  {
    this.al = al;
  }

  public int getMaxLivello()
  {
    return maxLivello;
  }

  public void setMaxLivello(int maxLivello)
  {
    this.maxLivello = maxLivello;
  }

  public boolean isOnlyOne()
  {
    return onlyOne;
  }

  public void setOnlyOne(boolean onlyOne)
  {
    this.onlyOne = onlyOne;
  }

  public List<File> getFiles()
  {
    return vFile;
  }

  protected int scanInternal(int livello, File fDir)
  {
    int count = 0;
    File[] fArr = fDir.listFiles();

    for(int i = 0; i < fArr.length; i++)
    {
      if(fArr[i].isDirectory() && livello < maxLivello)
      {
        count += scanInternal(livello + 1, fArr[i]);

        if(onlyOne && count > 0)
          return count;
      }
      else
      {
        if(al != null)
          al.actionPerformed(new ActionEvent(this, count, fArr[i].getAbsolutePath()));

        vFile.add(fArr[i]);
        count++;

        if(onlyOne)
          return count;
      }
    }

    return count;
  }

  protected int scanInternalFileFilter(int livello, File fDir)
  {
    int count = 0;
    File[] fArr = null;

    if((fArr = fDir.listFiles(ff)) != null)
    {
      for(int i = 0; i < fArr.length; i++)
      {
        if(!fArr[i].isDirectory())
        {
          if(al != null)
            al.actionPerformed(new ActionEvent(this, count, fArr[i].getAbsolutePath()));

          vFile.add(fArr[i]);
          count++;

          if(onlyOne)
            return count;
        }
      }
    }

    if(livello < maxLivello)
    {
      // ripete lo scan per tutte le directory
      if((fArr = fDir.listFiles()) != null)
      {
        for(int i = 0; i < fArr.length; i++)
        {
          if(fArr[i].isDirectory())
          {
            count += scanInternalFileFilter(livello + 1, fArr[i]);

            if(onlyOne && count > 0)
              return count;
          }
        }
      }
    }

    return count;
  }

  protected int scanInternalFilenameFilter(int livello, File fDir)
  {
    int count = 0;
    File[] fArr = null;

    if((fArr = fDir.listFiles(fn)) != null)
    {
      for(int i = 0; i < fArr.length; i++)
      {
        if(!fArr[i].isDirectory())
        {
          if(al != null)
            al.actionPerformed(new ActionEvent(this, count, fArr[i].getAbsolutePath()));

          vFile.add(fArr[i]);
          count++;

          if(onlyOne)
            return count;
        }
      }
    }

    if(livello < maxLivello)
    {
      // ripete lo scan per tutte le directory
      if((fArr = fDir.listFiles()) != null)
      {
        for(int i = 0; i < fArr.length; i++)
        {
          if(fArr[i].isDirectory())
          {
            count += scanInternalFilenameFilter(livello + 1, fArr[i]);

            if(onlyOne && count > 0)
              return count;
          }
        }
      }
    }

    return count;
  }

  public static List<File> scan(File fDir)
  {
    FileScanner fs = new FileScanner();
    fs.scanInternal(0, fDir);
    return fs.vFile;
  }

  public static List<File> scan(File fDir, int maxLev)
  {
    FileScanner fs = new FileScanner();
    fs.maxLivello = maxLev;
    fs.scanInternal(0, fDir);
    return fs.vFile;
  }

  public static List<File> scan(File fDir, int maxLev, FileFilter ff)
  {
    FileScanner fs = new FileScanner();
    fs.maxLivello = maxLev;
    fs.ff = ff;
    fs.scanInternalFileFilter(0, fDir);
    return fs.vFile;
  }

  public static List<File> scan(File fDir, int maxLev, FilenameFilter fn)
  {
    FileScanner fs = new FileScanner();
    fs.maxLivello = maxLev;
    fs.fn = fn;
    fs.scanInternalFilenameFilter(0, fDir);
    return fs.vFile;
  }

  public static List<File> scan(File fDir, int maxLev, String wildCard)
  {
    return scan(fDir, maxLev, wildCard, false);
  }

  public static List<File> scan(File fDir, int maxLev, String wildCard, boolean onlyOne)
  {
    FileScanner fs = new FileScanner();
    fs.maxLivello = maxLev;
    fs.onlyOne = onlyOne;
    fs.fn = new WildcardFileFilter(wildCard);
    fs.scanInternalFilenameFilter(0, fDir);
    return fs.vFile;
  }

  public static boolean fileExist(File fDir, String wildCard)
  {
    return !scan(fDir, 999, wildCard, true).isEmpty();
  }

  public static boolean fileExist(File fDir, int maxLevel, String wildCard)
  {
    return !scan(fDir, maxLevel, wildCard, true).isEmpty();
  }

  public static boolean acceptDicomFile(String name)
  {
    String test = name.toLowerCase();
//    return (test.indexOf(".dic") != -1 || test.indexOf(".dcm") != -1 || test.indexOf('.') == -1);
    return true;
  }

  public static File findFirst(File fDir, int maxLev, String wildCard)
  {
    List<File> res = scan(fDir, maxLev, wildCard, true);
    return res.isEmpty() ? null : res.get(0);
  }
}
