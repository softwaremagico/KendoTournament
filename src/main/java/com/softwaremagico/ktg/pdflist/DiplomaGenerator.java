/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

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
public class DiplomaGenerator extends ListFromTournament {

    float nameposition = 100;
    InputStream photoInput;
    long size;
    private boolean statistics = false;
    private RoleTags selectedRoles = null;

    public DiplomaGenerator(float nposition, boolean printStatistics, RoleTags roles) {
        nameposition = nposition;
        statistics = printStatistics;
        selectedRoles = roles;
        Start(false);
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
                Diploma pdf = new Diploma(listTournaments.get(TournamentComboBox.getSelectedIndex()), statistics && TournamentComboBox.getSelectedIndex() != 0, CheckBox.isSelected(), selectedRoles);

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
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
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
