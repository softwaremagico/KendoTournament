package com.softwaremagico.ktg.gui.base;
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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;

public class FightAreaComboBox extends KComboBox {

    private Tournament tournament;

    public FightAreaComboBox(Tournament tournament) {
        update(tournament);
    }

    public final void update(Tournament tournament) {
        this.tournament = tournament;
        fillFightingAreas();
    }

    private void fillFightingAreas() {
        Integer fightArea = getSelectedFightArea();
        removeAllItems();
        try {
            for (int i = 0; i < tournament.getFightingAreas(); i++) {
                addItem(KendoTournamentGenerator.getFightAreaName(i));
            }

            setSelectedIndex(fightArea);
        } catch (NullPointerException npe) {
        }
    }

    public Integer getSelectedFightArea() {
        try {
            return getSelectedIndex();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getSelectedFightAreaName() {
        try {
            return KendoTournamentGenerator.getFightAreaName(getSelectedFightArea());
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
