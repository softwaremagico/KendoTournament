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

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.gui.fight.TeamFight;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge
 */
public class OrderTeam extends NewTeam {

    private int level;
    Team team = null;
    TeamFight windowParent = null;

    public OrderTeam(Tournament tournament, int levelOrder, TeamFight tf) {
        level = levelOrder;
        windowParent = tf;
        newTeam = false;
        start();
        setLanguage2(KendoTournamentGenerator.getInstance().language);
        this.addWindowListener(new closeWindows());
        refreshTournament = false;
        fillTournaments();
        this.tournament = tournament;
        //fillCompetitors();
        inidividualTeams();
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed2(evt);
            }
        });
    }

    public OrderTeam(Tournament tournament, int levelOrder) {
        level = levelOrder;
        newTeam = false;
        start();
        setLanguage2(KendoTournamentGenerator.getInstance().language);
        competitors.add(0, new Competitor("", "", "", ""));
        this.addWindowListener(new closeWindows());
        refreshTournament = false;
        TournamentComboBox.addItem(tournament);
        //fillCompetitors();
        inidividualTeams();
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed2(evt);
            }
        });
    }

    public final void setLanguage2(String language) {
        this.setTitle(trans.returnTag("titleOrderTeam"));
    }

    public void updateOrderWindow(Team t) {
        team = t;
        try {
            if (t.realMembers() < tournament.teamSize) {
                competitors.add(0, new Competitor("", "", "", ""));
            }
            NameTextField.setText(t.getName());
            AddTeamCompetitorsSorted(t);
            fillCompetitors();
            NameTextField.setEnabled(false);
            TournamentComboBox.setEnabled(false);
            for (int i = 0; i < competitorsPanel.size(); i++) {
                try {
                    if (i < t.getNumberOfMembers(level) && t.getMember(i, level) != null && (t.getMember(i, level).getSurname().length() > 0 || t.getMember(i, level).getName().length() > 0)) {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(t.getMember(i, level).getSurname() + ", " + t.getMember(i, level).getName() + " (" + t.getMember(i, level).getId() + ")");
                    } else {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                    }
                } catch (NullPointerException | IndexOutOfBoundsException npe) {
                    KendoTournamentGenerator.getInstance().showErrorInformation(npe);
                    competitorsPanel.get(i).competitorComboBox.setSelectedItem(" ");
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    private void AcceptButtonActionPerformed2(java.awt.event.ActionEvent evt) {
        try {
            List<Competitor> participants = new ArrayList<>();

            for (int i = 0; i < competitorsPanel.size(); i++) {
                participants.add(competitors.get(competitorsPanel.get(i).competitorComboBox.getSelectedIndex()));
            }

            if (repeatedCompetitor()) {
                MessageManager.errorMessage("repeatedCompetitor", "League");
            } else {
                //Insert the change into the database.
                team.addMembers(participants, level);
                if (KendoTournamentGenerator.getInstance().database.insertMemebersOfTeamInLevel(team, level, false)) {
                    //Insert the change into the fightManager already loaded.
                    FightPool.getManager(KendoTournamentGenerator.getInstance().getLastSelectedTournament()).updateFightsWithNewOrderOfTeam(team);
                    MessageManager.informationMessage("orderChanged", "League");
                    this.dispose();
                }
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException npe) {
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
