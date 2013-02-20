package com.softwaremagico.ktg.database;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.statistics.CompetitorRanking;
import com.softwaremagico.ktg.statistics.TeamRanking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author jorge
 */
public abstract class Database {

    Connection connection = null;

    public Database() {
    }

    /**
     * Connect to the database
     *
     * @param tmp_password database password.
     * @param tmp_user user database.
     * @param tmp_database satabase schema.
     * @param tmp_server server IP
     * @param verbose show error messages.
     * @param retry do another try if can solve the SQL problem.
     * @return true if the connection is ok.
     */
    public abstract boolean connect(String password, String user, String database, String server, boolean verbose, boolean retry);

    public abstract void disconnect() throws SQLException;

    abstract void startDatabase();

    private void executeCommand(String[] commands) {
        showCommand(commands);
        try {
            Process child = Runtime.getRuntime().exec(commands);
            showCommandOutput(child);
        } catch (IOException ex1) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex1);
        }
    }

    void showCommand(String[] commands) {
        for (int i = 0; i < commands.length; i++) {
            System.out.print(commands[i] + " ");
        }
        System.out.println();
    }

    void showCommandOutput(Process child) throws IOException {
        try (InputStream in = child.getInputStream()) {
            int c;
            while ((c = in.read()) != -1) {
                System.out.print((char) c);
            }
        }

        BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));
        String s;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    abstract void installDatabase(String password, String user, String server, String database);

    abstract boolean isDatabaseInstalledCorrectly(boolean verbose);

    public abstract boolean updateDatabase(String path, boolean verbose);

    public abstract void clearDatabase();

    /**
     * The database only allows local connections (such as SQLite).
     *
     * @return
     */
    public abstract boolean onlyLocalConnection();

    /**
     * *******************************************************************
     *
     * COMPETITOR
     *
     ********************************************************************
     */
    /**
     * Stores into the database a competitor.
     *
     * @param c Competitor.
     */
    public abstract boolean storeCompetitor(CompetitorWithPhoto competitor, boolean verbose);

    public abstract boolean insertCompetitor(CompetitorWithPhoto competitor);

    public abstract boolean updateCompetitor(CompetitorWithPhoto competitor, boolean verbose);

    public abstract boolean updateClubCompetitor(Competitor competitor, boolean verbose);

    public abstract boolean updateIdCompetitor(Competitor competitor, boolean verbose);

    public abstract List<CompetitorWithPhoto> getCompetitorsWithPhoto(String query, boolean verbose);

    public abstract List<Competitor> getCompetitors(String query, boolean verbose);

    public abstract List<Participant> getParticipants(String query, boolean verbose);

    public abstract List<CompetitorWithPhoto> getAllCompetitorsWithPhoto();

    public abstract List<CompetitorWithPhoto> getCompetitorsWithPhoto(int fromRow, int numberOfRows);

    public abstract boolean storeAllCompetitors(List<CompetitorWithPhoto> competitors, boolean deleteOldOnes);

    public abstract List<Competitor> getAllCompetitors();

    public abstract List<Participant> getAllParticipants();

    /**
     * Select all competitors that are not included in any team for a
     * tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Competitor> selectAllCompetitorsWithoutTeamInTournament(Tournament tournament);

    /**
     * Select all competitors, organizer and refereer of the tournament that
     * still have not the accreditation card.
     *
     * @param tournament
     * @return
     */
    public abstract List<CompetitorWithPhoto> selectAllParticipantsInTournamentWithoutAccreditation(Tournament tournament, boolean printAll);

    /**
     * Select all competitors, organizer and refereer of the tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Competitor> selectAllCompetitorsInTournament(Tournament tournament);

    public abstract List<Competitor> selectAllCompetitorWithDiplomaInTournament(RoleTags roles, Tournament tournament, boolean onlyNotPrinted);

    public abstract List<Competitor> selectAllVolunteersInTournament(Tournament tournament);

    /**
     * Obtain from the database a competitor.
     *
     * @param id The Identificaction Number of the Competitor.
     * @return Competitor.
     */
    public abstract CompetitorWithPhoto selectCompetitor(String id, boolean verbose);

    public abstract List<CompetitorWithPhoto> searchCompetitorsBySimilarName(String name, boolean getImage, boolean verbose);

    public abstract List<CompetitorWithPhoto> searchCompetitorsBySimilarSurname(String surname, boolean getImage, boolean verbose);

    public abstract List<CompetitorWithPhoto> searchCompetitorsBySimilarID(String id, boolean getImage, boolean verbose);

    public abstract List<CompetitorWithPhoto> searchCompetitorsBySimilarClub(String club, boolean getImage, boolean verbose);

    public abstract List<Competitor> searchCompetitorsByClub(String club, boolean verbose);

    public abstract List<Competitor> searchCompetitorsWithoutClub(boolean verbose);

    public abstract List<CompetitorWithPhoto> searchCompetitorsByClubAndTournament(String club, Tournament tournament, boolean getImage, boolean verbose);

    public abstract boolean deleteCompetitor(Competitor competitor, boolean verbose);

    public abstract List<CompetitorRanking> getCompetitorsOrderByScore(boolean verbose, Tournament tournament);

    public abstract List<CompetitorWithPhoto> searchCompetitorsByRoleAndTournament(String role, Tournament tournament, boolean getImage, boolean verbose);

    public abstract List<CompetitorWithPhoto> searchRefereeByTournament(Tournament tournament, boolean getImage, boolean verbose);

    public abstract Integer searchVolunteerOrder(Competitor competitor, Tournament tournament);

    /**
     * *******************************************************************
     *
     * ROLE
     *
     ********************************************************************
     */
    /**
     * Store a Club into the database.
     *
     * @param club
     */
    public abstract boolean storeRole(RoleTag role, Tournament tournament, Participant p, boolean verbose);

    public abstract boolean storeRole(Role role, boolean verbose);

    public abstract boolean deleteRole(Tournament tournament, Participant p);

    public abstract String getTagRole(Tournament tournament, Participant p);

    public abstract void setAllParticipantsInTournamentAsAccreditationPrinted(Tournament tournament);

    public abstract void setParticipantInTournamentAsAccreditationPrinted(Competitor competitor, Tournament tournament);

    public abstract void setParticipantsInTournamentAsAccreditationPrinted(List<Competitor> competitors, Tournament tournament);

    public abstract void setAllParticipantsInTournamentAsDiplomaPrinted(RoleTags roles, Tournament tournament);

    public abstract List<Role> getAllRoles();

    public abstract List<Role> getRoles(int fromRow, int numberOfRows);

    public abstract boolean storeAllRoles(List<Role> roles, boolean deleteOldOnes);

    /**
     * *******************************************************************
     *
     * CLUB
     *
     ********************************************************************
     */
    /**
     * Store a Club into the database.
     *
     * @param club
     */
    public abstract boolean storeClub(Club club, boolean verbose);

    public abstract List<String> returnClubsName();

    public abstract List<Club> getAllClubs();

    public abstract List<Club> getClubs(int fromRow, int numberOfRows);

    public abstract boolean storeAllClubs(List<Club> clubs, boolean deleteOldOnes);

    public abstract List<Club> searchClub(String query, boolean verbose);

    public abstract List<Club> searchClubByName(String name, boolean verbose);

    public abstract List<Club> searchClubByCity(String city, boolean verbose);

    public abstract List<Club> searchClubByCountry(String country, boolean verbose);

    public abstract boolean deleteClub(Club club, boolean verbose);

    /**
     * *******************************************************************
     *
     * TOURNAMENT
     *
     ********************************************************************
     */
    /**
     * Store a Tournament into the database.
     *
     * @param club
     */
    public abstract boolean storeTournament(Tournament tournament, boolean verbose);

    public abstract boolean deleteTournament(Tournament tournament);

    public abstract boolean updateTournament(Tournament tournament, boolean verbose);

    public abstract List<Tournament> getAllTournaments();

    public abstract List<Tournament> getTournaments(int fromRow, int numberOfRows);

    public abstract boolean storeAllTournaments(List<Tournament> tournaments, boolean deleteOldOnes);

    public abstract Tournament getTournamentByName(String name, boolean verbose);

    public abstract List<Tournament> searchTournament(String query, boolean verbose);

    public abstract List<Tournament> searchTournamentsByName(String name, boolean verbose);

    public abstract void deleteGroupsOfTournament(Tournament tournament, List<Team> teams);

    public abstract void storeDiplomaImage(Tournament tournament, InputStream Image, long imageSize);

    public abstract void storeAccreditationImage(Tournament tournament, InputStream Image, long imageSize);

    public abstract int getLevelTournament(Tournament tournament);

    /**
     * *******************************************************************
     *
     * TEAM
     *
     ********************************************************************
     */
    /**
     * Store a Tournament into the database.
     *
     * @param club
     */
    public abstract boolean storeTeam(Team team, boolean verbose);

    public abstract boolean insertTeam(Team team, boolean verbose);

    public abstract List<Team> searchTeam(String query, boolean verbose);

    public abstract List<Team> searchTeamsByNameAndTournament(String name, Tournament tournament, boolean verbose);

    public abstract Team getTeamByName(String name, Tournament tournament, boolean verbose);

    public abstract List<Team> searchTeamsByTournament(Tournament tournament, boolean verbose);

    public abstract List<Team> searchTeamsByTournamentExactName(Tournament tournament, boolean verbose);

    public abstract List<Team> searchTeamsByLevel(Tournament tournament, int level, boolean verbose);

    public abstract List<Team> getAllTeams();

    public abstract List<Team> getTeams(int fromRow, int numberOfRows);

    public abstract boolean storeAllTeams(List<Team> teams, boolean deleteOldOnes);

    public abstract void updateTeamGroupOfLeague(Tournament tournament, Team team);

    public abstract boolean deleteTeam(Team team, boolean verbose);

    public abstract boolean deleteTeamByName(String team, String competition, boolean verbose);

    public abstract void setIndividualTeams(Tournament tournament);

    public abstract boolean deleteTeamsOfTournament(Tournament tournament, boolean verbose);

    public abstract List<TeamRanking> getTeamsOrderByScore(Tournament tournament, boolean verbose);

    public abstract Team getTeamOfCompetitor(String competitorID, Tournament tournament, boolean verbose);

    public abstract boolean insertMembersOfTeamInLevel(Team team, int level, boolean verbose);

    public abstract boolean deleteTeamInLevel(Team team, int level, boolean verbose);

    public abstract boolean deleteAllMemberChangesInTeams(Tournament tournament, boolean verbose);

    /**
     * *******************************************************************
     *
     * FIGHTS
     *
     ********************************************************************
     */
    /**
     * Store a fight into the database.
     */
    public abstract boolean storeFights(List<Fight> fights, boolean purgeTournament, boolean verbose);

    public abstract boolean deleteAllFights();

    public abstract boolean storeAllFightsAndDeleteOldOnes(List<Fight> fights);

    public abstract boolean storeFight(Fight fight, boolean verbose, boolean deleteOldOne);

    public abstract boolean deleteFightsOfTournament(Tournament tournament, boolean verbose);

    public abstract boolean deleteFightsOfLevelOfTournament(Tournament tournament, int level, boolean verbose);

    public abstract List<Fight> searchFights(String query, Tournament tournament);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Fight> searchFightsByTournament(Tournament tournament);

    public abstract List<Fight> searchFightsByTournamentLevelEqualOrGreater(Tournament tournament, int level);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Fight> searchFightsByTournamentAndFightArea(Tournament tournament, int fightArea);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Fight> searchFightsByTournamentAndTeam(Tournament tournament, String team);

    public abstract int obtainFightID(Fight fight);

    public abstract boolean deleteFight(Fight fight, boolean verbose);

    public abstract boolean updateFightAsOver(Fight fight);

    public abstract boolean updateFightAsNotOver(Fight fight);

    public abstract List<Fight> getAllFights();

    public abstract List<Fight> getFights(int fromRow, int numberOfRows);

    /**
     * *******************************************************************
     *
     * DUEL
     *
     ********************************************************************
     */
    /**
     * Store a duel into the database.
     */
    public abstract boolean storeDuel(Duel d, Fight fight, int player);

    public abstract boolean storeDuelsOfFight(Fight fight);

    public abstract boolean deleteDuelsOfFight(Fight fight);

    public abstract List<Duel> getDuelsOfFight(Fight fight);

    public abstract Duel getDuel(Fight fight, int player);

    public abstract List<Duel> getDuelsOfTournament(Tournament tournament);

    public abstract List<Duel> getDuelsOfcompetitor(String competitorID, boolean teamRight);

    public abstract List<Duel> getAllDuels();

    public abstract List<Duel> getDuels(int fromRow, int numberOfRows);

    /**
     * *******************************************************************
     *
     * UNDRAWS
     *
     ********************************************************************
     */
    /**
     * Store a undraw into the database.
     */
    public abstract boolean storeUndraw(Tournament tournament, Team team, int order, int group, int level);

    public abstract boolean storeUndraw(Undraw undraw);

    public abstract List<Undraw> getUndraws(int fromRow, int numberOfRows);

    public abstract List<Undraw> getUndraws(Tournament tournament);
    
    public abstract List<Undraw> getUndraws();

    public abstract boolean storeAllUndraws(List<Undraw> undraws, boolean deleteOldOnes);

    public abstract List<Team> getWinnersInUndraws(Tournament tournament, int level, int group);

    public abstract int getValueWinnerInUndraws(Tournament tournament, String team);

    public abstract int getValueWinnerInUndrawInGroup(Tournament tournament, int group, int level, String team);

    public abstract void deleteDrawsOfTournament(Tournament tournament);

    public abstract void deleteDrawsOfGroupOfTournament(Tournament tournament, int group);

    public abstract void deleteDrawsOfLevelOfTournament(Tournament tournament, int level);
}
