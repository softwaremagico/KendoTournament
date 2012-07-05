/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.Club;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author jorge
 */
public class ClubListPDF extends ParentList {

    private Tournament championship;

    public ClubListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
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

        List<Club> clubs = KendoTournamentGenerator.getInstance().database.getAllClubs();

        for (int i = 0; i < clubs.size(); i++) {
            List<CompetitorWithPhoto> competitors = KendoTournamentGenerator.getInstance().database.searchCompetitorsByClubAndTournament(clubs.get(i).returnName(), championship.name, false, false);

            if (competitors.size() > 0) {
                if (!firstClub) {
                    mainTable.addCell(getEmptyRow());
                } else {
                    firstClub = false;
                }
                /**
                 * Club
                 */
                String text = clubs.get(i).returnName();
                if (clubs.get(i).returnCountry().length() > 1) {
                    text += " (" + clubs.get(i).returnCountry() + ")";
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
                mainTable.addCell(getCell(KendoTournamentGenerator.getInstance().getAvailableRoles().getTraduction(KendoTournamentGenerator.getInstance().database.getTagRole(championship, competitors.get(j))), 1, 1, color));

                cellNumber++;
            }
        }
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        p = new Paragraph(championship.name + "\n ", FontFactory.getFont(font, fontSize + 15, Font.BOLD));
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
