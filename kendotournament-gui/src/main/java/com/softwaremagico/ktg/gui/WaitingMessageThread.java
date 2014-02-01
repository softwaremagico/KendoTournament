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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class WaitingMessageThread extends Thread {

    private static final Integer WIDTH = 600;
    private static final Integer HEIGHT = 155;
    private static final Integer CONNECTION_TASK_PERIOD = 3000;
    private JDialog waitingDialog;
    private ImageIcon clockIcon = null;
    private Timer timer = new Timer("Waiting Message");
    private WaitingTask timerTask;
    private Frame parent;

    public WaitingMessageThread(Frame parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        openWaitingMessage();
    }

    private void createWaitingMessage() {
        if (waitingDialog == null) {
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
    }

    public void openWaitingMessage() {
        // Close old ones.
        try {
            timerTask.cancel();
        } catch (NullPointerException npe) {
        }
        timerTask = new WaitingTask();
        timer.schedule(timerTask, CONNECTION_TASK_PERIOD);
    }

    public void closeWaitingMessage() {
        if (waitingDialog != null) {
            waitingDialog.setVisible(false);
            KendoLog.info(WaitingMessageThread.class.getName(), "Closing 'Waiting Database' Message.");
        }
        try {
            timerTask.cancel();
        } catch (NullPointerException npe) {
        }
    }

    class WaitingTask extends TimerTask {

        @Override
        public void run() {
            if (waitingDialog == null) {
                createWaitingMessage();
            }
            KendoLog.info(WaitingMessageThread.class.getName(), "Create 'Waiting Database' Message.");
            waitingDialog.setVisible(true);
        }
    }
}
