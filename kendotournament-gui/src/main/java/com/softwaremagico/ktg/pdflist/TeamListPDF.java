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
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.gui.AlertManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamListPDF extends ParentList {

    private Tournament tournament;
    private final int border = 0;

    public TeamListPDF(Tournament tmp_championship) {
        tournament = tmp_championship;
    }

    public PdfPTable teamTable(Team t, String font, int fontSize) {
        PdfPTable teamTable = new PdfPTable(1);

        teamTable.addCell(getHeader4(t.getShortName().toUpperCase(), 0));

        for (int i = 0; i < t.getNumberOfMembers(0); i++) {
            String member;
            try {
                if (t.getMember(i, 0).getSurname().length() + t.getMember(i, 0).getName().length() > 0) {
                    member = t.getMember(i, 0).getSurnameName();
                } else {
                    member = " ";
                }
            } catch (NullPointerException npe) {
                member = " ";
            }
            teamTable.addCell(getCell(member));
        }

        return teamTable;

    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;

        mainTable.addCell(getEmptyRow());

        List<Team> listTeams = new ArrayList<>();
        try {
            listTeams = TeamPool.getInstance().get(tournament);
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        for (int i = 0; i < listTeams.size(); i++) {

            cell = new PdfPCell(teamTable(listTeams.get(i), font, fontSize));
            cell.setColspan(1);
            mainTable.addCell(cell);

            if (i % 2 == 0) {
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                mainTable.addCell(cell);
            }
        }
        mainTable.completeRow();
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.46f, 0.08f, 0.46f};
        return widths;
    }

    @Override
    public void setTablePropierties(PdfPTable mainTable) {
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;

        p = new Paragraph(tournament.getName(), FontFactory.getFont(font, fontSize + 15, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(getTableWidths().length);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
        return "teamsListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "teamsListBad";
    }
}
