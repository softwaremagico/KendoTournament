package com.softwaremagico.ktg.database;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.Path;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DatabaseConnection {

    private Database database = null;
    private String password = "";
    private String user = "kendouser";
    private String databaseName = "kendotournament";
    private String server = "localhost";
    private DatabaseEngine databaseEngine = null;
    private boolean databaseLazyUpdate = false;
    private boolean databaseConnected = false;
    private static DatabaseConnection connection = null;

    private DatabaseConnection() {
        obtainStoredDatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        if (connection == null) {
            connection = new DatabaseConnection();
        }
        return connection;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isDatabaseConnected() {
        return databaseConnected;
    }

    public void setDatabaseConnected(boolean databaseConnected) {
        this.databaseConnected = databaseConnected;
    }

    public static DatabaseConnection getConnection() {
        return connection;
    }

    public static void setConnection(DatabaseConnection connection) {
        DatabaseConnection.connection = connection;
    }

    public boolean testDatabaseConnection(String password, String user, String databaseName, String server) {
        this.password = password;
        this.user = user;
        this.databaseName = databaseName;
        this.server = server;
        generateDatabaseConnectionFile();
        this.database = databaseEngine.getDatabaseClass();
        this.database.disconnect();
        databaseConnected = database.connect(password, user, databaseName, server, true, true);
        return databaseConnected;
    }

    public void resetPassword() {
        password = "";
    }

    private void obtainStoredDatabaseConnection() {
        try {
            List<String> connectionData;

            connectionData = Folder.readFileLines(Path.getPathConnectionConfigInHome(), false);

            for (int i = 0; i < connectionData.size(); i++) {
                if (connectionData.get(i).contains("User:")) {
                    try {
                        user = connectionData.get(i).split("User:")[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
                if (connectionData.get(i).contains("Machine:")) {
                    try {
                        server = connectionData.get(i).split("Machine:")[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
                if (connectionData.get(i).contains("Database:")) {
                    try {
                        databaseName = connectionData.get(i).split("Database:")[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
                if (connectionData.get(i).contains("Engine:")) {
                    try {
                        databaseEngine = DatabaseEngine.getDatabase(connectionData.get(i).split("Engine:")[1]);
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
            }
        } catch (IOException ex) {
        }
    }

    private void generateDatabaseConnectionFile() {
        List<String> connectionData = new ArrayList<>();
        connectionData.add("User:" + user);
        connectionData.add("Machine:" + server);
        connectionData.add("Database:" + databaseName);
        connectionData.add("Engine:" + databaseEngine);
        Folder.saveListInFile(connectionData, Path.getPathConnectionConfigInHome());
    }

    public boolean isLocallyConnected() {
        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex1) {
        }

        if (database.onlyLocalConnection()) {
            return true;
        }

        //The easy way. 
        if (server.equals("localhost") || server.equals("127.0.0.1") || server.equals(ownIP.getHostAddress())) {
            return true;
        }
        //Read all network interfaces.
        try {
            Enumeration net = NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) net.nextElement();
                Enumeration addr = ni.getInetAddresses();
                //Get the IP of each interface.
                while (addr.hasMoreElements()) {
                    java.net.InetAddress inet = (java.net.InetAddress) addr.nextElement();
                    if (server.equals(inet.getHostAddress())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public DatabaseEngine getDatabaseEngine() {
        if (databaseEngine == null) {
            return DatabaseEngine.getDatabase("MySQL");
        }
        return databaseEngine;
    }

    public void setDatabaseEngine(String engine) {
        databaseEngine = DatabaseEngine.getDatabase(engine);
    }

    public boolean isDatabaseLazyUpdate() {
        return databaseLazyUpdate;
    }

    public void setDatabaseLazyUpdate(boolean value) {
        databaseLazyUpdate = value;
    }

    public void updateDatabase() {
        getDatabase().connect(password, user, databaseName, server, false, true);
        ClubPool.getInstance().updateDatabase();
        RegisteredPersonPool.getInstance().updateDatabase();
        PhotoPool.getInstance().updateDatabase();
        TournamentPool.getInstance().updateDatabase();
        RolePool.getInstance().updateDatabase();
        TeamPool.getInstance().updateDatabase();
        FightPool.getInstance().updateDatabase();
        DuelPool.getInstance().updateDatabase();
        UndrawPool.getInstance().updateDatabase();
        getDatabase().disconnect();
    }
}
