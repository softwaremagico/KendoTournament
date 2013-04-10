package com.softwaremagico.ktg.pdflist;
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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TournamentManagerPool;

/**
 *
 * @author Jorge
 */
public class FightList extends ListFromTournamentCreatePDF {

    public FightList() {
        super();
        this.setTitle(trans.returnTag("titleListFights"));
    }

    @Override
    public String defaultFileName() {
        String shiaijo = "";
        if (returnSelectedArena() >= 0) {
            shiaijo = "_" + KendoTournamentGenerator.getFightAreaName(returnSelectedArena());
        }
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_FightList" + shiaijo;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    @Override
    protected ParentList getPdfGenerator() {
        Tournament tournament = listTournaments.get(TournamentComboBox.getSelectedIndex());
        return new FightListPDF(tournament);
    }
}