package com.softwaremagico.ktg.tournament.simple;

import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TGroup;

public class SimpleTournamentGroup extends TGroup {

	public SimpleTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
		super(tournament, level, fightArea);
	}

	@Override
	public List<Fight> createFights(boolean maximizeFights, boolean random) {
		return createCompleteFightList(getTournament(), getTeams(), getFightArea(), getLevel(), getIndex(), random);
	}
}
