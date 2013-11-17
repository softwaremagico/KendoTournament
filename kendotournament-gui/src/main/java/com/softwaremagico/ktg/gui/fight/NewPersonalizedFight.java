package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TeamComboBox;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.persistence.FightPool;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class NewPersonalizedFight extends KFrame {

    private final static int DEFAULT_LEVEL = 0;
    private final static int DEFAULT_GROUP = 0;
    private FightPanel parent;
    private Tournament tournament;
    private TeamComboBox team1, team2;
    private KButton acceptButton;

    public NewPersonalizedFight(Tournament tournament, FightPanel parent) {
        this.tournament = tournament;
        this.parent = parent;
        defineWindow(450, 190);
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(newFightLabel, gridBagConstraints);

        KLabel team1Label;
        if (!parent.isColorChanged()) {
            team1Label = new KLabel("RedTeam");
        } else {
            team1Label = new KLabel("WhiteTeam");
        }
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

        KLabel team2Label;
        if (!parent.isColorChanged()) {
            team2Label = new KLabel("WhiteTeam");
        } else {
            team2Label = new KLabel("RedTeam");
        }
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(team2Label, gridBagConstraints);

        team1 = new TeamComboBox(tournament, this);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(team1, gridBagConstraints);

        JLabel versusLabel = new JLabel(" vs ");
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(versusLabel, gridBagConstraints);

        team2 = new TeamComboBox(tournament, this);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 2;
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
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptAction();
            }
        });
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
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
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

    protected void acceptAction() {
        try {
            Fight fight = new Fight(tournament, team1.getSelectedTeam(), team2.getSelectedTeam(), parent.getSelectedFightArea(), DEFAULT_LEVEL, DEFAULT_GROUP, FightPool.getInstance().getFromLevel(tournament, DEFAULT_LEVEL).size() + 1);
            FightPool.getInstance().add(tournament, fight);
        } catch (SQLException ex) {
            Logger.getLogger(NewPersonalizedFight.class.getName()).log(Level.SEVERE, null, ex);
        }
        parent.updateScorePanel();
    }
}
