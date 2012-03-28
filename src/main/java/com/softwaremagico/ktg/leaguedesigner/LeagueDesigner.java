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
 *  Created on 21-jul-2009.
 */
package com.softwaremagico.ktg.leaguedesigner;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.Timer;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author  jorge
 */
public class LeagueDesigner extends javax.swing.JFrame {

    private DefaultListModel<String> teamModel = new DefaultListModel<String>();
    private Translator trans = null;
    List<Team> teams;
    Tournament championship;
    private List<Tournament> listTournaments = new ArrayList<Tournament>();
    private boolean refreshTournament = true;
    private boolean refreshMode = true;
    Integer numberMaxOfWinners = 1;
    Timer timer;
    private boolean wasDoubleClick = true;
    private BlackBoardPanel bbp;
    //private final String FOLDER = "designer";
    private Point p;
    private JViewport viewport;

    /** Creates new form LeagueDesigner */
    public LeagueDesigner() {
        try {
            refreshMode = false;
            initComponents();
            setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                    (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
            setLanguage(KendoTournamentGenerator.getInstance().language);
            bbp = new BlackBoardPanel();
            BlackBoardScrollPane.setViewportView(bbp);
            PassSpinner.setValue(numberMaxOfWinners);
            fillTournaments();
            updateInterface();
            updateLevel();

            if (KendoTournamentGenerator.getInstance().designedGroups.size() == 0) {
                addDesignedPanelFirstLevel();
            }
            updateMode();
            refreshMode = true;
        } catch (NullPointerException npe) {
        }
    }

    private void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleLeagueDesigner", language));
        TournamentLabel.setText(trans.returnTag("TournamentLabel", language));
        PassLabel.setText(trans.returnTag("PassLabel", language));
        AddButton.setText(trans.returnTag("AddGroupButton", language));
        DeleteButton.setText(trans.returnTag("DeleteGroupButton", language));
        CleanButton.setText(trans.returnTag("CleanButton", language));
        CleanAllButton.setText(trans.returnTag("CleanAllButton", language));
        AddTeamButton.setText(trans.returnTag("AddTeamButton", language));
        AcceptButton.setText(trans.returnTag("GenerateMatchButton", language));
        CloseButton.setText(trans.returnTag("CloseButton", language));
        ChampionshipRadioButton.setText(trans.returnTag("ChampionshipRadioButton", language));
        TreeRadioButton.setText(trans.returnTag("TreeRadioButton", language));
        SimpleRadioButton.setText(trans.returnTag("SimpleRadioButton", language));
        CleanLinksButton.setText(trans.returnTag("CleanLinks", language));
        DeleteLevelLabel.setText(trans.returnTag("DeleteLevelLabel", language));
        DeleteLevelButton.setText(trans.returnTag("DeleteButton", language));
        LoadButton.setText(trans.returnTag("ButtonLoadTournament", language));
        TreeEditionLabel.setText(trans.returnTag("TournamentLabel", language));
        GroupEditionLabel.setText(trans.returnTag("GroupLabel", language));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
        } catch (NullPointerException npe) {
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
    }

