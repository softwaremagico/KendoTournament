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
import com.softwaremagico.ktg.language.LanguagePool;
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
            try (ObjectOutputStream os = new ObjectOutputStream(
                            new BufferedOutputStream(new FileOutputStream(fileName)))) {
                os.writeObject(objectToSave);
            }
        } catch (FileNotFoundException fnoe) {
        }
    }

    private StoreDatabase load() throws IOException, ClassNotFoundException,
            FileNotFoundException, InvalidClassException {
        StoreDatabase sd;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName))) {
            sd = (StoreDatabase) is.readObject();
        }
        return sd;
    }

    public class LoadDatabase extends Thread {

        TimerPanel timerPanel;
        private Translator transl;
        private String file;
        private int total = 0;
        private int current = 0;

        LoadDatabase(TimerPanel tp, String fileName) {
            transl = LanguagePool.getTranslator("gui.xml");
            timerPanel = tp;
            file = fileName;
            timerPanel.updateTitle(transl.returnTag("ImportDatabaseProgressBarTitle"));
        }

        @Override
        public void run() {
            if (!saveFileInDatabase()) {
                MessageManager.errorMessage(this.getClass().getName(), "corruptedDatabase", "MySQL");
            }
            timerPanel.dispose();
        }

        private boolean readFile() {
            boolean error = false;
            try {
                StoreDatabase sd = load();
                if (sd != null) {
                    timerPanel.updateTitle(transl.returnTag("ReadFile"));
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
            } catch (IOException | ClassNotFoundException ex) {
                error = true;
                MessageManager.errorMessage(this.getClass().getName(), "corruptedDatabase", "MySQL");
            }
            return !error;
        }

        private boolean convertPhotos() {
            for (int i = 0; i < photos.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertPhotosProgressBarLabel") + " " + (i + 1) + "/" + photos.size(), current, total);
                if (photos.get(i) != null) {
                    competitors.get(i).setPhoto(photos.get(i).getImage());
                }
                current++;
            }

            for (int i = 0; i < banners.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertBannersProgressBarLabel") + " " + (i + 1) + "/" + banners.size(), current, total);
                if (banners.get(i) != null) {
                    tournaments.get(i).addBanner(banners.get(i).getImage());
                }
                current++;
            }
            for (int i = 0; i < accreditations.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertAccreditationsProgressBarLabel") + " " + (i + 1) + "/" + banners.size(), current, total);
                if (accreditations.get(i) != null) {
                    tournaments.get(i).addAccreditation(accreditations.get(i).getImage());
                }
                current++;
            }
            for (int i = 0; i < diplomas.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertDiplomasProgressBarLabel") + " " + (i + 1) + "/" + banners.size(), current, total);
                if (diplomas.get(i) != null) {
                    tournaments.get(i).addBanner(diplomas.get(i).getImage());
                }
                current++;
            }

            return true;
        }

        private boolean storeInDatabase() {
            for (int i = 0; i < clubs.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelClub") + " " + (i + 1) + "/" + clubs.size(), current, total);
                if (!DatabaseConnection.getInstance().getDatabase().storeClub(clubs.get(i), false)) {
                    return false;
                }
                current++;
            }

            for (int i = 0; i < tournaments.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelTournament") + " " + (i + 1) + "/" + tournaments.size(), current, total);
                if (!DatabaseConnection.getInstance().getDatabase().storeTournament(tournaments.get(i), false)) {
                    return false;
                }
                current++;
            }

            for (int i = 0; i < competitors.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelCompetitor") + " " + (i + 1) + "/" + competitors.size(), current, total);
                if (!DatabaseConnection.getInstance().getDatabase().insertCompetitor(competitors.get(i))) {
                    return false;
                }
                current++;
            }


            for (int i = 0; i < roles.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelRole") + " " + (i + 1) + "/" + roles.size(), current, total);
                if (!DatabaseConnection.getInstance().getDatabase().storeRole(roles.get(i), false)) {
                    return false;
                }
                current++;
            }

            for (int i = 0; i < teams.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelTeam") + " " + (i + 1) + "/" + roles.size(), current, total);
                if (!DatabaseConnection.getInstance().getDatabase().insertTeam(teams.get(i), false)) {
                    System.out.println("false");
                    return false;
                }
                current++;
            }


            // DatabaseConnection.getInstance().getDatabase().storeFights(fightManager, false, false);

            for (int i = 0; i < fights.size(); i++) {
                timerPanel.updateText(transl.returnTag("ImportDatabaseProgressBarLabelFight") + " " + (i + 1) + "/" + fights.size(), current, total);
                if (DatabaseConnection.getInstance().getDatabase().storeFight(fights.get(i), false, false)) {
                    if (!DatabaseConnection.getInstance().getDatabase().storeDuelsOfFight(fights.get(i))) {
                        return false;
                    }
                } else {
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
                timerPanel.updateText(transl.returnTag("DeleteDatabaseProgressBarLabel"), current, total);
                DatabaseConnection.getInstance().getDatabase().clearDatabase();
                if (convertPhotos()) {
                    if (storeInDatabase()) {
                        error = false;
                    }
                }
            }

            if (!error) {
                MessageManager.translatedMessage(this.getClass().getName(), "databaseImported", "MySQL", JOptionPane.INFORMATION_MESSAGE);
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
            transl = LanguagePool.getTranslator("gui.xml");
            timerPanel = tp;
            file = fileName;
            timerPanel.updateTitle(transl.returnTag("ExportDatabaseProgressBarTitle"));
        }

        @Override
        public void run() {
            saveDatabaseInFile();
            timerPanel.dispose();
            //total = ;
        }

        private boolean getCompetitorsPhotos() {
            photos = new ArrayList<>();
            for (int i = 0; i < competitors.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertPhotosProgressBarLabel") + " " + (i + 1) + "/" + competitors.size(), current, total);
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
            banners = new ArrayList<>();
            accreditations = new ArrayList<>();
            diplomas = new ArrayList<>();
            for (int i = 0; i < tournaments.size(); i++) {
                timerPanel.updateText(transl.returnTag("ConvertBannersProgressBarLabel") + " " + (i + 1) + "/" + tournaments.size(), current, total);
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
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelClub"), current, total);
            clubs = DatabaseConnection.getInstance().getDatabase().getAllClubs();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelCompetitor"), current, total);
            competitors = DatabaseConnection.getInstance().getDatabase().getAllCompetitorsWithPhoto();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTournament"), current, total);
            current += 10;
            tournaments = DatabaseConnection.getInstance().getDatabase().getAllTournaments();
            //Progress bar is only useful when changing the photos. 
            try {
                total = competitors.size() + tournaments.size() + 60;
            } catch (NullPointerException npe) {
                total = 100;
            }
            getCompetitorsPhotos();
            getTournamentBanners();
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelTeam"), current, total);
            teams = DatabaseConnection.getInstance().getDatabase().getAllTeams();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelFight"), current, total);
            fights = DatabaseConnection.getInstance().getDatabase().getAllFights();
            current += 10;
            timerPanel.updateText(transl.returnTag("ExportDatabaseProgressBarLabelRole"), current, total);
            roles = DatabaseConnection.getInstance().getDatabase().getAllRoles();
            current += 10;
            try {
                timerPanel.updateText(transl.returnTag("WriteFile"), current, total);
                write(storeDatabase);
                MessageManager.translatedMessage(this.getClass().getName(), "exportDatabase", "MySQL", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                error = true;
                MessageManager.errorMessage(this.getClass().getName(), "exportDatabaseFail", "MySQL");
            }
            return !error;
        }
    }
}
