package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.log.KendoLog;

import java.sql.SQLException;

public class AutoSaveThread extends Thread {

    @Override
    public void run() {
        if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
            if (DatabaseConnection.getInstance().needsToBeStoredInDatabase()) {
                KendoLog.debug(AutoSaveByTime.class.getName(), "Autosaving...");
                try {
                    DatabaseConnection.getInstance().updateDatabase();
                } catch (SQLException ex) {
                    KendoLog.errorMessage(this.getClass().getName(), ex);
                }
            }
        }
    }
}
