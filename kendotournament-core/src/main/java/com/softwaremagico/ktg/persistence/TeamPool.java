package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.log.KendoLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TeamPool extends TournamentDependentPool<Team> {

	private static TeamPool instance;
	private HashMap<Tournament, Integer> membersOrderToDelete;

	private TeamPool() {
		membersOrderToDelete = new HashMap<>();
	}

	public static TeamPool getInstance() {
		if (instance == null) {
			synchronized (TeamPool.class) {
				if (instance == null) {
					instance = new TeamPool();
				}
			}
		}
		return instance;
	}

	/**
	 * Obtain the teams that participates in a fight of a level
	 *
	 * @param tournament
	 * @param level
	 * @return
	 * @throws java.sql.SQLException
	 */
	public List<Team> get(Tournament tournament, Integer level) throws SQLException {
		List<Team> results = new ArrayList<>();
		List<Fight> fights = FightPool.getInstance().getFromLevel(tournament, level);
		// If level is zero and no fights.
		if (level == 0 && fights.isEmpty()) {
			return get(tournament);
		}
		// If tournament has started.
		for (Fight fight : fights) {
			if (!results.contains(fight.getTeam1())) {
				results.add(fight.getTeam1());
			}
			if (!results.contains(fight.getTeam2())) {
				results.add(fight.getTeam2());
			}
		}
		return results;
	}

	public Team get(Tournament tournament, RegisteredPerson competitor) throws SQLException {
		for (Team team : getMap(tournament).values()) {
			if (team.isMember(competitor)) {
				return team;
			}
		}
		return null;
	}

	public List<RegisteredPerson> getCompetitorsWithoutTeam(Tournament tournament) throws SQLException {
		List<RegisteredPerson> competitors = RolePool.getInstance().getCompetitors(tournament);
		List<Team> teams = new ArrayList<>(getMap(tournament).values());
		for (Team team : teams) {
			for (RegisteredPerson teamIntegrator : team.getMembersOrder(0).values()) {
				competitors.remove(teamIntegrator);
			}
		}
		return competitors;
	}

	@Override
	protected HashMap<String, Team> getElementsFromDatabase(Tournament tournament) throws SQLException {
		if (!DatabaseConnection.getInstance().connect()) {
			return null;
		}
		HashMap<String, Team> hashMap = new HashMap<>();

		try {
			List<Team> teams = DatabaseConnection.getConnection().getDatabase().getTeams(tournament);
			DatabaseConnection.getInstance().disconnect();

			for (Team t : teams) {
				hashMap.put(getId(t), t);
			}
		} catch (TeamMemberOrderException e) {
			KendoLog.errorMessage(this.getClass().getName(), e);
		}
		return hashMap;
	}

	@Override
	protected String getId(Team element) {
		return normalizeElementId(element.getName());
	}

	/**
	 * Removing a team must delete any fight (if not, tournament is not
	 * consistent).
	 *
	 * @param tournament
	 * @throws java.sql.SQLException
	 */
	@Override
	public void remove(Tournament tournament) throws SQLException {
		FightPool.getInstance().remove(tournament);
		super.remove(tournament);
	}

	/**
	 * Removing a team must delete any fight (if not, tournament is not
	 * consistent).
	 *
	 * @param tournament
	 * @param elements
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean remove(Tournament tournament, List<Team> elements) throws SQLException {
		for (Team element : elements) {
			remove(tournament, element);
		}
		return true;
	}

	/**
	 * Removing a team must delete any fight (if not, tournament is not
	 * consistent).
	 *
	 * @param tournament
	 * @param elementName
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean remove(Tournament tournament, String elementName) throws SQLException {
		return remove(tournament, getById(tournament, elementName));
	}

	/**
	 * Removing a team must delete any fight (if not, tournament is not
	 * consistent).
	 *
	 * @param tournament
	 * @param element
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean remove(Tournament tournament, Team element) throws SQLException {
		FightPool.getInstance().remove(tournament);
		return super.remove(tournament, element);
	}

	@Override
	protected boolean removeElementsFromDatabase(Tournament tournament, List<Team> elementsToDelete)
			throws SQLException {
		if (elementsToDelete.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().removeTeams(elementsToDelete);
		}
		return true;
	}

	public void removeTeamsGroup(Tournament tournament) throws SQLException {
		List<Team> teams = new ArrayList<>(getMap(tournament).values());
		for (Team team : teams) {
			team.setGroup(0);
			update(tournament, team, team);
		}
	}

	public void setIndividualTeams(Tournament tournament) throws SQLException {
		// Delete old teams of tournament.
		remove(tournament);

		// Create new teams with only one member.
		List<RegisteredPerson> competitors = RolePool.getInstance().getCompetitors(tournament);
		for (RegisteredPerson competitor : competitors) {
			Team team = new Team(competitor.getSurnameName(), tournament);
			team.setMember(competitor, 0);
			add(tournament, team);
		}
		tournament.setTeamSize(1);
	}

	@Override
	protected List<Team> sort(Tournament tournament) throws SQLException {
		List<Team> unsorted = new ArrayList<>(getMap(tournament).values());
		Collections.sort(unsorted);
		return unsorted;
	}

	@Override
	protected boolean storeElementsInDatabase(Tournament tournament, List<Team> elementsToStore) throws SQLException {
		if (elementsToStore.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().addTeams(elementsToStore);
		}
		return true;
	}

	@Override
	protected boolean updateElements(Tournament tournament, HashMap<Team, Team> elementsToUpdate) throws SQLException {
		if (elementsToUpdate.size() > 0) {
			return DatabaseConnection.getConnection().getDatabase().updateTeams(elementsToUpdate);
		}
		return true;
	}

	public void removeTeamsOrder(Tournament tournament, int fightIndex) throws SQLException {
		membersOrderToDelete.put(tournament, fightIndex);

		List<Team> teams = get(tournament);
		for (Team team : teams) {
			team.removeMembersOrder(fightIndex);
		}
	}

	@Override
	public void reset() {
		resetAuxiliaryParameters();
		super.reset();
	}

	@Override
	public void reset(Tournament tournament) {
		resetAuxiliaryParameters(tournament);
		super.reset(tournament);
	}

	/**
	 * Reset auxiliary lists and variables used to speed up some methods.
	 *
	 * @param tournament
	 */
	private void resetAuxiliaryParameters(Tournament tournament) {
		membersOrderToDelete.put(tournament, null);
	}

	/**
	 * Reset auxiliary lists and variables used to speed up some methods.
	 *
	 * @param tournament
	 */
	private void resetAuxiliaryParameters() {
		membersOrderToDelete = new HashMap<>();
	}

	public boolean removeOldMembersOrderFromDatabase(Tournament tournament) throws SQLException {
		if (!DatabaseConnection.getConnection().getDatabase().removeTeamsOrder(tournament,
				membersOrderToDelete.get(tournament))) {
			return false;
		}
		membersOrderToDelete.put(tournament, null);
		return true;
	}

	public boolean removeOldMembersOrderFromDatabase() throws SQLException {
		for (Tournament tournament : membersOrderToDelete.keySet()) {
			if (!DatabaseConnection.getConnection().getDatabase().removeTeamsOrder(tournament,
					membersOrderToDelete.get(tournament))) {
				return false;
			}
		}
		membersOrderToDelete = new HashMap<>();
		return true;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		membersOrderToDelete = new HashMap<>();
	}
}
