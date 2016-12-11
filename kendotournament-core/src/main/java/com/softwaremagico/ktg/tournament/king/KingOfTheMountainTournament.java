package com.softwaremagico.ktg.tournament.king;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
	private ListIterator<Team> redTeam;
	private ListIterator<Team> whiteTeam;

	public KingOfTheMountainTournament(Tournament tournament) {
		super(tournament);
		redTeams = new ArrayList<>();
		redTeam = redTeams.listIterator();
		whiteTeams = new ArrayList<>();
		whiteTeam = whiteTeams.listIterator();
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
		return !redTeam.hasNext() || !whiteTeam.hasNext();
	}

	public void setRedTeams(List<Team> redTeams) {
		this.redTeams = redTeams;
		redTeam = redTeams.listIterator();
		initializeLevelZero();
	}

	public void setWhiteTeams(List<Team> whiteTeams) {
		this.whiteTeams = whiteTeams;
		whiteTeam = whiteTeams.listIterator();
		initializeLevelZero();
	}

	public List<Team> getRedTeams() {
		return redTeams;
	}

	public List<Team> getWhiteTeams() {
		return whiteTeams;
	}

	private void initializeLevelZero() {
		if (!redTeams.isEmpty() && !whiteTeams.isEmpty()) {
			setLevelZero(new KingLevel(getTournament(), 0, null, null, redTeam, whiteTeam));
			// Create group of level.
			getLevel(0).update();
		}
	}

}
