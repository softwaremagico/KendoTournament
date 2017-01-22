package com.softwaremagico.ktg.gui;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2016 Softwaremagico
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.KTextField;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;

public class CloneTournamentNameWindow extends KFrame {
	private static final long serialVersionUID = -2853090165591580750L;
	private KButton acceptButton;
	private KTextField nameTextField;

	public CloneTournamentNameWindow() {
		defineWindow(350, 120);
		setResizable(false);
		setElements();
	}

	private void setElements() {
		getContentPane().removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);

		gridBagConstraints.gridheight = 1;
		getContentPane().add(createTournamentName(), gridBagConstraints);

		KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setMinimumSize(new Dimension(200, 50));
		acceptButton = new KButton();
		acceptButton.setTranslatedText("AcceptButton");
		acceptButton.setPreferredSize(new Dimension(80, 40));
		buttonPanel.add(acceptButton);

		CloseButton closeButton = new CloseButton(this);
		buttonPanel.add(closeButton);

		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	private KPanel createTournamentName() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);

		KPanel tournamentPanel = new KPanel(new GridBagLayout());
		tournamentPanel.setMinimumSize(new Dimension(200, 50));

		KLabel tournamentLabel = new KLabel("NameTournamentLabel");
		gridBagConstraints.gridx = 0;
		tournamentPanel.add(tournamentLabel, gridBagConstraints);

		nameTextField = new KTextField();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.weightx = 1;
		tournamentPanel.add(nameTextField, gridBagConstraints);

		return tournamentPanel;
	}

	public void addAcceptButtonListener(ActionListener listener) {
		acceptButton.addActionListener(listener);
	}

	public void setTournamentName(String text) {
		nameTextField.setText(text);
	}

	public String getTournamentName() {
		return nameTextField.getText();
	}

	@Override
	public void update() {
	}

	@Override
	public void elementChanged() {
	}

}
