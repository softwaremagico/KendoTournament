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
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Ranking;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroup;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroups;
import com.softwaremagico.ktg.statistics.TeamRanking;
import java.util.List;

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

    private PdfPTable simpleTable(PdfPTable mainTable) {
        teamTopTen = KendoTournamentGenerator.getInstance().database.getTeamsOrderByScore(championship.name, false);

        mainTable.addCell(getCell(trans.returnTag("Team", KendoTournamentGenerator.getInstance().language), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.returnTag("fightsWon", KendoTournamentGenerator.getInstance().language), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.returnTag("duelsWon", KendoTournamentGenerator.getInstance().language), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.returnTag("histsWon", KendoTournamentGenerator.getInstance().language), 0, Element.ALIGN_CENTER));


        for (int i = 0; i < teamTopTen.size(); i++) {
            mainTable.addCell(getCell(teamTopTen.get(i).returnShortName(), 1));
            mainTable.addCell(getCell(teamTopTen.get(i).wonMatchs + "/" + teamTopTen.get(i).drawMatchs, 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell(teamTopTen.get(i).wonFights + "/" + teamTopTen.get(i).drawFights, 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell("" + teamTopTen.get(i).score, 1, Element.ALIGN_CENTER));
        }
        return mainTable;
    }

    private PdfPTable championshipTable(PdfPTable mainTable) {
        PdfPCell cell;
        Paragraph p;
        int cellNumber = 0;

        KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(championship.name);
        KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));

        for (int l = 0; l < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); l++) {
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getHeader1(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (l + 1) + ":", 0, Element.ALIGN_LEFT));

            List<DesignedGroup> groups = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(l);

            for (int i = 0; i < groups.size(); i++) {

                mainTable.addCell(getEmptyRow());
                String head = trans.returnTag("GroupString", KendoTournamentGenerator.getInstance().language) + " " + (i + 1);
                if (championship.fightingAreas > 1) {
                    head += " (" + trans.returnTag("FightArea", KendoTournamentGenerator.getInstance().language) + " " + KendoTournamentGenerator.getInstance().shiaijosName[groups.get(i).getShiaijo(KendoTournamentGenerator.getInstance().fights.getFights())] + ")";
                }

                mainTable.addCell(getHeader2(head, 0, Element.ALIGN_LEFT));
                mainTable.addCell(getCell(trans.returnTag("Team", KendoTournamentGenerator.getInstance().language), 1, Element.ALIGN_CENTER));
                mainTable.addCell(getCell(trans.returnTag("fightsWon", KendoTournamentGenerator.getInstance().language), 1, Element.ALIGN_CENTER));
                mainTable.addCell(getCell(trans.returnTag("duelsWon", KendoTournamentGenerator.getInstance().language), 1, Element.ALIGN_CENTER));
                mainTable.addCell(getCell(trans.returnTag("histsWon", KendoTournamentGenerator.getInstance().language), 1, Element.ALIGN_CENTER));


                String winnerUndraw = KendoTournamentGenerator.getInstance().database.getWinnerInUndraws(championship.name, i, groups.get(i).teams);

                for (int j = 0; j < groups.get(i).teams.size(); j++) {
                    /*
                     * Header of the teams
                     */
                    Team t = groups.get(i).getTeamInOrderOfScore(j, KendoTournamentGenerator.getInstance().fights.getFights(), false);

                    mainTable.addCell(getCell(t.returnName(), 0, Element.ALIGN_LEFT));
                    mainTable.addCell(getCell(Ranking.obtainWonFights(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()) + "/" + Ranking.obtainDrawFights(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()), 0, Element.ALIGN_CENTER));
                    mainTable.addCell(getCell(Ranking.obtainWonDuels(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()) + "/" + Ranking.obtainDrawDuels(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()), 0, Element.ALIGN_CENTER));

                    String score = "" + (int) (float) (Ranking.obtainHits(KendoTournamentGenerator.getInstance().fights.getFights(), t, groups.get(i).getLevel()));
                    if (winnerUndraw != null) {
                        if (winnerUndraw.equals(t.returnName())) {
                            score += "*";
                        }
                    }
                    mainTable.addCell(getCell(score, 0, Element.ALIGN_CENTER));
                }
            }
        }

        return mainTable;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        if (championship.mode.equals("simple")) {
            mainTable = simpleTable(mainTable);
        } else {
            mainTable = championshipTable(mainTable);
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.40f, 0.20f, 0.20f, 0.20f};
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
        return "scoreListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "scoreListBad";
    }
}
