/*
 * 
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 25-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.TeamAccreditationCardPDF;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author Jorge
 */
public class NewTeam extends KendoFrame {

    protected Translator trans = null;
    protected List<Competitor> competitors = new ArrayList<>();
    private List<Tournament> listTournaments = new ArrayList<>();
    protected boolean refreshTournament = true;
    private boolean individualTeams = false;
    protected List<CompetitorPanel> competitorsPanel = new ArrayList<>();
    Tournament championship = null;
    boolean newTeam = true; //To avoid that OrderTeam also use the event . 

    /**
     * Creates new form NewTeam
     */
    public NewTeam(Team t) {
        updateWindow(t);
    }

    public NewTeam() {
    }

    public final void start() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
    }

    public void fill() {
        refreshCompetitors();
        fillTournaments();
        NameTextField.setEnabled(true);
        try {
            championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
            refreshTournament();
            fillCompetitors();
            inidividualTeams();
        } catch (NullPointerException npe) {
        }
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleNewTeam"));
        AcceptButton.setText(trans.returnTag("AcceptButton"));
        CancelButton.setText(trans.returnTag("CancelButton"));
        SearchButton.setText(trans.returnTag("SearchButton"));
        DeleteButton.setText(trans.returnTag("DeleteButton"));
        PDFButton.setText(trans.returnTag("AccreditationPDFButton"));
        NameLabel.setText(trans.returnTag("NameTeamLabel"));
        IndividualTeamsCheckBox.setText(trans.returnTag("IndividualTeamsCheckBox"));
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
    }

    void fillTournaments() {
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

    protected void fillCompetitors() {
        competitorsPanel = new ArrayList<>();
        for (int i = 0; i < championship.teamSize; i++) {
            CompetitorPanel cp = new CompetitorPanel(KendoTournamentGenerator.getInstance().language, i + 1);

            for (int j = 0; j < competitors.size(); j++) {
                if (competitors.get(j).getName().equals("") && competitors.get(j).getSurname().equals("")) {
                    cp.competitorComboBox.addItem(" ");
                } else {
                    cp.competitorComboBox.addItem(competitors.get(j).getSurname() + ", " + competitors.get(j).getName() + " (" + competitors.get(j).getId() + ")");
                }
            }
            if (cp.competitorComboBox.getItemCount() > i + 1) {
                cp.competitorComboBox.setSelectedIndex(i + 1);
            }

            competitorsPanel.add(cp);
        }
        updateCompetitors();
    }

    private void updateCompetitors() {
        CompetitorsFrame.removeAll();
        for (int i = 0; i < competitorsPanel.size(); i++) {
            CompetitorsFrame.add(competitorsPanel.get(i), BorderLayout.CENTER);
        }
        this.pack();
    }

    private void cleanWindow() {
        NameTextField.setText("");
        //fillTournaments();
        competitors = KendoTournamentGenerator.getInstance().database.selectAllCompetitorsWithoutTeamInTournament(TournamentComboBox.getSelectedItem().toString());
        competitors.add(0, new Competitor("", "", "", ""));
        fillCompetitors();

    }

    public final void updateWindow(Team t) {
        try {
            NameTextField.setText(t.returnName());
            TournamentComboBox.setSelectedItem(t.competition.name);
            AddTeamCompetitorsSorted(t);
            fillCompetitors();
            inidividualTeams();
            NameTextField.setEnabled(false);
            TournamentComboBox.setEnabled(false);
            for (int i = 0; i < competitorsPanel.size(); i++) {
                try {
                    if (i < t.getNumberOfMembers(0) && t.getMember(i, 0) != null && (t.getMember(i, 0).getSurname().length() > 0 || t.getMember(i, 0).getName().length() > 0)) {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(t.getMember(i, 0).getSurname() + ", " + t.getMember(i, 0).getName() + " (" + t.getMember(i, 0).getId() + ")");
                    } else {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                    }
                } catch (NullPointerException npe) {
                    competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                } catch (IndexOutOfBoundsException iob) {
                    KendoTournamentGenerator.getInstance().showErrorInformation(iob);
                    competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    @Override
    public String defaultFileName() {
        try {
            if (NameTextField.getText().length() > 0) {
                return NameTextField.getText();
            } else {
                return "TeamAccreditationCard";
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private void refreshCompetitors() {
        try {
            competitors = KendoTournamentGenerator.getInstance().database.selectAllCompetitorsWithoutTeamInTournament(TournamentComboBox.getSelectedItem().toString());
            competitors.add(0, new Competitor("", "", "", ""));
        } catch (NullPointerException npe) {
            competitors = KendoTournamentGenerator.getInstance().database.getAllCompetitors();
        }
    }

    private void refreshTournament() {
        refreshCompetitors();
        fillCompetitors();
        inidividualTeams();
    }

    private void enablePlayers(boolean b) {
        for (int i = 0; i < competitorsPanel.size(); i++) {
            competitorsPanel.get(i).competitorComboBox.setEnabled(b);
        }
        NameTextField.setEnabled(b);
    }

    public boolean repeatedCompetitor() {
        List<String> selectedCompetitors = new ArrayList<>();

        for (int i = 0; i < competitorsPanel.size(); i++) {
            if (selectedCompetitors.contains(competitorsPanel.get(i).competitorComboBox.getSelectedItem().toString())) {
                return true;
            } else {
                if (!competitorsPanel.get(i).competitorComboBox.getSelectedItem().toString().equals(" ")) {
                    selectedCompetitors.add(competitorsPanel.get(i).competitorComboBox.getSelectedItem().toString());
                }
            }
        }
        return false;
    }

    protected void inidividualTeams() {
        if (championship.teamSize == 1) {
            individualTeams = true;
            IndividualTeamsCheckBox.setEnabled(true);
        } else {
            IndividualTeamsCheckBox.setEnabled(false);
            individualTeams = false;
        }
        enablePlayers(!individualTeams);
        IndividualTeamsCheckBox.setSelected(individualTeams);
    }

    private boolean checkTeam() {
        List<String> selectedCompetitors = new ArrayList<>();

        for (int i = 0; i < competitorsPanel.size(); i++) {
            if (!competitorsPanel.get(i).competitorComboBox.getSelectedItem().toString().equals(" ")) {
                selectedCompetitors.add(competitorsPanel.get(i).competitorComboBox.getSelectedItem().toString());
            }
        }

        if (selectedCompetitors.size() > Math.floor(competitorsPanel.size() / 2)) {
            return true;
        }
        return false;
    }

    protected void AddTeamCompetitorsSorted(Team t) {
        int i;
        if (t.levelChangesSize() > 0) {
            for (int j = 0; j < t.getNumberOfMembers(t.levelChangesSize() - 1); j++) {
                if (competitors.size() > 0) {
                    for (i = 0; i < competitors.size(); i++) {
                        if (competitors.get(i).getSurnameName().compareTo(t.getMember(j, 0).getSurnameName()) > 0) {
                            if (t.getMember(j, 0).getSurnameName().replaceAll("-", "").trim().length() > 0 || t.getMember(j, 0).getName().replaceAll("-", "").trim().length() > 0) {
                                competitors.add(i, t.getMember(j, 0));
                            }
                            break;
                        }
                    }
                    if (i == competitors.size()) { //If has not been added, then add it in the last position.
                        if (t.getMember(j, 0).getSurnameName().replaceAll("-", "").trim().length() > 0 || t.getMember(j, 0).getName().replaceAll("-", "").trim().length() > 0) {
                            competitors.add(t.getMember(j, 0));
                        }
                    }
                } else {
                    competitors.add(t.getMember(j, 0));
                }
            }
        }
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * Add the same action listener to all langugaes of the menu.
     *
     * @param al
     */
    public void addSearchListener(ActionListener al) {
        SearchButton.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TeamPanel = new javax.swing.JPanel();
        NameLabel = new javax.swing.JLabel();
        NameTextField = new javax.swing.JTextField();
        TournamentComboBox = new javax.swing.JComboBox<String>();
        TournamentLabel = new javax.swing.JLabel();
        IndividualTeamsCheckBox = new javax.swing.JCheckBox();
        CompetitorsFrame = new javax.swing.JPanel();
        AcceptButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        SearchButton = new javax.swing.JButton();
        PDFButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate Team");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        TeamPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        NameLabel.setText("Name:");

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        TournamentLabel.setText("Tournament");

        IndividualTeamsCheckBox.setText("Individual Teams");
        IndividualTeamsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IndividualTeamsCheckBoxActionPerformed(evt);
            }
        });

        CompetitorsFrame.setLayout(new javax.swing.BoxLayout(CompetitorsFrame, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout TeamPanelLayout = new javax.swing.GroupLayout(TeamPanel);
        TeamPanel.setLayout(TeamPanelLayout);
        TeamPanelLayout.setHorizontalGroup(
            TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TeamPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(IndividualTeamsCheckBox)
                    .addComponent(CompetitorsFrame, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, TeamPanelLayout.createSequentialGroup()
                        .addGroup(TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TournamentLabel)
                            .addComponent(NameLabel))
                        .addGap(25, 25, 25)
                        .addGroup(TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TournamentComboBox, 0, 435, Short.MAX_VALUE)
                            .addComponent(NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))))
                .addContainerGap())
        );
        TeamPanelLayout.setVerticalGroup(
            TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(NameLabel)
                    .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(TournamentLabel)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CompetitorsFrame, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IndividualTeamsCheckBox)
                .addContainerGap())
        );

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        SearchButton.setText("Search");

        PDFButton.setText("PDF");
        PDFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TeamPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PDFButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(AcceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CancelButton, DeleteButton, PDFButton, SearchButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TeamPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchButton)
                    .addComponent(PDFButton)
                    .addComponent(CancelButton)
                    .addComponent(DeleteButton)
                    .addComponent(AcceptButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void PDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFButtonActionPerformed
        if (NameTextField.getText().length() > 0) {
            Team t = new Team(NameTextField.getText(), listTournaments.get(TournamentComboBox.getSelectedIndex()));

            List<Competitor> participants = new ArrayList<>();

            for (int i = 0; i < competitorsPanel.size(); i++) {
                participants.add(competitors.get(competitorsPanel.get(i).competitorComboBox.getSelectedIndex()));
            }

            t.addMembers(participants, 0);

            try {
                String file;
                if (!(file = exploreWindowsForPdf(trans.returnTag("ExportPDF"),
                        JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                    TeamAccreditationCardPDF pdf = new TeamAccreditationCardPDF(t, listTournaments.get(TournamentComboBox.getSelectedIndex()));
                    pdf.createFile(file);
                }

            } catch (Exception ex) {
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }

        } else {
            MessageManager.errorMessage("noTeamFieldsFilled", "MySQL");
        }
    }//GEN-LAST:event_PDFButtonActionPerformed

    private void IndividualTeamsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IndividualTeamsCheckBoxActionPerformed
        individualTeams = !individualTeams;
        enablePlayers(!individualTeams);
    }//GEN-LAST:event_IndividualTeamsCheckBoxActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
            refreshTournament();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().database.deleteTeamByName(NameTextField.getText(), TournamentComboBox.getSelectedItem().toString(), true);
            refreshTournament();
            NameTextField.setText("");
            NameTextField.setEnabled(true);
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        if (newTeam) {
            if (individualTeams) {
                try {
                    if (KendoTournamentGenerator.getInstance().database.deleteTeamsOfTournament(TournamentComboBox.getSelectedItem().toString(), true)) {
                        KendoTournamentGenerator.getInstance().database.setIndividualTeams(TournamentComboBox.getSelectedItem().toString());
                        championship.teamSize = 1;
                        KendoTournamentGenerator.getInstance().database.updateTournament(championship, false);
                        MessageManager.informationMessage("teamsStored", "Team");
                        this.dispose();
                    }
                    //No competitors exist. 
                } catch (NullPointerException npe) {
                }
            } else {
                try {
                    if (NameTextField.getText().length() > 0) {
                        List<Competitor> participants = new ArrayList<>();
                        Team t = new Team(NameTextField.getText().trim(), listTournaments.get(TournamentComboBox.getSelectedIndex()));

                        for (int i = 0; i < competitorsPanel.size(); i++) {
                            participants.add(competitors.get(competitorsPanel.get(i).competitorComboBox.getSelectedIndex()));
                        }

                        if (repeatedCompetitor()) {
                            MessageManager.errorMessage("repeatedCompetitor", "League");
                        }
                        if (!checkTeam()) {
                            MessageManager.errorMessage("notEnoughCompetitors", "League");
                        } else {
                            t.addMembers(participants, 0);
                            if (KendoTournamentGenerator.getInstance().database.storeTeam(t, true)) {
                                cleanWindow();
                            }
                        }
                    } else {
                        MessageManager.errorMessage("noTeamFieldsFilled", "MySQL");
                    }
                } catch (NullPointerException | ArrayIndexOutOfBoundsException npe) {
                
                }
            }
            NameTextField.setEnabled(true);
        }
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton AcceptButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JPanel CompetitorsFrame;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JCheckBox IndividualTeamsCheckBox;
    private javax.swing.JLabel NameLabel;
    protected javax.swing.JTextField NameTextField;
    private javax.swing.JButton PDFButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JPanel TeamPanel;
    protected javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    // End of variables declaration//GEN-END:variables
}
