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
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.CustomLinkPool;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeagueLevelCustom extends LeagueLevel {

    private Links links;

    protected LeagueLevelCustom(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
        super(tournament, level, nextLevel, previousLevel);
        links = new Links();
    }

    public void setLinks(List<CustomWinnerLink> links) {
        this.links.set(links);
    }

    @Override
    protected TGroup getGroupSourceOfWinner(TGroup group, Integer winner) {
        Links sources = new Links();

        //Get all sources of Winner
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getAddressGroup().equals(group)) {
                sources.add(links.get(i));
            }
        }

        // Winners in the custom linking are stored by order.
        if (winner < sources.size()) {
            return sources.get(winner).getSourceGroup();
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
    public Integer getGroupIndexDestinationOfWinner(TGroup group, Integer winner) {
        Links destinations = new Links();

        //Get all destination of Winner
        for (int i = 0; i < links.size(); i++) {
            try {
                if (links.get(i).getSourceGroup().equals(group)) {
                    destinations.add(links.get(i));
                }
            } catch (NullPointerException npe) {
            }
        }

        // Winners in the custom linking are stored by order.
        if (winner < destinations.size()) {
            return destinations.get(winner).getAddress();
        }
        return null;
    }

    public List<CustomWinnerLink> getLinks() {
        return links.getLinks();
    }

    /**
     * Stores the arrows of the designer.
     */
    private class Links implements Serializable {
		private static final long serialVersionUID = -5598140979350514701L;
		private List<CustomWinnerLink> customLinks = new ArrayList<>();

        Links() {
        }

        protected void add(TGroup from, TGroup to) {
            if (to.getLevel() == from.getLevel() + 1) {
                customLinks.add(new CustomWinnerLink(tournament, from, to));
                setWinnerOrder(from);
            }
        }

        /**
         * Update the order of winners for each link.
         */
        private void setWinnerOrder() {
            for (int i = 0; i < getGroups().size(); i++) {
                setWinnerOrder(getGroups().get(i));
            }
        }

        /**
         * Update the order of winners of a source group.
         *
         * @param from
         */
        private void setWinnerOrder(TGroup from) {
            List<CustomWinnerLink> sourceLinksOfGroup = getSourceLinksOfGroup(from);
            for (int i = 0; i < sourceLinksOfGroup.size(); i++) {
                sourceLinksOfGroup.get(i).setWinner(i);
            }
        }

        private List<CustomWinnerLink> getSourceLinksOfGroup(TGroup from) {
            Integer source = getIndexOfGroup(from);
            return getSourceLinksOfGroup(source);
        }

        private List<CustomWinnerLink> getSourceLinksOfGroup(Integer source) {
            List<CustomWinnerLink> sourceLinksOfGroup = new ArrayList<>();
            for (CustomWinnerLink link : getLinks()) {
                if (link.getSource().equals(source)) {
                    sourceLinksOfGroup.add(link);
                }
            }
            return sourceLinksOfGroup;
        }

        protected void add(CustomWinnerLink link) {
            customLinks.add(link);
            setWinnerOrder();
        }

        protected void set(List<CustomWinnerLink> links) {
            this.customLinks = links;
        }

        protected int size() {
            return customLinks.size();
        }

        protected CustomWinnerLink get(int index) {
            return customLinks.get(index);
        }

        protected void remove(int index) {
            customLinks.remove(index);
            setWinnerOrder();
        }

        protected List<CustomWinnerLink> getLinks() {
            return customLinks;
        }

        @Override
        public String toString() {
            String text = "";
            for (CustomWinnerLink link : customLinks) {
                text += link;
            }
            return text;
        }
    }

    protected void addLink(TGroup source, TGroup address) {
        if (source.getLevel() == address.getLevel() - 1) {
            int previousLinksNumber = getNumberOfSourcesOfLink(source);
            if (previousLinksNumber >= source.getMaxNumberOfWinners()) {
                removefirstSourceLink(source);
            }
            if (getNumberOfAddressesOfLink(address) > 1) {
                removefirstAddressLink(address);
            }
            links.add(source, address);
        }
    }

    protected int getNumberOfSourcesOfLink(TGroup from) {
        int number = 0;

        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getSourceGroup().equals(from)) {
                number++;
            }
        }
        return number;

    }

    protected int getNumberOfAddressesOfLink(TGroup to) {
        int number = 0;
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getAddressGroup().equals(to)) {
                number++;
            }
        }
        return number;

    }

    protected void removefirstSourceLink(TGroup from) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getSourceGroup().equals(from)) {
                links.remove(i);
                break;
            }
        }
    }

    protected void removefirstAddressLink(TGroup to) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getAddressGroup().equals(to)) {
                links.remove(i);
                break;
            }
        }
    }

    public boolean allGroupsHaveCustomLink() {
        try {
            for (int i = 0; i < tournamentGroups.size(); i++) {
                boolean found = false;

                for (int j = 0; j < links.size(); j++) {
                    if (links.get(j).getSourceGroup().equals(tournamentGroups.get(i))) {
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

    protected void removeLinksSelectedGroup(TGroup lastSelected) {
        try {
            CustomLinkPool.getInstance().remove(tournament, getIndexOfGroup(lastSelected));
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getSourceGroup().equals(lastSelected)) {
                    links.remove(i);
                    i--;
                }
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
    }

    protected void removeLinks() {
        for (int i = tournamentGroups.size() - 1; i >= 0; i--) {
            removeLinksSelectedGroup(tournamentGroups.get(i));
        }
    }
}
