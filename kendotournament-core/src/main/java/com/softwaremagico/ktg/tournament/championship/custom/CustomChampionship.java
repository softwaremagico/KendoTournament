package com.softwaremagico.ktg.tournament.championship.custom;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.CustomLinkPool;
import com.softwaremagico.ktg.tournament.CustomWinnerLink;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.championship.Championship;

import java.sql.SQLException;
import java.util.List;

public class CustomChampionship extends Championship {

	public CustomChampionship(Tournament tournament) {
		super(tournament);
		setLevelZero(new LeagueLevelCustom(tournament, 0, null, null));
	}

	public boolean allGroupsHaveNextLink() {
		return ((LeagueLevelCustom) getLevelZero()).allGroupsHaveCustomLink();
	}

	public void addLink(TGroup source, TGroup address) {
		((LeagueLevelCustom) getLevelZero()).addLink(source, address);
	}

	public void removeLinks(TGroup group) {
		// Remove links from manager.
		((LeagueLevelCustom) getLevelZero()).removeLinksSelectedGroup(group);
	}

	public void removeLinks() {
		// Remove all links from manager.
		((LeagueLevelCustom) getLevelZero()).removeLinks();
	}

	public List<CustomWinnerLink> getLinks() {
		return ((LeagueLevelCustom) getLevelZero()).getLinks();
	}

	@Override
	public void fillGroups() {
		super.fillGroups();
		try {
			((LeagueLevelCustom) getLevelZero()).setLinks(CustomLinkPool.getInstance().get(getTournament()));
		} catch (SQLException ex) {
			KendoLog.errorMessage(this.getClass().getName(), ex);
		}
	}
}
