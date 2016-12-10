package com.softwaremagico.ktg.tournament.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.tournament.TeamSelector;

public class LoopTeamSelector extends TeamSelector {

	protected LoopTeamSelector(List<Team> teams) {
		super(teams);
	}

	@Override
	protected Map<Team, List<Team>> getAdversaries() {
		Map<Team, List<Team>> combinations = new HashMap<>();
		for (int i = 0; i < getTeams().size(); i++) {
			List<Team> otherTeams = new ArrayList<>();
			combinations.put(getTeams().get(i), otherTeams);

			// Teams already in the loop are changed to last position.
			for (int j = i + 1; j < getTeams().size(); j++) {
				otherTeams.add(getTeams().get(j));
			}
			for (int j = 0; j < i; j++) {
				otherTeams.add(getTeams().get(j));
			}
		}
		return combinations;
	}
}
