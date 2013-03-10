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
import com.softwaremagico.ktg.files.Path;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author LOCAL\jhortelano
 */
public class SQLite extends SQL {

    public static final String defaultDatabaseName = "kendotournament_empty";
    public static final String defaultSQLiteExtension = "sqlite";

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
            MessageManager.basicErrorMessage(this.getClass().getName(), "Sqlite driver for Java is not installed. Check your configuration.", "SQLite");
            error = true;
        }

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + Path.getPathDatabaseFolderInHome() + File.separator + tmp_database + "." + defaultSQLiteExtension);

            if (!isDatabaseInstalledCorrectly()) {
                installDatabase(tmp_password, tmp_user, tmp_server, tmp_database);
                if (retry) {
                    return connect(tmp_password, tmp_user, tmp_database, tmp_server, verbose, false);
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            showSQLError(ex.getErrorCode());
            error = true;
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage(this.getClass().getName(), "databaseConnected", "SQLite", "SQLite", JOptionPane.INFORMATION_MESSAGE);
            }
            KendoLog.info(this.getClass().getName(), "Connected to Database!");
        }
        return !error;
    }

    @Override
    public void disconnectDatabase() {
        try {
            connection.close();
        } catch (SQLException | NullPointerException npe) {
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
            copyFile(new File(Path.returnDatabaseSchemaPath() + File.separator + defaultDatabaseName + "." + defaultSQLiteExtension), new File(Path.getPathDatabaseFolderInHome() + File.separator + tmp_database + "." + defaultSQLiteExtension));
        } catch (IOException ex) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
            destFile.setExecutable(true);
            destFile.setWritable(true);
            destFile.setReadable(true);
        }

        InputStream source = null;
        OutputStream destination = null;
        try {
            source = new FileInputStream(sourceFile);
            destination = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes 
            while ((length = source.read(buffer)) > 0) {
                destination.write(buffer, 0, length);
            }
        } catch (Exception e) {
            KendoTournamentGenerator.showErrorInformation(SQLite.class.getName(), e);
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
    boolean isDatabaseInstalledCorrectly() {
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
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
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
                MessageManager.errorMessage(this.getClass().getName(), "deniedUser", "SQLite");
                return true;
            case 1049:
                MessageManager.errorMessage(this.getClass().getName(), "noDatabase", "SQLite");
                return true;
            case 1062:
                MessageManager.errorMessage(this.getClass().getName(), "repeatedCompetitor", "SQLite");
                return true;
            case 1054:
                MessageManager.errorMessage(this.getClass().getName(), "unknownColumn", "SQLite");
                return true;
            case 1146:
                MessageManager.errorMessage(this.getClass().getName(), "corruptedDatabase", "SQLite");
                return true;
            case 1130:
                MessageManager.errorMessage(this.getClass().getName(), "noAccessUser", "SQLite");
                return true;
            case 1044:
                MessageManager.errorMessage(this.getClass().getName(), "noUserPrivileges", "SQLite");
                return true;
            case 0:
                MessageManager.errorMessage(this.getClass().getName(), "noDatabase", "SQLite");
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
