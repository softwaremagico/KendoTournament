package com.softwaremagico.ktg;
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

import java.io.Serializable;

/**
 *
 * @author jorge
 */
public class Club implements Serializable {

    private String name = "";
    private String country = "";
    private String city = "";
    private String address = "";
    public String representativeID = "";
    public String email = "";
    public String phone = null;
    private String web = "";

    public Club(String tmp_name, String tmp_country, String tmp_city) {
        storeName(tmp_name);
        storeCountry(tmp_country);
        storeCity(tmp_city);
    }

    public void RefreshRepresentative(String tmp_representative, String tmp_email, String tmp_phone) {
        representativeID = tmp_representative;
        email = tmp_email;
        phone = tmp_phone;
    }

    private void storeName(String value) {
        name = "";
        String[] data = value.split(" ");
        for (int i = 0; i < data.length; i++) {
            if ((data[i].length() > 2) && (data[i].substring(1).equals(data[i].substring(1).toLowerCase()))) { //There is not capital letters.                  
                name += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
            } else {
                name += data[i] + " ";
            }
        }
        name = name.trim();
    }

    public String returnName() {
        return name;
    }

    private void storeCountry(String value) {
        country = "";
        String[] data = value.split(" ");
        for (int i = 0; i < data.length; i++) {
            if (data[i].length() > 2) {
                country += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
            } else {
                country += data[i] + " ";
            }
        }
        country = country.trim();
    }

    private void storeCity(String value) {
        city = "";
        String[] data = value.split(" ");
        for (int i = 0; i < data.length; i++) {
            if (data[i].length() > 2) {
                city += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
            } else {
                city += data[i] + " ";
            }
        }
        city = city.trim();
    }

    public void storeAddress(String value) {
        address = "";
        String[] data = value.split(" ");
        for (int i = 0; i < data.length; i++) {
            if (data[i].length() > 2) {
                address += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
            } else {
                address += data[i] + " ";
            }
        }
        address = address.trim();
    }

    public void storeWeb(String value) {
        web = value.trim();
    }

    public String returnCountry() {
        return country;
    }

    public String returnCity() {
        return city;
    }

    public String returnAddress() {
        return address;
    }

    public String returnWeb() {
        return web;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Club)) {
            return false;
        }
        Club otherClub = (Club) object;
        return this.name.equals(otherClub.name) && this.city.equals(otherClub.city);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.city != null ? this.city.hashCode() : 0);
        return hash;
    }
}
