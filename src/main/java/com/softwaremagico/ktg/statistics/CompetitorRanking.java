package com.softwaremagico.ktg.statistics;
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
public class CompetitorRanking {
    public String name;
    public String surname;
    public String id;
    public Integer victorias;
    public Integer puntos;

    public CompetitorRanking(String tmp_name, String tmp_surname, String tmp_id, Integer tmp_victorias, Integer tmp_puntos){
        name = tmp_name;
        surname = tmp_surname;
        id = tmp_id;
        victorias = tmp_victorias;
        puntos = tmp_puntos;
    }
    
}
