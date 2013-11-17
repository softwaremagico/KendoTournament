package com.softwaremagico.ktg.gui.base;

import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.ListFromTournamentCreateFile;
import com.softwaremagico.ktg.gui.NewRole;
import com.softwaremagico.ktg.persistence.RolePool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class RolesMenu {

    private NewRole parentWindow;
    private JMenu optionsMenu;
    private JMenuItem accreditationMenuItem, importMenuItem;
    private SelectTournamentImport selectTournament;

    public RolesMenu() {
    }

    public JMenuBar createMenu(NewRole parentWindow) {
        this.parentWindow = parentWindow;
        JMenuBar mainMenu = new JMenuBar();

        mainMenu.add(windowMenu());
        mainMenu.add(createOptionsListMenu());

        return mainMenu;
    }

    private JMenu windowMenu() {
        KMenu windowMenu = new KMenu("WindowMenuItem");
        windowMenu.setMnemonic(KeyEvent.VK_E);
        windowMenu.setIcon(new ImageIcon(Path.getIconPath() + "panel.png"));

        KMenuItem exitMenuItem = new KMenuItem("ExitMenuItem");
        exitMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "exit.png"));

        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentWindow.dispose();
            }
        });

        windowMenu.add(exitMenuItem);

        return windowMenu;
    }

    private JMenu createOptionsListMenu() {
        optionsMenu = new KMenu("OptionsMenu");
        optionsMenu.setMnemonic(KeyEvent.VK_O);
        optionsMenu.setIcon(new ImageIcon(Path.getIconPath() + "options.png"));


        accreditationMenuItem = new KMenuItem("AccreditationMenuItem");
        accreditationMenuItem.setMnemonic(KeyEvent.VK_A);
        accreditationMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "acreditation.png"));
        accreditationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentWindow.createAccreditations();
            }
        });

        optionsMenu.add(accreditationMenuItem);


        importMenuItem = new KMenuItem("Import");
        importMenuItem.setMnemonic(KeyEvent.VK_I);
        importMenuItem.setIcon(new ImageIcon(Path.getIconPath() + "import2.png"));
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTournament = new SelectTournamentImport("Import", "ImportMenu");
                selectTournament.setVisible(true);
            }
        });


        optionsMenu.add(importMenuItem);

        return optionsMenu;
    }

    class SelectTournamentImport extends ListFromTournamentCreateFile {

        public SelectTournamentImport(String title, String buttonTag) {
            createGui(false);
            //Not modified last selected tournament.
            this.changeLastSelectedTournament(false);
            this.setTitle(trans.getTranslatedText(title));
            GenerateButton.setText(trans.getTranslatedText(buttonTag));
            addGenerateButtonListener(new SelectTournamentListener());
        }

        @Override
        public String defaultFileName() {
            return "importedData";
        }

        class SelectTournamentListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!getSelectedTournament().equals(parentWindow.getSelectedTournament())) {
                        RolePool.getInstance().importRoles(getSelectedTournament(), parentWindow.getSelectedTournament());
                        AlertManager.informationMessage(RolesMenu.class.getName(), "importSuccess", "");
                        parentWindow.dispose();
                        dispose();
                    }
                } catch (Exception ex) {
                    AlertManager.errorMessage(RolesMenu.class.getName(), "importFail", "");
                    KendoLog.errorMessage(RolesMenu.class.getName(), ex);
                }
            }
        }
    }
}
