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
    
    List<Fight> getRandomFights();
    
    List<Fight> getSortedFights();

    List<TournamentGroup> getGroups();

    List<TournamentGroup> getGroups(Integer level);

    TournamentGroup getGroup(Fight fight);

    void addGroup(TournamentGroup group);

    void removeGroup(Integer level, Integer groupIndex);

    void removeGroup(TournamentGroup group);

    void removeGroups(Integer level);

    LeagueLevel getLevel(Integer level);

    Integer getNumberOfLevels();

    Integer getLastLevelUsed();

    boolean exist(Team team);

    boolean allGroupsHaveNextLink();

    void addLink(TournamentGroup source, TournamentGroup address);

    void removeLinks();

    void deleteTeams(Integer level);

    /**
     * Divide groups into fight areas.
     */
    void setDefaultFightAreas();
}
