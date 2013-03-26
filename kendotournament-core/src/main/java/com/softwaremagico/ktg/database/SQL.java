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

import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.Photo;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.TournamentType;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.files.MyFile;
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

/**
 *
 * @author LOCAL\jhortelano
 */
public abstract class SQL extends Database {

    @Override
    public void clearDatabase() {
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
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
    }

    protected void executeScript(String fileName) {
        String query = "";
        try {
            List<String> lines = MyFile.inLines(fileName, false);
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
                                    showSQLError(1049);
                                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), sql);
                                    break;
                                }
                            }
                            query = "";
                        }
                    }
                }
            }
        } catch (IOException ex) {
            showSQLError(1049);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size) throws SQLException;

    protected abstract InputStream getBinaryStream(ResultSet rs, String column) throws SQLException;

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
    protected boolean addRegisteredPeople(List<RegisteredPerson> registeredPeople) {
        KendoLog.entering(this.getClass().getName(), "addRegisteredPeople");
        for (RegisteredPerson person : registeredPeople) {
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize) VALUES (?,?,?,?,?,?);")) {
                stmt.setString(1, person.getId());
                stmt.setString(2, person.getName());
                stmt.setString(3, person.getSurname());
                stmt.setString(4, person.getClub().getName());
                storeBinaryStream(stmt, 5, person.getPhoto().getPhotoInput(), person.getPhoto().getPhotoSize());
                stmt.setLong(6, person.getPhoto().getPhotoSize());
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                }
            } catch (SQLException ex) {
                if (!showSQLError(ex.getErrorCode())) {
                    MessageManager.errorMessage(this.getClass().getName(), "storeCompetitorError", "SQL");
                }
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                return false;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addRegisteredPeople");
        return true;
    }

    @Override
    protected List<RegisteredPerson> getRegisteredPeople() {
        KendoLog.entering(this.getClass().getName(), "getRegisteredPeople");
        List<RegisteredPerson> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM competitor ORDER BY Surname;")) {
                while (rs.next()) {
                    RegisteredPerson registered = new RegisteredPerson(rs.getObject("ID").toString(), rs.getObject("Name").toString(), rs.getObject("Surname").toString());
                    registered.setClub(ClubPool.getInstance().get(rs.getObject("Club").toString()));
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "MySQL database connection fail", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getRegisteredPeople");
        return results;
    }

    @Override
    protected boolean removeRegisteredPeople(List<RegisteredPerson> peoples) {
        KendoLog.entering(this.getClass().getName(), "removeRegisteredPeople");
        String query = "";
        for (RegisteredPerson people : peoples) {
            query += "DELETE FROM competitor WHERE ID='" + people.getId() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteCompetitorError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "removeRegisteredPeople");
        return true;
    }

    @Override
    protected boolean updateRegisteredPeople(HashMap<RegisteredPerson, RegisteredPerson> peopleExchange) {
        KendoLog.entering(this.getClass().getName(), "updateRoles");
        List<RegisteredPerson> oldPeople = new ArrayList<>(peopleExchange.values());
        List<RegisteredPerson> newPeople = new ArrayList<>(peopleExchange.keySet());
        for (RegisteredPerson newPerson : newPeople) {
            RegisteredPerson oldPerson = peopleExchange.get(newPerson);
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET ID=?, Name=?, Surname=?, Club=? WHERE ID='" + oldPerson.getId() + "'")) {
                stmt.setString(1, newPerson.getId());
                stmt.setString(2, newPerson.getName());
                stmt.setString(3, newPerson.getSurname());
                stmt.setString(4, newPerson.getClub().getName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                if (!showSQLError(ex.getErrorCode())) {
                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                }
                return false;
            } catch (NullPointerException npe) {
                MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                return false;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "updateRoles");
        return true;
    }

    @Override
    protected Photo getPhoto(String competitorId) {
        KendoLog.entering(this.getClass().getName(), "getPhoto");
        String query = "SELECT Photo,PhotoSize FROM competitor WHERE ID='" + competitorId + "'";
        Photo photo = new Photo(competitorId);
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                try {
                    InputStream sImage = getBinaryStream(rs, "Photo");
                    Integer size = rs.getInt("PhotoSize");
                    photo.addImage(sImage, size);
                } catch (NullPointerException npe) {
                    photo.addImage(null, 0);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getPhoto");
        return null;
    }

    @Override
    protected boolean setPhotos(List<Photo> photos) {
        KendoLog.entering(this.getClass().getName(), "setPhoto");
        for (Photo photo : photos) {
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET Photo=?, PhotoSize=? WHERE ID='" + photo.getId() + "'")) {
                storeBinaryStream(stmt, 1, photo.getPhotoInput(), (int) photo.getPhotoSize());
                stmt.setLong(2, photo.getPhotoSize());
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                }
            } catch (SQLException ex) {
                if (!showSQLError(ex.getErrorCode())) {
                    MessageManager.errorMessage(this.getClass().getName(), "storeCompetitorError", "SQL");
                }
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
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
    protected boolean addRoles(List<Role> roles) {
        KendoLog.entering(this.getClass().getName(), "addRoles");
        String query = "";
        //Insert team.
        for (Role role : roles) {
            try {
                RegisteredPerson participant = RegisteredPersonPool.getInstance().get(role.getCompetitor().getId());
                query += "INSERT INTO role (Role, Tournament, Competitor, ImpressCardOrder, ImpressCardPrinted, DiplomaPrinted) VALUES ('" + role.getDatabaseTag() + "','" + role.getTournament().getName() + "','" + participant.getId() + "', " + role.getAccreditationOrder() + ", " + role.isAccreditationPrinted() + "," + role.isDiplomaPrinted() + ");\n";
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            }
        }

        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.executeUpdate();
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "roleChanged", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "addRoles");
        return true;
    }

    @Override
    protected List<Role> getRoles(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "getRoles");
        String query = "SELECT * FROM role WHERE Tournament='" + tournament.getName() + "' ORDER BY Role; ";
        KendoLog.finer(SQL.class.getName(), query);

        List<Role> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Role role = new Role(TournamentPool.getInstance().get(rs.getObject("Tournament").toString()), RegisteredPersonPool.getInstance().get(rs.getObject("Competitor").toString()), KendoTournamentGenerator.getInstance().getAvailableRoles().getRole(rs.getObject("Role").toString()), rs.getInt("ImpressCardOrder"), rs.getBoolean("ImpressCardPrinted"), rs.getBoolean("DiplomaPrinted"));
                    results.add(role);
                }
            }
            if (results.isEmpty()) {
                MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getRoles");
        return results;
    }

    @Override
    protected boolean removeRoles(Tournament tournament, List<Role> roles) {
        KendoLog.entering(this.getClass().getName(), "removeRoles");
        String query = "";
        for (Role role : roles) {
            query += "DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + role.getCompetitor() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteRoleError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "removeRoles");
        return true;
    }

    @Override
    protected boolean updateRoles(Tournament tournament, HashMap<Role, Role> rolesExchange) {
        KendoLog.entering(this.getClass().getName(), "updateRoles");
        List<Role> oldRoles = new ArrayList<>(rolesExchange.values());
        List<Role> newRoles = new ArrayList<>(rolesExchange.keySet());
        String query = "";
        for (Role role : newRoles) {
            query += "UPDATE Role SET Role='" + role.getDatabaseTag() + "', ImpressCardOrder=" + role.getAccreditationOrder() + ", ImpressCardPrinted=" + role.isAccreditationPrinted() + ", DiplomaPrinted=" + role.isDiplomaPrinted() + "  WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + role.getCompetitor().getId() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            return false;
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
    protected boolean addClubs(List<Club> clubs) {
        KendoLog.entering(this.getClass().getName(), "storeClub");
        String query = "";
        for (Club club : clubs) {
            query += "INSERT INTO club (Name, Country, City, Address, Web, Mail, Phone, Representative) VALUES ('" + club.getName() + "','" + club.getCountry() + "','" + club.getCity() + "','" + club.getAddress() + "','" + club.getWeb() + "','" + club.getMail() + "'," + club.getPhone() + ",'" + club.getRepresentative() + "');\n";
        }
        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException micve) {
            MessageManager.errorMessage(this.getClass().getName(), "nameClub", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), micve);
            return false;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "storeClub");
        return true;
    }

    @Override
    protected List<Club> getClubs() {
        KendoLog.entering(this.getClass().getName(), "getClubs");
        List<Club> results = new ArrayList<>();
        try {
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
                    if (c != null) {
                        try {
                            c.setRepresentative(rs.getObject("Representative").toString(), rs.getObject("Mail").toString(), rs.getObject("Phone").toString());
                        } catch (NullPointerException npe) {
                        }
                        results.add(c);
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "MySQL database connection fail", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getClubs");
        return results;
    }

    @Override
    protected boolean removeClubs(List<Club> clubs) {
        KendoLog.entering(this.getClass().getName(), "deleteClubs");
        String query = "";
        for (Club club : clubs) {
            query += "DELETE FROM club WHERE Name='" + club.getName() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteClubError", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "deleteClubError");
        return true;
    }

    @Override
    protected boolean updateClubs(HashMap<Club, Club> clubsExchange) {
        KendoLog.entering(this.getClass().getName(), "updateClubs");
        List<Club> oldClubs = new ArrayList<>(clubsExchange.values());
        List<Club> newClubs = new ArrayList<>(clubsExchange.keySet());
        String query = "";
        for (Club newClub : newClubs) {
            Club oldClub = clubsExchange.get(newClub);
            query += "UPDATE Club SET Name='" + newClub.getName() + "', Country='" + newClub.getCountry() + "', City='" + newClub.getCity() + "', Address='" + newClub.getAddress() + "', Web='" + newClub.getWeb() + "', Mail='" + newClub.getMail() + "', Phone='" + newClub.getPhone() + "', Representative='" + newClub.getRepresentative() + "' WHERE Name='" + oldClub.getName() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            return false;
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
    protected boolean addTournaments(List<Tournament> tournaments) {
        KendoLog.entering(this.getClass().getName(), "addTournaments");
        for (Tournament tournament : tournaments) {
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO tournament (Name, Banner, Size, FightingAreas, PassingTeams, TeamSize, Type, ScoreWin, ScoreDraw, ScoreType, Diploma, DiplomaSize, Accreditation, AccreditationSize) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                stmt.setString(1, tournament.getName());
                storeBinaryStream(stmt, 2, tournament.getBannerInput(), (int) tournament.getBannerSize());
                stmt.setLong(3, tournament.getBannerSize());
                stmt.setInt(4, tournament.getFightingAreas());
                stmt.setInt(5, tournament.getHowManyTeamsOfGroupPassToTheTree());
                stmt.setInt(6, tournament.getTeamSize());
                stmt.setString(7, tournament.getMode().getSqlName());
                stmt.setFloat(8, tournament.getScoreForWin());
                stmt.setFloat(9, tournament.getScoreForDraw());
                stmt.setString(10, tournament.getChoosedScore());
                storeBinaryStream(stmt, 11, tournament.getDiplomaInput(), (int) tournament.getDiplomaSize());
                stmt.setLong(12, tournament.getDiplomaSize());
                storeBinaryStream(stmt, 13, tournament.getAccreditationInput(), (int) tournament.getAccreditationSize());
                stmt.setLong(14, tournament.getAccreditationSize());
                stmt.executeUpdate();
            } catch (MysqlDataTruncation mdt) {
                MessageManager.errorMessage(this.getClass().getName(), "storeImageError", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), mdt);
                return false;
            } catch (SQLException ex) {
                showSQLError(ex.getErrorCode());
                MessageManager.errorMessage(this.getClass().getName(), "storeTournamentError", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                return false;
            } catch (NullPointerException npe) {
                MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
                return false;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "addTournaments");
        return true;
    }

    @Override
    protected List<Tournament> getTournaments() {
        KendoLog.entering(this.getClass().getName(), "getTournaments");
        List<Tournament> results = new ArrayList<>();
        KendoLog.fine(SQL.class.getName(), "Getting all tournaments.");
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM tournament ORDER BY Name")) {
                while (rs.next()) {
                    Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentType.getType(rs.getObject("Type").toString()));
                    t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"), rs.getInt("ScoreDraw"));
                    InputStream sImage = getBinaryStream(rs, "Banner");
                    Long size = rs.getLong("Size");
                    t.addBanner(sImage, size);
                    InputStream sImage2 = getBinaryStream(rs, "Accreditation");
                    Long size2 = rs.getLong("AccreditationSize");
                    t.addAccreditation(sImage2, size2);
                    InputStream sImage3 = getBinaryStream(rs, "Diploma");
                    Long size3 = rs.getLong("DiplomaSize");
                    t.addDiploma(sImage3, size3);
                    results.add(t);
                }
            }
            KendoLog.exiting(this.getClass().getName(), "getTournaments");
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getTournaments");
        return null;
    }

    @Override
    protected boolean removeTournaments(List<Tournament> tournaments) {
        KendoLog.entering(this.getClass().getName(), "removeTournaments");
        String query = "";
        for (Tournament tournament : tournaments) {
            query += "DELETE FROM tournament WHERE Name='" + tournament.getName() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            return false;
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }

        KendoLog.exiting(this.getClass().getName(), "removeTournaments");
        return true;
    }

    @Override
    protected boolean updateTournaments(HashMap<Tournament, Tournament> tournamentsExchange) {
        KendoLog.entering(this.getClass().getName(), "updateTournaments");
        List<Tournament> oldTournaments = new ArrayList<>(tournamentsExchange.values());
        List<Tournament> newTournaments = new ArrayList<>(tournamentsExchange.keySet());
        for (Tournament tournament : newTournaments) {
            Tournament oldTournament = tournamentsExchange.get(tournament);
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Banner=?, Size=?, FightingAreas=?, PassingTeams=?, TeamSize=?, Type=?, ScoreWin=?, ScoreDraw=?, ScoreType=?, Diploma=?, DiplomaSize=?, Accreditation=?, AccreditationSize=?, Name=? WHERE Name='" + oldTournament.getName() + "';")) {
                storeBinaryStream(stmt, 1, tournament.getBannerInput(), (int) tournament.getBannerSize());
                stmt.setLong(2, tournament.getBannerSize());
                stmt.setInt(3, tournament.getFightingAreas());
                stmt.setInt(4, tournament.getHowManyTeamsOfGroupPassToTheTree());
                stmt.setInt(5, tournament.getTeamSize());
                stmt.setString(6, tournament.getMode().getSqlName());
                stmt.setFloat(7, tournament.getScoreForWin());
                stmt.setFloat(8, tournament.getScoreForDraw());
                stmt.setString(9, tournament.getChoosedScore());
                storeBinaryStream(stmt, 10, tournament.getDiplomaInput(), (int) tournament.getDiplomaSize());
                stmt.setLong(11, tournament.getDiplomaSize());
                storeBinaryStream(stmt, 12, tournament.getAccreditationInput(), (int) tournament.getAccreditationSize());
                stmt.setLong(13, tournament.getAccreditationSize());
                stmt.setString(14, tournament.getName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                if (!showSQLError(ex.getErrorCode())) {
                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                }
                return false;
            } catch (NullPointerException npe) {
                MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                return false;
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
     *
     */
    @Override
    protected List<Team> getTeams(Tournament tournament) {
        String query = "SELECT * FROM team WHERE Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name; ";
        KendoLog.entering(this.getClass().getName(), "searchTeam");
        KendoLog.finer(SQL.class.getName(), query);

        HashMap<String, Team> teams = new HashMap<>();
        try (Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Team t = teams.get(rs.getObject("Name").toString());
                if (t == null) {
                    t = new Team(rs.getObject("Name").toString(), TournamentPool.getInstance().get(rs.getObject("Tournament").toString()));
                    teams.put(t.getName(), t);
                    t.addGroup(rs.getInt("LeagueGroup"));
                }
                //For each line obtained from the database, add a member. 
                t.setMember(RegisteredPersonPool.getInstance().get(rs.getObject("Member").toString()),
                        rs.getInt("Position"), rs.getInt("LevelTournament"));
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
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
    protected boolean addTeams(List<Team> teams) {
        KendoLog.entering(this.getClass().getName(), "addTeams");
        String query = "";
        //Insert team.
        for (Team team : teams) {
            for (Integer level : team.getMembersOrder().keySet()) {
                for (int indexCompetitor = 0; indexCompetitor < team.getNumberOfMembers(level); indexCompetitor++) {
                    try {
                        query += "INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, LevelTournament) VALUES ('" + team.getName() + "','" + team.getMember(indexCompetitor, level).getId() + "','" + team.getTournament().getName() + "'," + indexCompetitor + "," + team.getGroup() + "," + level + ");\n";
                    } catch (NullPointerException npe) { //The team has one competitor less...
                    }
                }
            }
        }

        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.executeUpdate();
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "storeTeamError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "addTeams");
        return true;
    }

    @Override
    protected boolean removeTeams(List<Team> teams) {
        KendoLog.entering(this.getClass().getName(), "removeTeams");
        String query = "";
        for (Team team : teams) {
            query += "DELETE FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.getTournament().getName() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteTeamError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "removeTeams");
        return true;
    }

    @Override
    protected boolean updateTeams(HashMap<Team, Team> teamsExchange) {
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
    protected List<Fight> getFights(Tournament tournament) {
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "'";
        KendoLog.entering(this.getClass().getName(), "getFights");
        KendoLog.finer(SQL.class.getName(), query);

        List<Fight> results = new ArrayList<>();
        try (Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Fight f = new Fight(tournament,
                        TeamPool.getInstance().get(tournament, rs.getObject("Team1").toString()),
                        TeamPool.getInstance().get(tournament, rs.getObject("Team2").toString()),                        
                        rs.getInt("FightArea"), rs.getInt("Level"), rs.getInt("Index"));
                f.setWinner(rs.getInt("Winner"));
                f.setMaxWinners(rs.getInt("MaxWinners"));
                f.calculateOverWithDuels();
                //Set duels of fight:
                results.add(f);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getFights");
        return results;
    }

    @Override
    protected boolean addFights(List<Fight> fights) {
        KendoLog.entering(this.getClass().getName(), "addFights");
        String query = "";
        for (Fight fight : fights) {
            try {
                query += "INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, Level, MaxWinners, Index) VALUES ('" + fight.getTeam1().getName() + "','" + fight.getTeam2().getName() + "','" + fight.getTournament().getName() + "'," + fight.getAsignedFightArea() + "," + fight.getWinner() + "," + fight.getLevel() + "," + fight.getMaxWinners() + "," + fight.getIndex() + ");\n";
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            }
        }

        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.executeUpdate();
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "addFights");
        return true;
    }

    @Override
    protected boolean removeFights(List<Fight> fights) {
        KendoLog.entering(this.getClass().getName(), "removeFights");
        String query = "";
        for (Fight fight : fights) {
            query += "DELETE FROM fight WHERE Tournament='" + fight.getTournament().getName() + "' AND Level=" + fight.getLevel() + " AND Team1='" + fight.getTeam1().getName() + "' AND Team2='" + fight.getTeam2().getName() + "' AND Index=" + fight.getIndex() + ";\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteFightError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "removeFights");
        return true;
    }

    @Override
    protected boolean updateFights(HashMap<Fight, Fight> fightsExchange) {
        KendoLog.entering(this.getClass().getName(), "updateFights");
        List<Fight> oldFights = new ArrayList<>(fightsExchange.values());
        List<Fight> newFights = new ArrayList<>(fightsExchange.keySet());
        String query = "";
        for (Fight newFight : newFights) {
            Fight oldFight = fightsExchange.get(newFight);
            query += "UPDATE Fight SET Team1='" + newFight.getTeam1() + "', Tournament='" + newFight.getTournament() + "' Team2='" + newFight.getTeam2() + "', Winner='" + newFight.getWinner() + "', Level='" + newFight.getLevel() + "', MaxWinners='" + newFight.getMaxWinners() + "' FightArea=" + newFight.getAsignedFightArea() + ", "
                    + " WHERE Team1='" + oldFight.getTeam1() + "' AND Team2='" + oldFight.getTeam2() + "' AND Tournament='" + oldFight.getTournament().getName() + "' AND Level='" + oldFight.getLevel() + "' AND Index=" + oldFight.getIndex() + ";\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            return false;
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
    protected List<Duel> getDuels(Tournament tournament) {
        String query = "SELECT * FROM Duel WHERE Tournament='" + tournament.getName() + "'";
        KendoLog.entering(this.getClass().getName(), "getDuels");
        KendoLog.finer(SQL.class.getName(), query);

        List<Duel> results = new ArrayList<>();
        try (Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                Fight fight = FightPool.getInstance().get(tournament,
                        TeamPool.getInstance().get(tournament, rs.getObject("Team1").toString()),
                        TeamPool.getInstance().get(tournament, rs.getObject("Team2").toString()),
                        rs.getInt("Level"), rs.getInt("Index"));
                Duel duel = new Duel(fight, rs.getInt("Order"));

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
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getDuels");
        return results;
    }

    @Override
    protected boolean addDuels(List<Duel> duels) {
        KendoLog.entering(this.getClass().getName(), "addDuels");
        String query = "";
        for (Duel duel : duels) {
            try {
                query += "INSERT INTO duel (Team1, Team2, Tournament, Index, Level, Order, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES ('" + duel.getFight().getTeam1().getName() + "', '" + duel.getFight().getTeam2().getName() + "', '" + duel.getFight().getTournament().getName() + "', "
                        + duel.getFight().getIndex() + ", " + duel.getFight().getLevel() + ", " + duel.getOrder() + ", "
                        + duel.getHits(true).get(0) + ", " + duel.getHits(true).get(1) + ", " + duel.getHits(false).get(0) + ", " + duel.getHits(false).get(1) + ", " + duel.getFaults(true) + ", " + duel.getFaults(false) + ");\n";
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            }
        }

        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.executeUpdate();
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "addDuels");
        return true;
    }

    @Override
    protected boolean removeDuels(List<Duel> duels) {
        KendoLog.entering(this.getClass().getName(), "removeDuels");
        String query = "";
        for (Duel duel : duels) {
            query += "DELETE FROM Duel WHERE Tournament='" + duel.getFight().getTournament().getName() + "' AND Level=" + duel.getFight().getLevel() + " AND Team1='" + duel.getFight().getTeam1().getName() + "' AND Team2='" + duel.getFight().getTeam2().getName() + "' AND Index=" + duel.getFight().getIndex() + " AND Order=" + duel.getOrder() + ";\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteDuelError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "removeDuels");
        return true;
    }

    @Override
    protected boolean updateDuels(HashMap<Duel, Duel> duelsExchange) {
        KendoLog.entering(this.getClass().getName(), "updateDuels");
        List<Duel> oldDuels = new ArrayList<>(duelsExchange.values());
        List<Duel> newDuels = new ArrayList<>(duelsExchange.keySet());
        String query = "";
        for (Duel newDuel : newDuels) {
            Duel oldDuel = duelsExchange.get(newDuel);
            query += "UPDATE Duel SET Team1='" + newDuel.getFight().getTeam1() + "', Tournament='" + newDuel.getFight().getTournament() + "' Team2='" + newDuel.getFight().getTeam2() + "', Level=" + newDuel.getFight().getLevel() + ", Index=" + newDuel.getFight().getIndex() + ", Order=" + newDuel.getOrder() + ", PointPlayer1A='" + newDuel.getHits(true).get(0) + "', PointPlayer1B='" + newDuel.getHits(true).get(1) + "', PointPlayer2A=" + newDuel.getHits(false).get(0) + ", PointPlayer2B=" + newDuel.getHits(false).get(1) + ", FaultsPlayer1=" + newDuel.getFaults(true) + ", FaultsPlayer2=" + newDuel.getFaults(false)
                    + " WHERE Team1='" + oldDuel.getFight().getTeam1() + "' AND Team2='" + oldDuel.getFight().getTeam2() + "' AND Tournament='" + oldDuel.getFight().getTournament().getName() + "' AND Level=" + oldDuel.getFight().getLevel() + " AND Index=" + oldDuel.getFight().getIndex() + " AND Order=" + oldDuel.getOrder() + ";\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            return false;
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
    protected List<Undraw> getUndraws(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "getUndraws");
        String query = "SELECT * FROM undraw WHERE Tournament='" + tournament.getName() + "'";
        KendoLog.finer(SQL.class.getName(), query);
        List<Undraw> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Undraw u = new Undraw(tournament,
                            Integer.parseInt(rs.getObject("Group").toString()),
                            TeamPool.getInstance().get(tournament, rs.getObject("Team").toString()),
                            (Integer) rs.getObject("Player"), (Integer) rs.getObject("Level"));
                    results.add(u);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getUndraws");
        return results;
    }

    @Override
    protected boolean addUndraws(List<Undraw> undraws) {
        KendoLog.entering(this.getClass().getName(), "addUndraws");
        String query = "";
        for (Undraw undraw : undraws) {
            try {
                query += "INSERT INTO Undraw (Tournament, Team, Player, Group, Level) VALUES ('" + undraw.getTournament().getName() + "', '" + undraw.getTeam().getName() + "', " + undraw.getPlayer() + ", " + undraw.getGroupIndex() + ", " + undraw.getLevel() + ");\n";
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            }
        }

        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.executeUpdate();
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "storeUndrawError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "addUndraws");
        return true;
    }

    @Override
    protected boolean removeUndraws(List<Undraw> undraws) {
        KendoLog.entering(this.getClass().getName(), "removeUndraws");
        String query = "";
        for (Undraw undraw : undraws) {
            query += "DELETE FROM Undraw WHERE Tournament='" + undraw.getTournament().getName() + "' AND Level=" + undraw.getLevel() + " AND Team='" + undraw.getTeam().getName() + "' AND Group=" + undraw.getGroupIndex() + ";\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteDuelError", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "removeUndraws");
        return true;
    }

    @Override
    protected boolean updateUndraws(HashMap<Undraw, Undraw> undrawsExchange) {
        KendoLog.entering(this.getClass().getName(), "updateUndraws");
        List<Undraw> oldUndraws = new ArrayList<>(undrawsExchange.values());
        List<Undraw> newUndraws = new ArrayList<>(undrawsExchange.keySet());
        String query = "";
        for (Undraw newUndraw : newUndraws) {
            Undraw oldUndraw = undrawsExchange.get(newUndraw);
            query += "UPDATE Undraw SET Team='" + newUndraw.getTeam().getName() + "', Tournament='" + newUndraw.getTournament().getName() + "', Level=" + newUndraw.getLevel() + ", Group=" + newUndraw.getGroupIndex() + ", Player=" + newUndraw.getPlayer() + ", "
                    + " WHERE Team='" + oldUndraw.getTeam().getName() + "' AND Tournament='" + oldUndraw.getTournament().getName() + "' AND Level=" + oldUndraw.getLevel() + " AND Group=" + oldUndraw.getGroupIndex() + ";\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
            return false;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "updateUndraws");
        return true;
    }

    /**
     * *******************************************************************
     *
     * EXCEPTIONS
     *
     ********************************************************************
     */
    /**
     *
     * @param numberError
     * @return
     */
    protected abstract boolean showSQLError(int numberError);
}
