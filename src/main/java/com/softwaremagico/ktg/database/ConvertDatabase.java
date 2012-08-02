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

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.TimerPanel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LOCAL\jhortelano
 */
public class ConvertDatabase {

    private Database fromDatabase;
    private Database toDatabase;

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
                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTournament"), 1, 7);
                List<Tournament> tournaments = fromDatabase.getAllTournaments();
                if (!toDatabase.storeAllTournaments(tournaments)) {
                    return false;
                }
                tournaments.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelClub"), 2, 7);
                List<Club> clubs = fromDatabase.getAllClubs();
                if (!toDatabase.storeAllClubs(clubs)) {
                    return false;
                }
                clubs.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelCompetitor"), 3, 7);
                List<CompetitorWithPhoto> competitors = fromDatabase.getAllCompetitorsWithPhoto();
                if (!toDatabase.storeAllCompetitors(competitors)) {
                    return false;
                }
                competitors.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelRole"), 4, 7);
                List<Role> roles = fromDatabase.getAllRoles();
                if (!toDatabase.storeAllRoles(roles)) {
                    return false;
                }
                roles.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTeam"), 5, 7);
                List<Team> teams = fromDatabase.getAllTeams();
                if (!toDatabase.storeAllTeams(teams)) {
                    return false;
                }
                teams.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight"), 6, 7);
                ArrayList<Fight> fights = fromDatabase.getAllFights();
                if (!toDatabase.storeAllFightsAndDeleteOldOnes(fights)) {
                    return false;
                }
                fights.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight"), 7, 7);
                List<Undraw> undraws = fromDatabase.getAllUndraws();
                if (!toDatabase.storeAllUndraws(undraws)) {
                    return false;
                }
                undraws.clear();

                timerPanel.dispose();
                MessageManager.informationMessage("ConversionCompleted", "Database");
            } catch (Exception e) {
                KendoTournamentGenerator.getInstance().showErrorInformation(e);
                timerPanel.dispose();
                return false;
            }
            return true;
        }
    }
}
