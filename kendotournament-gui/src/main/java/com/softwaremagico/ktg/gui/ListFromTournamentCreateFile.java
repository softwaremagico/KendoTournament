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
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.base.KendoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.TournamentPool;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ListFromTournamentCreateFile extends KendoFrame {

    protected Translator trans = null;
    protected List<Tournament> listTournaments = new ArrayList<>();
    protected boolean voidTournament; // Add "All tournaments" option.
    protected boolean refreshTournament = true;

    public void createGui(boolean voidTournament) {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLanguage();
        this.voidTournament = voidTournament;
        fillTournaments();
        updateArena();
        CheckBox.setVisible(false);
        ArenaComboBox.setEnabled(false);
    }

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        ArenaLabel.setText(trans.getTranslatedText("FightArea"));
        CancelButton.setText(trans.getTranslatedText("CancelButton"));
        GenerateButton.setText(trans.getTranslatedText("GenerateButton"));
    }

    protected void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = TournamentPool.getInstance().getSorted();
            if (voidTournament) {
                TournamentComboBox.addItem(trans.getTranslatedText("All"));
            }
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i));
            }
            Tournament tournament = KendoTournamentGenerator.getInstance().getLastSelectedTournament();
            if (tournament != null) {
                TournamentComboBox.setSelectedItem(tournament);
            } else if (TournamentComboBox.getItemCount() > 0) {
                TournamentComboBox.setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        refreshTournament = true;
    }

    public String getSelectedTournamentName() {
        if (voidTournament && TournamentComboBox.getSelectedIndex() == 0) {
            return null;
        }
        return TournamentComboBox.getSelectedItem().toString();
    }

    public Tournament getSelectedTournament() {
        if (voidTournament) {
            if (TournamentComboBox.getSelectedIndex() > 0) {
                //return listTournaments.get(TournamentComboBox.getSelectedIndex() - 1); // -1																	// avoid
                return (Tournament) TournamentComboBox.getSelectedItem();
                // "all".
            } else {
                return null;
            }
        } else {
             return (Tournament) TournamentComboBox.getSelectedItem();
        }

    }

    private int getSelectedTournamentOfList() {
        if (voidTournament) {
            if (TournamentComboBox.getSelectedIndex() == 0) {
                return -1;
            } else {
                return TournamentComboBox.getSelectedIndex() - 1;
            }
        } else {
            return TournamentComboBox.getSelectedIndex();
        }
    }

    private void updateArena() {
        ArenaComboBox.removeAllItems();
        try {
            int selectedTourn = getSelectedTournamentOfList();
            if (selectedTourn >= 0 && listTournaments.get(selectedTourn).getFightingAreas() > 1) {
                ArenaComboBox.addItem(trans.getTranslatedText("All"));
            }

            if (selectedTourn >= 0) {
                for (int i = 0; i < listTournaments.get(selectedTourn).getFightingAreas(); i++) {
                    ArenaComboBox.addItem(Tournament.getFightAreaName(i));
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    public int getSelectedArena() {
        return (ArenaComboBox.getSelectedIndex() - 1);
    }

    /**
     * **********************************************
     *
     * CHECKBOX
     *
     ***********************************************
     */
    /**
     *
     */
    private void checkBoxClicked() {
    }

    private void comboBoxAction() {
    }

    public boolean isCheckBoxSelected() {
        return CheckBox.isSelected();
    }

    public void changeCheckBoxText(String text) {
        CheckBox.setText(text);
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
    public void addGenerateButtonListener(ActionListener al) {
        GenerateButton.addActionListener(al);
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

		TournamentComboBox = new javax.swing.JComboBox();
		TournamentLabel = new javax.swing.JLabel();
		CancelButton = new javax.swing.JButton();
		GenerateButton = new javax.swing.JButton();
		CheckBox = new javax.swing.JCheckBox();
		ArenaLabel = new javax.swing.JLabel();
		ArenaComboBox = new javax.swing.JComboBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TournamentComboBoxActionPerformed(evt);
			}
		});

		TournamentLabel.setText("Tournament");

		CancelButton.setText("Cancel");
		CancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CancelButtonActionPerformed(evt);
			}
		});

		GenerateButton.setText("Generate List");

		CheckBox.setText("CheckBox");
		CheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CheckBoxActionPerformed(evt);
			}
		});

		ArenaLabel.setText("Arena:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(CheckBox,
																		javax.swing.GroupLayout.DEFAULT_SIZE, 215,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(GenerateButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 140,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(TournamentLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(TournamentComboBox, 0,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)))
								.addGap(4, 4, 4)
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addGap(6, 6, 6)
																.addComponent(ArenaLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(ArenaComboBox, 0,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)).addComponent(CancelButton))
								.addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { CancelButton, GenerateButton });

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(TournamentComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(TournamentLabel)
												.addComponent(ArenaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(ArenaLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(CancelButton).addComponent(CheckBox)
												.addComponent(GenerateButton)).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }// GEN-LAST:event_CancelButtonActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            try {
                KendoTournamentGenerator.getInstance().setLastSelectedTournament(
                        TournamentComboBox.getSelectedItem().toString());
            } catch (NullPointerException npe) {
                // No problem "All tournaments selected".
            }
            comboBoxAction();
            if (ArenaComboBox.isEnabled()) {
                updateArena();
            }
        }
    }// GEN-LAST:event_TournamentComboBoxActionPerformed

    private void CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CheckBoxActionPerformed
        checkBoxClicked();
    }// GEN-LAST:event_CheckBoxActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        this.toFront();
    }// GEN-LAST:event_formWindowOpened
		// Variables declaration - do not modify//GEN-BEGIN:variables

	protected javax.swing.JComboBox ArenaComboBox;
	protected javax.swing.JLabel ArenaLabel;
	private javax.swing.JButton CancelButton;
	public javax.swing.JCheckBox CheckBox;
	protected javax.swing.JButton GenerateButton;
	protected javax.swing.JComboBox TournamentComboBox;
	private javax.swing.JLabel TournamentLabel;
	// End of variables declaration//GEN-END:variables
}
