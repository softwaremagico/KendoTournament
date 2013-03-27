/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

import com.softwaremagico.ktg.FightPool;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.TournamentType;
import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.gui.tournament.TournamentGroupPool;
import java.util.List;

public class SelectTournamentExportFightsToCsv extends SelectTournamentForCsv {

    public SelectTournamentExportFightsToCsv(String title, String buttonTag) {
        super(title, buttonTag);
    }

    @Override
    public String defaultFileName() {
        return "Fights_" + returnSelectedTournament().getName() + "_Lvl" + FightPool.getManager(returnSelectedTournament()).getLastLevel() + ".csv";
    }

    @Override
    protected boolean doAction(String file) {
        if (!MyFile.fileExist(file) || MessageManager.questionMessage("existFile", "Warning!")) {

            List<String> csv;
            if (returnSelectedTournament().getMode().equals(TournamentType.SIMPLE)) {
                csv = FightPool.getManager(returnSelectedTournament()).exportToCsv();
            } else {
                csv = TournamentGroupPool.getManager(returnSelectedTournament()).exportToCsv();
            }

            if (Folder.saveListInFile(csv, file)) {
                MessageManager.informationMessage(this.getClass().getName(), "csvExported", "CSV");
                return true;
            }
        }
        return false;
    }
}
