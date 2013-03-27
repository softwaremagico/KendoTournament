package com.softwaremagico.ktg.gui.fight;
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

import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.TournamentType;
import com.softwaremagico.ktg.gui.tournament.BlackBoardPanel;
import com.softwaremagico.ktg.gui.tournament.TournamentGroupPool;
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
    private Tournament tournament;

    /**
     * Creates new form LeagueEvolution
     */
    public LeagueEvolution(Tournament tournament) {
        this.tournament = tournament;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        this.setSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        this.setExtendedState(this.getExtendedState() | LeagueEvolution.MAXIMIZED_BOTH);
        bbp = new BlackBoardPanel();
        BlackBoardScrollPane.setViewportView(bbp);
        updateListeners();
    }

    public void updateBlackBoard(Tournament tournament, boolean refill) {
        this.tournament=tournament;
        if (!tournament.getMode().equals(TournamentType.SIMPLE)) {
            TournamentGroupPool.getManager(tournament).color(true);
            TournamentGroupPool.getManager(tournament).update();
            bbp.updateBlackBoard(tournament, refill);
            TournamentGroupPool.getManager(tournament).unselectDesignedGroups();
            TournamentGroupPool.getManager(tournament).enhance(true);
            TournamentGroupPool.getManager(tournament).onlyShow();

            BlackBoardScrollPane.revalidate();
            BlackBoardScrollPane.repaint();
            if (timer == null) {
                startTimer(tournament);
            }
        } else {
            this.dispose();
        }
    }

    private void startTimer(final Tournament tournament) {
        timer = new Timer(seconds * 100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                updateBlackBoard(tournament, true);
                centerImage();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    final void updateListeners() {
        for (int i = 0; i < TournamentGroupPool.getManager(tournament).size(); i++) {
            if (TournamentGroupPool.getManager(tournament).getGroup(i).listenerAdded) {
                TournamentGroupPool.getManager(tournament).getGroup(i).removeMouseClickListener();
            }
        }
    }

    private void centerImage() {
        JScrollBar verticalScrollBar = BlackBoardScrollPane.getVerticalScrollBar();
        JScrollBar horizontalScrollBar = BlackBoardScrollPane.getHorizontalScrollBar();
        int columns = 1;
        int rows = 1;
        try {
            columns = horizontalScrollBar.getMaximum() / TournamentGroupPool.getManager(tournament).getLevels().size();
            rows = verticalScrollBar.getMaximum() / TournamentGroupPool.getManager(tournament).returnGroupsOfLevel(0).size();
        } catch (ArithmeticException ae) {
        }

        Integer x = TournamentGroupPool.getManager(tournament).getIndexLastLevelNotUsed();

        if (x == null) {
            x = TournamentGroupPool.getManager(tournament).getLevels().size() - 1;
        }

        int y;

        if (TournamentGroupPool.getManager(tournament).default_max_winners < 2) {
            y = (int) (Math.pow(2, x + 1));
        } else {
            if (x == 0) {
                y = (int) (Math.pow(2, x + 1));
            } else {
                y = (int) (Math.pow(2, x));
            }
        }

        verticalScrollBar.setValue(rows * y);
        horizontalScrollBar.setValue(columns * x);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
