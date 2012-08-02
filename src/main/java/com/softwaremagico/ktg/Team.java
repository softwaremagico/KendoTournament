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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class Team implements Serializable {

    public Tournament competition;
    private List<List<Competitor>> participantsPerLevel = new ArrayList<>();
    private String name;
    public int group = 0; //for the league

    public Team(String tmp_name, Tournament tmp_competition) {
        storeName(tmp_name);
        competition = tmp_competition;
        participantsPerLevel.add(new ArrayList<Competitor>());
    }

    public void setMembers(List<List<Competitor>> members) {
        participantsPerLevel = members;
    }

    public void addMembers(List<Competitor> tmp_participants, int level) {
        //Add empty levels to avoid null exceptions.
        for (int i = participantsPerLevel.size(); i <= level; i++) {
            participantsPerLevel.add(new ArrayList<Competitor>());
        }
        //if (level < participantsPerLevel.size()) {
        //    participantsPerLevel.set(level, tmp_participants);
        //} else {
        //Change the selected level.
        participantsPerLevel.set(level, tmp_participants);
        //}
    }

    public void addOneMember(int priority, Competitor participant, int level) {
        if (level >= participantsPerLevel.size()) {
            participantsPerLevel.add(new ArrayList<Competitor>());
            participantsPerLevel.get(level).add(participant);
        } else {
            participantsPerLevel.get(level).set(priority, participant);
        }
    }

    public void addOneMember(Competitor participant, int level) {
        if (level >= participantsPerLevel.size()) {
            participantsPerLevel.add(new ArrayList<Competitor>());
        }
        participantsPerLevel.get(level).add(participant);
    }

    public Competitor getMember(int order, int level) {
        try {
            return getCompetitorsInLevel(level).get(order);
        } catch (NullPointerException npe) {
        }
        return null;
    }

    public int getNumberOfMembers(int level) {
        try {
            return getCompetitorsInLevel(level).size();
        } catch (NullPointerException npe) {
        }
        return 0;
    }

    public int getIndexOfMember(int level, Competitor c) {
        try {
            return getCompetitorsInLevel(level).indexOf(c);
        } catch (NullPointerException npe) {
        }
        return -1;
    }

    public int getIndexOfMember(int level, String competitorID) {
        try {
            List<Competitor> orderInLevel = getCompetitorsInLevel(level);
            for (int i = 0; i < orderInLevel.size(); i++) {
                if (orderInLevel.get(i).id.equals(competitorID)) {
                    return i;
                }
            }
        } catch (NullPointerException npe) {
        }
        return -1;
    }

    /**
     * Search for the order of competitors in a level.
     *
     * @param level
     * @return
     */
    public List<Competitor> getCompetitorsInLevel(int level) {
        for (int i = level; i >= 0; i--) {
            if (i < participantsPerLevel.size() && participantsPerLevel.get(i) != null && !participantsPerLevel.get(i).isEmpty()) {
                return participantsPerLevel.get(i);
            }
        }
        return null;
    }

    public boolean changesInThisLevel(int level) {
        if (level < participantsPerLevel.size() || participantsPerLevel.isEmpty()) {
            if (participantsPerLevel.get(level) != null) {
                return true;
            }
        }
        return false;
    }

    public int levelChangesSize() {
        return participantsPerLevel.size();
    }

    public void addGroup(int g) {
        if (g >= 0) {
            group = g;
        }
    }

    public String returnShortName() {
        int length = 21;
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 3) + ". " + name.substring(name.length() - 2, name.length());
        }
    }

    public String returnShortName(int xSize) {
        int length = xSize / 7;
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 6) + "... " + name.substring(name.length() - 2, name.length());
        }
    }

    public final void storeName(String value) {
        //name = value.substring(0, 1).toUpperCase() + value.substring(1);
        /*
         * name = ""; String[] data = value.trim().split(" "); for (int i = 0; i
         * < data.length; i++) { if (data[i].length() > 1) { name +=
         * data[i].substring(0, 1).toUpperCase() +
         * data[i].substring(1).toLowerCase() + " "; } else { name += data[i] +
         * " "; } }
         */
        name = value.trim();
    }

    public String returnName() {
        return name;
    }

    public int realMembers() {
        int counter = 0;
        if (participantsPerLevel.size() > 0) {
            for (int i = 0; i < participantsPerLevel.get(0).size(); i++) {
                if ((participantsPerLevel.get(0).get(i).getName().length() > 0)
                        || (participantsPerLevel.get(0).get(i).getSurname().length() > 0)) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public void completeTeam(int numberOfParticipants, int level) {
        if (level < participantsPerLevel.size()) {
            for (int i = participantsPerLevel.get(level).size(); i < numberOfParticipants; i++) {
                participantsPerLevel.get(level).add(new Competitor("", "", "", ""));
            }
        }
    }

    public int numberOfMembers() {
        if (participantsPerLevel.size() > 0) {
            return participantsPerLevel.get(0).size();
        }
        return 0;
    }

    public void showMembers() {
        if (returnName().equals("3")) {
            System.out.println(" +++++++ " + returnName() + " +++++++ ");
            for (int i = 0; i < participantsPerLevel.size(); i++) {
                for (int j = 0; j < participantsPerLevel.get(i).size(); j++) {
                    System.out.println(participantsPerLevel.get(i).get(j).getSurnameName() + " LeveL: " + i);
                }
            }
            System.out.println(" +++++++ +++++++++++++ +++++++ ");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Team)) {
            return false;
        }
        Team otherTeam = (Team) object;
        return this.name.equals(otherTeam.name) && this.competition.equals(otherTeam.competition);

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.competition != null ? this.competition.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
