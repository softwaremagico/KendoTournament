package com.softwaremagico.ktg.gui;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
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
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PanelBackground extends JPanel {

    Image imagen = null;

    public void setBackground(File file) throws IOException {
        if (file == null) {
            imagen = null;
        } else {
            imagen = ImageIO.read(file);
        }
        FileInputStream imageInput = new FileInputStream(file.getPath());
        BufferedImage input = ImageIO.read(imageInput);
        int srcHeight = input.getHeight();
        int srcWidth = input.getWidth();
        imagen = imagen.getScaledInstance(getPreferredSize().width, (int) ((((double) getPreferredSize().width / srcWidth) * srcHeight)), Image.SCALE_FAST);
    }

    public void setBackgroundExtended(File file) throws IOException {
        if (file == null) {
            imagen = null;
        } else {
            imagen = ImageIO.read(file);
        }
        FileInputStream imageInput = new FileInputStream(file.getPath());
        BufferedImage input = ImageIO.read(imageInput);
        imagen = imagen.getScaledInstance(getPreferredSize().width, getPreferredSize().height, Image.SCALE_FAST);
    }

    public void removeBackground() {
        imagen = null;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, null);
        }
        Component c;
        for (int i = 0; i < getComponentCount(); i++) {
            c = getComponent(i);
            g.translate(c.getX(), c.getY());
            c.print(g);
            g.translate(-c.getX(), -c.getY());
        }
    }
}
