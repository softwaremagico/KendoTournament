package com.softwaremagico.ktg.gui.fight;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
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
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TeamComboBox;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class NewPersonalizedFight extends KFrame {

    private static final long serialVersionUID = -8484422346146735562L;
    private final static int DEFAULT_LEVEL = 0;
    private final static int DEFAULT_GROUP = 0;
    private FightPanel parent;
    private Tournament tournament;
    private TeamComboBox team1, team2;
    private KButton acceptButton;

    public NewPersonalizedFight(Tournament tournament, FightPanel parent) {
        this.tournament = tournament;
        this.parent = parent;
        defineWindow(450, 160);
        setResizable(false);
        setElements();
        setTitle(LanguagePool.getTranslator("gui.xml").getTranslatedText("AddNewFight"));
    }

    private void setElements() {
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        KLabel team1Label;
        if (!parent.isColorChanged()) {
            team1Label = new KLabel("RedTeam");
        } else {
            team1Label = new KLabel("WhiteTeam");
        }
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.ipady = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 5);
        getContentPane().add(team1Label, gridBagConstraints);

        KLabel team2Label;
        if (!parent.isColorChanged()) {
            team2Label = new KLabel("WhiteTeam");
        } else {
            team2Label = new KLabel("RedTeam");
        }
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.ipady = xPadding;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(10, 5, 0, 10);
        getContentPane().add(team2Label, gridBagConstraints);

        team1 = new TeamComboBox(tournament, this);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        getContentPane().add(team1, gridBagConstraints);

        JLabel versusLabel = new JLabel(" vs ");
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 2, 5, 2);
        getContentPane().add(versusLabel, gridBagConstraints);

        team2 = new TeamComboBox(tournament, this);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 10);
        getContentPane().add(team2, gridBagConstraints);

        setDefaultFight();

        KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setMinimumSize(new Dimension(200, 50));
        acceptButton = new KButton();
        acceptButton.setTranslatedText("AcceptButton");
        acceptButton.setPreferredSize(new Dimension(80, 35));
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptAction();
            }
        });
        buttonPanel.add(acceptButton);
        CustomCloseButton closeButton = new CustomCloseButton(this);
        closeButton.setPreferredSize(new Dimension(80, 35));
        buttonPanel.add(closeButton);

        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        //gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        getContentPane().add(buttonPanel, gridBagConstraints);

    }

    private int getFightArea() {
        return parent.getSelectedFightArea();
    }

    private void setDefaultFight() {
        Fight lastFight;
        try {
            lastFight = FightPool.getInstance().getCurrentFight(tournament, getFightArea());
            if (lastFight != null) {
                if (!parent.isTeamChanged()) {
                    team1.setSelectedItem(lastFight.getTeam1());
                    team2.setSelectedItem(lastFight.getTeam2());
                } else {
                    team1.setSelectedItem(lastFight.getTeam2());
                    team2.setSelectedItem(lastFight.getTeam1());
                }
            } else {
                if (team2.getItemCount() > 1) {
                    team2.setSelectedIndex(1);
                }
            }
        } catch (SQLException e) {
            if (team2.getItemCount() > 1) {
                team2.setSelectedIndex(1);
            }
        }
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elementChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected Fight createFight() throws SQLException {
        Fight fight = null;
        if (!team1.getSelectedTeam().equals(team2.getSelectedTeam())) {
            if (!parent.isTeamChanged()) {
                fight = new Fight(tournament, team1.getSelectedTeam(), team2.getSelectedTeam(),
                        parent.getSelectedFightArea(), DEFAULT_LEVEL, DEFAULT_GROUP, FightPool.getInstance()
                        .getFromLevel(tournament, DEFAULT_LEVEL).size() + 1);
            } else {
                fight = new Fight(tournament, team2.getSelectedTeam(), team1.getSelectedTeam(),
                        parent.getSelectedFightArea(), DEFAULT_LEVEL, DEFAULT_GROUP, FightPool.getInstance()
                        .getFromLevel(tournament, DEFAULT_LEVEL).size() + 1);
            }
        }
        return fight;
    }

    protected void acceptAction() {
        try {
            Fight newFight = createFight();
            if (newFight != null) {
                FightPool.getInstance().add(tournament, newFight);
            }
        } catch (SQLException ex) {
            KendoLog.errorMessage(NewPersonalizedFight.class.getName(), ex);
        }
        parent.updateScorePanel();
    }

    class CustomCloseButton extends CloseButton {

        private static final long serialVersionUID = -3713010823196887285L;

        public CustomCloseButton(JFrame window) {
            super(window);
        }

        @Override
        public void closeAction() {
            dispose();
        }
    }
}
