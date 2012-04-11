/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import java.util.List;

/**
 *
 * @author jorge
 */
public class TeamListPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;

    public TeamListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
    }

    public PdfPTable teamTable(Team t, String font, int fontSize) {
        PdfPTable teamTable = new PdfPTable(1);

        teamTable.addCell(getHeader4(t.returnShortName().toUpperCase(), 0));

        for (int i = 0; i < t.getNumberOfMembers(0); i++) {
            String member;
            try {
                if (t.getMember(i, 0).returnSurname().length() + t.getMember(i, 0).returnName().length() > 0) {
                    member = t.getMember(i, 0).returnSurnameName();
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

        List<Team> listTeams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournament(championship.name, false);
        for (int i = 0; i < listTeams.size(); i++) {

            cell = new PdfPCell(teamTable(listTeams.get(i), font, fontSize));
            cell.setBorderWidth(1);
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

        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 15, Font.BOLD));
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
