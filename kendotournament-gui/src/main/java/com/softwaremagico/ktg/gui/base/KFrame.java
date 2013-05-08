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
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public abstract class KFrame extends JFrame {

    private static final String FRAME_TITLE = "Kendo Tournament Generator";
    protected static final int margin = 5;
    protected Integer textDefaultWidth = 80;
    protected Integer textDefaultHeight = 25;
    protected Integer inputDefaultWidth = 160;
    protected Integer inputColumns = 12;
    protected Integer xPadding = 5;
    protected Integer yPadding = 10;

    public KFrame() {
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(this.getClass().getResource("/kendo.png")).getImage());
    }

    public void defineWindow(Integer width, Integer height) {
        setSize(width, height);
        setMinimumSize(new Dimension(width, height));
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
                - (int) (this.getWidth() / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                / 2 - (int) (this.getHeight() / 2));
    }

    public abstract void update();

    public abstract void tournamentChanged();
}
