package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.List;

public class Championship implements ITournamentManager {

    Tournament tournament;

    protected Championship(Tournament tournament) {
        this.tournament = tournament;
    }

    @Override
    public List<Fight> getFights(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Fight> getRandomFights() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Fight> getSortedFights() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<TournamentGroup> getGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<TournamentGroup> getGroups(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TournamentGroup getGroup(Fight fight) {
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
    public Integer getNumberOfLevels() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getLastLevelUsed() {
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
