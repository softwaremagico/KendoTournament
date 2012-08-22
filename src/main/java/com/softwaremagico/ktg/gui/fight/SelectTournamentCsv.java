package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.gui.SelectTournament;
import javax.swing.JFileChooser;

public class SelectTournamentCsv extends SelectTournament {

    public SelectTournamentCsv(String title, String buttonTag) {
        super(title, buttonTag);
    }

    public void generate() {
        try {
            String file;
            if (!(file = exploreWindowsForCsv("CSV", JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
            }
        } catch (Exception ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }
}
