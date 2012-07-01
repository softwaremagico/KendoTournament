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
 *  Created on 27-jul-2009.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.language.Translator;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 *
 * @author Jorge
 */
public class CompetitorPanel extends JPanel {

    private Translator trans = null;
    JComboBox<String> competitorComboBox;
    JLabel competitorLabel;
    int position;

    CompetitorPanel(String language, int tmp_position) {
        competitorComboBox = new JComboBox<>();
        competitorLabel = new JLabel();
        position = tmp_position;

        setLanguage(language);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        //c.weightx = 0.5;
        c.ipadx = 10;
        c.ipady = 15;
        add(competitorLabel, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;

        add(competitorComboBox, c);
    }

    void updateSize() {
        setPreferredSize(new Dimension(550, 550));
        setMaximumSize(new Dimension(100, 550));
        setMinimumSize(new Dimension(0, 0));
    }

    public final void setLanguage(String language) {
        trans = new Translator("gui.xml");
        competitorLabel.setText(position + "º " + trans.returnTag("CompetitorLabel", language));

    }
}
