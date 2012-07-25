package com.softwaremagico.ktg.pdflist;
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

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.files.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.swing.JFileChooser;

/**
 *
 * @author Jorge
 */
public class AccreditionCards extends ListFromTournamentPDF {

    InputStream photoInput;
    long size;

    public AccreditionCards() {
        super();
        this.setTitle(trans.returnTag("titleAccreditionCard"));
        CheckBox.setVisible(true);
        changeCheckBoxText(trans.returnTag("PrintAll"));
    }

    @Override
    public String defaultFileName() {
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_AccreditionCards";
        } catch (NullPointerException npe) {
            return null;
        }
    }

    @Override
    public void generate() {
        try {
            String file;
            if (!(file = exploreWindowsForPdf(trans.returnTag("ExportPDF"),
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {

                /*
                 * Store image for my personal pleasure.
                 */
                try {
                    photoInput = new FileInputStream(Path.returnBackgroundPath());
                    File fileImage = new File(Path.returnBackgroundPath());
                    size = fileImage.length();
                    KendoTournamentGenerator.getInstance().database.storeAccreditationImage(listTournaments.get(TournamentComboBox.getSelectedIndex()), photoInput, size);
                } catch (FileNotFoundException fnf) {
                    KendoTournamentGenerator.getInstance().showErrorInformation(fnf);
                }
                TournamentAccreditationPDF pdf = new TournamentAccreditationPDF(KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false));
                pdf.setPrintAll(isCheckBoxSelected());
                pdf.createFile(file);
            }
        } catch (Exception ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    @Override
    protected ParentList getPdfGenerator() {
        return null;
    }
}
