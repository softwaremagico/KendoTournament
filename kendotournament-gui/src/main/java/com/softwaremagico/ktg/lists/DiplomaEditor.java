package com.softwaremagico.ktg.lists;
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

import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.RoleTags;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.RolePool;

public class DiplomaEditor extends javax.swing.JFrame {
	private static final long serialVersionUID = 1530108136900847063L;
	private DiplomaGenerator diplomaGui = null;
	private ITranslator trans = null;
	private RoleTags roles = null;
	private List<JCheckBox> rolesSelected = new ArrayList<>();
	private DiplomaBlackBoard DiplomaPanel = new DiplomaBlackBoard();

	/**
	 * Creates new form DiplomaEditor
	 */
	public DiplomaEditor() {
		initComponents();
		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
		Slider.setValue(KendoTournamentGenerator.getInstance().getLastNamePositionOnDiploma());
		setLanguage();
		addRoles();
		DiplomaPanel.setBounds(new Rectangle(mainPanel.getSize().width, mainPanel.getSize().height));
		mainPanel.add(DiplomaPanel);
	}

	/**
	 * Translate the GUI to the selected language.
	 */
	public final void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("titleDiplomaEditor"));
		CloseButton.setText(trans.getTranslatedText("CloseButton"));
		AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
		InformationLabel.setText(trans.getTranslatedText("InformationDiplomaLabel"));
		StatisticsCheckBox.setText(trans.getTranslatedText("StatisticsCheckBox"));
		RoleLabel.setText(trans.getTranslatedText("DiplomaRole"));
	}

	private void addRoles() {
		GridBagConstraints lc = new GridBagConstraints();
		lc.gridwidth = GridBagConstraints.REMAINDER;
		lc.fill = GridBagConstraints.BOTH;

		roles = RolePool.getInstance().getRoleTags();

		for (int i = 0; i < roles.size(); i++) {
			JCheckBox cb = new JCheckBox(roles.get(i).name);
			rolesSelected.add(cb);
			RolePanel.add(cb, lc);
		}
	}

	private List<RoleTag> obtainSelectedRoles() {
		List<RoleTag> selectedRoles = new ArrayList<>();
		for (int i = 0; i < rolesSelected.size(); i++) {
			JCheckBox cb = rolesSelected.get(i);
			if (cb.isSelected()) {
				selectedRoles.add(roles.get(i));
			}
		}
		return selectedRoles;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		Slider = new javax.swing.JSlider();
		mainPanel = new javax.swing.JPanel();
		AcceptButton = new javax.swing.JButton();
		CloseButton = new javax.swing.JButton();
		InformationLabel = new javax.swing.JLabel();
		StatisticsCheckBox = new javax.swing.JCheckBox();
		RoleScrollPane = new javax.swing.JScrollPane();
		RolePanel = new javax.swing.JPanel();
		RoleLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Diploma Editor");
		setMinimumSize(new java.awt.Dimension(400, 350));
		setResizable(false);
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				formComponentResized(evt);
			}
		});

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		Slider.setMaximum(200);
		Slider.setOrientation(javax.swing.JSlider.VERTICAL);
		Slider.setValue(100);
		Slider.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				SliderMouseReleased(evt);
			}
		});

		javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout.setHorizontalGroup(mainPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addComponent(Slider, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(Slider, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(mainPanel,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));

		AcceptButton.setText("Accept");
		AcceptButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AcceptButtonActionPerformed(evt);
			}
		});

		CloseButton.setText("Close");
		CloseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CloseButtonActionPerformed(evt);
			}
		});

		InformationLabel.setText("Name position:");

		StatisticsCheckBox.setText("Statistics");

		RoleScrollPane.setBorder(null);

		RolePanel.setBorder(null);
		RolePanel.setLayout(new java.awt.GridBagLayout());
		RoleScrollPane.setViewportView(RolePanel);

		RoleLabel.setText("Roles:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(StatisticsCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 183,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(AcceptButton)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(CloseButton))
						.addGroup(layout.createSequentialGroup().addComponent(InformationLabel).addGap(0, 0,
								Short.MAX_VALUE))
						.addGroup(layout.createSequentialGroup()
								.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup().addComponent(RoleLabel)
														.addGap(0, 137, Short.MAX_VALUE))
												.addComponent(RoleScrollPane))))
						.addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { AcceptButton, CloseButton });

		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup().addComponent(InformationLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
										.addComponent(RoleLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(RoleScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(AcceptButton).addComponent(CloseButton).addComponent(StatisticsCheckBox))
						.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_AcceptButtonActionPerformed
		try {
			diplomaGui.dispose();
		} catch (NullPointerException npe) {
		}
		diplomaGui = new DiplomaGenerator((float) Slider.getValue() / (float) Slider.getMaximum(),
				StatisticsCheckBox.isSelected(), obtainSelectedRoles());
		diplomaGui.setVisible(true);
		this.dispose();
	}// GEN-LAST:event_AcceptButtonActionPerformed

	private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CloseButtonActionPerformed
		this.dispose();
	}// GEN-LAST:event_CloseButtonActionPerformed

	private void formComponentResized(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_formComponentResized
		DiplomaPanel.repaint();
		DiplomaPanel.changeLine(Slider.getValue());
		DiplomaPanel.revalidate();
	}// GEN-LAST:event_formComponentResized

	private void SliderMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_SliderMouseReleased
		DiplomaPanel.changeLine(Slider.getValue());
		KendoTournamentGenerator.getInstance().setLastNamePositionOnDiploma(Slider.getValue());
	}// GEN-LAST:event_SliderMouseReleased
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private javax.swing.JButton AcceptButton;
	private javax.swing.JButton CloseButton;
	private javax.swing.JLabel InformationLabel;
	private javax.swing.JLabel RoleLabel;
	private javax.swing.JPanel RolePanel;
	private javax.swing.JScrollPane RoleScrollPane;
	private javax.swing.JSlider Slider;
	private javax.swing.JCheckBox StatisticsCheckBox;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel mainPanel;
	// End of variables declaration//GEN-END:variables
}
