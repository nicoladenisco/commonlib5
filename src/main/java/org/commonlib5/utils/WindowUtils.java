/*
 * WindowUtils.java
 *
 * Created on 4-mar-2009, 16.47.03
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 * Funzioni di utilita' per la manipolazione delle finestre.
 *
 * @author Nicola De Nisco
 */
public class WindowUtils
{
  /**
   * Ritorna una dimensione scalata in percentuale rispetto alle dimensioni schermo.
   * @param percWidth da 0 a 1
   * @param percHeight da 0 a 1
   * @return dimensione scalata
   */
  public static Dimension getRelativeScreenDimension(float percWidth, float percHeight)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    return new Dimension((int) (screenSize.width * percWidth), (int) (screenSize.height * percHeight));
  }

  /**
   * Ritorna una dimensione scalata in percentuale rispetto alle dimensioni del contenitore.
   * @param parent contenitore di riferimento
   * @param percWidth da 0 a 1
   * @param percHeight da 0 a 1
   * @return dimensione scalata
   */
  public static Dimension getRelativeParentDimension(Container parent, float percWidth, float percHeight)
  {
    Dimension parentSize = parent.getSize();
    return new Dimension((int) (parentSize.width * percWidth), (int) (parentSize.height * percHeight));
  }

  /**
   * Centra una pannello nello schermo ridimensionandolo
   * ad una frazione della dimensione dello schermo.
   * @param wnd
   * @param percWidth da 0 a 1
   * @param percHeight da 0 a 1
   */
  public static void fitAndCenterInScreen(Component wnd, float percWidth, float percHeight)
  {
    // dimensione automatiche al percWidth/percHeight dello schermo
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dlgSize = new Dimension((int) (screenSize.width * percWidth), (int) (screenSize.height * percHeight));
    wnd.setSize(dlgSize);
    wnd.setLocation((screenSize.width - dlgSize.width) / 2, (screenSize.height - dlgSize.height) / 2);
  }

  /**
   * Centra una pannello all'interno del suo contenitore ridimensionandolo
   * ad una frazione della dimensione del contenitore.
   * @param parent
   * @param wnd
   * @param percWidth da 0 a 1
   * @param percHeight da 0 a 1
   */
  public static void fitAndCenterInParent(Container parent, Window wnd, float percWidth, float percHeight)
  {
    // dimensione automatiche al percWidth/percHeight dello schermo
    Dimension parentSize = parent.getSize();
    Dimension dlgSize = new Dimension((int) (parentSize.width * percWidth), (int) (parentSize.height * percHeight));
    wnd.setSize(dlgSize);
    wnd.setLocationRelativeTo(parent);
  }

  /**
   * Centra un pannello nello schermo.
   * @param ed pannello da centrare
   */
  public static void centerInScreen(Component ed)
  {
    // centraggio nello schermo
    Dimension dlgSize = ed.getSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    ed.setLocation((screenSize.width - dlgSize.width) / 2, (screenSize.height - dlgSize.height) / 2);
  }

  /**
   * Centra un pannello all'interno di un contenitore.
   * @param parent contentitore
   * @param wnd pannello da centrare
   */
  public static void centerInParent(Container parent, Window wnd)
  {
    wnd.setLocationRelativeTo(parent);
  }

  /**
   * Scorre la catena dei contenitori a caccia dell'oggetto Frame contenitore.
   * Serve per aprire correttamente le JDialog da qualsiasi pannello.
   * @param c componente da cui iniziare la ricerca
   * @return la Frame più esterna oppure null
   */
  public static Frame findParentFrame(Container c)
  {
    while(c != null)
    {
      if(c instanceof Frame)
        return (Frame) c;

      c = c.getParent();
    }
    return null;
  }

  /**
   * Recupera tutti i valori di un combo box.
   * Vengono eliminati i doppioni.
   * L'ordine non è assicurato uguale a quello dell'oggetto.
   * @param cb oggetto di riferimento
   * @return array dei valori
   */
  public static String[] getAllItems(JComboBox cb)
  {
    HashSet<String> hs = new HashSet<String>();
    if(cb.isEditable())
      hs.add(StringOper.okStr(cb.getSelectedItem()));

    for(int i = 0; i < cb.getItemCount(); i++)
      hs.add(StringOper.okStr(cb.getItemAt(i)));

    return hs.toArray(new String[hs.size()]);
  }

  /**
   * This routine used to set column widths using percentages.
   * @param table table to resize
   * @param percentages column width (will be normalized)
   */
  public static void setPreferredTableColumnWidths(JTable table, double[] percentages)
  {
    Dimension tableDim = table.getSize();
    double total = 0;
    for(int i = 0; i < table.getColumnModel().getColumnCount(); i++)
      total += percentages[i];

    int resMod = table.getAutoResizeMode();
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    for(int i = 0; i < table.getColumnModel().getColumnCount(); i++)
    {
      TableColumn column = table.getColumnModel().getColumn(i);
      column.setPreferredWidth((int) Math.round(tableDim.width * (percentages[i] / total)));
    }
    table.doLayout();
    table.setAutoResizeMode(resMod);
  }

  /**
   * Recupera dimensioni attuali delle colonne.
   * @param table tabella di cui recuperare le dimensioni colonne
   * @return array di dimensioni, uno per colonna
   */
  public static double[] getTableColumnWidths(JTable table)
  {
    double[] rv = new double[table.getColumnModel().getColumnCount()];
    for(int i = 0; i < table.getColumnModel().getColumnCount(); i++)
    {
      TableColumn column = table.getColumnModel().getColumn(i);
      rv[i] = column.getWidth();
    }
    return rv;
  }

  /**
   * Draw a text string with justify.
   * @param s string to print
   * @param px point x coord
   * @param py point y coort
   * @param Xjustify 0=center -1=left +1=right
   * @param Yjustify 0=center -1=botto +1=top
   * @param g graphics context
   * @return altezza in pixel della riga stampata
   */
  public static int drawString(String s, int px, int py, int Xjustify, int Yjustify, Graphics g)
  {
    int x, y, w, h;
    FontMetrics fm = g.getFontMetrics();
    w = fm.stringWidth(s);
    h = fm.getAscent() + fm.getDescent();

    switch(Xjustify)
    {
      default:
      case 0: // center
        x = px - w / 2;
        break;
      case -1: // left
        x = px;
        break;
      case +1: // right
        x = px - w;
        break;
    }

    switch(Yjustify)
    {
      default:
      case 0: // center
        y = py - h / 2;
        break;
      case -1: // bottom
        y = py;
        break;
      case +1: // top
        y = py - h;
        break;
    }

    y += fm.getAscent();

    g.drawString(s, x, y);
    return h;
  }

  /**
   * Stampa un array di stringhe.
   * Le stringhe vengono stampate una sotto l'altra nell'oggetto
   * Grapthics specificato.
   * @param strings array di stringhe
   * @param px point x coord
   * @param py point y coort
   * @param Xjustify 0=center -1=left +1=right
   * @param Yjustify 0=center -1=botto +1=top
   * @param g graphics context
   * @param yinc incremento di y fra una riga e l'altra
   * se maggiore di 0 è l'incremento desiderato
   * se minore di 0 è l'altezza dalla riga più - yinc
   * se uguale a 0 è l'altezza della riga
   */
  public static void drawStrings(String[] strings, int px, int py, int Xjustify, int Yjustify, Graphics g, int yinc)
  {
    for(int i = 0; i < strings.length; i++)
    {
      String s = strings[i];
      int h = drawString(s, px, py, Xjustify, Yjustify, g);
      if(yinc > 0)
        py += yinc;
      else
        py += h - yinc;
    }
  }

  /**
   * Abilita/Disabilita tutti i componenti di un contenitore.
   * Esegue setEnable(enable) per tutti i componenti del contenitore.
   * Ad esempio se eseguito su una JDialog abilita o disabilita tutti
   * i controlli in essa contenuti.
   * La ricerca componenti è ricorsiva se fra i componenti ci sono
   * oggetti di tipo Container (generalmente oggetti JPanel all'interno
   * di una JDialog).
   * @param container container su cui eseguire l'azione
   * @param enable valore da passare a setEnable()
   */
  public static void enableComponents(Container container, boolean enable)
  {
    Component[] components = container.getComponents();
    for(Component component : components)
    {
      component.setEnabled(enable);
      if(component instanceof Container)
      {
        enableComponents((Container) component, enable);
      }
    }
  }

  /**
   * Aggiunge una funzione di chiusura alla dialog con tasto ESC.
   * La dialog viene chiusa con setVisible(false) ovvero
   * va bene per dialog non modali.
   * @param dialog dialog a cui aggiungere la funzione
   */
  public static void addEscapeListenerVisible(final JDialog dialog)
  {
    ActionListener escListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        dialog.setVisible(false);
      }
    };

    dialog.getRootPane().registerKeyboardAction(escListener,
       KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
       JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  /**
   * Aggiunge una funzione di chiusura alla dialog con tasto ESC.
   * La dialog viene chiusa con dispose() ovvero
   * va bene per dialog modali.
   * @param dialog dialog a cui aggiungere la funzione
   */
  public static void addEscapeListenerDispose(final JDialog dialog)
  {
    ActionListener escListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        dialog.dispose();
      }
    };

    dialog.getRootPane().registerKeyboardAction(escListener,
       KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
       JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  /**
   * Aggiunge una funzione generica alla dialog con tasto ESC.
   * @param dialog dialog a cui aggiungere la funzione
   * @param escListener azione da intraprendere alla pressione di ESC
   */
  public static void addEscapeListener(JDialog dialog, ActionListener escListener)
  {
    dialog.getRootPane().registerKeyboardAction(escListener,
       KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
       JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  /**
   * Abilita/disabilita un pannello e tutti i controlli che contiene in modo ricorsivo.
   * @param panel pannello da modificare
   * @param isEnabled vero per abilitare altrimenti false
   */
  public static void setPanelEnabled(JPanel panel, boolean isEnabled)
  {
    panel.setEnabled(isEnabled);

    for(Component cmp : panel.getComponents())
    {
      if(cmp instanceof javax.swing.JPanel)
      {
        setPanelEnabled((JPanel) cmp, isEnabled);
      }

      cmp.setEnabled(isEnabled);
    }
  }
}
