package com.softwaremagico.ktg.tournament;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.championship.LeagueLevel;

public abstract class LevelBasedTournament implements ITournamentManager {
	private Level levelZero;
	private Tournament tournament;

	protected LevelBasedTournament(Tournament tournament) {
		setTournament(tournament);
	}

	public void setLevelZero(Level levelZero) {
		this.levelZero = levelZero;
	}

	public Level getLevelZero() {
		return levelZero;
	}

	@Override
	public List<Fight> getFights(Integer level) {
		try {
			return FightPool.getInstance().getFromLevel(getTournament(), level);
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
			return new ArrayList<>();
		}
	}

	@Override
	public int getNumberOfFightsFinished() {
		int i = 0;
		try {
			for (Fight fight : FightPool.getInstance().get(getTournament())) {
				if (fight.isOver()) {
					i++;
				}
			}
		} catch (SQLException e) {
			return 0;
		}
		return i;
	}

	@Override
	public List<TGroup> getGroups() {
		List<TGroup> allGroups = new ArrayList<>();
		for (int i = 0; i < getNumberOfLevels(); i++) {
			allGroups.addAll(getLevel(i).getGroups());
		}
		return allGroups;
	}

	@Override
	public List<TGroup> getGroups(Integer level) {
		return getLevel(level).getGroups();
	}

	@Override
	public Integer getNumberOfLevels() {
		Integer total = 1; // Always exist level zero.
		Level level = getLevelZero().getNextLevel();
		while (level != null) {
			total++;
			level = level.getNextLevel();
		}
		return total;
	}

	@Override
	public Tournament getTournament() {
		return tournament;
	}

	@Override
	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
		// Update levels if exists.
		for (Level level : getLevels()) {
			level.setTournament(tournament);
		}
	}

	@Override
	public void removeTeams() {
		Level level = getLevelZero();
		while (level != null) {
			removeTeams(level);
			level = level.getNextLevel();
		}
	}

	@Override
	public void removeTeams(Integer level) {
		if (level != null) {
			removeTeams(getLevel(level));
		}
	}

	public void removeTeams(Level level) {
		while (level != null) {
			level.removeTeams();
			level = level.getNextLevel();
		}
	}

	@Override
	public int getIndex(Integer level, TGroup group) {
		return getGroups(level).indexOf(group);
	}

	@Override
	public void resetFights() {
		for (TGroup group : getGroups()) {
			group.resetFights();
		}
	}

	@Override
	public List<TGroup> getGroupsByShiajo(Integer shiaijo) {
		List<TGroup> groups = new ArrayList<>();
		for (TGroup group : getGroups()) {
			if (group.getFightArea().equals(shiaijo)) {
				groups.add(group);
			}
		}
		return groups;
	}

	@Override
	public List<Level> getLevels() {
		List<Level> levels = new ArrayList<>();
		Level level = getLevelZero();
		while (level != null) {
			levels.add(level);
			level = level.getNextLevel();
		}
		return levels;
	}

	@Override
	public void removeWinners(Integer level) {
		if (level != null) {
			List<TGroup> groups = getGroups(level);
			for (TGroup group : groups) {
				List<Fight> fights = group.getFights();
				for (Fight fight : fights) {
					fight.setWinner(null);
				}
			}
		}
	}

	@Override
	public Level getCurrentLevel() {
		Level prevLevel = null;
		Level level = getLevelZero();
		while (level != null && level.isLevelFinished()) {
			prevLevel = level;
			level = level.getNextLevel();
		}
		if (level != null) {
			return level;
		}
		// All levels are finished. Return the last one.
		return prevLevel;
	}

	@Override
	public int getIndexOfGroup(TGroup group) {
		return getGroups(group.getLevel()).indexOf(group);
	}

	@Override
	public void removeGroup(Integer level, Integer groupIndex) {
		getLevel(level).removeGroup(getGroups(level).get(groupIndex));
	}

	@Override
	public void removeGroup(TGroup group) {
		getLevel(group.getLevel()).removeGroup(group);
	}

	@Override
	public void removeGroups(Integer level) {
		getLevel(level).removeGroups();
	}

	@Override
	public TGroup getGroup(Fight fight) {
		for (TGroup group : getGroups()) {
			if (group.isFightOfGroup(fight)) {
				return group;
			}
		}
		return null;
	}

	@Override
	public Level getLevel(Integer levelIndex) {
		Level leagueLevel = getLevelZero();
		while (levelIndex > 0) {
			levelIndex--;
			try {
				leagueLevel = leagueLevel.getNextLevel();
			} catch (NullPointerException npe) {
				return null;
			}
		}
		return leagueLevel;
	}

	@Override
	public Integer getLastLevelUsed() {
		for (int i = 0; i < getNumberOfLevels(); i++) {
			if (getLevel(i).isLevelFinished()) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public Level getLastLevel() {
		Level level = getLevelZero();
		Level selectedLevel = level;
		while (level != null) {
			selectedLevel = level;
			level = level.getNextLevel();
		}
		return selectedLevel;
	}

	@Override
	public void fillGroups() {
		try {
			// Read groups from fights.
			List<Fight> fights = FightPool.getInstance().get(getTournament());
			for (Fight fight : fights) {
				LeagueLevel leagueLevel = (LeagueLevel) getLevel(fight.getLevel());
				leagueLevel.fillGroups(fight);
				// Create duels if multiple computers to avoid repetitions.
				if (getTournament().isUsingMultipleComputers()) {
					fight.getDuels();
				}
			}
			// Fill teams of groups without fights. (i.e group with only one
			// team).
			Level level = getLevelZero();
			while (level.getNextLevel() != null) {
				level = level.getNextLevel();
				// A level with several groups, has already filled teams and one
				// of them has no teams.
				if (level.getPreviousLevel().getGroups().size() % 2 == 1 && level.getPreviousLevel().isLevelFinished()) {
					// Search a group without teams.
					for (TGroup group : level.getGroups()) {
						if (group.getTeams().isEmpty()) {
							level.update();
						}
					}
				}
			}
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
		}
	}

	@Override
	public void addGroup(TGroup group) {
		getLevel(0).addGroup(group);
	}

	@Override
	public boolean exist(Team team) {
		for (Level level : getLevels()) {
			for (TGroup group : level.getGroups()) {
				if (group.getTeams().contains(team)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setDefaultFightAreas() {
		getLevel(0).updateArenaOfGroups();
	}

	@Override
	public boolean hasDrawScore(TGroup group) {
		Ranking ranking = new Ranking(group.getFights());
		List<Team> teamsInDraw = ranking.getFirstTeamsWithDrawScore(getTournament().getHowManyTeamsOfGroupPassToTheTree());
		return (teamsInDraw != null);
	}

}
