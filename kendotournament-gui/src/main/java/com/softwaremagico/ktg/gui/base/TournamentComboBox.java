package com.softwaremagico.ktg.gui.base;
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
import com.softwaremagico.ktg.database.TournamentPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TournamentComboBox extends KComboBox {

    private List<Tournament> listTournaments;
    private KFrame parent;

    public TournamentComboBox(KFrame parent) {
        listTournaments = TournamentPool.getInstance().getSorted();
        this.parent = parent;
        fillTournaments();
        addActionListener(new ComboBoxActionListener());
    }

    private void fillTournaments() {
        Tournament selectedTournament;
        try {
            for (int i = 0; i < listTournaments.size(); i++) {
                addItem(listTournaments.get(i));
            }
            selectedTournament = KendoTournamentGenerator.getInstance().getLastSelectedTournament();
            if (selectedTournament != null) {
                setSelectedItem(selectedTournament);
            } else if (getItemCount() > 0) {
                setSelectedIndex(0);
                KendoTournamentGenerator.getInstance().setLastSelectedTournament(getSelectedTournament().toString());
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    public Tournament getSelectedTournament() {
        try {
            return ((Tournament) getSelectedItem());
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private void TournamentComboBoxActionPerformed(ActionEvent evt) {
        parent.tournamentChanged();
    }

    class ComboBoxActionListener implements ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            TournamentComboBoxActionPerformed(evt);
        }
    }
}
