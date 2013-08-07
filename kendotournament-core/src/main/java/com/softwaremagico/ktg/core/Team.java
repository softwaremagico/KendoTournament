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

import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.persistence.RegisteredPersonPool;

import java.sql.SQLException;
import java.util.HashMap;

public class Team implements Comparable<Team> {

	private Tournament tournament;
	private HashMap<Integer, HashMap<Integer, RegisteredPerson>> membersOrder; // HashMap<Level,HashMap<Order,
																				// Member>>;
	private String name;
	private int group = 0; // for the league

	public Team(String name, Tournament tournament) {
		setName(name);
		this.tournament = tournament;
		initializeMemberOrder();
	}

	private void initializeMemberOrder() {
		membersOrder = new HashMap<>();
		membersOrder.put(0, new HashMap<Integer, RegisteredPerson>());
	}

	/**
	 * Set a member in a team or change the order with current member if level >
	 * 0
	 * 
	 * @param member
	 * @param order
	 * @param level
	 * @throws TeamMemberOrderException
	 */
	public void setMember(RegisteredPerson member, Integer order, int level) throws TeamMemberOrderException {
		if (member.isValid() && level >= 0) {
			// First level must to put the user.
			if (level == 0) {
				membersOrder.get(level).put(order, member);
			} else {
				exchangeMembersOrder(getMemberOrder(level, member), order, level);
			}
		}
	}

	/**
	 * Exchange the position of two members.
	 * 
	 * @param order1
	 * @param order2
	 * @throws TeamMemberOrderException
	 */
	private void exchangeMembersOrder(Integer order1, Integer order2, int level) throws TeamMemberOrderException {
		if (order1 == null || order2 == null) {
			throw new TeamMemberOrderException("Team member order cannot be exchanged due to order null value used.");
		}

		RegisteredPerson memberInOrder1 = getMember(order1, level);
		RegisteredPerson memberInOrder2 = getMember(order2, level);

		HashMap<Integer, RegisteredPerson> levelOrder = membersOrder.get(level);
		if (levelOrder == null) {
			levelOrder = copyLastOrder(level);
			membersOrder.put(level, levelOrder);
		}

		levelOrder.put(order2, memberInOrder1);
		levelOrder.put(order1, memberInOrder2);
	}

	/**
	 * Creates a copy of a the order of the team members using the last order
	 * defined.
	 * 
	 * @param level
	 * @return
	 */
	private HashMap<Integer, RegisteredPerson> copyLastOrder(int level) {
		HashMap<Integer, RegisteredPerson> levelOrder = getMembersOrder(level);
		HashMap<Integer, RegisteredPerson> newOrder = new HashMap<Integer, RegisteredPerson>();
		for (Integer order : levelOrder.keySet()) {
			newOrder.put(order, levelOrder.get(order));
		}
		return newOrder;
	}

	public RegisteredPerson getMember(Integer order, int level) {
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

	/**
	 * Get the order of a member in a level.
	 * 
	 * @param level
	 * @param competitorID
	 * @return
	 * @throws SQLException
	 */
	public Integer getMemberOrder(int level, String competitorID) throws SQLException {
		return getMemberOrder(level, RegisteredPersonPool.getInstance().get(competitorID));
	}

	/**
	 * Get the order of a member in a level.
	 * 
	 * @param level
	 * @param competitor
	 * @return
	 */
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

	/**
	 * Get all members order in one level.
	 * 
	 * @param level
	 * @return
	 */
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
				if (level >= 0) {
					return getMembersOrder(level - 1);
				}
			}
		}
		return null;
	}

	/**
	 * User previously has change the order of one or more members in a level.
	 * 
	 * @param level
	 * @return
	 */
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
		return getShortName(21);
	}

	public String getShortName(int length) {
		if (name.length() <= length) {
			return name;
		} else {
			return name.substring(0, length - 3) + ". " + name.substring(name.length() - 2, name.length());
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

	/**
	 * Remove a meber of the team
	 * 
	 * @return false if the member is not of this team.
	 */
	public boolean removeMemeber(RegisteredPerson member) {
		boolean found = false;
		for (Integer level : membersOrder.keySet()) {
			for (Integer order : membersOrder.get(level).keySet()) {
				if (membersOrder.get(level).get(order).equals(member)) {
					membersOrder.get(level).remove(order);
					found = true;
				}
			}
		}
		return found;
	}
}
