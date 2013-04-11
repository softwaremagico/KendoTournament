package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import java.util.Timer;
import java.util.TimerTask;

public class AutoSave {

    private static final Integer AUTOSAVE_PERIOD = 1200;
    private static AutoSave instance = new AutoSave(AUTOSAVE_PERIOD);
    private Timer timer = new Timer("Autosave");
    private Task timerTask;

    private AutoSave(Integer autosaveTime) {
        timerTask = new Task();
        timer.schedule(timerTask, 0, 1000 * autosaveTime);

    }

    public static AutoSave getInstance() {
        return instance;
    }

    public static void setTime(Integer seconds) {
        instance = new AutoSave(seconds);
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            if (KendoTournamentGenerator.isAutosaveOptionSelected()) {
                KendoLog.debug(AutoSave.class.getName(), "Autosaving...");
                if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
                    DatabaseConnection.getInstance().updateDatabase();
                }
            }
        }
    }
}
