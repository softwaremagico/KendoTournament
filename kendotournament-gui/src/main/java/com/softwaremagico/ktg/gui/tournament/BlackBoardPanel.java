package com.softwaremagico.ktg.gui.tournament;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
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
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.CustomChampionship;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.Timer;

public class BlackBoardPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -6193257530262904629L;
    private GridBagConstraints c = new GridBagConstraints();
    private Tournament tournament;
    private static int TITLE_COLUMN = 1;
    private static int TITLE_ROW = 2;
    transient private Translator trans = LanguagePool.getTranslator("gui.xml");
    private HashMap<Integer, List<TournamentGroupBox>> grpsBox;  //GroupBox per level.
    private Integer selectedGroupIndex = null;
    private Timer timer;
    private boolean wasDoubleClick = true;
    private LeagueDesigner parent;
    private boolean interactive = false;

    public BlackBoardPanel(LeagueDesigner parent, boolean interactive) {
        this.parent = parent;
        this.interactive = interactive;
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(255, 255, 255));
    }

    public void update(Tournament tournament) {
        this.tournament = tournament;
        paintDesignedGroups();
        paintSpaces();
    }

    public void clearBlackBoard() {
        removeAll();
    }

    private void paintDesignedGroups() {
        Integer lastSelectedGroupIndex = getSelectedBoxIndex();
        removeAll();
        grpsBox = new HashMap<>();
        //Simple tournament is not defined in the blackboard. 
        if (!tournament.isChampionship()) {
            return;
        }
        c.gridx = 0;
        c.gridy = 1;

        GridBagConstraints lc = new GridBagConstraints();

        Separator sp = new Separator(tournament.getName());
        sp.updateFont("sansserif", 44);
        lc.gridwidth = GridBagConstraints.REMAINDER;
        add(sp, lc);

        add(new Separator(), c);

        /*
         * Paint information row
         */
        for (int i = 0; i < TournamentManagerFactory.getManager(tournament).getNumberOfLevels(); i++) {
            c.gridx = (i + 1) * 2;
            c.gridy = 1;

            Separator s;
            if (i < TournamentManagerFactory.getManager(tournament).getNumberOfLevels() - 2) {
                s = new Separator(trans.getTranslatedText("Round") + " " + (TournamentManagerFactory.getManager(tournament).getNumberOfLevels() - i));
            } else if (i == TournamentManagerFactory.getManager(tournament).getNumberOfLevels() - 2) {
                s = new Separator(trans.getTranslatedText("SemiFinalLabel"));
            } else {
                s = new Separator(trans.getTranslatedText("FinalLabel"));
            }
            s.updateFont("sansserif", 24);
            add(s, c);
        }

        /*
         * Paint information column
         */
        List<TGroup> grps = TournamentManagerFactory.getManager(tournament).getGroups(0);
        if (grps != null) {
            for (int i = 0; i < grps.size(); i++) {
                c.gridx = 0;
                c.gridy = i + 2;

                Separator s = new Separator(trans.getTranslatedText("GroupString") + " " + (i + 1)
                        + "<br>" + trans.getTranslatedText("ArenaString") + " "
                        + KendoTournamentGenerator.getFightAreaName(grps.get(i).getFightArea()));
                s.updateFont("sansserif", 24);
                add(s, c);
            }
        }

        /*
         * Paint teams group
         */
        for (int level = 0; level < TournamentManagerFactory.getManager(tournament).getNumberOfLevels(); level++) {
            for (int groupIndex = 0; groupIndex < TournamentManagerFactory.getManager(tournament).getGroups(level).size(); groupIndex++) {
                try {
                    if (TournamentManagerFactory.getManager(tournament).getGroups(level).get(groupIndex).getTournament().equals(tournament)) {
                        c.anchor = GridBagConstraints.WEST;
                        c.gridx = level * 2 + 1 + TITLE_COLUMN;
                        if (tournament.getHowManyTeamsOfGroupPassToTheTree() < 2) {
                            c.gridy = groupIndex * (int) (Math.pow(2, level)) + TITLE_ROW;
                        } else {
                            if (level == 0) {
                                c.gridy = groupIndex * (int) (Math.pow(2, level)) + TITLE_ROW;
                            } else {
                                c.gridy = groupIndex * (int) (Math.pow(2, level - 1)) + TITLE_ROW;
                            }
                        }

                        c.weightx = 0.5;
                        TournamentGroupBox tournamentGroupBox = createBox(TournamentManagerFactory.getManager(tournament).getGroups(level).get(groupIndex), level);
                        add(tournamentGroupBox, c);
                        if (level == 0 && groupIndex == 0 && interactive) {
                            tournamentGroupBox.setSelected();
                        }
                    }
                } catch (NullPointerException npe) {
                    AlertManager.showErrorInformation(this.getClass().getName(), npe);
                }
            }
        }
            selectGroup(lastSelectedGroupIndex);
    }

    private TournamentGroupBox createBox(TGroup tournamentGroup, Integer level) {
        TournamentGroupBox tournamentGroupBox = new TournamentGroupBox(tournamentGroup);
        List<TournamentGroupBox> list = grpsBox.get(level);
        if (list == null) {
            list = new ArrayList<>();
            grpsBox.put(level, list);
        }
        list.add(tournamentGroupBox);
        if (interactive) {
            tournamentGroupBox.addMouseClickListener(new MouseAdapters(tournamentGroupBox));
            tournamentGroupBox.activateColor(false);
        } else {
            tournamentGroupBox.activateColor(true);
        }
        tournamentGroupBox.setUnselected();
        return tournamentGroupBox;
    }

    private void paintSpaces() {
        for (int i = 1; i < TournamentManagerFactory.getManager(tournament).getNumberOfLevels(); i++) {
            c.gridx = i * 2 + TITLE_COLUMN;
            c.gridy = 0;
            add(new Separator(), c);
        }
    }

    private void paintLinks(Graphics g) {
        Integer destination;
        try {
            if (grpsBox.size() > 1) {
                for (int i = 0; i < grpsBox.size() - 1; i++) {
                    List<TournamentGroupBox> designedGroupsFromLevel = grpsBox.get(i);
                    List<TournamentGroupBox> designedGroupsToLevel = grpsBox.get(i + 1);
                    for (int j = 0; j < designedGroupsFromLevel.size(); j++) {
                        for (int winners = 0; winners < designedGroupsFromLevel.get(j).getMaxNumberOfWinners(); winners++) {
                            if (designedGroupsToLevel.size() > 0) {
                                destination = TournamentManagerFactory.getManager(tournament).getLevel(i).getGroupIndexDestinationOfWinner(designedGroupsFromLevel.get(j).getTournamentGroup(), winners);
                            } else { //Final group
                                destination = null;
                            }
                            if (destination != null && destination < designedGroupsToLevel.size() && destination >= 0) {
                                drawLink(g, designedGroupsFromLevel.get(j), designedGroupsToLevel.get(destination), winners, j, destination, j < designedGroupsFromLevel.size() / 2);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            AlertManager.showErrorInformation(this.getClass().getName(), e);
        }
    }

    private void paintArrow(Graphics g, double x0, double y0, double x1, double y1) {
        double deltaX = x1 - x0;
        double deltaY = y1 - y0;
        double frac = 0.5;
        double mult = 50;

        /*
         * The head of the arrow depends on its size. We use a Normal vector to
         * ensure all arrows have the same size of head.
         */
        double vectorLenght = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double deltaXUnit = deltaX / vectorLenght;
        double deltaYUnit = deltaY / vectorLenght;

        /*
         * The normal vector is only of one pixel size, we enlarge it.
         */
        deltaX = deltaXUnit * mult;
        deltaY = deltaYUnit * mult;

        g.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
        int xpoints[] = {(int) x1,
            (int) x1 - (int) ((1 - frac) * deltaX + frac / 3 * deltaY),
            //(int) x1 - (int) ((1 - frac / 2) * deltaX),
            (int) x1 - (int) ((1 - frac) * deltaX - frac / 3 * deltaY)
        };
        int ypoints[] = {(int) y1,
            (int) y1 - (int) ((1 - frac) * deltaY - frac / 3 * deltaX),
            (int) y1 - (int) ((1 - frac) * deltaY + frac / 3 * deltaX)
        };

        int npoints = 3;
        g.fillPolygon(xpoints, ypoints, npoints);
    }

    private void paintSeparationLines(Graphics g) {
        g.setColor(Color.black);
        List<TournamentGroupBox> groupList = grpsBox.get(0);
        if (groupList != null) {
            for (int i = 1; i < groupList.size(); i++) {
                if (groupList.get(i).getTournamentGroup().getFightArea() != groupList.get(i - 1).getTournamentGroup().getFightArea()) {
                    g.drawLine(0, (int) groupList.get(i).getY(), (int) this.getWidth(), (int) groupList.get(i).getY());
                }
            }
        }
    }

    private void customPaintingMethod(Graphics g) {
        try {
            //paintDesignedGroups();
            paintSpaces();
            paintLinks(g);
            paintSeparationLines(g);
        } catch (NullPointerException npe) {
        }
    }

    private void drawLink(Graphics g, TournamentGroupBox d1, TournamentGroupBox d2, int winner, int originNumber, int destinationNumber, boolean half) {
        Rectangle r1 = d1.getBounds();
        Rectangle r2 = d2.getBounds();
        g.setColor(d1.obtainWinnerColor(winner, false));
        if (d1.getMaxNumberOfWinners() > 1) {
            //paintArrow(g, r1.x + r1.width, r1.y + ((r1.height / (d1.getMaxNumberOfWinners() + 1)) * (winner + 1)), r2.x, r2.y + r2.height / 2);
            if ((originNumber < destinationNumber) || (half && originNumber == destinationNumber)) {
                paintArrow(g, r1.x + r1.width, r1.y + ((r1.height / (d1.getMaxNumberOfWinners() + 1)) * (winner + 1)), r2.x, r2.y + r2.height / 2 - 4);
            } else {
                paintArrow(g, r1.x + r1.width, r1.y + ((r1.height / (d1.getMaxNumberOfWinners() + 1)) * (winner + 1)), r2.x, r2.y + r2.height / 2 + 4);
            }
        } else {
            //paintArrow(g, r1.x + r1.width, r1.y + r1.height / 2, r2.x, r2.y + r2.height / 2);
            if (originNumber % 2 == 0) {
                paintArrow(g, r1.x + r1.width, r1.y + r1.height / 2, r2.x, r2.y + r2.height / 2 - 4);
            } else {
                paintArrow(g, r1.x + r1.width, r1.y + r1.height / 2, r2.x, r2.y + r2.height / 2 + 4);
            }
        }
    }

    public void selectGroup(Integer index) {
        if (index != null && interactive && grpsBox.size() > 0 && index < grpsBox.get(0).size()) {
            for (TournamentGroupBox grpBox : grpsBox.get(0)) {
                grpBox.setUnselected();
            }
            grpsBox.get(0).get(index).setSelected();
            selectedGroupIndex = index;
        } else {
            if (grpsBox.size() > 0) {
                selectedGroupIndex = 0;
            }
        }
    }

    public TournamentGroupBox getSelectedBox() {
        try {
            if (selectedGroupIndex != null && selectedGroupIndex >= 0) {
                return grpsBox.get(0).get(selectedGroupIndex);
            }
        } catch (NullPointerException npe) {
        }
        return null;
    }

    public Integer getSelectedBoxIndex() {
        if (selectedGroupIndex != null && grpsBox != null && grpsBox.get(0) != null
                && selectedGroupIndex >= 0 && selectedGroupIndex < grpsBox.get(0).size()) {
            return selectedGroupIndex;
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        customPaintingMethod(g);
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

        TournamentGroupBox designedFight;

        MouseAdapters(TournamentGroupBox d) {
            designedFight = d;
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            clicked(evt, designedFight);
        }
    }

    void clicked(java.awt.event.MouseEvent e, TournamentGroupBox group) {

        if (group.getTournamentGroup().getLevel() == 0) {
            if (e.getClickCount() == 2) {
                if (parent != null) {
                    group.openDesignGroupWindow(parent);
                }
                wasDoubleClick = true;
            } else {
                //Avoid to run the one-click functions when performing a doubleclick.
                Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
                timer = new Timer(timerinterval.intValue(), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (wasDoubleClick) {
                            wasDoubleClick = false; // reset flag
                        } else {
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
            selectedGroupIndex = grpsBox.get(0).indexOf(group);
            selectGroup(getSelectedBoxIndex());

        } else if (group.getTournamentGroup().getLevel() == 1 && group.getTournamentGroup().getTeams().isEmpty()) {
            //Clicking in the second level is only useful for defining links and the tournament has not started. 
            if (tournament.getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
                CustomChampionship championship = (CustomChampionship) TournamentManagerFactory.getManager(tournament);
                championship.addLink(getSelectedBox().getTournamentGroup(), group.getTournamentGroup());
                this.revalidate();
                this.repaint();
            }
        }
    }
}
