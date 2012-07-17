package com.softwaremagico.ktg;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class Fight implements Serializable {

    public Team team1;
    public Team team2;
    public Tournament competition;
    public int asignedFightArea;
    public List<Duel> duels = new ArrayList<>();
    private int winner;   //-1-> Winner team1, 1-> Winner team2, 0-> Draw Game, 2-> Not finished
    public int level;
    private int maxWinners = 1;
    private boolean overUpdated = false; //Over value has been stored into the database or not. 

    public boolean isOverStored() {
        return overUpdated;
    }

    public void setOverStored(boolean overUpdated) {
        this.overUpdated = overUpdated;
    }

    public void calculateOverWithDuels() {
        if (returnWinner() == 2) {
            overUpdated = false;
        } else {
            overUpdated = true;
        }
    }

    public Fight(Team tmp_team1, Team tmp_team2, Tournament tmp_tournament, int asignedArea, int tmp_winner, int tmp_level) {
        team1 = tmp_team1;
        team2 = tmp_team2;
        competition = tmp_tournament;
        asignedFightArea = asignedArea;
        addDuels();
        winner = tmp_winner;
        level = tmp_level;
    }

    public Fight(Team tmp_team1, Team tmp_team2, Tournament tmp_tournament, int asignedArea, int tmp_level) {
        team1 = tmp_team1;
        team2 = tmp_team2;
        competition = tmp_tournament;
        asignedFightArea = asignedArea;
        addDuels();
        winner = 2;
        level = tmp_level;
    }

    public Fight(Team tmp_team1, Team tmp_team2, Tournament tmp_tournament, int asignedArea) {
        team1 = tmp_team1;
        team2 = tmp_team2;
        competition = tmp_tournament;
        asignedFightArea = asignedArea;
        addDuels();
        winner = 2;
        level = 0;
    }

    public boolean isOver() {
        /*
         * try { Team winnerTeam = winner(); if
         * (winnerTeam.returnName().equals(team1.returnName())) { winner = -1;
         * return -1; } if (winnerTeam.returnName().equals(team2.returnName()))
         * { winner = 1; return 1; } } catch (NullPointerException npe) { return
         * winner; } winner = 2;
         */
        return winner < 2;
    }

    public int returnWinner() {
        return winner;
    }

    public void setOver() {
        completeIppons();
        if (winner == 2) {
            overUpdated = false;
        }
        try {
            Team winnerTeam = winner();
            if (winnerTeam.returnName().equals(team1.returnName())) {
                winner = -1;
            } else if (winnerTeam.returnName().equals(team2.returnName())) {
                winner = 1;
            } else {
                winner = 0;
            }
        } catch (NullPointerException npe) {
            winner = 0;
        }
    }

    public void setAsNotOver() {
        winner = 2;
        overUpdated = false;
    }

    public final boolean addDuels() {
        try {
            int bound;
            for (int i = 0; i < competition.teamSize; i++) {
                duels.add(new Duel());
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return true;
    }

    public void setDuel(Duel d, int player) {
        duels.set(player, d);
    }

    /**
     * Return the winner only counting the duels.
     *
     * @return Team winner of match.
     */
    public Team winnerByDuels() {
        int points1 = 0;
        int points2 = 0;
        int duelsScore = 0;
        for (int i = 0; i < duels.size(); i++) {
            duelsScore += duels.get(i).winner();
        }
        if (duelsScore < 0) {
            return team1;
        }
        if (duelsScore > 0) {
            return team2;
        }

        for (int i = 0; i < duels.size(); i++) {
            points1 += duels.get(i).howManyPoints(true);
            points2 += duels.get(i).howManyPoints(false);
        }

        if (points1 > points2) {
            return team1;
        }
        if (points1 < points2) {
            return team2;
        }

        return null;
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
        for (int i = 0; i < duels.size(); i++) {
            points += duels.get(i).winner();
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
        for (int i = 0; i < duels.size(); i++) {
            pointLeft += duels.get(i).howManyPoints(true);
            pointRight += duels.get(i).howManyPoints(false);
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
        System.out.println(team1.returnName() + " vs " + team2.returnName());
        for (int i = 0; i < duels.size(); i++) {
            System.out.println(team1.getMember(i, level).getName() + ": " + duels.get(i).hitsFromCompetitorA.get(0).getAbbreviature() + " " + duels.get(i).hitsFromCompetitorA.get(1).getAbbreviature() + " Faults: " + duels.get(i).faultsCompetitorA + " vs " + team2.getMember(i, level).getName() + ": " + duels.get(i).hitsFromCompetitorB.get(0).getAbbreviature() + " " + duels.get(i).hitsFromCompetitorB.get(1).getAbbreviature() + " Faults: " + duels.get(i).faultsCompetitorB);
        }
        System.out.println("---------------");
    }

    public void changeMaxWinners(int value) {
        maxWinners = value;
    }

    public int getMaxWinners() {
        return maxWinners;
    }

    private void completeIppons() {
        for (int i = 0; i < team1.getNumberOfMembers(level); i++) {
            if (((i < team2.getNumberOfMembers(level) && team2.getMember(i, level) != null) //There is a player
                    && (!team2.getMember(i, level).getName().equals("")
                    || !team2.getMember(i, level).id.equals("")))
                    && ((team1.getMember(i, level) == null) //Versus no player.
                    || (team1.getMember(i, level).getName().equals("")
                    && team1.getMember(i, level).id.equals("")))) {
                duels.get(i).completeIppons(false);
                KendoTournamentGenerator.getInstance().database.storeDuel(duels.get(i), this, i);
            }
        }

        for (int i = 0; i < team2.getNumberOfMembers(level); i++) {
            if (((i < team1.getNumberOfMembers(level) && team1.getMember(i, level) != null) //There is a player
                    && (!team1.getMember(i, level).getName().equals("")
                    || !team1.getMember(i, level).id.equals("")))
                    && ((team2.getMember(i, level) == null) //Versus no player.
                    || (team2.getMember(i, level).getName().equals("")
                    && team2.getMember(i, level).id.equals("")))) {
                duels.get(i).completeIppons(true);
                KendoTournamentGenerator.getInstance().database.storeDuel(duels.get(i), this, i);
            }
        }

    }

    public int getWinnedDuels(boolean team1) {
        int winDuels = 0;
        for (int i = 0; i < duels.size(); i++) {
            if (duels.get(i).winner() < 0 && team1) {
                winDuels++;
            }
            if (duels.get(i).winner() > 0 && !team1) {
                winDuels++;
            }
        }
        return winDuels;
    }

    public int getScore(boolean team1) {
        int score = 0;
        for (int i = 0; i < duels.size(); i++) {
            score += duels.get(i).howManyPoints(team1);
        }
        return score;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Fight)) {
            return false;
        }
        Fight otherFight = (Fight) object;
        return this.team1.equals(otherFight.team1)
                && this.team2.equals(otherFight.team2)
                && this.competition.equals(otherFight.competition)
                && this.level == otherFight.level;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.team1 != null ? this.team1.hashCode() : 0);
        hash = 59 * hash + (this.team2 != null ? this.team2.hashCode() : 0);
        hash = 59 * hash + (this.competition != null ? this.competition.hashCode() : 0);
        hash = 59 * hash + this.level;
        return hash;
    }

    public boolean areUpdatedDuelsOfFight() {
        if (duels.isEmpty()) {
            return false;
        }
        for (Duel d : duels) {
            if (!d.isStored()) {
                return false;
            }
        }
        return true;
    }
    
    public String showFight(){
        return "'"+team1.returnName() + " vs " + team2.returnName()+"'";
    }
}
