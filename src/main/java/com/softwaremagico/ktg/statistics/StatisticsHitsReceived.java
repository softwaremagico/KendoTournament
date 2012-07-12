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

import com.softwaremagico.ktg.Competitor;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Jorge
 */
public class StatisticsHitsReceived extends StatisticsHits {

    public StatisticsHitsReceived(Competitor tmp_competitor) {
        obtainDuels(competitor);
        start();
    }

    @Override
    public void generateStatistics() {
    }

    @Override
    public JPanel createPanel() {
        JFreeChart chart = createChart(createDataset(false), "ReceivedHitStatistics");
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