    /**
     * Fill the list with the results obtained
     */
    public void fillTeams() {
        try {
            teamModel.removeAllElements();
            for (int i = 0; i < teams.size(); i++) {
                if (!KendoTournamentGenerator.getInstance().designedGroups.containTeamInTournament(teams.get(i), TournamentComboBox.getSelectedItem().toString())) {
                    teamModel.addElement(teams.get(i).returnName());
                }
            }
            if (teamModel.size() > 0) {
                TeamList.setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
        }
    }

    private void updateTeams() {
        int index = TeamList.getSelectedIndex();
        fillTeams();
        if (index < teamModel.size() && index >= 0) {
            TeamList.setSelectedIndex(index);
        } else {
            index--;
            if (index >= 0) {
                TeamList.setSelectedIndex(index);
            }
        }
    }

    private void addDesignedPanelFirstLevel() {
        try {
            if (KendoTournamentGenerator.getInstance().designedGroups.sizeOfTournament(TournamentComboBox.getSelectedItem().toString()) < obtainNumberOfGroupsOfLeague()) {
                //int defaultArena = (KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(0).size()) / championship.fightingAreas;
                int defaultArena = 0;
                DesignedGroup designedFight = new DesignedGroup(obtainMaxNumberOfTeamsByGroup(), numberMaxOfWinners, championship, 0, defaultArena);
                designedFight.addMouseClickListener(new MouseAdapters(designedFight));
                KendoTournamentGenerator.getInstance().designedGroups.add(designedFight, 0, true, true);
                designedFight.setSelected(KendoTournamentGenerator.getInstance().designedGroups);
                KendoTournamentGenerator.getInstance().designedGroups.updateArenasInLevelZero();
                updateBlackBoard();
                updateDesignerGroups();
                updateListeners();
            }
        } catch (NullPointerException npe) {
        }
    }

    private void addTeamToSelectedPanel(Team t) {
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.size(); i++) {
            if (KendoTournamentGenerator.getInstance().designedGroups.get(i).isSelected()) {
                KendoTournamentGenerator.getInstance().designedGroups.get(i).addTeam(t);
                break;
            }
        }
        updateTeams();
        updateDesignerGroups();
    }

    private void removeSelectedPanel() {
        try {
            int select = KendoTournamentGenerator.getInstance().designedGroups.returnIndexLastSelected();
            KendoTournamentGenerator.getInstance().designedGroups.remove(select, 0);
            if (select > KendoTournamentGenerator.getInstance().designedGroups.returnLastPositionOfLevel(0)) {
                KendoTournamentGenerator.getInstance().designedGroups.selectGroup(KendoTournamentGenerator.getInstance().designedGroups.returnLastPositionOfLevel(0));
            } else {
                KendoTournamentGenerator.getInstance().designedGroups.selectGroup(select);
            }
            updateTeams();
            updateBlackBoard();
            updateDesignerGroups();
        } catch (NullPointerException npe) {
        }
    }

