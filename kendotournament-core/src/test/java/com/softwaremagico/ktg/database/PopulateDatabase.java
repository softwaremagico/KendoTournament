package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.RegisteredPerson;
import java.sql.SQLException;
import java.text.DecimalFormat;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Test of access to database. Needs a user for connecting.
 */
@Test(groups = {"populateDatabase"})
public class PopulateDatabase {

    private static final String DATABASE_NAME = "kendotournament_test";
    private static final String DATABASE_USER = "kendouser";
    private static final String DATABASE_PASSWORD = "MenKoteDo";
    private static final String DATABASE_SERVER = "127.0.0.1";
    protected static final String[] clubs = {"clb1", "clb2", "clb3", "clb4", "clb5", "clb6"};
    protected static final String[] competitors = {"comp1", "comp2", "comp3", "comp4", "comp5", "comp6"};

    public DatabaseEngine getDatabaseTested() {
        return DatabaseEngine.MySQL;
    }

    @Test
    public void connectToDatabase() {
        DatabaseConnection.getInstance().setDatabase(getDatabaseTested().getDatabaseClass());
        DatabaseConnection.getInstance().setDatabaseEngine(getDatabaseTested().toString());
        DatabaseConnection.getInstance().setPassword(DATABASE_PASSWORD);
        DatabaseConnection.getInstance().setServer(DATABASE_SERVER);
        DatabaseConnection.getInstance().setUser(DATABASE_USER);
        DatabaseConnection.getInstance().setDatabaseName(DATABASE_NAME);
        /*Assert.assertTrue(DatabaseConnection.getInstance().testDatabaseConnection(DATABASE_PASSWORD, DATABASE_USER,
         DATABASE_NAME, DATABASE_SERVER));*/
    }

    @Test(dependsOnMethods = {"connectToDatabase"})
    public void addClubs() throws SQLException {
        for (String clubName : clubs) {
            Club club = new Club(clubName, "España", "Valencia");
            ClubPool.getInstance().add(club);
        }
        Assert.assertTrue(ClubPool.getInstance().getByName("clb").size() == clubs.length);
    }

    @Test(dependsOnMethods = {"addClubs"})
    public void addCompetitors() throws SQLException {
        int dni = 0;
        DecimalFormat myFormatter = new DecimalFormat("########");
        for (String clubName : clubs) {
            for (String competitorName : competitors) {
                RegisteredPersonPool.getInstance().add(new RegisteredPerson(myFormatter.format(dni), competitorName + clubName, competitorName + clubName));
                dni++;
            }
        }

        Assert.assertTrue(RegisteredPersonPool.getInstance().getAll().size() == clubs.length * competitors.length);
    }
}
