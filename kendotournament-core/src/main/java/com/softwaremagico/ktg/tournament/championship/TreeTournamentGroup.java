package com.softwaremagico.ktg.tournament.championship;

import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TGroup;

public class TreeTournamentGroup extends TGroup {

	public static final int MAX_TEAMS_PER_GROUP = 8;

	public TreeTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
		super(tournament, level, fightArea);
	}

	@Override
	public List<Fight> createFights(boolean maximizeFights, boolean random) {
		if (!maximizeFights) {
			return createTwoFightsForEachTeam(getTournament(), getTeams(), getFightArea(), getLevel(), getIndex());
		} else {
			return createCompleteFightList(getTournament(), getTeams(), getFightArea(), getLevel(), getIndex(), random);
		}
	}

	@Override
	public void addTeam(Team team) {
		// Can not be repeated.
		if (!getTeams().contains(team)) {
			getTeams().add(team);
			// Delete one, because cannot be more than eight.
			if (getTeams().size() > MAX_TEAMS_PER_GROUP) {
				getTeams().remove(0);
			}
		}
	}
}
