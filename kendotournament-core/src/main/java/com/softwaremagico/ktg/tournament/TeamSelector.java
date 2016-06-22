package com.softwaremagico.ktg.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.softwaremagico.ktg.core.Team;

class TeamSelector {

	private List<Team> teams;
	private Map<Team, List<Team>> combination;

	protected TeamSelector(List<Team> teams) {
		this.teams = teams;

		Collections.sort(this.teams);
		combination = getAdversaries();
	}

	public void shuffleTeams() {
		if (teams != null) {
			Collections.shuffle(teams);
			combination = getAdversaries();
		}
	}

	public List<Team> getAdversaries(Team team) {
		return combination.get(team);
	}

	public List<Team> getTeams() {
		return teams;
	}

	protected Map<Team, List<Team>> getAdversaries() {
		Map<Team, List<Team>> combinations = new HashMap<>();
		for (int i = 0; i < getTeams().size(); i++) {
			List<Team> otherTeams = new ArrayList<>();
			combinations.put(getTeams().get(i), otherTeams);

			for (int j = 0; j < getTeams().size(); j++) {
				if (i != j) {
					otherTeams.add(getTeams().get(j));
				}
			}
		}
		return combinations;
	}

	public Team getTeamWithMoreAdversaries(boolean random) {
		return getTeamWithMoreAdversaries(teams, random);
	}

	public Team getTeamWithMoreAdversaries(List<Team> teamGroup, boolean random) {
		Integer maxAdv = -1;
		// Get max Adversaries value:
		for (Team team : teamGroup) {
			if (combination.get(team).size() > maxAdv) {
				maxAdv = combination.get(team).size();
			}
		}

		// Select one of the teams with max adversaries
		List<Team> possibleAdversaries = new ArrayList<>();
		for (Team team : teamGroup) {
			if (combination.get(team).size() == maxAdv) {
				// If no random, return the first one.
				if (!random) {
					return team;
				} else {
					possibleAdversaries.add(team);
				}
			}
		}

		if (possibleAdversaries.size() > 0) {
			return possibleAdversaries.get(new Random().nextInt(possibleAdversaries.size()));
		}
		return null;
	}

	public Team getNextAdversary(Team team, boolean random) {
		return getTeamWithMoreAdversaries(combination.get(team), random);
	}

	public void removeAdveresary(Team team, Team adversary) {
		combination.get(team).remove(adversary);
		combination.get(adversary).remove(team);
	}

	public boolean remainFights() {
		for (Team team : teams) {
			if (combination.get(team).size() > 0) {
				return true;
			}
		}
		return false;
	}
}
