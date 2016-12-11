package com.softwaremagico.ktg.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.king.KingLevel;
import com.softwaremagico.ktg.tournament.king.KingOfTheMountainTournament;
import com.softwaremagico.ktg.tournament.score.ScoreType;
import com.softwaremagico.ktg.tournament.score.TournamentScore;

@Test(groups = { "kingOfTheMountain" }, dependsOnGroups = { "populateDatabase" })
public class KingOfTheMountainTest {
	private static final Integer MEMBERS = 3;
	private static final Integer TEAMS_PER_GROUP = 2;
	private static final Integer GROUPS = 1;
	public static final String TOURNAMENT_NAME = "kingOfTheMountainTest";
	private Tournament tournament = null;
	private KingOfTheMountainTournament kingOfTheMountainTournament;

	@Test
	public void addTournament() throws SQLException {
		tournament = new Tournament(TOURNAMENT_NAME, 1, 2, 1, TournamentType.KING_OF_THE_MOUNTAIN);
		tournament.setTournamentScore(new TournamentScore(ScoreType.WIN_OVER_DRAWS));
		TournamentPool.getInstance().add(tournament);
		Assert.assertTrue(TournamentPool.getInstance().get(TOURNAMENT_NAME) != null);
	}

	@Test(dependsOnMethods = { "addTournament" })
	public void addRoles() throws SQLException {
		for (RegisteredPerson competitor : RegisteredPersonPool.getInstance().getAll()) {
			RolePool.getInstance().add(tournament,
					new Role(tournament, competitor, RolePool.getInstance().getRoleTags().getRole("Competitor"), false, false));
		}
		Assert.assertTrue(RolePool.getInstance().get(tournament).size() == PopulateDatabase.clubs.length
				* PopulateDatabase.competitors.length);
	}

	@Test(dependsOnMethods = { "addRoles" })
	public void addTeams() throws SQLException, TeamMemberOrderException {
		int teamIndex = 0;
		Team team = null;
		int memberOrder = 0;
		while (TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).size() > 0) {
			// Create a new team.
			if (team == null) {
				teamIndex++;
				team = new Team("Team" + String.format("%02d", teamIndex), tournament);
				memberOrder = 0;
				TeamPool.getInstance().add(tournament, team);
			}

			// Add member.
			RegisteredPerson member = TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).get(0);
			Assert.assertNotNull(member);
			team.setMember(member, memberOrder);
			memberOrder++;

			// Team fill up, create a new team.
			if (memberOrder >= MEMBERS) {
				team = null;
			}
		}
		Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == RolePool.getInstance().getCompetitors(tournament).size()
				/ MEMBERS);
	}

	@Test(dependsOnMethods = { "addTeams" })
	public void setTournamentTeams() throws SQLException {
		tournament.setHowManyTeamsOfGroupPassToTheTree(1);
		kingOfTheMountainTournament = (KingOfTheMountainTournament) TournamentManagerFactory.getManager(tournament);

		List<Team> redTeams = new ArrayList<>();
		List<Team> whiteTeams = new ArrayList<>();

		// Split teams
		for (int i = 0; i < TeamPool.getInstance().get(tournament).size(); i++) {
			if (i % 2 == 0) {
				redTeams.add(TeamPool.getInstance().get(tournament).get(i));
			} else {
				whiteTeams.add(TeamPool.getInstance().get(tournament).get(i));
			}
		}

		kingOfTheMountainTournament.setRedTeams(redTeams);
		kingOfTheMountainTournament.setWhiteTeams(whiteTeams);
	}

	@Test(dependsOnMethods = { "setTournamentTeams" })
	public void setTournamentGroups() {
		// Group created automatically. Check if it is correct.
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(0).getGroups().size(), (int) GROUPS);

		for (TGroup groupTest : kingOfTheMountainTournament.getLevel(0).getGroups()) {
			Assert.assertEquals(groupTest.getTeams().size(), (int) TEAMS_PER_GROUP);
		}
	}

	@Test(dependsOnMethods = { "setTournamentGroups" })
	public void createFirstLevelFights() throws SQLException, PersonalizedFightsException {
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 0));
		Assert.assertEquals(FightPool.getInstance().get(tournament).size(), 1);
	}

	@Test(dependsOnMethods = { "createFirstLevelFights" })
	public void resolveFirstLevelFights() {
		// Wins red team.
		Fight fight = kingOfTheMountainTournament.getLevel(0).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.setOver(true);
		System.out.println("1-> " + fight);
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(0).getGroups().get(0).getWinners().get(0), kingOfTheMountainTournament
				.getRedTeams().get(0));
	}

	@Test(dependsOnMethods = { "resolveFirstLevelFights" })
	public void createSecondLevelFights() throws SQLException, PersonalizedFightsException {
		kingOfTheMountainTournament.createNextLevel();
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 1));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(1).getGroups().size(), 1);
		// Check red team is the same. White team is the next.		
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(1)).getCurrentRedTeam(), kingOfTheMountainTournament
				.getRedTeams().get(0));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(1)).getCurrentWhiteTeam(), kingOfTheMountainTournament
				.getWhiteTeams().get(1));
	}

	@Test(dependsOnMethods = { "createSecondLevelFights" })
	public void resolveSecondLevelFights() {
		// Wins red team.
		Fight fight = kingOfTheMountainTournament.getLevel(1).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.setOver(true);
		System.out.println("2-> " + fight);
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(1).getGroups().get(0).getWinners().get(0), kingOfTheMountainTournament
				.getRedTeams().get(0));
	}

	@Test(dependsOnMethods = { "resolveSecondLevelFights" })
	public void createThirdLevelFights() throws SQLException, PersonalizedFightsException {
		kingOfTheMountainTournament.createNextLevel();
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 2));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(2).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(2)).getCurrentRedTeam(), kingOfTheMountainTournament
				.getRedTeams().get(0));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(2)).getCurrentWhiteTeam(), kingOfTheMountainTournament
				.getWhiteTeams().get(2));
	}

	@Test(dependsOnMethods = { "createThirdLevelFights" })
	public void resolveThirdLevelFights() {
		// Wins red team.
		Fight fight = kingOfTheMountainTournament.getLevel(2).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(false, 0, Score.MEN);
		fight.setOver(true);
		System.out.println(fight);
		System.out.println(kingOfTheMountainTournament.getLevel(2).getGroups().get(0).getWinners());
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(2).getGroups().get(0).getWinners().get(0), kingOfTheMountainTournament
				.getWhiteTeams().get(2));
	}

	@Test(dependsOnMethods = { "resolveThirdLevelFights" })
	public void createFourthLevelFights() throws SQLException, PersonalizedFightsException {
		kingOfTheMountainTournament.createNextLevel();
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 3));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(3).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(3)).getCurrentRedTeam(), kingOfTheMountainTournament
				.getRedTeams().get(1));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(3)).getCurrentWhiteTeam(), kingOfTheMountainTournament
				.getWhiteTeams().get(2));
	}
}
