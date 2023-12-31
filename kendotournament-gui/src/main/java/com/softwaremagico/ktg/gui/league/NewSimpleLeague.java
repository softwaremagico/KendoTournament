package com.softwaremagico.ktg.gui.league;

import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
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
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.AutoSaveByAction;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;

public class NewSimpleLeague extends javax.swing.JFrame {
	private static final long serialVersionUID = 2052810834933904519L;
	private ITranslator trans = null;
	private DefaultListModel<String> fightsModel = new DefaultListModel<>();
	private List<Team> listTeams = new ArrayList<>();
	private List<Fight> fights = new ArrayList<>();
	private boolean refreshTournament = true;
	private boolean refreshTeam1 = true;

	/**
	 * Creates new form NewSimpleLeague
	 */
	public NewSimpleLeague() {
		initComponents();
		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
				- (int) (this.getWidth() / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()
				/ 2 - (int) (this.getHeight() / 2));
		setLanguage();
		fillTournaments();
		refreshTournament();
		fillTeam1ComboBox();
		fillTeam2ComboBox();
		fillFightingAreas();
	}

	/**
	 * Translate the GUI to the selected language.
	 */
	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("titleNewFight"));
		TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
		Team1Label.setText(trans.getTranslatedText("Team1Label"));
		Team2Label.setText(trans.getTranslatedText("Team2Label"));
		AddButton.setText(trans.getTranslatedText("AddButton"));
		AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
		DeleteButton.setText(trans.getTranslatedText("DeleteButton"));
		RandomButton.setText(trans.getTranslatedText("RandomButton"));
		FightAreaLabel.setText(trans.getTranslatedText("FightArea"));
		UpButton.setText(trans.getTranslatedText("UpButton"));
		DownButton.setText(trans.getTranslatedText("DownButton"));
		DeleteAllButton.setText(trans.getTranslatedText("CleanAllButton"));
		SortedButton.setText(trans.getTranslatedText("SortedButton"));
	}

	private void fillTournaments() {
		refreshTournament = false;
		try {
			TournamentComboBox.removeAllItems();
			List<Tournament> listTournaments = TournamentPool.getInstance().getSorted();
			for (int i = 0; i < listTournaments.size(); i++) {
				TournamentComboBox.addItem(listTournaments.get(i));
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		TournamentComboBox
				.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
		refreshTournament = true;
	}

	private void fillTeam1ComboBox() {
		refreshTeam1 = false;
		try {
			Team1ComboBox.removeAllItems();
			listTeams = TeamPool.getInstance().get((Tournament) TournamentComboBox.getSelectedItem());
			for (int i = 0; i < listTeams.size(); i++) {
				Team1ComboBox.addItem(listTeams.get(i));
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		refreshTeam1 = true;
	}

	private void fillTeam2ComboBox() {
		Team selected = null;
		try {
			selected = (Team) Team2ComboBox.getSelectedItem();
		} catch (NullPointerException npe) {
		}
		Team2ComboBox.removeAllItems();
		try {
			for (int i = 0; i < listTeams.size(); i++) {
				if (!listTeams.get(i).getName().equals(Team1ComboBox.getSelectedItem().toString())) {
					Team2ComboBox.addItem(listTeams.get(i));
				}
			}
			if (selected != null) {
				Team2ComboBox.setSelectedItem(selected);
			}
		} catch (NullPointerException npe) {
			// npe.printStackTrace();
		}
	}

	private void fillFightingAreas() {
		try {
			FightAreaComboBox.removeAllItems();
			Tournament tournament = (Tournament) TournamentComboBox.getSelectedItem();
			for (int i = 0; i < tournament.getFightingAreas(); i++) {
				FightAreaComboBox.addItem(Tournament.getFightAreaName(i));
			}
		} catch (NullPointerException npe) {
		}
	}

	private void moveUpFight(int index) {
		if (index >= 0 && index < fights.size()) {
			Fight f = fights.get(index);
			fights.remove(index);
			if (index > 0) {
				index--;
			}
			fights.add(index, f);
		}
	}

	private void moveDownFight(int index) {
		if (index >= 0 && index < fights.size()) {
			Fight f = fights.get(index);
			fights.remove(index);
			if (index < fights.size()) {
				index++;
			}
			fights.add(index, f);
		}
	}

	private void refreshTournament() {
		try {
			if (TournamentComboBox.getItemCount() > 0) {
				fights = FightPool.getInstance().get((Tournament) TournamentComboBox.getSelectedItem());
				fillTeam1ComboBox();
				fillTeam2ComboBox();
				fillFightingAreas();
				fillFights();
			} else {
				Team1ComboBox.removeAllItems();
				Team2ComboBox.removeAllItems();
				FightAreaComboBox.removeAllItems();
				fightsModel.removeAllElements();
			}
		} catch (NullPointerException npe) {
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
	}

	/**
	 * Fill the list with the results obtained
	 */
	public void fillFights() {
		fightsModel.removeAllElements();
		if (((Tournament) TournamentComboBox.getSelectedItem()).getType().equals(getDefinedType())) {
			for (int i = 0; i < fights.size(); i++) {
				String text = fights.get(i).getTeam1().getName() + " - " + fights.get(i).getTeam2().getName();
				if (((Tournament) TournamentComboBox.getSelectedItem()).getFightingAreas() > 1) {
					text += "  (" + trans.getTranslatedText("FightArea") + " "
							+ Tournament.getFightAreaName(fights.get(i).getAsignedFightArea()) + ")";

				}
				fightsModel.addElement(text);
			}
		}
	}

	private void setTournamentType() {
		((Tournament) TournamentComboBox.getSelectedItem()).setType(getDefinedType());
	}

	protected TournamentType getDefinedType() {
		return TournamentType.LEAGUE;
	}

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		NewFightPanel = new javax.swing.JPanel();
		AddButton = new javax.swing.JButton();
		Team1ComboBox = new JComboBox<Team>();
		Team2ComboBox = new JComboBox<Team>();
		Team1Label = new javax.swing.JLabel();
		Team2Label = new javax.swing.JLabel();
		FightAreaLabel = new javax.swing.JLabel();
		FightAreaComboBox = new JComboBox<String>();
		RandomButton = new javax.swing.JButton();
		SortedButton = new javax.swing.JButton();
		FightsPanel = new javax.swing.JPanel();
		FightScrollPane = new javax.swing.JScrollPane();
		FightsList = new javax.swing.JList();
		DeleteButton = new javax.swing.JButton();
		UpButton = new javax.swing.JButton();
		DownButton = new javax.swing.JButton();
		DeleteAllButton = new javax.swing.JButton();
		AcceptButton = new javax.swing.JButton();
		TournamentLabel = new javax.swing.JLabel();
		TournamentComboBox = new JComboBox<Tournament>();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		NewFightPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		AddButton.setText("Add");
		AddButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AddButtonActionPerformed(evt);
			}
		});

		Team1ComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Team1ComboBoxActionPerformed(evt);
			}
		});

		Team1Label.setText("Team 1:");

		Team2Label.setText("Team 2:");

		FightAreaLabel.setText("Fight Area:");

		RandomButton.setText("Random");
		RandomButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				RandomButtonActionPerformed(evt);
			}
		});

		SortedButton.setText("Sorted");
		SortedButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SortedButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout NewFightPanelLayout = new javax.swing.GroupLayout(NewFightPanel);
		NewFightPanel.setLayout(NewFightPanelLayout);
		NewFightPanelLayout
				.setHorizontalGroup(NewFightPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								NewFightPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												NewFightPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																NewFightPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				NewFightPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								Team2Label)
																						.addComponent(
																								Team1Label)
																						.addComponent(
																								FightAreaLabel))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				107, Short.MAX_VALUE)
																		.addGroup(
																				NewFightPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addComponent(
																								FightAreaComboBox,
																								0,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								Team2ComboBox,
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								0,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								Team1ComboBox,
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								257,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																NewFightPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				RandomButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				76,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(SortedButton)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				171, Short.MAX_VALUE)
																		.addComponent(
																				AddButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				87,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));

		NewFightPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
				AddButton, RandomButton, SortedButton });

		NewFightPanelLayout.setVerticalGroup(NewFightPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				NewFightPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								NewFightPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
										.addComponent(Team1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(Team1Label))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								NewFightPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
										.addComponent(Team2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(Team2Label))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								NewFightPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
										.addComponent(FightAreaComboBox,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(FightAreaLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								NewFightPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(AddButton).addComponent(RandomButton)
										.addComponent(SortedButton))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		FightsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		FightsList.setModel(fightsModel);
		FightsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		FightScrollPane.setViewportView(FightsList);

		DeleteButton.setText("Delete");
		DeleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				DeleteButtonActionPerformed(evt);
			}
		});

		UpButton.setText("Up");
		UpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				UpButtonActionPerformed(evt);
			}
		});

		DownButton.setText("Down");
		DownButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				DownButtonActionPerformed(evt);
			}
		});

		DeleteAllButton.setText("Delete All");
		DeleteAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				DeleteAllButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout FightsPanelLayout = new javax.swing.GroupLayout(FightsPanel);
		FightsPanel.setLayout(FightsPanelLayout);
		FightsPanelLayout
				.setHorizontalGroup(FightsPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								FightsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												FightsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																FightsPanelLayout
																		.createSequentialGroup()
																		.addComponent(UpButton)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(DownButton)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				78, Short.MAX_VALUE)
																		.addComponent(
																				DeleteButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				87,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(DeleteAllButton))
														.addComponent(FightScrollPane,
																javax.swing.GroupLayout.DEFAULT_SIZE, 438,
																Short.MAX_VALUE)).addContainerGap()));

		FightsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
				DeleteAllButton, DeleteButton, DownButton, UpButton });

		FightsPanelLayout.setVerticalGroup(FightsPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				FightsPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(FightScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 177,
								Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								FightsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(UpButton).addComponent(DownButton)
										.addComponent(DeleteAllButton).addComponent(DeleteButton))
						.addContainerGap()));

		AcceptButton.setText("Accept");
		AcceptButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AcceptButtonActionPerformed(evt);
			}
		});

		TournamentLabel.setText("Tournament:");

		TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TournamentComboBoxActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(20, 20, 20)
								.addComponent(TournamentLabel)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99,
										Short.MAX_VALUE)
								.addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
										257, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(23, 23, 23))
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(NewFightPanel,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(FightsPanel,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(AcceptButton,
														javax.swing.GroupLayout.PREFERRED_SIZE, 88,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(TournamentComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(TournamentLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(NewFightPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40,
										Short.MAX_VALUE)
								.addComponent(FightsPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(AcceptButton).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void Team1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_Team1ComboBoxActionPerformed
		if (refreshTeam1) {
			fillTeam2ComboBox();
		}
	}// GEN-LAST:event_Team1ComboBoxActionPerformed

	private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_AddButtonActionPerformed
		try {
			setTournamentType();
			if (Team1ComboBox.getSelectedItem() != null && Team2ComboBox.getSelectedItem() != null) {
				// Fight fight = new
				// Fight(DatabaseConnection.getInstance().getDatabase().getTeamByName(Team1ComboBox.getSelectedItem().toString(),
				// (Tournament) TournamentComboBox.getSelectedItem(), true),
				Fight fight = new Fight((Tournament) TournamentComboBox.getSelectedItem(),
						(Team) (Team1ComboBox.getSelectedItem()), (Team) (Team2ComboBox.getSelectedItem()),
						FightAreaComboBox.getSelectedIndex(), 0, 0, fights.size());
				int ind = FightsList.getSelectedIndex();
				if (ind >= 0) {
					fights.add(ind + 1, fight);
				} else {
					fights.add(fight);
				}

				fillFights();
				if (fights.size() > 0) {
					if (ind >= 0) {
						FightsList.setSelectedIndex(ind + 1);
					} else {
						FightsList.setSelectedIndex(fightsModel.getSize() - 1);
					}
				}
			}
		} catch (NullPointerException npe) {
		}
	}// GEN-LAST:event_AddButtonActionPerformed

	private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_TournamentComboBoxActionPerformed
		if (refreshTournament) {
			refreshTournament();
			KendoTournamentGenerator.getInstance().setLastSelectedTournament(
					TournamentComboBox.getSelectedItem().toString());
		}
	}// GEN-LAST:event_TournamentComboBoxActionPerformed

	private void RandomButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_RandomButtonActionPerformed
		try {
			if (fightsModel.isEmpty() || AlertManager.questionMessage("deleteFights", "Warning!")) {
				fightsModel.removeAllElements();
				setTournamentType();
				try {
					// Remove all fights and teams if exists.
					TournamentManagerFactory.removeManager((Tournament) TournamentComboBox.getSelectedItem());
					// Create new teams.
					fights = TournamentManagerFactory.getManager(
							(Tournament) TournamentComboBox.getSelectedItem(), getDefinedType())
							.createRandomFights(true, 0);
				} catch (PersonalizedFightsException e) {
					// Not possible here.
				}
				fillFights();
				try {
					FightsList.setSelectedIndex(0);
				} catch (ArrayIndexOutOfBoundsException aiob) {
				}
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		}
	}// GEN-LAST:event_RandomButtonActionPerformed

	private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DeleteButtonActionPerformed
		try {
			int ind = FightsList.getSelectedIndex();
			fights.remove(ind);
			// fillFights();
			fightsModel.remove(ind);
			if (ind < fightsModel.getSize()) {
				FightsList.setSelectedIndex(ind);
			} else {
				FightsList.setSelectedIndex(fightsModel.getSize() - 1);
			}
		} catch (ArrayIndexOutOfBoundsException aiob) {
		}
	}// GEN-LAST:event_DeleteButtonActionPerformed

	private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_AcceptButtonActionPerformed
		try {
			if ((Tournament) TournamentComboBox.getSelectedItem() != null && fights != null
					&& fights.size() > 0) {
				if (!FightPool.existRepeatedFight(fights)) {
					FightPool.getInstance().remove((Tournament) TournamentComboBox.getSelectedItem());
					// Delete old group fights if any.
					TournamentManagerFactory.getManager((Tournament) TournamentComboBox.getSelectedItem())
							.resetFights();
					// Create new fights
					FightPool.getInstance().add((Tournament) TournamentComboBox.getSelectedItem(), fights);
					TournamentPool.getInstance().update((Tournament) TournamentComboBox.getSelectedItem());
					AlertManager.informationMessage(this.getClass().getName(), "fightStored", "New Fight");
					AutoSaveByAction.getInstance().save();
					this.dispose();
				} else {
					AlertManager.errorMessage(this.getClass().getName(), "repeatedFight", "New Fight");
				}
			} else {
				AlertManager.errorMessage(this.getClass().getName(), "noFight", "New Fight");
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
	}// GEN-LAST:event_AcceptButtonActionPerformed

	private void UpButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_UpButtonActionPerformed
		int index = FightsList.getSelectedIndex();
		moveUpFight(index);
		fillFights();
		if (index > 0) {
			index--;
		}
		FightsList.setSelectedIndex(index);
	}// GEN-LAST:event_UpButtonActionPerformed

	private void DownButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DownButtonActionPerformed
		int index = FightsList.getSelectedIndex();
		moveDownFight(index);
		fillFights();
		if (index < fights.size() - 1) {
			index++;
		}
		FightsList.setSelectedIndex(index);
	}// GEN-LAST:event_DownButtonActionPerformed

	private void DeleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DeleteAllButtonActionPerformed
		if (fightsModel.isEmpty() || AlertManager.questionMessage("deleteFights", "Warning!")) {
			fights = new ArrayList<>();
			fightsModel.removeAllElements();
		}
	}// GEN-LAST:event_DeleteAllButtonActionPerformed

	private void SortedButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_SortedButtonActionPerformed
		try {
			if (fightsModel.isEmpty() || AlertManager.questionMessage("deleteFights", "Warning!")) {
				setTournamentType();
				try {
					fights = TournamentManagerFactory.getManager(
							(Tournament) TournamentComboBox.getSelectedItem(), getDefinedType())
							.createSortedFights(true, 0);
				} catch (PersonalizedFightsException e) {
					// Not possible here.
				}
				fightsModel.removeAllElements();

				fillFights();

				try {
					FightsList.setSelectedIndex(0);
				} catch (ArrayIndexOutOfBoundsException aiob) {
				}
			}
		} catch (NullPointerException npe) {
		}
	}// GEN-LAST:event_SortedButtonActionPerformed
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private javax.swing.JButton AcceptButton;
	private javax.swing.JButton AddButton;
	private javax.swing.JButton DeleteAllButton;
	private javax.swing.JButton DeleteButton;
	private javax.swing.JButton DownButton;
	private javax.swing.JComboBox<String> FightAreaComboBox;
	private javax.swing.JLabel FightAreaLabel;
	private javax.swing.JScrollPane FightScrollPane;
	private javax.swing.JList FightsList;
	private javax.swing.JPanel FightsPanel;
	private javax.swing.JPanel NewFightPanel;
	private javax.swing.JButton RandomButton;
	private javax.swing.JButton SortedButton;
	private javax.swing.JComboBox<Team> Team1ComboBox;
	private javax.swing.JLabel Team1Label;
	private javax.swing.JComboBox<Team> Team2ComboBox;
	private javax.swing.JLabel Team2Label;
	protected javax.swing.JComboBox<Tournament> TournamentComboBox;
	private javax.swing.JLabel TournamentLabel;
	private javax.swing.JButton UpButton;
	// End of variables declaration//GEN-END:variables
}
