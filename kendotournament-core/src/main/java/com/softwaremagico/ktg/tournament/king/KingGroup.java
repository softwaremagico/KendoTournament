package com.softwaremagico.ktg.tournament.king;

import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TGroup;

public class KingGroup extends TGroup {

	public KingGroup(Tournament tournament, Integer level, Integer fightArea) {
		super(tournament, level, fightArea);
	}

	@Override
	public List<Fight> createFights(boolean maximizeFights, boolean random) {
		return createTwoFightsForEachTeam(getTournament(), getTeams(), getFightArea(), getLevel(), getIndex());
	}

}
