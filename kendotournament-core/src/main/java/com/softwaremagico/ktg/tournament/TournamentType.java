package com.softwaremagico.ktg.tournament;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
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

public enum TournamentType {

	CHAMPIONSHIP("championship"),

	TREE("tree"),

	LEAGUE("simple"),

	LOOP("loop"),

	CUSTOM_CHAMPIONSHIP("custom_championship"),
	
	KING_OF_THE_MOUNTAIN("king_of_the_mountain"),

	PERSONALIZED("personalized");

	private String sqlName;

	private TournamentType(String sqlName) {
		this.sqlName = sqlName;
	}

	public String getSqlName() {
		return sqlName;
	}

	public static TournamentType getType(String value) {
		for (TournamentType types : TournamentType.values()) {
			if (types.getSqlName().equals(value)) {
				return types;
			}
		}
		return null;
	}
}
