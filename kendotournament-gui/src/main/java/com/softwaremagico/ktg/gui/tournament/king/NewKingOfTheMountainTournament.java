package com.softwaremagico.ktg.gui.tournament.king;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2016 Softwaremagico
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TeamComboBox;
import com.softwaremagico.ktg.gui.base.TournamentComboBox;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;

public class NewKingOfTheMountainTournament extends KFrame {
	private static final long serialVersionUID = -9216293186060151629L;
	private ITranslator trans = null;
	private Tournament tournament;
	private TeamComboBox teamComboBox;
	private TournamentComboBox tournamentComboBox;

	public NewKingOfTheMountainTournament() {
		defineWindow(400, 600);
		setResizable(false);
		setElements();
		setLanguage();
		fillTeams();
	}

	private void setElements() {
		getContentPane().removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		
		KPanel tournamentPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		tournamentPanel.setMinimumSize(new Dimension(200, 50));
		
		KLabel tournamentLabel = new KLabel("championship");
		tournamentPanel.add(tournamentLabel);

		tournamentComboBox = new TournamentComboBox(this);
		tournamentComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				KendoTournamentGenerator.getInstance().setLastSelectedTournament(tournamentComboBox.getSelectedItem().toString());
				fillTeams();
			}
		});
		tournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
		tournamentComboBox.setWidth(280);
		tournamentPanel.add(tournamentComboBox);
		getContentPane().add(tournamentPanel, gridBagConstraints);

		
		KPanel teamPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		teamPanel.setMinimumSize(new Dimension(200, 50));
		
		KLabel teamLabel = new KLabel("Team");
		teamPanel.add(teamLabel);
		
		gridBagConstraints.gridy = 1;
		teamComboBox = new TeamComboBox(tournament, this);
		teamComboBox.setWidth(280);
		teamPanel.add(teamComboBox);
		getContentPane().add(teamPanel, gridBagConstraints);

		KPanel addButtonPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
		addButtonPanel.setMinimumSize(new Dimension(200, 50));

		KButton addToRedTeam = new KButton();
		addToRedTeam.setTranslatedText("RedTeamButton");
		addButtonPanel.add(addToRedTeam);
		
		KButton addToWhiteTeam = new KButton();
		addToWhiteTeam.setTranslatedText("WhiteTeamButton");
		addButtonPanel.add(addToWhiteTeam);

		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(addButtonPanel, gridBagConstraints);

		gridBagConstraints.gridy = 3;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		KPanel listPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
		listPanel.setMinimumSize(new Dimension(200, 60));
		getContentPane().add(listPanel, gridBagConstraints);

		KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setMinimumSize(new Dimension(200, 50));
		CloseButton closeButton = new CloseButton(this);
		buttonPanel.add(closeButton);

		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("kingOfTheMountainTournament"));
	}

	private void fillTeams() {
		if (teamComboBox != null) {
			teamComboBox.fillTeams((Tournament) tournamentComboBox.getSelectedItem());
		}
	}

	@Override
	public void update() {

	}

	@Override
	public void elementChanged() {

	}

}
