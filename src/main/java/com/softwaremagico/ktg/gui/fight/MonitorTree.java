package com.softwaremagico.ktg.gui.fight;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.championship.TournamentGroupManager;

/**
 *
 * @author jorge
 */
public class MonitorTree extends LeagueEvolution {

    Tournament selectedTournament = null;

    public MonitorTree(Tournament selectedTournament) {

        try {
            KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(selectedTournament.name);

            if (KendoTournamentGenerator.getInstance().tournamentManager == null) {
                KendoTournamentGenerator.getInstance().tournamentManager = new TournamentGroupManager(selectedTournament);
                KendoTournamentGenerator.getInstance().tournamentManager.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(selectedTournament.name));
            }
        } catch (NullPointerException npe) {
        }
    }
}
