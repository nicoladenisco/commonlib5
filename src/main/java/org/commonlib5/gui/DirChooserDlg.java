/*
 * DirChooserDlg.java
 *
 * Created on 16-feb-2010, 13.17.14
 *
 * Copyright (C) 2011 Nicola De Nisco
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
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.Position;
import javax.swing.tree.*;

/**
 * Dialog per il directory chooser.
 * @author Nicola De Nisco
 */
public class DirChooserDlg extends javax.swing.JDialog
{
  /** Creates new form DirChooserDlg
   * @param parent */
  public DirChooserDlg(java.awt.Frame parent)
  {
    super(parent, true);
    initComponents();
    initTree(null);
  }

  /** Creates new form DirChooserDlg
   * @param root */
  public DirChooserDlg(java.awt.Frame parent, File root)
  {
    super(parent, true);
    initComponents();
    initTree(root);
  }

  /**
   * Enable the 'Create directory' button in dialog.
   * @param enable true to make the button visible
   */
  public void enableNewDir(boolean enable)
  {
    jbNewDir.setVisible(enable);
    jbNewDir.setEnabled(enable);
  }

  /**
   * Get state of 'Create directory' button option.
   * @return true if the button will be displayed
   */
  public boolean isEnabledNewDir()
  {
    return jbNewDir.isEnabled();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    panTree = new javax.swing.JPanel();
    spTree = new javax.swing.JScrollPane();
    panButton = new javax.swing.JPanel();
    jbNewDir = new javax.swing.JButton();
    jbAnnulla = new javax.swing.JButton();
    jbOk = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    panTree.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 337;
    gridBagConstraints.ipady = 241;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
    panTree.add(spTree, gridBagConstraints);

    getContentPane().add(panTree, java.awt.BorderLayout.CENTER);

    panButton.setPreferredSize(new java.awt.Dimension(400, 45));

    jbNewDir.setText("Nuova directory");
    jbNewDir.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbNewDirActionPerformed(evt);
      }
    });
    panButton.add(jbNewDir);

    jbAnnulla.setText("Annulla");
    jbAnnulla.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbAnnullaActionPerformed(evt);
      }
    });
    panButton.add(jbAnnulla);

    jbOk.setText("Ok");
    jbOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbOkActionPerformed(evt);
      }
    });
    panButton.add(jbOk);

    getContentPane().add(panButton, java.awt.BorderLayout.SOUTH);

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void jbAnnullaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbAnnullaActionPerformed
    {//GEN-HEADEREND:event_jbAnnullaActionPerformed
    // annulla
    returnValue = CANCEL_OPTION;
    dispose();
    }//GEN-LAST:event_jbAnnullaActionPerformed

    private void jbOkActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbOkActionPerformed
    {//GEN-HEADEREND:event_jbOkActionPerformed
    // conferma
    returnValue = OK_OPTION;
    dispose();
    }//GEN-LAST:event_jbOkActionPerformed

    private void jbNewDirActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbNewDirActionPerformed
    {//GEN-HEADEREND:event_jbNewDirActionPerformed
    // crea nuova directory nella directory corrente
    TreePath path = tree.getSelectionPath();

    if(path == null)
    {
      JOptionPane.showMessageDialog(this, "Selezionare una directory padre.");
      return;
    }

    // Get the last element of the path which we know to be an instance of Directory
    Directory dir = (Directory) path.getLastPathComponent();

    // return the File object which specifies this Directory
    File current = dir.getDirectory();

    String val = JOptionPane.showInputDialog(this, "Specificare il nome della directory da creare:");
    if(val == null)
      return;

    File newDir = new File(current, val);
    newDir.mkdirs();

    // ricostruisce il modello e l'albero per aggiornare la visualizzazione
    spTree.getViewport().removeAll();
    dirModel = new DirectoryTreeModel();
    initTree(root.getDirectory());

    // rende la nuova directory quella corrente
    setSelectedDirectory(newDir);
    tree.repaint();

    }//GEN-LAST:event_jbNewDirActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jbAnnulla;
  private javax.swing.JButton jbNewDir;
  private javax.swing.JButton jbOk;
  private javax.swing.JPanel panButton;
  private javax.swing.JPanel panTree;
  private javax.swing.JScrollPane spTree;
  // End of variables declaration//GEN-END:variables

  /*
   * Gestione della visualizzazione directory.
   * Questa parte è stata prelevata da JDirecoryChooser.
   */
  //This is the JTree instance which we will use to display the directory
  //structure within
  private JTree tree;
  //The TreeModel implementation which understands the structure
  //of the computers file system (We don't currently use this
  //other than when we create the tree so in theory we don't
  //need to store a reference to it).
  private DirectoryTreeModel dirModel = new DirectoryTreeModel();
  //A FileSystemView instance which we use to get the roots of
  //the file system among other things.
  private FileSystemView fsv = FileSystemView.getFileSystemView();
  //This variable holds the return value from the dialog
  //which we initially want to be set to cancel so that
  //users don't try to get a valid directory from us,
  //before they have chosen one.
  private int returnValue = CANCEL_OPTION;
  //A variable to hold the title of the dialog, this can
  //be changed by the user
  private String dialogTitle = "Select a directory";
  //A variable to hold the root of the tree (this isn't the root of
  //the file system, but a dummy invisible node in the JTree).
  private Directory root;
  //An instance of JFileChooser which we use to get the correct icon to display
  //for each folder (this means we can deal with special folders in a consistent
  //way).
  private JFileChooser chooser = new JFileChooser();
  //An instance of file view that a user can add to provide custom icons for
  //directories and to override the default isTraversable method
  private FileView fv = null;
  // *******************************
  // ***** Dialog Return Types *****
  // *******************************
  /**
   * Return value from showDialog method if OK is chosen.
   * */
  public static final int OK_OPTION = JOptionPane.OK_OPTION;
  /**
   * Return value from showDialog method if CANCEL is chosen.
   * */
  public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

  // ********************************************
  // ***** JDirectoryChooser Dialog Methods *****
  // ********************************************
  /**
   * Sets the string that goes in the <code>JDirectoryChooser</code> windows's title bar.
   * @param dialogTitle the new <code>String</code> for the title bar
   * */
  public void setDialogTitle(String dialogTitle)
  {
    this.dialogTitle = dialogTitle;
  }

  /**
   * Gets the string that goes in the <code>JDirectoryChooser</code>'s title bar.
   *
   * @return */
  public String getDialogTitle()
  {
    return dialogTitle;
  }

  /**
   * Pops up a "Select Directory" directory chooser dialog. Note that the text the appears in the
   * approve button is determined by the Look and Feel.
   *
   * <p>
   *
   * The <code>parent</code> argument determines two things:
   * the frame on which the dialog depends and
   * the component whose position the look and feel
   * should consider when placing the dialog. If the parent
   * is a <code>Frame</code> object (such as a <code>JFrame</code>)
   * then the dialog depends on the frame and
   * the look and feel positions the dialog
   * relative to the frame (for example, centred over the frame).
   * If the parent is a component, then the dialog
   * depends on the frame containing the component,
   * and is positioned relative to the component
   * (for example, centred over the component).
   * If the parent is <code>null</code>, then the dialog depends on
   * no visible window, and it's placed in a look-and-feel-dependent position
   * such as the centre of the screen.
   *
   * @return the return state of the directory chooser on popdown:
   * <ul>
   * <li>JDirectoryChooser.CANCEL_OPTION
   * <li>JDirectoryChooser.OK_OPTION
   * </ul>
   * */
  public int showDialog()
  {
    //Set the dialog title
    setTitle(dialogTitle);

    //Arrange the components properly
    pack();

    //Re-size the dialog to a reasonable size
    setSize(600, 500);

    //Make sure the dialog will be modal when it's displayed
    setModal(true);

    //Set the location of the dialog to be relative to it's parent
    setLocationRelativeTo(getParent());

    //Re-set the return value to cancel
    returnValue = CANCEL_OPTION;

    //Show the dialog.  As the dialog is modal this will block until the
    //dialog is closed
    setVisible(true);

    //return the value associated with the users action
    return returnValue;
  }

  // ***************************
  // ***** File Operations *****
  // ***************************
  /**
   * Returns the selected directory. This can be <code>null</code> if the user has not
   * selected a directory.
   * @return a <code>File</code> object specifying the selected directory
   * */
  public File getSelectedDirectory()
  {
    //Get the path through the JTree which is currently
    //selected
    TreePath path = tree.getSelectionPath();

    if(path == null)
    {
      //If the path is null then nothing is selected so
      //simply return null
      return null;
    }

    //Get the last element of the path which we know to be an instance
    //of Directory
    Directory dir = (Directory) path.getLastPathComponent();

    //return the File object which specifies this Directory
    return dir.getDirectory();
  }

  /**
   * Sets the currently selected directory.
   * @param dir the File instance representing the directory that should
   * be made visible and selected.
   * */
  public void setSelectedDirectory(File dir)
  {
    //Start with the directory the user specified
    File p = dir;

    //Create a list to hold the bits of the path
    List path = new ArrayList();

    while(p != null)
    {
      //while the current directory is not null (i.e. we haven't
      //yet reached a filesystem root)...

      //add the current directory to the beginning of the list
      path.add(0, new Directory(p, false));

      //get the parent directory
      p = fsv.getParentDirectory(p);
    }

    //Assume that we are not able to select the required
    //directory
    TreePath tp = null;

    //we will start at the beginning of the tree
    int current = 0;

    for(int i = 0; i < path.size(); ++i)
    {
      //loop through each directory we found in the previous loop...

      //get the current directory
      Directory d = (Directory) path.get(i);

      //find the occurance of this directory down the
      //tree from the previous directory
      tp = tree.getNextMatch(fsv.getSystemDisplayName(d.getDirectory()), current, Position.Bias.Forward);

      if(tp != null)
      {
        //if we found the directory then

        //expand this directory
        tree.expandPath(tp);

        //update the current index to make sure we don't match
        //something we shouldn't
        current = tree.getRowForPath(tp) + 1;
      }
    }

    if(tp != null)
    {
      //if we managed to expand at least part of the path then...

      //make sure it is within the visible area and select the end node
      tree.scrollPathToVisible(tp);
      tree.setSelectionPath(tp);
    }
  }

  /**
   * Sets the file view used to retrieve UI information, such as the icon
   * that represents a directory.
   *
   * @param fv */
  public void setFileView(FileView fv)
  {
    this.fv = fv;
    chooser.setFileView(fv);
  }

  /**
   * Returns the current file view.
   *
   * @return */
  public FileView getFileView()
  {
    return chooser.getFileView();
  }

