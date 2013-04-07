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

import com.softwaremagico.ktg.core.Photo;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.base.KPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PhotoFrame extends KPanel {

    private Photo photo;
    private JPanel parentFrame;

    public PhotoFrame(JPanel container, String defaultImage) {
        parentFrame = container;
        changePhoto(defaultImage);
    }

    public long getPhotoSize() {
        return photo.getSize();
    }

    public Photo getPhoto() {
        return photo;
    }

    public final void changePhoto(String imagePath) {
        try {
            Image scaledImage;
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.getImage(imagePath);
            InputStream inputStream = new FileInputStream(imagePath);
            BufferedImage input = ImageIO.read(inputStream);
            int srcHeight = input.getHeight();
            int srcWidth = input.getWidth();
            if ((double) parentFrame.getHeight() / srcHeight < (double) parentFrame.getWidth() / srcWidth) {
                scaledImage = image.getScaledInstance((int) (((double) parentFrame.getHeight() / srcHeight) * srcWidth), parentFrame.getHeight(), Image.SCALE_AREA_AVERAGING);
            } else {
                try {
                    scaledImage = image.getScaledInstance(parentFrame.getWidth(), (int) ((((double) parentFrame.getWidth() / srcWidth) * srcHeight)), Image.SCALE_FAST);
                } catch (IllegalArgumentException iae) {
                    scaledImage = image.getScaledInstance(parentFrame.getPreferredSize().width, (int) ((((double) parentFrame.getPreferredSize().width / srcWidth) * srcHeight)), Image.SCALE_FAST);
                }
            }
            photo.setImage(scaledImage);
        } catch (IOException | NullPointerException sof) {
            if (!imagePath.equals(Path.getWhiteSquare())) {
                cleanPhoto();
            }
        }
    }

    public void changePhoto(Photo imagePhoto) {
        if (imagePhoto != null) {
            try {
                Image scaledImage;
                Image image = imagePhoto.getImage();
                BufferedImage input = imagePhoto.getBufferedImage();
                int srcHeight = input.getHeight();
                int srcWidth = input.getWidth();
                if ((double) parentFrame.getHeight() / srcHeight < (double) parentFrame.getWidth() / srcWidth) {
                    scaledImage = image.getScaledInstance((int) (((double) parentFrame.getHeight() / srcHeight) * srcWidth), parentFrame.getHeight(), Image.SCALE_AREA_AVERAGING);
                } else {
                    try {
                        scaledImage = image.getScaledInstance(parentFrame.getWidth(), (int) ((((double) parentFrame.getWidth() / srcWidth) * srcHeight)), Image.SCALE_FAST);
                    } catch (IllegalArgumentException iae) {
                        scaledImage = image.getScaledInstance(parentFrame.getPreferredSize().width, (int) ((((double) parentFrame.getPreferredSize().width / srcWidth) * srcHeight)), Image.SCALE_FAST);
                    }
                }
                photo.setImage(scaledImage);
            } catch (NullPointerException sof) {
                cleanPhoto();
            }
        }
    }

    public void cleanPhoto() {
        changePhoto(Path.getWhiteSquare());
    }

    @Override
    public void paint(Graphics screen) {
        try {
            int iWidth = photo.getImage().getWidth(this);
            int iHeight = photo.getImage().getHeight(this);
            int xPos = parentFrame.getWidth() / 2 - (photo.getImage().getWidth(this)) / 2;
            int yPos = parentFrame.getHeight() / 2 - (photo.getImage().getHeight(this)) / 2;
            screen.drawImage(photo.getImage(), xPos, yPos,
                    iWidth, iHeight, this);
        } catch (NullPointerException npe) {
        }
        this.setOpaque(false);
    }
}
