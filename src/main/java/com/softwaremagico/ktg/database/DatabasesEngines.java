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
public enum DatabasesEngines {

    MySQL(true, new MySQL()),
    SQLite(false, new SQLite());
    
    private final boolean networkConnection;
    private Database database = null;
    private static final List<DatabasesEngines> availableDatabase = new ArrayList<DatabasesEngines>();

    DatabasesEngines(boolean network, Database db) {
        this.networkConnection = network;
        this.database = db;
    }

    public boolean getHasNetworkConnection() {
        return networkConnection;
    }

    public static DatabasesEngines getDatabase(int index) {
        return availableDatabase.get(index);
    }

    public static DatabasesEngines getDatabase(String database) {
        for (DatabasesEngines db : availableDatabase) {
            if (database.equals(db.toString())) {
                return db;
            }
        }
        return null;
    }

    public Database getDatabaseClass() {
        return database;
    }

    static {
        availableDatabase.addAll(Arrays.asList(DatabasesEngines.values()));
    }
}
