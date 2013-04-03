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

import com.softwaremagico.ktg.database.RegisteredPersonPool;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Jorge
 */
public class Team implements Serializable, Comparable<Team> {

    private Tournament tournament;
    private HashMap<Integer, HashMap<Integer, RegisteredPerson>> membersOrder; //HashMap<Level,HashMap<MemberOrder, Order>>;
    private String name;
    private int group = 0; //for the league

    public Team(String name, Tournament tournament) {
        setName(name);
        this.tournament = tournament;
        initializeMemberOrder();
    }

    private void initializeMemberOrder() {
        membersOrder = new HashMap<>();
        membersOrder.put(0, new HashMap<Integer, RegisteredPerson>());
    }

    public void setMember(RegisteredPerson member, Integer order, Integer level) {
        if (member.isValid()) {
            HashMap<Integer, RegisteredPerson> levelOrder = membersOrder.get(level);
            if (levelOrder == null) {
                levelOrder = new HashMap<>();
            }
            levelOrder.put(order, member);
        }
    }

    public RegisteredPerson getMember(int order, int level) {
        try {
            return getMembersOrder(level).get(order);
        } catch (NullPointerException npe) {
        }
        return null;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public int getNumberOfMembers(int level) {
        try {
            return getMembersOrder(level).size();
        } catch (NullPointerException npe) {
        }
        return 0;
    }

    public Integer getMemberOrder(int level, String competitorID) {
        return getMemberOrder(level, RegisteredPersonPool.getInstance().get(competitorID));
    }

    public Integer getMemberOrder(int level, RegisteredPerson competitor) {
        try {
            HashMap<Integer, RegisteredPerson> orderInLevel = getMembersOrder(level);
            for (Integer order : orderInLevel.keySet()) {
                if (orderInLevel.get(order).equals(competitor)) {
                    return order;
                }
            }
        } catch (NullPointerException npe) {
        }
        return null;
    }

    public HashMap<Integer, HashMap<Integer, RegisteredPerson>> getMembersOrder() {
        return membersOrder;
    }

    public HashMap<Integer, RegisteredPerson> getMembersOrder(int level) {
        if (level >= 0) {
            try {
                HashMap<Integer, RegisteredPerson> membersInLevel = membersOrder.get(level);
                if (membersInLevel == null) {
                    return getMembersOrder(level - 1);
                } else {
                    return membersInLevel;
                }
            } catch (Exception e) {
                return getMembersOrder(level - 1);
            }
        }
        return null;
    }

    public boolean areMemberOrderChanges(int level) {
        try {
            if (membersOrder.get(level) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public int levelChangesSize() {
        return membersOrder.size();
    }

    public void addGroup(int g) {
        if (g >= 0) {
            group = g;
        }
    }

    public String getShortName() {
        int length = 21;
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 3) + ". " + name.substring(name.length() - 2, name.length());
        }
    }

    public String getShortName(int xSize) {
        int length = xSize / 7;
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 6) + "... " + name.substring(name.length() - 2, name.length());
        }
    }

    public final void setName(String name) {
        this.name = name.trim().replace(";", ",");
    }

    public String getName() {
        return name;
    }

    public int realMembers() {
        int counter = 0;
        if (membersOrder.size() > 0) {
            for (int i = 0; i < membersOrder.get(0).size(); i++) {
                if (membersOrder.get(0).get(i) != null) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int numberOfMembers() {
        if (membersOrder.size() > 0) {
            return membersOrder.get(0).size();
        }
        return 0;
    }

    public boolean isMember(RegisteredPerson member) {
        return getMembersOrder(0).values().contains(member);
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
        return this.name.equals(otherTeam.name) && this.tournament.equals(otherTeam.tournament);

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.tournament != null ? this.tournament.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(Team t) {
        return getName().compareTo(t.getName());
    }
}
