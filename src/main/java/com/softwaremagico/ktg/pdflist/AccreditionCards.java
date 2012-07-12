/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

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
public class AccreditionCards extends ListFromTournament {

    InputStream photoInput;
    long size;

    public AccreditionCards() {
        Start(false);
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
