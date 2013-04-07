package com.softwaremagico.ktg.gui.base;
/*
 * #%L
 * Kendo Tournament Generator GUI
 * %%
 * Copyright (C) 2008 - 2013 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class CloseButton extends KButton {

    private static final long serialVersionUID = -3656938562430153336L;
    protected JFrame window;

    public CloseButton(JFrame window) {
        setTranslatedText("CloseButton");
        this.window = window;
        this.setPreferredSize(new Dimension(80, 40));
        addActionListener(new CloseListener());
    }

    protected void closeAction() {
        window.dispose();
    }

    class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            closeAction();
        }
    }
}
