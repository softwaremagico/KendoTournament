package com.softwaremagico.ktg.core;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.database.RolePool;
import com.softwaremagico.ktg.database.TournamentPool;
import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.Translator;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KendoTournamentGenerator {

    private static KendoTournamentGenerator kendoTournament = null;
    private static boolean debugMode = true;
    public String language = "en";
    private String explorationFolder = null;
    //public TournamentGroupManager tournamentManager = null;
    private char[] shiaijosName = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private String lastSelectedTournament = "";
    private String lastSelectedClub = "";
    private int nameDiplomaPosition = 100;
    private boolean logActivated = true;
    public boolean inverseColours = false;
    public boolean inverseTeams = false;

    private KendoTournamentGenerator() {
        loadConfig();
    }

    public static KendoTournamentGenerator getInstance() {
        if (kendoTournament == null) {
            kendoTournament = new KendoTournamentGenerator();
        }
        return kendoTournament;
    }

    public void changeLastSelectedTournament(String selected) {
        lastSelectedTournament = selected;
        storeConfig();
    }

    public void changeLastSelectedClub(String selected) {
        lastSelectedClub = selected;
        storeConfig();
    }

    public void changeLastNamePositionOnDiploma(int selected) {
        nameDiplomaPosition = selected;
        storeConfig();
    }

    public void changeLogOption(boolean option) {
        logActivated = option;
        storeConfig();
    }

    public void changeDebugOption(boolean option) {
        debugMode = option;
        storeConfig();
    }

    public Tournament getLastSelectedTournament() {
        return TournamentPool.getInstance().get(lastSelectedTournament);
    }

    public String getLastSelectedClub() {
        return lastSelectedClub;
    }

    public Integer getLastNamePositionOnDiploma() {
        return nameDiplomaPosition;
    }

    public boolean getLogOption() {
        return logActivated;
    }

    public List<Team> getTeamsOfFights(List<Fight> fightList) {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < fightList.size(); i++) {
            if (!teams.contains(fightList.get(i).getTeam1())) {
                teams.add(fightList.get(i).getTeam1());
            }
            if (!teams.contains(fightList.get(i).getTeam2())) {
                teams.add(fightList.get(i).getTeam2());
            }
        }
        return teams;
    }

    public String getVersion() {
        String text;
        text = MyFile.readTextFile(this.getClass().getResource("/version.txt").getPath(), false);
        if (text != null && text.length() > 0) {
            return text;
        }
        return MyFile.readTextFromJar("/version.txt");
    }

    /**
     * **********************************************
     *
     * ERRORS
     *
     ***********************************************
     */
    /**
     * If debug is activated, show information about the error.
     */
    public static void showErrorInformation(String className, Exception ex) {
        if (isDebugOptionSelected()) {
            MessageManager.errorMessage(className, ex);
        }
    }

    public static boolean isDebugOptionSelected() {
        return debugMode;
    }

    /**
     * **********************************************
     *
     * FILES
     *
     ***********************************************
     */
    /**
     * Load config from the config file.
     */
    private void loadConfig() {
        obtainStoredLastTournament();
        obtainStoredLastClub();
        obtainStoredNamePositionOnDiploma();
        obtainLogOption();
        obtainDebugOption();
        //obtainStoredScore();
    }

    /**
     * Return the roles defined into a XML file.
     *
     * @return
     */
    public RoleTags getAvailableRoles() {
        return new Translator("roles.xml").returnAvailableRoles(language);
    }

    public String getDefaultDirectory() {
        if (explorationFolder == null) {
            return System.getProperty("user.home");
        } else {
            return explorationFolder;
        }
    }

    public void changeDefaultExplorationFolder(String path) {
        explorationFolder = path;
    }

    private String obtainStoredDataInConfig(String tag) {
        try {
            List<String> tournamentConfigFile;

            tournamentConfigFile = Folder.readFileLines(Path.getPathConfigInHome(), false);

            for (int i = 0; i < tournamentConfigFile.size(); i++) {
                if (tournamentConfigFile.get(i).contains(tag)) {
                    try {
                        return tournamentConfigFile.get(i).split(tag)[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
            }
        } catch (IOException ex) {
        }
        return "";
    }

    private String obtainStoredLastTournament() {
        return lastSelectedTournament = obtainStoredDataInConfig("Tournament:");
    }

    private String obtainStoredLastClub() {
        return lastSelectedClub = obtainStoredDataInConfig("Club:");
    }

    private Integer obtainStoredNamePositionOnDiploma() {
        try {
            nameDiplomaPosition = Integer.parseInt(obtainStoredDataInConfig("NameDiploma:"));
            return nameDiplomaPosition;
        } catch (Exception e) {
            return 100;
        }
    }

    private boolean obtainLogOption() {
        try {
            logActivated = Boolean.parseBoolean(obtainStoredDataInConfig("Log:"));
            return logActivated;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean obtainDebugOption() {
        try {
            debugMode = Boolean.parseBoolean(obtainStoredDataInConfig("Debug:"));
            return debugMode;
        } catch (Exception e) {
            return true;
        }
    }

    public void storeConfig() {
        List<String> configData = new ArrayList<>();
        configData.add("Tournament:" + lastSelectedTournament);
        configData.add("Club:" + lastSelectedClub);
        configData.add("NameDiploma:" + nameDiplomaPosition);
        configData.add("Log:" + logActivated);
        configData.add("Debug:" + debugMode);
//        configData.add("ScoreOption:" + choosedScore);
//        configData.add("ScoreWin:" + scoreForWin);
//        configData.add("ScoreDraw:" + scoreForDraw);
        Folder.saveListInFile(configData, Path.getPathConfigInHome());
    }

    public boolean existCompetitor(List<RegisteredPerson> competitors, RegisteredPerson competitor) {
        for (int i = 0; i < competitors.size(); i++) {
            if (competitors.get(i).id.equals(competitor.id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A competitor has the order of the primary key of the database, except the
     * VCLO that has a particular order.
     *
     * @param competitor
     * @param role
     * @param championship
     * @return
     */
    public String getRegisteredPersonNumber(RegisteredPerson competitor, Role role, Tournament tournament) {
        DecimalFormat myFormatter = new DecimalFormat("00000");
        if (RoleTag.volunteerRoles.contains(role.getTag().getName())) {
            Integer order = RolePool.getInstance().getVolunteerOrder(tournament, competitor);
            if (order != null) {
                return myFormatter.format(order);
            }
        }
        return myFormatter.format(role.getAccreditationOrder());
    }

    public String returnShiaijo(int pos) {
        if (pos < shiaijosName.length) {
            return shiaijosName[pos] + "";
        } else {
            return (pos + 1) + "";
        }
    }
}
