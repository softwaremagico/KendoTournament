package com.softwaremagico.ktg.tournament.loop;

import java.util.ArrayList;
import java.util.List;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TeamSelector;

public class LoopTournamentGroup extends TGroup {

	public LoopTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
		super(tournament, level, fightArea);
	}

	@Override
	public List<Fight> createFights(boolean maximizeFights, boolean random) {
		return createLoopFights(getTournament(), getTeams(), getFightArea(), getLevel(), getIndex(), random);
	}

	/**
	 * One team fights agains all other teams.
	 * 
	 * @param tournament
	 * @param teams
	 * @param fightArea
	 * @param level
	 * @param index
	 * @param random
	 * @return
	 */
	private static List<Fight> createLoopFights(Tournament tournament, List<Team> teams, int fightArea, int level, int index, boolean random) {
		if (teams.size() < 2) {
			return null;
		}
		List<Fight> fights = new ArrayList<>();
		TeamSelector remainingFights = new LoopTeamSelector(teams);
		if (random) {
			remainingFights.shuffleTeams();
		}

		List<Team> remainingTeams = remainingFights.getTeams();

		for (Team team : remainingTeams) {
			for (Team adversary : remainingFights.getAdversaries(team)) {
				Fight fight = new Fight(tournament, team, adversary, fightArea, level, index, fights.size());
				// Force the creation of duels for more than one fight area. If
				// not, multiple computers generates different duels.
				if (tournament.isUsingMultipleComputers() && tournament.getFightingAreas() > 1) {
					fight.getDuels();
				}
				fights.add(fight);
			}
		}

		return fights;
	}
}
