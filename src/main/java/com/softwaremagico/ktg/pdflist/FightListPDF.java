/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.championship.DesignedGroup;
import com.softwaremagico.ktg.championship.DesignedGroups;
import com.softwaremagico.ktg.language.Translator;
import java.util.List;

/**
 *
 * @author jorge
 */
public class FightListPDF extends ParentList {

    private Tournament championship;

    public FightListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
    }

    public PdfPTable fightTable(Fight f, String font, int fontSize) {
        float[] widths = getTableWidths();
        PdfPTable fightTable = new PdfPTable(widths);
        fightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        fightTable.addCell(getHeader3(f.team1.returnName() + " Vs " + f.team2.returnName(), 0));

        for (int i = 0; i < f.team1.getNumberOfMembers(f.level); i++) {
            fightTable.addCell(getCell(f.team1.getMember(i, f.level).returnSurnameNameIni(), 1, Element.ALIGN_LEFT));
            fightTable.addCell(getEmptyCell());
            fightTable.addCell(getCell(f.team2.getMember(i, f.level).returnSurnameNameIni(), 1, Element.ALIGN_RIGHT));
        }

        return fightTable;
    }

    private PdfPTable simpleTable(PdfPTable mainTable) {
        PdfPCell cell;
        Paragraph p;
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        for (int i = 0; i < championship.fightingAreas; i++) {
            List<Fight> fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentNameAndFightArea(championship.name, i);
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getHeader2("Shiaijo: " + KendoTournamentGenerator.getInstance().shiaijosName[i], 0));

            for (int j = 0; j < fights.size(); j++) {
                cell = new PdfPCell(fightTable(fights.get(j), font, fontSize));
                cell.setBorderWidth(0);
                cell.setColspan(3);
                //if (fights.get(j).isOver()) {
                //    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                //} else {
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                //}
                //cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                mainTable.addCell(cell);
            }
        }

        return mainTable;
    }

    private PdfPTable championshipTable(PdfPTable mainTable) {
        PdfPCell cell;

        KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championship.name);
        KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));

        for (int l = 0; l < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); l++) {
            /*
             * Header of the phase
             */
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getHeader1(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (l + 1) + ":", 0, Element.ALIGN_LEFT));

            List<DesignedGroup> groups = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(l);

            for (int i = 0; i < groups.size(); i++) {
                mainTable.addCell(getEmptyRow());
                mainTable.addCell(getHeader2(trans.returnTag("GroupString", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + " (" + trans.returnTag("FightArea", KendoTournamentGenerator.getInstance().language) + " " + KendoTournamentGenerator.getInstance().shiaijosName[groups.get(i).getShiaijo(KendoTournamentGenerator.getInstance().fightManager.getFights())] + ")", 0));

                for (int j = 0; j < KendoTournamentGenerator.getInstance().fightManager.size(); j++) {
                    if (groups.get(i).isFightOfGroup(KendoTournamentGenerator.getInstance().fightManager.get(j))) {

                        cell = new PdfPCell(fightTable(KendoTournamentGenerator.getInstance().fightManager.get(j), font, fontSize));
                        cell.setBorderWidth(1);
                        cell.setColspan(3);
                        //if (KendoTournamentGenerator.getInstance().fightManager.get(j).isOver()) {
                        //    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                        // } else {
                        cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                        //}
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        mainTable.addCell(cell);
                    }
                }
            }
        }
        return mainTable;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        if (championship.mode.equals("simple")) {
            simpleTable(mainTable);
        } else {
            championshipTable(mainTable);
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.40f, 0.10f, 0.40f};
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
        cell.setColspan(getTableWidths().length);
        cell.setMinimumHeight(50);
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
        return "fightsListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "fightsListBad";
    }
}
