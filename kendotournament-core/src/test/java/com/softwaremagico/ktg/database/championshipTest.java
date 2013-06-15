package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TournamentType;
import org.testng.annotations.Test;

@Test(groups = {"championshipTest"}, dependsOnGroups={"populateDatabase"})
public class championshipTest {

    private static final String tournament = "championshipTest";
    
    @Test
    public void addTournament() {
        TournamentPool.getInstance().add(new Tournament(tournament, 1, 2, 3, TournamentType.CHAMPIONSHIP));
    }

    @Test(dependsOnMethods = {"addTournament"})
    public void addRoles() {
    }

    @Test(dependsOnMethods = {"addRoles"})
    public void addTeams() {
    }
}
