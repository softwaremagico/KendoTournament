package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.CustomLinkPool;

public class CustomChampionship extends Championship {

    @Override
    public void fillGroups() {
        super.fillGroups();
        ((LeagueLevelCustom) levelZero).setLinks(CustomLinkPool.getInstance().get(tournament));
        //List<CustomWinnerLink> links = CustomLinkPool.getInstance().get(tournament);
    }

    public CustomChampionship(Tournament tournament) {
        super(tournament);
        levelZero = new LeagueLevelCustom(tournament, 0, null, null);
    }

    public boolean allGroupsHaveNextLink() {
        return ((LeagueLevelCustom) levelZero).allGroupsHaveManualLink();
    }

    public void addLink(TournamentGroup source, TournamentGroup address) {
        ((LeagueLevelCustom) levelZero).addLink(source, address);
    }

    public void removeLinks(TournamentGroup group) {
        //Remove links from manager.
        ((LeagueLevelCustom) levelZero).removeLinksSelectedGroup(group);
    }

    public void removeLinks() {
        //Remove all links from manager.
        ((LeagueLevelCustom) levelZero).removeLinks();
    }
}
