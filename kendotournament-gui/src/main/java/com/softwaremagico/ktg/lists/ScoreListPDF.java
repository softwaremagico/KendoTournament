package com.softwaremagico.ktg.lists;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.tournament.ScoreOfTeam;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.UndrawPool;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoreListPDF extends ParentList {

    private Tournament tournament;
    private ITournamentManager tournamentManager;
    private List<ScoreOfTeam> teamTopTen;

    public ScoreListPDF(Tournament tournament) {
        this.tournament = tournament;
        this.tournamentManager = TournamentManagerFactory.getManager(tournament);
        trans = LanguagePool.getTranslator("gui.xml");
    }

    private PdfPTable simpleTable(PdfPTable mainTable) {
        try {
            teamTopTen = Ranking.getTeamsScoreRanking(FightPool.getInstance().get(tournament));
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
            teamTopTen = new ArrayList<>();
        }

        mainTable.addCell(getCell(trans.getTranslatedText("Team"), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.getTranslatedText("fightsWon"), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.getTranslatedText("duelsWon"), 0, Element.ALIGN_CENTER));
        mainTable.addCell(getCell(trans.getTranslatedText("histsWon"), 0, Element.ALIGN_CENTER));

        for (int i = 0; i < teamTopTen.size(); i++) {
            mainTable.addCell(getCell(teamTopTen.get(i).getTeam().getShortName(), 1));
            mainTable.addCell(getCell(teamTopTen.get(i).getWonFights() + "/" + teamTopTen.get(i).getDrawFights(), 1,
                    Element.ALIGN_CENTER));
            mainTable.addCell(getCell(teamTopTen.get(i).getWonDuels() + "/" + teamTopTen.get(i).getDrawDuels(), 1,
                    Element.ALIGN_CENTER));
            mainTable.addCell(getCell("" + teamTopTen.get(i).getHits(), 1, Element.ALIGN_CENTER));
        }
        return mainTable;
    }

    private PdfPTable championshipTable(PdfPTable mainTable) {
        for (int l = 0; l < tournamentManager.getNumberOfLevels(); l++) {
            List<TGroup> groups = tournamentManager.getGroups(l);
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
                mainTable.addCell(getHeader1(trans.getTranslatedText("Round") + " " + (l + 1) + ":", 0,
                        Element.ALIGN_LEFT));

                for (int i = 0; i < groups.size(); i++) {
                    if (groups.get(i).areFightsOver()) {
                        mainTable.addCell(getEmptyRow());
                        String head = trans.getTranslatedText("GroupString") + " " + (i + 1);
                        if (tournament.getFightingAreas() > 1) {
                            head += " (" + trans.getTranslatedText("FightArea") + " "
                                    + Tournament.getFightAreaName(groups.get(i).getFightArea()) + ")";
                        }

                        mainTable.addCell(getHeader2(head, 0, Element.ALIGN_LEFT));
                        mainTable.addCell(getCell(trans.getTranslatedText("Team"), 1, Element.ALIGN_CENTER));
                        mainTable.addCell(getCell(trans.getTranslatedText("fightsWon"), 1, Element.ALIGN_CENTER));
                        mainTable.addCell(getCell(trans.getTranslatedText("duelsWon"), 1, Element.ALIGN_CENTER));
                        mainTable.addCell(getCell(trans.getTranslatedText("histsWon"), 1, Element.ALIGN_CENTER));

                        List<Team> winnersUndraw = new ArrayList<>();
                        try {
                            winnersUndraw = UndrawPool.getInstance()
                                    .getWinners(tournament, groups.get(i).getLevel(), i);
                        } catch (SQLException ex) {
                            AlertManager.showSqlErrorMessage(ex);
                        }

                        for (int j = 0; j < groups.get(i).getTeams().size(); j++) {
                            /*
                             * Header of the teams
                             */
                            Ranking ranking = new Ranking(groups.get(i).getFights());
                            ScoreOfTeam scoreOfTeam = ranking.getScoreOfTeam(j);

                            mainTable.addCell(getCell(scoreOfTeam.getTeam().getName(), 0, Element.ALIGN_LEFT));
                            mainTable.addCell(getCell(scoreOfTeam.getWonFights() + "/" + scoreOfTeam.getDrawFights(),
                                    0, Element.ALIGN_CENTER));
                            mainTable.addCell(getCell(scoreOfTeam.getWonDuels() + "/" + scoreOfTeam.getDrawDuels(), 0,
                                    Element.ALIGN_CENTER));

                            String score = scoreOfTeam.getHits() + "";
                            if (winnersUndraw != null) {
                                if (winnersUndraw.contains(scoreOfTeam.getTeam())) {
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
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
            String font, int fontSize) {
        if (tournament.getType().equals(TournamentType.SIMPLE)) {
            simpleTable(mainTable);
        } else {
            championshipTable(mainTable);
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
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
            String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;

        p = new Paragraph(tournament.getName(), FontFactory.getFont(font, fontSize + 15, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(headerBorder);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // cell.setBackgroundColor(new Color(255, 255, 255));
        mainTable.addCell(cell);
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
            String font, int fontSize) {
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
