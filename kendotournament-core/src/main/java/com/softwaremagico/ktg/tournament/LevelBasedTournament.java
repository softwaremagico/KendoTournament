package com.softwaremagico.ktg.tournament;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.FightPool;

public abstract class LevelBasedTournament implements ITournamentManager {
	private LeagueLevel levelZero;
	private Tournament tournament;

	protected LevelBasedTournament(Tournament tournament) {
		setTournament(tournament);
	}

	public void setLevelZero(LeagueLevel levelZero) {
		this.levelZero = levelZero;
	}

	public LeagueLevel getLevelZero() {
		return levelZero;
	}

	@Override
	public List<Fight> getFights(Integer level) {
		try {
			return FightPool.getInstance().getFromLevel(getTournament(), level);
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
			return new ArrayList<>();
		}
	}

	@Override
	public int getNumberOfFightsFinished() {
		int i = 0;
		try {
			for (Fight fight : FightPool.getInstance().get(getTournament())) {
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
	public List<TGroup> getGroups() {
		List<TGroup> allGroups = new ArrayList<>();
		for (int i = 0; i < getNumberOfLevels(); i++) {
			allGroups.addAll(getLevel(i).getGroups());
		}
		return allGroups;
	}

	@Override
	public List<TGroup> getGroups(Integer level) {
		return getLevel(level).getGroups();
	}

	@Override
	public Integer getNumberOfLevels() {
		Integer total = 1; // Always exist level zero.
		LeagueLevel level = getLevelZero().getNextLevel();
		while (level != null) {
			total++;
			level = level.getNextLevel();
		}
		return total;
	}

	@Override
	public Tournament getTournament() {
		return tournament;
	}

	@Override
	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
		// Update levels if exists.
		for (LeagueLevel level : getLevels()) {
			level.setTournament(tournament);
		}
	}

}
