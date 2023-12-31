package com.softwaremagico.ktg.core;
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoleTag {

    public static final String refereeRole = "Referee";
    public static final List<String> volunteerRoles;
    public static final List<String> competitorsRoles;
    private String tagName;     //Identical for all languages.
    public String name;    //The translation for each language.
    public String abbrev;
    public Color color;

    static {
        volunteerRoles = new ArrayList<>();
        volunteerRoles.add("VCLO");
        volunteerRoles.add("Volunteer");
        volunteerRoles.add("VolunteerK");

        competitorsRoles = new ArrayList<>();
        competitorsRoles.add("Competitor");
        competitorsRoles.add("VolunteerK");
    }

    public RoleTag(String tag, String tmp_name, String tmp_abbrev) {
        this.tagName = tag;
        name = tmp_name;
        abbrev = tmp_abbrev;
    }

    public String getName() {
        return tagName;
    }

    public void addColor(int red, int green, int blue) {
        color = new Color(red, green, blue);
    }

    public com.itextpdf.text.BaseColor getItextColor() {
        return new com.itextpdf.text.BaseColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.tagName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleTag other = (RoleTag) obj;
        if (!Objects.equals(this.tagName, other.tagName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
