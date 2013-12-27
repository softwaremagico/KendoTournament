package com.softwaremagico.ktg.core;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.softwaremagico.ktg.tournament.ScoreType;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentScore;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.awt.Image;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Tournament implements Comparable<Tournament> {

    private static char[] fightAreaNames = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final List<TournamentType> CHAMPIONSHIP_TYPES = new ArrayList<>();
    private String name;
    private transient Photo banner;
    private transient Photo diploma;
    private transient Photo accreditation;
    private int fightingAreas;
    private int howManyTeamsOfGroupPassToTheTree;
    private int teamSize;
    private TournamentType mode; // simple, championship, custom, personalized, tree
    // private float scoreForWin = 1;
    // private float scoreForDraw = 0;
    // private ScoreType choosedScore = ScoreType.INTERNATIONAL;
    private TournamentScore tournamentScore;

    static {
        CHAMPIONSHIP_TYPES.add(TournamentType.CHAMPIONSHIP);
        CHAMPIONSHIP_TYPES.add(TournamentType.CUSTOM_CHAMPIONSHIP);
    }

    public Tournament(String name, int areas, int passingTeams, int teamSize, TournamentType mode) {
        this.name = name;
        fightingAreas = areas;
        howManyTeamsOfGroupPassToTheTree = passingTeams;
        this.teamSize = teamSize;
        this.mode = mode;
        tournamentScore = new TournamentScore(ScoreType.INTERNATIONAL, 1, 1);
    }

    public Photo getBanner() {
        return banner;
    }

    public void setBanner(Photo banner) {
        this.banner = banner;
    }

    public Photo getDiploma() {
        return diploma;
    }

    public void setDiploma(Photo diploma) {
        this.diploma = diploma;
    }

    public Photo getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(Photo accreditation) {
        this.accreditation = accreditation;
    }

    public int getFightingAreas() {
        return fightingAreas;
    }

    public void setFightingAreas(int fightingAreas) {
        this.fightingAreas = fightingAreas;
    }

    public int getHowManyTeamsOfGroupPassToTheTree() {
        return howManyTeamsOfGroupPassToTheTree;
    }

    public void setHowManyTeamsOfGroupPassToTheTree(int howManyTeamsOfGroupPassToTheTree) {
        this.howManyTeamsOfGroupPassToTheTree = howManyTeamsOfGroupPassToTheTree;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public TournamentType getType() {
        return mode;
    }

    public boolean isChampionship() {
        return isChampionship(mode);
    }

    public boolean isChampionship(TournamentType mode) {
        return CHAMPIONSHIP_TYPES.contains(mode);
    }

    public void setType(TournamentType newMode) {
        if (!this.mode.equals(newMode)) {
            // Groups are mantained between custom and championship modes.
            if (!isChampionship(newMode) || !isChampionship()) {
                TournamentManagerFactory.removeManager(this);
            }
            this.mode = newMode;
        }
    }

    public String getName() {
        return name;
    }

    public void addBanner(InputStream input, int size) {
        banner = new Photo(getName());
        banner.setImage(input, size);
    }

    public void addBanner(Image img) {
        banner = new Photo(getName());
        banner.setImage(img);
    }

    public void addDiploma(InputStream input, int size) {
        diploma = new Photo(getName());
        diploma.setImage(input, size);
    }

    public void addDiploma(Image img) {
        diploma = new Photo(getName());
        diploma.setImage(img);
    }

    public void addAccreditation(InputStream input, int size) {
        accreditation = new Photo(getName());
        accreditation.setImage(input, size);
    }

    public void addAccreditation(Image img) {
        accreditation = new Photo(getName());
        accreditation.setImage(img);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Tournament)) {
            return false;
        }
        Tournament otherTournament = (Tournament) object;
        return this.name.equals(otherTournament.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(Tournament t) {
        return getName().compareTo(t.getName());
    }

    public static String getFightAreaName(int pos) {
        if (pos < fightAreaNames.length) {
            return fightAreaNames[pos] + "";
        } else {
            return (pos + 1) + "";
        }
    }

    public TournamentScore getTournamentScore() {
        return tournamentScore;
    }

    public void setTournamentScore(TournamentScore tournamentScore) {
        this.tournamentScore = tournamentScore;
    }
}
