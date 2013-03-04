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
import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.tournament.TournamentGroupPool;
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
import javax.swing.JOptionPane;

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
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize) VALUES (?,?,?,?,?,?)")) {
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
                    ResultSet rs = s.executeQuery("SELECT * FROM competitor ORDER BY Surname")) {
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
        for (RegisteredPerson person : newPeople) {
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET Name=?, Surname=?, Club=? WHERE ID='" + person.getId() + "'")) {
                stmt.setString(1, person.getName());
                stmt.setString(2, person.getSurname());
                stmt.setString(3, person.getClub().getName());
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
    protected boolean addRoles(Tournament tournament, List<Role> roles) {
        KendoLog.entering(this.getClass().getName(), "addRoles");
        String query = "";
        //Insert team.
        for (Role role : roles) {
            try {
                RegisteredPerson participant = RegisteredPersonPool.get(role.getCompetitorId());
                query += "INSERT INTO role (Role, Tournament, Competitor) VALUES ('" + role.getDatabaseTag() + "','" + tournament.getName() + "','" + participant.getId() + "');\n";
            } catch (NullPointerException npe) { //The team has one competitor less...
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
                    Role role = new Role(TournamentPool.getInstance().get(rs.getObject("Tournament").toString()), rs.getObject("Competitor").toString(), KendoTournamentGenerator.getInstance().getAvailableRoles().getRole(rs.getObject("Role").toString()), rs.getInt("ImpressCard"));
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
            query += "DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + role.getCompetitorId() + "';\n";
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(query);
        } catch (SQLException ex) {
            if (!showSQLError(ex.getErrorCode())) {
                MessageManager.errorMessage(this.getClass().getName(), "deleteRoleBad", "SQL");
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
            query += "UPDATE Role SET Role='" + role.getDatabaseTag() + "', ImpressCard=" + role.isAccreditationPrinted() + ", Diploma=" + role.isDiplomaPrinted() + "  WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + role.getCompetitorId() + "';\n";
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
        for (Club club : newClubs) {
            query += "UPDATE Club SET Country='" + club.getCountry() + "', City='" + club.getCity() + "', Address='" + club.getAddress() + "', Web='" + club.getWeb() + "', Mail='" + club.getMail() + "', Phone='" + club.getPhone() + "', Representative='" + club.getRepresentative() + "' WHERE Name='" + clubsExchange.get(club).getName() + "'\n";
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
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Banner=?, Size=?, FightingAreas=?, PassingTeams=?, TeamSize=?, Type=?, ScoreWin=?, ScoreDraw=?, ScoreType=?, Diploma=?, DiplomaSize=?, Accreditation=?, AccreditationSize=? WHERE Name='" + oldTournament.getName() + "'")) {
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
     * @param tournament
     * @return
     */
    @Override
    protected List<Team> getTeams(Tournament tournament) {
        String query = "SELECT * FROM team WHERE Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name ";
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
            for (int levelIndex = 0; levelIndex < team.levelChangesSize(); levelIndex++) {
                if (team.areMemberOrderChanges(levelIndex)) {
                    for (int indexCompetitor = 0; indexCompetitor < team.getNumberOfMembers(levelIndex); indexCompetitor++) {
                        try {
                            query += "INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, LevelTournament) VALUES ('" + team.getName() + "','" + team.getMember(indexCompetitor, levelIndex).getId() + "','" + team.tournament.getName() + "'," + indexCompetitor + "," + team.group + "," + levelIndex + ");\n";
                        } catch (NullPointerException npe) { //The team has one competitor less...
                        }
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
            query += "DELETE FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "';\n";
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
     * Obtain the members of a team in a specific level.
     *
     * @param team
     * @param verbose
     * @param level
     * @return
     */
    private List<RegisteredPerson> searchTeamMembersInLevel(Team team, boolean verbose, int level) {
        KendoLog.entering(this.getClass().getName(), "searchTeamMembersInLevel");
        List<Competitor> results = new ArrayList<>();
        KendoLog.fine(SQL.class.getName(), "Obtaining the members of team " + team.getName() + " in level " + level);
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "' AND LevelTournament=" + level + " ORDER BY Position ASC")) {
                while (rs.next()) {
                    String memberID = rs.getObject("Member").toString();
                    if (!memberID.equals("")) {
                        Competitor c = selectCompetitor(memberID, verbose);
                        //Add previous void competitors.
                        for (int i = results.size(); i < rs.getInt("Position"); i++) {
                            results.add(new Competitor("", "", "", ""));
                        }
                        results.add(c);
                    } else {
                        //not defined member.
                        results.add(new Competitor("", "", "", ""));
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "searchTeamMembersInLevel");
        return results;
    }

    /**
     * Obtain the members of a team at the start of the championship.
     *
     * @param team
     * @param verbose
     * @return
     */
    private List<List<RegisteredPerson>> searchTeamMembers(Team team, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTeamMembers");
        KendoLog.fine(SQL.class.getName(), "Obtain the members of " + team.getName());
        List<List<RegisteredPerson>> membersPerLevel = new ArrayList<>();
        try {
            Statement s = connection.createStatement();
            String query = "SELECT MAX(LevelTournament) AS level FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "'";
            KendoLog.finest(SQL.class.getName(), query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int level = rs.getInt("level");
                for (int i = 0; i <= level; i++) {
                    List<RegisteredPerson> members = searchTeamMembersInLevel(team, verbose, i);
                    membersPerLevel.add(members);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "searchTeamMembers");
        return membersPerLevel;
    }

    @Override
    public boolean insertMembersOfTeamInLevel(Team t, int level, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "insertMemebersOfTeamInLevel");
        boolean error = false;
        deleteTeamInLevel(t, level, verbose); //To allow a change in the current level and avoid the MySQLIntegrityConstraintViolationException
        try {
            for (int indexCompetitor = 0; indexCompetitor < t.getNumberOfMembers(level); indexCompetitor++) {
                try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, LevelTournament) VALUES (?,?,?,?,?,?)")) {
                    stmt.setString(1, t.getName());
                    stmt.setString(2, t.getMember(indexCompetitor, level).getId());
                    stmt.setString(3, t.tournament.getName());
                    stmt.setInt(4, indexCompetitor);
                    stmt.setInt(5, t.group);
                    stmt.setInt(6, level);
                    stmt.executeUpdate();
                }
            }
        } catch (MySQLIntegrityConstraintViolationException micve) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage(this.getClass().getName(), "repeatedCompetitor", "SQL");
                }
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), micve);
        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage(this.getClass().getName(), "storeTeamError", "SQL");
                    }
                }
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "teamStored", this.getClass().getName(), t.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(this.getClass().getName(), "Team stored: " + t.getName());
        }
        KendoLog.exiting(this.getClass().getName(), "insertMemebersOfTeamInLevel");
        return !error;
    }

    @Override
    public boolean deleteTeamInLevel(Team t, int level, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteTeamInLevel");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM team WHERE Name='" + t.getName() + "' AND LevelTournament >=" + level + " AND Tournament='" + t.tournament.getName() + "'");
            }
            KendoLog.exiting(this.getClass().getName(), "deleteTeamInLevel");
            return true;
        } catch (SQLException ex) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            if (!error) {
                error = true;
            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteTeamInLevel");
        return !error;

    }

    @Override
    public boolean deleteAllMemberChangesInTeams(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteAllMemberChangesInTeams");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM team WHERE LevelTournament > 0  AND Tournament='" + tournament.getName() + "'");
            }
            KendoLog.exiting(this.getClass().getName(), "deleteAllMemberChangesInTeams");
            return true;
        } catch (SQLException ex) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            if (!error) {
                error = true;
            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteAllMemberChangesInTeams");
        return !error;
    }

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
    @Override
    public boolean storeFights(List<Fight> fights, boolean purgeTournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "storeFightsError");
        boolean error = false;
        boolean answer = false;
        if (fights.size() > 0) {
            try {
                //Delete all previous fightManager.
                if (verbose) {
                    answer = MessageManager.questionMessage("deleteFights", "Warning!");
                } else {
                    answer = true;
                }

                if (answer) {
                    try (Statement s = connection.createStatement()) {
                        if (purgeTournament) {
                            deleteFightsOfTournament(fights.get(0).tournament, false);
                            s.executeUpdate("DELETE FROM team WHERE Tournament='" + fights.get(0).tournament.getName() + "' AND LevelTournament > " + 0);
                        }

                        //Obtain the max level of figths.
                        int level = 0;
                        for (int i = 0; i < fights.size(); i++) {
                            if (level < fights.get(i).level) {
                                level = fights.get(i).level;
                            }
                        }

                        for (Fight f : fights) {
                            //Add the fightManager that depends on the level and the teams.
                            s.executeUpdate("INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, LeagueLevel, MaxWinners) VALUES ('" + f.team1.getName() + "','" + f.team2.getName() + "','" + f.tournament.getName() + "','" + f.asignedFightArea + "'," + f.returnWinner() + "," + f.level + "," + f.getMaxWinners() + ")");
                            f.setOverStored(true);
                            for (int i = 0; i < f.duels.size(); i++) {
                                storeDuel(f.duels.get(i), f, i);
                            }
                        }
                    }
                }

            } catch (SQLException ex) {
                error = true;
                MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }

            if (!error && answer) {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "fightStored", this.getClass().getName(), fights.get(0).tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(this.getClass().getName(), "Fight stored: " + fights.get(0).tournament.getName());
            }
        } else {
            KendoLog.exiting(this.getClass().getName(), "storeFightsError");
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "storeFightsError");
        return !error && answer;
    }

    @Override
    public boolean deleteAllFights() {
        KendoLog.entering(this.getClass().getName(), "deleteAllFights");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM fight");
                s.executeUpdate("DELETE FROM duel");
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteAllFights");
        return error;
    }

    @Override
    public boolean storeAllFightsAndDeleteOldOnes(List<Fight> fights) {
        KendoLog.entering(this.getClass().getName(), "storeAllFightsAndDeleteOldOnes");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM fight");
                s.executeUpdate("DELETE FROM duel");
                for (Fight f : fights) {
                    s.executeUpdate("INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, LeagueLevel, MaxWinners) VALUES ('" + f.team1.getName() + "','" + f.team2.getName() + "','" + f.tournament.getName() + "','" + f.asignedFightArea + "'," + f.returnWinner() + "," + f.level + "," + f.getMaxWinners() + ")");
                    f.setOverStored(true);
                    storeDuelsOfFight(f);
                }
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.info(this.getClass().getName(), "Fight stored " + fights.get(0).tournament.getName());
        KendoLog.exiting(this.getClass().getName(), "storeAllFightsAndDeleteOldOnes");
        return !error;
    }

    @Override
    public boolean storeFight(Fight fight, boolean verbose, boolean deleteOldOne) {
        KendoLog.entering(this.getClass().getName(), "storeFight");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                if (deleteOldOne) {
                    deleteFight(fight, false);
                }
                s.executeUpdate("INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, LeagueLevel, MaxWinners) VALUES ('" + fight.team1.getName() + "','" + fight.team2.getName() + "','" + fight.tournament.getName() + "','" + fight.asignedFightArea + "'," + fight.returnWinner() + "," + fight.level + "," + fight.getMaxWinners() + ")");
                fight.setOverStored(true);
            }
        } catch (SQLException | NullPointerException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "fightStored", this.getClass().getName(), fight.tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(this.getClass().getName(), "Fight stored: " + fight.tournament.getName());
        }
        KendoLog.exiting(this.getClass().getName(), "storeFight");
        return !error;
    }

    @Override
    public List<Fight> searchFights(String query, Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "searchFights");
        List<Fight> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    //Fight f = new Fight(getTeamByName(rs.getObject("Team1").toString(), tournament, false),
                    //        getTeamByName(rs.getObject("Team2").toString(), tournament, false),
                    Fight f = new Fight(TeamPool.getInstance().get(tournament, rs.getObject("Team1").toString()),
                            TeamPool.getInstance().get(tournament, rs.getObject("Team2").toString()),
                            tournament,
                            rs.getInt("FightArea"), rs.getInt("Winner"), rs.getInt("LeagueLevel"));
                    f.setMaxWinners(rs.getInt("MaxWinners"));
                    f.calculateOverWithDuels();
                    try {
                        if (f.team1.levelChangesSize() > 0 && f.team2.levelChangesSize() > 0) {
                            for (int i = 0; i < Math.max(f.team1.getNumberOfMembers(0), f.team2.getNumberOfMembers(0)); i++) {
                                Duel d = getDuel(f, i);
                                if (d != null) {
                                    f.setDuel(d, i);
                                }

                            }
                        }
                    } catch (NullPointerException npe) {
                    }
                    results.add(f);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "searchFights");
        return results;
    }

    /**
     * Search all fightManager from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Fight> searchFightsByTournament(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "searchFightsByTournament");
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "'";
        List<Fight> fights = searchFights(query, tournament);
        KendoLog.exiting(this.getClass().getName(), "searchFightsByTournament");
        return fights;
    }

    @Override
    public List<Fight> searchFightsByTournamentLevelEqualOrGreater(Tournament tournament, int level) {
        KendoLog.entering(this.getClass().getName(), "searchFightsByTournamentLevelEqualOrGreater");
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "' AND LeagueLevel >=" + level;
        List<Fight> fights = searchFights(query, tournament);
        KendoLog.exiting(this.getClass().getName(), "searchFightsByTournamentLevelEqualOrGreater");
        return fights;
    }

    /**
     * Search all fightManager from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Fight> searchFightsByTournamentAndFightArea(Tournament tournament, int fightArea) {
        KendoLog.entering(this.getClass().getName(), "searchFightsByTournamentAndFightArea");
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "' AND FightArea=" + fightArea;
        List<Fight> fights = searchFights(query, tournament);
        KendoLog.exiting(this.getClass().getName(), "searchFightsByTournamentAndFightArea");
        return fights;
    }

    /**
     * Search all fightManager from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Fight> searchFightsByTournamentAndTeam(Tournament tournament, String team) {
        KendoLog.entering(this.getClass().getName(), "searchFightsByTournamentAndTeam");
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "' AND (Team1='" + team + "' OR Team2='" + team + "')";
        List<Fight> fights = searchFights(query, tournament);
        KendoLog.exiting(this.getClass().getName(), "searchFightsByTournamentAndTeam");
        return fights;
    }

    @Override
    public int obtainFightID(Fight f) {
        KendoLog.entering(this.getClass().getName(), "obtainFightID");
        int ID = -1;
        try {
            String query = "SELECT ID FROM fight WHERE Tournament='" + f.tournament.getName() + "' AND Team1='" + f.team1.getName() + "' AND Team2='" + f.team2.getName() + "' AND LeagueLevel=" + f.level;
            KendoLog.finest(SQL.class.getName(), query);
            try (Statement s = connection.createStatement();
                    ResultSet rts = s.executeQuery(query)) {
                if (rts.next()) {
                    ID = rts.getInt("ID");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        KendoLog.debug(SQL.class.getName(), "Id of fight " + f.show() + " is " + ID);
        KendoLog.exiting(this.getClass().getName(), "obtainFightID");
        return ID;
    }

    @Override
    public boolean updateFightAsOver(Fight fight) {
        KendoLog.entering(this.getClass().getName(), "updateFightAsOver");
        KendoLog.fine(SQL.class.getName(), "Updating fight '" + fight.team1 + " vs " + fight.team2 + "' as over.");
        boolean error = false;
        try {
            /*
             * Considering the fight over if is updated
             */
            int over = fight.returnWinner();
            if (over == 2) {
                over = 0;
            }
            try (Statement s = connection.createStatement();
                    PreparedStatement stmt = connection.prepareStatement("UPDATE fight SET Winner=? WHERE Tournament='" + fight.tournament.getName() + "' AND Team1='" + fight.team1.getName() + "' AND Team2='" + fight.team2.getName() + "' AND LeagueLevel=" + fight.level)) {
                stmt.setInt(1, over);
                KendoLog.finest(SQL.class.getName(), "UPDATE fight SET Winner=" + over + " WHERE Tournament='" + fight.tournament.getName() + "' AND Team1='" + fight.team1.getName() + "' AND Team2='" + fight.team2.getName() + "' AND LeagueLevel=" + fight.level);
                stmt.executeUpdate();
            }
            fight.setOverStored(true);
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "updateFightAsOver");
        return !error;
    }

    @Override
    public boolean updateFightAsNotOver(Fight fight) {
        KendoLog.entering(this.getClass().getName(), "updateFightAsNotOver");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement();
                    PreparedStatement stmt = connection.prepareStatement("UPDATE fight SET Winner=? WHERE Tournament='" + fight.tournament.getName() + "' AND Team1='" + fight.team1.getName() + "' AND Team2='" + fight.team2.getName() + "' AND LeagueLevel=" + fight.level)) {
                stmt.setInt(1, 2);
                stmt.executeUpdate();
            }
            fight.setOverStored(true);
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "updateFightAsNotOver");
        return !error;
    }

    @Override
    public List<Fight> getAllFights() {
        KendoLog.entering(this.getClass().getName(), "getAllFights");
        List<Fight> fights = getFights(0, Integer.MAX_VALUE);
        KendoLog.exiting(this.getClass().getName(), "getAllFights");
        return fights;
    }

    @Override
    public List<Fight> getFights(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getFights");
        ArrayList<Fight> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM fight LIMIT " + fromRow + "," + numberOfRows)) {

                while (rs.next()) {
                    Fight f = new Fight(getTeamByName(rs.getObject("Team1").toString(), TournamentPool.getTournament(rs.getObject("Tournament").toString()), false),
                            getTeamByName(rs.getObject("Team2").toString(), TournamentPool.getTournament(rs.getObject("Tournament").toString()), false),
                            TournamentPool.getTournament(rs.getObject("Tournament").toString()),
                            rs.getInt("FightArea"), rs.getInt("Winner"), rs.getInt("LeagueLevel"));
                    f.setMaxWinners(rs.getInt("MaxWinners"));
                    if (f.team1.levelChangesSize() > 0 && f.team2.levelChangesSize() > 0) {
                        for (int i = 0; i < Math.max(f.team1.getNumberOfMembers(0), f.team2.getNumberOfMembers(0)); i++) {
                            Duel d = getDuel(f, i);
                            if (d != null) {
                                f.setDuel(d, i);
                            }
                        }
                    }
                    results.add(f);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getFights");
        return results;
    }

    /**
     * Delete fights must delete duels. MySQL use foreign key, but SQLite need
     * to delete one by one
     *
     * @param tournament.getName()
     * @param level
     * @param verbose
     * @return
     */
    @Override
    public boolean deleteFightsOfLevelOfTournament(Tournament tournament, int level, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteFightsOfLevelOfTournament");
        KendoLog.fine(SQL.class.getName(), "Deleting fight of level " + level);
        boolean error;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("deleteFights", "Warning!");
            }
            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM fight WHERE Tournament='" + tournament.getName() + "' AND LeagueLevel >=" + level);
                }
                KendoLog.finer(SQL.class.getName(), "Delete draw fights of tournament.");
                deleteDrawsOfLevelOfTournament(tournament, level);
                KendoLog.exiting(this.getClass().getName(), "deleteFightsOfLevelOfTournament");
                return true;
            } else {
                KendoLog.exiting(this.getClass().getName(), "deleteFightsOfLevelOfTournament");
                return false;
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteFightsOfLevelOfTournament");
        return !error;
    }

    /**
     * Delete fights must delete duels. MySQL use foreign key, but SQLite need
     *
     * @param championship
     * @param verbose
     * @return
     */
    @Override
    public boolean deleteFightsOfTournament(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteFightsOfTournament");
        boolean error;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("deleteFights", "Warning!");
            }
            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM fight WHERE Tournament='" + tournament.getName() + "'");
                }
                deleteDrawsOfTournament(tournament);
                KendoLog.exiting(this.getClass().getName(), "deleteFightsOfTournament");
                return true;
            } else {
                KendoLog.exiting(this.getClass().getName(), "deleteFightsOfTournament");
                return false;
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteFightsOfTournament");
        return !error;
    }

    /**
     * Delete fights and duels of fights.
     *
     * @param fight
     * @param verbose
     * @return
     */
    @Override
    public boolean deleteFight(Fight fight, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteFight");
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("deleteOneFight", "Warning!");
            }
            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM fight WHERE Tournament='" + fight.tournament.getName() + "' AND Team1='" + fight.team1.getName() + "' AND Team2='" + fight.team2.getName() + "' AND LeagueLevel=" + fight.level);
                }
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "deleteFight", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }

        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "fightDeleted", this.getClass().getName(), fight.tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(SQL.class.getName(), "Fights deledte from: " + fight.tournament.getName());
        }
        KendoLog.exiting(this.getClass().getName(), "deleteFight");
        return (answer && !error);
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
    public boolean storeDuel(Duel d, Fight f, int player) {
        KendoLog.entering(this.getClass().getName(), "storeDuelError");
        KendoLog.fine(SQL.class.getName(), "Storing duel " + d.showScore() + " into database.");
        boolean error = false;
        try {
            //Obtain the ID of the fight..

            int fightID = obtainFightID(f);

            //Delete the duel if exist previously.
            Statement s = connection.createStatement();
            KendoLog.finest(SQL.class.getName(), "DELETE FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player);
            s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player);
            s.close();

            //Add the new duel.
            s = connection.createStatement();
            KendoLog.finest(SQL.class.getName(), "INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + player + ",'" + d.hitsFromCompetitorA.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorA.get(1).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + d.faultsCompetitorA + "," + d.faultsCompetitorB + ")");
            s.executeUpdate("INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + player + ",'" + d.hitsFromCompetitorA.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorA.get(1).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + d.faultsCompetitorA + "," + d.faultsCompetitorB + ")");
            d.setStored(true);
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeDuelError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeDuelError");
        return !error;
    }

    @Override
    public boolean storeDuelsOfFight(Fight f) {
        KendoLog.entering(this.getClass().getName(), "storeDuelsOfFight");
        boolean error = false;
        try {
            //Obtain the ID of the fight..
            int fightID = obtainFightID(f);

            //Delete the duel if exist previously.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID);
            s.close();

            //Add the new duels.
            s = connection.createStatement();
            for (int i = 0; i < f.duels.size(); i++) {
                s.executeUpdate("INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + i + ",'" + f.duels.get(i).hitsFromCompetitorA.get(0).getAbbreviature() + "','" + f.duels.get(i).hitsFromCompetitorA.get(1).getAbbreviature() + "','" + f.duels.get(i).hitsFromCompetitorB.get(0).getAbbreviature() + "','" + f.duels.get(i).hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + f.duels.get(i).faultsCompetitorA + "," + f.duels.get(i).faultsCompetitorB + ")");
                f.duels.get(i).setStored(true);
            }
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeDuelError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeDuelsOfFight");
        return !error;
    }

    @Override
    public boolean deleteDuelsOfFight(Fight f) {
        KendoLog.entering(this.getClass().getName(), "deleteDuelsOfFight");
        boolean error = false;
        try {
            //Obtain the ID of the fight..
            int fightID = obtainFightID(f);
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID);
            }
        } catch (SQLException ex) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteDuelsOfFight");
        return !error;
    }

    @Override
    public List<Duel> getDuelsOfFight(Fight f) {
        KendoLog.entering(this.getClass().getName(), "getDuelsOfFight");
        int fightID = obtainFightID(f);
        Statement s;

        Duel d;
        List<Duel> results = new ArrayList<>();
        try {
            s = connection.createStatement();
            try (ResultSet rs = s.executeQuery("SELECT * FROM duel WHERE Fight=" + fightID)) {
                while (rs.next()) {
                    d = new Duel();
                    char c;
                    try {
                        c = rs.getString("PointPlayer1A").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorA.set(0, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer1B").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorA.set(1, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer2A").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorB.set(0, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer2B").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorB.set(1, Score.getScore(c));
                    d.setStored(true);
                    results.add(d);
                }
            }
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getDuelsOfFight");
        return results;
    }

    @Override
    public Duel getDuel(Fight f, int player) {
        KendoLog.entering(this.getClass().getName(), "getDuel");
        Statement s;
        int fightID = obtainFightID(f);

        Duel d = null;
        try {
            s = connection.createStatement();
            try (ResultSet rs = s.executeQuery("SELECT * FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player)) {
                if (rs.next()) {
                    d = new Duel();
                    char c;
                    try {
                        c = rs.getString("PointPlayer1A").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorA.set(0, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer1B").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorA.set(1, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer2A").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorB.set(0, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer2B").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorB.set(1, Score.getScore(c));

                    d.faultsCompetitorA = rs.getInt("FaultsPlayer1");
                    d.faultsCompetitorB = rs.getInt("FaultsPlayer2");

                }
            }
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getDuel");
        d.setStored(true);
        return d;
    }

    @Override
    public List<Duel> getDuelsOfTournament(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "getDuelsOfTournament");
        Statement s;
        List<Duel> results = new ArrayList<>();

        List<Fight> fights = searchFightsByTournament(tournament);
        for (int i = 0; i < fights.size(); i++) {
            results.addAll(getDuelsOfFight(fights.get(i)));
        }
        KendoLog.exiting(this.getClass().getName(), "getDuelsOfTournament");
        return results;
    }

    @Override
    public List<Duel> getDuelsOfcompetitor(String competitorID, boolean teamRight) {
        KendoLog.entering(this.getClass().getName(), "getDuelsOfcompetitor");
        Statement s;
        List<Duel> results = new ArrayList<>();
        Duel d;
        ResultSet rs;

        String queryTeamRight = "select * from duel d1 where EXISTS " + "(SELECT * from fight f1 WHERE f1.ID=d1.Fight AND EXISTS " + "(SELECT * FROM team t1 WHERE (f1.Team2=t1.name) AND EXISTS  " + "(SELECT * FROM competitor c1 WHERE t1.Member='" + competitorID + "') AND " + "d1.OrderPlayer=t1.Position))";

        String queryTeamLeft = "select * from duel d1 where EXISTS " + "(SELECT * from fight f1 WHERE f1.ID=d1.Fight AND EXISTS " + "(SELECT * FROM team t1 WHERE (f1.Team1=t1.name) AND EXISTS  " + "(SELECT * FROM competitor c1 WHERE t1.Member='" + competitorID + "') AND " + "d1.OrderPlayer=t1.Position))";

        try {
            s = connection.createStatement();
            if (teamRight) {
                rs = s.executeQuery(queryTeamRight);
            } else {
                rs = s.executeQuery(queryTeamLeft);
            }

            while (rs.next()) {
                d = new Duel();
                char c;
                try {
                    c = rs.getString("PointPlayer1A").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }

                d.hitsFromCompetitorA.set(0, Score.getScore(c));

                try {
                    c = rs.getString("PointPlayer1B").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }

                d.hitsFromCompetitorA.set(1, Score.getScore(c));

                try {
                    c = rs.getString("PointPlayer2A").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }

                d.hitsFromCompetitorB.set(0, Score.getScore(c));

                try {
                    c = rs.getString("PointPlayer2B").charAt(0);
                } catch (StringIndexOutOfBoundsException siob) {
                    c = ' ';
                }

                d.hitsFromCompetitorB.set(1, Score.getScore(c));

                //Faults
                d.faultsCompetitorA = rs.getInt("FaultsPlayer1");
                d.faultsCompetitorB = rs.getInt("FaultsPlayer2");
                d.setStored(true);
                results.add(d);
            }
            rs.close();
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getDuelsOfcompetitor");
        return results;
    }

    @Override
    public List<Duel> getAllDuels() {
        KendoLog.entering(this.getClass().getName(), "getAllDuels");
        List<Duel> duels = getDuels(0, Integer.MAX_VALUE);
        KendoLog.exiting(this.getClass().getName(), "getAllDuels");
        return duels;
    }

    @Override
    public List<Duel> getDuels(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getDuels");
        Statement s;
        List<Duel> results = new ArrayList<>();
        Duel d;
        try {
            s = connection.createStatement();
            try (ResultSet rs = s.executeQuery("SELECT * FROM duel LIMIT " + fromRow + "," + numberOfRows)) {
                while (rs.next()) {
                    d = new Duel();
                    char c;
                    try {
                        c = rs.getString("PointPlayer1A").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorA.set(0, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer1B").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorA.set(1, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer2A").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorB.set(0, Score.getScore(c));

                    try {
                        c = rs.getString("PointPlayer2B").charAt(0);
                    } catch (StringIndexOutOfBoundsException siob) {
                        c = ' ';
                    }

                    d.hitsFromCompetitorB.set(1, Score.getScore(c));

                    //Faults
                    d.faultsCompetitorA = rs.getInt("FaultsPlayer1");
                    d.faultsCompetitorB = rs.getInt("FaultsPlayer2");
                    d.setStored(true);
                    results.add(d);
                }
            }
            s.close();
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
    public boolean storeUndraw(Tournament tournament, Team team, int order, int group, int level) {
        KendoLog.entering(this.getClass().getName(), "storeUndraw");
        boolean error = false;
        try {
            //Delete the undraw if exist previously.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "' AND Team='" + team.getName() + "'  AND UndrawGroup=" + group + " AND LevelUndraw=" + level);
            s.close();

            //Add the new undraw.
            s = connection.createStatement();
            s.executeUpdate("INSERT INTO undraw (Championship, Team, Player, UndrawGroup, LevelUndraw) VALUES ('" + tournament.getName() + "', '" + team.getName() + "', " + order + ", " + group + ", " + level + ")");
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeUndraw", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeUndraw");
        return !error;
    }

    @Override
    public boolean storeUndraw(Undraw undraw) {
        KendoLog.entering(this.getClass().getName(), "storeUndraw2");
        boolean value = storeUndraw(undraw.getTournament(), undraw.getWinnerTeam(), undraw.getPlayer(), undraw.getIndexOfGroup(), undraw.getLevel());
        KendoLog.exiting(this.getClass().getName(), "storeUndraw2");
        return value;
    }

    private List<Undraw> getUndraws(String sqlQuery) {
        List<Undraw> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(sqlQuery)) {
                while (rs.next()) {
                    Tournament tournament = TournamentPool.getTournament(rs.getObject("Championship").toString());
                    Undraw u = new Undraw(tournament,
                            TournamentGroupPool.getManager(tournament).getGroup(Integer.parseInt(rs.getObject("UndrawGroup").toString())),
                            TeamPool.getInstance().get(TournamentPool.getTournament(rs.getObject("Championship").toString()), rs.getObject("Team").toString()),
                            (Integer) rs.getObject("Player"), (Integer) rs.getObject("LevelUndraw"));
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
        return results;
    }

    @Override
    public List<Undraw> getUndraws(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getUndraws");
        String query = "SELECT * FROM undraw LIMIT " + fromRow + "," + numberOfRows;
        List<Undraw> results = getUndraws(query);
        KendoLog.exiting(this.getClass().getName(), "getUndraws");
        return results;
    }

    @Override
    public List<Undraw> getUndraws(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "getUndraws");
        String query = "SELECT * FROM undraw WHERE championship='" + tournament.getName() + "'";
        List<Undraw> results = getUndraws(query);
        KendoLog.exiting(this.getClass().getName(), "getUndraws");
        return results;
    }

    @Override
    public List<Undraw> getUndraws() {
        KendoLog.entering(this.getClass().getName(), "getUndraws");
        String query = "SELECT * FROM undraw";
        List<Undraw> results = getUndraws(query);
        KendoLog.exiting(this.getClass().getName(), "getUndraws");
        return results;
    }

    @Override
    public boolean storeAllUndraws(List<Undraw> undraws, boolean deleteOldOnes) {
        KendoLog.entering(this.getClass().getName(), "storeAllUndraws");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                if (deleteOldOnes) {
                    s.executeUpdate("DELETE FROM undraw");
                }
                for (int i = 0; i < undraws.size(); i++) {
                    s.executeUpdate("INSERT INTO undraw (Championship, UndrawGroup, Team, Player, LevelUndraw) VALUES ('"
                            + undraws.get(i).getTournament() + "'," + undraws.get(i).getIndexOfGroup() + ",'" + undraws.get(i).getWinnerTeam().getName() + "'," + undraws.get(i).getPlayer() + "," + undraws.get(i).getGroup().getLevel() + ")");
                }
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeFightsError", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.info(this.getClass().getName(), "Fights stored: " + undraws.get(0).getTournament() + ": " + undraws.get(0).getWinnerTeam());
        KendoLog.exiting(this.getClass().getName(), "storeAllUndraws");
        return !error;
    }

    @Override
    public List<Team> getWinnersInUndraws(Tournament tournament, int level, int group) {
        KendoLog.entering(this.getClass().getName(), "getWinnersInUndraws");
        List<Team> teamWinners = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement()) {
                String query = "SELECT * FROM undraw WHERE Championship='" + tournament.getName() + "' AND UndrawGroup=" + group + " AND LevelUndraw=" + level;
                try (ResultSet rs = s.executeQuery(query)) {
                    while (rs.next()) {
                        teamWinners.add(TeamPool.getInstance().get(tournament, rs.getObject("Team").toString()));
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getWinnersInUndraws");
        return teamWinners;
    }

    @Override
    public int getValueWinnerInUndraws(Tournament tournament, String team) {
        KendoLog.entering(this.getClass().getName(), "getValueWinnerInUndraws");
        int value = 0;
        try {
            try (Statement s = connection.createStatement()) {
                String query = "SELECT * FROM undraw WHERE Championship='" + tournament.getName() + "' AND Team='" + team + "'";
                try (ResultSet rs = s.executeQuery(query)) {
                    while (rs.next()) {
                        value++;
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getValueWinnerInUndraws");
        return value;
    }

    @Override
    public int getValueWinnerInUndrawInGroup(Tournament tournament, int group, int level, String team) {
        KendoLog.entering(this.getClass().getName(), "getValueWinnerInUndrawInGroup");
        int value = 0;
        try {
            try (Statement s = connection.createStatement()) {
                String query = "SELECT * FROM undraw WHERE Championship='" + tournament.getName() + "' AND UndrawGroup=" + group + " AND LevelUndraw=" + level + " AND Team='" + team + "'";
                try (ResultSet rs = s.executeQuery(query)) {
                    while (rs.next()) {
                        value++;
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getValueWinnerInUndrawInGroup");
        return value;
    }

    @Override
    public void deleteDrawsOfTournament(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "deleteDrawsOfTournament");
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "'");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteDrawsOfTournament");
    }

    @Override
    public void deleteDrawsOfGroupOfTournament(Tournament tournament, int group) {
        KendoLog.entering(this.getClass().getName(), "deleteDrawsOfGroupOfTournament");
        KendoLog.fine(SQL.class.getName(), "Deleting undraws of group " + group);
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "' AND UndrawGroup=" + group);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteDrawsOfGroupOfTournament");
    }

    @Override
    public void deleteDrawsOfLevelOfTournament(Tournament tournament, int level) {
        KendoLog.entering(this.getClass().getName(), "deleteDrawsOfLevelOfTournament");
        KendoLog.fine(SQL.class.getName(), "Deleting undraws of level " + level);
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "' AND LevelUndraw=" + level);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteDrawsOfLevelOfTournament");
    }

    public List<String> getUndrawMySQLCommands() {
        KendoLog.entering(this.getClass().getName(), "getUndrawMySQLCommands");
        List<String> commands = new ArrayList<>();
        try {
            Statement s = connection.createStatement();
            String query = "SELECT * FROM undraw ORDER BY Championship";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                String command = "INSERT INTO `undraw` VALUES('" + rs.getObject("Championship").toString() + "',"
                        + rs.getInt("UndrawGroup") + "," + rs.getInt("LevelUndraw") + ",'" + rs.getObject("Team").toString() + "',"
                        + rs.getInt("Player")
                        + ");\n";
                commands.add(command);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getUndrawMySQLCommands");
        return commands;
    }

    protected abstract boolean showSQLError(int numberError);
}
