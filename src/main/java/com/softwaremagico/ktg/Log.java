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

import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.Translator;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Log {

    private static final Translator trans = new Translator("messages.xml");
    private static final Logger logger = Logger.getLogger("KendoLog");
    private static final Level logLevel = Level.ALL; //INFO, OFF, ALL, ... 
    private static final int maxLines = 5000;
    private static final int numLogFiles = 10;

    static {
        try {
            FileHandler fh = new FileHandler(Path.returnLogFile(), maxLines, numLogFiles, true);
            logger.addHandler(fh);
            logger.setLevel(logLevel);
            //fh.setFormatter(new SimpleFormatter());
            fh.setFormatter(getCustomFormatter());
        } catch (IOException | SecurityException ex) {
            MessageManager.errorMessage("Logger failed. Probably the log file can not be created. Error Message: " + ex.getMessage(), "Logger");
        }
    }

    /**
     * Defines our own formatter.
     */
    public static Formatter getCustomFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                String text = record.getLevel() + " [" + new Date() + "] " + record.getSourceClassName() + " -> " + record.getSourceMethodName() + " - " + record.getMessage() + "\n";
                return text;
            }
        };
    }

    private Log() {
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String tag, String title, String language) {
        info(title + ": " + trans.returnTag(tag, language));
    }

    public static void info(String tag, String title, String language, String message) {
        info(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void config(String message) {
        logger.config(message);
    }

    public static void config(String tag, String title, String language) {
        config(title + ": " + trans.returnTag(tag, language));
    }

    public static void config(String tag, String title, String language, String message) {
        config(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void warning(String tag, String title, String language) {
        warning(title + ": " + trans.returnTag(tag, language));
    }

    public static void warning(String tag, String title, String language, String message) {
        warning(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void debug(String message) {
        logger.severe(message);
    }

    public static void debug(String tag, String title, String language) {
        debug(title + ": " + trans.returnTag(tag, language));
    }

    public static void debug(String tag, String title, String language, String message) {
        debug(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void severe(String tag, String title, String language) {
        severe(title + ": " + trans.returnTag(tag, language));
    }

    public static void severe(String tag, String title, String language, String message) {
        severe(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void fine(String message) {
        logger.fine(message);
    }

    public static void fine(String tag, String title, String language) {
        fine(title + ": " + trans.returnTag(tag, language));
    }

    public static void fine(String tag, String title, String language, String message) {
        fine(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void finer(String message) {
        logger.finer(message);
    }

    public static void finer(String tag, String title, String language) {
        finer(title + ": " + trans.returnTag(tag, language));
    }

    public static void finer(String tag, String title, String language, String message) {
        finer(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }

    public static void finest(String messsage) {
        logger.finest(messsage);
    }

    public static void finest(String tag, String title, String language) {
        finest(title + ": " + trans.returnTag(tag, language));
    }

    public static void finest(String tag, String title, String language, String message) {
        finest(title + ": " + trans.returnTag(tag, language) + " -> " + message);
    }
}
