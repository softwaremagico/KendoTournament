/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.softwaremagico.ktg.statistics;

/**
 *
 * @author Jorge
 */
public class CompetitorRanking {
    public String name;
    public String surname;
    public String id;
    public Integer victorias;
    public Integer puntos;

    public CompetitorRanking(String tmp_name, String tmp_surname, String tmp_id, Integer tmp_victorias, Integer tmp_puntos){
        name = tmp_name;
        surname = tmp_surname;
        id = tmp_id;
        victorias = tmp_victorias;
        puntos = tmp_puntos;
    }
    
}
