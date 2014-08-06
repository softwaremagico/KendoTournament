package com.softwaremagico.ktg.persistence;
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

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Photo;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.tournament.CustomWinnerLink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public abstract class Database {

    protected Connection connection = null;

    public Database() {
    }

    /**
     * Connect to the database
     *
     * @param password database password.
     * @param user user database.
     * @param database satabase schema.
     * @param server server IP
     * @param verbose show error messages.
     * @param retry do another try if can solve the SQL problem.
     * @return true if the connection is ok.
     */
    protected abstract boolean connect(String password, String user, String database, String server, boolean verbose, boolean retry) throws CommunicationsException, SQLException;

    protected abstract void disconnectDatabase();

    protected void disconnect() {
        disconnectDatabase();
    }

    abstract void startDatabase();

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

    abstract void installDatabase(String database) throws Exception;

    abstract boolean isDatabaseInstalledCorrectly();

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
     * Add people to the database.
     */
    protected abstract boolean addRegisteredPeople(List<RegisteredPerson> people) throws SQLException;

    protected abstract List<RegisteredPerson> getRegisteredPeople() throws SQLException;

    protected abstract boolean removeRegisteredPeople(List<RegisteredPerson> people) throws SQLException;

    protected abstract boolean updateRegisteredPeople(HashMap<RegisteredPerson, RegisteredPerson> peopleExchange) throws SQLException;

    protected abstract Photo getPhoto(String competitorId) throws SQLException;

    protected abstract boolean setPhotos(List<Photo> photos) throws SQLException;

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
    protected abstract boolean addRoles(List<Role> roles) throws SQLException;

    protected abstract List<Role> getRoles(Tournament tournament) throws SQLException;

    protected abstract boolean removeRoles(Tournament tournament, List<Role> roles) throws SQLException;

    protected abstract boolean updateRoles(Tournament tournament, HashMap<Role, Role> rolesExchange) throws SQLException;

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
    protected abstract boolean addClubs(List<Club> clubs) throws SQLException;

    protected abstract List<Club> getClubs() throws SQLException;

    protected abstract boolean removeClubs(List<Club> clubs) throws SQLException;

    protected abstract boolean updateClubs(HashMap<Club, Club> clubsExchange) throws SQLException;

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
    protected abstract boolean addTournaments(List<Tournament> tournaments) throws SQLException;

    protected abstract List<Tournament> getTournaments() throws SQLException;

    protected abstract boolean removeTournaments(List<Tournament> tournaments) throws SQLException;

    protected abstract boolean updateTournaments(HashMap<Tournament, Tournament> tournamentsExchange) throws SQLException;

    /**
     * *******************************************************************
     *
     * TEAM
     *
     ********************************************************************
     */
    /**
     * @throws TeamMemberOrderException
     *
     */
    protected abstract List<Team> getTeams(Tournament tournament) throws SQLException, TeamMemberOrderException;

    protected abstract boolean addTeams(List<Team> teams) throws SQLException;

    protected abstract boolean removeTeams(List<Team> teams) throws SQLException;

    protected abstract boolean removeTeamsOrder(Tournament tournament, Integer fightIndex) throws SQLException;

    protected abstract boolean updateTeams(HashMap<Team, Team> teamsExchange) throws SQLException;

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
    protected abstract List<Fight> getFights(Tournament tournament) throws SQLException;

    protected abstract boolean addFights(List<Fight> fights) throws SQLException;

    protected abstract boolean removeFights(List<Fight> fights) throws SQLException;

    protected abstract boolean updateFights(HashMap<Fight, Fight> fightsExchange) throws SQLException;

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
    protected abstract List<Duel> getDuels(Tournament tournament) throws SQLException;

    protected abstract boolean addDuels(List<Duel> duels) throws SQLException;

    protected abstract boolean removeDuels(List<Duel> duels) throws SQLException;

    protected abstract boolean updateDuels(HashMap<Duel, Duel> duelsExchange) throws SQLException;

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
    protected abstract List<Undraw> getUndraws(Tournament tournament) throws SQLException;

    protected abstract boolean addUndraws(List<Undraw> undraws) throws SQLException;

    protected abstract boolean removeUndraws(List<Undraw> undraws) throws SQLException;

    protected abstract boolean updateUndraws(HashMap<Undraw, Undraw> undrawsExchange) throws SQLException;

    /**
     * *******************************************************************
     *
     * CUSTOM LINKS
     *
     ********************************************************************
     */
    /**
     * Store user defined links.
     */
    protected abstract List<CustomWinnerLink> getCustomWinnerLinks(Tournament tournament) throws SQLException;

    protected abstract boolean addCustomWinnerLinks(List<CustomWinnerLink> customWinnerLinks) throws SQLException;

    protected abstract boolean removeCustomWinnerLinks(List<Tournament> tournaments) throws SQLException;

    protected abstract boolean updateCustomWinnerLinks(HashMap<CustomWinnerLink, CustomWinnerLink> customWinnerLinks) throws SQLException;

    /**
     * *******************************************************************
     *
     * EXCEPTIONS
     *
     ********************************************************************
     */
    /**
     * Process any sql exception.
     *
     * @param exception
     * @throws SQLException
     */
    protected void showSqlError(SQLException exception) throws SQLException {
        KendoLog.severe(this.getClass().getName(), getSqlErrorMessage(exception));
        KendoLog.errorMessage(this.getClass().getName(), exception);
        throw new SQLException(exception);
    }

    public abstract String getSqlErrorMessage(SQLException exception);
}
