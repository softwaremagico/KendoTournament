package com.softwaremagico.ktg;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Serializable;

/**
 * The objective of this class is store the database into a file. For managing
 * the Role used in the GUI, use the RoleTag class.
 */
public class Role implements Serializable {

    public String tournament;
    private String competitorID;
    public String roleName;
    public int impressCard;

    public Role(String championship, String competitor, String selectedRole, int impressedCard) {
        tournament = championship;
        competitorID = competitor;
        roleName = selectedRole;
        impressCard = impressedCard;
    }

    public String competitorID() {
        return competitorID;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Role)) {
            return false;
        }
        Role otherRole = (Role) object;
        return this.tournament.equals(otherRole.tournament)
                && this.roleName.equals(otherRole.roleName)
                && this.competitorID.equals(otherRole.competitorID);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.tournament != null ? this.tournament.hashCode() : 0);
        hash = 67 * hash + (this.competitorID != null ? this.competitorID.hashCode() : 0);
        hash = 67 * hash + (this.roleName != null ? this.roleName.hashCode() : 0);
        return hash;
    }
}
