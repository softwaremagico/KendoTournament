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

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Ranking;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.statistics.TeamRanking;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentGroupPool;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author jorge
 */
public class MonitorFightPosition extends JFrame {

    private Timer timer = null;
    private Translator transl;
    private final int seconds = 8;
    private List<TeamRanking> teamTopTen = new ArrayList<>();
    private static int minRows = 10;
    private List<Team> teams = new ArrayList<>();
    private TournamentGroup group;

    /**
     * Creates new form MonitorPosition
     */
    public MonitorFightPosition(TournamentGroup designedGroup, boolean endTime) {
        group = designedGroup;
        transl = LanguagePool.getTranslator("gui.xml");
        initComponents();
        //setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
        //        (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        this.setSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        if (endTime) {
            if (timer == null) {
                startTimer();
            }
        }
        updateList();
    }

    private void startTimer() {
        timer = new Timer(seconds * 1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean containTeam(String team) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getName().equals(team)) {
                return true;
            }
        }
        return false;
    }

    private void setTeams() {
        teams.addAll(group.teams);
    }

    private void createRanking() {
        setTeams();
//        for (int i = 0; i < teams.size(); i++) {
        Ranking ranking = new Ranking();
        teamTopTen = ranking.getRanking(group.getFights());

        /*
         * Team t = group.getTeamInOrderOfScore(i, fightManager, false); int
         * fightsWinned = group.obtainWonFights(fightManager, t); int
         * duelsWinned = group.obtainWonDuels(fightManager, t); int score =
         * (int) group.obtainHits(fightManager, t); TeamRanking tr = new
         * TeamRanking(t.getName(), group.getChampionshipOfGroup().name,
         * fightsWinned, duelsWinned, score); teamTopTen.add(tr);
         */
        //     }
    }

    private void updateList() {
        int fontSize;
        TablePanel.removeAll();

        fontSize = this.getWidth() / 45;
        addTitle(fontSize);

        //teamTopTen = DatabaseConnection.getInstance().getDatabase().getTeamsOrderByScore(tournament.name, false);
        createRanking();

        if (teamTopTen.size() > 0) {
            if (fontSize > this.getWidth() / (teamTopTen.size() * 4)) {
                fontSize = this.getWidth() / (teamTopTen.size() * 4);
            }
        }

        for (int i = 0; i < teamTopTen.size(); i++) {
            addTeamToList(teamTopTen.get(i), fontSize);
        }
        for (int i = teamTopTen.size() - 1; i < minRows; i++) {
            addVoidTeamToList(fontSize);
        }

        TablePanel.repaint();
        TablePanel.revalidate();
    }

    void addTitle(int fontSize) {
        JLabel nameLabel;
        nameLabel = new JLabel(transl.returnTag("GroupString") + " " + (TournamentGroupPool.getManager(group.getChampionshipOfGroup()).returnPositionOfGroupInItsLevel(group) + 1) + " / "
                + transl.returnTag("FightArea") + " " + KendoTournamentGenerator.getInstance().returnShiaijo(group.arena));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);


        nameLabel = new JLabel(transl.returnTag("WonMatchs"));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        nameLabel = new JLabel(transl.returnTag("WonFights"));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        nameLabel = new JLabel(transl.returnTag("PerformedHitStatistics"));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);
    }

    void addTeamToList(TeamRanking tr, int fontSize) {
        int longName = 18 + (42 - fontSize) / 3;
        int type = Font.PLAIN;
        JLabel nameLabel;

        nameLabel = new JLabel(tr.returnShortName(longName));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        if (fontSize < 28) {
            type = Font.BOLD;
        }

        nameLabel = new JLabel(tr.wonMatchs + "/" + tr.drawMatchs);
        nameLabel.setFont(new Font("Tahoma", type, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        nameLabel = new JLabel(tr.wonFights + "/" + tr.drawFights);
        nameLabel.setFont(new Font("Tahoma", type, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        nameLabel = new JLabel(tr.score + "");
        nameLabel.setFont(new Font("Tahoma", type, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);
    }

    void addVoidTeamToList(int fontSize) {
        JLabel nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);


        nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);

        nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        TablePanel.add(nameLabel);
    }

    private void close() {
        try {
            timer.stop();
            timer = null;
        } catch (NullPointerException npe) {
        }
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TablePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        TablePanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TablePanelKeyReleased(evt);
            }
        });
        TablePanel.setLayout(new java.awt.GridLayout(0, 4, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TablePanelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TablePanelKeyReleased
        int ke = evt.getKeyCode();

        // ESC or q pressed (with bloq num and without)
        if ((ke == 1048689) || (ke == 1048603) || (ke == 27) || (ke == 113)) {
            close();
        }
    }//GEN-LAST:event_TablePanelKeyReleased

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        int ke = evt.getKeyCode();

        // ESC or q pressed (with bloq num and without)
        if ((ke == 1048689) || (ke == 1048603) || (ke == 27) || (ke == 113)) {
            close();
        }
    }//GEN-LAST:event_formKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel TablePanel;
    // End of variables declaration//GEN-END:variables
}
