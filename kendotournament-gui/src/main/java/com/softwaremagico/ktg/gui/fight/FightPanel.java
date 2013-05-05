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
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.base.FightAreaComboBox;
import com.softwaremagico.ktg.gui.base.KCheckBoxMenuItem;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KMenu;
import com.softwaremagico.ktg.gui.base.KMenuItem;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TournamentComboBox;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.DownButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.gui.base.buttons.UpButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

public class FightPanel extends KFrame {

    private KPanel tournamentDefinitionPanel;
    private ScorePanel scorePanel;
    private KPanel buttonPlacePanel;
    private TournamentComboBox tournamentComboBox;
    private FightAreaComboBox fightAreaComboBox;
    private KButton nextButton, previousButton;
    //private KCheckBox changeColor, changeTeam;
    private JMenuItem showTreeMenuItem, scoreMenuItem;
    private KCheckBoxMenuItem changeTeam, changeColor;

    public FightPanel() {
        defineWindow(700, 600);
        setResizable(true);
        setElements();
        addResizedEvent();
    }

    private void setElements() {
        // Add Main menu.
        setJMenuBar(createMenu());

        setLayout(new GridBagLayout());
        setMainPanels();
        updateScorePanel();
    }

    public JMenuBar createMenu() {
        JMenuBar mainMenu = new JMenuBar();
        mainMenu.add(windowMenu());
        mainMenu.add(createOptionsMenu());
        mainMenu.add(createShowMenu());

        return mainMenu;
    }

    private JMenu windowMenu() {
        KMenu windowMenu = new KMenu("WindowMenuItem");
        windowMenu.setMnemonic(KeyEvent.VK_E);
        windowMenu.setIcon(new ImageIcon(Path.getIconPath() + "panel.png"));

        KMenuItem exitMenuItem = new KMenuItem("ExitMenuItem");
        exitMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "exit.png"));

        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });

        windowMenu.add(exitMenuItem);

        return windowMenu;
    }

    private JMenu createShowMenu() {
        KMenu showMenu = new KMenu("ShowMenuItem");
        showMenu.setMnemonic(KeyEvent.VK_S);
        showMenu.setIcon(new ImageIcon(Path.getIconPath() + "show.png"));

        showTreeMenuItem = new KMenuItem("TreeButton");
        showTreeMenuItem.setMnemonic(KeyEvent.VK_T);
        showTreeMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "tree.png"));
        showMenu.add(showTreeMenuItem);

        scoreMenuItem = new KMenuItem("PointListMenuItem");
        scoreMenuItem.setMnemonic(KeyEvent.VK_T);
        scoreMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "highscores.png"));
        showMenu.add(scoreMenuItem);


        return showMenu;
    }

    private JMenu createOptionsMenu() {
        KMenu optionsMenu = new KMenu("OptionsMenu");
        optionsMenu.setMnemonic(KeyEvent.VK_O);
        optionsMenu.setIcon(new ImageIcon(Path.getIconPath() + "options.png"));

        changeColor = new KCheckBoxMenuItem("ColourCheckBox");
        changeColor.setMnemonic(KeyEvent.VK_C);
        changeColor.setIcon(new ImageIcon(Path.getIconPath() + "color-invert.png"));

        changeColor.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateScorePanel();
            }
        });

        optionsMenu.add(changeColor);

        changeTeam = new KCheckBoxMenuItem("InverseCheckBox");
        changeTeam.setMnemonic(KeyEvent.VK_T);
        changeTeam.setIcon(new ImageIcon(Path.getIconPath() + "team-invert.png"));

        changeTeam.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateScorePanel();
            }
        });

        optionsMenu.add(changeTeam);

        return optionsMenu;
    }

    private void setMainPanels() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        tournamentDefinitionPanel = createTournamentPanel();
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

        buttonPlacePanel = createButtonPanel();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        getContentPane().add(buttonPlacePanel, gridBagConstraints);
    }

    private KPanel createTournamentPanel() {
        KPanel tournamentPanel = new KPanel();
        tournamentPanel.setLayout(new GridBagLayout());
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
        tournamentPanel.add(tournamentLabel, gridBagConstraints);

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
        tournamentPanel.add(tournamentComboBox, gridBagConstraints);

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
        tournamentPanel.add(fightAreaLabel, gridBagConstraints);

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
        tournamentPanel.add(fightAreaComboBox, gridBagConstraints);

        return tournamentPanel;
    }

    private KPanel createButtonPanel() {
        KPanel buttonPanel = new KPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        previousButton = new PreviousButton();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        buttonPanel.add(previousButton, gridBagConstraints);

        KPanel teamOptions = new KPanel();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        buttonPanel.add(teamOptions, gridBagConstraints);

        nextButton = new NextButton();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        buttonPanel.add(nextButton, gridBagConstraints);

        return buttonPanel;
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
        if (scorePanel != null) {
            scorePanel.updateTournament(getSelectedTournament(), getSelectedFightArea(), changeTeam.isSelected(), changeColor.isSelected());
        }
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

    private void addResizedEvent() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateScorePanel();
            }
        });
    }

    class NextButton extends DownButton {

        protected NextButton() {
            updateText();
            updateIcon();
        }

        protected final void updateIcon() {
            setIcon(new ImageIcon("highscores.png"));
            setIcon(new ImageIcon(Path.getIconPath() + "down.png"));
        }

        protected final void updateText() {
            setTranslatedText("FinishtButton");
            setTranslatedText("NextButton");
        }

        @Override
        public void acceptAction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class PreviousButton extends UpButton {

        protected PreviousButton() {
            setTranslatedText("PreviousButton");
        }

        @Override
        public void acceptAction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class FightCloseButton extends CloseButton {

        protected FightCloseButton(JFrame window) {
            super(window);
        }
    }
}
