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
import com.softwaremagico.ktg.Score;
import com.softwaremagico.ktg.Tournament;
import java.util.List;

/**
 *
 * @author jorge
 */
public class SummaryPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;
    private int useOnlyShiaijo = -1;
    protected boolean showfinishedFights = true; //If true, only show finished fights, if false only show not finished fights.
    protected boolean showAll = true; //If true, show finished and not finished fights;

    public SummaryPDF(Tournament tmp_championship, int shiaijo) {
        championship = tmp_championship;
        useOnlyShiaijo = shiaijo;
    }

    protected String getDrawFight(Fight f, int duel) {
        //Draw Fights
        String draw;
        if (f.duels.get(duel).winner() == 0 && f.isOver()) {
            draw = "" + Score.DRAW.getAbbreviature();
        } else {
            draw = "" + Score.EMPTY.getAbbreviature();
        }
        return draw;
    }

    protected String getFaults(Fight f, int duel, boolean leftTeam) {
        String faultSimbol;
        int faults;
        if (leftTeam) {
            faults = f.duels.get(duel).faultsCompetitorA;
        } else {
            faults = f.duels.get(duel).faultsCompetitorB;
        }
        if (faults > 0) {
            faultSimbol = "" + Score.FAULT.getAbbreviature();
        } else {
            faultSimbol = "" + Score.EMPTY.getAbbreviature();
        }
        return faultSimbol;
    }

    protected String getScore(Fight f, int duel, int score, boolean leftTeam) {
        if (leftTeam) {
            return f.duels.get(duel).hitsFromCompetitorA.get(score).getAbbreviature() + "";
        } else {
            return f.duels.get(duel).hitsFromCompetitorB.get(score).getAbbreviature() + "";
        }
    }

    private PdfPTable fightTable(Fight f, boolean first) throws DocumentException {
        PdfPTable Table;
        Table = new PdfPTable(getTableWidths());

        if (!first) {
            Table.addCell(getEmptyRow());
        }

        //Team1
        Table.addCell(getHeader3(f.team1.returnName(), 0, 4));

        //Separation Draw Fights
        Table.addCell(getEmptyCell());

        //Team2
        Table.addCell(getHeader3(f.team2.returnName(), 0, 4));

        for (int i = 0; i < f.team1.getNumberOfMembers(f.level); i++) {
            //Team 1
            Table.addCell(getCell(f.team1.getMember(i, f.level).getSurnameNameIni(), 1, 1, Element.ALIGN_LEFT));

            //Faults
            Table.addCell(getCell(getFaults(f, i, true), 1, 1, Element.ALIGN_CENTER));

            //Points
            Table.addCell(getCell(getScore(f, i, 1, true), 1, 1, Element.ALIGN_CENTER));
            Table.addCell(getCell(getScore(f, i, 0, true), 1, 1, Element.ALIGN_CENTER));

            Table.addCell(getCell(getDrawFight(f, i), 1, Element.ALIGN_CENTER));

            //Points Team 2
            Table.addCell(getCell(getScore(f, i, 0, false), 1, 1, Element.ALIGN_CENTER));
            Table.addCell(getCell(getScore(f, i, 1, false), 1, 1, Element.ALIGN_CENTER));

            //Faults
            Table.addCell(getCell(getFaults(f, i, false), 1, 1, Element.ALIGN_CENTER));

            //Team 2
            Table.addCell(getCell(f.team2.getMember(i, f.level).getSurnameNameIni(), 1, 1, Element.ALIGN_RIGHT));
        }
        Table.addCell(getEmptyRow());

        return Table;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;

        boolean first = true;
        int lastLevel = -1;

        List<Fight> fights;
        if (useOnlyShiaijo < 0) {
            fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name);
        } else {
            fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentNameAndFightArea(championship.name, useOnlyShiaijo);
        }


        for (int i = 0; i < fights.size(); i++) {
            if ((fights.get(i).isOver() == showfinishedFights) || (showAll)) {
                /*
                 * Header of the phase
                 */
                if (lastLevel != fights.get(i).level && !championship.mode.equals("simple")) {
                    mainTable.addCell(getEmptyRow());
                    mainTable.addCell(getHeader1(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (fights.get(i).level + 1) + ":", 0, Element.ALIGN_LEFT));
                    lastLevel = fights.get(i).level;
                }

                try {
                    cell = new PdfPCell(fightTable(fights.get(i), first));
                } catch (DocumentException ex) {
                    cell = new PdfPCell();
                    KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                }
                cell.setBorderWidth(border);
                cell.setColspan(getTableWidths().length);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                first = false;
            }
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.29f, 0.03f, 0.08f, 0.08f, 0.04f, 0.08f, 0.08f, 0.03f, 0.29f};
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
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(headerBorder);
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
        return "SummaryListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "SummaryListBad";
    }
}
