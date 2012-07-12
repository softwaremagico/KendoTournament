/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 1-jul-2012.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.language.LanguagePool;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author jorge
 */
public final class SearchCompetitor extends Search<CompetitorWithPhoto> {

    private JLabel ClubLabel = new JLabel("Club:");
    private JTextField ClubTextField = new JTextField();
    private JLabel IDLabel = new JLabel("ID:");
    private JTextField IDTextField = new JTextField();
    private JLabel NameLabel = new JLabel("Name:");
    private JTextField NameTextField = new JTextField();
    private JLabel SurnameLabel = new JLabel("Surname:");
    private JTextField SurnameTextField = new JTextField();

    public SearchCompetitor() {
        super();
        fillSearchFieldPanel();
        setLanguage();
    }

    /**
     * Translate the GUI to the selected language.
     */
    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        NameLabel.setText(trans.returnTag("NameLabel"));
        SurnameLabel.setText(trans.returnTag("SurnameLabel"));
        IDLabel.setText(trans.returnTag("IDLabel"));
        ClubLabel.setText(trans.returnTag("ClubLabel"));
    }

    @Override
    protected void fillSearchFieldPanel() {
        javax.swing.GroupLayout SearchFieldPanelLayout = new javax.swing.GroupLayout(SearchFieldPanel);
        SearchFieldPanel.setLayout(SearchFieldPanelLayout);
        SearchFieldPanelLayout.setHorizontalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(ClubLabel).addComponent(IDLabel).addComponent(SurnameLabel).addComponent(NameLabel)).addGap(40, 40, 40).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(ClubTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE).addComponent(NameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE).addComponent(SurnameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE).addComponent(IDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)))).addContainerGap()));
        SearchFieldPanelLayout.setVerticalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(IDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(IDLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(SurnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(SurnameLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(NameLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(ClubTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(ClubLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addContainerGap()));

    }

    @Override
    protected String getResultInformation(CompetitorWithPhoto object) {
        return object.getSurname() + ", " + object.getName() + " (" + object.getId() + ")";
    }

    @Override
    protected void searchButtonActionPerformed(ActionEvent evt) {
        results = new ArrayList<>();
        if (IDTextField.getText().length() > 0) {
            results = KendoTournamentGenerator.getInstance().database.searchCompetitorsBySimilarID(IDTextField.getText(), true, true);
        } else if (SurnameTextField.getText().length() > 0) {
            results = KendoTournamentGenerator.getInstance().database.searchCompetitorsBySimilarSurname(SurnameTextField.getText(), true, true);
        } else if (NameTextField.getText().length() > 0) {
            results = KendoTournamentGenerator.getInstance().database.searchCompetitorsBySimilarName(NameTextField.getText(), true, true);
        } else if (ClubTextField.getText().length() > 0) {
            results = KendoTournamentGenerator.getInstance().database.searchCompetitorsBySimilarClub(ClubTextField.getText(), true, true);
        } else {
            MessageManager.errorMessage("fillFields", "Search");
        }
        fillResults(results);
        if (results.size() > 0) {
            ResultList.setSelectedIndex(0);
        }
    }

    @Override
    protected boolean deleteFromDatabase(CompetitorWithPhoto object) {
        return KendoTournamentGenerator.getInstance().database.deleteCompetitor(object, true);
    }
}
