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
public class Participant implements Serializable {

    protected String id;
    protected String name;
    protected String surname;

    public Participant(String tmp_id, String name, String surname) {
        setName(name);
        setSurname(surname);
        setId(tmp_id);
    }

    public final void setId(String value) {
        id = value.replaceAll("-", "").replaceAll(" ", "").trim().toUpperCase();
    }

    public String getId() {
        return id;
    }

    protected final void setName(String value) {
        name = "";
        String[] names = value.split(" ");
        for (int i = 0; i < names.length; i++) {
            if (names[i].length() > 0) {
                name += names[i].substring(0, 1).toUpperCase();
                if (names[i].length() > 1) {
                    name += names[i].substring(1).toLowerCase();
                }
                if (i < names.length - 1) {
                    name += " ";
                }
            }
        }
        name = name.trim();
    }

    public String getShortName(int length) {
        return name.substring(0, Math.min(length, name.length()));
    }

    public String getShortName() {
        return getShortName(8);
    }

    public String getShortSurname(int length) {
        String[] shortSurname = surname.split(" ");
        if (shortSurname[0].length() > 3) {
            return shortSurname[0].substring(0, Math.min(length, shortSurname[0].length()));
        } else {
            return surname.substring(0, Math.min(length - 1, surname.length()));
        }
    }

    public String getShortSurname() {
        return getShortSurname(8);
    }

    public String getShortSurnameName(int maxLength) {
        if (name.length() + surname.length() == 0) {
            return "";
        }

        float rateSurname = (name.length() + getShortSurname(20).length()) / (float) surname.length();
        float rateName = (name.length() + getShortSurname(20).length()) / (float) name.length();
        String ret = getShortSurname((int) (maxLength / rateSurname)) + ", " + getShortName((int) (maxLength / rateName));
        return ret;
    }

    public String getSurnameName() {
        if (surname.length() > 0 || name.length() > 0) {
            return surname + ", " + name;
        } else {
            return " --- --- ";

        }
    }

    public String getSurnameNameIni(int maxLength) {
        if (surname.length() > 0 || name.length() > 0) {
            String surnameShort = surname.substring(0, Math.min(maxLength, surname.length())).toUpperCase();
            if (surname.length() > maxLength) {
                surnameShort += ".";
            }
            return surnameShort + ", " + name.substring(0, 1) + ".";
        } else {
            return " --- --- ";
        }
    }

    public String getSurnameNameIni() {
        return getSurnameNameIni(11);
    }

    protected final void setSurname(String value) {
        surname = "";
        String[] surnames = value.split(" ");
        for (int i = 0; i < surnames.length; i++) {
            if (surnames[i].length() > 0) {
                surname += surnames[i].substring(0, 1).toUpperCase();
                if (surnames[i].length() > 1) {
                    surname += surnames[i].substring(1).toLowerCase();
                }
            }
            if (i < surnames.length - 1) {
                surname += " ";
            }
        }
        surname = surname.trim();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAcronim() {
        String acronim = "";
        acronim += name.substring(0, 1).toUpperCase();
        String[] shortSurname = surname.split(" ");
        if (shortSurname[0].length() < 4 && shortSurname.length > 1) {
            acronim += shortSurname[0].substring(0, 1).toLowerCase();
            acronim += shortSurname[1].substring(0, 1).toUpperCase();
        } else {
            acronim += shortSurname[0].substring(0, 1).toUpperCase();
        }
        return acronim;
    }

    public static String nifFromDni(Integer dni) {
        if (dni == null)  {
            return null;
        }
        String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
        return String.valueOf(dni) + NIF_STRING_ASOCIATION.charAt(dni % 23);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Participant)) {
            return false;
        }
        Participant otherParticipant = (Participant) object;
        return this.id.equals(otherParticipant.id);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
