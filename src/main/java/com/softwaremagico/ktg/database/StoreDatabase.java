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
 */
package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Club;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Role;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.TimerPanel;
import java.awt.Image;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class StoreDatabase implements Serializable {

    private List<Club> clubs;
    private List<CompetitorWithPhoto> competitors;
    private List<ImageIcon> photos;
    private List<Tournament> tournaments;
    private List<ImageIcon> banners;
    private List<ImageIcon> diplomas;
    private List<ImageIcon> accreditations;
    private List<Team> teams;
    private List<Fight> fights;
    private List<Role> roles;
    private transient String fileName;

    public StoreDatabase() {
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public void save(String file) {
        fileName = file;
        TimerPanel tp = new TimerPanel();
        SaveDatabase sd = new SaveDatabase(this, tp, file);
        sd.start();
    }

    public void load(String file) {
        fileName = file;
        TimerPanel tp = new TimerPanel();
        LoadDatabase ld = new LoadDatabase(tp, file);
        ld.start();
    }

    private void write(Object objectToSave) throws IOException {
        try {
            ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(fileName)));
            os.writeObject(objectToSave);
            os.close();
        } catch (FileNotFoundException fnoe) {
        }
    }

    private StoreDatabase load() throws IOException, ClassNotFoundException,
            FileNotFoundException, InvalidClassException {
        //List l;
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName));
        StoreDatabase sd = (StoreDatabase) is.readObject();
        is.close();
        return sd;
    }

    public class LoadDatabase extends Thread {

        TimerPanel timerPanel;
        private Translator transl;
        private String file;
        private int total = 0;
        private int current = 0;

        LoadDatabase(TimerPanel tp, String fileName) {
            transl = new Translator("gui.xml");
            timerPanel = tp;
            file = fileName;
            timerPanel.updateTitle(transl.returnTag("ImportDatabaseProgressBarTitle", KendoTournamentGenerator.getInstance().language));
        }

        @Override
        public void run() {
            if(!saveFileInDatabase()){
                MessageManager.errorMessage("corruptedDatabase", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
            timerPanel.dispose();
        }

        private boolean readFile() {
            boolean error = false;
            try {
                StoreDatabase sd = load();
                if (sd != null) {
                    timerPanel.updateTitle(transl.returnTag("ReadFile", KendoTournamentGenerator.getInstance().language));
                    clubs = sd.clubs;
                    competitors = sd.competitors;
                    photos = sd.photos;
                    tournaments = sd.tournaments;
                    banners = sd.banners;
                    diplomas = sd.diplomas;
                    accreditations = sd.accreditations;
                    teams = sd.teams;
                    fights = sd.fights;
                    roles = sd.roles;
                } else {
                    return false;
                }
            } catch (InvalidClassException ice) {
                error = true;
                MessageManager.errorMessage("corruptedDatabase", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (IOException ex) {
                error = true;
                MessageManager.errorMessage("corruptedDatabase", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (ClassNotFoundException ex) {
                error = true;
                MessageManager.errorMessage("corruptedDatabase", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
            return !error;
        }

        private boolean convertPhotos() {
            for (int i = 0; i < photos.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertPhotosProgressBarLabel", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + photos.size(), current, total);
                if (photos.get(i) != null) {
                    competitors.get(i).setPhoto(photos.get(i).getImage());
                }
                current++;
            }

            for (int i = 0; i < banners.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertBannersProgressBarLabel", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + banners.size(), current, total);
                if (banners.get(i) != null) {
                    tournaments.get(i).addBanner(banners.get(i).getImage());
                }
                current++;
            }
            for (int i = 0; i < accreditations.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertAccreditationsProgressBarLabel", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + banners.size(), current, total);
                if (accreditations.get(i) != null) {
                    tournaments.get(i).addAccreditation(accreditations.get(i).getImage());
                }
                current++;
            }
            for (int i = 0; i < diplomas.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertDiplomasProgressBarLabel", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + banners.size(), current, total);
                if (diplomas.get(i) != null) {
                    tournaments.get(i).addBanner(diplomas.get(i).getImage());
                }
                current++;
            }

            return true;
        }

        private boolean storeInDatabase() {
            for (int i = 0; i < clubs.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelClub", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + clubs.size(), current, total);
                if (!KendoTournamentGenerator.getInstance().database.storeClub(clubs.get(i), false)) {
                    return false;
                }
                current++;
            }

            for (int i = 0; i < tournaments.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelTournament", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + tournaments.size(), current, total);
                if (!KendoTournamentGenerator.getInstance().database.storeTournament(tournaments.get(i), false)) {
                    return false;
                }
                current++;
            }

            for (int i = 0; i < competitors.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelCompetitor", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + competitors.size(), current, total);
                if (!KendoTournamentGenerator.getInstance().database.insertCompetitor(competitors.get(i))) {
                    return false;
                }
                current++;
            }


            for (int i = 0; i < roles.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelRole", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + roles.size(), current, total);
                if (!KendoTournamentGenerator.getInstance().database.storeRole(roles.get(i), false)) {
                    return false;
                }
                current++;
            }
            
            for (int i = 0; i < teams.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelTeam", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + roles.size(), current, total);
                if (!KendoTournamentGenerator.getInstance().database.insertTeam(teams.get(i), false)) {
                    System.out.println("false");
                    return false;
                }
                current++;
            }


            // KendoTournamentGenerator.getInstance().database.storeFights(fightManager, false, false);

            for (int i = 0; i < fights.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelFight", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + fights.size(), current, total);
                if (KendoTournamentGenerator.getInstance().database.storeFight(fights.get(i), false)) {
                    if (!KendoTournamentGenerator.getInstance().database.storeDuelsOfFight(fights.get(i))) {
                        return false;
                    }
                }else{
                    return false;
                }
                current++;
            }

            return true;
        }

        private boolean saveFileInDatabase() {
            boolean error = true;

            if (readFile()) {
                total = photos.size() + banners.size() + clubs.size() + competitors.size() + tournaments.size() + roles.size() + teams.size() + fights.size() + diplomas.size() + accreditations.size();
                current++;
                timerPanel.updateText(transl.returnTag("DeleteDatabaseProgressBarLabel", KendoTournamentGenerator.getInstance().language), current, total);
                KendoTournamentGenerator.getInstance().database.clearDatabase();
                if (convertPhotos()) {
                    if (storeInDatabase()) {
                        error = false;
                    }
                }
            }

            if (!error) {
                MessageManager.customMessage("databaseImported", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            }
            return !error;
        }
    }

    public class SaveDatabase extends Thread {

        TimerPanel timerPanel;
        private Translator transl;
        private String file;
        private int total = 0;
        private int current = 0;
        private StoreDatabase storeDatabase;

        SaveDatabase(StoreDatabase sd, TimerPanel tp, String fileName) {
            storeDatabase = sd;
            transl = new Translator("gui.xml");
            timerPanel = tp;
            file = fileName;
            timerPanel.updateTitle(transl.returnTag("ExportDatabaseProgressBarTitle", KendoTournamentGenerator.getInstance().language));
        }

        @Override
        public void run() {
            saveDatabaseInFile();
            timerPanel.dispose();
            //total = ;
        }

        private boolean getCompetitorsPhotos() {
            photos = new ArrayList<ImageIcon>();
            for (int i = 0; i < competitors.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertPhotosProgressBarLabel", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + competitors.size(), current, total);
                try {
                    Image photo = competitors.get(i).photo();
                    if (photo != null) {
                        photos.add(new ImageIcon(photo));
                    } else {
                        photos.add(null);
                    }
                } catch (IOException ex) {
                    return false;
                }
                current++;
            }
            return true;
        }

        private boolean getTournamentBanners() {
            banners = new ArrayList<ImageIcon>();
            accreditations = new ArrayList<ImageIcon>();
            diplomas = new ArrayList<ImageIcon>();
            for (int i = 0; i < tournaments.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertBannersProgressBarLabel", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + "/" + tournaments.size(), current, total);
                try {
                    Image banner = tournaments.get(i).banner();
                    if (banner != null) {
                        banners.add(new ImageIcon(banner));
                    } else {
                        banners.add(null);
                    }
                } catch (IOException ex) {
                    return false;
                }
                try {
                    Image accreditation = tournaments.get(i).accreditation();
                    if (accreditation != null) {
                        accreditations.add(new ImageIcon(accreditation));
                    } else {
                        accreditations.add(null);
                    }
                } catch (IOException ex) {
                    return false;
                }
                try {
                    Image diploma = tournaments.get(i).diploma();
                    if (diploma != null) {
                        diplomas.add(new ImageIcon(diploma));
                    } else {
                        diplomas.add(null);
                    }
                } catch (IOException ex) {
                    return false;
                }
                current++;
            }
            return true;
        }

        private boolean saveDatabaseInFile() {
            boolean error = false;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelClub", KendoTournamentGenerator.getInstance().language), current, total);
            clubs = KendoTournamentGenerator.getInstance().database.getAllClubs();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelCompetitor", KendoTournamentGenerator.getInstance().language), current, total);
            competitors = KendoTournamentGenerator.getInstance().database.getAllCompetitorsWithPhoto();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTournament", KendoTournamentGenerator.getInstance().language), current, total);
            current += 10;
            tournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            //Progress bar is only useful when changing the photos. 
            try {
                total = competitors.size() + tournaments.size() + 60;
            } catch (NullPointerException npe) {
                total = 100;
            }
            getCompetitorsPhotos();
            getTournamentBanners();
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTeam", KendoTournamentGenerator.getInstance().language), current, total);
            teams = KendoTournamentGenerator.getInstance().database.getAllTeams();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight", KendoTournamentGenerator.getInstance().language), current, total);
            fights = KendoTournamentGenerator.getInstance().database.getAllFights();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelRole", KendoTournamentGenerator.getInstance().language), current, total);
            roles = KendoTournamentGenerator.getInstance().database.getAllRoles();
            current += 10;
            try {
                timerPanel.updateText(transl.returnTag("WriteFile", KendoTournamentGenerator.getInstance().language), current, total);
                write(storeDatabase);
                MessageManager.customMessage("exportDatabase", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (IOException ex) {
                error = true;
                MessageManager.errorMessage("exportDatabaseFail", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                ex.printStackTrace();
            }
            return !error;
        }
    }
}
