package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Tournament;
import java.util.Objects;

public class CustomWinnerLink implements Comparable<CustomWinnerLink> {

    private Tournament tournament;
    private Integer source = null;
    private Integer address = null;
    private Integer winner = 1;

    public CustomWinnerLink(Tournament tournament, TGroup from, TGroup to) {
        this.tournament = tournament;
        if (isValidTournament(tournament) && from != null && to != null) {
            source = ((CustomChampionship) (TournamentManagerFactory.getManager(tournament))).getLevel(0).getIndexOfGroup(from);
            address = ((CustomChampionship) (TournamentManagerFactory.getManager(tournament))).getLevel(1).getIndexOfGroup(to);
        }
    }

    public CustomWinnerLink(Tournament tournament, Integer from, Integer to, Integer winner) {
        this.tournament = tournament;
        if (isValidTournament(tournament) && from != null && to != null) {
            source = from;
            address = to;
        }
        this.winner = winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public Integer getWinner() {
        return winner;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Integer getSource() {
        if (isValidTournament(tournament)) {
            return source;
        }
        return null;
    }

    public TGroup getSourceGroup() {
        try {
            if (isValidTournament(tournament) && TournamentManagerFactory.getManager(tournament).getNumberOfLevels() > 1) {
                return TournamentManagerFactory.getManager(tournament).getLevel(0).getGroups().get(source);
            }
        } catch (IndexOutOfBoundsException iob) {
        }
        return null;
    }

    public void setSource(TGroup source) {
        if (isValidTournament(tournament)) {
            this.source = ((CustomChampionship) (TournamentManagerFactory.getManager(getTournament()))).getLevel(0).getIndexOfGroup(source);
        }
    }

    public Integer getAddress() {
        if (isValidTournament(tournament)) {
            return address;
        }
        return null;
    }

    public TGroup getAddressGroup() {
        if (isValidTournament(tournament)) {
            return TournamentManagerFactory.getManager(tournament).getLevel(1).getGroups().get(address);
        }
        return null;
    }

    public void setAddress(TGroup address) {
        if (isValidTournament(tournament)) {
            this.address = ((CustomChampionship) (TournamentManagerFactory.getManager(getTournament()))).getLevel(1).getIndexOfGroup(address);
        }
    }

    private boolean isValidTournament(Tournament tournament) {
        return (tournament.getType().equals(TournamentType.MANUAL));
    }

    public String getId() {
        return hashCode() + "";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.tournament);
        hash = 59 * hash + Objects.hashCode(this.source);
        hash = 59 * hash + Objects.hashCode(this.address);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomWinnerLink other = (CustomWinnerLink) obj;
        if (!Objects.equals(this.tournament, other.tournament)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CustomWinnerLink o) {
        if (!tournament.equals(o.tournament)) {
            return tournament.compareTo(o.tournament);
        }

        if (source != o.source) {
            return source.compareTo(o.source);
        }

        return winner.compareTo(o.winner);
    }

    @Override
    public String toString() {
        return "source: " + source + ", address: " + address + "\n";
    }
}
