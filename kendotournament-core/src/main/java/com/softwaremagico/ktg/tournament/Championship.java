package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.persistence.FightPool;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Championship implements ITournamentManager {

	protected Tournament tournament;
	protected LeagueLevel levelZero;

	protected Championship(Tournament tournament) {
		this.tournament = tournament;
		levelZero = new LeagueLevelChampionship(tournament, 0, null, null);
	}

	@Override
	public void fillGroups() {
		try {
			// Read groups from fights.
			List<Fight> fights = FightPool.getInstance().get(tournament);
			for (Fight fight : fights) {
				LeagueLevel leagueLevel = getLevel(fight.getLevel());
				leagueLevel.fillGroups(fight);
                                fight.getDuels();
			}
			// Fill teams of groups without fights. (i.e grouo with only one
			// team).
			LeagueLevel level = levelZero;
			while (level.nextLevel != null) {
				level = level.nextLevel;
				// A level with several groups, has already filled teams and one
				// of them has no teams.
				if (level.previousLevel.getGroups().size() % 2 == 1 && level.previousLevel.isLevelFinished()) {
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
	public List<Fight> getFights(Integer level) {
		try {
			return FightPool.getInstance().getFromLevel(tournament, level);
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
			return new ArrayList<>();
		}
	}

	@Override
	public List<Fight> createRandomFights(Integer level) {
		return createFightsOfGroups(level, true);
	}

	@Override
	public List<Fight> createSortedFights(Integer level) {
		return createFightsOfGroups(level, false);
	}

	private List<Fight> createFightsOfGroups(Integer level, boolean random) {
		List<Fight> fights = new ArrayList<>();
		// Obtain winners of previous level.
		if (level < getNumberOfLevels()) {
			getLevel(level).update();
			List<TGroup> groupsOfLevel = getGroups(level);
			for (int i = 0; i < groupsOfLevel.size(); i++) {
				try {
					fights.addAll(groupsOfLevel.get(i).createFights(random));
				} catch (NullPointerException npe) {
					// No teams in group. Add no fights.
				}
			}
		}
		return fights;
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
	public TGroup getGroup(Fight fight) {
		for (TGroup group : getGroups()) {
			if (group.isFightOfGroup(fight)) {
				return group;
			}
		}
		return null;
	}

	@Override
	public void addGroup(TGroup group) {
		getLevel(0).addGroup(group);
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
	public LeagueLevel getLevel(Integer levelIndex) {
		LeagueLevel leagueLevel = levelZero;
		while (levelIndex > 0) {
			levelIndex--;
			try {
				leagueLevel = leagueLevel.nextLevel;
			} catch (NullPointerException npe) {
				return null;
			}
		}
		return leagueLevel;
	}

	@Override
	public Integer getNumberOfLevels() {
		Integer total = 1; // Always exist level zero.
		LeagueLevel level = levelZero.nextLevel;
		while (level != null) {
			total++;
			level = level.nextLevel;
		}
		return total;
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
	public boolean exist(Team team) {
		List<TGroup> groups = getGroups(0);
		for (TGroup group : groups) {
			if (group.getTeams().contains(team)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeTeams(Integer level) {
		removeTeams(getLevel(level));
	}

	public void removeTeams(LeagueLevel level) {
		while (level != null) {
			level.removeTeams();
			level = level.nextLevel;
		}
	}

	@Override
	public void setDefaultFightAreas() {
		getLevel(0).updateArenaOfGroups();
	}

	@Override
	public void setHowManyTeamsOfGroupPassToTheTree(Integer winners) {
		tournament.setHowManyTeamsOfGroupPassToTheTree(winners);
		for (TGroup group : levelZero.getGroups()) {
			group.setMaxNumberOfWinners(winners);
		}
		if (levelZero.nextLevel != null) {
			levelZero.nextLevel.updateGroupsSize();
		}
	}

	@Override
	public int getIndex(Integer level, TGroup group) {
		return getGroups(level).indexOf(group);
	}

	@Override
	public boolean inTheLastFight() {
		if (!getGroups().isEmpty()) {
			if (getGroups().size() > 1) {
				// With more than one group If penultimus group is over, then we
				// are in last group that only can have one fight.
				List<Fight> fightsLastGroup = getGroups().get(getGroups().size() - 1).getFights();
				if (fightsLastGroup.size() > 0) {
					if (getGroups().get(getGroups().size() - 2).areFightsOver()) {
						return true;
					}
				}
			} else {
				// With one group is the same that a Simple Tournament.
				try {
					List<Fight> fights = FightPool.getInstance().get(tournament);
					if (fights.size() > 0) {
						if (fights.size() == 1 || fights.get(fights.size() - 2).isOver()) {
							return true;
						}
					}
				} catch (SQLException ex) {
				}
			}
		}
		return false;
	}

	@Override
	public void resetFights() {
		for (TGroup group : getGroups()) {
			group.resetFights();
		}
	}

	@Override
	public void removeTeams() {
		LeagueLevel level = levelZero;
		while (level != null) {
			removeTeams(level);
			level = level.nextLevel;
		}
	}
}
