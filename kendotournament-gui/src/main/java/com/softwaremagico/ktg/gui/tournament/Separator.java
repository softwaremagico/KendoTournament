package com.softwaremagico.ktg.gui.tournament;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.gui.tournament.Group;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

public class Separator extends Group {

    public Separator() {
        xSize = 75;
        ySize = 0;

        setPreferredSize(new Dimension(xSize, ySize));
        setMaximumSize(new Dimension(250, ySize));
        setMinimumSize(new Dimension(10, 0));
        setBackground(new Color(255, 255, 255));
    }

    public Separator(String text) {
        setBackground(new Color(255, 255, 255));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.LINE_START;
        label.setHorizontalTextPosition(JLabel.LEFT);
        add(label, c);
        updateText(text);
        updateSize();
    }

    final void updateSize() {
        //xSize = 110 * Math.max(2, teams.size());
        xSize = 200;
        ySize = 50 + 30;
        setPreferredSize(new Dimension(xSize, ySize));
        setMaximumSize(new Dimension(xSize, ySize * 2));
        setMinimumSize(new Dimension(0, 0));
    }
}
