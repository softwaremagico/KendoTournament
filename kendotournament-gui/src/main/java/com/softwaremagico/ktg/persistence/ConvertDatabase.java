package com.softwaremagico.ktg.persistence;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
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

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.lists.TimerPanel;
import java.sql.SQLException;
import java.util.List;

public class ConvertDatabase {

    private Database fromDatabase;
    private Database toDatabase;

    public ConvertDatabase(Database fromDatabase, Database toDatabase) {
        this.fromDatabase = fromDatabase;
        this.toDatabase = toDatabase;
    }

    private void startThread() {
        TimerPanel tp = new TimerPanel();
        ThreadConversion tc = new ThreadConversion(tp);
        tc.start();
    }

    public void stablishConnection(String fromDatabasePassword, String fromDatabaseUser, String fromDatabaseDatabaseName, String fromDatabaseServer,
            String toDatabasePassword, String toDatabaseUser, String toDatabaseDatabaseName, String toDatabaseServer) throws CommunicationsException {
        if (createConnection(fromDatabasePassword, fromDatabaseUser, fromDatabaseDatabaseName, fromDatabaseServer,
                toDatabasePassword, toDatabaseUser, toDatabaseDatabaseName, toDatabaseServer)) {
            startThread();
        }
    }

    private boolean createConnection(String fromDatabasePassword, String fromDatabaseUser, String fromDatabaseDatabaseName, String fromDatabaseServer,
            String toDatabasePassword, String toDatabaseUser, String toDatabaseDatabaseName, String toDatabaseServer) throws CommunicationsException {
        try {
            if (fromDatabase.connect(fromDatabasePassword, fromDatabaseUser, fromDatabaseDatabaseName, fromDatabaseServer, true, true)) {
                if (toDatabase.connect(toDatabasePassword, toDatabaseUser, toDatabaseDatabaseName, toDatabaseServer, true, true)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        return false;
    }

    public class ThreadConversion extends Thread {

        TimerPanel timerPanel;
        Translator transl;

        public ThreadConversion(TimerPanel tp) {
            transl = LanguagePool.getTranslator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.getTranslatedText("ExportDatabaseProgressBarTitle"));
            tp.updateLabel(transl.getTranslatedText("ExportDatabaseProgressBarLabelTournament"));
        }

        @Override
        public void run() {
            dumpData();
        }

        private boolean dumpData() {
            try {
                timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelTournament"), 0, 1);
                List<Tournament> tournaments = fromDatabase.getTournaments();
                toDatabase.addTournaments(tournaments);
                
                Integer total = 3 + tournaments.size() * 5;
                Integer current = 0;

                timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelClub"), current++, total);
                List<Club> clubs = fromDatabase.getClubs();
                toDatabase.addClubs(clubs);

                timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelCompetitor"), current++, total);
                List<RegisteredPerson> people = fromDatabase.getRegisteredPeople();
                toDatabase.addRegisteredPeople(people);

                for (Tournament tournament : tournaments) {
                    timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelRole") + " (" + tournament + ")", current++, total);
                    List<Role> roles = fromDatabase.getRoles(tournament);
                    toDatabase.addRoles(roles);

                    timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelTeam" + " (" + tournament + ")"), current++, total);
                    List<Team> teams = fromDatabase.getTeams(tournament);
                    toDatabase.addTeams(teams);

                    timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelFight" + " (" + tournament + ")"), current++, total);
                    List<Fight> fights = fromDatabase.getFights(tournament);
                    toDatabase.addFights(fights);

                    timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelFight" + " (" + tournament + ")"), current++, total);
                    List<Duel> duels = fromDatabase.getDuels(tournament);
                    toDatabase.addDuels(duels);

                    timerPanel.updateText(transl.getTranslatedText("ExportDatabaseProgressBarLabelFight" + " (" + tournament + ")"), current++, total);
                    List<Undraw> undraws = fromDatabase.getUndraws(tournament);
                    toDatabase.addUndraws(undraws);
                }

                timerPanel.dispose();
                AlertManager.informationMessage(this.getClass().getName(), "ConversionCompleted", "Database");
            } catch (SQLException | TeamMemberOrderException e) {
                AlertManager.showErrorInformation(this.getClass().getName(), e);
                timerPanel.dispose();
                return false;
            }
            return true;
        }
    }
}
