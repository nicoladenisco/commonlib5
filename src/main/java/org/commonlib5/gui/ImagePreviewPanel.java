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

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Pannello di preview per realizzare un file chooser
 * con anteprima dell'immagine selezionata.
 * <code>
 * JFileChooser chooser = new JFileChooser();
 * ImagePreviewPanel preview = new ImagePreviewPanel();
 * chooser.setAccessory(preview);
 * chooser.addPropertyChangeListener(preview);
 * </code>
 * @author Nicola De Nisco
 */
public class ImagePreviewPanel extends JPanel
   implements PropertyChangeListener
{
  private int width, height;
  private Image image;
  private static final int ACCSIZE = 155;
  private Color bg;

  public ImagePreviewPanel()
  {
    setPreferredSize(new Dimension(ACCSIZE, -1));
    bg = getBackground();
  }

  @Override
  public void propertyChange(PropertyChangeEvent e)
  {
    String propertyName = e.getPropertyName();

    // Make sure we are responding to the right event.
    if(propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
    {
      File selection = (File) e.getNewValue();
      if(selection == null)
        return;

      try
      {
        image = ImageIO.read(selection);
        scaleImage();
        repaint();
      }
      catch(IOException ex)
      {
      }
    }
  }

  private void scaleImage()
  {
    width = image.getWidth(this);
    height = image.getHeight(this);
    double ratio = 1.0;

    /*
     * Determine how to scale the image. Since the accessory can expand
     * vertically make sure we don't go larger than 150 when scaling
     * vertically.
     */
    if(width >= height)
    {
      ratio = (double) (ACCSIZE - 5) / width;
      width = ACCSIZE - 5;
      height = (int) (height * ratio);
    }
    else if(getHeight() > 150)
    {
      ratio = (double) (ACCSIZE - 5) / height;
      height = ACCSIZE - 5;
      width = (int) (width * ratio);
    }
    else
    {
      ratio = (double) getHeight() / height;
      height = getHeight();
      width = (int) (width * ratio);
    }

    image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
  }

  @Override
  public void paintComponent(Graphics g)
  {
    g.setColor(bg);

    /*
     * If we don't do this, we will end up with garbage from previous
     * images if they have larger sizes than the one we are currently
     * drawing. Also, it seems that the file list can paint outside
     * of its rectangle, and will cause odd behavior if we don't clear
     * or fill the rectangle for the accessory before drawing. This might
     * be a bug in JFileChooser.
     */
    g.fillRect(0, 0, ACCSIZE, getHeight());
    g.drawImage(image, getWidth() / 2 - width / 2 + 5,
       getHeight() / 2 - height / 2, this);
  }
}
