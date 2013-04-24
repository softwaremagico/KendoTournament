package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.TeamPool;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple tournament is a tournament with only one group.
 */
public class SimpleTournamentManager implements ITournamentManager {

    Tournament tournament;
    TournamentGroup group;

    protected SimpleTournamentManager(Tournament tournament) {
        this.tournament = tournament;
        addGroup();
    }

    @Override
    public Integer getNumberOfLevels() {
        return 1;
    }

    @Override
    public List<TournamentGroup> getGroups(Integer level) {
        if (level == 0) {
            List<TournamentGroup> groups = new ArrayList<>();
            groups.add(group);
            return groups;
        }
        return null;
    }

    @Override
    public List<Fight> getFights(Integer level) {
        if (level == 0) {
            FightPool.getInstance().get(tournament);
        }
        return null;
    }

    @Override
    public List<TournamentGroup> getGroups() {
        List<TournamentGroup> groups = new ArrayList<>();
        groups.add(group);
        return groups;
    }

    @Override
    public void addGroup(TournamentGroup group) {
        this.group = group;
    }

    public final void addGroup() {
        this.group = new TournamentGroup(tournament, 0, 0);
        group.addTeams(TeamPool.getInstance().get(tournament));
    }

    @Override
    public void removeGroup(Integer level, Integer groupIndex) {
        if (level == 0 && groupIndex == 0) {
            group = null;
        }
    }

    @Override
    public void removeGroup(TournamentGroup group) {
        if (this.group.equals(group)) {
            this.group = null;
        }
    }

    @Override
    public void removeGroups(Integer level) {
        if (level == 0) {
        }
    }

    @Override
    public LeagueLevel getLevel(Integer level) {
        return null;
    }

    @Override
    public boolean exist(Team team) {
        if (group != null) {
            return group.getTeams().contains(team);
        }
        return false;
    }

    @Override
    public void removeTeams(Integer level) {
        if (level == 0 && group != null) {
            List<Team> teams = new ArrayList<>();
            group.setTeams(teams);
        }
    }

    @Override
    public void setDefaultFightAreas() {
        if (group != null) {
            group.setFightArea(0);
        }
    }

    @Override
    public List<Fight> createRandomFights(Integer level) {
        if (group == null || level != 0) {
            return null;
        }
        return group.createFights(true);
    }

    @Override
    public List<Fight> createSortedFights(Integer level) {
        if (group == null || level != 0) {
            return null;
        }
        return group.createFights(false);
    }

    @Override
    public TournamentGroup getGroup(Fight fight) {
        if (group != null) {
            if (group.isFightOfGroup(fight)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public Integer getLastLevelUsed() {
        return 0;
    }
}
