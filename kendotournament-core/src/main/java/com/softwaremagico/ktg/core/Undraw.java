package com.softwaremagico.ktg.core;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
public class Undraw implements Comparable<Undraw> {

    private static final String UNDRAW_TAG = "UNDRAW";
    private Tournament tournament;
    private Integer group;
    private Team winnerTeam;
    private Integer player;
    private Integer level;
    private Integer points;

    public Undraw(Tournament tournament, Integer group, Team winnerTeam, Integer player, Integer level) {
        this.winnerTeam = winnerTeam;
        this.tournament = tournament;
        this.player = player;
        this.group = group;
        this.level = level;
        points = 1;
    }

    public Integer getGroupIndex() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam() {
        return winnerTeam;
    }

    public void setTeam(Team winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    public static String getCsvTag() {
        return UNDRAW_TAG;
    }

    public List<String> exportToCsv() {
        List<String> csv = new ArrayList<>();
        csv.add(UNDRAW_TAG + ";" + winnerTeam.getName() + ";" + 0 + ";" + level);
        return csv;
    }

    public Integer getLevel() {
        return level;
    }

    @Override
    public int compareTo(Undraw o) {
        Integer levelCompare = getLevel().compareTo(o.getLevel());
        if (levelCompare != 0) {
            return levelCompare;
        }
        return getGroupIndex().compareTo(o.getGroupIndex());
    }
}
