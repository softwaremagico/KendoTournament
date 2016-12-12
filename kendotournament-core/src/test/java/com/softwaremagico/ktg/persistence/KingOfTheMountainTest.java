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
import com.softwaremagico.ktg.tournament.king.DrawResolution;
import com.softwaremagico.ktg.tournament.king.KingLevel;
import com.softwaremagico.ktg.tournament.king.KingOfTheMountainTournament;
import com.softwaremagico.ktg.tournament.king.TournamentFinishedException;
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
			RolePool.getInstance().add(
					tournament,
					new Role(tournament, competitor, RolePool.getInstance().getRoleTags()
							.getRole("Competitor"), false, false));
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
		Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == RolePool.getInstance()
				.getCompetitors(tournament).size()
				/ MEMBERS);
	}

	@Test(dependsOnMethods = { "addTeams" })
	public void setTournamentTeams() throws SQLException {
		tournament.setHowManyTeamsOfGroupPassToTheTree(1);
		kingOfTheMountainTournament = (KingOfTheMountainTournament) TournamentManagerFactory
				.getManager(tournament);

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
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(0).getGroups().get(0).getWinners().get(0),
				kingOfTheMountainTournament.getRedTeams().get(0));
	}

	@Test(dependsOnMethods = { "resolveFirstLevelFights" })
	public void createSecondLevelFights() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		kingOfTheMountainTournament.createNextLevel();
		Assert.assertEquals((int)kingOfTheMountainTournament.getNumberOfLevels(), 2);
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 1));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(1).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(1)).getCurrentRedTeam(),
				kingOfTheMountainTournament.getRedTeams().get(0));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(1)).getCurrentWhiteTeam(),
				kingOfTheMountainTournament.getWhiteTeams().get(1));
	}

	@Test(dependsOnMethods = { "createSecondLevelFights" })
	public void resolveSecondLevelFights() {
		// Wins red team.
		Fight fight = kingOfTheMountainTournament.getLevel(1).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.setOver(true);
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(1).getGroups().get(0).getWinners().get(0),
				kingOfTheMountainTournament.getRedTeams().get(0));
	}

	@Test(dependsOnMethods = { "resolveSecondLevelFights" })
	public void createThirdLevelFights() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		kingOfTheMountainTournament.createNextLevel();
		Assert.assertEquals((int)kingOfTheMountainTournament.getNumberOfLevels(), 3);
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 2));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(2).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(2)).getCurrentRedTeam(),
				kingOfTheMountainTournament.getRedTeams().get(0));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(2)).getCurrentWhiteTeam(),
				kingOfTheMountainTournament.getWhiteTeams().get(2));
	}

	@Test(dependsOnMethods = { "createThirdLevelFights" })
	public void resolveThirdLevelFights() {
		// Wins red team.
		Fight fight = kingOfTheMountainTournament.getLevel(2).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(false, 0, Score.MEN);
		fight.setOver(true);
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(2).getGroups().get(0).getWinners().get(0),
				kingOfTheMountainTournament.getWhiteTeams().get(2));
	}

	@Test(dependsOnMethods = { "resolveThirdLevelFights" })
	public void createFourthLevelFights() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		kingOfTheMountainTournament.createNextLevel();
		Assert.assertEquals((int)kingOfTheMountainTournament.getNumberOfLevels(), 4);
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 3));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(3).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(3)).getCurrentRedTeam(),
				kingOfTheMountainTournament.getRedTeams().get(1));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(3)).getCurrentWhiteTeam(),
				kingOfTheMountainTournament.getWhiteTeams().get(2));
	}

	@Test(dependsOnMethods = { "createFourthLevelFights" })
	public void resolveFourthLevelFights() {
		// Wins red team.
		Fight fight = kingOfTheMountainTournament.getLevel(3).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.setOver(true);
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(3).getGroups().get(0).getWinners().get(0),
				kingOfTheMountainTournament.getRedTeams().get(1));
	}

	@Test(dependsOnMethods = { "resolveFourthLevelFights" })
	public void createFifthLevelFights() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		kingOfTheMountainTournament.createNextLevel();
		Assert.assertEquals((int)kingOfTheMountainTournament.getNumberOfLevels(), 5);
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 4));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(4).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(4)).getCurrentRedTeam(),
				kingOfTheMountainTournament.getRedTeams().get(1));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(4)).getCurrentWhiteTeam(),
				kingOfTheMountainTournament.getWhiteTeams().get(3));
	}

	@Test(dependsOnMethods = { "createFifthLevelFights" })
	public void resolveFifthLevelFights() {
		kingOfTheMountainTournament.setDrawResolution(DrawResolution.OLDEST_ELIMINATED);
		// Draw fight.
		Fight fight = kingOfTheMountainTournament.getLevel(4).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.getDuels().get(0).setHit(false, 0, Score.MEN);
		fight.setOver(true);
		Assert.assertTrue(kingOfTheMountainTournament.getLevel(4).getGroups().get(0).getFights().get(0)
				.isDrawFight());
	}

	@Test(dependsOnMethods = { "resolveFifthLevelFights" })
	public void createSixthLevelFights() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		kingOfTheMountainTournament.createNextLevel();
		Assert.assertEquals((int)kingOfTheMountainTournament.getNumberOfLevels(), 6);
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 5));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(5).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(5)).getCurrentRedTeam(),
				kingOfTheMountainTournament.getRedTeams().get(2));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(5)).getCurrentWhiteTeam(),
				kingOfTheMountainTournament.getWhiteTeams().get(3));
	}

	@Test(dependsOnMethods = { "createSixthLevelFights" })
	public void resolveSixthLevelFights() {
		kingOfTheMountainTournament.setDrawResolution(DrawResolution.NEWEST_ELIMINATED);
		// Draw fight.
		Fight fight = kingOfTheMountainTournament.getLevel(5).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.getDuels().get(0).setHit(false, 0, Score.MEN);
		fight.setOver(true);
		Assert.assertTrue(kingOfTheMountainTournament.getLevel(5).getGroups().get(0).getFights().get(0)
				.isDrawFight());
	}

	@Test(dependsOnMethods = { "resolveSixthLevelFights" })
	public void createSeventhLevelFights() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		kingOfTheMountainTournament.createNextLevel();
		Assert.assertEquals((int)kingOfTheMountainTournament.getNumberOfLevels(), 7);
		FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, 6));
		Assert.assertEquals(kingOfTheMountainTournament.getLevel(6).getGroups().size(), 1);
		// Check red team is the same. White team is the next.
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(6)).getCurrentRedTeam(),
				kingOfTheMountainTournament.getRedTeams().get(3));
		Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(6)).getCurrentWhiteTeam(),
				kingOfTheMountainTournament.getWhiteTeams().get(3));
	}

	@Test(dependsOnMethods = { "createSeventhLevelFights" })
	public void resolveSeventhLevelFights() {
		kingOfTheMountainTournament.setDrawResolution(DrawResolution.BOTH_ELIMINATED);
		// Draw fight.
		Fight fight = kingOfTheMountainTournament.getLevel(6).getGroups().get(0).getFights().get(0);
		fight.getDuels().get(0).setHit(true, 0, Score.MEN);
		fight.getDuels().get(0).setHit(false, 0, Score.MEN);
		fight.setOver(true);
		Assert.assertTrue(kingOfTheMountainTournament.getLevel(6).getGroups().get(0).getFights().get(0)
				.isDrawFight());
	}

	@Test(dependsOnMethods = { "resolveSeventhLevelFights" }, expectedExceptions = { TournamentFinishedException.class })
	public void checkFinalOfTournament() throws SQLException, PersonalizedFightsException,
			TournamentFinishedException {
		for (int i = 4; i < kingOfTheMountainTournament.getWhiteTeams().size(); i++) {
			kingOfTheMountainTournament.createNextLevel();
			FightPool.getInstance().add(tournament, kingOfTheMountainTournament.createSortedFights(false, i));
			Assert.assertEquals(kingOfTheMountainTournament.getLevel(i).getGroups().size(), 1);
			// Check red team is the same. White team is the next.
			Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(i)).getCurrentRedTeam(),
					kingOfTheMountainTournament.getRedTeams().get(4));
			Assert.assertEquals(((KingLevel) kingOfTheMountainTournament.getLevel(i)).getCurrentWhiteTeam(),
					kingOfTheMountainTournament.getWhiteTeams().get(i));

			Fight fight = kingOfTheMountainTournament.getLevel(i).getGroups().get(0).getFights().get(0);
			fight.getDuels().get(0).setHit(true, 0, Score.MEN);
			fight.setOver(true);
			Assert.assertEquals(kingOfTheMountainTournament.getLevel(i).getGroups().get(0).getWinners()
					.get(0), kingOfTheMountainTournament.getRedTeams().get(1));
		}
	}

	@Test(dependsOnMethods = { "checkFinalOfTournament" })
	public void checkTournamentResult() {
		Assert.assertTrue(kingOfTheMountainTournament.isTheLastFight());
	}
}
