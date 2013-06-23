package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.sql.SQLException;
import org.junit.After;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"simpleChampionshipTest"}, dependsOnGroups = {"populateDatabase"})
public class SimpleChampionshipTest {

    private static final Integer MEMBERS = 3;
    private static final Integer FIGHT_AREA = 0;
    private static final String TOURNAMENT_NAME = "simpleChampionshipTest";
    private static Tournament tournament = null;

    public static Integer getNumberOfCombats(Integer numberOfTeams) {
        return factorial(numberOfTeams) / (2 * factorial(numberOfTeams - 2));
    }

    private static Integer factorial(Integer n) {
        Integer total = 1;
        while (n > 1) {
            total = total * n;
            n--;
        }
        return total;
    }

    @Test
    public void addTournament() throws SQLException {
        tournament = new Tournament(TOURNAMENT_NAME, 1, 2, MEMBERS, TournamentType.CHAMPIONSHIP);
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
            if (teamMember > 2) {
                team = null;
            }
        }
        Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == RolePool.getInstance().getCompetitors(tournament).size() / MEMBERS);
    }

    @Test(dependsOnMethods = {"addTeams"})
    public void createFights() throws SQLException {
        ITournamentManager tournamentManager = TournamentManagerFactory.getManager(tournament, TournamentType.SIMPLE);
        FightPool.getInstance().add(tournament, tournamentManager.createSortedFights(0));
        Assert.assertTrue(FightPool.getInstance().get(tournament).size() == getNumberOfCombats(TeamPool.getInstance().get(tournament).size()));
        //Check than teams are not crossed. 
        for (int i = 0; i < FightPool.getInstance().get(tournament).size() - 1; i++) {
            Assert.assertFalse(FightPool.getInstance().get(tournament).get(i).getTeam1().equals(FightPool.getInstance().get(tournament).get(i+1).getTeam2()));
            Assert.assertFalse(FightPool.getInstance().get(tournament).get(i).getTeam2().equals(FightPool.getInstance().get(tournament).get(i+1).getTeam1()));
        }
    }

    @Test(dependsOnMethods = {"createFights"})
    public void testSimpleWinner() throws SQLException {
        while (!FightPool.getInstance().areAllOver(tournament)) {
            //First duel
            FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0).setHit(true, 0, Score.MEN);
            FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0).setHit(true, 1, Score.MEN);
            FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).setOver(true);
        }
        System.out.println("\n" + FightPool.getInstance().get(tournament));
        Ranking ranking = new Ranking(FightPool.getInstance().get(tournament, FIGHT_AREA));
        System.out.println(ranking.getTeamsScoreRanking());

        for (int i = 0; i < ranking.getTeamsScoreRanking().size() - 1; i++) {
            Assert.assertTrue(ranking.getTeamsScoreRanking().get(i).getWonFights() >= ranking.getTeamsScoreRanking().get(i + 1).getWonFights());
            Assert.assertTrue(ranking.getTeamsScoreRanking().get(i).getWonDuels() >= ranking.getTeamsScoreRanking().get(i + 1).getWonDuels());
            Assert.assertTrue(ranking.getTeamsScoreRanking().get(i).getHits() >= ranking.getTeamsScoreRanking().get(i + 1).getHits());
        }
    }

    @After
    @Test
    public void deleteTournament() throws SQLException {
        TournamentPool.getInstance().remove(TOURNAMENT_NAME);
        Assert.assertTrue(TournamentPool.getInstance().getAll().isEmpty());
        Assert.assertTrue(FightPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(DuelPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(TeamPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(RolePool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(CustomLinkPool.getInstance().get(tournament).isEmpty());
        Assert.assertTrue(UndrawPool.getInstance().get(tournament).isEmpty());
    }
}