package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In a ring tournament, one team fights with all other teams consecutively.
 */
public class LoopTournamentManager extends SimpleTournamentManager {

    public LoopTournamentManager(Tournament tournament) {
        super(tournament);
    }

    @Override
    public List<Fight> getRandomFights() {
        return getFights(true);
    }

    @Override
    public List<Fight> getSortedFights() {
        return getFights(false);
    }

    private List<Fight> getFights(boolean random) {
        if (group == null || group.getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        RemainingFights remainingFights = new RemainingFights(group.getTeams());

        List<Team> teams = remainingFights.getTeams();
        if (random) {
            Collections.shuffle(teams);
        }
        for (Team team : teams) {
            for (Team adversary : remainingFights.getAdversaries(team)) {
                Fight fight = new Fight(tournament, team, adversary, group.getFightArea(), group.getLevel());
                fights.add(fight);
            }
        }

        return fights;
    }
}
