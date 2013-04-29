package com.softwaremagico.ktg.gui.fight;
/*
 * #%L
 * Kendo Tournament Generator GUI
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
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

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.FightAreaComboBox;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TournamentComboBox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.SwingConstants;

public class FightPanel extends KFrame {

    private KPanel tournamentDefinitionPanel;
    private ScorePanel scorePanel;
    private KPanel buttonPanel;
    private TournamentComboBox tournamentComboBox;
    private FightAreaComboBox fightAreaComboBox;

    public FightPanel() {
        defineWindow(700, 600);
        setResizable(true);
        setElements();
    }

    private void setElements() {
        setLayout(new GridBagLayout());
        setMainPanels();
        createTournamentPanel();
        updateScorePanel();
    }

    private void setMainPanels() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        tournamentDefinitionPanel = new KPanel();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(tournamentDefinitionPanel, gridBagConstraints);

        scorePanel = new ScorePanel();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(scorePanel, gridBagConstraints);

        buttonPanel = new KPanel();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(buttonPanel, gridBagConstraints);
    }

    private void createTournamentPanel() {
        tournamentDefinitionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        KLabel tournamentLabel = new KLabel("TournamentLabel");
        tournamentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        tournamentDefinitionPanel.add(tournamentLabel, gridBagConstraints);
        
        tournamentComboBox = new TournamentComboBox(this);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        tournamentDefinitionPanel.add(tournamentComboBox, gridBagConstraints);

        KLabel fightAreaLabel = new KLabel("FightArea");
        fightAreaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        tournamentDefinitionPanel.add(fightAreaLabel, gridBagConstraints);

        fightAreaComboBox = new FightAreaComboBox(getSelectedTournament());
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        tournamentDefinitionPanel.add(fightAreaComboBox, gridBagConstraints);
    }

    @Override
    public void update() {
        updateSelectedTournament();
    }

    public Tournament getSelectedTournament() {
        return tournamentComboBox.getSelectedTournament();
    }

    public Integer getSelectedFightArea() {
        return fightAreaComboBox.getSelectedFightArea();
    }

    public void updateScorePanel() {
        scorePanel.updateTournament(tournamentComboBox.getSelectedTournament(), fightAreaComboBox.getSelectedFightArea());
    }

    public void updateSelectedTournament() {
        fightAreaComboBox.update(getSelectedTournament());
        updateSelectedFightArea();
    }

    public void updateSelectedFightArea() {
        updateScorePanel();
    }

    @Override
    public void tournamentChanged() {
        updateSelectedTournament();
    }
}
