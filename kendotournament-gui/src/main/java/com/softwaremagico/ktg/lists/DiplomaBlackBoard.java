package com.softwaremagico.ktg.lists;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.AlertManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author jorge
 */
public class DiplomaBlackBoard extends JPanel {

    Image imagen = null;
    int yline = 113;

    public DiplomaBlackBoard() {
        setLayout(new java.awt.BorderLayout());
    }

    public void setBackground(File file, Dimension d) throws IOException {
        try {
            if (file == null) {
                imagen = null;
            } else {
                imagen = ImageIO.read(file);
            }
            FileInputStream imageInput = new FileInputStream(file.getPath());
            BufferedImage input = ImageIO.read(imageInput);
            //imagen = imagen.getScaledInstance( (int) ((((double) getPreferredSize().height / srcHeight) * srcWidth)),getPreferredSize().height, Image.SCALE_FAST);
            imagen = imagen.getScaledInstance(d.width, d.height, Image.SCALE_FAST);
        } catch (javax.imageio.IIOException ie) {
            AlertManager.showErrorInformation(this.getClass().getName(),ie);
        }
    }

    public void paintLine(Graphics g, double y) {
        g.drawLine(0, (int) (super.getHeight() - y), super.getWidth(), (int) (super.getHeight() - y));
    }

    private File getBackground(String image) {
        File file = new File(image);
        if (!file.exists()) {
            file = new File(Path.getDiplomaPath());
            if (!file.exists()) {
            }
        }
        return file;
    }

    public void changeLine(int y) {
        yline = (getSize().height * y) / 200;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        try {
            setBackground(new Color(255, 255, 255));
            setBackground(getBackground(Path.getDiplomaPath()), getSize());
            if (imagen != null) {
                g.drawImage(imagen, 0, 0, null);
            }
            paintLine(g, yline);
            //super.paint(g);
        } catch (IOException ex) {
            Logger.getLogger(DiplomaBlackBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
