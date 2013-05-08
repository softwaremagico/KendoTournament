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

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.ScoreOfTeam;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JLabel;

public class RankingWindow extends KFrame {

    private Ranking ranking;
    private static int MIN_ROWS = 6;

    public RankingWindow(Ranking ranking) {
        this.ranking = ranking;
        defineWindow(750, 400);
        setResizable(true);
        setElements();
        addResizedEvent();
    }

    private void setElements() {
        getContentPane().removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        KPanel rankingPanel = createRankingPanel();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(rankingPanel, gridBagConstraints);

        KPanel buttonPanel = new KPanel();
        CloseButton closeButton = new CloseButton(this);
        buttonPanel.add(closeButton);

        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(buttonPanel, gridBagConstraints);
    }

    private KPanel createRankingPanel() {
        KPanel rankingPanel = new KPanel();
        GridLayout experimentLayout = new GridLayout(0, 4);
        rankingPanel.setLayout(experimentLayout);
        setTitle(rankingPanel, getFontSize() + 2);
        setTeams(rankingPanel, getFontSize());
        return rankingPanel;
    }

    private void setTitle(KPanel rankingPanel, int titleFontSize) {
        KLabel teamLabel = new KLabel("TeamTopTenMenuItem");
        teamLabel.setBoldFont(true);
        teamLabel.setFontSize(titleFontSize);
        teamLabel.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(teamLabel);


        KLabel wonMatch = new KLabel("WonMatchs");
        wonMatch.setBoldFont(true);
        wonMatch.setFontSize(titleFontSize);
        wonMatch.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(wonMatch);

        KLabel wonFights = new KLabel("WonFights");
        wonFights.setBoldFont(true);
        wonFights.setFontSize(titleFontSize);
        wonFights.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(wonFights);

        KLabel hits = new KLabel("PerformedHitStatistics");
        hits.setBoldFont(true);
        hits.setFontSize(titleFontSize);
        hits.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(hits);
    }

    private void setTeams(KPanel rankingPanel, int teamsFontSize) {
        List<ScoreOfTeam> scores = ranking.getTeamsScoreRanking();
        int rows = 0;
        for (ScoreOfTeam score : scores) {
            addTeamLine(rankingPanel, teamsFontSize, score.getTeam().getShortName(15), score.getWonFights() + "/" + score.getDrawFights(), score.getWonDuels() + "/" + score.getDrawDuels(), score.getHits().toString());
            rows++;
        }
        while (rows < MIN_ROWS) {
            addTeamLine(rankingPanel, teamsFontSize, "", "", "", "");
            rows++;
        }
    }

    private void addTeamLine(KPanel rankingPanel, int teamsFontSize, String teamName, String fightsScore, String duelsScore, String hits) {
        KLabel teamLabel = new KLabel();
        teamLabel.setText(teamName);
        teamLabel.setFontSize(teamsFontSize);
        teamLabel.setHorizontalAlignment(JLabel.LEFT);
        rankingPanel.add(teamLabel);

        KLabel fightLabel = new KLabel();
        fightLabel.setText(fightsScore);
        fightLabel.setFontSize(teamsFontSize);
        fightLabel.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(fightLabel);

        KLabel duelLabel = new KLabel();
        duelLabel.setText(duelsScore);
        duelLabel.setFontSize(teamsFontSize);
        duelLabel.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(duelLabel);

        KLabel hitLabel = new KLabel();
        hitLabel.setText(hits);
        hitLabel.setFontSize(teamsFontSize);
        hitLabel.setHorizontalAlignment(JLabel.CENTER);
        rankingPanel.add(hitLabel);
    }

    private int getFontSize() {
        return this.getWidth() / 40;
    }

    @Override
    public void update() {
       
    }

    @Override
    public void tournamentChanged() {
    }
    
    private void addResizedEvent() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                setElements();
                revalidate();
            }
        });
    }
}