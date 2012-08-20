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
import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.statistics.CompetitorRanking;
import com.softwaremagico.ktg.statistics.TeamRanking;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        Log.fine("Clearing database");
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    /**
     * *******************************************************************
     *
     * EXPORT DATABASE
     *
     ********************************************************************
     */
    /**
     * Export the database into a local file.
     *
     * @param fileName
     */
    public void exportDatabase(String fileName) {
        Log.fine("Exporting database");
        if (!fileName.endsWith(".sql")) {
            fileName += ".sql";
        }
        try {
            File f = new File(fileName);
            f.delete();

            exportClubs(fileName);
            exportCompetitors(fileName);
            exportTournaments(fileName);
            exportRole(fileName);
            exportTeams(fileName);
            exportFights(fileName);
            exportDuels(fileName);
            exportUndraws(fileName);
            MessageManager.translatedMessage("exportDatabase", this.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            MessageManager.errorMessage("exportDatabaseFail", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(e);
        }
    }

    private void exportClubs(String file) {
        Folder.appendTextToFile("LOCK TABLES `club` WRITE;\n", file);
        List<Club> clubs = getAllClubs();
        for (int i = 0; i < clubs.size(); i++) {
            Folder.appendTextToFile("INSERT INTO `club` VALUES('" + clubs.get(i).returnName() + "','" + clubs.get(i).returnCountry() + "','"
                    + clubs.get(i).representativeID + "','" + clubs.get(i).email + "','"
                    + clubs.get(i).phone + "','" + clubs.get(i).returnCity() + "',"
                    + "NULL" + ",'" + clubs.get(i).returnAddress() + "');\n", file);
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    private void exportCompetitors(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        Folder.appendTextToFile("LOCK TABLES `competitor` WRITE;\n", file);
        List<CompetitorWithPhoto> competitors = getAllCompetitorsWithPhoto();
        for (int i = 0; i < competitors.size(); i++) {
            //FileOutputStream fos;
            byte[] photo = {0x0};
            if (competitors.get(i).photoInput != null) {
                try {
                    StoreInputStream(competitors.get(i).photoInput, (int) competitors.get(i).photoSize);
                } catch (Exception ex) {
                    Log.severe(ex.getMessage());
                }
            }
            //Select Photo from competitor where competitor.ListOrder=1 into dumpfile '/tmp/image.jpg';
            Folder.appendTextToFile("INSERT INTO `competitor` VALUES('" + competitors.get(i).getId() + "','" + competitors.get(i).getName() + "','"
                    + competitors.get(i).getSurname() + "','" + competitors.get(i).club + "','" + convertInputStream2String(competitors.get(i).photoInput) + "',"
                    + competitors.get(i).photoSize + "," + i + ");\n", file);
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    /**
     * Funciona!
     *
     * @param inputStream
     * @param length
     */
    private void StoreInputStream(InputStream inputStream, int length) {
        try {
            byte[] data;
            int offset;
            try (InputStream in = new BufferedInputStream(inputStream)) {
                data = new byte[length];
                int bytesRead;
                offset = 0;
                while (offset < length) {
                    try {
                        bytesRead = in.read(data, offset, data.length - offset);
                        if (bytesRead == -1) {
                            break;
                        }
                        offset += bytesRead;
                    } catch (IOException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (offset != length) {
                throw new IOException("Only read " + offset + " bytes; Expected " + length + " bytes");
            }
            /*
             * try (FileOutputStream out = new
             * FileOutputStream("/tmp/test.jpg")) { out.write(data);
             * out.flush(); }
             */
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String convertInputStream2String(InputStream inputStream) {
        //FUNCIONA-> SELECT Photo FROM kendotournament.competitor WHERE competitor.ListOrder=200 INTO OUTFILE '/tmp/Prueba6.txt' FIELDS TERMINATED BY '' ENCLOSED BY '' ESCAPED BY '' LINES TERMINATED BY '' STARTING BY '';
        /*
         * if (inputStream != null) { try { BufferedImage originalImage =
         * ImageIO.read(inputStream);
         *
         * //convert BufferedImage to byte array ByteArrayOutputStream baos =
         * new ByteArrayOutputStream(); ImageIO.write(originalImage, "jpg",
         * baos); baos.flush(); byte[] imageInByte = baos.toByteArray();
         * baos.close(); return new String(imageInByte, "UTF-8"); } catch
         * (IOException ex) { } catch (IllegalArgumentException iae){ } }
         */
        return "";
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            //len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;
    }

    private void exportTournaments(String file) {
        Folder.appendTextToFile("LOCK TABLES `tournament` WRITE;\n", file);
        List<Tournament> tournaments = getAllTournaments();
        for (int i = 0; i < tournaments.size(); i++) {
            Folder.appendTextToFile("INSERT INTO `tournament` VALUES('" + tournaments.get(i).getName() + "','" + convertInputStream2String(tournaments.get(i).bannerInput) + "',"
                    + tournaments.get(i).bannerSize + "," + tournaments.get(i).fightingAreas + "," + tournaments.get(i).howManyTeamsOfGroupPassToTheTree + ","
                    + tournaments.get(i).teamSize + ",'" + tournaments.get(i).mode + "'," + (int) tournaments.get(i).getScoreForWin() + ","
                    + tournaments.get(i).getScoreForDraw() + ",'" + tournaments.get(i).getChoosedScore() + "',NULL,NULL"
                    + ");\n", file);
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    private void exportRole(String file) {
        Folder.appendTextToFile("LOCK TABLES `role` WRITE;\n", file);
        List<String> commands = getRoleSqlCommands();
        for (int i = 0; i < commands.size(); i++) {
            Folder.appendTextToFile(commands.get(i), file);
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    /**
     * Obtain the SQL commands for inserting the roles into the database. Used
     * for exporting database into a file.
     *
     * @return
     */
    public List<String> getRoleSqlCommands() {
        List<String> commands = new ArrayList<>();
        try {
            int id = 0;
            Statement s = connection.createStatement();
            String query = "SELECT * FROM role ORDER BY Tournament";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                String command = "INSERT INTO `role` VALUES('" + rs.getObject("Tournament").toString() + "','"
                        + rs.getObject("Competitor").toString() + "','" + rs.getObject("Role").toString() + "',"
                        + id + "," + rs.getInt("ImpressCard")
                        + ");\n";
                commands.add(command);
                id++;
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return commands;
    }

    private void exportTeams(String file) {
        Folder.appendTextToFile("LOCK TABLES `team` WRITE;\n", file);
        List<Team> teams = getAllTeams();
        for (int i = 0; i < teams.size(); i++) {
            for (int levelIndex = 0; levelIndex < teams.get(i).levelChangesSize(); levelIndex++) {
                for (int member = 0; member < teams.get(i).getNumberOfMembers(levelIndex); member++) {
                    String memberID = "";
                    if (teams.get(i).getMember(member, levelIndex) != null) {
                        memberID = teams.get(i).getMember(member, levelIndex).getId();
                    }
                    Folder.appendTextToFile("INSERT INTO `team` VALUES('" + teams.get(i).getName() + "','"
                            + memberID + "'," + member + "," + levelIndex + ",'"
                            + teams.get(i).tournament.getName() + "'," + teams.get(i).group
                            + ");\n", file);
                }
            }
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    private void exportFights(String file) {
        Folder.appendTextToFile("LOCK TABLES `fight` WRITE;\n", file);
        ArrayList<Fight> fights = getAllFights();
        for (int i = 0; i < fights.size(); i++) {
            Folder.appendTextToFile("INSERT INTO `fight` VALUES('" + fights.get(i).team1.getName() + "','"
                    + fights.get(i).team2.getName() + "','" + fights.get(i).tournament.getName() + "',"
                    + fights.get(i).asignedFightArea + "," + i + ","
                    + fights.get(i).returnWinner() + "," + fights.get(i).level + ","
                    + fights.get(i).getMaxWinners()
                    + ");\n", file);
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    private void exportDuels(String file) {
        Folder.appendTextToFile("LOCK TABLES `duel` WRITE;\n", file);
        ArrayList<Fight> fights = getAllFights();
        int id = 0;
        for (int i = 0; i < fights.size(); i++) {
            for (int j = 0; j < fights.get(i).duels.size(); j++) {
                Folder.appendTextToFile("INSERT INTO `duel` VALUES(" + id + "," + i + ","
                        + j + ",'" + fights.get(i).duels.get(j).hitsFromCompetitorA.get(0).getAbbreviature() + "','"
                        + fights.get(i).duels.get(j).hitsFromCompetitorA.get(1).getAbbreviature() + "','"
                        + fights.get(i).duels.get(j).hitsFromCompetitorB.get(0).getAbbreviature() + "','"
                        + fights.get(i).duels.get(j).hitsFromCompetitorB.get(1).getAbbreviature() + "',"
                        + fights.get(i).duels.get(j).faultsCompetitorA + ","
                        + fights.get(i).duels.get(j).faultsCompetitorB + ","
                        + ");\n", file);
                id++;
            }
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    private void exportUndraws(String file) {
        Folder.appendTextToFile("LOCK TABLES `undraw` WRITE;\n", file);
        List<String> commands = getUndrawMySQLCommands();
        for (int i = 0; i < commands.size(); i++) {
            Folder.appendTextToFile(commands.get(i), file);
        }
        Folder.appendTextToFile("UNLOCK TABLES;\n", file);
        Folder.appendTextToFile("--------------------\n", file);
    }

    public void importDatabase(String fileName) {
        clearDatabase();
        executeScript(fileName);
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
                                    KendoTournamentGenerator.getInstance().showErrorInformation(sql);
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
     * Stores into the database a competitor. Frist check if exist.
     *
     * @param competitorWithPhoto Competitor.
     */
    @Override
    public boolean storeCompetitor(CompetitorWithPhoto competitorWithPhoto, boolean verbose) {
        Log.fine("Storing competitor " + competitorWithPhoto.getSurnameName() + " into database");
        boolean error = false;
        boolean update = false;
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM competitor WHERE ID='" + competitorWithPhoto.getId() + "'")) {

                if (rs.next()) {
                    return updateCompetitor(competitorWithPhoto, verbose);
                } else {
                    try {
                        if (competitorWithPhoto.photoInput.markSupported()) {
                            competitorWithPhoto.photoInput.reset();
                        }
                    } catch (IOException | NullPointerException ex) {
                    }
                    try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize, ListOrder) VALUES (?,?,?,?,?,?,?)")) {
                        stmt.setString(1, competitorWithPhoto.getId());
                        stmt.setString(2, competitorWithPhoto.getName());
                        stmt.setString(3, competitorWithPhoto.getSurname());
                        stmt.setString(4, competitorWithPhoto.club);
                        storeBinaryStream(stmt, 5, competitorWithPhoto.photoInput, (int) competitorWithPhoto.photoSize);
                        stmt.setLong(6, competitorWithPhoto.photoSize);
                        stmt.setLong(7, obtainCompetitorOrder());
                        try {
                            stmt.executeUpdate();
                        } catch (OutOfMemoryError ofm) {
                            MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
                        }
                    }
                }
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName());
        } catch (SQLException ex) {
            error = true;
            if (competitorWithPhoto.photoSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
            } else {
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName());
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            error = true;
        }

        if (!error) {
            if (verbose) {
                if (update) {
                    MessageManager.translatedMessage("competitorUpdated", this.getClass().getName(), competitorWithPhoto.getName() + " " + competitorWithPhoto.getSurname(), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    MessageManager.translatedMessage("competitorStored", this.getClass().getName(), competitorWithPhoto.getName() + " " + competitorWithPhoto.getSurname(), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        return !error;
    }

    /**
     * Stores into the database a competitor, without any check or question.
     * Quick version for import com.softwaremagico.ktg.database option.
     *
     * @param competitorWithPhoto Competitor.
     */
    @Override
    public boolean insertCompetitor(CompetitorWithPhoto competitorWithPhoto) {
        Log.fine("Inserting competitor " + competitorWithPhoto.getSurnameName() + " into database");
        boolean error = false;
        try {
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize, ListOrder) VALUES (?,?,?,?,?,?,?)")) {
                stmt.setString(1, competitorWithPhoto.getId());
                stmt.setString(2, competitorWithPhoto.getName());
                stmt.setString(3, competitorWithPhoto.getSurname());
                stmt.setString(4, competitorWithPhoto.club);
                storeBinaryStream(stmt, 5, competitorWithPhoto.photoInput, (int) competitorWithPhoto.photoSize);
                stmt.setLong(6, competitorWithPhoto.photoSize);
                stmt.setInt(7, obtainCompetitorOrder());
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
                }
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName());
        } catch (SQLException ex) {
            error = true;
            if (competitorWithPhoto.photoSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
            } else {
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName());
            }
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            error = true;
        }
        return !error;
    }

    @Override
    public boolean updateCompetitor(CompetitorWithPhoto competitor, boolean verbose) {
        Log.fine("Updating competitor " + competitor.getSurnameName() + "  from database");
        boolean answer = true;
        boolean error = false;
        if (verbose) {
            answer = MessageManager.questionMessage("questionUpdateCompetitor", "Warning!");
        }
        if (answer || !verbose) {
            try {
                try {
                    if (competitor.photoInput.markSupported()) {
                        competitor.photoInput.reset();
                    }
                } catch (IOException ex) {
                }
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET Name=?, Surname=?, Club=?, Photo=?, PhotoSize=? WHERE ID='" + competitor.getId() + "'")) {
                    stmt.setString(1, competitor.getName());
                    stmt.setString(2, competitor.getSurname());
                    stmt.setString(3, competitor.club);
                    storeBinaryStream(stmt, 4, competitor.photoInput, (int) competitor.photoSize);
                    //stmt.setBlob(4, c.photo);
                    stmt.setLong(5, competitor.photoSize);
                    stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                error = true;
                if (competitor.photoSize > 1048576) {
                    MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
                } else {
                    MessageManager.errorMessage("storeCompetitor", this.getClass().getName());
                }
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
        } else {
            return false;
        }
        return !error;
    }

    @Override
    public boolean updateIdCompetitor(Competitor competitor, boolean verbose) {
        Log.fine("Updating ID of competitor " + competitor.getSurnameName());
        boolean answer = true;
        boolean error = false;
        if (verbose) {
            answer = MessageManager.questionMessage("questionUpdateCompetitor", "Warning!");
        }
        if (answer || !verbose) {
            try {
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET ID=? WHERE Name='" + competitor.getName() + "' AND Surname='" + competitor.getSurname() + "' AND Club='" + competitor.club + "'")) {
                    stmt.setString(1, competitor.getId());
                    stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                error = true;
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
        } else {
            return false;
        }
        return !error;
    }

    @Override
    public boolean updateClubCompetitor(Competitor competitor, boolean verbose) {
        Log.fine("Updating the club of competitor " + competitor.getSurnameName());
        boolean answer = true;
        boolean error = false;
        if (verbose) {
            answer = MessageManager.questionMessage("questionUpdateCompetitor", "Warning!");
        }
        if (answer || !verbose) {
            try {
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET Name=?, Surname=?, Club=?, Photo=?, PhotoSize=? WHERE ID='" + competitor.getId() + "'")) {
                    stmt.setString(1, competitor.getName());
                    stmt.setString(2, competitor.getSurname());
                    stmt.setString(3, competitor.club);
                    stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                error = true;
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
        } else {
            return false;
        }
        return !error;
    }

    @Override
    public List<CompetitorWithPhoto> getCompetitorsWithPhoto(String query, boolean verbose) {
        Log.fine("Obtaining a group of competitors with photo.");
        Log.finer(query);
        List<CompetitorWithPhoto> results = new ArrayList<>();
        String name, surname;
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    name = rs.getObject("Name").toString();
                    surname = rs.getObject("Surname").toString();
                    try {
                        CompetitorWithPhoto c = new CompetitorWithPhoto(rs.getObject("ID").toString(), name, surname, rs.getObject("Club").toString());
                        c.addOrder(rs.getInt("ListOrder"));
                        try {
                            InputStream sImage = getBinaryStream(rs, "Photo");
                            Long size = rs.getLong("PhotoSize");
                            c.addImage(sImage, size);
                        } catch (NullPointerException npe) {
                            c.addImage(null, 0);
                        }
                        results.add(c);
                    } catch (NullPointerException npe) {
                        MessageManager.basicErrorMessage("Error in: " + name + " " + surname, this.getClass().getName());
                    }
                }
            }

            if (results.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName());
            }

            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noDatabase", this.getClass().getName());
        }

        return null;
    }

    @Override
    public List<Competitor> getCompetitors(String query, boolean verbose) {
        Log.fine("Getting competitors.");
        Log.finer(query);
        List<Competitor> results = new ArrayList<>();
        String name, surname;
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    name = rs.getObject("Name").toString();
                    surname = rs.getObject("Surname").toString();
                    try {
                        Competitor c = new Competitor(rs.getObject("ID").toString(), name, surname, rs.getObject("Club").toString());
                        c.addOrder(rs.getInt("ListOrder"));
                        results.add(c);
                    } catch (NullPointerException npe) {
                        MessageManager.basicErrorMessage("Error in: " + name + " " + surname, this.getClass().getName());
                    }
                }
            }

            if (results.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName());
            }

            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noDatabase", this.getClass().getName());
        }

        return null;
    }

    @Override
    public List<Participant> getParticipants(String query, boolean verbose) {
        Log.fine("Getting participants");
        Log.finer(query);
        List<Participant> results = new ArrayList<>();
        String name, surname;
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT ID,Name,Surname FROM competitor ORDER BY Surname")) {
                while (rs.next()) {
                    name = rs.getObject("Name").toString();
                    surname = rs.getObject("Surname").toString();
                    try {
                        Participant p = new Participant(rs.getObject("ID").toString(), name, surname);
                        results.add(p);
                    } catch (NullPointerException npe) {
                        MessageManager.basicErrorMessage("Error in: " + name + " " + surname, this.getClass().getName());
                    }
                }
            }
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noDatabase", this.getClass().getName());
        }

        return null;
    }

    @Override
    public List<CompetitorWithPhoto> getAllCompetitorsWithPhoto() {
        String query = "SELECT * FROM competitor ORDER BY Surname";
        return getCompetitorsWithPhoto(query, false);
    }

    @Override
    public boolean storeAllCompetitors(List<CompetitorWithPhoto> competitors) {
        Log.fine("Storing a list of competitors");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                Log.finer("Deleting previous competitors");
                s.executeUpdate("DELETE FROM competitor");
            }

            for (int i = 0; i < competitors.size(); i++) {
                if (!storeCompetitor(competitors.get(i), false)) {
                    Log.finer("New competitors stored.");
                    error = true;
                } else {
                    Log.severe("Failed to store the list of competitors");
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Competitor> getAllCompetitors() {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor ORDER BY Surname";
        return getCompetitors(query, false);
    }

    @Override
    public List<Participant> getAllParticipants() {
        String query = "SELECT ID,Name,Surname FROM competitor ORDER BY Surname";
        return getParticipants(query, false);
    }

    /**
     * Select all competitors that are not included in any team for a
     * tournament.
     *
     * @param tournament.getName()
     * @return
     */
    @Override
    public List<Competitor> selectAllCompetitorsWithoutTeamInTournament(Tournament tournament) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE NOT EXISTS (SELECT Member FROM team WHERE Tournament='" + tournament.getName() + "' AND competitor.id=team.Member) AND EXISTS (SELECT * FROM role WHERE Tournament='" + tournament.getName() + "' AND role.Competitor=competitor.id AND (role.Role='Competitor' OR role.Role='VolunteerK')) ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Select all competitors, organizer and refereer of the tournament that
     * still have not the accreditation card.
     *
     * @param tournament.getName()
     * @return
     */
    @Override
    public List<CompetitorWithPhoto> selectAllParticipantsInTournamentWithoutAccreditation(Tournament tournament, boolean printAll) {
        String query = "SELECT * FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + tournament.getName() + "' AND competitor.ID=role.Competitor";
        if (!printAll) {
            query += " AND role.ImpressCard=0 ";
        }
        query += ") ORDER BY Surname";
        return getCompetitorsWithPhoto(query, false);
    }

    /**
     * Select all competitors, organizer and refereer of the tournament.
     *
     * @param tournament.getName()
     * @return
     */
    @Override
    public List<Competitor> selectAllCompetitorsInTournament(Tournament tournament) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + tournament.getName() + "' AND competitor.ID=role.Competitor AND (role.Role='Competitor' OR role.Role='VolunteerK')) ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Select participants of all selected roles. If no role is selected, select
     * all participants.
     *
     * @param roleTags roles that have diploma.
     * @param tournament.getName()
     * @param printAll if false select competitors without printed diploma.
     * before.
     * @return
     */
    @Override
    public List<Competitor> selectAllCompetitorWithDiplomaInTournament(RoleTags roleTags, Tournament tournament, boolean printAll) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + tournament.getName() + "' AND competitor.ID=role.Competitor ";

        if (!printAll) {
            query += " AND role.Diploma=0 ";
        }

        //Select the roles
        if (roleTags != null && roleTags.size() > 0) {
            query += " AND (";
            for (int i = 0; i < roleTags.size(); i++) {
                query += " role.Role='" + roleTags.get(i).tag + "' ";
                if (i < roleTags.size() - 1) {
                    query += " OR ";
                }
            }
            query += ")";
        }

        query += ") ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Select all VCLO.
     *
     * @param tournament.getName()
     * @return
     */
    @Override
    public List<Competitor> selectAllVolunteersInTournament(Tournament tournament) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor c1 WHERE EXISTS (SELECT Competitor FROM role r1 WHERE Tournament='" + tournament.getName() + "' AND c1.ID=r1.Competitor AND (r1.Role='VCLO' OR r1.Role='VolunteerK')) ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Obtain from database a competitor.
     *
     * @param id The Identificaction Number of the Competitor.
     * @return Competitor.
     */
    @Override
    public CompetitorWithPhoto selectCompetitor(String id, boolean verbose) {
        Log.fine("Obtaining a competitor with ID " + id);
        CompetitorWithPhoto c = null;
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM competitor WHERE ID='" + id + "'")) {
                if (rs.next()) {
                    c = new CompetitorWithPhoto(rs.getObject("ID").toString(), rs.getObject("Name").toString(), rs.getObject("Surname").toString(), rs.getObject("Club").toString());
                    c.addOrder(rs.getInt("ListOrder"));
                    try {
                        InputStream sImage = getBinaryStream(rs, "Photo");
                        Long size = rs.getLong("PhotoSize");
                        c.addImage(sImage, size);
                    } catch (NullPointerException npe) {
                        c.addImage(null, 0);
                    }
                }
            }
            return c;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        return null;
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarName(String name, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor WHERE Name LIKE '%" + name + "%' ORDER BY Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarSurname(String surname, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor WHERE Surname LIKE '%" + surname + "%' ORDER BY Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarID(String id, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor WHERE ID LIKE '%" + id + "%' ORDER BY Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<Competitor> searchCompetitorsByClub(String clubName, boolean verbose) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE Club='" + clubName + "' ORDER BY Surname";
        return getCompetitors(query, verbose);
    }

    @Override
    public List<Competitor> searchCompetitorsWithoutClub(boolean verbose) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE Club IS NULL ORDER BY Surname";
        return getCompetitors(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarClub(String clubName, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor WHERE Club LIKE '%" + clubName + "%' ORDER BY Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsByClubAndTournament(String clubName, Tournament tournament, boolean getImage, boolean verbose) {
        String query = "SELECT c1.* FROM competitor c1 INNER JOIN role r1 ON c1.ID=r1.Competitor WHERE c1.Club='" + clubName + "' AND r1.Tournament='" + tournament.getName() + "'  ORDER BY c1.Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public boolean deleteCompetitor(Competitor competitor, boolean verbose) {
        Log.fine("Deleting the competitor " + competitor.getSurnameName());
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionDeleteCompetitor", "Warning!");
            }

            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM competitor WHERE ID='" + competitor.getId() + "'");
                }
            }

        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteCompetitor", this.getClass().getName());
                    }
                    Log.severe("deleteCompetitor", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
                Log.severe("noRunningDatabase", this.getClass().getName());
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage("competitorDeleted", this.getClass().getName(), competitor.getName() + " " + competitor.getSurname(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("competitorDeleted", this.getClass().getName(), competitor.getName() + " " + competitor.getSurname());
        }
        return !error && (answer || !verbose);
    }

    @Override
    public List<CompetitorRanking> getCompetitorsOrderByScore(boolean verbose, Tournament tournament) {
        Log.fine("Getting competitors ordered by score from " + tournament.getName());
        List<CompetitorRanking> competitorsOrdered = new ArrayList<>();
        String query = "SELECT " + "t3.Name as Name, " + "t3.Surname as Surname, " + "t3.ID as ID, " + "t1.NumVictorias, " + "TotalPtos " + "FROM( " + "SELECT " + "count(Distinct t1.IdDuelo) as NumVictorias, " + "CASE " + "WHEN TotalJugador1 > TotalJugador2 THEN t1.IdCompetidor1  " + "ELSE t2.IdCompetidor2 END " + "as  IDGanador " + "FROM " + "(SELECT " + "t1.ID as IdCompetidor1, " + "t1.NAME as Competidor1, " + "t4.ID as IdDuelo, " + "CASE " + "WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalJugador1 " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team1 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer " + ")t1 " + "INNER JOIN " + "(SELECT " + "t1.ID as IdCompetidor2, " + "t1.NAME as Competidor2, " + "t4.ID as IdDuelo, " + "CASE " + "WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalJugador2 " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team2 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer " + ")t2 " + "ON t1.IdDuelo = t2.IdDuelo " + "WHERE " + "TotalJugador1 <> TotalJugador2   " + "GROUP BY " + "CASE " + "WHEN TotalJugador1 > TotalJugador2 THEN t1.IdCompetidor1  " + "ELSE t2.IdCompetidor2 END " + ") t1 " + "RIGHT OUTER JOIN " + "(SELECT " + "t1.IdCompetidor, " + "sum(TotalPtos) as TotalPtos " + "FROM " + "(SELECT " + "t1.ID as IdCompetidor, " + "CASE " + "WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalPtos " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team1 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer " + "UNION ALL " + "SELECT " + "t1.ID as IdCompetidor, " + "CASE " + "WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalPtos " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team2 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer) t1 " + "GROUP BY " + "t1.IdCompetidor " + ") " + "t2 " + "ON t2.IdCompetidor = t1.IDGanador " + "INNER JOIN " + "competitor t3 " + "ON t2.IdCompetidor = t3.ID " + "ORDER BY " + "NumVictorias DESC,TotalPtos DESC, t3.surname asc;";

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {

                while (rs.next()) {
                    competitorsOrdered.add(new CompetitorRanking(rs.getObject("Name").toString(), rs.getObject("Surname").toString(), rs.getObject("ID").toString(), rs.getInt("NumVictorias"), rs.getInt("TotalPtos")));
                }
            }
            if (competitorsOrdered.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName());
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }

        return competitorsOrdered;
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsByRoleAndTournament(String roleName, Tournament tournament, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor, role WHERE competitor.ID=role.Competitor AND role.Role='" + roleName + "' AND role.Tournament='" + tournament.getName() + "'  ORDER BY competitor.Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchRefereeByTournament(Tournament tournament, boolean getImage, boolean verbose) {
        return searchCompetitorsByRoleAndTournament("Referee", tournament, getImage, verbose);
    }

    @Override
    public Integer searchVolunteerOrder(Competitor competitor, Tournament tournament) {
        Log.fine("Obtain the numeration order of the volunteer " + competitor.getSurnameName());
        List<Competitor> allVolunteers = selectAllVolunteersInTournament(tournament);

        for (int i = 0; i < allVolunteers.size(); i++) {
            if (allVolunteers.get(i).getId().equals(competitor.getId())) {
                return i + 1; //Order starts in 1. 
            }
        }
        return null;
    }

    /**
     * SQLite does not support autoincrement, then I've implemented an
     * alternative.
     *
     * @return
     */
    protected int obtainCompetitorOrder() {
        Log.fine("Obtain the numeration of competitors into the database");
        int order = 0;
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT count(*) from competitor");
            if (rs.next()) {
                order = rs.getInt(1);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }
        return order + 1;
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
    public boolean storeRole(RoleTag roleTag, Tournament tournament, Participant participant, boolean verbose) {
        Log.fine("Storing the role of participant " + participant.getSurnameName() + " in tournament " + tournament.getName() + " as " + roleTag.name);
        boolean inserted = true;
        try {
            try (Statement s = connection.createStatement()) {
                Log.finer("Deleting role of participant " + participant.getShortSurname() + " in tournament " + tournament.getName());
                s.executeUpdate("DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + participant.getId() + "'");
            }
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("INSERT INTO role (Role, Tournament, Competitor) VALUES ('" + roleTag.tag + "','" + tournament.getName() + "','" + participant.getId() + "')");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            inserted = false;
        } catch (NullPointerException npe) {
            inserted = false;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        if (inserted && verbose) {
            MessageManager.translatedMessage("roleChanged", this.getClass().getName(), participant.getName() + " " + participant.getSurname() + " -> " + roleTag.name, JOptionPane.INFORMATION_MESSAGE);
            Log.info("Role of " + participant.getSurnameName() + " changed to " + roleTag.name);
        }
        return inserted;
    }

    @Override
    public boolean storeRole(Role role, boolean verbose) {
        Log.fine("Storing role " + role.roleName);
        boolean inserted = true;
        try {
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("INSERT INTO role (Role, Tournament, Competitor,ImpressCard) VALUES ('" + role.roleName + "','" + role.tournament + "','" + role.competitorID() + "'," + role.impressCard + ")");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            inserted = false;
        } catch (NullPointerException npe) {
            inserted = false;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        if (inserted && verbose) {
            MessageManager.translatedMessage("roleChanged", this.getClass().getName(), role.competitorID() + " -> " + role.roleName, JOptionPane.INFORMATION_MESSAGE);
            Log.finer("Role " + role.roleName + " stored.");
        }

        return inserted;
    }

    @Override
    public boolean deleteRole(Tournament tournament, Participant participant) {
        Log.fine("Deleting role of participant " + participant.getSurnameName() + " in tournament " + tournament.getName());
        boolean answer = false;
        try {
            Statement s = connection.createStatement();

            answer = MessageManager.questionMessage("roleDeleteQuestion", "Warning!");
            if (answer) {
                s.executeUpdate("DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + participant.getId() + "'");
                MessageManager.translatedMessage("roleDeleted", this.getClass().getName(), participant.getName() + " " + participant.getSurname(), JOptionPane.INFORMATION_MESSAGE);
                s.close();
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return answer;
    }

    @Override
    public String getTagRole(Tournament tournament, Participant participant) {
        Log.fine("Getting roleTag of participant " + participant.getSurnameName() + " in tournament " + tournament.getName());
        String role = null;
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + participant.getId() + "'")) {
                rs.next();
                role = rs.getObject("Role").toString();
            }


        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                showSQLError(ex.getErrorCode());
            }

        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }

        Log.finer("RolTag obtained for participant " + participant.getSurnameName() + " in tournament " + tournament.getName() + " is " + role);
        return role;
    }

    @Override
    public void setAllParticipantsInTournamentAsAccreditationPrinted(Tournament tournament) {
        Log.fine("Disabling printing all accreditations cards of " + tournament.getName());
        try {
            try (Statement st = connection.createStatement();
                    PreparedStatement stmt = connection.prepareStatement("UPDATE role SET ImpressCard=1 WHERE Tournament='" + tournament.getName() + "'")) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    public void setParticipantInTournamentAsAccreditationPrinted(Competitor competitor, Tournament tournament) {
        Log.fine("Disabling printing the accreditation card of " + competitor.getSurnameName() + " in tournament " + tournament.getName());
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(competitor);
        setParticipantsInTournamentAsAccreditationPrinted(competitors, tournament);
    }

    /**
     * Set all selected participants as accredition card already printed. If no
     * competitors are selected, set all participants of the championship.
     *
     * @param competitors
     * @param tournament.getName()
     */
    @Override
    public void setParticipantsInTournamentAsAccreditationPrinted(List<Competitor> competitors, Tournament tournament) {
        Log.fine("Disabling printing the accreditation card of a list of competitors in tournament " + tournament.getName());
        try {
            //Basic query
            String query = "UPDATE role SET ImpressCard=1 WHERE Tournament='" + tournament.getName() + "'";

            //Select the competitors
            if (competitors != null && competitors.size() > 0) {
                query += " AND (";
                for (int i = 0; i < competitors.size(); i++) {
                    query += " Competitor='" + competitors.get(i).getId() + "' ";
                    if (i < competitors.size() - 1) {
                        query += " OR ";
                    }
                }
                query += ")";
            }
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    /**
     * Set all selected roles as diploma already printed. If no roles are
     * selected, set all participants of the championship.
     *
     * @param roleTags
     * @param tournament.getName()
     */
    @Override
    public void setAllParticipantsInTournamentAsDiplomaPrinted(RoleTags roleTags, Tournament tournament) {
        Log.fine("Disabling printing all diplomas of " + tournament.getName() + " for " + roleTags);
        try {
            //Basic query
            String query = "UPDATE role SET Diploma=1 WHERE Tournament='" + tournament.getName() + "'";

            //Select the roles
            if (roleTags != null && roleTags.size() > 0) {
                query += " AND (";
                for (int i = 0; i < roleTags.size(); i++) {
                    query += " Role='" + roleTags.get(i).tag + "' ";
                    if (i < roleTags.size() - 1) {
                        query += " OR ";
                    }
                }
                query += ")";
            }
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        Log.fine("Getting all roles.");
        List<Role> roles = new ArrayList<>();
        try {
            int id = 0;
            Statement s = connection.createStatement();
            String query = "SELECT * FROM role ORDER BY Tournament,Role";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Role role = new Role(rs.getObject("Tournament").toString(), rs.getObject("Competitor").toString(), rs.getObject("Role").toString(), rs.getInt("ImpressCard"));
                roles.add(role);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return roles;
    }

    @Override
    public boolean storeAllRoles(List<Role> roles) {
        Log.fine("Storing all roles into database.");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                Log.finer("Deleting all roles");
                s.executeUpdate("DELETE FROM role");
            }

            for (int i = 0; i < roles.size(); i++) {
                if (!storeRole(roles.get(i), false)) {
                    Log.severe("Role " + roles.get(i).roleName);
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
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
    public boolean storeClub(Club club, boolean verbose) {
        Log.fine("Storing club " + club.returnName() + " into database.");
        boolean inserted = true;
        boolean update = false;
        boolean answer = false;
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM club WHERE Name='" + club.returnName() + "'")) {
                if (rs.next()) {
                    if (verbose) {
                        answer = MessageManager.questionMessage("questionUpdateClub", "Warning!");
                    }
                    if (answer || !verbose) {
                        Log.finer("Club exist, updating club.");
                        try (PreparedStatement stmt = connection.prepareStatement("UPDATE club SET Country=?, City=?, Phone=?, Mail=?, Representative=?, Address=?, Web=? WHERE Name='" + club.returnName() + "'")) {
                            stmt.setString(1, club.returnCountry());
                            stmt.setString(2, club.returnCity());
                            stmt.setString(3, club.phone);
                            stmt.setString(4, club.email);
                            stmt.setString(5, club.representativeID);
                            stmt.setString(6, club.returnAddress());
                            stmt.setString(7, club.returnWeb());
                            stmt.executeUpdate();
                        }
                        update = true;
                    } else {
                        return false;
                    }
                } else {
                    try (Statement st = connection.createStatement()) {
                        st.executeUpdate("INSERT INTO club (Name, Country, City, Address, Web, Mail, Phone, Representative) VALUES ('" + club.returnName() + "','" + club.returnCountry() + "','" + club.returnCity() + "','" + club.returnAddress() + "','" + club.returnWeb() + "','" + club.email + "'," + club.phone + ",'" + club.representativeID + "')");
                    }
                }
            }
        } catch (MySQLIntegrityConstraintViolationException micve) {
            inserted = false;
            if (verbose) {
                MessageManager.errorMessage("nameClub", this.getClass().getName());
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(micve);
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            inserted = false;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            inserted = false;
            if (verbose) {
                MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        if (inserted && verbose) {
            if (!update) {
                MessageManager.translatedMessage("clubStored", this.getClass().getName(), club.returnName(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                MessageManager.translatedMessage("clubUpdated", this.getClass().getName(), club.returnName(), JOptionPane.INFORMATION_MESSAGE);
            }

        }
        return inserted;
    }

    @Override
    public List<String> returnClubsName() {
        Log.fine("Obtaining the name of all clubs.");
        List<String> clubs = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("select * FROM club ORDER BY Name")) {
                while (rs.next()) {
                    clubs.add(rs.getString(1));
                }
            }
            return clubs;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage("MySQL database connection fail", this.getClass().getName());
        }

        return null;
    }

    @Override
    public List<Club> getAllClubs() {
        Log.fine("Obtaining all clubs.");
        List<Club> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("select * FROM club ORDER BY Name")) {
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
                        c.storeAddress(rs.getObject("Address").toString());
                    } catch (NullPointerException npe) {
                    }
                    try {
                        c.storeWeb(rs.getObject("Web").toString());
                    } catch (NullPointerException npe) {
                    }
                    if (c != null) {
                        try {
                            c.RefreshRepresentative(rs.getObject("Representative").toString(), rs.getObject("Mail").toString(), rs.getObject("Phone").toString());
                        } catch (NullPointerException npe) {
                        }
                        results.add(c);
                    }
                }
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage("MySQL database connection fail", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return results;
    }

    @Override
    public boolean storeAllClubs(List<Club> clubs) {
        Log.fine("Storing a list of clubs.");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM club");
            }

            for (int i = 0; i < clubs.size(); i++) {
                if (!storeClub(clubs.get(i), false)) {
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Club> searchClub(String query, boolean verbose) {
        Log.fine("Searching club.");
        Log.finer(query);
        List<Club> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {

                while (rs.next()) {
                    Club c = new Club(rs.getObject("Name").toString(), rs.getObject("Country").toString(), rs.getObject("City").toString());
                    try {
                        c.storeAddress(rs.getObject("Address").toString());
                    } catch (NullPointerException npe) {
                    }
                    try {
                        c.storeWeb(rs.getObject("Web").toString());
                    } catch (NullPointerException npe) {
                    }
                    try {
                        c.RefreshRepresentative(rs.getObject("Representative").toString(), rs.getObject("Mail").toString(), rs.getObject("Phone").toString());
                    } catch (NullPointerException npe) {
                    }
                    results.add(c);
                }
            }
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage("noResults", this.getClass().getName());
                }
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        return results;
    }

    @Override
    public List<Club> searchClubByName(String name, boolean verbose) {
        String query = "SELECT * FROM club WHERE Name LIKE '%" + name + "%' ORDER BY Name";
        return searchClub(query, verbose);
    }

    @Override
    public List<Club> searchClubByCity(String city, boolean verbose) {
        String query = "SELECT * FROM club WHERE City LIKE '%" + city + "%' ORDER BY Name";
        return searchClub(query, verbose);
    }

    @Override
    public List<Club> searchClubByCountry(String country, boolean verbose) {
        String query = "SELECT * FROM club WHERE Country LIKE '%" + country + "%' ORDER BY Name";
        return searchClub(query, verbose);
    }

    @Override
    public boolean deleteClub(Club club, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        Log.fine("Deleting club " + club.returnName());
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionDeleteClub", "Warning!");
            }

            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM club WHERE Name='" + club.returnName() + "'");
                }
            }

        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteClub", this.getClass().getName());
                    }
                    KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                }

            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }

            }
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage("clubDeleted", this.getClass().getName(), club.returnName(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("clubDeleted", this.getClass().getName(), club.returnName());
        }

        return !error && (answer || !verbose);
    }

    /**
     * *******************************************************************
     *
     * TOURNAMENT
     *
     ********************************************************************
     */
    /**
     * Store a Tournament into the database.
     *
     * @param club
     */
    @Override
    public boolean storeTournament(Tournament tournament, boolean verbose) {
        boolean error = false;
        boolean update = false;
        Log.fine("Storing tournament " + tournament.getName());
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM tournament WHERE Name='" + tournament.getName() + "'")) {

                if (rs.next()) {
                    return updateTournament(tournament, verbose);
                } else {
                    try {
                        if (tournament.bannerInput.markSupported()) {
                            tournament.bannerInput.reset();
                        }
                    } catch (IOException | NullPointerException ex) {
                        KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                    }
                    try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO tournament (Name, Banner, Size, FightingAreas, PassingTeams, TeamSize, Type, ScoreWin, ScoreDraw, ScoreType, Diploma, DiplomaSize, Accreditation, AccreditationSize) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                        stmt.setString(1, tournament.getName());
                        storeBinaryStream(stmt, 2, tournament.bannerInput, (int) tournament.bannerSize);
                        stmt.setLong(3, tournament.bannerSize);
                        stmt.setInt(4, tournament.fightingAreas);
                        stmt.setInt(5, tournament.howManyTeamsOfGroupPassToTheTree);
                        stmt.setInt(6, tournament.teamSize);
                        stmt.setString(7, tournament.mode.getSqlName());
                        stmt.setFloat(8, tournament.getScoreForWin());
                        stmt.setFloat(9, tournament.getScoreForDraw());
                        stmt.setString(10, tournament.getChoosedScore());
                        storeBinaryStream(stmt, 11, tournament.diplomaInput, (int) tournament.diplomaSize);
                        stmt.setLong(12, tournament.diplomaSize);
                        storeBinaryStream(stmt, 13, tournament.accreditationInput, (int) tournament.accreditationSize);
                        stmt.setLong(14, tournament.accreditationSize);
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (MysqlDataTruncation mdt) {
            MessageManager.errorMessage("storeImage", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(mdt);
            error = true;
        } catch (SQLException ex) {
            MessageManager.errorMessage("storeTournament", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            error = true;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            error = true;
        }

        if (!error) {
            if (!update) {
                if (verbose) {
                    MessageManager.translatedMessage("tournamentStored", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                Log.info("tournamentStored", this.getClass().getName(), tournament.getName());
            } else {
                if (verbose) {
                    MessageManager.translatedMessage("tournamentUpdated", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                Log.info("tournamentUpdated", this.getClass().getName(), tournament.getName());
            }
        }

        return !error;
    }

    @Override
    public boolean deleteTournament(Tournament tournament) {
        boolean answer = false;
        Log.fine("Deleting tournament " + tournament.getName());
        try {
            answer = MessageManager.questionMessage("tournamentDeleteQuestion", "Warning!");
            if (answer) {
                try (Statement s = connection.createStatement()) {
                    deleteFightsOfTournament(tournament, false);
                    Log.fine("Deleting teams of tournament.");
                    s.executeUpdate("DELETE FROM team WHERE Tournament='" + tournament.getName() + "'");
                    Log.fine("Deleting roles of tournament.");
                    s.executeUpdate("DELETE FROM role WHERE Tournament='" + tournament.getName() + "'");
                    Log.fine("Deleting tournament.");
                    s.executeUpdate("DELETE FROM tournament WHERE Name='" + tournament.getName() + "'");
                    MessageManager.translatedMessage("tournamentDeleted", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        return answer;
    }

    @Override
    public boolean updateTournament(Tournament tournament, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        Log.fine("Updating tournament.");
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionUpdateTournament", "Warning!");
            }
            if (!verbose || answer) {
                try {
                    if (tournament.bannerInput.markSupported()) {
                        tournament.bannerInput.reset();
                    }
                } catch (IOException ex) {
                }
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Banner=?, Size=?, FightingAreas=?, PassingTeams=?, TeamSize=?, Type=?, ScoreWin=?, ScoreDraw=?, ScoreType=?, Diploma=?, DiplomaSize=?, Accreditation=?, AccreditationSize=? WHERE Name='" + tournament.getName() + "'")) {
                    storeBinaryStream(stmt, 1, tournament.bannerInput, (int) tournament.bannerSize);
                    stmt.setLong(2, tournament.bannerSize);
                    stmt.setInt(3, tournament.fightingAreas);
                    stmt.setInt(4, tournament.howManyTeamsOfGroupPassToTheTree);
                    stmt.setInt(5, tournament.teamSize);
                    stmt.setString(6, tournament.mode.getSqlName());
                    stmt.setFloat(7, tournament.getScoreForWin());
                    stmt.setFloat(8, tournament.getScoreForDraw());
                    stmt.setString(9, tournament.getChoosedScore());
                    storeBinaryStream(stmt, 10, tournament.diplomaInput, (int) tournament.diplomaSize);
                    stmt.setLong(11, tournament.diplomaSize);
                    storeBinaryStream(stmt, 12, tournament.accreditationInput, (int) tournament.accreditationSize);
                    stmt.setLong(13, tournament.accreditationSize);
                    stmt.executeUpdate();
                }
            } else {
                return false;
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName());
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeTournament", this.getClass().getName());
        } catch (NullPointerException npe) {
            error = true;
            MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
        }

        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage("tournamentUpdated", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("tournamentUpdated", this.getClass().getName(), tournament.getName());
        }

        return !error;
    }

    @Override
    public List<Tournament> getAllTournaments() {
        List<Tournament> results = new ArrayList<>();
        Log.fine("Getting all tournaments.");
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM tournament ORDER BY Name")) {
                while (rs.next()) {
                    Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentTypes.getType(rs.getObject("Type").toString()));
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
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }

        return null;
    }

    @Override
    public boolean storeAllTournaments(List<Tournament> tournaments) {
        boolean error = false;
        Log.fine("Storing a list of tournaments.");
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM tournament");
            }

            for (int i = 0; i < tournaments.size(); i++) {
                if (!storeTournament(tournaments.get(i), false)) {
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public Tournament getTournamentByName(String tournamentName, boolean verbose) {
        Log.fine("Get tournament " + tournamentName);
        try {
            Tournament t;
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM tournament WHERE Name='" + tournamentName + "' ")) {
                rs.next();
                t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentTypes.getType(rs.getObject("Type").toString()));
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
            }
            return t;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }

        return null;
    }

    @Override
    public List<Tournament> searchTournament(String query, boolean verbose) {
        Log.fine("Searching tournament.");
        Log.finer(query);
        List<Tournament> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentTypes.getType(rs.getObject("Type").toString()));
                    t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"), rs.getInt("ScoreDraw"));
                    InputStream sImage = (InputStream) getBinaryStream(rs, "Banner");
                    Long size = rs.getLong("Size");
                    t.addBanner(sImage, size);
                    results.add(t);
                }
            }
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage("noResults", this.getClass().getName());
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return results;
    }

    @Override
    public List<Tournament> searchTournamentsByName(String tournamentName, boolean verbose) {
        String query = "SELECT * FROM tournament WHERE Name LIKE '%" + tournamentName + "%' ORDER BY Name";
        return searchTournament(query, verbose);
    }

    @Override
    public void deleteGroupsOfTournament(Tournament tournament, List<Team> teams) {
        Log.fine("Deleting groups of tournament " + tournament.getName());
        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            t.group = 0;
            updateTeamGroupOfLeague(tournament, t);
        }
    }

    @Override
    public void storeDiplomaImage(Tournament tournament, InputStream Image, long imageSize) {
        Log.fine("Store diploma image of " + tournament.getName());
        try {
            try {
                if (Image.markSupported()) {
                    Image.reset();
                }
            } catch (IOException ex) {
            }
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Diploma=?, DiplomaSize=? WHERE Name='" + tournament.getName() + "'")) {
                storeBinaryStream(stmt, 1, Image, (int) imageSize);
                stmt.setLong(2, imageSize);
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
                }
            }
        } catch (MysqlDataTruncation mdt) {
            //error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName());
        } catch (SQLException ex) {
            //error = true;
            if (imageSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
            } else {
                //ShowMessage.errorMessage("storeCompetitor", this.getClass().getName());
            }
        } catch (NullPointerException npe) {
            //error = true;
            MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
        }
    }

    @Override
    public void storeAccreditationImage(Tournament tournament, InputStream Image, long imageSize) {
        Log.fine("Store accreditation of " + tournament.getName());
        try {
            try {
                if (Image.markSupported()) {
                    Image.reset();
                }
            } catch (IOException ex) {
            }
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Accreditation=?, AccredotationSize=? WHERE Name='" + tournament.getName() + "'")) {
                storeBinaryStream(stmt, 1, Image, (int) imageSize);
                stmt.setLong(2, imageSize);
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
                }
            }
        } catch (MysqlDataTruncation mdt) {
            //error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName());
        } catch (SQLException ex) {
            //error = true;
            if (imageSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName());
            } else {
                //ShowMessage.errorMessage("storeCompetitor", this.getClass().getName());
            }
        } catch (NullPointerException npe) {
            //error = true;
            MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
        }
    }

    @Override
    public int getLevelTournament(Tournament tournament) {
        Log.fine("Getting max level of " + tournament.getName());
        String query = "SELECT MAX(LeagueLevel) FROM fight WHERE Tournament='" + tournament.getName() + "';";

        int level = -1;
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                rs.next();
                level = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return level;
    }

    /**
     * *******************************************************************
     *
     * TEAM
     *
     ********************************************************************
     */
    /**
     * Check if the team already exists into the database and update or insert
     * it.
     *
     * @param club
     */
    @Override
    public boolean storeTeam(Team team, boolean verbose) {
        Log.fine("Storing team " + team.getName());
        boolean error = false;
        boolean answer = false;
        boolean update = false;
        //Delete all old entries for these team if exists.
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs1 = s.executeQuery("SELECT * FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "'")) {
                if (rs1.next()) {
                    if (verbose) {
                        answer = MessageManager.questionMessage("questionUpdateTeam", "Warning!");
                    }
                    if (answer || !verbose) {
                        Log.finer("Deleting an existing team " + team.getName());
                        s.executeUpdate("DELETE FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "' AND LeagueGroup=" + team.group);
                    } else {
                        return false;
                    }
                }

                insertTeam(team, verbose);
            }
        } catch (MySQLIntegrityConstraintViolationException micve) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("repeatedCompetitor", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(micve);
        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("storeTeam", this.getClass().getName());
                    }
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        if (!error) {
            if (update) {
                if (verbose) {
                    MessageManager.translatedMessage("teamUpdated", this.getClass().getName(), team.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                Log.info("teamUpdated", this.getClass().getName(), team.getName());
            } else {
                if (verbose) {
                    MessageManager.translatedMessage("teamStored", this.getClass().getName(), team.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                Log.info("teamStored", this.getClass().getName(), team.getName());
            }
        }
        return !error;
    }

    /**
     * Insert a team into the database.
     *
     * @param team
     * @param verbose
     * @return
     */
    @Override
    public boolean insertTeam(Team team, boolean verbose) {
        Log.fine("Inserting team " + team.getName());
        boolean error = false;
        //Insert team.
        for (int levelIndex = 0; levelIndex < team.levelChangesSize(); levelIndex++) {
            if (team.changesInThisLevel(levelIndex)) {
                for (int indexCompetitor = 0; indexCompetitor < team.getNumberOfMembers(levelIndex); indexCompetitor++) {
                    try {
                        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, LevelTournament) VALUES (?,?,?,?,?,?)")) {
                            stmt.setString(1, team.getName());
                            stmt.setString(2, team.getMember(indexCompetitor, levelIndex).getId());
                            stmt.setString(3, team.tournament.getName());
                            stmt.setInt(4, indexCompetitor);
                            stmt.setInt(5, team.group);
                            stmt.setInt(6, levelIndex);
                            stmt.executeUpdate();
                        }
                    } catch (NullPointerException npe) { //The team has one competitor less...
                    } catch (SQLException ex) {
                        if (!error) {
                            error = true;
                            if (!showSQLError(ex.getErrorCode())) {
                                if (verbose) {
                                    MessageManager.errorMessage("storeTeam", this.getClass().getName());
                                }
                            }
                        }
                        KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                    }
                }
            }
        }
        return !error;
    }

    /**
     * Obtain the members of a team in a specific level.
     *
     * @param team
     * @param verbose
     * @param level
     * @return
     */
    private List<Competitor> searchTeamMembersInLevel(Team team, boolean verbose, int level) {
        List<Competitor> results = new ArrayList<>();
        Log.fine("Obtaining the members of team " + team.getName() + " in level " + level);
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return results;
    }

    /**
     * Obtain the members of a team at the start of the championship.
     *
     * @param team
     * @param verbose
     * @return
     */
    private List<List<Competitor>> searchTeamMembers(Team team, boolean verbose) {
        Log.fine("Obtain the members of " + team.getName());
        List<List<Competitor>> membersPerLevel = new ArrayList<>();
        try {
            Statement s = connection.createStatement();
            String query = "SELECT MAX(LevelTournament) AS level FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "'";
            Log.finest(query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int level = rs.getInt("level");
                for (int i = 0; i <= level; i++) {
                    List<Competitor> members = searchTeamMembersInLevel(team, verbose, i);
                    membersPerLevel.add(members);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return membersPerLevel;
    }

    /**
     * Search a team.
     *
     * @param query
     * @param verbose
     * @return
     */
    @Override
    public List<Team> searchTeam(String query, boolean verbose) {
        Log.fine("Searching team.");
        Log.finer(query);

        List<Team> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Team t = new Team(rs.getObject("Name").toString(), TournamentPool.getTournament(rs.getObject("Tournament").toString()));
                    t.addGroup(rs.getInt("LeagueGroup"));
                    t.setMembers(searchTeamMembers(t, false));
                    results.add(t);
                }
            }
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage("noResults", this.getClass().getName());
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return results;
    }

    @Override
    public List<Team> searchTeamsByNameAndTournament(String name, Tournament tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Name LIKE '%" + name + "%' AND Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name";
        return searchTeam(query, verbose);
    }

    @Override
    public Team getTeamByName(String name, Tournament tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Name='" + name + "' AND Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name";
        List<Team> teams = searchTeam(query, verbose);
        if (!teams.isEmpty()) {
            return searchTeam(query, verbose).get(0);
        } else {
            if (verbose) {
                MessageManager.customMessage("Error obtaining team " + name, "Error", 0);
                Log.warning("Error obtaining team " + name);
            }
            return null;
        }
    }

    @Override
    public List<Team> searchTeamsByTournament(Tournament tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Tournament LIKE '" + tournament.getName() + "' GROUP BY Name ORDER BY Name ";
        return searchTeam(query, verbose);
    }

    @Override
    public List<Team> searchTeamsByTournamentExactName(Tournament tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name";
        return searchTeam(query, verbose);
    }

    @Override
    public List<Team> searchTeamsByLevel(Tournament tournament, int level, boolean verbose) {
        String query = "SELECT t1.name,t1.Tournament,t1.LeagueGroup FROM team t1 LEFT JOIN fight f1 ON (t1.Name=f1.team1 OR t1.Name=f1.team2)  WHERE t1.Tournament='" + tournament.getName() + "' AND f1.Tournament='" + tournament.getName() + "' AND f1.LeagueLevel>=" + level + " GROUP BY Name ORDER BY Name ";
        return searchTeam(query, verbose);
    }

    @Override
    public List<Team> getAllTeams() {
        String query = "SELECT * FROM team GROUP BY Name,Tournament ORDER BY Name ";
        return searchTeam(query, false);
    }

    @Override
    public boolean storeAllTeams(List<Team> teams) {
        Log.fine("Store a group of teams.");
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM team");
            }

            for (int i = 0; i < teams.size(); i++) {
                if (!storeTeam(teams.get(i), false)) {
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public void updateTeamGroupOfLeague(Tournament tournament, Team team) {
        Log.fine("Upgrading team " + team.getName() + " of " + tournament.getName());
        try {
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE team SET LeagueGroup=? WHERE Name='" + team.getName() + "' AND Tournament='" + tournament.getName() + "'")) {
                stmt.setInt(1, team.group);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

    }

    @Override
    public boolean deleteTeam(Team team, boolean verbose) {
        Log.fine("Deleting team " + team.getName());
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionDeleteTeam", "Warning!");
            }

            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "'");
                }
            }

        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteTeam", this.getClass().getName());
                    }
                }

            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage("teamDeleted", this.getClass().getName(), team.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("teamDeleted", this.getClass().getName(), team.getName());
        }
        return !error && (answer || !verbose);
    }

    @Override
    public boolean deleteTeamByName(String teamName, String toournamentName, boolean verbose) {
        Log.debug("Deleting team " + teamName);
        boolean error = false;
        boolean answer = false;
        int sol = 0;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionDeleteTeam", "Warning!");
            }

            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    sol = s.executeUpdate("DELETE FROM team WHERE Name='" + teamName + "' AND Tournament='" + toournamentName + "'");
                }
            }
        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteTeam", this.getClass().getName());
                    }
                    KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                }

            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        if (!error && answer) {
            if (sol > 0) {
                if (verbose) {
                    MessageManager.translatedMessage("teamDeleted", this.getClass().getName(), teamName, JOptionPane.INFORMATION_MESSAGE);
                }
                Log.info("teamDeleted", this.getClass().getName(), teamName);
            } else {
                MessageManager.errorMessage("teamNotDeleted", this.getClass().getName());
            }

        }
        return !error && (answer || !verbose);
    }

    @Override
    public void setIndividualTeams(Tournament tournament) {
        Log.fine("Creating individual teams for tournament " + tournament.getName());
        List<Competitor> competitors = selectAllCompetitorsInTournament(tournament);
        MessageManager.translatedMessage("oneTeamPerCompetitor", this.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
        for (int i = 0; i < competitors.size(); i++) {
            Team t = new Team(competitors.get(i).getSurname() + ", " + competitors.get(i).getName(), tournament);
            t.addOneMember(competitors.get(i), 0);
            storeTeam(t, false);
        }

    }

    @Override
    public boolean deleteTeamsOfTournament(Tournament tournament, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionDeleteTeams", "Warning!");
            }

            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM team WHERE Tournament='" + tournament.getName() + "'");
                }
            }

        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteTeam", this.getClass().getName());
                    }
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return !error && (answer || !verbose);
    }

    @Override
    public List<TeamRanking> getTeamsOrderByScore(Tournament tournament, boolean verbose) {
        List<TeamRanking> teamsOrdered = new ArrayList<>();
        //String query = "SELECT " + "t1.NomEquipo as Equipo, " + "ifnull(t3.NumVictorias,0) as Victorias, " + "ifnull(t2.TotalDuelos,0) as Duelos, " + "ifnull(t1.TotalPtos,0) as Puntos " + "FROM " + "(SELECT " + "		t1.NomEquipo as NomEquipo,  " + "		sum(TotalPtos) as TotalPtos  " + "FROM  " + "		(SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos  " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team1  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "				t2.Position = t4.OrderPlayer " + "				 " + "		UNION ALL  " + "		 " + "		SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team2  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "			t2.Position = t4.OrderPlayer " + " " + "		) t1  " + "GROUP BY  " + "		t1.NomEquipo " + ") t1  " + "LEFT OUTER JOIN " + "(	SELECT  " + "		CASE  " + "			WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "			ELSE t2.Team  " + "		END as NomEquipo, " + "		count(Distinct t1.IdDuelo) as TotalDuelos " + "FROM  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo1  " + "	FROM  " + "			team t2  " + "			INNER JOIN  " + "			fight t3  " + "			ON t2.Name = t3.Team1  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "			INNER JOIN duel t4  " + "			ON t3.ID = t4.Fight  " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t1  " + "	INNER JOIN  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo2  " + "	FROM  " + "			team t2  " + "			INNER JOIN fight t3  " + "			ON t2.Name = t3.Team2  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "			INNER JOIN duel t4 " + "			ON t3.ID = t4.Fight " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t2 " + "	ON t1.IdDuelo = t2.IdDuelo  " + "	WHERE  " + "			TotalDuelo1 <> TotalDuelo2    " + "	GROUP BY " + "			CASE  " + "				WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "				ELSE t2.Team  " + "			END " + "	) t2  " + "	ON t1.NomEquipo = t2.NomEquipo " + "	LEFT OUTER JOIN " + "	(SELECT " + "			CASE " + "					WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "					WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "			END as NomEquipo, " + "			count(idcombate) as NumVictorias " + "		FROM " + "		(SELECT  " + "				idcombate, " + "				EquipoIzq, " + "				EquipoDer, " + "				Sum(NumDuelosGanados1) as VictoriaIzq, " + "				Sum(NumDuelosGanados2) as VictoriaDer, " + "				Sum(TotalDueloA) as TotalPuntosA, " + "				Sum(TotalDueloB) as TotalPuntosB" + "		FROM  " + "				(SELECT " + "						t1.Team as EquipoIzq, " + "						t2.Team as EquipoDer, " + "						t1.IdCombate, " + "						TotalDuelo1 as TotalDueloA, " + "						TotalDuelo2 as TotalDueloB," + "						CASE WHEN TotalDuelo1 > TotalDuelo2 THEN 1 " + "						ELSE 0 END as NumDuelosGanados1, " + "						CASE WHEN TotalDuelo2 > TotalDuelo1 THEN 1 " + "						ELSE 0 END  as NumDuelosGanados2 " + "								 " + "				FROM	 " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo1  " + "					FROM  " + "							team t2  " + "							INNER JOIN  " + "							fight t3  " + "							ON t2.Name = t3.Team1  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "							INNER JOIN duel t4  " + "							ON t3.ID = t4.Fight  " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t1  " + "					INNER JOIN  " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo2  " + "					FROM  " + "							team t2  " + "							INNER JOIN fight t3  " + "							ON t2.Name = t3.Team2  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "							INNER JOIN duel t4 " + "							ON t3.ID = t4.Fight " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t2 " + "					ON t1.IdDuelo = t2.IdDuelo  " + "					AND t1.IdCombate = t2.IDCombate " + "				WHERE  " + "						TotalDuelo1 <> TotalDuelo2 " + "				)t1 " + "			GROUP BY " + "					idcombate, " + "					EquipoIzq, " + "					EquipoDer " + "		) t1 " + "	GROUP BY " + "	CASE " + "			WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "			WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "	END   " + "	)t3  " + "	ON t1.NomEquipo = t3.NomEquipo " + "ORDER BY " + "	ifnull(t3.NumVictorias,0) DESC, " + "	ifnull(t2.TotalDuelos,0) DESC, " + "	ifnull(t1.TotalPtos,0)  DESC, " + "	t1.NomEquipo ";
        String query = "SELECT " + "t1.NomEquipo as Equipo, " + "ifnull(t3.NumVictorias,0) as Victorias, " + "ifnull(t2.TotalDuelos,0) as Duelos, " + "ifnull(t1.TotalPtos,0) as Puntos " + "FROM " + "(SELECT " + "		t1.NomEquipo as NomEquipo,  " + "		sum(TotalPtos) as TotalPtos  " + "FROM  " + "		(SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos  " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team1  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "				t2.Position = t4.OrderPlayer " + "				 " + "		UNION ALL  " + "		 " + "		SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team2  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "			t2.Position = t4.OrderPlayer " + " " + "		) t1  " + "GROUP BY  " + "		t1.NomEquipo " + ") t1  " + "LEFT OUTER JOIN " + "(	SELECT  " + "		CASE  " + "			WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "			ELSE t2.Team  " + "		END as NomEquipo, " + "		count(Distinct t1.IdDuelo) as TotalDuelos " + "FROM  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo1  " + "	FROM  " + "			team t2  " + "			INNER JOIN  " + "			fight t3  " + "			ON t2.Name = t3.Team1  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "')  " + "			INNER JOIN duel t4  " + "			ON t3.ID = t4.Fight  " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t1  " + "	INNER JOIN  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo2  " + "	FROM  " + "			team t2  " + "			INNER JOIN fight t3  " + "			ON t2.Name = t3.Team2  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "') " + "			INNER JOIN duel t4 " + "			ON t3.ID = t4.Fight " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t2 " + "	ON t1.IdDuelo = t2.IdDuelo  " + "	WHERE  " + "			TotalDuelo1 <> TotalDuelo2    " + "	GROUP BY " + "			CASE  " + "				WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "				ELSE t2.Team  " + "			END " + "	) t2  " + "	ON t1.NomEquipo = t2.NomEquipo " + "	LEFT OUTER JOIN " + "	(SELECT " + "			CASE " + "					WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "					WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "			END as NomEquipo, " + "			count(idcombate) as NumVictorias " + "		FROM " + "		(SELECT  " + "				idcombate, " + "				EquipoIzq, " + "				EquipoDer, " + "				Sum(NumDuelosGanados1) as VictoriaIzq, " + "				Sum(NumDuelosGanados2) as VictoriaDer, " + "				Sum(TotalDueloA) as TotalPuntosA, " + "				Sum(TotalDueloB) as TotalPuntosB" + "		FROM  " + "				(SELECT " + "						t1.Team as EquipoIzq, " + "						t2.Team as EquipoDer, " + "						t1.IdCombate, " + "						TotalDuelo1 as TotalDueloA, " + "						TotalDuelo2 as TotalDueloB," + "						CASE WHEN TotalDuelo1 > TotalDuelo2 THEN 1 " + "						ELSE 0 END as NumDuelosGanados1, " + "						CASE WHEN TotalDuelo2 > TotalDuelo1 THEN 1 " + "						ELSE 0 END  as NumDuelosGanados2 " + "								 " + "				FROM	 " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo1  " + "					FROM  " + "							team t2  " + "							INNER JOIN  " + "							fight t3  " + "							ON t2.Name = t3.Team1  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "')  " + "							INNER JOIN duel t4  " + "							ON t3.ID = t4.Fight  " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t1  " + "					INNER JOIN  " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo2  " + "					FROM  " + "							team t2  " + "							INNER JOIN fight t3  " + "							ON t2.Name = t3.Team2  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + tournament.getName() + "' OR 'All' = '" + tournament.getName() + "') " + "							INNER JOIN duel t4 " + "							ON t3.ID = t4.Fight " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t2 " + "					ON t1.IdDuelo = t2.IdDuelo  " + "					AND t1.IdCombate = t2.IDCombate " + "				WHERE  " + "						TotalDuelo1 <> TotalDuelo2 " + "				)t1 " + "			GROUP BY " + "					idcombate, " + "					EquipoIzq, " + "					EquipoDer " + "		) t1 " + "	GROUP BY " + "	CASE " + "			WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "			WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "	END   " + "	)t3  " + "	ON t1.NomEquipo = t3.NomEquipo " + "ORDER BY " + "	ifnull(t3.NumVictorias,0) DESC, " + "	ifnull(t2.TotalDuelos,0) DESC, " + "	ifnull(t1.TotalPtos,0)  DESC, " + "	t1.NomEquipo ";
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    teamsOrdered.add(new TeamRanking(rs.getObject("Equipo").toString(), tournament, rs.getInt("Victorias"), 0, rs.getInt("Duelos"), 0, rs.getInt("Puntos")));
                }
            }
            if (teamsOrdered.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName());
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }
        return teamsOrdered;
    }

    @Override
    public Team getTeamOfCompetitor(String competitorID, Tournament tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Member='" + competitorID + "' AND Tournament='" + tournament.getName() + "' GROUP BY Name";
        try {
            return searchTeam(query, verbose).get(0);
        } catch (IndexOutOfBoundsException iob) {
            return null;
        }
    }

    @Override
    public boolean insertMemebersOfTeamInLevel(Team t, int level, boolean verbose) {
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
            //connection.commit();
            //s.execute("COMMIT");
        } catch (MySQLIntegrityConstraintViolationException micve) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("repeatedCompetitor", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(micve);
        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("storeTeam", this.getClass().getName());
                    }
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage("teamStored", this.getClass().getName(), t.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("teamStored", this.getClass().getName(), t.getName());
        }
        return !error;
    }

    @Override
    public boolean extendTeamInLevel(Team t, int level, boolean verbose) {
        return false;
    }

    @Override
    public boolean deleteTeamInLevel(Team t, int level, boolean verbose) {
        boolean error = false;

        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM team WHERE Name='" + t.getName() + "' AND LevelTournament >=" + level + " AND Tournament='" + t.tournament.getName() + "'");
            }

            return true;
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return !error;

    }

    @Override
    public boolean deleteAllMemberChangesInTeams(Tournament tournament, boolean verbose) {
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM team WHERE LevelTournament > 0  AND Tournament='" + tournament.getName() + "'");
            }
            return true;
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage("noRunningDatabase", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
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
    public boolean storeFights(ArrayList<Fight> fights, boolean purgeTournament, boolean verbose) {
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
                        }
                    }
                }

            } catch (SQLException ex) {
                error = true;
                MessageManager.errorMessage("storeFights", this.getClass().getName());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }

            if (!error && answer) {
                if (verbose) {
                    MessageManager.translatedMessage("fightStored", this.getClass().getName(), fights.get(0).tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                Log.info("fightStored", this.getClass().getName(), fights.get(0).tournament.getName());
            }
        } else {
            return false;
        }

        return !error && answer;
    }

    @Override
    public boolean storeAllFightsAndDeleteOldOnes(ArrayList<Fight> fights) {
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
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        Log.info("fightStored", this.getClass().getName(), fights.get(0).tournament.getName());
        return !error;
    }

    @Override
    public boolean storeFight(Fight fight, boolean verbose, boolean deleteOldOne) {
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
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage("fightStored", this.getClass().getName(), fight.tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("fightStored", this.getClass().getName(), fight.tournament.getName());
        }
        return !error;
    }

    @Override
    public ArrayList<Fight> searchFights(String query, Tournament tournament) {
        ArrayList<Fight> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Fight f = new Fight(getTeamByName(rs.getObject("Team1").toString(), tournament, false),
                            getTeamByName(rs.getObject("Team2").toString(), tournament, false),
                            tournament,
                            rs.getInt("FightArea"), rs.getInt("Winner"), rs.getInt("LeagueLevel"));
                    f.changeMaxWinners(rs.getInt("MaxWinners"));
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        return results;
    }

    /**
     * Search all fightManager from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public ArrayList<Fight> searchFightsByTournament(Tournament tournament) {
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "'";
        return searchFights(query, tournament);
    }

    @Override
    public ArrayList<Fight> searchFightsByTournamentLevelEqualOrGreater(Tournament tournament, int level) {
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "' AND LeagueLevel >=" + level;
        return searchFights(query, tournament);
    }

    /**
     * Search all fightManager from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public ArrayList<Fight> searchFightsByTournamentAndFightArea(Tournament tournament, int fightArea) {
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "' AND FightArea=" + fightArea;
        return searchFights(query, tournament);
    }

    /**
     * Search all fightManager from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public ArrayList<Fight> searchFightsByTournamentAndTeam(Tournament tournament, String team) {
        String query = "SELECT * FROM fight WHERE Tournament='" + tournament.getName() + "' AND (Team1='" + team + "' OR Team2='" + team + "')";
        return searchFights(query, tournament);
    }

    @Override
    public int obtainFightID(Fight f) {
        int ID = -1;
        try {
            String query = "SELECT ID FROM fight WHERE Tournament='" + f.tournament.getName() + "' AND Team1='" + f.team1.getName() + "' AND Team2='" + f.team2.getName() + "' AND LeagueLevel=" + f.level;
            Log.finest(query);
            try (Statement s = connection.createStatement();
                    ResultSet rts = s.executeQuery(query)) {
                if (rts.next()) {
                    ID = rts.getInt("ID");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        Log.debug("Id of fight " + f.show() + " is " + ID);
        return ID;
    }

    @Override
    public boolean updateFightAsOver(Fight fight) {
        Log.fine("Updating fight '" + fight.team1 + " vs " + fight.team2 + "' as over.");
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
                Log.finest("UPDATE fight SET Winner=" + over + " WHERE Tournament='" + fight.tournament.getName() + "' AND Team1='" + fight.team1.getName() + "' AND Team2='" + fight.team2.getName() + "' AND LeagueLevel=" + fight.level);
                stmt.executeUpdate();
            }
            fight.setOverStored(true);
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public boolean updateFightAsNotOver(Fight fight) {
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
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public ArrayList<Fight> getAllFights() {
        ArrayList<Fight> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM fight")) {

                while (rs.next()) {
                    Fight f = new Fight(getTeamByName(rs.getObject("Team1").toString(), TournamentPool.getTournament(rs.getObject("Tournament").toString()), false),
                            getTeamByName(rs.getObject("Team2").toString(), TournamentPool.getTournament(rs.getObject("Tournament").toString()), false),
                            TournamentPool.getTournament(rs.getObject("Tournament").toString()),
                            rs.getInt("FightArea"), rs.getInt("Winner"), rs.getInt("LeagueLevel"));
                    f.changeMaxWinners(rs.getInt("MaxWinners"));
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
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }

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
        Log.fine("Deleting fight of level " + level);
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
                Log.finer("Delete draw fights of tournament.");
                deleteDrawsOfLevelOfTournament(tournament, level);
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
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
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
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
            MessageManager.errorMessage("deleteFight", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage("fightDeleted", this.getClass().getName(), fight.tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("fightDeleted", this.getClass().getName(), fight.tournament.getName());
        }

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
        Log.fine("Storing duel " + d.showScore() + " into database.");
        boolean error = false;
        try {
            //Obtain the ID of the fight..

            int fightID = obtainFightID(f);

            //Delete the duel if exist previously.
            Statement s = connection.createStatement();
            Log.finest("DELETE FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player);
            s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player);
            s.close();

            //Add the new duel.
            s = connection.createStatement();
            Log.finest("INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + player + ",'" + d.hitsFromCompetitorA.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorA.get(1).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + d.faultsCompetitorA + "," + d.faultsCompetitorB + ")");
            s.executeUpdate("INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + player + ",'" + d.hitsFromCompetitorA.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorA.get(1).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + d.faultsCompetitorA + "," + d.faultsCompetitorB + ")");
            d.setStored(true);
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeDuel", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        return !error;
    }

    @Override
    public boolean storeDuelsOfFight(Fight f) {
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
            MessageManager.errorMessage("storeDuel", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        return !error;
    }

    @Override
    public boolean deleteDuelsOfFight(Fight f) {
        boolean error = false;
        try {
            //Obtain the ID of the fight..
            int fightID = obtainFightID(f);
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID);
            }
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Duel> getDuelsOfFight(Fight f) {
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
                    results.add(d);
                }
            }
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }

        return results;
    }

    @Override
    public Duel getDuel(Fight f, int player) {
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }

        return d;
    }

    @Override
    public List<Duel> getDuelsOfTournament(Tournament tournament) {
        Statement s;
        List<Duel> results = new ArrayList<>();

        ArrayList<Fight> fights = searchFightsByTournament(tournament);
        for (int i = 0; i < fights.size(); i++) {
            results.addAll(getDuelsOfFight(fights.get(i)));
        }

        return results;
    }

    @Override
    public List<Duel> getDuelsOfcompetitor(String competitorID, boolean teamRight) {
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

                results.add(d);
            }
            rs.close();
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        return results;
    }

    @Override
    public List<Duel> getAllDuels() {
        Statement s;
        List<Duel> results = new ArrayList<>();
        Duel d;
        try {
            s = connection.createStatement();
            try (ResultSet rs = s.executeQuery("SELECT * FROM duel")) {
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

                    results.add(d);
                }
            }
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

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
    public boolean storeUndraw(Tournament tournament, String team, int order, int group) {
        boolean error = false;
        try {
            //Delete the undraw if exist previously.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "' AND Team='" + team + "'  AND UndrawGroup=" + group);
            s.close();

            //Add the new undraw.
            s = connection.createStatement();
            s.executeUpdate("INSERT INTO undraw (Championship, Team, Player, UndrawGroup) VALUES ('" + tournament.getName() + "', '" + team + "', " + order + ", " + group + ")");
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeUndraw", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Undraw> getAllUndraws() {
        String query = "SELECT * FROM undraw ";

        List<Undraw> results = new ArrayList<>();

        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Undraw u = new Undraw(rs.getObject("Championship").toString(), (Integer) rs.getObject("UndrawGroup"), rs.getObject("Team").toString(), (Integer) rs.getObject("Player"));
                    results.add(u);
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        return results;
    }

    @Override
    public boolean storeAllUndraws(List<Undraw> undraws) {
        boolean error = false;
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw");
                for (int i = 0; i < undraws.size(); i++) {
                    s.executeUpdate("INSERT INTO undraw (Championship, UndrawGroup, Team, Player) VALUES ('"
                            + undraws.get(i).getTournament() + "'," + undraws.get(i).getGroup() + ",'" + undraws.get(i).getWinnerTeam() + "'," + undraws.get(i).getPlayer() + ")");
                }
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        Log.info("fightStored", this.getClass().getName(), undraws.get(0).getTournament() + ": " + undraws.get(0).getWinnerTeam());
        return !error;
    }

    @Override
    public String getWinnerInUndraws(Tournament tournament, int group, List<Team> teams) {
        String teamWinner = null;
        try {
            try (Statement s = connection.createStatement()) {
                String query = "SELECT * FROM undraw WHERE Championship='" + tournament.getName() + "' AND UndrawGroup=" + group;
                try (ResultSet rs = s.executeQuery(query)) {
                    if (rs.next()) {
                        teamWinner = rs.getObject("Team").toString();
                    }
                }
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return teamWinner;
    }

    @Override
    public int getValueWinnerInUndraws(Tournament tournament, String team) {
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
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return value;
    }

    @Override
    public int getValueWinnerInUndrawInGroup(Tournament tournament, int group, String team) {
        int value = 0;
        try {
            try (Statement s = connection.createStatement()) {
                String query = "SELECT * FROM undraw WHERE Championship='" + tournament.getName() + "' AND UndrawGroup=" + group + " AND Team='" + team + "'";
                try (ResultSet rs = s.executeQuery(query)) {
                    while (rs.next()) {
                        value++;
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return value;
    }

    @Override
    public void deleteDrawsOfTournament(Tournament tournament) {
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "'");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    public void deleteDrawsOfGroupOfTournament(Tournament tournament, int group) {
        Log.fine("Deleting undraws of group " + group);
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "' AND UndrawGroup=" + group);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    public void deleteDrawsOfLevelOfTournament(Tournament tournament, int level) {
        Log.fine("Deleting undraws of level " + level);
        try {
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM undraw WHERE Championship='" + tournament.getName() + "' AND LevelUndraw=" + level);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    public List<String> getUndrawMySQLCommands() {
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
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName());
        }
        return commands;
    }

    protected abstract boolean showSQLError(int numberError);
}
