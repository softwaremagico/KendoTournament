package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.RegisteredPerson;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.testng.annotations.Test;

/**
 * Access test to the database. MySQL needs a user for connecting.
 */
@Test(groups = {"populateDatabase"})
public class PopulateDatabase {

    private static final String DATABASE_NAME = "kendotournament_test";
    private static final String DATABASE_USER = "kendouser";
    private static final String DATABASE_PASSWORD = "MenKoteDo";
    private static final String DATABASE_SERVER = "127.0.0.1";
    protected static final String[] clubs = {"Clb1", "Clb2", "Clb3", "Clb4", "Clb5", "Clb6", "Clb7", "Clb8"};
    protected static final String[] competitors = {"comp1", "comp2", "comp3", "comp4", "comp5", "comp6"};

    public DatabaseEngine getDatabaseTested() {
        return DatabaseEngine.SQLite;
    }

    /**
     * Deletes any previously stored information. 
     */
    private void resetDatabase() {
        try {
            DatabaseConnection.getInstance().connect();
            DatabaseConnection.getInstance().getDatabase().clearDatabase();
            DatabaseConnection.getInstance().disconnect();
        } catch (SQLException ex) {
        }
    }

    @Before
    @Test
    public void connectToDatabase() {
        DatabaseConnection.getInstance().setDatabase(getDatabaseTested().getDatabaseClass());
        DatabaseConnection.getInstance().setDatabaseEngine(getDatabaseTested().toString());
        DatabaseConnection.getInstance().setPassword(DATABASE_PASSWORD);
        DatabaseConnection.getInstance().setServer(DATABASE_SERVER);
        DatabaseConnection.getInstance().setUser(DATABASE_USER);
        DatabaseConnection.getInstance().setDatabaseName(DATABASE_NAME);
        //Create database if not exists.
        try {
            DatabaseConnection.getInstance().testDatabaseConnection(DATABASE_USER, DATABASE_PASSWORD, DATABASE_NAME, DATABASE_SERVER, false);
        } catch (SQLException ex) {
            Logger.getLogger(PopulateDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        resetDatabase();
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
        int dni = 1;
        for (String clubName : clubs) {
            for (String competitorName : competitors) {
                RegisteredPerson competitor = new RegisteredPerson(String.format("%08d", dni), competitorName + clubName, competitorName + clubName);
                RegisteredPersonPool.getInstance().add(competitor);
                competitor.setClub(ClubPool.getInstance().get(clubName));
                Assert.assertNotNull(competitor.getClub());
                dni++;
            }
        }
        Assert.assertTrue(RegisteredPersonPool.getInstance().getAll().size() == clubs.length * competitors.length);
    }
}
