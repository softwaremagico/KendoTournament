package com.softwaremagico.ktg.statistics;

import com.softwaremagico.ktg.Tournament;

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
 * @author Jorge
 */
public class TeamRanking {

    public String name;
    public Tournament tournament;
    public Integer wonMatchs;  /* partidos ganados por el equipo */

    public Integer drawMatchs;
    public Integer wonFights; /* combates ganados por los miembros del equipo */
    public Integer drawFights;

    public Integer score; /* puntos totales del equipo */


    public TeamRanking(String name, Tournament tournament, Integer won, Integer draw, Integer fights_won, Integer figths_draw, Integer score) {
        this.name = name;
        this.tournament = tournament;
        wonFights = fights_won;
        drawFights = figths_draw;
        this.score = score;
        wonMatchs = won;
        drawMatchs = draw;
    }

    public String returnShortName() {
        int length = 24;
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 6) + "... " + name.substring(name.length() - 2, name.length());
        }
    }

    public String returnShortName(int length) {
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 6) + "... " + name.substring(name.length() - 2, name.length());
        }
    }
}
