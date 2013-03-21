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

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.TeamPool;
import com.softwaremagico.ktg.database.UndrawPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

public class TournamentGroup extends Group implements Serializable {

    public static final int MAX_TEAMS_PER_GROUP = 8;
    private static final long serialVersionUID = -8425766161404716635L;
    transient Tournament tournament;
    transient DesignGroupWindow dgw;
    private List<Team> teams = new ArrayList<>();
    private boolean selected = false;
    private Integer numberMaxOfWinners = 1;
    transient private Translator trans = null;
    private Integer level;
    transient public boolean listenerAdded = false;
    public int arena = 0;
    transient private boolean color = true;
    transient private java.awt.event.MouseAdapter ma;

    TournamentGroup(Integer numberMaxOfWinners, Tournament championship, Integer level, Integer arena) {
        this.tournament = championship;
        //this.numberMaxOfTeams = numberMaxOfTeams;
        this.level = level;
        this.arena = arena;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.LINE_START;
        this.numberMaxOfWinners = numberMaxOfWinners;
        //numberMaxOfWinnersLeague = tmp_numberMaxOfWinners;

        updateSize();

        setBackground(new Color(230, 230, 230));
        //setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));

        setLanguage();
        updateText();
        removeAll();
        label.setHorizontalTextPosition(JLabel.LEFT);
        label.setForeground(Color.BLACK);
        add(label, c);
    }
 
    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        if (level == 0) {
            this.setToolTipText(trans.returnTag("ToolTipEditable"));
        } else {
            this.setToolTipText(trans.returnTag("ToolTipNotEditable"));
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

    protected boolean load(Tournament tournament) {
        this.tournament = tournament;
        setLanguage();
        List<Team> updatedTeams = new ArrayList<>();
        //variable participants of each teams are "transient". The program must obtain it from the database.
        try {
            for (int i = 0; i < teams.size(); i++) {
                Team t = TeamPool.getInstance().get(tournament, teams.get(i).getName());
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

        List<Team> teamRanking = Ranking.getTeamsRanking(FightPool.getInstance().get(tournament));
        if (teamRanking.isEmpty()) {
            text += "<b>" + getDefaultLabel() + "</b>";
        } else {
            for (int i = 0; i < teamRanking.size(); i++) {
                Color c = obtainWinnerColor(i, true);

                if (color) {
                    //text += "<b><font size=\"+1\" color=\"#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue()) + "\">";
                    text += "<b><font color=\"#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue()) + "\">";
                }
                text += teamRanking.get(i).getShortName();
                if (color) {
                    text += "</b></font>";
                }
                if (i < teamRanking.size() - 1) {
                    text += "<br>";
                }
            }
        }
        text += "</html>";
        return text;
    }

    public String getDefaultLabel() {
        //Select label
        String s;
        if (level < TournamentGroupPool.getManager(tournament).getLevels().size() - 2) {
            s = trans.returnTag("Round") + " " + (TournamentGroupPool.getManager(tournament).getLevels().size() - level);
        } else if (level == TournamentGroupPool.getManager(tournament).getLevels().size() - 2) {
            s = trans.returnTag("SemiFinalLabel");
        } else {
            s = trans.returnTag("FinalLabel");
        }
        return s;
    }

    public Color obtainWinnerColor(int winner, boolean check) {
        int red, green, blue;
        if ((!check) || (winner < numberMaxOfWinners && winner >= 0 && areFightsOver(FightPool.getInstance().get(tournament)))) {
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

    public void setSelected(TournamentGroupManager ds) {
        if (level == 0) {
            ds.unselectDesignedGroups();
            selected = true;
            setBackground(new Color(200, 200, 200));
            //setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
            setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        }
    }

    public void setUnselected() {
        selected = false;
        setBackground(new Color(230, 230, 230));
        //setBorder(javax.swing.BorderFactory.createEtchedBorder());
        if (listenerAdded) {
            setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        } else {
            setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    public boolean isSelected() {
        return selected;
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

    public void activateColor(boolean value) {
        color = value;
    }

    public Tournament getChampionshipOfGroup() {
        return tournament;
    }

    public void openDesignGroupWindow(LeagueDesigner jf) {
        dgw = new DesignGroupWindow(this);
        addDesignGroupListeners(jf);
        dgw.setVisible(true);
    }

    public TournamentGroup convertToMode() {
        int maxWinners = 1;
        switch (tournament.getMode()) {
            case LEAGUE_TREE:
                maxWinners = 1;
                break;
            case CHAMPIONSHIP:
                maxWinners = 2;
                break;
        }
        return new TournamentGroup(maxWinners, tournament, level, arena);
    }

    public List<Team> getTeams() {
        return teams;
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
        for (int i = value; i < teams.size(); i++) {
            teams.remove(i);
        }
        update();
    }

    public int numberOfTeams() {
        return teams.size();
    }

    public void deleteTeams() {
        teams = new ArrayList<>();
        label.setText(returnText());
        updateSize();
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

    /**
     * ********************************************
     *
     * FIGHTS MANIPULATION
     *
     *********************************************
     */
    /**
     * Generate new fightManager of this group
     *
     * @return
     */
    public List<Fight> generateGroupFights(int level) {
        KendoLog.entering(this.getClass().getName(), "generateGroupFights");
        List<Fight> fights = generateGroupFights(level, arena);
        KendoLog.exiting(this.getClass().getName(), "generateGroupFights");
        return fights;
    }

    public List<Fight> generateGroupFights(int level, int fightArea) {
        KendoLog.entering(this.getClass().getName(), "generateGroupFights");
        List<Fight> fights = new ArrayList<>();
        int count = 0;
        Fight f;

        for (int j = 0; j < teams.size() - 1; j++) {
            if (count % 2 == 0) {
                f = new Fight(tournament, teams.get(j), teams.get(j + 1), fightArea, level);
            } else {
                f = new Fight(tournament, teams.get(j + 1), teams.get(j), fightArea, level);
            }
            f.setMaxWinners(numberMaxOfWinners);
            if (!existFight(fights, f)) {
                fights.add(f);
                count++;
            }
        }
        //Last versus first.
        if (teams.size() > 2) {
            if (count % 2 == 0) {
                f = new Fight(tournament, teams.get(teams.size() - 1), teams.get(0), fightArea, level);
            } else {
                f = new Fight(tournament, teams.get(0), teams.get(teams.size() - 1), fightArea, level);
            }
            f.setMaxWinners(numberMaxOfWinners);
            if (!existFight(fights, f)) {
                fights.add(f);
                count++;
            }
        }
        KendoLog.exiting(this.getClass().getName(), "generateGroupFights");
        return fights;
    }

    public boolean isFightOfGroup(Fight f) {
        if (isTeamOfGroup(f.getTeam1()) && isTeamOfGroup(f.getTeam2()) && level == f.getLevel()) {
            return true;
        }
        return false;
    }

    public int getShiaijo(List<Fight> fights) {
        for (int i = 0; i < fights.size(); i++) {
            if (isFightOfGroup(fights.get(i))) {
                return fights.get(i).getAsignedFightArea();
            }
        }
        return 0;
    }

    private boolean existFight(List<Fight> fights, Fight f) {
        return fights.contains(f);
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

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     *
     */
    public List<String> exportToCsv() {
        List<String> csv = new ArrayList<>();
        List<Fight> fights = getFights();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                csv.addAll(fights.get(i).exportToCsv(i, TournamentGroupPool.getManager(tournament).getIndexOfGroup(this), level));
            }
        }
        List<Undraw> undraws = UndrawPool.getInstance().getSorted(tournament);
        for (int i = 0; i < undraws.size(); i++) {
            csv.addAll(undraws.get(i).exportToCsv());
        }
        return csv;
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * Add Listeners
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
            TournamentGroupPool.getManager(tournament).updateArenas(1);
            blackboard.updateBlackBoard();
            blackboard.fillTeams();
            blackboard.repaint();
        }
    }
}
