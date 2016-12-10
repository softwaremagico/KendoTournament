package com.softwaremagico.ktg.tournament.championship;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.LeagueLevelChampionship;
import com.softwaremagico.ktg.tournament.LevelBasedTournament;
import com.softwaremagico.ktg.tournament.TGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Championship extends LevelBasedTournament {

	public Championship(Tournament tournament) {
		super(tournament);
		setLevelZero(new LeagueLevelChampionship(tournament, 0, null, null));
	}

	@Override
	public void fillGroups() {
		try {
			// Read groups from fights.
			List<Fight> fights = FightPool.getInstance().get(getTournament());
			for (Fight fight : fights) {
				LeagueLevel leagueLevel = getLevel(fight.getLevel());
				leagueLevel.fillGroups(fight);
				// Create duels if multiple computers to avoid repetitions.
				if (getTournament().isUsingMultipleComputers()) {
					fight.getDuels();
				}
			}
			// Fill teams of groups without fights. (i.e group with only one
			// team).
			LeagueLevel level = getLevelZero();
			while (level.getNextLevel() != null) {
				level = level.getNextLevel();
				// A level with several groups, has already filled teams and one
				// of them has no teams.
				if (level.getPreviousLevel().getGroups().size() % 2 == 1
						&& level.getPreviousLevel().isLevelFinished()) {
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
	public List<Fight> createRandomFights(boolean maximizeFights, Integer level) {
		return createFightsOfGroups(maximizeFights, level, true);
	}

	@Override
	public List<Fight> createSortedFights(boolean maximizeFights, Integer level) {
		return createFightsOfGroups(maximizeFights, level, false);
	}

	private List<Fight> createFightsOfGroups(boolean maximizeFights, Integer level, boolean random) {
		List<Fight> fights = new ArrayList<>();
		// Obtain winners of previous level.
		if (level < getNumberOfLevels()) {
			getLevel(level).update();
			List<TGroup> groupsOfLevel = getGroups(level);
			for (TGroup groupsOfLevel1 : groupsOfLevel) {
				try {
					fights.addAll(groupsOfLevel1.createFights(maximizeFights, random));
				} catch (NullPointerException npe) {
					// No teams in group. Add no fights.
				}
			}
		}
		return fights;
	}

	@Override
	public void addGroup(TGroup group) {
		getLevel(0).addGroup(group);
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
	public void setDefaultFightAreas() {
		getLevel(0).updateArenaOfGroups();
	}

	@Override
	public void setHowManyTeamsOfGroupPassToTheTree(Integer winners) {
		getTournament().setHowManyTeamsOfGroupPassToTheTree(winners);
		for (TGroup group : getLevelZero().getGroups()) {
			group.setMaxNumberOfWinners(winners);
		}
		if (getLevelZero().getNextLevel() != null) {
			getLevelZero().getNextLevel().updateGroupsSize();
		}
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
					List<Fight> fights = FightPool.getInstance().get(getTournament());
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

}
