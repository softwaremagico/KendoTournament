package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.simple.SimpleTournament;

import java.util.List;

public class PersonalizedTournament extends SimpleTournament {

	protected PersonalizedTournament(Tournament tournament) {
		super(tournament);
	}

	@Override
	public List<Fight> createRandomFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException {
		throw new PersonalizedFightsException("");
	}

	@Override
	public List<Fight> createSortedFights(boolean maximizeFights, Integer level) throws PersonalizedFightsException {
		throw new PersonalizedFightsException("");
	}

}
