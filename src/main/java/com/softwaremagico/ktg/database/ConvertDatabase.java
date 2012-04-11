/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.*;
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
            transl = new Translator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.returnTag("ExportDatabaseProgressBarTitle", KendoTournamentGenerator.getInstance().language));
            tp.updateLabel(transl.returnTag("ExportDatabaseProgressBarLabelTournament", KendoTournamentGenerator.getInstance().language));
        }

        @Override
        public void run() {
            dumpData();
        }

        private boolean dumpData() {
            try {
                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTournament", KendoTournamentGenerator.getInstance().language), 1, 7);
                List<Tournament> tournaments = fromDatabase.getAllTournaments();
                if (!toDatabase.storeAllTournaments(tournaments)) {
                    return false;
                }
                tournaments.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelClub", KendoTournamentGenerator.getInstance().language), 2, 7);
                List<Club> clubs = fromDatabase.getAllClubs();
                if (!toDatabase.storeAllClubs(clubs)) {
                    return false;
                }
                clubs.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelCompetitor", KendoTournamentGenerator.getInstance().language), 3, 7);
                List<CompetitorWithPhoto> competitors = fromDatabase.getAllCompetitorsWithPhoto();
                if (!toDatabase.storeAllCompetitors(competitors)) {
                    return false;
                }
                competitors.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelRole", KendoTournamentGenerator.getInstance().language), 4, 7);
                List<Role> roles = fromDatabase.getAllRoles();
                if (!toDatabase.storeAllRoles(roles)) {
                    return false;
                }
                roles.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTeam", KendoTournamentGenerator.getInstance().language), 5, 7);
                List<Team> teams = fromDatabase.getAllTeams();
                if (!toDatabase.storeAllTeams(teams)) {
                    return false;
                }
                teams.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight", KendoTournamentGenerator.getInstance().language), 6, 7);
                ArrayList<Fight> fights = fromDatabase.getAllFights();
                if (!toDatabase.storeAllFightsAndDeleteOldOnes(fights)) {
                    return false;
                }
                fights.clear();

                timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight", KendoTournamentGenerator.getInstance().language), 7, 7);
                List<Undraw> undraws = fromDatabase.getAllUndraws();
                if (!toDatabase.storeAllUndraws(undraws)) {
                    return false;
                }
                undraws.clear();

                timerPanel.dispose();
                MessageManager.informationManager("ConversionCompleted", "Database", KendoTournamentGenerator.getInstance().language, true);
            } catch (Exception e) {
                KendoTournamentGenerator.getInstance().showErrorInformation(e);
                timerPanel.dispose();
                return false;
            }
            return true;
        }
    }
}
