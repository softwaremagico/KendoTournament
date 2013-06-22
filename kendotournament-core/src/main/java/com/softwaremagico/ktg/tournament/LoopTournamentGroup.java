package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoopTournamentGroup extends TGroup {
    
    public LoopTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
        super(tournament,  level, fightArea);
    }
    
    @Override
    public List<Fight> createFights(boolean random) {
        if (getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        TeamSelector remainingFights = new TeamSelector(getTeams());

        List<Team> remainingTeams = remainingFights.getTeams();
        if (random) {
            Collections.shuffle(remainingTeams);
        }
        for (Team team : remainingTeams) {
            for (Team adversary : remainingFights.getAdversaries(team)) {
                Fight fight = new Fight(getTournament(), team, adversary, getFightArea(), getLevel(), 0);
                fights.add(fight);
            }
        }

        return fights;
    }
    
}
