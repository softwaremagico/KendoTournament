/*
 * 
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
 *   Created on 06-feb-2009.
 */
package com.softwaremagico.ktg;

import java.awt.Color;

public class RoleTag {

    public String tag;     //Identical for all languages.
    public String name;    //The traduction for each language.
    public String abbrev;
    public Color color; 

    public RoleTag(String tmp_tag, String tmp_name, String tmp_abbrev) {
        tag = tmp_tag;
        name = tmp_name;
        abbrev = tmp_abbrev;
    }

    public void addColor(int red, int green, int blue){
        color = new Color(red, green, blue);
    }
}