    private void cleanSelectedPanel() {
        try {
            for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.size(); i++) {
                if (KendoTournamentGenerator.getInstance().designedGroups.get(i).isSelected()) {
                    KendoTournamentGenerator.getInstance().designedGroups.get(i).cleanTeams();
                }

            }
            updateTeams();
        } catch (NullPointerException npe) {
        }
    }

    private Team returnTeamByName(String name) {
        try {
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).returnName().equals(name)) {
                    return teams.get(i);
                }

            }
        } catch (NullPointerException npe) {
        }
        return null;
    }

    private Team returnSelectedTeam() {
        String name = (String) TeamList.getSelectedValue();
        //int index = TeamList.getSelectedIndex();
        return returnTeamByName(name);
    }

    private void addTeam() {
        Team t = returnSelectedTeam();
        if (t != null) {
            addTeamToSelectedPanel(returnSelectedTeam());
        }
    }

    private void updateDesignerGroups() {
        KendoTournamentGenerator.getInstance().designedGroups.update();
    }

    public int obtainNumberOfGroupsOfLeague() {
        return teams.size() / 2;
    }

    public int obtainMaxNumberOfTeamsByGroup() {
        try {
            return (int) Math.min(Math.floor((float) teams.size() / KendoTournamentGenerator.getInstance().designedGroups.returnActualSizeOfLevel(0)), 4);
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    private void updateInterface() {
        try {
            championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
            KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            teams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournamentExactName(TournamentComboBox.getSelectedItem().toString(), false);

            if (!championship.mode.equals("simple")) {
                //If it is not stored in a file, generate it. 
                if (!KendoTournamentGenerator.getInstance().designedGroups.loadDesigner()) {
                    KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));
                }
            }

            KendoTournamentGenerator.getInstance().designedGroups.mode = championship.mode;

            fillTeams();
            updateListeners();
            updateBlackBoard();
            updateRadioButton();
        } catch (NullPointerException npe) {
        }
    }

    void updateBlackBoard() {
        try {
            int select = KendoTournamentGenerator.getInstance().designedGroups.returnIndexLastSelected();
            if (!KendoTournamentGenerator.getInstance().designedGroups.mode.equals("simple")) {
                bbp.updateBlackBoard(TournamentComboBox.getSelectedItem().toString(), false);
                KendoTournamentGenerator.getInstance().designedGroups.enhance(false);
            } else {
                bbp.clearBlackBoard();
            }
            if (select >= 0) {
                KendoTournamentGenerator.getInstance().designedGroups.selectGroup(select);
            }

            // KendoTournamentGenerator.getInstance().designedGroups.get(29).scrollRectToVisible(new Rectangle(0,0,getWidth(), getHeight()));

            BlackBoardScrollPane.revalidate();
            BlackBoardScrollPane.repaint();
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    private void consistentTree() {
        try {
            //It is impossible in a tree league that are more than one winner. If it is, change to a championship.
            if (KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfmaxTeamsGroupPassNextRound(0) > 1 && KendoTournamentGenerator.getInstance().designedGroups.mode.equals("tree")) {
                ChampionshipRadioButton.setSelected(true);
                KendoTournamentGenerator.getInstance().designedGroups.mode = "championship";
                championship.mode = KendoTournamentGenerator.getInstance().designedGroups.mode;
                updateBlackBoard();
            }
        } catch (NullPointerException npe) {
        }
    }

    private void updateRadioButton() {
        if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("championship")) {
            ChampionshipRadioButton.setSelected(true);
        } else if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("manual")) {
            ManualRadioButton.setSelected(true);
        } else if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("tree")) {
            TreeRadioButton.setSelected(true);
        } else if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("simple")) {
            SimpleRadioButton.setSelected(true);
        }
    }

    private void updateMode() {
        try {
            if (ManualRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().designedGroups.mode = "manual";
                championship.mode = "manual";
                CleanLinksButton.setVisible(true);
            } else if (ChampionshipRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().designedGroups.mode = "championship";
                championship.mode = "championship";
                CleanLinksButton.setVisible(false);
            } else if (TreeRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().designedGroups.mode = "tree";
                championship.mode = "tree";
                CleanLinksButton.setVisible(false);
            } else if (SimpleRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().designedGroups.mode = "simple";
                championship.mode = "simple";
                CleanLinksButton.setVisible(false);
            }
            if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("tree") || KendoTournamentGenerator.getInstance().designedGroups.mode.equals("simple")) {
                PassSpinner.setValue(1);
                PassSpinner.setEnabled(false);
            } else {
                PassSpinner.setEnabled(true);
            }

            if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("simple")) {
                AddButton.setVisible(false);
                DeleteButton.setVisible(false);
                CleanAllButton.setVisible(false);
                CleanButton.setVisible(false);
                AddTeamButton.setVisible(false);
                AcceptButton.setVisible(false);
                bbp.clearBlackBoard();
            } else {
                AddButton.setVisible(true);
                DeleteButton.setVisible(true);
                CleanAllButton.setVisible(true);
                CleanButton.setVisible(true);
                AddTeamButton.setVisible(true);
                AcceptButton.setVisible(true);
            }
            if (refreshMode) {
                KendoTournamentGenerator.getInstance().database.updateTournament(championship, false);
            }
            KendoTournamentGenerator.getInstance().designedGroups.updateInnerLevels();
        } catch (NullPointerException npe) {
        }
    }

    private void updateLevel() {
        LevelComboBox.removeAllItems();
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); i++) {
            if (i < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - 2) {
                LevelComboBox.addItem(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - i));
            } else if (i == KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels() - 2) {
                LevelComboBox.addItem(trans.returnTag("SemiFinalLabel", KendoTournamentGenerator.getInstance().language));
            } else {
                LevelComboBox.addItem(trans.returnTag("FinalLabel", KendoTournamentGenerator.getInstance().language));
            }
        }
        LevelComboBox.addItem(trans.returnTag("All", KendoTournamentGenerator.getInstance().language));
    }

    private void focus(int x, int y) {
        p = new Point();
        Dimension dv = viewport.getViewSize();
        Dimension de = viewport.getExtentSize();


        int columnsWide = this.getWidth() / KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels();
        int rowsWide;
        if (KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(0).size() > 0) {
            rowsWide = this.getWidth() / KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(0).size();
        } else {
            rowsWide = 1;
        }

        p.x = columnsWide * x;
        p.y = rowsWide * y;

        viewport.setViewPosition(p);
    }

    /************************************************
     *
     *                    LISTENERS
     *
     ************************************************/
    /**
     * When clicking to a box.
     */
    class MouseAdapters extends MouseAdapter {

        DesignedGroup designedFight;

        MouseAdapters(DesignedGroup d) {
            designedFight = d;
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            clicked(evt, designedFight);
        }
    }

    void clicked(java.awt.event.MouseEvent e, DesignedGroup d) {
        final boolean selected = d.isSelected();

        if (d.getLevel() == 0) {
            if (e.getClickCount() == 2) {
                d.openDesignGroupWindow(this);
                wasDoubleClick = true;
            } else {
                //Avoid to run the one-click functions when performing a doubleclick.
                Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
                timer = new Timer(timerinterval.intValue(), new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (wasDoubleClick) {
                            wasDoubleClick = false; // reset flag
                        } else {
                            //If is a group already selected, add the selected team.
                            if (selected) {
                                //addTeam();
                            }

                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }

            KendoTournamentGenerator.getInstance().designedGroups.unselectDesignedGroups();
            d.setSelected(KendoTournamentGenerator.getInstance().designedGroups);
        } else {
            //Clicking in the second level is only useful for defining links. 
            if (KendoTournamentGenerator.getInstance().designedGroups.mode.equals("manual")) {
                KendoTournamentGenerator.getInstance().designedGroups.addLink(KendoTournamentGenerator.getInstance().designedGroups.returnLastSelected(), d);
                updateBlackBoard();
            }
        }
        updateListeners();
    }

    private void updateListeners() {
        for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.size(); i++) {
            if (!KendoTournamentGenerator.getInstance().designedGroups.get(i).listenerAdded) {
                KendoTournamentGenerator.getInstance().designedGroups.get(i).addMouseClickListener(new MouseAdapters(KendoTournamentGenerator.getInstance().designedGroups.get(i)));
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TreeChampionshipButtonGroup = new javax.swing.ButtonGroup();
        TournamentLabel = new javax.swing.JLabel();
        TournamentComboBox = new javax.swing.JComboBox<String>();
        AcceptButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        ChampionshipRadioButton = new javax.swing.JRadioButton();
        TreeRadioButton = new javax.swing.JRadioButton();
        ManualRadioButton = new javax.swing.JRadioButton();
        SimpleRadioButton = new javax.swing.JRadioButton();
        LoadButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        BlackBoardScrollPane = new javax.swing.JScrollPane();
        DeleteLevelLabel = new javax.swing.JLabel();
        LevelComboBox = new javax.swing.JComboBox<String>();
        DeleteLevelButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        CleanAllButton = new javax.swing.JButton();
        TreeEditionLabel = new javax.swing.JLabel();
        PassLabel = new javax.swing.JLabel();
        PassSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        TeamScrollPane = new javax.swing.JScrollPane();
        TeamList = new javax.swing.JList<String>();
        AddTeamButton = new javax.swing.JButton();
        CleanButton = new javax.swing.JButton();
        CleanLinksButton = new javax.swing.JButton();
        GroupEditionLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        TournamentLabel.setText("Tournament:");

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });
        TournamentComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TournamentComboBoxFocusGained(evt);
            }
        });

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        TreeChampionshipButtonGroup.add(ChampionshipRadioButton);
        ChampionshipRadioButton.setText("Championship");
        ChampionshipRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChampionshipRadioButtonItemStateChanged(evt);
            }
        });

        TreeChampionshipButtonGroup.add(TreeRadioButton);
        TreeRadioButton.setSelected(true);
        TreeRadioButton.setText("Tree");
        TreeRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TreeRadioButtonItemStateChanged(evt);
            }
        });

        TreeChampionshipButtonGroup.add(ManualRadioButton);
        ManualRadioButton.setText("Manual");
        ManualRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ManualRadioButtonItemStateChanged(evt);
            }
        });

        TreeChampionshipButtonGroup.add(SimpleRadioButton);
        SimpleRadioButton.setText("Simple");
        SimpleRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SimpleRadioButtonItemStateChanged(evt);
            }
        });

        LoadButton.setText("Load");
        LoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BlackBoardScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        DeleteLevelLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DeleteLevelLabel.setText("Delete Level:");

        DeleteLevelButton.setText("Delete");
        DeleteLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteLevelButtonActionPerformed(evt);
            }
        });

        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        CleanAllButton.setText("Clean All");
        CleanAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CleanAllButtonActionPerformed(evt);
            }
        });

        TreeEditionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        TreeEditionLabel.setText("Tree Edition:");

        PassLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PassLabel.setText("Pass:");

        PassSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 3, 1));
        PassSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                PassSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TreeEditionLabel)
                            .addComponent(AddButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addComponent(DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addComponent(CleanAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DeleteLevelLabel)
                            .addComponent(LevelComboBox, 0, 149, Short.MAX_VALUE)
                            .addComponent(DeleteLevelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(PassLabel)
                        .addComponent(PassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BlackBoardScrollPane)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddButton, CleanAllButton, DeleteButton, LevelComboBox});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BlackBoardScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TreeEditionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CleanAllButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteLevelLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteLevelButton)
                        .addGap(18, 18, 18)
                        .addComponent(PassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AddButton, CleanAllButton, DeleteButton, DeleteLevelButton, LevelComboBox});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        TeamList.setModel(teamModel);
        TeamList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TeamListMouseClicked(evt);
            }
        });
        TeamScrollPane.setViewportView(TeamList);

        AddTeamButton.setText("Add Team");
        AddTeamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddTeamButtonActionPerformed(evt);
            }
        });

        CleanButton.setText("Clear");
        CleanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CleanButtonActionPerformed(evt);
            }
        });

        CleanLinksButton.setText("Clear Links");
        CleanLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CleanLinksButtonActionPerformed(evt);
            }
        });

        GroupEditionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        GroupEditionLabel.setText("Group Edition:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CleanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CleanLinksButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GroupEditionLabel)
                    .addComponent(AddTeamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TeamScrollPane)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddTeamButton, CleanButton, CleanLinksButton});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TeamScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(GroupEditionLabel)
                        .addGap(18, 18, 18)
                        .addComponent(AddTeamButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CleanButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CleanLinksButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AddTeamButton, CleanButton, CleanLinksButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TournamentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(SimpleRadioButton)
                        .addGap(27, 27, 27)
                        .addComponent(ManualRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(ChampionshipRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(TreeRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(LoadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AcceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CloseButton))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CloseButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentLabel)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SimpleRadioButton)
                    .addComponent(ManualRadioButton)
                    .addComponent(ChampionshipRadioButton)
                    .addComponent(TreeRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CloseButton)
                    .addComponent(LoadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AcceptButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AcceptButton, CloseButton, LoadButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            updateInterface();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        removeSelectedPanel();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        addDesignedPanelFirstLevel();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void AddTeamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddTeamButtonActionPerformed
        addTeam();
    }//GEN-LAST:event_AddTeamButtonActionPerformed

    private void CleanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanButtonActionPerformed
        cleanSelectedPanel();
    }//GEN-LAST:event_CleanButtonActionPerformed

    private void PassSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_PassSpinnerStateChanged
        try {
            if ((Integer) PassSpinner.getValue() < 1) {
                PassSpinner.setValue(1);
            }

            if ((Integer) PassSpinner.getValue() > 2) {
                PassSpinner.setValue(2);
            }

            numberMaxOfWinners = (Integer) PassSpinner.getValue();
            KendoTournamentGenerator.getInstance().designedGroups.setNumberOfTeamsPassNextRound(numberMaxOfWinners);

            consistentTree();
            KendoTournamentGenerator.getInstance().designedGroups.updateInnerLevels(0);
            updateBlackBoard();
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_PassSpinnerStateChanged

    private void TeamListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TeamListMouseClicked
        if (!KendoTournamentGenerator.getInstance().designedGroups.mode.equals("simple")) {
            if (evt.getClickCount() == 2) {
                addTeam();
            }
        }
    }//GEN-LAST:event_TeamListMouseClicked

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        //try{
        if (TournamentComboBox.getItemCount() > 0 && KendoTournamentGenerator.getInstance().fights.size() == 0) {
            KendoTournamentGenerator.getInstance().designedGroups.storeDesigner();
        }
        //}catch(NullPointerException npe){}
        this.dispose();
}//GEN-LAST:event_CloseButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        try {
            if (!(KendoTournamentGenerator.getInstance().designedGroups.mode.equals("manual")) || KendoTournamentGenerator.getInstance().designedGroups.allGroupsHaveManualLink()) {
                if (MessageManager.question("questionCreateFight", "Warning!", KendoTournamentGenerator.getInstance().language)) {
                    KendoTournamentGenerator.getInstance().fights.deleteAllFights(championship.name, false);
                    if (KendoTournamentGenerator.getInstance().fights.setAll(KendoTournamentGenerator.getInstance().designedGroups.generateLevelFights(0), true)) {
                        //Delete inner levels when delete old fights.
                        KendoTournamentGenerator.getInstance().designedGroups.emptyInnerLevels();
                        KendoTournamentGenerator.getInstance().database.deleteDrawsOfTournament(TournamentComboBox.getSelectedItem().toString());
                        this.dispose();
                    }
                }
            } else {
                MessageManager.errorMessage("noLinkFinished", "Error", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } catch (NullPointerException npe) {
        }
        /*if (TournamentComboBox.getItemCount() > 0) {
        KendoTournamentGenerator.getInstance().designedGroups.storeDesigner(FOLDER + File.separator + TournamentComboBox.getSelectedItem().toString() + ".dsg");
        }*/
        KendoTournamentGenerator.getInstance().designedGroups.deleteUsedDesigner();
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (TournamentComboBox.getItemCount() > 0 && KendoTournamentGenerator.getInstance().fights.size() == 0) {
            KendoTournamentGenerator.getInstance().designedGroups.storeDesigner();
        }
    }//GEN-LAST:event_formWindowClosing

    private void CleanAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanAllButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().fights.deleteAllFightsButNotFromDatabase(championship.name, false);
            KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().designedGroups.mode = championship.mode;
            KendoTournamentGenerator.getInstance().designedGroups.setNumberOfTeamsPassNextRound(numberMaxOfWinners);
            updateMode();
            updateBlackBoard();
            fillTeams();
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_CleanAllButtonActionPerformed

    private void TournamentComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TournamentComboBoxFocusGained
        if (TournamentComboBox.getItemCount() > 0 && KendoTournamentGenerator.getInstance().fights.size() == 0) {
            KendoTournamentGenerator.getInstance().designedGroups.storeDesigner();
        }
    }//GEN-LAST:event_TournamentComboBoxFocusGained

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        consistentTree();
    }//GEN-LAST:event_formWindowGainedFocus

    private void ManualRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ManualRadioButtonItemStateChanged
        if (refreshMode && ManualRadioButton.isSelected()) {
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_ManualRadioButtonItemStateChanged

    private void ChampionshipRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChampionshipRadioButtonItemStateChanged
        if (refreshMode && ChampionshipRadioButton.isSelected()) {
            numberMaxOfWinners = 2;
            PassSpinner.setValue(numberMaxOfWinners);
            KendoTournamentGenerator.getInstance().designedGroups.setNumberOfTeamsPassNextRound(numberMaxOfWinners);
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_ChampionshipRadioButtonItemStateChanged

    private void TreeRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TreeRadioButtonItemStateChanged
        try {
            if (refreshMode && TreeRadioButton.isSelected()) {
                numberMaxOfWinners = 1;
                PassSpinner.setValue(numberMaxOfWinners);
                KendoTournamentGenerator.getInstance().designedGroups.setNumberOfTeamsPassNextRound(1);
                updateMode();
                updateBlackBoard();
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_TreeRadioButtonItemStateChanged

    private void CleanLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanLinksButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().designedGroups.cleanLinksSelectedGroup();
            updateBlackBoard();
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_CleanLinksButtonActionPerformed

    private void SimpleRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SimpleRadioButtonItemStateChanged
        if (refreshMode && SimpleRadioButton.isSelected()) {
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_SimpleRadioButtonItemStateChanged

    private void DeleteLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteLevelButtonActionPerformed
        //Select All levels
        if (LevelComboBox.getSelectedIndex() == LevelComboBox.getItemCount() - 1) {
            if (KendoTournamentGenerator.getInstance().fights.deleteAllFights(championship.name, true)) {
                KendoTournamentGenerator.getInstance().designedGroups.deleteTeamsOfLevel(0);
                MessageManager.customMessage("fightDeleted", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } else {
            if (KendoTournamentGenerator.getInstance().fights.deleteFightsOfLevel(championship.name, LevelComboBox.getSelectedIndex(), true)) {
                KendoTournamentGenerator.getInstance().designedGroups.deleteTeamsOfLevel(LevelComboBox.getSelectedIndex());
                MessageManager.customMessage("fightDeleted", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            }
        }
        updateBlackBoard();
        fillTeams();
    }//GEN-LAST:event_DeleteLevelButtonActionPerformed

    private void LoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadButtonActionPerformed
        if (MessageManager.question("questionLoadDesign", "Warning!", KendoTournamentGenerator.getInstance().language)) {
            KendoTournamentGenerator.getInstance().fights.setAll(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name), false);
            KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().fights.getFights());
            KendoTournamentGenerator.getInstance().designedGroups.mode = championship.mode;
            fillTeams();
            updateListeners();
            updateBlackBoard();
            refreshMode = false;
            updateRadioButton();
            refreshMode = true;
            KendoTournamentGenerator.getInstance().designedGroups.storeDesigner();
        }
    }//GEN-LAST:event_LoadButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton AddButton;
    private javax.swing.JButton AddTeamButton;
    private javax.swing.JScrollPane BlackBoardScrollPane;
    private javax.swing.JRadioButton ChampionshipRadioButton;
    private javax.swing.JButton CleanAllButton;
    private javax.swing.JButton CleanButton;
    private javax.swing.JButton CleanLinksButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DeleteLevelButton;
    private javax.swing.JLabel DeleteLevelLabel;
    private javax.swing.JLabel GroupEditionLabel;
    private javax.swing.JComboBox<String> LevelComboBox;
    private javax.swing.JButton LoadButton;
    private javax.swing.JRadioButton ManualRadioButton;
    private javax.swing.JLabel PassLabel;
    private javax.swing.JSpinner PassSpinner;
    private javax.swing.JRadioButton SimpleRadioButton;
    private javax.swing.JList<String> TeamList;
    private javax.swing.JScrollPane TeamScrollPane;
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.ButtonGroup TreeChampionshipButtonGroup;
    private javax.swing.JLabel TreeEditionLabel;
    private javax.swing.JRadioButton TreeRadioButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
