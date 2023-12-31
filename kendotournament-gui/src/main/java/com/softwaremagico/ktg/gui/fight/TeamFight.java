package com.softwaremagico.ktg.gui.fight;
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

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.OrderTeam;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class TeamFight extends JPanel {
	private static final long serialVersionUID = -9000360981825238564L;
	private List<CompetitorFight> competitorFights = new ArrayList<>();
    private Fight fight;
    private final int lineBorder = 5;
    private RoundFight roundFight = null;
    private boolean wasDoubleClick = true;
    private Team team;
    private Timer timer;
    private boolean leftJustify, selectedFight, menuActive;
    private Tournament tournament;

    protected TeamFight(Tournament tournament, RoundFight rf, Team t, Fight f, boolean left, boolean selected, boolean menu, int fight_number, int fight_total, boolean invertedColor) {
        this.tournament = tournament;
        fight = f;
        roundFight = rf;
        team = t;

        leftJustify = left;
        selectedFight = selected;
        menuActive = menu;

        addMouseListener(new MouseAdapters());
        if (t != null) {
            decoration(left, t.getName().toUpperCase(), fight_number, fight_total, invertedColor);
            fill(t, left, selected, menu);
        }
    }

    TeamFight(Tournament tournament, boolean left, int teamSize, int fight_number, int fight_total, boolean invertedColor) {
        this.tournament = tournament;
        fight = null;
        decoration(left, " --- ", fight_number, fight_total, invertedColor);
        fill(left, teamSize);
    }
    
    public RoundFight getRoundFight(){
        return roundFight;
    }

    private void decoration(boolean left, String titleString, int fight_number, int fight_total, boolean invertedColor) {
        setLayout(new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        TitledBorder title;
        String number = "";
        if (fight_total > 0) {
            number = "(" + (fight_number + 1) + "/" + fight_total + ")";
        }
        if (left) {
            title = BorderFactory.createTitledBorder(number + " " + titleString);
        } else {
            title = BorderFactory.createTitledBorder(titleString + " " + number);
        }
        title.setTitleFont(new Font("Tahoma", Font.BOLD, 16));


        if (left) {
            title.setTitleJustification(TitledBorder.LEFT);

            if (invertedColor) {
                title.setBorder(BorderFactory.createLineBorder(Color.WHITE, lineBorder));
            } else {
                title.setBorder(BorderFactory.createLineBorder(new Color(255, 25, 25), lineBorder));
            }
        } else {
            title.setTitleJustification(TitledBorder.RIGHT);

            if (!invertedColor) {
                title.setBorder(BorderFactory.createLineBorder(Color.WHITE, lineBorder));
            } else {
                title.setBorder(BorderFactory.createLineBorder(new Color(255, 25, 25), lineBorder));
            }
        }

        setBorder(title);
    }

    private void addCompetitorFight(CompetitorFight cp) {
        add(cp, BorderLayout.WEST);
        competitorFights.add(cp);
        Dimension minSize = new Dimension(5, 0);
        Dimension prefSize = new Dimension(5, 5);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 50);
        add(new Box.Filler(minSize, prefSize, maxSize));
    }

    private final void fill(Team t, boolean left, boolean selected, boolean menu) {
        removeAll();
        competitorFights = new ArrayList<>();
        for (int i = 0; i < tournament.getTeamSize(); i++) {
            CompetitorFight cp = new CompetitorFight(this, t.getMember(i, fight.getIndex()), fight, left, selected, menu);
            addCompetitorFight(cp);
        }
        repaint();
        revalidate();
    }
    
    protected void updateCompetitorsName(int width){
        for(CompetitorFight cp : competitorFights){
            cp.updateCompetitorNameLength(width);
        }
    }

    private final void fill(boolean left, int teamSize) {
        removeAll();
        competitorFights = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            CompetitorFight cp = new CompetitorFight(left);
            addCompetitorFight(cp);
        }
        repaint();
        revalidate();
    }

    protected void updateScorePanel() {
        for (int i = 0; i < competitorFights.size(); i++) {
            competitorFights.get(i).updateScorePanels();
        }
    }

    private void showTeam() {
        //Are more than one member, and fight is not over, and there is not another fight in this level already done.
        if ((team.numberOfMembers() > 1) && (!fight.isOver())) {
            if (!TournamentManagerFactory.getManager(tournament).getGroup(fight).areFightsStarted()) {
                OrderTeam orderTeam;
                orderTeam = new OrderTeam(fight.getTournament(), fight.getIndex(), this);
                orderTeam.updateOrderWindow(team);
                orderTeam.setVisible(true);
            } else {
                AlertManager.errorMessage(this.getClass().getName(), "waitNewLevel", "Team");
            }
        }
    }

    public void reorder() {
        fill(team, leftJustify, selectedFight, menuActive);
        updateScorePanel();
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * When clicking to a box.
     */
    class MouseAdapters extends MouseAdapter {

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            //clicked(evt);  //Disabled reorder team in fight panel. 
        }
    }

    void clicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
            wasDoubleClick = true;

        } else {
            //Avoid to run the one-click functions when performing a doubleclick.
            Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
            timer = new Timer(timerinterval.intValue(), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (wasDoubleClick) {
                        showTeam();
                        wasDoubleClick = false; // reset flag
                    } else {
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}
