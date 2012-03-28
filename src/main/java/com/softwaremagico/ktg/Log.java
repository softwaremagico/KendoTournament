/*
 *
 This software is designed by Jorge Hortelano Otero.
 softwaremagico@gmail.com
 Copyright (C) 2012 Jorge Hortelano Otero.
 C/Quart 89, 3. Valencia CP:46008 (Spain).
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 Created on march of 2009.
 */
package com.softwaremagico.ktg;

import com.softwaremagico.ktg.files.Folder;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.Translator;

public class Log {

    private static final Translator trans = new Translator("messages.xml");

    private Log() {
    }

    public static void storeLog(String tag, String title, String language) {
        storeLog(title + ": " + trans.returnTag(tag, language));
    }

    public static void storeLog(String tag, String title, String language, String finalText) {
        storeLog(title + ": " + trans.returnTag(tag, language) + " -> " + finalText);
    }

    public static void storeLog(String text) {
        java.util.Date date = new java.util.Date();
        long lnMilisegundos = date.getTime();
        java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
        java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

        Folder f = null;
        try {
            f = new Folder("");
        } catch (Exception ex) {
        }
        f.AppendTextToFile("[" + sqlDate + " " + sqlTime + "] " + text + "\n", Path.returnLogFile());
    }
}
