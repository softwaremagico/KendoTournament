package com.softwaremagico.ktg.tools;

import com.softwaremagico.ktg.core.Photo;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Media {

	public static BufferedImage getImageFitted(String imagePath, JPanel parent) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(imagePath);
			BufferedImage input = ImageIO.read(inputStream);
			BufferedImage scaled = resizeImage(input, parent.getWidth(), parent.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			return scaled;
		} catch (IOException | NullPointerException ex) {
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}

	public static BufferedImage getImageFitted(Photo photo, JPanel parent) {
		return resizeImage(photo.getBufferedImage(), parent.getWidth(), parent.getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	private static BufferedImage resizeImage(BufferedImage originalImage, int panelWidth, int panelHeight, int type) {
		BufferedImage resizedImage;
		int dstHeight, dstWidth;
		if (((double) originalImage.getHeight()) / panelHeight < ((double) originalImage.getWidth()) / panelWidth) {
			dstWidth = panelWidth;
			dstHeight = (int) (originalImage.getHeight() * panelWidth / (double) originalImage.getWidth());
		} else {
			dstWidth = (int) (originalImage.getWidth() * panelHeight / (double) originalImage.getHeight());
			dstHeight = panelHeight;
		}
		resizedImage = new BufferedImage(dstWidth, dstHeight, type);
		// System.out.println(originalImage.getWidth() + " " + originalImage.getHeight() + "/" + resizedImage.getWidth()
		// + "  " + resizedImage.getHeight() + " / " + panelWidth + " " + panelHeight);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(originalImage, 0, 0, dstWidth, dstHeight, null);
		g.dispose();

		return resizedImage;
	}
}
