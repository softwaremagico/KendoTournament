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
import java.util.ArrayList;
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
    public abstract boolean connect(String tmp_password, String tmp_user, String tmp_database, String tmp_server, boolean verbose, boolean retry);

    public abstract void disconnect() throws SQLException;

    abstract void startDatabase();

    private void executeCommand(String[] commands) {
        showCommand(commands);
        try {
            Process child = Runtime.getRuntime().exec(commands);
            showCommandOutput(child);
        } catch (IOException ex1) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex1);
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

    abstract void installDatabase(String tmp_password, String tmp_user, String tmp_server, String tmp_database);

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
    public abstract boolean storeCompetitor(CompetitorWithPhoto c, boolean verbose);

    public abstract boolean insertCompetitor(CompetitorWithPhoto c);

    public abstract boolean updateCompetitor(CompetitorWithPhoto c, boolean verbose);

    public abstract boolean updateClubCompetitor(Competitor c, boolean verbose);

    public abstract boolean updateIdCompetitor(Competitor c, boolean verbose);

    public abstract List<CompetitorWithPhoto> getCompetitorsWithPhoto(String query, boolean verbose);

    public abstract List<Competitor> getCompetitors(String query, boolean verbose);

    public abstract List<Participant> getParticipants(String query, boolean verbose);

    public abstract List<CompetitorWithPhoto> getAllCompetitorsWithPhoto();

    public abstract boolean storeAllCompetitors(List<CompetitorWithPhoto> competitors);

    public abstract List<Competitor> getAllCompetitors();

    public abstract List<Participant> getAllParticipants();

    /**
     * Select all competitors that are not included in any team for a
     * tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Competitor> selectAllCompetitorsWithoutTeamInTournament(String championship);

    /**
     * Select all competitors, organizer and refereer of the tournament that
     * still have not the accreditation card.
     *
     * @param tournament
     * @return
     */
    public abstract List<CompetitorWithPhoto> selectAllParticipantsInTournamentWithoutAccreditation(String championship, boolean printAll);

    /**
     * Select all competitors, organizer and refereer of the tournament.
     *
     * @param tournament
     * @return
     */
    public abstract List<Competitor> selectAllCompetitorsInTournament(String championship);

    public abstract List<Competitor> selectAllCompetitorWithDiplomaInTournament(RoleTags roles, String championship, boolean onlyNotPrinted);

    public abstract List<Competitor> selectAllVolunteersInTournament(String championship);

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

    public abstract List<CompetitorWithPhoto> searchCompetitorsByClubAndTournament(String club, String championship, boolean getImage, boolean verbose);

    public abstract boolean deleteCompetitor(Competitor c, boolean verbose);

    public abstract List<CompetitorRanking> getCompetitorsOrderByScore(boolean verbose, String championship);

    public abstract List<CompetitorWithPhoto> searchCompetitorsByRoleAndTournament(String role, String championship, boolean getImage, boolean verbose);

    public abstract List<CompetitorWithPhoto> searchRefereeByTournament(String championship, boolean getImage, boolean verbose);

    public abstract Integer searchVolunteerOrder(Competitor c, String championship);

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
    public abstract boolean storeRole(RoleTag role, Tournament t, Participant p, boolean verbose);

    public abstract boolean storeRole(Role role, boolean verbose);

    public abstract boolean deleteRole(Tournament t, Participant p);

    public abstract String getTagRole(Tournament t, Participant p);

    public abstract void setAllParticipantsInTournamentAsAccreditationPrinted(String championship);

    public abstract void setParticipantInTournamentAsAccreditationPrinted(Competitor competitor, String championship);

    public abstract void setParticipantsInTournamentAsAccreditationPrinted(List<Competitor> competitors, String championship);

    public abstract void setAllParticipantsInTournamentAsDiplomaPrinted(RoleTags roles, String championship);

    public abstract List<Role> getAllRoles();

    public abstract boolean storeAllRoles(List<Role> roles);

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

    public abstract boolean storeAllClubs(List<Club> clubs);

    public abstract List<Club> searchClub(String query, boolean verbose);

    public abstract List<Club> searchClubByName(String name, boolean verbose);

    public abstract List<Club> searchClubByCity(String city, boolean verbose);

    public abstract List<Club> searchClubByCountry(String country, boolean verbose);

    public abstract boolean deleteClub(Club c, boolean verbose);

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
    public abstract boolean storeTournament(Tournament t, boolean verbose);

    public abstract boolean deleteTournament(String championship);

    public abstract boolean updateTournament(Tournament t, boolean verbose);

    public abstract List<Tournament> getAllTournaments();

    public abstract boolean storeAllTournaments(List<Tournament> tournaments);

    public abstract Tournament getTournamentByName(String name, boolean verbose);

    public abstract List<Tournament> searchTournament(String query, boolean verbose);

    public abstract List<Tournament> searchTournamentsByName(String name, boolean verbose);

    public abstract void deleteGroupsOfTournament(String league, List<Team> teams);

    public abstract void storeDiplomaImage(Tournament t, InputStream Image, long imageSize);

    public abstract void storeAccreditationImage(Tournament t, InputStream Image, long imageSize);

    public abstract int getLevelTournament(String tournament);

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
    public abstract boolean storeTeam(Team t, boolean verbose);

    public abstract boolean insertTeam(Team t, boolean verbose);

    public abstract List<Team> searchTeam(String query, boolean verbose);

    public abstract List<Team> searchTeamsByNameAndTournament(String name, String tournament, boolean verbose);

    public abstract Team getTeamByName(String name, String championship, boolean verbose);

    public abstract List<Team> searchTeamsByTournament(String tournament, boolean verbose);

    public abstract List<Team> searchTeamsByTournamentExactName(String tournament, boolean verbose);

    public abstract List<Team> searchTeamsByLevel(String tournament, int level, boolean verbose);

    public abstract List<Team> getAllTeams();

    public abstract boolean storeAllTeams(List<Team> teams);

    public abstract void updateTeamGroupOfLeague(String league, Team t);

    public abstract boolean deleteTeam(Team t, boolean verbose);

    public abstract boolean deleteTeamByName(String team, String competition, boolean verbose);

    public abstract void setIndividualTeams(String championship);

    public abstract boolean deleteTeamsOfTournament(String championship, boolean verbose);

    public abstract List<TeamRanking> getTeamsOrderByScore(String championship, boolean verbose);

    public abstract Team getTeamOfCompetitor(String competitorID, String championship, boolean verbose);

    public abstract boolean insertMemebersOfTeamInLevel(Team t, int level, boolean verbose);

    public abstract boolean extendTeamInLevel(Team t, int level, boolean verbose);

    public abstract boolean deleteTeamInLevel(Team t, int level, boolean verbose);

    public abstract boolean deleteAllMemberChangesInTeams(String championship, boolean verbose);

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
    public abstract boolean storeFights(ArrayList<Fight> fights, boolean purgeTournament, boolean verbose);

    public abstract boolean storeAllFightsAndDeleteOldOnes(ArrayList<Fight> fights);

    public abstract boolean storeFight(Fight fight, boolean verbose, boolean deleteOldOne);

    public abstract boolean deleteFightsOfTournament(String championship, boolean verbose);

    public abstract boolean deleteFightsOfLevelOfTournament(String championship, int level, boolean verbose);

    public abstract ArrayList<Fight> searchFights(String query, String championship);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract ArrayList<Fight> searchFightsByTournamentName(String championship);

    public abstract ArrayList<Fight> searchFightsByTournamentNameLevelEqualOrGreater(String championship, int level);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract ArrayList<Fight> searchFightsByTournamentNameAndFightArea(String championship, int fightArea);

    /**
     * Search all fights from one determined tournament.
     *
     * @param tournament
     * @return
     */
    public abstract ArrayList<Fight> searchFightsByTournamentNameAndTeam(String championship, String team);

    public abstract int obtainFightID(Fight f);

    public abstract boolean deleteFight(Fight fight, boolean verbose);

    public abstract boolean updateFightAsOver(Fight fight);

    public abstract boolean updateFightAsNotOver(Fight fight);

    public abstract ArrayList<Fight> getAllFights();

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
    public abstract boolean storeDuel(Duel d, Fight f, int player);

    public abstract boolean storeDuelsOfFight(Fight f);

    public abstract boolean deleteDuelsOfFight(Fight f);

    public abstract List<Duel> getDuelsOfFight(Fight f);

    public abstract Duel getDuel(Fight f, int player);

    public abstract List<Duel> getDuelsOfTournament(String championship);

    public abstract List<Duel> getDuelsOfcompetitor(String competitorID, boolean teamRight);

    public abstract List<Duel> getAllDuels();

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
    public abstract boolean storeUndraw(String championship, String team, int order, int group);

    public abstract List<Undraw> getAllUndraws();

    public abstract boolean storeAllUndraws(List<Undraw> undraws);

    //public abstract boolean defineWinnerInUndraw(String championship, String team, int level, List<Team> drawTeams);
    public abstract String getWinnerInUndraws(String championship, int group, List<Team> drawTeams);

    //public abstract int getValueWinnerInUndrawInLevel(String championship, String team);
    public abstract int getValueWinnerInUndraws(String championship, String team);

    public abstract int getValueWinnerInUndrawInGroup(String championship, int group, String team);

    public abstract void deleteDrawsOfTournament(String championship);

    public abstract void deleteDrawsOfGroupOfTournament(String championship, int group);
}
