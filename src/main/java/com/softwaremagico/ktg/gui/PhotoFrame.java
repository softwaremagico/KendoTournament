/*
 * 
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 28-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.files.Path;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PhotoFrame extends JPanel {

    Image photo;
    InputStream photoInput;
    JPanel parentFrame;
    long size;

    public PhotoFrame(JPanel container, String defaultImage) {
        photo(container, defaultImage);
    }

    private void photo(JPanel container, String defaultImage) {
        try {
            parentFrame = container;
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            photo = toolkit.getImage(defaultImage);
            photoInput = new FileInputStream(defaultImage);
            BufferedImage input = ImageIO.read(photoInput);
            int srcHeight = input.getHeight();
            int srcWidth = input.getWidth();
            if ((double) parentFrame.getHeight() / srcHeight < (double) parentFrame.getWidth() / srcWidth) {
                photo = photo.getScaledInstance((int) (((double) parentFrame.getHeight() / srcHeight) * srcWidth), parentFrame.getHeight(), Image.SCALE_AREA_AVERAGING);
            } else {
                try {
                    photo = photo.getScaledInstance(parentFrame.getWidth(), (int) ((((double) parentFrame.getWidth() / srcWidth) * srcHeight)), Image.SCALE_FAST);
                } catch (IllegalArgumentException iae) {
                    photo = photo.getScaledInstance(parentFrame.getPreferredSize().width, (int) ((((double) parentFrame.getPreferredSize().width / srcWidth) * srcHeight)), Image.SCALE_FAST);
                }
            }
            File file = new File(defaultImage);
            size = file.length();
            photoInput = new FileInputStream(defaultImage);
        } catch (StackOverflowError | IOException sof) {
        }
    }

    private static Image Resize(Image picture, double xFactor, double yFactor) {
        BufferedImage buffer;
        Graphics2D g;
        AffineTransform transformer;
        AffineTransformOp operation;

        buffer = new BufferedImage(
                picture.getWidth(null),
                picture.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        g = buffer.createGraphics();
        g.drawImage(picture, 0, 0, null);
        transformer = new AffineTransform();
        transformer.scale(xFactor, yFactor);
        operation = new AffineTransformOp(transformer, AffineTransformOp.TYPE_BILINEAR);
        buffer = operation.filter(buffer, null);
        return (Toolkit.getDefaultToolkit().createImage(buffer.getSource()));
    }

    public void Resize(int width, int height) throws Exception {
        InputStream tmp_image;
        BufferedImage src = ImageIO.read(photoInput);
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance((double) width / src.getWidth(), (double) height / src.getHeight());
        g.drawRenderedImage(src, at);
        ImageIO.write(dest, "PNG", new File(Path.returnImagePath() + "tmp.png"));
        tmp_image = new FileInputStream(Path.returnImagePath() + "tmp.png");
        File file = new File(Path.returnImagePath() + "tmp.png");
        size = file.length();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        photo = toolkit.getImage(Path.returnImagePath() + "tmp.png");
        photoInput = tmp_image;
    }

    void ChangePhoto(String path) {
        CleanPhoto();
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            photo = toolkit.getImage(path);
            photoInput = new FileInputStream(path);
            BufferedImage input = ImageIO.read(photoInput);
            float srcHeight = input.getHeight();
            float srcWidth = input.getWidth();
            if ((double) parentFrame.getHeight() / srcHeight < (double) parentFrame.getWidth() / srcWidth) {
                photo = photo.getScaledInstance((int) (((double) parentFrame.getHeight() / srcHeight) * srcWidth), parentFrame.getHeight(), Image.SCALE_AREA_AVERAGING);
            } else {
                photo = photo.getScaledInstance(parentFrame.getWidth(), (int) ((double) (parentFrame.getWidth() / srcWidth) * srcHeight), Image.SCALE_FAST);
            }
            File file = new File(path);
            size = file.length();
            photoInput = new FileInputStream(path);
        } catch (IOException ex) {
            Logger.getLogger(NewCompetitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ChangePhoto(Image tmp_photo, InputStream tmp_photoInput, long tmp_size) {
        try {
            CleanPhoto();
            photo = tmp_photo;
            tmp_photoInput.reset();
            photoInput = tmp_photoInput;
            size = tmp_size;
            BufferedImage input = (BufferedImage) tmp_photo;
            //BufferedImage input = ImageIO.read(photoInput);
            int srcHeight = input.getHeight();
            int srcWidth = input.getWidth();
            if ((double) parentFrame.getHeight() / srcHeight < (double) parentFrame.getWidth() / srcWidth) {
                photo = photo.getScaledInstance((int) (((double) parentFrame.getHeight() / srcHeight) * srcWidth), parentFrame.getHeight(), Image.SCALE_AREA_AVERAGING);
            } else {
                photo = photo.getScaledInstance(parentFrame.getWidth(), (int) ((((double) parentFrame.getWidth() / srcWidth) * srcHeight)), Image.SCALE_FAST);
            }
        } catch (IOException ex) {
            Logger.getLogger(PhotoFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
        }
    }

    public void CleanPhoto() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            photo = toolkit.getImage(Path.returnWhiteSquare());
            photoInput = new FileInputStream(Path.returnWhiteSquare());
            BufferedImage input = ImageIO.read(photoInput);
            photo = photo.getScaledInstance(600, 600, Image.SCALE_FAST);
            File file = new File(Path.returnWhiteSquare());
            size = file.length();
            //photoInput.close();
            this.repaint();
            this.revalidate();
        } catch (IOException ex) {
            Logger.getLogger(PhotoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paint(Graphics screen) {
        try {
            int iWidth = photo.getWidth(this);
            int iHeight = photo.getHeight(this);
            int xPos = parentFrame.getWidth() / 2 - (photo.getWidth(this)) / 2;
            int yPos = parentFrame.getHeight() / 2 - (photo.getHeight(this)) / 2;
            screen.drawImage(photo, xPos, yPos,
                    iWidth, iHeight, this);
        } catch (NullPointerException npe) {
        }
        this.setOpaque(false);
    }
}
