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
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.softwaremagico.ktg.core.KendoLog;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL extends SQL {

    private static final String MYSQL_TABLE_DEFINITION = " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    private static final Integer MYSQL_MAX_INT_LENGTH = 10;
    private static final Integer MAX_ELEMENTS_IN_QUERY = 10;
    public int port = 3306;

    public MySQL() {
    }

    /**
     * *******************************************************************
     *
     * CONNECTION
     *
     ********************************************************************
     */
    /**
     * Connect to the database
     *
     * @param password database password.
     * @param user user database.
     * @param database satabase schema.
     * @param server server IP
     * @param verbose show error messages.
     * @param retry do another try if can solve the SQL problem.
     * @return true if the connection is ok.
     */
    @Override
    public boolean connect(String password, String user, String database, String server, boolean verbose, boolean retry)
            throws CommunicationsException, SQLException {
        boolean error = false;
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (Exception e) {
            KendoLog.severe(this.getClass().getName(),
                    "Mysql driver for Java is not installed. Check your configuration.");
            error = true;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database
                    + "?allowMultiQueries=true", user, password);
            // MysqlDataSource dataSource = new MysqlDataSource();
            // dataSource.setUser(user);
            // dataSource.setPassword(password);
            // dataSource.setServerName(server);

            // connection = dataSource.getConnection();
            if (!isDatabaseInstalledCorrectly()) {
                if (retry) {
                    //Close connection for entering in to the recursivity. 
                    connection.close();
                    installDatabase(password, user, server, database);
                    return connect(password, user, database, server, verbose, false);
                } else {
                    KendoLog.severe("MySQL", "Database not installed correctly.");
                    return false;
                }
            }
        } catch (SQLException ex) {
            // If server is not started (but assume that it is installed) start it.
            if (ex.getErrorCode() == 0) {
                if (retry && DatabaseConnection.getInstance().isLocallyConnected()) {
                    startDatabase();
                    return connect(password, user, database, server, verbose, false);
                } else {
                    error = true;
                }
            } else if (ex.getErrorCode() == 1049) { // Server started, but no
                // database found.
                if (retry && DatabaseConnection.getInstance().isLocallyConnected()) {
                    installDatabase(password, user, server, database);
                    return connect(password, user, database, server, verbose, false);
                } else {
                    error = true;
                }
            } else {
                throw ex;
            }
        }
        return !error;
    }

    @Override
    public void disconnectDatabase() {
        try {
            connection.close();
        } catch (NullPointerException | SQLException npe) {
        }
    }

    @Override
    void startDatabase() {
        String[] commands = new String[]{""};
        String soName = System.getProperty("os.name");
        if (soName.contains("Linux") || soName.contains("linux")) {
            // commands = new String[]{"/etc/init.d/mysql", "start"};
            commands = new String[]{"/usr/bin/service", "mysql", "start"};
        } else if (soName.contains("Windows") || soName.contains("windows") || soName.contains("vista")
                || soName.contains("Vista")) {
            commands = new String[]{"sc", "start", "mysql"};
        }
        executeCommand(commands);
    }

    private void executeCommand(String[] commands) {
        showCommand(commands);
        try {
            Process child = Runtime.getRuntime().exec(commands);
            showCommandOutput(child);
        } catch (IOException ex1) {
            KendoLog.errorMessage(this.getClass().getName(), ex1);
        }
    }

    @Override
    public boolean onlyLocalConnection() {
        return false;
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
     * Install empty database.
     *
     * @param password
     * @param user
     * @param server
     */
    @Override
    protected void installDatabase(String password, String user, String server, String database) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port, user, password);
            try (PreparedStatement sm = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database)) {
                sm.executeUpdate();
            }

            createTableClub();
            createTableTournament();
            createTableCompetitor();
            createTableRole();
            createTableTeam();
            createTableFight();
            createTableDuel();
            createTableUndraw();
            createTableCustomLink();

        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
    }

    private void createTableClub() throws SQLException {
        String sqlQuery = "CREATE TABLE \"club\" (" + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Country\" varchar(" + MAX_COUNTRY_LENGTH + ") NOT NULL," + "\"Representative\" varchar(" + MAX_ID_LENGTH + ") DEFAULT NULL,"
                + "\"Mail\" varchar(" + MAX_EMAIL_LENGTH + ") DEFAULT NULL," + "\"Phone\" varchar(" + MAX_PHONE_LENGTH + ") DEFAULT NULL,"
                + "\"City\" varchar(" + MAX_CITY_LENGTH + ") DEFAULT NULL," + "\"Web\" varchar(" + MAX_WEB_LENGTH + ") DEFAULT NULL,"
                + "\"Address\" varchar(" + MAX_ADDRESS_LENGTH + ") DEFAULT NULL," + "PRIMARY KEY (Name), KEY \"Representative\" (\"Representative\")" + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableTournament() throws SQLException {
        String sqlQuery = "CREATE TABLE \"tournament\" (" + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL," + "\"Banner\" mediumblob,"
                + "\"Size\" double NOT NULL DEFAULT 0," + "\"FightingAreas\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT '1',"
                + "\"PassingTeams\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT 1," + "\"TeamSize\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT '3',"
                + "\"Type\" varchar(" + MAX_TOURNAMENT_TYPE_LENGTH + ") NOT NULL DEFAULT 'simple'," + "\"ScoreWin\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT '1',"
                + "\"ScoreDraw\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT 0,"
                + "\"ScoreType\" varchar(" + MAX_SCORE_TYPE_LENGTH + ") NOT NULL DEFAULT 'Classic'," + "\"Diploma\" mediumblob,"
                + "\"Accreditation\" mediumblob," + "\"DiplomaSize\" double NOT NULL DEFAULT '0',"
                + "\"AccreditationSize\" double NOT NULL," + "PRIMARY KEY (Name)" + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
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
                + "KEY \"ClubBelongKey\" (\"Club\"),"
                + "CONSTRAINT \"ClubBelong\" FOREIGN KEY (\"Club\") REFERENCES \"club\" (\"Name\") ON DELETE SET NULL ON UPDATE CASCADE"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableRole() throws SQLException {
        String sqlQuery = "CREATE TABLE \"role\" ("
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Competitor\" varchar(" + MAX_ID_LENGTH + ") NOT NULL DEFAULT '0000000Z',"
                + "\"Role\" varchar(" + MAX_ROLE_NAME_LENGTH + ") NOT NULL,"
                + "\"ImpressCardOrder\" int(" + MYSQL_MAX_INT_LENGTH + ") DEFAULT 0,"
                + "\"ImpressCardPrinted\" int(" + MYSQL_MAX_INT_LENGTH + ") DEFAULT 0,"
                + "\"DiplomaPrinted\" int(" + MYSQL_MAX_INT_LENGTH + ") DEFAULT 0,"
                + "PRIMARY KEY (Competitor, Tournament),"
                + "KEY \"TournamentRole\" (\"Tournament\"),"
                + "KEY \"CompetitorRole\" (\"Competitor\"),"
                + "CONSTRAINT \"CompetitorRoleC\" FOREIGN KEY (\"Competitor\") REFERENCES \"competitor\" (\"ID\") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "CONSTRAINT \"TournamentRoleC\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableTeam() throws SQLException {
        String sqlQuery = "CREATE TABLE \"team\" ("
                + "\"Name\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Member\" varchar(" + MAX_ID_LENGTH + ") DEFAULT NULL,"
                + "\"Position\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"FightOfTournament\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT 0,"
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"LeagueGroup\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT -1,"
                + "PRIMARY KEY (Name, Position, FightOfTournament, Tournament),"
                + "KEY \"CompetitorTeamIndex\" (\"Member\"),"
                + "KEY \"TournamentTeamIndex\" (\"Tournament\"),"
                + "KEY \"LevelTeamIndex\" (\"FightOfTournament\"),"
                + "KEY \"TCLTeamIndex\" (\"Name\",\"FightOfTournament\",\"Tournament\"),"
                + "CONSTRAINT \"Tournament\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableFight() throws SQLException {
        String sqlQuery = "CREATE TABLE \"fight\" ("
                + "\"Team1\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Team2\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"GroupIndex\" int(" + MYSQL_MAX_INT_LENGTH + ") unsigned NOT NULL,"
                + "\"TournamentGroup\" int(" + MYSQL_MAX_INT_LENGTH + ") unsigned NOT NULL,"
                + "\"TournamentLevel\" int(" + MYSQL_MAX_INT_LENGTH + ") unsigned NOT NULL DEFAULT 0, "
                + "\"FightArea\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT 1,"
                + "\"Winner\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL DEFAULT 3,"
                + "PRIMARY KEY (Team1,Team2,Tournament,GroupIndex,TournamentLevel, TournamentGroup),"
                + "KEY \"TournamentFightIndex\" (\"Tournament\"),"
                + "KEY \"TCL1FightIndex\" (\"Team1\",\"TournamentLevel\",\"Tournament\"),"
                + "KEY \"Team2Fight\" (\"Team2\",\"TournamentLevel\",\"Tournament\"),"
                + "CONSTRAINT \"TournamentFight\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableDuel() throws SQLException {
        String sqlQuery = "CREATE TABLE \"duel\" ("
                + "\"Team1\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Team2\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"GroupIndex\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"TournamentGroup\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"TournamentLevel\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"MemberOrder\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"PointPlayer1A\" char(1) NOT NULL,"
                + "\"PointPlayer1B\" char(1) NOT NULL,"
                + "\"PointPlayer2A\" char(1) NOT NULL,"
                + "\"PointPlayer2B\" char(1) NOT NULL,"
                + "\"FaultsPlayer1\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"FaultsPlayer2\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "PRIMARY KEY (Team1,Team2,Tournament,GroupIndex,TournamentLevel,MemberOrder,TournamentGroup),"
                + "KEY \"fk_duel_1\" (\"Team1\",\"Team2\",\"Tournament\",\"GroupIndex\",\"TournamentLevel\"),"
                + "KEY \"fight_FK\" (\"Team1\",\"Team2\",\"Tournament\",\"GroupIndex\",\"TournamentLevel\"),"
                + "KEY \"Fight_K\" (\"Team1\",\"Team2\",\"Tournament\",\"GroupIndex\",\"TournamentLevel\",\"TournamentGroup\"),  "
                + "CONSTRAINT \"Fight\"  FOREIGN KEY (\"Team1\", \"Team2\", \"Tournament\", \"GroupIndex\", \"TournamentLevel\", \"TournamentGroup\") REFERENCES \"fight\" (\"Team1\", \"Team2\", \"Tournament\", \"GroupIndex\", \"TournamentLevel\", \"TournamentGroup\") ON DELETE NO ACTION ON UPDATE NO ACTION"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableUndraw() throws SQLException {
        String sqlQuery = "CREATE TABLE \"undraw\" ("
                + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL , "
                + "\"Points\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL  DEFAULT 1, "
                + "\"TournamentLevel\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL , "
                + "\"Team\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL , "
                + "\"Player\" int(" + MYSQL_MAX_INT_LENGTH + "), "
                + "\"TournamentGroup\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL  DEFAULT 0, "
                + "PRIMARY KEY (\"Tournament\", \"TournamentLevel\", \"Team\", \"TournamentGroup\"),"
                + "KEY \"Team\" (\"Team\"),"
                + "CONSTRAINT \"TeamDraw\" FOREIGN KEY (\"Team\") REFERENCES \"team\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "CONSTRAINT \"TournamentUndraw\" FOREIGN KEY (\"Tournament\") REFERENCES \"tournament\" (\"Name\") ON DELETE CASCADE ON UPDATE CASCADE"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    private void createTableCustomLink() throws SQLException {
        String sqlQuery = "CREATE TABLE \"customlinks\" (" + "\"Tournament\" varchar(" + MAX_NAME_LENGTH + ") NOT NULL,"
                + "\"SourceGroup\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL," + "\"AddressGroup\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL,"
                + "\"WinnerOrder\" int(" + MYSQL_MAX_INT_LENGTH + ") NOT NULL," + "PRIMARY KEY (Tournament,SourceGroup,AddressGroup,WinnerOrder)"
                + ") " + MYSQL_TABLE_DEFINITION;
        createTable(sqlQuery);
    }

    @Override
    protected boolean isDatabaseInstalledCorrectly() {
        int count = 0;
        try {
            java.sql.DatabaseMetaData md = connection.getMetaData();
            try (ResultSet rs = md.getTables(null, null, "%", null)) {
                while (rs.next()) {
                    String catalogo = rs.getString(1);
                    String tabla = rs.getString(3);
                    if (catalogo != null && tabla != null) {
                        count++;
                    }
                }
            }
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size)
            throws SQLException {
        stmt.setBinaryStream(index, input, size);
    }

    @Override
    protected InputStream getBinaryStream(ResultSet rs, String column) throws SQLException {
        return rs.getBinaryStream(column);
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
            case 1045:
                return trans.getTranslatedText("deniedUser");
            case 1049:
                return trans.getTranslatedText("noDatabase");
            case 1062:
                return trans.getTranslatedText("repeatedCompetitor");
            case 1054:
                return trans.getTranslatedText("noDatabase");
            case 1146:
                return trans.getTranslatedText("corruptedDatabase");
            case 1130:
                return trans.getTranslatedText("noAccessUser");
            case 1044:
                return trans.getTranslatedText("noUserPrivileges");
            case 0:
                return trans.getTranslatedText("noDatabase");
        }
        KendoLog.errorMessage(this.getClass().getName(), exception);
        return trans.getTranslatedText("unknownDatabaseError");
    }
}
