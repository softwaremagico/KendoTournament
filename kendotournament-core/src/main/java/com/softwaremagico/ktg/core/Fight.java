package com.softwaremagico.ktg.core;
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

import com.softwaremagico.ktg.database.DuelPool;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fight implements Serializable, Comparable<Fight> {

    private static final Integer DEFAULT_WINNER_VALUE = 2;
    private static final String FIGHT_TAG = "FIGHT";
    private Team team1;
    private Team team2;
    private Tournament tournament;
    private Integer asignedFightArea;
    private Integer winner;   //-1-> Winner team1, 1-> Winner team2, 0-> Draw Game, 2-> Not finished
    private Integer group;
    private Integer groupIndex;
    private Integer level;

    public Fight(Tournament tournament, Team team1, Team team2, int asignedArea, int level, int group, int groupIndex) {
        this.team1 = team1;
        this.team2 = team2;
        this.tournament = tournament;
        this.asignedFightArea = asignedArea;
        this.group = group;
        this.groupIndex = groupIndex;
        this.level = level;
    }

    public Integer getGroupIndex() {
        return groupIndex;
    }

    public Integer getGroup() {
        return group;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Integer getAsignedFightArea() {
        return asignedFightArea;
    }

    public void setAsignedFightArea(int asignedFightArea) {
        this.asignedFightArea = asignedFightArea;
    }

    public List<Duel> getDuels() {
        try {
            return DuelPool.getInstance().get(tournament, this);
        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
        return new ArrayList<>();
    }

    public boolean isOver() {
        if (winner != null) {
            return winner < 2;
        }
        return false;
    }

    public Integer getWinner() {
        if (winner != null) {
            return winner;
        }
        return DEFAULT_WINNER_VALUE;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public void setOver(boolean over) {
        if (over) {
            completeIppons();
            try {
                Team winnerTeam = winner();
                if (winnerTeam.getName().equals(team1.getName())) {
                    winner = -1;
                } else if (winnerTeam.getName().equals(team2.getName())) {
                    winner = 1;
                } else {
                    winner = 0;
                }
            } catch (NullPointerException npe) {
                winner = 0;
            }
        } else {
            winner = DEFAULT_WINNER_VALUE;
        }
    }

    /**
     * To win a fight, a team need to win more duels or do more points.
     *
     * @return
     */
    public boolean isDrawFight() {
        if (winner() == null) {
            return true;
        }
        return false;
    }

    public Team winner() {
        int points = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            points += getDuels().get(i).winner();
        }
        if (points < 0) {
            return team1;
        }
        if (points > 0) {
            return team2;
        }
        //If are draw rounds, win who has more points.
        int pointLeft = 0;
        int pointRight = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            pointLeft += getDuels().get(i).getScore(true);
            pointRight += getDuels().get(i).getScore(false);
        }
        if (pointLeft > pointRight) {
            return team1;
        }
        if (pointLeft < pointRight) {
            return team2;
        }
        return null;
    }

    public void showDuels() {
        System.out.println("---------------");
        System.out.println(team1.getName() + " vs " + team2.getName());
        for (int i = 0; i < getDuels().size(); i++) {
            System.out.println(team1.getMember(i, level).getName() + ": " + getDuels().get(i).getHits(true).get(0).getAbbreviature() + " " + getDuels().get(i).getHits(true).get(1).getAbbreviature() + " Faults: " + getDuels().get(i).getFaults(true) + " vs " + team2.getMember(i, level).getName() + ": " + getDuels().get(i).getHits(false).get(0).getAbbreviature() + " " + getDuels().get(i).getHits(false).get(1).getAbbreviature() + " Faults: " + getDuels().get(i).getFaults(false));
        }
        System.out.println("---------------");
    }

    private void completeIppons() {
        for (int i = 0; i < team1.getNumberOfMembers(level); i++) {
            if (((i < team2.getNumberOfMembers(level) && team2.getMember(i, level) != null) //There is a player
                    && (!team2.getMember(i, level).getName().equals("")
                    || !team2.getMember(i, level).getId().equals("")))
                    && ((team1.getMember(i, level) == null) //Versus no player.
                    || (team1.getMember(i, level).getName().equals("")
                    && team1.getMember(i, level).getId().equals("")))) {
                getDuels().get(i).completeIppons(false);
                // DatabaseConnection.getInstance().getDatabase().storeDuel(getDuels().get(i), this, i);
            }
        }

        for (int i = 0; i < team2.getNumberOfMembers(level); i++) {
            if (((i < team1.getNumberOfMembers(level) && team1.getMember(i, level) != null) //There is a player
                    && (!team1.getMember(i, level).getName().equals("")
                    || !team1.getMember(i, level).getId().equals("")))
                    && ((team2.getMember(i, level) == null) //Versus no player.
                    || (team2.getMember(i, level).getName().equals("")
                    && team2.getMember(i, level).getId().equals("")))) {
                getDuels().get(i).completeIppons(true);
                // DatabaseConnection.getInstance().getDatabase().storeDuel(getDuels().get(i), this, i);
            }
        }

    }

    public Integer getWonDuels(Team team) {
        if (team1.equals(team)) {
            return getWonDuels(true);
        }
        if (team2.equals(team)) {
            return getWonDuels(false);
        }
        return 0;
    }

    public Integer getWonDuels(RegisteredPerson competitor) {
        int winDuels = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            if (team1.getMember(i, level).equals(competitor) && getDuels().get(i).winner() < 0) {
                winDuels++;
            }
            if (team2.getMember(i, level).equals(competitor) && getDuels().get(i).winner() > 0) {
                winDuels++;
            }
        }
        return winDuels;
    }

    public Integer getWonDuels(boolean team1) {
        int winDuels = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            if (getDuels().get(i).winner() < 0 && team1) {
                winDuels++;
            }
            if (getDuels().get(i).winner() > 0 && !team1) {
                winDuels++;
            }
        }
        return winDuels;
    }

    public Integer getDrawDuels(Team team) {
        int drawDuels = 0;
        if ((getTeam1().equals(team) || getTeam2().equals(team))) {
            for (int i = 0; i < getDuels().size(); i++) {
                if (getDuels().get(i).winner() == 0) {
                    drawDuels++;
                }
            }
        }
        return drawDuels;
    }

    public Integer getDrawDuels(RegisteredPerson competitor) {
        int drawDuels = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            if ((team1.getMember(i, level).equals(competitor) || team2.getMember(i, level).equals(competitor)) && getDuels().get(i).winner() == 0) {
                drawDuels++;
            }
        }
        return drawDuels;
    }

    public Integer getScore(Team team) {
        if (team1.equals(team)) {
            return getScore(true);
        }
        if (team2.equals(team)) {
            return getScore(false);
        }
        return 0;
    }

    public Integer getScore(boolean team1) {
        int score = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            score += getDuels().get(i).getScore(team1);
        }
        return score;
    }

    public Integer getScore(RegisteredPerson competitor) {
        int hits = 0;
        for (int i = 0; i < getDuels().size(); i++) {
            if (team1.getMember(i, level).equals(competitor)) {
                hits += getDuels().get(i).getScore(true);
            }
            if (team2.getMember(i, level).equals(competitor)) {
                hits += getDuels().get(i).getScore(false);
            }
        }
        return hits;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.team1);
        hash = 41 * hash + Objects.hashCode(this.team2);
        hash = 41 * hash + Objects.hashCode(this.tournament);
        hash = 41 * hash + Objects.hashCode(this.groupIndex);
        hash = 41 * hash + Objects.hashCode(this.group);
        hash = 41 * hash + Objects.hashCode(this.level);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fight other = (Fight) obj;
        if (!Objects.equals(this.team1, other.team1)) {
            return false;
        }
        if (!Objects.equals(this.team2, other.team2)) {
            return false;
        }
        if (!Objects.equals(this.tournament, other.tournament)) {
            return false;
        }
        if (!Objects.equals(this.groupIndex, other.groupIndex)) {
            return false;
        }
        if (!Objects.equals(this.group, other.group)) {
            return false;
        }
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        return true;
    }

    public String show() {
        return "'" + team1.getName() + "' vs '" + team2.getName() + "'";
    }

    @Override
    public String toString() {
        String text = "(" + group + "/" + groupIndex + ") Tournament: " + tournament + ", Area: " + asignedFightArea + ", Teams: '" + team1.getName() + "' vs '" + team2.getName() + "'\n";
        for (Duel d : getDuels()) {
            text += d + "\n";
        }
        return text;
    }

    /**
     * @param order Order starts in 1.
     * @return
     */
    public List<String> exportToCsv(int order) {
        List<String> csv = new ArrayList<>();
        csv.add(FIGHT_TAG + ";" + order + ";" + team1.getName() + ";" + team2.getName());
        for (Duel d : getDuels()) {
            csv.add(d.exportToCsv());
        }
        return csv;
    }

    /**
     * @param order Order starts in 1.
     * @return
     */
    public List<String> exportToCsv(int order, int group, int level) {
        List<String> csv = new ArrayList<>();
        csv.add(FIGHT_TAG + ";" + order + ";" + group + ";" + level + ";" + team1.getName() + ";" + team2.getName());
        for (Duel d : getDuels()) {
            csv.add(d.exportToCsv());
        }
        return csv;
    }

    public static String getTag() {
        return FIGHT_TAG;
    }

    @Override
    public int compareTo(Fight o) {
        Integer levelCompare = level.compareTo(o.level);
        if (levelCompare != 0) {
            return levelCompare;
        }
        return groupIndex.compareTo(o.groupIndex);
    }
}
