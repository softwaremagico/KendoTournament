package com.softwaremagico.ktg.tournament;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.softwaremagico.ktg.core.Tournament;

public class LeagueLevelChampionship extends LeagueLevel {
	private static final long serialVersionUID = -2490242867449978156L;

	protected LeagueLevelChampionship(Tournament tournament, int level, LeagueLevel nextLevel,
			LeagueLevel previousLevel) {
		super(tournament, level, nextLevel, previousLevel);
		if (level > 0) { // Inner levels always have at least one group.
			addGroup(new TreeTournamentGroup(tournament, level, 0));
		}
	}

	@Override
	protected LeagueLevel addNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel,
			LeagueLevel previousLevel) {
		return new LeagueLevelChampionship(tournament, level, nextLevel, previousLevel);
	}

	/**
	 * Even number of groups.
	 *
	 * @param branch
	 * @param branchs
	 * @return
	 */
	private Integer obtainPositionOfOneWinnerEven(Integer branch, Integer branchs) {
		if (branch % 2 == 0) {
			return branch / 2;
		} else {
			return (branchs - branch) / 2;
		}
	}

	private Integer obtainPositionOfWinner(Integer branch, Integer branchs) {
		return branch / 2;
	}

	/**
	 * Odd number of groups
	 *
	 * @param branch
	 * @param branchs
	 * @return
	 */
	private Integer obtainPositionOfOneWinnerOdd(Integer branch, Integer branchs) {
		if (branch % 2 == 0) {
			return branch / 2;
		} else {
			return ((branch + 1) % branchs) / 2;
		}
	}

	@Override
	public Integer getGroupIndexDestinationOfWinner(TGroup group, Integer winner) {
		Integer winnerTeams = getNumberOfTotalTeamsPassNextRound(); // [1..N]
		Integer winnerIndex = getGlobalPositionWinner(group, winner); // [0..N-1]
		// If inner level or tree championship
		if ((previousLevel != null || tournament.getHowManyTeamsOfGroupPassToTheTree() == 1)
				&& this.getGroups().size() % 2 == 0) {
			return obtainPositionOfWinner(winnerIndex, winnerTeams);
		} else {
			if ((size() % 2) == 0) {
				return obtainPositionOfOneWinnerEven(winnerIndex, winnerTeams);
			} else {
				return obtainPositionOfOneWinnerOdd(winnerIndex, winnerTeams);
			}
		}
	}
}
