package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.TeamPool;
import com.softwaremagico.ktg.tournament.level.LeagueLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple tournament is a tournament with only one group.
 */
public class SimpleTournamentManager implements ITournamentManager {

    Tournament tournament;
    TournamentGroup group;

    public SimpleTournamentManager(Tournament tournament) {
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
    public boolean allGroupsHaveNextLink() {
        return false;
    }

    @Override
    public void addLink(TournamentGroup source, TournamentGroup address) {
    }

    @Override
    public void removeLinks() {
    }

    @Override
    public void deleteTeams(Integer level) {
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
    public List<Fight> getRandomFights() {
        return getFights(true);
    }

    @Override
    public List<Fight> getSortedFights() {
        return getFights(false);
    }

    private List<Fight> getFights(boolean random) {
        if (group == null || group.getTeams().size() < 2) {
            return null;
        }
        List<Fight> fights = new ArrayList<>();
        RemainingFights remainingFights = new RemainingFights(group.getTeams());

        Team team1 = remainingFights.getTeamWithMoreAdversaries(random);
        Fight fight;
        while (remainingFights.remainFights()) {
            Team team2 = remainingFights.getNextAdversary(team1, random);
            //Team1 has no more adversaries. Use another one. 
            if (team2 == null) {
                team1 = remainingFights.getTeamWithMoreAdversaries(random);
                continue;
            }
            if (fights.size() % 2 == 0) {
                fight = new Fight(tournament, team1, team2, group.getFightArea(), group.getLevel());
            } else {
                fight = new Fight(tournament, team2, team1, group.getFightArea(), group.getLevel());
            }
            fights.add(fight);
            remainingFights.removeAdveresary(team1, team2);
            team1 = team2;
        }
        return fights;
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

    class RemainingFights {

        List<Team> teams;
        HashMap<Team, List<Team>> combination;

        RemainingFights(List<Team> teams) {
            this.teams = teams;
            Collections.sort(teams);
            combination = getAdversaries();
        }

        private HashMap<Team, List<Team>> getAdversaries() {
            HashMap<Team, List<Team>> combinations = new HashMap<>();
            for (int i = 0; i < teams.size(); i++) {
                List<Team> otherTeams = new ArrayList<>();
                combinations.put(teams.get(i), otherTeams);

                for (int j = 0; j < teams.size(); j++) {
                    if (i != j) {
                        otherTeams.add(teams.get(j));
                    }
                }
            }
            return combinations;
        }

        public Team getTeamWithMoreAdversaries(boolean random) {
            return getTeamWithMoreAdversaries(teams, random);
        }

        public Team getTeamWithMoreAdversaries(List<Team> teamGroup, boolean random) {
            Integer maxAdv = -1;
            //Get max Adversaries value:
            for (Team team : teamGroup) {
                if (combination.get(team).size() > maxAdv) {
                    maxAdv = combination.get(team).size();
                }
            }

            System.out.println(teamGroup.size() + "---" + maxAdv);
            //Select one of the teams with max adversaries
            List<Team> possibleAdversaries = new ArrayList<>();
            for (Team team : teamGroup) {
                if (combination.get(team).size() == maxAdv) {
                    //If no random, return the first one. 
                    if (!random) {
                        return team;
                    } else {
                        possibleAdversaries.add(team);
                    }
                }
            }

            if (possibleAdversaries.size() > 0) {
                return possibleAdversaries.get(new Random().nextInt(possibleAdversaries.size()));
            }
            return null;
        }

        public Team getNextAdversary(Team team, boolean random) {
            return getTeamWithMoreAdversaries(combination.get(team), random);
        }

        public void removeAdveresary(Team team, Team adversary) {
            combination.get(team).remove(adversary);
            combination.get(adversary).remove(team);
        }

        public boolean remainFights() {
            for (Team team : teams) {
                if (combination.get(team).size() > 0) {
                    return true;
                }
            }
            return false;
        }
    }
}
