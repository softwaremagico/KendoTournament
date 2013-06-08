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

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.UndrawPool;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.base.FightAreaComboBox;
import com.softwaremagico.ktg.gui.base.KCheckBoxMenuItem;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KMenu;
import com.softwaremagico.ktg.gui.base.KMenuItem;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TournamentComboBox;
import com.softwaremagico.ktg.gui.base.buttons.DownButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.gui.base.buttons.UpButton;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
        defineWindow(750, 500);
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
        scoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRankingWindow();
            }
        });
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

    /**
     * If there are more than one team with the max punctuation, ask for a
     * winner to the user.
     *
     * @param winnersOfgroup
     * @return position in the list of the choosen one.
     */
    private int resolvDrawTeams(List<Team> drawTeams, int level, int group) {
        JFrame frame = null;

        //If it is draw because there is only one team. Then it wins.
        if (drawTeams.size() == 1) {
            return 0;
        }

        //Ask the user who is the real winner.
        List<String> optionsList = new ArrayList<>();
        for (int i = 0; i < drawTeams.size(); i++) {
            optionsList.add(drawTeams.get(i).getName());
        }
        Object[] options = optionsList.toArray();
        int n = JOptionPane.showOptionDialog(frame,
                LanguagePool.getTranslator("gui.xml").getTranslatedText("DrawText"),
                LanguagePool.getTranslator("gui.xml").getTranslatedText("DrawTitle"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        //Add golden point.
        if (n >= 0) {
            Undraw undraw = UndrawPool.getInstance().get(getSelectedTournament(), level, group, drawTeams.get(n));
            undraw.setPoints(undraw.getPoints() + 1);
            UndrawPool.getInstance().update(getSelectedTournament(), undraw);
        }
        return n;
    }

    private void messagesFinishedGroup(TournamentGroup currentGroup) {
        //When a group is finished, show different messages with the winner, score, etc.
        if (currentGroup.areFightsOver()) {
            //Show score.
           /* MonitorFightPosition mfp = new MonitorFightPosition(currentGroup, true);
             mfp.setVisible(true);
             mfp.setExtendedState(mfp.getExtendedState() | JFrame.MAXIMIZED_BOTH);
             boolean message;

             //The message of next group will be shown only if there are more levels in the championship.
             //The last level always only has one group!
             if (TournamentManagerPool.getManager(getSelectedTournament()).getGroups(currentGroup.getLevel()).size() > 1) {
             message = true;
             } else {
             message = false;
             }

             //Alert message with the passing teams. 
             currentGroup.showWinnersOfGroup();*/
        }
    }
    
    private void openRankingWindow(){
        Fight currentFight = FightPool.getInstance().getCurrentFight(getSelectedTournament(), getSelectedFightArea());
        TournamentGroup group = TournamentManagerFactory.getManager(getSelectedTournament()).getGroup(currentFight);
        Ranking ranking = new Ranking(group.getFights());
        openRankingWindow(ranking);
    }
    
    private void openRankingWindow(Ranking ranking){
        RankingWindow mp = new RankingWindow(ranking);
        mp.setVisible(true);
    }

    class NextButton extends DownButton {

        protected NextButton() {
            updateText(false);
            updateIcon(false);
        }

        protected final void updateIcon(boolean last) {
            if (last) {
                setIcon(new ImageIcon("highscores.png"));
            } else {
                setIcon(new ImageIcon(Path.getIconPath() + "down.png"));
            }
        }

        protected final void updateText(boolean last) {
            if (last) {
                setTranslatedText("FinishtButton");
            } else {
                setTranslatedText("NextButton");
            }
        }

        @Override
        public void acceptAction() {
            //Finish current fight.
            Fight currentFight = FightPool.getInstance().getCurrentFight(getSelectedTournament(), getSelectedFightArea());
            currentFight.setOver(true);

            TournamentGroup group = TournamentManagerFactory.getManager(getSelectedTournament()).getGroup(currentFight);
            //If it was the last fight of group.
            if (group.areFightsOver()) {
                boolean moreDrawTeams = true;
                Ranking ranking = null;
                while (moreDrawTeams) {
                    //Search for draw scores.
                    ranking = new Ranking(group.getFights());
                    List<Team> teamsInDraw = ranking.getFirstTeamsWithDrawScore(getSelectedTournament().getHowManyTeamsOfGroupPassToTheTree());
                    if (teamsInDraw != null) {
                        //Solve Draw Scores
                        resolvDrawTeams(teamsInDraw, currentFight.getLevel(), currentFight.getGroupIndex());
                    } else {
                        //No more draw teams, exit loop.
                        moreDrawTeams = false;
                    }
                }
                //Show score.
                openRankingWindow(ranking);

                //If it was the last fight of all groups.
                if (FightPool.getInstance().areAllOver(getSelectedTournament())) {
                    //Create new fights.
                } else {
                    //If it was the last fight of arena groups.
                    if (FightPool.getInstance().areAllOver(getSelectedTournament(), getSelectedFightArea())) {
                        //wait for other arena fights.
                    } else {
                        //Now it was the last one of a group.
                        if (group.inTheLastFight()) {
                            updateIcon(true);
                        } else {
                            updateIcon(false);
                        }
                    }
                }
            }

            //Update score panel.
            updateScorePanel();
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
}
