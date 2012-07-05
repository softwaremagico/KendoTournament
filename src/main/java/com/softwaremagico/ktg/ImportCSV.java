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
 *  Created on 28-ene-2011.
 */
package com.softwaremagico.ktg;

import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.language.Translator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Jorge
 */
public class ImportCSV {

    private Tournament championship;
    private String text;
    private String[] fields;
    private int index = 0;
    transient private Translator trans = null;
    private JFileChooser fc;
    private String lastClub = "";

    ImportCSV(String champions) {
        championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(champions, false);
        //CorrectNIF nif = new CorrectNIF(tournam);
        trans = new Translator("gui.xml");
        String path = exploreWindowsForCsv(JFileChooser.FILES_AND_DIRECTORIES, "");
        if (path.length() > 0) {
            MyFile file = new MyFile(path);
            try {
                text = file.InString(true);
                text = text.replace("\\s", "");
                text = text.replace("\\n", "");
                fields = text.split(";");
                selectOption();
            } catch (IOException ex) {
                Logger.getLogger(ImportCSV.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean obtainDelegate() {
        boolean error = false;
        String name = fields[index];
        index++;
        String surname = fields[index];
        index++;
        String id = fields[index];
        index++;
        String city = fields[index];
        index++;
        String club = fields[index];
        lastClub = club;
        index++;
        String phone = fields[index];
        index++;
        String address = fields[index];
        index++;
        String email = fields[index];
        index++;

        //Update club
        Club c = new Club(club, " ", city);
        c.storeAddress(address);
        if (!KendoTournamentGenerator.getInstance().database.storeClub(c, false)) {
            error = true;
        }


        //Update competitor.
        CompetitorWithPhoto d = KendoTournamentGenerator.getInstance().database.selectCompetitor(id, false);
        if (d == null) {
            d = new CompetitorWithPhoto(id, name, surname, club);
            if (!KendoTournamentGenerator.getInstance().database.storeCompetitor(d, false)) {
                error = true;
            }
        }

        //Update representative.
        c.RefreshRepresentative(id, email, phone);
        if (!KendoTournamentGenerator.getInstance().database.storeClub(c, false)) {
            error = true;
        }

        return !error;
    }

    private boolean obtainTeam() {
        boolean error = false;
        String teamName = fields[index].replace("EQUIPO_", "").replace("\n", "").trim();
        Team t = new Team(teamName, championship);
        index++;
        while (index < fields.length && !fields[index].toLowerCase().contains("arbitro") && !fields[index].toLowerCase().contains("seminar")) {
            //First Team
            if (!fields[index].contains("EQUIPO_") && fields[index].length() > 1) {
                //Add member.
                String memberName = fields[index];
                index++;
                String memberSurname = fields[index];
                index++;
                String memberID = fields[index];

                CompetitorWithPhoto c = KendoTournamentGenerator.getInstance().database.selectCompetitor(memberID, false);
                if (c == null) {
                    c = new CompetitorWithPhoto(memberID, memberName, memberSurname, lastClub);
                    if (!KendoTournamentGenerator.getInstance().database.storeCompetitor(c, false)) {
                        error = true;
                    }

                }
                // RoleTag of members.
                RoleTag role = KendoTournamentGenerator.getInstance().getAvailableRoles().getRole("Competitor");
                if (!KendoTournamentGenerator.getInstance().database.storeRole(role, championship, c, false)) {
                    error = true;
                }

                //Team.
                t.addOneMember(c, 0);
            } else {
                //New team
                if (fields[index].contains("EQUIPO_") && !fields[index].replace("EQUIPO_", "").replace("\n", "").trim().equals(teamName)) {
                    t.completeTeam(championship.teamSize, 0);
                    if (!KendoTournamentGenerator.getInstance().database.storeTeam(t, false)) {
                        error = true;
                    }

                    teamName = fields[index].replace("EQUIPO_", "").replace("\n", "").trim();
                    t = new Team(teamName, championship);
                }
            }
            index++;
        }
        //Store last team.
        if (t.levelChangesSize() > 0) {
            t.completeTeam(championship.teamSize, 0);
            if (!KendoTournamentGenerator.getInstance().database.storeTeam(t, false)) {
                error = true;
            }
        }

        return !error;
    }

    private boolean obtainReferee() {
        boolean error = false;
        String name = fields[index];
        index++;
        String surname = fields[index];
        index++;
        String id = fields[index];
        index++;

        //Update competitor.
        CompetitorWithPhoto r = KendoTournamentGenerator.getInstance().database.selectCompetitor(id, false);
        if (r == null) {
            r = new CompetitorWithPhoto(id, name, surname, lastClub);
            if (!KendoTournamentGenerator.getInstance().database.storeCompetitor(r, false)) {
                error = true;
            }
        }

        // RoleTag of referee.
        RoleTag role = KendoTournamentGenerator.getInstance().getAvailableRoles().getRole("Referee");
        if (!KendoTournamentGenerator.getInstance().database.storeRole(role, championship, r, false)) {
            error = true;
        }
        return !error;
    }

    private boolean obtainSeminar() {
        boolean error = false;
        String name = fields[index];
        index++;
        String surname = fields[index];
        index++;
        String id = fields[index];
        index++;

        //Update competitor.
        CompetitorWithPhoto s = KendoTournamentGenerator.getInstance().database.selectCompetitor(id, false);
        if (s == null) {
            s = new CompetitorWithPhoto(id, name, surname, lastClub);
            if (!KendoTournamentGenerator.getInstance().database.storeCompetitor(s, false)) {
                error = true;
            }
        }

        // RoleTag of referee.
        RoleTag role = KendoTournamentGenerator.getInstance().getAvailableRoles().getRole("Seminar");
        if (!KendoTournamentGenerator.getInstance().database.storeRole(role, championship, s, false)) {
            error = true;
        }
        return !error;
    }

    private void selectOption() {
        boolean error = false;
        while (index < fields.length) {
            if (fields[index].trim().toLowerCase().equals("delegado")
                    || fields[index].trim().toLowerCase().equals("delegate")
                    || fields[index].trim().equals(trans.returnTag("RepresentativeLabel", KendoTournamentGenerator.getInstance().language).replace(":", ""))) {
                index++;
                if (!obtainDelegate()) {
                    error = true;
                    MessageManager.errorMessage("clubNotStored", "MySQL", KendoTournamentGenerator.getInstance().language);
                }
            } else if (fields[index].trim().toLowerCase().contains("equipo")
                    || fields[index].trim().toLowerCase().equals("equipo")
                    || fields[index].trim().toLowerCase().equals("team")
                    || fields[index].trim().equals(trans.returnTag("Team", KendoTournamentGenerator.getInstance().language).replace(":", ""))) {
                if (!obtainTeam()) {
                    error = true;
                    MessageManager.errorMessage("storeTeam", "MySQL", KendoTournamentGenerator.getInstance().language);
                }
            } else if (fields[index].trim().toLowerCase().contains("arbitro")
                    || fields[index].trim().toLowerCase().equals("arbitro")
                    || fields[index].trim().toLowerCase().equals("referee")
                    || fields[index].trim().equals(KendoTournamentGenerator.getInstance().getAvailableRoles().getRole("Referee").name)) {
                index++;
                if (!obtainReferee()) {
                    error = true;
                    MessageManager.errorMessage("storeRefereeBad", "MySQL", KendoTournamentGenerator.getInstance().language);
                }
            } else if (fields[index].trim().toLowerCase().contains("seminar")
                    || fields[index].trim().toLowerCase().equals("seminar")
                    || fields[index].trim().toLowerCase().equals("seminar")) {
                index++;
                if (!obtainSeminar()) {
                    error = true;
                    MessageManager.errorMessage("storeSeminarBad", "MySQL", KendoTournamentGenerator.getInstance().language);
                }
            } else {
                //Unknown field, choose the next one. 
                index++;
            }
        }
        if (!error) {
            MessageManager.translatedMessage("csvInserted", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String exploreWindowsForCsv(int mode, String file) {
        JFrame frame = null;
        try {
            fc = new JFileChooser(new File(KendoTournamentGenerator.getInstance().getDefaultDirectory() + File.separator));
            fc.setFileFilter(new CsvFilter());
            fc.setFileSelectionMode(mode);
            String title = trans.returnTag("ImportMenu", KendoTournamentGenerator.getInstance().language);
            if (file.length() == 0) {
                fc.setSelectedFile(new File(defaultFileName()));
            } else {
                fc.setSelectedFile(new File(file));
            }
            int fcReturn = fc.showDialog(frame, title);
            if (fcReturn == JFileChooser.APPROVE_OPTION) {
                KendoTournamentGenerator.getInstance().changeDefaultExplorationFolder(fc.getSelectedFile().toString());
                if (fc.getSelectedFile().isDirectory()) {
                    return fc.getSelectedFile().toString()
                            + File.pathSeparator + defaultFileName();
                }
                return fc.getSelectedFile().toString();
            } else {
            }
        } catch (NullPointerException npe) {
        }
        return "";
    }

    private String defaultFileName() {
        return "data.csv";
    }

    private class CsvFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".csv");
        }

        @Override
        public String getDescription() {
            return "Comma-separated values";
        }
    }
}
