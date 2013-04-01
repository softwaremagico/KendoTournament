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
import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.ClubPool;
import com.softwaremagico.ktg.database.RolePool;
import com.softwaremagico.ktg.language.LanguagePool;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author jorge
 */
public class ClubListPDF extends ParentList {

    private Tournament tournament;

    public ClubListPDF(Tournament tournament) {
        this.tournament = tournament;
        trans = LanguagePool.getTranslator("gui.xml");
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.60f, 0.30f};
        return widths;
    }

    @Override
    public void setTablePropierties(PdfPTable mainTable) {
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        int cellNumber = 0;
        boolean firstClub = true;

        List<Club> clubs = ClubPool.getInstance().getAll();

        for (int i = 0; i < clubs.size(); i++) {
            List<RegisteredPerson> competitors = RolePool.getInstance().getPeople(tournament, clubs.get(i));

            if (competitors.size() > 0) {
                if (!firstClub) {
                    mainTable.addCell(getEmptyRow());
                } else {
                    firstClub = false;
                }
                /**
                 * Club
                 */
                String text = clubs.get(i).getName();
                if (clubs.get(i).getCountry().length() > 1) {
                    text += " (" + clubs.get(i).getCountry() + ")";
                }
                mainTable.addCell(getHeader2(text, 0));
            }

            for (int j = 0; j < competitors.size(); j++) {
                Color color;
                if (cellNumber % 2 == 0) {
                    color = new Color(255, 255, 255);
                } else {
                    color = new Color(230, 230, 230);
                }
                mainTable.addCell(getCell(competitors.get(j).getSurnameName() + " (" + competitors.get(j).getId() + ")", 1, Element.ALIGN_LEFT, color));
                mainTable.addCell(getCell(KendoTournamentGenerator.getInstance().getAvailableRoles().getTranslation(RolePool.getInstance().getRole(tournament, competitors.get(j)).getDatabaseTag()), 1, 1, color));

                cellNumber++;
            }
        }
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        p = new Paragraph(tournament.getName() + "\n ", FontFactory.getFont(font, fontSize + 15, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(headerBorder);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //cell.setBackgroundColor(new Color(255, 255, 255));
        mainTable.addCell(cell);
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        mainTable.addCell(getEmptyRow());
    }

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A4;
    }

    @Override
    protected String fileCreatedOkTag() {
        return "ClubListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "ClubListBad";
    }
}
