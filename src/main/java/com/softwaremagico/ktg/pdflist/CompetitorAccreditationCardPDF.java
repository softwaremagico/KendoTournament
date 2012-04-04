/*
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
 *   Created on 31-ene-2009.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.files.Path;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author Jorge
 */
public class CompetitorAccreditationCardPDF extends PdfDocument {

    Tournament competition;
    CompetitorWithPhoto competitor;
    private final int border = 0;
    private String role;

    public CompetitorAccreditationCardPDF(CompetitorWithPhoto tmp_competitor, Tournament tmp_competition) throws Exception {
        competitor = tmp_competitor;
        competition = tmp_competition;
        role = KendoTournamentGenerator.getInstance().database.getTagRole(competition, competitor);
    }

    protected void createPagePDF(Document document, PdfWriter writer, String font) throws Exception {
        PdfPTable table = pageTable(document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        table.setWidthPercentage(100);
        document.add(table);
    }

    private PdfPTable createNameTable(String font, int fontSize) throws IOException, BadElementException {
        PdfPCell cell;
        Paragraph p;
        float[] widths = {0.05f, 0.35f, 0.05f, 0.60f};
        PdfPTable table = new PdfPTable(widths);
        com.lowagie.text.Image img = null;
        boolean success = false;

        try {
            img = Image.getInstance(competitor.photo(), null);
        } catch (NullPointerException npe) {
            /*
             * If we do not redo the competitor object each time, when trying to
             * generate two times the accredtion card, the second one fails and
             * loss the photo!!!
             */
            competitor = KendoTournamentGenerator.getInstance().database.selectCompetitor(competitor.getId(), false);
            try {
                img = Image.getInstance(competitor.photo(), null);
                success = true;
            } catch (NullPointerException npe2) {
                if (!success) {
                    img = Image.getInstance(Path.returnDefaultPhoto());
                    //npe2.printStackTrace();
                }
            } catch (IllegalArgumentException ie) {
                if (!success) {
                    img = Image.getInstance(Path.returnDefaultPhoto());
                    //ie.printStackTrace();
                }
            }
        }

        table.addCell(this.getEmptyCell(1));



        cell = new PdfPCell(img, true);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);

        table.addCell(this.getEmptyCell(1));


        float[] widths2 = {0.90f, 0.10f};
        PdfPTable table2 = new PdfPTable(widths2);

        p = new Paragraph(competitor.getShortName(18), FontFactory.getFont(font, fontSize, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setCellEvent(new TransparentCellBackground());
        table2.addCell(cell);

        table2.addCell(this.getEmptyCell(1));

        p = new Paragraph(competitor.getShortSurname().toUpperCase(), FontFactory.getFont(font, fontSize + 6, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setCellEvent(new TransparentCellBackground());
        table2.addCell(cell);

        table2.addCell(this.getEmptyCell(1));

        p = new Paragraph(competitor.club, FontFactory.getFont(font, fontSize - 2));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setCellEvent(new TransparentCellBackground());
        table2.addCell(cell);

        table2.addCell(this.getEmptyCell(1));

        cell = new PdfPCell(table2);
        cell.setColspan(1);
        cell.setBorderWidth(border);
        table.addCell(cell);

        return table;
    }

    private PdfPTable createIdentificationTable(float height, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        PdfPTable table = new PdfPTable(1);


        float[] widths = {0.02f, 0.96f, 0.02f};
        PdfPTable table2 = new PdfPTable(widths);

        p = new Paragraph(KendoTournamentGenerator.getInstance().getAvailableRoles().getTraduction(role), FontFactory.getFont(font, fontSize - 2));
        cell = new PdfPCell(p);
        cell.setColspan(3);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table2.addCell(cell);

        p = new Paragraph("", FontFactory.getFont(font, fontSize - 2));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table2.addCell(cell);

        String identification = KendoTournamentGenerator.getInstance().getAvailableRoles().getAbbrev(role)
                + "-" + KendoTournamentGenerator.getInstance().getCompetitorOrder(competitor, role, competition.name);
        p = new Paragraph(identification, FontFactory.getFont(font, fontSize + 20));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border + 2);
        cell.setColspan(1);
        //cell.setFixedHeight(height);
        cell.setFixedHeight(height * 0.15f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try {
            cell.setBackgroundColor(KendoTournamentGenerator.getInstance().getAvailableRoles().getRole(role).color);
        } catch (NullPointerException npe) {
            cell.setBackgroundColor(new Color(35, 144, 239));
        }
        table2.addCell(cell);

        p = new Paragraph(" ", FontFactory.getFont(font, fontSize));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(cell);

        cell = new PdfPCell(table2);
        cell.setColspan(1);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);


        return table;
    }

    private PdfPTable createBannerTable(float width, float height) throws BadElementException, MalformedURLException, IOException {
        PdfPCell cell;
        PdfPTable table = new PdfPTable(1);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setTotalWidth(width);
        table.getTotalWidth();

        com.lowagie.text.Image png;
        png = com.lowagie.text.Image.getInstance(Path.returnBannerPath());
        //png.scaleAbsoluteWidth(width);
        cell = new PdfPCell(png, true);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);

        return table;
    }

    private PdfPTable mainTable(float width, float height, String font, int fontSize) throws IOException, BadElementException {
        PdfPCell cell;
        Paragraph p;
        float[] widths = {1};
        PdfPTable mainTable = new PdfPTable(widths);
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.setTotalWidth(width);

        mainTable.addCell(this.getEmptyCell(1));

        cell = new PdfPCell(createNameTable(font, fontSize));
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setFixedHeight(height * 0.20f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        mainTable.addCell(cell);

        cell = new PdfPCell(createIdentificationTable(height, font, fontSize));
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setFixedHeight(height * 0.20f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        mainTable.addCell(cell);

        cell = new PdfPCell(createBannerTable(width, height / 5));
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setFixedHeight(height * 0.20f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        mainTable.addCell(cell);

        return mainTable;
    }

    public PdfPTable pageTable(float width, float height, PdfWriter writer, String font, int fontSize) throws IOException, BadElementException {
        PdfPCell cell;
        float[] widths = {0.90f, 0.10f};
        PdfPTable mainTable = new PdfPTable(widths);
        mainTable.setTableEvent(new TableBgEvent()); //Add background image to the table. 
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.setTotalWidth(width + 30);


        cell = new PdfPCell(mainTable(width, height, font, fontSize));
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);

        cell = new PdfPCell(createSignature(font, fontSize - 5));
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setPaddingBottom(0);
        cell.setPaddingRight(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        mainTable.addCell(cell);

        return mainTable;
    }

    private PdfPTable createSignature(String font, int fontSize) {
        PdfPTable table = new PdfPTable(1);
        Paragraph p;
        PdfPCell cell;

        java.util.Date date = new java.util.Date();
        long lnMilisegundos = date.getTime();
        java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
        java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

        try {
            p = new Paragraph(competition.name + " (" + sqlTime + " " + sqlDate + ")",
                    FontFactory.getFont(font, fontSize));
        } catch (NullPointerException npen) {
            p = new Paragraph("Accredition Card (" + sqlTime + " " + sqlDate + ")",
                    FontFactory.getFont(font, fontSize));
        }
        cell = new PdfPCell(p);
        cell.setBorderWidth(0);
        cell.setRotation(90);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);

        return table;
    }

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A6;
    }

    @Override
    protected String fileCreatedOkTag() {
        return "accreditationOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "accreditationBad";
    }
}
