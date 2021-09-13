/*
 * Copyright (C) 2013 Nicola De Nisco
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
package org.commonlib5.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.commonlib5.utils.OsIdent;
import org.commonlib5.utils.WindowUtils;

/**
 * Uso semplificato di Dialog per la selezione di file e directory.
 * In caso di MacOSX usa le sue dialog native.
 *
 * @author Nicola De Nisco
 */
public class FileDialogHelper
{
  public static class FilterWrapper implements FilenameFilter
  {
    protected FileFilter[] flt;

    public FilterWrapper(FileFilter[] flt)
    {
      this.flt = flt;
    }

    @Override
    public boolean accept(File file, String string)
    {
      File target = new File(file, string);

      for(int i = 0; i < flt.length; i++)
      {
        if(flt[i].accept(target))
          return true;
      }

      return false;
    }
  }

  /**
   * Seleziona una directory.
   * @param c parent
   * @param title titolo da visualizzare nella dialog
   * @param lastDir directory iniziale
   * @param approveButtonText testo da inserire nel pulsante di accettazione
   * @return directory scelta o null per abbandono
   * @throws Exception
   */
  public static File chooseDir(Container c, String title, File lastDir, String approveButtonText)
     throws Exception
  {
    if(lastDir == null)
      lastDir = new File(".");

    if(OsIdent.checkOStype() == OsIdent.OS_MACOSX)
    {
      FileDialog chooser = new FileDialog(WindowUtils.findParentFrame(c), title);
      System.setProperty("apple.awt.fileDialogForDirectories", "true");
      chooser.setDirectory(lastDir.getAbsolutePath());
      chooser.setMode(FileDialog.LOAD);
      chooser.setVisible(true);

      System.setProperty("apple.awt.fileDialogForDirectories", "false");
      if(chooser.getFile() != null)
      {
        String folderName = chooser.getDirectory();
        folderName += chooser.getFile();
        return new File(folderName);
      }
    }
    else
    {
      JFileChooser chooser = new JFileChooser(lastDir)
      {
        @Override
        protected JDialog createDialog(Component parent)
           throws HeadlessException
        {
          JDialog dlg = super.createDialog(parent);
          WindowUtils.fitAndCenterInScreen(dlg, 0.5f, 0.5f);
          return dlg;
        }
      };
      chooser.setDialogTitle(title);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = chooser.showDialog(c, approveButtonText);
      if(returnVal == JFileChooser.APPROVE_OPTION)
        return chooser.getSelectedFile();
    }

    return null;
  }

  /**
   * Seleziona un gruppo di files.
   * @param c
   * @param title
   * @param lastDir
   * @param filters
   * @param acceptAll
   * @return
   */
  public static File[] chooseFiles(Container c, String title, File lastDir, boolean acceptAll, FileFilter... filters)
  {
    if(lastDir == null)
      lastDir = new File(".");

    JFileChooser fc = new JFileChooser(lastDir)
    {
      @Override
      protected JDialog createDialog(Component parent)
         throws HeadlessException
      {
        JDialog dlg = super.createDialog(parent);
        WindowUtils.fitAndCenterInScreen(dlg, 0.5f, 0.5f);
        return dlg;
      }
    };

    fc.setDialogTitle(title);
    fc.setMultiSelectionEnabled(true);

    if(filters != null && filters.length > 0)
    {
      fc.setAcceptAllFileFilterUsed(acceptAll);
      for(int i = 0; i < filters.length; i++)
        fc.addChoosableFileFilter(filters[i]);
      fc.setFileFilter(filters[0]);
    }

    if(fc.showOpenDialog(c) == JFileChooser.APPROVE_OPTION)
      return fc.getSelectedFiles();

    return null;
  }

