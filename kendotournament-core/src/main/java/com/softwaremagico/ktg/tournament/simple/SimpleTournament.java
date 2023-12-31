package com.softwaremagico.ktg.tournament.simple;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.Level;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.championship.LeagueLevel;
import com.softwaremagico.ktg.tournament.king.TournamentFinishedException;

/**
 * A simple tournament is a tournament with only one group.
 */
public class SimpleTournament implements ITournamentManager {

	private Tournament tournament;
	private TGroup group = null;

	public SimpleTournament(Tournament tournament) {
		setTournament(tournament);
	}

	public Tournament getTournament() {
		return tournament;
	}

	protected TGroup getGroup() {
		if (group == null) {
			addGroup();
		}
		return group;
	}

	@Override
	public Integer getNumberOfLevels() {
		return 1;
	}

	@Override
	public List<TGroup> getGroups(Integer level) {
		if (level == 0) {
			List<TGroup> groups = new ArrayList<>();
			groups.add(getGroup());
			return groups;
		}
		return null;
	}

	@Override
	public List<Fight> getFights(Integer level) {
		if (level == 0) {
			try {
				return FightPool.getInstance().get(tournament);
			} catch (SQLException ex) {
				KendoLog.errorMessage(this.getClass().getName(), ex);
			}
		}
		return null;
	}

	@Override
	public List<TGroup> getGroups() {
		List<TGroup> groups = new ArrayList<>();
		groups.add(getGroup());
		return groups;
	}

	@Override
	public void addGroup(TGroup group) {
		this.group = group;
	}

	public void addGroup() {
		if (group == null) {
			this.group = new SimpleTournamentGroup(tournament, 0, 0);
			try {
				group.addTeams(TeamPool.getInstance().get(tournament));
			} catch (SQLException ex) {
				KendoLog.errorMessage(this.getClass().getName(), ex);
			}
		}
	}

	@Override
	public void removeGroup(Integer level, Integer groupIndex) {
		if (level == 0 && groupIndex == 0) {
			group = null;
		}
	}

	@Override
	public void removeGroup(TGroup group) {
		if (this.group != null && this.group.equals(group)) {
			this.group = null;
		}
	}

	@Override
	public void removeGroups(Integer level) {
		if (level == 0) {
		}
	}

	@Override
	public LeagueLevel getLevel(Integer level) {
		return null;
	}

	@Override
	public boolean exist(Team team) {
		if (group != null) {
			return group.getTeams().contains(team);
		}
		return false;
	}

	@Override
	public void removeTeams(Integer level) {
		if (level == 0 && group != null) {
			List<Team> teams = new ArrayList<>();
			group.setTeams(teams);
		}
	}

	@Override
	public void setDefaultFightAreas() {
		if (group != null) {
			group.setFightArea(0);
		}
	}

	@Override
	public List<Fight> createRandomFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException {
		if (level != 0) {
			return null;
		}
		//Clear cache.
		
		//Create fights.
		return getGroup().createFights(maximizeFights, true);
	}

	@Override
	public List<Fight> createSortedFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException {
		if (level != 0) {
			return null;
		}
		return getGroup().createFights(maximizeFights, false);
	}

	@Override
	public TGroup getGroup(Fight fight) {
		if (getGroup().isFightOfGroup(fight)) {
			return getGroup();
		}
		return null;
	}

	@Override
	public Integer getLastLevelUsed() {
		return 0;
	}

	@Override
	public void setHowManyTeamsOfGroupPassToTheTree(Integer winners) {
		// Do nothing.
	}

	@Override
	public void fillGroups() {
		try {
			List<Fight> fights = FightPool.getInstance().get(tournament);
			for (Fight fight : fights) {
				getGroup().addTeam(fight.getTeam1());
				getGroup().addTeam(fight.getTeam2());
			}
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
		}
	}

	@Override
	public int getIndex(Integer level, TGroup group) {
		if (group.equals(this.group)) {
			return 0;
		}
		return -1;
	}

	@Override
	public boolean isTheLastFight() {
		try {
			List<Fight> fights = FightPool.getInstance().get(tournament);
			if (fights.size() > 0) {
				if (fights.size() == 1 || fights.get(fights.size() - 2).isOver()) {
					return true;
				}
			}
		} catch (SQLException ex) {
		}
		return false;
	}

	@Override
	public void resetFights() {
		if (group != null) {
			group.resetFights();
		}
	}

	@Override
	public void removeTeams() {
		removeTeams(0);
	}

	@Override
	public int getIndexOfGroup(TGroup group) {
		return getIndex(0, group);
	}

	@Override
	public LeagueLevel getCurrentLevel() {
		return null;
	}

	@Override
	public List<TGroup> getGroupsByShiajo(Integer shiaijo) {
		List<TGroup> groups = new ArrayList<>();
		if (getGroup() != null && getGroup().getFightArea().equals(shiaijo)) {
			groups.add(getGroup());
		}
		return groups;
	}

	@Override
	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	@Override
	public List<Level> getLevels() {
		return new ArrayList<>();
	}

	@Override
	public int getNumberOfFightsFinished() {
		int i = 0;
		try {
			for (Fight fight : FightPool.getInstance().get(tournament)) {
				if (fight.isOver()) {
					i++;
				}
			}
		} catch (SQLException e) {
			return 0;
		}
		return i;
	}

	@Override
	public void removeWinners(Integer level) {
		if (level != null) {
			List<TGroup> groups = getGroups(level);
			for (TGroup group : groups) {
				List<Fight> fights = group.getFights();
				for (Fight fight : fights) {
					fight.setWinner(null);
				}
			}
		}
	}

	@Override
	public Level getLastLevel() {
		return getLevel(0);
	}

	@Override
	public boolean isNewLevelNeeded() {
		// Only one level is needed.
		return false;
	}

	@Override
	public void createNextLevel() throws TournamentFinishedException {
		// Only one level is needed.
	}

	@Override
	public boolean hasDrawScore(TGroup group) {
		Ranking ranking = new Ranking(group.getFights());
		List<Team> teamsInDraw = ranking.getFirstTeamsWithDrawScore(getTournament().getHowManyTeamsOfGroupPassToTheTree());
		return (teamsInDraw != null);
	}
}
