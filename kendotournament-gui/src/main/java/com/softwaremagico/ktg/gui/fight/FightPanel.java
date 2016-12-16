package com.softwaremagico.ktg.gui.fight;

/*
 * #%L
 * Kendo Tournament Generator GUI
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.FightAreaComboBox;
import com.softwaremagico.ktg.gui.base.KCheckBoxMenuItem;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KMenu;
import com.softwaremagico.ktg.gui.base.KMenuItem;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.PasswordConfirmator;
import com.softwaremagico.ktg.gui.base.PasswordCreator;
import com.softwaremagico.ktg.gui.base.TournamentComboBox;
import com.softwaremagico.ktg.gui.base.buttons.DownButton;
import com.softwaremagico.ktg.gui.base.buttons.UpButton;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.AutoSaveByAction;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.persistence.UndrawPool;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.king.TournamentFinishedException;

import java.util.Objects;

public class FightPanel extends KFrame {
	// Wait for some seconds until show waiting message.

	private static final long serialVersionUID = -7506045720514481230L;
	private static final Integer WAITING_TASK_PERIOD = 10000;
	private static final Integer REFRESH_MONITOR = 10;
	private KPanel tournamentDefinitionPanel;
	private ScorePanel scorePanel;
	private KPanel buttonPlacePanel;
	private TournamentComboBox tournamentComboBox;
	private FightAreaComboBox fightAreaComboBox;
	private NextButton nextButton;
	private PreviousButton previousButton;
	private JMenuItem showTreeMenuItem, groupScoreMenuItem, globalScoreMenuItem, deleteFightMenuItem, addFightMenuItem, changeMemberOrder, updateDatabase;
	private KCheckBoxMenuItem changeTeam, changeColor, monitorMode;
	private NewPersonalizedFight newPersonalizedFight;
	private JDialog waitingNetworkDialog, waitingArenaDialog;
	private JMenu optionsMenu;
	private boolean menuAllowed = true;
	private boolean moreDrawTeams = false; // We are waiting for user action for
											// undraw.
	private Timer monitorTimer = new Timer("Monitor Database Refresh");
	private MonitorTask monitorTimerTask;
	private FightPanel reference;

	// private Timer timer = new Timer("Database Exchange");
	public FightPanel() {
		defineWindow(850, 500);
		setResizable(true);
		setElements();
		addResizedEvent();
	}

	private void setElements() {
		// Add Main menu.
		setJMenuBar(createMenu());

		setLayout(new GridBagLayout());
		setMainPanels();
		tournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
		updateSelectedTournament();
		checkMonitorBlock();
	}

	public JMenuBar createMenu() {
		JMenuBar mainMenu = new JMenuBar();
		mainMenu.add(windowMenu());
		optionsMenu = createOptionsMenu();
		mainMenu.add(optionsMenu);
		mainMenu.add(createShowMenu());

		return mainMenu;
	}

	private JMenu windowMenu() {
		reference = this;
		KMenu windowMenu = new KMenu("WindowMenuItem");
		windowMenu.setMnemonic(KeyEvent.VK_E);
		windowMenu.setIcon(new ImageIcon(Path.getIconPath() + "panel.png"));

		changeColor = new KCheckBoxMenuItem("ColourCheckBox");
		changeColor.setMnemonic(KeyEvent.VK_C);
		changeColor.setIcon(new ImageIcon(Path.getIconPath() + "color-invert.png"));

		changeColor.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateScorePanel();
			}
		});
		windowMenu.add(changeColor);

		changeTeam = new KCheckBoxMenuItem("InverseCheckBox");
		changeTeam.setMnemonic(KeyEvent.VK_T);
		changeTeam.setIcon(new ImageIcon(Path.getIconPath() + "team-invert.png"));

		changeTeam.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateScorePanel();
			}
		});
		windowMenu.add(changeTeam);

		monitorMode = new KCheckBoxMenuItem("MonitorMenuItem");
		monitorMode.setIcon(new ImageIcon(Path.getIconPath() + "monitor.png"));
		monitorMode.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (monitorMode.isSelected()) {
					blockMonitor();
					// Block monitor mode if desired.
					new PasswordCreator();
				} else {
					// Unblock monitor mode
					boolean allowed = false;
					if (KendoTournamentGenerator.getBlockingString() == null || String.copyValueOf(KendoTournamentGenerator.getBlockingString()).length() == 0) {
						allowed = true;
					} else {
						new PasswordConfirmator(reference);
						// Password has been reseted.
						if (KendoTournamentGenerator.getBlockingString() == null
								|| String.copyValueOf(KendoTournamentGenerator.getBlockingString()).length() == 0) {
							allowed = true;
						}
					}

					if (allowed) {
						unblockMonitor();
					} else {
						monitorMode.setSelected(true);
					}
				}
				updateScorePanel();
			}
		});
		windowMenu.add(monitorMode);

		updateDatabase = new KMenuItem("DatabaseUpdateMenuItem");
		updateDatabase.setIcon(new ImageIcon(Path.getIconPath() + "updatePanel.png"));

		// Ctrl + S for save.
		updateDatabase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));

		updateDatabase.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					updateDatabaseForMultipleComputers();
					AlertManager.informationMessage(this.getClass().getName(), "importSuccess", "Update");
					// Update score panel.
					updateScorePanel();
					updateNextButton();
				} catch (SQLException ex) {
					AlertManager.errorMessage(this.getClass().getName(), "importFail", "Update");
					KendoLog.errorMessage(this.getClass().getName(), ex);
				}
			}
		});
		windowMenu.add(updateDatabase);

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

	private void checkMonitorBlock() {
		if (KendoTournamentGenerator.getBlockingString() != null && String.copyValueOf(KendoTournamentGenerator.getBlockingString()).length() > 0) {
			monitorMode.setSelected(true);
			blockMonitor();
		}
	}

	private void blockMonitor() {
		menuAllowed = false;
		buttonPlacePanel.setVisible(false);
		optionsMenu.setVisible(false);
		monitorTimerTask = new MonitorTask();
		tournamentComboBox.setEnabled(false);
		monitorTimer.schedule(monitorTimerTask, 1000, REFRESH_MONITOR * 1000);
	}

	public void unblockMonitor() {
		menuAllowed = true;
		buttonPlacePanel.setVisible(true);
		optionsMenu.setVisible(true);
		tournamentComboBox.setEnabled(true);
		try {
			monitorTimerTask.cancel();
		} catch (Exception e) {
		}
		monitorMode.setSelected(false);
		updateScorePanel();
	}

	private JMenu createShowMenu() {
		KMenu showMenu = new KMenu("ShowMenuItem");
		showMenu.setMnemonic(KeyEvent.VK_S);
		showMenu.setIcon(new ImageIcon(Path.getIconPath() + "show.png"));

		showTreeMenuItem = new KMenuItem("TreeButton");
		showTreeMenuItem.setMnemonic(KeyEvent.VK_T);
		showTreeMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "tree.png"));
		showTreeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openTreeWindow();
			}
		});
		showMenu.add(showTreeMenuItem);

		groupScoreMenuItem = new KMenuItem("PointListMenuItemGroup");
		groupScoreMenuItem.setMnemonic(KeyEvent.VK_P);
		groupScoreMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "highscores.png"));
		groupScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					Fight currentFight;
					currentFight = FightPool.getInstance().getCurrentFight(getSelectedTournament(), getSelectedFightArea());
					TGroup group = TournamentManagerFactory.getManager(getSelectedTournament()).getGroup(currentFight);
					if (group != null) {
						openRankingWindow(group.getFights());
					}
				} catch (SQLException e) {
					AlertManager.showSqlErrorMessage(e);
				}
			}
		});
		showMenu.add(groupScoreMenuItem);

		globalScoreMenuItem = new KMenuItem("PointListMenuItemGlobal");
		globalScoreMenuItem.setMnemonic(KeyEvent.VK_G);
		globalScoreMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "highscores.png"));
		globalScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					openRankingWindow(FightPool.getInstance().get(getSelectedTournament()));
				} catch (SQLException e) {
					AlertManager.showSqlErrorMessage(e);
				}
			}
		});
		showMenu.add(globalScoreMenuItem);

		return showMenu;
	}

	private JMenu createOptionsMenu() {
		KMenu options = new KMenu("OptionsMenu");
		options.setMnemonic(KeyEvent.VK_O);
		options.setIcon(new ImageIcon(Path.getIconPath() + "options.png"));

		changeMemberOrder = new KMenuItem("ChangeTeamOrder");
		changeMemberOrder.setMnemonic(KeyEvent.VK_O);
		changeMemberOrder.setIcon(new ImageIcon(Path.getIconPath() + "changeTeam.png"));

		options.add(changeMemberOrder);

		final FightPanel fightPanel = this;
		addFightMenuItem = new KMenuItem("AddFigthtButton");
		addFightMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "list-add.png"));
		addFightMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				createPersonalizedFights(fightPanel);
			}
		});
		options.add(addFightMenuItem);

		deleteFightMenuItem = new KMenuItem("DeleteFigthtButton");
		deleteFightMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "list-remove.png"));
		deleteFightMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteSelectedFight();
			}
		});

		options.add(deleteFightMenuItem);

		return options;
	}

	public void addChangeTeamMenuItemListener(ActionListener al) {
		changeMemberOrder.addActionListener(al);
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

		JScrollPane scrollPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scorePanel = new ScorePanel(scrollPanel);
		scrollPanel.setViewportView(scorePanel);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder());
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(scrollPanel, gridBagConstraints);

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
		fightAreaComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSelectedFightArea();
			}
		});

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

		nextButton = new NextButton(this);
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

	public int getSelectedFightArea() {
		return fightAreaComboBox.getSelectedFightArea();
	}

	public void updateScorePanel() {
		if (scorePanel != null) {
			scorePanel.updateTournament(getSelectedTournament(), getSelectedFightArea(), changeTeam.isSelected(), changeColor.isSelected(), menuAllowed);
		}
	}

	/**
	 * User has selected the option to change color.
	 *
	 * @return
	 */
	public boolean isColorChanged() {
		return changeColor.isSelected();
	}

	/**
	 * User has selected the option to change team order.
	 *
	 * @return
	 */
	public boolean isTeamChanged() {
		return changeTeam.isSelected();
	}

	public void updateSelectedTournament() {
		fightAreaComboBox.update(getSelectedTournament());
		try {
			// Preload the undraw pool to ensure that not stop if network
			// connection is down.
			UndrawPool.getInstance().initialize(getSelectedTournament());
		} catch (SQLException ex) {
			// Only for increase speed. Do nothing if fails.
		}
		updateSelectedFightArea();
		// Disable tree window if championship is not the correct one.
		if (getSelectedTournament() != null
				&& (getSelectedTournament().getType().equals(TournamentType.CHAMPIONSHIP) || getSelectedTournament().getType().equals(TournamentType.TREE) || getSelectedTournament()
						.getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP))) {
			showTreeMenuItem.setVisible(true);
			// Disable group score panel if not useful.
			groupScoreMenuItem.setVisible(true);
		} else {
			showTreeMenuItem.setVisible(false);
			groupScoreMenuItem.setVisible(false);
		}

		if (getSelectedTournament() != null
				&& (getSelectedTournament().getType().equals(TournamentType.PERSONALIZED) || getSelectedTournament().getType().equals(TournamentType.LEAGUE))) {
			deleteFightMenuItem.setVisible(true);
			addFightMenuItem.setVisible(true);
		} else {
			deleteFightMenuItem.setVisible(false);
			addFightMenuItem.setVisible(false);
		}
		// Network update if it is using more than one computes.
		if (getSelectedTournament() == null || !getSelectedTournament().isUsingMultipleComputers()) {
			updateDatabase.setVisible(false);
		} else {
			updateDatabase.setVisible(true);
		}
		updateNextButton();
	}

	public void updateSelectedFightArea() {
		updateScorePanel();
	}

	@Override
	public void elementChanged() {
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
	private int resolveDrawTeams(List<Team> drawTeams, int level, int group) {
		JFrame frame = null;

		// If it is draw because there is only one team. Then it wins.
		if (drawTeams.size() == 1) {
			return 0;
		}

		// Ask the user who is the real winner.
		List<String> optionsList = new ArrayList<>();
		for (Team drawTeam : drawTeams) {
			optionsList.add(drawTeam.getName());
		}
		Object[] options = optionsList.toArray();
		int n = JOptionPane.showOptionDialog(frame, LanguagePool.getTranslator("gui.xml").getTranslatedText("DrawText"), LanguagePool.getTranslator("gui.xml")
				.getTranslatedText("DrawTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		// Add golden point.
		if (n >= 0) {
			try {
				// Undraws are added or increased automatically.
				Undraw undraw = new Undraw(getSelectedTournament(), group, drawTeams.get(n), 0, level);
				UndrawPool.getInstance().add(getSelectedTournament(), undraw);
			} catch (SQLException ex) {
				AlertManager.showSqlErrorMessage(ex);
			}
		}
		return n;
	}

	private void openRankingWindow(List<Fight> fights) {
		Ranking ranking = new Ranking(fights);
		openRankingWindow(ranking, false);
	}

	private void openRankingWindow(Ranking ranking, boolean autoclose) {
		RankingWindow mp = new RankingWindow(ranking, autoclose);
		mp.setVisible(true);
		mp.setAlwaysOnTop(true);
	}

	private void openTreeWindow() {
		TreeWindow tw = new TreeWindow(getSelectedTournament());
		tw.setVisible(true);
	}

	private void updateNextButton() {
		// Now it was the last one of a group.
		if (TournamentManagerFactory.getManager(getSelectedTournament()) != null && !getSelectedTournament().getType().equals(TournamentType.PERSONALIZED)) {
			if (getSelectedFightArea() == 0 && TournamentManagerFactory.getManager(getSelectedTournament()).isTheLastFight()
					&& !TournamentManagerFactory.getManager(getSelectedTournament()).isNewLevelNeeded()) {
				nextButton.updateIcon(true);
				nextButton.updateText(true);
			} else {
				nextButton.updateIcon(false);
				nextButton.updateText(false);
			}
		}
	}

	private void createPersonalizedFights(FightPanel fightPanel) {
		// Personalized championship. Open window for
		// creating new fights.
		newPersonalizedFight = new NewPersonalizedFight(getSelectedTournament(), fightPanel);
		newPersonalizedFight.setVisible(true);
	}

	/**
	 * Deletes the selected fight.
	 */
	private void deleteSelectedFight() {
		try {
			Fight currentFight = FightPool.getInstance().getCurrentFight(getSelectedTournament(), getSelectedFightArea());

			if (currentFight != null) {
				TournamentManagerFactory.getManager(getSelectedTournament()).getGroups().get(currentFight.getGroup()).removeFight(currentFight);
				// Update score panel.
				updateScorePanel();
				AlertManager.informationMessage(NewPersonalizedFight.class.getName(), "fightDeleted", "");
			}
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
		}

	}

	class PreviousButton extends UpButton {

		private static final long serialVersionUID = -6545316498980721275L;

		protected PreviousButton() {
			setTranslatedText("PreviousButton");
		}

		@Override
		public void acceptAction() {
			try {
				Fight currentFight = FightPool.getInstance().getCurrentFight(getSelectedTournament(), getSelectedFightArea());
				if (currentFight != null) {
					currentFight.setOver(false);
					Fight previousFight = FightPool.getInstance().get(getSelectedTournament(), getSelectedFightArea(),
							FightPool.getInstance().getCurrentFightIndex(getSelectedTournament(), getSelectedFightArea()) - 1);
					// In multiples computers we cannot change fights of
					// previous levels.
					if (previousFight != null
							&& (!getSelectedTournament().isUsingMultipleComputers() || Objects.equals(previousFight.getLevel(), currentFight.getLevel()))) {
						previousFight.setOver(false);
					}
				}
			} catch (SQLException ex) {
				Logger.getLogger(FightPanel.class.getName()).log(Level.SEVERE, null, ex);
			}
			// Update score panel.
			updateScorePanel();
			updateNextButton();
		}
	}

	class NextButton extends DownButton {

		private static final long serialVersionUID = -7724190339280274613L;

		protected NextButton(FightPanel fightPanel) {
			updateText(false);
			updateIcon(false);
		}

		protected final void updateIcon(boolean last) {
			if (last) {
				setIcon(new ImageIcon(Path.getIconPath() + "highscores.png"));
			} else {
				setIcon(new ImageIcon(Path.getIconPath() + "down.png"));
			}
		}

		protected final void updateText(boolean last) {
			if (last) {
				setTranslatedText("FinishGroupButton");
			} else {
				setTranslatedText("NextButton");
			}
		}

		@Override
		public void acceptAction() {
			waitingNetworkDialog = AlertManager.createWaitingNetworkMessage();
			UpdateDataWorker updateData = new UpdateDataWorker();
			updateData.execute();
		}
	}

	/**
	 * Real next button action. Creates next fights, ask for fights in other
	 * computers and show information messages to the user.
	 */
	private void nextFights() {
		try {
			// Exists fights.
			if (!FightPool.getInstance().get(getSelectedTournament(), getSelectedFightArea()).isEmpty()) {
				// Disable button until data synchronized.
				nextButton.setEnabled(false);
				// Finish current fight.
				Fight currentFight = FightPool.getInstance().getCurrentFight(getSelectedTournament(), getSelectedFightArea());
				currentFight.setOver(true);
				FightPool.getInstance().update(currentFight.getTournament(), currentFight);

				TGroup group = TournamentManagerFactory.getManager(getSelectedTournament()).getGroup(currentFight);
				// If it was the last fight of group.
				if (group.areFightsOver()) {
					// Personalized and king of the mountain cannot undraw.
					Ranking ranking = null;
					try {
						ranking = new Ranking(group.getFights());
						if (!getSelectedTournament().getType().equals(TournamentType.PERSONALIZED)
						// King of the mountain only resolve draw fights in the
						// last fight.
								&& (!getSelectedTournament().getType().equals(TournamentType.KING_OF_THE_MOUNTAIN) || (!TournamentManagerFactory.getManager(
										getSelectedTournament()).isNewLevelNeeded() && Objects.equals(currentFight.getLevel(), TournamentManagerFactory
										.getManager(getSelectedTournament()).getCurrentLevel().getLevelIndex())))) {

							moreDrawTeams = true;
							while (moreDrawTeams) {
								ranking = new Ranking(group.getFights());
								// Search for draw scores.
								List<Team> teamsInDraw = ranking.getFirstTeamsWithDrawScore(getSelectedTournament().getHowManyTeamsOfGroupPassToTheTree());
								if (teamsInDraw != null) {
									// Solve Draw Scores
									resolveDrawTeams(teamsInDraw, currentFight.getLevel(), currentFight.getGroup());
								} else {
									// No more draw teams, exit loop.
									moreDrawTeams = false;
								}
							}
							ranking = new Ranking(group.getFights());
							openRankingWindow(ranking, true);
						}

						// Exchange fights with other computer.
						updateDatabaseForMultipleComputers();

						// If it was the last fight of all groups.
						if (FightPool.getInstance().areAllOver(getSelectedTournament())) {
							// Generate next level if needed.
							if (TournamentManagerFactory.getManager(getSelectedTournament()).isNewLevelNeeded()) {
								TournamentManagerFactory.getManager(getSelectedTournament()).createNextLevel();
							}
							// Create fights of next level (if any).
							List<Fight> newFights;
							try {

								// Standard championship.
								newFights = TournamentManagerFactory.getManager(getSelectedTournament()).createSortedFights(false, currentFight.getLevel() + 1);
								if (newFights != null && newFights.size() > 0) {
									// Add new fights and continue.
									FightPool.getInstance().add(getSelectedTournament(), newFights);
									// Save this fights to avoid multiple
									// creation.
									updateDatabaseForMultipleComputers();
								} else {
									// No more fights, show final winner
									// message.
									if (ranking != null) {
										throw new TournamentFinishedException("");
									}
								}
							} catch (PersonalizedFightsException e) {
								createPersonalizedFights(this);
							}
						} else {
							// If it was the last fight of one arena groups.
							if (FightPool.getInstance().areAllOver(getSelectedTournament(), getSelectedFightArea())) {
								// wait for other arena fights. Show message.
								waitingArenaDialog = AlertManager.createWaitingArenasMessage(getSelectedTournament(), getSelectedFightArea());
								waitingArenaDialog.setVisible(true);
							}
						}
					} catch (TournamentFinishedException ex) {
						// No more fights, show final winner
						// message.
						JDialog winnerMessage = AlertManager.winnerMessage(this.getClass().getName(), "leagueFinished", "!!!!!!", ranking.getTeamsRanking()
								.get(0).getName());
						winnerMessage.setVisible(true);
					}
				}
				updateNextButton();
			} else {
				// Personalized tournament, if empty, add new fight.
				if (getSelectedTournament().getType().equals(TournamentType.PERSONALIZED)) {
					createPersonalizedFights(this);
				}
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		AutoSaveByAction.getInstance().save();
	}

	/**
	 * Database must be updated if different arenas (with different computers)
	 * are used.
	 */
	private void updateDatabaseForMultipleComputers() throws SQLException {
		// Exchange fights if more than one fight area exists.
		if (getSelectedTournament() != null && getSelectedTournament().isUsingMultipleComputers()) {
			updateFightsInMultipleComputers();
		}
	}

	/**
	 * Exchange data among computers.
	 */
	private void updateFightsInMultipleComputers() {
		// Save fights.
		try {
			DatabaseConnection.getInstance().updateDatabase(getSelectedTournament());
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		// Load it again.
		reloadData();
	}

	private void reloadData() {
		// Load fights.
		FightPool.getInstance().reset(getSelectedTournament());
		// Load teams.
		TeamPool.getInstance().reset(getSelectedTournament());
		// Recreate Tournament structure.
		TournamentManagerFactory.getManager(getSelectedTournament()).resetFights();
		TournamentManagerFactory.getManager(getSelectedTournament()).fillGroups();
	}

	private void closeWaitingDialog() {
		waitingNetworkDialog.setVisible(false);
		waitingNetworkDialog.dispose();
		nextButton.setEnabled(true);
	}

	/**
	 * Swing worker for exchange data between computers and show a waiting
	 * message without freeze the GUI.
	 */
	class UpdateDataWorker extends SwingWorker<Boolean, Object> {

		private Timer timer = new Timer("Waiting");
		private Task timerTask;

		@Override
		public Boolean doInBackground() {
			if (getSelectedTournament().isUsingMultipleComputers()) {
				timerTask = new Task();
				timer.schedule(timerTask, WAITING_TASK_PERIOD);
			}
			nextFights();
			return true;
		}

		@Override
		protected void done() {
			// Close waiting message
			try {
				timerTask.cancel();
			} catch (NullPointerException npe) {
			}
			try {
				closeWaitingDialog();
			} catch (Exception ignore) {
			}
			// Update score panel.
			updateScorePanel();
		}

		class Task extends TimerTask {

			@Override
			public void run() {
				// If are draw fights, the next fight action has not finished
				// BUT does not means that is a network
				// error.
				if (!moreDrawTeams) {
					waitingNetworkDialog.setVisible(true);
				}
			}
		}
	}

	class MonitorTask extends TimerTask {

		private int fightArea = getSelectedFightArea();

		@Override
		public void run() {
			reloadData();
			fightAreaComboBox.setSelectedFightArea(fightArea);
			updateSelectedFightArea();
			fightArea++;
			if (fightArea >= getSelectedTournament().getFightingAreas()) {
				fightArea = 0;
			}
		}
	}
}
