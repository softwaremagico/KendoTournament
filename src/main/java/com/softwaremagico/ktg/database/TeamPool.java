package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TeamPool extends Pool<Team> {

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
            hashMap.put(t.getName(), t);
        }
        return hashMap;
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Team, Team> elementsToExchange) {
        DatabaseConnection.getConnection().getDatabase().updateTeams(elementsToExchange);
    }

    @Override
    protected void storeInDatabase(List<Team> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addTeams(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(List<Team> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeTeams(elementsToDelete);
    }

    @Override
    protected List<Team> sort(Tournament tournament) {
        List<Team> unsortedTeams = new ArrayList(get(tournament).values());
        Collections.sort(unsortedTeams);
        return unsortedTeams;
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

    public Team get(Tournament tournament, Competitor competitor) {
        for (Team team : get(tournament).values()) {
            if (team.isMember(competitor)) {
                return team;
            }
        }
        return null;
    }

    public void setIndividualTeams(Tournament tournament) {
        List<Competitor> competitors = selectAllCompetitorsInTournament(tournament);
        //MessageManager.translatedMessage(this.getClass().getName(), "oneTeamPerCompetitor", this.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
        for (Competitor competitor : competitors) {
            Team team = new Team(competitor.getSurnameName(), tournament);
            team.addOneMember(competitor, 0);
            add(tournament, team);
        }

    }
}
