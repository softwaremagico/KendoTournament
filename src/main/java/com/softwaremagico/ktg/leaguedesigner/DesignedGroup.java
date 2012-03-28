/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 21-jul-2009.
 */
package com.softwaremagico.ktg.leaguedesigner;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author jorge
 */
public class DesignedGroup extends Group implements Serializable {

    transient Tournament championship;
    transient DesignGroupWindow dgw;
    public List<Team> teams = new ArrayList<Team>();
    private boolean selected = false;
    Integer numberMaxOfTeams;
    private Integer numberMaxOfWinners = 1;
    Integer numberMaxOfWinnersLeague = 1;
    transient private Translator trans = null;
    private Integer level;
    transient public boolean listenerAdded = false;
    private List<Double> teamsScore;
    private List<Double> teamsScoreOrdered;
    public int arena = 0;
    transient private boolean color = true;
    transient private java.awt.event.MouseAdapter ma;
    private static final Float SCORE_WON_FIGHTS = new Float(1000000);
    private static final Float SCORE_WON_DUELS = new Float(10000);
    private static final Float SCORE_HITS = new Float(1);
    private static final Float SCORE_DRAW_FIGHTS = new Float(0.001);
    private static final Float SCORE_DRAW_DUELS = new Float(0.000001);
    private static final Float SCORE_GOLDEN_POINT = new Float(0.000001);

    DesignedGroup(Integer tmp_numberMaxOfTeams, Integer tmp_numberMaxOfWinners, Tournament tmp_championship, Integer tmp_level, Integer tmp_arena) {
        championship = tmp_championship;
        numberMaxOfTeams = tmp_numberMaxOfTeams;
        level = tmp_level;
        arena = tmp_arena;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.LINE_START;
        numberMaxOfWinners = tmp_numberMaxOfWinners;
        numberMaxOfWinnersLeague = tmp_numberMaxOfWinners;

        updateSize();
        setDefaultScore();

        setBackground(new Color(200, 200, 200));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        setLanguage(KendoTournamentGenerator.getInstance().language);
        updateText();
        removeAll();
        label.setHorizontalTextPosition(JLabel.LEFT);
        add(label, c);
    }

    private void setLanguage(String language) {
        trans = new Translator("gui.xml");
        if (level == 0) {
            this.setToolTipText(trans.returnTag("ToolTipEditable", language));
        } else {
            this.setToolTipText(trans.returnTag("ToolTipNotEditable", language));
        }
    }

    final void updateSize() {
        //xSize = 110 * Math.max(2, teams.size());
        xSize = 200;
        ySize = 50 + 12 * Math.max(2, teams.size());
        setPreferredSize(new Dimension(xSize, ySize));
        setMaximumSize(new Dimension(xSize, ySize));
        setMinimumSize(new Dimension(0, 0));
    }

    public final void updateText() {
        label.setText(returnText());
    }

    public void update() {
        updateText();
        updateSize();
    }

    void onlyShow() {
        this.setToolTipText("");
    }

    void enhance(boolean yes) {
        if (yes) {
            label.setFont(new Font("Tahoma", Font.BOLD, 12));
        } else {
            label.setFont(new Font("Tahoma", Font.PLAIN, 12));
        }
    }

