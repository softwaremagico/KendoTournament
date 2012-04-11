/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Tournament;

/**
 *
 * @author jorge
 */
public class EmptyFightsListPDF extends SummaryPDF {

    EmptyFightsListPDF(Tournament championship, int shiaijo, boolean showAll) {
        super(championship, shiaijo);
        this.showAll = showAll;
    }

    @Override
    protected String getDrawFight(Fight f, int duel) {
        return "";
    }

    @Override
    protected String getFaults(Fight f, int duel, boolean leftTeam) {
        return "";
    }

    @Override
    protected String getScore(Fight f, int duel, int score, boolean leftTeam) {
        return "";
    }
}
