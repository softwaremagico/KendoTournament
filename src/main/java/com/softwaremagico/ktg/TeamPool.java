/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;
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
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jorge
 */
public class TeamPool {

    private static HashMap<Tournament, TeamManager> existingManagers = new HashMap<>();

    public static TeamManager getManager(Tournament tournament) {
        TeamManager teamManager = existingManagers.get(tournament);
        if (teamManager == null) {
            teamManager = createTeamManager(tournament);
            existingManagers.put(tournament, teamManager);
        }
        return teamManager;
    }

    private static TeamManager createTeamManager(Tournament tournament) {
        TeamManager teamManager = new TeamManager(tournament);
        return teamManager;
    }

    public static List<Team> getTeamsByLevel(Tournament tournament, Integer level, boolean verbose) {
        List<String> databaseTeams = KendoTournamentGenerator.getInstance().database.getTeamsNameByLevel(tournament, level, false);
        //Team in database is not the same object that team in java heap.
        List<Team> includedTeams = new ArrayList<>();
        for (String teamName : databaseTeams) {
            includedTeams.add(getManager(tournament).getTeam(teamName));
        }
        return includedTeams;
    }
}
