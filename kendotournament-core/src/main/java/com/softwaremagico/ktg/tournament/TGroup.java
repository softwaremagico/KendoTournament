package com.softwaremagico.ktg.tournament;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
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
import java.util.Collections;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;

/**
 * Defines a group of teams that fight together in a tournament. A league has
 * multiple groups but a simple tournament only has one.
 */
public abstract class TGroup {

	private Tournament tournament;
	private List<Team> teams = new ArrayList<>();
	private Integer numberMaxOfWinners = 1;
	protected Integer level;
	private Integer fightArea = 0;
	private List<Fight> fightsOfGroup;

	public TGroup(Tournament tournament, Integer level, Integer fightArea) {
		this.tournament = tournament;
		this.level = level;
		this.fightArea = fightArea;
		fightsOfGroup = new ArrayList<>();
		if (level == 0) {
			this.numberMaxOfWinners = tournament.getHowManyTeamsOfGroupPassToTheTree();
		} else {
			this.numberMaxOfWinners = 1;
		}
	}

	public void setMaxNumberOfWinners(int value) {
		if (numberMaxOfWinners < 1) {
			numberMaxOfWinners = 1;
		} else {
			numberMaxOfWinners = value;
		}
	}

	public int getMaxNumberOfWinners() {
		if (level > 0) {
			return 1;
		} else {
			return numberMaxOfWinners;
		}
	}

