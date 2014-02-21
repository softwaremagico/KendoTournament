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

import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;

public abstract class Search<T> extends javax.swing.JFrame {
	private static final long serialVersionUID = -1190635923759340815L;
	protected Translator trans = null;
	protected DefaultListModel<String> resultModel = new DefaultListModel<>();
	protected List<T> results;

	/**
	 * Creates new form SearchClub
	 */
	public Search() {
		initComponents();
		addListerners();
		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
		setLanguage();
	}

	/**
	 * Translate the GUI to the selected language.
	 */
	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("titleSearch"));
		CancelButton.setText(trans.getTranslatedText("CancelButton"));
		SearchButton.setText(trans.getTranslatedText("SearchButton"));
		SelectButton.setText(trans.getTranslatedText("SelectButton"));
		DeleteButton.setText(trans.getTranslatedText("DeleteButton"));
	}

	protected abstract void fillSearchFieldPanel();

	protected abstract String getResultInformation(T object);

	/**
	 * Fill the list with the results obtained
	 */
	protected void fillResults(List<T> objects) {
		resultModel.removeAllElements();
		if (objects.isEmpty()) {
			AlertManager.informationMessage(this.getClass().getName(), "noResults", "Search");
		} else {
			for (int i = 0; i < objects.size(); i++) {
				resultModel.addElement(getResultInformation(objects.get(i)));
			}
		}
	}

	public T returnSelectedItem() {
		try {
			int index = resultList.getSelectedIndex();
			return results.get(index);
		} catch (ArrayIndexOutOfBoundsException | NullPointerException npe) {
			return null;
		}
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
	public void addSelectButtonListener(ActionListener al) {
		SelectButton.addActionListener(al);
	}

	private void addListerners() {
		SearchButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				searchButtonActionPerformed(evt);
			}
		});
	}

	protected abstract void searchButtonActionPerformed(java.awt.event.ActionEvent evt);

	protected abstract boolean deleteElement(T object);

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		SearchPanel = new javax.swing.JPanel();
		SearchButton = new javax.swing.JButton();
		SearchFieldPanel = new javax.swing.JPanel();
		ResultPanel = new javax.swing.JPanel();
		ResultScrollPanel = new javax.swing.JScrollPane();
		resultList = new javax.swing.JList();
		SelectButton = new javax.swing.JButton();
		DeleteButton = new javax.swing.JButton();
		CancelButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		SearchPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		SearchButton.setText("Search");

		javax.swing.GroupLayout SearchFieldPanelLayout = new javax.swing.GroupLayout(SearchFieldPanel);
		SearchFieldPanel.setLayout(SearchFieldPanelLayout);
		SearchFieldPanelLayout.setHorizontalGroup(SearchFieldPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		SearchFieldPanelLayout.setVerticalGroup(SearchFieldPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

		javax.swing.GroupLayout SearchPanelLayout = new javax.swing.GroupLayout(SearchPanel);
		SearchPanel.setLayout(SearchPanelLayout);
		SearchPanelLayout.setHorizontalGroup(SearchPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						SearchPanelLayout
								.createSequentialGroup()
								.addContainerGap(295, Short.MAX_VALUE)
								.addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 108,
										javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap())
				.addComponent(SearchFieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		SearchPanelLayout.setVerticalGroup(SearchPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				SearchPanelLayout
						.createSequentialGroup()
						.addComponent(SearchFieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(SearchButton)
						.addContainerGap()));

		ResultPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		resultList.setModel(resultModel);
		resultList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		ResultScrollPanel.setViewportView(resultList);

		SelectButton.setText("Select");

		DeleteButton.setText("Delete");
		DeleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				DeleteButtonActionPerformed(evt);
			}
		});

		CancelButton.setText("Cancel");
		CancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CancelButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout ResultPanelLayout = new javax.swing.GroupLayout(ResultPanel);
		ResultPanel.setLayout(ResultPanelLayout);
		ResultPanelLayout.setHorizontalGroup(ResultPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				ResultPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								ResultPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(ResultScrollPanel, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
										.addGroup(
												ResultPanelLayout
														.createSequentialGroup()
														.addComponent(DeleteButton,
																javax.swing.GroupLayout.PREFERRED_SIZE, 96,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97,
																Short.MAX_VALUE)
														.addComponent(SelectButton,
																javax.swing.GroupLayout.PREFERRED_SIZE, 89,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(CancelButton))).addContainerGap()));

		ResultPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { CancelButton,
				DeleteButton, SelectButton });

		ResultPanelLayout.setVerticalGroup(ResultPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				ResultPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(ResultScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(DeleteButton).addComponent(CancelButton)
										.addComponent(SelectButton)).addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(SearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(ResultPanel, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(SearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(ResultPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CancelButtonActionPerformed
		this.dispose();
	}// GEN-LAST:event_CancelButtonActionPerformed

	private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
		this.toFront();
	}// GEN-LAST:event_formWindowOpened

	private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DeleteButtonActionPerformed
		try {
			if (resultList.getSelectedIndex() >= 0 && resultList.getSelectedIndex() < resultList.getModel().getSize()) {
				T o = results.get(resultList.getSelectedIndex());
				if (deleteElement(o)) {
					results.remove(o);
					fillResults(results);
					if (results.size() > 0) {
						resultList.setSelectedIndex(0);
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException | NullPointerException aiob) {
		}
	}// GEN-LAST:event_DeleteButtonActionPerformed
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private javax.swing.JButton CancelButton;
	private javax.swing.JButton DeleteButton;
	private javax.swing.JPanel ResultPanel;
	private javax.swing.JScrollPane ResultScrollPanel;
	private javax.swing.JButton SearchButton;
	protected javax.swing.JPanel SearchFieldPanel;
	private javax.swing.JPanel SearchPanel;
	private javax.swing.JButton SelectButton;
	protected javax.swing.JList resultList;
	// End of variables declaration//GEN-END:variables
}
