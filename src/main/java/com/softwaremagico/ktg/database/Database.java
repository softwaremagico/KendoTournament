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

    protected abstract void disconnectDatabase();

    public void disconnect() {
        DatabaseConnection.getInstance().setDatabaseConnected(false);
        disconnectDatabase();
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

    abstract boolean isDatabaseInstalledCorrectly();

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
    protected abstract boolean addRegisteredPeople(List<RegisteredPerson> people);

    protected abstract List<RegisteredPerson> getRegisteredPeople();

    protected abstract boolean removeRegisteredPeople(List<RegisteredPerson> people);

    protected abstract boolean updateRegisteredPeople(HashMap<RegisteredPerson, RegisteredPerson> peopleExchange);

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
    protected abstract boolean addRoles(List<Role> roles);

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
    protected abstract List<Duel> getDuels(Tournament tournament);

    protected abstract boolean addDuels(List<Duel> duels);

    protected abstract boolean removeDuels(List<Duel> duels);

    protected abstract boolean updateDuels(HashMap<Duel, Duel> duelsExchange);

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
    protected abstract List<Undraw> getUndraws(Tournament tournament);

    protected abstract boolean addUndraws(List<Undraw> undraws);

    protected abstract boolean removeUndraws(List<Undraw> undraws);

    protected abstract boolean updateUndraws(HashMap<Undraw, Undraw> undrawsExchange);
}
