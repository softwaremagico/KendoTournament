/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;

import java.util.ArrayList;
import java.util.List;
import com.softwaremagico.ktg.statistics.TeamRanking;

/**
 *
 * @author Jorge
 */
public class Ranking {

    //transient Tournament championship;
    public List<Team> teams = new ArrayList<Team>();
    private List<Double> teamsScore;
    private final Float SCORE_WON_FIGHTS = new Float(1000000);
    private final Float SCORE_WON_DUELS = new Float(10000);
    private final Float SCORE_HITS = new Float(1);
    private final Float SCORE_DRAW_FIGHTS = new Float(0.001);
    private final Float SCORE_DRAW_DUELS = new Float(0.000001);
    private final Float SCORE_GOLDEN_POINT = new Float(0.000001);

    public Ranking() {
    }

    /**
     * Calculate the score regarding to the hits.
     *
     * @param fight
     * @param team
     * @return
     */
    private double obtainPointsForHits(Fight fight, Team team) {
        double score = (double) 0;

        //Team1
        if (fight.team1.returnName().equals(team.returnName())) {
            for (int k = 0; k < fight.duels.size(); k++) {
                score += fight.duels.get(k).howManyPoints(true) * SCORE_HITS;
            }
        }

        //Team2
        if (fight.team2.returnName().equals(team.returnName())) {
            for (int k = 0; k < fight.duels.size(); k++) {
                score += fight.duels.get(k).howManyPoints(false) * SCORE_HITS;
            }
        }
        return score;
    }

    private int obtainPointsForWonDuels(Fight fight, Team team) {
        int score = 0;
        //Team1
        if (fight.team1.returnName().equals(team.returnName())) {
            for (int k = 0; k < fight.duels.size(); k++) {
                //Winned
                if (fight.duels.get(k).winner() < 0) {
                    score += SCORE_WON_DUELS * fight.competition.getScoreForWin();
                }
                //Draw
                if (fight.duels.get(k).winner() == 0) {
                    score += SCORE_WON_DUELS * fight.competition.getScoreForDraw();
                }
            }
        }
        //Team2
        if (fight.team2.returnName().equals(team.returnName())) {
            for (int k = 0; k < fight.duels.size(); k++) {
                //Winned
                if (fight.duels.get(k).winner() > 0) {
                    score += SCORE_WON_DUELS * fight.competition.getScoreForWin();
                }
                //Draw
                if (fight.duels.get(k).winner() == 0) {
                    score += SCORE_WON_DUELS * fight.competition.getScoreForDraw();
                }
            }
        }
        return score;
    }

    private Float obtainPointsForDrawFights(Fight fight, Team team) {
        Float score = new Float(0);
        try {
            if ((fight.team1.returnName().equals(team.returnName())
                    || fight.team2.returnName().equals(team.returnName()))) {
                for (int i = 0; i < fight.duels.size(); i++) {
                    if (fight.duels.get(i).winner() == 0) {
                        score += SCORE_DRAW_FIGHTS;
                    }
                }
            }
        } catch (NullPointerException npe) {
        }
        return score;
    }

    private Float obtainPointsForDrawDuels(Fight fight, Team team) {
        try {
            if ((fight.team1.returnName().equals(team.returnName())
                    || fight.team2.returnName().equals(team.returnName()))
                    && (fight.isDrawFight())) {
                return SCORE_DRAW_DUELS;
            }
        } catch (NullPointerException npe) {
        }
        return new Float(0);
    }

    private Float obtainPointsForWonFights(Fight fight, Team team) {
        try {
            if (fight.winnerByDuels().returnName().equals(team.returnName())) {
                return SCORE_WON_FIGHTS * fight.competition.getScoreForWin();
            } else if (fight.isDrawFight() && (fight.team1.returnName().equals(team.returnName())
                    || fight.team2.returnName().equals(team.returnName()))) {
                return SCORE_WON_FIGHTS * fight.competition.getScoreForDraw(); //In Custom championships the draw fight also has score. 
            }
        } catch (NullPointerException npe) {
        }
        return new Float(0);
    }

