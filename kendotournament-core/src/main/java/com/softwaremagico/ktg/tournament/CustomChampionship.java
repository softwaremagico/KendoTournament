package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Tournament;

public class CustomChampionship extends Championship {
    
    public CustomChampionship(Tournament tournament){
        super(tournament);
        levelZero = new LeagueLevelCustom(tournament, 0, null, null);
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
