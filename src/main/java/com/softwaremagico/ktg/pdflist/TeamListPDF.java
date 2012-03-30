/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import java.awt.Color;
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
        Paragraph p;
        PdfPCell cell;
        //float[] widths = {0.50f, 0.50f};
        PdfPTable teamTable = new PdfPTable(1);
        teamTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        p = new Paragraph(t.returnShortName().toUpperCase(), FontFactory.getFont(font, fontSize + 1, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        teamTable.addCell(cell);

        for (int i = 0; i < t.getNumberOfMembers(0); i++) {
            try {
                if (t.getMember(i, 0).returnSurname().length() + t.getMember(i, 0).returnName().length() > 0) {
                    p = new Paragraph(t.getMember(i, 0).returnShortSurnameName(22), FontFactory.getFont(font, fontSize));
                } else {
                    p = new Paragraph(" ");
                }
            } catch (NullPointerException npe) {
                p = new Paragraph(" ");
            }
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            teamTable.addCell(cell);

        }

        p = new Paragraph(" ");
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        teamTable.addCell(cell);

        return teamTable;

    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;

        int cellNumber = 0;

        List<Team> listTeams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournament(championship.name, false);
        for (int i = 0; i < listTeams.size(); i += 2) {
            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            cell = new PdfPCell(teamTable(listTeams.get(i), font, fontSize));
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);
            cellNumber++;

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            if (i + 1 < listTeams.size()) {
                cell = new PdfPCell(teamTable(listTeams.get(i + 1), font, fontSize));
            } else {
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
            }
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
            cellNumber++;

            //Add a new page if is necessary...
            /*
             * if ((cellNumber > teamsByPage - (championship.teamSize)) && (i <
             * listTeams.size() - 1)) { mainTable.writeSelectedRows(0, -1, 0,
             * document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); cellNumber = 0; }
             */
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.05f, 0.40f, 0.05f, 0.40f, 0.05f};
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

        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 16, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(5);
        cell.setMinimumHeight(50);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        cell = new PdfPCell();
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(footerBorder);
        mainTable.addCell(cell);
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