	public int getLevel() {
		return level;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public Integer getFightArea() {
		return fightArea;
	}

	public void setFightArea(Integer fightArea) {
		this.fightArea = fightArea;
	}

	public int numberOfTeams() {
		return teams.size();
	}

	public void removeTeams() {
		teams = new ArrayList<>();
	}

	public void addTeam(Team team) {
		// Can not be repeated.
		if (!teams.contains(team)) {
			teams.add(team);
		}
	}

	public void addTeams(List<Team> teams) {
		this.teams.addAll(teams);
	}

	private boolean isTeamOfGroup(Team team) {
		return teams.contains(team);
	}

	public List<Team> getWinners() {
		try {
			// If only exists one team in this group, is the winner.
			if (getTeams().size() == 1) {
				return getTeams();
			}
			// If exists fights, the winner is the first of the ranking.
			if (getFights() != null && getFights().size() > 0) {
				return Ranking.getTeamsRanking(getFights()).subList(0, numberMaxOfWinners);
			}
		} catch (Exception iob) {
		}

		return new ArrayList<>();
	}

	public boolean isFightOfGroup(Fight f) {
		if (isTeamOfGroup(f.getTeam1()) && isTeamOfGroup(f.getTeam2()) && level.equals(f.getLevel())) {
			return true;
		}
		return false;
	}

	public List<Fight> getFights() {
		try {
			if (fightsOfGroup == null || fightsOfGroup.isEmpty()) {
				fightsOfGroup = getFightsOfGroup(FightPool.getInstance().getFromLevel(tournament, level));
			}
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
		}
		return fightsOfGroup;
	}

	private List<Fight> getFightsOfGroup(List<Fight> fights) {
		List<Fight> fightsG = new ArrayList<>();
		for (Fight fight : fights) {
			if (fight.getGroup() == getIndex() && fight.getLevel().equals(level)) {
				fightsG.add(fight);
			}
		}
		return fightsG;
	}

	/**
	 * Create fights for this group depending on the tournament type and the
	 * assigned teams.
	 * 
	 * @param maximizeFights
	 *            Create maxim number of fights.
	 * @param random
	 *            Do not sort teams by name.
	 * @return
	 */
	public abstract List<Fight> createFights(boolean maximizeFights, boolean random);

	public boolean areFightsOver(List<Fight> allFights) {
		List<Fight> fights = getFightsOfGroup(allFights);

		if (fights.size() > 0) {
			for (Fight fight : fights) {
				if (!fight.isOver()) {
					return false;
				}
			}
			return true;
		}
		// Only one team. Cannot have fights. Is over always.
		if (getTeams() != null && getTeams().size() == 1) {
			return true;
		}
		return false;
	}

	public boolean areFightsOver() {
		List<Fight> fights = getFights();

		if (fights.size() > 0) {
			for (Fight fight : fights) {
				if (!fight.isOver()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean areFightsStarted() {
		List<Fight> fights = getFights();

		for (Fight fight : fights) {
			if (fight.isOver() || fight.getScore(true) > 0 || fight.getScore(false) > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean areFightsOverOrNull() {
		if (teams.size() < 2) {
			return true;
		}
		return areFightsOverOrNull(getFights());
	}

	/**
	 * If the fightManager are over or fightManager are not needed.
	 *
	 * @param fights
	 * @return
	 */
	public static boolean areFightsOverOrNull(List<Fight> fights) {
		if (fights.size() > 0) {
			for (Fight fight : fights) {
				if (!fight.isOver()) {
					return false;
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + tournament + ") Group in level: " + level + ", GroupIndex: " + getIndex() + ", fight area: " + fightArea + ", teams " + teams + "\n";
	}

	/**
	 * Add a fight obtained from database to the group.
	 *
	 * @param fight
	 */
	public void addFight(Fight fight) {
		if (!fightsOfGroup.contains(fight)) {
			fightsOfGroup.add(fight);
			addTeam(fight.getTeam1());
			addTeam(fight.getTeam2());
			Collections.sort(fightsOfGroup);
		}
	}

	/**
	 * Gets the index of the group in the tournament. The index is relative to
	 * all groups independent of the level.
	 *
	 * @return
	 */
	public int getIndex() {
		return TournamentManagerFactory.getManager(tournament, tournament.getType()).getIndexOfGroup(this);
	}

	public void resetFights() {
		fightsOfGroup = new ArrayList<>();
	}

	public void removeFight(Fight fight) throws SQLException {
		if (getFights().contains(fight)) {
			FightPool.getInstance().remove(tournament, fight);
			fightsOfGroup.remove(fight);
			resetFightsOrder();
		}
	}

	/**
	 * Remove missed index in the fights when one is deleted.
	 */
	private void resetFightsOrder() throws SQLException {
		for (int i = 0; i < getFights().size(); i++) {
			// Order in group is part of the Id. Cannot be updated, must be
			// deleted the original one and added the new one.
			Fight original = getFights().get(i);
			FightPool.getInstance().remove(tournament, original);
			original.setOrderInGroup(i);
			FightPool.getInstance().add(tournament, original);
		}
		resetFights();
	}

	/**
	 * Create a list of fights where all teams fight versus all others.
	 * 
	 * @param tournament
	 * @param teams
	 * @param fightArea
	 * @param level
	 * @param index
	 * @param random
	 * @return
	 */
	protected static List<Fight> createCompleteFightList(Tournament tournament, List<Team> teams, int fightArea, int level, int index, boolean random) {
		if (teams == null || tournament == null || teams.size() < 2) {
			return null;
		}
		List<Fight> fights = new ArrayList<>();
		TeamSelector remainingFights = new TeamSelector(teams);

		Team team1 = remainingFights.getTeamWithMoreAdversaries(random);
		Fight fight, lastFight = null;
		while (remainingFights.remainFights()) {
			Team team2 = remainingFights.getNextAdversary(team1, random);
			// Team1 has no more adversaries. Use another one.
			if (team2 == null) {
				team1 = remainingFights.getTeamWithMoreAdversaries(random);
				continue;
			}
			// Remaining fights sometimes repeat team. Align them.
			if (lastFight != null && (lastFight.getTeam1().equals(team2) || lastFight.getTeam2().equals(team1))) {
				fight = new Fight(tournament, team2, team1, fightArea, level, index, fights.size());
			} else if (lastFight != null && (lastFight.getTeam1().equals(team1) || lastFight.getTeam2().equals(team2))) {
				fight = new Fight(tournament, team1, team2, fightArea, level, index, fights.size());
			} else if (fights.size() % 2 == 0) {
				fight = new Fight(tournament, team1, team2, fightArea, level, index, fights.size());
			} else {
				fight = new Fight(tournament, team2, team1, fightArea, level, index, fights.size());
			}
			// Force the creation of duels for more than one fight area. If not,
			// multiple computers
			// generates different duels.
			if (tournament.isUsingMultipleComputers() && tournament.getFightingAreas() > 1) {
				fight.getDuels();
			}
			fights.add(fight);
			lastFight = fight;
			remainingFights.removeAdveresary(team1, team2);
			team1 = team2;
		}
		return fights;
	}

	/**
	 * All teams fights agains the next and previous team of the list.
	 * 
	 * @param tournament
	 * @param teams
	 * @param fightArea
	 * @param level
	 * @param index
	 * @param random
	 * @return
	 */
	protected static List<Fight> createTwoFightsForEachTeam(Tournament tournament, List<Team> teams, int fightArea, int level, int index) {
		if (teams == null || tournament == null || teams.size() < 2) {
			return null;
		}
		List<Fight> fights = new ArrayList<>();

		// If only exists two teams, there are only one fight. If no, as many
		// fights as teams
		for (int i = 0; i < (teams.size() > 2 ? teams.size() : 1); i++) {
			Fight fight;
			Team team1 = teams.get(i);
			Team team2 = teams.get((i + 1) % teams.size());

			if (fights.size() % 2 == 0) {
				fight = new Fight(tournament, team1, team2, fightArea, level, index, fights.size());
			} else {
				fight = new Fight(tournament, team2, team1, fightArea, level, index, fights.size());
			}
			// Force the creation of duels for more than one fight area. If not,
			// multiple computers
			// generates different duels.
			if (tournament.isUsingMultipleComputers() && tournament.getFightingAreas() > 1) {
				fight.getDuels();
			}
			fights.add(fight);
		}
		return fights;
	}

	/**
	 * One team fights agains all other teams.
	 * 
	 * @param tournament
	 * @param teams
	 * @param fightArea
	 * @param level
	 * @param index
	 * @param random
	 * @return
	 */
	protected static List<Fight> createLoopFights(Tournament tournament, List<Team> teams, int fightArea, int level, int index, boolean random) {
		if (teams.size() < 2) {
			return null;
		}
		List<Fight> fights = new ArrayList<>();
		TeamSelector remainingFights = new LoopTeamSelector(teams);

		List<Team> remainingTeams = remainingFights.getTeams();
		if (random) {
			Collections.shuffle(remainingTeams);
		}
		for (Team team : remainingTeams) {
			for (Team adversary : remainingFights.getAdversaries(team)) {
				Fight fight = new Fight(tournament, team, adversary, fightArea, level, index, fights.size());
				// Force the creation of duels for more than one fight area. If
				// not, multiple computers generates different duels.
				if (tournament.isUsingMultipleComputers() && tournament.getFightingAreas() > 1) {
					fight.getDuels();
				}
				fights.add(fight);
			}
		}

		return fights;
	}
}
