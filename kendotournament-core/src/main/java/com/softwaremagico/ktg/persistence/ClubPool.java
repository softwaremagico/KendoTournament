package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.tools.Tools;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClubPool extends SimplePool<Club> {

	private static ClubPool instance;

	private ClubPool() {
	}

	public static ClubPool getInstance() {
		if (instance == null) {
			synchronized (ClubPool.class) {
				if (instance == null) {
					instance = new ClubPool();
				}
			}
		}
		return instance;
	}

	/**
	 * Obtain all elements that contains the desired string
	 * 
	 * @param city
	 * @return
	 * @throws java.sql.SQLException
	 */
	public List<Club> getByCity(String city) throws SQLException {
		List<Club> result = new ArrayList<>();
		for (Club element : getMap().values()) {
			if (Tools.isSimilar(element.getCity(), city)) {
				result.add(element);
			}
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * Obtain all elements that contains the desired string
	 * 
	 * @param country
	 * @return
	 * @throws java.sql.SQLException
	 */
	public List<Club> getByCountry(String country) throws SQLException {
		List<Club> result = new ArrayList<>();
		for (Club element : getMap().values()) {
			if (Tools.isSimilar(element.getCountry(), country)) {
				result.add(element);
			}
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * Obtain all elements that contains the desired string
	 * 
	 * @param name
	 * @return
	 * @throws java.sql.SQLException
	 */
	public List<Club> getByName(String name) throws SQLException {
		List<Club> result = new ArrayList<>();
		for (Club element : getMap().values()) {
			if (Tools.isSimilar(element.getName(), name)) {
				result.add(element);
			}
		}
		Collections.sort(result);
		return result;
	}

	@Override
	protected HashMap<String, Club> getElementsFromDatabase() throws SQLException {
		if (!DatabaseConnection.getInstance().connect()) {
			return null;
		}
		List<Club> clubs = DatabaseConnection.getConnection().getDatabase().getClubs();
		DatabaseConnection.getInstance().disconnect();
		HashMap<String, Club> hashMap = new HashMap<>();
		for (Club c : clubs) {
			hashMap.put(getId(c), c);
		}
		return hashMap;
	}

	@Override
	protected String getId(Club element) {
		return element.getName().toLowerCase();
	}

	@Override
	protected boolean removeElementsFromDatabase(List<Club> elementsToDelete) throws SQLException {
		if (elementsToDelete.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().removeClubs(elementsToDelete);
		}
		return true;
	}

	@Override
	protected List<Club> sort() throws SQLException {
		List<Club> unsorted = new ArrayList<>(getMap().values());
		Collections.sort(unsorted);
		return unsorted;
	}

	@Override
	protected boolean storeElementsInDatabase(List<Club> elementsToStore) throws SQLException {
		if (elementsToStore.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().addClubs(elementsToStore);
		}
		return true;
	}

	@Override
	protected boolean updateElements(HashMap<Club, Club> elementsToUpdate) throws SQLException {
		if (elementsToUpdate.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().updateClubs(elementsToUpdate);
		}
		return true;
	}
}