    public boolean load(Tournament c) {
        championship = c;
        setLanguage(KendoTournamentGenerator.getInstance().language);
        List<Team> updatedTeams = new ArrayList<Team>();
        //variable participants of each teams are "transient". The program must obtain it from the database.
        try {
            for (int i = 0; i < teams.size(); i++) {
                Team t = KendoTournamentGenerator.getInstance().database.getTeamByName(teams.get(i).returnName(), c.name, false);
                if (t != null) {
                    updatedTeams.add(t);
                } else {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException iob) {
            return false;
        }
        teams = updatedTeams;
        return true;
    }

    private String returnText() {
        String text = "<html>";

        updateScoreForTeams(KendoTournamentGenerator.getInstance().fights.getFights());
        List<Team> teamsO = getTeamsOrderedByScore();
        if (teamsO.isEmpty()) {
            text = "<html><b>" + trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (level + 1) + "</html>";
        } else {
            for (int i = 0; i < teamsO.size(); i++) {
                int order = getOrderOfTeam(teamsO.get(i));
                Color c = obtainWinnerColor(order, true);

                if (color) {
                    //text += "<b><font size=\"+1\" color=\"#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue()) + "\">";
                    text += "<b><font color=\"#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue()) + "\">";
                }
                text += teamsO.get(i).returnShortName();
                if (color) {
                    text += "</b></font>";
                }
                if (i < teamsO.size() - 1) {
                    text += "<br>";
                }
            }
        }
        text += "</html>";
        return text;
    }

    public Color obtainWinnerColor(int winner, boolean check) {
        int red, green, blue;
        if ((!check) || (winner < numberMaxOfWinners && winner >= 0 && areFightsOver(KendoTournamentGenerator.getInstance().fights.getFights()))) {
            if (winner == 0) {
                red = 220;
                green = 20;
                blue = 20;
            } else {
                red = 0 + (winner) * 37;
                red = red % 221;
                green = 0 + (winner) * 144;
                green = green % 171;
                blue = 0 + (winner) * 239 - winner * 150;
                blue = blue % 245;
            }
            return new Color(red, green, blue);
        }
        return new Color(0, 0, 0);
    }

    public void setSelected(DesignedGroups ds) {
        if (level == 0) {
            ds.unselectDesignedGroups();
            selected = true;
            setBackground(new Color(230, 230, 230));
        }
    }

    public void setUnselected() {
        selected = false;
        setBackground(new Color(200, 200, 200));
    }

    public boolean isSelected() {
        return selected;
    }

    public void updateMaxNumberOfWinners(int value) {
        if (value <= numberMaxOfTeams) {
            numberMaxOfWinners = value;
        } else {
            numberMaxOfWinners = numberMaxOfTeams;
        }
        if (numberMaxOfWinners < 1) {
            numberMaxOfWinners = 1;
        }
    }

    public int returnMaxNumberOfWinners() {
        if (level > 0) {
            return 1;
        }
        return numberMaxOfWinners;
    }

    public int getLevel() {
        return level;
    }

    public void activateColor(boolean value) {
        color = value;
    }

    public Tournament getChampionshipOfGroup() {
        return championship;
    }

    public void openDesignGroupWindow(LeagueDesigner jf) {
        dgw = new DesignGroupWindow(this);
        addDesignGroupListeners(jf);
        dgw.setVisible(true);
    }

    /**
     * ********************************************
     *
     * TEAMS MANIPULATION
     *
     *********************************************
     */
    /**
     * Max limit of teams to store.
     *
     * @param value
     */
    public void setNumberMaxOfTeams(int value) {
        numberMaxOfTeams = value;
        for (int i = numberMaxOfTeams; i < teams.size(); i++) {
            teams.remove(i);
        }
        update();
    }

    public int numberOfTeams() {
        return teams.size();
    }

    public void cleanTeams() {
        teams = new ArrayList<Team>();
        label.setText(returnText());
        updateSize();
    }

    public void addTeam(Team team) {
        //Can not be repeated.
        if (!teams.contains(team)) {
            teams.add(team);
            //Delete one, because con not be more than maxteams.
            //if (teams.size() > numberMaxOfTeams) {
            if (teams.size() > 8) {
                teams.remove(0);
            }
        }
    }

    public void addTeams(List<Team> tmp_teams) {
        teams.addAll(tmp_teams);
    }

    private boolean isTeamOfGroup(String team) {
        for (int i = 0; i < teams.size(); i++) {
            try {
                if (teams.get(i).returnName().equals(team)) {
                    return true;
                }
            } catch (NullPointerException npe) {
            }
        }
        return false;
    }

    public List<Team> getWinners() {
        List<Team> result = new ArrayList<Team>();
        List<Team> orderedTeams = getTeamsOrderedByScore();
        for (int i = 0; i < numberMaxOfWinners; i++) {
            try {
                result.add(orderedTeams.get(i));
            } catch (IndexOutOfBoundsException iob) {
            }
        }
        return result;
    }

    /**
     * ********************************************
     *
     * FIGHTS MANIPULATION
     *
     *********************************************
     */
    /**
     * Generate new fights of this group
     *
     * @return
     */
    public List<Fight> generateGroupFights(int level) {
        return generateGroupFights(level, arena);
    }

    public List<Fight> generateGroupFights(int level, int fightArea) {
        List<Fight> fights = new ArrayList<Fight>();
        int count = 0;
        Fight f;

        for (int j = 0; j < teams.size() - 1; j++) {
            if (count % 2 == 0) {
                f = new Fight(teams.get(j), teams.get(j + 1), championship, fightArea, level);
            } else {
                f = new Fight(teams.get(j + 1), teams.get(j), championship, fightArea, level);
            }
            f.changeMaxWinners(numberMaxOfWinners);
            if (!existFight(fights, f)) {
                fights.add(f);
                count++;
            }
        }
        //Last versus first.
        if (teams.size() > 2) {
            if (count % 2 == 0) {
                f = new Fight(teams.get(teams.size() - 1), teams.get(0), championship, fightArea, level);
            } else {
                f = new Fight(teams.get(0), teams.get(teams.size() - 1), championship, fightArea, level);
            }
            f.changeMaxWinners(numberMaxOfWinners);
            if (!existFight(fights, f)) {
                fights.add(f);
                count++;
            }
        }
        return fights;
    }

    public boolean isFightOfGroup(Fight f) {
        if (isTeamOfGroup(f.team1.returnName()) && isTeamOfGroup(f.team2.returnName()) && level == f.level) {
            return true;
        }
        return false;
    }

    public int getShiaijo(List<Fight> fights) {
        for (int i = 0; i < fights.size(); i++) {
            if (isFightOfGroup(fights.get(i))) {
                return fights.get(i).asignedFightArea;
            }
        }
        return 0;
    }

    private boolean existFight(List<Fight> fights, Fight f) {
        for (int i = 0; i < fights.size(); i++) {
            if ((fights.get(i).team1.equals(f.team1)) && (fights.get(i).team2.equals(f.team2))) {
                return true;
            }
        }
        return false;
    }

    public List<Fight> getFightsOfGroup(List<Fight> fights) {
        List<Fight> fightsG = new ArrayList<Fight>();
        for (int i = 0; i < fights.size(); i++) {
            for (int j = 0; j < teams.size(); j++) {
                try {
                    if ((fights.get(i).team1.returnName().equals(teams.get(j).returnName())
                            || fights.get(i).team2.returnName().equals(teams.get(j).returnName()))
                            && fights.get(i).level == level) {
                        fightsG.add(fights.get(i));
                        break;
                    }
                } catch (NullPointerException npe) {
                   KendoTournamentGenerator.getInstance().showErrorInformation(npe);
                }
            }
        }
        return fightsG;
    }

    public boolean areFightsOver(List<Fight> allFights) {
        List<Fight> fights = getFightsOfGroup(allFights);

        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (fights.get(i).isOver() == 2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean areFightsOver() {
        List<Fight> fights = getFightsOfGroup(KendoTournamentGenerator.getInstance().fights.getFights());

        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (fights.get(i).isOver() == 2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * If the fights are over or fights are not needed.
     */
    public boolean areFightsOverOrNull(List<Fight> allFights) {
        List<Fight> fights = getFightsOfGroup(allFights);

        if (fights.size() > 0) {
            for (int i = 0; i < fights.size(); i++) {
                if (fights.get(i).isOver() == 2) {
                    return false;
                }
            }
            return true;
        } else if (teams.size() > 1) { //If there are only one team, no fights are needed.
            return false;
        }
        return true;
    }

    /**
     * ********************************************
     *
     * SCORE
     *
     *********************************************
     */
    /**
     * Init the score.
     */
    private void setDefaultScore() {
        teamsScore = new ArrayList<Double>();
        for (int i = 0; i < teams.size(); i++) {
            teamsScore.add((double) 0);
        }
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
                if (fight.duels.get(k).winner() < 0) {
                    score += SCORE_WON_DUELS * championship.getScoreForWin();
                }
                if (fight.duels.get(k).winner() == 0) {
                    score += SCORE_WON_DUELS * championship.getScoreForDraw();
                }
            }
        }
        //Team2
        if (fight.team2.returnName().equals(team.returnName())) {
            for (int k = 0; k < fight.duels.size(); k++) {
                if (fight.duels.get(k).winner() > 0) {
                    score += SCORE_WON_DUELS * championship.getScoreForWin();
                }
                if (fight.duels.get(k).winner() == 0) {
                    score += SCORE_WON_DUELS * championship.getScoreForDraw();
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
                return SCORE_WON_FIGHTS * championship.getScoreForWin();
            } else if (fight.isDrawFight() && (fight.team1.returnName().equals(team.returnName())
                    || fight.team2.returnName().equals(team.returnName()))) {
                return SCORE_WON_FIGHTS * championship.getScoreForDraw();
            }
        } catch (NullPointerException npe) {
        }
        return new Float(0);
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
        double multiplier = (double) KendoTournamentGenerator.getInstance().database.getValueWinnerInUndrawInGroup(championship.name, KendoTournamentGenerator.getInstance().designedGroups.returnIndexOfGroup(this), team.returnName());
        score += (double) SCORE_GOLDEN_POINT * multiplier;
        return score;
    }

    /**
     * Creates a score for a team. The score is 1000000 for each duel won, 1000
     * for each fight won and 1 for each point.
     */
    void updateScoreForTeams(List<Fight> fights) {
        setDefaultScore();

        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            double teamScore = (double) 0;

            for (int j = 0; j < fights.size(); j++) {
                Fight oneFight = fights.get(j);
                if (oneFight.level == level) { //Only count the points of this designed group!!
                    if (isFightOfGroup(oneFight) && (oneFight.team1.returnName().equals(team.returnName()) || oneFight.team2.returnName().equals(team.returnName()))) {
                        teamScore += obtainPointsForWonFights(oneFight, team);
                        teamScore += obtainPointsForWonDuels(oneFight, team);
                        teamScore += obtainPointsForHits(oneFight, team);
                        if (oneFight.competition.getChoosedScore().equals("European")) { //In an European champiosnhip, draw fights are used to 
                            teamScore += obtainPointsForDrawFights(oneFight, team);
                            teamScore += obtainPointsForDrawDuels(oneFight, team);
                        }
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

    private double obainScoreOfTeam(int team) {
        try {
            return teamsScore.get(team);
        } catch (IndexOutOfBoundsException iob) {
            return 0;
        }
    }

    private List<Team> getTeamsOrderedByScore() {
        List<Team> sortedTeams = new ArrayList<Team>();
        List<Team> tmp_teams = new ArrayList<Team>();
        List<Double> tmp_teamsScore = new ArrayList<Double>();

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

    public Team getTeamInOrderOfScore(int order, List<Fight> fights, boolean resolvDraws) {
        updateScoreForTeams(fights);
        List<Team> sortedTeams = getTeamsOrderedByScore();
        try {
            //if (resolvDraws && teamsScoreOrdered.get(order).equals(teamsScoreOrdered.get(order + 1)) && order >= numberMaxOfWinners - 1) {
            if (resolvDraws && teamsScoreOrdered.get(order).equals(teamsScoreOrdered.get(order + 1))) {
                return resolvDrawTeams(order, sortedTeams);
            }
        } catch (IndexOutOfBoundsException iob) {
        }
        try {
            return sortedTeams.get(order);
        } catch (IndexOutOfBoundsException iob) {
            //If there are only one team in a group, there are no fights. In trees where are even groups.
            try {
                return teams.get(0);
            } catch (IndexOutOfBoundsException iob2) {
                return null;
            }
        }
    }

    private int getOrderOfTeam(Team team) {
        updateScoreForTeams(getFightsOfGroup(KendoTournamentGenerator.getInstance().fights.getFights()));
        List<Team> sortedTeams = new ArrayList<Team>();
        try {
            sortedTeams = getTeamsOrderedByScore();
        } catch (NullPointerException npe) {
            //npe.printStackTrace();
        }

        for (int i = 0; i < sortedTeams.size(); i++) {
            if (sortedTeams.get(i).returnName().equals(team.returnName())) {
                return i;
            }
        }
        return -1;
    }

    private Team resolvDrawTeams(int index, List<Team> sortedTeams) {
        int select = -1;
        List<Team> drawTeams = obtainDrawTeams(index, sortedTeams);
        do {
            select = resolvDrawTeams(drawTeams);
        } while (select < 0);
        return drawTeams.get(select);
    }

    /**
     * Obtain a list of teams with the same score that the team selected with an
     * index.
     *
     * @param index The index of the team to compare.
     * @return
     */
    private List<Team> obtainDrawTeams(int index, List<Team> sortedTeams) {
        List<Team> result = new ArrayList<Team>();
        double drawScore = (double) 0;
        try {
            drawScore = teamsScoreOrdered.get(index);
        } catch (Exception e) {
            KendoTournamentGenerator.getInstance().showErrorInformation(e);
            return null;
        }
        for (int i = 0; i < teamsScoreOrdered.size(); i++) {
            if (teamsScoreOrdered.get(i).equals(drawScore)) {
                result.add(sortedTeams.get(i));
            }
        }
        return result;
    }

    /**
     * If there are more than one team with the max punctuation, ask for a
     * winner to the user.
     *
     * @param winnersOfgroup
     * @return position in the list of the choosen one.
     */
    private int resolvDrawTeams(List<Team> drawTeams) {
        JFrame frame = null;
        String team;

        //If it is draw because there is only one team. Then it wins.
        if (drawTeams.size() == 1) {
            return 0;
        }

        //If the user has already define a winner. Use it.
        if ((team = isDrawStored(drawTeams)) != null) {
            for (int i = 0; i < drawTeams.size(); i++) {
                if (drawTeams.get(i).returnName().equals(team)) {
                    return i;
                }
            }
        }

        //Ask the user who is the real winner.
        List<String> optionsList = new ArrayList<String>();
        for (int i = 0; i < drawTeams.size(); i++) {
            optionsList.add(drawTeams.get(i).returnName());
//            KendoTournamentGenerator.getInstance().database.storeUndraw(championship.name, drawTeams.get(i).returnName(), 0, level);
            //KendoTournamentGenerator.getInstance().database.storeUndraw(championship.name, drawTeams.get(i).returnName(), 0, KendoTournamentGenerator.getInstance().designedGroups.returnIndexOfGroup(this));

        }


        Object[] options = optionsList.toArray();

        int n = JOptionPane.showOptionDialog(frame,
                trans.returnTag("DrawText", KendoTournamentGenerator.getInstance().language),
                trans.returnTag("DrawTitle", KendoTournamentGenerator.getInstance().language),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (n >= 0) {
            KendoTournamentGenerator.getInstance().database.storeUndraw(championship.name, drawTeams.get(n).returnName(), 0, KendoTournamentGenerator.getInstance().designedGroups.returnIndexOfGroup(this));
            //KendoTournamentGenerator.getInstance().database.defineWinnerInUndraw(championship.name, drawTeams.get(n).returnName(), level, drawTeams);
        }
        return n;
    }

    private String isDrawStored(List<Team> drawTeams) {
        return KendoTournamentGenerator.getInstance().database.getWinnerInUndraws(championship.name, level, drawTeams);
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * Add the same action listener to all langugaes of the menu.
     *
     * @param al
     */
    public void addMouseClickListener(java.awt.event.MouseAdapter e) {
        ma = e;
        addMouseListener(ma);
        listenerAdded = true;
    }

    public void removeMouseClickListener() {
        removeMouseListener(ma);
        listenerAdded = false;
    }

    /**
     * *******************************************************************
     *
     * DESIGN GROUP WINDOW LISTENERS
     *
     ********************************************************************
     */
    /**
     * Add all listeners to GUI.
     */
    private void addDesignGroupListeners(LeagueDesigner jf) {
        dgw.addWindowCloseListener(new closeWindows(jf));
    }

    class closeWindows extends WindowAdapter {

        LeagueDesigner blackboard;

        closeWindows(LeagueDesigner jf) {
            blackboard = jf;
        }

        @Override
        public void windowClosed(WindowEvent evt) {
            update();
            KendoTournamentGenerator.getInstance().designedGroups.updateArenaInnerLevels();
            blackboard.updateBlackBoard();
            blackboard.fillTeams();
            blackboard.repaint();
        }
    }
}
