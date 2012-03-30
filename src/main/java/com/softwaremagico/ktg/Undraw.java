/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;

/**
 *
 * @author LOCAL\jhortelano
 */
public class Undraw {

    private String tournament;
    private Integer group;
    private String winnerTeam;
    private Integer player;

    public Undraw(String tournament, Integer group, String winnerTeam, Integer player) {
        this.winnerTeam = winnerTeam;
        this.tournament = tournament;
        this.player = player;
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    public String getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeam(String winnerTeam) {
        this.winnerTeam = winnerTeam;
    }
}