  /**
   * Seleziona un gruppo di files con preview delle immagini.
   * Vedi ImagePreviewPanel.
   * @param c
   * @param title
   * @param lastDir
   * @param filters
   * @param acceptAll
   * @return
   */
  public static File[] chooseFilesImagePreview(Container c, String title, File lastDir, boolean acceptAll, FileFilter... filters)
  {
    if(lastDir == null)
      lastDir = new File(".");

    JFileChooser fc = new JFileChooser(lastDir)
    {
      @Override
      protected JDialog createDialog(Component parent)
         throws HeadlessException
      {
        JDialog dlg = super.createDialog(parent);
        WindowUtils.fitAndCenterInScreen(dlg, 0.5f, 0.5f);
        return dlg;
      }
    };

    fc.setDialogTitle(title);
    fc.setMultiSelectionEnabled(true);

    if(filters != null && filters.length > 0)
    {
      fc.setAcceptAllFileFilterUsed(acceptAll);
      for(int i = 0; i < filters.length; i++)
        fc.addChoosableFileFilter(filters[i]);
      fc.setFileFilter(filters[0]);
    }

    ImagePreviewPanel preview = new ImagePreviewPanel();
    fc.setAccessory(preview);
    fc.addPropertyChangeListener(preview);

    if(fc.showOpenDialog(c) == JFileChooser.APPROVE_OPTION)
      return fc.getSelectedFiles();

    return null;
  }

  /**
   * Seleziona un file per lettura.
   * @param c contenitore padre
   * @param title titolo della dialog
   * @param lastDir directory iniziale da visualizzare
   * @param filters filtri dei files da visualizzare
   * @param acceptAll se vero aggiunge un filtro visualizza tutti
   * @return il file scelto o null
   * @throws Exception
   */
  public static File chooseFile(Container c, String title, File lastDir, boolean acceptAll, FileFilter... filters)
     throws Exception
  {
    if(lastDir == null)
      lastDir = new File(".");

    if(OsIdent.checkOStype() == OsIdent.OS_MACOSX)
    {
      FileDialog chooser = new FileDialog(WindowUtils.findParentFrame(c), title);
      System.setProperty("apple.awt.fileDialogForDirectories", "false");
      chooser.setDirectory(lastDir.getAbsolutePath());
      chooser.setMode(FileDialog.LOAD);

      if(acceptAll)
        chooser.setFilenameFilter(new FilenameFilter()
        {
          @Override
          public boolean accept(File dir, String name)
          {
            return true;
          }
        });

      if(filters != null && filters.length > 0)
        chooser.setFilenameFilter(new FilterWrapper(filters));

      chooser.setVisible(true);

      if(chooser.getFile() != null)
      {
        String folderName = chooser.getDirectory();
        folderName += chooser.getFile();
        return new File(folderName);
      }
    }
    else
    {
      JFileChooser fc = new JFileChooser(lastDir)
      {
        @Override
        protected JDialog createDialog(Component parent)
           throws HeadlessException
        {
          JDialog dlg = super.createDialog(parent);
          WindowUtils.fitAndCenterInScreen(dlg, 0.5f, 0.5f);
          return dlg;
        }
      };

      fc.setDialogTitle(title);
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

      if(filters != null && filters.length > 0)
      {
        fc.setAcceptAllFileFilterUsed(acceptAll);
        for(int i = 0; i < filters.length; i++)
          fc.addChoosableFileFilter(filters[i]);
        fc.setFileFilter(filters[0]);
      }

      if(fc.showOpenDialog(c) == JFileChooser.APPROVE_OPTION)
        return fc.getSelectedFile();
    }

    return null;
  }

  /**
   * Seleziona un file o una directory.
   * @param c contenitore padre
   * @param title titolo della dialog
   * @param lastDir directory iniziale da visualizzare
   * @param filters filtri dei files da visualizzare
   * @param acceptAll se vero aggiunge un filtro visualizza tutti
   * @return il file scelto o null
   */
  public static File chooseFileOrDirectory(Container c, String title, File lastDir, boolean acceptAll, FileFilter... filters)
  {
    if(lastDir == null)
      lastDir = new File(".");

    if(OsIdent.checkOStype() == OsIdent.OS_MACOSX)
    {
      FileDialog chooser = new FileDialog(WindowUtils.findParentFrame(c), title);
      System.setProperty("apple.awt.fileDialogForDirectories", "true");
      chooser.setDirectory(lastDir.getAbsolutePath());
      chooser.setMode(FileDialog.LOAD);

      if(filters != null)
        chooser.setFilenameFilter(new FilterWrapper(filters));

      chooser.setVisible(true);

      System.setProperty("apple.awt.fileDialogForDirectories", "false");
      if(chooser.getFile() != null)
      {
        String folderName = chooser.getDirectory();
        folderName += chooser.getFile();
        return new File(folderName);
      }
    }
    else
    {
      JFileChooser fc = new JFileChooser(lastDir)
      {
        @Override
        protected JDialog createDialog(Component parent)
           throws HeadlessException
        {
          JDialog dlg = super.createDialog(parent);
          WindowUtils.fitAndCenterInScreen(dlg, 0.5f, 0.5f);
          return dlg;
        }
      };

      fc.setDialogTitle(title);
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      if(filters != null && filters.length > 0)
      {
        fc.setAcceptAllFileFilterUsed(acceptAll);
        for(int i = 0; i < filters.length; i++)
          fc.addChoosableFileFilter(filters[i]);
        fc.setFileFilter(filters[0]);
      }

      if(fc.showOpenDialog(c) == JFileChooser.APPROVE_OPTION)
        return fc.getSelectedFile();
    }

    return null;
  }

