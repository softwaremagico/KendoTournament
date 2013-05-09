package com.softwaremagico.ktg.tournament;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.core.Tournament;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LeagueLevelCustom extends LeagueLevel {

    private Links links;

    protected LeagueLevelCustom(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
        super(tournament, level, nextLevel, previousLevel);
        links = new Links();
    }

    @Override
    protected TournamentGroup getGroupSourceOfWinner(TournamentGroup group, Integer winner) {
        Links sources = new Links();

        //Get all sources of Winner
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(group)) {
                sources.add(links.get(i));
            }
        }

        // Winners in the manual linking are stored by order.
        if (winner < sources.size()) {
            return sources.get(winner).source;
        }

        return null;
    }

    @Override
    protected LeagueLevel addNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
        //Only first level is particular. 
        if (level > 0) {
            return new LeagueLevelChampionship(tournament, level, nextLevel, previousLevel);
        }
        return new LeagueLevelCustom(tournament, level, nextLevel, previousLevel);
    }

    @Override
    public Integer getGroupIndexDestinationOfWinner(TournamentGroup group, Integer winner) {
        Links destinations = new Links();

        //Get all destination of Winner
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(group)) {
                destinations.add(links.get(i));
            }
        }

        // Winners in the manual linking are stored by order.
        if (winner < destinations.size()) {
            return nextLevel.getIndexOfGroup(destinations.get(winner).address);
        }

        return null;
    }

    /**
     * Stores the arrows of the designer.
     */
    private class Links implements Serializable {

        private List<Link> links = new ArrayList<>();

        Links() {
        }

        void add(TournamentGroup from, TournamentGroup to) {
            if (to.getLevel() == from.getLevel() + 1) {
                links.add(new Link(from, to));
            }
        }

        void add(Link link) {
            links.add(link);
        }

        int size() {
            return links.size();
        }

        Link get(int index) {
            return links.get(index);
        }

        void remove(int index) {
            links.remove(index);
        }

        class Link implements Serializable {

            TournamentGroup source;
            TournamentGroup address;

            Link(TournamentGroup from, TournamentGroup to) {
                source = from;
                address = to;
            }
        }
    }

    protected void addLink(TournamentGroup source, TournamentGroup address) {
        if (source.getLevel() == address.getLevel() - 1) {
            if (getNumberOfSourcesOfLink(source) >= source.getMaxNumberOfWinners()) {
                removefirstSourceLink(source);
            }
            if (getNumberOfSourcesOfLink(source) >= source.getTeams().size()) {
                removefirstSourceLink(source);
            }
            if (getNumberOfAddressesOfLink(address) >= 2) {
                removefirstAddressLink(address);
            }
            links.add(source, address);
        }
    }

    protected int getNumberOfSourcesOfLink(TournamentGroup from) {
        int number = 0;

        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                number++;
            }
        }
        return number;

    }

    protected int getNumberOfAddressesOfLink(TournamentGroup to) {
        int number = 0;
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                number++;
            }
        }
        return number;

    }

    protected void removefirstSourceLink(TournamentGroup from) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                links.remove(i);
                break;
            }
        }
    }

    protected void removefirstAddressLink(TournamentGroup to) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                links.remove(i);
                break;
            }
        }
    }

    public boolean allGroupsHaveManualLink() {
        try {
            for (int i = 0; i < tournamentGroups.size(); i++) {
                boolean found = false;

                for (int j = 0; j < links.size(); j++) {
                    if (links.get(j).source.equals(tournamentGroups.get(i))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    protected void removeLinksSelectedGroup(TournamentGroup lastSelected) {
        try {
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).source.equals(lastSelected)) {
                    links.remove(i);
                    i--;
                }
            }
        } catch (NullPointerException npe) {
        }
    }
}
