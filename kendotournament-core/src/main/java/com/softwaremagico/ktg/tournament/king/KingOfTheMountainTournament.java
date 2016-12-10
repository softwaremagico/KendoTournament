package com.softwaremagico.ktg.tournament.king;

import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.LevelBasedTournament;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TGroup;

/**
 * Each level has only one group. The winner pass to the next level.
 * 
 */
public class KingOfTheMountainTournament extends LevelBasedTournament {
	private List<Team> redTeams;
	private List<Team> whiteTeams;

	protected KingOfTheMountainTournament(Tournament tournament) {
		super(tournament);
		redTeams = new ArrayList<>();
		whiteTeams = new ArrayList<>();
		setLevelZero(new KingLevel(tournament, 0, null, null));
	}

	private Team getNextRedTeam() {
		return redTeams.get(0);
	}

	private Team getNextWhiteTeam() {
		return whiteTeams.get(0);
	}

	@Override
	public List<Fight> createRandomFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException {
		return createFightsOfGroups(maximizeFights, level, true);
	}

	@Override
	public List<Fight> createSortedFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException {
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
		for (TGroup group : getLevelZero().getGroups()) {
			group.setMaxNumberOfWinners(winners);
		}
	}

	@Override
	public boolean isTheLastFight() {
		return redTeams.isEmpty() || whiteTeams.isEmpty();
	}

}
