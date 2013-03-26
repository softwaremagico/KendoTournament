package com.softwaremagico.ktg.database;
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

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.TimerPanel;
import java.util.List;

public class ConvertDatabase {

    private Database fromDatabase;
    private Database toDatabase;
    private final static Integer MAX_DATA_BY_STEP = 50;

    public ConvertDatabase(Database fromDatabase, Database toDatabase,
            String fromDatabasePassword, String fromDatabaseUser, String fromDatabaseDatabaseName, String fromDatabaseServer,
            String toDatabasePassword, String toDatabaseUser, String toDatabaseDatabaseName, String toDatabaseServer) {
        this.fromDatabase = fromDatabase;
        this.toDatabase = toDatabase;
        if (stablishConnection(fromDatabasePassword, fromDatabaseUser, fromDatabaseDatabaseName, fromDatabaseServer,
                toDatabasePassword, toDatabaseUser, toDatabaseDatabaseName, toDatabaseServer)) {
            startThread();
        }
    }

    private void startThread() {
        TimerPanel tp = new TimerPanel();
        ThreadConversion tc = new ThreadConversion(tp);
        tc.start();
    }

    private boolean stablishConnection(String fromDatabasePassword, String fromDatabaseUser, String fromDatabaseDatabaseName, String fromDatabaseServer,
            String toDatabasePassword, String toDatabaseUser, String toDatabaseDatabaseName, String toDatabaseServer) {
        if (fromDatabase.connect(fromDatabasePassword, fromDatabaseUser, fromDatabaseDatabaseName, fromDatabaseServer, true, false)) {
            if (toDatabase.connect(toDatabasePassword, toDatabaseUser, toDatabaseDatabaseName, toDatabaseServer, true, false)) {
                return true;
            }
        }
        return false;
    }

    public class ThreadConversion extends Thread {

        TimerPanel timerPanel;
        Translator transl;

        public ThreadConversion(TimerPanel tp) {
            transl = LanguagePool.getTranslator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.returnTag("ExportDatabaseProgressBarTitle"));
            tp.updateLabel(transl.returnTag("ExportDatabaseProgressBarLabelTournament"));
        }

        @Override
        public void run() {
            dumpData();
        }

        private boolean dumpData() {
            try {
                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTournament"), 0, 1);
                List<Tournament> tournaments = fromDatabase.getTournaments();
                toDatabase.addTournaments(tournaments);

                Integer total = 3 + tournaments.size() * 5;
                Integer current = 0;

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelClub"), current++, total);
                List<Club> clubs = fromDatabase.getClubs();
                toDatabase.addClubs(clubs);

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelCompetitor"), current++, total);
                List<RegisteredPerson> people = fromDatabase.getRegisteredPeople();
                toDatabase.addRegisteredPeople(people);

                for (Tournament tournament : tournaments) {
                    timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelRole") + " (" + tournament + ")", current++, total);
                    List<Role> roles = fromDatabase.getRoles(tournament);
                    toDatabase.addRoles(roles);

                    timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTeam" + " (" + tournament + ")"), current++, total);
                    List<Team> teams = fromDatabase.getTeams(tournament);
                    toDatabase.addTeams(teams);

                    timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight" + " (" + tournament + ")"), current++, total);
                    List<Fight> fights = fromDatabase.getFights(tournament);
                    toDatabase.addFights(fights);

                    timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight" + " (" + tournament + ")"), current++, total);
                    List<Duel> duels = fromDatabase.getDuels(tournament);
                    toDatabase.addDuels(duels);

                    timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight" + " (" + tournament + ")"), current++, total);
                    List<Undraw> undraws = fromDatabase.getUndraws(tournament);
                    toDatabase.addUndraws(undraws);
                }

                timerPanel.dispose();
                MessageManager.informationMessage(this.getClass().getName(), "ConversionCompleted", "Database");
            } catch (Exception e) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), e);
                timerPanel.dispose();
                return false;
            }
            return true;
        }
    }
}
