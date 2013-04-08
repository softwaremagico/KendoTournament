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

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.database.ClubPool;
import com.softwaremagico.ktg.language.LanguagePool;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;

public final class SearchClub extends Search<Club> {

    private JLabel NameLabel = new JLabel("Name:");
    private JTextField NameTextField = new JTextField();
    private JLabel CountryLabel = new JLabel("Country:");
    private JTextField CountryTextField = new JTextField();
    private JLabel CityLabel = new JLabel("City:");
    private JTextField CityTextField = new JTextField();

    public SearchClub() {
        super();
        fillSearchFieldPanel();
        setLanguage();
    }

    /**
     * Translate the GUI to the selected language.
     */
    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        CountryLabel.setText(trans.returnTag("CountryLabel"));
        CityLabel.setText(trans.returnTag("CityLabel"));
        NameLabel.setText(trans.returnTag("NameLabel"));
    }

    @Override
    protected void fillSearchFieldPanel() {
        javax.swing.GroupLayout SearchFieldPanelLayout = new javax.swing.GroupLayout(SearchFieldPanel);
        SearchFieldPanel.setLayout(SearchFieldPanelLayout);
        SearchFieldPanelLayout.setHorizontalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SearchFieldPanelLayout.createSequentialGroup().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(NameLabel).addComponent(CountryLabel).addComponent(CityLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(CityTextField, javax.swing.GroupLayout.Alignment.TRAILING).addComponent(CountryTextField, javax.swing.GroupLayout.Alignment.TRAILING).addComponent(NameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)))).addContainerGap()));
        SearchFieldPanelLayout.setVerticalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(NameLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(CountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(CountryLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(CityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(CityLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE).addContainerGap()));
    }

    @Override
    protected void searchButtonActionPerformed(ActionEvent evt) {
        results = new ArrayList<>();
        if (NameTextField.getText().length() > 0) {
            results = ClubPool.getInstance().getByName(NameTextField.getText());
        } else if (CountryTextField.getText().length() > 0) {
            results = ClubPool.getInstance().getByCountry(NameTextField.getText());
        } else if (CityTextField.getText().length() > 0) {
            results = ClubPool.getInstance().getByCity(NameTextField.getText());
        } else {
            MessageManager.errorMessage(this.getClass().getName(), "fillFields", "Search");
        }
        fillResults(results);
        if (results.size() > 0) {
            ResultList.setSelectedIndex(0);
        }
    }

    @Override
    protected String getResultInformation(Club object) {
        return object.getName() + " (" + object.getCountry() + ")";
    }

    @Override
    protected boolean deleteElement(Club object) {
        return ClubPool.getInstance().remove(object);         
    }
}
