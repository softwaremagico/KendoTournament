package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Assert;
import org.testng.annotations.Test;

@Test(groups = {"championshipTest"}, dependsOnGroups = {"populateDatabase"})
public class championshipTest {

    private static final String tournamentName = "championshipTest";
    private static Tournament tournament = null;
    private static final String[] teamsName = {"team1", "team2"};

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
        Assert.assertTrue(RolePool.getInstance().get(tournament).size() == populateDatabase.clubs.length * populateDatabase.competitors.length);
    }

    @Test(dependsOnMethods = {"addRoles"})
    public void addTeams() {
        for (String teamName : teamsName) {
        }
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
