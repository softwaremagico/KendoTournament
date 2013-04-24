package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Tournament;

public class ManualChampionship extends Championship {
    
    public ManualChampionship(Tournament tournament){
        super(tournament);
    }

    public boolean allGroupsHaveNextLink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addLink(TournamentGroup source, TournamentGroup address) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeLinks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
