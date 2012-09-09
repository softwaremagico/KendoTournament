package com.softwaremagico.ktg;
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
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jorge
 */
public class Tournament implements Serializable {

    private String name;
    public transient InputStream bannerInput;
    public transient InputStream diplomaInput;
    public transient InputStream accreditationInput;
    public long bannerSize;
    public long diplomaSize;
    public long accreditationSize;
    public int fightingAreas;
    public int howManyTeamsOfGroupPassToTheTree;
    public int teamSize;
    public TournamentType mode;    //simple, championship, manual, tree
    private float scoreForWin = 1;
    private float scoreForDraw = 0;
    private String choosedScore = "European";

    public Tournament(String name, int areas, int passingTeams, int teamSize, TournamentType mode) {
        this.name = name;
        fightingAreas = areas;
        howManyTeamsOfGroupPassToTheTree = passingTeams;
        this.teamSize = teamSize;
        this.mode = mode;
    }
    
    public String getName(){
        return name;
    }

    public void addBanner(InputStream tmp_photo, long size) {
        bannerInput = tmp_photo;
        bannerSize = size;
    }

    public void addBanner(Image img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage bb = toBufferedImage(img);
            ImageIO.write(bb, "png", os);
        } catch (IOException ex) {
        }
        bannerInput = new ByteArrayInputStream(os.toByteArray());
        bannerSize = os.toByteArray().length;
    }

    public Image banner() throws IOException {
        if (bannerInput != null) {
            return ImageIO.read(bannerInput);
        } else {
            return null;
        }
    }

    public byte[] getBytesFromBanner() throws IOException {

        // Get the size of the file
        long length = bannerInput.available();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = bannerInput.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file ");
        }

        // Close the input stream and return bytes
        bannerInput.close();
        return bytes;
    }

    public void changeScoreOptions(String type, float win, float draw) {
        choosedScore = type;
        scoreForWin = win;
        scoreForDraw = draw;
        //storeConfig();
    }

    public float getScoreForWin() {
        return scoreForWin;
    }

    public float getScoreForDraw() {
        return scoreForDraw;
    }

    public String getChoosedScore() {
        return choosedScore;
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

    public void addDiploma(InputStream tmp_photo, long size) {
        diplomaInput = tmp_photo;
        diplomaSize = size;
    }

    public void addDiploma(Image img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage bb = toBufferedImage(img);
            ImageIO.write(bb, "png", os);
        } catch (IOException ex) {
        }
        diplomaInput = new ByteArrayInputStream(os.toByteArray());
        diplomaSize = os.toByteArray().length;
    }

    public Image diploma() throws IOException {
        if (diplomaInput != null) {
            return ImageIO.read(diplomaInput);
        } else {
            return null;
        }
    }

    public void addAccreditation(InputStream tmp_photo, long size) {
        accreditationInput = tmp_photo;
        accreditationSize = size;
    }

    public void addAccreditation(Image img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage bb = toBufferedImage(img);
            ImageIO.write(bb, "png", os);
        } catch (IOException ex) {
        }
        accreditationInput = new ByteArrayInputStream(os.toByteArray());
        accreditationSize = os.toByteArray().length;
    }

    public Image accreditation() throws IOException {
        if (accreditationInput != null) {
            return ImageIO.read(accreditationInput);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Tournament)) {
            return false;
        }
        Tournament otherTournament = (Tournament) object;
        return this.name.equals(otherTournament.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
