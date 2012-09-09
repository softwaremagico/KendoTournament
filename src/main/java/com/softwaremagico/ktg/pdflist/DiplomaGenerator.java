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
import com.softwaremagico.ktg.RoleTags;
import com.softwaremagico.ktg.files.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JFileChooser;

/**
 *
 * @author Jorge
 */
public class DiplomaGenerator extends ListFromTournamentCreatePDF {

    float nameposition = 100;
    InputStream photoInput;
    long size;
    private boolean statistics = false;
    private RoleTags selectedRoles = null;

    public DiplomaGenerator(float nposition, boolean printStatistics, RoleTags roles) {
        super();
        nameposition = nposition;
        statistics = printStatistics;
        selectedRoles = roles;
        this.setTitle(trans.returnTag("titleDiplomas"));
        CheckBox.setVisible(true);
        prepareCheckBox();
    }

    @Override
    public String defaultFileName() {
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_Diplomas";
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
                DiplomaPDF pdf = new DiplomaPDF(listTournaments.get(TournamentComboBox.getSelectedIndex()), statistics && TournamentComboBox.getSelectedIndex() != 0, CheckBox.isSelected(), selectedRoles);

                /*
                 * Store image for my pleasure.
                 */
                photoInput = new FileInputStream(Path.returnDiplomaPath());
                File fileImage = new File(Path.returnDiplomaPath());
                size = fileImage.length();
                KendoTournamentGenerator.getInstance().database.storeDiplomaImage(listTournaments.get(TournamentComboBox.getSelectedIndex()), photoInput, size);

                if (pdf.generateDiplomaPDF(file, nameposition)) {
                    this.dispose();
                }
            }
        } catch (Exception ex) {
            KendoTournamentGenerator.showErrorInformation(ex);
        }
    }

    public void comboBoxAction() {
        prepareCheckBox();
    }

    private void prepareCheckBox() {
        CheckBox.setEnabled(true);
        CheckBox.setText(trans.returnTag("PrintAll"));
    }

    @Override
    protected ParentList getPdfGenerator() {
        return null;
    }
}
