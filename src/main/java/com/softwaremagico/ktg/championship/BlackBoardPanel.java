/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class BlackBoardPanel extends javax.swing.JPanel {

    private GridBagConstraints c = new GridBagConstraints();
    private String last_championship;
    private int titleColumn = 1;
    private int titleRow = 2;
    transient private Translator trans = new Translator("gui.xml");

    public BlackBoardPanel() {
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(255, 255, 255));
    }

    public void updateBlackBoard(String championshipName, boolean refill) {
        last_championship = championshipName;
        //removeAll();
        if (!refill) {
            KendoTournamentGenerator.getInstance().designedGroups.updateInnerLevel(0);
        } else {
            KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championshipName);
            KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(KendoTournamentGenerator.getInstance().database.getTournamentByName(championshipName, false), KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            ArrayList<Fight> fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
            KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(fights);
            KendoTournamentGenerator.getInstance().designedGroups.updateInnerLevel(0);
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


        //Separator ts = new Separator(last_championship, 24);

        GridBagConstraints lc = new GridBagConstraints();

        /*Label l = new Label(last_championship);
        l.setFont(new Font("sansserif", Font.BOLD, 24));
        lc.gridwidth = GridBagConstraints.REMAINDER;
        add(l, lc);*/

        Separator sp = new Separator(last_championship);
        sp.updateFont("sansserif", 44);
        lc.gridwidth = GridBagConstraints.REMAINDER;
        add(sp, lc);

        add(new Separator(), c);

        /* Paint information row*/
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); i++) {
            c.gridx = (i + 1) * 2;
            c.gridy = 1;

            Separator s;
            if (i < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - 2) {
                s = new Separator(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - i));
            } else if (i == KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - 2) {
                s = new Separator(trans.returnTag("SemiFinalLabel", KendoTournamentGenerator.getInstance().language));
            } else {
                s = new Separator(trans.returnTag("FinalLabel", KendoTournamentGenerator.getInstance().language));
            }
            s.updateFont("sansserif", 24);
            add(s, c);
        }

        /* Paint information column */
        List<DesignedGroup> grps = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(0);
        for (int i = 0; i < grps.size(); i++) {
            c.gridx = 0;
            c.gridy = i + 2;
            //int arena = (i) / (int) Math.ceil((double) grps.size() / (double) KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfArenas());

            Separator s = new Separator(trans.returnTag("GroupString", KendoTournamentGenerator.getInstance().language) + " " + (i + 1)
                    + "<br>" + trans.returnTag("ArenaString", KendoTournamentGenerator.getInstance().language) + " " + KendoTournamentGenerator.getInstance().returnShiaijo(grps.get(i).arena));
            s.updateFont("sansserif", 24);
            add(s, c);
        }

        /* Paint teams group */
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.size(); i++) {
            try {
                if (KendoTournamentGenerator.getInstance().designedGroups.get(i).championship.name.equals(last_championship)) {
                    c.anchor = GridBagConstraints.WEST;
                    c.gridx = KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(i) * 2 + 1 + titleColumn;
                    if (KendoTournamentGenerator.getInstance().designedGroups.default_max_winners < 2) {
                        c.gridy = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(i) * (int) (Math.pow(2, KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(i))) + titleRow;
                    } else {
                        if (KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(i) == 0) {
                            c.gridy = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(i) * (int) (Math.pow(2, KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(i))) + titleRow;
                        } else {
                            c.gridy = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(i) * (int) (Math.pow(2, KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(i) - 1)) + titleRow;
                        }
                    }

                    c.weightx = 0.5;
                    add(KendoTournamentGenerator.getInstance().designedGroups.get(i), c);
                    if (i == 0) {
                        KendoTournamentGenerator.getInstance().designedGroups.get(i).setSelected(KendoTournamentGenerator.getInstance().designedGroups);
                    }
                }
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            }
        }
    }

    private void paintSpaces() {
        for (int i = 1; i < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); i++) {
            c.gridx = i * 2 + titleColumn;
            c.gridy = 0;
            add(new Separator(), c);
        }
    }

    private void paintLinks(Graphics g) {
        int destination;
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - 1; i++) {
            List<DesignedGroup> designedGroupsOfLevel = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(i);
            List<DesignedGroup> designedGroupsOfNextLevel = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(i + 1);
            for (int j = 0; j < designedGroupsOfLevel.size(); j++) {
                for (int k = 0; k < designedGroupsOfLevel.get(j).returnMaxNumberOfWinners(); k++) {
                    if (designedGroupsOfNextLevel.size() > 1) {
                        if (!KendoTournamentGenerator.getInstance().designedGroups.mode.equals("manual") || i > 0) {
                            //    destination = (int) KendoTournamentGenerator.getInstance().fightManager.obtainPositonOfOneWinnerInTree(j, designedGroupsOfLevel.size()) / 2;
                            destination = (int) KendoTournamentGenerator.getInstance().designedGroups.obtainPositonOfOneWinnerInTournament(KendoTournamentGenerator.getInstance().designedGroups.obtainGlobalPositionWinner(i, j, k),
                                    KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfTotalTeamsPassNextRound(i), i);
                        } else {
                            destination = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(
                                    KendoTournamentGenerator.getInstance().designedGroups.returnIndexOfGroup(KendoTournamentGenerator.getInstance().designedGroups.obtainManualDestination(designedGroupsOfLevel.get(j), k)));
                        }
                    } else {
                        destination = 0;
                    }
                    if (destination < designedGroupsOfNextLevel.size() && destination >= 0) {
                        drawLink(g, designedGroupsOfLevel.get(j), designedGroupsOfNextLevel.get(destination), k, j, destination, j < designedGroupsOfLevel.size() / 2);
                    }
                }
            }
        }
    }

    private void paintArrow(Graphics g, double x0, double y0, double x1, double y1) {
        double deltaX = x1 - x0;
        double deltaY = y1 - y0;
        double frac = 0.5;
        double mult = 50;

        /* The head of the arrow depends on its size. We use a Normal vector to ensure all arrows have the same size of head. */
        double vectorLenght = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double deltaXUnit = deltaX / vectorLenght;
        double deltaYUnit = deltaY / vectorLenght;

        /* The normal vector is only of one pixel size, we enlarge it.  */
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
        List<DesignedGroup> grps = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(0);
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

    private void drawLink(Graphics g, DesignedGroup d1, DesignedGroup d2, int winner, int originNumber, int destinationNumber, boolean half) {
        Rectangle r1 = d1.getBounds();
        Rectangle r2 = d2.getBounds();
        g.setColor(d1.obtainWinnerColor(winner, false));
        if (d1.returnMaxNumberOfWinners() > 1) {
            //paintArrow(g, r1.x + r1.width, r1.y + ((r1.height / (d1.returnMaxNumberOfWinners() + 1)) * (winner + 1)), r2.x, r2.y + r2.height / 2);
            if ((originNumber < destinationNumber) || (half && originNumber == destinationNumber)) {
                paintArrow(g, r1.x + r1.width, r1.y + ((r1.height / (d1.returnMaxNumberOfWinners() + 1)) * (winner + 1)), r2.x, r2.y + r2.height / 2 - 4);
            } else {
                paintArrow(g, r1.x + r1.width, r1.y + ((r1.height / (d1.returnMaxNumberOfWinners() + 1)) * (winner + 1)), r2.x, r2.y + r2.height / 2 + 4);
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
