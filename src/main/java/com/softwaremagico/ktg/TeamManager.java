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

import com.softwaremagico.ktg.database.DatabaseConnection;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class TeamManager {

    private static HashMap<String, Team> existingTeams = new HashMap<>();
    private Tournament tournament;

    TeamManager(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam(String teamName) {
        if (teamName == null) {
            return null;
        }
        Team team = existingTeams.get(teamName);
        if (team == null) {
            team = DatabaseConnection.getInstance().getDatabase().getTeamByName(teamName, tournament, false);
            existingTeams.put(teamName, team);
        }
        return team;
    }
    
    public void addTeam(Team team){
        existingTeams.put(team.getName(), team);
    }
}
