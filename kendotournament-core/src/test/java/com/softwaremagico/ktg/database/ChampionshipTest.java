package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.TreeTournamentGroup;
import com.softwaremagico.ktg.tournament.TGroup;
import java.sql.SQLException;
import org.junit.After;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"championshipTest"}, dependsOnGroups = {"populateDatabase"})
public class ChampionshipTest {

    private static final Integer MEMBERS = 3;
    private static final Integer FIGHT_AREA = 0;
    private static final Integer TEAMS_PER_GROUP = 4;
    private static final String tournamentName = "championshipTest";
    private static Tournament tournament = null;

    @Test
    public void addTournament() throws SQLException {
        tournament = new Tournament(tournamentName, 1, 2, 3, TournamentType.CHAMPIONSHIP);
        TournamentPool.getInstance().add(tournament);
        Assert.assertTrue(TournamentPool.getInstance().getAll().size() == 1);
    }

    @Test(dependsOnMethods = {"addTournament"})
    public void addRoles() throws SQLException {
        for (RegisteredPerson competitor : RegisteredPersonPool.getInstance().getAll()) {
            RolePool.getInstance().add(tournament, new Role(tournament, competitor, RolePool.getInstance().getRoleTags().getRole("Competitor"), false, false));
        }
        Assert.assertTrue(RolePool.getInstance().get(tournament).size() == PopulateDatabase.clubs.length * PopulateDatabase.competitors.length);
    }

    @Test(dependsOnMethods = {"addRoles"})
    public void addTeams() throws SQLException {
        int teamIndex = 0;
        Team team = null;
        int teamMember = 0;
        while (TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).size() > 0) {
            //Create a new team.
            if (team == null) {
                teamIndex++;
                team = new Team("Team" + teamIndex, tournament);
                teamMember = 0;
                TeamPool.getInstance().add(tournament, team);
            }

            //Add member.
            RegisteredPerson member = TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).get(0);
            Assert.assertNotNull(member);
            team.setMember(member, teamMember, 0);
            teamMember++;

            //Team fill up, create a new team. 
            if (teamMember >= MEMBERS) {
                team = null;
            }
        }
        Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == RolePool.getInstance().getCompetitors(tournament).size() / MEMBERS);
    }

    @Test(dependsOnMethods = {"addTeams"})
    public void createTournamentGroups() throws SQLException {
        tournament.setHowManyTeamsOfGroupPassToTheTree(2);
        TGroup group = null;
        for (int i = 0; i < TeamPool.getInstance().get(tournament).size(); i++) {
            if (i % TEAMS_PER_GROUP == 0) {
                group = new TreeTournamentGroup(tournament, 0, 0);
                TournamentManagerFactory.getManager(tournament).addGroup(group);
            }
            if (group != null) {
                group.addTeam(TeamPool.getInstance().get(tournament).get(i));
            }
        }
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().size() == TeamPool.getInstance().get(tournament).size() / TEAMS_PER_GROUP);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().size() == TeamPool.getInstance().get(tournament).size() / TEAMS_PER_GROUP);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().size() == TeamPool.getInstance().get(tournament).size() / (2 * TEAMS_PER_GROUP));
        for (TGroup groupTest : TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups()) {
            Assert.assertTrue(groupTest.getTeams().size() == TEAMS_PER_GROUP);
        }
    }

    @Test(dependsOnMethods = {"createTournamentGroups"})
    public void createFights() throws SQLException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(0));
        Assert.assertTrue(FightPool.getInstance().get(tournament).size() == TeamPool.getInstance().get(tournament).size());
    }

    @Test(dependsOnMethods = {"createFights"})
    public void solveFirstLevel() throws SQLException {
        //Win first and second team of group.
        for (TGroup groupTest : TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups()) {
            groupTest.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
            groupTest.getFights().get(0).getDuels().get(1).setHit(true, 0, Score.MEN);
            groupTest.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.KOTE);
        }

        //finish fights.
        for (Fight fight : FightPool.getInstance().get(tournament)) {
            fight.setOver(true);
        }

        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(0);
        Ranking ranking1 = new Ranking(group1.getFights());
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team1")));
        Assert.assertTrue(ranking1.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team2")));

        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(1);
        Ranking ranking2 = new Ranking(group2.getFights());
        Assert.assertTrue(ranking2.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team5")));
        Assert.assertTrue(ranking2.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team6")));
    }

    @Test(dependsOnMethods = {"solveFirstLevel"})
    public void solveSecondLevel() throws SQLException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(1));

        //Check teams of group.
        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(0);
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team1")));
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team6")));
        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(1);
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team5")));
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team2")));

        //Add new points. Wins Team1 and Team2.
        group1.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
        Undraw undraw = new Undraw(tournament, 1, TeamPool.getInstance().get(tournament, "Team2"), 0, 1);
        UndrawPool.getInstance().add(tournament, undraw);
        
        //finish fights.
        for (Fight fight : FightPool.getInstance().get(tournament)) {
            fight.setOver(true);
        }
          
        Ranking ranking1 = new Ranking(group1.getFights());
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team1")));        
        Ranking ranking2 = new Ranking(group2.getFights());
        Assert.assertTrue(ranking2.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team2")));
        
    }

    @After
    @Test
    public void deleteTournament() throws SQLException {
        TournamentPool.getInstance().remove(tournamentName);
        Assert.assertTrue(TournamentPool.getInstance().getAll().isEmpty());
        Assert.assertTrue(FightPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(DuelPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(TeamPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(RolePool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(CustomLinkPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(UndrawPool.getInstance().get(tournament).isEmpty());
    }
}
