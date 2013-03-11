package com.softwaremagico.ktg;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FightManager {

    private List<Fight> fights = new ArrayList<>();

    FightManager() {
    }

    /**
     * ****************************************************************************
     *
     *
     * RANDOM FIGHTS
     *
     *
     *****************************************************************************
     */
    

    public static int getMaxLevelOfFights(List<Fight> fightsList) {
        int level = 0;
        for (Fight f : fightsList) {
            if (f.getLevel() > level) {
                level = f.getLevel();
            }
        }
        return level;
    }

    /**
     * **********************************************
     *
     * UPDATE FIGHTS
     *
     ***********************************************
     */
    


    public List<String> exportToCsv() {
        List<String> csv = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                csv.addAll(fights.get(i).exportToCsv(i));
            }
        }
        return csv;
    }

    public boolean importFromCsv(List<String> csv) {
        int duelsCount = 0;
        Fight fight = null;
        int fightsInFile = 0;
        int fightsImported = 0;
        for (String csvLine : csv) {
            if (csvLine.startsWith(Fight.getTag())) {
                fight = null;
                fightsInFile++;
                duelsCount = 0;
                String[] fields = csvLine.split(";");
                //Obtain fight.
                int fightNumber = Integer.parseInt(fields[1]);
                if (fightNumber < fights.size() && fightNumber >= 0) {
                    //Fight not finished and correct.
                    if (fights.get(fightNumber).getTeam1().getName().equals(fields[2]) && fights.get(fightNumber).getTeam2().getName().equals(fields[3])) {
                        if (!fights.get(fightNumber).isOver()) {
                            fight = fights.get(fightNumber);
                            fightsImported++;
                            fight.setOver(true);
                            fight.setOverStored(false);
                        }
                    } else {
                        MessageManager.errorMessage(this.getClass().getName(), "csvNotImported", "Error");
                        return false;
                    }
                }
            } else if (csvLine.startsWith(Duel.getCsvTag())) {
                if (fight != null) {
                    fight.getDuels().get(duelsCount).importFromCsv(csvLine);
                    duelsCount++;
                }
            } else if (csvLine.startsWith(Undraw.getCsvTag())) {
                //Do nothing. 
                KendoLog.warning(FightManager.class.getName(), "Undraw line found in a simple tournament!");
            }
        }

        if (fightsImported > 0) {
            MessageManager.informationMessage(this.getClass().getName(), "csvImported", "CSV", " (" + fightsImported + "/" + fightsInFile + ")");
            return true;
        } else {
            MessageManager.errorMessage(this.getClass().getName(), "csvNotImported", "Error");
            return false;
        }
    }

    /**
     * **********************************************
     *
     * SECONDARY CLASSES
     *
     ***********************************************
     */
    /**
     * A couple of teams for a fight.
     */
    private class Couple {

        int a;
        int b;
        int weight = 1;

        Couple(int valuea, int valueb) {
            a = valuea;
            b = valueb;
        }
    }
}
