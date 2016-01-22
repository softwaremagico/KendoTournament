package com.softwaremagico.ktg.gui;
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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.KendoFrame;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.lists.TeamAccreditationCardPDF;
import com.softwaremagico.ktg.persistence.AutoSaveByAction;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.persistence.TournamentPool;

public class NewTeam extends KendoFrame {
	private static final long serialVersionUID = -6984131015475977589L;
	private static Integer FIGHT = 0;
    protected ITranslator trans = null;
    protected List<RegisteredPerson> competitors = new ArrayList<>();
    private List<Tournament> tournaments = new ArrayList<>();
    protected boolean refreshTournament = true;
    private boolean individualTeams = false;
    protected List<CompetitorPanel> competitorsPanel = new ArrayList<>();
    protected Tournament tournament = null;
    protected boolean newTeam = true; //To avoid that OrderTeam also use the event . 
    private Team oldTeam = null;

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
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });
    }

    public void fill() {
        fillTournaments();
        refreshCompetitors();
        NameTextField.setEnabled(true);
        try {

            refreshTournament();
            fillCompetitorsComboBox();
            inidividualTeams();
        } catch (NullPointerException npe) {
        }
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleNewTeam"));
        AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        SearchButton.setText(trans.getTranslatedText("SearchButton"));
        CleanButton.setText(trans.getTranslatedText("CancelButton"));
        PDFButton.setText(trans.getTranslatedText("AccreditationPDFButton"));
        NameLabel.setText(trans.getTranslatedText("NameTeamLabel"));
        IndividualTeamsCheckBox.setText(trans.getTranslatedText("IndividualTeamsCheckBox"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
    }

    void fillTournaments() {
        refreshTournament = false;
        try {
            tournaments = TournamentPool.getInstance().getSorted();
            for (int i = 0; i < tournaments.size(); i++) {
                TournamentComboBox.addItem(tournaments.get(i));
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
        tournament = (Tournament) TournamentComboBox.getSelectedItem();
    }

    protected void fillCompetitorsComboBox() {
        competitorsPanel = new ArrayList<>();
        for (int i = 0; i < tournament.getTeamSize(); i++) {
            CompetitorPanel cp = new CompetitorPanel(i + 1);

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
        oldTeam = null;
        NameTextField.setText("");
        TournamentComboBox.setEnabled(true);
    }

    public final void updateWindow(Team team) {
        try {
            oldTeam = team;
            NameTextField.setText(team.getName());
            TournamentComboBox.setSelectedItem(team.getTournament());
            addTeamCompetitorsSorted(team);
            fillCompetitorsComboBox();
            inidividualTeams();
            NameTextField.setEnabled(false);
            TournamentComboBox.setEnabled(false);
            for (int i = 0; i < competitorsPanel.size(); i++) {
                try {
                    if (i < team.getNumberOfMembers(0) && team.getMember(i, 0) != null && (team.getMember(i, 0).getSurname().length() > 0 || team.getMember(i, 0).getName().length() > 0)) {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(team.getMember(i, 0).getSurname() + ", " + team.getMember(i, 0).getName() + " (" + team.getMember(i, 0).getId() + ")");
                    } else {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                    }
                } catch (NullPointerException | IndexOutOfBoundsException npe) {
                    AlertManager.showErrorInformation(this.getClass().getName(), npe);
                    competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                }
            }
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
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
            competitors = TeamPool.getInstance().getCompetitorsWithoutTeam(tournament);
            competitors.add(0, new RegisteredPerson("", "", ""));
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }

    private void refreshTournament() {
        refreshCompetitors();
        fillCompetitorsComboBox();
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
        if (tournament.getTeamSize() == 1) {
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

    protected void addTeamCompetitorsSorted(Team t) {
        List<RegisteredPerson> teamCompetitors = new ArrayList<>(t.getMembersOrder(0).values());
        competitors.addAll(teamCompetitors);
        Collections.sort(competitors);
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

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (newTeam) {
            if (individualTeams) {
                try {
                    TeamPool.getInstance().setIndividualTeams(tournament);
                    this.dispose();
                } catch (SQLException ex) {
                    AlertManager.showSqlErrorMessage(ex);
                } 
            } else {
                if (NameTextField.getText().length() > 0) {
                    List<RegisteredPerson> participants = new ArrayList<>();
                    Team team = new Team(NameTextField.getText().trim(), tournaments.get(TournamentComboBox.getSelectedIndex()));

                    for (CompetitorPanel competitorsPanel1 : competitorsPanel) {
                        participants.add(competitors.get(competitorsPanel1.competitorComboBox.getSelectedIndex()));
                    }

                    if (repeatedCompetitor()) {
                        AlertManager.errorMessage(this.getClass().getName(), "repeatedCompetitor", "League");
                    } else if (!checkTeam()) {
                        AlertManager.errorMessage(this.getClass().getName(), "notEnoughCompetitors", "League");
                    } else {
                        for (int i = 0; i < participants.size(); i++) {
                            team.setMember(participants.get(i), i);
                        }
                        //Insert or update?
                        try {
                            if (oldTeam != null) {
                                if (TeamPool.getInstance().update(tournament, oldTeam, team)) {
                                    AlertManager.informationMessage(this.getClass().getName(), "teamUpdated", "Team");
                                    AutoSaveByAction.getInstance().save();
                                }
                            } else {
                                if (TeamPool.getInstance().add(tournament, team)) {
                                    AlertManager.informationMessage(this.getClass().getName(), "teamStored", "Team");
                                    AutoSaveByAction.getInstance().save();
                                } else {
                                    AlertManager.informationMessage(this.getClass().getName(), "repatedTeam", "Team");
                                }
                            }
                        } catch (SQLException ex) {
                            AlertManager.showSqlErrorMessage(ex);
                        }
                        cleanWindow();
                        refreshTournament();
                    }
                } else {
                    AlertManager.errorMessage(this.getClass().getName(), "noTeamFieldsFilled", "MySQL");
                }
            }
            NameTextField.setEnabled(true);
            NameTextField.requestFocusInWindow();
        }
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
        TournamentComboBox = new javax.swing.JComboBox();
        TournamentLabel = new javax.swing.JLabel();
        IndividualTeamsCheckBox = new javax.swing.JCheckBox();
        CompetitorsFrame = new javax.swing.JPanel();
        AcceptButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        SearchButton = new javax.swing.JButton();
        PDFButton = new javax.swing.JButton();
        CleanButton = new javax.swing.JButton();

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

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        SearchButton.setText("Search");

        PDFButton.setText("PDF");
        PDFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFButtonActionPerformed(evt);
            }
        });

        CleanButton.setText("Clean");
        CleanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CleanButtonActionPerformed(evt);
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
                        .addComponent(CleanButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CloseButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CleanButton, CloseButton, PDFButton, SearchButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TeamPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchButton)
                    .addComponent(PDFButton)
                    .addComponent(CloseButton)
                    .addComponent(CleanButton)
                    .addComponent(AcceptButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void PDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFButtonActionPerformed
        if (NameTextField.getText().length() > 0) {
            Team t = new Team(NameTextField.getText(), tournaments.get(TournamentComboBox.getSelectedIndex()));
            //Team t = TeamPool.getManager(tournaments.get(TournamentComboBox.getSelectedIndex())).getTeam(NameTextField.getText());

            for (int i = 0; i < competitorsPanel.size(); i++) {
                t.setMember(competitors.get(competitorsPanel.get(i).competitorComboBox.getSelectedIndex()), i);
            }

            try {
                String file;
                if (!(file = exploreWindowsForPdf(trans.getTranslatedText("ExportPDF"),
                        JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                    TeamAccreditationCardPDF pdf = new TeamAccreditationCardPDF(t, tournaments.get(TournamentComboBox.getSelectedIndex()));
                    pdf.createFile(file);
                }

            } catch (Exception ex) {
                AlertManager.showErrorInformation(this.getClass().getName(), ex);
            }

        } else {
            AlertManager.errorMessage(this.getClass().getName(), "noTeamFieldsFilled", "MySQL");
        }
    }//GEN-LAST:event_PDFButtonActionPerformed

    private void IndividualTeamsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IndividualTeamsCheckBoxActionPerformed
        individualTeams = !individualTeams;
        enablePlayers(!individualTeams);
    }//GEN-LAST:event_IndividualTeamsCheckBoxActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().setLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            tournament = (Tournament) TournamentComboBox.getSelectedItem();
            refreshTournament();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void CleanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanButtonActionPerformed
        cleanWindow();
        refreshTournament();
    }//GEN-LAST:event_CleanButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton AcceptButton;
    private javax.swing.JButton CleanButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JPanel CompetitorsFrame;
    private javax.swing.JCheckBox IndividualTeamsCheckBox;
    private javax.swing.JLabel NameLabel;
    protected javax.swing.JTextField NameTextField;
    private javax.swing.JButton PDFButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JPanel TeamPanel;
    protected javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    // End of variables declaration//GEN-END:variables
}
