package com.softwaremagico.ktg.tournament.king;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
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

	public KingOfTheMountainTournament(Tournament tournament) {
		super(tournament);
		redTeams = new ArrayList<>();
		whiteTeams = new ArrayList<>();
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
		return isInLastGroup();
	}

	public Team getWinner() {
		if (isInLastGroup()) {
			return getLastLevel().getGroups().get(0).getWinners().get(0);
		}
		return null;
	}

	public void setRedTeams(List<Team> redTeams) {
		this.redTeams = redTeams;
		initializeLevelZero();
	}

	public void setWhiteTeams(List<Team> whiteTeams) {
		this.whiteTeams = whiteTeams;
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
			setLevelZero(new KingLevel(getTournament(), 0, null, null, redTeams.listIterator(), whiteTeams.listIterator()));
		}
	}

	private boolean needsNewLevel() {
		KingLevel lastLevel = (KingLevel) getLastLevel();
		if (lastLevel != null && !lastLevel.getGroups().isEmpty()) {
			return !isInLastGroup();
		}
		return false;
	}

	private boolean isInLastGroup() {
		KingLevel lastLevel = (KingLevel) getLastLevel();
		if (lastLevel != null && !lastLevel.getGroups().isEmpty()) {
			KingGroup group = (KingGroup) lastLevel.getGroups().get(0);
			if (!group.getWinners().isEmpty()) {
				Team winnerTeam = lastLevel.getGroups().get(0).getWinners().get(0);
				// is Red.
				if (winnerTeam.equals(lastLevel.getCurrentRedTeam())) {
					// Not the last white member.
					return whiteTeams.get(whiteTeams.size() - 1).equals(lastLevel.getCurrentWhiteTeam());
				} else
				// is White
				if (winnerTeam.equals(lastLevel.getCurrentWhiteTeam())) {
					return redTeams.get(redTeams.size() - 1).equals(lastLevel.getCurrentRedTeam());
				}
			}
		}
		return false;
	}

	public void createNextLevel() throws TournamentFinishedException {
		if (needsNewLevel()) {
			KingLevel lastLevel = (KingLevel) getLastLevel();
			// Move iterators
			ListIterator<Team> redTeam = lastLevel.getRedTeam();
			ListIterator<Team> whiteTeam = lastLevel.getWhiteTeam();

			Team winner = lastLevel.getGroups().get(0).getWinners().get(0);
			if (redTeams.contains(winner)) {
				whiteTeam.next();
			} else if (whiteTeams.contains(winner)) {
				redTeam.next();
			}
			KendoLog.debug(
					this.getClass().getName(),
					"Creating level '" + (lastLevel.getLevelIndex() + 1) + "' with teams '" + redTeam.nextIndex() + "' and '"
							+ whiteTeam.nextIndex() + "'.");
			lastLevel.setNextLevel(new KingLevel(getTournament(), lastLevel.getLevelIndex() + 1, null, lastLevel, redTeam, whiteTeam));
		} else {
			throw new TournamentFinishedException("All teams has been discualified");
		}
	}

}
