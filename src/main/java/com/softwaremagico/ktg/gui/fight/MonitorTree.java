/*
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 02-ene-2009.
 */
package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.championship.DesignedGroups;

/**
 *
 * @author jorge
 */
public class MonitorTree extends LeagueEvolution {

    Tournament selectedTournament = null;

    public MonitorTree(Tournament selectedTournament) {

        try {
            KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(selectedTournament.name);

            if (KendoTournamentGenerator.getInstance().designedGroups == null) {
                KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(selectedTournament, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(selectedTournament.name));
            }
        } catch (NullPointerException npe) {
        }
    }
}
