package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import java.util.ArrayList;
import java.util.List;

public class Championship implements ITournamentManager {

    private Tournament tournament;
    protected LeagueLevel levelZero;

    protected Championship(Tournament tournament) {
        this.tournament = tournament;
        levelZero = new LeagueLevelChampionship(tournament, 0, null, null);
    }
    
    @Override
    public void fillGroups(){
        List<Fight> fights = FightPool.getInstance().get(tournament);     
        for(Fight fight : fights){
            LeagueLevel leagueLevel = getLevel(fight.getLevel());
            leagueLevel.fillGroups(fight);
        }
    }
    
    @Override
    public List<Fight> getFights(Integer level) {
        return FightPool.getInstance().getFromLevel(tournament, level);
    }

    @Override
    public List<Fight> createRandomFights(Integer level) {
        return createFightsOfGroups(level, true);
    }

    @Override
    public List<Fight> createSortedFights(Integer level) {
        return createFightsOfGroups(level, false);
    }

    private List<Fight> createFightsOfGroups(Integer level, boolean random) {
        List<Fight> fights = new ArrayList<>();
        List<TournamentGroup> groupsOfLevel = getGroups(level);
        for (int i = 0; i < groupsOfLevel.size(); i++) {
            fights.addAll(groupsOfLevel.get(i).createFights(random, i));
        }
        return fights;
    }

    @Override
    public List<TournamentGroup> getGroups() {
        List<TournamentGroup> allGroups = new ArrayList<>();
        for (int i = 0; i < getNumberOfLevels(); i++) {
            allGroups.addAll(getLevel(i).getGroups());
        }
        return allGroups;
    }

    @Override
    public List<TournamentGroup> getGroups(Integer level) {
        return getLevel(level).getGroups();
    }

    @Override
    public TournamentGroup getGroup(Fight fight) {
        for (TournamentGroup group : getGroups()) {
            if (group.isFightOfGroup(fight)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void addGroup(TournamentGroup group) {
        getLevel(0).addGroup(group);
    }

    @Override
    public void removeGroup(Integer level, Integer groupIndex) {
        getLevel(level).removeGroup(getGroups(level).get(groupIndex));
    }

    @Override
    public void removeGroup(TournamentGroup group) {
        getLevel(group.getLevel()).getGroups().remove(group);
    }

    @Override
    public void removeGroups(Integer level) {
        getLevel(level).removeGroups();
    }

    @Override
    public LeagueLevel getLevel(Integer levelIndex) {
        LeagueLevel leagueLevel = levelZero;
        while (levelIndex > 0) {
            levelIndex--;
            leagueLevel = leagueLevel.nextLevel;
        }
        return leagueLevel;
    }

    @Override
    public Integer getNumberOfLevels() {
        Integer total = 1; //Always exist level zero. 
        LeagueLevel level = levelZero.nextLevel;
        while (level != null) {
            total++;
            level = level.nextLevel;
        }
        return total;
    }

    @Override
    public Integer getLastLevelUsed() {
        for (int i = 0; i < getNumberOfLevels(); i++) {
            if (getLevel(i).isLevelFinished()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean exist(Team team) {
        List<TournamentGroup> groups = getGroups(0);
        for (TournamentGroup group : groups) {
            if (group.getTeams().contains(team)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeTeams(Integer level) {
        getLevel(level).removeTeams();
    }

    @Override
    public void setDefaultFightAreas() {
        getLevel(0).updateArenaOfGroups();
    }

    @Override
    public void setHowManyTeamsOfGroupPassToTheTree(Integer winners) {
        tournament.setHowManyTeamsOfGroupPassToTheTree(winners);
        for (TournamentGroup group : levelZero.getGroups()) {
            group.setMaxNumberOfWinners(winners);
        }
        if (levelZero.nextLevel != null) {
            levelZero.nextLevel.updateGroupsSize();
        }
    }
}
