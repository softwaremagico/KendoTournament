package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;

public class AutoSaveByAction {
	private static AutoSaveByAction instance = new AutoSaveByAction();

	private AutoSaveByAction() {

	}

	public static AutoSaveByAction getInstance() {
		return instance;
	}

	public void save() {
		if (KendoTournamentGenerator.getAutosaveOption().equals(AutoSaveOption.BY_ACTION)) {
			AutoSaveByTimeThread autosaveThread = new AutoSaveByTimeThread();
			autosaveThread.run();
		}
	}
}
