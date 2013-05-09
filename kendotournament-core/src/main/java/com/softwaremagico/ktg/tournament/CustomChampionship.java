package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Tournament;

public class CustomChampionship extends Championship {
    
    public CustomChampionship(Tournament tournament){
        super(tournament);
        levelZero = new LeagueLevelCustom(tournament, 0, null, null);
    }

    public boolean allGroupsHaveNextLink() {
       return ((LeagueLevelCustom)levelZero).allGroupsHaveManualLink();
    }

    public void addLink(TournamentGroup source, TournamentGroup address) {
        ((LeagueLevelCustom)levelZero).addLink(source, address);
    }

    public void removeLinks(TournamentGroup group) {
        ((LeagueLevelCustom)levelZero).removeLinksSelectedGroup(group);
    }
}
