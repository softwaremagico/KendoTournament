package com.softwaremagico.ktg;

import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentGroupPool;
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
/**
 *
 * @author LOCAL\jhortelano
 */
public class Undraw {

    private static final String UNDRAW_TAG = "UNDRAW";
    private Tournament tournament;
    private TournamentGroup group;
    private Team winnerTeam;
    private Integer player;

    public Undraw(Tournament tournament, TournamentGroup group, Team winnerTeam, Integer player) {
        this.winnerTeam = winnerTeam;
        this.tournament = tournament;
        this.player = player;
        this.group = group;
    }

    public TournamentGroup getGroup() {
        return group;
    }

    public Integer getIndexOfGroup() {
        return TournamentGroupPool.getManager(getTournament()).getIndexOfGroup(getGroup());
    }

    public void setGroup(TournamentGroup group) {
        this.group = group;
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

    public Team getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeam(Team winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    public static String getCsvTag() {
        return UNDRAW_TAG;
    }

    public static List<String> exportToCsv(Team team) {
        List<String> csv = new ArrayList<>();
        csv.add(UNDRAW_TAG + ";" + team.getName() + ";" + 0);
        return csv;
    }
}
