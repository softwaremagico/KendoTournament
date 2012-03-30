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
 *   Created on 23-ene-2009.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroup;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroups;
import com.softwaremagico.ktg.statistics.TeamRanking;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class ScoreListPDF extends ParentList {

    private Tournament championship;
    List<TeamRanking> teamTopTen;
    private final int border = 0;
    Translator trans = null;

    public ScoreListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
    }

    private PdfPTable simpleTable(Document document, PdfWriter writer, PdfPTable mainTable, float[] widths, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        int cellNumber = 0;
        int teamsByPage = 40;

        teamTopTen = KendoTournamentGenerator.getInstance().database.getTeamsOrderByScore(championship.name, false);
        /*
         * left margin of teams
         */
        p = new Paragraph(" ");
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);

        p = new Paragraph(trans.returnTag("Team", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(1);
        cell.setColspan(1);
        cell.setBackgroundColor(new Color(255, 255, 255));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        mainTable.addCell(cell);

        p = new Paragraph(trans.returnTag("fightsWon", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(1);
        cell.setColspan(1);
        cell.setBackgroundColor(new Color(255, 255, 255));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        mainTable.addCell(cell);

        p = new Paragraph(trans.returnTag("duelsWon", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(1);
        cell.setColspan(1);
        cell.setBackgroundColor(new Color(255, 255, 255));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        mainTable.addCell(cell);

        p = new Paragraph(trans.returnTag("histsWon", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(1);
        cell.setColspan(1);
        cell.setBackgroundColor(new Color(255, 255, 255));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        mainTable.addCell(cell);

        cellNumber += 2;

        /*
         * right margin of teams
         */
        p = new Paragraph(" ");
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);


        for (int i = 0; i < teamTopTen.size(); i++) {
            /*
             * left margin
             */
            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            p = new Paragraph(teamTopTen.get(i).returnShortName(), FontFactory.getFont(font, fontSize + 1, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);

            p = new Paragraph(teamTopTen.get(i).wonMatchs + "/" + teamTopTen.get(i).drawMatchs);
            cell = new PdfPCell(p);
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);

            p = new Paragraph(teamTopTen.get(i).wonFights + "/" + teamTopTen.get(i).drawFights);
            cell = new PdfPCell(p);
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);

            p = new Paragraph("" + teamTopTen.get(i).score);
            cell = new PdfPCell(p);
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);

            /*
             * right margin of teams
             */
            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            cellNumber++;

            //Add a new page if is necessary...
            /*
             * if ((cellNumber % teamsByPage >= teamsByPage - 1) && (i <
             * teamTopTen.size() - 1)) { mainTable.writeSelectedRows(0, -1, 0,
             * document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); }
             */
        }
        return mainTable;
    }

    private PdfPTable championshipTable(Document document, PdfWriter writer, PdfPTable mainTable, float[] widths, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        int cellNumber = 0;
        int teamsByPage = 45;

        KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(championship.name);
        KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));

        for (int l = 0; l < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); l++) {

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
            cellNumber++;

            /*
             * Header of the phase
             */
            p = new Paragraph(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (l + 1) + ":", FontFactory.getFont(font, fontSize + 10, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(6);
            cell.setBorderWidth(border);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            //cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);
            cellNumber += 3;

            List<DesignedGroup> groups = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(l);

            for (int i = 0; i < groups.size(); i++) {
                /*
                 * Header of the group
                 */

                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);
                cellNumber++;

                /*
                 * left margin
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                String head = trans.returnTag("GroupString", KendoTournamentGenerator.getInstance().language) + " " + (i + 1);
                if (championship.fightingAreas > 1) {
                    head += " (" + trans.returnTag("FightArea", KendoTournamentGenerator.getInstance().language) + " " + KendoTournamentGenerator.getInstance().shiaijosName[groups.get(i).getShiaijo(KendoTournamentGenerator.getInstance().fights.getFights())] + ")";
                }
                p = new Paragraph(head, FontFactory.getFont(font, fontSize + 2, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(4);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(new Color(255, 255, 255));
                mainTable.addCell(cell);
                cellNumber++;

                /*
                 * right margin
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                /*
                 * left margin of teams
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                p = new Paragraph(trans.returnTag("Team", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setColspan(1);
                cell.setBackgroundColor(new Color(255, 255, 255));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(cell);

                p = new Paragraph(trans.returnTag("fightsWon", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setColspan(1);
                cell.setBackgroundColor(new Color(255, 255, 255));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(cell);

                p = new Paragraph(trans.returnTag("duelsWon", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setColspan(1);
                cell.setBackgroundColor(new Color(255, 255, 255));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(cell);

                p = new Paragraph(trans.returnTag("histsWon", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize - 1, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setColspan(1);
                cell.setBackgroundColor(new Color(255, 255, 255));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(cell);

                cellNumber += 2;

                /*
                 * right margin of teams
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                String winnerUndraw = KendoTournamentGenerator.getInstance().database.getWinnerInUndraws(championship.name, i, groups.get(i).teams);

                for (int j = 0; j < groups.get(i).teams.size(); j++) {
                    /*
                     * Header of the teams
                     */
                    Team t = groups.get(i).getTeamInOrderOfScore(j, KendoTournamentGenerator.getInstance().fights.getFights(), false);

                    /*
                     * left margin
                     */
                    p = new Paragraph(" ");
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(border);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    mainTable.addCell(cell);

                    p = new Paragraph(t.returnName(), FontFactory.getFont(font, fontSize, Font.BOLD));
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(1);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(new Color(255, 255, 255));
                    mainTable.addCell(cell);


                    Ranking ranking = new Ranking();

                    p = new Paragraph(ranking.obtainWonFights(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()) + "/" + ranking.obtainDrawFights(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()));
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(1);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(new Color(255, 255, 255));
                    mainTable.addCell(cell);

                    p = new Paragraph(ranking.obtainWonDuels(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()) + "/" + ranking.obtainDrawDuels(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()));
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(1);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(new Color(255, 255, 255));
                    mainTable.addCell(cell);

                    String parag = "" + (int) (float) (ranking.obtainHits(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()));
                    if (winnerUndraw != null) {
                        if (winnerUndraw.equals(t.returnName())) {
                            parag += "*";
                        }
                    }
                    p = new Paragraph(parag);
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(1);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(new Color(255, 255, 255));
                    mainTable.addCell(cell);

                    /*
                     * right margin of teams
                     */
                    p = new Paragraph(" ");
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(border);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    mainTable.addCell(cell);


                    cellNumber++;
                    //Add a new page if it is necessary...
                    /*
                     * if ((cellNumber >= teamsByPage) && (j <
                     * groups.get(i).teams.size() - 1)) {
                     * mainTable.writeSelectedRows(0, -1, 0,
                     * document.getPageSize().getHeight() - 40,
                     * writer.getDirectContent()); mainTable.flushContent();
                     * document.newPage(); AddBackGroundImage(document,
                     * Path.returnBackgroundPath()); cellNumber = 0; }
                     */
                }

                //Add a new page if it is necessary...
                /*
                 * if ((cellNumber >= teamsByPage) && (i < groups.size() - 1)) {
                 * mainTable.writeSelectedRows(0, -1, 0,
                 * document.getPageSize().getHeight() - 40,
                 * writer.getDirectContent()); mainTable.flushContent();
                 * document.newPage(); AddBackGroundImage(document,
                 * Path.returnBackgroundPath()); cellNumber = 0; }
                 */
            }
            //Add a new page if it is necessary...
            /*
             * if ((cellNumber >= teamsByPage - 4) && (l <
             * KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels()
             * - 1)) { mainTable.writeSelectedRows(0, -1, 0,
             * document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); cellNumber = 0; }
             */
        }

        return mainTable;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        if (championship.mode.equals("simple")) {
            mainTable = simpleTable(document, writer, mainTable, getTableWidths(), font, fontSize - 2);
        } else {
            mainTable = championshipTable(document, writer, mainTable, getTableWidths(), font, fontSize - 2);
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.09f, 0.31f, 0.15f, 0.15f, 0.15f, 0.05f};
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
        cell.setColspan(6);
        cell.setBorderWidth(headerBorder);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //cell.setBackgroundColor(new Color(255, 255, 255));
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
        return "scoreListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "scoreListBad";
    }
}
