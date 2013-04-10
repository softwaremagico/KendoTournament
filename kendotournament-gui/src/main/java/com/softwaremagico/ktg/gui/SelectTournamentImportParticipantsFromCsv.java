package com.softwaremagico.ktg.gui;

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
public class SelectTournamentImportParticipantsFromCsv extends SelectTournamentForCsv {

    public SelectTournamentImportParticipantsFromCsv(String title, String buttonTag) {
        super(title, buttonTag);
    }

    @Override
    public String defaultFileName() {
        return "Participants" + returnSelectedTournament().getName() + ".csv";
    }

    @Override
    protected boolean doAction(String file) {
        /*
         * try {
         * if(FightPool.getManager(returnSelectedTournament()).importFromCsv(Folder.readFileLines(file,
         * false))){ MessageManager.informationMessage("csvImported", "CSV");
         * return true; } } catch (IOException ex) {
         * KendoTournamentGenerator.showErrorInformation(ex);
        }
         */
        return false;
    }
}