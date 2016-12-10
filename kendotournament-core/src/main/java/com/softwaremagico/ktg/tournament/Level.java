package com.softwaremagico.ktg.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;

public class Level implements Serializable {
	private int levelIndex;
	private Tournament tournament;
	private List<TGroup> tournamentGroups;
	private LeagueLevel nextLevel;
	private LeagueLevel previousLevel;

	protected Level(Tournament tournament, int levelIndex, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		this.tournament = tournament;
		tournamentGroups = new ArrayList<>();
		this.levelIndex = levelIndex;
		this.nextLevel = nextLevel;
		this.previousLevel = previousLevel;
	}

	protected Integer size() {
		return tournamentGroups.size();
	}

	public List<TGroup> getGroups() {
		return tournamentGroups;
	}

	/**
	 * Return the last group of the level.
	 * 
	 * @return
	 */
	protected TGroup getLastGroupOfLevel() {
		if (tournamentGroups.size() > 0) {
			return tournamentGroups.get(tournamentGroups.size() - 1);
		} else {
			return null;
		}
	}

	protected void removeTeams() {
		for (TGroup group : tournamentGroups) {
			group.removeTeams();
		}
	}

	protected boolean isFightOfLevel(Fight fight) {
		for (TGroup t : tournamentGroups) {
			if (t.isFightOfGroup(fight)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isGroupOfLevel(TGroup tgroup) {
		for (TGroup t : tournamentGroups) {
			if (tgroup.equals(t)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isLevelFinished() {
		for (TGroup t : tournamentGroups) {
			if (!t.areFightsOverOrNull()) {
				return false;
			}
		}
		return true;
	}

	protected int getNumberOfTotalTeamsPassNextRound() {
		int teams = 0;
		for (TGroup tournamentGroup : tournamentGroups) {
			teams += tournamentGroup.getMaxNumberOfWinners();
		}
		return teams;
	}

	protected void updateArenaOfGroups() {
		if (tournamentGroups.size() > 0) {
			// Divide groups by arena.
			double groupsPerArena = Math.ceil((double) tournamentGroups.size()
					/ (double) tournament.getFightingAreas());
			for (int j = 0; j < tournamentGroups.size(); j++) {
				tournamentGroups.get(j).setFightArea((j) / (int) groupsPerArena);
			}
		}
		if (nextLevel != null) {
			nextLevel.updateArenaOfGroups();
		}
	}

	protected TGroup getGroupOfFight(Fight fight) {
		for (TGroup group : tournamentGroups) {
			if (group.getTeams().contains(fight.getTeam1()) && group.getTeams().contains(fight.getTeam2())) {
				return group;
			}
		}
		return null;
	}

	public Set<Team> getUsedTeams() {
		Set<Team> usedTeams = new HashSet<>();
		for (TGroup group : tournamentGroups) {
			usedTeams.addAll(group.getTeams());
		}
		return usedTeams;
	}

	public Integer getIndexOfGroup(TGroup group) {
		for (int i = 0; i < tournamentGroups.size(); i++) {
			if (tournamentGroups.get(i).equals(group)) {
				return i;
			}
		}
		return null;
	}

	public List<TGroup> getTournamentGroups() {
		return tournamentGroups;
	}

	public void setTournamentGroups(List<TGroup> tournamentGroups) {
		this.tournamentGroups = tournamentGroups;
	}

	public int getLevelIndex() {
		return levelIndex;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public LeagueLevel getNextLevel() {
		return nextLevel;
	}

	public LeagueLevel getPreviousLevel() {
		return previousLevel;
	}

	public void setNextLevel(LeagueLevel nextLevel) {
		this.nextLevel = nextLevel;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

}
