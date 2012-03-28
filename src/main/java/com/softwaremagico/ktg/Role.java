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
 *   Created on 10-nov-2011.
 */
package com.softwaremagico.ktg;

import java.io.Serializable;

/**
 * The objective of this class is store the database into a file. For managing the Role used in the GUI, use the RoleTag class. 
 */
public class Role implements Serializable {

    public String tournament;
    private String competitorID;
    public String Role;
    public int impressCard;

    public Role(String championship, String competitor, String selectedRole, int impressedCard) {
        tournament = championship;
        competitorID = competitor;
        Role = selectedRole;
        impressCard = impressedCard;
    }

    public String competitorID() {
        /*Float dni = Float.parseFloat(competitorID);
        if (competitorID.length() == 8 && dni != null) {
            String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
            return String.valueOf(dni) + NIF_STRING_ASOCIATION.charAt(Integer.parseInt(competitorID) % 23);
        }*/
        return competitorID;
    }
}
