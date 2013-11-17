package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TeamComboBox;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class NewPersonalizedFight extends KFrame {

    private FightPanel parent;
    private Tournament tournament;
    private TeamComboBox team1, team2;
    private KButton acceptButton;

    public NewPersonalizedFight(Tournament tournament, FightPanel parent) {
        this.tournament = tournament;
        this.parent = parent;
        defineWindow(300, 190);
        setResizable(false);
        setElements();
    }

    private void setElements() {
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        KLabel newFightLabel = new KLabel("AddNewFight");
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(newFightLabel, gridBagConstraints);

        KLabel team1Label = new KLabel("Team1Label");
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(team1Label, gridBagConstraints);

        team1 = new TeamComboBox(tournament, this);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(team1, gridBagConstraints);

        KLabel team2Label = new KLabel("Team2Label");
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(team2Label, gridBagConstraints);

        team2 = new TeamComboBox(tournament, this);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(team2, gridBagConstraints);

        KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setMinimumSize(new Dimension(200, 50));
        acceptButton = new KButton();
        acceptButton.setTranslatedText("AcceptButton");
        acceptButton.setPreferredSize(new Dimension(80, 35));
        buttonPanel.add(acceptButton);
        CloseButton closeButton = new CloseButton(this);
        closeButton.setPreferredSize(new Dimension(80, 35));
        buttonPanel.add(closeButton);

        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(buttonPanel, gridBagConstraints);

    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elementChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
