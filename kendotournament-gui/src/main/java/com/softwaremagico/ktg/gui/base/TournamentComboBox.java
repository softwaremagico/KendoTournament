package com.softwaremagico.ktg.gui.base;

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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.persistence.TournamentPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TournamentComboBox extends KComboBox<Tournament> {
	private static final long serialVersionUID = 470079477244112666L;
	private List<Tournament> listTournaments;
	private KFrame parent;

	public TournamentComboBox(KFrame parent) {
		try {
			listTournaments = TournamentPool.getInstance().getSorted();
		} catch (SQLException ex) {
			listTournaments = new ArrayList<>();
			AlertManager.showSqlErrorMessage(ex);
		}
		this.parent = parent;
		fillTournaments();
		addActionListener(new ComboBoxActionListener());
	}

	private void fillTournaments() {
		Tournament selectedTournament;
		try {
			for (int i = 0; i < listTournaments.size(); i++) {
				addItem(listTournaments.get(i));
			}
			selectedTournament = KendoTournamentGenerator.getInstance().getLastSelectedTournament();
			if (selectedTournament != null) {
				setSelectedItem(selectedTournament);
			} else if (getItemCount() > 0) {
				setSelectedIndex(0);
				KendoTournamentGenerator.getInstance().setLastSelectedTournament(getSelectedTournament().toString());
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		}
	}

	public Tournament getSelectedTournament() {
		try {
			return ((Tournament) getSelectedItem());
		} catch (NullPointerException npe) {
			return null;
		}
	}

	private void tournamentComboBoxActionPerformed(ActionEvent evt) {
		parent.elementChanged();
	}

	class ComboBoxActionListener implements ActionListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent evt) {
			tournamentComboBoxActionPerformed(evt);
		}
	}

	@Override
	public void setSelectedItem(Object object) {
		if (object instanceof Tournament) {
			Tournament tournament = (Tournament) object;
			super.setSelectedItem(tournament);
			KendoTournamentGenerator.getInstance().setLastSelectedTournament(tournament.getName());
		}
	}
}
