/*
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 23-ene-2009.
 */
package com.softwaremagico.ktg.statistics;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.pdflist.ListFromTournament;

/**
 *
 * @author Jorge
 */
public class SelectTournamentForCSV extends ListFromTournament {

    public SelectTournamentForCSV() {
        Start(false);
        this.setTitle(trans.returnTag("CvsMenuItem", KendoTournamentGenerator.getInstance().language));
        GenerateButton.setText(trans.returnTag("ImportMenu", KendoTournamentGenerator.getInstance().language));
    }

    @Override
    public String defaultFileName() {
        return null;
    }

    @Override
    public void generate() {
    }
}

