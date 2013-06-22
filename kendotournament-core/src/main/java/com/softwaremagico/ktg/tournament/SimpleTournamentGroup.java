package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.List;

public class SimpleTournamentGroup extends TGroup {
    
    public SimpleTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
        super(tournament,  level, fightArea);
    }
    
    @Override
    public List<Fight> createFights(boolean random) {
        if (getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        TeamSelector remainingFights = new TeamSelector(getTeams());

        Team team1 = remainingFights.getTeamWithMoreAdversaries(random);
        Fight fight;
        while (remainingFights.remainFights()) {
            Team team2 = remainingFights.getNextAdversary(team1, random);
            //Team1 has no more adversaries. Use another one. 
            if (team2 == null) {
                team1 = remainingFights.getTeamWithMoreAdversaries(random);
                continue;
            }
            if (fights.size() % 2 == 0) {
                fight = new Fight(getTournament(), team1, team2, getFightArea(), getLevel(), fights.size());
            } else {
                fight = new Fight(getTournament(), team2, team1, getFightArea(), getLevel(), fights.size());
            }
            fights.add(fight);
            remainingFights.removeAdveresary(team1, team2);
            team1 = team2;
        }
        return fights;
    }
}
