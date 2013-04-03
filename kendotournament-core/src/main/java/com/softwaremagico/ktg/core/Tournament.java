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

import java.awt.*;
import java.io.*;

public class Tournament implements Serializable, Comparable<Tournament> {

    private String name;
    private transient Photo banner;
    private transient Photo diploma;
    private transient Photo accreditation;
    private int fightingAreas;
    private int howManyTeamsOfGroupPassToTheTree;
    private int teamSize;
    private TournamentType mode;    //simple, championship, manual, tree
    private float scoreForWin = 1;
    private float scoreForDraw = 0;
    private String choosedScore = "European";

    public Tournament(String name, int areas, int passingTeams, int teamSize, TournamentType mode) {
        this.name = name;
        fightingAreas = areas;
        howManyTeamsOfGroupPassToTheTree = passingTeams;
        this.teamSize = teamSize;
        this.mode = mode;
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

    public void setType(TournamentType mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void changeScoreOptions(String type, float win, float draw) {
        choosedScore = type;
        scoreForWin = win;
        scoreForDraw = draw;
        //storeConfig();
    }

    public float getScoreForWin() {
        return scoreForWin;
    }

    public float getScoreForDraw() {
        return scoreForDraw;
    }

    public String getChoosedScore() {
        return choosedScore;
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
}
