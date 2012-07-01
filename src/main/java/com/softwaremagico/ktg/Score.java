/*
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 26-mar-2012.
 */
package com.softwaremagico.ktg;

import java.util.ArrayList;

/**
 *
 * @author LOCAL\jhortelano
 */
public enum Score {

    MEN("men.png", "Men", 'M'),
    KOTE("kote.png", "Kote", 'K'),
    DO("do.png", "Do", 'D'),
    TSUKI("tsuki.png", "Tsuki", 'T'),
    IPPON("ippon.png", "Ippon", 'I'),
    HANSOKU("hansoku.png", "Hansoku", 'H'),
    EMPTY("empty.png", "ClearMenuItem", ' '),
    FAULT("fault.png", "FaultMenuItem", '^'),
    DRAW("draw.png", "Draw", 'X');
    private final String imageName;
    private final char abbreviature;
    private final String name;

    Score(String imageName, String name, char abbreviature) {
        this.imageName = imageName;
        this.abbreviature = abbreviature;
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public char getAbbreviature() {
        return abbreviature;
    }

    public String getName() {
        return name;
    }

    public static Score getScore(char abbreviature) {
        for (Score s : Score.values()) {
            if (s.abbreviature == abbreviature) {
                return s;
            }
        }
        return EMPTY;
    }

    public static Score getScore(String name) {
        for (Score s : Score.values()) {
            if (s.name == null ? name == null : s.name.equals(name)) {
                return s;
            }
        }
        return EMPTY;
    }
    
    public static boolean isValidPoint(Score sc){
        return getValidPoints().contains(sc);
    }

    public static ArrayList<Score> getValidPoints() {
        ArrayList<Score> points = new ArrayList<>();
        points.add(MEN);
        points.add(KOTE);
        points.add(DO);
        points.add(TSUKI);
        points.add(IPPON);
        points.add(HANSOKU);
        return points;
    }
}
