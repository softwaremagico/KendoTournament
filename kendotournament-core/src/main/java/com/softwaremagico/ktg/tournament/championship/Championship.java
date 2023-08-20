package com.softwaremagico.ktg.tournament.championship;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.LeagueLevelChampionship;
import com.softwaremagico.ktg.tournament.LevelBasedTournament;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.king.TournamentFinishedException;

public class Championship extends LevelBasedTournament {

	public Championship(Tournament tournament) {
		super(tournament);
		setLevelZero(new LeagueLevelChampionship(tournament, 0, null, null));
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
	public boolean isTheLastFight() {
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

	@Override
	public boolean isNewLevelNeeded() {
		// Leves are created at the beginning.
		return false;
	}

	@Override
	public void createNextLevel() throws TournamentFinishedException {
		// Autoegenerated at the beginning.
	}

}
