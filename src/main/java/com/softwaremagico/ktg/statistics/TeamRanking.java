/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.statistics;

/**
 *
 * @author Jorge
 */
public class TeamRanking {

    public String name;
    public String tournament;
    public Integer wonMatchs;  /* partidos ganados por el equipo */

    public Integer drawMatchs;
    public Integer wonFights; /* combates ganados por los miembros del equipo */
    public Integer drawFights;

    public Integer score; /* puntos totales del equipo */


    public TeamRanking(String tmp_name, String tmp_tournament, Integer won, Integer draw, Integer fights_won, Integer figths_draw, Integer tmp_score) {
        name = tmp_name;
        tournament = tmp_tournament;
        wonFights = fights_won;
        drawFights = figths_draw;
        score = tmp_score;
        wonMatchs = won;
        drawMatchs = draw;
    }

    public String returnShortName() {
        int length = 24;
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 6) + "... " + name.substring(name.length() - 2, name.length());
        }
    }

    public String returnShortName(int length) {
        if (name.length() <= length) {
            return name;
        } else {
            return name.substring(0, length - 6) + "... " + name.substring(name.length() - 2, name.length());
        }
    }
}
