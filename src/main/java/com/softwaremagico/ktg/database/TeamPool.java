package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.RegisteredPerson;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
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
        List<Team> teams = DatabaseConnection.getConnection().getDatabase().getTeams(tournament);
        HashMap<String, Team> hashMap = new HashMap<>();
        for (Team t : teams) {
            hashMap.put(getId(t), t);
        }
        return hashMap;
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Team, Team> elementsToExchange) {
        DatabaseConnection.getConnection().getDatabase().updateTeams(elementsToExchange);
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Team> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addTeams(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Team> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeTeams(elementsToDelete);
    }

    @Override
    protected List<Team> sort(Tournament tournament) {
        List<Team> unsorted = new ArrayList(get(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    /**
     * Obtain the teams that participates in a fight of a level
     *
     * @param tournament
     * @param level
     */
    public void getTeamsByLevel(Tournament tournament, Integer level) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Team get(Tournament tournament, RegisteredPerson competitor) {
        for (Team team : get(tournament).values()) {
            if (team.isMember(competitor)) {
                return team;
            }
        }
        return null;
    }

    public void setIndividualTeams(Tournament tournament) {
        List<RegisteredPerson> competitors = RolePool.getInstance().getCompetitors(tournament);
        //MessageManager.translatedMessage(this.getClass().getName(), "oneTeamPerCompetitor", this.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
        for (RegisteredPerson competitor : competitors) {
            Team team = new Team(competitor.getSurnameName(), tournament);
            team.setMember(competitor, 0, 0);
            add(tournament, team);
        }

    }

    public void deleteTeamsGroup(Tournament tournament) {
        List<Team> teams = new ArrayList<>(get(tournament).values());
        for (Team team : teams) {
            team.setGroup(0);
            update(tournament, team, team);
        }
    }

    public List<RegisteredPerson> getCompetitorsWithoutTeam(Tournament tournament) {
        List<RegisteredPerson> competitors = RolePool.getInstance().getCompetitors(tournament);
        List<Team> teams = new ArrayList<>(get(tournament).values());
        for (Team team : teams) {
            for (RegisteredPerson teamIntegrator : team.getMembersOrder(0).values()) {
                competitors.remove(teamIntegrator);
            }
        }
        return competitors;
    }
}
