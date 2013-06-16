package com.softwaremagico.ktg.tournament;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Defines a group of teams that fight together in a tournament. A league has
 * multiple groups but a simple tournament only has one.
 */
public class TournamentGroup implements Serializable {

    public static final int MAX_TEAMS_PER_GROUP = 8;
    private Tournament tournament;
    private List<Team> teams = new ArrayList<>();
    private Integer numberMaxOfWinners = 1;
    protected Integer level;
    private Integer fightArea = 0;
    private List<Fight> fightsOfGroup;

    public TournamentGroup(Tournament tournament, Integer level, Integer fightArea) {
        this.tournament = tournament;
        this.level = level;
        this.fightArea = fightArea;
        if (level == 0) {
            this.numberMaxOfWinners = tournament.getHowManyTeamsOfGroupPassToTheTree();
        } else {
            this.numberMaxOfWinners = 1;
        }
    }

    public void setMaxNumberOfWinners(int value) {
        if (numberMaxOfWinners < 1) {
            numberMaxOfWinners = 1;
        } else {
            numberMaxOfWinners = value;
        }
    }

    public int getMaxNumberOfWinners() {
        if (level > 0) {
            return 1;
        } else {
            return numberMaxOfWinners;
        }
    }

    public int getLevel() {
        return level;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Integer getFightArea() {
        return fightArea;
    }

    public void setFightArea(Integer fightArea) {
        this.fightArea = fightArea;
    }

    public int numberOfTeams() {
        return teams.size();
    }

    public void removeTeams() {
        teams = new ArrayList<>();
    }

    public void addTeam(Team team) {
        //Can not be repeated.
        if (!teams.contains(team)) {
            teams.add(team);
            //Delete one, because cannot be more than eight.
            if (teams.size() > MAX_TEAMS_PER_GROUP) {
                teams.remove(0);
            }
        }
    }

    public void addTeams(List<Team> tmp_teams) {
        teams.addAll(tmp_teams);
    }

    private boolean isTeamOfGroup(Team team) {
        return teams.contains(team);
    }

    public List<Team> getWinners() {
        try {
            return Ranking.getTeamsRanking(getFights()).subList(0, numberMaxOfWinners);
        } catch (Exception iob) {
        }

        return new ArrayList<>();
    }

    public boolean isFightOfGroup(Fight f) {
        if (isTeamOfGroup(f.getTeam1()) && isTeamOfGroup(f.getTeam2()) && level == f.getLevel()) {
            return true;
        }
        return false;
    }

    public List<Fight> getFights() {
        try {
            if (fightsOfGroup == null || fightsOfGroup.isEmpty()) {
                fightsOfGroup = getFightsOfGroup(FightPool.getInstance().getFromLevel(tournament, level));
            }
        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
        return fightsOfGroup;
    }

    private List<Fight> getFightsOfGroup(List<Fight> fights) {
        List<Fight> fightsG = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).getLevel() == level) {
                if (teams.contains(fights.get(i).getTeam1()) || teams.contains(fights.get(i).getTeam2())) {
                    fightsG.add(fights.get(i));
                }
            }
        }
        return fightsG;
    }

    public List<Fight> createFights(boolean random, Integer groupIndex) {
        if (getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        RemainingFights remainingFights = new RemainingFights(getTeams());

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
                fight = new Fight(tournament, team1, team2, getFightArea(), getLevel(), groupIndex);
            } else {
                fight = new Fight(tournament, team2, team1, getFightArea(), getLevel(), groupIndex);
            }
            fights.add(fight);
            remainingFights.removeAdveresary(team1, team2);
            team1 = team2;
        }
        return fights;
    }

    public List<Fight> createLoopFights(boolean random) {
        if (getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        RemainingFights remainingFights = new RemainingFights(getTeams());

        List<Team> remainingTeams = remainingFights.getTeams();
        if (random) {
            Collections.shuffle(remainingTeams);
        }
        for (Team team : remainingTeams) {
            for (Team adversary : remainingFights.getAdversaries(team)) {
                Fight fight = new Fight(tournament, team, adversary, getFightArea(), getLevel(), 0);
                fights.add(fight);
            }
        }

        return fights;
    }

    public boolean areFightsOver(List<Fight> allFights) {
        List<Fight> fights = getFightsOfGroup(allFights);

        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (!fights.get(i).isOver()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean areFightsOver() {
        List<Fight> fights = getFights();

        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (!fights.get(i).isOver()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * All but the last fight are over.
     *
     * @return
     */
    public boolean inTheLastFight() {
        List<Fight> fights = getFights();
        if (fights.size() > 0) {
            if (!fights.get(fights.size() - 1).isOver()) {
                if (fights.size() == 1 || fights.get(fights.size() - 2).isOver()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean areFightsStarted() {
        List<Fight> fights = getFights();

        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                return false;
            }
        }
        return true;
    }

    public boolean areFightsOverOrNull() {
        if (teams.size() < 2) {
            return true;
        }
        return areFightsOverOrNull(getFights());
    }

    /**
     * If the fightManager are over or fightManager are not needed.
     */
    public static boolean areFightsOverOrNull(List<Fight> fights) {
        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (!fights.get(i).isOver()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    class RemainingFights {

        List<Team> teams;
        HashMap<Team, List<Team>> combination;

        protected RemainingFights(List<Team> teams) {
            this.teams = teams;
            Collections.sort(teams);
            combination = getAdversaries();
        }

        public List<Team> getAdversaries(Team team) {
            return combination.get(team);
        }

        public List<Team> getTeams() {
            return teams;
        }

        private HashMap<Team, List<Team>> getAdversaries() {
            HashMap<Team, List<Team>> combinations = new HashMap<>();
            for (int i = 0; i < teams.size(); i++) {
                List<Team> otherTeams = new ArrayList<>();
                combinations.put(teams.get(i), otherTeams);

                for (int j = 0; j < teams.size(); j++) {
                    if (i != j) {
                        otherTeams.add(teams.get(j));
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
            //Get max Adversaries value:
            for (Team team : teamGroup) {
                if (combination.get(team).size() > maxAdv) {
                    maxAdv = combination.get(team).size();
                }
            }

            //Select one of the teams with max adversaries
            List<Team> possibleAdversaries = new ArrayList<>();
            for (Team team : teamGroup) {
                if (combination.get(team).size() == maxAdv) {
                    //If no random, return the first one. 
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

    @Override
    public String toString() {
        return "Group in level: " + level + ", fight area: " + fightArea + ", teams " + teams;
    }
}
