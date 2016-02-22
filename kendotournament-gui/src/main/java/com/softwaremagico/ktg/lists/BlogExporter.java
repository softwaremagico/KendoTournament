package com.softwaremagico.ktg.lists;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2015 Softwaremagico
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.ClubPool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.RolePool;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.ScoreOfCompetitor;
import com.softwaremagico.ktg.tournament.ScoreOfTeam;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;

public class BlogExporter {
	private final static String NEW_LINE = "&nbsp;\n";
	private static ITranslator trans = LanguagePool.getTranslator("gui.xml");

	public static String getWordpressFormat(Tournament tournament) {
		StringBuilder stringBuilder = new StringBuilder();
		addTitle(stringBuilder, tournament);
		addInformation(stringBuilder, tournament);
		addCompetitors(stringBuilder, tournament);
		addScoreTables(stringBuilder, tournament);
		if (tournament.getTeamSize() > 1) {
			addTeamClassificationTable(stringBuilder, tournament);
		}
		addCompetitorClassificationTable(stringBuilder, tournament);
		return stringBuilder.toString();
	}

	/**
	 * Header of the document
	 * 
	 * @param stringBuilder
	 * @param tournament
	 */
	private static void addTitle(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append("<h2>" + tournament.getName() + "</h2>\n");
	}

	/**
	 * Extra information of the tournament
	 * 
	 * @param stringBuilder
	 * @param tournament
	 */
	private static void addInformation(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append("Tipo: " + trans.getTranslatedText(tournament.getType().getSqlName()));
		if (tournament.getTeamSize() > 1) {
			stringBuilder.append(" (" + trans.getTranslatedText("Teams") + " " + tournament.getTeamSize() + ").\n");
		} else {
			stringBuilder.append(".\n");
		}
		stringBuilder.append(NEW_LINE);
	}

