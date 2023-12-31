package com.softwaremagico.ktg.core;

import java.util.Objects;

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

    private Tournament tournament;
    private Integer groupIndex;
    private Team winnerTeam;
    private Integer player;
    private Integer level;
    private Integer points;

    public Undraw(Tournament tournament, Integer groupIndex, Team winnerTeam, Integer player, Integer level) {
        this.winnerTeam = winnerTeam;
        this.tournament = tournament;
        this.player = player;
        this.groupIndex = groupIndex;
        this.level = level;
        setPoints(1);
    }

    public Integer getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(Integer group) {
        this.groupIndex = group;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.tournament);
        hash = 59 * hash + Objects.hashCode(this.groupIndex);
        hash = 59 * hash + Objects.hashCode(this.winnerTeam);
        hash = 59 * hash + Objects.hashCode(this.player);
        hash = 59 * hash + Objects.hashCode(this.level);
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
        final Undraw other = (Undraw) obj;
        if (!Objects.equals(this.tournament, other.tournament)) {
            return false;
        }
        if (!Objects.equals(this.groupIndex, other.groupIndex)) {
            return false;
        }
        if (!Objects.equals(this.winnerTeam, other.winnerTeam)) {
            return false;
        }
        if (!Objects.equals(this.player, other.player)) {
            return false;
        }
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return "Undraw winned by " + winnerTeam.getName() + ", level: " + getLevel() + ", group:" + getGroupIndex() + ", points:" + points;
    }
}
