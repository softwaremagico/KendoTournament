package com.softwaremagico.ktg.gui;


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


public class SelectTournament extends ListFromTournamentCreateFile {

    public SelectTournament(String title, String buttonTag) {
        createGui(false);
        this.setTitle(trans.returnTag(title));
        GenerateButton.setText(trans.returnTag(buttonTag));
    }
    
    public SelectTournament(String title) {
        createGui(true);
        this.setTitle(trans.returnTag(title));
    }

    @Override
    public String defaultFileName() {
        return "exportedData";
    }

    /*@Override
    public void generate() {
        try {
            String file;
            if (!(file = exploreWindowsForCsv("CSV",
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
            }
        } catch (Exception ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }*/

}
