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
 *  Created on 24-feb-2009.
 */
package com.softwaremagico.ktg.statistics;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.language.Translator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Jorge
 */
public class StatisticsReceivedHits extends StatisticsGUI {

    List<Duel> duels = new ArrayList<Duel>();
    List<Duel> duelsTeamRight = new ArrayList<Duel>();
    List<Duel> duelsTeamLeft = new ArrayList<Duel>();
    Competitor competitor;

    public StatisticsReceivedHits(Competitor tmp_competitor) {
        competitor = tmp_competitor;
        if (tmp_competitor == null) {
            duels = KendoTournamentGenerator.getInstance().database.getAllDuels();
        } else {
            duelsTeamRight = KendoTournamentGenerator.getInstance().database.getDuelsOfcompetitor(tmp_competitor.getId(), true);
            duelsTeamLeft = KendoTournamentGenerator.getInstance().database.getDuelsOfcompetitor(tmp_competitor.getId(), false);
        }
        start();
    }

    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int mems = 0, kotes = 0, tsukis = 0, hansokus = 0, does = 0, ippones = 0;
        for (int i = 0; i < duels.size(); i++) {
            Duel d = duels.get(i);
            mems += d.getMems();
            kotes += d.getKotes();
            tsukis += d.getTsukis();
            hansokus += d.getHansokus();
            does += d.getDoes();
            ippones += d.getIppones();
        }

        for (int i = 0; i < duelsTeamRight.size(); i++) {
            Duel d = duelsTeamRight.get(i);
            mems += d.getMems(true);
            kotes += d.getKotes(true);
            tsukis += d.getTsukis(true);
            hansokus += d.getHansokus(true);
            does += d.getDoes(true);
            ippones += d.getIppones(true);
        }

        for (int i = 0; i < duelsTeamLeft.size(); i++) {
            Duel d = duelsTeamLeft.get(i);
            mems += d.getMems(false);
            kotes += d.getKotes(false);
            tsukis += d.getTsukis(false);
            hansokus += d.getHansokus(false);
            does += d.getDoes(false);
            ippones += d.getIppones(false);

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

    private JFreeChart createChart(DefaultPieDataset dataset) {
        Translator transl = new Translator("gui.xml");
        // create the chart…
        String title;
        if (competitor != null) {
            title = transl.returnTag("ReceivedHitStatistics", KendoTournamentGenerator.getInstance().language) + ": " + competitor.returnName() + " " + competitor.returnSurname(); // Titulo de grafico
        } else {
            title = transl.returnTag("ReceivedHitStatistics", KendoTournamentGenerator.getInstance().language);
        }
        try {
            JFreeChart chart = ChartFactory.createPieChart(
                    title, // Titulo de grafico
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

    @Override
    public void generateStatistics() {
    }

    @Override
    public JPanel createPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    @Override
    public String defaultFileName() {
        return "PercentageOfReceivedHits.png";
    }

    @Override
    void numberSpinnedChanged() {
    }
}
