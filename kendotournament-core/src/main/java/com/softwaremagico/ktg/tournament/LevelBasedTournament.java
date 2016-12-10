package com.softwaremagico.ktg.tournament;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.championship.LeagueLevel;

public abstract class LevelBasedTournament implements ITournamentManager {
	private LeagueLevel levelZero;
	private Tournament tournament;

	protected LevelBasedTournament(Tournament tournament) {
		setTournament(tournament);
	}

	public void setLevelZero(LeagueLevel levelZero) {
		this.levelZero = levelZero;
	}

	public LeagueLevel getLevelZero() {
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
		LeagueLevel level = getLevelZero().getNextLevel();
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
		for (LeagueLevel level : getLevels()) {
			level.setTournament(tournament);
		}
	}

	@Override
	public void removeTeams() {
		LeagueLevel level = getLevelZero();
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

	public void removeTeams(LeagueLevel level) {
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
	public List<LeagueLevel> getLevels() {
		List<LeagueLevel> levels = new ArrayList<>();
		LeagueLevel level = getLevelZero();
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
	public LeagueLevel getCurrentLevel() {
		LeagueLevel prevLevel = null;
		LeagueLevel level = getLevelZero();
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
	public LeagueLevel getLevel(Integer levelIndex) {
		LeagueLevel leagueLevel = getLevelZero();
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

}
