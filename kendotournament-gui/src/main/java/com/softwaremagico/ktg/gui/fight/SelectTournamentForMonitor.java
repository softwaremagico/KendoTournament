package com.softwaremagico.ktg.gui.fight;
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

import com.softwaremagico.ktg.gui.ListFromTournamentCreateFile;

/**
 *
 * @author jorge
 */
public class SelectTournamentForMonitor extends ListFromTournamentCreateFile {

    public SelectTournamentForMonitor() {
        createGui(false);
        this.setTitle(trans.getTranslatedText("titleMonitorTournament"));
        GenerateButton.setText(trans.getTranslatedText("ButtonMonitorTournament"));
        ArenaComboBox.setEnabled(true);
    }

    @Override
    public String defaultFileName() {
        return "";
    }

}
