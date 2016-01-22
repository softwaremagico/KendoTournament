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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.DuelPool;

public class StatisticsGeneralHits extends StatisticsGUI {
	private static final long serialVersionUID = -2681437218335984145L;
	private List<Duel> duels = new ArrayList<>();
    private Tournament tournament;

    public StatisticsGeneralHits(Tournament tournament) {
        this.tournament = tournament;
        try {
            if (tournament == null) { //null == all.
                duels = DuelPool.getInstance().getAll();
            } else {
                duels = DuelPool.getInstance().get(tournament);
            }
            start();
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
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
        ITranslator transl = LanguagePool.getTranslator("gui.xml");
        // create the chart…
        JFreeChart chart = ChartFactory.createPieChart(
                transl.getTranslatedText("TitleHits") + ": " + tournament, // Titulo de grafico
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
