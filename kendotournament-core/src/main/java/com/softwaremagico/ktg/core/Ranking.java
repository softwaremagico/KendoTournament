package com.softwaremagico.ktg.core;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.softwaremagico.ktg.tournament.score.ScoreOfCompetitor;
import com.softwaremagico.ktg.tournament.score.ScoreOfCompetitorClassic;
import com.softwaremagico.ktg.tournament.score.ScoreOfCompetitorCustom;
import com.softwaremagico.ktg.tournament.score.ScoreOfCompetitorEuropean;
import com.softwaremagico.ktg.tournament.score.ScoreOfCompetitorInternational;
import com.softwaremagico.ktg.tournament.score.ScoreOfCompetitorWinOverDraws;
import com.softwaremagico.ktg.tournament.score.ScoreOfTeam;
import com.softwaremagico.ktg.tournament.score.ScoreType;

public class Ranking {

	private List<Fight> fights;
	private List<Team> teamRanking = null;
	private List<RegisteredPerson> competitorsRanking = null;
	private List<ScoreOfTeam> teamScoreRanking = null;
	private List<ScoreOfCompetitor> competitorsScoreRanking = null;

	public Ranking(List<Fight> fights) {
		this.fights = fights;
	}

	public List<Team> getTeamsRanking() {
		if (teamRanking == null) {
			teamRanking = getTeamsRanking(fights);
		}
		return teamRanking;
	}

	public List<ScoreOfTeam> getTeamsScoreRanking() {
		if (teamScoreRanking == null) {
			teamScoreRanking = getTeamsScoreRanking(fights);
		}
		return teamScoreRanking;
	}

	/**
	 * Return a Hashmap that classify the teams by position (1st, 2nd, 3rd,...)
	 * 
	 * @param maxWinners
	 * @return
	 */
	private HashMap<Integer, List<Team>> getTeamsByPosition() {
		HashMap<Integer, List<Team>> teamsByPosition = new HashMap<>();
		List<ScoreOfTeam> scores = getTeamsScoreRanking();

		Integer position = 0;
		for (int i = 0; i < scores.size(); i++) {
			if (teamsByPosition.get(position) == null) {
				teamsByPosition.put(position, new ArrayList<Team>());
			}
			// Put team in position.
			teamsByPosition.get(position).add(scores.get(i).getTeam());
			// Different score with next team.
			if ((i < scores.size() - 1) && scores.get(i).compareTo(scores.get(i + 1)) != 0) {
				position++;
			}
		}

		return teamsByPosition;
	}

	public List<Team> getFirstTeamsWithDrawScore(Integer maxWinners) {
		HashMap<Integer, List<Team>> teamsByPosition = getTeamsByPosition();
		for (Integer i = 0; i < maxWinners; i++) {
			List<Team> teamsInDraw = teamsByPosition.get(i);
			if (teamsInDraw.size() > 1) {
				return teamsInDraw;
			}
		}
		return null;
	}

	public Team getTeam(Integer order) {
		List<Team> teamsOrder = getTeamsRanking();
		if (order >= 0 && order < teamsOrder.size()) {
			return teamsOrder.get(order);
		}
		return null;
	}

	public ScoreOfTeam getScoreOfTeam(Integer order) {
		List<ScoreOfTeam> teamsOrder = getTeamsScoreRanking();
		if (order >= 0 && order < teamsOrder.size()) {
			return teamsOrder.get(order);
		}
		return null;
	}

	public List<RegisteredPerson> getCompetitorsRanking() {
		if (competitorsRanking == null) {
			competitorsRanking = getCompetitorsRanking(fights);
		}
		return competitorsRanking;
	}

	public List<ScoreOfCompetitor> getCompetitorsScoreRanking() {
		if (competitorsScoreRanking == null) {
			competitorsScoreRanking = getCompetitorsScoreRanking(fights);
		}
		return competitorsScoreRanking;
	}

	public ScoreOfCompetitor getScoreRanking(RegisteredPerson competitor) {
		List<ScoreOfCompetitor> scoreRanking = getCompetitorsScoreRanking();
		for (ScoreOfCompetitor score : scoreRanking) {
			if (score.getCompetitor().equals(competitor)) {
				return score;
			}
		}
		return null;
	}

	public RegisteredPerson getCompetitor(Integer order) {
		List<RegisteredPerson> competitorOrder = getCompetitorsRanking();
		if (order >= 0 && order < competitorOrder.size()) {
			return competitorOrder.get(order);
		}
		return null;
	}

	public ScoreOfCompetitor getScoreOfCompetitor(Integer order) {
		List<ScoreOfCompetitor> teamsOrder = getCompetitorsScoreRanking();
		if (order >= 0 && order < teamsOrder.size()) {
			return teamsOrder.get(order);
		}
		return null;
	}

	private static List<Team> getTeams(List<Fight> fights) {
		List<Team> teamsOfFights = new ArrayList<>();
		for (Fight fight : fights) {
			if (!teamsOfFights.contains(fight.getTeam1())) {
				teamsOfFights.add(fight.getTeam1());
			}
			if (!teamsOfFights.contains(fight.getTeam2())) {
				teamsOfFights.add(fight.getTeam2());
			}
		}
		return teamsOfFights;
	}

