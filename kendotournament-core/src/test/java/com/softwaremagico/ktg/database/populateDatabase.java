package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Club;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Test of access to database. Needs a user for connecting.
 */
@Test(groups = {"cipherTest"})
public class populateDatabase {

    private static final String DATABASE_NAME = "kendotournament";
    private static final String DATABASE_USER = "kendouser";
    private static final String DATABASE_PASSWORD = "MenKoteDo";
    private static final String DATABASE_SERVER = "127.0.0.1";
    private static String[] clubs = {"testClub1", "testClub2", "testClub2"};

    public DatabaseEngine getDatabaseTested() {
        return DatabaseEngine.MySQL;
    }

    @Test(groups = {"createData"})
    public void connectToDatabase() {
        DatabaseConnection.getInstance().setDatabaseEngine(getDatabaseTested().toString());
        Assert.assertTrue(DatabaseConnection.getInstance().testDatabaseConnection(DATABASE_PASSWORD, DATABASE_USER,
                DATABASE_NAME, DATABASE_SERVER));
    }

    @Test(groups = {"createData"})
    public void addClubs() {
        for (String clubName : clubs) {
            Club club = new Club(clubName, "España", "Valencia");
            
        }
    }

    @Test(groups = {"createData"}, dependsOnMethods = {"addClubs"})
    public void addCompetitors() {
    }

    @Test(groups = {"createData"})
    public void addTournament() {
    }

    @Test(groups = {"createData"}, dependsOnMethods = {"addCompetitors", "addTournament"})
    public void addRoles() {
    }

    @Test(groups = {"createData"}, dependsOnMethods = {"addRoles"})
    public void addTeams() {
    }
}
