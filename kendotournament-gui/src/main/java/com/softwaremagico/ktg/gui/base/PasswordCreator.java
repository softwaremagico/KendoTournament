package com.softwaremagico.ktg.gui.base;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2014 Softwaremagico
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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.buttons.AcceptButton;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;

public class PasswordCreator extends KFrame {
	private static final long serialVersionUID = 2025016043586443325L;
	private static final Integer MAX_PASS_LENGTH = 15;
	private JPasswordField passwordField, confirmPassword;
	private PasswordCreator reference;

	public PasswordCreator() {
		defineWindow(300, 200);
		setResizable(false);
		setVisible(true);
		//setAlwaysOnTop(true);
		setElements();
	}

	private void setElements() {
		reference = this;
		// Pasword fields.
		passwordField = new JPasswordField(MAX_PASS_LENGTH);
		KLabel label = new KLabel("PasswordLabel");
		label.setLabelFor(passwordField);

		confirmPassword = new JPasswordField(MAX_PASS_LENGTH);
		JLabel label2 = new JLabel("Confirm: ");
		label2.setLabelFor(confirmPassword);

		KPanel textPane = new KPanel(new FlowLayout());
		textPane.add(label);
		textPane.add(passwordField);
		textPane.add(label2);
		textPane.add(confirmPassword);

		// Buttons
		JComponent buttonPanel = new KPanel(new FlowLayout());
		AcceptButton okButton = new AcceptButton() {
			private static final long serialVersionUID = -8633564569185503959L;

			@Override
			public void acceptAction() {
				if (String.copyValueOf(passwordField.getPassword()).equals(
						String.copyValueOf(confirmPassword.getPassword()))) {
					KendoTournamentGenerator.setBlockingString(passwordField.getPassword());
					reference.dispose();
				} else {
					AlertManager.errorMessage(PasswordCreator.class.getName(), "passwordDoesNotMatch", "Password");
					confirmPassword.setText("");
					confirmPassword.requestFocusInWindow();
				}
			}
		};
		CloseButton cancelButton = new CloseButton(this);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		// Main panel
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
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(label, gridBagConstraints);

		gridBagConstraints.gridy = 0;
		getContentPane().add(label, gridBagConstraints);
		gridBagConstraints.gridy = 1;
		getContentPane().add(passwordField, gridBagConstraints);
		gridBagConstraints.gridy = 2;
		getContentPane().add(label2, gridBagConstraints);
		gridBagConstraints.gridy = 3;
		getContentPane().add(confirmPassword, gridBagConstraints);
		gridBagConstraints.gridy = 4;
		getContentPane().add(buttonPanel, gridBagConstraints);

	}

	protected JComponent createButtonPanel() {
		KPanel buttonPanel = new KPanel(new GridLayout(1, 2));
		KButton okButton = new KButton();
		okButton.setTranslatedText("Accept");
		KButton cancelButton = new KButton();
		cancelButton.setTranslatedText("Cancel");

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	@Override
	public void update() {

	}

	@Override
	public void elementChanged() {
	}
}