//  // *********************************
//  // ***** Look-and-Feel Methods *****
//  // *********************************
//  /**
//   * Resets the UI property to a value from the current look and feel.
//   **/
//  @Override
//  public void updateUI()
//  {
//    //Let the super class do most of the work for us
//    super.updateUI();
//
//    //Re-set the cell renderer for the tree.  This makes sure
//    //that all L&F related visual stuff for the tree now matches
//    //the new L&F
//    tree.setCellRenderer(new DirectoryTreeCellRenderer());
//  }
  // ***********************************
  // ***** Private Methods/Classes *****
  // ***********************************
  private void initTree(File rootDir)
  {
    //Create a new Root dir for the tree model to use
    root = new Directory(rootDir, true);

    //Lets create a new JTree to display the directory structure encapsulated
    //by the DirectoryTreeModel instance dirModel;
    tree = new JTree(dirModel);

    //We don't want to iterate through the entire directory structure to create
    //a static model for the entire tree.  So what we do is when a node is just
    //about to be expanded we generate it's children, if they haven't already
    //been generated.
    tree.addTreeWillExpandListener(new TreeWillExpandListener()
    {
      @Override
      public void treeWillCollapse(TreeExpansionEvent e)
      {
      }

      @Override
      public void treeWillExpand(TreeExpansionEvent e)
      {
        TreePath path = e.getPath();
        Directory dir = (Directory) path.getLastPathComponent();

        if(!dir.isExpanded())
          dir.setExpanded(true);

      }
    });

    //The root node isn't real, as we may have more than one root into the file system
    //i.e. under DOS each drive is a separate file system root.  So we turn off display of
    //the root element in the tree
    tree.setRootVisible(false);

    if(root.getChildCount() == 1)
    {
      //If the tree root only has one child, i.e. there is only one file system
      //root (like the desktop under Windows) then expand the file system root
      ((Directory) root.getChild(0)).setExpanded(true);
      tree.expandRow(0);
    }

    //Add our tree cell render to the tree so that all the folders, drives and special folders
    //get given the correct icons and labels
    tree.setCellRenderer(new DirectoryTreeCellRenderer());

    //Add a small gap from the edge of the tree to the icons, just so it looks better
    tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    //We only want to allow one directory to be selected at once so..
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    spTree.setViewportView(tree);
  }

  class DirectoryTreeCellRenderer extends DefaultTreeCellRenderer
  {
    public DirectoryTreeCellRenderer()
    {
      super();
      chooser = new JFileChooser();

      if(fv != null)
        chooser.setFileView(fv);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      //Call the standard method to do most of the work for us
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if(value == null)
      {
        //we have been passed a null value for some reason so just
        //set the text to display to the empty string and return
        setText("");
        return this;
      }

      //We know that the value (i.e. a node) should be an instance of Directory
      //so lets cast it so we can call the methods we need
      Directory dir = (Directory) value;

      //Lets get the File object which represents this directory
      File directory = dir.getDirectory();

      if(directory == null)
      {
        //the File object was null, probably meaning that this object
        //is the invisible root of the tree, so set the text to the
        //empty string and return
        setText("");
        return this;
      }

      //Set the name of this directory to an empty string
      String name = "";

      //Use a FileSystemView object to get the display name of
      //the directory.  This is used mostly under Windows to get names
      //such as 'My Computer'.
      name = fsv.getSystemDisplayName(directory);

      if(name.equals(""))
      {
        //If the name is still blank then set it equal to the path of
        //the File object (this happens with drives).
        name = directory.getPath();
      }

      //Now we have some text to display set it
      setText(name);

      //Get the correct icon for the folder and L&F and set that
      setIcon(chooser.getIcon(directory));

      //return so that the folder is correctly rendered.
      return this;
    }
  }

  class DirectoryTreeModel implements TreeModel
  {
    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
      //We don't currently use this method but it has to be here
      //so that we correctly implement the TreeModel interface
    }

    @Override
    public Object getChild(Object parent, int index)
    {
      //We know that any node in the tree is an instance
      //of Directory so cast parent to Directory
      Directory dir = (Directory) parent;

      //Now get the requested child from the Directory instance
      return dir.getChild(index);
    }

    @Override
    public int getChildCount(Object parent)
    {
      //We know that any node in the tree is an instance
      //of Directory so cast parent to Directory
      Directory dir = (Directory) parent;

      if(!dir.isExpanded())
      {
        //If the directory hasn't been expanded yet then
        //we don't really know how many children it has but
        //for safety we'll assume it's zer0.
        return 0;
      }

      //If we are still here then use the Directory instance to get
      //the number of children this node has
      return dir.getChildCount();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
      Directory dir = (Directory) parent;

      if(!dir.isExpanded())
      {
        //If the directory hasn't been expanded yet then
        //we don't really know what the index of any child will
        //be so return -1
        return -1;
      }

      //If we are still here then use the Directory instance to get
      //the number of children this node has
      return dir.getChildIndex((Directory) child);
    }

    @Override
    public Object getRoot()
    {
      //We stored the root as a private class variable when we
      //initialised the tree, so just return that variable.
      return root;
    }

    @Override
    public boolean isLeaf(Object node)
    {
      //We know that any node in the tree is an instance
      //of Directory so cast parent to Directory
      Directory dir = (Directory) node;

      //Now use the directory instance to find out if this node
      //is a leaf node or not
      return dir.isLeaf();
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
      //We don't currently use this method but it has to be here
      //so that we correctly implement the TreeModel interface
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
      //We don't currently use this method but it has to be here
      //so that we correctly implement the TreeModel interface
    }
  }

  class Directory
  {
    //This variable will hold the File object which
    //specifies this directory
    private File dir;
    //An array of directory instances which are children
    //of this Directory in the computers file system
    //hierarchy
    private Directory[] children = null;
    //Two flags which are used for keeping track of things
    //through this class
    private boolean root = false;
    private boolean expanded = false;
    //An instance of FileFilter which only
    //accepts directories
    private FileFilter filter = new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return fsv.isTraversable(pathname);
      }
    };

    public Directory(File dir, boolean root)
    {
      //Store whether or not this directory instance represents
      //the root of the JTree or not
      this.root = root;

      if(!root)
      {
        //If this isn't a root node then we must store the
        //File object which we have been passed.
        this.dir = dir;
      }
      else
      {
        //This is a root node so lets immediately generate the children
        //as these are what the user will initially see

        //set the expanded flag to true so we only have to do this once
        expanded = true;

        if(dir == null)
        {
          //If the File instance we were passed was null then we
          //will root the tree at the root(s) of the file system
          children = filterOutFiles(fsv.getRoots(), null);
        }
        else
        {
          //The File instance we were passed was not null so we
          //will root the tree at this directory.
          children = filterOutFiles(new File[]
          {
            dir
          }, null);
        }
      }
    }

    public boolean isRoot()
    {
      //Simply return the boolean flag
      return root;
    }

    private Directory[] filterOutFiles(File[] files, FileFilter filter)
    {
      if(files == null)
        return null;

      Directory[] temp = new Directory[files.length];
      int counter = 0;

      for(int i = 0; i < files.length; ++i)
      {

        if(filter == null)
        {
          temp[counter] = new Directory(files[i], false);
          ++counter;
        }
        else if(filter.accept(files[i]))
        {
          temp[counter] = new Directory(files[i], false);
          ++counter;
        }
      }

      Directory[] result = new Directory[counter];

      System.arraycopy(temp, 0, result, 0, counter);
      Arrays.sort(result, new Comparator<Directory>()
      {

        @Override
        public int compare(Directory o1, Directory o2)
        {
          return o1.dir.getName().compareTo(o2.dir.getName());
        }
      });

      return result;
    }

    protected void setExpanded(boolean value)
    {
      expanded = value;

      if(value && chooser.isTraversable(dir))
      {
        File actual = dir;

        if(fsv.isComputerNode(dir))
          children = filterOutFiles(File.listRoots(), null);
        else
          children = filterOutFiles(fsv.getFiles(actual, true), filter);
      }
      else
        children = null;
    }

    public int getChildCount()
    {

      if(!expanded)
        return 0;

      if(children == null)
        return 0;

      return children.length;
    }

    public int getChildIndex(Directory child)
    {
      int val = -1;

      if(expanded)
      {
        for(int i = 0; i < children.length; ++i)
        {
          if(children[i].equals(child))
          {
            val = i;
            i = children.length;
          }
        }
      }

      return val;
    }

    public Object getChild(int index)
    {
      return children[index];
    }

    protected boolean isExpanded()
    {
      return expanded;
    }

    protected boolean isLeaf()
    {
      if(!expanded)
        return false;

      if(children == null || children.length == 0)
        return true;
      else
        return false;
    }

    protected File getDirectory()
    {
      return dir;
    }

    @Override
    public boolean equals(Object o)
    {
      if(dir == null && o == null)
        return true;

      if(dir == null || o == null)
        return false;

      Directory d = (Directory) o;
      return dir.equals(d.getDirectory());
    }

    @Override
    public String toString()
    {
      if(dir == null)
        return "";
      else
        return fsv.getSystemDisplayName(dir);
    }
  }
}
