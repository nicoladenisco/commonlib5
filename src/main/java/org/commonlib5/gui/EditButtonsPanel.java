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
package org.commonlib5.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * A generic button panel used in setup forms.
 *
 * @author Nicola De Nisco
 */
public class EditButtonsPanel extends javax.swing.JPanel
{
  public static final String CMD_ADD = "add";
  public static final String CMD_DOWN = "down";
  public static final String CMD_REMOVE = "remove";
  public static final String CMD_UP = "up";
  public static final String CMD_EDIT = "edit";
  public static final String CMD_DUPLICATE = "dup";
  //
  private ArrayList<ActionListener> arListeners = new ArrayList<ActionListener>();
  private Function<String, String> fnI18n;

  /** Creates new form EditButtonsPanel */
  public EditButtonsPanel()
  {
    initComponents();
    jbAdd.setActionCommand(CMD_ADD);
    jbRemove.setActionCommand(CMD_REMOVE);
    jbUp.setActionCommand(CMD_UP);
    jbDown.setActionCommand(CMD_DOWN);
    jbEdit.setActionCommand(CMD_EDIT);
    jbDup.setActionCommand(CMD_DUPLICATE);
  }

  public void addActionListener(ActionListener l)
  {
    arListeners.add(l);
  }

  public void removeActionListener(ActionListener l)
  {
    arListeners.remove(l);
  }

  private void notifyButtons(ActionEvent evt)
  {
    for(ActionListener al : arListeners)
    {
      al.actionPerformed(evt);
    }
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);

    jbAdd.setEnabled(enabled);
    jbDown.setEnabled(enabled);
    jbDup.setEnabled(enabled);
    jbEdit.setEnabled(enabled);
    jbRemove.setEnabled(enabled);
    jbUp.setEnabled(enabled);
  }

  protected String i18n(String msg)
  {
    if(fnI18n != null)
      return fnI18n.apply(msg);

    return msg;
  }

  public Function<String, String> getFnI18n()
  {
    return fnI18n;
  }

  public void setFnI18n(Function<String, String> fnI18n)
  {
    this.fnI18n = fnI18n;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    jbAdd = new javax.swing.JButton();
    jbRemove = new javax.swing.JButton();
    jbDown = new javax.swing.JButton();
    jbUp = new javax.swing.JButton();
    jbEdit = new javax.swing.JButton();
    jbDup = new javax.swing.JButton();

    jbAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tab_new.png"))); // NOI18N
    jbAdd.setToolTipText(i18n("Agginge un nuovo elemento"));
    jbAdd.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jbAddActionPerformed(evt);
      }
    });

    jbRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tab_remove.png"))); // NOI18N
    jbRemove.setToolTipText(i18n("Rimuove la selezione"));
    jbRemove.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jbRemoveActionPerformed(evt);
      }
    });

    jbDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/downarrow.png"))); // NOI18N
    jbDown.setToolTipText(i18n("Sposta la selezione verso il basso"));
    jbDown.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jbDownActionPerformed(evt);
      }
    });

    jbUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uparrow.png"))); // NOI18N
    jbUp.setToolTipText(i18n("Sposta la selezione verso l'alto"));
    jbUp.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jbUpActionPerformed(evt);
      }
    });

    jbEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edit.png"))); // NOI18N
    jbEdit.setToolTipText(i18n("Modifica l'elemento selezionato"));
    jbEdit.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jbEditActionPerformed(evt);
      }
    });

    jbDup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tab_duplicate.png"))); // NOI18N
    jbDup.setToolTipText(i18n("Duplica elemento selezionato"));
    jbDup.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jbDupActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jbAdd)
          .addComponent(jbRemove)
          .addComponent(jbDown)
          .addComponent(jbUp)
          .addComponent(jbEdit)
          .addComponent(jbDup))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jbAdd)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jbRemove)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jbEdit)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jbDup)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
        .addComponent(jbUp)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jbDown)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void jbAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbAddActionPerformed
  {//GEN-HEADEREND:event_jbAddActionPerformed
    notifyButtons(evt);
  }//GEN-LAST:event_jbAddActionPerformed

  private void jbRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbRemoveActionPerformed
  {//GEN-HEADEREND:event_jbRemoveActionPerformed
    notifyButtons(evt);
  }//GEN-LAST:event_jbRemoveActionPerformed

  private void jbUpActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbUpActionPerformed
  {//GEN-HEADEREND:event_jbUpActionPerformed
    notifyButtons(evt);
  }//GEN-LAST:event_jbUpActionPerformed

  private void jbDownActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbDownActionPerformed
  {//GEN-HEADEREND:event_jbDownActionPerformed
    notifyButtons(evt);
  }//GEN-LAST:event_jbDownActionPerformed

  private void jbEditActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbEditActionPerformed
  {//GEN-HEADEREND:event_jbEditActionPerformed
    notifyButtons(evt);
  }//GEN-LAST:event_jbEditActionPerformed

  private void jbDupActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jbDupActionPerformed
  {//GEN-HEADEREND:event_jbDupActionPerformed
    notifyButtons(evt);
  }//GEN-LAST:event_jbDupActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  public javax.swing.JButton jbAdd;
  public javax.swing.JButton jbDown;
  public javax.swing.JButton jbDup;
  public javax.swing.JButton jbEdit;
  public javax.swing.JButton jbRemove;
  public javax.swing.JButton jbUp;
  // End of variables declaration//GEN-END:variables
}
