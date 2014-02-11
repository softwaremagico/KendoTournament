package com.softwaremagico.ktg.persistence;

import java.sql.SQLException;

import com.softwaremagico.ktg.core.KendoLog;

public class AutoSaveThread extends Thread {

	@Override
	public void run() {
		if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
			if (DatabaseConnection.getInstance().needsToBeStoredInDatabase()) {
				KendoLog.debug(AutoSave.class.getName(), "Autosaving...");
				try {
					DatabaseConnection.getInstance().updateDatabase();
				} catch (SQLException ex) {
					KendoLog.errorMessage(this.getClass().getName(), ex);
				}
			}
		}
	}
}
