package com.softwaremagico.ktg.core;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.logging.*;

public class KendoLog {

    private static final Logger logger = Logger.getLogger("KendoLog");
    private static final Level logLevel = Level.ALL; //INFO, OFF, ALL, ... 
    private static final int MAX_BYTES = 50000000;
    private static final int NUMBER_MAX_OF_FILES = 10;
    private static final Translator trans = LanguagePool.getTranslator("messages.xml");

    static {
        try {
            FileHandler fh = new FileHandler(Path.getLogFile(), MAX_BYTES, NUMBER_MAX_OF_FILES, true);
            logger.addHandler(fh);
            logger.setLevel(logLevel);
            //fh.setFormatter(new SimpleFormatter());
            fh.setFormatter(getCustomFormatter());
        } catch (IOException | SecurityException ex) {
            KendoLog.severe(KendoLog.class.getName(), "Logger failed. Probably the log file can not be created. Error Message: " + ex.getMessage());
        }
    }

    /**
     * Defines our own formatter.
     */
    public static Formatter getCustomFormatter() {
        return new Formatter() {
            //StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            @Override
            public String format(LogRecord record) {
                String text = record.getLevel() + " [" + new Date() + "] " + " - " + record.getMessage() + "\n";
                return text;
            }
        };
    }

    private KendoLog() {
    }

    private static void info(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.info(message);
        }
    }

    public static void info(String className, String message) {
        info(className + ": " + message);
    }

    private static void config(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.config(message);
        }
    }

    public static void config(String className, String message) {
        config(className + ": " + message);
    }

    private static void warning(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.warning(message);
        }
    }

    public static void warning(String className, String message) {
        warning(className + ": " + message);
    }

    private static void debug(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.finest(message);
        }
    }

    public static void debug(String className, String message) {
        debug(className + ": " + message);
    }

    private static void severe(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.severe(message);
        }
    }

    public static void severe(String className, String message) {
        severe(className + ": " + message);
    }
    
    public static void translatedSevere(String className, String code){
        severe(className, trans.getTranslatedText(code));
    }

    private static void fine(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.fine(message);
        }
    }

    public static void fine(String className, String message) {
        fine(className + ": " + message);
    }

    private static void finer(String message) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.finer(message);
        }
    }

    public static void finer(String className, String message) {
        finer(className + ": " + message);
    }

    private static void finest(String messsage) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            logger.finest(messsage);
        }
    }

    public static void finest(String className, String message) {
        finest(className + ": " + message);
    }

    public static void entering(String className, String method) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            debug(className, "ENTRY (" + method + ")");
        }
    }

    public static void exiting(String className, String method) {
        if (KendoTournamentGenerator.getInstance().getLogOption()) {
            debug(className, "RETURN (" + method + ")");
        }
    }

    public static void errorMessage(String className, Throwable throwable) {
        String error = getStackTrace(throwable);
        severe(className, error);
    }

    private static String getStackTrace(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}
