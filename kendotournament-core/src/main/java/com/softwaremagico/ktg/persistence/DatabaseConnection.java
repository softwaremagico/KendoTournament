package com.softwaremagico.ktg.persistence;

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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseConnection {

    // Database connection keep alive for X tenth of a second
    private static Integer CONNECTION_TRIES = 10;
    private static Integer CONNECTION_TASK_PERIOD = 100;
    private Database database = null;
    private String password = "";
    private String user = "kendouser";
    private String databaseName = "kendotournament";
    private String server = "localhost";
    private DatabaseEngine databaseEngine = DatabaseEngine.SQLite;
    private boolean databaseConnectionTested = false;
    private static DatabaseConnection connection = null;
    private static Integer connectionCounts = 0;
    private Timer timer = new Timer("Database Connection");
    private Task timerTask;
    private boolean disconnected = true;

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
        databaseConnectionTested = false;
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        databaseConnectionTested = false;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        databaseConnectionTested = false;
        this.user = user;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        databaseConnectionTested = false;
        this.databaseName = databaseName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        databaseConnectionTested = false;
        this.server = server;
    }

    public boolean isDatabaseConnectionTested() {
        return databaseConnectionTested;
    }

    public static DatabaseConnection getConnection() {
        return connection;
    }

    public static void setConnection(DatabaseConnection connection) {
        DatabaseConnection.connection = connection;
    }

    public boolean testDatabaseConnection(String password, String user, String databaseName, String server)
            throws SQLException {
        this.password = password;
        this.user = user;
        this.databaseName = databaseName;
        this.server = server;
        this.database = databaseEngine.getDatabaseClass();
        generateDatabaseConnectionFile();
        databaseConnectionTested = connect();
        return databaseConnectionTested;
    }

    public void resetPassword() {
        password = "";
    }

    private void obtainStoredDatabaseConnection() {
        try {
            List<String> connectionData;

            connectionData = Folder.readFileLines(Path.getPathConnectionConfigInHome());

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

        // The easy way.
        if (server.equals("localhost") || server.equals("127.0.0.1")
                || (ownIP != null && server.equals(ownIP.getHostAddress()))) {
            return true;
        }
        // Read all network interfaces.
        try {
            Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) net.nextElement();
                Enumeration<InetAddress> addr = ni.getInetAddresses();
                // Get the IP of each interface.
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
            return DatabaseEngine.getDatabase("SQLite");
        }
        return databaseEngine;
    }

    public void setDatabaseEngine(String engine) {
        databaseEngine = DatabaseEngine.getDatabase(engine);
    }

    public boolean updateDatabase() throws SQLException {
        connect();

        // Delete actions.
        if (!UndrawPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }

        if (!DuelPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!FightPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!TeamPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!RolePool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!CustomLinkPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!TournamentPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!PhotoPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!RegisteredPersonPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }
        if (!ClubPool.getInstance().removeElementsFromDatabase()) {
            return false;
        }

        // Add actions
        if (!ClubPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!RegisteredPersonPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!PhotoPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!TournamentPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!CustomLinkPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!RolePool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!TeamPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!FightPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!DuelPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        if (!UndrawPool.getInstance().addElementsToDatabase()) {
            return false;
        }
        disconnect();
        AutoSave.getInstance().resetTime();
        return true;
    }

    public boolean needsToBeStoredInDatabase() {
        return ClubPool.getInstance().needsToBeStoredInDatabase()
                || RegisteredPersonPool.getInstance().needsToBeStoredInDatabase()
                || PhotoPool.getInstance().needsToBeStoredInDatabase()
                || TournamentPool.getInstance().needsToBeStoredInDatabase()
                || RolePool.getInstance().needsToBeStoredInDatabase()
                || TeamPool.getInstance().needsToBeStoredInDatabase()
                || FightPool.getInstance().needsToBeStoredInDatabase()
                || DuelPool.getInstance().needsToBeStoredInDatabase()
                || UndrawPool.getInstance().needsToBeStoredInDatabase()
                || CustomLinkPool.getInstance().needsToBeStoredInDatabase();
    }

    public synchronized boolean connect() throws SQLException {
        boolean connectionSuccess = true;
        try {
            timerTask.cancel();
        } catch (NullPointerException npe) {
        }
        if (connectionCounts == 0 && disconnected) {
            try {
                connectionSuccess = getDatabase().connect(password, user, databaseName, server, false, true);
                disconnected = false;
            } catch (NullPointerException npe) {
                connectionSuccess = false;
            }
        }
        if (connectionSuccess) {
            connectionCounts++;
        }
        return connectionSuccess;
    }

    public synchronized void disconnect() {
        connectionCounts--;
        if (connectionCounts == 0) {
            try {
                timerTask.cancel();
            } catch (NullPointerException npe) {
            }
            timerTask = new Task();
            timer.schedule(timerTask, CONNECTION_TASK_PERIOD, CONNECTION_TASK_PERIOD);
        }
    }

    class Task extends TimerTask {
        // times member represent calling times.

        private int times = 0;

        @Override
        public void run() {
            times++;
            if (times >= CONNECTION_TRIES && connectionCounts == 0) {
                getDatabase().disconnect();
                disconnected = true;
                this.cancel();
            }
        }
    }
}
