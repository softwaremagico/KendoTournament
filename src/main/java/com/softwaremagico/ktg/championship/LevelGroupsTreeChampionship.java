/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class LevelGroupsTreeChampionship extends LevelGroups {

    LevelGroupsTreeChampionship(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        super(tournament, level, nextLevel, previousLevel, groupManager);
    }

    @Override
    protected LevelGroups addNewLevel(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        return new LevelGroupsTreeChampionship(tournament, level, nextLevel, previousLevel, groupManager);
    }

    private Integer obtainPositionOfOneWinnerInTree(int branch, int branchs) {
        //Design a tree grouping the designed groups by two.
        if ((branchs / 2) % 2 == 0) {
            return (branch);
        } else {
            //If the number of groups are odd, are one group that never fightManager. Then, shuffle it.
            return obtainPositionOfOneWinnerInTreeOdd(branch, branchs);
        }
    }

    @Override
    protected Integer getPositonOfOneWinnerInTournament(int branch, int branchs) {
        return obtainPositionOfOneWinnerInTree(branch, branchs) / 2;
    }
}
