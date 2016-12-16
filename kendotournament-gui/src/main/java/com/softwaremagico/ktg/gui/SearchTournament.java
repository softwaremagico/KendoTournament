package com.softwaremagico.ktg.gui;

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

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.TournamentPool;

public final class SearchTournament extends Search<Tournament> {
	private static final long serialVersionUID = 3444782180073421986L;
	private JLabel NameLabel = new JLabel("Name:");
	private JTextField NameTextField = new JTextField();

	public SearchTournament() {
		super();
		fillSearchFieldPanel();
		setLanguage();
		getCloneButton().setVisible(true);
	}

	/**
	 * Translate the GUI to the selected language.
	 */
	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		NameLabel.setText(trans.getTranslatedText("NameLabel"));
	}

	@Override
	protected void fillSearchFieldPanel() {
		javax.swing.GroupLayout SearchFieldPanelLayout = new javax.swing.GroupLayout(SearchFieldPanel);
		SearchFieldPanel.setLayout(SearchFieldPanelLayout);
		SearchFieldPanelLayout.setHorizontalGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				SearchFieldPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
										SearchFieldPanelLayout
												.createSequentialGroup()
												.addComponent(NameLabel)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
												.addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 252,
														javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		SearchFieldPanelLayout.setVerticalGroup(SearchFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				SearchFieldPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								SearchFieldPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(NameLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));
	}

	@Override
	protected String getResultInformation(Tournament tournament) {
		return tournament.getName();
	}

	@Override
	protected void searchButtonActionPerformed(ActionEvent evt) {
		results = new ArrayList<>();
		try {
			if (NameTextField.getText().length() > 0) {
				results = TournamentPool.getInstance().getBySimilarName(NameTextField.getText().trim());
			} else {
				AlertManager.errorMessage(this.getClass().getName(), "fillFields", "Search");
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		fillResults(results);
	}

	@Override
	protected boolean deleteElement(Tournament tournament) {
		if (AlertManager.questionMessage("tournamentDeleteQuestion", "Competitor")) {
			try {
				if (TournamentPool.getInstance().remove(tournament)) {
					AlertManager.informationMessage(this.getClass().getName(), "tournamentDeleted", "Tournament");
					return true;
				}
			} catch (SQLException ex) {
				AlertManager.showSqlErrorMessage(ex);
			}
		}
		return false;
	}
}
