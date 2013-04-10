package com.softwaremagico.ktg.pdflist;
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

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.PdfDocument;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;

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
    static Translator trans = LanguagePool.getTranslator("gui.xml");
    protected static com.itextpdf.text.Image defaultPhoto;

    static {
        try {
            defaultPhoto = Image.getInstance(Path.getDefaultPhoto());
        } catch (BadElementException | IOException ex) {
            KendoLog.severe(PdfDocument.class.getName(), "Default image not found when creating pdf document.");
        }
    }

    PdfDocument() {
    }

    private void startBackgroundImage() {
        try {
            bgImage = Image.getInstance(Path.getBackgroundPath());
        } catch (BadElementException | IOException ex) {
            MessageManager.errorMessage(this.getClass().getName(), "imageNotFound", "Error");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
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
        if (!MyFile.fileExist(path) || MessageManager.questionMessage("existFile", "Warning!")) {
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                TableFooter event = new TableFooter();
                writer.setPageEvent(event);
                generatePDF(document, writer);
                MessageManager.translatedMessage(this.getClass().getName(), fileCreatedOkTag(), "PDF", JOptionPane.INFORMATION_MESSAGE);
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
                return false;
            } catch (Exception ex) {
                MessageManager.errorMessage(this.getClass().getName(), fileCreatedBadTag(), "PDF");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
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
     * Creates an empty cell.
     *
     * @param colspan The width of the cell.
     * @return
     */
    public PdfPCell getEmptyCell(int colspan) {
        Paragraph p = new Paragraph(" ", FontFactory.getFont(font, fontSize, fontType));
        PdfPCell cell = new PdfPCell(p);
        cell.setColspan(colspan);
        cell.setBorder(0);
        //cell.setBackgroundColor(Color.WHITE);
        return cell;
    }

    /**
     * Creates a cell.
     *
     * @param text Test to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param colspan Width of the cell.
     * @param align Text alignment.
     * @param color Background color of the cell.
     * @param font Font of the text.
     * @param fontSize Size of the font.
     * @param fontType Cursive, Bold, ...
     * @return
     */
    public PdfPCell getCell(String text, int border, int colspan, int align, com.itextpdf.text.BaseColor color, String font, int fontSize, int fontType) {
        Paragraph p = new Paragraph(text, FontFactory.getFont(font, fontSize, fontType));
        PdfPCell cell = new PdfPCell(p);
        cell.setColspan(colspan);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(align);
        cell.setBackgroundColor(color);

        return cell;
    }

    /**
     * Creates an empty cell.
     *
     * @return
     */
    public PdfPCell getEmptyCell() {
        return getEmptyCell(1);
    }

    /**
     * Default cell
     *
     * @param text Text that will appear into the cell.
     * @return
     */
    public PdfPCell getCell(String text) {
        return getCell(text, 0, 1, Element.ALIGN_LEFT, com.itextpdf.text.BaseColor.WHITE, font, fontSize, fontType);
    }

    /**
     * Default cell
     *
     * @param text Text that will appear into the cell.
     * @return
     */
    public PdfPCell getCellSize(String text, int fontSize) {
        return getCell(text, 0, 1, Element.ALIGN_LEFT, com.itextpdf.text.BaseColor.WHITE, font, fontSize, fontType);
    }

    /**
     * Creates a cell.
     *
     * @param text Text to be putted in the cell.
     * @param colspan Width of the cell.
     * @return
     */
    public PdfPCell getCell(String text, int colspan) {
        return getCell(text, 0, colspan, Element.ALIGN_LEFT, com.itextpdf.text.BaseColor.WHITE, font, fontSize, fontType);
    }

    /**
     * Creates a cell.
     *
     * @param text Text to be putted in the cell.
     * @param colspan Width of the cell.
     * @param align Text alignment.
     * @return
     */
    public PdfPCell getCell(String text, int colspan, int align) {
        return getCell(text, 0, colspan, align, com.itextpdf.text.BaseColor.WHITE, font, fontSize, fontType);
    }

    /**
     * Creates a cell.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param colspan Width of the cell.
     * @param align Text alignment.
     * @return
     */
    public PdfPCell getCell(String text, int border, int colspan, int align) {
        return getCell(text, border, colspan, align, com.itextpdf.text.BaseColor.WHITE, font, fontSize, fontType);
    }

    /**
     * Creates a cell.
     *
     * @param text Text to be putted in the cell.
     * @param colspan Width of the cell.
     * @param align Text alignment.
     * @param color Background color of the cell.
     * @return
     */
    public PdfPCell getCell(String text, int colspan, int align, Color color) {
        return getCell(text, 0, colspan, align, com.itextpdf.text.BaseColor.WHITE, font, fontSize, fontType);
    }

    /**
     * Default header.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param align Text alignment.
     * @param fontSize Size of the font.
     * @param colspan Width of the cell.
     * @return
     */
    public PdfPCell getHeader(String text, int border, int align, int fontSize, int colspan) {
        return getCell(text, border, colspan, align, new com.itextpdf.text.BaseColor(255, 255, 255), font, fontSize, Font.BOLD);
    }

    /**
     * Marked text.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param colspan Width of the cell.
     * @return
     */
    public PdfPCell getHeader3(String text, int border, int colspan) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 4, colspan);
    }

    /**
     * Marked text.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param colspan Width of the cell.
     * @return
     */
    public PdfPCell getHeader4(String text, int border, int colspan) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 2, colspan);
    }

    /**
     * Event for creating a transparent cell.
     */
    class TransparentCellBackground implements PdfPCellEvent {

        public PdfGState documentGs = new PdfGState();

        public TransparentCellBackground() {
            documentGs.setFillOpacity(opacity);
            documentGs.setStrokeOpacity(1f);
        }

        @Override
        public void cellLayout(PdfPCell cell, Rectangle rect,
                PdfContentByte[] canvas) {
            PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setGState(documentGs);
            cb.setColorFill(new com.itextpdf.text.BaseColor(255, 255, 255));
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

        @Override
        public void cellLayout(PdfPCell cell, Rectangle rect,
                PdfContentByte[] canvas) {

            try {
                Image cellBgImage = Image.getInstance(Path.getBackgroundPath());
                PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
                if (cellBgImage != null) {
                    cellBgImage.scaleAbsolute(rect.getWidth(), rect.getHeight());
                    cb.addImage(cellBgImage, rect.getWidth(), 0, 0, rect.getHeight(),
                            rect.getLeft(), rect.getBottom());
                }

            } catch (IOException | DocumentException e) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), e);
            }
        }
    }

    /**
     * Event for adding a background image to a table.
     */
    class TableBgEvent implements PdfPTableEvent {

        @Override
        public void tableLayout(PdfPTable ppt, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] pcbs) {
            try {
                if (bgImage != null) {
                    int row = 0;
                    int columns = widths[row].length - 1;
                    Rectangle rect = new Rectangle(widths[row][0], heights[0], widths[row][columns], heights[row + 1]);
                    pcbs[PdfPTable.BASECANVAS].addImage(bgImage, rect.getWidth(), 0, 0, -rect.getHeight(), rect.getLeft(), rect.getTop());
                }
            } catch (Exception e) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), e);
            }
        }
    }

    class TableFooter extends PdfPageEventHelper {

        /**
         * The header text.
         */
        String header;
        /**
         * The template with the total number of pages.
         */
        PdfTemplate total;

        /**
         * Allows us to change the content of the header.
         *
         * @param header The new header String
         */
        public void setHeader(String header) {
            this.header = header;
        }

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        /**
         * Adds a header to every page
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable table = new PdfPTable(3);
            try {
                table.setWidths(new int[]{24, 24, 2});
                table.setTotalWidth(527);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
                table.getDefaultCell().setBorder(Rectangle.TOP);
                table.addCell(header);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                //table.addCell(String.format("Page %d of", writer.getPageNumber()));
                table.addCell(String.format(trans.returnTag("Page")
                        + " %d " + trans.returnTag("Of"), writer.getPageNumber()));
                PdfPCell cell = new PdfPCell(Image.getInstance(total));
                cell.setBorder(Rectangle.TOP);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, 34, table.getTotalHeight() + bottomMargin, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        /**
         * Fills out the total number of pages before the document is closed.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber() - 1)),
                    2, 2, 0);
        }
    }
}
