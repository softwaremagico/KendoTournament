package com.softwaremagico.ktg;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ranking {

    public List<Team> teams;

    public Ranking() {
        teams = new ArrayList<>();
    }

    private static List<Team> getTeams(List<Fight> fights) {
        List<Team> teamsOfFights = new ArrayList<>();
        for (Fight fight : fights) {
            if (!teamsOfFights.contains(fight.getTeam1())) {
                teamsOfFights.add(fight.getTeam1());
            }
            if (!teamsOfFights.contains(fight.getTeam2())) {
                teamsOfFights.add(fight.getTeam2());
            }
        }
        return teamsOfFights;
    }

    private static List<RegisteredPerson> getRegisteredPersons(List<Fight> fights) {
        List<RegisteredPerson> competitorsOfFight = new ArrayList<>();
        List<RegisteredPerson> allCompetitors = new ArrayList<>();
        for (Fight fight : fights) {
            allCompetitors.addAll(fight.getTeam1().getMembersOrder(0).values());
            allCompetitors.addAll(fight.getTeam2().getMembersOrder(0).values());
        }
        for (RegisteredPerson competitor : allCompetitors) {
            if (!competitorsOfFight.contains(competitor)) {
                competitorsOfFight.add(competitor);
            }
        }
        return competitorsOfFight;
    }

    public static List<Team> getTeamRanking(List<Fight> fights) {
        List<Team> teamsOfFights = getTeams(fights);
        List<ScoreOfTeam> scores = new ArrayList<>();
        for (Team team : teamsOfFights) {
            scores.add(new ScoreOfTeam(team, fights));
        }
        Collections.sort(scores);
        List<Team> teamRanking = new ArrayList<>();
        for (ScoreOfTeam score : scores) {
            teamRanking.add(score.getTeam());
        }
        return teamRanking;
    }

    public static List<ScoreOfTeam> getTeamScoreRanking(List<Fight> fights) {
        List<Team> teamsOfFights = getTeams(fights);
        List<ScoreOfTeam> scores = new ArrayList<>();
        for (Team team : teamsOfFights) {
            scores.add(new ScoreOfTeam(team, fights));
        }
        Collections.sort(scores);

        return scores;
    }

    public static List<ScoreOfCompetitor> getCompetitorsScoreRanking(List<Fight> fights) {
        List<RegisteredPerson> competitors = getRegisteredPersons(fights);
        List<ScoreOfCompetitor> scores = new ArrayList<>();
        for (RegisteredPerson competitor : competitors) {
            scores.add(new ScoreOfCompetitor(competitor, fights));
        }
        Collections.sort(scores);
        return scores;
    }

    public static Integer getOrder(List<Fight> fights, Team team) {
        List<Team> ranking = getTeamRanking(fights);

        for (Integer i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).equals(team)) {
                return i;
            }
        }
        return null;
    }

    public static Integer getOrderFromRanking(List<ScoreOfTeam> ranking, Team team) {
        for (Integer i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getTeam().equals(team)) {
                return i;
            }
        }
        return null;
    }

    public static Team getTeam(List<Fight> fights, Integer order) {
        List<Team> ranking = getTeamRanking(fights);
        if (order < ranking.size() && order > 0) {
            return ranking.get(order);
        }
        return null;
    }

    public static List<RegisteredPerson> getCompetitorsRanking(List<Fight> fights) {
        List<RegisteredPerson> competitors = getRegisteredPersons(fights);
        List<ScoreOfCompetitor> scores = new ArrayList<>();
        for (RegisteredPerson competitor : competitors) {
            scores.add(new ScoreOfCompetitor(competitor, fights));
        }
        Collections.sort(scores);
        List<RegisteredPerson> competitorsRanking = new ArrayList<>();
        for (ScoreOfCompetitor score : scores) {
            competitorsRanking.add(score.getRegisteredPerson());
        }
        return competitorsRanking;
    }
}
