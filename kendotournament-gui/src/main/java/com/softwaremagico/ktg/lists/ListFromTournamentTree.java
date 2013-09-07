package com.softwaremagico.ktg.lists;

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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.ListFromTournamentCreateFile;
import com.softwaremagico.ktg.gui.fight.TreeWindow;
import com.softwaremagico.ktg.persistence.TournamentPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ListFromTournamentTree extends ListFromTournamentCreateFile {

    public ListFromTournamentTree() {
        createGui(false);
        addGenerateListeners();
        GenerateButton.setText(trans.getTranslatedText("TreeButton"));
    }

    @Override
    protected void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = TournamentPool.getInstance().getSorted();
            for (int i = 0; i < listTournaments.size(); i++) {
                if (listTournaments.get(i).isChampionship()) {
                    TournamentComboBox.addItem(listTournaments.get(i));
                }
            }
            Tournament tournament = KendoTournamentGenerator.getInstance().getLastSelectedTournament();
            if (tournament != null) {
                TournamentComboBox.setSelectedItem(tournament);
            } else if (TournamentComboBox.getItemCount() > 0) {
                TournamentComboBox.setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        refreshTournament = true;
    }

    @Override
    public String defaultFileName() {
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_tree";
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private void addGenerateListeners() {
        GenerateButton.addActionListener(new ButtonListener());
    }
    
    private ListFromTournamentTree getInstance(){
        return this;
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            TreeWindow tw = new TreeWindow(getSelectedTournament());
            tw.setVisible(true);
            getInstance().dispose();
        }
    }
}