  /**
   * Richiesta di salvataggio file con selezione della directory
   * e dal file.
   * @param c contenitore padre
   * @param title titolo della dialog
   * @param lastDir directory iniziale da visualizzare
   * @param defName il nome di default per il file da salvare
   * @param filters filtri dei files da visualizzare
   * @param acceptAll se vero aggiunge un filtro visualizza tutti
   * @return il file scelto o null
   * @throws Exception
   */
  public static File saveFile(Container c, String title, File lastDir, String defName, boolean acceptAll, FileFilter... filters)
     throws Exception
  {
    if(lastDir == null)
      lastDir = new File(".");

    if(OsIdent.checkOStype() == OsIdent.OS_MACOSX)
    {
      FileDialog chooser = new FileDialog(WindowUtils.findParentFrame(c), title);
      System.setProperty("apple.awt.fileDialogForDirectories", "false");
      chooser.setDirectory(lastDir.getAbsolutePath());
      chooser.setMode(FileDialog.SAVE);

      if(defName != null)
        chooser.setFile(defName);

      if(filters != null)
        chooser.setFilenameFilter(new FilterWrapper(filters));

      chooser.setVisible(true);

      if(chooser.getFile() != null)
      {
        String folderName = chooser.getDirectory();
        folderName += chooser.getFile();
        return new File(folderName);
      }
    }
    else
    {
      JFileChooser fc = new JFileChooser(lastDir)
      {
        @Override
        protected JDialog createDialog(Component parent)
           throws HeadlessException
        {
          JDialog dlg = super.createDialog(parent);
          WindowUtils.fitAndCenterInScreen(dlg, 0.5f, 0.5f);
          return dlg;
        }
      };

      fc.setDialogTitle(title);
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

      if(defName != null)
      {
        File deffile = new File(lastDir, defName);
        fc.setSelectedFile(deffile);
      }

      if(filters != null && filters.length > 0)
      {
        fc.setAcceptAllFileFilterUsed(acceptAll);
        for(int i = 0; i < filters.length; i++)
          fc.addChoosableFileFilter(filters[i]);
        fc.setFileFilter(filters[0]);
      }

      if(fc.showSaveDialog(c) == JFileChooser.APPROVE_OPTION)
        return fc.getSelectedFile();
    }

    return null;
  }

  /**
   * Richiesta di un file immagine con anteprima.
   * @param c contenitore padre
   * @param title titolo della dialog
   * @param lastDir directory iniziale da visualizzare
   * @param acceptAll se vero aggiunge un filtro visualizza tutti
   * @return il file scelto o null
   * @throws Exception
   */
  public static File chooseImage(Container c, String title, File lastDir, boolean acceptAll)
     throws Exception
  {
    // jfilechooser con preview dell'immagine
    JFileChooser jfc = new JFileChooser(lastDir);
    jfc.setDialogTitle(title);
    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    ImagePreviewPanel preview = new ImagePreviewPanel();
    jfc.setAccessory(preview);
    jfc.addPropertyChangeListener(preview);

    jfc.setPreferredSize(WindowUtils.getRelativeScreenDimension(0.7f, 0.6f));
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
       "JPG GIF PNG Images", "jpg", "jpeg", "gif", "png");
    jfc.setFileFilter(filter);
    jfc.setAcceptAllFileFilterUsed(acceptAll);

    if(jfc.showOpenDialog(c) == JFileChooser.APPROVE_OPTION)
      return jfc.getSelectedFile();

    return null;
  }
}
