package com.softwaremagico.ktg.pdflist;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.database.DatabaseConnection;
import com.softwaremagico.ktg.files.Path;
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
    private com.itextpdf.text.Image banner = null;

    public CompetitorAccreditationCardPDF(CompetitorWithPhoto competitor, Tournament competition) throws Exception {
        this.competitor = competitor;
        this.competition = competition;
        role = DatabaseConnection.getInstance().getDatabase().getTagRole(competition, competitor);
    }

    public CompetitorAccreditationCardPDF(CompetitorWithPhoto competitor, Tournament competition, com.itextpdf.text.Image banner) throws Exception {
        this.competitor = competitor;
        this.competition = competition;
        role = DatabaseConnection.getInstance().getDatabase().getTagRole(competition, competitor);
        setBanner(banner);
    }

    private void setBanner(com.itextpdf.text.Image banner) {
        this.banner = banner;
    }

    @Override
    protected void createPagePDF(Document document, PdfWriter writer, String font) throws Exception {
        PdfPTable table = pageTable(document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        table.setWidthPercentage(100);
        document.add(table);
    }

    private PdfPTable createNameTable(String font, int fontSize) throws IOException, BadElementException {
        PdfPCell cell;
        Paragraph p;
        float[] widths = {0.03f, 0.35f, 0.03f, 0.64f};
        PdfPTable table = new PdfPTable(widths);
        com.itextpdf.text.Image competitorImage;

        if (competitor.photoSize > 0) {
            try {
                competitorImage = Image.getInstance(competitor.photo(), null);
            } catch (NullPointerException npe) {
                competitorImage = defaultPhoto;
            }
        } else {
            competitorImage = defaultPhoto;
        }


        table.addCell(this.getEmptyCell(1));

        cell = new PdfPCell(competitorImage, true);
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


        float[] widths = {0.08f, 0.90f, 0.02f};
        PdfPTable table2 = new PdfPTable(widths);

        table2.addCell(this.getEmptyCell());
        p = new Paragraph(KendoTournamentGenerator.getInstance().getAvailableRoles().getTraduction(role), FontFactory.getFont(font, fontSize - 2, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(1);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table2.addCell(cell);
        table2.addCell(this.getEmptyCell());

        table2.addCell(getEmptyCell(1));

        String identification = KendoTournamentGenerator.getInstance().getAvailableRoles().getAbbrev(role)
                + "-" + KendoTournamentGenerator.getInstance().getCompetitorOrder(competitor, role, competition);
        p = new Paragraph(identification, FontFactory.getFont(font, fontSize + 20));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border + 2);
        cell.setColspan(1);
        //cell.setFixedHeight(height);
        cell.setFixedHeight(height * 0.15f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try {
            cell.setBackgroundColor(KendoTournamentGenerator.getInstance().getAvailableRoles().getRole(role).getItextColor());
        } catch (NullPointerException npe) {
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(35, 144, 239));
        }
        table2.addCell(cell);

        table2.addCell(this.getEmptyCell());

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

        if (banner == null) {
            banner = com.itextpdf.text.Image.getInstance(Path.getBannerPath());
        }
        //png.scaleAbsoluteWidth(width);
        cell = new PdfPCell(banner, true);
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
            p = new Paragraph(competition.getName() + " (" + sqlTime + " " + sqlDate + ")",
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
