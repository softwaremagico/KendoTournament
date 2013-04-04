package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.tournament.TournamentManagerPool;

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

public class SelectTournamentImportFightsFromCsv extends SelectTournamentForCsv {

    public SelectTournamentImportFightsFromCsv(String title, String buttonTag) {
        super(title, buttonTag);
    }

    @Override
    public String defaultFileName() {
        return "Fights_" + returnSelectedTournament().getName() + "_Lvl" + TournamentManagerPool.getManager(returnSelectedTournament()).getLastLevelUsed() + ".csv";
    }

    @Override
    protected boolean doAction(String file) {
       /* try {
            if (returnSelectedTournament().getType().equals(TournamentType.SIMPLE)) {
                if (FightPool.getManager(returnSelectedTournament()).importFromCsv(Folder.readFileLines(file, false))) {
                    return true;
                }
            } else {
                if (TournamentGroupPool.getManager(returnSelectedTournament()).importFromCsv(Folder.readFileLines(file, false))) {
                    return true;
                }
            }
        } catch (IOException ex) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(),ex);
        }*/
        return false;
    }
}
