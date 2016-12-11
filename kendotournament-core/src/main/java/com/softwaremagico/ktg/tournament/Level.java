package com.softwaremagico.ktg.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.championship.LeagueLevel;
import com.softwaremagico.ktg.tournament.championship.TreeTournamentGroup;

public class Level implements Serializable {
	private static final long serialVersionUID = -1611900089563066556L;
	private int levelIndex;
	private Tournament tournament;
	private List<TGroup> tournamentGroups;
	private Level nextLevel;
	private Level previousLevel;

	protected Level(Tournament tournament, int levelIndex, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		this.tournament = tournament;
		tournamentGroups = new ArrayList<>();
		this.levelIndex = levelIndex;
		this.nextLevel = nextLevel;
		this.previousLevel = previousLevel;
	}

	public Integer size() {
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

	public void removeTeams() {
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

	public boolean isLevelFinished() {
		for (TGroup t : tournamentGroups) {
			if (!t.areFightsOverOrNull()) {
				return false;
			}
		}
		return true;
	}

	public int getNumberOfTotalTeamsPassNextRound() {
		int teams = 0;
		for (TGroup tournamentGroup : tournamentGroups) {
			teams += tournamentGroup.getMaxNumberOfWinners();
		}
		return teams;
	}

	public void updateArenaOfGroups() {
		if (tournamentGroups.size() > 0) {
			// Divide groups by arena.
			double groupsPerArena = Math.ceil((double) tournamentGroups.size() / (double) tournament.getFightingAreas());
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

	public Level getNextLevel() {
		return nextLevel;
	}

	public Level getPreviousLevel() {
		return previousLevel;
	}

	public void setNextLevel(Level nextLevel) {
		this.nextLevel = nextLevel;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public void update() {

	}

	public void removeGroups() {
		setTournamentGroups(new ArrayList<TGroup>());
		setNextLevel(null);
	}

	public void removeGroup(TGroup group) {
		if (getTournamentGroups().size() > 0) {
			getTournamentGroups().remove(group);
			if (getNextLevel() != null) {
				getNextLevel().updateGroupsSize();
				if (getNextLevel().size() == 0) {
					setNextLevel(null);
				}
			}
		}
	}

	public void updateGroupsSize() {
		// At least, one group by level.
		if (getGroups().isEmpty()) {
			addGroup(new TreeTournamentGroup(getTournament(), getLevelIndex(), 0));
		}

		updateArenaOfGroups();
	}

	public void addGroup(TGroup group) {
		getTournamentGroups().add(group);
	}

	protected Level createNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		return new Level(tournament, level, nextLevel, previousLevel);
	}

	public TGroup getGroupDestinationOfWinner(TGroup group, Integer winner) {
		return getNextLevel().getGroups().get(getGroupIndexDestinationOfWinner(group, winner));
	}

	public Integer getGroupIndexDestinationOfWinner(TGroup group, Integer winner) {
		return 0;
	}

	public boolean hasFightsAssigned() {
		for (TGroup group : getTournamentGroups()) {
			if (group.getFights() == null || group.getFights().isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
