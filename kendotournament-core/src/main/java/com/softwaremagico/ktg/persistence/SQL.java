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
import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.Photo;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.CustomWinnerLink;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SQL extends Database {
    public static final Integer DATABASE_NUMBER_TABLES = 9;
    public static final Integer MAX_ID_LENGTH = 12;
    public static final Integer MAX_NAME_LENGTH = 50;
    public static final Integer MAX_COUNTRY_LENGTH = 20;
    public static final Integer MAX_EMAIL_LENGTH = 50;
    public static final Integer MAX_PHONE_LENGTH = 20;
    public static final Integer MAX_CITY_LENGTH = 20;
    public static final Integer MAX_WEB_LENGTH = 100;
    public static final Integer MAX_ADDRESS_LENGTH = 100;
    public static final Integer MAX_TOURNAMENT_TYPE_LENGTH = 20;
    public static final Integer MAX_SCORE_TYPE_LENGTH = 15;
    public static final Integer MAX_ROLE_NAME_LENGTH = 15;
    
    
    
    protected static final Translator trans = LanguagePool.getTranslator("messages.xml");
    

    @Override
    public synchronized void clearDatabase() {
        KendoLog.fine(SQL.class.getName(), "Clearing database");
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("delete from competitor;");
                s.executeUpdate("delete from tournament;");
                s.executeUpdate("delete from club;");
                s.executeUpdate("delete from fight;");
                s.executeUpdate("delete from duel;");
                s.executeUpdate("delete from role;");
                s.executeUpdate("delete from team;");
                s.executeUpdate("delete from undraw;");
            }
        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
    }
    
    protected synchronized void createTable(String query) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException ex) {
            showSqlError(ex);
        }
    }

    protected void executeScript(String fileName) {
        String query = "";
        try {
            List<String> lines = MyFile.inLines(fileName);
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).startsWith("--")) {
                    if (!lines.get(i).endsWith(";")) {
                        query += lines.get(i).trim();
                    } else {
                        if (lines.get(i).trim().length() > 0) {
                            query += lines.get(i).trim();
                            try (PreparedStatement s = connection.prepareStatement(query)) {
                                try {
                                    s.executeUpdate();
                                } catch (SQLException sql) {
                                    showSqlError(sql);
                                    break;
                                }
                            }
                            query = "";
                        }
                    }
                }
            }
        } catch (FileNotFoundException fne) {
            KendoLog.severe(this.getClass().getName(), "Script not found: " + fileName);
        } catch (IOException ex) {
            KendoLog.severe(this.getClass().getName(), "Script not found: " + fileName);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size)
            throws SQLException;

    protected abstract InputStream getBinaryStream(ResultSet rs, String column) throws SQLException;
    
    protected abstract String getBoolean(Boolean value);

    protected abstract int getMaxElementsInQuery();

    /**
     * *******************************************************************
     *
     * COMPETITOR
     *
     ********************************************************************
     */
    /**
     * Store a role into the database.
     *
     * @param roleTag
     * @param tournament
     * @param c
     * @param verbose
     * @return
     */
    @Override
    protected synchronized boolean addRegisteredPeople(List<RegisteredPerson> registeredPeople) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addRegisteredPeople");
        for (RegisteredPerson person : registeredPeople) {
            try (PreparedStatement stmt = connection
                    .prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize) VALUES (?,?,?,?,?,?);")) {
                stmt.setString(1, person.getId());
                stmt.setString(2, person.getName());
                stmt.setString(3, person.getSurname());
                String clubName = null;
                if (person.getClub() != null) {
                    clubName = person.getClub().getName();
                }
                stmt.setString(4, clubName);
                InputStream photo = null;
                Integer size = 0;
                if (person.getPhoto() != null) {
                    photo = person.getPhoto().getInput();
                    size = person.getPhoto().getSize();
                }
                storeBinaryStream(stmt, 5, photo, size);
                stmt.setLong(6, size);
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    KendoLog.translatedSevere(this.getClass().getName(), "imageTooLarge");
                }
            } catch (SQLException ex) {
                showSqlError(ex);
                return false;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addRegisteredPeople");
        return true;
    }

    @Override
    protected synchronized List<RegisteredPerson> getRegisteredPeople() throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getRegisteredPeople");
        List<RegisteredPerson> results = new ArrayList<>();
        try (Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM competitor ORDER BY Surname;")) {
            while (rs.next()) {
                RegisteredPerson registered = new RegisteredPerson(rs.getObject("ID").toString(), rs.getObject("Name")
                        .toString(), rs.getObject("Surname").toString());
                Object clubName = rs.getObject("Club");
                if (clubName != null) {
                    registered.setClub(ClubPool.getInstance().get(clubName.toString()));
                }
                results.add(registered);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getRegisteredPeople");
        return results;
    }

    @Override
    protected synchronized boolean removeRegisteredPeople(List<RegisteredPerson> people) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeRegisteredPeople");
        String query = "";
        for (int i = 0; i < people.size(); i++) {
            query += "DELETE FROM competitor WHERE ID='" + people.get(i).getId() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == people.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeRegisteredPeople");
        return true;
    }

    @Override
    protected synchronized boolean updateRegisteredPeople(HashMap<RegisteredPerson, RegisteredPerson> peopleExchange)
            throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateRoles");
        List<RegisteredPerson> newPeople = new ArrayList<>(peopleExchange.keySet());
        for (RegisteredPerson newPerson : newPeople) {
            RegisteredPerson oldPerson = peopleExchange.get(newPerson);
            try (PreparedStatement stmt = connection
                    .prepareStatement("UPDATE competitor SET ID=?, Name=?, Surname=?, Club=? WHERE ID='"
                    + oldPerson.getId() + "'")) {
                stmt.setString(1, newPerson.getId());
                stmt.setString(2, newPerson.getName());
                stmt.setString(3, newPerson.getSurname());
                stmt.setString(4, newPerson.getClub().getName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                showSqlError(ex);
                return false;
            } catch (NullPointerException npe) {
                KendoLog.severe(this.getClass().getName(), "Database connection fail");
                throw new SQLException("Database connection fail.");
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateRoles");
        return true;
    }

    @Override
    protected synchronized Photo getPhoto(String competitorId) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getPhoto");
        String query = "SELECT Photo,PhotoSize FROM competitor WHERE ID='" + competitorId + "'";
        Photo photo = new Photo(competitorId);
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                try {
                    InputStream sImage = getBinaryStream(rs, "Photo");
                    Integer size = rs.getInt("PhotoSize");
                    photo.setImage(sImage, size);
                } catch (NullPointerException npe) {
                    photo.setImage(null, 0);
                }
                return photo;
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getPhoto");
        return null;
    }

    @Override
    protected synchronized boolean setPhotos(List<Photo> photos) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "setPhoto");
        for (Photo photo : photos) {
            try (PreparedStatement stmt = connection
                    .prepareStatement("UPDATE competitor SET Photo=?, PhotoSize=? WHERE ID='" + photo.getId() + "'")) {
                storeBinaryStream(stmt, 1, photo.getInput(), (int) photo.getSize());
                stmt.setLong(2, photo.getSize());
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    KendoLog.translatedSevere(this.getClass().getName(), "imageTooLarge");
                }
            } catch (SQLException ex) {
                showSqlError(ex);
                return false;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "setPhoto");
        return true;
    }

    /**
     * *******************************************************************
     *
     * ROLE
     *
     ********************************************************************
     */
    /**
     * Store a role into the database.
     *
     * @param roleTag
     * @param tournament
     * @param c
     * @param verbose
     * @return
     */
    @Override
    protected synchronized boolean addRoles(List<Role> roles) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addRoles");
        String query = "";
        // Insert team.
        for (int i = 0; i < roles.size(); i++) {
            try {
                RegisteredPerson participant = RegisteredPersonPool.getInstance().get(roles.get(i).getCompetitor().getId());
                query += "INSERT INTO role (Role, Tournament, Competitor, ImpressCardOrder, ImpressCardPrinted, DiplomaPrinted) VALUES ('"
                        + roles.get(i).getDatabaseTag()
                        + "','"
                        + roles.get(i).getTournament().getName()
                        + "','"
                        + participant.getId()
                        + "', "
                        + roles.get(i).getAccreditationOrder()
                        + ", "
                        + getBoolean(roles.get(i).isAccreditationPrinted())
                        + ","
                        + getBoolean(roles.get(i).isDiplomaPrinted()) + ");\n ";
            } catch (NullPointerException npe) {
                KendoLog.errorMessage(this.getClass().getName(), npe);
            }
            if (i % getMaxElementsInQuery() == 0 || i == roles.size() - 1) {
                try (PreparedStatement s = connection.prepareStatement(query)) {
                    s.executeUpdate();
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addRoles");
        return true;
    }

    @Override
    protected synchronized List<Role> getRoles(Tournament tournament) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getRoles");
        String query = "SELECT * FROM role WHERE Tournament='" + tournament.getName() + "' ORDER BY Role; ";
        KendoLog.finer(SQL.class.getName(), query);

        List<Role> results = new ArrayList<>();

        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Role role = new Role(TournamentPool.getInstance().get(rs.getObject("Tournament").toString()),
                        RegisteredPersonPool.getInstance().get(rs.getObject("Competitor").toString()), RolePool
                        .getInstance().getRoleTags().getRole(rs.getObject("Role").toString()),
                        rs.getInt("ImpressCardOrder"), rs.getBoolean("ImpressCardPrinted"),
                        rs.getBoolean("DiplomaPrinted"));
                results.add(role);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getRoles");
        return results;
    }

    @Override
    protected synchronized boolean removeRoles(Tournament tournament, List<Role> roles) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeRoles");
        String query = "";
        for (int i = 0; i < roles.size(); i++) {
            query += "DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='"
                    + roles.get(i).getCompetitor() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == roles.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeRoles");
        return true;
    }

    @Override
    protected synchronized boolean updateRoles(Tournament tournament, HashMap<Role, Role> rolesExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateRoles");
        List<Role> newRoles = new ArrayList<>(rolesExchange.keySet());
        String query = "";
        for (int i = 0; i < newRoles.size(); i++) {
            query += "UPDATE Role SET Role='" + newRoles.get(i).getDatabaseTag() + "', ImpressCardOrder="
                    + newRoles.get(i).getAccreditationOrder() + ", ImpressCardPrinted=" + newRoles.get(i).isAccreditationPrinted()
                    + ", DiplomaPrinted=" + newRoles.get(i).isDiplomaPrinted() + "  WHERE Tournament='" + tournament.getName()
                    + "' AND Competitor='" + newRoles.get(i).getCompetitor().getId() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == newRoles.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateRoles");
        return true;
    }

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
    @Override
    protected synchronized boolean addClubs(List<Club> clubs) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "storeClub");
        String query = "";
        for (int i = 0; i < clubs.size(); i++) {
            query += "INSERT INTO club (Name, Country, City, Address, Web, Mail, Phone, Representative) VALUES ('"
                    + clubs.get(i).getName() + "','" + clubs.get(i).getCountry() + "','" + clubs.get(i).getCity() + "','" + clubs.get(i).getAddress()
                    + "','" + clubs.get(i).getWeb() + "','" + clubs.get(i).getMail() + "','" + clubs.get(i).getPhone() + "','"
                    + clubs.get(i).getRepresentativeID() + "'); ";
            if (i % getMaxElementsInQuery() == 0 || i == clubs.size() - 1) {
                try (PreparedStatement s = connection.prepareStatement(query)) {
                    s.executeUpdate();
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "storeClub");
        return true;
    }

    @Override
    protected synchronized List<Club> getClubs() throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getClubs");
        List<Club> results = new ArrayList<>();
        try (Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM club ORDER BY Name")) {
            while (rs.next()) {
                String city = "";
                if (rs.getObject("City") != null) {
                    city = rs.getObject("City").toString();
                }

                String country = "";
                if (rs.getObject("Country") != null) {
                    country = rs.getObject("Country").toString();
                }

                Club c = new Club(rs.getObject("Name").toString(), country, city);
                try {
                    c.setAddress(rs.getObject("Address").toString());
                } catch (NullPointerException npe) {
                }
                try {
                    c.storeWeb(rs.getObject("Web").toString());
                } catch (NullPointerException npe) {
                }
                try {
                    c.setRepresentative(rs.getObject("Representative").toString(), rs.getObject("Mail").toString(), rs
                            .getObject("Phone").toString());
                } catch (NullPointerException npe) {
                }
                results.add(c);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getClubs");
        return results;
    }

    @Override
    protected synchronized boolean removeClubs(List<Club> clubs) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "deleteClubs");
        String query = "";
        for (int i = 0; i < clubs.size(); i++) {
            query += "DELETE FROM club WHERE Name='" + clubs.get(i).getName() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == clubs.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "deleteClubError");
        return true;
    }

    @Override
    protected synchronized boolean updateClubs(HashMap<Club, Club> clubsExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateClubs");
        List<Club> newClubs = new ArrayList<>(clubsExchange.keySet());
        String query = "";
        for (int i = 0; i < newClubs.size(); i++) {
            Club oldClub = clubsExchange.get(newClubs.get(i));
            query += "UPDATE Club SET Name='" + newClubs.get(i).getName() + "', Country='" + newClubs.get(i).getCountry() + "', City='"
                    + newClubs.get(i).getCity() + "', Address='" + newClubs.get(i).getAddress() + "', Web='" + newClubs.get(i).getWeb()
                    + "', Mail='" + newClubs.get(i).getMail() + "', Phone='" + newClubs.get(i).getPhone() + "', Representative='"
                    + newClubs.get(i).getRepresentativeID() + "' WHERE Name='" + oldClub.getName() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == newClubs.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateClubs");
        return true;
    }

    /**
     * *******************************************************************
     *
     * TOURNAMENT
     *
     ********************************************************************
     */
    /**
     * Store a Club into the database.
     *
     * @param club
     */
    @Override
    protected synchronized boolean addTournaments(List<Tournament> tournaments) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addTournaments");
        for (Tournament tournament : tournaments) {
            try (PreparedStatement stmt = connection
                    .prepareStatement("INSERT INTO tournament (Name, Banner, Size, FightingAreas, PassingTeams, TeamSize, Type, ScoreWin, ScoreDraw, ScoreType, Diploma, DiplomaSize, Accreditation, AccreditationSize) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                stmt.setString(1, tournament.getName());
                if (tournament.getBanner() != null) {
                    storeBinaryStream(stmt, 2, tournament.getBanner().getInput(), (int) tournament.getBanner()
                            .getSize());
                    stmt.setLong(3, tournament.getBanner().getSize());
                } else {
                    storeBinaryStream(stmt, 2, null, 0);
                    stmt.setLong(3, 0);
                }
                stmt.setInt(4, tournament.getFightingAreas());
                stmt.setInt(5, tournament.getHowManyTeamsOfGroupPassToTheTree());
                stmt.setInt(6, tournament.getTeamSize());
                stmt.setString(7, tournament.getType().getSqlName());
                stmt.setFloat(8, tournament.getScoreForWin());
                stmt.setFloat(9, tournament.getScoreForDraw());
                stmt.setString(10, tournament.getChoosedScore());
                if (tournament.getDiploma() != null) {
                    storeBinaryStream(stmt, 11, tournament.getDiploma().getInput(), (int) tournament.getDiploma()
                            .getSize());
                    stmt.setLong(12, tournament.getDiploma().getSize());
                } else {
                    storeBinaryStream(stmt, 11, null, 0);
                    stmt.setLong(12, 0);
                }
                if (tournament.getAccreditation() != null) {
                    storeBinaryStream(stmt, 13, tournament.getAccreditation().getInput(), (int) tournament
                            .getAccreditation().getSize());
                    stmt.setLong(14, tournament.getAccreditation().getSize());
                } else {
                    storeBinaryStream(stmt, 13, null, 0);
                    stmt.setLong(14, 0);
                } 
               stmt.executeUpdate();
            } catch (SQLException ex) {
                showSqlError(ex);
                return false;
            } catch (NullPointerException npe) {
                KendoLog.severe(this.getClass().getName(), "Database connection fail");
                throw new SQLException("Database connection fail.");
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addTournaments");
        return true;
    }

    @Override
    protected synchronized List<Tournament> getTournaments() throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getTournaments");
        List<Tournament> results = new ArrayList<>();
        KendoLog.fine(SQL.class.getName(), "Getting all tournaments.");
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM tournament ORDER BY Name")) {
            while (rs.next()) {
                Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"),
                        rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentType.getType(rs.getObject("Type")
                        .toString()));
                t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"),
                        rs.getInt("ScoreDraw"));
                InputStream sImage = getBinaryStream(rs, "Banner");
                Integer size = rs.getInt("Size");
                Photo banner = new Photo(t.getName());
                banner.setImage(sImage, size);
                t.setBanner(banner);
                sImage = getBinaryStream(rs, "Accreditation");
                size = rs.getInt("AccreditationSize");
                Photo accreditation = new Photo(t.getName());
                accreditation.setImage(sImage, size);
                t.setAccreditation(accreditation);
                sImage = getBinaryStream(rs, "Diploma");
                size = rs.getInt("DiplomaSize");
                Photo diploma = new Photo(t.getName());
                diploma.setImage(sImage, size);
                t.setDiploma(diploma);
                results.add(t);
            }
            KendoLog.exiting(this.getClass().getName(), "getTournaments");
            return results;
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getTournaments");
        return null;
    }

    @Override
    protected synchronized boolean removeTournaments(List<Tournament> tournaments) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeTournaments");
        String query = "";
        for (int i = 0; i < tournaments.size(); i++) {
            query += "DELETE FROM tournament WHERE Name='" + tournaments.get(i).getName() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == tournaments.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeTournaments");
        return true;
    }

    @Override
    protected synchronized boolean updateTournaments(HashMap<Tournament, Tournament> tournamentsExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateTournaments");
        List<Tournament> newTournaments = new ArrayList<>(tournamentsExchange.keySet());
        for (Tournament tournament : newTournaments) {
            Tournament oldTournament = tournamentsExchange.get(tournament);
            try (PreparedStatement stmt = connection
                    .prepareStatement("UPDATE tournament SET Banner=?, Size=?, FightingAreas=?, PassingTeams=?, TeamSize=?, Type=?, ScoreWin=?, ScoreDraw=?, ScoreType=?, Diploma=?, DiplomaSize=?, Accreditation=?, AccreditationSize=?, Name=? WHERE Name='"
                    + oldTournament.getName() + "';")) {
                try {
                    storeBinaryStream(stmt, 1, tournament.getBanner().getInput(), (int) tournament.getBanner()
                            .getSize());
                    stmt.setLong(2, tournament.getBanner().getSize());
                } catch (NullPointerException npe) {
                    storeBinaryStream(stmt, 1, null, 0);
                    stmt.setLong(2, 0);
                }
                stmt.setInt(3, tournament.getFightingAreas());
                stmt.setInt(4, tournament.getHowManyTeamsOfGroupPassToTheTree());
                stmt.setInt(5, tournament.getTeamSize());
                stmt.setString(6, tournament.getType().getSqlName());
                stmt.setFloat(7, tournament.getScoreForWin());
                stmt.setFloat(8, tournament.getScoreForDraw());
                stmt.setString(9, tournament.getChoosedScore());
                try {
                    storeBinaryStream(stmt, 10, tournament.getDiploma().getInput(), (int) tournament.getDiploma()
                            .getSize());
                    stmt.setLong(11, tournament.getDiploma().getSize());
                } catch (NullPointerException npe) {
                    storeBinaryStream(stmt, 10, null, 0);
                    stmt.setLong(11, 0);
                }
                try {
                    storeBinaryStream(stmt, 12, tournament.getAccreditation().getInput(), (int) tournament
                            .getAccreditation().getSize());
                    stmt.setLong(13, tournament.getAccreditation().getSize());
                } catch (NullPointerException npe) {
                    storeBinaryStream(stmt, 12, null, 0);
                    stmt.setLong(13, 0);
                }
                stmt.setString(14, tournament.getName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                showSqlError(ex);
                return false;
            } catch (NullPointerException npe) {
                KendoLog.severe(this.getClass().getName(), "Database connection fail");
                throw new SQLException("Database connection fail.");
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateTournaments");
        return true;
    }

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
    @Override
    protected synchronized List<Team> getTeams(Tournament tournament) throws SQLException, TeamMemberOrderException {
        String query = "SELECT * FROM team WHERE Tournament='" + tournament.getName() + "' ORDER BY Name, FightOfTournament, Position; ";
        KendoLog.entering(this.getClass().getName(), "searchTeam");
        KendoLog.finer(SQL.class.getName(), query);

        HashMap<String, Team> teams = new HashMap<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Team t = teams.get(rs.getObject("Name").toString());
                if (t == null) {
                    t = new Team(rs.getObject("Name").toString(), TournamentPool.getInstance().get(
                            rs.getObject("Tournament").toString()));
                    teams.put(t.getName(), t);
                    t.addGroup(rs.getInt("LeagueGroup"));
                }
                // For each line obtained from the database, add a member.
                t.setMember(RegisteredPersonPool.getInstance().get(rs.getObject("Member").toString()),
                        rs.getInt("Position"), rs.getInt("FightOfTournament"));
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.errorMessage(this.getClass().getName(), npe);
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "searchTeam");
        return new ArrayList<>(teams.values());
    }

    /**
     * Insert a team into the database.
     *
     * @param team
     * @param verbose
     * @return
     */
    @Override
    protected synchronized boolean addTeams(List<Team> teams) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addTeams");
        String query = "";
        // Insert team.
        for (int i = 0; i < teams.size(); i++) {
            for (Integer fightIndex : teams.get(i).getMembersOrder().keySet()) {
                for (Integer indexCompetitor : teams.get(i).getMembersOrder().get(fightIndex).keySet()) {
                    try {
                        query += "INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, FightOfTournament) VALUES ('"
                                + teams.get(i).getName()
                                + "','"
                                + teams.get(i).getMember(indexCompetitor, fightIndex).getId()
                                + "','"
                                + teams.get(i).getTournament().getName()
                                + "',"
                                + indexCompetitor
                                + ","
                                + teams.get(i).getGroup()
                                + ","
                                + fightIndex + ");\n";
                    } catch (NullPointerException npe) { // The team has one
                        // competitor less...
                    }
                    if (i % getMaxElementsInQuery() == 0) {
                        try (PreparedStatement s = connection.prepareStatement(query)) {
                            s.executeUpdate();
                        } catch (SQLException ex) {
                            showSqlError(ex);
                            return false;
                        }
                        query = "";
                    }
                }
            }
        }
        //Store last team.
        if (query.length() > 0) {
            try (PreparedStatement s = connection.prepareStatement(query)) {
                s.executeUpdate();
            } catch (SQLException ex) {
                showSqlError(ex);
                return false;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addTeams");
        return true;
    }

    @Override
    protected synchronized boolean removeTeams(List<Team> teams) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeTeams");
        String query = "";
        for (int i = 0; i < teams.size(); i++) {
            query += "DELETE FROM team WHERE Name='" + teams.get(i).getName() + "' AND Tournament='"
                    + teams.get(i).getTournament().getName() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == teams.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeTeams");
        return true;
    }

    @Override
    protected synchronized boolean updateTeams(HashMap<Team, Team> teamsExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateTeams");
        List<Team> oldTeams = new ArrayList<>(teamsExchange.values());
        List<Team> newTeams = new ArrayList<>(teamsExchange.keySet());
        removeTeams(oldTeams);
        addTeams(newTeams);
        KendoLog.exiting(this.getClass().getName(), "updateTeams");
        return true;
    }

    /**
     * *******************************************************************
     *
     * FIGHTS
     *
     ********************************************************************
     */
    /**
     *
     */
    @Override
    protected synchronized List<Fight> getFights(Tournament tournament) throws SQLException {
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "'";
        KendoLog.entering(this.getClass().getName(), "getFights");
        KendoLog.finer(SQL.class.getName(), query);

        List<Fight> results = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Fight f = new Fight(tournament, TeamPool.getInstance()
                        .get(tournament, rs.getObject("Team1").toString()), TeamPool.getInstance().get(tournament,
                        rs.getObject("Team2").toString()), rs.getInt("FightArea"), rs.getInt("TournamentLevel"),
                        rs.getInt("TournamentGroup"), rs.getInt("GroupIndex"));
                f.setWinner(rs.getInt("Winner"));
                // Set duels of fight:
                results.add(f);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getFights");
        return results;
    }

    @Override
    protected synchronized boolean addFights(List<Fight> fights) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addFights");
        String query = "";
        for (int i = 0; i < fights.size(); i++) {
            try {
                query += "INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, TournamentLevel, TournamentGroup, GroupIndex) VALUES ('"
                        + fights.get(i).getTeam1().getName()
                        + "','"
                        + fights.get(i).getTeam2().getName()
                        + "','"
                        + fights.get(i).getTournament().getName()
                        + "',"
                        + fights.get(i).getAsignedFightArea()
                        + ","
                        + fights.get(i).getWinner()
                        + ","
                        + fights.get(i).getLevel()
                        + ","
                        + fights.get(i).getGroup()
                        + ","
                        + fights.get(i).getOrderInGroup() + "); ";
            } catch (NullPointerException npe) {
                KendoLog.errorMessage(this.getClass().getName(), npe);
            }
            if (i % getMaxElementsInQuery() == 0 || i == fights.size() - 1) {
                try (PreparedStatement s = connection.prepareStatement(query)) {
                    s.executeUpdate();
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addFights");
        return true;
    }

    @Override
    protected synchronized boolean removeFights(List<Fight> fights) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeFights");
        String query = "";
        for (int i = 0; i < fights.size(); i++) {
            query += "DELETE FROM fight WHERE Tournament='" + fights.get(i).getTournament().getName()
                    + "' AND TournamentLevel=" + fights.get(i).getLevel() + " AND Team1='" + fights.get(i).getTeam1().getName()
                    + "' AND Team2='" + fights.get(i).getTeam2().getName() + "' AND TournamentGroup=" + fights.get(i).getGroup()
                    + " AND GroupIndex=" + fights.get(i).getOrderInGroup() + "; ";
            if (i % getMaxElementsInQuery() == 0 || i == fights.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeFights");
        return true;
    }

    @Override
    protected synchronized boolean updateFights(HashMap<Fight, Fight> fightsExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateFights");
        List<Fight> newFights = new ArrayList<>(fightsExchange.keySet());
        String query = "";
        for (int i = 0; i < newFights.size(); i++) {
            Fight oldFight = fightsExchange.get(newFights.get(i));
            query += "UPDATE Fight SET Team1='" + newFights.get(i).getTeam1() + "', Tournament='" + newFights.get(i).getTournament()
                    + "' Team2='" + newFights.get(i).getTeam2() + "', Winner=" + newFights.get(i).getWinner() + ", TournamentLevel="
                    + newFights.get(i).getLevel() + ", FightArea=" + newFights.get(i).getAsignedFightArea() + ", TournamentGroup="
                    + newFights.get(i).getGroup() + ", GroupIndex=" + newFights.get(i).getOrderInGroup() + " WHERE Team1='"
                    + oldFight.getTeam1() + "' AND Team2='" + oldFight.getTeam2() + "' AND Tournament='"
                    + oldFight.getTournament().getName() + "' AND TournamentLevel=" + oldFight.getLevel()
                    + " AND TournamentGroup=" + oldFight.getGroup() + " AND GroupIndex=" + oldFight.getOrderInGroup()
                    + "; ";
            if (i % getMaxElementsInQuery() == 0 || i == newFights.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateFights");
        return true;
    }

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
    @Override
    protected synchronized List<Duel> getDuels(Tournament tournament) throws SQLException {
        String query = "SELECT * FROM duel WHERE Tournament='" + tournament.getName() + "'";
        KendoLog.entering(this.getClass().getName(), "getDuels");
        KendoLog.finer(SQL.class.getName(), query);

        List<Duel> results = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Fight fight = FightPool.getInstance().get(tournament,
                        TeamPool.getInstance().get(tournament, rs.getObject("Team1").toString()),
                        TeamPool.getInstance().get(tournament, rs.getObject("Team2").toString()),
                        rs.getInt("TournamentLevel"), rs.getInt("TournamentGroup"), rs.getInt("GroupIndex"));
                Duel duel = new Duel(fight, rs.getInt("MemberOrder"));

                char c;
                try {
                    c = rs.getString("PointPlayer1A").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }
                duel.setHit(true, 0, Score.getScore(c));

                try {
                    c = rs.getString("PointPlayer1B").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }
                duel.setHit(true, 1, Score.getScore(c));

                try {
                    c = rs.getString("PointPlayer2A").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }
                duel.setHit(false, 0, Score.getScore(c));

                try {
                    c = rs.getString("PointPlayer2B").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }
                duel.setHit(false, 1, Score.getScore(c));

                if (rs.getInt("FaultsPlayer1") > 0) {
                    duel.setFaults(true);
                }

                if (rs.getInt("FaultsPlayer2") > 0) {
                    duel.setFaults(false);
                }

                results.add(duel);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getDuels");
        return results;
    }

    @Override
    protected synchronized boolean addDuels(List<Duel> duels) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addDuels");
        String query = "";
        for (int i = 0; i < duels.size(); i++) {
            try {
                query += "INSERT INTO duel (Team1, Team2, Tournament, TournamentGroup, GroupIndex, TournamentLevel, MemberOrder, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES ('"
                        + duels.get(i).getFight().getTeam1().getName()
                        + "', '"
                        + duels.get(i).getFight().getTeam2().getName()
                        + "', '"
                        + duels.get(i).getFight().getTournament().getName()
                        + "', "
                        + duels.get(i).getFight().getGroup()
                        + ", "
                        + duels.get(i).getFight().getOrderInGroup()
                        + ", "
                        + duels.get(i).getFight().getLevel()
                        + ", "
                        + duels.get(i).getOrder()
                        + ", '"
                        + duels.get(i).getHits(true).get(0).getAbbreviature()
                        + "', '"
                        + duels.get(i).getHits(true).get(1).getAbbreviature()
                        + "', '"
                        + duels.get(i).getHits(false).get(0).getAbbreviature()
                        + "', '"
                        + duels.get(i).getHits(false).get(1).getAbbreviature()
                        + "', "
                        + getBoolean(duels.get(i).getFaults(true))
                        + ", "
                        + getBoolean(duels.get(i).getFaults(false)) + ");\n";
            } catch (NullPointerException npe) {
                KendoLog.errorMessage(this.getClass().getName(), npe);
            }
            if (i % getMaxElementsInQuery() == 0 || i == duels.size() - 1) {
                try (PreparedStatement s = connection.prepareStatement(query)) {
                    s.executeUpdate();
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addDuels");
        return true;
    }

    @Override
    protected synchronized boolean removeDuels(List<Duel> duels) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeDuels");
        String query = "";
        for (int i = 0; i < duels.size(); i++) {
            query += "DELETE FROM duel WHERE Tournament='" + duels.get(i).getFight().getTournament().getName()
                    + "' AND TournamentLevel=" + duels.get(i).getFight().getLevel() + " AND Team1='"
                    + duels.get(i).getFight().getTeam1().getName() + "' AND Team2='" + duels.get(i).getFight().getTeam2().getName()
                    + "' AND TournamentGroup=" + duels.get(i).getFight().getGroup() + " AND GroupIndex="
                    + duels.get(i).getFight().getOrderInGroup() + " AND MemberOrder=" + duels.get(i).getOrder() + "; ";
            if (i % getMaxElementsInQuery() == 0 || i == duels.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeDuels");
        return true;
    }

    @Override
    protected synchronized boolean updateDuels(HashMap<Duel, Duel> duelsExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateDuels");
        List<Duel> newDuels = new ArrayList<>(duelsExchange.keySet());
        String query = "";
        for (int i = 0; i < newDuels.size(); i++) {
            Duel oldDuel = duelsExchange.get(newDuels.get(i));
            query += "UPDATE duel SET Team1='" + newDuels.get(i).getFight().getTeam1() + "', Tournament='"
                    + newDuels.get(i).getFight().getTournament() + "' Team2='" + newDuels.get(i).getFight().getTeam2()
                    + "', TournamentLevel=" + newDuels.get(i).getFight().getLevel() + ", TournamentGroup="
                    + newDuels.get(i).getFight().getGroup() + ", GroupIndex=" + newDuels.get(i).getFight().getOrderInGroup()
                    + ", MemberOrder=" + newDuels.get(i).getOrder() + ", PointPlayer1A='" + newDuels.get(i).getHits(true).get(0)
                    + "', PointPlayer1B='" + newDuels.get(i).getHits(true).get(1) + "', PointPlayer2A="
                    + newDuels.get(i).getHits(false).get(0) + ", PointPlayer2B=" + newDuels.get(i).getHits(false).get(1)
                    + ", FaultsPlayer1=" + newDuels.get(i).getFaults(true) + ", FaultsPlayer2=" + newDuels.get(i).getFaults(false)
                    + " WHERE Team1='" + oldDuel.getFight().getTeam1() + "' AND Team2='"
                    + oldDuel.getFight().getTeam2() + "' AND Tournament='"
                    + oldDuel.getFight().getTournament().getName() + "' AND TournamentLevel="
                    + oldDuel.getFight().getLevel() + " AND TournamentGroup=" + oldDuel.getFight().getGroup()
                    + " AND GroupIndex=" + oldDuel.getFight().getOrderInGroup() + " AND MemberOrder="
                    + oldDuel.getOrder() + "; ";
            if (i % getMaxElementsInQuery() == 0 || i == newDuels.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateDuels");
        return true;
    }

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
    @Override
    protected synchronized List<Undraw> getUndraws(Tournament tournament) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getUndraws");
        String query = "SELECT * FROM undraw WHERE Tournament='" + tournament.getName() + "'";
        KendoLog.finer(SQL.class.getName(), query);
        List<Undraw> results = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Undraw u = new Undraw(tournament, Integer.parseInt(rs.getObject("TournamentGroup").toString()),
                        TeamPool.getInstance().get(tournament, rs.getObject("Team").toString()),
                        (Integer) rs.getObject("Player"), (Integer) rs.getObject("TournamentLevel"));
                u.setPoints((Integer) rs.getObject("Points"));
                results.add(u);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getUndraws");
        return results;
    }

    @Override
    protected synchronized boolean addUndraws(List<Undraw> undraws) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addUndraws");
        String query = "";
        for (int i = 0; i < undraws.size(); i++) {
            try {
                query += "INSERT INTO undraw (Tournament, Team, Player, TournamentGroup, TournamentLevel, Points) VALUES ('"
                        + undraws.get(i).getTournament().getName()
                        + "', '"
                        + undraws.get(i).getTeam().getName()
                        + "', "
                        + undraws.get(i).getPlayer()
                        + ", "
                        + undraws.get(i).getGroupIndex()
                        + ", "
                        + undraws.get(i).getLevel()
                        + ", "
                        + undraws.get(i).getPoints() + "); ";
            } catch (NullPointerException npe) {
                KendoLog.errorMessage(this.getClass().getName(), npe);
            }
            if (i % getMaxElementsInQuery() == 0 || i == undraws.size() - 1) {
                try (PreparedStatement s = connection.prepareStatement(query)) {
                    s.executeUpdate();
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addUndraws");
        return true;
    }

    @Override
    protected synchronized boolean removeUndraws(List<Undraw> undraws) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeUndraws");
        String query = "";
        for (int i = 0; i < undraws.size(); i++) {
            query += "DELETE FROM undraw WHERE Tournament='" + undraws.get(i).getTournament().getName()
                    + "' AND TournamentLevel=" + undraws.get(i).getLevel() + " AND Team='" + undraws.get(i).getTeam().getName()
                    + "' AND TournamentGroup=" + undraws.get(i).getGroupIndex() + "; ";
            if (i % getMaxElementsInQuery() == 0 || i == undraws.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeUndraws");
        return true;
    }

    @Override
    protected synchronized boolean updateUndraws(HashMap<Undraw, Undraw> undrawsExchange) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateUndraws");
        List<Undraw> newUndraws = new ArrayList<>(undrawsExchange.keySet());
        String query = "";
        for (int i = 0; i < newUndraws.size(); i++) {
            Undraw oldUndraw = undrawsExchange.get(newUndraws.get(i));
            query += "UPDATE undraw SET Team='" + newUndraws.get(i).getTeam().getName() + "', Tournament='"
                    + newUndraws.get(i).getTournament().getName() + "', TournamentLevel=" + newUndraws.get(i).getLevel()
                    + ", TournamentGroup=" + newUndraws.get(i).getGroupIndex() + ", Player=" + newUndraws.get(i).getPlayer()
                    + ", Points=" + newUndraws.get(i).getPoints() + " WHERE Team='" + oldUndraw.getTeam().getName()
                    + "' AND Tournament='" + oldUndraw.getTournament().getName() + "' AND TournamentLevel="
                    + oldUndraw.getLevel() + " AND TournamentGroup=" + oldUndraw.getGroupIndex() + "; ";
            if (i % getMaxElementsInQuery() == 0 || i == newUndraws.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateUndraws");
        return true;
    }

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
    @Override
    protected synchronized List<CustomWinnerLink> getCustomWinnerLinks(Tournament tournament) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "getCustomWinnerLinks");
        if (!tournament.getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
            return null;
        }
        String query = "SELECT * FROM customlinks WHERE Tournament='" + tournament.getName()
                + "' ORDER BY Tournament, SourceGroup, WinnerOrder";
        KendoLog.finer(SQL.class.getName(), query);
        List<CustomWinnerLink> results = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                CustomWinnerLink link = new CustomWinnerLink(tournament, Integer.parseInt(rs.getObject("SourceGroup")
                        .toString()), Integer.parseInt(rs.getObject("AddressGroup").toString()), Integer.parseInt(rs
                        .getObject("WinnerOrder").toString()));
                results.add(link);
            }
        } catch (SQLException ex) {
            showSqlError(ex);
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Database connection fail");
            throw new SQLException("Database connection fail.");
        }
        KendoLog.exiting(this.getClass().getName(), "getCustomWinnerLinks");
        return results;
    }

    @Override
    protected synchronized boolean addCustomWinnerLinks(List<CustomWinnerLink> customWinnerLinks) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "addCustomWinnerLinks");
        String query = "";
        for (int i = 0; i < customWinnerLinks.size(); i++) {
            try {
                query += "INSERT INTO customlinks (Tournament, SourceGroup, AddressGroup, WinnerOrder) VALUES ('"
                        + customWinnerLinks.get(i).getTournament().getName() + "', " + customWinnerLinks.get(i).getSource() + ", " + customWinnerLinks.get(i).getAddress() + ", "
                        + customWinnerLinks.get(i).getWinner() + "); ";
            } catch (NullPointerException npe) {
                KendoLog.errorMessage(this.getClass().getName(), npe);
            }

            if (i % getMaxElementsInQuery() == 0 || i == customWinnerLinks.size() - 1) {
                try (PreparedStatement s = connection.prepareStatement(query)) {
                    s.executeUpdate();
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addCustomWinnerLinks");
        return true;
    }

    @Override
    protected synchronized boolean removeCustomWinnerLinks(List<Tournament> tournaments) throws SQLException {
        KendoLog.entering(this.getClass().getName(), "removeUndraws");
        String query = "";
        for (int i = 0; i < tournaments.size(); i++) {
            query += "DELETE FROM customlinks WHERE Tournament='" + tournaments.get(i).getName() + "'; ";
            if (i % getMaxElementsInQuery() == 0 || i == tournaments.size() - 1) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate(query);
                } catch (SQLException ex) {
                    showSqlError(ex);
                    return false;
                } catch (NullPointerException npe) {
                    KendoLog.severe(this.getClass().getName(), "Database connection fail");
                    throw new SQLException("Database connection fail.");
                }
                query = "";
            }
        }
        KendoLog.exiting(this.getClass().getName(), "removeUndraws");
        return true;
    }

    @Override
    protected synchronized boolean updateCustomWinnerLinks(HashMap<CustomWinnerLink, CustomWinnerLink> customWinnerLinks)
            throws SQLException {
        KendoLog.entering(this.getClass().getName(), "updateUndraws");
        List<CustomWinnerLink> oldLinks = new ArrayList<>(customWinnerLinks.values());
        List<CustomWinnerLink> newLinks = new ArrayList<>(customWinnerLinks.keySet());
        List<Tournament> oldLinksTournaments = new ArrayList<>();
        // Remove old links
        for (CustomWinnerLink oldLink : oldLinks) {
            if (!oldLinksTournaments.contains(oldLink.getTournament())) {
                oldLinksTournaments.add(oldLink.getTournament());
            }
        }
        removeCustomWinnerLinks(oldLinksTournaments);
        // Add new ones.
        addCustomWinnerLinks(newLinks);
        return true;
    }
}
