package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.List;

public class SimpleTournamentGroup extends TGroup {

    public SimpleTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
        super(tournament, level, fightArea, 0);
    }

    @Override
    public List<Fight> createFights(boolean random) {
        if (getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        TeamSelector remainingFights = new TeamSelector(getTeams());

        Team team1 = remainingFights.getTeamWithMoreAdversaries(random);
        Fight fight, lastFight = null;
        while (remainingFights.remainFights()) {
            Team team2 = remainingFights.getNextAdversary(team1, random);
            // Team1 has no more adversaries. Use another one.
            if (team2 == null) {
                team1 = remainingFights.getTeamWithMoreAdversaries(random);
                continue;
            }
            // Remaining fights sometimes repeat team. Align them.
            if (lastFight != null && (lastFight.getTeam1().equals(team2) || lastFight.getTeam2().equals(team1))) {
                fight = new Fight(getTournament(), team2, team1, getFightArea(), getLevel(), getIndex(), fights.size());
            } else if (lastFight != null && (lastFight.getTeam1().equals(team1) || lastFight.getTeam2().equals(team2))) {
                fight = new Fight(getTournament(), team1, team2, getFightArea(), getLevel(), getIndex(), fights.size());
            } else if (fights.size() % 2 == 0) {
                fight = new Fight(getTournament(), team1, team2, getFightArea(), getLevel(), getIndex(), fights.size());
            } else {
                fight = new Fight(getTournament(), team2, team1, getFightArea(), getLevel(), getIndex(), fights.size());
            }
			// Force the creation of duels for more than one fight area. If not, multiple computers
			// generates different duels.
			if (getTournament().getFightingAreas() > 1) {
				fight.getDuels();
			}
            fights.add(fight);
            lastFight = fight;
            remainingFights.removeAdveresary(team1, team2);
            team1 = team2;
        }
        return fights;
    }
}
