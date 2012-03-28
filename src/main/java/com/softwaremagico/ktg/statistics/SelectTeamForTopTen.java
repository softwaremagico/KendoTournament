/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.statistics;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.pdflist.ListFromTeams;

/**
 *
 * @author jorge
 */
public class SelectTeamForTopTen extends ListFromTeams {

    public SelectTeamForTopTen() {
        Start(true);
        this.setTitle(trans.returnTag("titleHitStatistics", KendoTournamentGenerator.getInstance().language));
    }

    @Override
    public String defaultFileName() {
        return "TeamTopTen.png";
    }

    @Override
    public void Generate() {
    }

}
