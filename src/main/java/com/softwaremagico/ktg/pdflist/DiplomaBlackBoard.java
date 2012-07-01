/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2009 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 27-oct-2009.
 */
package com.softwaremagico.ktg.pdflist;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.files.Path;
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ie);
        }
    }

    public void paintLine(Graphics g, double y) {
        g.drawLine(0, (int) (super.getHeight() - y), super.getWidth(), (int) (super.getHeight() - y));
    }

    private File getBackground(String image) {
        File file = new File(image);
        if (!file.exists()) {
            file = new File(Path.returnDiplomaPath());
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
            setBackground(getBackground(Path.returnDiplomaPath()), getSize());
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
