package com.softwaremagico.ktg.tournament;

import java.sql.SQLException;
import java.util.HashMap;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.tournament.championship.Championship;
import com.softwaremagico.ktg.tournament.championship.custom.CustomChampionship;
import com.softwaremagico.ktg.tournament.king.KingOfTheMountainTournament;
import com.softwaremagico.ktg.tournament.loop.LoopTournamentManager;
import com.softwaremagico.ktg.tournament.simple.SimpleTournament;

public class TournamentManagerFactory {

	private static HashMap<Tournament, HashMap<TournamentType, ITournamentManager>> managers = new HashMap<>();

	/**
	 * Get the manager of a tournament for a specific type. If it has other
	 * tournament type, it will create a new manager.
	 *
	 * @param tournament
	 * @param type
	 * @return
	 */
	public static ITournamentManager getManager(Tournament tournament, TournamentType type) {
		if (tournament != null) {
			HashMap<TournamentType, ITournamentManager> managersOfTournament = getManagers(tournament);
			ITournamentManager manager = managersOfTournament.get(type);
			if (manager == null) {
				manager = createManager(tournament, type);
				managersOfTournament.put(type, manager);
			}
			return manager;
		}
		return null;
	}

	private static HashMap<TournamentType, ITournamentManager> getManagers(Tournament tournament) {
		if (tournament != null) {
			HashMap<TournamentType, ITournamentManager> managersOfTournament = managers.get(tournament);
			if (managersOfTournament == null) {
				managersOfTournament = new HashMap<>();
				managers.put(tournament, managersOfTournament);
			}
			return managersOfTournament;
		}
		return null;
	}

	public static ITournamentManager getManager(Tournament tournament) {
		if (tournament != null) {
			return getManager(tournament, tournament.getType());
		}
		return null;
	}

	private static void removeManager(Tournament tournament, TournamentType type) {
		try {
			managers.get(tournament).remove(type);
			try {
				FightPool.getInstance().remove(tournament);
			} catch (SQLException ex) {
				KendoLog.errorMessage(TournamentManagerFactory.class.getName(), ex);
			}
		} catch (NullPointerException npe) {
		}
	}

	public static void removeManager(Tournament tournament) {
		removeManager(tournament, tournament.getType());
	}

	public static void resetManager(Tournament tournament) {
		managers.get(tournament).remove(tournament.getType());
	}

	private static ITournamentManager createManager(Tournament tournament, TournamentType type) {
		ITournamentManager manager = null;
		switch (type) {
		case LOOP:
			manager = new LoopTournamentManager(tournament);
			break;
		case TREE:
		case CHAMPIONSHIP:
			manager = new Championship(tournament);
			manager.fillGroups();
			break;
		case CUSTOM_CHAMPIONSHIP:
			manager = new CustomChampionship(tournament);
			manager.fillGroups();
			break;
		case PERSONALIZED:
			manager = new PersonalizedTournament(tournament);
			break;
		case LEAGUE:
			manager = new SimpleTournament(tournament);
			break;
		case KING_OF_THE_MOUNTAIN:
			manager = new KingOfTheMountainTournament(tournament);
			break;
		}
		return manager;
	}

	public static void clearCache() {
		managers = new HashMap<>();
	}
}
