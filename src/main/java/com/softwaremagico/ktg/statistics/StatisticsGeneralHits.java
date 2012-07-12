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

import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Score;
import com.softwaremagico.ktg.language.Translator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Jorge
 */
public class StatisticsGeneralHits extends StatisticsGUI {

    private List<Duel> duels = new ArrayList<>();
    private String championship;

    public StatisticsGeneralHits(String tmp_championship) {
        championship = tmp_championship;
        if (tmp_championship.equals("All")) {
            duels = KendoTournamentGenerator.getInstance().database.getAllDuels();
        } else {
            duels = KendoTournamentGenerator.getInstance().database.getDuelsOfTournament(tmp_championship);
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

        dataset.setValue(Score.MEN.getName(), (float) mems);
        dataset.setValue(Score.KOTE.getName(), (float) kotes);
        dataset.setValue(Score.TSUKI.getName(), (float) tsukis);
        dataset.setValue(Score.DO.getName(), (float) does);
        dataset.setValue(Score.IPPON.getName(), (float) ippones);
        dataset.setValue(Score.HANSOKU.getName(), (float) hansokus);
        return dataset;
    }

    private JFreeChart createChart(DefaultPieDataset dataset) {
        Translator transl = new Translator("gui.xml");
        // create the chart…
        JFreeChart chart = ChartFactory.createPieChart(
                transl.returnTag("TitleHits")+": " + championship, // Titulo de grafico
                dataset, // data
                true, // incluye leyenda
                true, // visualiza tooltips
                false // urls
                );
        return chart;
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
        return "PercentageOfHits.png";
    }
    
    @Override
    void numberSpinnedChanged() {
    }
}
