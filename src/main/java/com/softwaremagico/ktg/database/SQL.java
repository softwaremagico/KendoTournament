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
import com.softwaremagico.ktg.tournament.TournamentGroupPool;
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
        KendoLog.fine(SQL.class.getName(), "Exporting database");
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
            MessageManager.translatedMessage(this.getClass().getName(), "exportDatabase", "SQL", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            MessageManager.errorMessage(this.getClass().getName(), "exportDatabaseFail", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), e);
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
        Folder.appendTextToFile("LOCK TABLES `competitor` WRITE;\n", file);
        List<CompetitorWithPhoto> competitors = getAllCompetitorsWithPhoto();
        for (int i = 0; i < competitors.size(); i++) {
            //FileOutputStream fos;
            //byte[] photo = {0x0};
            if (competitors.get(i).photoInput != null) {
                try {
                    StoreInputStream(competitors.get(i).photoInput, (int) competitors.get(i).photoSize);
                } catch (Exception ex) {
                    KendoLog.severe(SQL.class.getName(), ex.getMessage());
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
            Folder.appendTextToFile("INSERT INTO `tournament` VALUES('" + tournaments.get(i).getName() + "','" + convertInputStream2String(tournaments.get(i).getBannerInput()) + "',"
                    + tournaments.get(i).getBannerSize() + "," + tournaments.get(i).getFightingAreas() + "," + tournaments.get(i).getHowManyTeamsOfGroupPassToTheTree() + ","
                    + tournaments.get(i).getTeamSize() + ",'" + tournaments.get(i).getMode() + "'," + (int) tournaments.get(i).getScoreForWin() + ","
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
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
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
        List<Fight> fights = getAllFights();
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
        List<Fight> fights = getAllFights();
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
     * Stores into the database a competitor. Frist check if exist.
     *
     * @param competitorWithPhoto Competitor.
     */
    @Override
    public boolean storeCompetitor(CompetitorWithPhoto competitorWithPhoto, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "storeCompetitor");
        KendoLog.fine(SQL.class.getName(), "Storing competitor " + competitorWithPhoto.getSurnameName() + " into database");
        boolean error = false;
        boolean update = false;
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM competitor WHERE ID='" + competitorWithPhoto.getId() + "'")) {

                if (rs.next()) {
                    return updateCompetitor(competitorWithPhoto, verbose);
                } else {
                    /*try {
                     if (competitorWithPhoto.photoInput.markSupported()) {
                     competitorWithPhoto.photoInput.reset();
                     }
                     } catch (IOException | NullPointerException ex) {
                     KendoTournamentGenerator.showErrorInformation(ex);
                     }*/
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
                            MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                        }
                    }
                }
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeImage", "SQL");
        } catch (SQLException ex) {
            error = true;
            if (competitorWithPhoto.photoSize > 1048576) {
                MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
            } else {
                MessageManager.errorMessage(this.getClass().getName(), "storeCompetitor", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            error = true;
        }

        if (!error) {
            if (verbose) {
                if (update) {
                    MessageManager.translatedMessage(this.getClass().getName(), "competitorUpdated", this.getClass().getName(), competitorWithPhoto.getName() + " " + competitorWithPhoto.getSurname(), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    MessageManager.translatedMessage(this.getClass().getName(), "competitorStored", this.getClass().getName(), competitorWithPhoto.getName() + " " + competitorWithPhoto.getSurname(), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        KendoLog.exiting(this.getClass().getName(), "storeCompetitor");
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
        KendoLog.entering(this.getClass().getName(), "insertCompetitor");
        KendoLog.fine(this.getClass().getName(), "Inserting competitor " + competitorWithPhoto.getSurnameName() + " into database");
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
                    MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                }
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeImage", "SQL");
        } catch (SQLException ex) {
            error = true;
            if (competitorWithPhoto.photoSize > 1048576) {
                MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
            } else {
                MessageManager.errorMessage(this.getClass().getName(), "storeCompetitor", "SQL");
            }
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            error = true;
        }
        KendoLog.exiting(this.getClass().getName(), "insertCompetitor");
        return !error;
    }

    @Override
    public boolean updateCompetitor(CompetitorWithPhoto competitor, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "updateCompetitor");
        KendoLog.fine(this.getClass().getName(), "Updating competitor " + competitor.getSurnameName() + "  from database");
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
                    MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                } else {
                    MessageManager.errorMessage(this.getClass().getName(), "storeCompetitor", "SQL");
                }
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
        } else {
            KendoLog.exiting(this.getClass().getName(), "updateIdCompetitor");
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "updateCompetitor");
        return !error;
    }

    @Override
    public boolean updateIdCompetitor(Competitor competitor, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "updateIdCompetitor");
        KendoLog.fine(this.getClass().getName(), "Updating ID of competitor " + competitor.getSurnameName());
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
                MessageManager.errorMessage(this.getClass().getName(), "storeCompetitor", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
        } else {
            KendoLog.exiting(this.getClass().getName(), "updateIdCompetitor");
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "updateIdCompetitor");
        return !error;
    }

    @Override
    public boolean updateClubCompetitor(Competitor competitor, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "updateClubCompetitor");
        KendoLog.fine(this.getClass().getName(), "Updating the club of competitor " + competitor.getSurnameName());
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
                MessageManager.errorMessage(this.getClass().getName(), "storeCompetitor", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }
        } else {
            KendoLog.exiting(this.getClass().getName(), "updateClubCompetitor");
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "updateClubCompetitor");
        return !error;
    }

    @Override
    public List<CompetitorWithPhoto> getCompetitorsWithPhoto(String query, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getCompetitorsWithPhoto");
        KendoLog.fine(this.getClass().getName(), "Obtaining a group of competitors with photo.");
        KendoLog.finer(this.getClass().getName(), query);
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
                        MessageManager.basicErrorMessage(this.getClass().getName(), "Error in: " + name + " " + surname, this.getClass().getName());
                    }
                }
            }

            if (results.isEmpty() && verbose) {
                MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
            }
            KendoLog.exiting(this.getClass().getName(), "getCompetitorsWithPhoto");
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getCompetitorsWithPhoto");
        return null;
    }

    @Override
    public List<Competitor> getCompetitors(String query, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getCompetitors");
        KendoLog.fine(this.getClass().getName(), "Getting competitors.");
        KendoLog.finer(this.getClass().getName(), query);
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
                        MessageManager.basicErrorMessage(this.getClass().getName(), "Error in: " + name + " " + surname, this.getClass().getName());
                    }
                }
            }

            if (results.isEmpty() && verbose) {
                MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
            }
            KendoLog.exiting(this.getClass().getName(), "getCompetitors");
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getCompetitors");
        return null;
    }

    @Override
    public List<Participant> getParticipants(String query, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getParticipants");
        KendoLog.finer(this.getClass().getName(), query);
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
                        MessageManager.basicErrorMessage(this.getClass().getName(), "Error in: " + name + " " + surname, this.getClass().getName());
                    }
                }
            }
            KendoLog.exiting(this.getClass().getName(), "getParticipants");
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getParticipants");
        return null;
    }

    @Override
    public List<CompetitorWithPhoto> getCompetitorsWithPhoto(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getCompetitorsWithPhoto");
        String query = "SELECT * FROM competitor ORDER BY Surname LIMIT " + fromRow + "," + numberOfRows;
        KendoLog.exiting(this.getClass().getName(), "getCompetitorsWithPhoto");
        return getCompetitorsWithPhoto(query, false);
    }

    @Override
    public List<CompetitorWithPhoto> getAllCompetitorsWithPhoto() {
        KendoLog.entering(this.getClass().getName(), "getAllCompetitorsWithPhoto");
        String query = "SELECT * FROM competitor ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "getAllCompetitorsWithPhoto");
        return getCompetitorsWithPhoto(query, false);
    }

    @Override
    public boolean storeAllCompetitors(List<CompetitorWithPhoto> competitors, boolean deleteOldOnes) {
        KendoLog.entering(this.getClass().getName(), "storeAllCompetitors");
        boolean error = false;
        try {
            if (deleteOldOnes) {
                try (Statement s = connection.createStatement()) {
                    KendoLog.finer(this.getClass().getName(), "Deleting previous competitors");
                    s.executeUpdate("DELETE FROM competitor");
                }
            }

            for (int i = 0; i < competitors.size(); i++) {
                if (storeCompetitor(competitors.get(i), false)) {
                    KendoLog.finer(this.getClass().getName(), "New competitors stored.");
                } else {
                    KendoLog.severe(this.getClass().getName(), "Failed to store the list of competitors");
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeAllCompetitors");
        return !error;
    }

    @Override
    public List<Competitor> getAllCompetitors() {
        KendoLog.entering(this.getClass().getName(), "getAllCompetitors");
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "getAllCompetitors");
        return getCompetitors(query, false);
    }

    @Override
    public List<Participant> getAllParticipants() {
        KendoLog.entering(this.getClass().getName(), "getAllParticipants");
        String query = "SELECT ID,Name,Surname FROM competitor ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "getAllParticipants");
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
        KendoLog.entering(this.getClass().getName(), "selectAllCompetitorsWithoutTeamInTournament");
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE NOT EXISTS (SELECT Member FROM team WHERE Tournament='" + tournament.getName() + "' AND competitor.id=team.Member) AND EXISTS (SELECT * FROM role WHERE Tournament='" + tournament.getName() + "' AND role.Competitor=competitor.id AND (role.Role='Competitor' OR role.Role='VolunteerK')) ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "selectAllCompetitorsWithoutTeamInTournament");
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
        KendoLog.entering(this.getClass().getName(), "selectAllParticipantsInTournamentWithoutAccreditation");
        String query = "SELECT * FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + tournament.getName() + "' AND competitor.ID=role.Competitor";
        if (!printAll) {
            query += " AND role.ImpressCard=0 ";
        }
        query += ") ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "selectAllParticipantsInTournamentWithoutAccreditation");
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
        KendoLog.entering(this.getClass().getName(), "selectAllCompetitorsInTournament");
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + tournament.getName() + "' AND competitor.ID=role.Competitor AND (role.Role='Competitor' OR role.Role='VolunteerK')) ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "selectAllCompetitorsInTournament");
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
        KendoLog.entering(this.getClass().getName(), "selectAllCompetitorWithDiplomaInTournament");
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
        KendoLog.exiting(this.getClass().getName(), "selectAllCompetitorWithDiplomaInTournament");
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
        KendoLog.entering(this.getClass().getName(), "selectAllVolunteersInTournament");
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor c1 WHERE EXISTS (SELECT Competitor FROM role r1 WHERE Tournament='" + tournament.getName() + "' AND c1.ID=r1.Competitor AND (r1.Role='VCLO' OR r1.Role='VolunteerK')) ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "selectAllVolunteersInTournament");
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
        KendoLog.entering(this.getClass().getName(), "selectCompetitor");
        KendoLog.fine(SQL.class.getName(), "Obtaining a competitor with ID " + id);
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
            KendoLog.exiting(this.getClass().getName(), "selectCompetitor");
            return c;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "selectCompetitor");
        return null;
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarName(String name, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsBySimilarName");
        String query = "SELECT * FROM competitor WHERE Name LIKE '%" + name + "%' ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsBySimilarName");
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarSurname(String surname, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsBySimilarSurname");
        String query = "SELECT * FROM competitor WHERE Surname LIKE '%" + surname + "%' ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsBySimilarSurname");
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarID(String id, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsBySimilarID");
        String query = "SELECT * FROM competitor WHERE ID LIKE '%" + id + "%' ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsBySimilarID");
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<Competitor> searchCompetitorsByClub(String clubName, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsByClub");
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE Club='" + clubName + "' ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsByClub");
        return getCompetitors(query, verbose);
    }

    @Override
    public List<Competitor> searchCompetitorsWithoutClub(boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsWithoutClub");
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE Club IS NULL ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsWithoutClub");
        return getCompetitors(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarClub(String clubName, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsBySimilarClub");
        String query = "SELECT * FROM competitor WHERE Club LIKE '%" + clubName + "%' ORDER BY Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsBySimilarClub");
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsByClubAndTournament(String clubName, Tournament tournament, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsByClubAndTournament");
        String query = "SELECT c1.* FROM competitor c1 INNER JOIN role r1 ON c1.ID=r1.Competitor WHERE c1.Club='" + clubName + "' AND r1.Tournament='" + tournament.getName() + "'  ORDER BY c1.Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsByClubAndTournament");
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public boolean deleteCompetitor(Competitor competitor, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteCompetitor");
        KendoLog.fine(SQL.class.getName(), "Deleting the competitor " + competitor.getSurnameName());
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
                        MessageManager.errorMessage(this.getClass().getName(), "deleteCompetitor", "SQL");
                    }
                    KendoLog.severe("deleteCompetitor", this.getClass().getName());
                }
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                }
                KendoLog.severe("noRunningDatabase", this.getClass().getName());
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "competitorDeleted", this.getClass().getName(), competitor.getName() + " " + competitor.getSurname(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(SQL.class.getName(), "Competitor deleted: " + competitor.getName() + " " + competitor.getSurname());
        }
        KendoLog.exiting(this.getClass().getName(), "deleteCompetitor");
        return !error && (answer || !verbose);
    }

    @Override
    public List<CompetitorRanking> getCompetitorsOrderByScore(boolean verbose, Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "getCompetitorsOrderByScore");
        KendoLog.fine(SQL.class.getName(), "Getting competitors ordered by score from " + tournament.getName());
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
                MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }
        KendoLog.exiting(this.getClass().getName(), "getCompetitorsOrderByScore");
        return competitorsOrdered;
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsByRoleAndTournament(String roleName, Tournament tournament, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchCompetitorsByRoleAndTournament");
        String query = "SELECT * FROM competitor, role WHERE competitor.ID=role.Competitor AND role.Role='" + roleName + "' AND role.Tournament='" + tournament.getName() + "'  ORDER BY competitor.Surname";
        KendoLog.exiting(this.getClass().getName(), "searchCompetitorsByRoleAndTournament");
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchRefereeByTournament(Tournament tournament, boolean getImage, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchRefereeByTournament");
        return searchCompetitorsByRoleAndTournament("Referee", tournament, getImage, verbose);
    }

    @Override
    public Integer searchVolunteerOrder(Competitor competitor, Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "searchVolunteerOrder");
        KendoLog.fine(SQL.class.getName(), "Obtain the numeration order of the volunteer " + competitor.getSurnameName());
        List<Competitor> allVolunteers = selectAllVolunteersInTournament(tournament);

        for (int i = 0; i < allVolunteers.size(); i++) {
            if (allVolunteers.get(i).getId().equals(competitor.getId())) {
                KendoLog.exiting(this.getClass().getName(), "searchVolunteerOrder");
                return i + 1; //Order starts in 1. 
            }
        }
        KendoLog.exiting(this.getClass().getName(), "searchVolunteerOrder");
        return null;
    }

    /**
     * SQLite does not support autoincrement, then I've implemented an
     * alternative.
     *
     * @return
     */
    protected int obtainCompetitorOrder() {
        KendoLog.entering(this.getClass().getName(), "searchVolunteerOrder");
        KendoLog.fine(SQL.class.getName(), "Obtain the numeration of competitors into the database");
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
        KendoLog.exiting(this.getClass().getName(), "searchVolunteerOrder");
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
        KendoLog.entering(this.getClass().getName(), "storeRole");
        KendoLog.fine(SQL.class.getName(), "Storing the role of participant " + participant.getSurnameName() + " in tournament " + tournament.getName() + " as " + roleTag.name);
        boolean inserted = true;
        try {
            try (Statement s = connection.createStatement()) {
                KendoLog.finer(SQL.class.getName(), "Deleting role of participant " + participant.getShortSurname() + " in tournament " + tournament.getName());
                s.executeUpdate("DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + participant.getId() + "'");
            }
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("INSERT INTO role (Role, Tournament, Competitor) VALUES ('" + roleTag.tag + "','" + tournament.getName() + "','" + participant.getId() + "')");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            inserted = false;
        } catch (NullPointerException npe) {
            inserted = false;
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }

        if (inserted && verbose) {
            MessageManager.translatedMessage(this.getClass().getName(), "roleChanged", this.getClass().getName(), participant.getName() + " " + participant.getSurname() + " -> " + roleTag.name, JOptionPane.INFORMATION_MESSAGE);
            KendoLog.info(SQL.class.getName(), "Role of " + participant.getSurnameName() + " changed to " + roleTag.name);
        }
        KendoLog.exiting(this.getClass().getName(), "storeRole");
        return inserted;
    }

    @Override
    public boolean storeRole(Role role, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "storeRole2");
        KendoLog.fine(SQL.class.getName(), "Storing role " + role.roleName);
        boolean inserted = true;
        try {
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("INSERT INTO role (Role, Tournament, Competitor,ImpressCard) VALUES ('" + role.roleName + "','" + role.tournament + "','" + role.competitorID() + "'," + role.impressCard + ")");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            inserted = false;
        } catch (NullPointerException npe) {
            inserted = false;
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }

        if (inserted && verbose) {
            MessageManager.translatedMessage(this.getClass().getName(), "roleChanged", this.getClass().getName(), role.competitorID() + " -> " + role.roleName, JOptionPane.INFORMATION_MESSAGE);
            KendoLog.finer(SQL.class.getName(), "Role " + role.roleName + " stored.");
        }

        KendoLog.exiting(this.getClass().getName(), "storeRole2");
        return inserted;
    }

    @Override
    public boolean deleteRole(Tournament tournament, Participant participant) {
        KendoLog.entering(this.getClass().getName(), "deleteRole");
        KendoLog.fine(SQL.class.getName(), "Deleting role of participant " + participant.getSurnameName() + " in tournament " + tournament.getName());
        boolean answer = false;
        try {
            Statement s = connection.createStatement();

            answer = MessageManager.questionMessage("roleDeleteQuestion", "Warning!");
            if (answer) {
                s.executeUpdate("DELETE FROM role WHERE Tournament='" + tournament.getName() + "' AND Competitor='" + participant.getId() + "'");
                MessageManager.translatedMessage(this.getClass().getName(), "roleDeleted", this.getClass().getName(), participant.getName() + " " + participant.getSurname(), JOptionPane.INFORMATION_MESSAGE);
                s.close();
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteRole");
        return answer;
    }

    @Override
    public String getTagRole(Tournament tournament, Participant participant) {
        KendoLog.entering(this.getClass().getName(), "getTagRole");
        KendoLog.fine(SQL.class.getName(), "Getting roleTag of participant " + participant.getSurnameName() + " in tournament " + tournament.getName());
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
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }

        KendoLog.finer(SQL.class.getName(), "RolTag obtained for participant " + participant.getSurnameName() + " in tournament " + tournament.getName() + " is " + role);
        KendoLog.exiting(this.getClass().getName(), "getTagRole");
        return role;
    }

    @Override
    public void setAllParticipantsInTournamentAsAccreditationPrinted(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "setAllParticipantsInTournamentAsAccreditationPrinted");
        KendoLog.fine(SQL.class.getName(), "Disabling printing all accreditations cards of " + tournament.getName());
        try {
            try (Statement st = connection.createStatement();
                    PreparedStatement stmt = connection.prepareStatement("UPDATE role SET ImpressCard=1 WHERE Tournament='" + tournament.getName() + "'")) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "setAllParticipantsInTournamentAsAccreditationPrinted");
    }

    @Override
    public void setParticipantInTournamentAsAccreditationPrinted(Competitor competitor, Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "setParticipantInTournamentAsAccreditationPrinted");
        KendoLog.fine(SQL.class.getName(), "Disabling printing the accreditation card of " + competitor.getSurnameName() + " in tournament " + tournament.getName());
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(competitor);
        setParticipantsInTournamentAsAccreditationPrinted(competitors, tournament);
        KendoLog.exiting(this.getClass().getName(), "setParticipantInTournamentAsAccreditationPrinted");
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
        KendoLog.entering(this.getClass().getName(), "setParticipantsInTournamentAsAccreditationPrinted");
        KendoLog.fine(SQL.class.getName(), "Disabling printing the accreditation card of a list of competitors in tournament " + tournament.getName());
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
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "setParticipantsInTournamentAsAccreditationPrinted");
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
        KendoLog.entering(this.getClass().getName(), "setAllParticipantsInTournamentAsDiplomaPrinted");
        KendoLog.fine(SQL.class.getName(), "Disabling printing all diplomas of " + tournament.getName() + " for " + roleTags);
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
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "setAllParticipantsInTournamentAsDiplomaPrinted");
    }

    @Override
    public List<Role> getAllRoles() {
        KendoLog.entering(this.getClass().getName(), "getAllRoles");
        List<Role> roles = getRoles(0, Integer.MAX_VALUE);
        KendoLog.exiting(this.getClass().getName(), "getAllRoles");
        return roles;
    }

    @Override
    public List<Role> getRoles(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getRoles");
        KendoLog.fine(SQL.class.getName(), "Getting all roles.");
        List<Role> roles = new ArrayList<>();
        try {
            int id = 0;
            Statement s = connection.createStatement();
            String query = "SELECT * FROM role ORDER BY Tournament,Role LIMIT " + fromRow + "," + numberOfRows;
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Role role = new Role(rs.getObject("Tournament").toString(), rs.getObject("Competitor").toString(), rs.getObject("Role").toString(), rs.getInt("ImpressCard"));
                roles.add(role);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
        }
        KendoLog.exiting(this.getClass().getName(), "getRoles");
        return roles;
    }

    @Override
    public boolean storeAllRoles(List<Role> roles, boolean deleteOldOnes) {
        KendoLog.entering(this.getClass().getName(), "storeAllRoles");
        KendoLog.fine(SQL.class.getName(), "Storing all roles into database.");
        boolean error = false;
        try {
            if (deleteOldOnes) {
                try (Statement s = connection.createStatement()) {
                    KendoLog.finer(SQL.class.getName(), "Deleting all roles");
                    s.executeUpdate("DELETE FROM role");
                }
            }

            for (int i = 0; i < roles.size(); i++) {
                if (!storeRole(roles.get(i), false)) {
                    KendoLog.severe(SQL.class.getName(), "Role " + roles.get(i).roleName);
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeAllRoles");
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
        KendoLog.entering(this.getClass().getName(), "storeClub");
        KendoLog.fine(SQL.class.getName(), "Storing club " + club.returnName() + " into database.");
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
                        KendoLog.finer(SQL.class.getName(), "Club exist, updating club.");
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
                MessageManager.errorMessage(this.getClass().getName(), "nameClub", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), micve);
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            inserted = false;
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            inserted = false;
            if (verbose) {
                MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            }
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }

        if (inserted && verbose) {
            if (!update) {
                MessageManager.translatedMessage(this.getClass().getName(), "clubStored", this.getClass().getName(), club.returnName(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                MessageManager.translatedMessage(this.getClass().getName(), "clubUpdated", this.getClass().getName(), club.returnName(), JOptionPane.INFORMATION_MESSAGE);
            }

        }
        KendoLog.exiting(this.getClass().getName(), "storeClub");
        return inserted;
    }

    @Override
    public List<String> returnClubsName() {
        KendoLog.entering(this.getClass().getName(), "returnClubsName");
        KendoLog.fine(SQL.class.getName(), "Obtaining the name of all clubs.");
        List<String> clubs = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("select * FROM club ORDER BY Name")) {
                while (rs.next()) {
                    clubs.add(rs.getString(1));
                }
            }
            KendoLog.exiting(this.getClass().getName(), "returnClubsName");
            return clubs;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "MySQL database connection fail", this.getClass().getName());
        }

        KendoLog.exiting(this.getClass().getName(), "returnClubsName");
        return null;
    }

    @Override
    public List<Club> getAllClubs() {
        KendoLog.entering(this.getClass().getName(), "getAllClubs");
        List<Club> clubs = getClubs(0, Integer.MAX_VALUE);
        KendoLog.exiting(this.getClass().getName(), "getAllClubs");
        return clubs;
    }

    @Override
    public List<Club> getClubs(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getClubs");
        KendoLog.fine(SQL.class.getName(), "Obtaining all clubs.");
        List<Club> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("select * FROM club ORDER BY Name LIMIT " + fromRow + "," + numberOfRows)) {
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
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "MySQL database connection fail", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getClubs");
        return results;
    }

    @Override
    public boolean storeAllClubs(List<Club> clubs, boolean deleteOldOnes) {
        KendoLog.entering(this.getClass().getName(), "storeAllClubs");
        KendoLog.fine(SQL.class.getName(), "Storing a list of clubs.");
        boolean error = false;
        try {
            if (deleteOldOnes) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM club");
                }
            }

            for (int i = 0; i < clubs.size(); i++) {
                if (!storeClub(clubs.get(i), false)) {
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeAllClubs");
        return !error;
    }

    @Override
    public List<Club> searchClub(String query, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchClub");
        KendoLog.finer(SQL.class.getName(), query);
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
                    MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
                }
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "searchClub");
        return results;
    }

    @Override
    public List<Club> searchClubByName(String name, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchClubByName");
        String query = "SELECT * FROM club WHERE Name LIKE '%" + name + "%' ORDER BY Name";
        List<Club> clubs = searchClub(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchClubByName");
        return clubs;
    }

    @Override
    public List<Club> searchClubByCity(String city, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchClubByCity");
        String query = "SELECT * FROM club WHERE City LIKE '%" + city + "%' ORDER BY Name";
        List<Club> clubs = searchClub(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchClubByCity");
        return clubs;
    }

    @Override
    public List<Club> searchClubByCountry(String country, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchClubByCountry");
        String query = "SELECT * FROM club WHERE Country LIKE '%" + country + "%' ORDER BY Name";
        List<Club> clubs = searchClub(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchClubByCountry");
        return clubs;
    }

    @Override
    public boolean deleteClub(Club club, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteClub");
        boolean error = false;
        boolean answer = false;
        KendoLog.fine(SQL.class.getName(), "Deleting club " + club.returnName());
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
                        MessageManager.errorMessage(this.getClass().getName(), "deleteClub", "SQL");
                    }
                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                }

            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
                }

            }
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "clubDeleted", this.getClass().getName(), club.returnName(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(SQL.class.getName(), "Club deleted: " + club.returnName());
        }

        KendoLog.exiting(this.getClass().getName(), "deleteClub");
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
        KendoLog.entering(this.getClass().getName(), "storeTournament");
        boolean error = false;
        boolean update = false;
        KendoLog.fine(SQL.class.getName(), "Storing tournament " + tournament.getName());
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM tournament WHERE Name='" + tournament.getName() + "'")) {

                if (rs.next()) {
                    return updateTournament(tournament, verbose);
                } else {
                    try {
                        if (tournament.getBannerInput().markSupported()) {
                            tournament.getBannerInput().reset();
                        }
                    } catch (IOException | NullPointerException ex) {
                        KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                    }
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
                    }
                }
            }
        } catch (MysqlDataTruncation mdt) {
            MessageManager.errorMessage(this.getClass().getName(), "storeImage", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), mdt);
            error = true;
        } catch (SQLException ex) {
            MessageManager.errorMessage(this.getClass().getName(), "storeTournament", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            error = true;
        } catch (NullPointerException npe) {
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            error = true;
        }

        if (!error) {
            if (!update) {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "tournamentStored", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(SQL.class.getName(), "Tournament stored: " + tournament.getName());
            } else {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "tournamentUpdated", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(SQL.class.getName(), "Tournament updated: " + tournament.getName());
            }
        }

        KendoLog.exiting(this.getClass().getName(), "storeTournament");
        return !error;
    }

    @Override
    public boolean deleteTournament(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "deleteTournament");
        boolean answer = false;
        KendoLog.fine(SQL.class.getName(), "Deleting tournament " + tournament.getName());
        try {
            answer = MessageManager.questionMessage("tournamentDeleteQuestion", "Warning!");
            if (answer) {
                try (Statement s = connection.createStatement()) {
                    deleteFightsOfTournament(tournament, false);
                    KendoLog.fine(SQL.class.getName(), "Deleting teams of tournament.");
                    s.executeUpdate("DELETE FROM team WHERE Tournament='" + tournament.getName() + "'");
                    KendoLog.fine(SQL.class.getName(), "Deleting roles of tournament.");
                    s.executeUpdate("DELETE FROM role WHERE Tournament='" + tournament.getName() + "'");
                    KendoLog.fine(SQL.class.getName(), "Deleting tournament.");
                    s.executeUpdate("DELETE FROM tournament WHERE Name='" + tournament.getName() + "'");
                    MessageManager.translatedMessage(this.getClass().getName(), "tournamentDeleted", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }

        KendoLog.exiting(this.getClass().getName(), "deleteTournament");
        return answer;
    }

    @Override
    public boolean updateTournament(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "updateTournament");
        boolean error = false;
        boolean answer = false;
        KendoLog.fine(SQL.class.getName(), "Updating tournament.");
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("questionUpdateTournament", "Warning!");
            }
            if (!verbose || answer) {
                try {
                    if (tournament.getBannerInput().markSupported()) {
                        tournament.getBannerInput().reset();
                    }
                } catch (IOException | NullPointerException npe) {
                }
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Banner=?, Size=?, FightingAreas=?, PassingTeams=?, TeamSize=?, Type=?, ScoreWin=?, ScoreDraw=?, ScoreType=?, Diploma=?, DiplomaSize=?, Accreditation=?, AccreditationSize=? WHERE Name='" + tournament.getName() + "'")) {
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
                }
            } else {
                KendoLog.exiting(this.getClass().getName(), "updateTournament");
                return false;
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeImage", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), mdt);
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeTournament", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            error = true;
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }

        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "tournamentUpdated", this.getClass().getName(), tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(this.getClass().getName(), "Tournament updated: " + tournament.getName());
        }
        KendoLog.exiting(this.getClass().getName(), "updateTournament");
        return !error;
    }

    @Override
    public List<Tournament> getAllTournaments() {
        KendoLog.entering(this.getClass().getName(), "getAllTournaments");
        List<Tournament> tournaments = getTournaments(0, Integer.MAX_VALUE);
        KendoLog.exiting(this.getClass().getName(), "getAllTournaments");
        return tournaments;
    }

    @Override
    public List<Tournament> getTournaments(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getTournaments");
        List<Tournament> results = new ArrayList<>();
        KendoLog.fine(SQL.class.getName(), "Getting all tournaments.");
        try {
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM tournament ORDER BY Name LIMIT " + fromRow + "," + numberOfRows)) {
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
    public boolean storeAllTournaments(List<Tournament> tournaments, boolean deleteOldOnes) {
        KendoLog.entering(this.getClass().getName(), "storeAllTournaments");
        boolean error = false;
        KendoLog.fine(SQL.class.getName(), "Storing a list of tournaments.");
        try {
            if (deleteOldOnes) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM tournament");
                }
            }

            for (int i = 0; i < tournaments.size(); i++) {
                if (!storeTournament(tournaments.get(i), false)) {
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeAllTournaments");
        return !error;
    }

    @Override
    public Tournament getTournamentByName(String tournamentName, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getTournamentByName");
        KendoLog.fine(SQL.class.getName(), "Get tournament " + tournamentName);
        try {
            Tournament t;
            try (Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM tournament WHERE Name='" + tournamentName + "' ")) {
                if (rs.next()) {
                    t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentType.getType(rs.getObject("Type").toString()));
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
                    KendoLog.exiting(this.getClass().getName(), "getTournamentByName");
                    return t;
                } else {
                    if (verbose) {
                        MessageManager.errorMessage(this.getClass().getName(), "errorTournament", "SQL", tournamentName);
                    }
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getTournamentByName");
        return null;
    }

    @Override
    public List<Tournament> searchTournament(String query, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTournament");
        KendoLog.fine(SQL.class.getName(), "Searching tournament.");
        KendoLog.finer(SQL.class.getName(), query);
        List<Tournament> results = new ArrayList<>();
        try {
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), TournamentType.getType(rs.getObject("Type").toString()));
                    t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"), rs.getInt("ScoreDraw"));
                    InputStream sImage = (InputStream) getBinaryStream(rs, "Banner");
                    Long size = rs.getLong("Size");
                    t.addBanner(sImage, size);
                    results.add(t);
                }
            }
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "searchTournament");
        return results;
    }

    @Override
    public List<Tournament> searchTournamentsByName(String tournamentName, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTournamentsByName");
        String query = "SELECT * FROM tournament WHERE Name LIKE '%" + tournamentName + "%' ORDER BY Name";
        List<Tournament> tournaments = searchTournament(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchTournamentsByName");
        return tournaments;
    }

    @Override
    public void deleteGroupsOfTournament(Tournament tournament, List<Team> teams) {
        KendoLog.entering(this.getClass().getName(), "deleteGroupsOfTournament");
        KendoLog.fine(SQL.class.getName(), "Deleting groups of tournament " + tournament.getName());
        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            t.group = 0;
            updateTeamGroupOfLeague(tournament, t);
        }
        KendoLog.exiting(this.getClass().getName(), "deleteGroupsOfTournament");
    }

    @Override
    public void storeDiplomaImage(Tournament tournament, InputStream Image, long imageSize) {
        KendoLog.entering(this.getClass().getName(), "storeDiplomaImage");
        KendoLog.fine(SQL.class.getName(), "Store diploma image of " + tournament.getName());
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
                    MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                }
            }
        } catch (MysqlDataTruncation mdt) {
            //error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeImage", "SQL");
        } catch (SQLException ex) {
            //error = true;
            if (imageSize > 1048576) {
                MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
            }
        } catch (NullPointerException npe) {
            //error = true;
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
        }
        KendoLog.exiting(this.getClass().getName(), "storeDiplomaImage");
    }

    @Override
    public void storeAccreditationImage(Tournament tournament, InputStream Image, long imageSize) {
        KendoLog.entering(this.getClass().getName(), "storeAccreditationImage");
        KendoLog.fine(SQL.class.getName(), "Store accreditation of " + tournament.getName());
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
                    MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
                }
            }
        } catch (MysqlDataTruncation mdt) {
            //error = true;
            MessageManager.errorMessage(this.getClass().getName(), "storeImage", "SQL");
        } catch (SQLException ex) {
            //error = true;
            if (imageSize > 1048576) {
                MessageManager.errorMessage(this.getClass().getName(), "imageTooLarge", "SQL");
            }
        } catch (NullPointerException npe) {
            //error = true;
            MessageManager.basicErrorMessage(this.getClass().getName(), "noRunningDatabase", this.getClass().getName());
        }
        KendoLog.exiting(this.getClass().getName(), "storeAccreditationImage");
    }

    @Override
    public int getLevelTournament(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "getLevelTournament");
        KendoLog.fine(SQL.class.getName(), "Getting max level of " + tournament.getName());
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
        KendoLog.exiting(this.getClass().getName(), "getLevelTournament");
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
        KendoLog.entering(this.getClass().getName(), "storeTeam");
        KendoLog.fine(SQL.class.getName(), "Storing team " + team.getName());
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
                        KendoLog.finer(SQL.class.getName(), "Deleting an existing team " + team.getName());
                        s.executeUpdate("DELETE FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "' AND LeagueGroup=" + team.group);
                    } else {
                        KendoLog.exiting(this.getClass().getName(), "storeTeam");
                        return false;
                    }
                }

                if (insertTeam(team, verbose)) {
                    TeamPool.getManager(team.tournament).addTeam(team);
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
                        MessageManager.errorMessage(this.getClass().getName(), "storeTeam", "SQL");
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
            if (update) {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "teamUpdated", this.getClass().getName(), team.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(this.getClass().getName(), "Team updated: " + team.getName());
            } else {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "teamStored", this.getClass().getName(), team.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(this.getClass().getName(), "Team Stored: " + team.getName());
            }
        }
        KendoLog.exiting(this.getClass().getName(), "storeTeam");
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
        KendoLog.entering(this.getClass().getName(), "insertTeam");
        KendoLog.fine(SQL.class.getName(), "Inserting team " + team.getName());
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
                                    MessageManager.errorMessage(this.getClass().getName(), "storeTeam", "SQL");
                                }
                            }
                        }
                        KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                    }
                }
            }
        }
        KendoLog.exiting(this.getClass().getName(), "insertTeam");
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
    private List<List<Competitor>> searchTeamMembers(Team team, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTeamMembers");
        KendoLog.fine(SQL.class.getName(), "Obtain the members of " + team.getName());
        List<List<Competitor>> membersPerLevel = new ArrayList<>();
        try {
            Statement s = connection.createStatement();
            String query = "SELECT MAX(LevelTournament) AS level FROM team WHERE Name='" + team.getName() + "' AND Tournament='" + team.tournament.getName() + "'";
            KendoLog.finest(SQL.class.getName(), query);
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
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "searchTeamMembers");
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
        KendoLog.entering(this.getClass().getName(), "searchTeam");
        KendoLog.finer(SQL.class.getName(), query);

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
                    MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "searchTeam");
        return results;
    }

    @Override
    public List<Team> searchTeamsByNameAndTournament(String name, Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTeamsByNameAndTournament");
        String query = "SELECT * FROM team WHERE Name LIKE '%" + name + "%' AND Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name";
        List<Team> teams = searchTeam(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchTeamsByNameAndTournament");
        return teams;
    }

    @Override
    public Team getTeamByName(String name, Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getTeamByName");
        String query = "SELECT * FROM team WHERE Name='" + name + "' AND Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name";
        List<Team> teams = searchTeam(query, verbose);
        if (!teams.isEmpty()) {
            KendoLog.exiting(this.getClass().getName(), "getTeamByName");
            return searchTeam(query, verbose).get(0);
        } else {
            if (verbose) {
                MessageManager.customMessage(this.getClass().getName(), "Error obtaining team " + name, "Error", 0);
                KendoLog.warning(SQL.class.getName(), "Error obtaining team " + name);
            }
            KendoLog.exiting(this.getClass().getName(), "getTeamByName");
            return null;
        }
    }

    @Override
    public List<Team> searchTeamsByTournament(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTeamsByTournament");
        String query = "SELECT * FROM team WHERE Tournament LIKE '" + tournament.getName() + "' GROUP BY Name ORDER BY Name ";
        List<Team> teams = searchTeam(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchTeamsByTournament");
        return teams;
    }

    @Override
    public List<Team> searchTeamsByTournamentExactName(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTeamsByTournamentExactName");
        String query = "SELECT * FROM team WHERE Tournament='" + tournament.getName() + "' GROUP BY Name ORDER BY Name";
        List<Team> teams = searchTeam(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchTeamsByTournamentExactName");
        return teams;
    }

    @Override
    public List<Team> searchTeamsByLevel(Tournament tournament, int level, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "searchTeamsByLevel");
        String query = "SELECT t1.name,t1.Tournament,t1.LeagueGroup FROM team t1 LEFT JOIN fight f1 ON (t1.Name=f1.team1 OR t1.Name=f1.team2)  WHERE t1.Tournament='" + tournament.getName() + "' AND f1.Tournament='" + tournament.getName() + "' AND f1.LeagueLevel>=" + level + " GROUP BY Name ORDER BY Name ";
        List<Team> teams = searchTeam(query, verbose);
        KendoLog.exiting(this.getClass().getName(), "searchTeamsByLevel");
        return teams;
    }
    
    public List<String> getTeamsNameByLevel(Tournament tournament, int level, boolean verbose){
        KendoLog.entering(this.getClass().getName(), "getTeamsNameByLevel");
         List<String> results = new ArrayList<>();

        try {
            String query = "SELECT t1.name FROM team t1 LEFT JOIN fight f1 ON (t1.Name=f1.team1 OR t1.Name=f1.team2)  WHERE t1.Tournament='" + tournament.getName() + "' AND f1.Tournament='" + tournament.getName() + "' AND f1.LeagueLevel>=" + level + " GROUP BY Name ORDER BY Name ";
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    results.add(rs.getObject("Name").toString());
                }
            }
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "getTeamsNameByLevel");
        return results;
    }

    @Override
    public List<Team> getTeams(int fromRow, int numberOfRows) {
        KendoLog.entering(this.getClass().getName(), "getTeams");
        String query = "SELECT * FROM team GROUP BY Name,Tournament ORDER BY Name LIMIT " + fromRow + "," + numberOfRows;
        List<Team> teams = searchTeam(query, false);
        KendoLog.exiting(this.getClass().getName(), "getTeams");
        return teams;
    }

    @Override
    public List<Team> getAllTeams() {
        KendoLog.entering(this.getClass().getName(), "getAllTeams");
        String query = "SELECT * FROM team GROUP BY Name,Tournament ORDER BY Name ";
        List<Team> teams = searchTeam(query, false);
        KendoLog.exiting(this.getClass().getName(), "getAllTeams");
        return teams;
    }

    @Override
    public boolean storeAllTeams(List<Team> teams, boolean deleteOldOnes) {
        KendoLog.entering(this.getClass().getName(), "storeAllTeams");
        KendoLog.fine(SQL.class.getName(), "Store a group of teams.");
        boolean error = false;
        try {
            if (deleteOldOnes) {
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM team");
                }
            }

            for (int i = 0; i < teams.size(); i++) {
                if (!storeTeam(teams.get(i), false)) {
                    error = true;
                }
            }
        } catch (SQLException ex) {
            error = true;
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeAllTeams");
        return !error;
    }

    @Override
    public void updateTeamGroupOfLeague(Tournament tournament, Team team) {
        KendoLog.entering(this.getClass().getName(), "updateTeamGroupOfLeague");
        KendoLog.fine(SQL.class.getName(), "Upgrading team " + team.getName() + " of " + tournament.getName());
        try {
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE team SET LeagueGroup=? WHERE Name='" + team.getName() + "' AND Tournament='" + tournament.getName() + "'")) {
                stmt.setInt(1, team.group);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noRunningDatabase", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        KendoLog.exiting(this.getClass().getName(), "updateTeamGroupOfLeague");
    }

    @Override
    public boolean deleteTeam(Team team, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteTeam");
        KendoLog.fine(SQL.class.getName(), "Deleting team " + team.getName());
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
                        MessageManager.errorMessage(this.getClass().getName(), "deleteTeam", "SQL");
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
        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "teamDeleted", this.getClass().getName(), team.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(this.getClass().getName(), "Team deleted: " + team.getName());
        }
        KendoLog.exiting(this.getClass().getName(), "deleteTeam");
        return !error && (answer || !verbose);
    }

    @Override
    public boolean deleteTeamByName(String teamName, String toournamentName, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteTeamByName");
        KendoLog.debug(SQL.class.getName(), "Deleting team " + teamName);
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
                        MessageManager.errorMessage(this.getClass().getName(), "deleteTeam", "SQL");
                    }
                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                }

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
        if (!error && answer) {
            if (sol > 0) {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "teamDeleted", this.getClass().getName(), teamName, JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(SQL.class.getName(), "Team deleted: " + teamName);
            } else {
                MessageManager.errorMessage(this.getClass().getName(), "teamNotDeleted", "SQL");
            }

        }
        KendoLog.exiting(this.getClass().getName(), "deleteTeamByName");
        return !error && (answer || !verbose);
    }

    @Override
    public void setIndividualTeams(Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "setIndividualTeams");
        KendoLog.fine(SQL.class.getName(), "Creating individual teams for tournament " + tournament.getName());
        List<Competitor> competitors = selectAllCompetitorsInTournament(tournament);
        MessageManager.translatedMessage(this.getClass().getName(), "oneTeamPerCompetitor", this.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
        for (int i = 0; i < competitors.size(); i++) {
            Team t = new Team(competitors.get(i).getSurname() + ", " + competitors.get(i).getName(), tournament);
            t.addOneMember(competitors.get(i), 0);
            storeTeam(t, false);
        }
        KendoLog.exiting(this.getClass().getName(), "setIndividualTeams");
    }

    @Override
    public boolean deleteTeamsOfTournament(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "deleteTeamsOfTournament");
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
                        MessageManager.errorMessage(this.getClass().getName(), "deleteTeam", "SQL");
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
        KendoLog.exiting(this.getClass().getName(), "deleteTeamsOfTournament");
        return !error && (answer || !verbose);
    }

    @Override
    public List<TeamRanking> getTeamsOrderByScore(Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getTeamsOrderByScore");
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
                MessageManager.errorMessage(this.getClass().getName(), "noResults", "SQL");
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }
        KendoLog.exiting(this.getClass().getName(), "getTeamsOrderByScore");
        return teamsOrdered;
    }

    @Override
    public Team getTeamOfCompetitor(String competitorID, Tournament tournament, boolean verbose) {
        KendoLog.entering(this.getClass().getName(), "getTeamOfCompetitor");
        String query = "SELECT * FROM team WHERE Member='" + competitorID + "' AND Tournament='" + tournament.getName() + "' GROUP BY Name";
        try {
            KendoLog.exiting(this.getClass().getName(), "getTeamOfCompetitor");
            return searchTeam(query, verbose).get(0);
        } catch (IndexOutOfBoundsException iob) {
            KendoLog.exiting(this.getClass().getName(), "getTeamOfCompetitor");
            return null;
        }
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
            //connection.commit();
            //s.execute("COMMIT");
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
                        MessageManager.errorMessage(this.getClass().getName(), "storeTeam", "SQL");
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
        KendoLog.entering(this.getClass().getName(), "storeFights");
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
                MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
            }

            if (!error && answer) {
                if (verbose) {
                    MessageManager.translatedMessage(this.getClass().getName(), "fightStored", this.getClass().getName(), fights.get(0).tournament.getName(), JOptionPane.INFORMATION_MESSAGE);
                }
                KendoLog.info(this.getClass().getName(), "Fight stored: " + fights.get(0).tournament.getName());
            }
        } else {
            KendoLog.exiting(this.getClass().getName(), "storeFights");
            return false;
        }
        KendoLog.exiting(this.getClass().getName(), "storeFights");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
                    Fight f = new Fight(TeamPool.getManager(tournament).getTeam(rs.getObject("Team1").toString()),
                            TeamPool.getManager(tournament).getTeam(rs.getObject("Team2").toString()),
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
        KendoLog.entering(this.getClass().getName(), "storeDuel");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeDuel", "SQL");
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
        KendoLog.exiting(this.getClass().getName(), "storeDuel");
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
            MessageManager.errorMessage(this.getClass().getName(), "storeDuel", "SQL");
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
                            TeamPool.getManager(TournamentPool.getTournament(rs.getObject("Championship").toString())).getTeam(rs.getObject("Team").toString()),
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
            MessageManager.errorMessage(this.getClass().getName(), "storeFights", "SQL");
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
                        teamWinners.add(TeamPool.getManager(tournament).getTeam(rs.getObject("Team").toString()));
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
