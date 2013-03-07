package com.softwaremagico.ktg;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class Duel implements Serializable, Comparable<Duel> {

    private Fight fight;
    private Integer order;
    private static final String DUEL_TAG = "DUEL";
    private final int POINTS_TO_WIN = 2;
    private List<Score> hitsFromCompetitorA = new ArrayList<>(); //M, K, T, D, H, I
    private List<Score> hitsFromCompetitorB = new ArrayList<>(); //M, K, T, D, H, I
    private Boolean faultsCompetitorA = false;
    private Boolean faultsCompetitorB = false;

    public Duel(Fight fight, Integer order) {
        this.fight = fight;
        this.order = order;
        addRounds();
    }

    public Fight getFight() {
        return fight;
    }

    public Integer getOrder() {
        return order;
    }

    public void setHit(boolean playerA, Integer hitNumber, Score score) {
        if (playerA) {
            hitsFromCompetitorA.set(hitNumber, score);
        } else {
            hitsFromCompetitorB.set(hitNumber, score);
        }
    }

    public List<Score> getHits(boolean playerA) {
        if (playerA) {
            return hitsFromCompetitorA;
        } else {
            return hitsFromCompetitorB;
        }
    }

    /**
     * Generate the structure of the rounds.
     */
    private void addRounds() {
        for (int i = 0; i < POINTS_TO_WIN; i++) {
            hitsFromCompetitorA.add(Score.EMPTY);
            hitsFromCompetitorB.add(Score.EMPTY);
        }
    }

    /**
     * Count the rounds and the punction of each player to know if the duels is
     * over or not.
     *
     * @return true if the round is over.
     */
    public boolean isOver() {
        int pointA = 0;
        int pointB = 0;
        for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
            if (Score.isValidPoint(hitsFromCompetitorA.get(i))) {
                pointA++;
            }
        }
        for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
            if (Score.isValidPoint(hitsFromCompetitorB.get(i))) {
                pointB++;
            }
        }
        if (pointA >= POINTS_TO_WIN || pointB >= POINTS_TO_WIN) {
            return true;
        }
        return false;
    }

    /**
     * Add a hit to a player to store.
     *
     * @param round Number of round of the result is obtained.
     * @param result Hit caused.
     * @param player1 True if is the player1 who caused the hit, false if is the
     * player2.
     * @return the round updated.
     */
    public Integer setResultInRound(int round, Score result, boolean player1) {
        int roundUpdated = round;
        try {
            if (Score.isValidPoint(result)) {
                if (player1) {
                    //If the first round has no point and we put the second one, it is a mistake!
                    if (round > 0 && hitsFromCompetitorA.get(round - 1).equals(Score.EMPTY) && !result.equals(Score.EMPTY)) {
                        roundUpdated = setResultInRound(round - 1, result, player1);
                    } else {
                        hitsFromCompetitorA.set(round, result);
                        //It is impossible that both players has the second point.
                        if ((round == hitsFromCompetitorA.size() - 1) && (!result.equals(Score.EMPTY))) {
                            setResultInRound(round, Score.EMPTY, !player1);
                        }
                    }
                } else {
                    //If the first round has no point and we put the second one, is a mistake!
                    if (round > 0 && hitsFromCompetitorB.get(round - 1).equals(Score.EMPTY) && !result.equals(Score.EMPTY)) {
                        roundUpdated = setResultInRound(round - 1, result, player1);
                    } else {
                        hitsFromCompetitorB.set(round, result);
                        //It is impossible that both players has the second point.
                        if ((round == hitsFromCompetitorB.size() - 1) && (!result.equals(Score.EMPTY))) {
                            setResultInRound(round, Score.EMPTY, !player1);
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException iob) {
        }
        return roundUpdated;
    }

    public void clearResultInRound(int round, boolean player1) {
        try {
            if (player1) {
                for (int i = round; i < POINTS_TO_WIN; i++) {
                    hitsFromCompetitorA.set(i, Score.EMPTY);
                }
            } else {
                for (int i = round; i < POINTS_TO_WIN; i++) {
                    hitsFromCompetitorB.set(i, Score.EMPTY);
                }
            }
        } catch (IndexOutOfBoundsException iob) {
        }
    }

    public void setFaults(boolean player1) {
        int faultRound = howManyPoints((!player1));
        if (player1) {
            if (faultsCompetitorA == true) {
                setResultInRound(faultRound, Score.HANSOKU, !player1);
                faultsCompetitorA = false;
            } else {
                faultsCompetitorA = true;
            }
        } else {
            if (faultsCompetitorB == true) {
                setResultInRound(faultRound, Score.HANSOKU, !player1);
                faultsCompetitorB = false;
            } else {
                faultsCompetitorB = true;
            }
        }
    }

    public Boolean getFaults(boolean player1) {
        if (player1) {
            return faultsCompetitorA;
        } else {
            return faultsCompetitorB;
        }
    }

    public void resetFaults(boolean player1) {
        if (player1) {
            faultsCompetitorA = false;
        } else {
            faultsCompetitorB = false;
        }
    }

    public Integer howManyPoints(boolean player1) {
        int round = 0;
        if (player1) {
            for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
                if (!hitsFromCompetitorA.get(i).equals(Score.EMPTY)) {
                    round++;
                } else {
                    return round;
                }
            }
        } else {
            for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
                if (!hitsFromCompetitorB.get(i).equals(Score.EMPTY)) {
                    round++;
                } else {
                    return round;
                }
            }
        }
        return round;
    }

    /**
     * Gets the winner of the duel.
     *
     * @return -1 if player1, 0 if draw, 1 if player2
     */
    public int winner() {
        int pointA = 0;
        int pointB = 0;
        for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
            if (Score.isValidPoint(hitsFromCompetitorA.get(i))) {
                pointA++;
            }
        }
        for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
            if (Score.isValidPoint(hitsFromCompetitorB.get(i))) {
                pointB++;
            }
        }

        if (pointA > pointB) {
            return -1;
        }
        if (pointA < pointB) {
            return 1;
        }
        return 0;
    }

    private int getHits(Score sc) {
        int count = 0;
        for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
            if (hitsFromCompetitorA.get(i).equals(sc)) {
                count++;
            }
        }
        for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
            if (hitsFromCompetitorB.get(i).equals(sc)) {
                count++;
            }
        }
        return count;
    }

    public int getMems() {
        return getHits(Score.MEN);
    }

    public int getKotes() {
        return getHits(Score.KOTE);
    }

    public int getDoes() {
        return getHits(Score.DO);
    }

    public int getTsukis() {
        return getHits(Score.TSUKI);
    }

    public int getHansokus() {
        return getHits(Score.HANSOKU);
    }

    public int getIppones() {
        return getHits(Score.IPPON);
    }

    private int getHits(Score sc, boolean leftPlayer) {
        int count = 0;
        if (leftPlayer) {
            for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
                if (hitsFromCompetitorA.get(i).equals(sc)) {
                    count++;
                }
            }
        } else {
            for (int i = 0; i < hitsFromCompetitorB.size(); i++) {
                if (hitsFromCompetitorB.get(i).equals(sc)) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getMems(boolean leftPlayer) {
        return getHits(Score.MEN, leftPlayer);
    }

    public int getKotes(boolean leftPlayer) {
        return getHits(Score.KOTE, leftPlayer);
    }

    public int getDoes(boolean leftPlayer) {
        return getHits(Score.DO, leftPlayer);
    }

    public int getTsukis(boolean leftPlayer) {
        return getHits(Score.TSUKI, leftPlayer);
    }

    public int getHansokus(boolean leftPlayer) {
        return getHits(Score.HANSOKU, leftPlayer);
    }

    public int getIppones(boolean leftPlayer) {
        return getHits(Score.IPPON, leftPlayer);
    }

    public void completeIppons(boolean leftPlayer) {
        setResultInRound(0, Score.IPPON, leftPlayer);
        setResultInRound(1, Score.IPPON, leftPlayer);
    }

    public String showScore() {
        String score = "";
        score += "TeamA: ";
        for (Score s : hitsFromCompetitorA) {
            score += s.getAbbreviature() + " ";
        }

        score += "(f:" + faultsCompetitorA + "), ";
        score += "TeamB: ";
        for (Score s : hitsFromCompetitorB) {
            score += s.getAbbreviature() + " ";
        }
        score += "(f:" + faultsCompetitorB + ")";
        return score;
    }

    private Score getScoreFromField(String field) {
        if (field.length() == 1) {
            return Score.getScore(field.charAt(0));
        }
        return Score.EMPTY;
    }

    public String exportToCsv() {
        String Csv = DUEL_TAG + ";";
        for (Score s : hitsFromCompetitorA) {
            Csv += s.getAbbreviature() + ";";
        }
        Csv += ((!faultsCompetitorA) ? Score.EMPTY.getAbbreviature() : Score.FAULT.getAbbreviature()) + ";";

        for (Score s : hitsFromCompetitorB) {
            Csv += s.getAbbreviature() + ";";
        }
        Csv += ((!faultsCompetitorB) ? Score.EMPTY.getAbbreviature() : Score.FAULT.getAbbreviature()) + ";";
        return Csv;
    }

    public void importFromCsv(String csvLine) {
        //TODO No lee las faltas!!!
        String[] fields = csvLine.split(";");
        for (int i = 0; i < POINTS_TO_WIN; i++) {
            setResultInRound(i, getScoreFromField(fields[i + 1]), true);
            setResultInRound(i, getScoreFromField(fields[i + 1 + POINTS_TO_WIN]), false);
        }
    }

    public static String getCsvTag() {
        return DUEL_TAG;
    }

    @Override
    public String toString() {
        String text = "Player A  (";
        for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
            text += (hitsFromCompetitorA.get(i).getAbbreviature());
        }
        text += (") Player B (");
        for (int i = 0; i < hitsFromCompetitorA.size(); i++) {
            text += (hitsFromCompetitorB.get(i).getAbbreviature());
        }
        text += ")";

        return text;
    }

    @Override
    public int compareTo(Duel o) {
        Integer levelCompare = getFight().getLevel().compareTo(o.getFight().getLevel());
        if (levelCompare != 0) {
            return levelCompare;
        }
        Integer indexCompare = getFight().getIndex().compareTo(o.getFight().getIndex());
        if (indexCompare != 0) {
            return indexCompare;
        }
        return order.compareTo(o.order);
    }
}
