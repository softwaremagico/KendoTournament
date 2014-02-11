package com.softwaremagico.ktg.persistence;

/*
 * #%L
 * Kendo Tournament Generator GUI
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

import java.util.Timer;
import java.util.TimerTask;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;

public class AutoSave {

	private static final Integer AUTOSAVE_MINUTES_PERIOD = 20;
	private static AutoSave instance = new AutoSave();
	private Timer timer = new Timer("Autosave");
	private Task timerTask;

	private AutoSave() {
		timerTask = new Task();
		//For each minute. 
		timer.schedule(timerTask, 0, 60000);

	}

	public static AutoSave getInstance() {
		return instance;
	}

	public void resetTime() {
		timerTask.reset();
	}

	public void setAutosavePeriod(Integer minutes) {
		timerTask.setPeriod(minutes);
	}

	/**
	 * Starts a thread for saving data when expired a counter.
	 */
	class Task extends TimerTask {

		private int minutes = 0;
		private int save_period = AUTOSAVE_MINUTES_PERIOD;

		@Override
		public void run() {
			minutes++;
			if (minutes >= save_period) {
				if (KendoTournamentGenerator.getAutosaveOption().equals(AutoSaveOption.BY_TIME)) {
					AutoSaveThread autosaveThread = new AutoSaveThread();
					autosaveThread.run();
					// if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
					// KendoLog.debug(AutoSave.class.getName(), "Autosaving...");
					// try {
					// DatabaseConnection.getInstance().updateDatabase();
					// } catch (SQLException ex) {
					// KendoLog.errorMessage(this.getClass().getName(), ex);
					// }
					// }
				}
			}
		}

		public void reset() {
			minutes = 0;
		}

		public void setPeriod(Integer newMinutes) {
			if (newMinutes > 0) {
				save_period = newMinutes;
			}
		}
	}
}
