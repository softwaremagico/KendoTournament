/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author jorge
 */
public final class SearchTeam extends Search<Team> {

    private JLabel NameLabel = new JLabel("Name:");
    private JTextField NameTextField = new JTextField();
    private JComboBox<String> TournamentComboBox = new JComboBox();
    private JLabel TournamentLabel = new JLabel("Tournament:");
    private List<Tournament> listTournaments = new ArrayList<>();
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
        trans = new Translator("gui.xml");
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        NameLabel.setText(trans.returnTag("NameLabel"));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
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
        return object.returnName();
    }

    @Override
    protected void searchButtonActionPerformed(ActionEvent evt) {
        results = new ArrayList<>();
        if (NameTextField.getText().length() > 0) {
            results = KendoTournamentGenerator.getInstance().database.searchTeamsByNameAndTournament(NameTextField.getText(), TournamentComboBox.getSelectedItem().toString(), true);
        } else {
            MessageManager.errorMessage("fillFields", "Search");
        }
        fillResults(results);
        if (results.size() > 0) {
            ResultList.setSelectedIndex(0);
        }
    }

    @Override
    protected boolean deleteFromDatabase(Team object) {
        return KendoTournamentGenerator.getInstance().database.deleteTeam(object, true);
    }

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
        }
    }
}
