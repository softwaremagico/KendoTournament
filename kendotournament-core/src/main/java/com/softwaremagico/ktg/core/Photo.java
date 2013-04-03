package com.softwaremagico.ktg.core;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Photo {

    private String id;
    private transient InputStream photoInput;
    private Integer photoSize;

    public Photo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImage(InputStream tmp_photo, Integer size) {
        photoInput = tmp_photo;
        photoSize = size;
    }

    public Image getImage() {
        if (photoInput != null) {
            try {
                if (photoInput.markSupported()) {
                    photoInput.reset();
                }
                return ImageIO.read(photoInput);
            } catch (IOException | NullPointerException ex) {
            }
        }
        return null;
    }

    public BufferedImage getBufferedImage() {
        return toBufferedImage(getImage());
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

    public void setImage(Image img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage bb = toBufferedImage(img);
            ImageIO.write(bb, "png", os);
        } catch (IOException ex) {
        }
        photoInput = new ByteArrayInputStream(os.toByteArray());
        photoSize = new Integer(os.toByteArray().length);
    }

    public InputStream getInput() {
        try {
            if (photoInput.markSupported()) {
                photoInput.reset();
            }
        } catch (IOException ex) {
        }
        return photoInput;
    }

    public Integer getSize() {
        return photoSize;
    }
}
