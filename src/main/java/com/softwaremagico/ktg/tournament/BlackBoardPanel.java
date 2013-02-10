package com.softwaremagico.ktg.tournament;
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

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.FightPool;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.util.List;

public class BlackBoardPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -6193257530262904629L;
    private GridBagConstraints c = new GridBagConstraints();
    private Tournament tournament;
    private int titleColumn = 1;
    private int titleRow = 2;
    transient private Translator trans = LanguagePool.getTranslator("gui.xml");

    public BlackBoardPanel() {
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(255, 255, 255));
    }

    public void updateBlackBoard(Tournament tournament, boolean refill) {
        this.tournament = tournament;
        //removeAll();
        if (!refill) {
            //TournamentGroupPool.getManager(selectedTournament).updateInnerLevel(0);
        } else {
            FightPool.getManager(tournament).getFightsFromDatabase(tournament);
            List<Fight> fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournament(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        }
        paintDesignedGroups();
        paintSpaces();
    }

    public void clearBlackBoard() {
        this.removeAll();
    }

    private void paintDesignedGroups() {
        removeAll();
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
        for (int i = 0; i < TournamentGroupPool.getManager(tournament).getLevels().size(); i++) {
            c.gridx = (i + 1) * 2;
            c.gridy = 1;

            Separator s;
            if (i < TournamentGroupPool.getManager(tournament).getLevels().size() - 2) {
                s = new Separator(trans.returnTag("Round") + " " + (TournamentGroupPool.getManager(tournament).getLevels().size() - i));
            } else if (i == TournamentGroupPool.getManager(tournament).getLevels().size() - 2) {
                s = new Separator(trans.returnTag("SemiFinalLabel"));
            } else {
                s = new Separator(trans.returnTag("FinalLabel"));
            }
            s.updateFont("sansserif", 24);
            add(s, c);
        }

        /*
         * Paint information column
         */
        List<TournamentGroup> grps = TournamentGroupPool.getManager(tournament).returnGroupsOfLevel(0);
        if (grps != null) {
            for (int i = 0; i < grps.size(); i++) {
                c.gridx = 0;
                c.gridy = i + 2;
                //int arena = (i) / (int) Math.ceil((double) grps.size() / (double) TournamentGroupPool.getManager(selectedTournament).returnNumberOfArenas());

                Separator s = new Separator(trans.returnTag("GroupString") + " " + (i + 1)
                        + "<br>" + trans.returnTag("ArenaString") + " " + KendoTournamentGenerator.getInstance().returnShiaijo(grps.get(i).arena));
                s.updateFont("sansserif", 24);
                add(s, c);
            }
        }

        /*
         * Paint teams group
         */
        try {
            for (int level = 0; level < TournamentGroupPool.getManager(tournament).getLevels().size(); level++) {
                for (int groupIndex = 0; groupIndex < TournamentGroupPool.getManager(tournament).getLevels().get(level).getGroups().size(); groupIndex++) {
                    try {
                        if (TournamentGroupPool.getManager(tournament).getLevels().get(level).getGroups().get(groupIndex).tournament.equals(tournament)) {
                            c.anchor = GridBagConstraints.WEST;
                            c.gridx = level * 2 + 1 + titleColumn;
                            if (TournamentGroupPool.getManager(tournament).default_max_winners < 2) {
                                c.gridy = groupIndex * (int) (Math.pow(2, level)) + titleRow;
                            } else {
                                if (level == 0) {
                                    c.gridy = groupIndex * (int) (Math.pow(2, level)) + titleRow;
                                } else {
                                    c.gridy = groupIndex * (int) (Math.pow(2, level - 1)) + titleRow;
                                }
                            }

                            c.weightx = 0.5;
                            add(TournamentGroupPool.getManager(tournament).getLevels().get(level).getGroups().get(groupIndex), c);
                            if (level == 0 && groupIndex == 0) {
                                TournamentGroupPool.getManager(tournament).getLevels().get(level).getGroups().get(groupIndex).setSelected(TournamentGroupPool.getManager(tournament));
                            }
                        }
                    } catch (NullPointerException npe) {
                        KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
                    }
                }
            }
        } catch (ClassCastException e) {
        }
    }

    private void paintSpaces() {
        for (int i = 1; i < TournamentGroupPool.getManager(tournament).getLevels().size(); i++) {
            c.gridx = i * 2 + titleColumn;
            c.gridy = 0;
            add(new Separator(), c);
        }
    }

    private void paintLinks(Graphics g) {
        Integer destination;
        try {
            for (int i = 0; i < TournamentGroupPool.getManager(tournament).getLevels().size(); i++) {
                List<TournamentGroup> designedGroupsFromLevel = TournamentGroupPool.getManager(tournament).returnGroupsOfLevel(i);
                List<TournamentGroup> designedGroupsToLevel = TournamentGroupPool.getManager(tournament).returnGroupsOfLevel(i + 1);
                if (designedGroupsToLevel != null) {
                    for (int j = 0; j < designedGroupsFromLevel.size(); j++) {
                        for (int winners = 0; winners < designedGroupsFromLevel.get(j).getMaxNumberOfWinners(); winners++) {
                            if (designedGroupsToLevel.size() > 1) {
                                destination = TournamentGroupPool.getManager(tournament).getLevels().get(i).getGroupIndexDestinationOfWinner(designedGroupsFromLevel.get(j), winners);
                            } else { //Final group
                                destination = 0;
                            }
                            if (destination != null && destination < designedGroupsToLevel.size() && destination >= 0) {
                                drawLink(g, designedGroupsFromLevel.get(j), designedGroupsToLevel.get(destination), winners, j, destination, j < designedGroupsFromLevel.size() / 2);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), e);
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
            //(int) y1 - (int) ((1 - frac / 2) * deltaY),
            (int) y1 - (int) ((1 - frac) * deltaY + frac / 3 * deltaX)
        };

        int npoints = 3;
        //System.out.println("(" + xpoints[0] + "," + ypoints[0] + ")" + " " + "(" + xpoints[1] + "," + ypoints[1] + ")" + " " + "(" + xpoints[2] + "," + ypoints[2] + ")");
        g.fillPolygon(xpoints, ypoints, npoints);
    }

    private void paintSeparationLines(Graphics g) {
        List<TournamentGroup> grps = TournamentGroupPool.getManager(tournament).returnGroupsOfLevel(0);
        g.setColor(Color.black);
        for (int i = 0; i < grps.size(); i++) {
            if (i > 0) {
                if (grps.get(i).arena != grps.get(i - 1).arena) {
                    g.drawLine((int) 0, (int) grps.get(i).getY(), (int) this.getWidth(), (int) (int) grps.get(i).getY());
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

    private void drawLink(Graphics g, TournamentGroup d1, TournamentGroup d2, int winner, int originNumber, int destinationNumber, boolean half) {
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        customPaintingMethod(g);
    }
}
