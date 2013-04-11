package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;

public class AutoSave {

    private static final Integer AUTOSAVE_PERIOD = 1200;
    private static AutoSave instance = new AutoSave(AUTOSAVE_PERIOD);
    private Timer timer = new Timer("Autosave");
    private Task timerTask;
    private static Translator trans;
    //private AutosavePanel autosavePanel;

    private AutoSave(Integer autosaveTime) {
        trans = LanguagePool.getTranslator("messages.xml");
        //autosavePanel = new AutosavePanel();
        timerTask = new Task();
        timer.schedule(timerTask, 0, 1000 * autosaveTime);
        // autosavePanel.setVisible(true);

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
            // autosavePanel.setVisible(true);
            // autosavePanel.setAlwaysOnTop(true);
            if (KendoTournamentGenerator.isAutosaveOptionSelected()) {
                KendoLog.debug(AutoSave.class.getName(), "Autosaving...");
                if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
                    DatabaseConnection.getInstance().updateDatabase();
                }
            }
            // autosavePanel.setVisible(false);
            // autosavePanel.dispose();
        }
    }

    class AutosavePanel extends KFrame {

        public AutosavePanel() {
            defineWindow(300, 180);
            setResizable(false);
            setElements();
        }

        private void setElements() {
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            String text = trans.getTranslatedText("autosaving");
            JLabel label = new JLabel(text);
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipadx = xPadding;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 1;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            getContentPane().add(label, gridBagConstraints);
        }

        @Override
        public void update() {
        }

        @Override
        public void tournamentChanged() {
        }
    }
}
