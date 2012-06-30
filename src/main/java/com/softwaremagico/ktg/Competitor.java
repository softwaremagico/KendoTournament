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
 *  Created on 20-dic-2008.
 */
package com.softwaremagico.ktg;

import java.io.Serializable;

/**
 *
 * @author jorge
 */
public class Competitor extends Participant implements Serializable {

    public String club;
    private int order;

    public Competitor(String tmp_id, String tmp_name, String tmp_surname, String tmp_club) {
        super(tmp_id, tmp_name, tmp_surname);
        storeClub(tmp_club);
    }

    public void addOrder(int value) {
        order = value;
    }

    public int getOrder() {
        return order;
    }

    private void storeClub(String value) {
        club = "";
        String[] data = value.split(" ");
        for (int i = 0; i < data.length; i++) {
            if ((data[i].length() > 2) && (data[i].substring(1).equals(data[i].substring(1).toLowerCase()))) { //There is not capital letters.                  
                club += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
            } else {
                club += data[i] + " ";
            }
        }
        club = club.trim();
    }

}
