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
import java.util.List;

/**
 * Defines a group of teams that fight together in a tournament. A league has
 * multiple groups but a simple tournament only has one.
 */
public abstract class TGroup implements Serializable {

    private Tournament tournament;
    private List<Team> teams = new ArrayList<>();
    private Integer numberMaxOfWinners = 1;
    protected Integer level;
    private Integer fightArea = 0;
    private List<Fight> fightsOfGroup;

    public TGroup(Tournament tournament, Integer level, Integer fightArea) {
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
            //If only exists one team in this group, is the winner. 
            if (getTeams().size() == 1) {
                return getTeams();
            }
            //If exists fights, the winner is the first of the ranking.
            if (getFights() != null && getFights().size() > 0) {
                return Ranking.getTeamsRanking(getFights()).subList(0, numberMaxOfWinners);
            }
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

    public abstract List<Fight> createFights(boolean random);

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

    @Override
    public String toString() {
        return "(" + tournament + ")Group in level: " + level + ", fight area: " + fightArea + ", teams " + teams + "\n";
    }
}
