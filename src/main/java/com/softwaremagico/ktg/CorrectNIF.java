/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 29-ene-2011.
 */
package com.softwaremagico.ktg;

import java.util.List;

/**
 *
 * @author Jorge
 */
public class CorrectNIF {

    CorrectNIF(KendoTournamentGenerator tournament) {
        correctTeams(tournament); //Teams previous to competitors!
        correctCompetitors(tournament);
    }

    private void correctCompetitors(KendoTournamentGenerator tournament) {
        //Nif of competitors.
        List<Competitor> competitors = tournament.database.getAllCompetitors();
        for (int i = 0; i < competitors.size(); i++) {
            String idNumber = competitors.get(i).id;
            idNumber = idNumber.replace("-", "");
            idNumber = idNumber.replace(" ", "");
            idNumber = idNumber.toUpperCase();

            competitors.get(i).id = idNumber;

            try {
                Integer dni = Integer.parseInt(idNumber);
                if (competitors.get(i).id.length() == 8 && dni != null) {
                    competitors.get(i).id = nifFromDni(dni);
                }
            } catch (NumberFormatException nfe) {
            }
            tournament.database.updateIdCompetitor(competitors.get(i), false);
        }
    }

    private void correctTeams(KendoTournamentGenerator tournament) {
        //NIF in teams.
        List<Team> teams = tournament.database.searchAllTeamsInAllTournaments(false);
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < teams.get(i).levelChangesSize(); j++) {
                for (int k = 0; k < teams.get(i).getNumberOfMembers(j); k++) {
                    try {
                        String idNumber = teams.get(i).getMember(k, j).id;
                        idNumber = idNumber.replace("-", "");
                        idNumber = idNumber.replace(" ", "");
                        idNumber = idNumber.toUpperCase();

                        teams.get(i).getMember(k, j).id = idNumber;

                        try {
                            Integer dni = Integer.parseInt(idNumber);
                            if (teams.get(i).getMember(k, j).id.length() == 8 && dni != null) {
                                teams.get(i).getMember(k, j).id = nifFromDni(dni);
                            }
                        } catch (NumberFormatException nfe) {
                        }
                    } catch (NullPointerException npe) {
                    }
                }
            }
            tournament.database.storeTeam(teams.get(i), false);
        }
    }

    public static String nifFromDni(int dni) {
        String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
        //System.out.println(String.valueOf(dni) + NIF_STRING_ASOCIATION.charAt(dni % 23));
        return String.valueOf(dni) + NIF_STRING_ASOCIATION.charAt(dni % 23);
    }
}
