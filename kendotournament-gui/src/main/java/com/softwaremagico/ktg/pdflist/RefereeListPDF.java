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
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.RolePool;
import com.softwaremagico.ktg.gui.AlertManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge
 */
public class RefereeListPDF extends ParentList {

    private Tournament tournament;
    private final int border = 0;

    public RefereeListPDF(Tournament tmp_championship) {
        tournament = tmp_championship;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        Paragraph p;

        mainTable.addCell(getHeader2(tournament.getName(), 0));
        List<RegisteredPerson> listReferee = new ArrayList<>();
        try {
            listReferee = RolePool.getInstance().getPeople(tournament, RoleTag.refereeRole);
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        for (int i = 0; i < listReferee.size(); i++) {
            mainTable.addCell(getCell(listReferee.get(i).getSurnameName() + " (" + listReferee.get(i).getClub() + ")", 0));
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {1};
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

        p = new Paragraph(trans.getTranslatedText("RefereeTitle"), FontFactory.getFont(font, fontSize + 26, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(getTableWidths().length);
        //cell.setMinimumHeight(50);
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
        return "refereeListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "refereeListBad";
    }
}
