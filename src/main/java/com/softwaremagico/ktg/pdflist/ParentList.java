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

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.files.Path;
import java.awt.Color;
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
        addBackGroundImage(document, Path.returnBackgroundPath());
        PdfPTable mainTable = createMainTable(document, document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        mainTable.setWidthPercentage(100);
        document.add(mainTable);
    }

    public abstract void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize);

    public abstract void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize);

    public abstract void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize);

    public abstract float[] getTableWidths();

    public PdfPCell getEmptyCell(int colspan) {
        Paragraph p = new Paragraph(" ", FontFactory.getFont(font, fontSize, fontType));
        PdfPCell cell = new PdfPCell(p);
        cell.setColspan(colspan);
        cell.setBorder(0);
        cell.setBackgroundColor(Color.WHITE);
        return cell;
    }

    public PdfPCell getEmptyRow() {
        return getEmptyCell(getTableWidths().length);
    }

    public PdfPCell getEmptyCell() {
        return getEmptyCell(1);
    }

    public PdfPCell getCell(String text, int border, int colspan, int align, Color color, String font, int fontSize, int fontType) {
        Paragraph p = new Paragraph(text, FontFactory.getFont(font, fontSize, fontType));
        PdfPCell cell = new PdfPCell(p);
        cell.setColspan(colspan);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(align);
        cell.setBackgroundColor(color);

        return cell;
    }

    public PdfPCell getCell(String text) {
        return getCell(text, 0, 1, Element.ALIGN_LEFT, Color.WHITE, font, fontSize, fontType);
    }

    public PdfPCell getCell(String text, int colspan) {
        return getCell(text, 0, colspan, Element.ALIGN_LEFT, Color.WHITE, font, fontSize, fontType);
    }

    public PdfPCell getCell(String text, int colspan, int align) {
        return getCell(text, 0, colspan, align, Color.WHITE, font, fontSize, fontType);
    }

    public PdfPCell getCell(String text, int border, int colspan, int align) {
        return getCell(text, border, colspan, align, Color.WHITE, font, fontSize, fontType);
    }

    public PdfPCell getCell(String text, int colspan, int align, Color color) {
        return getCell(text, 0, colspan, align, Color.WHITE, font, fontSize, fontType);
    }

    public PdfPCell getHeader(String text, int border, int align, int fontSize) {
        return getCell(text, border, getTableWidths().length, align, new Color(255, 255, 255), font, fontSize, Font.BOLD);
    }

    public PdfPCell getHeader(String text, int border, int align, int fontSize, int colspan) {
        return getCell(text, border, colspan, align, new Color(255, 255, 255), font, fontSize, Font.BOLD);
    }

    public PdfPCell getHeader1(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 8);
    }

    public PdfPCell getHeader1(String text, int border, int align) {
        return getHeader(text, border, align, fontSize + 8);
    }

    public PdfPCell getHeader2(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 6);
    }

    public PdfPCell getHeader2(String text, int border, int align) {
        return getHeader(text, border, align, fontSize + 6);
    }

    public PdfPCell getHeader3(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 4);
    }

    public PdfPCell getHeader3(String text, int border, int colspan) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 4, colspan);
    }

    public PdfPCell getHeader4(String text, int border) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 2);
    }

    public PdfPCell getHeader4(String text, int border, int colspan) {
        return getHeader(text, border, Element.ALIGN_CENTER, fontSize + 2, colspan);
    }

    public abstract void setTablePropierties(PdfPTable mainTable);

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
