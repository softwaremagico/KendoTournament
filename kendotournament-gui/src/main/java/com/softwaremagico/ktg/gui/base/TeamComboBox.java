package com.softwaremagico.ktg.gui.base;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.persistence.TeamPool;

public class TeamComboBox extends KComboBox<Team> {
	private static final long serialVersionUID = 5739678513371517215L;
	private List<Team> listTeams;
	private Set<Team> hiddenTeams;

	public TeamComboBox(Tournament tournament, KFrame parent) {
		hiddenTeams = new HashSet<>();
		fillTeams(tournament);
	}

	public void fillTeams(Tournament tournament) {
		try {
			listTeams = TeamPool.getInstance().getSorted(tournament);
		} catch (SQLException ex) {
			listTeams = new ArrayList<>();
			AlertManager.showSqlErrorMessage(ex);
		}
		this.removeAllItems();
		try {
			for (int i = 0; i < listTeams.size(); i++) {
				if (!hiddenTeams.contains(listTeams.get(i))) {
					addItem(listTeams.get(i));
				}
			}
			if (getItemCount() > 0) {
				setSelectedIndex(0);
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
			npe.printStackTrace();
		}
	}

	public void setHiddenTeams(Tournament tournament, Set<Team> hiddenTeams) {
		this.hiddenTeams = hiddenTeams;
		fillTeams(tournament);
	}

	public Team getSelectedTeam() {
		try {
			return ((Team) getSelectedItem());
		} catch (NullPointerException npe) {
			return null;
		}
	}

	@Override
	public void setSelectedItem(Object object) {
		if (object instanceof Team) {
			Team team = (Team) object;
			super.setSelectedItem(team);
		}
	}

	public List<Team> getTeams() {
		return listTeams;
	}
}
