/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Log;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.files.Path;
import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author LOCAL\jhortelano
 */
public class SQLite extends SQL {

    public SQLite() {
    }

    /**
     * *******************************************************************
     *
     * CONNECTION
     *
     ********************************************************************
     */
    /**
     * Connect to the SQLite file.
     *
     * @param tmp_password
     * @param tmp_user
     * @param tmp_database
     * @param tmp_server //Not used in SQLite.
     * @param verbose
     * @param retry
     * @return
     */
    @Override
    public boolean connect(String tmp_password, String tmp_user, String tmp_database, String tmp_server, boolean verbose, boolean retry) {
        boolean error = false;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            MessageManager.errorMessage("Sqlite driver for Java is not installed. Check your configuration.", "SQLite");
            error = true;
        }

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + Path.returnDatabasePath() + tmp_database + ".sqlite");

            if (!isDatabaseInstalledCorrectly(false)) {
                installDatabase(tmp_password, tmp_user, tmp_server, tmp_database);
                return connect(tmp_password, tmp_user, tmp_database, tmp_server, verbose, false);
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            error = true;
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage("databaseConnected", "SQLite", KendoTournamentGenerator.getInstance().language, "SQLite (" + tmp_server + ")", JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("databaseConnected", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
        }
        return !error;
    }

    @Override
    public void disconnect() throws SQLException {
        try {
            connection.close();
        } catch (NullPointerException npe) {
        }
    }

    @Override
    void startDatabase() {
        //It is not necesary. 
    }

    @Override
    public boolean onlyLocalConnection() {
        return true;
    }

    /**
     * *******************************************************************
     *
     * INSTALL
     *
     ********************************************************************
     */
    /**
     * If the SQL server has not installed the database, the program install a
     * default one.
     *
     * @param tmp_password
     * @param tmp_user
     * @param tmp_server
     */
    @Override
    void installDatabase(String tmp_password, String tmp_user, String tmp_server, String tmp_database) {
        try {
            copyFile(new File("database/kendotournament_empty.sqlite"), new File("database/" + tmp_database + ".sqlite"));
        } catch (IOException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();

            // previous code: destination.transferFrom(source, 0, source.size());
            // to avoid infinite loops, should be:
            long count = 0;
            long size = source.size();
            while (count < size) {
                count += destination.transferFrom(source, 0, size - count);
            }
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    @Override
    boolean isDatabaseInstalledCorrectly(boolean verbose) {
        //SELECT name FROM sqlite_master WHERE type='table' AND name='table_name';
        try {
            int tables;
            try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("SELECT count(name) FROM sqlite_master WHERE type='table'")) {
                rs.next();
                tables = rs.getInt(1);
            }
            if (tables > 6) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean updateDatabase(String path, boolean verbose) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size) throws SQLException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            try {
                while ((nRead = input.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
            } catch (NullPointerException npe) {
            }

            buffer.flush();

            stmt.setBytes(index, buffer.toByteArray());
        } catch (IOException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    protected InputStream getBinaryStream(ResultSet rs, String column) throws SQLException {
        byte[] bytes = rs.getBytes(column);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * *******************************************************************
     *
     * FIGHTS
     *
     ********************************************************************
     */
    /**
     * Delete fights must delete duels. MySQL use foreign key, but SQLite need
     * to delete one by one
     *
     * @param championship
     * @param level
     * @param verbose
     * @return
     */
    @Override
    public boolean deleteFightsOfLevelOfTournament(String championship, int level, boolean verbose) {
        boolean error;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
            }
            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    List<Fight> fightsOfTournament = searchFightsByTournamentNameLevelEqualOrGreater(championship, level);
                    for (Fight f : fightsOfTournament) {
                        deleteFight(f, false);
                    }
                }
                List<Integer> groups = KendoTournamentGenerator.getInstance().designedGroups.returnIndexOfGroupsOfLevelOrMore(level);
                for (int i = 0; i < groups.size(); i++) {
                    deleteDrawsOfGroupOfTournament(championship, i);
                }
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
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
    public boolean deleteFightsOfTournament(String championship, boolean verbose) {
        boolean error;
        boolean answer = false;
        try {
            if (verbose) {
                answer = MessageManager.questionMessage("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
            }
            if (answer || !verbose) {
                try (Statement s = connection.createStatement()) {
                    List<Fight> fightsOfTournament = searchFightsByTournamentName(championship);
                    if (fightsOfTournament.size() > 0) {
                        for (Fight f : fightsOfTournament) {
                            deleteFight(f, false);
                        }
                    }
                }
                deleteDrawsOfTournament(championship);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("storeFights", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
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
                answer = MessageManager.questionMessage("deleteOneFight", "Warning!", KendoTournamentGenerator.getInstance().language);
            }
            if (answer || !verbose) {
                deleteDuelsOfFight(fight);
                try (Statement s = connection.createStatement()) {
                    s.executeUpdate("DELETE FROM fight WHERE Tournament='" + fight.competition.name + "' AND Team1='" + fight.team1.returnName() + "' AND Team2='" + fight.team2.returnName() + "' AND LeagueLevel=" + fight.level);
                }
            }

        } catch (SQLException ex) {
            error = true;
            MessageManager.errorMessage("deleteFight", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }

        if (!error && answer) {
            if (verbose) {
                MessageManager.translatedMessage("fightDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fight.competition.name, JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("fightDeleted", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, fight.competition.name);
        }

        return (answer && !error);
    }

    /**
     * *******************************************************************
     *
     * ERRORS
     *
     ********************************************************************
     */
    /**
     * Translate a SQL error in a human readable error.
     *
     * @param numberError
     */
    @Override
    protected boolean showSQLError(int numberError) {
        System.out.println("Error: " + numberError);
        switch (numberError) {
            case 1045:
                MessageManager.errorMessage("deniedUser", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1049:
                MessageManager.errorMessage("noDatabase", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1062:
                MessageManager.errorMessage("repeatedCompetitor", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1054:
                MessageManager.errorMessage("unknownColumn", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1146:
                MessageManager.errorMessage("corruptedDatabase", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1130:
                MessageManager.errorMessage("noAccessUser", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1044:
                MessageManager.errorMessage("noUserPrivileges", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
            case 0:
                MessageManager.errorMessage("noDatabase", "SQLite", KendoTournamentGenerator.getInstance().language);
                return true;
        }

//#define SQLITE_OK           0   /* Successful result */
//#define SQLITE_ERROR        1   /* SQL error or missing database */
//#define SQLITE_INTERNAL     2   /* An internal logic error in SQLite */
//#define SQLITE_PERM         3   /* Access permission denied */
//#define SQLITE_ABORT        4   /* Callback routine requested an abort */
//#define SQLITE_BUSY         5   /* The database file is locked */
//#define SQLITE_LOCKED       6   /* A table in the database is locked */
//#define SQLITE_NOMEM        7   /* A malloc() failed */
//#define SQLITE_READONLY     8   /* Attempt to write a readonly database */
//#define SQLITE_INTERRUPT    9   /* Operation terminated by sqlite_interrupt() */
//#define SQLITE_IOERR       10   /* Some kind of disk I/O error occurred */
//#define SQLITE_CORRUPT     11   /* The database disk image is malformed */
//#define SQLITE_NOTFOUND    12   /* (Internal Only) Table or record not found */
//#define SQLITE_FULL        13   /* Insertion failed because database is full */
//#define SQLITE_CANTOPEN    14   /* Unable to open the database file */
//#define SQLITE_PROTOCOL    15   /* Database lock protocol error */
//#define SQLITE_EMPTY       16   /* (Internal Only) Database table is empty */
//#define SQLITE_SCHEMA      17   /* The database schema changed */
//#define SQLITE_TOOBIG      18   /* Too much data for one row of a table */
//#define SQLITE_CONSTRAINT  19   /* Abort due to constraint violation */
//#define SQLITE_MISMATCH    20   /* Data type mismatch */
//#define SQLITE_MISUSE      21   /* Library used incorrectly */
//#define SQLITE_NOLFS       22   /* Uses OS features not supported on host */
//#define SQLITE_AUTH        23   /* Authorization denied */
//#define SQLITE_ROW         100  /* sqlite_step() has another row ready */
//#define SQLITE_DONE        101  /* sqlite_step() has finished executing */

        return false;
    }
}
