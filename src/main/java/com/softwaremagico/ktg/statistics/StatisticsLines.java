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
 *  Created on 25-feb-2008.
 */
package com.softwaremagico.ktg.statistics;

import javax.swing.JPanel;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Statistics;
import com.softwaremagico.ktg.language.Translator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Jorge
 */
public class StatisticsLines extends StatisticsGUI {

    Translator transl;

    public StatisticsLines(KendoTournamentGenerator tmp_tournament) {
        transl = new Translator("gui.xml");
        start();
    }

    @Override
    public void generateStatistics() {
        Statistics stats = new Statistics();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String defaultFileName() {
        return "TopTen.png";
    }

    private static XYDataset createDataset() {
        XYSeries series1 = new XYSeries("Primero");
        series1.add(1.0, 1.0);
        series1.add(2.0, 4.0);
        series1.add(3.0, 3.0);
        series1.add(4.0, 5.0);
        series1.add(5.0, 5.0);
        series1.add(6.0, 7.0);
        series1.add(7.0, 7.0);
        series1.add(8.0, 8.0);
        XYSeries series2 = new XYSeries("Segundo");
        series2.add(1.0, 5.0);
        series2.add(2.0, 7.0);
        series2.add(3.0, 6.0);
        series2.add(4.0, 8.0);
        series2.add(5.0, 4.0);
        series2.add(6.0, 4.0);
        series2.add(7.0, 2.0);
        series2.add(8.0, 1.0);
        XYSeries series3 = new XYSeries("Tercero");
        series3.add(3.0, 4.0);
        series3.add(4.0, 3.0);
        series3.add(5.0, 2.0);
        series3.add(6.0, 3.0);
        series3.add(7.0, 6.0);
        series3.add(8.0, 3.0);
        series3.add(9.0, 4.0);
        series3.add(10.0, 3.0);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {
        // create the chart…
        JFreeChart chart = ChartFactory.createXYLineChart(
                transl.returnTag("TopTenTitle", KendoTournamentGenerator.getInstance().language), // Titulo de grafico
                "X", // Etiqueta del eje x
                "Y", // Etiqueta del eje y
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // incluye leyenda
                true, // visualiza tooltips
                false // urls
                );
        return chart;
    }

    @Override
    public JPanel createPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    @Override
    void numberSpinnedChanged() {
    }
}
