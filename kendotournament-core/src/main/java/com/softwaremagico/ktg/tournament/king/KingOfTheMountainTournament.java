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
			// Draw game.
			KingLevel lastLevel = (KingLevel) getLastLevel();
			if (lastLevel.getGroups().get(0).getFights().get(0).getWinner() == 0) {
				Team olderTeam = getOlderTeam(lastLevel);
				switch (getTournament().getDrawResolution()) {
				case BOTH_ELIMINATED:
					return null;
				case NEWEST_ELIMINATED:
					if (isInRedTeam(olderTeam)) {
						return lastLevel.getCurrentRedTeam();
					} else {
						return lastLevel.getCurrentWhiteTeam();
					}
				case OLDEST_ELIMINATED:
					if (isInRedTeam(olderTeam)) {
						return lastLevel.getCurrentWhiteTeam();
					} else {
						return lastLevel.getCurrentRedTeam();
					}
				}
			}
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

	public void initializeLevelZero() {
		if (!redTeams.isEmpty() && !whiteTeams.isEmpty()) {
			setLevelZero(new KingLevel(getTournament(), 0, null, null, redTeams.listIterator(), whiteTeams.listIterator()));
		}
	}

	@Override
	public boolean isNewLevelNeeded() {
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
			if (group.getFights().get(0).getWinner() == 0) {
				// Draw fight. Check the disqualified team.
				Team olderTeam = getOlderTeam(lastLevel);
				switch (getTournament().getDrawResolution()) {
				case BOTH_ELIMINATED:
					return whiteTeams.get(whiteTeams.size() - 1).equals(lastLevel.getCurrentWhiteTeam())
							|| redTeams.get(redTeams.size() - 1).equals(lastLevel.getCurrentRedTeam());
				case NEWEST_ELIMINATED:
					if (isInRedTeam(olderTeam)) {
						return whiteTeams.get(whiteTeams.size() - 1).equals(lastLevel.getCurrentWhiteTeam());
					} else {
						return redTeams.get(redTeams.size() - 1).equals(lastLevel.getCurrentRedTeam());
					}
				case OLDEST_ELIMINATED:
					if (isInRedTeam(olderTeam)) {
						return redTeams.get(redTeams.size() - 1).equals(lastLevel.getCurrentRedTeam());
					} else {
						return whiteTeams.get(whiteTeams.size() - 1).equals(lastLevel.getCurrentWhiteTeam());
					}
				}
			} else if (!group.getWinners().isEmpty()) {
				// Check if looser has more teams left.
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

	@Override
	public void createNextLevel() throws TournamentFinishedException {
		if (isNewLevelNeeded()) {
			KingLevel lastLevel = (KingLevel) getLastLevel();
			// Move iterators
			ListIterator<Team> redTeam = lastLevel.getRedTeam();
			ListIterator<Team> whiteTeam = lastLevel.getWhiteTeam();

			// Draw game.
			if (lastLevel.getGroups().get(0).getFights().get(0).getWinner() == 0) {
				Team olderTeam = getOlderTeam(lastLevel);
				if (olderTeam == null) {
					// Both are new teams due to a previous draw.
					whiteTeam.next();
					redTeam.next();
				} else {
					switch (getTournament().getDrawResolution()) {
					case BOTH_ELIMINATED:
						whiteTeam.next();
						redTeam.next();
						break;
					case NEWEST_ELIMINATED:
						if (isInRedTeam(olderTeam)) {
							whiteTeam.next();
						} else {
							redTeam.next();
						}
						break;
					case OLDEST_ELIMINATED:
						if (isInRedTeam(olderTeam)) {
							redTeam.next();
						} else {
							whiteTeam.next();
						}
						break;
					}
				}
			} else {
				Team winner = lastLevel.getGroups().get(0).getWinners().get(0);
				if (isInRedTeam(winner)) {
					whiteTeam.next();
				} else if (isInWhiteTeam(winner)) {
					redTeam.next();
				}
			}
			KendoLog.debug(this.getClass().getName(), "Creating level '" + (lastLevel.getLevelIndex() + 1) + "' with teams '" + redTeam.nextIndex() + "' and '"
					+ whiteTeam.nextIndex() + "'.");
			lastLevel.setNextLevel(new KingLevel(getTournament(), lastLevel.getLevelIndex() + 1, null, lastLevel, redTeam, whiteTeam));
		} else {
			throw new TournamentFinishedException("All teams has been discualified");
		}
	}

	private boolean isInRedTeam(Team team) {
		return redTeams.contains(team);
	}

	private boolean isInWhiteTeam(Team team) {
		return whiteTeams.contains(team);
	}

	private Team getOlderTeam(KingLevel level) {
		if (level.getPreviousLevel() == null) {
			return null;
		}
		List<Team> teamsOfLevel = new ArrayList<>(level.getGroups().get(0).getTeams());
		teamsOfLevel.retainAll(level.getPreviousLevel().getGroups().get(0).getTeams());
		if (!teamsOfLevel.isEmpty()) {
			return teamsOfLevel.get(0);
		}
		return null;
	}

	public void setTeams(List<Fight> fights) {
		for (Fight fight : fights) {
			if (!getRedTeams().contains(fight.getTeam1())) {
				getRedTeams().add(fight.getTeam1());
			}

			if (!getWhiteTeams().contains(fight.getTeam2())) {
				getWhiteTeams().add(fight.getTeam2());
			}
		}
		if (!fights.isEmpty()) {
			initializeLevelZero();
		}
	}
}
