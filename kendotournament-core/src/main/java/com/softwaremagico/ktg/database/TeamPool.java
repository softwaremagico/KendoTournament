package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TeamPool extends TournamentDependentPool<Team> {

    private static TeamPool instance;

    private TeamPool() {
    }

    public static TeamPool getInstance() {
        if (instance == null) {
            instance = new TeamPool();
        }
        return instance;
    }

    @Override
    protected String getId(Team element) {
        return element.getName();
    }

    @Override
    protected HashMap<String, Team> getFromDatabase(Tournament tournament) {
        DatabaseConnection.getInstance().connect();
        List<Team> teams = DatabaseConnection.getConnection().getDatabase().getTeams(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Team> hashMap = new HashMap<>();
        for (Team t : teams) {
            hashMap.put(getId(t), t);
        }
        return hashMap;
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Team, Team> elementsToUpdate) {
        if (elementsToUpdate.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().updateTeams(elementsToUpdate);
        }
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Team> elementsToStore) {
        if (elementsToStore.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().addTeams(elementsToStore);
        }
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Team> elementsToDelete) {
        if (elementsToDelete.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().removeTeams(elementsToDelete);
        }
    }

    @Override
    protected List<Team> sort(Tournament tournament) {
        List<Team> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    /**
     * Obtain the teams that participates in a fight of a level
     *
     * @param tournament
     * @param level
     */
    public List<Team> get(Tournament tournament, Integer level) {
        List<Team> results = new ArrayList<>();
        List<Fight> fights = FightPool.getInstance().getFromLevel(tournament, level);
        for (Fight fight : fights) {
            if (!results.contains(fight.getTeam1())) {
                results.add(fight.getTeam1());
            }
            if (!results.contains(fight.getTeam2())) {
                results.add(fight.getTeam2());
            }
        }
        return results;
    }

    public Team get(Tournament tournament, RegisteredPerson competitor) {
        for (Team team : getMap(tournament).values()) {
            if (team.isMember(competitor)) {
                return team;
            }
        }
        return null;
    }

    public void setIndividualTeams(Tournament tournament) {
        //Delete old teams of tournament.
        remove(tournament);

        //Create new teams with only one member.
        List<RegisteredPerson> competitors = RolePool.getInstance().getCompetitors(tournament);
        //MessageManager.translatedMessage(this.getClass().getName(), "oneTeamPerCompetitor", this.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
        for (RegisteredPerson competitor : competitors) {
            Team team = new Team(competitor.getSurnameName(), tournament);
            team.setMember(competitor, 0, 0);
            add(tournament, team);
        }
        tournament.setTeamSize(1);
    }

    public void removeTeamsGroup(Tournament tournament) {
        List<Team> teams = new ArrayList<>(getMap(tournament).values());
        for (Team team : teams) {
            team.setGroup(0);
            update(tournament, team, team);
        }
    }

    public List<RegisteredPerson> getCompetitorsWithoutTeam(Tournament tournament) {
        List<RegisteredPerson> competitors = RolePool.getInstance().getCompetitors(tournament);
        List<Team> teams = new ArrayList<>(getMap(tournament).values());
        for (Team team : teams) {
            for (RegisteredPerson teamIntegrator : team.getMembersOrder(0).values()) {
                competitors.remove(teamIntegrator);
            }
        }
        return competitors;
    }
}