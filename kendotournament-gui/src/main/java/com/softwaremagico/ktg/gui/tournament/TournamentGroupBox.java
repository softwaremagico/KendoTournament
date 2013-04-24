package com.softwaremagico.ktg.gui.tournament;
/*
 * #%L
 * Kendo Tournament Generator GUI
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
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
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.UndrawPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerPool;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

public class TournamentGroupBox extends Group {

    private TournamentGroup tournamentGroup;
    private ITournamentManager tournamentManager;
    private DesignGroupWindow dgw;
    private boolean selected = false;
    private Translator trans = null;
    private boolean color = true;
    private java.awt.event.MouseAdapter ma;
    private boolean listenerAdded = false;

    public TournamentGroupBox(TournamentGroup tournamentGroup) {
        this.tournamentGroup = tournamentGroup;
        this.tournamentManager = TournamentManagerPool.getManager(tournamentGroup.getTournament());
        setLayout(new GridBagLayout());
        setLanguage();
        updateText();
        updateSize();
        setBackground(new Color(230, 230, 230));
        //setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
        removeAll();
        label.setHorizontalTextPosition(JLabel.LEFT);
        label.setForeground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.LINE_START;
        add(label, c);
    }

    public void update() {
        updateText();
        updateSize();
    }

    public TournamentGroup getTournamentGroup() {
        return tournamentGroup;
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

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        if (tournamentGroup.getLevel() == 0) {
            this.setToolTipText(trans.getTranslatedText("ToolTipEditable"));
        } else {
            this.setToolTipText(trans.getTranslatedText("ToolTipNotEditable"));
        }
    }

    final void updateSize() {
        //xSize = 110 * Math.max(2, teams.size());
        xSize = 200;
        ySize = 50 + 12 * Math.max(2, tournamentGroup.getTeams().size());
        setPreferredSize(new Dimension(xSize, ySize));
        setMaximumSize(new Dimension(xSize, ySize));
        setMinimumSize(new Dimension(0, 0));
    }

    private String getText(boolean withScore) {
        String text = "<html>";

        List<Team> teamRanking;
        if (withScore) {
            teamRanking = Ranking.getTeamsRanking(FightPool.getInstance().get(tournamentGroup.getTournament()));
        } else {
            teamRanking = tournamentGroup.getTeams();
        }
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

    public final void updateText() {
        label.setText(getText(false));
    }

    public final void updateTextOrderByScore() {
        label.setText(getText(true));
    }

    public String getDefaultLabel() {
        //Select label
        String s;
        if (tournamentGroup.getLevel() < tournamentManager.getNumberOfLevels() - 2) {
            s = trans.getTranslatedText("Round") + " " + (tournamentManager.getNumberOfLevels() - tournamentGroup.getLevel());
        } else if (tournamentGroup.getLevel() == tournamentManager.getNumberOfLevels() - 2) {
            s = trans.getTranslatedText("SemiFinalLabel");
        } else {
            s = trans.getTranslatedText("FinalLabel");
        }
        return s;
    }

    public Color obtainWinnerColor(int winner, boolean check) {
        int red, green, blue;
        if ((!check) || (winner < tournamentGroup.getMaxNumberOfWinners() && winner >= 0 && tournamentGroup.areFightsOver(FightPool.getInstance().get(tournamentGroup.getTournament())))) {
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

    public void setSelected() {
        if (tournamentGroup.getLevel() == 0) {
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

    public void activateColor(boolean value) {
        color = value;
    }

    public void openDesignGroupWindow(LeagueDesigner jf) {
        dgw = new DesignGroupWindow(tournamentGroup);
        addDesignGroupListeners(jf);
        dgw.setVisible(true);
    }

    public void removeTeams() {
        tournamentGroup.removeTeams();
        label.setText(getText(false));
        updateSize();
    }

    public Integer getMaxNumberOfWinners() {
        return tournamentGroup.getMaxNumberOfWinners();
    }

    /**
     *
     */
    public List<String> exportToCsv() {
        List<String> csv = new ArrayList<>();
        List<Fight> fights = tournamentGroup.getFights();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                //  csv.addAll(fights.get(i).exportToCsv(i, TournamentGroupPool.getManager(tournamentGroup.getTournament()).getIndexOfGroup(tournamentGroup), tournamentGroup.getLevel()));
            }
        }
        List<Undraw> undraws = UndrawPool.getInstance().getSorted(tournamentGroup.getTournament());
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

        private LeagueDesigner leagueDesigner;

        closeWindows(LeagueDesigner jf) {
            leagueDesigner = jf;
        }

        @Override
        public void windowClosed(WindowEvent evt) {
            update();
            //  TournamentGroupPool.getManager(tournamentGroup.getTournament()).updateArenas(1);
            leagueDesigner.updateInfo();
            leagueDesigner.repaint();
        }
    }
}
