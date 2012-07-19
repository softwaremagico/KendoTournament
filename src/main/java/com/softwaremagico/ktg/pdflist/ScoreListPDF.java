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
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Ranking;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.championship.DesignedGroup;
import com.softwaremagico.ktg.championship.DesignedGroups;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.statistics.TeamRanking;
import java.util.List;

/**
 *
 * @author jorge
 */
public class ScoreListPDF extends ParentList {

    private Tournament championship;
    List<TeamRanking> teamTopTen;

    public ScoreListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = LanguagePool.getTranslator("gui.xml");
    }

    private PdfPTable simpleTable(PdfPTable mainTable) {
        teamTopTen = KendoTournamentGenerator.getInstance().database.getTeamsOrderByScore(championship.name, false);

        mainTable.addCell(getCell(trans.returnTag("Team"), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.returnTag("fightsWon"), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.returnTag("duelsWon"), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.returnTag("histsWon"), 0, Element.ALIGN_CENTER));


        for (int i = 0; i < teamTopTen.size(); i++) {
            mainTable.addCell(getCell(teamTopTen.get(i).returnShortName(), 1));
            mainTable.addCell(getCell(teamTopTen.get(i).wonMatchs + "/" + teamTopTen.get(i).drawMatchs, 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell(teamTopTen.get(i).wonFights + "/" + teamTopTen.get(i).drawFights, 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell("" + teamTopTen.get(i).score, 1, Element.ALIGN_CENTER));
        }
        return mainTable;
    }

    private PdfPTable championshipTable(PdfPTable mainTable) {
        KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championship.name);
        KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship);
        KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));

        for (int l = 0; l < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); l++) {
            List<DesignedGroup> groups = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(l);
            boolean printTitle = false;
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).areFightsOver()) {
                    printTitle = true;
                    break;
                }
            }

            if (printTitle) {
                mainTable.addCell(getEmptyRow());
                mainTable.addCell(getEmptyRow());
                mainTable.addCell(getHeader1(trans.returnTag("Round") + " " + (l + 1) + ":", 0, Element.ALIGN_LEFT));

                for (int i = 0; i < groups.size(); i++) {
                    if (groups.get(i).areFightsOver()) {
                        mainTable.addCell(getEmptyRow());
                        String head = trans.returnTag("GroupString") + " " + (i + 1);
                        if (championship.fightingAreas > 1) {
                            head += " (" + trans.returnTag("FightArea") + " " + KendoTournamentGenerator.getInstance().returnShiaijo(groups.get(i).getShiaijo(KendoTournamentGenerator.getInstance().fightManager.getFights())) + ")";
                        }

                        mainTable.addCell(getHeader2(head, 0, Element.ALIGN_LEFT));
                        mainTable.addCell(getCell(trans.returnTag("Team"), 1, Element.ALIGN_CENTER));
                        mainTable.addCell(getCell(trans.returnTag("fightsWon"), 1, Element.ALIGN_CENTER));
                        mainTable.addCell(getCell(trans.returnTag("duelsWon"), 1, Element.ALIGN_CENTER));
                        mainTable.addCell(getCell(trans.returnTag("histsWon"), 1, Element.ALIGN_CENTER));


                        String winnerUndraw = KendoTournamentGenerator.getInstance().database.getWinnerInUndraws(championship.name, i, groups.get(i).teams);

                        for (int j = 0; j < groups.get(i).teams.size(); j++) {
                            /*
                             * Header of the teams
                             */
                            Team t = groups.get(i).getTeamInOrderOfScore(j, KendoTournamentGenerator.getInstance().fightManager.getFights(), false);

                            mainTable.addCell(getCell(t.returnName(), 0, Element.ALIGN_LEFT));
                            mainTable.addCell(getCell(Ranking.obtainWonFights(KendoTournamentGenerator.getInstance().fightManager.getFights(), t, groups.get(i).getLevel()) + "/" + Ranking.obtainDrawFights(KendoTournamentGenerator.getInstance().fightManager.getFights(), t, groups.get(i).getLevel()), 0, Element.ALIGN_CENTER));
                            mainTable.addCell(getCell(Ranking.obtainWonDuels(KendoTournamentGenerator.getInstance().fightManager.getFights(), t, groups.get(i).getLevel()) + "/" + Ranking.obtainDrawDuels(KendoTournamentGenerator.getInstance().fightManager.getFights(), t, groups.get(i).getLevel()), 0, Element.ALIGN_CENTER));

                            String score = "" + (int) (float) (Ranking.obtainHits(KendoTournamentGenerator.getInstance().fightManager.getFights(), t, groups.get(i).getLevel()));
                            if (winnerUndraw != null) {
                                if (winnerUndraw.equals(t.returnName())) {
                                    score += "*";
                                }
                            }
                            mainTable.addCell(getCell(score, 0, Element.ALIGN_CENTER));
                        }
                    }
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
