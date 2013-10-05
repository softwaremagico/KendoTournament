package com.softwaremagico.ktg.gui.tournament;
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

import java.awt.Font;
import javax.swing.JLabel;

public abstract class Group extends javax.swing.JPanel {

    int xSize;
    int ySize;
    JLabel label = new JLabel();

    Group() {
    }

    public final void updateText(String text) {
        label.setText(formatText(text));
    }

    void updateFont(String font, int size) {
        label.setFont(new Font(font, Font.BOLD, size));
    }

    String formatText(String text) {
        String tag = "<html><b><font size=\"+1\" color=\"#000000\">";
        text = tag + text;
        text += "</font></b></html>";
        return text;
    }
}
