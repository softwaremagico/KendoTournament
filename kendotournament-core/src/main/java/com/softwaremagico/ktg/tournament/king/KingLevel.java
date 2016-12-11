package com.softwaremagico.ktg.tournament.king;

import java.util.ListIterator;

import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.Level;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.championship.LeagueLevel;

public class KingLevel extends Level {
	private static final long serialVersionUID = -1706037125819985383L;
	private ListIterator<Team> redTeam;
	private ListIterator<Team> whiteTeam;

	protected KingLevel(Tournament tournament, int level, Level nextLevel, Level previousLevel, ListIterator<Team> redTeam,
			ListIterator<Team> whiteTeam) {
		super(tournament, level, nextLevel, previousLevel);
		this.redTeam = redTeam;
		this.whiteTeam = whiteTeam;
		addGroup(new KingGroup(tournament, level, 0));
		fillTeams();
	}

	@Override
	protected Level createNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		return null;
	}

	@Override
	public void update() {

	}

	private TGroup getGroup() {
		return getGroups().get(0);
	}

	@Override
	public int getNumberOfTotalTeamsPassNextRound() {
		if (!getGroups().isEmpty() && !getGroups().get(0).getFights().isEmpty()) {
			int winner = getGroups().get(0).getFights().get(0).getWinner();
			// e have a winner and the other team list has available teams.
			if (winner < 0 && whiteTeam.hasNext()) {
				return 2;
			} else if (winner == 1 && redTeam.hasNext()) {
				return 2;
			} else if (winner == 0 && redTeam.hasNext() && whiteTeam.hasNext()) {
				return 2;
			}
		}
		return 0;
	}

	/**
	 * Update level with winners of previous level.
	 */
	private void fillTeams() {
		if (getPreviousLevel() != null) {
			if (!getPreviousLevel().getGroups().isEmpty()) {
				getGroup().addTeam(getCurrentRedTeam());
				getGroup().addTeam(getCurrentWhiteTeam());
			}
		} else {
			// Initialize group with new team of both lists.
			getGroup().addTeam(getCurrentRedTeam());
			getGroup().addTeam(getCurrentWhiteTeam());
		}
	}

	public Team getCurrentRedTeam() {
		return getCurrentTeam(redTeam);
	}

	public Team getCurrentWhiteTeam() {
		return getCurrentTeam(whiteTeam);
	}

	/**
	 * Get current team without moving the iterator.
	 * 
	 * @param iterator
	 * @return
	 */
	private Team getCurrentTeam(ListIterator<Team> iterator) {
		if (!iterator.hasNext()) {
			return null;
		}
		Team team = iterator.next();
		// Move backwards the iterator.
		iterator.previous();
		return team;
	}

	public ListIterator<Team> getRedTeam() {
		return redTeam;
	}

	public ListIterator<Team> getWhiteTeam() {
		return whiteTeam;
	}

	@Override
	public String toString() {
		return "Level: " + getLevelIndex();
	}
}
