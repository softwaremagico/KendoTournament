package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

@Test(groups = {"databaseStore"})
public class DatabaseStore {

    private void checkTournament(Tournament tournament) throws SQLException {
        //Get copy of data.
        List<Role> roles = RolePool.getInstance().get(tournament);
        List<Team> teams = TeamPool.getInstance().get(tournament);
        List<Fight> fights = FightPool.getInstance().get(tournament);
        List<Duel> duels = DuelPool.getInstance().get(tournament);
        List<Undraw> undraws = UndrawPool.getInstance().get(tournament);
        List<TGroup> groups = TournamentManagerFactory.getManager(tournament).getGroups();

        //Store data into database.
        DatabaseConnection.getInstance().updateDatabase();

        //Delete data.
        RolePool.getInstance().reset(tournament);
        TeamPool.getInstance().reset(tournament);
        FightPool.getInstance().reset(tournament);
        DuelPool.getInstance().reset(tournament);
        UndrawPool.getInstance().reset(tournament);
        TournamentManagerFactory.resetManager(tournament);

        //Check database read
        Assert.assertFalse(RolePool.getInstance().get(tournament).isEmpty());
        Assert.assertFalse(TeamPool.getInstance().get(tournament).isEmpty());
        Assert.assertFalse(FightPool.getInstance().get(tournament).isEmpty());
        Assert.assertFalse(DuelPool.getInstance().get(tournament).isEmpty());
        Assert.assertFalse(TournamentManagerFactory.getManager(tournament).getGroups().isEmpty());

        //Compare data.       
        Assert.assertTrue(RolePool.getInstance().get(tournament).equals(roles));
        Assert.assertTrue(TeamPool.getInstance().get(tournament).equals(teams));
        Assert.assertTrue(TeamPool.getInstance().get(tournament).get(0).getNumberOfMembers(0) == tournament.getTeamSize());
        Assert.assertTrue(FightPool.getInstance().get(tournament).equals(fights));
        Assert.assertTrue(DuelPool.getInstance().get(tournament).equals(duels));
        Assert.assertTrue(UndrawPool.getInstance().get(tournament).equals(undraws));
        Assert.assertTrue(TournamentManagerFactory.getManager(tournament).getGroups().equals(groups));
    }

    @Test(dependsOnGroups = {"simpleChampionshipTest"})
    public void simpleChampionshipTest() throws SQLException {
        checkTournament(TournamentPool.getInstance().get(SimpleChampionshipTest.TOURNAMENT_NAME));
    }

    @Test(dependsOnGroups = {"championshipTest"})
    public void championshipTest() throws SQLException {
        checkTournament(TournamentPool.getInstance().get(ChampionshipTest.TOURNAMENT_NAME));
    }

    @Test(dependsOnGroups = {"customChampionshipTest"})
    public void customChampionshipTest() throws SQLException {
        checkTournament(TournamentPool.getInstance().get(CustomChampionshipTest.TOURNAMENT_NAME));
    }

    @Test(dependsOnGroups = {"championshipTreeTest"})
    public void championshipTreeTest() throws SQLException {
        checkTournament(TournamentPool.getInstance().get(ChampionshipTreeTest.TOURNAMENT_NAME));
    }

    @Test(dependsOnGroups = {"championshipFightAreaTest"})
    public void championshipFightAreaTest() throws SQLException {
        checkTournament(TournamentPool.getInstance().get(ChampionshipFightAreaTest.TOURNAMENT_NAME));
    }

    @Test(dependsOnGroups = {"evenGroupsChampionshipTest"})
    public void championshipEvenGroupsTest() throws SQLException {
        checkTournament(TournamentPool.getInstance().get(ChampionshipEvenGroupsTest.TOURNAMENT_NAME));
    }

    @Test(dependsOnMethods = {"championshipEvenGroupsTest", "championshipFightAreaTest", "championshipTreeTest", "customChampionshipTest", "championshipTest", "simpleChampionshipTest"})
    public void checkBasicStore() throws SQLException {
        List<RegisteredPerson> competitorsCheck = RegisteredPersonPool.getInstance().sort();
        List<Club> clubsCheck = ClubPool.getInstance().getAll();
        DatabaseConnection.getInstance().updateDatabase();
        RegisteredPersonPool.getInstance().reset();
        ClubPool.getInstance().reset();
        Assert.assertFalse(RegisteredPersonPool.getInstance().getAll().isEmpty());
        Assert.assertFalse(ClubPool.getInstance().getAll().isEmpty());
        Assert.assertTrue(ClubPool.getInstance().getAll().equals(clubsCheck));
        Assert.assertTrue(RegisteredPersonPool.getInstance().sort().equals(competitorsCheck));

    }

    @AfterSuite
    @Test(alwaysRun = true)
    public void clearDatabase() throws SQLException {
        //Delete Tournament Information.
        TournamentPool.getInstance().remove(TournamentPool.getInstance().getAll());

         //Delete elements from database.
         RegisteredPersonPool.getInstance().remove(RegisteredPersonPool.getInstance().getAll());
         ClubPool.getInstance().remove(ClubPool.getInstance().getAll());

         //Update database.
         DatabaseConnection.getInstance().updateDatabase();

         //Check that all data are deleted
         Assert.assertTrue(TournamentPool.getInstance().getAll().isEmpty());
         Assert.assertTrue(RegisteredPersonPool.getInstance().getAll().isEmpty());
         Assert.assertTrue(ClubPool.getInstance().getAll().isEmpty());
         Assert.assertTrue(RolePool.getInstance().getAll().isEmpty());
         Assert.assertTrue(TeamPool.getInstance().getAll().isEmpty());
         Assert.assertTrue(FightPool.getInstance().getAll().isEmpty());
         Assert.assertTrue(DuelPool.getInstance().getAll().isEmpty());
         Assert.assertTrue(UndrawPool.getInstance().getAll().isEmpty()); 
    }
}
