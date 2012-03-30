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
 *   Created on 5-feb-2009.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.JOptionPane;

public class TeamAccreditationCardPDF {

    Team team;
    private final int border = 1;
    private int fontSize = 17;
    Tournament competition;

    public TeamAccreditationCardPDF(Team tmp_team, Tournament tmp_competition) throws Exception {
        team = tmp_team;
        competition = tmp_competition;
    }

    public void GenerateTeamPDF(String path) {
        //DIN A6 105 x 148 mm
        Document document = new Document(PageSize.A4);
        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }
        if (!MyFile.fileExist(path) || MessageManager.question("existFile", "Warning!", KendoTournamentGenerator.getInstance().language)) {
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                GeneratePDF(document, writer);
                MessageManager.customMessage("teamOK", "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().database.setParticipantsInTournamentAsAccreditationPrinted(team.getCompetitorsInLevel(0), competition.name);
            } catch (NullPointerException npe) {
                MessageManager.errorMessage("noTeamFieldsFilled", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            } catch (Exception ex) {
                MessageManager.errorMessage("teamBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
        }
    }

    private void GeneratePDF(Document document, PdfWriter writer) throws Exception {
        String font = FontFactory.HELVETICA;
        DocumentData(document);
        document.open();
        AccreditationGroupPagePDF(document, writer, font);
        document.close();
    }

    private Document DocumentData(Document document) {
        document.addTitle("Kendo Tournament Team's accreditation card");
        document.addAuthor("Jorge Hortelano");
        document.addCreator("Kendo Tournament Tool");
        document.addSubject("Accreditation Card of Competitor");
        document.addKeywords("Kendo, Tournament, Card, Accreditation, Competitor");
        document.addCreationDate();
        return document;
    }

    private void AddBackGroundImage(Document document, String imagen) throws BadElementException,
            DocumentException, MalformedURLException, IOException {
        com.lowagie.text.Image png;

        png = com.lowagie.text.Image.getInstance(imagen);
        png.setAlignment(com.lowagie.text.Image.UNDERLYING);
        png.scaleToFit(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2);
        png.setAbsolutePosition(0, 0);
        document.add(png);
        png.setAbsolutePosition(document.getPageSize().getWidth() / 2, 0);
        document.add(png);
        png.setAbsolutePosition(0, document.getPageSize().getHeight() / 2);
        document.add(png);
        png.setAbsolutePosition(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2);
        document.add(png);
    }

    private void AccreditationGroupPagePDF(Document document, PdfWriter writer, String font) throws Exception {
        AddBackGroundImage(document, Path.returnBackgroundPath());
        PdfPTable table = PageTable(document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        table.setWidthPercentage(100);
        document.add(table);
        
    }

    public PdfPTable PageTable(float width, float height, PdfWriter writer, String font, int fontSize) throws IOException, BadElementException, Exception {
        PdfPCell cell;
        Paragraph p;
        int i;
        float[] widths = {0.50f, 0.50f};
        PdfPTable mainTable = new PdfPTable(widths);
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.setTotalWidth(width);

        for (i = 0; i < team.getNumberOfMembers(0); i++) {
            CompetitorWithPhoto c = KendoTournamentGenerator.getInstance().database.selectCompetitor(team.getMember(i, 0).getId(), false);
            CompetitorAccreditationCardPDF competitorPDF = new CompetitorAccreditationCardPDF(c, competition);
            PdfPTable competitorTable = competitorPDF.pageTable(width / 2, height / 2, writer, font, fontSize);
            cell = new PdfPCell(competitorTable);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.addElement(competitorTable);
            mainTable.addCell(cell);
        }
        if (i % 2 != 0) {
            p = new Paragraph(" ", FontFactory.getFont(font, fontSize));
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
        }
        return mainTable;
    }
}
