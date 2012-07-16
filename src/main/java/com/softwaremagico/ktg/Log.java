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
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Log {

    private static final Translator trans = LanguagePool.getTranslator("messages.xml");
    private static final Logger logger = Logger.getLogger("KendoLog");
    private static final Level logLevel = Level.ALL; //INFO, OFF, ALL, ... 
    private static final int maxBytes = 500000;
    private static final int numLogFiles = 10;

    static {
        try {
            FileHandler fh = new FileHandler(Path.returnLogFile(), maxBytes, numLogFiles, true);
            logger.addHandler(fh);
            logger.setLevel(logLevel);
            //fh.setFormatter(new SimpleFormatter());
            fh.setFormatter(getCustomFormatter());
        } catch (IOException | SecurityException ex) {
            MessageManager.basicErrorMessage("Logger failed. Probably the log file can not be created. Error Message: " + ex.getMessage(), "Logger");
        }
    }

    /**
     * Defines our own formatter.
     */
    public static Formatter getCustomFormatter() {
        return new Formatter() {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

            @Override
            public String format(LogRecord record) {
                String text = record.getLevel() + " [" + new Date() + "] " + record.getSourceClassName() + " " + stackTraceElements[6] + " -> " + record.getSourceMethodName() + " - " + record.getMessage() + "\n";
                return text;
            }
        };
    }

    private Log() {
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String tag, String title) {
        info(title + ": " + trans.returnTag(tag));
    }

    public static void info(String tag, String title, String message) {
        info(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void config(String message) {
        logger.config(message);
    }

    public static void config(String tag, String title) {
        config(title + ": " + trans.returnTag(tag));
    }

    public static void config(String tag, String title, String message) {
        config(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void warning(String tag, String title) {
        warning(title + ": " + trans.returnTag(tag));
    }

    public static void warning(String tag, String title, String message) {
        warning(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void debug(String message) {
        logger.severe(message);
    }

    public static void debug(String tag, String title) {
        debug(title + ": " + trans.returnTag(tag));
    }

    public static void debug(String tag, String title, String message) {
        debug(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void severe(String tag, String title) {
        severe(title + ": " + trans.returnTag(tag));
    }

    public static void severe(String tag, String title, String message) {
        severe(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void fine(String message) {
        logger.fine(message);
    }

    public static void fine(String tag, String title) {
        fine(title + ": " + trans.returnTag(tag));
    }

    public static void fine(String tag, String title, String message) {
        fine(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void finer(String message) {
        logger.finer(message);
    }

    public static void finer(String tag, String title) {
        finer(title + ": " + trans.returnTag(tag));
    }

    public static void finer(String tag, String title, String message) {
        finer(title + ": " + trans.returnTag(tag) + " -> " + message);
    }

    public static void finest(String messsage) {
        logger.finest(messsage);
    }

    public static void finest(String tag, String title) {
        finest(title + ": " + trans.returnTag(tag));
    }

    public static void finest(String tag, String title, String message) {
        finest(title + ": " + trans.returnTag(tag) + " -> " + message);
    }
}
