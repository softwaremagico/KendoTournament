package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.TreeTournamentGroup;
import java.sql.SQLException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"championshipFightAreaTest"}, dependsOnGroups = {"populateDatabase"})
public class ChampionshipFightAreaTest {

    private static final Integer MEMBERS = 3;
    private static final Integer TEAMS_PER_GROUP = 4;
    private static final Integer GROUPS = 4;
    public static final String TOURNAMENT_NAME = "fightAreaChampionshipTest";
    private static Tournament tournament = null;

    @Test
    public void addTournament() throws SQLException {
        tournament = new Tournament(TOURNAMENT_NAME, 1, 2, 3, TournamentType.CHAMPIONSHIP);
        TournamentPool.getInstance().add(tournament);
        Assert.assertTrue(TournamentPool.getInstance().get(TOURNAMENT_NAME) != null);
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
                team = new Team("Team" + String.format("%02d", teamIndex), tournament);
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
        TGroup group;
        for (int g = 0; g < GROUPS; g++) {
            int fightArea = 0;
            if (g > 1) {
                fightArea = 1;
            }
            group = new TreeTournamentGroup(tournament, 0, fightArea);
            TournamentManagerFactory.getManager(tournament).addGroup(group);
            for (int i = 0; i < TEAMS_PER_GROUP; i++) {
                group.addTeam(TeamPool.getInstance().get(tournament).get(g * TEAMS_PER_GROUP + i));
            }
        }
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().size() == GROUPS);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().size() == GROUPS);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().size() == 2);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(3).getGroups().size() == 1);
        for (TGroup groupTest : TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups()) {
            Assert.assertTrue(groupTest.getTeams().size() == TEAMS_PER_GROUP);
        }
        
        //Test fights area.
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(0).getFightArea()==0);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(1).getFightArea()==0);
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(2).getFightArea()==1);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(3).getFightArea()==1);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(0).getFightArea()==0);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(1).getFightArea()==0);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(2).getFightArea()==1);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(3).getFightArea()==1);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().get(0).getFightArea()==0);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().get(1).getFightArea()==1);
                Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getLevel(3).getGroups().get(0).getFightArea()==0);
        
    }

    @Test(dependsOnMethods = {"createTournamentGroups"})
    public void createFights() throws SQLException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(0));
        Assert.assertTrue(FightPool.getInstance().get(tournament).size() == GROUPS * TEAMS_PER_GROUP);
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
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team01")));
        Assert.assertTrue(ranking1.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team02")));

        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(1);
        Ranking ranking2 = new Ranking(group2.getFights());
        Assert.assertTrue(ranking2.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team05")));
        Assert.assertTrue(ranking2.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team06")));

        TGroup group3 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(2);
        Ranking ranking3 = new Ranking(group3.getFights());
        Assert.assertTrue(ranking3.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team09")));
        Assert.assertTrue(ranking3.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team10")));
        
        TGroup group4 = TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(3);
        Ranking ranking4 = new Ranking(group4.getFights());
        Assert.assertTrue(ranking4.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team13")));
        Assert.assertTrue(ranking4.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team14")));
        
        //Check fights areas.
        for(Fight fight: group1.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==0);
        }
        for(Fight fight: group2.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==0);
        }
        for(Fight fight: group3.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==1);
        }
        for(Fight fight: group4.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==1);
        }

    }

    @Test(dependsOnMethods = {"solveFirstLevel"})
    public void solveSecondLevel() throws SQLException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(1));

        //Check teams of group.
        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(0);
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team01")));
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team14")));
        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(1);
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team05")));
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team10")));
        TGroup group3 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(2);
        Assert.assertTrue(group3.getTeams().contains(TeamPool.getInstance().get(tournament, "Team09")));
        Assert.assertTrue(group3.getTeams().contains(TeamPool.getInstance().get(tournament, "Team06")));
        TGroup group4 = TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(3);
        Assert.assertTrue(group4.getTeams().contains(TeamPool.getInstance().get(tournament, "Team13")));
        Assert.assertTrue(group4.getTeams().contains(TeamPool.getInstance().get(tournament, "Team02")));

        //Check fights areas.
        for(Fight fight: group1.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==0);
        }
        for(Fight fight: group2.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==0);
        }
        for(Fight fight: group3.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==1);
        }
        for(Fight fight: group4.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==1);
        }

        //Add new points. Wins Team1, Team5, Team9, Team13.
        group1.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
        group1.getFights().get(0).getDuels().get(0).setHit(true, 1, Score.MEN);
        group1.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);
        group2.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
        group2.getFights().get(0).getDuels().get(0).setHit(true, 1, Score.MEN);
        group2.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);
        group3.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
        group3.getFights().get(0).getDuels().get(0).setHit(true, 1, Score.MEN);
        group3.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);
        group4.getFights().get(0).getDuels().get(0).setHit(true, 0, Score.MEN);
        group4.getFights().get(0).getDuels().get(0).setHit(true, 1, Score.MEN);
        group4.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);


        //finish fights.
        for (Fight fight : FightPool.getInstance().get(tournament)) {
            fight.setOver(true);
        }

        Ranking ranking1 = new Ranking(group1.getFights());
        Assert.assertTrue(ranking1.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team01")));
        Ranking ranking2 = new Ranking(group2.getFights());
        Assert.assertTrue(ranking2.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team05")));
        Ranking ranking3 = new Ranking(group3.getFights());
        Assert.assertTrue(ranking3.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team09")));
        Ranking ranking4 = new Ranking(group4.getFights());
        Assert.assertTrue(ranking4.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team13")));

    }

    @Test(dependsOnMethods = {"solveSecondLevel"})
    public void solveThirdLevel() throws SQLException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(2));

        //Check teams of group.
        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().get(0);
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team01")));

        TGroup group2 = TournamentManagerFactory.getManager(tournament).getLevel(2).getGroups().get(1);
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team09")));
        Assert.assertTrue(group2.getTeams().contains(TeamPool.getInstance().get(tournament, "Team05")));

        //Add new points. Wins Team9.
        group2.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);

        //finish fights.
        for (Fight fight : group1.getFights()) {
            fight.setOver(true);
        }
        
        //Check fights areas.
        for(Fight fight: group1.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==0);
        }
        for(Fight fight: group2.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==1);
        }

        Ranking ranking = new Ranking(group2.getFights());
        Assert.assertTrue(ranking.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team09")));
    }

    @Test(dependsOnMethods = {"solveThirdLevel"})
    public void solveFourthLevel() throws SQLException {
        FightPool.getInstance().add(tournament, TournamentManagerFactory.getManager(tournament).createSortedFights(3));

        //Check teams of group.
        TGroup group1 = TournamentManagerFactory.getManager(tournament).getLevel(3).getGroups().get(0);
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team01")));
        Assert.assertTrue(group1.getTeams().contains(TeamPool.getInstance().get(tournament, "Team09")));

        //Add new points. Wins Team9.
        group1.getFights().get(0).getDuels().get(0).setHit(false, 0, Score.MEN);

        //finish fights.
        for (Fight fight : group1.getFights()) {
            fight.setOver(true);
        }
        
        //Check fights areas.
        for(Fight fight: group1.getFights()){
            Assert.assertTrue(fight.getAsignedFightArea()==0);
        }

        Ranking ranking = new Ranking(group1.getFights());
        Assert.assertTrue(ranking.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team09")));
    }

    /*@After
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
    }*/
}