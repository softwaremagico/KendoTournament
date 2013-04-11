package com.softwaremagico.ktg.gui;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.TeamPool;
import com.softwaremagico.ktg.database.TournamentPool;
import com.softwaremagico.ktg.language.LanguagePool;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public final class SearchTeam extends Search<Team> {

    private JLabel NameLabel = new JLabel("Name:");
    private JTextField NameTextField = new JTextField();
    private JComboBox<Tournament> TournamentComboBox = new JComboBox();
    private JLabel TournamentLabel = new JLabel("Tournament:");
    private boolean refreshTournament = true;

    public SearchTeam() {
        super();
        fillSearchFieldPanel();
        setLanguage();
        fillTournaments();

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });
    }

    /**
     * Translate the GUI to the selected language.
     */
    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        NameLabel.setText(trans.getTranslatedText("NameLabel"));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            List<Tournament> listTournaments = TournamentPool.getInstance().getSorted();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i));
            }
        } catch (NullPointerException npe) {
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
    }

    @Override
    protected void fillSearchFieldPanel() {
        javax.swing.GroupLayout SearchFieldPanelLayout = new javax.swing.GroupLayout(SearchFieldPanel);
        SearchFieldPanel.setLayout(SearchFieldPanelLayout);
        SearchFieldPanelLayout.setHorizontalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SearchFieldPanelLayout.createSequentialGroup().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(NameLabel).addComponent(TournamentLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(TournamentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)))).addContainerGap()));
        SearchFieldPanelLayout.setVerticalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(NameLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(TournamentLabel).addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
    }

    @Override
    protected String getResultInformation(Team object) {
        return object.getName();
    }

    @Override
    protected void searchButtonActionPerformed(ActionEvent evt) {
        results = new ArrayList<>();
        if (NameTextField.getText().length() > 0) {
            results = TeamPool.getInstance().getById((Tournament) TournamentComboBox.getSelectedItem(), NameTextField.getText().trim());
        } else {
            MessageManager.errorMessage(this.getClass().getName(), "fillFields", "Search");
        }
        fillResults(results);
        if (results.size() > 0) {
            resultList.setSelectedIndex(0);
        }
    }

    @Override
    protected boolean deleteElement(Team team) {        
            if (MessageManager.questionMessage("questionDeleteTeam", "Team")) {
                if (TeamPool.getInstance().remove((Tournament) TournamentComboBox.getSelectedItem(), team)) {
                    MessageManager.informationMessage(this.getClass().getName(), "teamDeleted", "Team");
                    return true;
                }
            }
        return false;
    }

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().setLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
        }
    }
}
