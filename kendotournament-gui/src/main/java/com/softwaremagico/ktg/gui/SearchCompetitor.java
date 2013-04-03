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

import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.database.DatabaseConnection;
import com.softwaremagico.ktg.database.RegisteredPersonPool;
import com.softwaremagico.ktg.language.LanguagePool;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;

public final class SearchCompetitor extends Search<RegisteredPerson> {

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
        IDTextField.addKeyListener(new IDKeyEvent());
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
    protected String getResultInformation(RegisteredPerson object) {
        return object.getSurname() + ", " + object.getName() + " (" + object.getId() + ")";
    }

    @Override
    protected void searchButtonActionPerformed(ActionEvent evt) {
        results = new ArrayList<>();
        if (IDTextField.getText().length() > 0) {
            results = RegisteredPersonPool.getInstance().search(IDTextField.getText().trim());
        } else if (SurnameTextField.getText().length() > 0) {
            results = RegisteredPersonPool.getInstance().getBySurname(SurnameTextField.getText().trim());
        } else if (NameTextField.getText().length() > 0) {
            results = RegisteredPersonPool.getInstance().getByName(NameTextField.getText().trim());
        } else if (ClubTextField.getText().length() > 0) {
            results = RegisteredPersonPool.getInstance().getByClub(ClubTextField.getText().trim());
        } else {
            MessageManager.errorMessage(this.getClass().getName(), "fillFields", "Search");
        }
        fillResults(results);
        if (results.size() > 0) {
            ResultList.setSelectedIndex(0);
        }
    }

    @Override
    protected boolean deleteFromDatabase(RegisteredPerson object) {
        RegisteredPersonPool.getInstance().remove(object);
        return true;
    }

    public class IDKeyEvent implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (IDTextField.getText().length() > 12) {
                IDTextField.setText(IDTextField.getText().subSequence(0, 12).toString());
            }
            IDTextField.setText(IDTextField.getText().replace("-", ""));
            IDTextField.setText(IDTextField.getText().replace(" ", ""));
            IDTextField.setText(IDTextField.getText().toUpperCase());
        }
    }
}
