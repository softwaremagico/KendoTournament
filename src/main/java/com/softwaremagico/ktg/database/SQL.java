/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 21-feb-2012.
 */
package com.softwaremagico.ktg.database;

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
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("delete from competitor;");
            s.executeUpdate("delete from tournament;");
            s.executeUpdate("delete from club;");
            s.executeUpdate("delete from fight;");
            s.executeUpdate("delete from role;");
            s.executeUpdate("delete from team;");
            s.executeUpdate("delete from undraw;");
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
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
            MessageManager.customMessage("exportDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (Exception e) {
            MessageManager.errorMessage("exportDatabaseFail", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(e);
        }
    }

    private void exportClubs(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `club` WRITE;\n", file);
        List<Club> clubs = getAllClubs();
        for (int i = 0; i < clubs.size(); i++) {
            f.AppendTextToFile("INSERT INTO `club` VALUES('" + clubs.get(i).returnName() + "','" + clubs.get(i).returnCountry() + "','"
                    + clubs.get(i).representativeID + "','" + clubs.get(i).email + "','"
                    + clubs.get(i).phone + "','" + clubs.get(i).returnCity() + "',"
                    + "NULL" + ",'" + clubs.get(i).returnAddress() + "');\n", file);
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    private void exportCompetitors(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `competitor` WRITE;\n", file);
        List<CompetitorWithPhoto> competitors = getAllCompetitorsWithPhoto();
        for (int i = 0; i < competitors.size(); i++) {
            //FileOutputStream fos;
            byte[] photo = {0x0};
            if (competitors.get(i).photoInput != null) {
                try {
                    StoreInputStream(competitors.get(i).photoInput, (int) competitors.get(i).photoSize);
                } catch (Exception ex) {
                    Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Select Photo from competitor where competitor.ListOrder=1 into dumpfile '/tmp/image.jpg';
            f.AppendTextToFile("INSERT INTO `competitor` VALUES('" + competitors.get(i).getId() + "','" + competitors.get(i).returnName() + "','"
                    + competitors.get(i).returnSurname() + "','" + competitors.get(i).club + "','" + convertInputStream2String(competitors.get(i).photoInput) + "',"
                    + competitors.get(i).photoSize + "," + i + ");\n", file);
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    /**
     * Funciona!
     *
     * @param inputStream
     * @param length
     */
    private void StoreInputStream(InputStream inputStream, int length) {
        try {
            InputStream in = new BufferedInputStream(inputStream);
            byte[] data = new byte[length];
            int bytesRead = 0;
            int offset = 0;
            while (offset < length) {
                try {
                    bytesRead = in.read(data, offset, data.length - offset);
                    if (bytesRead == -1) {
                        break;
                    }
                    offset += bytesRead;
                } catch (IOException ex) {
                    Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            in.close();

            if (offset != length) {
                throw new IOException("Only read " + offset + " bytes; Expected " + length + " bytes");
            }

            FileOutputStream out = new FileOutputStream("/tmp/test.jpg");
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
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
        String line = null;
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
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `tournament` WRITE;\n", file);
        List<Tournament> tournaments = getAllTournaments();
        for (int i = 0; i < tournaments.size(); i++) {
            f.AppendTextToFile("INSERT INTO `tournament` VALUES('" + tournaments.get(i).name + "','" + convertInputStream2String(tournaments.get(i).bannerInput) + "',"
                    + tournaments.get(i).bannerSize + "," + tournaments.get(i).fightingAreas + "," + tournaments.get(i).howManyTeamsOfGroupPassToTheTree + ","
                    + tournaments.get(i).teamSize + ",'" + tournaments.get(i).mode + "'," + (int) tournaments.get(i).getScoreForWin() + ","
                    + tournaments.get(i).getScoreForDraw() + ",'" + tournaments.get(i).getChoosedScore() + "',NULL,NULL"
                    + ");\n", file);
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    private void exportRole(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `role` WRITE;\n", file);
        List<String> commands = getRoleMySQLCommands();
        for (int i = 0; i < commands.size(); i++) {
            f.AppendTextToFile(commands.get(i), file);
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    private void exportTeams(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `team` WRITE;\n", file);
        List<Team> teams = getAllTeams();
        for (int i = 0; i < teams.size(); i++) {
            for (int levelIndex = 0; levelIndex < teams.get(i).levelChangesSize(); levelIndex++) {
                for (int member = 0; member < teams.get(i).getNumberOfMembers(levelIndex); member++) {
                    String memberID = "";
                    if (teams.get(i).getMember(member, levelIndex) != null) {
                        memberID = teams.get(i).getMember(member, levelIndex).getId();
                    }
                    f.AppendTextToFile("INSERT INTO `team` VALUES('" + teams.get(i).returnName() + "','"
                            + memberID + "'," + member + "," + levelIndex + ",'"
                            + teams.get(i).competition.name + "'," + teams.get(i).group
                            + ");\n", file);
                }
            }
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    private void exportFights(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `fight` WRITE;\n", file);
        List<Fight> fights = getAllFights();
        for (int i = 0; i < fights.size(); i++) {
            f.AppendTextToFile("INSERT INTO `fight` VALUES('" + fights.get(i).team1.returnName() + "','"
                    + fights.get(i).team2.returnName() + "','" + fights.get(i).competition.name + "',"
                    + fights.get(i).asignedFightArea + "," + i + ","
                    + fights.get(i).returnWinner() + "," + fights.get(i).level + ","
                    + fights.get(i).getMaxWinners()
                    + ");\n", file);
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    private void exportDuels(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `duel` WRITE;\n", file);
        List<Fight> fights = getAllFights();
        int id = 0;
        for (int i = 0; i < fights.size(); i++) {
            for (int j = 0; j < fights.get(i).duels.size(); j++) {
                f.AppendTextToFile("INSERT INTO `duel` VALUES(" + id + "," + i + ","
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
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    private void exportUndraws(String file) {
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("LOCK TABLES `undraw` WRITE;\n", file);
        List<String> commands = getUndrawMySQLCommands();
        for (int i = 0; i < commands.size(); i++) {
            f.AppendTextToFile(commands.get(i), file);
        }
        f.AppendTextToFile("UNLOCK TABLES;\n", file);
        f.AppendTextToFile("--------------------\n", file);
    }

    public void importDatabase(String fileName) {
        clearDatabase();
        executeScript(fileName);
    }

    protected void executeScript(String fileName) {
        String query = "";
        try {
            MyFile file = new MyFile(fileName);
            List<String> lines = file.InLines(false);
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).startsWith("--")) {
                    if (!lines.get(i).endsWith(";")) {
                        query += lines.get(i).trim();
                    } else {
                        if (lines.get(i).trim().length() > 0) {
                            query += lines.get(i).trim();
                            PreparedStatement s = connection.prepareStatement(query);
                            try {
                                s.executeUpdate();
                            } catch (SQLException sql) {
                                showSQLError(1049);
                                KendoTournamentGenerator.getInstance().showErrorInformation(sql);
                                break;
                            }
                            s.close();
                            query = "";
                        }
                    }
                }
            }
        } catch (IOException ex) {
            showSQLError(1049);
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size) throws SQLException;

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
     * @param c Competitor.
     */
    @Override
    public boolean storeCompetitor(CompetitorWithPhoto c, boolean verbose) {
        boolean error = false;
        boolean update = false;
        try {
            //If exists the competitor is an update not a insert.
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM competitor WHERE ID='" + c.getId() + "'");

            if (rs.next()) {
                return updateCompetitor(c, verbose);
            } else {
                try {
                    if (c.photoInput.markSupported()) {
                        c.photoInput.reset();
                    }
                } catch (IOException ex) {
                } catch (NullPointerException npe) {
                }
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize, ListOrder) VALUES (?,?,?,?,?,?,?)");
                stmt.setString(1, c.getId());
                stmt.setString(2, c.returnName());
                stmt.setString(3, c.returnSurname());
                stmt.setString(4, c.club);
                storeBinaryStream(stmt, 5, c.photoInput, (int) c.photoSize);
                stmt.setLong(6, c.photoSize);
                stmt.setLong(7, obtainCompetitorOrder());
                try {
                    stmt.executeUpdate();
                } catch (OutOfMemoryError ofm) {
                    MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }
                stmt.close();
            }
            rs.close();
            s.close();
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            error = true;
            if (c.photoSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            error = true;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }

        if (!error) {
            if (verbose) {
                if (update) {
                    MessageManager.customMessage("competitorUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, c.returnName() + " " + c.returnSurname(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    MessageManager.customMessage("competitorStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, c.returnName() + " " + c.returnSurname(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        }
        return !error;
    }

    /**
     * Stores into the database a competitor, without any check or question.
     * Quick version for import com.softwaremagico.ktg.database option.
     *
     * @param c Competitor.
     */
    @Override
    public boolean insertCompetitor(CompetitorWithPhoto c) {
        boolean error = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO competitor (ID, Name, Surname, Club, Photo, PhotoSize, ListOrder) VALUES (?,?,?,?,?,?,?)");
            stmt.setString(1, c.getId());
            stmt.setString(2, c.returnName());
            stmt.setString(3, c.returnSurname());
            stmt.setString(4, c.club);
            storeBinaryStream(stmt, 5, c.photoInput, (int) c.photoSize);
            stmt.setLong(6, c.photoSize);
            stmt.setInt(7, obtainCompetitorOrder());
            try {
                stmt.executeUpdate();
            } catch (OutOfMemoryError ofm) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
            stmt.close();
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            error = true;
            if (c.photoSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            error = true;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }
        return !error;
    }

    @Override
    public boolean updateCompetitor(CompetitorWithPhoto c, boolean verbose) {
        boolean answer = true;
        boolean error = false;
        if (verbose) {
            answer = MessageManager.question("questionUpdateCompetitor", "Warning!", KendoTournamentGenerator.getInstance().language);
        }
        if (answer || !verbose) {
            try {
                try {
                    if (c.photoInput.markSupported()) {
                        c.photoInput.reset();
                    }
                } catch (IOException ex) {
                }
                PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET Name=?, Surname=?, Club=?, Photo=?, PhotoSize=? WHERE ID='" + c.getId() + "'");
                stmt.setString(1, c.returnName());
                stmt.setString(2, c.returnSurname());
                stmt.setString(3, c.club);
                storeBinaryStream(stmt, 4, c.photoInput, (int) c.photoSize);
                //stmt.setBlob(4, c.photo);
                stmt.setLong(5, c.photoSize);
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException ex) {
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                error = true;
                if (c.photoSize > 1048576) {
                    MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    MessageManager.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        } else {
            return false;
        }
        return !error;
    }

    @Override
    public boolean updateIdCompetitor(Competitor c, boolean verbose) {
        boolean answer = true;
        boolean error = false;
        if (verbose) {
            answer = MessageManager.question("questionUpdateCompetitor", "Warning!", KendoTournamentGenerator.getInstance().language);
        }
        if (answer || !verbose) {
            try {
                PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET ID=? WHERE Name='" + c.returnName() + "' AND Surname='" + c.returnSurname() + "' AND Club='" + c.club + "'");
                stmt.setString(1, c.getId());
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException ex) {
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                error = true;
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } else {
            return false;
        }
        return !error;
    }

    @Override
    public boolean updateClubCompetitor(Competitor c, boolean verbose) {
        boolean answer = true;
        boolean error = false;
        if (verbose) {
            answer = MessageManager.question("questionUpdateCompetitor", "Warning!", KendoTournamentGenerator.getInstance().language);
        }
        if (answer || !verbose) {
            try {
                PreparedStatement stmt = connection.prepareStatement("UPDATE competitor SET Name=?, Surname=?, Club=?, Photo=?, PhotoSize=? WHERE ID='" + c.getId() + "'");
                stmt.setString(1, c.returnName());
                stmt.setString(2, c.returnSurname());
                stmt.setString(3, c.club);
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException ex) {
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                error = true;
                MessageManager.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } else {
            return false;
        }
        return !error;
    }

    @Override
    public List<CompetitorWithPhoto> getCompetitorsWithPhoto(String query, boolean verbose) {
        List<CompetitorWithPhoto> results = new ArrayList<CompetitorWithPhoto>();
        String name = "", surname = "";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                name = rs.getObject("Name").toString();
                surname = rs.getObject("Surname").toString();
                try {
                    CompetitorWithPhoto c = new CompetitorWithPhoto(rs.getObject("ID").toString(), name, surname, rs.getObject("Club").toString());
                    c.addOrder(rs.getInt("ListOrder"));
                    InputStream sImage = rs.getBinaryStream("Photo");
                    Long size = rs.getLong("PhotoSize");
                    c.addImage(sImage, size);
                    results.add(c);
                } catch (NullPointerException npe) {
                    MessageManager.errorMessage("Error in: " + name + " " + surname, this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
            rs.close();
            st.close();

            if (results.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }

            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return null;
    }

    @Override
    public List<Competitor> getCompetitors(String query, boolean verbose) {
        List<Competitor> results = new ArrayList<Competitor>();
        String name = "", surname = "";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                name = rs.getObject("Name").toString();
                surname = rs.getObject("Surname").toString();
                try {
                    Competitor c = new Competitor(rs.getObject("ID").toString(), name, surname, rs.getObject("Club").toString());
                    c.addOrder(rs.getInt("ListOrder"));
                    results.add(c);
                } catch (NullPointerException npe) {
                    MessageManager.errorMessage("Error in: " + name + " " + surname, this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
            rs.close();
            st.close();

            if (results.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }

            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return null;
    }

    @Override
    public List<Participant> getParticipants(String query, boolean verbose) {
        List<Participant> results = new ArrayList<Participant>();
        String name, surname;
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID,Name,Surname FROM competitor ORDER BY Surname");
            while (rs.next()) {
                name = rs.getObject("Name").toString();
                surname = rs.getObject("Surname").toString();
                try {
                    Participant p = new Participant(rs.getObject("ID").toString(), name, surname);
                    results.add(p);
                } catch (NullPointerException npe) {
                    MessageManager.errorMessage("Error in: " + name + " " + surname, this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
            rs.close();
            st.close();
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
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
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM competitor");
            s.close();

            for (int i = 0; i < competitors.size(); i++) {
                if (!storeCompetitor(competitors.get(i), false)) {
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
     * @param championship
     * @return
     */
    @Override
    public List<Competitor> selectAllCompetitorsWithoutTeamInTournament(String championship) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE NOT EXISTS (SELECT Member FROM team WHERE Tournament='" + championship + "' AND competitor.id=team.Member) AND EXISTS (SELECT * FROM role WHERE Tournament='" + championship + "' AND role.Competitor=competitor.id AND (role.Role='Competitor' OR role.Role='VolunteerK')) ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Select all competitors, organizer and refereer of the tournament that
     * still have not the accreditation card.
     *
     * @param championship
     * @return
     */
    @Override
    public List<CompetitorWithPhoto> selectAllParticipantsInTournamentWithoutAccreditation(String championship, boolean printAll) {
        String query = "SELECT * FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + championship + "' AND competitor.ID=role.Competitor";
        if (!printAll) {
            query += " AND role.ImpressCard=0 ";
        }
        query += ") ORDER BY Surname";
        return getCompetitorsWithPhoto(query, false);
    }

    /**
     * Select all competitors, organizer and refereer of the tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Competitor> selectAllCompetitorsInTournament(String championship) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + championship + "' AND competitor.ID=role.Competitor AND (role.Role='Competitor' OR role.Role='VolunteerK')) ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Select participants of all selected roles. If no role is selected, select
     * all participants.
     *
     * @param roles roles that have diploma.
     * @param championship
     * @param printAll if false select competitors without printed diploma.
     * before.
     * @return
     */
    @Override
    public List<Competitor> selectAllCompetitorWithDiplomaInTournament(RoleTags roles, String championship, boolean printAll) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE EXISTS (SELECT Competitor FROM role WHERE Tournament='" + championship + "' AND competitor.ID=role.Competitor ";

        if (!printAll) {
            query += " AND role.Diploma=0 ";
        }

        //Select the roles
        if (roles != null && roles.size() > 0) {
            query += " AND (";
            for (int i = 0; i < roles.size(); i++) {
                query += " role.Role='" + roles.get(i).tag + "' ";
                if (i < roles.size() - 1) {
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
     * @param championship
     * @return
     */
    @Override
    public List<Competitor> selectAllVolunteersInTournament(String championship) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor c1 WHERE EXISTS (SELECT Competitor FROM role r1 WHERE Tournament='" + championship + "' AND c1.ID=r1.Competitor AND (r1.Role='VCLO' OR r1.Role='VolunteerK')) ORDER BY Surname";
        return getCompetitors(query, false);
    }

    /**
     * Obtain from the database a competitor.
     *
     * @param id The Identificaction Number of the Competitor.
     * @return Competitor.
     */
    @Override
    public CompetitorWithPhoto selectCompetitor(String id, boolean verbose) {
        CompetitorWithPhoto c = null;
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM competitor WHERE ID='" + id + "'");
            if (rs.next()) {
                c = new CompetitorWithPhoto(rs.getObject("ID").toString(), rs.getObject("Name").toString(), rs.getObject("Surname").toString(), rs.getObject("Club").toString());
                c.addOrder(rs.getInt("ListOrder"));
                InputStream sImage = rs.getBinaryStream("Photo");
                Long size = rs.getLong("PhotoSize");
                c.addImage(sImage, size);
            }
            rs.close();
            st.close();
            return c;
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
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
    public List<Competitor> searchCompetitorsByClub(String club, boolean verbose) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE Club='" + club + "' ORDER BY Surname";
        return getCompetitors(query, verbose);
    }

    @Override
    public List<Competitor> searchCompetitorsWithoutClub(boolean verbose) {
        String query = "SELECT ID, Name, Surname, Club, ListOrder FROM competitor WHERE Club IS NULL ORDER BY Surname";
        return getCompetitors(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsBySimilarClub(String club, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor WHERE Club LIKE '%" + club + "%' ORDER BY Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsByClubAndTournament(String club, String championship, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor c1 INNER JOIN role r1 ON c1.ID=r1.Competitor WHERE c1.Club='" + club + "' AND r1.Tournament='" + championship + "'  ORDER BY c1.Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public boolean deleteCompetitor(Competitor c, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("questionDeleteCompetitor", "Warning!", KendoTournamentGenerator.getInstance().language);
            }

            if (answer || !verbose) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM competitor WHERE ID='" + c.getId() + "'");
                s.close();
            }

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    } else {
                        if (KendoTournamentGenerator.getInstance().getLogOption()) {
                            Log.storeLog("deleteCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
                    }
                }

            }
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.customMessage("competitorDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, c.returnName() + " " + c.returnSurname(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("competitorDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, c.returnName() + " " + c.returnSurname());
                }
            }
        }

        return !error && (answer || !verbose);
    }

    @Override
    public List<CompetitorRanking> getCompetitorsOrderByScore(boolean verbose, String championship) {
        List<CompetitorRanking> competitorsOrdered = new ArrayList<CompetitorRanking>();
        String query = "SELECT " + "t3.Name as Name, " + "t3.Surname as Surname, " + "t3.ID as ID, " + "t1.NumVictorias, " + "TotalPtos " + "FROM( " + "SELECT " + "count(Distinct t1.IdDuelo) as NumVictorias, " + "CASE " + "WHEN TotalJugador1 > TotalJugador2 THEN t1.IdCompetidor1  " + "ELSE t2.IdCompetidor2 END " + "as  IDGanador " + "FROM " + "(SELECT " + "t1.ID as IdCompetidor1, " + "t1.NAME as Competidor1, " + "t4.ID as IdDuelo, " + "CASE " + "WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalJugador1 " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team1 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer " + ")t1 " + "INNER JOIN " + "(SELECT " + "t1.ID as IdCompetidor2, " + "t1.NAME as Competidor2, " + "t4.ID as IdDuelo, " + "CASE " + "WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalJugador2 " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team2 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer " + ")t2 " + "ON t1.IdDuelo = t2.IdDuelo " + "WHERE " + "TotalJugador1 <> TotalJugador2   " + "GROUP BY " + "CASE " + "WHEN TotalJugador1 > TotalJugador2 THEN t1.IdCompetidor1  " + "ELSE t2.IdCompetidor2 END " + ") t1 " + "RIGHT OUTER JOIN " + "(SELECT " + "t1.IdCompetidor, " + "sum(TotalPtos) as TotalPtos " + "FROM " + "(SELECT " + "t1.ID as IdCompetidor, " + "CASE " + "WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalPtos " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team1 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer " + "UNION ALL " + "SELECT " + "t1.ID as IdCompetidor, " + "CASE " + "WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "+ " + "CASE " + "WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1 " + "ELSE 0 END " + "as TotalPtos " + "FROM " + "competitor t1 " + "INNER JOIN " + "team t2 " + "ON t1.ID = t2.Member " + "INNER JOIN " + "fight t3 " + "ON t2.Name = t3.Team2 " + " AND t2.Tournament = t3.Tournament " + " AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "INNER JOIN " + "duel t4 " + "ON t3.ID = t4.Fight " + "WHERE " + "t2.Position = t4.OrderPlayer) t1 " + "GROUP BY " + "t1.IdCompetidor " + ") " + "t2 " + "ON t2.IdCompetidor = t1.IDGanador " + "INNER JOIN " + "competitor t3 " + "ON t2.IdCompetidor = t3.ID " + "ORDER BY " + "NumVictorias DESC,TotalPtos DESC, t3.surname asc;";

        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                competitorsOrdered.add(new CompetitorRanking(rs.getObject("Name").toString(), rs.getObject("Surname").toString(), rs.getObject("ID").toString(), rs.getInt("NumVictorias"), rs.getInt("TotalPtos")));
            }

            rs.close();
            s.close();
            if (competitorsOrdered.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }

        return competitorsOrdered;
    }

    @Override
    public List<CompetitorWithPhoto> searchCompetitorsByRoleAndTournament(String role, String championship, boolean getImage, boolean verbose) {
        String query = "SELECT * FROM competitor, role WHERE competitor.ID=role.Competitor AND role.Role='" + role + "' AND role.Tournament='" + championship + "'  ORDER BY competitor.Surname";
        return getCompetitorsWithPhoto(query, verbose);
    }

    @Override
    public List<CompetitorWithPhoto> searchRefereeByTournament(String championship, boolean getImage, boolean verbose) {
        return searchCompetitorsByRoleAndTournament("Referee", championship, getImage, verbose);
    }

    @Override
    public Integer searchVolunteerOrder(Competitor c, String championship) {

        List<Competitor> allVolunteers = selectAllVolunteersInTournament(championship);

        for (int i = 0; i < allVolunteers.size(); i++) {
            if (allVolunteers.get(i).getId().equals(c.getId())) {
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
     * @param role
     * @param t
     * @param c
     * @param verbose
     * @return
     */
    @Override
    public boolean storeRole(RoleTag role, Tournament t, Participant p, boolean verbose) {
        boolean inserted = true;
        try {
            //If exists the role is a update not a insert.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM role WHERE Tournament='" + t.name + "' AND Competitor='" + p.getId() + "'");
            s.close();

            Statement st = connection.createStatement();
            st.executeUpdate("INSERT INTO role (Role, Tournament, Competitor) VALUES ('" + role.tag + "','" + t.name + "','" + p.getId() + "')");
            st.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            inserted = false;
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            inserted = false;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        if (inserted && verbose) {
            MessageManager.customMessage("roleChanged", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, p.returnName() + " " + p.returnSurname() + " -> " + role.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return inserted;
    }

    @Override
    public boolean storeRole(Role role, boolean verbose) {
        boolean inserted = true;
        try {
            Statement st = connection.createStatement();
            st.executeUpdate("INSERT INTO role (Role, Tournament, Competitor,ImpressCard) VALUES ('" + role.Role + "','" + role.tournament + "','" + role.competitorID() + "'," + role.impressCard + ")");
            st.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            inserted = false;
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            inserted = false;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        if (inserted && verbose) {
            MessageManager.customMessage("roleChanged", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, role.competitorID() + " -> " + role.Role, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return inserted;
    }

    @Override
    public boolean deleteRole(Tournament t, Participant p) {
        boolean answer = false;
        try {
            Statement s = connection.createStatement();

            answer = MessageManager.question("roleDeleteQuestion", "Warning!", KendoTournamentGenerator.getInstance().language);
            if (answer) {
                s.executeUpdate("DELETE FROM role WHERE Tournament='" + t.name + "' AND Competitor='" + p.getId() + "'");
                MessageManager.customMessage("roleDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, p.returnName() + " " + p.returnSurname(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                s.close();
            }

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return answer;
    }

    @Override
    public String getTagRole(Tournament t, Participant p) {
        String role = null;
        try {
            //If exists the club is a update not a insert.
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM role WHERE Tournament='" + t.name + "' AND Competitor='" + p.getId() + "'");
            rs.next();
            role = rs.getObject("Role").toString();
            rs.close();
            s.close();


        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                showSQLError(ex.getErrorCode());
            }

        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return role;
    }

    @Override
    public void setAllParticipantsInTournamentAsAccreditationPrinted(String championship) {
        try {
            Statement st = connection.createStatement();
            PreparedStatement stmt = connection.prepareStatement("UPDATE role SET ImpressCard=1 WHERE Tournament='" + championship + "'");
            stmt.executeUpdate();
            stmt.close();
            st.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        }
    }

    @Override
    public void setParticipantInTournamentAsAccreditationPrinted(Competitor competitor, String championship) {
        List<Competitor> competitors = new ArrayList<Competitor>();
        competitors.add(competitor);
        setParticipantsInTournamentAsAccreditationPrinted(competitors, championship);
    }

    /**
     * Set all selected participants as accredition card already printed. If no
     * competitors are selected, set all participants of the championship.
     *
     * @param competitors
     * @param championship
     */
    @Override
    public void setParticipantsInTournamentAsAccreditationPrinted(List<Competitor> competitors, String championship) {
        try {
            Statement st = connection.createStatement();
            //Basic query
            String query = "UPDATE role SET ImpressCard=1 WHERE Tournament='" + championship + "'";

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

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.executeUpdate();
            stmt.close();
            st.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        }
    }

    /**
     * Set all selected roles as diploma already printed. If no roles are
     * selected, set all participants of the championship.
     *
     * @param roles
     * @param championship
     */
    @Override
    public void setAllParticipantsInTournamentAsDiplomaPrinted(RoleTags roles, String championship) {
        try {
            Statement st = connection.createStatement();
            //Basic query
            String query = "UPDATE role SET Diploma=1 WHERE Tournament='" + championship + "'";

            //Select the roles
            if (roles != null && roles.size() > 0) {
                query += " AND (";
                for (int i = 0; i < roles.size(); i++) {
                    query += " Role='" + roles.get(i).tag + "' ";
                    if (i < roles.size() - 1) {
                        query += " OR ";
                    }
                }
                query += ")";
            }

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.executeUpdate();
            stmt.close();
            st.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        }
    }

    public List<String> getRoleMySQLCommands() {
        List<String> commands = new ArrayList<String>();
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
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return commands;
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<Role>();
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
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return roles;
    }

    @Override
    public boolean storeAllRoles(List<Role> roles) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM role");
            s.close();

            for (int i = 0; i < roles.size(); i++) {
                if (!storeRole(roles.get(i), false)) {
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
        boolean inserted = true;
        boolean update = false;
        boolean answer = false;
        try {
            //If exists the club is a update not a insert.
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM club WHERE Name='" + club.returnName() + "'");
            if (rs.next()) {
                if (verbose) {
                    answer = MessageManager.question("questionUpdateClub", "Warning!", KendoTournamentGenerator.getInstance().language);
                }
                if (answer || !verbose) {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE club SET Country=?, City=?, Phone=?, Mail=?, Representative=?, Address=?, Web=? WHERE Name='" + club.returnName() + "'");
                    stmt.setString(1, club.returnCountry());
                    stmt.setString(2, club.returnCity());
                    stmt.setString(3, club.phone);
                    stmt.setString(4, club.email);
                    stmt.setString(5, club.representativeID);
                    stmt.setString(6, club.returnAddress());
                    stmt.setString(7, club.returnWeb());
                    stmt.executeUpdate();
                    stmt.close();
                    update = true;
                } else {
                    return false;
                }
            } else {
                Statement st = connection.createStatement();
                st.executeUpdate("INSERT INTO club (Name, Country, City, Address, Web, Mail, Phone, Representative) VALUES ('" + club.returnName() + "','" + club.returnCountry() + "','" + club.returnCity() + "','" + club.returnAddress() + "','" + club.returnWeb() + "','" + club.email + "'," + club.phone + ",'" + club.representativeID + "')");
                st.close();
            }
            rs.close();
            s.close();
        } catch (MySQLIntegrityConstraintViolationException micve) {
            inserted = false;
            KendoTournamentGenerator.getInstance().showErrorInformation(micve);
            if (verbose) {
                MessageManager.errorMessage("nameClub", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } catch (SQLException ex) {
            inserted = false;
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            inserted = false;
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (verbose) {
                MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        }

        if (inserted && verbose) {
            if (!update) {
                MessageManager.customMessage("clubStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, club.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                MessageManager.customMessage("clubUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, club.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            }

        }
        return inserted;
    }

    @Override
    public List<String> returnClubsName() {
        List<String> clubs = new ArrayList<String>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("select * FROM club ORDER BY Name");
            while (rs.next()) {
                clubs.add(rs.getString(1));
            }

            rs.close();
            s.close();
            return clubs;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("MySQL database connection fail", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }

        return null;
    }

    @Override
    public List<Club> getAllClubs() {
        List<Club> results = new ArrayList<Club>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("select * FROM club ORDER BY Name");
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
            rs.close();
            s.close();

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("MySQL database connection fail", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }
        return results;
    }

    @Override
    public boolean storeAllClubs(List<Club> clubs) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM club");
            s.close();

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
        List<Club> results = new ArrayList<Club>();

        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);

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
            rs.close();
            s.close();
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }
            }

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
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
    public boolean deleteClub(Club c, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("questionDeleteClub", "Warning!", KendoTournamentGenerator.getInstance().language);
            }

            if (answer || !verbose) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM club WHERE Name='" + c.returnName() + "'");
                s.close();
            }

        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteClub", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    }
                    KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                }

            }
        } catch (NullPointerException npe) {
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }

            }
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.customMessage("clubDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, c.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("clubDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, c.returnName());
                }
            }
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
    public boolean storeTournament(Tournament t, boolean verbose) {
        boolean error = false;
        boolean update = false;
        try {
            //If exists the tournament is an update not a insert.
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM tournament WHERE Name='" + t.name + "'");

            if (rs.next()) {
                return updateTournament(t, verbose);
            } else {
                try {
                    if (t.bannerInput.markSupported()) {
                        t.bannerInput.reset();
                    }
                } catch (IOException ex) {
                    KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                } catch (NullPointerException npe) {
                    KendoTournamentGenerator.getInstance().showErrorInformation(npe);
                }
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO tournament (Name, Banner, Size, FightingAreas, PassingTeams, TeamSize, Type, ScoreWin, ScoreDraw, ScoreType, Diploma, DiplomaSize, Accreditation, AccreditationSize) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                stmt.setString(1, t.name);
                storeBinaryStream(stmt, 2, t.bannerInput, (int) t.bannerSize);
                stmt.setLong(3, t.bannerSize);
                stmt.setInt(4, t.fightingAreas);
                stmt.setInt(5, t.howManyTeamsOfGroupPassToTheTree);
                stmt.setInt(6, t.teamSize);
                stmt.setString(7, t.mode);
                stmt.setFloat(8, t.getScoreForWin());
                stmt.setFloat(9, t.getScoreForDraw());
                stmt.setString(10, t.getChoosedScore());
                storeBinaryStream(stmt, 11, t.diplomaInput, (int) t.diplomaSize);
                stmt.setLong(12, t.diplomaSize);
                storeBinaryStream(stmt, 13, t.accreditationInput, (int) t.accreditationSize);
                stmt.setLong(14, t.accreditationSize);
                stmt.executeUpdate();
                stmt.close();
            }
            rs.close();
            s.close();
        } catch (MysqlDataTruncation mdt) {
            KendoTournamentGenerator.getInstance().showErrorInformation(mdt);
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            error = true;
            MessageManager.errorMessage("storeTournament", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            error = true;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }

        if (!error) {
            if (!update) {
                if (verbose) {
                    MessageManager.customMessage("tournamentStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("tournamentStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.name);
                    }
                }
            } else {
                if (verbose) {
                    MessageManager.customMessage("tournamentUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("tournamentUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.name);
                    }
                }
            }
        }

        return !error;
    }

    @Override
    public boolean deleteTournament(String championship) {
        boolean answer = false;
        try {
            answer = MessageManager.question("tournamentDeleteQuestion", "Warning!", KendoTournamentGenerator.getInstance().language);
            if (answer) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM tournament WHERE Name='" + championship + "'");
                MessageManager.customMessage("tournamentDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, championship, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                s.close();
            }

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return answer;
    }

    @Override
    public boolean updateTournament(Tournament t, boolean verbose) {
        boolean error = false;
        Tournament old_tournament = null;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("questionUpdateTournament", "Warning!", KendoTournamentGenerator.getInstance().language);
            }
            if (!verbose || answer) {
                old_tournament = getTournamentByName(t.name, false);
                try {
                    if (t.bannerInput.markSupported()) {
                        t.bannerInput.reset();
                    }
                } catch (IOException ex) {
                }
                PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Banner=?, Size=?, FightingAreas=?, PassingTeams=?, TeamSize=?, Type=?, ScoreWin=?, ScoreDraw=?, ScoreType=?, Diploma=?, DiplomaSize=?, Accreditation=?, AccreditationSize=? WHERE Name='" + t.name + "'");
                storeBinaryStream(stmt, 1, t.bannerInput, (int) t.bannerSize);
                stmt.setLong(2, t.bannerSize);
                stmt.setInt(3, t.fightingAreas);
                stmt.setInt(4, t.howManyTeamsOfGroupPassToTheTree);
                stmt.setInt(5, t.teamSize);
                stmt.setString(6, t.mode);
                stmt.setFloat(7, t.getScoreForWin());
                stmt.setFloat(8, t.getScoreForDraw());
                stmt.setString(9, t.getChoosedScore());
                storeBinaryStream(stmt, 10, t.diplomaInput, (int) t.diplomaSize);
                stmt.setLong(11, t.diplomaSize);
                storeBinaryStream(stmt, 12, t.accreditationInput, (int) t.accreditationSize);
                stmt.setLong(13, t.accreditationSize);
                stmt.executeUpdate();
                stmt.close();

                //Delete fights and teams because can be some changes (area fights or size of teams of the tournament).
                try {
                    if (old_tournament.fightingAreas != t.fightingAreas) {
                        deleteFightsOfTournament(t.name, false);
                    }
                    if (old_tournament.teamSize != t.teamSize) {
                        deleteTeamsOfTournament(t.name, false);
                    }
                } catch (NullPointerException npe) {
                    deleteTeamsOfTournament(t.name, false);
                    deleteFightsOfTournament(t.name, false);
                }
            } else {
                return false;
            }
        } catch (MysqlDataTruncation mdt) {
            error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeTournament", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (NullPointerException npe) {
            error = true;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }

        if (!error) {
            if (verbose) {
                MessageManager.customMessage("tournamentUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("tournamentUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.name);
                }
            }
        }

        return !error;
    }

    @Override
    public List<Tournament> getAllTournaments() {
        List<Tournament> results = new ArrayList<Tournament>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tournament ORDER BY Name");
            while (rs.next()) {
                Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), rs.getObject("Type").toString());
                t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"), rs.getInt("ScoreDraw"));
                InputStream sImage = rs.getBinaryStream("Banner");
                Long size = rs.getLong("Size");
                t.addBanner(sImage, size);
                InputStream sImage2 = rs.getBinaryStream("Accreditation");
                Long size2 = rs.getLong("AccreditationSize");
                t.addAccreditation(sImage2, size2);
                InputStream sImage3 = rs.getBinaryStream("Diploma");
                Long size3 = rs.getLong("DiplomaSize");
                t.addDiploma(sImage3, size3);
                results.add(t);
            }
            rs.close();
            st.close();
            return results;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return null;
    }

    @Override
    public boolean storeAllTournaments(List<Tournament> tournaments) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM tournament");
            s.close();

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
    public Tournament getTournamentByName(String name, boolean verbose) {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tournament WHERE Name='" + name + "' ");
            rs.next();
            Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), rs.getObject("Type").toString());
            t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"), rs.getInt("ScoreDraw"));
            InputStream sImage = rs.getBinaryStream("Banner");
            Long size = rs.getLong("Size");
            t.addBanner(sImage, size);
            InputStream sImage2 = rs.getBinaryStream("Accreditation");
            Long size2 = rs.getLong("AccreditationSize");
            t.addAccreditation(sImage2, size2);
            InputStream sImage3 = rs.getBinaryStream("Diploma");
            Long size3 = rs.getLong("DiplomaSize");
            t.addDiploma(sImage3, size3);
            rs.close();
            st.close();
            return t;
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return null;
    }

    @Override
    public List<Tournament> searchTournament(String query, boolean verbose) {
        List<Tournament> results = new ArrayList<Tournament>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Tournament t = new Tournament(rs.getObject("Name").toString(), rs.getInt("FightingAreas"), rs.getInt("PassingTeams"), rs.getInt("TeamSize"), rs.getObject("Type").toString());
                t.changeScoreOptions(rs.getObject("ScoreType").toString(), rs.getInt("ScoreWin"), rs.getInt("ScoreDraw"));
                InputStream sImage = (InputStream) rs.getBinaryStream("Banner");
                Long size = rs.getLong("Size");
                t.addBanner(sImage, size);
                results.add(t);
            }
            rs.close();
            s.close();
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }

            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return results;
    }

    @Override
    public List<Tournament> searchTournamentsByName(String name, boolean verbose) {
        String query = "SELECT * FROM tournament WHERE Name LIKE '%" + name + "%' ORDER BY Name";
        return searchTournament(query, verbose);
    }

    @Override
    public void cleanLeague(String league, List<Team> teams) {
        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            t.group = 0;
            updateTeamGroupOfLeague(league, t);
        }
    }

    @Override
    public void storeDiplomaImage(Tournament t, InputStream Image, long imageSize) {
        try {
            try {
                if (Image.markSupported()) {
                    Image.reset();
                }
            } catch (IOException ex) {
            }
            PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Diploma=?, DiplomaSize=? WHERE Name='" + t.name + "'");
            storeBinaryStream(stmt, 1, Image, (int) imageSize);
            stmt.setLong(2, imageSize);
            try {
                stmt.executeUpdate();
            } catch (OutOfMemoryError ofm) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
            stmt.close();
        } catch (MysqlDataTruncation mdt) {
            //error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            //error = true;
            if (imageSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                //ShowMessage.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
            }
        } catch (NullPointerException npe) {
            //error = true;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }
    }

    @Override
    public void storeAccreditationImage(Tournament t, InputStream Image, long imageSize) {
        try {
            try {
                if (Image.markSupported()) {
                    Image.reset();
                }
            } catch (IOException ex) {
            }
            PreparedStatement stmt = connection.prepareStatement("UPDATE tournament SET Accreditation=?, AccredotationSize=? WHERE Name='" + t.name + "'");
            storeBinaryStream(stmt, 1, Image, (int) imageSize);
            stmt.setLong(2, imageSize);
            try {
                stmt.executeUpdate();
            } catch (OutOfMemoryError ofm) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
            stmt.close();
        } catch (MysqlDataTruncation mdt) {
            //error = true;
            MessageManager.errorMessage("storeImage", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            //error = true;
            if (imageSize > 1048576) {
                MessageManager.errorMessage("imageTooLarge", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                //ShowMessage.errorMessage("storeCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
            }
        } catch (NullPointerException npe) {
            //error = true;
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
        }
    }

    @Override
    public int getLevelTournament(String tournament) {
        String query = "SELECT MAX(LeagueLevel) FROM kendotournament.fight WHERE Tournament='" + tournament + "';";

        int level = -1;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);
            rs.next();
            level = rs.getInt(1);
            rs.close();
            s.close();
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
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
     * Store a Tournament into the database.
     *
     * @param club
     */
    @Override
    public boolean storeTeam(Team t, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        boolean update = false;
        //Delete all old entries for these team if exists.
        try {
            //If exists the team is a update not a insert.
            Statement s = connection.createStatement();
            //updating member...
            ResultSet rs1 = s.executeQuery("SELECT * FROM team WHERE Name='" + t.returnName() + "' AND Tournament='" + t.competition.name + "'");
            if (rs1.next()) {
                if (verbose) {
                    answer = MessageManager.question("questionUpdateTeam", "Warning!", KendoTournamentGenerator.getInstance().language);
                }
                if (answer || !verbose) {
                    s.executeUpdate("DELETE FROM team WHERE Name='" + t.returnName() + "' AND Tournament='" + t.competition.name + "' AND LeagueGroup=" + t.group);
                } else {
                    return false;
                }
            }

            insertTeam(t, verbose);

            rs1.close();
            s.close();
        } catch (MySQLIntegrityConstraintViolationException micve) {
            KendoTournamentGenerator.getInstance().showErrorInformation(micve);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("repeatedCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("storeTeam", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    }
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        }
        if (!error) {
            if (update) {
                if (verbose) {
                    MessageManager.customMessage("teamUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("teamUpdated", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName());
                    }
                }
            } else {
                if (verbose) {
                    MessageManager.customMessage("teamStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("teamStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName());
                    }
                }
            }

        }
        return !error;
    }

    @Override
    public boolean insertTeam(Team t, boolean verbose) {
        boolean error = false;
        //Insert team.
        for (int levelIndex = 0; levelIndex < t.levelChangesSize(); levelIndex++) {
            if (t.changesInThisLevel(levelIndex)) {
                for (int indexCompetitor = 0; indexCompetitor < t.getNumberOfMembers(levelIndex); indexCompetitor++) {
                    try {
                        PreparedStatement stmt = connection.prepareStatement("INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, LevelTournament) VALUES (?,?,?,?,?,?)");
                        stmt.setString(1, t.returnName());
                        stmt.setString(2, t.getMember(indexCompetitor, levelIndex).getId());
                        stmt.setString(3, t.competition.name);
                        stmt.setInt(4, indexCompetitor);
                        stmt.setInt(5, t.group);
                        stmt.setInt(6, levelIndex);
                        stmt.executeUpdate();
                        stmt.close();
                    } catch (NullPointerException npe) { //The team has one competitor less...
                    } catch (SQLException ex) {
                        KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                        if (!error) {
                            error = true;
                            if (!showSQLError(ex.getErrorCode())) {
                                if (verbose) {
                                    MessageManager.errorMessage("storeTeam", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                                }
                            }
                        }
                    }
                }
            }
        }
        return !error;
    }

    private List<Competitor> searchTeamMembersInLevel(Team t, boolean verbose, int level) {
        List<Competitor> results = new ArrayList<Competitor>();
        try {
            //If exists the competitor is a update not a insert.
            Statement s = connection.createStatement();
            // ResultSet rs = s.executeQuery("SELECT * FROM team WHERE Name='" + t.returnName() + "' AND Tournament='" + t.competition.name + "' AND LevelTournament=" + level + " ORDER BY Position ASC");
            ResultSet rs = s.executeQuery("SELECT * FROM team WHERE Name='" + t.returnName() + "' AND Tournament='" + t.competition.name + "' AND LevelTournament=" + level + " ORDER BY Position ASC");
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
            rs.close();
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        }
        return results;
    }

    private List<List<Competitor>> searchTeamMembers(Team t, boolean verbose) {
        List<List<Competitor>> membersPerLevel = new ArrayList<List<Competitor>>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT MAX(LevelTournament) AS level FROM team WHERE Name='" + t.returnName() + "' AND Tournament='" + t.competition.name + "'");
            while (rs.next()) {
                int level = rs.getInt("level");
                for (int i = 0; i <= level; i++) {
                    List<Competitor> members = searchTeamMembersInLevel(t, verbose, i);
                    membersPerLevel.add(members);
                }
            }
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        }
        return membersPerLevel;
    }

    @Override
    public List<Team> searchTeam(String query, boolean verbose) {
        List<Team> results = new ArrayList<Team>();

        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Team t = new Team(rs.getObject("Name").toString(), getTournamentByName(rs.getObject("Tournament").toString(), false));
                t.addGroup(rs.getInt("LeagueGroup"));
                t.setMembers(searchTeamMembers(t, false));
                results.add(t);
            }
            rs.close();
            s.close();
            if (results.isEmpty()) {
                if (verbose) {
                    MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return results;
    }

    @Override
    public List<Team> searchTeamsByNameAndTournament(String name, String tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Name LIKE '%" + name + "%' AND Tournament='" + tournament + "' GROUP BY Name ORDER BY Name";
        return searchTeam(query, verbose);
    }

    @Override
    public Team getTeamByName(String name, String championship, boolean verbose) {
        String query = "SELECT * FROM team WHERE Name='" + name + "' AND Tournament='" + championship + "' GROUP BY Name ORDER BY Name";
        List<Team> teams = searchTeam(query, verbose);
        if (!teams.isEmpty()) {
            return searchTeam(query, verbose).get(0);
        } else {
            if (verbose) {
                MessageManager.customMessage("Error obtaining team " + name, "Error", 0, true);
            }
            return null;
        }
    }

    @Override
    public List<Team> searchTeamsByTournament(String tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Tournament LIKE '" + tournament + "' GROUP BY Name ORDER BY Name ";
        return searchTeam(query, verbose);
    }

    @Override
    public List<Team> searchTeamsByTournamentExactName(String tournament, boolean verbose) {
        String query = "SELECT * FROM team WHERE Tournament='" + tournament + "' GROUP BY Name ORDER BY Name";
        return searchTeam(query, verbose);
    }

    @Override
    public List<Team> searchTeamsByLevel(String tournament, int level, boolean verbose) {
        // String query = "SELECT * FROM team WHERE Tournament='" + tournament + "' AND LevelTournament>=" + level + " GROUP BY Name ORDER BY Name ";
        String query = "SELECT * FROM team t1 LEFT JOIN fight f1 ON (t1.Name=f1.team1 OR t1.Name=f1.team2)  WHERE t1.Tournament='" + tournament + "' AND f1.Tournament='" + tournament + "' AND f1.LeagueLevel>=" + level + " GROUP BY Name ORDER BY Name ";
        return searchTeam(query, verbose);
    }

    @Override
    public List<Team> getAllTeams() {
        String query = "SELECT * FROM team GROUP BY Name,Tournament ORDER BY Name ";
        return searchTeam(query, false);
    }

    @Override
    public boolean storeAllTeams(List<Team> teams) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM team");
            s.close();

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
    public void updateTeamGroupOfLeague(String league, Team t) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE team SET LeagueGroup=? WHERE Name='" + t.returnName() + "' AND Tournament='" + league + "'");
            stmt.setInt(1, t.group);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

    }

    @Override
    public boolean deleteTeam(Team t, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("questionDeleteTeam", "Warning!", KendoTournamentGenerator.getInstance().language);
            }

            if (answer || !verbose) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM team WHERE Name='" + t.returnName() + "' AND Tournament='" + t.competition.name + "'");
                s.close();
            }

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteTeam", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    }
                }

            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }

            }
        }
        if (!error && answer) {
            if (verbose) {
                MessageManager.customMessage("teamDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("teamDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName());
                }
            }
        }

        return !error && (answer || !verbose);
    }

    @Override
    public boolean deleteTeam(String team, String competition, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        int sol = 0;
        try {
            if (verbose) {
                answer = MessageManager.question("questionDeleteTeam", "Warning!", KendoTournamentGenerator.getInstance().language);
            }

            if (answer || !verbose) {
                Statement s = connection.createStatement();
                sol = s.executeUpdate("DELETE FROM team WHERE Name='" + team + "' AND Tournament='" + competition + "'");
                s.close();
            }

        } catch (SQLException ex) {
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteTeam", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    }
                    KendoTournamentGenerator.getInstance().showErrorInformation(ex);
                }

            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }

            }
        }
        if (!error && answer) {
            if (sol > 0) {
                if (verbose) {
                    MessageManager.customMessage("teamDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, team, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("teamDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, team);
                    }
                }
            } else {
                MessageManager.errorMessage("teamNotDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }

        }
        return !error && (answer || !verbose);
    }

    @Override
    public void setIndividualTeams(String championship) {
        List<Competitor> competitors = selectAllCompetitorsInTournament(championship);
        Tournament champ = getTournamentByName(championship, false);
        MessageManager.customMessage("oneTeamPerCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
        for (int i = 0; i < competitors.size(); i++) {
            Team t = new Team(competitors.get(i).returnSurname() + ", " + competitors.get(i).returnName(), champ);
            t.addOneMember(competitors.get(i), 0);
            storeTeam(t, false);
        }

    }

    @Override
    public boolean deleteTeamsOfTournament(String championship, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("questionDeleteTeams", "Warning!", KendoTournamentGenerator.getInstance().language);
            }

            if (answer || !verbose) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM team WHERE Tournament='" + championship + "'");
                s.close();
            }

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("deleteTeam", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    }
                }

            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }

            }
        }
        return !error && (answer || !verbose);
    }

    @Override
    public List<TeamRanking> getTeamsOrderByScore(String championship, boolean verbose) {
        List<TeamRanking> teamsOrdered = new ArrayList<TeamRanking>();
        //String query = "SELECT " + "t1.NomEquipo as Equipo, " + "ifnull(t3.NumVictorias,0) as Victorias, " + "ifnull(t2.TotalDuelos,0) as Duelos, " + "ifnull(t1.TotalPtos,0) as Puntos " + "FROM " + "(SELECT " + "		t1.NomEquipo as NomEquipo,  " + "		sum(TotalPtos) as TotalPtos  " + "FROM  " + "		(SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos  " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team1  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "				t2.Position = t4.OrderPlayer " + "				 " + "		UNION ALL  " + "		 " + "		SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team2  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "			t2.Position = t4.OrderPlayer " + " " + "		) t1  " + "GROUP BY  " + "		t1.NomEquipo " + ") t1  " + "LEFT OUTER JOIN " + "(	SELECT  " + "		CASE  " + "			WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "			ELSE t2.Team  " + "		END as NomEquipo, " + "		count(Distinct t1.IdDuelo) as TotalDuelos " + "FROM  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo1  " + "	FROM  " + "			team t2  " + "			INNER JOIN  " + "			fight t3  " + "			ON t2.Name = t3.Team1  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "			INNER JOIN duel t4  " + "			ON t3.ID = t4.Fight  " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t1  " + "	INNER JOIN  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo2  " + "	FROM  " + "			team t2  " + "			INNER JOIN fight t3  " + "			ON t2.Name = t3.Team2  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "			INNER JOIN duel t4 " + "			ON t3.ID = t4.Fight " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t2 " + "	ON t1.IdDuelo = t2.IdDuelo  " + "	WHERE  " + "			TotalDuelo1 <> TotalDuelo2    " + "	GROUP BY " + "			CASE  " + "				WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "				ELSE t2.Team  " + "			END " + "	) t2  " + "	ON t1.NomEquipo = t2.NomEquipo " + "	LEFT OUTER JOIN " + "	(SELECT " + "			CASE " + "					WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "					WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "			END as NomEquipo, " + "			count(idcombate) as NumVictorias " + "		FROM " + "		(SELECT  " + "				idcombate, " + "				EquipoIzq, " + "				EquipoDer, " + "				Sum(NumDuelosGanados1) as VictoriaIzq, " + "				Sum(NumDuelosGanados2) as VictoriaDer, " + "				Sum(TotalDueloA) as TotalPuntosA, " + "				Sum(TotalDueloB) as TotalPuntosB" + "		FROM  " + "				(SELECT " + "						t1.Team as EquipoIzq, " + "						t2.Team as EquipoDer, " + "						t1.IdCombate, " + "						TotalDuelo1 as TotalDueloA, " + "						TotalDuelo2 as TotalDueloB," + "						CASE WHEN TotalDuelo1 > TotalDuelo2 THEN 1 " + "						ELSE 0 END as NumDuelosGanados1, " + "						CASE WHEN TotalDuelo2 > TotalDuelo1 THEN 1 " + "						ELSE 0 END  as NumDuelosGanados2 " + "								 " + "				FROM	 " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo1  " + "					FROM  " + "							team t2  " + "							INNER JOIN  " + "							fight t3  " + "							ON t2.Name = t3.Team1  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "							INNER JOIN duel t4  " + "							ON t3.ID = t4.Fight  " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t1  " + "					INNER JOIN  " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo2  " + "					FROM  " + "							team t2  " + "							INNER JOIN fight t3  " + "							ON t2.Name = t3.Team2  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "							INNER JOIN duel t4 " + "							ON t3.ID = t4.Fight " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t2 " + "					ON t1.IdDuelo = t2.IdDuelo  " + "					AND t1.IdCombate = t2.IDCombate " + "				WHERE  " + "						TotalDuelo1 <> TotalDuelo2 " + "				)t1 " + "			GROUP BY " + "					idcombate, " + "					EquipoIzq, " + "					EquipoDer " + "		) t1 " + "	GROUP BY " + "	CASE " + "			WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "			WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "	END   " + "	)t3  " + "	ON t1.NomEquipo = t3.NomEquipo " + "ORDER BY " + "	ifnull(t3.NumVictorias,0) DESC, " + "	ifnull(t2.TotalDuelos,0) DESC, " + "	ifnull(t1.TotalPtos,0)  DESC, " + "	t1.NomEquipo ";
        String query = "SELECT " + "t1.NomEquipo as Equipo, " + "ifnull(t3.NumVictorias,0) as Victorias, " + "ifnull(t2.TotalDuelos,0) as Duelos, " + "ifnull(t1.TotalPtos,0) as Puntos " + "FROM " + "(SELECT " + "		t1.NomEquipo as NomEquipo,  " + "		sum(TotalPtos) as TotalPtos  " + "FROM  " + "		(SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos  " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team1  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "				t2.Position = t4.OrderPlayer " + "				 " + "		UNION ALL  " + "		 " + "		SELECT  " + "				t2.Name as NomEquipo,  " + "				CASE  " + "					WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "					ELSE 0 END + CASE  " + "										WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "										ELSE 0 END " + "				as TotalPtos " + "		FROM  " + "				team t2  " + "				INNER JOIN  " + "				fight t3  " + "				ON t2.Name = t3.Team2  " + "				AND t2.Tournament = t3.Tournament  " + "				AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "				INNER JOIN  " + "				duel t4  " + "				ON t3.ID = t4.Fight  " + "		WHERE  " + "			t2.Position = t4.OrderPlayer " + " " + "		) t1  " + "GROUP BY  " + "		t1.NomEquipo " + ") t1  " + "LEFT OUTER JOIN " + "(	SELECT  " + "		CASE  " + "			WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "			ELSE t2.Team  " + "		END as NomEquipo, " + "		count(Distinct t1.IdDuelo) as TotalDuelos " + "FROM  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo1  " + "	FROM  " + "			team t2  " + "			INNER JOIN  " + "			fight t3  " + "			ON t2.Name = t3.Team1  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "			INNER JOIN duel t4  " + "			ON t3.ID = t4.Fight  " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t1  " + "	INNER JOIN  " + "	(SELECT  " + "			t2.Name as Team, " + "			t4.ID as IdDuelo,  " + "			Sum(CASE  " + "				WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "				ELSE 0 END + CASE  " + "								WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END ) " + "			as TotalDuelo2  " + "	FROM  " + "			team t2  " + "			INNER JOIN fight t3  " + "			ON t2.Name = t3.Team2  " + "			AND t2.Tournament = t3.Tournament  " + "			AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "			INNER JOIN duel t4 " + "			ON t3.ID = t4.Fight " + "	WHERE  " + "			t2.Position = t4.OrderPlayer  " + "	GROUP BY " + "			t2.Name, " + "			t4.ID  " + "	)t2 " + "	ON t1.IdDuelo = t2.IdDuelo  " + "	WHERE  " + "			TotalDuelo1 <> TotalDuelo2    " + "	GROUP BY " + "			CASE  " + "				WHEN TotalDuelo1 > TotalDuelo2 THEN t1.Team   " + "				ELSE t2.Team  " + "			END " + "	) t2  " + "	ON t1.NomEquipo = t2.NomEquipo " + "	LEFT OUTER JOIN " + "	(SELECT " + "			CASE " + "					WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "					WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "			END as NomEquipo, " + "			count(idcombate) as NumVictorias " + "		FROM " + "		(SELECT  " + "				idcombate, " + "				EquipoIzq, " + "				EquipoDer, " + "				Sum(NumDuelosGanados1) as VictoriaIzq, " + "				Sum(NumDuelosGanados2) as VictoriaDer, " + "				Sum(TotalDueloA) as TotalPuntosA, " + "				Sum(TotalDueloB) as TotalPuntosB" + "		FROM  " + "				(SELECT " + "						t1.Team as EquipoIzq, " + "						t2.Team as EquipoDer, " + "						t1.IdCombate, " + "						TotalDuelo1 as TotalDueloA, " + "						TotalDuelo2 as TotalDueloB," + "						CASE WHEN TotalDuelo1 > TotalDuelo2 THEN 1 " + "						ELSE 0 END as NumDuelosGanados1, " + "						CASE WHEN TotalDuelo2 > TotalDuelo1 THEN 1 " + "						ELSE 0 END  as NumDuelosGanados2 " + "								 " + "				FROM	 " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer1A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer1B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo1  " + "					FROM  " + "							team t2  " + "							INNER JOIN  " + "							fight t3  " + "							ON t2.Name = t3.Team1  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "')  " + "							INNER JOIN duel t4  " + "							ON t3.ID = t4.Fight  " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t1  " + "					INNER JOIN  " + "					(SELECT  " + "							t2.Name as Team, " + "							t3.Id as IdCombate, " + "							t4.ID as IdDuelo,  " + "							Sum(CASE  " + "								WHEN PointPlayer2A in ('K','M','T','D','I','H')  THEN 1  " + "								ELSE 0 END + CASE  " + "												WHEN PointPlayer2B in ('K','M','T','D','I','H')  THEN 1  " + "												ELSE 0 END ) " + "							as TotalDuelo2  " + "					FROM  " + "							team t2  " + "							INNER JOIN fight t3  " + "							ON t2.Name = t3.Team2  " + "							AND t2.Tournament = t3.Tournament  " + "							AND (t2.Tournament = '" + championship + "' OR 'All' = '" + championship + "') " + "							INNER JOIN duel t4 " + "							ON t3.ID = t4.Fight " + "					WHERE  " + "							t2.Position = t4.OrderPlayer  " + "					GROUP BY " + "							t2.Name, " + "							t3.ID, " + "							t4.ID  " + "					)t2 " + "					ON t1.IdDuelo = t2.IdDuelo  " + "					AND t1.IdCombate = t2.IDCombate " + "				WHERE  " + "						TotalDuelo1 <> TotalDuelo2 " + "				)t1 " + "			GROUP BY " + "					idcombate, " + "					EquipoIzq, " + "					EquipoDer " + "		) t1 " + "	GROUP BY " + "	CASE " + "			WHEN VictoriaIzq >  VictoriaDer THEN EquipoIzq " + "			WHEN VictoriaIzq < VictoriaDer THEN EquipoDer " + "ELSE (CASE WHEN TotalPuntosA > TotalPuntosB THEN EquipoIzq WHEN TotalPuntosA < TotalPuntosB THEN EquipoDer END)" + "	END   " + "	)t3  " + "	ON t1.NomEquipo = t3.NomEquipo " + "ORDER BY " + "	ifnull(t3.NumVictorias,0) DESC, " + "	ifnull(t2.TotalDuelos,0) DESC, " + "	ifnull(t1.TotalPtos,0)  DESC, " + "	t1.NomEquipo ";
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                teamsOrdered.add(new TeamRanking(rs.getObject("Equipo").toString(), championship, rs.getInt("Victorias"), 0, rs.getInt("Duelos"), 0, rs.getInt("Puntos")));
            }

            rs.close();
            s.close();
            if (teamsOrdered.isEmpty() && verbose) {
                MessageManager.errorMessage("noResults", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        }
        return teamsOrdered;
    }

    @Override
    public Team getTeamOfCompetitor(String competitorID, String championship, boolean verbose) {
        String query = "SELECT * FROM team WHERE Member='" + competitorID + "' AND Tournament='" + championship + "' GROUP BY Name";
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
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO team (Name, Member, Tournament, Position, LeagueGroup, LevelTournament) VALUES (?,?,?,?,?,?)");
                stmt.setString(1, t.returnName());
                stmt.setString(2, t.getMember(indexCompetitor, level).getId());
                stmt.setString(3, t.competition.name);
                stmt.setInt(4, indexCompetitor);
                stmt.setInt(5, t.group);
                stmt.setInt(6, level);
                stmt.executeUpdate();
                stmt.close();
            }
            //connection.commit();
            //s.execute("COMMIT");
        } catch (MySQLIntegrityConstraintViolationException micve) {
            KendoTournamentGenerator.getInstance().showErrorInformation(micve);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("repeatedCompetitor", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
                if (!showSQLError(ex.getErrorCode())) {
                    if (verbose) {
                        MessageManager.errorMessage("storeTeam", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                    }
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        }
        if (!error) {
            if (verbose) {
                MessageManager.customMessage("teamStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName(), JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("teamStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, t.returnName());
                }
            }
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
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM team WHERE Name='" + t.returnName() + "' AND LevelTournament >=" + level + " AND Tournament='" + t.competition.name + "'");
            s.close();

            return true;
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
        }
        return !error;

    }

    @Override
    public boolean deleteAllMemberChangesInTeams(String championship, boolean verbose) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM team WHERE LevelTournament > 0  AND Tournament='" + championship + "'");
            s.close();
            return true;
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            if (!error) {
                error = true;
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            if (!error) {
                error = true;
                if (verbose) {
                    MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().getLogOption());
                }
            }
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
    public boolean storeFights(List<Fight> fights, boolean purgeTournament, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        if (fights.size() > 0) {
            try {
                //Delete all previous fights.
                if (verbose) {
                    answer = MessageManager.question("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
                } else {
                    answer = true;
                }

                if (answer) {
                    Statement s = connection.createStatement();
                    if (purgeTournament) {
                        s.executeUpdate("DELETE FROM fight WHERE Tournament='" + fights.get(0).competition.name + "'");
                        s.executeUpdate("DELETE FROM team WHERE Tournament='" + fights.get(0).competition.name + "' AND LevelTournament > " + 0);
                    }

                    //Obtain the max level of figths.
                    int level = 0;
                    for (int i = 0; i < fights.size(); i++) {
                        if (level < fights.get(i).level) {
                            level = fights.get(i).level;
                        }
                    }

                    for (int i = 0; i < fights.size(); i++) {
                        //Add the fights that depends on the level and the teams.
                        s.executeUpdate("INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, LeagueLevel, MaxWinners) VALUES ('" + fights.get(i).team1.returnName() + "','" + fights.get(i).team2.returnName() + "','" + fights.get(i).competition.name + "','" + fights.get(i).asignedFightArea + "'," + fights.get(i).returnWinner() + "," + fights.get(i).level + "," + fights.get(i).getMaxWinners() + ")", Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = s.getGeneratedKeys();
                        rs.next();
                        fights.get(i).setDatabaseID(rs.getInt(1));
                        rs.close();
                    }
                    s.close();
                }

            } catch (SQLException ex) {
                error = true;
                MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }

            if (!error && answer) {
                if (verbose) {
                    MessageManager.customMessage("fightStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fights.get(0).competition.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                } else {
                    if (KendoTournamentGenerator.getInstance().getLogOption()) {
                        Log.storeLog("fightStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fights.get(0).competition.name);
                    }
                }
            }
        } else {
            return false;
        }

        return !error && answer;
    }

    @Override
    public boolean storeAllFights(List<Fight> fights) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM fight");
            s.executeUpdate("DELETE FROM duel");
            for (int i = 0; i < fights.size(); i++) {
                s.executeUpdate("INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, LeagueLevel, MaxWinners) VALUES ('" + fights.get(i).team1.returnName() + "','" + fights.get(i).team2.returnName() + "','" + fights.get(i).competition.name + "','" + fights.get(i).asignedFightArea + "'," + fights.get(i).returnWinner() + "," + fights.get(i).level + "," + fights.get(i).getMaxWinners() + ")", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = s.getGeneratedKeys();
                rs.next();
                fights.get(i).setDatabaseID(rs.getInt(1));
                rs.close();

                storeDuelsOfFight(fights.get(i));
            }
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            Log.storeLog("fightStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fights.get(0).competition.name);
        }
        return !error;
    }

    @Override
    public boolean storeFight(Fight fight, boolean verbose) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM fight WHERE Tournament='" + fight.competition.name + "' AND Team1='" + fight.team1.returnName() + "' AND Team2='" + fight.team2.returnName() + "'");
            s.executeUpdate("INSERT INTO fight (Team1, Team2, Tournament, FightArea, Winner, LeagueLevel, MaxWinners) VALUES ('" + fight.team1.returnName() + "','" + fight.team2.returnName() + "','" + fight.competition.name + "','" + fight.asignedFightArea + "'," + fight.isOver() + "," + fight.level + "," + fight.getMaxWinners() + ")", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = s.getGeneratedKeys();
            rs.next();
            fight.setDatabaseID(rs.getInt(1));
            rs.close();
            s.close();
        } catch (SQLException ex) {
            error = true;
            ex.printStackTrace();
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (NullPointerException npe) {
            error = true;
            npe.printStackTrace();
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        if (!error) {
            if (verbose) {
                MessageManager.customMessage("fightStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fight.competition.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("fightStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fight.competition.name);
                }
            }
        }
        return !error;
    }

    @Override
    public boolean deleteFightsOfTournament(String championship, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
            }
            if (answer || !verbose) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM fight WHERE Tournament='" + championship + "'");
                s.close();
                deleteDrawsOfTournament(championship);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public boolean deleteFightsOfLevelOfTournament(String championship, int level, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.question("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
            }
            if (answer || !verbose) {
                Statement s = connection.createStatement();
                s.executeUpdate("DELETE FROM fight WHERE Tournament='" + championship + "' AND LeagueLevel >=" + level);
                s.close();
                List<Integer> groups = KendoTournamentGenerator.getInstance().designedGroups.returnIndexOfGroupsOfLevel(level);
                for (int i = 0; i < groups.size(); i++) {
                    deleteDrawsOfGroupOfTournament(championship, i);
                }
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Fight> searchFights(String query, String championship) {
        List<Fight> results = new ArrayList<Fight>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                Fight f = new Fight(getTeamByName(rs.getObject("Team1").toString(), championship, false),
                        getTeamByName(rs.getObject("Team2").toString(), championship, false),
                        getTournamentByName(rs.getObject("Tournament").toString(), false),
                        rs.getInt("FightArea"), rs.getInt("Winner"), rs.getInt("LeagueLevel"),
                        rs.getInt("ID"));
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
            rs.close();
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return results;
    }

    /**
     * Search all fights from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Fight> searchFightsByTournamentName(String championship) {
        String query = "SELECT * FROM fight WHERE Tournament='" + championship + "'";
        return searchFights(query, championship);
    }

    /**
     * Search all fights from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Fight> searchFightsByTournamentNameAndFightArea(String championship, int fightArea) {
        String query = "SELECT * FROM fight WHERE Tournament='" + championship + "' AND FightArea=" + fightArea;
        return searchFights(query, championship);
    }

    /**
     * Search all fights from one determined tournament.
     *
     * @param championship
     * @return
     */
    @Override
    public List<Fight> searchFightsByTournamentNameAndTeam(String championship, String team) {
        String query = "SELECT * FROM fight WHERE Tournament='" + championship + "' AND (Team1='" + team + "' OR Team2='" + team + "')";
        return searchFights(query, championship);
    }

    @Override
    public int obtainFightID(Fight f) {
        int ID = -1;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM fight WHERE Tournament='" + f.competition.name + "' AND Team1='" + f.team1.returnName() + "' AND Team2='" + f.team2.returnName() + "'");
            rs.next();
            ID = rs.getInt("ID");
            rs.close();
            s.close();
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ID;
    }

    @Override
    public boolean deleteFight(Fight fight, boolean verbose) {
        boolean error = false;
        boolean answer = false;
        try {
            answer = MessageManager.question("deleteOneFight", "Warning!", KendoTournamentGenerator.getInstance().language);
            if (answer) {
                Statement s = connection.createStatement();

                /*
                 * Try to delete by ID
                 */
                int result = s.executeUpdate("DELETE FROM fight WHERE  + ID=" + fight.returnDatabaseID() + "");

                /*
                 * If not, try to delete by data
                 */
                if (result < 1) {
                    s.executeUpdate("DELETE FROM fight WHERE Tournament='" + fight.competition.name + "' AND Team1='" + fight.team1.returnName() + "' AND Team2='" + fight.team2.returnName() + "'");
                }
                s.close();
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("deleteFight", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        if (!error && answer) {
            if (verbose) {
                MessageManager.customMessage("fightDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fight.competition.name, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("fightDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fight.competition.name);
                }
            }
        }

        return (answer && !error);
    }

    @Override
    public boolean updateFightAsOver(Fight fight) {
        boolean error = false;
        try {
            /*
             * Considering the fight over if is updated
             */
            int over = fight.isOver();
            if (over == 2) {
                over = 0;
            }
            Statement s = connection.createStatement();
            PreparedStatement stmt = connection.prepareStatement("UPDATE fight SET Winner=? WHERE Tournament='" + fight.competition.name + "' AND Team1='" + fight.team1.returnName() + "' AND Team2='" + fight.team2.returnName() + "' AND LeagueLevel=" + fight.level);
            stmt.setInt(1, over);
            stmt.executeUpdate();
            stmt.close();
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public boolean updateFightAsNotOver(Fight fight) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            PreparedStatement stmt = connection.prepareStatement("UPDATE fight SET Winner=? WHERE Tournament='" + fight.competition.name + "' AND Team1='" + fight.team1.returnName() + "' AND Team2='" + fight.team2.returnName() + "' AND LeagueLevel=" + fight.level);
            stmt.setInt(1, 2);
            stmt.executeUpdate();
            stmt.close();
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Fight> getAllFights() {
        List<Fight> results = new ArrayList<Fight>();

        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM fight");

            while (rs.next()) {
                Fight f = new Fight(getTeamByName(rs.getObject("Team1").toString(), rs.getObject("Tournament").toString(), false),
                        getTeamByName(rs.getObject("Team2").toString(), rs.getObject("Tournament").toString(), false),
                        getTournamentByName(rs.getObject("Tournament").toString(), false),
                        rs.getInt("FightArea"), rs.getInt("Winner"), rs.getInt("LeagueLevel"),
                        rs.getInt("ID"));
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
            rs.close();
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return results;
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
        boolean error = false;
        int fightID = f.returnDatabaseID();
        try {
            //Obtain the ID of the fight..
            if (fightID < 0) {
                fightID = obtainFightID(f);
            }
            //Delete the duel if exist previously.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player);
            s.close();

            //Add the new duel.
            s = connection.createStatement();
            s.executeUpdate("INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + player + ",'" + d.hitsFromCompetitorA.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorA.get(1).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(0).getAbbreviature() + "','" + d.hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + d.faultsCompetitorA + "," + d.faultsCompetitorB + ")");
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeDuel", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        return !error;
    }

    @Override
    public boolean storeDuelsOfFight(Fight f) {
        boolean error = false;
        int fightID = f.returnDatabaseID();
        try {
            //Obtain the ID of the fight..
            if (fightID < 0) {
                fightID = obtainFightID(f);
            }
            //Delete the duel if exist previously.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM duel WHERE Fight=" + fightID);
            s.close();

            //Add the new duels.
            s = connection.createStatement();
            for (int i = 0; i < f.duels.size(); i++) {
                s.executeUpdate("INSERT INTO duel (Fight, OrderPlayer, PointPlayer1A, PointPlayer1B, PointPlayer2A, PointPlayer2B, FaultsPlayer1, FaultsPlayer2) VALUES (" + fightID + "," + i + ",'" + f.duels.get(i).hitsFromCompetitorA.get(0).getAbbreviature() + "','" + f.duels.get(i).hitsFromCompetitorA.get(1).getAbbreviature() + "','" + f.duels.get(i).hitsFromCompetitorB.get(0).getAbbreviature() + "','" + f.duels.get(i).hitsFromCompetitorB.get(1).getAbbreviature() + "'" + "," + f.duels.get(i).faultsCompetitorA + "," + f.duels.get(i).faultsCompetitorB + ")");
            }
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeDuel", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        return !error;
    }

    @Override
    public List<Duel> getDuelsOfFight(Fight f) {
        int fightID = f.returnDatabaseID();
        if (fightID < 0) {
            fightID = obtainFightID(f);
        }

        Statement s;

        Duel d = null;
        List<Duel> results = new ArrayList<Duel>();
        try {
            s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM duel WHERE Fight=" + fightID);
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
            rs.close();
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return results;
    }

    @Override
    public Duel getDuel(Fight f, int player) {
        Statement s;
        int fightID = f.returnDatabaseID();
        if (fightID < 0) {
            fightID = obtainFightID(f);
        }


        Duel d = null;
        try {
            s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM duel WHERE Fight=" + fightID + " AND OrderPlayer=" + player);
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
            rs.close();
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }

        return d;
    }

    @Override
    public List<Duel> getDuelsOfTournament(String championship) {
        Statement s;
        List<Duel> results = new ArrayList<Duel>();

        List<Fight> fights = searchFightsByTournamentName(championship);
        for (int i = 0; i < fights.size(); i++) {
            results.addAll(getDuelsOfFight(fights.get(i)));
        }

        return results;
    }

    @Override
    public List<Duel> getDuelsOfcompetitor(String competitorID, boolean teamRight) {
        Statement s;
        List<Duel> results = new ArrayList<Duel>();
        Duel d = null;
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }

        return results;
    }

    @Override
    public List<Duel> getAllDuels() {
        Statement s;
        List<Duel> results = new ArrayList<Duel>();
        Duel d = null;
        try {
            s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM duel");
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
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
    public boolean storeUndraw(String championship, String team, int order, int group) {
        boolean error = false;
        try {
            //Delete the undraw if exist previously.
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM undraw WHERE Championship='" + championship + "' AND Team='" + team + "'  AND UndrawGroup=" + group);
            s.close();

            //Add the new undraw.
            s = connection.createStatement();
            s.executeUpdate("INSERT INTO undraw (Championship, Team, Player, UndrawGroup) VALUES ('" + championship + "', '" + team + "', " + order + ", " + group + ")");
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeUndraw", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        return !error;
    }

    @Override
    public List<Undraw> getAllUndraws() {
        String query = "SELECT * FROM undraw ";

        List<Undraw> results = new ArrayList<Undraw>();

        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Undraw u = new Undraw(rs.getObject("Championship").toString(), (Integer) rs.getObject("UndrawGroup"), rs.getObject("Team").toString(), (Integer) rs.getObject("Player"));
                results.add(u);
            }
            rs.close();
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return results;
    }

    @Override
    public boolean storeAllUndraws(List<Undraw> undraws) {
        boolean error = false;
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM undraw");
            for (int i = 0; i < undraws.size(); i++) {
                s.executeUpdate("INSERT INTO undraw (Championship, UndrawGroup, Team, Player) VALUES ('"
                        + undraws.get(i).getTournament() + "'," + undraws.get(i).getGroup() + ",'" + undraws.get(i).getWinnerTeam() + "'," + undraws.get(i).getPlayer() + ")");
            }
            s.close();
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            Log.storeLog("fightStored", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, undraws.get(0).getTournament() + ": " + undraws.get(0).getWinnerTeam());
        }
        return !error;
    }

    @Override
    public String getWinnerInUndraws(String championship, int group, List<Team> teams) {
        String teamWinner = null;
        try {
            Statement s = connection.createStatement();
            String query = "SELECT * FROM undraw WHERE Championship='" + championship + "' AND UndrawGroup=" + group;
            ResultSet rs = s.executeQuery(query);
            if (rs.next()) {
                teamWinner = rs.getObject("Team").toString();
            }
            rs.close();
            s.close();

        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return teamWinner;
    }

    @Override
    public int getValueWinnerInUndraws(String championship, String team) {
        int value = 0;
        try {
            Statement s = connection.createStatement();
            String query = "SELECT * FROM undraw WHERE Championship='" + championship + "' AND Team='" + team + "'";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                value++;
            }
            rs.close();
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return value;
    }

    @Override
    public int getValueWinnerInUndrawInGroup(String championship, int group, String team) {
        int value = 0;
        try {
            Statement s = connection.createStatement();
            String query = "SELECT * FROM undraw WHERE Championship='" + championship + "' AND UndrawGroup=" + group + " AND Team='" + team + "'";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                value++;
            }
            rs.close();
            s.close();
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return value;
    }

    @Override
    public void deleteDrawsOfTournament(String championship) {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM undraw WHERE Championship='" + championship + "'");
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    public void deleteDrawsOfGroupOfTournament(String championship, int group) {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("DELETE FROM undraw WHERE Championship='" + championship + "' AND UndrawGroup=" + group);
            s.close();
        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    public List<String> getUndrawMySQLCommands() {
        List<String> commands = new ArrayList<String>();
        try {
            Statement s = connection.createStatement();
            String query = "SELECT * FROM undraw ORDER BY Championship";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                String command = "INSERT INTO `undraw` VALUES('" + rs.getObject("Championship").toString() + "',"
                        + rs.getInt("UndrawGroup") + ",'" + rs.getObject("Team").toString() + "',"
                        + rs.getInt("Player")
                        + ");\n";
                commands.add(command);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
        } catch (NullPointerException npe) {
            MessageManager.errorMessage("noRunningDatabase", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        return commands;
    }

    protected abstract boolean showSQLError(int numberError);
}
