package com.softwaremagico.ktg.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.log.KendoLog;

public class SQLite extends SQL {

    private static final Integer MAX_ELEMENTS_IN_QUERY = 1;
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
     * @param password
     * @param user
     * @param database
     * @param server //Not used in SQLite.
     * @param verbose
     * @param retry
     * @return
     */
    @Override
    public boolean connect(String password, String user, String database, String server,
            boolean verbose, boolean retry) throws CommunicationsException, SQLException {
        boolean error = false;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            KendoLog.severe(this.getClass().getName(),
                    "Sqlite driver for Java is not installed. Check your configuration.");
            error = true;
        }

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + Path.getPathDatabaseFolderInHome()
                    + File.separator + database + "." + defaultSQLiteExtension);

            if (!isDatabaseInstalledCorrectly()) {
                try {
                    installDatabase(database);
                } catch (Exception ex) {
                    KendoLog.severe(this.getClass().getName(), "Database not installed correctly:\n" + ex);
                }
                if (retry) {
                    return connect(password, user, database, server, verbose, false);
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            showSqlError(ex);
            error = true;
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
        // It is not necesary.
    }

    @Override
    public boolean onlyLocalConnection() {
        return true;
    }

    @Override
    protected int getMaxElementsInQuery() {
        return MAX_ELEMENTS_IN_QUERY;
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
    public void installDatabase(String database)
            throws Exception {
        createTableTournament();
        createTableClub();
        createTableCompetitor();
        createTableRole();
        createTableTeam();
        createTableFight();
        createTableDuel();
        createTableUndraw();
        createTableCustomLink();
    }

    private void createTableClub() throws SQLException {
        String sqlQuery = "CREATE TABLE \"club\" (" + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Country\" varchar(" + MAX_COUNTRY_LENGTH + ") NOT NULL," + "\"Representative\" varchar(" + MAX_ID_LENGTH + ") DEFAULT NULL,"
                + "\"Mail\" varchar(" + MAX_EMAIL_LENGTH + ") DEFAULT NULL," + "\"Phone\" varchar(" + MAX_PHONE_LENGTH + ") DEFAULT NULL,"
                + "\"City\" varchar(" + MAX_CITY_LENGTH + ") DEFAULT NULL," + "\"Web\" varchar(" + MAX_WEB_LENGTH + ") DEFAULT NULL,"
                + "\"Address\" varchar(" + MAX_ADDRESS_LENGTH + ") DEFAULT NULL," + "PRIMARY KEY (Name)" + ")";
        executeQuery(sqlQuery);
    }

    private void createTableTournament() throws SQLException {
        String sqlQuery = "CREATE TABLE \"tournament\" (" + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL," + "\"Banner\" mediumblob,"
                + "\"Size\" double NOT NULL DEFAULT 0," + "\"FightingAreas\" integer NOT NULL DEFAULT '1',"
                + "\"PassingTeams\" integer NOT NULL DEFAULT 1," + "\"TeamSize\" integer NOT NULL DEFAULT '3',"
                + "\"Type\" varchar(" + MAX_TOURNAMENT_TYPE_LENGTH + ") NOT NULL DEFAULT 'simple'," + "\"ScoreWin\" integer NOT NULL DEFAULT '1',"
                + "\"ScoreDraw\" integer NOT NULL DEFAULT 0,"
                + "\"ScoreType\" varchar(" + MAX_SCORE_TYPE_LENGTH + ") NOT NULL DEFAULT 'Classic'," + "\"Diploma\" mediumblob,"
                + "\"Accreditation\" mediumblob," + "\"DiplomaSize\" double NOT NULL DEFAULT '0',"
                + "\"AccreditationSize\" double NOT NULL,"
                + "\"UsingMultipleComputers\" integer DEFAULT 0,"
                + "\"DrawResolution\" varchar(10) DEFAULT 'oldest',"
                + "PRIMARY KEY (Name)" + ")";
        executeQuery(sqlQuery);
    }

    private void createTableCompetitor() throws SQLException {
        String sqlQuery = "CREATE TABLE \"competitor\" ("
                + "\"ID\" varchar(" + MAX_ID_LENGTH + ") NOT NULL DEFAULT '0000000Z',"
                + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Surname\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Club\" varchar(" + MAX_NAME_LENGTH + ") DEFAULT NULL,"
                + "\"Photo\" mediumblob,"
                + "\"PhotoSize\" double NOT NULL DEFAULT 0,"
                + "PRIMARY KEY (ID),"
                + "CONSTRAINT \"ClubBelong\" FOREIGN KEY (\"Club\") REFERENCES \"club\" (\"Name\") ON DELETE SET NULL ON UPDATE CASCADE"
                + ")";
        executeQuery(sqlQuery);
    }

    private void createTableRole() throws SQLException {
        String sqlQuery = "CREATE TABLE \"role\" ("
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Competitor\" varchar(" + MAX_ID_LENGTH + ") NOT NULL DEFAULT '0000000Z',"
                + "\"Role\" varchar(" + MAX_ROLE_NAME_LENGTH + ") NOT NULL,"
                + "\"ImpressCardOrder\" integer DEFAULT 0,"
                + "\"ImpressCardPrinted\" integer DEFAULT 0,"
                + "\"DiplomaPrinted\" integer DEFAULT 0,"
                + "PRIMARY KEY (Competitor, Tournament),"
                + "CONSTRAINT \"CompetitorRoleC\" FOREIGN KEY (\"Competitor\") REFERENCES \"competitor\" (\"ID\") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "CONSTRAINT \"TournamentRoleC\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        executeQuery(sqlQuery);
    }

    private void createTableTeam() throws SQLException {
        String sqlQuery = "CREATE TABLE \"team\" ("
                + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Member\" varchar(" + MAX_ID_LENGTH + ") DEFAULT NULL,"
                + "\"Position\" integer NOT NULL,"
                + "\"FightOfTournament\" integer NOT NULL DEFAULT 0,"
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"LeagueGroup\" integer NOT NULL DEFAULT -1,"
                + "PRIMARY KEY (Name, Position, FightOfTournament, Tournament),"
                + "CONSTRAINT \"Tournament\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        executeQuery(sqlQuery);
    }

    private void createTableFight() throws SQLException {
        String sqlQuery = "CREATE TABLE \"fight\" ("
                + "\"Team1\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Team2\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"GroupIndex\" integer unsigned NOT NULL,"
                + "\"TournamentGroup\" integer unsigned NOT NULL,"
                + "\"TournamentLevel\" integer unsigned NOT NULL DEFAULT 0, "
                + "\"FightArea\" integer NOT NULL DEFAULT 1,"
                + "\"Winner\" integer NOT NULL DEFAULT 3,"
                + "PRIMARY KEY (Team1,Team2,Tournament,GroupIndex,TournamentLevel, TournamentGroup),"
                + "CONSTRAINT \"TournamentFight\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        executeQuery(sqlQuery);
    }

    private void createTableDuel() throws SQLException {
        String sqlQuery = "CREATE TABLE \"duel\" ("
                + "\"Team1\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Team2\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"GroupIndex\" integer unsigned NOT NULL,"
                + "\"TournamentGroup\" integer unsigned NOT NULL,"
                + "\"TournamentLevel\" integer unsigned NOT NULL,"
                + "\"MemberOrder\" integer unsigned NOT NULL,"
                + "\"PointPlayer1A\" char(1) NOT NULL,"
                + "\"PointPlayer1B\" char(1) NOT NULL,"
                + "\"PointPlayer2A\" char(1) NOT NULL,"
                + "\"PointPlayer2B\" char(1) NOT NULL,"
                + "\"FaultsPlayer1\" integer NOT NULL,"
                + "\"FaultsPlayer2\" integer NOT NULL,"
                + "PRIMARY KEY (Team1,Team2,Tournament,GroupIndex,TournamentLevel,MemberOrder,TournamentGroup),"
                + "CONSTRAINT \"DuelOfFight\"  FOREIGN KEY (\"Team1\", \"Team2\", \"Tournament\", \"GroupIndex\", \"TournamentLevel\", \"TournamentGroup\") REFERENCES \"fight\" (\"Team1\", \"Team2\", \"Tournament\", \"GroupIndex\", \"TournamentLevel\", \"TournamentGroup\") ON DELETE NO ACTION ON UPDATE NO ACTION"
                + ")";
        executeQuery(sqlQuery);
    }

    private void createTableUndraw() throws SQLException {
        String sqlQuery = "CREATE TABLE \"undraw\" ("
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL , "
                + "\"Points\" integer NOT NULL  DEFAULT 1, "
                + "\"TournamentLevel\" integer NOT NULL , "
                + "\"Team\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL , "
                + "\"Player\" integer, "
                + "\"TournamentGroup\" integer NOT NULL  DEFAULT 0, "
                + "PRIMARY KEY (\"Tournament\", \"TournamentLevel\", \"Team\", \"TournamentGroup\"),"
                + "CONSTRAINT \"TeamDraw\" FOREIGN KEY (\"Team\") REFERENCES \"team\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "CONSTRAINT \"TournamentUndraw\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        executeQuery(sqlQuery);
    }

    private void createTableCustomLink() throws SQLException {
        String sqlQuery = "CREATE TABLE \"customlinks\" (" + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"SourceGroup\" integer NOT NULL," + "\"AddressGroup\" integer NOT NULL,"
                + "\"WinnerOrder\" integer NOT NULL," + "PRIMARY KEY (Tournament,SourceGroup,AddressGroup,WinnerOrder)"
                + ")";
        executeQuery(sqlQuery);
    }

    @Override
    boolean isDatabaseInstalledCorrectly() {
        // SELECT name FROM sqlite_master WHERE type='table' AND
        // name='table_name';
        try {
            int tables;
            try (Statement s = connection.createStatement();
                    ResultSet rs = s.executeQuery("SELECT count(name) FROM sqlite_master WHERE type='table'")) {
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
    protected void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size)
            throws SQLException {
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
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
    }

    @Override
    protected InputStream getBinaryStream(ResultSet rs, String column) throws SQLException {
        byte[] bytes = rs.getBytes(column);
        return new ByteArrayInputStream(bytes);
    }

    @Override
    protected boolean createColumnUsingMultipleComputers() {
        try {
            String sqlQuery = "ALTER TABLE \"tournament\" ADD COLUMN \"usingMultipleComputers\" integer DEFAULT 0;";
            executeQuery(sqlQuery);
            KendoLog.info(MySQL.class.getName(), "Column `usingMultipleComputers` added to table `tournament`.");
            return true;
        } catch (SQLException ex) {
            KendoLog.errorMessage(MySQL.class.getName(), ex);
        }
        return false;
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
    public String getSqlErrorMessage(SQLException exception) {
        int numberError = exception.getErrorCode();
        switch (numberError) {
            case 14:
            case 11:
            case 10:
                return trans.getTranslatedText("corruptedDatabase");
            case 23:
            case 3:
                return trans.getTranslatedText("deniedUser");
            case 8:
                return trans.getTranslatedText("noUserPrivileges");
            case 1:
                return trans.getTranslatedText("noDatabase");
        }
        KendoLog.errorMessage(this.getClass().getName(), exception);
        return trans.getTranslatedText("unknownDatabaseError") + ": " + exception.getMessage();

        // #define SQLITE_OK 0 /* Successful result */
        // #define SQLITE_INTERNAL 2 /* An internal logic error in SQLite */
        // #define SQLITE_ABORT 4 /* Callback routine requested an abort */
        // #define SQLITE_BUSY 5 /* The database file is locked */
        // #define SQLITE_LOCKED 6 /* A table in the database is locked */
        // #define SQLITE_NOMEM 7 /* A malloc() failed */
        // #define SQLITE_READONLY 8 /* Attempt to write a readonly database */
        // #define SQLITE_INTERRUPT 9 /* Operation terminated by
        // sqlite_interrupt() */
        // #define SQLITE_IOERR 10 /* Some kind of disk I/O error occurred */
        // #define SQLITE_CORRUPT 11 /* The database disk image is malformed */
        // #define SQLITE_NOTFOUND 12 /* (Internal Only) Table or record not
        // found */
        // #define SQLITE_FULL 13 /* Insertion failed because database is full
        // */
        // #define SQLITE_CANTOPEN 14 /* Unable to open the database file */
        // #define SQLITE_PROTOCOL 15 /* Database lock protocol error */
        // #define SQLITE_EMPTY 16 /* (Internal Only) Database table is empty */
        // #define SQLITE_SCHEMA 17 /* The database schema changed */
        // #define SQLITE_TOOBIG 18 /* Too much data for one row of a table */
        // #define SQLITE_CONSTRAINT 19 /* Abort due to constraint violation */
        // #define SQLITE_MISMATCH 20 /* Data type mismatch */
        // #define SQLITE_MISUSE 21 /* Library used incorrectly */
        // #define SQLITE_NOLFS 22 /* Uses OS features not supported on host */
        // #define SQLITE_AUTH 23 /* Authorization denied */
        // #define SQLITE_ROW 100 /* sqlite_step() has another row ready */
        // #define SQLITE_DONE 101 /* sqlite_step() has finished executing */
    }

    @Override
    protected String getBoolean(Boolean value) {
        return value ? 1 + "" : 0 + "";
    }
}
