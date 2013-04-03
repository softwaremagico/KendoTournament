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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public enum DatabaseEngine {

    MySQL(true, new MySQL()),
    SQLite(false, new SQLite());
    private final boolean networkConnection;
    private Database database = null;
    private static final List<DatabaseEngine> availableDatabase = new ArrayList<>();

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

    public static DatabaseEngine getOtherDatabase(String database) {
        for (DatabaseEngine db : availableDatabase) {
            if (!database.equals(db.toString())) {
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
