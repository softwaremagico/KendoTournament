/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge
 */
public class LevelGroups {

    private List<TournamentGroup> tournamentGroups;

    LevelGroups() {
        tournamentGroups = new ArrayList<>();
    }

    protected void updateGroups() {
        for (TournamentGroup t : tournamentGroups) {
            t.update();
        }
    }

    protected void activateGroupsColor(boolean color) {
        for (TournamentGroup t : tournamentGroups) {
            t.activateColor(color);
        }
    }

    protected void enhanceGroups(boolean yes) {
        for (TournamentGroup t : tournamentGroups) {
            t.enhance(yes);
        }
    }

    protected void onlyShow() {
        for (TournamentGroup t : tournamentGroups) {
            t.onlyShow();
        }
    }
    
    protected int size(){
        return tournamentGroups.size();
    }
}
