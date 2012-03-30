/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Tournament;
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
            dumpData();
        }
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

    private boolean dumpData() {
        try {
            System.out.println("Transferring Tournaments");
            List<Tournament> tournaments = fromDatabase.getAllTournaments();
            System.out.println("Obtained Tournaments");
            if (toDatabase.storeAllTournaments(tournaments)) {
                return false;
            }
            tournaments.clear();

            /*
             * System.out.println("Transferring Clubs"); List<Club> clubs =
             * fromDatabase.getAllClubs(); toDatabase.storeAllClubs(clubs);
             * clubs.clear();
             *
             * System.out.println("Transferring Competitors");
             * List<CompetitorWithPhoto> competitors =
             * fromDatabase.getAllCompetitorsWithPhoto();
             * toDatabase.storeAllCompetitors(competitors); competitors.clear();
             *
             * System.out.println("Transferring Teams"); List<Team> teams =
             * fromDatabase.getAllTeams(); toDatabase.storeAllTeams(teams);
             * teams.clear();
             *
             * System.out.println("Transferring Fights"); List<Fight> fights =
             * fromDatabase.getAllFights(); toDatabase.storeAllFights(fights);
             * fights.clear();
             *
             * System.out.println("Transferring Roles"); List<Role> roles =
             * fromDatabase.getAllRoles(); toDatabase.storeAllRoles(roles);
             * roles.clear();
             *
             * System.out.println("Transferring Undraws"); List<Undraw> undraws
             * = fromDatabase.getAllUndraws();
             * toDatabase.storeAllUndraws(undraws); undraws.clear();
             */
            MessageManager.informationManager("ConversionCompleted", "Database", KendoTournamentGenerator.getInstance().language, true);
        } catch (Exception e) {
            KendoTournamentGenerator.getInstance().showErrorInformation(e);
            return false;
        }

        return true;
    }
}
