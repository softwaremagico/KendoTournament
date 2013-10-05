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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.fight.TeamFight;
import com.softwaremagico.ktg.persistence.TeamPool;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderTeam extends NewTeam {

    private int fightIndex;
    private Team team = null;
    private TeamFight windowParent = null;

    public OrderTeam(Tournament tournament, int fightIndex, TeamFight tf) {
        this.tournament = tournament;
        this.fightIndex = fightIndex;
        windowParent = tf;
        newTeam = false;
        start();
        setLanguage2(KendoTournamentGenerator.getInstance().getLanguage());
        this.addWindowListener(new closeWindows());
        refreshTournament = false;
        fillTournaments();
        this.tournament = tournament;
        //fillCompetitors();
        inidividualTeams();
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });
    }

    public OrderTeam(Tournament tournament, int fightIndex) {
        this.tournament = tournament;
        this.fightIndex = fightIndex;
        newTeam = false;
        start();
        setLanguage2(KendoTournamentGenerator.getInstance().getLanguage());
        competitors.add(0, new RegisteredPerson("", "", ""));
        this.addWindowListener(new closeWindows());
        refreshTournament = false;
        TournamentComboBox.addItem(tournament);
        //fillCompetitors();
        inidividualTeams();
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });
    }

    public final void setLanguage2(String language) {
        this.setTitle(trans.getTranslatedText("titleOrderTeam"));
    }

    public void updateOrderWindow(Team t) {
        team = t;
        try {
            if (t.realMembers() < tournament.getTeamSize()) {
                competitors.add(0, new RegisteredPerson("", "", ""));
            }
            NameTextField.setText(t.getName());
            addTeamCompetitorsSorted(t);
            fillCompetitorsComboBox();
            NameTextField.setEnabled(false);
            TournamentComboBox.setEnabled(false);
            for (int i = 0; i < competitorsPanel.size(); i++) {
                try {
                    if (i < t.getNumberOfMembers(fightIndex) && t.getMember(i, fightIndex) != null && (t.getMember(i, fightIndex).getSurname().length() > 0 || t.getMember(i, fightIndex).getName().length() > 0)) {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(t.getMember(i, fightIndex).getSurname() + ", " + t.getMember(i, fightIndex).getName() + " (" + t.getMember(i, fightIndex).getId() + ")");
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

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {
        List<RegisteredPerson> participants = new ArrayList<>();

        for (int i = 0; i < competitorsPanel.size(); i++) {
            participants.add(competitors.get(competitorsPanel.get(i).competitorComboBox.getSelectedIndex()));
        }

        if (repeatedCompetitor()) {
            AlertManager.errorMessage(this.getClass().getName(), "repeatedCompetitor", "League");
        } else {
            //Insert the change into the database.
            for (int i = 0; i < participants.size(); i++) {
                team.setMember(participants.get(i), i, fightIndex);
            }
            try {
                TeamPool.getInstance().update(tournament, team);
                AlertManager.informationMessage(this.getClass().getName(), "orderChanged", "League");
                this.dispose();
            } catch (SQLException ex) {
                AlertManager.showSqlErrorMessage(ex);
            }
        }
    }

    class closeWindows extends WindowAdapter {

        closeWindows() {
        }

        @Override
        public void windowClosed(WindowEvent evt) {
            if (windowParent != null) {
                windowParent.reorder();
            }
        }
    }
}
