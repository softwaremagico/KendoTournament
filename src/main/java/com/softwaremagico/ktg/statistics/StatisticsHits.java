/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.statistics;

import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.language.Translator;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author jhortelano
 */
public abstract class StatisticsHits extends StatisticsGUI {

    List<Duel> duels = new ArrayList<>();
    List<Duel> duelsOfCompetitorWhenIsInTeamRight = new ArrayList<>();
    List<Duel> duelsOfCOmpetitorWhenIsInTeamLeft = new ArrayList<>();
    Competitor competitor;

    /**
     * Obtain all duels of a specific competitor.
     * @param competitor 
     */
    protected void obtainDuels(Competitor competitor) {
        this.competitor = competitor;
        if (this.competitor == null) {
            duels = KendoTournamentGenerator.getInstance().database.getAllDuels();
        } else {
            duelsOfCompetitorWhenIsInTeamRight = KendoTournamentGenerator.getInstance().database.getDuelsOfcompetitor(competitor.getId(), true);
            duelsOfCOmpetitorWhenIsInTeamLeft = KendoTournamentGenerator.getInstance().database.getDuelsOfcompetitor(competitor.getId(), false);
        }
    }

    /**
     * Obtain the score of the competitor. Add all the score of the fights when competitor is in the left and when competitor is in the right. 
     * @param performedHits false if using received Hits, true if counts hits done. 
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

        for (int i = 0; i < duelsOfCOmpetitorWhenIsInTeamLeft.size(); i++) {
            Duel d = duelsOfCOmpetitorWhenIsInTeamLeft.get(i);
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
        Translator transl = new Translator("gui.xml");
        String title;
        if (competitor != null) {
            title = transl.returnTag(tag) + ": " + competitor.getName() + " " + competitor.getSurname(); // Titulo de grafico
        } else {
            title = transl.returnTag(tag);
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
