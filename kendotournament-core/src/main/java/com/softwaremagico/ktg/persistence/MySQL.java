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
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.files.Path;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL extends SQL {

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
    public boolean connect(String password, String user, String database, String server, boolean verbose, boolean retry) throws CommunicationsException, SQLException {
        boolean error = false;
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (Exception e) {
            KendoLog.severe(this.getClass().getName(), "Mysql driver for Java is not installed. Check your configuration.");
            error = true;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database + "?allowMultiQueries=true", user, password);
            //MysqlDataSource dataSource = new MysqlDataSource();
            // dataSource.setUser(tmp_user);
            //dataSource.setPassword(tmp_password);
            //dataSource.setServerName(tmp_server);

            //connection = dataSource.getConnection();
            if (!isDatabaseInstalledCorrectly()) {
                installDatabase(password, user, server, database);
                return connect(password, user, database, server, verbose, false);
            }
        } catch (SQLException ex) {
            //If server not started (but assume that is installed) start it.
            if (ex.getErrorCode() == 0) {
                if (retry && DatabaseConnection.getInstance().isLocallyConnected()) {
                    startDatabase();
                    return connect(password, user, database, server, verbose, false);
                } else {
                    error = true;
                }
            } else if (ex.getErrorCode() == 1049) { //Server started, but no database found.
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
            //commands = new String[]{"/etc/init.d/mysql", "start"};
            commands = new String[]{"/usr/bin/service", "mysql", "start"};
        } else if (soName.contains("Windows") || soName.contains("windows") || soName.contains("vista") || soName.contains("Vista")) {
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
     * @param tmp_password
     * @param tmp_user
     * @param tmp_server
     */
    @Override
    void installDatabase(String tmp_password, String tmp_user, String tmp_server, String tmp_database) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + tmp_server + ":" + port, tmp_user, tmp_password);
            // MysqlDataSource dataSource = new MysqlDataSource();
            // dataSource.setUser(tmp_user);
            // dataSource.setPassword(tmp_password);
            // dataSource.setServerName(tmp_server);

            // connection = dataSource.getConnection();
            try (PreparedStatement sm = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + tmp_database)) {
                sm.executeUpdate();
            }

            executeScript(Path.returnDatabaseSchemaPath() + File.separator + "kendotournament_empty.sql");

        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
    }

    @Override
    boolean isDatabaseInstalledCorrectly() {
        int count = 0;
        try {
            java.sql.DatabaseMetaData md = connection.getMetaData();
            try (ResultSet rs = md.getTables(null, null, "%", null)) {
                while (rs.next()) {
                    String catalogo = rs.getString(1);
                    String tabla = rs.getString(3);
                    count++;
                }
            }
        } catch (SQLException | NullPointerException ex) {
            return false;
        } 
        if (count > 6) {
            return true;
        }
        return false;
    }

    @Override
    protected void storeBinaryStream(PreparedStatement stmt, int index, InputStream input, int size) throws SQLException {
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
