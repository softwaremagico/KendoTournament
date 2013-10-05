package com.softwaremagico.ktg.lists;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
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
import com.softwaremagico.ktg.core.Tournament;

public class EmptyFightsListPDF extends SummaryPDF {

    EmptyFightsListPDF(Tournament championship, int shiaijo, boolean showAll) {
        super(championship, shiaijo);
        this.showAll = showAll;
    }

    @Override
    protected String getDrawFight(Fight f, int duel) {
        return "";
    }

    @Override
    protected String getFaults(Fight f, int duel, boolean leftTeam) {
        return "";
    }

    @Override
    protected String getScore(Fight f, int duel, int score, boolean leftTeam) {
        return "";
    }
}
