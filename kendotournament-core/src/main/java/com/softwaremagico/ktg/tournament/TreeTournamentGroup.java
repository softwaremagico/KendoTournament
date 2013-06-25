package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.List;

public class TreeTournamentGroup extends TGroup {

    public static final int MAX_TEAMS_PER_GROUP = 8;

    public TreeTournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
        super(tournament, level, fightArea);
    }

    @Override
    public List<Fight> createFights(boolean random) {
        if (getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();

        for (int i = 0; i < getTeams().size(); i++) {
            Fight fight;
            Team team1 = getTeams().get(i);
            Team team2 = getTeams().get((i + 1) % getTeams().size());

            if (fights.size() % 2 == 0) {
                fight = new Fight(getTournament(), team1, team2, getFightArea(), getLevel(), TournamentManagerFactory.getManager(getTournament()).getIndex(level, this), fights.size());
            } else {
                fight = new Fight(getTournament(), team2, team1, getFightArea(), getLevel(), TournamentManagerFactory.getManager(getTournament()).getIndex(level, this), fights.size());
            }
            fights.add(fight);
        }
        return fights;
    }

    @Override
    public void addTeam(Team team) {
        //Can not be repeated.
        if (!getTeams().contains(team)) {
            getTeams().add(team);
            //Delete one, because cannot be more than eight.
            if (getTeams().size() > MAX_TEAMS_PER_GROUP) {
                getTeams().remove(0);
            }
        }
    }
}
