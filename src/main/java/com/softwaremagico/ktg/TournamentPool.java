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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jhortelano
 */
public class TournamentPool {

    private static HashMap<String, Tournament> existingTournaments = new HashMap<>();

    private TournamentPool() {
    }

    static {
        loadAllTournaments();
    }

    public static Tournament getTournament(String tournamentName) {
        if (tournamentName == null || tournamentName.equals("All")) {
            return null;
        }
        Tournament tournament = existingTournaments.get(tournamentName);
        if (tournament == null) {
            tournament = DatabaseConnection.getInstance().getDatabase().getTournamentByName(tournamentName, false);
            existingTournaments.put(tournamentName, tournament);
        }
        return tournament;
    }

    private static void loadAllTournaments() {
        existingTournaments = new HashMap<>();
        List<Tournament> tournaments = DatabaseConnection.getInstance().getDatabase().getAllTournaments();
        for (Tournament tournament : tournaments) {
            addTournament(tournament);
        }
    }

    public static void addTournament(Tournament tournament) {
        existingTournaments.put(tournament.getName(), tournament);
    }

    public static void updateTournament(Tournament tournament) {
        addTournament(tournament);
    }
    
    public static List<Tournament> getAllTournaments(){
        return new ArrayList(existingTournaments.values());
    }
}
