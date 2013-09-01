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
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.KendoFrame;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.gui.tournament.BlackBoardPanel;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

public class TreeWindow extends KendoFrame {

    private BlackBoardPanel bbp;
    private JScrollPane blackBoardScrollPane;
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
        blackBoardScrollPane = new JScrollPane();
        blackBoardScrollPane.setViewportView(bbp);
        blackBoardScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        getContentPane().removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        getContentPane().add(blackBoardScrollPane, gridBagConstraints);

        KPanel saveButtonPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        saveButtonPanel.setMinimumSize(new Dimension(200, 50));
        KButton saveButton = new KButton();
        saveButton.setTranslatedText("SaveImageButton");
        saveButtonPanel.add(saveButton);
        saveButton.setPreferredSize(new Dimension(180, 40));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(saveButtonPanel, gridBagConstraints);

        KPanel closeButtonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButtonPanel.setMinimumSize(new Dimension(200, 50));
        CloseButton closeButton = new CloseButton(this);
        closeButton.setPreferredSize(new Dimension(180, 40));
        closeButtonPanel.add(closeButton);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = xPadding;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(closeButtonPanel, gridBagConstraints);
    }

    @Override
    public final void update() {
        try {
            bbp.update(tournament);
            blackBoardScrollPane.revalidate();
            blackBoardScrollPane.repaint();
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void saveImage() {
        String file;
        Translator trans = LanguagePool.getTranslator("gui.xml");
        if (!(file = exploreWindowsForPng(trans.getTranslatedText("ExportPNG"),
                JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
            try {
                Container c = bbp;
                BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
                c.paint(im.getGraphics());
                ImageIO.write(im, "PNG", new File(file));
            } catch (IOException ex) {
                AlertManager.showErrorInformation(this.getClass().getName(), ex);
            }
        }
    }

    @Override
    public String defaultFileName() {
        return "Tree.png";
    }
}
