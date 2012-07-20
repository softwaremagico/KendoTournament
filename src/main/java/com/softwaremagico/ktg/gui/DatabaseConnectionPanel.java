package com.softwaremagico.ktg.gui;
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

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.database.DatabaseEngine;
import com.softwaremagico.ktg.database.SQLite;
import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.event.KeyListener;
import java.util.List;

/**
 *
 * @author LOCAL\jhortelano
 */
public class DatabaseConnectionPanel extends javax.swing.JPanel {
    
    private boolean engineUpdate = true;
    private String selectedEngine = "MySQL";

    /**
     * Creates new form DatabaseConnectionPanel
     */
    public DatabaseConnectionPanel() {
        initComponents();
        fillEngines();
        setDefaultText();
        setLanguage();
    }
    
    private void setLanguage() {
        Translator trans = LanguagePool.getTranslator("gui.xml");
        UserLabel.setText(trans.returnTag("UserLabel"));
        ServerLabel.setText(trans.returnTag("ServerLabel"));
        PasswordLabel.setText(trans.returnTag("PasswordLabel"));
        DatabaseLabel.setText(trans.returnTag("DatabaseLabel"));
        EngineLabel.setText(trans.returnTag("DatabaseEngineLabel"));
    }
    
    private void setDefaultText() {
        ServerTextField.setText(KendoTournamentGenerator.getInstance().server);
        UserTextField.setText(KendoTournamentGenerator.getInstance().user);
        //DatabaseTextField.setText(KendoTournamentGenerator.getInstance().databaseName);
        //PasswordField.setText(KendoTournamentGenerator.getInstance().password);
    }
    
    private void fillEngines() {
        engineUpdate = false;
        int i = 0, index = 0;
        for (DatabaseEngine db : DatabaseEngine.values()) {
            EngineComboBox.addItem(db.toString());
            if (db.toString().equals(KendoTournamentGenerator.getInstance().getDatabaseEngine().toString())) {
                index = i;
            }
            i++;
        }
        engineUpdate = true;
        EngineComboBox.setSelectedIndex(index); //Update the window with the event and the selected database. 
        hasNetworkConnection(KendoTournamentGenerator.getInstance().getDatabaseEngine());
    }
    
    private void fillDatabaseList() {
        DatabaseComboBox.removeAllItems();
        if (!DatabaseEngine.getDatabase(EngineComboBox.getSelectedItem().toString()).getHasNetworkConnection()) {
            List<String> files = Folder.obtainFilesInFolder(Path.returnDatabasePath(), SQLite.defaultSQLiteExtension);
            files.remove(SQLite.defaultDatabaseName + "." + SQLite.defaultSQLiteExtension);
            DatabaseComboBox.addItem("");
            for (String file : files) {
                System.out.println(file +" " + file.indexOf('.'));
                DatabaseComboBox.addItem(file.substring(0, file.indexOf('.')));                
            }
            DatabaseComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().databaseName);
        } else {
            DatabaseComboBox.addItem("");
            DatabaseComboBox.addItem(KendoTournamentGenerator.getInstance().databaseName);
            DatabaseComboBox.setSelectedIndex(1);
        }
    }
    
    public char[] password() {
        return PasswordField.getPassword();
    }
    
    public String getPassword() {
        String p = "";
        for (int i = 0; i < PasswordField.getPassword().length; i++) {
            p += String.valueOf(PasswordField.getPassword()[i]);
        }
        return p;
    }
    
    public String getUser() {
        return UserTextField.getText();
    }
    
    public String getServer() {
        return ServerTextField.getText();
    }
    
    public String getDatabase() {
        //return DatabaseTextField.getText();
        return DatabaseComboBox.getSelectedItem().toString();
    }
    
    public void resetPassword() {
        PasswordField.setText("");
    }
    
    private void hasNetworkConnection(DatabaseEngine engine) {
        ServerTextField.setEnabled(engine.getHasNetworkConnection());
        UserTextField.setEnabled(engine.getHasNetworkConnection());
        PasswordField.setEnabled(engine.getHasNetworkConnection());
    }
    
    public String getSelectedEngine() {
        return selectedEngine;
    }
    
    public void setSelectedEngine(String selectedEngine) {
        EngineComboBox.setSelectedItem(selectedEngine);
        this.selectedEngine = selectedEngine;
    }
    
    public void setFocusOnPassword() {
        PasswordField.requestFocusInWindow();
    }
    
    public boolean getPasswordEnabled() {
        return PasswordField.isEnabled();
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * Update password.
     *
     * @param ka
     */
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

        EngineLabel = new javax.swing.JLabel();
        EngineComboBox = new javax.swing.JComboBox<String>();
        ServerTextField = new javax.swing.JTextField();
        ServerLabel = new javax.swing.JLabel();
        DatabaseLabel = new javax.swing.JLabel();
        UserTextField = new javax.swing.JTextField();
        UserLabel = new javax.swing.JLabel();
        PasswordLabel = new javax.swing.JLabel();
        PasswordField = new javax.swing.JPasswordField();
        DatabaseComboBox = new javax.swing.JComboBox();

        setMinimumSize(new java.awt.Dimension(400, 180));

        EngineLabel.setText("Engine:");

        EngineComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EngineComboBoxActionPerformed(evt);
            }
        });

        ServerTextField.setText("localhost");
        ServerTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ServerTextFieldKeyReleased(evt);
            }
        });

        ServerLabel.setText("Server:");

        DatabaseLabel.setText("Database:");

        UserTextField.setText("root");
        UserTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                UserTextFieldKeyReleased(evt);
            }
        });

        UserLabel.setText("User:");

        PasswordLabel.setText("Password:");

        PasswordField.setText("password");

        DatabaseComboBox.setEditable(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ServerLabel)
                    .addComponent(UserLabel)
                    .addComponent(PasswordLabel)
                    .addComponent(DatabaseLabel)
                    .addComponent(EngineLabel))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PasswordField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(UserTextField)
                    .addComponent(ServerTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .addComponent(EngineComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DatabaseComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EngineComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EngineLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ServerLabel)
                    .addComponent(ServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DatabaseLabel)
                    .addComponent(DatabaseComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(UserLabel)
                    .addComponent(UserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PasswordLabel)
                    .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void EngineComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EngineComboBoxActionPerformed
        if (engineUpdate) {
            //KendoTournamentGenerator.getInstance().setDatabaseEngine(EngineComboBox.getSelectedItem().toString());
            setSelectedEngine(EngineComboBox.getSelectedItem().toString());
            hasNetworkConnection(DatabaseEngine.getDatabase(EngineComboBox.getSelectedItem().toString()));
            fillDatabaseList();
            resetPassword();
        }
    }//GEN-LAST:event_EngineComboBoxActionPerformed
    
    private void ServerTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ServerTextFieldKeyReleased
        KendoTournamentGenerator.getInstance().server = ServerTextField.getText();
    }//GEN-LAST:event_ServerTextFieldKeyReleased
    
    private void UserTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UserTextFieldKeyReleased
        KendoTournamentGenerator.getInstance().user = UserTextField.getText();
    }//GEN-LAST:event_UserTextFieldKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox DatabaseComboBox;
    private javax.swing.JLabel DatabaseLabel;
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