    public static int obtainWonFights(List<Fight> fights, Team team, int level) {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight oneFight = fights.get(j);
            if (fights.get(j).level == level || level == -1) {
                if ((oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                    if (oneFight.winnerByDuels() != null && oneFight.winnerByDuels().returnName().equals(team.returnName())) {
                        total++;
                    }
                }
            }
        }
        return total;
    }

    public static int obtainDrawFights(List<Fight> fights, Team team, int level) {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight oneFight = fights.get(j);
            if (fights.get(j).level == level || level == -1) {
                if ((oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                    if (oneFight.isDrawFight()) {
                        total++;
                    }
                }
            }
        }
        return total;
    }

    public static int obtainWonDuels(List<Fight> fights, Team team, int level) {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight oneFight = fights.get(j);

            if (fights.get(j).level == level || level == -1) {
                if ((oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                    //Team1
                    if (oneFight.team1.returnName().equals(team.returnName())) {
                        for (int k = 0; k < oneFight.duels.size(); k++) {
                            if (oneFight.duels.get(k).winner() < 0) {
                                total++;
                            }
                        }
                    }
                    //Team2
                    if (oneFight.team2.returnName().equals(team.returnName())) {
                        for (int k = 0; k < oneFight.duels.size(); k++) {
                            if (oneFight.duels.get(k).winner() > 0) {
                                total++;
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    public static int obtainDrawDuels(List<Fight> fights, Team team, int level) {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight oneFight = fights.get(j);
            if (fights.get(j).level == level || level == -1) {
                if ((oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                    //Team1
                    if (oneFight.team1.returnName().equals(team.returnName())
                            || (oneFight.team2.returnName().equals(team.returnName()))) {
                        for (int k = 0; k < oneFight.duels.size(); k++) {
                            if (oneFight.duels.get(k).winner() == 0) {
                                total++;
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    public static int obtainHits(List<Fight> fights, Team team, int level) {
        int total = 0;
        //String undraw;
        for (int j = 0; j < fights.size(); j++) {
            Fight oneFight = fights.get(j);
            if (fights.get(j).level == level || level == -1) {
                if ((oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                    //Team1
                    if (oneFight.team1.returnName().equals(team.returnName())) {
                        for (int k = 0; k < oneFight.duels.size(); k++) {
                            total += oneFight.duels.get(k).howManyPoints(true);
                        }
                    }

                    //Team2
                    if (oneFight.team2.returnName().equals(team.returnName())) {
                        for (int k = 0; k < oneFight.duels.size(); k++) {
                            total += oneFight.duels.get(k).howManyPoints(false);
                        }
                    }
                }
            }
        }
        return total;
    }

    /**
     * This function obtain the golden point for undraw two teams of a group.
     *
     * @param team
     * @param level
     * @return
     */
    private double obtainPointsForGoldenPoint(Team team) {
        String t;
        double score = (double) 0;
        //Undraw hits.
        double undraws = (double) KendoTournamentGenerator.getInstance().database.getValueWinnerInUndraws(team.competition.name, team.returnName());
        score += SCORE_GOLDEN_POINT * undraws;
        return score;
    }

    private void updateTeamsWithFights(List<Fight> fights) {
        teams = new ArrayList<Team>();
        for (int i = 0; i < fights.size(); i++) {
            boolean inserted1 = false;
            boolean inserted2 = false;
            for (int j = 0; j < teams.size(); j++) {
                if (teams.get(j).returnName().equals(fights.get(i).team1.returnName())) {
                    inserted1 = true;
                }
                if (teams.get(j).returnName().equals(fights.get(i).team2.returnName())) {
                    inserted2 = true;
                }
            }
            if (!inserted1) {
                teams.add(fights.get(i).team1);
            }
            if (!inserted2) {
                teams.add(fights.get(i).team2);
            }
        }
    }

    /**
     * Creates a score for a team. The score is 1000000 for each duel won, 1000
     * for each fight won and 1 for each point.
     */
    public final void updateScoreForTeams(List<Fight> fights) {
        updateTeamsWithFights(fights);
        ResetScoreToZero();

        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            double teamScore = (double) 0;
            for (int j = 0; j < fights.size(); j++) {
                Fight oneFight = fights.get(j);
                if ((oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                    teamScore += obtainPointsForWonFights(oneFight, team);
                    teamScore += obtainPointsForWonDuels(oneFight, team);
                    teamScore += obtainPointsForHits(oneFight, team);
                    if (oneFight.competition.getChoosedScore().equals("European")) { //In an European champiosnhip, draw fightManager are used to 
                        teamScore += obtainPointsForDrawFights(oneFight, team);
                        teamScore += obtainPointsForDrawDuels(oneFight, team);
                    }
                }
            }
            teamScore += obtainPointsForGoldenPoint(team);
            //Storing the score.
            teamsScore.set(i, teamScore);
        }
    }

    private int obtainTeamWithMaxScore(List<Double> tmp_teamsScore) {
        Double max = (double) -1;
        int index = -1;
        for (int i = 0; i < tmp_teamsScore.size(); i++) {
            if (tmp_teamsScore.get(i) > max) {
                max = tmp_teamsScore.get(i);
                index = i;
            }
        }
        return index;
    }

    private void showScore() {
        for (int i = 0; i < teams.size(); i++) {
            System.out.println(teams.get(i).returnName() + ": " + teamsScore.get(i));
        }
    }

    public double obainScoreOfTeam(int team) {
        try {
            return teamsScore.get(team);
        } catch (IndexOutOfBoundsException iob) {
            KendoTournamentGenerator.getInstance().showErrorInformation(iob);
            return 0;
        }
    }

    private List<Team> getTeamsOrderedByScore() {
        List<Team> sortedTeams = new ArrayList<Team>();
        List<Team> tmp_teams = new ArrayList<Team>();
        List<Double> tmp_teamsScore = new ArrayList<Double>();
        List<Double> teamsScoreOrdered;

        tmp_teams.addAll(teams);
        tmp_teamsScore.addAll(teamsScore);
        teamsScoreOrdered = new ArrayList<Double>();

        while (tmp_teams.size() > 0) {
            int index = obtainTeamWithMaxScore(tmp_teamsScore);
            if (index >= 0) {
                sortedTeams.add(tmp_teams.get(index));
                teamsScoreOrdered.add(tmp_teamsScore.get(index));
                tmp_teams.remove(index);
                tmp_teamsScore.remove(index);
            } else {
                break;
            }
        }
        return sortedTeams;
    }

    public List<TeamRanking> getRanking(List<Fight> fights) {
        List<TeamRanking> teamsOrdered = new ArrayList<TeamRanking>();

        updateScoreForTeams(fights);
        List<Team> teamsOrd = getTeamsOrderedByScore();
        for (int i = 0; i < teamsOrd.size(); i++) {
            teamsOrdered.add(new TeamRanking(teamsOrd.get(i).returnName(), teamsOrd.get(i).competition.name,
                    obtainWonFights(fights, teamsOrd.get(i), -1), obtainDrawFights(fights, teamsOrd.get(i), -1),
                    obtainWonDuels(fights, teamsOrd.get(i), -1), obtainDrawDuels(fights, teamsOrd.get(i), -1),
                    obtainHits(fights, teamsOrd.get(i), -1)));
        }
        return teamsOrdered;
    }

    /**
     * Init the score.
     */
    private void ResetScoreToZero() {
        teamsScore = new ArrayList<Double>();
        for (int i = 0; i < teams.size(); i++) {
            teamsScore.add((double) 0);
        }
    }
}
