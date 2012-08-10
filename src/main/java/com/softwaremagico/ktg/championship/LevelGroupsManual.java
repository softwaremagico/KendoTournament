/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;
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

import com.softwaremagico.ktg.Tournament;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jhortelano
 */
public class LevelGroupsManual extends LevelGroups {

    private Links links;

    public LevelGroupsManual(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        super(tournament, level, nextLevel, previousLevel, groupManager);
        links = new Links();
    }

    @Override
    protected TournamentGroup getGroupSourceOfWinner(TournamentGroup group, int winner) {
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
    protected LevelGroups addNewLevel(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        //Only first level is particular. 
        if (level > 0) {
            return new LevelGroupsTreeChampionship(tournament, level, nextLevel, previousLevel, groupManager);
        }
        return new LevelGroupsManual(tournament, level, nextLevel, previousLevel, groupManager);
    }

    @Override
    protected Integer getGroupIndexDestinationOfWinner(TournamentGroup group, int winner) {        
        Links destinations = new Links();

        //Get all destination of Winner
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(group)) {
                destinations.add(links.get(i));
            }
        }

        // Winners in the manual linking are stored by order.
        if (winner < destinations.size()) {
            System.out.println("Winner " +winner + " dst.size "+ destinations.get(winner).address.teams.size() + " -> " + getIndexOfGroup(destinations.get(winner).address));
            return getIndexOfGroup(destinations.get(winner).address);
        }

        return null;
    }

    /**
     * Stores the arrows of the designer.
     */
    class Links implements Serializable {

        private List<Link> links = new ArrayList<>();

        Links() {
        }

        void add(TournamentGroup from, TournamentGroup to) {
            if (to.getLevel() == from.getLevel() + 1) {
                links.add(new Link(from, to));
            }
        }
        
        void add(Link link){
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

    void addLink(TournamentGroup source, TournamentGroup address) {
        if (source.getLevel() == address.getLevel() - 1) {
            /*if (numberOfSourcesOfLink(source) >= source.getMaxNumberOfWinners()) {
                removefirstSourceLink(source);
            }
            if (numberOfAddressesOfLink(address) >= 2) {
                removefirstAddressLink(address);
            }*/
            links.add(source, address);

        }
    }

    int numberOfSourcesOfLink(TournamentGroup from) {
        int number = 0;

        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                number++;
            }
        }
        return number;

    }

    int numberOfAddressesOfLink(TournamentGroup to) {
        int number = 0;
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                number++;
            }
        }
        return number;

    }

    void removefirstSourceLink(TournamentGroup from) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                links.remove(i);
                break;
            }
        }
    }

    void removefirstAddressLink(TournamentGroup to) {
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

    void removeLinksSelectedGroup() {
        try {
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).source.equals(getLastSelected())) {
                    links.remove(i);
                    i--;
                }
            }
        } catch (NullPointerException npe) {
        }
    }
}
