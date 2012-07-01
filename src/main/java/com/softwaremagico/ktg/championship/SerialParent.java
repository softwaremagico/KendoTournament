/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 17-ago-2009.
 */
package com.softwaremagico.ktg.championship;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class SerialParent {

    protected String FILENAME;

    public void write(Object objectToSave) throws IOException {
        try {
            try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(
                         new FileOutputStream(FILENAME)))) {
                os.writeObject(objectToSave);
            }
        } catch (FileNotFoundException fnoe) {
        }
    }

    public void save(Object o) throws IOException {
        List<Object> l = new ArrayList<>();
        l.add(o);
        write(l);
    }

    public List load() throws IOException, ClassNotFoundException,
            FileNotFoundException {
        List l;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME))) {
            l = (List) is.readObject();
        }
        return l;
    }

    public void dump() throws IOException, ClassNotFoundException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME))) {
            System.out.println(is.readObject());
        }
    }
}
