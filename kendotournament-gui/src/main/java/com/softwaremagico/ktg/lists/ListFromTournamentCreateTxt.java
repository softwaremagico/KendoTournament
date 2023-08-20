package com.softwaremagico.ktg.lists;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
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

import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.ListFromTournamentCreateFile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JFileChooser;

public abstract class ListFromTournamentCreateTxt extends ListFromTournamentCreateFile {

    public ListFromTournamentCreateTxt(boolean allowAllTournaments) {
        createGui(allowAllTournaments);
        addGenerateListeners();
    }

    protected abstract String getTxtGenerator();

    public void generate() {
        try {
            String file;
            if (!(file = exploreWindowsForTxt(trans.getTranslatedText("ExportTxt"),
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                String txt = getTxtGenerator();
                if (!MyFile.fileExist(file) || AlertManager.questionMessage("existFile", "Warning!")) {
                    try (PrintWriter out = new PrintWriter(file)) {
                        out.println(txt);
                    }
                    this.dispose();
                }
            }
        } catch (Exception ex) {
            AlertManager.showErrorInformation(this.getClass().getName(), ex);
        }
    }

    private void addGenerateListeners() {
        GenerateButton.addActionListener(new ButtonListener());
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            generate();
        }
    }
}
