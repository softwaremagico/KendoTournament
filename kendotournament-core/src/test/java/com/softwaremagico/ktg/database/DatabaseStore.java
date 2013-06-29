package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.RegisteredPerson;
import java.sql.SQLException;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.testng.annotations.Test;

@Test(groups = {"databaseStore"})
public class DatabaseStore {

    @Test(dependsOnGroups = {"simpleChampionshipTest"})
    public void simpleChampionshipTest() throws SQLException {
    }

    @Test(dependsOnGroups = {"championshipTest"})
    public void championshipTest() throws SQLException {
    }

    @Test(dependsOnGroups = {"customChampionshipTest"})
    public void customChampionshipTest() throws SQLException {
    }

    @Test(dependsOnGroups = {"championshipTreeTest"})
    public void championshipTreeTest() throws SQLException {
    }

    @Test(dependsOnGroups = {"championshipFightAreaTest"})
    public void championshipFightAreaTest() throws SQLException {
    }

    @Test(dependsOnGroups = {"evenGroupsChampionshipTest"})
    public void championshipEvenGroupsTest() throws SQLException {
    }

    @After
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

    @After
    @Test(dependsOnMethods = {"checkBasicStore"})
    public void clearDatabase() throws SQLException {
        //Delete Tournament Information.
        TournamentPool.getInstance().remove(TournamentPool.getInstance().getAll());

        //Delete elements from database.
        RegisteredPersonPool.getInstance().remove(RegisteredPersonPool.getInstance().getAll());
        ClubPool.getInstance().remove(ClubPool.getInstance().getAll());

        DatabaseConnection.getInstance().updateDatabase();
    }
}
