package com.softwaremagico.ktg.core;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author jorge
 */
public class CompetitorWithPhoto extends RegisteredPerson implements Serializable {

    transient public InputStream photoInput;
    public long photoSize;

    public CompetitorWithPhoto(String id, String name, String surname, Club club) {
        super(id, name, surname);
        setClub(club);
    }

    public void addImage(InputStream tmp_photo, long size) {
        photoInput = tmp_photo;
        photoSize = size;
    }

    public Image photo() throws IOException {
        photoInput.reset();
        if (photoInput != null) {
            return ImageIO.read(photoInput);
        } else {
            return null;
        }
    }

    public void setPhoto(Image img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            /*
             * ToolkitImage toolkitImage = (ToolkitImage) img;
             * ImageRepresentation ir = toolkitImage.getImageRep();
             * BufferedImage bb = ir.getOpaqueRGBImage();
             */
            BufferedImage bb = toBufferedImage(img);
            ImageIO.write(bb, "png", os);
        } catch (IOException ex) {
        }
        photoInput = new ByteArrayInputStream(os.toByteArray());
        photoSize = os.toByteArray().length;
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    public String returnTag() {
        //Tag is the initials of name and surname plus a random number.
        String n1, n2;
        if (getName() != null) {
            n1 = getName().substring(0, 1);
        } else {
            n1 = "0";
        }
        if (getSurname() != null) {
            n2 = getSurname().substring(0, 1);
        } else {
            n2 = "0";
        }
        DecimalFormat myFormatter = new DecimalFormat("000");
        return n1 + n2 + myFormatter.format(photoSize % 1000);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RegisteredPerson)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
