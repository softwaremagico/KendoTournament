package com.softwaremagico.ktg.statistics;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.KendoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jorge
 */
public abstract class StatisticsGUI extends KendoFrame {

    Translator trans = null;

    public void start() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        //this.setExtendedState(this.getExtendedState() | StatisticsGUI.MAXIMIZED_BOTH);
        setLanguage();
        SelectComboBox.setVisible(false);
        NumberSpinner.setVisible(false);
        NumberLabel.setVisible(false);
    }

    public void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleStatistics"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        SaveButton.setText(trans.getTranslatedText("SaveButton"));
    }

    public abstract void generateStatistics();

    public void changeSelectComboBox() {
    }

    /**
     * Crea un panel para la demostracion (Usado por SuperDemo.java).
     *
     * @return un Panel.
     */
    public abstract JPanel createPanel();

    public int returnNumberOfSpinner() {
        try {
            return (Integer) NumberSpinner.getValue();
        } catch (NullPointerException npe) {
            return 6;
        }
    }

    abstract void numberSpinnedChanged();

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GraphicPanel = createPanel();
        CloseButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();
        SelectComboBox = new javax.swing.JComboBox<String>();
        NumberSpinner = new javax.swing.JSpinner();
        NumberLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(650, 350));

        GraphicPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        GraphicPanel.setLayout(new java.awt.BorderLayout());

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        SaveButton.setText("Store");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });

        SelectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectComboBoxActionPerformed(evt);
            }
        });

        NumberSpinner.setValue(6);
        NumberSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                NumberSpinnerStateChanged(evt);
            }
        });

        NumberLabel.setText("Number of teams:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(GraphicPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SelectComboBox, 0, 215, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NumberLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CloseButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CloseButton, SaveButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GraphicPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CloseButton)
                    .addComponent(SelectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SaveButton)
                    .addComponent(NumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumberLabel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
        String file = "";
        try {
            if (!(file = exploreWindowsForPng(trans.getTranslatedText("ExportPNG"),
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
            }
        } catch (Exception ex) {
            AlertManager.showErrorInformation(this.getClass().getName(),ex);
        }
        if (!file.endsWith(".png")) {
            file = file.concat(".png");
        }

        File outputFile = new File(file);
        final JFrame frame = new JFrame();
        frame.add(GraphicPanel);
        frame.pack();

        Dimension prefSize = GraphicPanel.getPreferredSize();
        BufferedImage img = new BufferedImage(prefSize.width, prefSize.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        SwingUtilities.paintComponent(g, GraphicPanel, frame,
                0, 0, prefSize.width, prefSize.height);
        try {
            ImageIO.write(img, "png", outputFile);
        } catch (IOException ex) {
            Logger.getLogger(StatisticsGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_SaveButtonActionPerformed

    private void SelectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectComboBoxActionPerformed
        changeSelectComboBox();
    }//GEN-LAST:event_SelectComboBoxActionPerformed

    private void NumberSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_NumberSpinnerStateChanged
        numberSpinnedChanged();
    }//GEN-LAST:event_NumberSpinnerStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CloseButton;
    protected javax.swing.JPanel GraphicPanel;
    protected javax.swing.JLabel NumberLabel;
    protected javax.swing.JSpinner NumberSpinner;
    private javax.swing.JButton SaveButton;
    public javax.swing.JComboBox<String> SelectComboBox;
    // End of variables declaration//GEN-END:variables
}