	private static List<RegisteredPerson> getRegisteredPersons(List<Fight> fights) {
		List<RegisteredPerson> competitorsOfFight = new ArrayList<>();
		List<RegisteredPerson> allCompetitors = new ArrayList<>();
		for (Fight fight : fights) {
			allCompetitors.addAll(fight.getTeam1().getMembersOrder(0).values());
			allCompetitors.addAll(fight.getTeam2().getMembersOrder(0).values());
		}
		for (RegisteredPerson competitor : allCompetitors) {
			if (!competitorsOfFight.contains(competitor)) {
				competitorsOfFight.add(competitor);
			}
		}
		return competitorsOfFight;
	}

	public static List<Team> getTeamsRanking(List<Fight> fights) {
		List<ScoreOfTeam> scores = getTeamsScoreRanking(fights);
		List<Team> teamRanking = new ArrayList<>();
		for (ScoreOfTeam score : scores) {
			teamRanking.add(score.getTeam());
		}
		return teamRanking;
	}

	public static List<ScoreOfTeam> getTeamsScoreRanking(List<Fight> fights) {
		List<Team> teamsOfFights = getTeams(fights);
		List<ScoreOfTeam> scores = new ArrayList<>();
		for (Team team : teamsOfFights) {
			scores.add(ScoreType.getScoreOfTeam(team, fights));
		}
		Collections.sort(scores);

		return scores;
	}

	/**
	 * Gets the more restrictive score for obtaining the ranking.
	 * 
	 * @param fights
	 * @return
	 */
	private static ScoreOfCompetitor getScoreOfCompetitor(RegisteredPerson competitor, List<Fight> fights) {
		// If one fight is classic, use classic comparation.
		for (Fight fight : fights) {
			if (fight.getTournament().getTournamentScore().getScoreType().equals(ScoreType.CLASSIC)) {
				return new ScoreOfCompetitorClassic(competitor, fights);
			}
		}

		// If one fight is european, use european comparation
		for (Fight fight : fights) {
			if (fight.getTournament().getTournamentScore().getScoreType().equals(ScoreType.EUROPEAN)) {
				return new ScoreOfCompetitorEuropean(competitor, fights);
			}
		}

		// If one fight is european, use european comparation
		for (Fight fight : fights) {
			if (fight.getTournament().getTournamentScore().getScoreType().equals(ScoreType.INTERNATIONAL)) {
				return new ScoreOfCompetitorInternational(competitor, fights);
			}
		}

		// If one fight is winoverdraw, use winoverdraw comparation
		for (Fight fight : fights) {
			if (fight.getTournament().getTournamentScore().getScoreType().equals(ScoreType.WIN_OVER_DRAWS)) {
				return new ScoreOfCompetitorWinOverDraws(competitor, fights);
			}
		}

		return new ScoreOfCompetitorCustom(competitor, fights);
	}

	public static ScoreOfCompetitor getScoreRanking(RegisteredPerson competitor, List<Fight> fights) {
		return getScoreOfCompetitor(competitor, fights);
	}

	public static List<ScoreOfCompetitor> getCompetitorsScoreRanking(List<Fight> fights) {
		List<RegisteredPerson> competitors = getRegisteredPersons(fights);
		List<ScoreOfCompetitor> scores = new ArrayList<>();
		for (RegisteredPerson competitor : competitors) {
			scores.add(getScoreOfCompetitor(competitor, fights));
		}
		Collections.sort(scores);
		return scores;
	}

	public static Integer getOrder(List<Fight> fights, Team team) {
		List<Team> ranking = getTeamsRanking(fights);

		for (Integer i = 0; i < ranking.size(); i++) {
			if (ranking.get(i).equals(team)) {
				return i;
			}
		}
		return null;
	}

	public static Integer getOrderFromRanking(List<ScoreOfTeam> ranking, Team team) {
		for (Integer i = 0; i < ranking.size(); i++) {
			if (ranking.get(i).getTeam().equals(team)) {
				return i;
			}
		}
		return null;
	}

	public static Team getTeam(List<Fight> fights, Integer order) {
		List<Team> ranking = getTeamsRanking(fights);
		if (order < ranking.size() && order >= 0) {
			return ranking.get(order);
		}
		return null;
	}

	public static List<RegisteredPerson> getCompetitorsRanking(List<Fight> fights) {
		List<RegisteredPerson> competitors = getRegisteredPersons(fights);
		List<ScoreOfCompetitor> scores = new ArrayList<>();
		for (RegisteredPerson competitor : competitors) {
			scores.add(getScoreOfCompetitor(competitor, fights));
		}
		Collections.sort(scores);
		List<RegisteredPerson> competitorsRanking = new ArrayList<>();
		for (ScoreOfCompetitor score : scores) {
			competitorsRanking.add(score.getCompetitor());
		}
		return competitorsRanking;
	}

	@Override
	public String toString() {
		return getTeamsRanking().toString();
	}
}
