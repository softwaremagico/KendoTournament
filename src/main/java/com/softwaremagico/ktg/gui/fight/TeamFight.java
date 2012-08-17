package com.softwaremagico.ktg.gui.fight;
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

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.gui.OrderTeam;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Jorge
 */
public class TeamFight extends JPanel {

    private List<CompetitorFight> competitorFights = new ArrayList<>();
    private Fight fight;
    private final int lineBorder = 5;
    RoundFight roundFight = null;
    private boolean wasDoubleClick = true;
    Team team;
    Timer timer;
    private boolean leftJustify, selectedFight, menuActive;

    TeamFight(RoundFight rf, Team t, Fight f, boolean left, boolean selected, boolean menu, int fight_number, int fight_total) {
        fight = f;
        roundFight = rf;
        team = t;

        leftJustify = left;
        selectedFight = selected;
        menuActive = menu;

        addMouseListener(new MouseAdapters());
        if (t != null) {
            decoration(left, t.returnName().toUpperCase(), fight_number, fight_total);
            fill(t, left, selected, menu);
        }
    }

    TeamFight(boolean left, int teamSize, int fight_number, int fight_total) {
        fight = null;
        decoration(left, " --- ", fight_number, fight_total);
        fill(left, teamSize);
    }

    private void decoration(boolean left, String titleString, int fight_number, int fight_total) {
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

            if (KendoTournamentGenerator.getInstance().fightManager.inverseColours) {
                title.setBorder(BorderFactory.createLineBorder(new Color(255, 25, 25), lineBorder));
            } else {
                title.setBorder(BorderFactory.createLineBorder(Color.WHITE, lineBorder));
            }
        } else {
            title.setTitleJustification(TitledBorder.RIGHT);

            if (!KendoTournamentGenerator.getInstance().fightManager.inverseColours) {
                title.setBorder(BorderFactory.createLineBorder(new Color(255, 25, 25), lineBorder));
            } else {
                title.setBorder(BorderFactory.createLineBorder(Color.WHITE, lineBorder));
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

    final void fill(Team t, boolean left, boolean selected, boolean menu) {
        removeAll();
        competitorFights = new ArrayList<>();
        for (int i = 0; i < t.getNumberOfMembers(fight.level); i++) {
            CompetitorFight cp = new CompetitorFight(this, t.getMember(i, fight.level), fight, left, selected, menu);
            addCompetitorFight(cp);
        }
        repaint();
        revalidate();
    }

    final void fill(boolean left, int teamSize) {
        removeAll();
        competitorFights = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            CompetitorFight cp = new CompetitorFight(left);
            addCompetitorFight(cp);
        }
        repaint();
        revalidate();
    }

    void updateScorePanel() {
        for (int i = 0; i < competitorFights.size(); i++) {
            competitorFights.get(i).updateScorePanels();
        }
    }

    private void showTeam() {
        //Are more than one member, and fight is not over, and there is not another fight in this level already done.
        if ((team.numberOfMembers() > 1) && (!fight.isOver())) {
            if (!KendoTournamentGenerator.getInstance().fightManager.someFightWithTeamAndLevelIsStarted(team, fight.level)) {
                OrderTeam orderTeam;
                orderTeam = new OrderTeam(fight.tournament, fight.level, this);
                orderTeam.updateOrderWindow(team);
                orderTeam.setVisible(true);
            }else{
                MessageManager.errorMessage("waitNewLevel", "Team");
            }
        }
    }

    public void reorder() {
        fill(team, leftJustify, selectedFight, menuActive);
        updateScorePanel();
    }

    /************************************************
     *
     *                    LISTENERS
     *
     ************************************************/
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
