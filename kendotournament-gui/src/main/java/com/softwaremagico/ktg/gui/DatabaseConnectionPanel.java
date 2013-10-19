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

import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import com.softwaremagico.ktg.persistence.DatabaseEngine;
import com.softwaremagico.ktg.persistence.SQLite;
import java.awt.event.KeyListener;
import java.util.List;

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
        UserLabel.setText(trans.getTranslatedText("UserLabel"));
        ServerLabel.setText(trans.getTranslatedText("ServerLabel"));
        PasswordLabel.setText(trans.getTranslatedText("PasswordLabel"));
        DatabaseLabel.setText(trans.getTranslatedText("DatabaseLabel"));
        EngineLabel.setText(trans.getTranslatedText("DatabaseEngineLabel"));
    }

    private void setDefaultText() {
        ServerTextField.setText(DatabaseConnection.getInstance().getServer());
        UserTextField.setText(DatabaseConnection.getInstance().getUser());
        //DatabaseTextField.setText(DatabaseConnection.getInstance().getDatabase()Name);
        //PasswordField.setText(KendoTournamentGenerator.getInstance().password);
    }

    private void fillEngines() {
        engineUpdate = false;
        int i = 0, index = 0;
        for (DatabaseEngine db : DatabaseEngine.values()) {
            EngineComboBox.addItem(db.toString());
            if (db.toString().equals(DatabaseConnection.getInstance().getDatabaseEngine().toString())) {
                index = i;
            }
            i++;
        }
        engineUpdate = true;
        EngineComboBox.setSelectedIndex(index); //Update the window with the event and the selected database. 
        hasNetworkConnection(DatabaseConnection.getInstance().getDatabaseEngine());
    }

    private void fillDatabaseList() {
        DatabaseComboBox.removeAllItems();
        if (!DatabaseEngine.getDatabase(EngineComboBox.getSelectedItem().toString()).getHasNetworkConnection()) {
            List<String> files = Folder.obtainFilesInFolder(Path.getPathDatabaseFolderInHome(), SQLite.defaultSQLiteExtension);
            files.remove(SQLite.defaultDatabaseName + "." + SQLite.defaultSQLiteExtension);
            DatabaseComboBox.addItem("");
            for (String file : files) {
                DatabaseComboBox.addItem(file.substring(0, file.indexOf('.')));
            }
            DatabaseComboBox.setSelectedItem(DatabaseConnection.getInstance().getDatabaseName());
        } else {
            DatabaseComboBox.addItem("");
            DatabaseComboBox.addItem(DatabaseConnection.getInstance().getDatabaseName());
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
    
    public void setPassword(String password){
        PasswordField.setText(password);
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

    public String getSelectedEngine() {
        return selectedEngine;
    }

    public void resetPassword() {
        PasswordField.setText("");
    }

    private void hasNetworkConnection(DatabaseEngine engine) {
        ServerTextField.setEnabled(engine.getHasNetworkConnection());
        UserTextField.setEnabled(engine.getHasNetworkConnection());
        PasswordField.setEnabled(engine.getHasNetworkConnection());
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
        EngineComboBox = new javax.swing.JComboBox();
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

        ServerLabel.setText("Server:");

        DatabaseLabel.setText("Database:");

        UserTextField.setText("root");

        UserLabel.setText("User:");

        PasswordLabel.setText("Password:");

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
            //resetPassword();
        }
    }//GEN-LAST:event_EngineComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox DatabaseComboBox;
    private javax.swing.JLabel DatabaseLabel;
    private javax.swing.JComboBox EngineComboBox;
    private javax.swing.JLabel EngineLabel;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JLabel ServerLabel;
    private javax.swing.JTextField ServerTextField;
    private javax.swing.JLabel UserLabel;
    private javax.swing.JTextField UserTextField;
    // End of variables declaration//GEN-END:variables
}
