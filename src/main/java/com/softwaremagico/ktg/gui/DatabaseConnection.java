/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 19-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.database.DatabasesEngines;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author jorge
 */
public class DatabaseConnection extends javax.swing.JFrame {

    Translator trans = null;
    boolean engineUpdate = true;

    /**
     * Creates new form DatabaseConnection
     */
    public DatabaseConnection() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        fillEngines();
        setDefaultText();
        this.setBounds(this.getBounds().x, this.getBounds().y, (int) ContentPanel.getBounds().getWidth() + 40, this.getBounds().height);
        PasswordField.requestFocusInWindow();

    }

    private void setDefaultText() {
        System.out.println(KendoTournamentGenerator.getInstance().databaseName);
        ServerTextField.setText(KendoTournamentGenerator.getInstance().server);
        UserTextField.setText(KendoTournamentGenerator.getInstance().user);
        DatabaseTextField.setText(KendoTournamentGenerator.getInstance().databaseName);
        PasswordField.setText(KendoTournamentGenerator.getInstance().password);
    }

    private void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleDatabaseConnection", language));
        UserLabel.setText(trans.returnTag("UserLabel", language));
        ServerLabel.setText(trans.returnTag("ServerLabel", language));
        PasswordLabel.setText(trans.returnTag("PasswordLabel", language));
        DatabaseLabel.setText(trans.returnTag("DatabaseLabel", language));
        ConnectButton.setText(trans.returnTag("ConnectButton", language));
        CloseButton.setText(trans.returnTag("CloseButton", language));
        EngineLabel.setText(trans.returnTag("DatabaseEngineLabel", language));
    }

    public void performConnection() {
        String p = "";
        for (int i = 0; i < PasswordField.getPassword().length; i++) {
            p += String.valueOf(PasswordField.getPassword()[i]);
        }
        if (KendoTournamentGenerator.getInstance().startDatabaseConnection(p, UserTextField.getText(),
                DatabaseTextField.getText(), ServerTextField.getText())) {
            this.dispose();
        } else {
            PasswordField.setText("");
        }
    }

    public char[] password() {
        return PasswordField.getPassword();
    }

    private void fillEngines() {
        engineUpdate = false;
        int i = 0, index = 0;
        for (DatabasesEngines db : DatabasesEngines.values()) {
            EngineComboBox.addItem(db.toString());
            if (db.toString().equals(KendoTournamentGenerator.getInstance().getDatabaseEngine().toString())) {
                index = i;
            }
            i++;
        }
        engineUpdate = true;
        EngineComboBox.setSelectedIndex(index); //Update the window with the event and the selected database. 
        hasNetworkConnection();
    }

    private void hasNetworkConnection() {
        ServerTextField.setEnabled(KendoTournamentGenerator.getInstance().getDatabaseEngine().getHasNetworkConnection());
        UserTextField.setEnabled(KendoTournamentGenerator.getInstance().getDatabaseEngine().getHasNetworkConnection());
        PasswordField.setEnabled(KendoTournamentGenerator.getInstance().getDatabaseEngine().getHasNetworkConnection());
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
    public void addConnectButtonListener(ActionListener al) {
        ConnectButton.addActionListener(al);
    }

    public void addPasswordFieldKeyReleased(KeyListener ka) {
        PasswordField.addKeyListener(ka);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ContentPanel = new javax.swing.JPanel();
        ServerLabel = new javax.swing.JLabel();
        UserLabel = new javax.swing.JLabel();
        PasswordLabel = new javax.swing.JLabel();
        DatabaseLabel = new javax.swing.JLabel();
        ServerTextField = new javax.swing.JTextField();
        UserTextField = new javax.swing.JTextField();
        PasswordField = new javax.swing.JPasswordField();
        DatabaseTextField = new javax.swing.JTextField();
        EngineComboBox = new javax.swing.JComboBox<String>();
        EngineLabel = new javax.swing.JLabel();
        ConnectButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database connection");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        ContentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ContentPanel.setMaximumSize(new java.awt.Dimension(380, 120));
        ContentPanel.setMinimumSize(new java.awt.Dimension(380, 120));

        ServerLabel.setText("Server:");

        UserLabel.setText("User:");

        PasswordLabel.setText("Password:");

        DatabaseLabel.setText("Database:");

        ServerTextField.setText("localhost");
        ServerTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ServerTextFieldKeyReleased(evt);
            }
        });

        UserTextField.setText("root");
        UserTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                UserTextFieldKeyReleased(evt);
            }
        });

        PasswordField.setText("password");

        DatabaseTextField.setText("KendoTournament");
        DatabaseTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                DatabaseTextFieldKeyReleased(evt);
            }
        });

        EngineComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EngineComboBoxActionPerformed(evt);
            }
        });

        EngineLabel.setText("Engine:");

        javax.swing.GroupLayout ContentPanelLayout = new javax.swing.GroupLayout(ContentPanel);
        ContentPanel.setLayout(ContentPanelLayout);
        ContentPanelLayout.setHorizontalGroup(
            ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ServerLabel)
                    .addComponent(UserLabel)
                    .addComponent(PasswordLabel)
                    .addComponent(DatabaseLabel)
                    .addComponent(EngineLabel))
                .addGap(46, 46, 46)
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DatabaseTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PasswordField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(UserTextField)
                    .addComponent(ServerTextField)
                    .addComponent(EngineComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ContentPanelLayout.setVerticalGroup(
            ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EngineComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EngineLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ServerLabel)
                    .addComponent(ServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(DatabaseLabel)
                    .addComponent(DatabaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(UserLabel)
                    .addComponent(UserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PasswordLabel)
                    .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ConnectButton.setText("Connect");

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 248, Short.MAX_VALUE)
                        .addComponent(ConnectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CloseButton, ConnectButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ContentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CloseButton)
                    .addComponent(ConnectButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ServerTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ServerTextFieldKeyReleased
        KendoTournamentGenerator.getInstance().server = ServerTextField.getText();
    }//GEN-LAST:event_ServerTextFieldKeyReleased

    private void DatabaseTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DatabaseTextFieldKeyReleased
        KendoTournamentGenerator.getInstance().databaseName = DatabaseTextField.getText();
    }//GEN-LAST:event_DatabaseTextFieldKeyReleased

    private void UserTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UserTextFieldKeyReleased
        KendoTournamentGenerator.getInstance().user = UserTextField.getText();
    }//GEN-LAST:event_UserTextFieldKeyReleased

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened

    private void EngineComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EngineComboBoxActionPerformed
        if (engineUpdate) {
            KendoTournamentGenerator.getInstance().setDatabaseEngine(EngineComboBox.getSelectedItem().toString());
            hasNetworkConnection();
        }
    }//GEN-LAST:event_EngineComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton ConnectButton;
    private javax.swing.JPanel ContentPanel;
    private javax.swing.JLabel DatabaseLabel;
    private javax.swing.JTextField DatabaseTextField;
    private javax.swing.JComboBox<String> EngineComboBox;
    private javax.swing.JLabel EngineLabel;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JLabel ServerLabel;
    private javax.swing.JTextField ServerTextField;
    private javax.swing.JLabel UserLabel;
    private javax.swing.JTextField UserTextField;
    // End of variables declaration//GEN-END:variables
}
