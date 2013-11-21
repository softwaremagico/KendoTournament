package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.core.exceptions.TeamMemberOrderException;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.sql.SQLException;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = { "simpleChampionshipTest" }, dependsOnGroups = { "populateDatabase" })
public class SimpleChampionshipTest {

	private static final Integer MEMBERS = 3;
	private static final Integer FIGHT_AREA = 0;
	private static final Integer TEAMS = 6;
	public static final String TOURNAMENT_NAME = "simpleChampionshipTest";
	private static Tournament tournament = null;

	public static Integer getNumberOfCombats(Integer numberOfTeams) {
		return factorial(numberOfTeams) / (2 * factorial(numberOfTeams - 2));
	}

	private static Integer factorial(Integer n) {
		Integer total = 1;
		while (n > 1) {
			total = total * n;
			n--;
		}
		return total;
	}

	@Test
	public void addTournament() throws SQLException {
		tournament = new Tournament(TOURNAMENT_NAME, 1, 2, MEMBERS, TournamentType.SIMPLE);
		TournamentPool.getInstance().add(tournament);
		Assert.assertTrue(TournamentPool.getInstance().get(TOURNAMENT_NAME) != null);
	}

	@Test(dependsOnMethods = { "addTournament" })
	public void addRoles() throws SQLException {
		for (RegisteredPerson competitor : RegisteredPersonPool.getInstance().getAll()) {
			RolePool.getInstance().add(
					tournament,
					new Role(tournament, competitor, RolePool.getInstance().getRoleTags().getRole("Competitor"), false,
							false));
		}
		Assert.assertTrue(RolePool.getInstance().get(tournament).size() == PopulateDatabase.clubs.length
				* PopulateDatabase.competitors.length);
	}

