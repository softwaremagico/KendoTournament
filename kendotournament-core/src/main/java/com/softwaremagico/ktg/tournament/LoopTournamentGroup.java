package com.softwaremagico.ktg.tournament;

import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;

public class LoopTournamentGroup extends TGroup {

	public LoopTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
		super(tournament, level, fightArea);
	}

	@Override
	public List<Fight> createFights(boolean maximizeFights, boolean random) {
		return createLoopFights(getTournament(), getTeams(), getFightArea(), getLevel(), getIndex(), random);
	}
}
