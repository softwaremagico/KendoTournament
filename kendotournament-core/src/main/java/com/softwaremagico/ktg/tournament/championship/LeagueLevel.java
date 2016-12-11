package com.softwaremagico.ktg.tournament.championship;

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
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.Level;
import com.softwaremagico.ktg.tournament.TGroup;

public abstract class LeagueLevel extends Level {
	private static final long serialVersionUID = -2913139758103386033L;

	protected LeagueLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		super(tournament, level, nextLevel, previousLevel);
	}

	public void fillGroups(Fight fight) {
		while (fight.getGroup() >= getGroups().size()) {
			addGroup(new TreeTournamentGroup(fight.getTournament(), fight.getLevel(), fight.getAsignedFightArea()));
		}
		TGroup group = getGroups().get(fight.getGroup());
		group.addFight(fight);
	}

	protected int getGlobalPositionWinner(TGroup group, Integer winner) {
		int total = 0;

		for (int i = 0; i < getTournamentGroups().size() && i < getIndexOfGroup(group); i++) {
			if (getLevelIndex() > 0) {
				total++;
			} else {
				total += getTournamentGroups().get(i).getMaxNumberOfWinners();
			}
		}
		total += winner;
		return total;
	}

	/**
	 * Not in all levels the arenas used are the arenas available.
	 * 
	 * @return
	 */
	protected int getArenasUsed() {
		List<Integer> arenas = new ArrayList<>();
		for (TGroup tournamentGroup : getTournamentGroups()) {
			if (!arenas.contains(tournamentGroup.getFightArea())) {
				arenas.add(tournamentGroup.getFightArea());
			}
		}
		return arenas.size();
	}

	@Override
	public void update() {
		fillTeamsWithWinnersPreviousLevel();
	}

	/**
	 * Update level with winners of previous level.
	 */
	private void fillTeamsWithWinnersPreviousLevel() {
		if (getPreviousLevel() != null && !getPreviousLevel().getGroups().isEmpty()) {
			for (int winner = 0; winner < getTournament().getHowManyTeamsOfGroupPassToTheTree(); winner++) {
				for (TGroup previousLevelGroup : getPreviousLevel().getGroups()) {
					// Add winners only if created
					if (winner < previousLevelGroup.getWinners().size()) {
						TGroup group = getPreviousLevel().getGroupDestinationOfWinner(previousLevelGroup, winner);
						group.addTeam(previousLevelGroup.getWinners().get(winner));
					}
				}
			}
		}
	}



	/**
	 ********************************************* 
	 * 
	 * GROUPS MANIPULATION
	 * 
	 ********************************************* 
	 */
	/**
	 * Update the number of groups according of the size of the previous level.
	 */
	@Override
	public void updateGroupsSize() {
		while ((getPreviousLevel() != null) && ((getPreviousLevel().getNumberOfTotalTeamsPassNextRound() + 1) / 2 > this.size())) {
			addGroup(new TreeTournamentGroup(getTournament(), getLevelIndex(), 0));
		}

		// When we remove two groups in one level, we must remove one in the
		// next one.
		while ((getPreviousLevel() != null)
				&& (Math.ceil((float) getPreviousLevel().getNumberOfTotalTeamsPassNextRound() / 2) < this.size())) {
			removeGroup();
		}

		updateArenaOfGroups();

		// If only one group. It is lastlevel
		if (getTournamentGroups().size() < 2) {
			setNextLevel(null);
		}

		if (getNextLevel() != null) {
			getNextLevel().updateGroupsSize();
		}
	}

	@Override
	public void addGroup(TGroup group) {
		super.addGroup(group);

		if ((getNextLevel() == null) && ((getNumberOfTotalTeamsPassNextRound() > 1) || getTournamentGroups().size() > 1)) {
			setNextLevel(createNewLevel(getTournament(), getLevelIndex() + 1, null, this));
		}

		if (getNextLevel() != null) {
			getNextLevel().updateGroupsSize();
		}
	}

	/**
	 * 
	 * @return true if next Level must be deleted.
	 */
	protected boolean removeGroup() {
		if (getTournamentGroups().size() > 0) {
			getTournamentGroups().remove(getTournamentGroups().size() - 1);
			if (getNextLevel() != null) {
				getNextLevel().updateGroupsSize();
				if (getNextLevel().size() == 0) {
					setNextLevel(null);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 ********************************************* 
	 * 
	 * WINNER DESTINATION
	 * 
	 ********************************************* 
	 */
	/**
	 * Position of a group if the tree has odd number of leaves.
	 * 
	 * @param branch
	 * @param branchs
	 * @return
	 */
	protected Integer obtainPositionOfOneWinnerInTreeOdd(Integer branch, Integer branchs) {
		return ((branch + 1) % branchs) / 2;
	}

	protected Integer obtainPositionOfOneWinnerInTreePair(Integer branch, Integer branchs) {
		return (branch / 2);
	}

	protected Integer obtainPositionOfOneWinnerInTree(Integer branch, Integer branchs) {
		// Design a tree grouping the designed groups by two.
		if ((branchs / 2) % 2 == 0) {
			return obtainPositionOfOneWinnerInTreePair(branch, branchs);
		} else {
			// If the number of groups are odd, are one group that never fights.
			// Then, shuffle it.
			return obtainPositionOfOneWinnerInTreeOdd(branch, branchs);
		}
	}

	protected Integer getGroupIndexSourceOfWinner(TGroup group, Integer winner) {
		if (getLevelIndex() > 0) {
			for (int groupIndex = 0; groupIndex < getPreviousLevel().getGroups().size(); groupIndex++) {
				if (getPreviousLevel().getGroupDestinationOfWinner(getPreviousLevel().getGroups().get(groupIndex), winner).equals(group)) {
					return groupIndex;
				}
			}
		}
		return null;
	}

	protected TGroup getGroupSourceOfWinner(TGroup group, Integer winner) {
		return getPreviousLevel().getGroups().get(getGroupIndexSourceOfWinner(group, winner));
	}

	@Override
	public String toString() {
		String text = "-------------------------------------\n";
		text += "Level: " + getLevelIndex() + "\n";
		for (TGroup group : getTournamentGroups()) {
			text += group + "\n";
		}
		text += "-------------------------------------\n";
		if (getNextLevel() != null) {
			text += getNextLevel();
		}
		return text;
	}

}
