/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;

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
public class CompetitorWithPhoto extends Competitor implements Serializable {

    transient public InputStream photoInput;
    public long photoSize;

    public CompetitorWithPhoto(String tmp_id, String tmp_name, String tmp_surname, String tmp_club) {
        super(tmp_id, tmp_name, tmp_surname, tmp_club);
    }

    public void addImage(InputStream tmp_photo, long size) {
        photoInput = tmp_photo;
        photoSize = size;
    }

    public Image photo() throws IOException {
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
            ex.printStackTrace();
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
        if (name != null) {
            n1 = name.substring(0, 1);
        } else {
            n1 = "0";
        }
        if (surname != null) {
            n2 = surname.substring(0, 1);
        } else {
            n2 = "0";
        }
        DecimalFormat myFormatter = new DecimalFormat("000");
        return n1 + n2 + myFormatter.format(photoSize % 1000);
    }
}
