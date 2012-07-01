/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
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
 *  Created on 22-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

class FileChooserPreview extends JFileChooser {

    String[] format = {".jpg", ".jpeg", ".png", ".gif", "bmp"};

    public FileChooserPreview() {
        this("");
    }

    public FileChooserPreview(String filename) {
        super(filename);
        Preview p = new Preview();
        setFileFilter(new MyFilter());
        setAccessory(p);
        addPropertyChangeListener(p);

    }

    private boolean checkFormat(String filename) {
        for (int i = 0; i < format.length; i++) {
            if (filename.endsWith(format[i])) {
                return true;
            }
        }
        return false;
    }

    class MyFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || checkFormat(f.getName().toLowerCase());
        }

        @Override
        public String getDescription() {
            return "Directory, gif, png, jpg, jpeg, bmp";
        }
    }

    class Preview extends JPanel implements PropertyChangeListener {

        ImageIcon icon;
        JLabel label;
        Color c = new Color(255, 255, 255);

        Preview() {
            icon = new ImageIcon();
            label = new JLabel(icon);
            add(label);
            setPreferredSize(new Dimension(150, 50));
            //this.setBackground(c);
            this.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                /*
                 * if we change the selected file
                 */
                File f = (File) e.getNewValue();
                if (f == null) {
                    return;
                }

                String s = f.getPath().toLowerCase();
                if (checkFormat(s)) {
                    ImageIcon ii = new ImageIcon(f.getPath());
                    Image i = ii.getImage();
                    icon.setImage(i.getScaledInstance(130, 130, Image.SCALE_FAST));
                    //icon.setImage();
                    label.updateUI();
                } else {
                }
            }
        }
    }
}
