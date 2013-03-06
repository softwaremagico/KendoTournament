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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jorge
 */
public abstract class Database {

    Connection connection = null;

    public Database() {
    }

    /**
     * Connect to the database
     *
     * @param tmp_password database password.
     * @param tmp_user user database.
     * @param tmp_database satabase schema.
     * @param tmp_server server IP
     * @param verbose show error messages.
     * @param retry do another try if can solve the SQL problem.
     * @return true if the connection is ok.
     */
    public abstract boolean connect(String password, String user, String database, String server, boolean verbose, boolean retry);

    public abstract void disconnect() throws SQLException;

    public void disconnectDatabase() throws SQLException {
        DatabaseConnection.getInstance().setDatabaseConnected(false);
        disconnect();
    }

    abstract void startDatabase();

    void showCommand(String[] commands) {
        for (int i = 0; i < commands.length; i++) {
            System.out.print(commands[i] + " ");
        }
        System.out.println();
    }

    void showCommandOutput(Process child) throws IOException {
        try (InputStream in = child.getInputStream()) {
            int c;
            while ((c = in.read()) != -1) {
                System.out.print((char) c);
            }
        }

        BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));
        String s;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    abstract void installDatabase(String password, String user, String server, String database);

    abstract boolean isDatabaseInstalledCorrectly(boolean verbose);

    public abstract boolean updateDatabase(String path, boolean verbose);

    public abstract void clearDatabase();

    /**
     * The database only allows local connections (such as SQLite).
     *
     * @return
     */
    public abstract boolean onlyLocalConnection();

    /**
     * *******************************************************************
     *
     * COMPETITOR
     *
     ********************************************************************
     */
    /**
     * *
     *
     */
    protected abstract boolean addRegisteredPeople(List<RegisteredPerson> roles);

    protected abstract List<RegisteredPerson> getRegisteredPeople();

    protected abstract boolean removeRegisteredPeople(List<RegisteredPerson> roles);

    protected abstract boolean updateRegisteredPeople(HashMap<RegisteredPerson, RegisteredPerson> rolesExchange);

    protected abstract Photo getPhoto(String competitorId);

    protected abstract boolean setPhotos(List<Photo> photos);

    /**
     * *******************************************************************
     *
     * ROLE
     *
     ********************************************************************
     */
    /**
     * Store a Role into the database.
     */
    protected abstract boolean addRoles(Tournament tournament, List<Role> roles);

    protected abstract List<Role> getRoles(Tournament tournament);

    protected abstract boolean removeRoles(Tournament tournament, List<Role> roles);

    protected abstract boolean updateRoles(Tournament tournament, HashMap<Role, Role> rolesExchange);

    /**
     * *******************************************************************
     *
     * CLUB
     *
     ********************************************************************
     */
    /**
     * Store a Club into the database.
     *
     * @param club
     */
    protected abstract boolean addClubs(List<Club> clubs);

    protected abstract List<Club> getClubs();

    protected abstract boolean removeClubs(List<Club> clubs);

    protected abstract boolean updateClubs(HashMap<Club, Club> clubsExchange);

    /**
     * *******************************************************************
     *
     * TOURNAMENT
     *
     ********************************************************************
     */
    /**
     * Store a Tournament list into the database.
     */
    protected abstract boolean addTournaments(List<Tournament> tournaments);

    protected abstract List<Tournament> getTournaments();

    protected abstract boolean removeTournaments(List<Tournament> tournaments);

    protected abstract boolean updateTournaments(HashMap<Tournament, Tournament> tournamentsExchange);

    /**
     * *******************************************************************
     *
     * TEAM
     *
     ********************************************************************
     */
    /**
     *
     */
    protected abstract List<Team> getTeams(Tournament tournament);

    protected abstract boolean addTeams(List<Team> teams);

    protected abstract boolean removeTeams(List<Team> teams);

    protected abstract boolean updateTeams(HashMap<Team, Team> teamsExchange);

    /**
     * *******************************************************************
     *
     * FIGHTS
     *
     ********************************************************************
     */
    /**
     * Store a fight into the database.
     */
    protected abstract List<Fight> getFights(Tournament tournament);

    protected abstract boolean addFights(List<Fight> fights);

    protected abstract boolean removeFights(List<Fight> fights);

    protected abstract boolean updateFights(HashMap<Fight, Fight> fightsExchange);

    public abstract boolean storeFights(List<Fight> fights, boolean purgeTournament, boolean verbose);

    public abstract boolean deleteAllFights();

    public abstract boolean storeAllFightsAndDeleteOldOnes(List<Fight> fights);

    public abstract boolean storeFight(Fight fight, boolean verbose, boolean deleteOldOne);

    public abstract boolean deleteFightsOfTournament(Tournament tournament, boolean verbose);

    public abstract boolean deleteFightsOfLevelOfTournament(Tournament tournament, int level, boolean verbose);

    public abstract List<Fight> searchFights(String query, Tournament tournament);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Fight> searchFightsByTournament(Tournament tournament);

    public abstract List<Fight> searchFightsByTournamentLevelEqualOrGreater(Tournament tournament, int level);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Fight> searchFightsByTournamentAndFightArea(Tournament tournament, int fightArea);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Fight> searchFightsByTournamentAndTeam(Tournament tournament, String team);

    public abstract int obtainFightID(Fight fight);

    public abstract boolean deleteFight(Fight fight, boolean verbose);

    public abstract boolean updateFightAsOver(Fight fight);

    public abstract boolean updateFightAsNotOver(Fight fight);

    public abstract List<Fight> getAllFights();

    public abstract List<Fight> getFights(int fromRow, int numberOfRows);

    /**
     * *******************************************************************
     *
     * DUEL
     *
     ********************************************************************
     */
    /**
     * Store a duel into the database.
     */
    public abstract boolean storeDuel(Duel d, Fight fight, int player);

    public abstract boolean storeDuelsOfFight(Fight fight);

    public abstract boolean deleteDuelsOfFight(Fight fight);

    public abstract List<Duel> getDuelsOfFight(Fight fight);

    public abstract Duel getDuel(Fight fight, int player);

    public abstract List<Duel> getDuelsOfTournament(Tournament tournament);

    public abstract List<Duel> getDuelsOfcompetitor(String competitorID, boolean teamRight);

    public abstract List<Duel> getAllDuels();

    public abstract List<Duel> getDuels(int fromRow, int numberOfRows);

    /**
     * *******************************************************************
     *
     * UNDRAWS
     *
     ********************************************************************
     */
    /**
     * Store a undraw into the database.
     */
    public abstract boolean storeUndraw(Tournament tournament, Team team, int order, int group, int level);

    public abstract boolean storeUndraw(Undraw undraw);

    public abstract List<Undraw> getUndraws(int fromRow, int numberOfRows);

    public abstract List<Undraw> getUndraws(Tournament tournament);

    public abstract List<Undraw> getUndraws();

    public abstract boolean storeAllUndraws(List<Undraw> undraws, boolean deleteOldOnes);

    public abstract List<Team> getWinnersInUndraws(Tournament tournament, int level, int group);

    public abstract int getValueWinnerInUndraws(Tournament tournament, String team);

    public abstract int getValueWinnerInUndrawInGroup(Tournament tournament, int group, int level, String team);

    public abstract void deleteDrawsOfTournament(Tournament tournament);

    public abstract void deleteDrawsOfGroupOfTournament(Tournament tournament, int group);

    public abstract void deleteDrawsOfLevelOfTournament(Tournament tournament, int level);
}
