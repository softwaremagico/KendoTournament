package com.softwaremagico.ktg.gui.fight;

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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.tournament.BlackBoardPanel;

public class TreeWindow extends KFrame {
	private BlackBoardPanel bbp;
	private JScrollPane BlackBoardScrollPane;
	private Tournament tournament;

	public TreeWindow(Tournament tournament) {
		this.tournament = tournament;
		defineWindow(750, 400);
		setResizable(true);
		setElements();
		update();
	}

	private void setElements() {
		bbp = new BlackBoardPanel(null, false);
		BlackBoardScrollPane = new JScrollPane();
		BlackBoardScrollPane.setViewportView(bbp);
		BlackBoardScrollPane.setBackground(new java.awt.Color(255, 255, 255));

		getContentPane().removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		getContentPane().add(BlackBoardScrollPane, gridBagConstraints);

		KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setMinimumSize(new Dimension(200, 50));
		CloseButton closeButton = new CloseButton(this);
		buttonPanel.add(closeButton);

		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	@Override
	public void update() {
		try {
			bbp.update(tournament);
			BlackBoardScrollPane.revalidate();
			BlackBoardScrollPane.repaint();
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		}
	}

	@Override
	public void tournamentChanged() {

	}
}
