/*
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
 *   Created on 01-sep-2011.
 */
package com.softwaremagico.ktg.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.softwaremagico.ktg.gui.fight.TeamFight;
import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Team;

/**
 *
 * @author jorge
 */
public class OrderTeam extends NewTeam {

    private int level;
    Team team = null;
    TeamFight windowParent = null;

    public OrderTeam(String tourn, int levelOrder, TeamFight tf) {
        level = levelOrder;
        windowParent = tf;
        newTeam = false;
        start();
        setLanguage2(KendoTournamentGenerator.getInstance().language);
        this.addWindowListener(new closeWindows());
        refreshTournament = false;
        fillTournaments();
        championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(tourn, false);
        //fillCompetitors();
        inidividualTeams();
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed2(evt);
            }
        });
    }

    public OrderTeam(String tourn, int levelOrder) {
        level = levelOrder;
        newTeam = false;
        start();
        setLanguage2(KendoTournamentGenerator.getInstance().language);
        competitors.add(0, new Competitor("", "", "", ""));
        this.addWindowListener(new closeWindows());
        refreshTournament = false;
        TournamentComboBox.addItem(tourn);
        championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(tourn, false);
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
        this.setTitle(trans.returnTag("titleOrderTeam", language));
    }

    public void updateOrderWindow(Team t) {
        team = t;
        try {
            if (t.realMembers() < championship.teamSize) {
                competitors.add(0, new Competitor("", "", "", ""));
            }
            NameTextField.setText(t.returnName());
            AddTeamCompetitorsSorted(t);
            fillCompetitors();
            NameTextField.setEnabled(false);
            TournamentComboBox.setEnabled(false);
            for (int i = 0; i < competitorsPanel.size(); i++) {
                try {
                    if (i < t.getNumberOfMembers(level) && t.getMember(i, level) != null && (t.getMember(i, level).returnSurname().length() > 0 || t.getMember(i, level).returnName().length() > 0)) {
                        competitorsPanel.get(i).competitorComboBox.setSelectedItem(t.getMember(i, level).returnSurname() + ", " + t.getMember(i, level).returnName() + " (" + t.getMember(i, level).getId() + ")");
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

    private void AcceptButtonActionPerformed2(java.awt.event.ActionEvent evt) {
        try {
            List<Competitor> participants = new ArrayList<Competitor>();

            for (int i = 0; i < competitorsPanel.size(); i++) {
                participants.add(competitors.get(competitorsPanel.get(i).competitorComboBox.getSelectedIndex()));
            }

            if (repeatedCompetitor()) {
                MessageManager.errorMessage("repeatedCompetitor", "League", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } else {
                //Insert the change into the database.
                team.addMembers(participants, level);
                if (KendoTournamentGenerator.getInstance().database.insertMemebersOfTeamInLevel(team, level, false)) {
                    //Insert the change into the fights already loaded.
                    KendoTournamentGenerator.getInstance().fights.updateFightsWithNewOrderOfTeam(team);
                    MessageManager.customMessage("orderChanged", "League", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                    this.dispose();
                }
            }
        } catch (NullPointerException npe) {
        } catch (ArrayIndexOutOfBoundsException aiob) {
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
