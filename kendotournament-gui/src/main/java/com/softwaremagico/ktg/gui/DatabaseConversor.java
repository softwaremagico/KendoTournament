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

import java.awt.Rectangle;
import java.awt.Toolkit;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.ConvertDatabase;
import com.softwaremagico.ktg.persistence.Database;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import com.softwaremagico.ktg.persistence.DatabaseEngine;

public class DatabaseConversor extends javax.swing.JFrame {
	private static final long serialVersionUID = 8401826114483817621L;
	private DatabaseConnectionPanel fromDatabaseConnectionPanel = new DatabaseConnectionPanel();
	private DatabaseConnectionPanel toDatabaseConnectionPanel = new DatabaseConnectionPanel();

	/**
	 * Creates new form DatabaseConversor
	 */
	public DatabaseConversor() {
		initComponents();
		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
		setLanguage();
		SourcePanel.add(fromDatabaseConnectionPanel);
		fromDatabaseConnectionPanel.setBounds(new Rectangle(SourcePanel.getSize().width, SourcePanel.getSize().height));
		if (DatabaseConnection.getInstance().getPassword() != null) {
			fromDatabaseConnectionPanel.setPassword(DatabaseConnection.getInstance().getPassword());
		}
		// fromDatabaseConnectionPanel.resetPassword();
		fromDatabaseConnectionPanel.setSelectedEngine(DatabaseConnection.getInstance().getDatabaseEngine().name());

		DestinationPanel.add(toDatabaseConnectionPanel);
		toDatabaseConnectionPanel
				.setBounds(new Rectangle(DestinationPanel.getSize().width, DestinationPanel.getSize().height));
		toDatabaseConnectionPanel.resetPassword();
		toDatabaseConnectionPanel.setSelectedEngine(
				DatabaseEngine.getOtherDatabase(DatabaseConnection.getInstance().getDatabaseEngine().name()).name());

	}

	private void setLanguage() {
		ITranslator trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("ConvertDatabase"));
		FromDatabaseLabel.setText(trans.getTranslatedText("FromDatabaseLabel"));
		ToDatabaseLabel.setText(trans.getTranslatedText("ToDatabaseLabel"));
		ExportButton.setText(trans.getTranslatedText("ConvertButton"));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		SourcePanel = new javax.swing.JPanel();
		DestinationPanel = new javax.swing.JPanel();
		FromDatabaseLabel = new javax.swing.JLabel();
		ToDatabaseLabel = new javax.swing.JLabel();
		CloseButton = new javax.swing.JButton();
		ExportButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		SourcePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		SourcePanel.setPreferredSize(new java.awt.Dimension(400, 180));

		javax.swing.GroupLayout SourcePanelLayout = new javax.swing.GroupLayout(SourcePanel);
		SourcePanel.setLayout(SourcePanelLayout);
		SourcePanelLayout.setHorizontalGroup(SourcePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 396, Short.MAX_VALUE));
		SourcePanelLayout.setVerticalGroup(SourcePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 176, Short.MAX_VALUE));

		DestinationPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		DestinationPanel.setPreferredSize(new java.awt.Dimension(400, 180));

		javax.swing.GroupLayout DestinationPanelLayout = new javax.swing.GroupLayout(DestinationPanel);
		DestinationPanel.setLayout(DestinationPanelLayout);
		DestinationPanelLayout.setHorizontalGroup(DestinationPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 396, Short.MAX_VALUE));
		DestinationPanelLayout.setVerticalGroup(DestinationPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 176, Short.MAX_VALUE));

		FromDatabaseLabel.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
		FromDatabaseLabel.setText("From Database:");

		ToDatabaseLabel.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
		ToDatabaseLabel.setText("To Database:");

		CloseButton.setText("Close");
		CloseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CloseButtonActionPerformed(evt);
			}
		});

		ExportButton.setText("Export");
		ExportButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ExportButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
						.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(SourcePanel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(DestinationPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(ToDatabaseLabel).addComponent(FromDatabaseLabel))
						.addGap(0, 0, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
										.addComponent(ExportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86,
												javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(CloseButton)))
				.addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { DestinationPanel, SourcePanel });

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { CloseButton, ExportButton });

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(FromDatabaseLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(SourcePanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18).addComponent(ToDatabaseLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(DestinationPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(CloseButton).addComponent(ExportButton))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		layout.linkSize(javax.swing.SwingConstants.VERTICAL,
				new java.awt.Component[] { DestinationPanel, SourcePanel });

		layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] { CloseButton, ExportButton });

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CloseButtonActionPerformed
		this.dispose();
	}// GEN-LAST:event_CloseButtonActionPerformed

	private void ExportButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ExportButtonActionPerformed
		Database fromDatabase = DatabaseEngine.getDatabaseClass(fromDatabaseConnectionPanel.getSelectedEngine());
		Database toDatabase = DatabaseEngine.getDatabaseClass(toDatabaseConnectionPanel.getSelectedEngine());
		ConvertDatabase conversor = new ConvertDatabase(fromDatabase, toDatabase);
		try {
			conversor.stablishConnection(fromDatabaseConnectionPanel.getPassword(),
					fromDatabaseConnectionPanel.getUser(), fromDatabaseConnectionPanel.getDatabase(),
					fromDatabaseConnectionPanel.getServer(), toDatabaseConnectionPanel.getPassword(),
					toDatabaseConnectionPanel.getUser(), toDatabaseConnectionPanel.getDatabase(),
					toDatabaseConnectionPanel.getServer());
		} catch (CommunicationsException ex) {
			AlertManager.errorMessage(this.getClass().getName(), "databaseConnectionFailure", "MySQL");
		}
	}// GEN-LAST:event_ExportButtonActionPerformed

	private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
		this.toFront();
	}// GEN-LAST:event_formWindowOpened
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private javax.swing.JButton CloseButton;
	private javax.swing.JPanel DestinationPanel;
	private javax.swing.JButton ExportButton;
	private javax.swing.JLabel FromDatabaseLabel;
	private javax.swing.JPanel SourcePanel;
	private javax.swing.JLabel ToDatabaseLabel;
	// End of variables declaration//GEN-END:variables
}
