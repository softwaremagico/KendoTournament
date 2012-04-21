/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.softwaremagico.ktg.KendoTournamentGenerator;

/**
 *
 * @author jorge
 */
public class EmptyFightsList extends ListFromTournament {
    
     public EmptyFightsList() {
        Start(false);
        this.setTitle(trans.returnTag("titleSummary", KendoTournamentGenerator.getInstance().language));
        ArenaComboBox.setEnabled(true);
        CheckBox.setVisible(true);
        changeCheckBoxText(trans.returnTag("ShowEndFights", KendoTournamentGenerator.getInstance().language));
    }

    public String defaultFileName() {
        String shiaijo = "";
        if (returnSelectedArena() >= 0) {
            shiaijo = "_" + KendoTournamentGenerator.getInstance().returnShiaijo(returnSelectedArena());
        }
        try {
            return TournamentComboBox.getSelectedItem().toString() + "_FightsCard" + shiaijo;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    protected ParentList getPdfGenerator() {
        return new EmptyFightsListPDF(listTournaments.get(TournamentComboBox.getSelectedIndex()), returnSelectedArena(), isCheckBoxSelected());
    }
}
