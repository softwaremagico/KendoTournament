/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author jorge
 */
public final class SearchTournament extends Search<Tournament> {

    private JLabel NameLabel = new JLabel("Name:");
    private JTextField NameTextField = new JTextField();

    public SearchTournament() {
        super();
        fillSearchFieldPanel();
        setLanguage(KendoTournamentGenerator.getInstance().language);
    }

    /**
     * Translate the GUI to the selected language.
     */
    private void setLanguage(String language) {
        trans = new Translator("gui.xml");
        NameLabel.setText(trans.returnTag("NameLabel", language));
    }

    @Override
    protected void fillSearchFieldPanel() {
        javax.swing.GroupLayout SearchFieldPanelLayout = new javax.swing.GroupLayout(SearchFieldPanel);
        SearchFieldPanel.setLayout(SearchFieldPanelLayout);
        SearchFieldPanelLayout.setHorizontalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addComponent(NameLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE).addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
        SearchFieldPanelLayout.setVerticalGroup(
                SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(SearchFieldPanelLayout.createSequentialGroup().addContainerGap().addGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(NameLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
    }

    @Override
    protected String getResultInformation(Tournament object) {
        return object.name;
    }

    @Override
    protected void searchButtonActionPerformed(ActionEvent evt) {
        results = new ArrayList<>();
        if (NameTextField.getText().length() > 0) {
            results = KendoTournamentGenerator.getInstance().database.searchTournamentsByName(NameTextField.getText(), true);
        } else {
            MessageManager.errorMessage("fillFields", "Search", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
        fillResults(results);
    }

    @Override
    protected boolean deleteFromDatabase(Tournament object) {
        return KendoTournamentGenerator.getInstance().database.deleteTournament(object.name);
    }
}
