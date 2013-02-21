package com.softwaremagico.ktg.database;
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

import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.Participant;
import com.softwaremagico.ktg.Team;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class CorrectNIF {

    CorrectNIF() {
        correctTeams(); //Teams previous to competitors!
        correctCompetitors();
    }

    private void correctCompetitors() {
        //Nif of competitors.
        List<Competitor> competitors = DatabaseConnection.getInstance().getDatabase().getAllCompetitors();
        for (int i = 0; i < competitors.size(); i++) {
            String idNumber = competitors.get(i).getId();
            competitors.get(i).setId(idNumber);

            try {
                Integer dni = Integer.parseInt(idNumber);
                if (competitors.get(i).getId().length() == 8 && dni != null) {
                    competitors.get(i).setId(Participant.nifFromDni(dni));
                }
            } catch (NumberFormatException nfe) {
            }
            DatabaseConnection.getInstance().getDatabase().updateIdCompetitor(competitors.get(i), false);
        }
    }

    private void correctTeams() {
        //NIF in teams.
        List<Team> teams = DatabaseConnection.getInstance().getDatabase().getAllTeams();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < teams.get(i).levelChangesSize(); j++) {
                for (int k = 0; k < teams.get(i).getNumberOfMembers(j); k++) {
                    try {
                        String idNumber = teams.get(i).getMember(k, j).getId();
                        teams.get(i).getMember(k, j).setId(idNumber);

                        try {
                            Integer dni = Integer.parseInt(idNumber);
                            if (teams.get(i).getMember(k, j).getId().length() == 8 && dni != null) {
                                teams.get(i).getMember(k, j).setId(Participant.nifFromDni(dni));
                            }
                        } catch (NumberFormatException nfe) {
                        }
                    } catch (NullPointerException npe) {
                    }
                }
            }
            DatabaseConnection.getInstance().getDatabase().storeTeam(teams.get(i), false);
        }
    }


}
