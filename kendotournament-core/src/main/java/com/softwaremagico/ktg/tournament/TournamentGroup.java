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
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public TournamentGroup(Integer numberMaxOfWinners, Tournament championship, Integer level, Integer fightArea) {
        this.tournament = championship;
        this.level = level;
        this.fightArea = fightArea;
        this.numberMaxOfWinners = numberMaxOfWinners;
    }

    public void updateMaxNumberOfWinners(int value) {
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
            return Ranking.getTeamsRanking(FightPool.getInstance().get(tournament)).subList(0, numberMaxOfWinners);
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
        return getFightsOfGroup(FightPool.getInstance().getFromLevel(tournament, level));
    }

    private List<Fight> getFightsOfGroup(List<Fight> fights) {
        List<Fight> fightsG = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            for (int j = 0; j < teams.size(); j++) {
                try {
                    if ((fights.get(i).getTeam1().getName().equals(teams.get(j).getName())
                            || fights.get(i).getTeam2().getName().equals(teams.get(j).getName()))
                            && fights.get(i).getLevel() == level) {
                        fightsG.add(fights.get(i));
                        break;
                    }
                } catch (NullPointerException npe) {
                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
                }
            }
        }
        return fightsG;
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

    public boolean areFightsStarted() {
        List<Fight> fights = getFights();

        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                return false;
            }
        }
        return true;
    }

    /**
     * If the fightManager are over or fightManager are not needed.
     */
    public boolean areFightsOverOrNull(List<Fight> allFights) {
        List<Fight> fights = getFightsOfGroup(allFights);

        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (!fights.get(i).isOver()) {
                    return false;
                }
            }
            return true;
        } else if (teams.size() > 1) { //If there are only one team, no fightManager are needed.
            return false;
        }
        return true;
    }
}
