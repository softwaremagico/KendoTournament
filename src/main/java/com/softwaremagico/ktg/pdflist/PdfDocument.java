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
 *  Created on 27-mar-2012.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.JOptionPane;

/**
 * Basical document in PDF.
 *
 * @author LOCAL\jhortelano
 */
public abstract class PdfDocument {

    protected int fontSize = 12;
    protected String font = FontFactory.HELVETICA;
    protected int fontType = Font.BOLD;
    protected int rightMargin = 50;
    protected int leftMargin = 50;
    protected int topMargin = 65;
    protected int bottomMargin = 55;
    protected Image bgImage;
    private float opacity = 0.6f;

    private void startBackgroundImage() {
        try {
            bgImage = Image.getInstance(Path.returnBackgroundPath());
        } catch (BadElementException ex) {
            MessageManager.errorMessage("imageNotFound", "Error", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (MalformedURLException ex) {
            MessageManager.errorMessage("imageNotFound", "Error", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (IOException ex) {
            MessageManager.errorMessage("imageNotFound", "Error", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    protected Document documentData(Document document) {
        document.addTitle("Kendo Tournament's File");
        document.addAuthor("Software Magico");
        document.addCreator("Kendo Tournament Tool");
        document.addSubject("Kendo List");
        document.addKeywords("Kendo, Tournament, KTG");
        document.addCreationDate();
        return document;
    }

    protected void generatePDF(Document document, PdfWriter writer) throws Exception {
        documentData(document);
        document.open();
        startBackgroundImage();
        createPagePDF(document, writer, font);
        document.close();
    }

    public boolean createFile(String path) {
        //DIN A6 105 x 148 mm
        Document document = new Document(getPageSize(), rightMargin, leftMargin, topMargin, bottomMargin);
        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }
        if (!MyFile.fileExist(path) || MessageManager.question("existFile", "Warning!", KendoTournamentGenerator.getInstance().language)) {
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                generatePDF(document, writer);
                MessageManager.customMessage(fileCreatedOkTag(), "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
                return false;
            } catch (Exception ex) {
                MessageManager.errorMessage(fileCreatedBadTag(), "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                return false;
            }
        }
        return true;
    }

    protected abstract Rectangle getPageSize();

    protected abstract String fileCreatedOkTag();

    protected abstract String fileCreatedBadTag();

    protected abstract void createPagePDF(Document document, PdfWriter writer, String font) throws Exception;

    /**
     * Event for creating a transparent cell.
     */
    class TransparentCellBackground implements PdfPCellEvent {

        public PdfGState documentGs = new PdfGState();

        public TransparentCellBackground() {
            documentGs.setFillOpacity(opacity);
            documentGs.setStrokeOpacity(1f);
        }

        public void cellLayout(PdfPCell cell, Rectangle rect,
                PdfContentByte[] canvas) {
            PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setGState(documentGs);
            cb.setColorFill(new Color(255, 255, 255));
            cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(),
                    rect.getHeight());
            cb.fill();
            cb.restoreState();
        }
    }

    /**
     * Event class to draw the background image for table headers.
     */
    class CellBgEvent implements PdfPCellEvent {

        public void cellLayout(PdfPCell cell, Rectangle rect,
                PdfContentByte[] canvas) {

            try {
                Image cellBgImage = Image.getInstance(Path.returnBackgroundPath());
                PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
                if (cellBgImage != null) {
                    cellBgImage.scaleAbsolute(rect.getWidth(), rect.getHeight());
                    cb.addImage(cellBgImage, rect.getWidth(), 0, 0, rect.getHeight(),
                            rect.getLeft(), rect.getBottom());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Event for adding a background image to a table.
     */
    class TableBgEvent implements PdfPTableEvent {

        public void tableLayout(PdfPTable ppt, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] pcbs) {
            try {
                //bgImage = Image.getInstance(Path.returnBackgroundPath());
                if (bgImage != null) {
                    int row = 0;
                    int columns = widths[row].length - 1;
                    Rectangle rect = new Rectangle(widths[row][0], heights[0], widths[row][columns], heights[row + 1]);
                    //bgImage.scaleAbsolute(rect.getWidth(), rect.getHeight());
                    pcbs[PdfPTable.BASECANVAS].addImage(bgImage, rect.getWidth(), 0, 0, rect.getHeight(), rect.getLeft(), rect.getBottom());
                }
            } catch (Exception e) {
                KendoTournamentGenerator.getInstance().showErrorInformation(e);
            }
        }
    }
}
