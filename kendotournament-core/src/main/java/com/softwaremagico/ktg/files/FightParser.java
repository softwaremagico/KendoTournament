/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.files;

import com.softwaremagico.ktg.core.Fight;
import java.util.ArrayList;
import java.util.List;

public class FightParser {

    public List<String> exportToCsv(List<Fight> fights) {
        List<String> csv = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                csv.addAll(fights.get(i).exportToCsv(i));
            }
        }
        return csv;
    }

    public List<Fight> importFromCsv(List<String> csv) {
        List<Fight> fights = null;
        /*int duelsCount = 0;
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
                        return null;
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
            return fights;
        } else {
            MessageManager.errorMessage(this.getClass().getName(), "csvNotImported", "Error");
            return null;
        }*/
        return fights;
    }
}
