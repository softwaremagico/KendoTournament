/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.database;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Log;
import com.softwaremagico.ktg.MessageManager;

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
            MessageManager.errorMessage("Mysql driver for Java is not installed. Check your configuration.", "Mysql", KendoTournamentGenerator.getInstance().getLogOption());
            error = true;
        }

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:database/" + tmp_database + ".sqlite");

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
                MessageManager.customMessage("databaseConnected", this.getClass().getName(), KendoTournamentGenerator.getInstance().language, tmp_server, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                if (KendoTournamentGenerator.getInstance().getLogOption()) {
                    Log.storeLog("databaseConnected", this.getClass().getName(), KendoTournamentGenerator.getInstance().language);
                }
            }
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
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT count(name) FROM sqlite_master WHERE type='table'");
            rs.next();
            int tables = rs.getInt(1);
            rs.close();
            s.close();
            if (tables > 6) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
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
            
            try{
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            }catch(NullPointerException npe){
                
            }

            buffer.flush();

            stmt.setBytes(index, buffer.toByteArray());
        } catch (IOException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
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
                MessageManager.errorMessage("deniedUser", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 1049:
                MessageManager.errorMessage("noDatabase", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 1062:
                MessageManager.errorMessage("repeatedCompetitor", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 1054:
                MessageManager.errorMessage("unknownColumn", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 1146:
                MessageManager.errorMessage("corruptedDatabase", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 1130:
                MessageManager.errorMessage("noAccessUser", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 1044:
                MessageManager.errorMessage("noUserPrivileges", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                return true;
            case 0:
                MessageManager.errorMessage("noDatabase", "SQLite", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
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
