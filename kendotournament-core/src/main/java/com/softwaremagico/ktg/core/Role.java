package com.softwaremagico.ktg.core;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.sql.SQLException;

import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.RolePool;

/**
 * The objective of this class is store the database into a file. For managing
 * the Role used in the GUI, use the RoleTag class.
 */
public class Role implements Comparable<Role>, IClonable<Role> {

	private Tournament tournament;
	private RegisteredPerson competitor;
	private RoleTag tag;
	private Integer accreditationOrder;
	private boolean accreditationPrinted;
	private boolean diplomaPrinted;

	public Role(Tournament tournament, RegisteredPerson competitor, RoleTag tag, Integer accreditationOrder, boolean accreditationPrinted,
			boolean diplomaPrinted) {
		this.tournament = tournament;
		this.competitor = competitor;
		this.tag = tag;
		this.accreditationOrder = accreditationOrder;
		this.accreditationPrinted = accreditationPrinted;
		this.diplomaPrinted = diplomaPrinted;
	}

	public Role(Tournament tournament, RegisteredPerson competitor, RoleTag tag, boolean accreditationPrinted, boolean diplomaPrinted) {
		this.tournament = tournament;
		this.competitor = competitor;
		this.tag = tag;
		try {
			this.accreditationOrder = RolePool.getInstance().getPeople(tournament).size() + 1;
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
		}
		this.accreditationPrinted = accreditationPrinted;
		this.diplomaPrinted = diplomaPrinted;
	}

	public RegisteredPerson getCompetitor() {
		return competitor;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public String getDatabaseTag() {
		return tag.getName();
	}

	public RoleTag getTag() {
		return tag;
	}

	public void setAccreditationPrinted(boolean value) {
		accreditationPrinted = value;
	}

	public boolean isAccreditationPrinted() {
		return accreditationPrinted;
	}

	public Integer getAccreditationOrder() {
		return accreditationOrder;
	}

	public void setAccreditationOrder(Integer value) {
		accreditationOrder = value;
	}

	public boolean isDiplomaPrinted() {
		return diplomaPrinted;
	}

	public void setDiplomaPrinted(boolean value) {
		diplomaPrinted = value;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Role)) {
			return false;
		}
		Role otherRole = (Role) object;
		return this.tournament.equals(otherRole.tournament) && this.tag.equals(otherRole.tag) && this.competitor.equals(otherRole.competitor);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + (this.tournament != null ? this.tournament.hashCode() : 0);
		hash = 67 * hash + (this.competitor != null ? this.competitor.hashCode() : 0);
		hash = 67 * hash + (this.tag != null ? this.tag.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return competitor.getSurnameName() + "(" + RolePool.getInstance().getRoleTags().getTranslation(getDatabaseTag()) + ")";
	}

	@Override
	public int compareTo(Role o) {
		return this.getCompetitor().compareTo(o.getCompetitor());
	}

	@Override
	public Role clone(Tournament tournament) {
		Role newRole = new Role(tournament, getCompetitor(), getTag(), getAccreditationOrder(), false, false);
		return newRole;
	}
}
