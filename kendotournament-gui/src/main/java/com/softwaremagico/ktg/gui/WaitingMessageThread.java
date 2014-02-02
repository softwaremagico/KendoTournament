package com.softwaremagico.ktg.gui;

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
import com.softwaremagico.ktg.core.KendoLog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class WaitingMessageThread extends Thread {

    private static final Integer WIDTH = 600;
    private static final Integer HEIGHT = 155;
    private static final Integer CONNECTION_TASK_PERIOD = 3000;
    private JDialog waitingDialog;
    private ImageIcon clockIcon = null;
    private Timer timer;
    private Frame parent;
    private Runnable waitingRunnable;
    private ActionListener timerAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (waitingDialog != null) {
                waitingDialog.setVisible(true);
            }
        }
    };

    public WaitingMessageThread(Frame parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("2");
                // Here, we can safely update the GUI
                // because we'll be called from the
                // event dispatch thread
                createWaitingMessage();
                System.out.println("3");
                timer = new Timer(CONNECTION_TASK_PERIOD, timerAction);
            }
        });
    }

    private void createWaitingMessage() {
        if (clockIcon == null) {
            clockIcon = new ImageIcon(AlertManager.class.getResource("/waiting.png"));
        }

        final JOptionPane optionPane = AlertManager.createWaitingDatabaseMessage();

        optionPane.addPropertyChangeListener(
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        String prop = e.getPropertyName();

                        if (waitingDialog.isVisible()
                                && (e.getSource() == optionPane)
                                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                            waitingDialog.setVisible(false);
                        }
                    }
                });

        waitingDialog = new JDialog(parent, "tic tac", true);

        waitingDialog.setSize(WIDTH, HEIGHT);
        waitingDialog.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        waitingDialog.setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2
                - (int) (WIDTH / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                / 2 - (int) (HEIGHT / 2));

        waitingDialog.setContentPane(optionPane);
    }
    
    public void closeWaitingMessage() {
        try {
            waitingDialog.setVisible(false);
        } catch (Exception e) {
        }
        waitingDialog = null;
    }
}
