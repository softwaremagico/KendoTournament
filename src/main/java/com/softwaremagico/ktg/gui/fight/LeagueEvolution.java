/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 18-ago-2009.
 */
package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.leaguedesigner.BlackBoardPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JScrollBar;
import javax.swing.Timer;

/**
 *
 * @author Jorge
 */
public class LeagueEvolution extends javax.swing.JFrame {

    BlackBoardPanel bbp;
    Timer timer = null;
    final int seconds = 5;
    int center = 21;

    /** Creates new form LeagueEvolution */
    public LeagueEvolution() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        this.setSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        this.setExtendedState(this.getExtendedState() | LeagueEvolution.MAXIMIZED_BOTH);
        bbp = new BlackBoardPanel();
        BlackBoardScrollPane.setViewportView(bbp);
        updateListeners();
    }

    public void updateBlackBoard(String championship, boolean refill) {
        if (!KendoTournamentGenerator.getInstance().designedGroups.mode.equals("simple")) {
            KendoTournamentGenerator.getInstance().designedGroups.color(true);
            KendoTournamentGenerator.getInstance().designedGroups.update();
            bbp.updateBlackBoard(championship, refill);
            KendoTournamentGenerator.getInstance().designedGroups.unselectDesignedGroups();
            KendoTournamentGenerator.getInstance().designedGroups.enhance(true);
            KendoTournamentGenerator.getInstance().designedGroups.onlyShow();

            BlackBoardScrollPane.revalidate();
            BlackBoardScrollPane.repaint();
            if (timer == null) {
                startTimer(championship);
            }
        } else {
            this.dispose();
        }
    }

    private void startTimer(final String championship) {
        timer = new Timer(seconds * 100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                updateBlackBoard(championship, true);
                centerImage();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    final void updateListeners() {
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.size(); i++) {
            if (KendoTournamentGenerator.getInstance().designedGroups.get(i).listenerAdded) {
                KendoTournamentGenerator.getInstance().designedGroups.get(i).removeMouseClickListener();
            }
        }
    }

    private void centerImage() {
        JScrollBar verticalScrollBar = BlackBoardScrollPane.getVerticalScrollBar();
        JScrollBar horizontalScrollBar = BlackBoardScrollPane.getHorizontalScrollBar();

        int columns = horizontalScrollBar.getMaximum() / KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels();
        int rows = verticalScrollBar.getMaximum() / KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(0).size();

        int x = KendoTournamentGenerator.getInstance().designedGroups.firstLevelNotFinished(KendoTournamentGenerator.getInstance().fights.getFights());
        int y;

        int designedgroupIndex = KendoTournamentGenerator.getInstance().designedGroups.returnLastGroupUsed() + 1;
        if (designedgroupIndex >= KendoTournamentGenerator.getInstance().designedGroups.size()) {
            designedgroupIndex = KendoTournamentGenerator.getInstance().designedGroups.size() - 1;
        }
        if (KendoTournamentGenerator.getInstance().designedGroups.default_max_winners < 2) {
            y = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(designedgroupIndex) * (int) (Math.pow(2, KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(designedgroupIndex)));
        } else {
            if (KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(designedgroupIndex) == 0) {
                y = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(designedgroupIndex) * (int) (Math.pow(2, KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(designedgroupIndex)));
            } else {
                y = KendoTournamentGenerator.getInstance().designedGroups.returnPositionOfGroupInItsLevel(designedgroupIndex) * (int) (Math.pow(2, KendoTournamentGenerator.getInstance().designedGroups.returnLevelOfGroup(designedgroupIndex) - 1));
            }
        }

        verticalScrollBar.setValue(rows * y);
        horizontalScrollBar.setValue(columns * x);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BlackBoardScrollPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("League Viewer");
        setAlwaysOnTop(true);
        setUndecorated(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BlackBoardScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BlackBoardScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        int ke = evt.getKeyCode();

        //if (ke == 27) {
        // ESC or q pressed (with bloq num and without)
        if ((ke == 1048689) || (ke == 1048603) || (ke == 27) || (ke == 113)) {
            timer.stop();
            timer = null;
            this.dispose();
        }

        //Ctrl + P pressed.
        if (ke == 17) {
            //PrintUtilities.printComponent(this);
        }
    }//GEN-LAST:event_formKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JScrollPane BlackBoardScrollPane;
    // End of variables declaration//GEN-END:variables
}
