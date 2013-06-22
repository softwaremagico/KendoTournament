package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
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
    private static final Integer FIGHTS = 15;
    private static final String TOURNAMENT_NAME = "simpleChampionshipTest";
    private static Tournament tournament = null;

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
            if(teamMember>2){
                team=null;
            }
        }
        Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == RolePool.getInstance().getCompetitors(tournament).size() / MEMBERS);
    }

    @Test(dependsOnMethods = {"addTeams"})
    public void createFights() throws SQLException {
        ITournamentManager tournamentManager = TournamentManagerFactory.getManager(tournament, TournamentType.SIMPLE);
        System.out.println(tournamentManager);
        FightPool.getInstance().add(tournament, tournamentManager.createSortedFights(0));
        System.out.println("\n"+ FightPool.getInstance().get(tournament));
        Assert.assertTrue(FightPool.getInstance().get(tournament).size() == FIGHTS);
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
