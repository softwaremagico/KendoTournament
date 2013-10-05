package com.softwaremagico.ktg.gui;

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

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.persistence.ClubPool;
import com.softwaremagico.ktg.persistence.RegisteredPersonPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class NewClub extends javax.swing.JFrame {

	private Translator trans = null;
	private Club club;
	private List<RegisteredPerson> competitors;
	private NewCompetitor newCompetitor = null;
	private boolean updateClubOfCompetitor;

	/**
	 * Creates new form NewClub
	 */
	public NewClub() {
		initComponents();
		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
		setLanguage();
		fillCompetitors();
		updateClubOfCompetitor = true;
	}

	/**
	 * Translate the GUI to the selected language.
	 */
	public final void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("titleClub"));
		AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
		CancelButton.setText(trans.getTranslatedText("CancelButton"));
		NameLabel.setText(trans.getTranslatedText("NameLabel"));
		CountryLabel.setText(trans.getTranslatedText("CountryLabel"));
		CityLabel.setText(trans.getTranslatedText("CityLabel"));
		AddressLabel.setText(trans.getTranslatedText("AddressLabel"));
		RepresentativeLabel.setText(trans.getTranslatedText("RepresentativeLabel"));
		PhoneLabel.setText(trans.getTranslatedText("PhoneLabel"));
		MailLabel.setText(trans.getTranslatedText("MailLabel"));
		SearchButton.setText(trans.getTranslatedText("SearchButton"));
	}

	public void UpdateWindow(Club club) {
		try {
			this.club = club;
			NameTextField.setText(club.getName());
			CountryTextField.setText(club.getCountry());
			CityTextField.setText(club.getCity());
			AddressTextField.setText(club.getAddress());
			FillCompetitorsFromClub(club);
			selectRepresentative(club);
			PhoneTextField.setText(club.getPhone());
			MailTextField.setText(club.getMail());
		} catch (NullPointerException npe) {
		}
	}

	private void CleanWindow() {
		NameTextField.setText("");
		CountryTextField.setText("");
		CityTextField.setText("");
		AddressTextField.setText("");
		PhoneTextField.setText("");
		MailTextField.setText("");
		RepresentativeComboBox.removeAllItems();
	}

	public void FillCompetitorsFromClub(Club club) {
		try {
			competitors = RegisteredPersonPool.getInstance().getByClub(club.getName());
			RepresentativeComboBox.removeAllItems();
			RepresentativeComboBox.addItem("");
			for (int i = 0; i < competitors.size(); i++) {
				RepresentativeComboBox.addItem(competitors.get(i).getSurname() + ", " + competitors.get(i).getName());
				if (club.getRepresentativeID() != null && club.getRepresentativeID().equals(competitors.get(i).getId())) {
					RepresentativeComboBox.setSelectedIndex(i);
				}
			}
			// Disable options if no competitors exists.
			enableRepresentative();
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		updateClubOfCompetitor = false;
	}

	private void fillCompetitors() {
		try {
			competitors = RegisteredPersonPool.getInstance().getPeopleWithoutClub();
			RepresentativeComboBox.removeAllItems();
			RepresentativeComboBox.addItem("");
			for (int i = 0; i < competitors.size(); i++) {
				RepresentativeComboBox.addItem(competitors.get(i).getSurname() + ", " + competitors.get(i).getName());
			}
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
		// Disable options if no competitors exists.
		enableRepresentative();
		updateClubOfCompetitor = true;
	}

	private void enableRepresentative() {
		RepresentativeComboBox.setEnabled(!competitors.isEmpty());
		PhoneTextField.setEnabled(!competitors.isEmpty());
		MailTextField.setEnabled(!competitors.isEmpty());
	}

	private void selectRepresentative(Club c) {
		for (int i = 0; i < competitors.size(); i++) {
			if (competitors.get(i).getId().equals(c.getRepresentativeID())) {
				RepresentativeComboBox.setSelectedIndex(i + 1);
			}
		}
	}

	public void acceptClub() {
		try {
			setAlwaysOnTop(false);
			if (NameTextField.getText().length() > 0 && CountryTextField.getText().length() > 0
					&& CityTextField.getText().length() > 0) {
				club = new Club(NameTextField.getText().trim(), CountryTextField.getText().trim(), CityTextField
						.getText().trim());
				club.setAddress(AddressTextField.getText());
				try {
					club.setRepresentative(competitors.get(RepresentativeComboBox.getSelectedIndex() - 1).getId(),
							MailTextField.getText(), PhoneTextField.getText());
				} catch (NullPointerException | ArrayIndexOutOfBoundsException npe) {
				}
				if (ClubPool.getInstance().add(club)) {
					AlertManager.informationMessage(this.getClass().getName(), "clubStored", "Club");
				}
				CleanWindow();
				if (newCompetitor != null) {
					// newCompetitor.fillClub();
					newCompetitor.addClub(club); // Update competitor window.
					if (updateClubOfCompetitor) { // Update club of selected
													// competitor.
						if (RepresentativeComboBox.getSelectedIndex() > 0) {
							competitors.get(RepresentativeComboBox.getSelectedIndex() - 1).setClub(club);
							RegisteredPersonPool.getInstance().update(
									competitors.get(RepresentativeComboBox.getSelectedIndex() - 1),
									competitors.get(RepresentativeComboBox.getSelectedIndex() - 1));
						}
					}
					this.dispose();
				}
			} else {
				AlertManager.errorMessage(this.getClass().getName(), "noClubFieldsFilled", "SQL");
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
	}

	public void updateClubsInCompetitor(NewCompetitor nc) {
		newCompetitor = nc;
	}

	/**
	 * **********************************************
	 * 
	 * LISTENERS
	 * 
	 *********************************************** 
	 */
	/**
	 * Add the same action listener to all langugaes of the menu.
	 * 
	 * @param al
	 */
	public void addSearchListener(ActionListener al) {
		SearchButton.addActionListener(al);
	}

	public void addAcceptListener(ActionListener al) {
		AcceptButton.addActionListener(al);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		ClubPanel = new javax.swing.JPanel();
		NameTextField = new javax.swing.JTextField();
		CountryTextField = new javax.swing.JTextField();
		NameLabel = new javax.swing.JLabel();
		CountryLabel = new javax.swing.JLabel();
		CityTextField = new javax.swing.JTextField();
		CityLabel = new javax.swing.JLabel();
		AddressTextField = new javax.swing.JTextField();
		AddressLabel = new javax.swing.JLabel();
		AcceptButton = new javax.swing.JButton();
		CancelButton = new javax.swing.JButton();
		RepresentativePanel = new javax.swing.JPanel();
		RepresentativeComboBox = new javax.swing.JComboBox();
		PhoneTextField = new javax.swing.JTextField();
		MailTextField = new javax.swing.JTextField();
		RepresentativeLabel = new javax.swing.JLabel();
		PhoneLabel = new javax.swing.JLabel();
		MailLabel = new javax.swing.JLabel();
		SearchButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Insert new Club");
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		ClubPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		NameLabel.setText("Name:");

		CountryLabel.setText("Country:");

		CityLabel.setText("City:");

		AddressLabel.setText("Address:");

		javax.swing.GroupLayout ClubPanelLayout = new javax.swing.GroupLayout(ClubPanel);
		ClubPanel.setLayout(ClubPanelLayout);
		ClubPanelLayout.setHorizontalGroup(ClubPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				ClubPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(NameLabel).addComponent(CountryLabel).addComponent(CityLabel)
										.addComponent(AddressLabel))
						.addGap(68, 68, 68)
						.addGroup(
								ClubPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(AddressTextField, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
										.addComponent(CityTextField, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
										.addComponent(CountryTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 252,
												Short.MAX_VALUE)
										.addComponent(NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 252,
												Short.MAX_VALUE)).addContainerGap()));
		ClubPanelLayout
				.setVerticalGroup(ClubPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								ClubPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												ClubPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(NameTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(NameLabel))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												ClubPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(CountryTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(CountryLabel))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												ClubPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(CityTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(CityLabel))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												ClubPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(AddressTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(AddressLabel))
										.addContainerGap(15, Short.MAX_VALUE)));

		AcceptButton.setText("Accept");

		CancelButton.setText("Close");
		CancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CancelButtonActionPerformed(evt);
			}
		});

		RepresentativePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		RepresentativeComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				RepresentativeComboBoxActionPerformed(evt);
			}
		});

		PhoneTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				PhoneTextFieldKeyReleased(evt);
			}
		});

		MailTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				MailTextFieldKeyReleased(evt);
			}
		});

		RepresentativeLabel.setText("Representative:");

		PhoneLabel.setText("Phone:");

		MailLabel.setText("Mail:");

		javax.swing.GroupLayout RepresentativePanelLayout = new javax.swing.GroupLayout(RepresentativePanel);
		RepresentativePanel.setLayout(RepresentativePanelLayout);
		RepresentativePanelLayout.setHorizontalGroup(RepresentativePanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				RepresentativePanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								RepresentativePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(RepresentativeLabel).addComponent(PhoneLabel)
										.addComponent(MailLabel))
						.addGap(36, 36, 36)
						.addGroup(
								RepresentativePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(MailTextField, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
										.addComponent(PhoneTextField, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
										.addComponent(RepresentativeComboBox,
												javax.swing.GroupLayout.Alignment.TRAILING, 0, 235, Short.MAX_VALUE))
						.addContainerGap()));
		RepresentativePanelLayout.setVerticalGroup(RepresentativePanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				RepresentativePanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								RepresentativePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(RepresentativeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(RepresentativeLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								RepresentativePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(PhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(PhoneLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								RepresentativePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(MailTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(MailLabel))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		SearchButton.setText("Search");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addGroup(
												layout.createSequentialGroup()
														.addComponent(SearchButton)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(AcceptButton)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(CancelButton,
																javax.swing.GroupLayout.PREFERRED_SIZE, 85,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(RepresentativePanel, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(ClubPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { AcceptButton, CancelButton,
				SearchButton });

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(ClubPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGap(18, 18, 18)
						.addComponent(RepresentativePanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(AcceptButton).addComponent(SearchButton)
										.addComponent(CancelButton)).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CancelButtonActionPerformed
		this.dispose();
	}// GEN-LAST:event_CancelButtonActionPerformed

	private void RepresentativeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_RepresentativeComboBoxActionPerformed
		// UpdateRepresentative();
	}// GEN-LAST:event_RepresentativeComboBoxActionPerformed

	private void PhoneTextFieldKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_PhoneTextFieldKeyReleased
		// UpdateRepresentative();
	}// GEN-LAST:event_PhoneTextFieldKeyReleased

	private void MailTextFieldKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_MailTextFieldKeyReleased
		// UpdateRepresentative();
	}// GEN-LAST:event_MailTextFieldKeyReleased

	private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
		this.toFront();
	}// GEN-LAST:event_formWindowOpened
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private javax.swing.JButton AcceptButton;
	private javax.swing.JLabel AddressLabel;
	private javax.swing.JTextField AddressTextField;
	private javax.swing.JButton CancelButton;
	private javax.swing.JLabel CityLabel;
	private javax.swing.JTextField CityTextField;
	private javax.swing.JPanel ClubPanel;
	private javax.swing.JLabel CountryLabel;
	private javax.swing.JTextField CountryTextField;
	private javax.swing.JLabel MailLabel;
	private javax.swing.JTextField MailTextField;
	private javax.swing.JLabel NameLabel;
	private javax.swing.JTextField NameTextField;
	private javax.swing.JLabel PhoneLabel;
	private javax.swing.JTextField PhoneTextField;
	private javax.swing.JComboBox RepresentativeComboBox;
	private javax.swing.JLabel RepresentativeLabel;
	private javax.swing.JPanel RepresentativePanel;
	private javax.swing.JButton SearchButton;
	// End of variables declaration//GEN-END:variables
}
