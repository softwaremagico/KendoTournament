package com.softwaremagico.ktg.championship;
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

import com.softwaremagico.ktg.Tournament;

/**
 *
 * @author jhortelano
 */
public class LevelGroupsChampionship extends LevelGroups {

    LevelGroupsChampionship(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        super(tournament, level, nextLevel, previousLevel, groupManager);
    }

    @Override
    protected LevelGroups addNewLevel(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        return new LevelGroupsChampionship(tournament, level, nextLevel, previousLevel, groupManager);
    }

    private Integer obtainPositionOfOneWinnerInTreeLevelZeroPair(int branch, int branchs) {
        if (branch % 2 == 0) {
            return branch / 2;
        } else {
            return (branchs - branch) / 2;
        }
    }

    private Integer obtainPositionOfOneWinnerInTreeLevelZeroOdd(int branch, int branchs) {
        if (branch % 2 == 0) {
            return branch / 2;
        } else {
            return ((branch + 1) % branchs) / 2;
        }
    }

    @Override
    protected Integer getPositonOfOneWinnerInTournament(int branch, int branchs) {
        if (level == 0) {
            if ((size() % 2) == 0) {
                return obtainPositionOfOneWinnerInTreeLevelZeroPair(branch, branchs);
            } else {
                return obtainPositionOfOneWinnerInTreeLevelZeroOdd(branch, branchs);
            }
        } else {
            return obtainPositionOfOneWinnerInTree(branch, branchs);
        }

    }
}
