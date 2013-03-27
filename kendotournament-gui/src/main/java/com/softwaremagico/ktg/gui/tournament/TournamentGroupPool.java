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

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import java.util.HashMap;
import java.util.List;

public class TournamentGroupPool {

    private static HashMap<Tournament, TournamentGroupManager> existingManagers = new HashMap<>();

    private TournamentGroupPool() {
    }

    public static TournamentGroupManager getManager(Tournament tournament) {
        TournamentGroupManager tournamentGroupManager = existingManagers.get(tournament);
        if (tournamentGroupManager == null) {
            tournamentGroupManager = createGroupManager(tournament);
            existingManagers.put(tournament, tournamentGroupManager);
            List<Fight> fights = FightPool.getInstance().get(tournament);
            if (fights != null) {
                tournamentGroupManager.refillDesigner();
            }
        }
        return tournamentGroupManager;
    }

    private static TournamentGroupManager createGroupManager(Tournament tournament) {
        return new TournamentGroupManager(tournament);
    }

    public static void cleanGroupManager(Tournament tournament) {
        TournamentGroupManager tournamentGroupManager = new TournamentGroupManager(tournament);
        tournamentGroupManager.createLevelZero();
        tournamentGroupManager.setMode(tournament.getMode());
        existingManagers.put(tournament, tournamentGroupManager);
    }

    public static void replaceGroupManager(TournamentGroupManager tournamentGroupManager) {
        existingManagers.put(tournamentGroupManager.tournament, tournamentGroupManager);
    }

    public static void updateGroupManagers(Tournament tournament) {
        TournamentGroupManager tournamentGroupManager = getManager(tournament);
        tournamentGroupManager.refillDesigner();
    }
}
