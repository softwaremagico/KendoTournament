/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 09-dic-2008.
 */
package com.softwaremagico.ktg;

import com.softwaremagico.ktg.championship.DesignedGroups;
import com.softwaremagico.ktg.database.Database;
import com.softwaremagico.ktg.database.DatabaseEngine;
import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.language.Configuration;
import com.softwaremagico.ktg.language.Translator;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author jorge
 */
public class KendoTournamentGenerator {

    private static KendoTournamentGenerator kendoTournament = null;
    private boolean debugMode = true;
    public Database database = null;
    private String password = "";
    public String user = "kendouser";
    public String databaseName = "kendotournament";
    public String server = "localhost";
    public String language = "en";
    private DatabaseEngine databaseEngine = null;
    public Languages languages = new Languages();
    private String explorationFolder = null;
    public DesignedGroups designedGroups = null;
    private char[] shiaijosName = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private String lastSelectedTournament = "";
    private String lastSelectedClub = "";
    public boolean databaseConnected = false;
    public FightManager fightManager;
    private int nameDiplomaPosition = 100;
    private boolean logActivated = true;

    private KendoTournamentGenerator() {
        fightManager = new FightManager();
        obtainStoredDatabaseConnection();
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

    public String getLastSelectedTournament() {
        return lastSelectedTournament;
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
            if (!teams.contains(fightList.get(i).team1)) {
                teams.add(fightList.get(i).team1);
            }
            if (!teams.contains(fightList.get(i).team2)) {
                teams.add(fightList.get(i).team2);
            }
        }
        return teams;
    }

    public String getVersion() {
        return MyFile.ReadTextFile("version.txt",false);
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
    public void showErrorInformation(Exception ex) {
        if (getDebugOptionSelected()) {
            MessageManager.errorMessage(ex.getMessage(), "Error");
            Log.debug(ex.getMessage());
        }
    }

    public boolean getDebugOptionSelected() {
        return debugMode;
    }

    /**
     * **********************************************
     *
     * DATABASE
     *
     ***********************************************
     */
    /**
     * Start the connection to the defined database.
     */
    public boolean databaseConnection() {
        return (databaseConnected = startDatabaseConnection(password, user, databaseName, server));
    }

    public boolean startDatabaseConnection(String tmp_password, String tmp_user, String tmp_database, String tmp_server) {
        password = tmp_password;
        user = tmp_user;
        databaseName = tmp_database;
        server = tmp_server;
        generateDatabaseConnectionFile();
        database = databaseEngine.getDatabaseClass();
        try {
            database.disconnect();
        } catch (SQLException ex) {
        }
        databaseConnected = database.connect(tmp_password, tmp_user, tmp_database, tmp_server, true, true);
        return databaseConnected;
    }

    public void resetPassword() {
        password = "";
    }

    private void obtainStoredDatabaseConnection() {
        try {
            List<String> connectionData;

            Configuration conf = new Configuration();
            connectionData = Folder.ReadFileLines(conf.getPathConfigInHome() + "connection.txt", false);

            for (int i = 0; i < connectionData.size(); i++) {
                if (connectionData.get(i).contains("User:")) {
                    try {
                        user = connectionData.get(i).split("User:")[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
                if (connectionData.get(i).contains("Machine:")) {
                    try {
                        server = connectionData.get(i).split("Machine:")[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
                if (connectionData.get(i).contains("Database:")) {
                    try {
                        databaseName = connectionData.get(i).split("Database:")[1];
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
                if (connectionData.get(i).contains("Engine:")) {
                    try {
                        databaseEngine = DatabaseEngine.getDatabase(connectionData.get(i).split("Engine:")[1]);
                    } catch (ArrayIndexOutOfBoundsException aiofb) {
                    }
                }
            }
        } catch (IOException ex) {
        }
    }

    private void generateDatabaseConnectionFile() {
        List<String> connectionData = new ArrayList<>();
        connectionData.add("User:" + user);
        connectionData.add("Machine:" + server);
        connectionData.add("Database:" + databaseName);
        connectionData.add("Engine:" + databaseEngine);
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        Configuration conf = new Configuration();
        f.SaveListInFile(connectionData, conf.getPathConfigInHome() + "connection.txt");
    }

    public boolean isLocallyConnected() {
        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex1) {
        }

        if (database.onlyLocalConnection()) {
            return true;
        }

        //The easy way. 
        if (server.equals("localhost") || server.equals("127.0.0.1") || server.equals(ownIP.getHostAddress())) {
            return true;
        }
        //Read all network interfaces.
        try {
            Enumeration net = NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) net.nextElement();
                Enumeration addr = ni.getInetAddresses();
                //Get the IP of each interface.
                while (addr.hasMoreElements()) {
                    java.net.InetAddress inet = (java.net.InetAddress) addr.nextElement();
                    if (server.equals(inet.getHostAddress())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public DatabaseEngine getDatabaseEngine() {
        if (databaseEngine == null) {
            return DatabaseEngine.getDatabase("MySQL");
        }
        return databaseEngine;
    }

    public void setDatabaseEngine(String engine) {
        databaseEngine = DatabaseEngine.getDatabase(engine);
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

            Configuration conf = new Configuration();
            tournamentConfigFile = Folder.ReadFileLines(conf.getPathConfigInHome() + File.separator + "config.txt", false);

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

    private void storeConfig() {
        List<String> configData = new ArrayList<>();
        configData.add("Tournament:" + lastSelectedTournament);
        configData.add("Club:" + lastSelectedClub);
        configData.add("NameDiploma:" + nameDiplomaPosition);
        configData.add("Log:" + logActivated);
        configData.add("Debug:" + debugMode);
//        configData.add("ScoreOption:" + choosedScore);
//        configData.add("ScoreWin:" + scoreForWin);
//        configData.add("ScoreDraw:" + scoreForDraw);
        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        Configuration conf = new Configuration();
        f.SaveListInFile(configData, conf.getPathConfigInHome() + File.separator + "config.txt");
    }

    public boolean existCompetitor(List<Competitor> competitors, Competitor competitor) {
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
    public String getCompetitorOrder(Competitor competitor, String role, String championship) {
        DecimalFormat myFormatter = new DecimalFormat("00000");
        if (role.equals("VCLO") || role.equals("VolunteerK")) {
            Integer order = database.searchVolunteerOrder(competitor, championship);
            if (order != null) {
                return myFormatter.format(order);
            }
        }
        return myFormatter.format(competitor.getOrder());
    }

    public String returnShiaijo(int pos) {
        if (pos < shiaijosName.length) {
            return shiaijosName[pos] + "";
        } else {
            return (pos + 1) + "";
        }
    }
}
