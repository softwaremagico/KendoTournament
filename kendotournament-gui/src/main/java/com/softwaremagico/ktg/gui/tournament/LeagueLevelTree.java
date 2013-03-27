package com.softwaremagico.ktg.gui.tournament;
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

import com.softwaremagico.ktg.gui.tournament.LeagueLevel;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TournamentGroup;

public class LeagueLevelTree extends LeagueLevel {

    LeagueLevelTree(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel, TournamentGroupManager groupManager) {
        super(tournament, level, nextLevel, previousLevel, groupManager);
    }

    @Override
    protected LeagueLevel addNewLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel, TournamentGroupManager groupManager) {
        return new LeagueLevelTree(tournament, level, nextLevel, previousLevel, groupManager);
    }

    @Override
    protected  Integer getGroupIndexDestinationOfWinner(TournamentGroup group, int winner) {
        int winnerTeams = getNumberOfTotalTeamsPassNextRound(); // [1..N]
        int winnerIndex = getGlobalPositionWinner(group, winner); // [0..N-1]
        return obtainPositionOfOneWinnerInTree(winnerIndex, winnerTeams);
    }
}
