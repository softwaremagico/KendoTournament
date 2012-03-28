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
 *   Created on 06-feb-2009.
 */
package com.softwaremagico.ktg;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class RoleTags {

    private List<RoleTag> listRoles = new ArrayList<RoleTag>();

    public RoleTags() {
    }

    public void set(List<RoleTag> r) {
        listRoles = r;
    }

    public RoleTag get(int i) {
        return listRoles.get(i);
    }

    public int size() {
        return listRoles.size();
    }

    public void add(RoleTag r) {
        listRoles.add(r);
    }

    public int get(String tag) {
        for (int i = 0; i < listRoles.size(); i++) {
            if (listRoles.get(i).tag.equals(tag)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return the first letter of the role or 'X' if is not defined.
     * @param tag
     * @return
     */
    public String getAbbrev(String tag) {
        for (int i = 0; i < listRoles.size(); i++) {
            if (listRoles.get(i).tag.equals(tag)) {
                return listRoles.get(i).abbrev;
            }
        }
        return "D";
    }

    public RoleTag getRole(String tag) {
        for (int i = 0; i < listRoles.size(); i++) {
            if (listRoles.get(i).tag.equals(tag)) {
                return listRoles.get(i);
            }
        }
        return null;
    }

    public String getTraduction(String tag) {
        for (int i = 0; i < listRoles.size(); i++) {
            if (listRoles.get(i).tag.equals(tag)) {
                return listRoles.get(i).name;
            }
        }
        return null;
    }
}
