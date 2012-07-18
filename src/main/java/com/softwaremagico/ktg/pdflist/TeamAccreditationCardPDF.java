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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import java.io.IOException;

public class TeamAccreditationCardPDF extends PdfDocument {

    Team team;
    private final int border = 1;
    Tournament competition;

    public TeamAccreditationCardPDF(Team tmp_team, Tournament tmp_competition) throws Exception {
        team = tmp_team;
        competition = tmp_competition;
    }

    protected void createPagePDF(Document document, PdfWriter writer, String font) throws Exception {
        //addBackGroundImage(document, Path.returnBackgroundPath());
        PdfPTable table = pageTable(document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        table.setWidthPercentage(100);
        document.add(table);

    }

    public PdfPTable pageTable(float width, float height, PdfWriter writer, String font, int fontSize) throws IOException, BadElementException, Exception {
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
            competitorTable.setTableEvent(new PdfDocument.TableBgEvent());
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

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A4;
    }

    @Override
    protected String fileCreatedOkTag() {
        return "teamOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "teamBad";
    }
}
