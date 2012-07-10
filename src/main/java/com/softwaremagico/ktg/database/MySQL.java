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
 *  Created on 09-dic-2008.
 */
package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Log;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
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
     * @param tmp_password database password.
     * @param tmp_user user database.
     * @param tmp_database satabase schema.
     * @param tmp_server server IP
     * @param verbose show error messages.
     * @param retry do another try if can solve the SQL problem.
     * @return true if the connection is ok.
     */
    @Override
    public boolean connect(String tmp_password, String tmp_user, String tmp_database, String tmp_server, boolean verbose, boolean retry) {
        boolean error = false;
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (Exception e) {
            MessageManager.errorMessage("Mysql driver for Java is not installed. Check your configuration.", "Mysql");
            error = true;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + tmp_server + ":" + port + "/" + tmp_database, tmp_user, tmp_password);
            if (!isDatabaseInstalledCorrectly(false)) {
                installDatabase(tmp_password, tmp_user, tmp_server, tmp_database);
                return connect(tmp_password, tmp_user, tmp_database, tmp_server, verbose, false);
            }
        } catch (SQLException ex) {
            //If server not started (but assume that is installed) start it.
            if (ex.getErrorCode() == 0) {
                if (retry && KendoTournamentGenerator.getInstance().isLocallyConnected()) {
                    startDatabase();
                    return connect(tmp_password, tmp_user, tmp_database, tmp_server, verbose, false);
                } else {
                    error = true;
                }
            } else if (ex.getErrorCode() == 1049) { //Server started, but no database found.
                if (retry && KendoTournamentGenerator.getInstance().isLocallyConnected()) {
                    installDatabase(tmp_password, tmp_user, tmp_server, tmp_database);
                    return connect(tmp_password, tmp_user, tmp_database, tmp_server, verbose, false);
                } else {
                    error = true;
                }
            } else {
                showSQLError(ex.getErrorCode());
                error = true;
            }
        }
        if (!error) {
            if (verbose) {
                MessageManager.translatedMessage("databaseConnected", "MySQL", KendoTournamentGenerator.getInstance().language, "MySQL (" + tmp_server + ")", JOptionPane.INFORMATION_MESSAGE);
            }
            Log.info("databaseConnected", "MySQL", KendoTournamentGenerator.getInstance().language);
        }
        return !error;
    }

    @Override
    public void disconnect() throws SQLException {
        try {
            connection.close();
            KendoTournamentGenerator.getInstance().resetPassword();
        } catch (NullPointerException npe) {
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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex1);
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
            try (PreparedStatement sm = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + tmp_database)) {
                sm.executeUpdate();
            }

            executeScript(Path.returnDatabasePath() + "kendotournament_empty.sql");

        } catch (SQLException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    boolean isDatabaseInstalledCorrectly(boolean verbose) {
        int count = 0;
        try {
            java.sql.DatabaseMetaData md = connection.getMetaData();
            try (ResultSet rs = md.getTables(null, null, "%", null)) {
                if (verbose) {
                    System.out.println("Searching for tables... ");
                }
                while (rs.next()) {
                    String catalogo = rs.getString(1);
                    String tabla = rs.getString(3);
                    if (verbose) {
                        System.out.println("TABLE = " + catalogo + "." + tabla);
                    }
                    count++;
                }
            }
        } catch (SQLException ex) {
            return false;
        }
        if (count > 6) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateDatabase(String path, boolean verbose) {
        boolean answer = true;
        boolean updated = false;
        if (verbose) {
            answer = MessageManager.questionMessage("questionUpdateDatabase", "Warning!", KendoTournamentGenerator.getInstance().language);
        }
        if (answer) {
            updated = updateDatabaseAction(path);
            if (updated) {
                MessageManager.translatedMessage("updatedDatabase", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getVersion(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                MessageManager.errorMessage("notUpdateDatabase", "MySQL", KendoTournamentGenerator.getInstance().language);
            }
        }
        return updated;
    }

    private boolean updateDatabaseAction(String path) {
        String query = "";
        boolean returnValue = true;
        try {
            //File myFolder = new File(new Path().returnDatabasePath());
            File myFolder = new File(path);
            File allMyFolderObjects[] = myFolder.listFiles();

            for (int o = 0; o < allMyFolderObjects.length; o++) {
                if (allMyFolderObjects[o].isDirectory()) {
                    return updateDatabaseAction(allMyFolderObjects[o].getPath());
                } else {
                    if (!allMyFolderObjects[o].getPath().endsWith("~")) {
                        List<String> lines = MyFile.InLines(allMyFolderObjects[o].getPath(), false);
                        for (int i = 0; i < lines.size(); i++) {
                            if (!lines.get(i).startsWith("--")) {
                                if (!lines.get(i).endsWith(";")) {
                                    query += lines.get(i).trim();
                                } else {
                                    if (lines.get(i).trim().length() > 0) {
                                        query += lines.get(i).trim();
                                        try (PreparedStatement s = connection.prepareStatement(query)) {
                                            s.executeUpdate();
                                        }
                                        query = "";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            showSQLError(1049);
            returnValue = false;
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 1060) {  //Avoid error a column already exists.
                Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
                returnValue = false;
            }
        }
        return returnValue;
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
    protected boolean showSQLError(int numberError) {
        System.out.println("Error: " + numberError);
        switch (numberError) {
            case 1045:
                MessageManager.errorMessage("deniedUser", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1049:
                MessageManager.errorMessage("noDatabase", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1062:
                MessageManager.errorMessage("repeatedCompetitor", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1054:
                MessageManager.errorMessage("unknownColumn", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1146:
                MessageManager.errorMessage("corruptedDatabase", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1130:
                MessageManager.errorMessage("noAccessUser", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 1044:
                MessageManager.errorMessage("noUserPrivileges", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
            case 0:
                MessageManager.errorMessage("noDatabase", "MySQL", KendoTournamentGenerator.getInstance().language);
                return true;
        }

        return false;
    }
}
