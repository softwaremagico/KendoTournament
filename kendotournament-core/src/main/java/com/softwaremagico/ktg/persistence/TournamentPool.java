package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tools.Tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TournamentPool extends SimplePool<Tournament> {

	private static TournamentPool instance;

	private TournamentPool() {
	}

	public static TournamentPool getInstance() {
		if (instance == null) {
			synchronized (TournamentPool.class) {
				if (instance == null) {
					instance = new TournamentPool();
				}
			}
		}
		return instance;
	}

	public List<Tournament> getBySimilarName(String name) throws SQLException {
		List<Tournament> result = new ArrayList<>();
		for (Tournament element : getAll()) {
			if (Tools.isSimilar(element.getName(), name)) {
				result.add(element);
			}
		}
		Collections.sort(result);
		return result;
	}

	public Tournament getByName(String name) throws SQLException {
		for (Tournament element : getAll()) {
			if (Objects.equals(element.getName(), name)) {
				return element;
			}
		}
		return null;
	}

	@Override
	protected HashMap<String, Tournament> getElementsFromDatabase() throws SQLException {
		if (!DatabaseConnection.getInstance().connect()) {
			return null;
		}
		List<Tournament> tournaments = DatabaseConnection.getInstance().getDatabase().getTournaments();
		DatabaseConnection.getInstance().disconnect();
		HashMap<String, Tournament> hashMap = new HashMap<>();
		for (Tournament t : tournaments) {
			hashMap.put(getId(t), t);
		}
		return hashMap;
	}

	@Override
	protected String getId(Tournament element) {
		return normalizeElementId(element.getName().toLowerCase());
	}

	public Integer getLevelTournament(Tournament tournament) throws SQLException {
		int level = -1;
		for (Fight fight : FightPool.getInstance().getMap(tournament).values()) {
			if (fight.getLevel() > level) {
				level = fight.getLevel();
			}
		}
		return level;
	}

	@Override
	public void remove(String elementName) throws SQLException {
		remove(get(elementName));
	}

	@Override
	public boolean remove(Tournament tournament) throws SQLException {
		// Role deletes fights, teams, undraws and duels.
		CustomLinkPool.getInstance().remove(tournament);
		RolePool.getInstance().remove(tournament);
		TeamPool.getInstance().remove(tournament);
		return super.remove(tournament);
	}

	@Override
	protected boolean removeElementsFromDatabase(List<Tournament> elementsToDelete) throws SQLException {
		if (elementsToDelete.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().removeTournaments(elementsToDelete);
		}
		return true;
	}

	@Override
	protected List<Tournament> sort() throws SQLException {
		List<Tournament> unsorted = new ArrayList<>(getMap().values());
		Collections.sort(unsorted);
		return unsorted;
	}

	@Override
	protected boolean storeElementsInDatabase(List<Tournament> elementsToStore) throws SQLException {
		if (elementsToStore.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().addTournaments(elementsToStore);
		}
		return true;
	}

	@Override
	protected boolean updateElements(HashMap<Tournament, Tournament> elementsToUpdate) throws SQLException {
		if (elementsToUpdate.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().updateTournaments(elementsToUpdate);
		}
		return true;
	}
}