	/**
	 * List of all people that goes to the championship.
	 * 
	 * @param stringBuilder
	 */
	private static void addCompetitors(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append("<h4>" + trans.getTranslatedText("ClubListMenuItem") + "</h4>");
		try {
			List<Club> clubs = ClubPool.getInstance().getSorted();
			List<List<String>> rows = new ArrayList<>();
			for (Club club : clubs) {
				List<RegisteredPerson> competitors = RolePool.getInstance().getPeople(tournament, club);
				for (RegisteredPerson competitor : competitors) {
					List<String> columns = new ArrayList<>();

					columns.add(competitor.getSurnameName());
					columns.add(RolePool.getInstance().getRoleTags().getTranslation(RolePool.getInstance().getRole(tournament, competitor).getDatabaseTag()));

					rows.add(columns);
				}
			}
			createTable(stringBuilder, rows);
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
	}

	/**
	 * Create the tables with the results of the fights.
	 * 
	 * @param stringBuilder
	 */
	private static void addScoreTables(StringBuilder stringBuilder, Tournament tournament) {
		ITournamentManager tournamentManager = TournamentManagerFactory.getManager(tournament);
		stringBuilder.append(NEW_LINE + "<h4>" + trans.getTranslatedText("FightListMenuItem") + "</h4>");
		for (int l = 0; l < tournamentManager.getNumberOfLevels(); l++) {
			if (tournamentManager.getLevel(l) == null || tournamentManager.getLevel(l).hasFightsAssigned()) {
				List<TGroup> groups = tournamentManager.getGroups(l);

				List<Fight> fights = new ArrayList<>();
				try {
					fights = FightPool.getInstance().get(tournament);
				} catch (SQLException ex) {
					AlertManager.showSqlErrorMessage(ex);
				}
				// Separate by groups
				for (int i = 0; i < groups.size(); i++) {
					if (groups.size() > 1) {
						stringBuilder.append("<h4>" + trans.getTranslatedText("GroupString") + " " + (i + 1) + " ("
								+ trans.getTranslatedText("FightAreaNoDots") + " " + Tournament.getFightAreaName(groups.get(i).getFightArea()) + ")"
								+ "</h4>\n");
					}
					// For each fight
					for (Fight fight : fights) {
						List<List<String>> rows = new ArrayList<>();
						if (groups.get(i).isFightOfGroup(fight)) {
							stringBuilder.append(NEW_LINE + "<h5>" + fight.getTeam1().getName() + " - " + fight.getTeam2().getName() + "</h5>\n");
							// Create for each competitor
							for (int teamMember = 0; teamMember < fight.getTournament().getTeamSize(); teamMember++) {
								List<String> columns = new ArrayList<>();
								// Team 1
								RegisteredPerson competitor = fight.getTeam1().getMember(teamMember, fight.getIndex());
								String name = "";
								if (competitor != null) {
									name = competitor.getSurnameName();
								}
								columns.add(name);
								columns.add(getFaults(fight, teamMember, true));
								columns.add(getScore(fight, teamMember, 1, true));
								columns.add(getScore(fight, teamMember, 0, true));
								columns.add(getDrawFight(fight, teamMember));
								columns.add(getScore(fight, teamMember, 0, false));
								columns.add(getScore(fight, teamMember, 1, false));
								columns.add(getFaults(fight, teamMember, false));

								// Team 2
								competitor = fight.getTeam2().getMember(teamMember, fight.getIndex());
								name = "";
								if (competitor != null) {
									name = competitor.getSurnameName();
								}
								columns.add(name);
								rows.add(columns);
							}

							createTable(stringBuilder, rows);
						}
					}
				}
			}
		}
	}

	private static void addTeamClassificationTable(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append(NEW_LINE + "<h4>" + trans.getTranslatedText("ScoreMonitorMenuItem") + "</h4>\n");
		List<List<String>> rows = new ArrayList<>();
		// Header
		List<String> columns = new ArrayList<>();
		columns.add("<b>" + trans.getTranslatedText("Team") + "</b>");
		columns.add("<b>" + trans.getTranslatedText("fightsWon") + "</b>");
		columns.add("<b>" + trans.getTranslatedText("duelsWon") + "</b>");
		columns.add("<b>" + trans.getTranslatedText("histsWon") + "</b>");
		rows.add(columns);

		List<ScoreOfTeam> teamTopTen;
		try {
			teamTopTen = Ranking.getTeamsScoreRanking(FightPool.getInstance().get(tournament));
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
			teamTopTen = new ArrayList<>();
		}

		for (int i = 0; i < teamTopTen.size(); i++) {
			columns = new ArrayList<>();
			columns.add(teamTopTen.get(i).getTeam().getShortName());
			columns.add(teamTopTen.get(i).getWonFights() + "/" + teamTopTen.get(i).getDrawFights());
			columns.add(teamTopTen.get(i).getWonDuels() + "/" + teamTopTen.get(i).getDrawDuels());
			columns.add("" + teamTopTen.get(i).getHits());
			rows.add(columns);
		}
		createTable(stringBuilder, rows);
	}

	private static void addCompetitorClassificationTable(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append(NEW_LINE + "<h4>" + trans.getTranslatedText("GeneralClassification") + "</h4>\n");
		List<List<String>> rows = new ArrayList<>();
		// Header
		List<String> columns = new ArrayList<>();
		columns.add("<b>" + trans.getTranslatedText("CompetitorMenu") + "</b>");
		columns.add("<b>" + trans.getTranslatedText("duelsWon") + "</b>");
		columns.add("<b>" + trans.getTranslatedText("histsWon") + "</b>");
		rows.add(columns);

		List<ScoreOfCompetitor> competitorTopTen = new ArrayList<>();
		try {
			if (tournament == null) { // null == all.
				competitorTopTen = Ranking.getCompetitorsScoreRanking(FightPool.getInstance().getAll());
			} else {
				competitorTopTen = Ranking.getCompetitorsScoreRanking(FightPool.getInstance().get(tournament));
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}

		for (int i = 0; i < competitorTopTen.size(); i++) {
			columns = new ArrayList<>();
			columns.add(competitorTopTen.get(i).getCompetitor().getSurnameName());
			columns.add(competitorTopTen.get(i).getDuelsWon() + "/" + competitorTopTen.get(i).getDuelsDraw());
			columns.add("" + competitorTopTen.get(i).getHits());
			rows.add(columns);
		}
		createTable(stringBuilder, rows);
	}

	private static void createTable(StringBuilder stringBuilder, List<List<String>> rows) {
		stringBuilder.append("<table>\n");
		stringBuilder.append("<tbody>\n");
		for (List<String> row : rows) {
			stringBuilder.append("<tr>\n");
			for (String column : row) {
				stringBuilder.append("<td>\n");
				stringBuilder.append(column);
				stringBuilder.append("</td>\n");
			}
			stringBuilder.append("</tr>\n");
		}
		stringBuilder.append("</tbody>\n");
		stringBuilder.append("</table>\n");
	}

	private static String getDrawFight(Fight f, int duel) {
		// Draw Fights
		String draw;
		if (f.getDuels().get(duel).winner() == 0 && f.isOver()) {
			draw = "" + Score.DRAW.getAbbreviation();
		} else {
			draw = "" + Score.EMPTY.getAbbreviation();
		}
		return draw;
	}

	private static String getFaults(Fight f, int duel, boolean leftTeam) {
		String faultSimbol;
		boolean faults;
		if (leftTeam) {
			faults = f.getDuels().get(duel).getFaults(true);
		} else {
			faults = f.getDuels().get(duel).getFaults(false);
		}
		if (faults) {
			faultSimbol = "" + Score.FAULT.getAbbreviation();
		} else {
			faultSimbol = "" + Score.EMPTY.getAbbreviation();
		}
		return faultSimbol;
	}

	private static String getScore(Fight f, int duel, int score, boolean leftTeam) {
		if (leftTeam) {
			return f.getDuels().get(duel).getHits(true).get(score).getAbbreviation() + "";
		} else {
			return f.getDuels().get(duel).getHits(false).get(score).getAbbreviation() + "";
		}
	}
}
