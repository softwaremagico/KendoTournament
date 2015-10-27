package com.softwaremagico.ktg.lists;

import com.softwaremagico.ktg.core.Tournament;

public class BlogExporter {
	private final static String NEW_LINE = "&nbsp;";

	public static String getWordpressFormat(Tournament tournament) {
		StringBuilder stringBuilder = new StringBuilder();
		addTitle(stringBuilder, tournament);
		addInformation(stringBuilder, tournament);
		return stringBuilder.toString();
	}

	/**
	 * Header of the document
	 * 
	 * @param stringBuilder
	 * @param tournament
	 */
	private static void addTitle(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append("<h2>" + tournament.getName() + "</h2>" + NEW_LINE);
	}

	/**
	 * Extra information of the tournament
	 * 
	 * @param stringBuilder
	 * @param tournament
	 */
	private static void addInformation(StringBuilder stringBuilder, Tournament tournament) {
		stringBuilder.append("Tipo:" + tournament.getType());
		if (tournament.getTeamSize() > 1) {
			stringBuilder.append(" (Equipos)");
		}
	}

	/**
	 * List of all people that goes to the championship.
	 * 
	 * @param stringBuilder
	 */
	private static void addCompetitors(StringBuilder stringBuilder) {

	}

	/**
	 * Create the tables with the results of the fights.
	 * 
	 * @param stringBuilder
	 */
	private static void addScoreTables(StringBuilder stringBuilder) {

	}
}
