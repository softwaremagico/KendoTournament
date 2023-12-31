package com.softwaremagico.ktg.tournament;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
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

import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.king.TournamentFinishedException;

public interface ITournamentManager {

	/**
	 * Generate fights of level.
	 *
	 * @param level
	 * @return
	 */
	List<Fight> getFights(Integer level);

	List<Fight> createRandomFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException;

	List<Fight> createSortedFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException;

	int getNumberOfFightsFinished();

	void fillGroups();

	List<TGroup> getGroups();

	List<TGroup> getGroups(Integer level);

	List<TGroup> getGroupsByShiajo(Integer shiaijo);

	TGroup getGroup(Fight fight);

	void addGroup(TGroup group);

	int getIndexOfGroup(TGroup group);

	void removeGroup(Integer level, Integer groupIndex);

	void removeGroup(TGroup group);

	void removeGroups(Integer level);

	int getIndex(Integer level, TGroup group);

	Level getLevel(Integer level);

	Integer getNumberOfLevels();

	Integer getLastLevelUsed();

	boolean exist(Team team);

	void removeTeams(Integer level);

	void removeTeams();

	/**
	 * Divide groups into fight areas.
	 */
	void setDefaultFightAreas();

	void setHowManyTeamsOfGroupPassToTheTree(Integer winners);

	/**
	 * We are in the final fight of the tournament.
	 *
	 * @return
	 */
	boolean isTheLastFight();

	/**
	 * Remove all fights of all groups.
	 */
	void resetFights();

	/**
	 * Returns the level where still are fights not finished.
	 * 
	 * @return
	 */
	Level getCurrentLevel();

	Tournament getTournament();

	void setTournament(Tournament tournament);

	List<Level> getLevels();

	/**
	 * Unset the winners of the selected level.
	 * 
	 * @param level
	 */
	void removeWinners(Integer level);

	Level getLastLevel();

	boolean isNewLevelNeeded();

	void createNextLevel() throws TournamentFinishedException;

	/**
	 * Defines if a fight has a draw value or not.
	 * 
	 * @param fight
	 * @return
	 */
	boolean hasDrawScore(TGroup group);
}
