/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author LOCAL\jhortelano
 */
public enum DatabaseEngine {

    MySQL(true, new MySQL()),
    SQLite(false, new SQLite());
    private final boolean networkConnection;
    private Database database = null;
    private static final List<DatabaseEngine> availableDatabase = new ArrayList<DatabaseEngine>();

    DatabaseEngine(boolean network, Database db) {
        this.networkConnection = network;
        this.database = db;
    }

    public boolean getHasNetworkConnection() {
        return networkConnection;
    }

    public static DatabaseEngine getDatabase(int index) {
        return availableDatabase.get(index);
    }

    public static DatabaseEngine getDatabase(String database) {
        for (DatabaseEngine db : availableDatabase) {
            if (database.equals(db.toString())) {
                return db;
            }
        }
        return null;
    }

    public Database getDatabaseClass() {
        return database;
    }

    public static Database getDatabaseClass(String database) {
        return getDatabase(database).database;
    }
    

    static {
        availableDatabase.addAll(Arrays.asList(DatabaseEngine.values()));
    }
}
