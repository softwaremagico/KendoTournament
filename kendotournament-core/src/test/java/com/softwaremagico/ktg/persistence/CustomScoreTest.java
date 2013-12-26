package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.ScoreType;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentScore;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.TreeTournamentGroup;
import java.sql.SQLException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"customScoreTest"}, dependsOnGroups = {"populateDatabase"})
public class CustomScoreTest {

    private static final Integer MEMBERS = 3;
    private static final Integer TEAMS_PER_GROUP = 4;
    private static final Integer GROUPS = 2;
    private static final String TOURNAMENT_NAME = "customScoreChampionshipTest";
    private static final ScoreType SCORE_TYPE = ScoreType.CUSTOM;
    private static final int VICTORY_POINTS = 3;
    private static final int DRAW_POINTS = 1;
    private static Tournament tournament = null;

    @Test
    public void addTournament() throws SQLException {
        tournament = new Tournament(TOURNAMENT_NAME, 1, 2, 3, TournamentType.CHAMPIONSHIP);
        tournament.setTournamentScore(new TournamentScore(SCORE_TYPE, VICTORY_POINTS, DRAW_POINTS));
        TournamentPool.getInstance().add(tournament);
        Assert.assertTrue(TournamentPool.getInstance().get(TOURNAMENT_NAME) != null);
    }

    @Test(dependsOnMethods = {"addTournament"})
    public void addRoles() throws SQLException {
        for (RegisteredPerson competitor : RegisteredPersonPool.getInstance().getAll()) {
            RolePool.getInstance().add(
                    tournament,
                    new Role(tournament, competitor, RolePool.getInstance().getRoleTags().getRole("Competitor"), false,
                    false));
        }
        Assert.assertTrue(RolePool.getInstance().get(tournament).size() == PopulateDatabase.clubs.length
                * PopulateDatabase.competitors.length);
    }

    @Test(dependsOnMethods = {"addRoles"})
    public void addTeams() throws SQLException, TeamMemberOrderException {
        int teamIndex = 0;
        Team team = null;
        int teamMember = 0;
        while (TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).size() > 0) {
            // Create a new team.
            if (team == null) {
                teamIndex++;
                team = new Team("Team" + String.format("%02d", teamIndex), tournament);
                teamMember = 0;
                TeamPool.getInstance().add(tournament, team);
            }

            // Add member.
            RegisteredPerson member = TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).get(0);
            Assert.assertNotNull(member);
            team.setMember(member, teamMember);
            teamMember++;

            // Team fill up, create a new team.
            if (teamMember >= MEMBERS) {
                team = null;
            }
        }
        Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == RolePool.getInstance()
                .getCompetitors(tournament).size()
                / MEMBERS);
    }

    @Test(dependsOnMethods = {"addTeams"})
    public void createTournamentGroups() throws SQLException {
        tournament.setHowManyTeamsOfGroupPassToTheTree(2);
        TGroup group;
        for (int g = 0; g < GROUPS; g++) {
            group = new TreeTournamentGroup(tournament, 0, 0, g);
            TournamentManagerFactory.getManager(tournament).addGroup(group);
            for (int i = 0; i < TEAMS_PER_GROUP; i++) {
                group.addTeam(TeamPool.getInstance().get(tournament).get(g * TEAMS_PER_GROUP + i));
            }
        }
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().size() == GROUPS);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().size() == GROUPS);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().size() == GROUPS / 2);
        for (TGroup groupTest : TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups()) {
            Assert.assertTrue(groupTest.getTeams().size() == TEAMS_PER_GROUP);
        }
    }

    @Test(dependsOnMethods = {"createTournamentGroups"})
    public void createFights() throws SQLException, PersonalizedFightsException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(0));
        Assert.assertTrue(FightPool.getInstance().get(tournament).size() == GROUPS * TEAMS_PER_GROUP);
    }

    @Test(dependsOnMethods = {"createFights"})
    public void solveFirstLevel() throws SQLException {
        // Win first and second team of group.
        for (TGroup groupTest : TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups()) {
            groupTest.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
            groupTest.getFights().get(0).getDuels().get(1).setHit(true, 0, Score.MEN);
            groupTest.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.KOTE);
        }

        // finish fights.
        for (Fight fight : FightPool.getInstance().get(tournament)) {
            fight.setOver(true);
        }

        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(0);
        Ranking ranking1 = new Ranking(group1.getFights());
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team01")));
        Assert.assertTrue(ranking1.getTeam(3).equals(TeamPool.getInstance().get(tournament, "Team02")));

        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(1);
        Ranking ranking2 = new Ranking(group2.getFights());
        Assert.assertTrue(ranking2.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team05")));
        Assert.assertTrue(ranking2.getTeam(3).equals(TeamPool.getInstance().get(tournament, "Team06")));
    }

    @Test(dependsOnMethods = {"solveFirstLevel"})
    public void solveSecondLevel() throws SQLException, PersonalizedFightsException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(1));

        // Check teams of group.
        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(0);
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team01")));
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team07")));
        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(1);
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team05")));
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team03")));

        // Add new points. Wins Team1 and Team2.
        group1.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
        Undraw undraw = new Undraw(tournament, 1, TeamPool.getInstance().get(tournament, "Team03"), 0, 1);
        UndrawPool.getInstance().add(tournament, undraw);

        // finish fights.
        for (Fight fight : FightPool.getInstance().get(tournament)) {
            fight.setOver(true);
        }

        Ranking ranking1 = new Ranking(group1.getFights());
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team01")));
        Ranking ranking2 = new Ranking(group2.getFights());
        Assert.assertTrue(ranking2.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team03")));

    }

    @Test(dependsOnMethods = {"solveSecondLevel"})
    public void solveThirdLevel() throws SQLException, PersonalizedFightsException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(2));

        // Check teams of group.
        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().get(0);
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team01")));
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team03")));

        // Add new points. Wins Team2.
        group1.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);

        // finish fights.
        for (Fight fight : group1.getFights()) {
            fight.setOver(true);
        }

        Ranking ranking1 = new Ranking(group1.getFights());
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team03")));
    }
}
