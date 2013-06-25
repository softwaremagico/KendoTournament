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

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public interface ITournamentManager {

    /**
     * Generate fights of level.
     * @param level
     * @return 
     */
    List<Fight> getFights(Integer level);
    
    List<Fight> createRandomFights(Integer level);
    
    List<Fight> createSortedFights(Integer level);
    
    void fillGroups();
    
    List<TGroup> getGroups();

    List<TGroup> getGroups(Integer level);

    TGroup getGroup(Fight fight);

    void addGroup(TGroup group);

    void removeGroup(Integer level, Integer groupIndex);

    void removeGroup(TGroup group);

    void removeGroups(Integer level);
    
    int getIndex(Integer level, TGroup group);

    LeagueLevel getLevel(Integer level);

    Integer getNumberOfLevels();

    Integer getLastLevelUsed();

    boolean exist(Team team);

    void removeTeams(Integer level);

    /**
     * Divide groups into fight areas.
     */
    void setDefaultFightAreas();
    
    void setHowManyTeamsOfGroupPassToTheTree(Integer winners);
}
