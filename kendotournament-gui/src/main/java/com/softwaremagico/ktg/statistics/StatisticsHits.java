package com.softwaremagico.ktg.statistics;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.DuelPool;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public abstract class StatisticsHits extends StatisticsGUI {

    private List<Duel> duels = new ArrayList<>();
    private List<Duel> duelsOfCompetitorWhenIsInTeamRight = new ArrayList<>();
    private List<Duel> duelsOfCompetitorWhenIsInTeamLeft = new ArrayList<>();
    protected RegisteredPerson competitor;

    /**
     * Obtain all duels of a specific competitor.
     *
     * @param competitor
     */
    protected void obtainDuels(RegisteredPerson competitor) {
        this.competitor = competitor;
        try {
            if (this.competitor == null) {
                duels = DuelPool.getInstance().getAll();
            } else {
                duelsOfCompetitorWhenIsInTeamRight = DuelPool.getInstance().get(competitor, true);
                duelsOfCompetitorWhenIsInTeamLeft = DuelPool.getInstance().get(competitor, false);
            }
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }

    /**
     * Obtain the score of the competitor. Add all the score of the fights when
     * competitor is in the left and when competitor is in the right.
     *
     * @param performedHits false if using received Hits, true if counts hits
     * done.
     * @return
     */
    protected DefaultPieDataset createDataset(boolean performedHits) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        float mems = 0, kotes = 0, tsukis = 0, hansokus = 0, does = 0, ippones = 0;
        for (int i = 0; i < duels.size(); i++) {
            Duel d = duels.get(i);
            mems += d.getMems();
            kotes += d.getKotes();
            tsukis += d.getTsukis();
            hansokus += d.getHansokus();
            does += d.getDoes();
            ippones += d.getIppones();
        }

        for (int i = 0; i < duelsOfCompetitorWhenIsInTeamRight.size(); i++) {
            Duel d = duelsOfCompetitorWhenIsInTeamRight.get(i);
            mems += d.getMems(!performedHits);
            kotes += d.getKotes(!performedHits);
            tsukis += d.getTsukis(!performedHits);
            hansokus += d.getHansokus(!performedHits);
            does += d.getDoes(!performedHits);
            ippones += d.getIppones(!performedHits);
        }

        for (int i = 0; i < duelsOfCompetitorWhenIsInTeamLeft.size(); i++) {
            Duel d = duelsOfCompetitorWhenIsInTeamLeft.get(i);
            mems += d.getMems(performedHits);
            kotes += d.getKotes(performedHits);
            tsukis += d.getTsukis(performedHits);
            hansokus += d.getHansokus(performedHits);
            does += d.getDoes(performedHits);
            ippones += d.getIppones(performedHits);
        }

        float total = mems + kotes + tsukis + hansokus + does + ippones;

        dataset.setValue("Men (" + mems * 100 / total + "%)", (float) mems);
        dataset.setValue("Kote (" + kotes * 100 / total + "%)", (float) kotes);
        dataset.setValue("Tsuki (" + tsukis * 100 / total + "%)", (float) tsukis);
        dataset.setValue("Do (" + does * 100 / total + "%)", (float) does);
        dataset.setValue("Ippon (" + ippones * 100 / total + "%)", (float) ippones);
        dataset.setValue("Hansoku (" + hansokus * 100 / total + "%)", (float) hansokus);
        return dataset;
    }

    protected JFreeChart createChart(DefaultPieDataset dataset, String tag) {
        Translator transl = LanguagePool.getTranslator("gui.xml");
        String title;
        if (competitor != null) {
            title = transl.getTranslatedText(tag) + ": " + competitor.getName() + " " + competitor.getSurname(); // Titulo de grafico
        } else {
            title = transl.getTranslatedText(tag);
        }
        // create the chart…
        try {
            JFreeChart chart = ChartFactory.createPieChart(
                    title,
                    dataset, // data
                    true, // incluye leyenda
                    true, // visualiza tooltips
                    false // urls
                    );
            return chart;
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