	@Test(dependsOnMethods = { "addRoles" })
	public void addTeams() throws SQLException, TeamMemberOrderException {
		int teamIndex = 0;
		Team team = null;
		int teamMember = 0;
		boolean teamFinished = false;
		while (TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).size() > 0
				&& (TeamPool.getInstance().get(tournament).size() < TEAMS || !teamFinished)) {
			// Create a new team.
			if (team == null) {
				teamIndex++;
				team = new Team("Team" + String.format("%02d", teamIndex), tournament);
				teamMember = 0;
				TeamPool.getInstance().add(tournament, team);
				teamFinished = false;
			}

			// Add member.
			RegisteredPerson member = TeamPool.getInstance().getCompetitorsWithoutTeam(tournament).get(0);
			Assert.assertNotNull(member);
			team.setMember(member, teamMember);
			teamMember++;

			// Team fill up, create a new team.
			if (teamMember >= MEMBERS) {
				team = null;
				teamFinished = true;
			}
		}
		Assert.assertTrue(TeamPool.getInstance().get(tournament).size() == TEAMS);
	}

	@Test(dependsOnMethods = { "addTeams" })
	public void createFights() throws SQLException, PersonalizedFightsException {
		ITournamentManager tournamentManager = TournamentManagerFactory.getManager(tournament, TournamentType.SIMPLE);
		FightPool.getInstance().add(tournament, tournamentManager.createSortedFights(0));
		Assert.assertTrue(FightPool.getInstance().get(tournament).size() == getNumberOfCombats(TeamPool.getInstance()
				.get(tournament).size()));
		// Check than teams are not crossed.
		for (int i = 0; i < FightPool.getInstance().get(tournament).size() - 1; i++) {
			Assert.assertFalse(FightPool.getInstance().get(tournament).get(i).getTeam1()
					.equals(FightPool.getInstance().get(tournament).get(i + 1).getTeam2()));
			Assert.assertFalse(FightPool.getInstance().get(tournament).get(i).getTeam2()
					.equals(FightPool.getInstance().get(tournament).get(i + 1).getTeam1()));
		}
	}

	@Test(dependsOnMethods = { "createFights" })
	public void testSimpleWinner() throws SQLException {
		while (!FightPool.getInstance().areAllOver(tournament)) {
			// First duel
			FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
					.setHit(true, 0, Score.MEN);
			FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
					.setHit(true, 1, Score.MEN);
			FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).setOver(true);
		}

		Ranking ranking = new Ranking(FightPool.getInstance().get(tournament, FIGHT_AREA));

		for (int i = 0; i < ranking.getTeamsScoreRanking().size() - 1; i++) {
			Assert.assertTrue(ranking.getTeamsScoreRanking().get(i).getWonFights() >= ranking.getTeamsScoreRanking()
					.get(i + 1).getWonFights());
			Assert.assertTrue(ranking.getTeamsScoreRanking().get(i).getWonDuels() >= ranking.getTeamsScoreRanking()
					.get(i + 1).getWonDuels());
			Assert.assertTrue(ranking.getTeamsScoreRanking().get(i).getHits() >= ranking.getTeamsScoreRanking()
					.get(i + 1).getHits());
		}

		DuelPool.getInstance().remove(tournament);
	}

	/**
	 * Draw team1 and team3.
	 * 
	 * @throws SQLException
	 */
	@Test(dependsOnMethods = { "createFights", "testSimpleWinner" })
	public void testDrawWinner() throws SQLException {
		while (!FightPool.getInstance().areAllOver(tournament)) {
			// First duel
			if (FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam1()
					.equals(TeamPool.getInstance().get(tournament, "Team01"))
					&& FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam2()
							.equals(TeamPool.getInstance().get(tournament, "Team02"))) {
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 0, Score.MEN);
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 1, Score.MEN);
			} else if (FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam1()
					.equals(TeamPool.getInstance().get(tournament, "Team03"))
					&& FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam2()
							.equals(TeamPool.getInstance().get(tournament, "Team04"))) {
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 0, Score.MEN);
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 1, Score.MEN);
			}
			FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).setOver(true);
		}
		Ranking ranking = new Ranking(FightPool.getInstance().get(tournament, FIGHT_AREA));

		// Team1 is first one because the name.
		List<Team> drawTeams = ranking.getFirstTeamsWithDrawScore(1);
		Assert.assertTrue(drawTeams.size() == 2);
		Assert.assertTrue(drawTeams.contains(TeamPool.getInstance().get(tournament, "Team01")));
		Assert.assertTrue(drawTeams.contains(TeamPool.getInstance().get(tournament, "Team03")));
		Assert.assertTrue(ranking.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team01")));
		Assert.assertTrue(ranking.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team03")));

		// Finally wins Team3
		Undraw undraw = new Undraw(tournament, 0, drawTeams.get(1), 0, 0);
		UndrawPool.getInstance().add(tournament, undraw);
		ranking = new Ranking(FightPool.getInstance().get(tournament, FIGHT_AREA));
		Assert.assertTrue(ranking.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team03")));

		DuelPool.getInstance().remove(tournament);
		UndrawPool.getInstance().remove(tournament);
	}

	/**
	 * Draw team1, team3 and team5.
	 * 
	 * @throws SQLException
	 */
	@Test(dependsOnMethods = { "createFights", "testDrawWinner" })
	public void testDrawVariousWinner() throws SQLException {
		while (!FightPool.getInstance().areAllOver(tournament)) {
			// First duel
			if (FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam1()
					.equals(TeamPool.getInstance().get(tournament, "Team01"))
					&& FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam2()
							.equals(TeamPool.getInstance().get(tournament, "Team02"))) {
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 0, Score.MEN);
			} else if (FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam1()
					.equals(TeamPool.getInstance().get(tournament, "Team03"))
					&& FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam2()
							.equals(TeamPool.getInstance().get(tournament, "Team04"))) {
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 0, Score.MEN);
			} else if (FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam1()
					.equals(TeamPool.getInstance().get(tournament, "Team05"))
					&& FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getTeam2()
							.equals(TeamPool.getInstance().get(tournament, "Team06"))) {
				FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).getDuels().get(0)
						.setHit(true, 0, Score.MEN);
			}
			FightPool.getInstance().getCurrentFight(tournament, FIGHT_AREA).setOver(true);
		}
		Ranking ranking = new Ranking(FightPool.getInstance().get(tournament, FIGHT_AREA));

		// Team1 is first one because the name.
		List<Team> drawTeams = ranking.getFirstTeamsWithDrawScore(1);
		Assert.assertTrue(drawTeams.size() == 3);
		Assert.assertTrue(drawTeams.contains(TeamPool.getInstance().get(tournament, "Team01")));
		Assert.assertTrue(drawTeams.contains(TeamPool.getInstance().get(tournament, "Team03")));
		Assert.assertTrue(drawTeams.contains(TeamPool.getInstance().get(tournament, "Team05")));
		Assert.assertTrue(ranking.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team01")));
		Assert.assertTrue(ranking.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team03")));
		Assert.assertTrue(ranking.getTeam(2).equals(TeamPool.getInstance().get(tournament, "Team05")));

		// Finally wins Team3, Team5, Team1
		Undraw undraw = new Undraw(tournament, 0, drawTeams.get(1), 0, 0);
		UndrawPool.getInstance().add(tournament, undraw);
		undraw = new Undraw(tournament, 0, drawTeams.get(1), 0, 0);
		UndrawPool.getInstance().add(tournament, undraw);
		undraw = new Undraw(tournament, 0, drawTeams.get(2), 0, 0);
		UndrawPool.getInstance().add(tournament, undraw);
		ranking = new Ranking(FightPool.getInstance().get(tournament, FIGHT_AREA));
		Assert.assertTrue(ranking.getTeam(0).equals(TeamPool.getInstance().get(tournament, "Team03")));
		Assert.assertTrue(ranking.getTeam(1).equals(TeamPool.getInstance().get(tournament, "Team05")));
		Assert.assertTrue(ranking.getTeam(2).equals(TeamPool.getInstance().get(tournament, "Team01")));
	}
}
