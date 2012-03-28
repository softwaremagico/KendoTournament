/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroups;

/**
 *
 * @author jorge
 */
public class MonitorTree extends LeagueEvolution {

    Tournament selectedTournament = null;

    public MonitorTree(Tournament selectedTournament) {

        try {
            KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(selectedTournament.name);

            if (KendoTournamentGenerator.getInstance().designedGroups == null) {
                KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(selectedTournament, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(selectedTournament.name));
            }
        } catch (NullPointerException npe) {
        }
    }
}
