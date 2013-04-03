package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.tournament.level.LeagueLevel;
import java.util.List;

/**
 * A simple tournament is a tournament with only one group.
 */
public class SimpleTournamentManager implements ITournamentManager {

    TournamentGroup group;

    @Override
    public List<Fight> nextFights() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getNumberOfLevels() {
        return 1;
    }

    @Override
    public List<TournamentGroup> getGroups(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Fight> getFights(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<TournamentGroup> getGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addGroup(TournamentGroup group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroup(Integer level, Integer groupIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroup(TournamentGroup group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroups(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LeagueLevel getLevel(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean exist(Team team) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean allGroupsHaveNextLink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addLink(TournamentGroup source, TournamentGroup address) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeLinks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteTeams(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultFightAreas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
