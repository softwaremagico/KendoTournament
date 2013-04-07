package com.softwaremagico.ktg.gui.base;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;

public class FightAreaComboBox extends KComboBox {

    private Tournament tournament;

    public FightAreaComboBox(Tournament tournament) {
        this.tournament = tournament;
        fillFightingAreas();
    }

    private void fillFightingAreas() {
        removeAllItems();
        try {
            for (int i = 0; i < tournament.getFightingAreas(); i++) {
                addItem(KendoTournamentGenerator.getFightAreaName(i));
            }
        } catch (NullPointerException npe) {
        }
    }

    public Integer getSelectedFightArea() {
        try {
            return getSelectedIndex();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getSelectedFightAreaName() {
        try {
            return KendoTournamentGenerator.getFightAreaName(getSelectedFightArea());
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
