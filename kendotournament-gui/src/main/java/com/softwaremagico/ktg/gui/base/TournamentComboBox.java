package com.softwaremagico.ktg.gui.base;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.TournamentPool;
import java.util.List;

public class TournamentComboBox extends KComboBox {

    private List<Tournament> listTournaments;

    public TournamentComboBox() {
        listTournaments = TournamentPool.getInstance().getAll();
        fillTournaments();
    }

    private void fillTournaments() {
        Tournament selectedTournament;
        try {
            for (int i = 0; i < listTournaments.size(); i++) {
                addItem(listTournaments.get(i));
            }
            selectedTournament = KendoTournamentGenerator.getInstance().getLastSelectedTournament();
            if (selectedTournament != null) {
                setSelectedItem(selectedTournament);
            } else if (getItemCount() > 0) {
                setSelectedIndex(0);
                KendoTournamentGenerator.getInstance().changeLastSelectedTournament(getSelectedTournament().toString());
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    public Tournament getSelectedTournament() {
        try {
            return ((Tournament) getSelectedItem());
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
