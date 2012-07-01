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
 *  Created on 27-oct-2009.
 */
package com.softwaremagico.ktg.pdflist;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;

/**
 *
 * @author Jorge
 */
public abstract class ParentList extends PdfDocument {

    protected int footerBorder = 0;
    protected int headerBorder = 0;

    @Override
    protected void createPagePDF(Document document, PdfWriter writer, String font) throws Exception {
        //addBackGroundImage(document, Path.returnBackgroundPath(), writer);
        PdfPTable mainTable = createMainTable(document, document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        mainTable.setWidthPercentage(100);
        document.add(mainTable);
    }

    /**
     * Creates the header of the document.
     *
     * @param document
     * @param mainTable
     * @param width
     * @param height
     * @param writer
     * @param font
     * @param fontSize
     */
    public abstract void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize);

    /**
     * Creates the body of the document.
     *
     * @param document
     * @param mainTable
     * @param width
     * @param height
     * @param writer
     * @param font
     * @param fontSize
     */
    public abstract void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize);

    /**
     * Creates the footer of the document.
     *
     * @param document
     * @param mainTable
     * @param width
     * @param height
     * @param writer
     * @param font
     * @param fontSize
     */
    public abstract void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize);

    /**
     * Obtain the widh of the main table of the document.
     *
     * @return
     */
    public abstract float[] getTableWidths();

    /**
     * Creates an empty row. It is formed by as many cells as the width of the
     * table.
     *
     * @return
     */
    public PdfPCell getEmptyRow() {
        return getEmptyCell(getTableWidths().length);
    }

    /**
     * Creates a cell with a header (similar to html headers).
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param align Text alignment.
     * @param fontSize Size of the font.
     * @return
     */
    public PdfPCell getHeader(String text, int border, int align, int fontSize) {
        return getCell(text, border, getTableWidths().length, align, new com.itextpdf.text.BaseColor(255, 255, 255), font, fontSize, Font.BOLD);
    }

    /**
     * Section header.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @return
     */
    public PdfPCell getHeader1(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 8);
    }

    /**
     * The bigger header.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param align Text alignment.
     * @return
     */
    public PdfPCell getHeader1(String text, int border, int align) {
        return getHeader(text, border, align, fontSize + 8);
    }

    /**
     * Subsection header.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @return
     */
    public PdfPCell getHeader2(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 6);
    }

    /**
     * Subsection header
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @param align Text alignment.
     * @return
     */
    public PdfPCell getHeader2(String text, int border, int align) {
        return getHeader(text, border, align, fontSize + 6);
    }

    /**
     * Subsubsection header.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @return
     */
    public PdfPCell getHeader3(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 4);
    }

    /**
     * Subsubsection header.
     *
     * @param text Text to be putted in the cell.
     * @param border Number of pixels of the border width.
     * @return
     */
    public PdfPCell getHeader4(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 2);
    }

    /**
     * Defines the propierties of the main table of the document.
     *
     * @param mainTable
     */
    public abstract void setTablePropierties(PdfPTable mainTable);

    /**
     * Creates the main table of the document.
     *
     * @param document
     * @param width
     * @param height
     * @param writer
     * @param font
     * @param fontSize
     * @return
     * @throws IOException
     * @throws BadElementException
     * @throws Exception
     */
    private PdfPTable createMainTable(Document document, float width, float height, PdfWriter writer, String font, int fontSize) throws IOException, BadElementException, Exception {
        PdfPCell cellHeader, cellFooter;
        Paragraph p;
        PdfPTable mainTable = new PdfPTable(getTableWidths());
        setTablePropierties(mainTable);

        cellHeader = new PdfPCell();
        cellHeader.setColspan(getTableWidths().length);

        cellFooter = new PdfPCell();
        cellFooter.setColspan(getTableWidths().length);

        mainTable.setHeaderRows(2);
        mainTable.setFooterRows(1);

        createHeaderRow(document, mainTable, width, height, writer, font, fontSize);
        createFooterRow(document, mainTable, width, height, writer, font, fontSize);
        createBodyRows(document, mainTable, width, height, writer, font, fontSize);

        return mainTable;
    }

}
