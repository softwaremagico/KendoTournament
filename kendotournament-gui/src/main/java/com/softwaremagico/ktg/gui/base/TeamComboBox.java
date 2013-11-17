package com.softwaremagico.ktg.gui.base;

import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.TournamentComboBox.ComboBoxActionListener;
import com.softwaremagico.ktg.persistence.TeamPool;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamComboBox extends KComboBox {

    private Tournament tournament;
    private List<Team> listTeams;
    private KFrame parent;

    public TeamComboBox(Tournament tournament, KFrame parent) {
        this.tournament = tournament;
        try {
            listTeams = TeamPool.getInstance().getSorted(tournament);
        } catch (SQLException ex) {
            listTeams = new ArrayList<>();
            AlertManager.showSqlErrorMessage(ex);
        }
        this.parent = parent;
        fillTeams();
    }

    private void fillTeams() {
        try {
            for (int i = 0; i < listTeams.size(); i++) {
                addItem(listTeams.get(i));
            }
            if (getItemCount() > 0) {
                setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    public Team getSelectedTeam() {
        try {
            return ((Team) getSelectedItem());
        } catch (NullPointerException npe) {
            return null;
        }
    }

    @Override
    public void setSelectedItem(Object object) {
        if (object instanceof Team) {
            Team team = (Team) object;
            super.setSelectedItem(team);
        }
    }
}
